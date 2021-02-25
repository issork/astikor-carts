package de.mennomax.astikorcarts.entity;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.network.clientbound.UpdateDrawnMessage;
import de.mennomax.astikorcarts.util.CartWheel;
import de.mennomax.astikorcarts.world.AstikorWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEquipable;
import net.minecraft.entity.IRideable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.HandSide;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.TransportationHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class AbstractDrawnEntity extends Entity implements IEntityAdditionalSpawnData {
    private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.createKey(AbstractDrawnEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> FORWARD_DIRECTION = EntityDataManager.createKey(AbstractDrawnEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.createKey(AbstractDrawnEntity.class, DataSerializers.FLOAT);
    private static final UUID PULL_SLOWLY_MODIFIER_UUID = UUID.fromString("49B0E52E-48F2-4D89-BED7-4F5DF26F1263");
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;
    private double lerpPitch;
    protected List<CartWheel> wheels;
    private int pullingId = -1;
    private UUID pullingUUID = null;
    protected double spacing = 1.7D;
    public Entity pulling;
    protected AbstractDrawnEntity drawn;

    public AbstractDrawnEntity(final EntityType<? extends Entity> entityTypeIn, final World worldIn) {
        super(entityTypeIn, worldIn);
        this.stepHeight = 1.2F;
        this.preventEntitySpawning = true;
        this.initWheels();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return this.getBoundingBox().grow(3.0D, 3.0D, 3.0D);
    }

    @Override
    public void tick() {
        if (this.getTimeSinceHit() > 0) {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }
        if (!this.hasNoGravity()) {
            this.setMotion(0.0D, this.getMotion().y - 0.08D, 0.0D);
        }
        if (this.getDamageTaken() > 0.0F) {
            this.setDamageTaken(this.getDamageTaken() - 1.0F);
        }
        super.tick();
        this.tickLerp();
        if (this.pulling == null) {
            this.rotationPitch = 25.0F;
            this.move(MoverType.SELF, this.getMotion());
            this.attemptReattach();
        }
        for (final Entity entity : this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), EntityPredicates.pushableBy(this))) {
            this.applyEntityCollision(entity);
        }
    }

    /**
     * This method is called for every cart that is being pulled by another entity
     * after all other
     * entities have been ticked to ensure that the cart always behaves the same
     * when being pulled. (unless this cart is being pulled by another cart)
     */
    public void pulledTick() {
        if (this.pulling == null) {
            return;
        }
        Vector3d targetVec = this.getRelativeTargetVec(1.0F);
        this.handleRotation(targetVec);
        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }
        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }
        if (this.pulling.isOnGround()) {
            targetVec = new Vector3d(targetVec.x, 0.0D, targetVec.z);
        }
        final double targetVecLength = targetVec.length();
        final double r = 0.2D;
        final double relativeSpacing = Math.max(this.spacing + 0.5D * this.pulling.getWidth(), 1.0D);
        final double diff = targetVecLength - relativeSpacing;
        final Vector3d move;
        if (Math.abs(diff) < r) {
            move = this.getMotion();
        } else {
            move = this.getMotion().add(targetVec.subtract(targetVec.normalize().scale(relativeSpacing + r * Math.signum(diff))));
        }
        this.onGround = true;
        final double startX = this.getPosX();
        final double startY = this.getPosY();
        final double startZ = this.getPosZ();
        this.move(MoverType.SELF, move);
        if (!this.isAlive()) {
            return;
        }
        this.addStats(this.getPosX() - startX, this.getPosY() - startY, this.getPosZ() - startZ);
        if (this.world.isRemote) {
            for (final CartWheel wheel : this.wheels) {
                wheel.tick();
            }
        } else {
            targetVec = this.getRelativeTargetVec(1.0F);
            if (targetVec.length() > relativeSpacing + 1.0D) {
                this.setPulling(null);
            }
        }
        this.updatePassengers();
        if (this.drawn != null) {
            this.drawn.pulledTick();
        }
    }

    private void addStats(final double x, final double y, final double z) {
        if (!this.world.isRemote) {
            final int cm = Math.round(MathHelper.sqrt(x * x + y * y + z * z) * 100.0F);
            if (cm > 0) {
                for (final Entity passenger : this.getPassengers()) {
                    if (passenger instanceof PlayerEntity) {
                        ((PlayerEntity) passenger).addStat(AstikorCarts.Stats.CART_ONE_CM, cm);
                    }
                }
            }
        }
    }

    public void initWheels() {
        this.wheels = Arrays.asList(new CartWheel(this, 0.9F), new CartWheel(this, -0.9F));
    }

    /**
     * @return Whether the currently pulling entity should stop pulling this cart.
     */
    public boolean shouldRemovePulling() {
        if (this.collidedHorizontally) {
            this.getPositionVec().add(0.0D, this.getEyeHeight(), 0.0D);
            final Vector3d start = new Vector3d(this.getPosX(), this.getPosY() + this.getHeight(), this.getPosZ());
            final Vector3d end = new Vector3d(this.pulling.getPosX(), this.pulling.getPosY() + this.pulling.getHeight() / 2, this.pulling.getPosZ());
            final RayTraceResult result = this.world.rayTraceBlocks(new RayTraceContext(start, end, BlockMode.COLLIDER, FluidMode.NONE, this));
            return result.getType() == Type.BLOCK;
        }
        return false;
    }

    public void updatePassengers() {
        for (final Entity passenger : this.getPassengers()) {
            this.updatePassenger(passenger);
        }
    }

    @Nullable
    public Entity getPulling() {
        return this.pulling;
    }

    /**
     * Attaches the cart to an entity so that the cart follows it.
     *
     * @param entityIn new pulling entity
     */
    public void setPulling(final Entity entityIn) {
        if (!this.world.isRemote) {
            if (this.canBePulledBy(entityIn)) {
                if (entityIn == null) {
                    if (this.pulling instanceof LivingEntity) {
                        final ModifiableAttributeInstance attr = ((LivingEntity) this.pulling).getAttribute(Attributes.MOVEMENT_SPEED);
                        if (attr != null) attr.removeModifier(PULL_SLOWLY_MODIFIER_UUID);
                    } else if (this.pulling instanceof AbstractDrawnEntity) {
                        ((AbstractDrawnEntity) this.pulling).drawn = null;
                    }
                    AstikorCarts.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new UpdateDrawnMessage(-1, this.getEntityId()));
                    this.pullingUUID = null;
                    if (this.ticksExisted > 20) {
                        this.playDetachSound();
                    }
                } else {
                    if (entityIn instanceof MobEntity) {
                        ((MobEntity) entityIn).getNavigator().clearPath();
                    }
                    AstikorCarts.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new UpdateDrawnMessage(entityIn.getEntityId(), this.getEntityId()));
                    this.pullingUUID = entityIn.getUniqueID();
                    if (this.ticksExisted > 20) {
                        this.playAttachSound();
                    }
                }
                if (entityIn instanceof AbstractDrawnEntity) {
                    ((AbstractDrawnEntity) entityIn).drawn = this;
                }
                this.pulling = entityIn;
                AstikorWorld.get(this.world).ifPresent(w -> w.addPulling(this));

            }
        } else {
            if (entityIn == null) {
                this.pullingId = -1;
                for (final CartWheel wheel : this.wheels) {
                    wheel.clearIncrement();
                }
                if (this.pulling instanceof AbstractDrawnEntity) {
                    ((AbstractDrawnEntity) this.pulling).drawn = null;
                }
            } else {
                this.pullingId = entityIn.getEntityId();
                if (entityIn instanceof AbstractDrawnEntity) {
                    ((AbstractDrawnEntity) entityIn).drawn = this;
                }
            }
            this.pulling = entityIn;
            AstikorWorld.get(this.world).ifPresent(w -> w.addPulling(this));
        }
    }

    private void playAttachSound() {
        this.playSound(AstikorCarts.SoundEvents.CART_ATTACHED.get(), 0.2F, 1.0F);
    }

    private void playDetachSound() {
        this.playSound(AstikorCarts.SoundEvents.CART_DETACHED.get(), 0.2F, 1.0F);
    }

    /**
     * Attempts to reattach the cart to the last pulling entity.
     */
    private void attemptReattach() {
        if (this.world.isRemote) {
            if (this.pullingId != -1) {
                final Entity entity = this.world.getEntityByID(this.pullingId);
                if (entity != null && entity.isAlive()) {
                    this.setPulling(entity);
                }
            }
        } else {
            if (this.pullingUUID != null) {
                final Entity entity = ((ServerWorld) this.world).getEntityByUuid(this.pullingUUID);
                if (entity != null && entity.isAlive()) {
                    this.setPulling(entity);
                }
            }
        }
    }

    public boolean shouldStopPulledTick() {
        if (!this.isAlive() || this.getPulling() == null || !this.getPulling().isAlive() || this.getPulling().isPassenger()) {
            if (this.pulling != null && this.pulling instanceof PlayerEntity) {
                this.setPulling(null);
            } else {
                this.pulling = null;
            }
            return true;
        } else if (!this.world.isRemote && this.shouldRemovePulling()) {
            this.setPulling(null);
            return true;
        }
        return false;
    }

    /**
     * @return The position this cart should always face and travel towards.
     * Relative to the cart position.
     * @param delta
     */
    public Vector3d getRelativeTargetVec(final float delta) {
        final double x;
        final double y;
        final double z;
        if (delta == 1.0F) {
            x = this.pulling.getPosX() - this.getPosX();
            y = this.pulling.getPosY() - this.getPosY();
            z = this.pulling.getPosZ() - this.getPosZ();
        } else {
            x = MathHelper.lerp(delta, this.pulling.lastTickPosX, this.pulling.getPosX()) - MathHelper.lerp(delta, this.lastTickPosX, this.getPosX());
            y = MathHelper.lerp(delta, this.pulling.lastTickPosY, this.pulling.getPosY()) - MathHelper.lerp(delta, this.lastTickPosY, this.getPosY());
            z = MathHelper.lerp(delta, this.pulling.lastTickPosZ, this.pulling.getPosZ()) - MathHelper.lerp(delta, this.lastTickPosZ, this.getPosZ());
        }
        final float yaw = (float) Math.toRadians(this.pulling.rotationYaw);
        final float nx = -MathHelper.sin(yaw);
        final float nz = MathHelper.cos(yaw);
        final double r = 0.2D;
        return new Vector3d(x + nx * r, y, z + nz * r);
    }

    /**
     * Handles the rotation of this cart and its components.
     *
     * @param target
     */
    public void handleRotation(final Vector3d target) {
        this.rotationYaw = getYaw(target);
        this.rotationPitch = getPitch(target);
    }

    public static float getYaw(final Vector3d vec) {
        return MathHelper.wrapDegrees((float) Math.toDegrees(-MathHelper.atan2(vec.x, vec.z)));
    }

    public static float getPitch(final Vector3d vec) {
        return MathHelper.wrapDegrees((float) Math.toDegrees(-MathHelper.atan2(vec.y, MathHelper.sqrt(vec.x * vec.x + vec.z * vec.z))));
    }

    public double getWheelRotation(final int wheel) {
        return this.wheels.get(wheel).getRotation();
    }

    public double getWheelRotationIncrement(final int wheel) {
        return this.wheels.get(wheel).getRotationIncrement();
    }

    public abstract Item getCartItem();

    /**
     * Returns true if the passed in entity is allowed to pull this cart.
     *
     * @param entityIn
     */
    protected boolean canBePulledBy(final Entity entityIn) {
        if (this.world.isRemote) {
            return true;
        }
        if (entityIn == null) {
            return true;
        }
        return (this.pulling == null || !this.pulling.isAlive()) && !this.isPassenger(entityIn) && this.canPull(entityIn);
    }

    private boolean canPull(final Entity entity) {
        final String id = EntityType.getKey(entity.getType()).toString();
        final ArrayList<String> allowed = this.getConfig().pullAnimals.get();
        if (allowed.isEmpty()) {
            // real semantics = can wear saddle and not steered by item
            return entity instanceof PlayerEntity || entity instanceof IEquipable && ((IEquipable) entity).func_230264_L__() && !(entity instanceof IRideable);
        }
        return allowed.contains(id);
    }

    protected abstract AstikorCartsConfig.CartConfig getConfig();

    @Override
    public boolean attackEntityFrom(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.world.isRemote && this.isAlive()) {
            if (source == DamageSource.CACTUS) {
                return false;
            }
            if (source instanceof IndirectEntityDamageSource && source.getTrueSource() != null && this.isPassenger(source.getTrueSource())) {
                return false;
            }
            this.setForwardDirection(-this.getForwardDirection());
            this.setTimeSinceHit(10);
            this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
            final boolean flag = source.getTrueSource() instanceof PlayerEntity && ((PlayerEntity) source.getTrueSource()).abilities.isCreativeMode;
            if (flag || this.getDamageTaken() > 40.0F) {
                this.onDestroyed(source, flag);
                this.setPulling(null);
                this.remove();
            }
            return true;
        }
        return false;
    }

    /**
     * Called when the cart has been destroyed by a creative player or the carts
     * health hit 0.
     *
     * @param source
     * @param byCreativePlayer
     */
    public void onDestroyed(final DamageSource source, final boolean byCreativePlayer) {
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            if (!byCreativePlayer) {
                this.entityDropItem(this.getCartItem());
            }
            this.onDestroyedAndDoDrops(source);
        }
    }

    /**
     * This method is called from {@link #onDestroyed(DamageSource, boolean)} if the
     * GameRules allow entities to drop items.
     *
     * @param source
     */
    public void onDestroyedAndDoDrops(final DamageSource source) {

    }

    private void tickLerp() {
        if (this.lerpSteps > 0) {
            final double dx = (this.lerpX - this.getPosX()) / this.lerpSteps;
            final double dy = (this.lerpY - this.getPosY()) / this.lerpSteps;
            final double dz = (this.lerpZ - this.getPosZ()) / this.lerpSteps;
            this.rotationYaw = (float) (this.rotationYaw + MathHelper.wrapDegrees(this.lerpYaw - this.rotationYaw) / this.lerpSteps);
            this.rotationPitch = (float) (this.rotationPitch + (this.lerpPitch - this.rotationPitch) / this.lerpSteps);
            this.lerpSteps--;
            this.onGround = true;
            this.move(MoverType.SELF, new Vector3d(dx, dy, dz));
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void setPositionAndRotationDirect(final double x, final double y, final double z, final float yaw, final float pitch, final int posRotationIncrements, final boolean teleport) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = yaw;
        this.lerpPitch = pitch;
        this.lerpSteps = posRotationIncrements;
    }

    @Override
    protected void addPassenger(final Entity passenger) {
        super.addPassenger(passenger);
        if (this.canPassengerSteer() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.setPositionAndRotation(this.lerpX, this.lerpY, this.lerpZ, (float) this.lerpYaw, (float) this.lerpPitch);
        }
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        final List<Entity> passengers = this.getPassengers();
        if (passengers.isEmpty()) {
            return null;
        }
        final Entity first = passengers.get(0);
        if (first instanceof AnimalEntity || !(first instanceof LivingEntity)) {
            return null;
        }
        return (LivingEntity) first;
    }

    @Override
    public boolean canPassengerSteer() {
        return false;
    }

    @Override
    public Vector3d func_230268_c_(final LivingEntity rider) {
        for (final float angle : rider.getPrimaryHand() == HandSide.RIGHT ? new float[] { 90.0F, -90.0F } : new float[] { -90.0F, 90.0F }) {
            final Vector3d pos = this.dismount(func_233559_a_(this.getWidth(), rider.getWidth(), this.rotationYaw + angle), rider);
            if (pos != null) return pos;
        }
        return this.getPositionVec();
    }

    private Vector3d dismount(final Vector3d dir, LivingEntity rider) {
        final double x = this.getPosX() + dir.x;
        final double y = this.getBoundingBox().minY;
        final double z = this.getPosZ() + dir.z;
        final double limit = this.getBoundingBox().maxY + 0.75D;
        final BlockPos.Mutable blockPos = new BlockPos.Mutable();
        for (final Pose pose : rider.getAvailablePoses()) {
            blockPos.setPos(x, y, z);
            while (blockPos.getY() < limit) {
                final double ground = this.world.func_242403_h(blockPos);
                if (blockPos.getY() + ground > limit) break;
                if (TransportationHelper.func_234630_a_(ground)) {
                    final Vector3d pos = new Vector3d(x, blockPos.getY() + ground, z);
                    if (TransportationHelper.func_234631_a_(this.world, rider, rider.getPoseAABB(pose).offset(pos))) {
                        rider.setPose(pose);
                        return pos;
                    }
                }
                blockPos.move(Direction.UP);
            }
        }
        return null;
    }

    public void setDamageTaken(final float damageTaken) {
        this.dataManager.set(DAMAGE_TAKEN, damageTaken);
    }

    public float getDamageTaken() {
        return this.dataManager.get(DAMAGE_TAKEN);
    }

    public void setTimeSinceHit(final int timeSinceHit) {
        this.dataManager.set(TIME_SINCE_HIT, timeSinceHit);
    }

    public int getTimeSinceHit() {
        return this.dataManager.get(TIME_SINCE_HIT);
    }

    public void setForwardDirection(final int forwardDirection) {
        this.dataManager.set(FORWARD_DIRECTION, forwardDirection);
    }

    public int getForwardDirection() {
        return this.dataManager.get(FORWARD_DIRECTION);
    }

    @Override
    public ItemStack getPickedResult(final RayTraceResult target) {
        return new ItemStack(this.getCartItem());
    }

    @Override
    public void writeSpawnData(final PacketBuffer buffer) {
        buffer.writeInt(this.pulling != null ? this.pulling.getEntityId() : -1);
    }

    @Override
    public void readSpawnData(final PacketBuffer additionalData) {
        this.pullingId = additionalData.readInt();
    }

    @Override
    protected void registerData() {
        this.dataManager.register(TIME_SINCE_HIT, 0);
        this.dataManager.register(FORWARD_DIRECTION, 1);
        this.dataManager.register(DAMAGE_TAKEN, 0.0F);
    }

    @Override
    protected void readAdditional(final CompoundNBT compound) {
        if (compound.hasUniqueId("PullingUUID")) {
            this.pullingUUID = compound.getUniqueId("PullingUUID");
        }
    }

    @Override
    protected void writeAdditional(final CompoundNBT compound) {
        if (this.pulling != null) {
            compound.putUniqueId("PullingUUID", this.pullingUUID);
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public RenderInfo getInfo(final float delta) {
        return new RenderInfo(delta);
    }

    public void toggleSlow() {
        final Entity pulling = this.pulling;
        if (!(pulling instanceof LivingEntity)) return;
        final ModifiableAttributeInstance speed = ((LivingEntity) pulling).getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;
        final AttributeModifier modifier = speed.getModifier(PULL_SLOWLY_MODIFIER_UUID);
        if (modifier == null) {
            speed.applyNonPersistentModifier(new AttributeModifier(
                PULL_SLOWLY_MODIFIER_UUID,
                "Pull slowly modifier",
                this.getConfig().slowSpeed.get(),
                AttributeModifier.Operation.MULTIPLY_TOTAL
            ));
        } else {
            speed.removeModifier(modifier);
        }
    }

    public class RenderInfo {
        final float delta;
        Vector3d target;
        float yaw = Float.NaN;
        float pitch = Float.NaN;

        public RenderInfo(final float delta) {
            this.delta = delta;
        }

        public Vector3d getTarget() {
            if (this.target == null) {
                if (AbstractDrawnEntity.this.pulling == null) {
                    this.target = AbstractDrawnEntity.this.getLook(this.delta);
                } else {
                    this.target = AbstractDrawnEntity.this.getRelativeTargetVec(this.delta);
                }
            }
            return this.target;
        }

        public float getYaw() {
            if (Float.isNaN(this.yaw)) {
                if (AbstractDrawnEntity.this.pulling == null) {
                    this.yaw = MathHelper.lerp(this.delta, AbstractDrawnEntity.this.prevRotationYaw, AbstractDrawnEntity.this.rotationYaw);
                } else {
                    this.yaw = AbstractDrawnEntity.getYaw(this.getTarget());
                }
            }
            return this.yaw;
        }

        public float getPitch() {
            if (Float.isNaN(this.pitch)) {
                if (AbstractDrawnEntity.this.pulling == null) {
                    this.pitch = MathHelper.lerp(this.delta, AbstractDrawnEntity.this.prevRotationPitch, AbstractDrawnEntity.this.rotationPitch);
                } else {
                    this.pitch = AbstractDrawnEntity.getPitch(this.target);
                }
            }
            return this.pitch;
        }
    }
}

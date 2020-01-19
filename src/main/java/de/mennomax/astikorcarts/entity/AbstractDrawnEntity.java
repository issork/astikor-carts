package de.mennomax.astikorcarts.entity;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.init.Sounds;
import de.mennomax.astikorcarts.network.PacketHandler;
import de.mennomax.astikorcarts.network.packets.SPacketDrawnUpdate;
import de.mennomax.astikorcarts.util.CartWheel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
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
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class AbstractDrawnEntity extends Entity implements IEntityAdditionalSpawnData {

    private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.createKey(AbstractDrawnEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> FORWARD_DIRECTION = EntityDataManager.createKey(AbstractDrawnEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.createKey(AbstractDrawnEntity.class, DataSerializers.FLOAT);
    public static final UUID PULL_SLOWLY_MODIFIER_UUID = UUID.fromString("49B0E52E-48F2-4D89-BED7-4F5DF26F1263");
    public static final AttributeModifier PULL_SLOWLY_MODIFIER = (new AttributeModifier(PULL_SLOWLY_MODIFIER_UUID, "Pull slowly modifier", AstikorCartsConfig.COMMON.speedModifier.get(), Operation.MULTIPLY_TOTAL)).setSaved(false);
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;
    private double lerpPitch;
    protected boolean pullingOnGroundLastTick;
    protected List<CartWheel> wheels;
    private int pullingId = -1;
    private UUID pullingUUID = null;
    protected double spacing = 2.4D;
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
        Vec3d targetVec = this.getRelativeTargetVec(1.0F);
        this.handleRotation(targetVec);
        final double dRotation = this.prevRotationYaw - this.rotationYaw;
        if (dRotation < -180.0D) {
            this.prevRotationYaw += 360.0F;
        } else if (dRotation >= 180.0D) {
            this.prevRotationYaw -= 360.0F;
        }
        if (this.pulling.onGround) {
            targetVec = new Vec3d(targetVec.x, 0.0D, targetVec.z);
        }
        final double targetVecLength = targetVec.length();
        final double r = 0.2D;
        final double diff = targetVecLength - this.spacing;
        final Vec3d move;
        if (Math.abs(diff) < r) {
            move = this.getMotion();
        } else {
            move = this.getMotion().add(targetVec.subtract(targetVec.normalize().scale(this.spacing + r * Math.signum(diff))));
        }
        this.onGround = true;
        this.move(MoverType.SELF, move);
        if (!this.isAlive()) {
            return;
        }
        if (this.world.isRemote) {
            for (final CartWheel wheel : this.wheels) {
                wheel.tick();
            }
        } else {
            targetVec = this.getRelativeTargetVec(1.0F);
            if (targetVec.length() > this.spacing + 1.0D) {
                this.setPulling(null);
            }
        }
        this.updatePassengers();
        if (this.drawn != null) {
            this.drawn.pulledTick();
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
            final Vec3d start = new Vec3d(this.posX, this.posY + this.getHeight(), this.posZ);
            final Vec3d end = new Vec3d(this.pulling.posX, this.pulling.posY + this.getHeight() / 2, this.pulling.posZ);
            final RayTraceResult result = this.world.rayTraceBlocks(new RayTraceContext(start, end, BlockMode.COLLIDER, FluidMode.NONE, this));
            if (result != null) {
                return result.getType() == Type.BLOCK;
            }
        }
        return false;
    }

    public void updatePassengers() {
        for (final Entity passenger : this.getPassengers()) {
            this.updatePassenger(passenger);
        }
    }

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
                        ((LivingEntity) this.pulling).getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(PULL_SLOWLY_MODIFIER);
                    } else if (this.pulling instanceof AbstractDrawnEntity) {
                        ((AbstractDrawnEntity) this.pulling).drawn = null;
                    }
                    PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new SPacketDrawnUpdate(-1, this.getEntityId()));
                    this.pullingUUID = null;
                    if (this.ticksExisted > 20) {
                        this.playDetachSound();
                    }
                } else {
                    if (entityIn instanceof MobEntity) {
                        ((MobEntity) entityIn).getNavigator().clearPath();
                    }
                    if (!(entityIn instanceof AbstractDrawnEntity)) {
                        AstikorCarts.SERVERPULLMAP.put(entityIn, this);
                    }
                    PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new SPacketDrawnUpdate(entityIn.getEntityId(), this.getEntityId()));
                    this.pullingUUID = entityIn.getUniqueID();
                    if (this.ticksExisted > 20) {
                        this.playAttachSound();
                    }
                }
                if (entityIn instanceof AbstractDrawnEntity) {
                    ((AbstractDrawnEntity) entityIn).drawn = this;
                }
                this.pulling = entityIn;

            }
        } else {
            if (entityIn == null) {
                if (this.pulling instanceof AbstractDrawnEntity) {
                    ((AbstractDrawnEntity) this.pulling).drawn = null;
                }
                this.pullingId = -1;
                for (final CartWheel wheel : this.wheels) {
                    wheel.clearIncrement();
                }
            } else {
                this.pullingId = entityIn.getEntityId();
                if (!(entityIn instanceof AbstractDrawnEntity)) {
                    AstikorCarts.CLIENTPULLMAP.put(entityIn, this);
                }
                if (entityIn instanceof AbstractDrawnEntity) {
                    ((AbstractDrawnEntity) entityIn).drawn = this;
                }
            }
            this.pulling = entityIn;
        }
    }

    private void playAttachSound() {
        this.world.playSound(null, this.posX, this.posY, this.posZ, Sounds.CART_ATTACHED, this.getSoundCategory(), 0.2F, 1.0F);
    }

    private void playDetachSound() {
        this.world.playSound(null, this.posX, this.posY, this.posZ, Sounds.CART_DETACHED, this.getSoundCategory(), 0.2F, 0.1F);
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
        if (!this.isAlive() || this.getPulling() == null || !this.getPulling().isAlive()) {
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
    public Vec3d getRelativeTargetVec(final float delta) {
        if (delta == 1.0F) {
            return new Vec3d(this.pulling.posX - this.posX, this.pulling.posY - this.posY, this.pulling.posZ - this.posZ);
        }
        final double px = MathHelper.lerp(delta, this.pulling.lastTickPosX, this.pulling.posX);
        final double py = MathHelper.lerp(delta, this.pulling.lastTickPosY, this.pulling.posY);
        final double pz = MathHelper.lerp(delta, this.pulling.lastTickPosZ, this.pulling.posZ);
        final double x = MathHelper.lerp(delta, this.lastTickPosX, this.posX);
        final double y = MathHelper.lerp(delta, this.lastTickPosY, this.posY);
        final double z = MathHelper.lerp(delta, this.lastTickPosZ, this.posZ);
        return new Vec3d(px - x, py - y, pz - z);
    }

    /**
     * Handles the rotation of this cart and its components.
     *
     * @param target
     */
    public void handleRotation(final Vec3d target) {
        this.rotationYaw = getYaw(target);
        this.rotationPitch = getPitch(target);
    }

    public static float getYaw(final Vec3d vec) {
        return MathHelper.wrapDegrees((float) Math.toDegrees(-MathHelper.atan2(vec.x, vec.z)));
    }

    public static float getPitch(final Vec3d vec) {
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
        if (entityIn == null) {
            return true;
        }
        return (this.pulling == null || !this.pulling.isAlive()) && !this.isPassenger(entityIn) && this.isInPullList(entityIn.getType().getRegistryName().toString());
    }

    protected boolean isInPullList(final String entityId) {
        return this.getAllowedEntityList().contains(entityId);
    }

    protected ArrayList<String> getAllowedEntityList() {
        return new ArrayList<String>();
    }

    @Override
    public boolean attackEntityFrom(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.world.isRemote && this.isAlive()) {
            if (source instanceof IndirectEntityDamageSource && source.getTrueSource() != null && this.isPassenger(source.getTrueSource())) {
                return false;
            } else {
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
        }
        return true;
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
            final double dx = (this.lerpX - this.posX) / this.lerpSteps;
            final double dy = (this.lerpY - this.posY) / this.lerpSteps;
            final double dz = (this.lerpZ - this.posZ) / this.lerpSteps;
            this.rotationYaw = (float) (this.rotationYaw + MathHelper.wrapDegrees(this.lerpYaw - this.rotationYaw) / this.lerpSteps);
            this.rotationPitch = (float) (this.rotationPitch + (this.lerpPitch - this.rotationPitch) / this.lerpSteps);
            this.lerpSteps--;
            this.onGround = true;
            this.move(MoverType.SELF, new Vec3d(dx, dy, dz));
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
        this.lerpSteps = teleport ? 1 : this.pulling == null ? 3 : 10;
    }

    @Override
    protected void addPassenger(final Entity passenger) {
        super.addPassenger(passenger);
        if (this.canPassengerSteer() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.posX = this.lerpX;
            this.posY = this.lerpY;
            this.posZ = this.lerpZ;
            this.rotationYaw = (float) this.lerpYaw;
            this.rotationPitch = (float) this.lerpPitch;
        }
    }

    @Override
    protected void removePassenger(final Entity passenger) {
        super.removePassenger(passenger);
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

    public class RenderInfo {
        final float delta;
        Vec3d target;
        float yaw = Float.NaN;
        float pitch = Float.NaN;

        public RenderInfo(final float delta) {
            this.delta = delta;
        }

        public Vec3d getTarget() {
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

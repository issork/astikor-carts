package de.mennomax.astikorcarts.entity;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.network.clientbound.UpdateDrawnMessage;
import de.mennomax.astikorcarts.util.CartWheel;
import de.mennomax.astikorcarts.world.AstikorWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class AbstractDrawnEntity extends Entity implements IEntityAdditionalSpawnData {
    private static final EntityDataAccessor<Integer> TIME_SINCE_HIT = SynchedEntityData.defineId(AbstractDrawnEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FORWARD_DIRECTION = SynchedEntityData.defineId(AbstractDrawnEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE_TAKEN = SynchedEntityData.defineId(AbstractDrawnEntity.class, EntityDataSerializers.FLOAT);
    private static final UUID PULL_SLOWLY_MODIFIER_UUID = UUID.fromString("49B0E52E-48F2-4D89-BED7-4F5DF26F1263");
    private static final UUID PULL_MODIFIER_UUID = UUID.fromString("BA594616-5BE3-46C6-8B40-7D0230C64B77");
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

    public AbstractDrawnEntity(final EntityType<? extends Entity> entityTypeIn, final Level worldIn) {
        super(entityTypeIn, worldIn);
        this.maxUpStep = 1.2F;
        this.blocksBuilding = true;
        this.initWheels();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(3.0D, 3.0D, 3.0D);
    }

    @Override
    public void tick() {
        if (this.getTimeSinceHit() > 0) {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }
        if (!this.isNoGravity()) {
            this.setDeltaMovement(0.0D, this.getDeltaMovement().y - 0.08D, 0.0D);
        }
        if (this.getDamageTaken() > 0.0F) {
            this.setDamageTaken(this.getDamageTaken() - 1.0F);
        }
        super.tick();
        this.tickLerp();
        if (this.pulling == null) {
            this.setXRot(25.0F);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.attemptReattach();
        }
        for (final Entity entity : this.level.getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this))) {
            this.push(entity);
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
        Vec3 targetVec = this.getRelativeTargetVec(1.0F);
        this.handleRotation(targetVec);
        while (this.getYRot() - this.yRotO < -180.0F) {
            this.yRotO -= 360.0F;
        }
        while (this.getYRot() - this.yRotO >= 180.0F) {
            this.yRotO += 360.0F;
        }
        if (this.pulling.isOnGround()) {
            targetVec = new Vec3(targetVec.x, 0.0D, targetVec.z);
        }
        final double targetVecLength = targetVec.length();
        final double r = 0.2D;
        final double relativeSpacing = Math.max(this.spacing + 0.5D * this.pulling.getBbWidth(), 1.0D);
        final double diff = targetVecLength - relativeSpacing;
        final Vec3 move;
        if (Math.abs(diff) < r) {
            move = this.getDeltaMovement();
        } else {
            move = this.getDeltaMovement().add(targetVec.subtract(targetVec.normalize().scale(relativeSpacing + r * Math.signum(diff))));
        }
        this.onGround = true;
        final double startX = this.getX();
        final double startY = this.getY();
        final double startZ = this.getZ();
        this.move(MoverType.SELF, move);
        if (!this.isAlive()) {
            return;
        }
        this.addStats(this.getX() - startX, this.getY() - startY, this.getZ() - startZ);
        if (this.level.isClientSide) {
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
        if (!this.level.isClientSide) {
            final int cm = Math.round(Mth.sqrt((float) (x * x + y * y + z * z)) * 100.0F);
            if (cm > 0) {
                for (final Entity passenger : this.getPassengers()) {
                    if (passenger instanceof Player player) {
                        player.awardStat(AstikorCarts.Stats.CART_ONE_CM.get(), cm);
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
        if (this.horizontalCollision) {
            final Vec3 start = new Vec3(this.getX(), this.getY() + this.getBbHeight(), this.getZ());
            final Vec3 end = new Vec3(this.pulling.getX(), this.pulling.getY() + this.pulling.getBbHeight() / 2, this.pulling.getZ());
            final BlockHitResult result = this.level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            return result.getType() == HitResult.Type.BLOCK;
        }
        return false;
    }

    public void updatePassengers() {
        for (final Entity passenger : this.getPassengers()) {
            this.positionRider(passenger);
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
        if (!this.level.isClientSide) {
            if (this.canBePulledBy(entityIn)) {
                if (entityIn == null) {
                    if (this.pulling instanceof LivingEntity) {
                        final AttributeInstance attr = ((LivingEntity) this.pulling).getAttribute(Attributes.MOVEMENT_SPEED);
                        if (attr != null) {
                            attr.removeModifier(PULL_SLOWLY_MODIFIER_UUID);
                            attr.removeModifier(PULL_MODIFIER_UUID);
                        }
                    } else if (this.pulling instanceof AbstractDrawnEntity) {
                        ((AbstractDrawnEntity) this.pulling).drawn = null;
                    }
                    AstikorCarts.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new UpdateDrawnMessage(-1, this.getId()));
                    this.pullingUUID = null;
                    if (this.tickCount > 20) {
                        this.playDetachSound();
                    }
                } else {
                    if (entityIn instanceof LivingEntity && this.getConfig().pullSpeed.get() != 0.0D) {
                        final AttributeInstance attr = ((LivingEntity) entityIn).getAttribute(Attributes.MOVEMENT_SPEED);
                        if (attr != null && attr.getModifier(PULL_MODIFIER_UUID) == null) {
                            attr.addTransientModifier(new AttributeModifier(
                                PULL_MODIFIER_UUID,
                                "Pull modifier",
                                this.getConfig().pullSpeed.get(),
                                AttributeModifier.Operation.MULTIPLY_TOTAL
                            ));
                        }
                    }
                    if (entityIn instanceof PathfinderMob pathfinder) {
                        pathfinder.getNavigation().stop();
                    }
                    AstikorCarts.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new UpdateDrawnMessage(entityIn.getId(), this.getId()));
                    this.pullingUUID = entityIn.getUUID();
                    if (this.tickCount > 20) {
                        this.playAttachSound();
                    }
                }
                if (entityIn instanceof AbstractDrawnEntity) {
                    ((AbstractDrawnEntity) entityIn).drawn = this;
                }
                this.pulling = entityIn;
                AstikorWorld.get(this.level).ifPresent(w -> w.addPulling(this));

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
                this.pullingId = entityIn.getId();
                if (entityIn instanceof AbstractDrawnEntity) {
                    ((AbstractDrawnEntity) entityIn).drawn = this;
                }
            }
            this.pulling = entityIn;
            AstikorWorld.get(this.level).ifPresent(w -> w.addPulling(this));
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
        if (this.level.isClientSide) {
            if (this.pullingId != -1) {
                final Entity entity = this.level.getEntity(this.pullingId);
                if (entity != null && entity.isAlive()) {
                    this.setPulling(entity);
                }
            }
        } else {
            if (this.pullingUUID != null) {
                final Entity entity = ((ServerLevel) this.level).getEntity(this.pullingUUID);
                if (entity != null && entity.isAlive()) {
                    this.setPulling(entity);
                }
            }
        }
    }

    public boolean shouldStopPulledTick() {
        if (!this.isAlive() || this.getPulling() == null || !this.getPulling().isAlive() || this.getPulling().isPassenger()) {
            if (this.pulling != null && this.pulling instanceof Player) {
                this.setPulling(null);
            } else {
                this.pulling = null;
            }
            return true;
        } else if (!this.level.isClientSide && this.shouldRemovePulling()) {
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
    public Vec3 getRelativeTargetVec(final float delta) {
        final double x;
        final double y;
        final double z;
        if (delta == 1.0F) {
            x = this.pulling.getX() - this.getX();
            y = this.pulling.getY() - this.getY();
            z = this.pulling.getZ() - this.getZ();
        } else {
            x = Mth.lerp(delta, this.pulling.xOld, this.pulling.getX()) - Mth.lerp(delta, this.xOld, this.getX());
            y = Mth.lerp(delta, this.pulling.yOld, this.pulling.getY()) - Mth.lerp(delta, this.yOld, this.getY());
            z = Mth.lerp(delta, this.pulling.zOld, this.pulling.getZ()) - Mth.lerp(delta, this.zOld, this.getZ());
        }
        final float yaw = (float) Math.toRadians(this.pulling.getYRot());
        final float nx = -Mth.sin(yaw);
        final float nz = Mth.cos(yaw);
        final double r = 0.2D;
        return new Vec3(x + nx * r, y, z + nz * r);
    }

    /**
     * Handles the rotation of this cart and its components.
     *
     * @param target
     */
    public void handleRotation(final Vec3 target) {
        this.setYRot(getYaw(target));
        this.setXRot(getPitch(target));
    }

    public static float getYaw(final Vec3 vec) {
        return Mth.wrapDegrees((float) Math.toDegrees(-Mth.atan2(vec.x, vec.z)));
    }

    public static float getPitch(final Vec3 vec) {
        return Mth.wrapDegrees((float) Math.toDegrees(-Mth.atan2(vec.y, Mth.sqrt((float) (vec.x * vec.x + vec.z * vec.z)))));
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
        if (this.level.isClientSide) {
            return true;
        }
        if (entityIn == null) {
            return true;
        }
        return (this.pulling == null || !this.pulling.isAlive()) && !this.hasPassenger(entityIn) && this.canPull(entityIn);
    }

    private boolean canPull(final Entity entity) {
        if (entity instanceof Saddleable && !((Saddleable) entity).isSaddleable()) return false;
        if (entity instanceof TamableAnimal && !((TamableAnimal) entity).isTame()) return false;
        final ArrayList<String> allowed = this.getConfig().pullAnimals.get();
        if (allowed.isEmpty()) {
            return entity instanceof Player ||
                entity instanceof Saddleable && !(entity instanceof ItemSteerable);
        }
        return allowed.contains(EntityType.getKey(entity.getType()).toString());
    }

    protected abstract AstikorCartsConfig.CartConfig getConfig();

    @Override
    public boolean hurt(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.level.isClientSide && this.isAlive()) {
            if (source == DamageSource.CACTUS) {
                return false;
            }
            if (source instanceof IndirectEntityDamageSource && source.getEntity() != null && this.hasPassenger(source.getEntity())) {
                return false;
            }
            this.setForwardDirection(-this.getForwardDirection());
            this.setTimeSinceHit(10);
            this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
            final boolean flag = source.getEntity() instanceof Player && ((Player) source.getEntity()).getAbilities().instabuild;
            if (flag || this.getDamageTaken() > 40.0F) {
                this.onDestroyed(source, flag);
                this.setPulling(null);
                this.discard();
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
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            if (!byCreativePlayer) {
                this.spawnAtLocation(this.getCartItem());
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
            final double dx = (this.lerpX - this.getX()) / this.lerpSteps;
            final double dy = (this.lerpY - this.getY()) / this.lerpSteps;
            final double dz = (this.lerpZ - this.getZ()) / this.lerpSteps;
            this.setYRot((float) (this.getYRot() + Mth.wrapDegrees(this.lerpYaw - this.getYRot()) / this.lerpSteps));
            this.setXRot((float) (this.getXRot() + (this.lerpPitch - this.getXRot()) / this.lerpSteps));
            this.lerpSteps--;
            this.onGround = true;
            this.move(MoverType.SELF, new Vec3(dx, dy, dz));
            this.setRot(this.getYRot(), this.getXRot());
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpTo(final double x, final double y, final double z, final float yaw, final float pitch, final int posRotationIncrements, final boolean teleport) {
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
        if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.moveTo(this.lerpX, this.lerpY, this.lerpZ, (float) this.lerpYaw, (float) this.lerpPitch);
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
        if (first instanceof Animal || !(first instanceof LivingEntity)) {
            return null;
        }
        return (LivingEntity) first;
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return false;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(final LivingEntity rider) {
        for (final float angle : rider.getMainArm() == HumanoidArm.RIGHT ? new float[] { 90.0F, -90.0F } : new float[] { -90.0F, 90.0F }) {
            final Vec3 pos = this.dismount(getCollisionHorizontalEscapeVector(this.getBbWidth(), rider.getBbWidth(), this.getYRot() + angle), rider);
            if (pos != null) return pos;
        }
        return this.position();
    }

    private Vec3 dismount(final Vec3 dir, LivingEntity rider) {
        final double x = this.getX() + dir.x;
        final double y = this.getBoundingBox().minY;
        final double z = this.getZ() + dir.z;
        final double limit = this.getBoundingBox().maxY + 0.75D;
        final BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        for (final Pose pose : rider.getDismountPoses()) {
            blockPos.set(x, y, z);
            while (blockPos.getY() < limit) {
                final double ground = this.level.getBlockFloorHeight(blockPos);
                if (blockPos.getY() + ground > limit) break;
                if (DismountHelper.isBlockFloorValid(ground)) {
                    final Vec3 pos = new Vec3(x, blockPos.getY() + ground, z);
                    if (DismountHelper.canDismountTo(this.level, rider, rider.getLocalBoundsForPose(pose).move(pos))) {
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
        this.entityData.set(DAMAGE_TAKEN, damageTaken);
    }

    public float getDamageTaken() {
        return this.entityData.get(DAMAGE_TAKEN);
    }

    public void setTimeSinceHit(final int timeSinceHit) {
        this.entityData.set(TIME_SINCE_HIT, timeSinceHit);
    }

    public int getTimeSinceHit() {
        return this.entityData.get(TIME_SINCE_HIT);
    }

    public void setForwardDirection(final int forwardDirection) {
        this.entityData.set(FORWARD_DIRECTION, forwardDirection);
    }

    public int getForwardDirection() {
        return this.entityData.get(FORWARD_DIRECTION);
    }

    @Override
    public ItemStack getPickedResult(final HitResult target) {
        return new ItemStack(this.getCartItem());
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public void writeSpawnData(final FriendlyByteBuf buffer) {
        buffer.writeInt(this.pulling != null ? this.pulling.getId() : -1);
    }

    @Override
    public void readSpawnData(final FriendlyByteBuf additionalData) {
        this.pullingId = additionalData.readInt();
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TIME_SINCE_HIT, 0);
        this.entityData.define(FORWARD_DIRECTION, 1);
        this.entityData.define(DAMAGE_TAKEN, 0.0F);
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compound) {
        if (compound.hasUUID("PullingUUID")) {
            this.pullingUUID = compound.getUUID("PullingUUID");
        }
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compound) {
        if (this.pulling != null) {
            compound.putUUID("PullingUUID", this.pullingUUID);
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public RenderInfo getInfo(final float delta) {
        return new RenderInfo(delta);
    }

    public void toggleSlow() {
        final Entity pulling = this.pulling;
        if (!(pulling instanceof LivingEntity)) return;
        final AttributeInstance speed = ((LivingEntity) pulling).getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;
        final AttributeModifier modifier = speed.getModifier(PULL_SLOWLY_MODIFIER_UUID);
        if (modifier == null) {
            speed.addTransientModifier(new AttributeModifier(
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
        Vec3 target;
        float yaw = Float.NaN;
        float pitch = Float.NaN;

        public RenderInfo(final float delta) {
            this.delta = delta;
        }

        public Vec3 getTarget() {
            if (this.target == null) {
                if (AbstractDrawnEntity.this.pulling == null) {
                    this.target = AbstractDrawnEntity.this.getViewVector(this.delta);
                } else {
                    this.target = AbstractDrawnEntity.this.getRelativeTargetVec(this.delta);
                }
            }
            return this.target;
        }

        public float getYaw() {
            if (Float.isNaN(this.yaw)) {
                if (AbstractDrawnEntity.this.pulling == null) {
                    this.yaw = Mth.lerp(this.delta, AbstractDrawnEntity.this.yRotO, AbstractDrawnEntity.this.getYRot());
                } else {
                    this.yaw = AbstractDrawnEntity.getYaw(this.getTarget());
                }
            }
            return this.yaw;
        }

        public float getPitch() {
            if (Float.isNaN(this.pitch)) {
                if (AbstractDrawnEntity.this.pulling == null) {
                    this.pitch = Mth.lerp(this.delta, AbstractDrawnEntity.this.xRotO, AbstractDrawnEntity.this.getXRot());
                } else {
                    this.pitch = AbstractDrawnEntity.getPitch(this.target);
                }
            }
            return this.pitch;
        }
    }
}

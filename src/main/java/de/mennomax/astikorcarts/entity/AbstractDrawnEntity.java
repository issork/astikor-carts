package de.mennomax.astikorcarts.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.network.PacketHandler;
import de.mennomax.astikorcarts.network.packets.SPacketDrawnUpdate;
import de.mennomax.astikorcarts.util.CartWheel;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.SoundEvents;
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

public abstract class AbstractDrawnEntity extends Entity implements IEntityAdditionalSpawnData {

    private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.<Integer>createKey(AbstractDrawnEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.<Float>createKey(AbstractDrawnEntity.class, DataSerializers.FLOAT);
    public static final UUID PULL_SLOWLY_MODIFIER_UUID = UUID.fromString("49B0E52E-48F2-4D89-BED7-4F5DF26F1263");
    public static final AttributeModifier PULL_SLOWLY_MODIFIER = (new AttributeModifier(PULL_SLOWLY_MODIFIER_UUID, "Pull slowly modifier", AstikorCartsConfig.COMMON.SPEEDMODIFIER.get().doubleValue(), Operation.MULTIPLY_TOTAL)).setSaved(false);
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;
    protected boolean pullingOnGroundLastTick;
    protected List<CartWheel> wheels;
    private int pullingId = -1;
    private UUID pullingUUID = null;
    protected double spacing = 2.4D;
    public Entity pulling;
    protected AbstractDrawnEntity drawn;

    public AbstractDrawnEntity(EntityType<? extends Entity> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.stepHeight = 1.2F;
        this.preventEntitySpawning = true;
        this.initWheels();
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
            this.move(MoverType.SELF, this.getMotion());
            this.attemptReattach();
        }
        for (Entity entity : this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), EntityPredicates.pushableBy(this))) {
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
        this.rotationPitch = 0.0F;
        final Vec3d targetVec = this.getRelativeTargetVec();
        this.handleRotation(targetVec);
        double dRotation = this.prevRotationYaw - this.rotationYaw;
        if (dRotation < -180.0D) {
            this.prevRotationYaw += 360.0F;
        } else if (dRotation >= 180.0D) {
            this.prevRotationYaw -= 360.0F;
        }
        double targetVecLength = Math.sqrt(targetVec.x * targetVec.x + targetVec.z * targetVec.z);
        double lookX = targetVec.x / targetVecLength;
        double lookZ = targetVec.z / targetVecLength;
        double moveX = targetVec.x - lookX * this.spacing;
        double moveZ = targetVec.z - lookZ * this.spacing;
        this.fallDistance = this.pulling.fallDistance;
        if (!this.pulling.onGround && this.fallDistance == 0.0F && !this.pullingOnGroundLastTick) {
            setMotion(moveX, targetVec.y, moveZ);
        }
        this.pullingOnGroundLastTick = this.pulling.onGround;
        this.setMotion(moveX, this.getMotion().y, moveZ);
        this.move(MoverType.SELF, this.getMotion());
        if (this.world.isRemote) {
            for (CartWheel wheel : this.wheels) {
                wheel.tick(lookX, lookZ);
            }
        }
        updatePassengers();
        if (drawn != null) {
            drawn.pulledTick();
        }
    }

    public void initWheels() {
        this.wheels = Arrays.asList(new CartWheel(this, 0.9F), new CartWheel(this, -0.9F));
    }

    /**
     * 
     * @return Whether the currently pulling entity should stop pulling this cart.
     */
    public boolean shouldRemovePulling() {
        if (this.collidedHorizontally) {
            final Vec3d start = new Vec3d(this.posX, this.posY + this.getHeight(), this.posZ);
            final Vec3d end = new Vec3d(this.pulling.posX, this.pulling.posY + this.getHeight() / 2, this.pulling.posZ);
            RayTraceResult result = this.world.rayTraceBlocks(new RayTraceContext(start, end, BlockMode.COLLIDER, FluidMode.NONE, this));
            if (result != null) {
                if (result.getType() == Type.BLOCK) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updatePassengers() {
        for (Entity passenger : this.getPassengers()) {
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
    public void setPulling(Entity entityIn) {
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
                } else {
                    if (entityIn instanceof MobEntity) {
                        ((MobEntity) entityIn).getNavigator().clearPath();
                    }
                    if(!(entityIn instanceof AbstractDrawnEntity)) {
                        AstikorCarts.SERVERPULLMAP.put(entityIn, this);
                    }
                    PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new SPacketDrawnUpdate(entityIn.getEntityId(), this.getEntityId()));
                    this.pullingUUID = entityIn.getUniqueID();
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
                for (CartWheel wheel : this.wheels) {
                    wheel.clearIncrement();
                }
                if (this.ticksExisted > 20) {
                    this.world.playSound(Minecraft.getInstance().player, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ITEM_BREAK, this.getSoundCategory(), 0.5F, 0.1F);
                }
            } else {
                this.pullingId = entityIn.getEntityId();
                if(!(entityIn instanceof AbstractDrawnEntity)) {
                    AstikorCarts.CLIENTPULLMAP.put(entityIn, this);
                }
                if (this.ticksExisted > 20) {
                    this.world.playSound(Minecraft.getInstance().player, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_HORSE_ARMOR, this.getSoundCategory(), 0.5F, 1.0F);
                }
                if (entityIn instanceof AbstractDrawnEntity) {
                    ((AbstractDrawnEntity) entityIn).drawn = this;
                }
            }
            this.pulling = entityIn;
        }
    }

    /**
     * Attempts to reattach the cart to the last pulling entity.
     */
    private void attemptReattach() {
        if (this.world.isRemote) {
            if (this.pullingId != -1) {
                Entity entity = this.world.getEntityByID(this.pullingId);
                if (entity != null && entity.isAlive()) {
                    this.setPulling(entity);
                }
            }
        } else {
            if (this.pullingUUID != null) {
                Entity entity = ((ServerWorld) this.world).getEntityByUuid(this.pullingUUID);
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
     * 
     * @return The position this cart should always face and travel towards.
     *         Relative to the cart position.
     */
    public Vec3d getRelativeTargetVec() {
        return new Vec3d(this.pulling.posX - this.posX, this.pulling.posY - this.posY, this.pulling.posZ - this.posZ);
    }

    /**
     * Handles the rotation of this cart and its components.
     * 
     * @param targetVecIn
     */
    public void handleRotation(Vec3d targetVecIn) {
        this.rotationYaw = (float) Math.toDegrees(-Math.atan2(targetVecIn.x, targetVecIn.z));
    }

    public double getWheelRotation(int wheel) {
        return this.wheels.get(wheel).getRotation();
    }

    public double getWheelRotationIncrement(int wheel) {
        return this.wheels.get(wheel).getRotationIncrement();
    }

    public abstract Item getCartItem();

    /**
     * Returns true if the passed in entity is allowed to pull this cart.
     * 
     * @param entityIn
     */
    protected boolean canBePulledBy(Entity entityIn) {
        if (entityIn == null) {
            return true;
        }
        return (this.pulling == null || !this.pulling.isAlive()) && !this.isPassenger(entityIn) && isInPullList(entityIn.getType().getRegistryName().toString());
    }

    protected boolean isInPullList(String entityId) {
        return this.getAllowedEntityList().contains(entityId);
    }

    protected ArrayList<String> getAllowedEntityList() {
        return new ArrayList<String>();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.world.isRemote && this.isAlive()) {
            if (source instanceof IndirectEntityDamageSource && source.getTrueSource() != null && this.isPassenger(source.getTrueSource())) {
                return false;
            } else {
                this.setTimeSinceHit(10);
                this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
                boolean flag = source.getTrueSource() instanceof PlayerEntity && ((PlayerEntity) source.getTrueSource()).abilities.isCreativeMode;
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
    public void onDestroyed(DamageSource source, boolean byCreativePlayer) {
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            if (!byCreativePlayer) {
                this.entityDropItem(this.getCartItem());
            }
            onDestroyedAndDoDrops(source);
        }
    }

    /**
     * This method is called from {@link #onDestroyed(DamageSource, boolean)} if the
     * GameRules allow entities to drop items.
     * 
     * @param source
     * @param byCreativePlayer
     */
    public void onDestroyedAndDoDrops(DamageSource source) {

    }

    private void tickLerp() {
        if (this.lerpSteps > 0) {
            double dx = this.posX + (this.lerpX - this.posX) / this.lerpSteps;
            double dy = this.posY + (this.lerpY - this.posY) / this.lerpSteps;
            double dz = this.posZ + (this.lerpZ - this.posZ) / this.lerpSteps;
            double drot = MathHelper.wrapDegrees(this.lerpYaw - this.rotationYaw);
            this.rotationYaw = (float) (this.rotationYaw + drot / this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(dx, dy, dz);
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
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        // if(this.distanceTravelled < 0) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = yaw;
        this.lerpSteps = posRotationIncrements;
        // }
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.canPassengerSteer() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.posX = this.lerpX;
            this.posY = this.lerpY;
            this.posZ = this.lerpZ;
            this.rotationYaw = (float) this.lerpYaw;
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
    }

    public void setDamageTaken(float damageTaken) {
        this.dataManager.set(DAMAGE_TAKEN, damageTaken);
    }

    public float getDamageTaken() {
        return this.dataManager.get(DAMAGE_TAKEN).floatValue();
    }

    public void setTimeSinceHit(int timeSinceHit) {
        this.dataManager.set(TIME_SINCE_HIT, timeSinceHit);
    }

    public int getTimeSinceHit() {
        return this.dataManager.get(TIME_SINCE_HIT).intValue();
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(this.getCartItem());
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeInt(this.pulling != null ? this.pulling.getEntityId() : -1);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        this.pullingId = additionalData.readInt();
    }

    @Override
    protected void registerData() {
        this.dataManager.register(TIME_SINCE_HIT, 0);
        this.dataManager.register(DAMAGE_TAKEN, 0.0F);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        if (compound.hasUniqueId("PullingUUID")) {
            this.pullingUUID = compound.getUniqueId("PullingUUID");
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        if (this.pulling != null) {
            compound.putUniqueId("PullingUUID", this.pullingUUID);
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}

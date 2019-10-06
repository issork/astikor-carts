package de.mennomax.astikorcarts.entity;

import java.util.UUID;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.network.PacketHandler;
import de.mennomax.astikorcarts.network.packets.SPacketDrawnUpdate;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
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
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
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
    public static final AttributeModifier PULL_SLOWLY_MODIFIER = (new AttributeModifier(PULL_SLOWLY_MODIFIER_UUID, "Pull slowly modifier", -0.65, Operation.MULTIPLY_TOTAL)).setSaved(false);
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;
    private boolean fellLastTick;
    private double motionY;
    protected double distanceTravelled;
    protected float wheelRotation[] = new float[2];
    private int pullingId = -1;
    private UUID pullingUUID = null;
    protected double spacing = 2.4D;
    private Entity pulling;

    public AbstractDrawnEntity(EntityType<? extends Entity> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.stepHeight = 1.2F;
        this.preventEntitySpawning = true;
    }

    @Override
    public void tick() {
        if (this.getTimeSinceHit() > 0) {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }
        if (!this.hasNoGravity()) {
            this.motionY -= 0.08D;
        }
        if (this.getDamageTaken() > 0.0F) {
            this.setDamageTaken(this.getDamageTaken() - 1.0F);
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationYaw = this.rotationYaw;
        super.tick();
        this.tickLerp();
        if (this.pulling == null) {
            this.setMotion(0, this.motionY, 0);
            this.move(MoverType.SELF, this.getMotion());
            this.motionY = this.getMotion().y - 0.08F;
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
     * when being pulled.
     */
    public void pulledTick() {
        if (!this.world.isRemote && shouldRemovePulling()) {
            this.setPulling(null);
            return;
        }
        Vec3d targetVec = this.getTargetVec();
        this.handleRotation(targetVec);
        double dRotation = this.prevRotationYaw - this.rotationYaw;
        if (dRotation < -180.0D) {
            this.prevRotationYaw += 360.0F;
        } else if (dRotation >= 180.0D) {
            this.prevRotationYaw -= 360.0F;
        }
        double lookX = MathHelper.sin(-this.rotationYaw * 0.017453292F - (float) Math.PI);
        double lookZ = MathHelper.cos(-this.rotationYaw * 0.017453292F - (float) Math.PI);
        double moveX = targetVec.x - this.posX + lookX * this.spacing;
        double moveZ = targetVec.z - this.posZ + lookZ * this.spacing;
        if (!this.pulling.onGround && this.pulling.fallDistance <= 0.0F) {
            this.motionY = targetVec.y - this.posY;
            this.fallDistance = 0.0F;
            this.fellLastTick = false;
        } else if (!fellLastTick) {
            this.motionY = 0.0D;
            this.fellLastTick = true;
        }
        this.setMotion(moveX, this.motionY, moveZ);
        this.move(MoverType.SELF, this.getMotion());
        Vec3d motion = getMotion();
        this.distanceTravelled = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        this.spawnWheelParticles();
        this.motionY = motion.y - 0.08F;
        if (this.world.isRemote) {
            // TODO: Make wheel rotate independently in tickWheels(double, double)
            // boolean travelledForward = Math.sqrt((moveX-lookX) * (moveX-lookX) +
            // (moveZ-lookZ) * (moveZ-lookZ)) > 1;
        }
    }

    /**
     * 
     * @return Whether the currently pulling entity should stop pulling this cart.
     */
    protected boolean shouldRemovePulling() {
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
                    if (this.pulling != null) {
                        if (this.pulling instanceof LivingEntity) {
                            ((LivingEntity) this.pulling).getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(PULL_SLOWLY_MODIFIER);
                        }
                        this.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.5F, 0.1F);
                    }
                    PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new SPacketDrawnUpdate(-1, this.getEntityId()));
                    this.pullingUUID = null;
                } else {
                    if (entityIn instanceof MobEntity) {
                        ((MobEntity) entityIn).getNavigator().clearPath();
                    }
                    AstikorCarts.SERVERPULLMAP.put(entityIn, this);
                    this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
                    PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new SPacketDrawnUpdate(entityIn.getEntityId(), this.getEntityId()));
                    this.pullingUUID = entityIn.getUniqueID();
                }
                this.pulling = entityIn;

            }
        } else {
            if (entityIn == null) {
                this.pullingId = -1;
            } else {
                this.pullingId = entityIn.getEntityId();
                AstikorCarts.CLIENTPULLMAP.put(entityIn, this);
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

    /**
     * 
     * @return The position this cart should always face and travel towards.
     */
    public Vec3d getTargetVec() {
        return new Vec3d(this.pulling.posX, this.pulling.posY, this.pulling.posZ);
    }

    /**
     * Handles the rotation of this cart and its components.
     * 
     * @param targetVecIn
     */
    public void handleRotation(Vec3d targetVecIn) {
        this.rotationYaw = (float) Math.toDegrees(-Math.atan2(targetVecIn.x - this.posX, targetVecIn.z - this.posZ));
        // System.out.println(Math.toDegrees(-Math.atan2(targetVecIn.x - this.posX,
        // targetVecIn.z - this.posZ)));
    }

    public void tickWheels(double distanceTravelled, double angle) {

    }

    public float getWheelRotation(int wheel) {
        return this.wheelRotation[wheel];
    }

    public abstract Item getCartItem();

    /**
     * Returns true if the passed in entity is allowed to pull this cart.
     * 
     * @param entityIn
     */
    protected boolean canBePulledBy(Entity entityIn) {
        return this.pulling == null || entityIn == null || !this.pulling.isAlive();
    }

    /**
     * Sadly {@link Entity#spawnRunningParticles()} might look weird due to
     * different tick order.
     */
    public void spawnWheelParticles() {
        if (!this.isInWater() && this.distanceTravelled > 0.2F) {
            this.createRunningParticles();
        }
    }

    @Override
    protected void createRunningParticles() {
        BlockPos blockpos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY - 0.2F), MathHelper.floor(this.posZ));
        BlockState blockstate = this.world.getBlockState(blockpos);
        if (!blockstate.addRunningEffects(world, blockpos, this))
            if (blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
                Vec3d vec3d = this.getMotion();
                double xOffset = MathHelper.sin((this.rotationYaw - 90) * 0.017453292F) * 0.75D;
                double zOffset = MathHelper.cos((this.rotationYaw - 90) * 0.017453292F) * 0.75D;
                this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(blockpos), this.posX + xOffset, this.posY, this.posZ - zOffset, vec3d.x, vec3d.y, vec3d.z);
                this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(blockpos), this.posX - xOffset, this.posY, this.posZ + zOffset, vec3d.x, vec3d.y, vec3d.z);
            }
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
     * Returns true if this entity should push and be pushed by other entities when
     * colliding.
     */
    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
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
    @OnlyIn(Dist.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = yaw;
        this.lerpSteps = posRotationIncrements;
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

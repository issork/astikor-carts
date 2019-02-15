package de.mennomax.astikorcarts.entity;

import java.util.UUID;

import de.mennomax.astikorcarts.capabilities.PullProvider;
import de.mennomax.astikorcarts.config.ModConfig;
import de.mennomax.astikorcarts.handler.PacketHandler;
import de.mennomax.astikorcarts.packets.SPacketDrawnUpdate;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractDrawn extends Entity implements IEntityAdditionalSpawnData
{
    public static final UUID PULL_SLOWLY_MODIFIER_UUID = UUID.fromString("49B0E52E-48F2-4D89-BED7-4F5DF26F1263");
    public static final AttributeModifier PULL_SLOWLY_MODIFIER = (new AttributeModifier(PULL_SLOWLY_MODIFIER_UUID, "Pull slowly modifier", ModConfig.speedModifier, 2)).setSaved(false);
    protected Entity pulling;
    @SideOnly(Side.CLIENT)
    private float wheelrot;
    @SideOnly(Side.CLIENT)
    private double factor;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;
    private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.<Integer>createKey(AbstractDrawn.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.<Float>createKey(AbstractDrawn.class, DataSerializers.FLOAT);
    protected double offsetFactor;

    public AbstractDrawn(World worldIn)
    {
        super(worldIn);
        this.stepHeight = 1.2F;
    }

    @Override
    public void onUpdate()
    {
        if (this.getTimeSinceHit() > 0)
        {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }

        if (this.getDamageTaken() > 0.0F)
        {
            this.setDamageTaken(this.getDamageTaken() - 1.0F);
        }
        super.onUpdate();
        if (!this.hasNoGravity())
        {
            this.motionY -= 0.04D;
        }
        if (this.pulling != null)
        {
            if (!this.world.isRemote)
            {
                if (this.shouldRemovePulling())
                {
                    this.setPulling(null);
                    return;
                }
            }
            Vec3d targetVec = this.getTargetVec();
            this.handleRotation(targetVec);
            double dRotation = (double) (this.prevRotationYaw - this.rotationYaw);
            if (dRotation < -180.0D)
            {
                this.prevRotationYaw += 360.0F;
            }
            else if (dRotation >= 180.0D)
            {
                this.prevRotationYaw -= 360.0F;
            }
            double lookX = MathHelper.sin(-this.rotationYaw * 0.017453292F - (float) Math.PI);
            double lookZ = MathHelper.cos(-this.rotationYaw * 0.017453292F - (float) Math.PI);
            double moveX = targetVec.x - this.posX + lookX * this.offsetFactor;
            double moveZ = targetVec.z - this.posZ + lookZ * this.offsetFactor;
            this.motionX = moveX;
            if (!this.onGround && this.fallDistance == 0.0D)
            {
                this.motionY = targetVec.y - this.posY;
            }
            this.motionZ = moveZ;
            
            if (this.world.isRemote)
            {
                this.factor = Math.sqrt((moveX+lookX) * (moveX+lookX) + (moveZ+lookZ) * (moveZ+lookZ)) > 1 ? Math.sqrt(moveX * moveX + moveZ * moveZ) : -Math.sqrt(moveX * moveX + moveZ * moveZ);
            }
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.tickLerp();
        }
        else
        {
            if (this.world.isRemote)
            {
                this.factor = 0.0D;
            }
        }
        for (Entity entity : this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox(), EntitySelectors.getTeamCollisionPredicate(this)))
        {
            this.applyEntityCollision(entity);
        }
    }
    
    /**
     * Handles the rotation of this cart and its components.
     * 
     * @param targetVecIn
     */
    public void handleRotation(Vec3d targetVecIn)
    {
        this.rotationYaw = (float) Math.toDegrees(-Math.atan2(targetVecIn.x - this.posX, targetVecIn.z - this.posZ));
    }

    /**
     * 
     * @return The position this cart should always face and travel towards.
     */
    public Vec3d getTargetVec()
    {
        return new Vec3d(this.pulling.posX, this.pulling.posY, this.pulling.posZ);
    }

    /**
     * 
     * @return Whether the currently pulling entity should stop pulling this cart.
     */
    protected boolean shouldRemovePulling()
    {
        if (this.pulling != null)
        {
            if (this.collidedHorizontally)
            {
                RayTraceResult result = this.world.rayTraceBlocks(new Vec3d(this.posX, this.posY + this.height, this.posZ), new Vec3d(this.pulling.posX, this.pulling.posY + this.height / 2, this.pulling.posZ), false, true, false);
                if (result != null)
                {
                    if (result.typeOfHit == Type.BLOCK)
                    {
                        return true;
                    }
                }
            }
        }
        return false || this.pulling.isDead;
    }

    /**
     * 
     * @param pullingIn {@link Entity} that tries to pull this cart.
     * @return {@code true}, if the entity is able pull this cart, {@code false}
     *         else.
     */
    public boolean canPull(Entity pullingIn)
    {
        return true;
    }

    public Entity getPulling()
    {
        return this.pulling;
    }

    /**
     * @param entityIn {@link Entity} that should pull this cart.
     */
    public void setPulling(Entity entityIn)
    {
        if (this.pulling == null || entityIn == null)
        {
            if (!this.world.isRemote)
            {
                if (entityIn == null)
                {
                    if (this.pulling != null)
                    {
                        if (this.pulling instanceof EntityLivingBase)
                        {
                            ((EntityLivingBase) this.pulling).getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(AbstractDrawn.PULL_SLOWLY_MODIFIER);
                        }
                        this.pulling.getCapability(PullProvider.PULL, null).setDrawn(null);
                        this.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.5F, 0.1F);
                    }
                    ((WorldServer) this.world).getEntityTracker().sendToTracking(this, PacketHandler.INSTANCE.getPacketFrom(new SPacketDrawnUpdate(-1, this.getEntityId())));
                }
                else
                {
                    if (entityIn instanceof EntityLiving)
                    {
                        ((EntityLiving) entityIn).getNavigator().setPath(null, 0.0D);
                    }
                    entityIn.getCapability(PullProvider.PULL, null).setDrawn(this);
                    ((WorldServer) this.world).getEntityTracker().sendToTracking(this, PacketHandler.INSTANCE.getPacketFrom(new SPacketDrawnUpdate(entityIn.getEntityId(), this.getEntityId())));
                    this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
                }
            }
            this.pulling = entityIn;
        }
    }

    /**
     * @return The current wheel rotation angle.
     */
    @SideOnly(Side.CLIENT)
    public float getWheelRotation()
    {
        if (Minecraft.getMinecraft().isGamePaused())
        {
            this.factor = 0F;
        }
        this.wheelrot -= 0.12F * this.factor;
        return this.wheelrot;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    @Override
    public boolean canBePushed()
    {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else if (!this.world.isRemote && !this.isDead)
        {
            if (source instanceof EntityDamageSourceIndirect && source.getTrueSource() != null && this.isPassenger(source.getTrueSource()))
            {
                return false;
            }
            else
            {
                this.setTimeSinceHit(10);
                this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
                boolean flag = source.getTrueSource() instanceof EntityPlayer && ((EntityPlayer)source.getTrueSource()).capabilities.isCreativeMode;

                if (flag || this.getDamageTaken() > 40.0F)
                {
                    this.onDestroyed(source, flag);
                    this.setPulling(null);

                    this.setDead();
                }

                return true;
            }
        }
        return true;
    }
    
    public void setDamageTaken(float damageTaken)
    {
        this.dataManager.set(DAMAGE_TAKEN, damageTaken);
    }

    public float getDamageTaken()
    {
        return this.dataManager.get(DAMAGE_TAKEN).floatValue();
    }

    public void setTimeSinceHit(int timeSinceHit)
    {
        this.dataManager.set(TIME_SINCE_HIT, timeSinceHit);
    }

    public int getTimeSinceHit()
    {
        return this.dataManager.get(TIME_SINCE_HIT).intValue();
    }
    
    public abstract Item getCartItem();

    /**
     * Executes upon carts destruction. Currently only used to drop items on death.
     * 
     * @param source The damage source.
     * @param byCreativePlayer Whether or not this entity was destroyed by a player in creative mode.
     */
    public void onDestroyed(DamageSource source, boolean byCreativePlayer)
    {
        if (!byCreativePlayer && this.world.getGameRules().getBoolean("doEntityDrops"))
        {
            this.dropItemWithOffset(this.getCartItem(), 1, 0.0F);
        }
    }
    
    @Override
    public ItemStack getPickedResult(RayTraceResult target)
    {
        return new ItemStack(this.getCartItem());
    }

    @Override
    protected void entityInit()
    {
        this.dataManager.register(TIME_SINCE_HIT, 0);
        this.dataManager.register(DAMAGE_TAKEN, 0.0F);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        
    }

    public void writeSpawnData(ByteBuf buffer)
    {
        if (this.pulling != null)
        {
            buffer.writeInt(this.pulling.getEntityId());
        }
    }

    public void readSpawnData(ByteBuf additionalData)
    {
        if (additionalData.readableBytes() >= 4)
        {
            this.setPulling(world.getEntityByID(additionalData.readInt()));
        }
    }

    private void tickLerp()
    {
        if (this.lerpSteps > 0 && !this.pulling.canPassengerSteer())
        {
            double dx = this.posX + (this.lerpX - this.posX) / this.lerpSteps;
            double dy = this.posY + (this.lerpY - this.posY) / this.lerpSteps;
            double dz = this.posZ + (this.lerpZ - this.posZ) / this.lerpSteps;
            double drot = MathHelper.wrapDegrees(this.lerpYaw - this.rotationYaw);
            this.rotationYaw = (float)(this.rotationYaw + drot / this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(dx, dy, dz);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = (double)yaw;
        this.lerpSteps = 10;
    }

    @Override
    protected void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        if (this.canPassengerSteer() && this.lerpSteps > 0)
        {
            this.lerpSteps = 0;
            this.posX = this.lerpX;
            this.posY = this.lerpY;
            this.posZ = this.lerpZ;
            this.rotationYaw = (float)this.lerpYaw;
        }
    }
}

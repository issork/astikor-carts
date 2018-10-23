package de.mennomax.astikoorcarts.entity;

import de.mennomax.astikoorcarts.capabilities.PullProvider;
import de.mennomax.astikoorcarts.handler.PacketHandler;
import de.mennomax.astikoorcarts.packets.SPacketDrawnUpdate;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
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
    protected Entity pulling;
    @SideOnly(Side.CLIENT)
    private float wheelrot;
    @SideOnly(Side.CLIENT)
    private double factor;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private int lasthit;
    private int hitcount;
    protected double offsetFactor;

    public AbstractDrawn(World worldIn)
    {
        super(worldIn);
    }

    @Override
    public void onUpdate()
    {
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
            Vec3d moveVec = new Vec3d(targetVec.x - this.posX - this.getLookVec().x * this.offsetFactor, 0.0, targetVec.z - this.posZ - this.getLookVec().z * this.offsetFactor);
            this.motionX = moveVec.x;
            if (!this.pulling.onGround && this.pulling.fallDistance == 0.0D)
            {
                this.motionY = targetVec.y - this.posY;
            }
            this.motionZ = moveVec.z;
            this.tickLerp();
            if (this.world.isRemote)
            {
                this.factor = -Math.sqrt(moveVec.x * moveVec.x + moveVec.z * moveVec.z);
                if (moveVec.subtract(this.getLookVec()).lengthVector() > 1)
                {
                    this.factor = -this.factor;
                }
            }
        }
        else
        {
            if (this.world.isRemote)
            {
                this.factor = 0.0D;
            }
            this.motionX = 0.0D;
            this.motionZ = 0.0D;
        }
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
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
            RayTraceResult result = this.world.rayTraceBlocks(new Vec3d(this.posX, this.posY + this.height, this.posZ), new Vec3d(this.pulling.posX, this.pulling.posY + this.height / 2, this.pulling.posZ), false, true, false);
            if (result != null)
            {
                if (result.typeOfHit == Type.BLOCK)
                {
                    return true;
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
            if (entityIn instanceof EntityLiving)
            {
                ((EntityLiving) entityIn).getNavigator().setPath(null, 0.0D);
            }
            if (!this.world.isRemote)
            {
                if (entityIn == null)
                {
                    if (this.pulling != null)
                    {
                        this.pulling.getCapability(PullProvider.PULL, null).setDrawn(null);
                    }
                    ((WorldServer) this.world).getEntityTracker().sendToTracking(this, PacketHandler.INSTANCE.getPacketFrom(new SPacketDrawnUpdate(-1, this.getEntityId())));
                }
                else
                {
                    entityIn.getCapability(PullProvider.PULL, null).setDrawn(this);
                    ((WorldServer) this.world).getEntityTracker().sendToTracking(this, PacketHandler.INSTANCE.getPacketFrom(new SPacketDrawnUpdate(entityIn.getEntityId(), this.getEntityId())));
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
        if (!this.isDead)
        {
            if (source.isCreativePlayer())
            {
                this.setDead();
            }
            else
            {
                this.lasthit = this.ticksExisted;

                if (this.lasthit >= ticksExisted - 20)
                {
                    this.hitcount++;
                }
                else
                {
                    this.hitcount = 0;
                }
                if (this.hitcount == 10)
                {
                    this.setDead();
                }
            }
            if (this.isDead)
            {
                this.setPulling(null);
                if (!this.world.isRemote)
                {
                    this.onDestroyed(source);
                }
            }
        }
        return true;
    }

    /**
     * Executes upon carts destruction.
     * 
     * @param source The damage source.
     */
    public void onDestroyed(DamageSource source)
    {

    }

    @Override
    protected void entityInit()
    {

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
            setPulling(world.getEntityByID(additionalData.readInt()));
        }
    }

    private void tickLerp()
    {
        if (this.lerpSteps > 0 && !this.pulling.canPassengerSteer())
        {
            double dx = this.posX + (this.lerpX - this.posX) / (double) this.lerpSteps;
            double dy = this.posY + (this.lerpY - this.posY) / (double) this.lerpSteps;
            double dz = this.posZ + (this.lerpZ - this.posZ) / (double) this.lerpSteps;
            --this.lerpSteps;
            this.setPosition(dx, dy, dz);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
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
        }
    }
}

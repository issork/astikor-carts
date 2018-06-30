package astikoor.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import astikoor.handler.PacketHandler;
import astikoor.packets.CPacketEntityCartUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCart extends Entity
{
    protected Entity pulling;
    private float wheelrot;
    private double factor;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private int lasthit;
    private int hitcount;
    private final Set<EntityAITaskEntry> TASKENTRIES = new LinkedHashSet<>();
    protected double offsetFactor;

    public EntityCart(World worldIn)
    {
        super(worldIn);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if(!this.hasNoGravity())
        {
            this.motionY -= 0.04D;
        }
        if(this.pulling != null)
        {
            if(!this.world.isRemote)
            {
                if(this.shouldRemovePulling())
                {
                    ((WorldServer) this.world).getEntityTracker().sendToTracking(this, PacketHandler.INSTANCE.getPacketFrom(new CPacketEntityCartUpdate(this.pulling.getEntityId(), this.getEntityId())));
                    this.setPulling(null);
                    return;
                }
            }
            Vec3d targetVec = this.getTargetVec();
            this.handleRotation(targetVec);
            double dRotation = (double) (this.prevRotationYaw - this.rotationYaw);
            if(dRotation < -180.0D)
            {
                this.prevRotationYaw += 360.0F;
            }
            else if(dRotation >= 180.0D)
            {
                this.prevRotationYaw -= 360.0F;
            }
            Vec3d moveVec = new Vec3d(targetVec.x - this.posX - this.getLookVec().x * this.offsetFactor, 0.0, targetVec.z - this.posZ - this.getLookVec().z * this.offsetFactor);
            this.factor = -Math.sqrt(moveVec.x * moveVec.x + moveVec.z * moveVec.z);
            this.motionX = moveVec.x;
            this.motionZ = moveVec.z;
            this.tickLerp();
            if(moveVec.subtract(this.getLookVec()).lengthVector() > 1)
            {
                this.factor = -this.factor;
            }
        }
        else
        {
            this.factor = 0.0D;
            this.motionX = 0.0D;
            this.motionZ = 0.0D;
        }
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        this.collideWithNearbyEntities();
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
        if(this.pulling != null)
        {
            RayTraceResult result = this.world.rayTraceBlocks(new Vec3d(this.posX, this.posY + this.height, this.posZ), new Vec3d(this.pulling.posX, this.pulling.posY + this.height / 2, this.pulling.posZ), false, true, false);
            if(result != null)
            {
                if(result.typeOfHit == Type.BLOCK)
                {
                    return true;
                }
            }
        }
        return false || this.pulling.isDead;
    }

    /**
     * 
     * @param pullingIn
     *            {@link Entity} that tries to pull this cart.
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
     * @param entityIn
     *            {@link Entity} that should pull this cart.
     */
    public void setPulling(Entity entityIn)
    {
        if(this.pulling == null || entityIn == null)
        {
            // TODO Find a better way to disable entity movement when pulling a cart.
            if(entityIn instanceof EntityLiving)
            {
                for(EntityAITaskEntry task : ((EntityLiving) entityIn).tasks.taskEntries)
                {
                    this.TASKENTRIES.add(task);
                    task.action.resetTask();
                }
                for(EntityAITaskEntry task : this.TASKENTRIES)
                {
                    ((EntityLiving) entityIn).tasks.removeTask(task.action);
                }
            }
            else if(this.pulling instanceof EntityLiving)
            {
                for(EntityAITaskEntry task : this.TASKENTRIES)
                {
                    ((EntityLiving) this.pulling).tasks.taskEntries.add(task);
                }
                this.TASKENTRIES.clear();
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
        if(Minecraft.getMinecraft().isGamePaused())
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

    protected void collideWithNearbyEntities()
    {
        for(Entity entity : this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox(), EntitySelectors.getTeamCollisionPredicate(this)))
        {
            double dx = entity.posX - this.posX;
            double dz = entity.posZ - this.posZ;
            double delta = MathHelper.absMax(dx, dz);
            if(delta >= 0.01D)
            {
                delta = (double) MathHelper.sqrt(delta);
                dx = dx / delta;
                dz = dz / delta;
                double d3 = 1.0D / delta;
                if(d3 > 1.0D)
                {
                    d3 = 1.0D;
                }
                dx = dx * d3;
                dz = dz * d3;
                dx = dx * 0.05D;
                dz = dz * 0.05D;
                dx = dx * (double) (1.0F - this.entityCollisionReduction);
                dz = dz * (double) (1.0F - this.entityCollisionReduction);
                if(!entity.isBeingRidden())
                {
                    entity.addVelocity(dx, 0.0D, dz);
                }
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if(!this.isDead)
        {
            if(source.isCreativePlayer())
            {
                this.setDead();
            }
            else
            {
                this.lasthit = this.ticksExisted;

                if(this.lasthit >= ticksExisted - 20)
                {
                    this.hitcount++;
                }
                else
                {
                    this.hitcount = 0;
                }
                if(this.hitcount == 10)
                {
                    this.setDead();
                }
            }
            if(this.isDead)
            {
                this.setPulling(null);
                if(!this.world.isRemote)
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
     * @param source
     *            The damage source.
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

    private void tickLerp()
    {
        if(this.lerpSteps > 0 && !this.pulling.canPassengerSteer())
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
        if(this.canPassengerSteer() && this.lerpSteps > 0)
        {
            this.lerpSteps = 0;
            this.posX = this.lerpX;
            this.posY = this.lerpY;
            this.posZ = this.lerpZ;
        }
    }
}

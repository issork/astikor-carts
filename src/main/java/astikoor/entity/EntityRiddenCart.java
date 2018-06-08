package astikoor.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.world.World;

public class EntityRiddenCart extends EntityCart
{
    private boolean forward;

    public EntityRiddenCart(World worldIn)
    {
        super(worldIn);
    }

    @Override
    public void onUpdate()
    {
        if(this.getControllingPassenger() != null && this.getPulling() != null)
        {
            if(this.getPulling() instanceof AbstractHorse)
            {
                if(((AbstractHorse) this.getPulling()).isEatingHaystack())
                {
                    ((AbstractHorse) this.getPulling()).setEatingHaystack(false);
                }
            }
            if(this.forward)
            {
                this.getPulling().rotationYaw = this.getControllingPassenger().getRotationYawHead();
                if(((EntityLivingBase) this.getPulling()).isSprinting())
                {
                    ((EntityLivingBase) this.getPulling()).travel(0.0F, 0.0F, (float) (this.getPullingSpeed() + SharedMonsterAttributes.MOVEMENT_SPEED.getDefaultValue()));
                }
                else
                {
                    ((EntityLivingBase) this.getPulling()).travel(0.0F, 0.0F, (float) (this.getPullingSpeed()));
                }
            }
            else
            {
                if(this.getPulling().isSprinting())
                {
                    this.getPulling().setSprinting(false);
                }
            }
        }
        else
        {
            if(this.forward)
            {
                this.forward = false;
            }
            if(this.getPulling() instanceof EntityLivingBase)
            {
                if(this.getPulling().isSprinting())
                {
                    this.getPulling().setSprinting(false);
                }
            }
        }
        super.onUpdate();
    }

    public double getPullingSpeed()
    {
        return ((EntityLivingBase) this.getPulling()).getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
    }

    @Override
    public Entity getControllingPassenger()
    {
        return this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
    }

    public void updateForward(boolean forwardIn)
    {
        forward = forwardIn;
    }
}

package de.mennomax.horsecarts.entity;

import de.mennomax.horsecarts.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityChariot extends EntityRiddenCart
{
    public EntityChariot(World worldIn)
    {
        super(worldIn);
        this.setSize(1.5F, 1.4F);
        this.stepHeight = 1.2F;
        this.offsetFactor = 2.4D;
    }
    
    @Override
    public boolean canPull(Entity pullingIn)
    {
        if(!(pullingIn instanceof EntityLivingBase))
        {
            return false;
        }
        String[] canPullArray = ModConfig.chariot.canPull;
        for(int i = 0; i < canPullArray.length; i++)
        {
            if(canPullArray[i].equals(pullingIn instanceof EntityPlayer ? "minecraft:player" : EntityList.getKey(pullingIn).toString()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if(!this.world.isRemote)
        {
            player.startRiding(this);
        }
        return true;
    }

    @Override
    public double getMountedYOffset()
    {
        return 0.8D;
    }

    @Override
    public boolean shouldRiderSit()
    {
        return false;
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        if(this.isPassenger(passenger))
        {
            Vec3d vec3d = (new Vec3d((double) -0.3D, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - ((float) Math.PI / 2F));
            passenger.setPosition(this.posX + vec3d.x, this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ + vec3d.z);
        }
    }
}

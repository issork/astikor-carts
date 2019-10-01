package de.mennomax.astikorcarts.entity;

import de.mennomax.astikorcarts.init.Items;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MobCartEntity extends AbstractDrawnEntity {

    public MobCartEntity(EntityType<? extends Entity> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public boolean processInitialInteract(PlayerEntity player, Hand hand) {
        if (!this.world.isRemote) {
            if (player.isSneaking()) {
                for (Entity entity : this.getPassengers()) {
                    if (!(entity instanceof PlayerEntity)) {
                        entity.stopRiding();
                    }
                }
            } else if (this.getPulling() != player) {
                player.startRiding(this);
            }
        }
        return true;
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {
        if (!entityIn.isPassenger(this)) {
            if (!this.world.isRemote && this.getPulling() != entityIn && !(this.getControllingPassenger() instanceof Entity) && this.getPassengers().size() < 2 && !entityIn.isPassenger() && entityIn.getWidth() < this.getWidth() && entityIn instanceof LivingEntity&& !(entityIn instanceof WaterMobEntity) && !(entityIn instanceof PlayerEntity)) {
                entityIn.startRiding(this);
            } else {
                super.applyEntityCollision(entityIn);
            }
        }
    }

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return this.getPassengers().size() < 2;
    }
    
    @Override
    public double getMountedYOffset() {
        return 0.7D;
    }

    @Override
    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            double f = -0.1D;

            if (this.getPassengers().size() > 1) {
                f = this.getPassengers().indexOf(passenger) == 0 ? 0.2D : -0.6D;

                if (passenger instanceof AnimalEntity) {
                    f += 0.2D;
                }
            }

            Vec3d vec3d = new Vec3d(f, 0.0D, 0.0D).rotateYaw(-this.rotationYaw * 0.017453292F - ((float) Math.PI / 2F));
            passenger.setPosition(this.posX + vec3d.x, this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ + vec3d.z);
            passenger.setRenderYawOffset(this.rotationYaw);
            float f2 = MathHelper.wrapDegrees(passenger.rotationYaw - this.rotationYaw);
            float f1 = MathHelper.clamp(f2, -105.0F, 105.0F);
            passenger.prevRotationYaw += f1 - f2;
            passenger.rotationYaw += f1 - f2;
            passenger.setRotationYawHead(passenger.rotationYaw);
            if (passenger instanceof AnimalEntity && this.getPassengers().size() > 1) {
                int j = passenger.getEntityId() % 2 == 0 ? 90 : 270;
                passenger.setRenderYawOffset(((AnimalEntity) passenger).renderYawOffset + j);
                passenger.setRotationYawHead(passenger.getRotationYawHead() + j);
            }
        }
    }

    @Override
    public Item getCartItem() {
        return Items.MOBCART;
    }

}

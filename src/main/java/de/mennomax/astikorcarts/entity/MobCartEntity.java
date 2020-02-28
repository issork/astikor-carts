package de.mennomax.astikorcarts.entity;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.config.AstikorCartsConfig;
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

import java.util.ArrayList;

public final class MobCartEntity extends AbstractDrawnEntity {
    public MobCartEntity(final EntityType<? extends Entity> entityTypeIn, final World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected ArrayList<String> getAllowedEntityList() {
        return AstikorCartsConfig.COMMON.mobPullable.get();
    }

    @Override
    public void tick() {
        super.tick();
        final Entity coachman = this.getControllingPassenger();
        final Entity pulling = this.getPulling();
        if (pulling != null && coachman != null && pulling.getControllingPassenger() == null) {
            final PostilionEntity postilion = AstikorCarts.EntityTypes.POSTILION.get().create(this.world);
            if (postilion != null) {
                postilion.setPositionAndRotation(pulling.getPosX(), pulling.getPosY(), pulling.getPosZ(), coachman.rotationYaw, coachman.rotationPitch);
                if (postilion.startRiding(pulling)) {
                    this.world.addEntity(postilion);
                } else {
                    postilion.remove();
                }
            }
        }
    }

    @Override
    public boolean processInitialInteract(final PlayerEntity player, final Hand hand) {
        if (!this.world.isRemote) {
            if (player.isCrouching()) {
                for (final Entity entity : this.getPassengers()) {
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
    public void applyEntityCollision(final Entity entityIn) {
        if (!entityIn.isPassenger(this)) {
            if (!this.world.isRemote && this.getPulling() != entityIn && this.getControllingPassenger() == null && this.getPassengers().size() < 2 && !entityIn.isPassenger() && entityIn.getWidth() < this.getWidth() && entityIn instanceof LivingEntity
                && !(entityIn instanceof WaterMobEntity) && !(entityIn instanceof PlayerEntity)) {
                entityIn.startRiding(this);
            } else {
                super.applyEntityCollision(entityIn);
            }
        }
    }

    @Override
    protected boolean canFitPassenger(final Entity passenger) {
        return this.getPassengers().size() < 2;
    }

    @Override
    public double getMountedYOffset() {
        return 11.0D / 16.0D;
    }

    @Override
    public void updatePassenger(final Entity passenger) {
        if (this.isPassenger(passenger)) {
            double f = -0.1D;

            if (this.getPassengers().size() > 1) {
                f = this.getPassengers().indexOf(passenger) == 0 ? 0.2D : -0.6D;

                if (passenger instanceof AnimalEntity) {
                    f += 0.2D;
                }
            }

            final Vec3d forward = this.getLookVec();
            final Vec3d origin = new Vec3d(0.0D, this.getMountedYOffset(), 1.0D / 16.0D);
            final Vec3d pos = origin.add(forward.scale(f + MathHelper.sin((float) Math.toRadians(this.rotationPitch)) * 0.7D));
            passenger.setPosition(this.getPosX() + pos.x, this.getPosY() + pos.y + passenger.getYOffset(), this.getPosZ() + pos.z);
            passenger.setRenderYawOffset(this.rotationYaw);
            final float f2 = MathHelper.wrapDegrees(passenger.rotationYaw - this.rotationYaw);
            final float f1 = MathHelper.clamp(f2, -105.0F, 105.0F);
            passenger.prevRotationYaw += f1 - f2;
            passenger.rotationYaw += f1 - f2;
            passenger.setRotationYawHead(passenger.rotationYaw);
            if (passenger instanceof AnimalEntity && this.getPassengers().size() > 1) {
                final int j = passenger.getEntityId() % 2 == 0 ? 90 : 270;
                passenger.setRenderYawOffset(((AnimalEntity) passenger).renderYawOffset + j);
                passenger.setRotationYawHead(passenger.getRotationYawHead() + j);
            }
        }
    }

    @Override
    public Item getCartItem() {
        return AstikorCarts.Items.MOB_CART.get();
    }
}

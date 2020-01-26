package de.mennomax.astikorcarts.entity;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.init.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class PostilionEntity extends DummyLivingEntity {
    public PostilionEntity(final EntityType<? extends PostilionEntity> type, final World world) {
        super(type, world);
    }

    public PostilionEntity(final World world) {
        super(Entities.POSTILION, world);
    }

    @Override
    public double getYOffset() {
        return 0.125D;
    }

    @Override
    public void tick() {
        super.tick();
        final LivingEntity coachman = this.getCoachman();
        if (coachman != null) {
            this.rotationYaw = coachman.rotationYaw;
            this.prevRotationYaw = this.rotationYaw;
            this.rotationPitch = coachman.rotationPitch * 0.5F;
            this.moveForward = coachman.moveForward;
            this.moveStrafing = 0.0F;
        } else {
            this.remove();
        }
    }

    @Nullable
    private LivingEntity getCoachman() {
        final Entity mount = this.getRidingEntity();
        if (mount != null) {
            final AbstractDrawnEntity drawn = (this.world.isRemote ? AstikorCarts.CLIENTPULLMAP : AstikorCarts.SERVERPULLMAP).get(mount);
            if (drawn != null) {
                return drawn.getControllingPassenger();
            }
        }
        return null;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}

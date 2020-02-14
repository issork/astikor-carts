package de.mennomax.astikorcarts.entity;

import de.mennomax.astikorcarts.world.AstikorWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Optional;

public final class PostilionEntity extends DummyLivingEntity {
    public PostilionEntity(final EntityType<? extends PostilionEntity> type, final World world) {
        super(type, world);
    }

    @Override
    public double getYOffset() {
        return 0.125D;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isRemote) {
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
    }

    @Nullable
    private LivingEntity getCoachman() {
        final Entity mount = this.getRidingEntity();
        if (mount != null) {
            return AstikorWorld.get(this.world).map(m -> m.getDrawn(mount)).orElse(Optional.empty())
                .map(AbstractDrawnEntity::getControllingPassenger).orElse(null);
        }
        return null;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}

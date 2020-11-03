package de.mennomax.astikorcarts.entity.ai.goal;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public final class RideCartGoal extends Goal {
    private final Entity mob;

    public RideCartGoal(final Entity mob) {
        this.mob = mob;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean shouldExecute() {
        return this.mob.getRidingEntity() instanceof AbstractDrawnEntity;
    }
}

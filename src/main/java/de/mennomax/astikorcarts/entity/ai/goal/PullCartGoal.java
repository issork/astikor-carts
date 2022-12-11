package de.mennomax.astikorcarts.entity.ai.goal;

import de.mennomax.astikorcarts.world.AstikorWorld;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public final class PullCartGoal extends Goal {
    private final Entity mob;

    public PullCartGoal(final Entity entity) {
        this.mob = entity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return AstikorWorld.get(this.mob.level).map(w -> w.isPulling(this.mob)).orElse(false);
    }
}

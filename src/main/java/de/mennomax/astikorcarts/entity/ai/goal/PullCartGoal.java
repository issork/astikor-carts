package de.mennomax.astikorcarts.entity.ai.goal;

import java.util.EnumSet;

import de.mennomax.astikorcarts.AstikorCarts;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;

public class PullCartGoal extends Goal {
    private final Entity mob;

    public PullCartGoal(Entity entity) {
        this.mob = entity;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean shouldExecute() {
        return AstikorCarts.SERVERPULLMAP.containsKey(mob);
    }

}

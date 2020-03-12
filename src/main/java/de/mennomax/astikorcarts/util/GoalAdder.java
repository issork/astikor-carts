package de.mennomax.astikorcarts.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.TriggerSkeletonTrapGoal;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * <p>The traditional usage of {@link EntityJoinWorldEvent} to add custom AI appends goals to the end of
 * the {@link GoalSelector}'s goal set, for example:
 * <pre> {@code
 *    mob.goalSelector.addGoal(1, new CustomGoal(mob))
 * } </pre>
 * <p>However, by this point an entity would have already had a chance to add toggleable goals to its
 * selectors from construction and deserialization, which may be expected to be the final goals.
 * <p>This is notably an issue with {@link SkeletonHorseEntity}, as it uses a goal
 * {@link TriggerSkeletonTrapGoal} which depends on being the last goal and when otherwise causes a
 * {@link ConcurrentModificationException} during {@link Entity#tick}.
 * <p>This class addresses the problem be adding custom goals to the beginning of the goal set.
 */
public final class GoalAdder<T extends Entity> {
    private static final Field GOALS = ObfuscationReflectionHelper.findField(GoalSelector.class, "field_220892_d");

    private final Class<T> type;

    private final Function<T, GoalSelector> selector;

    private final ImmutableList<GoalEntry<T>> goals;

    private GoalAdder(final Builder<T> builder) {
        this.type = builder.type;
        this.selector = builder.selector;
        this.goals = builder.goals.build();
    }

    @SubscribeEvent
    public void onEntityJoinWorld(final EntityJoinWorldEvent event) {
        final Entity entity = event.getEntity();
        if (!entity.world.isRemote && this.type.isInstance(entity)) {
            final Set<PrioritizedGoal> oldGoals = this.getGoals(this.type.cast(entity));
            final List<PrioritizedGoal> newGoals = new ArrayList<>(oldGoals.size() + this.goals.size());
            for (final GoalEntry<T> goal : this.goals) {
                newGoals.add(new PrioritizedGoal(goal.priority, goal.factory.apply(this.type.cast(entity))));
            }
            newGoals.addAll(oldGoals);
            oldGoals.clear();
            oldGoals.addAll(newGoals);
        }
    }

    @SuppressWarnings("unchecked")
    private Set<PrioritizedGoal> getGoals(final T entity) {
        try {
            return (Set<PrioritizedGoal>) GOALS.get(this.selector.apply(entity));
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends MobEntity> Builder<T> mobGoal(final Class<T> type) {
        return GoalAdder.builder(type, m -> m.goalSelector);
    }

    public static <T extends MobEntity> Builder<T> mobTarget(final Class<T> type) {
        return GoalAdder.builder(type, m -> m.targetSelector);
    }

    public static <T extends Entity> Builder<T> builder(final Class<T> type, final Function<T, GoalSelector> selector) {
        return new GoalAdder.Builder<>(type, selector);
    }

    public static final class Builder<T extends Entity> {
        private final Class<T> type;

        private final Function<T, GoalSelector> selector;

        private final ImmutableList.Builder<GoalEntry<T>> goals = new ImmutableList.Builder<>();

        private Builder(final Class<T> type, final Function<T, GoalSelector> selector) {
            this.type = type;
            this.selector = selector;
        }

        public Builder<T> add(final int priority, final Function<T, Goal> factory) {
            this.goals.add(new GoalEntry<>(priority, factory));
            return this;
        }

        public GoalAdder<T> build() {
            return new GoalAdder<>(this);
        }
    }

    private static final class GoalEntry<T extends Entity> {
        private final int priority;

        private final Function<T, Goal> factory;

        private GoalEntry(final int priority, final Function<T, Goal> factory) {
            this.priority = priority;
            this.factory = factory;
        }
    }
}

package de.mennomax.astikorcarts.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;

public final class EntityBuilder<T extends Entity> {
    private final EntityType.IFactory<T> factory;
    private final EntityClassification classification;
    private boolean serializable;
    private boolean summonable;
    private boolean fireImmune;
    private boolean sendVelocityUpdates;
    private int trackingRange;
    private int updateInterval;
    private boolean ignoreSpawnRange;
    private EntitySize size;

    private EntityBuilder(final EntityType.IFactory<T> factory, final EntityClassification classification) {
        this.factory = factory;
        this.classification = classification;
        this.serializable = true;
        this.summonable = true;
        this.sendVelocityUpdates = true;
        this.trackingRange = 5;
        this.updateInterval = 3;
        this.ignoreSpawnRange = classification == EntityClassification.CREATURE || classification == EntityClassification.MISC;
        this.size = EntitySize.flexible(0.6F, 1.8F);
    }

    public EntityBuilder<T> size(final float width, final float height) {
        this.size = EntitySize.flexible(width, height);
        return this;
    }

    public EntityBuilder<T> insummonable() {
        this.summonable = false;
        return this;
    }

    public EntityBuilder<T> unserializable() {
        this.serializable = false;
        return this;
    }

    public EntityBuilder<T> fireImmune() {
        this.fireImmune = true;
        return this;
    }

    public EntityBuilder<T> ignoreSpawnRange() {
        this.ignoreSpawnRange = true;
        return this;
    }

    public EntityBuilder<T> updateInterval(final int updateInterval) {
        this.updateInterval = updateInterval;
        return this;
    }

    public EntityBuilder<T> trackingRange(final int trackingRange) {
        this.trackingRange = trackingRange;
        return this;
    }

    public EntityBuilder<T> sendVelocityUpdates(final boolean sendVelocityUpdates) {
        this.sendVelocityUpdates = sendVelocityUpdates;
        return this;
    }

    public EntityType<T> build() {
        final boolean sendVelocityUpdates = this.sendVelocityUpdates;
        final int trackingRange = this.trackingRange;
        final int updateInterval = this.updateInterval;
        return new EntityType<T>(
            this.factory,
            this.classification,
            this.serializable,
            this.summonable,
            this.fireImmune,
            this.ignoreSpawnRange,
            this.size,
            t -> sendVelocityUpdates,
            t -> trackingRange,
            t -> updateInterval,
            (msg, world) -> null
        ) {
            @Override
            public T customClientSpawn(final FMLPlayMessages.SpawnEntity msg, final World world) {
                return this.create(world);
            }
        };
    }

    public static <T extends Entity> EntityBuilder<T> create(final EntityType.IFactory<T> factory, final EntityClassification classification) {
        return new EntityBuilder<>(factory, classification);
    }

    public static <T extends Entity> EntityBuilder<T> create(final EntityClassification classification) {
        //noinspection ConstantConditions
        return new EntityBuilder<>((type, world) -> null, classification);
    }
}

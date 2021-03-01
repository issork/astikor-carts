package de.mennomax.astikorcarts.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.reflect.ClassPath;
import com.google.gson.internal.UnsafeAllocator;
import com.mojang.serialization.Lifecycle;
import net.jodah.typetools.TypeResolver;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEquipable;
import net.minecraft.entity.IRideable;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.ServerWorldInfo;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;

public final class AstikorCartsConfig {
    public static Common get() {
        return Holder.COMMON;
    }

    public static ForgeConfigSpec spec() {
        return Holder.COMMON_SPEC;
    }

    private static final class Holder {
        private static final Common COMMON;

        private static final ForgeConfigSpec COMMON_SPEC;

        static {
            final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
            COMMON = specPair.getLeft();
            COMMON_SPEC = specPair.getRight();
        }
    }

    public static class Common {
        public final CartConfig supplyCart;
        public final CartConfig animalCart;
        public final CartConfig plow;

        Common(final ForgeConfigSpec.Builder builder) {
            builder.comment("Configuration for all carts and cart-like vehicles\n\nDefault pull_entities = " + referencePullAnimals()).push("carts");
            this.supplyCart = new CartConfig(builder, "supply_cart", "The Supply Cart, a type of cart that stores items");
            this.animalCart = new CartConfig(builder, "animal_cart", "The Animal Cart, a type of cart to haul other animals");
            this.plow = new CartConfig(builder, "plow", "The Plow, an animal pulled machine for tilling soil and creating paths");
            builder.pop();
        }

        static String referencePullAnimals() {
            return "[\n" +
                StreamSupport.stream(ForgeRegistries.ENTITIES.spliterator(), false)
                    .filter(type -> {
                        final Class<?> entityClass = TypeResolver.resolveRawArgument(Supplier.class, Objects.requireNonNull(
                            ObfuscationReflectionHelper.getPrivateValue(EntityType.class, type, "field_200732_aK"),
                            "factory"
                        ).getClass());
                        if (Entity.class.equals(entityClass)) return type == EntityType.PLAYER;
                        return IEquipable.class.isAssignableFrom(entityClass) &&
                            !IRideable.class.isAssignableFrom(entityClass) &&
                            !LlamaEntity.class.isAssignableFrom(entityClass); // no horse-llamas
                    })
                    .map(ForgeRegistryEntry::getRegistryName)
                    .filter(Objects::nonNull)
                    .map(type -> "    \"" + type.toString() + "\"")
                    .collect(Collectors.joining(",\n")) +
                "\n  ]";
        }
    }

    public static class CartConfig {
        public final ForgeConfigSpec.ConfigValue<ArrayList<String>> pullAnimals;
        public final ForgeConfigSpec.DoubleValue slowSpeed;
        public final ForgeConfigSpec.DoubleValue pullSpeed;

        CartConfig(final ForgeConfigSpec.Builder builder, final String name, final String description) {
            builder.comment(description).push(name);
            this.pullAnimals = builder
                .comment(
                    "Animals that are able to pull this cart, such as [\"minecraft:horse\"]\n" +
                    "An empty list defaults to all which may wear a saddle but not steered by an item"
                )
                .define("pull_animals", new ArrayList<>());
            this.slowSpeed = builder.comment("Slow speed modifier toggled by the sprint key")
                .defineInRange("slow_speed", -0.65D, -1.0D, 0.0D);
            this.pullSpeed = builder.comment("Base speed modifier applied to animals (-0.5 = half normal speed)")
                .defineInRange("pull_speed", 0.0D, -1.0D, 0.0D);
            builder.pop();
        }
    }
}

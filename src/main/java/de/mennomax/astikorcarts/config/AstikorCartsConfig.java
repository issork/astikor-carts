package de.mennomax.astikorcarts.config;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public final class AstikorCartsConfig {
    public static final Common COMMON;

    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMON_SPEC = specPair.getRight();
    }

    public static class Common {
        public final ForgeConfigSpec.DoubleValue speedModifier;
        public final ForgeConfigSpec.ConfigValue<ArrayList<String>> cargoPullable;
        public final ForgeConfigSpec.ConfigValue<ArrayList<String>> plowPullable;
        public final ForgeConfigSpec.ConfigValue<ArrayList<String>> mobPullable;

        Common(final ForgeConfigSpec.Builder builder) {
            builder.push("common");

            this.speedModifier = builder.comment("Speed modifier for when the sprint key is pressed while riding a living entity")
                .worldRestart()
                .defineInRange("speedModifier", -0.65, -1.0, 0.0);

            this.cargoPullable = builder.comment("List of entities that are allowed to pull this cart.")
                .define("cargoCart.pullEntities", new ArrayList<String>(Arrays.asList(
                    "minecraft:horse",
                    "minecraft:donkey",
                    "minecraft:mule",
                    "minecraft:pig")));

            this.plowPullable = builder.comment("List of entities that are allowed to pull this cart.")
                .define("plowCart.pullEntities", new ArrayList<String>(Arrays.asList(
                    "minecraft:horse",
                    "minecraft:donkey",
                    "minecraft:mule",
                    "minecraft:pig")));

            this.mobPullable = builder.comment("List of entities that are allowed to pull this cart.")
                .define("mobCart.pullEntities", new ArrayList<String>(Arrays.asList(
                    "minecraft:horse",
                    "minecraft:donkey",
                    "minecraft:mule",
                    "minecraft:pig")));

            builder.pop();
        }
    }
}

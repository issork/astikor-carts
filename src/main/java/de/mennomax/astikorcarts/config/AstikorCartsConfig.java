package de.mennomax.astikorcarts.config;

import com.electronwill.nightconfig.core.Config;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        public final ForgeConfigSpec.ConfigValue<List<? extends Config>> plowReplace;
        // public final ConfigValue<Config> BREAKMAP;
        // public final ConfigValue<Config> PLACEMAP;

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

            final Config shovel = Config.inMemory();
            shovel.add("tool", "forge:shovels");
            final Config path = Config.inMemory();
            path.add("target", Arrays.asList("minecraft:grass_block", "minecraft:dirt"));
            path.add("result", "minecraft:grass_path");
            shovel.add("blocks", Arrays.asList(path));
            final Config hoe = Config.inMemory();
            hoe.add("tool", "forge:hoes");
            final Config farmland = Config.inMemory();
            farmland.add("target", Arrays.asList("minecraft:dirt", "minecraft:grass_block", "minecraft:grass_path"));
            farmland.add("result", "minecraft:farmland");
            final Config dirt = Config.inMemory();
            dirt.add("target", Arrays.asList("minecraft:coarse_dirt"));
            dirt.add("result", "minecraft:dirt");
            hoe.add("blocks", Arrays.asList(farmland, dirt));
            this.plowReplace = builder.comment("Mappings to replace blocks (for example to till dirt with a hoe).\n"
                + "If the item can be damaged, it will be damaged otherwise it will be consumed.\n"
                + "Both tags and registry names may be used.")
                .defineList("plowCart.replaceMap", Arrays.asList(shovel, hoe), o -> {
                    if (!(o instanceof Config)) return false;
                    final Config config = (Config) o;
                    final Object tool = config.get("tool");
                    if (!(tool instanceof String)) return false;
                    final Object blocks = config.get("blocks");
                    if (!(blocks instanceof List<?>)) return false;
                    for (final Object block : (List<?>) blocks) {
                        if (!(block instanceof Config)) return false;
                        final Config blockConfig = (Config) block;
                        final Object result = blockConfig.get("result");
                        if (!(result instanceof String)) return false;
                        final Object targets = blockConfig.get("targets");
                        if (!(targets instanceof List<?>)) return false;
                        for (final Object target : (List<?>) targets) {
                            if (!(target instanceof String)) return false;
                        }
                    }
                    return true;
                });

            // Will be implemented later.
            // Map<String, Object> breakMap = new HashMap<>();
            // breakMap.put("minecraft:wheat_seeds", new
            // ArrayList<String>(Arrays.asList("minecraft:wheat")));
            // BREAKMAP = builder.comment("<item> -> <blocks item can break> mappings for
            // destroying Blocks behind the plow (for example to break crops)."
            // + "\nIf the item can be damaged, it will be damaged, else it will be consumed
            // - unless the player is in creative mode."
            // + "\nAn item can't be used as key for both place and break map."
            // + "\nThe key item also supports tags.")
            // .define("plowCart.breakMap", Config.wrap(breakMap, TomlFormat.instance()));
            //
            // Map<String, Object> placeMap = new HashMap<>();
            // placeMap.put("domain:item", "domain:item");
            // PLACEMAP = builder.comment("Mappings for placing items behind the plow (for
            // example to plant seeds)."
            // + "\nAn item can't be used as key for both place and break map."
            // + "\nIf the item can be damaged, it will be damaged, else it will be consumed
            // - unless the player is in creative mode."
            // + "\nThe key item also supports tags.")
            // .define("plowCart.placeMap", Config.wrap(placeMap, TomlFormat.instance()));

            builder.pop();
        }
    }
}

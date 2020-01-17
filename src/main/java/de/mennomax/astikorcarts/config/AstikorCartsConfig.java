package de.mennomax.astikorcarts.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlFormat;
import de.mennomax.astikorcarts.AstikorCarts;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = AstikorCarts.MODID, bus = Bus.MOD)
public class AstikorCartsConfig {

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMONSPEC;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMONSPEC = specPair.getRight();
    }

    public static class Common {

        public final DoubleValue SPEEDMODIFIER;
        public final ConfigValue<ArrayList<String>> CARGOCART;
        public final ConfigValue<ArrayList<String>> PLOWCART;
        public final ConfigValue<ArrayList<String>> MOBCART;
        public final ConfigValue<Config> REPLACEMAP;
        // public final ConfigValue<Config> BREAKMAP;
        // public final ConfigValue<Config> PLACEMAP;

        Common(final Builder builder) {
            builder.push("common");

            this.SPEEDMODIFIER = builder.comment("Speed modifier for when the sprint key is pressed while riding a living entity")
                .worldRestart()
                .defineInRange("speedModifier", -0.65, -1.0, 0.0);

            this.CARGOCART = builder.comment("List of entities that are allowed to pull this cart.")
                .define("cargoCart.pullEntities", new ArrayList<String>(Arrays.asList(
                    "minecraft:horse",
                    "minecraft:donkey",
                    "minecraft:mule",
                    "minecraft:pig")));

            this.PLOWCART = builder.comment("List of entities that are allowed to pull this cart.")
                .define("plowCart.pullEntities", new ArrayList<String>(Arrays.asList(
                    "minecraft:horse",
                    "minecraft:donkey",
                    "minecraft:mule",
                    "minecraft:pig")));

            this.MOBCART = builder.comment("List of entities that are allowed to pull this cart.")
                .define("mobCart.pullEntities", new ArrayList<String>(Arrays.asList(
                    "minecraft:horse",
                    "minecraft:donkey",
                    "minecraft:mule",
                    "minecraft:pig")));

            final Map<String, Object> itemReplaceMap = new HashMap<>();
            final Map<String, Object> hoeReplaceMap = new HashMap<>();
            hoeReplaceMap.put("minecraft:farmland", Arrays.asList("minecraft:dirt", "minecraft:grass_block", "minecraft:grass_path"));
            hoeReplaceMap.put("minecraft:dirt", Arrays.asList("minecraft:coarse_dirt"));
            itemReplaceMap.put("#forge:tools/hoes", Config.wrap(hoeReplaceMap, TomlFormat.instance()));
            final Map<String, Object> shovelReplaceMap = new HashMap<>();
            shovelReplaceMap.put("minecraft:grass_path", Arrays.asList("minecraft:grass_block", "minecraft:dirt"));
            itemReplaceMap.put("#forge:tools/shovels", Config.wrap(shovelReplaceMap, TomlFormat.instance()));
            this.REPLACEMAP = builder.comment("<new block> -> <old blocks> mappings to replace blocks (for example to till dirt with a hoe)."
                + "\nIf the item can be damaged, it will be damaged, else it will be consumed - unless the player is in creative mode."
                + "\nItem and the value list also supports tags.")
                .define("plowCart.replaceMap", Config.wrap(itemReplaceMap, TomlFormat.instance()));

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

package de.mennomax.astikorcarts;

import de.mennomax.astikorcarts.client.ClientInitializer;
import de.mennomax.astikorcarts.entity.AnimalCartEntity;
import de.mennomax.astikorcarts.entity.PlowEntity;
import de.mennomax.astikorcarts.entity.PostilionEntity;
import de.mennomax.astikorcarts.entity.SupplyCartEntity;
import de.mennomax.astikorcarts.inventory.container.PlowContainer;
import de.mennomax.astikorcarts.item.CartItem;
import de.mennomax.astikorcarts.network.NetBuilder;
import de.mennomax.astikorcarts.network.clientbound.UpdateDrawnMessage;
import de.mennomax.astikorcarts.network.serverbound.ActionKeyMessage;
import de.mennomax.astikorcarts.network.serverbound.OpenSupplyCartMessage;
import de.mennomax.astikorcarts.network.serverbound.ToggleSlowMessage;
import de.mennomax.astikorcarts.server.ServerInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

@Mod(AstikorCarts.ID)
public final class AstikorCarts {
    public static final String ID = "astikorcarts";

    public static final Logger LOGGER = LoggerFactory.getLogger(AstikorCarts.class);

    public static final SimpleChannel CHANNEL = new NetBuilder(new ResourceLocation(ID, "main"))
        .version(1).optionalServer().requiredClient()
        .serverbound(ActionKeyMessage::new).consumer(() -> ActionKeyMessage::handle)
        .serverbound(ToggleSlowMessage::new).consumer(() -> ToggleSlowMessage::handle)
        .clientbound(UpdateDrawnMessage::new).consumer(() -> new UpdateDrawnMessage.Handler())
        .serverbound(OpenSupplyCartMessage::new).consumer(() -> OpenSupplyCartMessage::handle)
        .build();

    public static final class Items {
        private Items() {
        }

        private static final DeferredRegister<Item> R = DeferredRegister.create(ForgeRegistries.ITEMS, ID);

        public static final RegistryObject<Item> WHEEL, SUPPLY_CART, PLOW, ANIMAL_CART;

        static {
            WHEEL = R.register("wheel", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));
            final Supplier<Item> cart = () -> new CartItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TRANSPORTATION));
            SUPPLY_CART = R.register("supply_cart", cart);
            PLOW = R.register("plow", cart);
            ANIMAL_CART = R.register("animal_cart", cart);
        }
    }

    public static final class EntityTypes {
        private EntityTypes() {
        }

        private static final DeferredRegister<EntityType<?>> R = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ID);

        public static final RegistryObject<EntityType<SupplyCartEntity>> SUPPLY_CART;
        public static final RegistryObject<EntityType<PlowEntity>> PLOW;
        public static final RegistryObject<EntityType<AnimalCartEntity>> ANIMAL_CART;
        public static final RegistryObject<EntityType<PostilionEntity>> POSTILION;

        static {
            SUPPLY_CART = R.register("supply_cart", () -> EntityType.Builder.of(SupplyCartEntity::new, MobCategory.MISC)
                .sized(1.5F, 1.4F)
                .build(ID + ":supply_cart"));
            PLOW = R.register("plow", () -> EntityType.Builder.of(PlowEntity::new, MobCategory.MISC)
                .sized(1.3F, 1.4F)
                .build(ID + ":plow"));
            ANIMAL_CART = R.register("animal_cart", () -> EntityType.Builder.of(AnimalCartEntity::new, MobCategory.MISC)
                .sized(1.3F, 1.4F)
                .build(ID + ":animal_cart"));
            POSTILION = R.register("postilion", () -> EntityType.Builder.of(PostilionEntity::new, MobCategory.MISC)
                .sized(0.25F, 0.25F)
                .noSummon()
                .noSave()
                .build(ID + ":postilion"));
        }
    }

    public static final class SoundEvents {
        private SoundEvents() {
        }

        private static final DeferredRegister<SoundEvent> R = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ID);

        public static final RegistryObject<SoundEvent> CART_ATTACHED = R.register("entity.cart.attach", () -> new SoundEvent(new ResourceLocation(ID, "entity.cart.attach")));
        public static final RegistryObject<SoundEvent> CART_DETACHED = R.register("entity.cart.detach", () -> new SoundEvent(new ResourceLocation(ID, "entity.cart.detach")));
        public static final RegistryObject<SoundEvent> CART_PLACED = R.register("entity.cart.place", () -> new SoundEvent(new ResourceLocation(ID, "entity.cart.place")));
    }

    public static final class Stats {
        private Stats() {
        }

        public static final ResourceLocation CART_ONE_CM = new ResourceLocation(ID, "cart_one_cm");

        private static void register(final IEventBus bus) {
            bus.addListener(Stats::registerEntries);
        }

        private static void registerEntries(RegisterEvent e) {
            if (Registry.STAT_TYPE_REGISTRY.equals(e.getRegistryKey())) {
                Registry.register(Registry.CUSTOM_STAT, Stats.CART_ONE_CM, Stats.CART_ONE_CM);
                net.minecraft.stats.Stats.CUSTOM.get(Stats.CART_ONE_CM, StatFormatter.DISTANCE);
            }
        }
    }

    public static final class ContainerTypes {
        private ContainerTypes() {
        }

        private static final DeferredRegister<MenuType<?>> R = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ID);

        public static final RegistryObject<MenuType<PlowContainer>> PLOW_CART = R.register("plow", () -> IForgeMenuType.create(PlowContainer::new));
    }

    public AstikorCarts() {
        final Initializer.Context ctx = new InitContext();
        DistExecutor.runForDist(() -> ClientInitializer::new, () -> ServerInitializer::new).init(ctx);
        Stats.register(ctx.modBus());
        Items.R.register(ctx.modBus());
        EntityTypes.R.register(ctx.modBus());
        SoundEvents.R.register(ctx.modBus());
        ContainerTypes.R.register(ctx.modBus());
    }

    private static class InitContext implements Initializer.Context {
        @Override
        public ModLoadingContext context() {
            return ModLoadingContext.get();
        }

        @Override
        public IEventBus bus() {
            return MinecraftForge.EVENT_BUS;
        }

        @Override
        public IEventBus modBus() {
            return FMLJavaModLoadingContext.get().getModEventBus();
        }
    }
}

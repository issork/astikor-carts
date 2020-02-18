package de.mennomax.astikorcarts;

import de.mennomax.astikorcarts.client.ClientInitializer;
import de.mennomax.astikorcarts.entity.CargoCartEntity;
import de.mennomax.astikorcarts.entity.MobCartEntity;
import de.mennomax.astikorcarts.entity.PlowCartEntity;
import de.mennomax.astikorcarts.entity.PostilionEntity;
import de.mennomax.astikorcarts.inventory.container.PlowCartContainer;
import de.mennomax.astikorcarts.item.CartItem;
import de.mennomax.astikorcarts.network.NetBuilder;
import de.mennomax.astikorcarts.network.packets.CPacketActionKey;
import de.mennomax.astikorcarts.network.packets.CPacketOpenCargoCartGui;
import de.mennomax.astikorcarts.network.packets.CPacketToggleSlow;
import de.mennomax.astikorcarts.network.packets.SPacketDrawnUpdate;
import de.mennomax.astikorcarts.server.ServerInitializer;
import de.mennomax.astikorcarts.util.DefRegister;
import de.mennomax.astikorcarts.util.EntityBuilder;
import de.mennomax.astikorcarts.util.RegObject;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.stats.IStatFormatter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@Mod(AstikorCarts.ID)
public final class AstikorCarts {
    public static final String ID = "astikorcarts";

    public static final SimpleChannel CHANNEL = new NetBuilder(new ResourceLocation(ID, "main"))
        .version(1).optionalServer().requiredClient()
        .serverbound(CPacketActionKey::new).consumer(() -> CPacketActionKey::handle)
        .serverbound(CPacketToggleSlow::new).consumer(() -> CPacketToggleSlow::handle)
        .clientbound(SPacketDrawnUpdate::new).consumer(() -> new SPacketDrawnUpdate.Handler())
        .serverbound(CPacketOpenCargoCartGui::new).consumer(() -> CPacketOpenCargoCartGui::handle)
        .build();

    private static final DefRegister REG = new DefRegister(ID);

    public static final class Items {
        private Items() {
        }

        private static final DefRegister.Forge<Item> R = REG.of(ForgeRegistries.ITEMS);

        public static final RegObject<Item> WHEEL, CARGO_CART, PLOW_CART, MOB_CART;

        static {
            WHEEL = R.make("wheel", () -> new Item(new Item.Properties().group(ItemGroup.MATERIALS)));
            final Supplier<Item> cart = () -> new CartItem(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION));
            CARGO_CART = R.make("cargocart", cart);
            PLOW_CART = R.make("plowcart", cart);
            MOB_CART = R.make("mobcart", cart);
        }
    }

    public static final class EntityTypes {
        private EntityTypes() {
        }

        private static final DefRegister.Forge<EntityType<?>> R = REG.of(ForgeRegistries.ENTITIES);

        public static final RegObject<EntityType<CargoCartEntity>> CARGO_CART;
        public static final RegObject<EntityType<PlowCartEntity>> PLOW_CART;
        public static final RegObject<EntityType<MobCartEntity>> MOB_CART;
        public static final RegObject<EntityType<PostilionEntity>> POSTILION;

        static {
            CARGO_CART = R.make("cargocart", () -> EntityBuilder.create(CargoCartEntity::new, EntityClassification.MISC)
                .size(1.5F, 1.4F)
                .build());
            PLOW_CART = R.make("plowcart", () -> EntityBuilder.create(PlowCartEntity::new, EntityClassification.MISC)
                .size(1.3F, 1.4F)
                .build());
            MOB_CART = R.make("mobcart", () -> EntityBuilder.create(MobCartEntity::new, EntityClassification.MISC)
                .size(1.3F, 1.4F)
                .build());
            POSTILION = R.make("postilion", () -> EntityBuilder.create(PostilionEntity::new, EntityClassification.MISC)
                .size(0.25F, 0.25F)
                .insummonable()
                .unserializable()
                .build());
        }
    }

    public static final class SoundEvents {
        private SoundEvents() {
        }

        private static final DefRegister.Forge<SoundEvent> R = REG.of(ForgeRegistries.SOUND_EVENTS);

        public static final RegObject<SoundEvent> CART_ATTACHED = R.make("cart.attached", SoundEvent::new);
        public static final RegObject<SoundEvent> CART_DETACHED = R.make("cart.detached", SoundEvent::new);
    }

    public static final class Stats {
        private Stats() {
        }

        private static final DefRegister.Vanilla<ResourceLocation, IStatFormatter> R = REG.of(Registry.CUSTOM_STAT, net.minecraft.stats.Stats.CUSTOM::get, rl -> IStatFormatter.DEFAULT);

        public static final ResourceLocation CART_ONE_CM = R.make("cart_one_cm", rl -> rl, rl -> IStatFormatter.DISTANCE);
    }

    public static final class ContainerTypes {
        private ContainerTypes() {
        }

        private static final DefRegister.Forge<ContainerType<?>> R = REG.of(ForgeRegistries.CONTAINERS);

        public static final RegObject<ContainerType<PlowCartContainer>> PLOWCARTCONTAINER = R.make("plowcartcontainer", () -> IForgeContainerType.create(PlowCartContainer::new));
    }

    public AstikorCarts() {
        final Initializer.Context ctx = new InitContext();
        DistExecutor.runForDist(() -> ClientInitializer::new, () -> ServerInitializer::new).init(ctx);
        REG.registerAll(ctx.modBus(), Items.R, EntityTypes.R, SoundEvents.R, ContainerTypes.R, Stats.R);
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

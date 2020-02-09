package de.mennomax.astikorcarts;

import de.mennomax.astikorcarts.client.ClientInitializer;
import de.mennomax.astikorcarts.server.ServerInitializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AstikorCarts.ID)
public final class AstikorCarts {
    public static final String ID = "astikorcarts";

    public AstikorCarts() {
        DistExecutor.runForDist(() -> ClientInitializer::new, () -> ServerInitializer::new).init(new Initializer.Mod() {
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
        });
    }
}

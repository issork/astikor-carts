package de.mennomax.astikorcarts;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;

public interface Initializer {
    void init(final Mod mod);

    interface Mod {
        ModLoadingContext context();

        IEventBus bus();

        IEventBus modBus();
    }
}

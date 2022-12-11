package de.mennomax.astikorcarts.client.renderer;

import de.mennomax.astikorcarts.AstikorCarts;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class AstikorCartsModelLayers {

    public static final ModelLayerLocation ANIMAL_CART = main("animal_cart");
    public static final ModelLayerLocation PLOW = main("plow");
    public static final ModelLayerLocation SUPPLY_CART = main("supply_cart");

    private static ModelLayerLocation main(String name) {
        return layer(name, "main");
    }

    private static ModelLayerLocation layer(String name, String layer) {
        return new ModelLayerLocation(new ResourceLocation(AstikorCarts.ID, name), layer);
    }
}

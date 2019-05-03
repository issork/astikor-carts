package de.mennomax.astikorcarts.handler;

import de.mennomax.astikorcarts.client.gui.inventory.GuiPlow;
import de.mennomax.astikorcarts.entity.EntityCargoCart;
import de.mennomax.astikorcarts.entity.EntityPlowCart;
import de.mennomax.astikorcarts.inventory.ContainerPlowCart;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (id)
        {
            case 0:
                EntityCargoCart cart = (EntityCargoCart) world.getEntityByID(x);
                return new ContainerChest(player.inventory, cart.inventory, player);
            case 1:
                EntityPlowCart plow = (EntityPlowCart) world.getEntityByID(x);
                return new ContainerPlowCart(player.inventory, plow.inventory, plow, player);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (id)
        {
            case 0:
                return new GuiChest(player.inventory, ((EntityCargoCart) world.getEntityByID(x)).inventory);
            case 1:
                EntityPlowCart plow = (EntityPlowCart) world.getEntityByID(x);
                return new GuiPlow(player.inventory, plow.inventory, plow, player);
            default:
                return null;
        }
    }

}

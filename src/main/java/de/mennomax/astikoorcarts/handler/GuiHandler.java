package de.mennomax.astikoorcarts.handler;

import de.mennomax.astikoorcarts.entity.EntityCargoCart;
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
            return new ContainerChest(player.inventory, ((EntityCargoCart) world.getEntityByID(x)).cargo, player);
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
            return new GuiChest(player.inventory, ((EntityCargoCart) world.getEntityByID(x)).cargo);
        default:
            return null;
        }
    }

}

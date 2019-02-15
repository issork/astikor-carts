package de.mennomax.astikorcarts.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PullStorage implements IStorage<IPull>
{
    @Override
    public NBTBase writeNBT(Capability<IPull> capability, IPull instance, EnumFacing side)
    {
        return null;
        
    }

    @Override
    public void readNBT(Capability<IPull> capability, IPull instance, EnumFacing side, NBTBase nbt)
    {
        
    }
}
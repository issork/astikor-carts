package de.mennomax.astikoorcarts.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PullStorage implements IStorage<IPull>
{
    @Override
    public NBTBase writeNBT(Capability<IPull> capability, IPull instance, EnumFacing side)
    {
        if (instance.getDrawn() != null)
        {
            return NBTUtil.createUUIDTag(instance.getDrawn().getPersistentID());
        }
        return new NBTTagCompound();
    }

    @Override
    public void readNBT(Capability<IPull> capability, IPull instance, EnumFacing side, NBTBase nbt)
    {
        if (!((NBTTagCompound) nbt).hasNoTags())
        {
            instance.setFirstDrawnUUID(NBTUtil.getUUIDFromTag((NBTTagCompound) nbt));
        }
    }
}
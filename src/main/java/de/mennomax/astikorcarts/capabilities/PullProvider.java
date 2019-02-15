package de.mennomax.astikorcarts.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class PullProvider implements ICapabilitySerializable<NBTBase>
{
    @CapabilityInject(IPull.class)
    public static final Capability<IPull> PULL = null;

    private IPull instance = PULL.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == PULL;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == PULL)
        {
            return PULL.cast(this.instance);
        }
        return null;
    }

    @Override
    public NBTBase serializeNBT()
    {
        return PULL.getStorage().writeNBT(PULL, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        PULL.getStorage().readNBT(PULL, this.instance, null, nbt);
    }
}
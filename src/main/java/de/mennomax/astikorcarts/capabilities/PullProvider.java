package de.mennomax.astikorcarts.capabilities;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class PullProvider implements ICapabilityProvider
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
}
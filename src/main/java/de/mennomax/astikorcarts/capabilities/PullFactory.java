package de.mennomax.astikorcarts.capabilities;

import java.util.UUID;

import de.mennomax.astikorcarts.entity.AbstractDrawn;

public class PullFactory implements IPull
{
    private AbstractDrawn drawn = null;
    private UUID drawnUUID = null;

    @Override
    public void setDrawn(AbstractDrawn drawnIn)
    {
        this.drawn = drawnIn;
    }

    @Override
    public AbstractDrawn getDrawn()
    {
        return this.drawn;
    }

    @Override
    public UUID getFirstDrawnUUID()
    {
        return drawnUUID;
    }

    @Override
    public void setFirstDrawnUUID(UUID uuidIn)
    {
        this.drawnUUID = uuidIn;
    }

}
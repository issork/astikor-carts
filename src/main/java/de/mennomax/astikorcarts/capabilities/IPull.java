package de.mennomax.astikorcarts.capabilities;

import java.util.UUID;

import de.mennomax.astikorcarts.entity.AbstractDrawn;

public interface IPull
{
    public void setDrawn(AbstractDrawn drawnIn);

    public AbstractDrawn getDrawn();

    public UUID getFirstDrawnUUID();

    public void setFirstDrawnUUID(UUID uuidIn);
}
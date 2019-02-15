package de.mennomax.astikorcarts.capabilities;

import de.mennomax.astikorcarts.entity.AbstractDrawn;

public interface IPull
{
    public void setDrawn(AbstractDrawn drawnIn);

    public AbstractDrawn getDrawn();
}
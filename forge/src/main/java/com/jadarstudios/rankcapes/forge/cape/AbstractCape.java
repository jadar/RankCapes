/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.cape;

import net.minecraft.client.entity.AbstractClientPlayer;

import java.awt.image.BufferedImage;

public abstract class AbstractCape implements Cloneable
{

    public abstract BufferedImage getCapeTexture();

    public abstract void loadTexture(AbstractClientPlayer player);

    public abstract String getName();

    @Override
    public final AbstractCape clone()
    {
        try
        {
            return (AbstractCape) super.clone();
        }
        catch (CloneNotSupportedException ignored)
        {
        }

        return null;
    }
}

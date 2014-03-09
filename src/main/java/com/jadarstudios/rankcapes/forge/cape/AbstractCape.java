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

/**
 * This class is the base class for all capes.
 */
public abstract class AbstractCape implements Cloneable
{

    /*
     * Gets the cape's texture.
     */
    public abstract BufferedImage getCapeTexture();

    /**
     * Loads the capes texture. Used for setting the cape.
     *
     * @param player the player to load the texture to.
     */
    public abstract void loadTexture(AbstractClientPlayer player);

    /*
     * Gets the name of a cape.
     */
    public abstract String getName();

    /**
     * Clones the cape so each player has a unique cape instance.
     */
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

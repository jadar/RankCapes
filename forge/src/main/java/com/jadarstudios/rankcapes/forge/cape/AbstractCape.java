/**
 * RankCapes Forge Mod
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.cape;

import java.awt.image.BufferedImage;

import net.minecraft.client.entity.AbstractClientPlayer;

public abstract class AbstractCape implements Cloneable
{
    
    public abstract BufferedImage getCapeTexture();
    public abstract void loadTexture(AbstractClientPlayer player);
    public abstract String getName();
    
    @Override
    public AbstractCape clone()
    {
        try
        {
            return (AbstractCape)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            ;
        }
        
        return null;
    }
}

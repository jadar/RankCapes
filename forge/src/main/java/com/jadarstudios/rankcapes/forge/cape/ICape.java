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

public interface ICape
{
    
    BufferedImage getCapeTexture();
    
    void loadTexture(AbstractClientPlayer player);
    
    String getName();
}

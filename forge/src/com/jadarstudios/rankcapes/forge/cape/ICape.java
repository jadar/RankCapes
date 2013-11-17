/**
 * RankCapes Forge Mod
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.cape;

import net.minecraft.util.ResourceLocation;

public interface ICape
{
    
    LoadCapeData getCapeData();
    
    ResourceLocation getCapeResource();
    
    void loadTexture();
    
}

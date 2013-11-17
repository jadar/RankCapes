package com.jadarstudios.rankcapes.forge.cape;

import net.minecraft.util.ResourceLocation;

public interface ICape
{
    
    LoadCapeData getCapeData();
    
    ResourceLocation getCapeResource();

    void loadTexture();
    
}

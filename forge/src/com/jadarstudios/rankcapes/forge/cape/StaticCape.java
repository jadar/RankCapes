package com.jadarstudios.rankcapes.forge.cape;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class StaticCape implements ICape
{
    
    private boolean loadedTexture = false;
    private LoadCapeData capeData;
    private ResourceLocation capeLocation;
    
    public StaticCape(ResourceLocation parCapeLocation, LoadCapeData parCapeData)
    {
        capeData = parCapeData;
        capeLocation = parCapeLocation;
    }
    
    @Override
    public LoadCapeData getCapeData()
    {
        return capeData;
    }
    
    @Override
    public ResourceLocation getCapeResource()
    {
        return capeLocation;
    }
    
    @Override
    public void loadTexture()
    {
        if(!this.loadedTexture)
        {
            this.loadedTexture = Minecraft.getMinecraft().getTextureManager().loadTexture(this.getCapeResource(), this.getCapeData());
        }
    }
    
}

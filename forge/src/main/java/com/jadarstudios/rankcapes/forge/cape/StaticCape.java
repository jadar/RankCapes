/**
 * RankCapes Forge Mod
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.cape;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.ITextureObject;

public class StaticCape implements ICape
{
    
    protected ITextureObject capeData;
    protected String name;
    
    public StaticCape(String name, ITextureObject parCapeData)
    {
        capeData = parCapeData;
    }
    
    @Override
    public ITextureObject getCapeTexture()
    {
        return capeData;
    }

    
    @Override
    public void loadTexture(AbstractClientPlayer player)
    {
//        if (!loadedTexture)
//        {
           // loadedTexture = 
        Minecraft.getMinecraft().getTextureManager().loadTexture(player.getLocationCape(), getCapeTexture());
//        }
    }

    @Override
    public String getName()
    {
        return this.name;
    }
    
    public StaticCape setName(String name)
    {
        this.name = name;
        return this;
    }
}

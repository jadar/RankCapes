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
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureUtil;

public class StaticCape implements ICape
{
    
    protected BufferedImage capeImage;
    protected String name;
    
    public StaticCape(String name, BufferedImage capeImage)
    {
        this.capeImage = new HDImageBuffer().parseUserSkin(capeImage);
        this.name = name;
    }
    
    @Override
    public BufferedImage getCapeTexture()
    {
        return this.capeImage;
    }
    
    @Override
    public void loadTexture(AbstractClientPlayer player)
    {
        ThreadDownloadImageData data = player.getTextureCape();
        data.setBufferedImage(this.capeImage);
        TextureUtil.uploadTextureImage(data.getGlTextureId(), this.capeImage);
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

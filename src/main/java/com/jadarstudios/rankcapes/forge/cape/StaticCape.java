/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.cape;

import com.jadarstudios.rankcapes.forge.handler.CapeHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureUtil;

import java.awt.image.BufferedImage;

/**
 * This class is a static cape. It holds the texture and loads the texture to the GPU.
 */
public class StaticCape extends AbstractCape
{

    protected BufferedImage capeImage;
    protected int[] texture;
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
        if (this.texture == null)
        {
            this.texture = readImageData(capeImage);
        }

        ThreadDownloadImageData data = (ThreadDownloadImageData) CapeHandler.getPlayerCapeTexture(player);
        data.setBufferedImage(this.capeImage);
        TextureUtil.uploadTexture(data.getGlTextureId(), this.texture, capeImage.getWidth(), capeImage.getHeight());
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

    private static int[] readImageData(BufferedImage bufferedImage)
    {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] data = new int[width * height];
        bufferedImage.getRGB(0, 0, width, height, data, 0, width);
        return data;
    }
}

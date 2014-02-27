/**
 * RankCapes Forge Mod
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.cape;

import java.awt.image.BufferedImage;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResourceManager;

import com.jadarstudios.rankcapes.forge.RankCapesForge;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
/**
 * This class is meant to put into AbstractClientPlayer.downloadImageCape instead of ThreadDownloadImageData.
 * It is used to load a cape from a texture on disk/memory and not from a URL.
 * 
 * @author Jadar
 */
public class BufferedImageTexture extends AbstractTexture
{
    
    // for printing debug text.
    protected boolean debug = false;
    protected BufferedImage image;
    protected IImageBuffer imageBuffer;

    public BufferedImageTexture(BufferedImage image)
    {
        this(image, new HDImageBuffer());
    }
    
    public BufferedImageTexture(BufferedImage image, IImageBuffer imageBuffer)
    {
        this.image = image;
        this.imageBuffer = imageBuffer;
    }
    
    @Override
    /**
     * Parses the BufferedImage with its ImageBuffer and uploads it to the GPU.
     */
    public void loadTexture(IResourceManager par1ResourceManager)
    {
        if (debug)
        {
            RankCapesForge.log.info("Loading cape data!");
        }
        
        // uses ImageBuffer to parse image.
        if (imageBuffer != null)
        {
            image = imageBuffer.parseUserSkin(image);
        }
    }
}

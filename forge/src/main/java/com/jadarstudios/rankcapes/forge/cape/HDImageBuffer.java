/**
 * RankCapes Forge Mod
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.cape;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import net.minecraft.client.renderer.IImageBuffer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HDImageBuffer implements IImageBuffer
{
    
    private int imageWidth;
    private int imageHeight;
    
    @Override
    public BufferedImage parseUserSkin(BufferedImage image)
    {
        if (image == null)
            return null;
        else
        {
            this.imageWidth = image.getWidth() <= 64 ? 64 : image.getWidth();
            this.imageHeight = image.getHeight() <= 32 ? 32 : image.getHeight();
            
            BufferedImage capeImage = new BufferedImage(this.imageWidth, this.imageHeight, 2);
            
            Graphics graphics = capeImage.getGraphics();
            graphics.drawImage(image, 0, 0, (ImageObserver) null);
            graphics.dispose();
            
            return capeImage;
        }
    }
}

/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.cape;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.IImageBuffer;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This class makes sure the capes are the right size for binding.
 */
@SideOnly(Side.CLIENT)
public class HDImageBuffer implements IImageBuffer
{

    @Override
    public BufferedImage parseUserSkin(BufferedImage image)
    {
        if (image == null)
        {
            return null;
        }

        int imageWidth = image.getWidth() <= 64 ? 64 : image.getWidth();
        int imageHeight = image.getHeight() <= 32 ? 32 : image.getHeight();

        BufferedImage capeImage = new BufferedImage(imageWidth, imageHeight, 2);

        Graphics graphics = capeImage.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        return capeImage;
    }
}

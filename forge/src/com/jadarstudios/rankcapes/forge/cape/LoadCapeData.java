package com.jadarstudios.rankcapes.forge.cape;

import java.awt.image.BufferedImage;
import java.io.IOException;

import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
/**
 * This class is meant to put into AbstractClientPlayer.downloadImageCape instead of ThreadDownloadImageData.
 * It is used to load a cape from a texture on disk/memory and not from a URL.
 * 
 * @author Jadar
 */
public class LoadCapeData extends ThreadDownloadImageData
{
    
    // for printing debug text.
    private boolean debug = false;
    
    /**
     * Creates a new instance of CapeData.
     * 
     * @param imageInput
     *            the InputStream that the image data is loaded from.
     * @param location
     *            the ResourceLocation in which to load the image data into.
     */
    public LoadCapeData(BufferedImage image, ResourceLocation location)
    {
        super("", location, new CapeImageBufferDownload());
        bufferedImage = image;
    }
    
    @Override
    /**
     * Parses the BufferedImage with its ImageBuffer and uploads it to the GPU.
     */
    public void loadTexture(ResourceManager par1ResourceManager) throws IOException
    {
        if(debug)
            System.out.println("***Load Cape Data!***");
        
        // uses ImageBuffer to parse image.
        if (imageBuffer != null)
        {
            bufferedImage = imageBuffer.parseUserSkin(bufferedImage);
            TextureUtil.uploadTextureImage(getGlTextureId(), bufferedImage);
        }
    }
}

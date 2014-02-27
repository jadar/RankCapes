/**
 * RankCapes Forge Mod
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.cape;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import joptsimple.internal.Strings;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.jadarstudios.rankcapes.forge.RankCapesForge;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
/**
 * This class represents a cape pack. 
 * It stores the capes, parses metadata, and assembles AnimatedCapes.
 * 
 * @author Jadar
 */
public class CapePack
{
    
    /**
     * JSON Parser.
     */
    private static Gson parser = new Gson();
    
    /**
     * The server address that this cape pack belongs to.
     */
    private String serverAddress = "";
    
    /** map of filenames (no extension) to static capes. */
    private HashMap<String, StaticCape> unprocessedCapes;
    /** processed capes based on the metadata. */
    HashMap<String, ICape> processedCapes;
    
    /**
     * Creates a new CapePack. Stores all the capes.
     * 
     * @param input
     *            bytes of a valid zip file.
     */
    public CapePack(String address, byte[] input)
    {
        serverAddress = address;
        
        unprocessedCapes = new HashMap<String, StaticCape>();
        processedCapes = new HashMap<String, ICape>();
        
        parsePack(input);
    }
    
    /**
     * Parses the Zip file in memory.
     * 
     * @param input
     *            the bytes of a valid zip file
     */
    private void parsePack(byte[] input)
    {
        try
        {
            ZipInputStream zipInput = new ZipInputStream(new ByteArrayInputStream(input));
            
            String metadata = "";
            
            ZipEntry entry;
            while ((entry = zipInput.getNextEntry()) != null)
            {
                String name = entry.getName();
                if (name.endsWith(".png"))
                {
                    // remove file extension from the name.
                    name = FilenameUtils.removeExtension(name);
                    
                    StaticCape cape = loadCape(name, zipInput);
                    unprocessedCapes.put(name, cape);
                }
                else if (name.endsWith(".mcmeta"))
                {
                    // parses the pack metadata.
                    InputStreamReader streamReader = new InputStreamReader(zipInput);
                    while (streamReader.ready())
                    {
                        metadata += (char) streamReader.read();
                    }
                }
            }
            if (!Strings.isNullOrEmpty(metadata))
            {
                parsePackMetadata(StringUtils.remove(metadata, (char) 65535));
            }
            else
            {
                RankCapesForge.log.warn("Cape Pack metadata is missing!");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Parses the pack JSON metadata.
     * 
     * @param metadata
     */
    @SuppressWarnings("rawtypes")
    private void parsePackMetadata(String metadata)
    {
        try
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> groups = parser.fromJson(metadata, Map.class);
            
            // loops through every entry in the base of the JSON file.
            for (Entry<String, Object> n : groups.entrySet())
            {
                String name = n.getKey();
                Object value = n.getValue();

                if (value instanceof Map)
                {
                    
                    parseAnimatedCapeNode(name, (Map)value);
                }
                else if (value instanceof String)
                {
                    parseStaticCapeNode(name, (String)value);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Parses an animated cape JSON node and creates the cape.
     * 
     * @param nodeName
     * @param node
     */
    private void parseAnimatedCapeNode(String name, @SuppressWarnings("rawtypes") Map node)
    {
        Object framesObj = node.get("frames");
        Object fpsObj = node.get("fps");
        
        if(framesObj instanceof String[] && fpsObj instanceof Integer)
        { 
            String[] frames = (String[])framesObj;
            int fps = (Integer)fpsObj;
            
            AnimatedCape cape = new AnimatedCape(name, fps);
            
            for (String frame : frames)
            {
                String fileName = FilenameUtils.removeExtension(frame);
                if (!Strings.isNullOrEmpty(fileName))
                {
                    if (unprocessedCapes.containsKey(fileName))
                
                    {
                        cape.addFrame(unprocessedCapes.get(fileName));
                    }
            
                }
            }
            
            processedCapes.put(name, cape);
        }
    }
    
    /**
     * Parses a static cape JSON node.
     * 
     * @param nodeKey
     *            the JSON node key
     * @param node
     *            the JSON node.
     */
    private void parseStaticCapeNode(String name, String capeFile)
    {
        String rankName = name;
        String fileName = FilenameUtils.removeExtension(capeFile);
        if (unprocessedCapes.containsKey(fileName))
        {
            StaticCape cape = unprocessedCapes.get(fileName).setName(rankName);
            processedCapes.put(rankName, cape);
        }
    }
    
    /**
     * Loads a cape using the given InputStream.
     * 
     * @param name
     *            name to use with the created ResourceLocation.
     * @param imageInput
     *            input to a valid image file.
     * @return a StaticCape instance.
     * @throws IOException
     */
    public StaticCape loadCape(String name, InputStream imageInput) throws IOException
    {
        // removes extension just in case.
        name = FilenameUtils.removeExtension(name).trim();

        BufferedImage image = ImageIO.read(imageInput);
        BufferedImageTexture loadImageData = new BufferedImageTexture(image);
        
        return new StaticCape(name, loadImageData);
    }
    
    /**
     * Gets a cape from the pack.
     * 
     * @param capeName
     *            name of the cape
     * @return cape that the name is mapped to.
     */
    public ICape getCape(String capeName)
    {
        return processedCapes.get(capeName);
    }
    
    /**
     * Gets the server address that this cape pack belongs to.
     * 
     * @return
     */
    public String getServerAddress()
    {
        return serverAddress;
    }
    
    /**
     * Debug code to print all the finished capes.
     */
    public void printCapeKeys()
    {
        System.out.println("\n\n\n");
        for (String s : processedCapes.keySet())
        {
            System.out.println(String.format("Print Cape: %s", s));
        }
        System.out.println("\n\n\n");
    }
}

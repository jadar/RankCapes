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
import java.util.ArrayList;
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
    public CapePack(byte[] input)
    {
        this.unprocessedCapes = new HashMap<String, StaticCape>();
        this.processedCapes = new HashMap<String, ICape>();
        
        this.parsePack(input);
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
                // System.out.println(name);
                if (name.endsWith(".png"))
                {
                    // remove file extension from the name.
                    name = FilenameUtils.removeExtension(name);
                    
                    StaticCape cape = this.loadCape(name, zipInput);
                    this.unprocessedCapes.put(name, cape);
                }
                else if (name.endsWith(".mcmeta"))
                {
                    // parses the pack metadata.
                    InputStreamReader streamReader = new InputStreamReader(zipInput);
                    while (streamReader.ready())
                        metadata += (char) streamReader.read();
                }
            }
            if (!Strings.isNullOrEmpty(metadata))
                this.parsePackMetadata(StringUtils.remove(metadata, (char) 65535));
            else
                RankCapesForge.log.warn("Cape Pack metadata is missing!");
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
                    this.parseAnimatedCapeNode(name, (Map) value);
                else if (value instanceof String)
                    this.parseStaticCapeNode(name, (String) value);
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
        
        if (framesObj instanceof ArrayList && fpsObj instanceof Double)
        {
            @SuppressWarnings("unchecked")
            ArrayList<Object> frames = (ArrayList<Object>) framesObj;
            int fps = ((Double) fpsObj).intValue();
            
            AnimatedCape cape = new AnimatedCape(name, fps);
            
            for (Object frameObj : frames)
                if (frameObj instanceof String)
                {
                    String frame = (String) frameObj;
                    
                    String fileName = FilenameUtils.removeExtension(frame);
                    if (!Strings.isNullOrEmpty(fileName))
                        if (this.unprocessedCapes.containsKey(fileName))
                            cape.addFrame(this.unprocessedCapes.get(fileName));
                }
            
            this.processedCapes.put(name, cape);
        }
        else
            RankCapesForge.log.warn("Well shit..");
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
        if (this.unprocessedCapes.containsKey(fileName))
        {
            StaticCape cape = this.unprocessedCapes.get(fileName).setName(rankName);
            this.processedCapes.put(rankName, cape);
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
        
        return new StaticCape(name, image);
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
        return this.processedCapes.get(capeName);
    }
    
    /**
     * Debug code to print all the finished capes.
     */
    public void printCapeKeys()
    {
        System.out.println("\n\n\n");
        for (String s : this.processedCapes.keySet())
            System.out.println(String.format("Print Cape: %s", s));
        System.out.println("\n\n\n");
    }
}

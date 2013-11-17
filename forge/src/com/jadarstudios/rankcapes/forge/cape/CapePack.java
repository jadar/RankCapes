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
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import joptsimple.internal.Strings;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import argo.saj.InvalidSyntaxException;

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
    private static JdomParser parser = new JdomParser();
    
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
                RankCapesForge.log.severe("Cape Pack metadata is missing!");
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
    private void parsePackMetadata(String metadata)
    {
        try
        {
            JsonRootNode root = parser.parse(metadata);
            
            // loops through every entry in the base of the JSON file.
            for (Entry<JsonStringNode, JsonNode> n : root.getFields().entrySet())
            {
                JsonNode node = n.getValue();
                
                if (node.hasFields() && !node.hasElements())
                {
                    parseAnimatedCapeNode(n.getKey(), node);
                }
                else if (node.hasText() && !node.hasElements())
                {
                    parseStaticCapeNode(n.getKey(), node);
                }
            }
        }
        catch (InvalidSyntaxException e)
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
    private void parseAnimatedCapeNode(JsonStringNode nodeName, JsonNode node)
    {
        String rankName = nodeName.getStringValue();
        int fps = Integer.parseInt(node.getNode("fps").getNumberValue());
        List<JsonNode> frames = node.getArrayNode("frames");
        if (frames != null)
        {
            AnimatedCape cape = new AnimatedCape(fps);
            
            for (JsonNode field : frames)
            {
                if (field.hasElements() || field.hasFields())
                {
                    continue;
                    // invalid node report!
                }
                
                String fileName = FilenameUtils.removeExtension(field.getText());
                if (!Strings.isNullOrEmpty(fileName))
                    if (unprocessedCapes.containsKey(fileName))
                    {
                        cape.addFrame(unprocessedCapes.get(fileName));
                    }
                
            }
            
            processedCapes.put(rankName, cape);
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
    private void parseStaticCapeNode(JsonStringNode nodeKey, JsonNode node)
    {
        String rankName = nodeKey.getStringValue();
        String fileName = FilenameUtils.removeExtension(node.getStringValue());
        if (unprocessedCapes.containsKey(fileName))
        {
            StaticCape cape = unprocessedCapes.get(fileName);
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
        
        ResourceLocation location = new ResourceLocation("RankCapes/" + name);
        
        BufferedImage image = ImageIO.read(imageInput);
        LoadCapeData loadImageData = new LoadCapeData(image, location);
        
        return new StaticCape(location, loadImageData);
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

/**
 * RankCapes Bukkit Plugin.
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.persistence.PersistenceException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mcstats.MetricsLite;

import com.jadarstudios.rankcapes.bukkit.command.MyCapeCommand;
import com.jadarstudios.rankcapes.bukkit.database.PlayerCape;
import com.jadarstudios.rankcapes.bukkit.network.PluginPacketHandler;

/**
 * Handles all player event. Usually just passes them off to the PacketHandler.
 * 
 * @author Jadar
 * 
 */
public class RankCapesBukkit extends JavaPlugin
{
    private static RankCapesBukkit INSTANCE;
    public static Logger log;
    
    public static final String PLUGIN_CHANNEL = "rankcapes";
    
    private String capePackName = "";
    private byte[] capePack = null;
    
    private List<String> availableCapes;
    
    @Override
    public void onEnable()
    {
        INSTANCE = this;
        
        // initializes the availableCapes list.
        this.availableCapes = new ArrayList<String>();
        
        // sets up the logger.
        log = this.getLogger();
        
        // makes the plugin data folder if necessary.
        this.getDataFolder().mkdir();
        
        // sets up the config.
        this.setupConfig();
        
        // sets up the plugin metrics/stats (MCStats.org)
        this.setupMetrics();
        
        // registers the communication channels with bukkit.
        this.registerChannels();
        
        // sets up the plugin database.
        this.setupDatabase();
        
        // sets up test command.
        this.getCommand("mycape").setExecutor(new MyCapeCommand(this));
        //this.getCommand("testpacket").setExecutor(new CommandTestPacket(this));
        
        // loads cape pack into the capePack fild.
        this.loadCapePack();
        if (this.capePack == null)
        {
            log.severe("Cape Pack not found! It is either an invalid ZIP file or does not exist!");
            this.disable();
            return;
        }
        
        // checks if the pack has a pack.mcmeta in the zip.
        boolean valid = this.validatePack(this.capePack);
        if (!valid)
        {
            log.severe("Cape Pack is not valid! Either the pack.mcmeta file is missing or the file is corrupt.");
            this.disable();
            return;
        }
        
        // registers the player event hander.
        this.getServer().getPluginManager().registerEvents(PlayerEventHandler.INSTANCE, this);
        
        log.info("RankCapes Initialized!");
    }
    
    /**
     * Loads in the plugin config.
     */
    private void setupConfig()
    {
        // makes default config file if its not already there.
        this.saveDefaultConfig();
        
        this.capePackName = this.getConfig().getString("cape-pack");
    }
    
    /**
     * Sets up plugin metrics (MCStats.org)
     */
    private void setupMetrics()
    {
        try
        {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        }
        catch (IOException e)
        {
            ;
        }
        catch (NoClassDefFoundError e)
        {
            ;
        }
    }
    
    private void setupDatabase()
    {
        try
        {
            // finds if database exists.
            this.getDatabase().find(PlayerCape.class).findRowCount();
        }
        catch (PersistenceException ex)
        {
            log.info("Installing database for " + this.getDescription().getName() + " due to first time usage");
            this.installDDL();
        }
    }
    
    /**
     * Reads the cape pack into the capePack byte array to be sent to the
     * client.
     */
    private void loadCapePack()
    {
        // location of the cape pack.
        File file = new File(this.getDataFolder() + "/" + this.capePackName);
        
        if (file.isDirectory())
        {
            log.severe("Error parsing Cape Pack: Cape Pack " + file.getName() + " is a directory, not a file.");
            return;
        }
        
        // copies default capes.zip to the plugin folder
        if (!file.exists())
            this.saveResource("capes.zip", false);
        
        log.info("Loading cape pack: " + file.getName());
        
        // read cape pack from the RankCapes folder.
        try
        {
            FileInputStream tmpFileIn = new FileInputStream(file);
            this.capePack = new byte[(int) file.length()];
            
            tmpFileIn.read(this.capePack);
            tmpFileIn.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            log.severe("Error parsing Cape Pack: Could not find the cape pack file " + file.getName());
            return;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            log.severe("Error parsing Cape Pack: There was an error while loading " + file.getName());
            return;
        }
    }
    
    /**
     * Registers the plugin channels to communicate with the client.
     */
    private void registerChannels()
    {
        // outgoing channel
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, PLUGIN_CHANNEL);
        
        // 'get' the packet handler orginal to initialize it.
        PluginPacketHandler.INSTANCE.ordinal();
        
        // incoming channel.
        Bukkit.getMessenger().registerIncomingPluginChannel(this, PLUGIN_CHANNEL, PluginPacketHandler.INSTANCE);
    }
    
    @Override
    /**
     * Gets all the classes used to get data from the database.
     * @return list of classes for a table.
     */
    public List<Class<?>> getDatabaseClasses()
    {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(PlayerCape.class);
        return list;
    }
    
    @Override
    /**
     * Called when the plugin is disabled.
     */
    public void onDisable()
    {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this, PLUGIN_CHANNEL);
    }
    
    /**
     * Disables the plugin. Also logs a message.
     */
    public void disable()
    {
        log.info("Disabling!");
        this.getServer().getPluginManager().disablePlugin(this);
    }
    
    /**
     * Validates a cape pack and returns true if it is valid.
     * 
     * @param pack
     *            to validate
     * @return boolean is valid
     */
    private boolean validatePack(byte[] pack)
    {
        try
        {
            if (pack == null)
                return false;
            
            ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(pack));
            ZipEntry entry = null;
            
            // reads the zip and finds the files. if the pack config file is not
            // found, return false.
            while ((entry = zipIn.getNextEntry()) != null)
                // if the zip contains a file names "pack.mcmeta"
                if (entry.getName().equals("pack.mcmeta"))
                {
                    boolean b = this.parseMetadata(zipIn);
                    zipIn.close();
                    return b;
                }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            log.severe("Error parsing cape pack: Could not validate cape pack!");
            return false;
        }
        
        return true;
    }
    
    /**
     * Parses cape pack metadata.
     * 
     * @param input
     *            zip input stream of the cape pack.
     * @return was successful.
     */
    private boolean parseMetadata(ZipInputStream input)
    {
        try
        {
            // JsonRootNode root = parser.parse(new InputStreamReader(input));
            
            Object root = JSONValue.parse(new InputStreamReader(input));
            JSONObject object = (JSONObject) root;
            
            // loops through every entry in the base of the JSON file.
            for (Object key : object.keySet())
                if (key instanceof String)
                {
                    String cape = (String) key;
                    this.availableCapes.add(cape);
                }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets all the capes that are available to be used.
     * 
     * @return list of all capes.
     */
    public List<String> getAvailableCapes()
    {
        return this.availableCapes;
    }
    
    /**
     * Gets the cape pack in bytes.
     * 
     * @return cape pack byte array.
     */
    public byte[] getPack()
    {
        return this.capePack;
    }
    
    /**
     * Gets player's cape from plugin database.
     * 
     * @param player
     *            player whose cape to lookup.
     * @return player's cape.
     */
    public PlayerCape getPlayerCape(Player player)
    {
        return this.getDatabase().find(PlayerCape.class).where().ieq("playerName", player.getName()).findUnique();
    }
    
    public static RankCapesBukkit instance()
    {
        return INSTANCE;
    }
}

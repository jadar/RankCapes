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
import com.jadarstudios.rankcapes.bukkit.network.CapePackServerListenThread;
import com.jadarstudios.rankcapes.bukkit.network.PluginPacketHandler;

/**
 * Handles all player event. Usually just passes them off to the PacketHandler.
 *
 * @author Jadar
 *
 */
public class RankCapesBukkit extends JavaPlugin
{
    /**
     * The channel to use to exchange messages with the client.
     */
    public static final String PLUGIN_CHANNEL = "rankcapes";
    
    /**
     * Used to log messages.
     */
    public static Logger log;
    
    /**
     * Cape page file name from config file.
     */
    private String capePackName = "";
    
    /**
     * Cape pack zip bytes.
     */
    private byte[] capePack = null;
    
    /**
     * Server port to listen for connections on.
     */
    private int capeServerPort;
    
    /**
     * CapePackServerListenThread instance.
     */
    private CapePackServerListenThread listenThread;
    
    /**
     * plugin's packet handler instance
     */
    private PluginPacketHandler packetHandler;
    
    /**
     * all capes that are available.
     */
    private List<String> availableCapes;
    
    @Override
    /**
     * Called when the plugin is enabled.
     */
    public void onEnable()
    {
        // initializes the availableCapes list.
        availableCapes = new ArrayList<String>();
        
        // sets up the logger.
        log = getLogger();
        
        // makes the plugin data folder if necessary.
        getDataFolder().mkdir();
        
        // sets up the config.
        setupConfig();
        
        // sets up the plugin metrics/stats (MCStats.org)
        setupMetrics();
        
        // registers the communication channels with bukkit.
        registerChannels();
        
        // sets up the plugin database.
        setupDatabase();
        
        // sets up test command.
        getCommand("mycape").setExecutor(new MyCapeCommand(this));
        
        // loads cape pack into the capePack fild.
        setupCapePack();
        if (capePack == null)
        {
            log.severe("Cape Pack is null! It is either an invalid ZIP file or does not exist!");
            disable();
            return;
        }
        
        // checks if the pack has a pack.mcmeta in the zip.
        boolean valid = validatePack(capePack);
        if (!valid)
        {
            log.severe("Cape Pack is invalid! Either the pack.mcmeta file is missing or the file is corrupt.");
            disable();
            return;
        }
        
        // creates and starts the listening thread.
        listenThread = new CapePackServerListenThread(this, capeServerPort);
        listenThread.setDaemon(true);
        listenThread.setName("RankCapes Cape Pack Server");
        listenThread.start();
        
        // registers the player event hander.
        getServer().getPluginManager().registerEvents(new PlayerEventHandler(this), this);
        
        log.info("RankCapes Initialized!");
    }
    
    /**
     * Loads in the plugin config.
     */
    private void setupConfig()
    {
        // makes default config file if its not already there.
        saveDefaultConfig();
        
        capePackName = getConfig().getString("cape-pack");
        capeServerPort = getConfig().getInt("port");
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
        catch(IOException e)
        {
            ;
        }
        catch(NoClassDefFoundError e)
        {
            ;
        }
    }
    
    private void setupDatabase()
    {
        try
        {
            // finds if database exists.
            getDatabase().find(PlayerCape.class).findRowCount();
        }
        catch (PersistenceException ex)
        {
            log.info("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
    }
    
    /**
     * Reads the cape pack into the capePack byte array to be sent to the
     * client.
     */
    private void setupCapePack()
    {
        // location of the cape pack.
        File tmpFile = new File(getDataFolder() + "/" + capePackName);
        
        if (tmpFile.isDirectory())
        {
            log.severe("Error parsing Cape Pack: Cape Pack " + tmpFile.getName() + " is a directory, not a file.");
            return;
        }
        
        // copies default capes.zip to the plugin folder
        if (!tmpFile.exists())
        {
            saveResource("capes.zip", false);
        }
        
        log.info("Loading cape pack: " + tmpFile.getName());
        
        // if file size of cape pack is greater than 5 mb then return.
        if (tmpFile.length() > 5242880)
            return;
        
        // read cape pack from the RankCapes folder.
        try
        {
            FileInputStream tmpFileIn = new FileInputStream(tmpFile);
            capePack = new byte[(int) tmpFile.length()];
            
            tmpFileIn.read(capePack);
            tmpFileIn.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            log.severe("Error parsing Cape Pack: Could not find the cape pack file " + tmpFile.getName());
            return;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            log.severe("Error parsing Cape Pack: There was an error while loading " + tmpFile.getName());
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
        
        // declare new packet handler
        packetHandler = new PluginPacketHandler(this);
        
        // incoming channel.
        Bukkit.getMessenger().registerIncomingPluginChannel(this, PLUGIN_CHANNEL, packetHandler);
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
        listenThread.stopAllThreads();
        listenThread.stopThread();
    }
    
    /**
     * Disables the plugin. Also logs a message.
     */
    public void disable()
    {
        log.info("Disabling!");
        getServer().getPluginManager().disablePlugin(this);
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
            {
                // if the zip contains a file names "pack.mcmeta"
                if (entry.getName().equals("pack.mcmeta"))
                {
                    boolean b = parseMetadata(zipIn);
                    zipIn.close();
                    return b;
                }
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
            //JsonRootNode root = parser.parse(new InputStreamReader(input));
            
            Object root = JSONValue.parse(new InputStreamReader(input));
            JSONObject object = (JSONObject)root;
           
            
            // loops through every entry in the base of the JSON file.
            for (Object key : object.keySet())
            {
                if(key instanceof String)
                {
                    String cape = (String)key;
                    availableCapes.add(cape);
                }
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
        return availableCapes;
    }
    
    /**
     * Gets the cape pack in bytes.
     * 
     * @return cape pack byte array.
     */
    public byte[] getPack()
    {
        return capePack;
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
        return getDatabase().find(PlayerCape.class).where().ieq("playerName", player.getName()).findUnique();
    }
    
    /**
     * Gets the packet handler instance.
     * 
     * @return packet handler instance.
     */
    public PluginPacketHandler getPacketHandler()
    {
        return packetHandler;
    }
    
    /**
     * Gets the CapePackServerListenThread instance.
     * 
     * @return CapePackServerListenThread instance.
     */
    public CapePackServerListenThread getListenThread()
    {
        return listenThread;
    }
    
    /**
     * Gets the port that the cape server should be hosted on.
     * 
     * @return cape port.
     */
    public int getCapeServerPort()
    {
        return capeServerPort;
    }
}

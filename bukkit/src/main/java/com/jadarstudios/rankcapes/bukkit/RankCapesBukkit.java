/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit;

import com.jadarstudios.rankcapes.bukkit.CapePackValidator.InvalidCapePackException;
import com.jadarstudios.rankcapes.bukkit.command.MyCapeCommand;
import com.jadarstudios.rankcapes.bukkit.database.PlayerCape;
import com.jadarstudios.rankcapes.bukkit.network.PluginPacketHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.mcstats.MetricsLite;

import javax.persistence.PersistenceException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class RankCapesBukkit extends JavaPlugin
{
    public static final String CAPE_PERMISSION_BASE = "rankcapes.cape.use.";
    public static final String PLUGIN_CHANNEL = "rankcapes";

    private static RankCapesBukkit INSTANCE;

    private String capePackName = "";
    private byte[] capePack = null;

    private List<String> availableCapes;

    public static RankCapesBukkit instance()
    {
        return INSTANCE;
    }

    @Override
    public void onEnable()
    {
        INSTANCE = this;

        // initializes the availableCapes list.
        this.availableCapes = new ArrayList<String>();

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
        // this.getCommand("testpacket").setExecutor(new CommandTestPacket(this));

        // loads cape pack into the capePack fild.
        this.loadCapePack();
        if (this.capePack == null)
        {
            this.getLogger().severe("Cape Pack not found! It is either an invalid ZIP file or does not exist!");
            this.disable();
            return;
        }

        try
        {
            this.validatePack(this.capePack);
        }
        catch(IOException e)
        {
            getLogger().severe("Error while validating Cape Pack! The file may be corrupt.");
            e.printStackTrace();
            this.disable();
            return;
        }
        catch(InvalidCapePackException e)
        {
            getLogger().severe("Error while validating Cape Pack!");
            e.printStackTrace();
            this.disable();
            return;
        }
        catch (ParseException e)
        {
            getLogger().severe("Error while validating Cape Pack!");
            e.printStackTrace();
            this.disable();
            return;
        }

        // registers the player event hander.
        this.getServer().getPluginManager().registerEvents(PlayerEventHandler.INSTANCE, this);

        PluginPacketHandler packetHandler = PluginPacketHandler.INSTANCE;

        for(Player p : packetHandler.getPlayersServing())
        {
            packetHandler.sendCapePack(p);
            packetHandler.sendAvailableCapes(p);
        }

        getLogger().info("RankCapes Initialized!");
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable()
    {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this, PLUGIN_CHANNEL);
        this.capePack = null;
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
        catch (IOException ignored) {}
        catch (NoClassDefFoundError ignored) {}
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
            getLogger().info("Installing database for " + this.getDescription().getName() + " due to first time usage");
            this.installDDL();
        }
    }

    /**
     * Registers the plugin channels to communicate with the client.
     */
    private void registerChannels()
    {
        // 'get' the packet handler ordinal to initialize it.
        PluginPacketHandler.INSTANCE.ordinal();

        // outgoing channel
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, PLUGIN_CHANNEL);

        // incoming channel.
        Bukkit.getMessenger().registerIncomingPluginChannel(this, PLUGIN_CHANNEL, PluginPacketHandler.INSTANCE);
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
            getLogger().severe("Error parsing Cape Pack: Cape Pack " + file.getName() + " is a directory, not a file.");
            return;
        }

        // copies default capes.zip to the plugin folder
        if (!file.exists())
        {
            this.saveResource("capes.zip", false);
        }

        getLogger().info("Loading cape pack: " + file.getName());

        // read cape pack from the RankCapes folder.
        try
        {
            FileInputStream fis = new FileInputStream(file);
            this.capePack = new byte[(int) file.length()];

            fis.read(this.capePack);
            fis.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            getLogger().severe("Error parsing Cape Pack: Could not find the cape pack file " + file.getName());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            getLogger().severe("Error parsing Cape Pack: There was an error while loading " + file.getName());
        }
    }

    /**
     * Validates a cape pack and returns true if it is valid.
     *
     * @param pack to validate
     */
    private void validatePack(byte[] pack) throws IOException, InvalidCapePackException, ParseException
    {
        boolean foundMetadata = false;

        if (pack == null)
        {
            throw new InvalidCapePackException("The cape pack was null");
        }

        if(!CapePackValidator.isZipFile(pack))
        {
            throw new InvalidCapePackException("The cape pack is not a ZIP file.");
        }

        ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(pack));
        ZipEntry entry;

        // reads the zip and finds the files. if the pack config file is not found, return false.
        while ((entry = zipIn.getNextEntry()) != null)
        // if the zip contains a file names "pack.mcmeta"
        {
            if (entry.getName().equals("pack.mcmeta"))
            {
                foundMetadata = true;
                try
                {
                    this.parseMetadata(zipIn);
                }
                finally
                {
                    zipIn.close();
                }

                break;
            }
        }

        if(!foundMetadata)
        {
            throw new InvalidCapePackException("The Cape Pack metadata was not found.");
        }
    }

    /**
     * Parses cape pack metadata.
     *
     * @param input zip input stream of the cape pack.
     */
    private void parseMetadata(ZipInputStream input) throws InvalidCapePackException, IOException, ParseException
    {
        Object root = JSONValue.parseWithException(new InputStreamReader(input));
        JSONObject object = (JSONObject) root;

        CapePackValidator.validatePack(object);

        for (Object key: object.keySet())
        {
            if (key instanceof String)
            {
                this.availableCapes.add((String)key);
            }
        }
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
    public byte[] getCapePack()
    {
        return this.capePack;
    }

    /**
     * Gets player's cape from plugin database.
     */
    public PlayerCape getPlayerCape(Player player)
    {
        return this.getDatabase().find(PlayerCape.class).where().ieq("playerName", player.getName()).findUnique();
    }

    public void setPlayerCape(PlayerCape cape)
    {
        this.getDatabase().save(cape);
    }

    public boolean deletePlayerCape(Player player)
    {
        PlayerCape cape = this.getPlayerCape(player);

        if(cape != null)
        {
            this.getDatabase().delete(cape);
            return true;
        }

        return false;
    }
    /**
     * Gets all the classes used to get data from the database.
     *
     * @return list of classes for a table.
     */
    @Override
    public List<Class<?>> getDatabaseClasses()
    {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(PlayerCape.class);
        return list;
    }

    /**
     * Disables the plugin. Also logs a message.
     */
    public void disable()
    {
        getLogger().info("Disabling!");
        this.getServer().getPluginManager().disablePlugin(this);
    }
}

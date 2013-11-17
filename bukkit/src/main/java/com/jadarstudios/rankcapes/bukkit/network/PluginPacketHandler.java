/**
 * RankCapesBukkit Plugin.
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapesBukkit/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.network;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.jadarstudios.rankcapes.bukkit.RankCapesBukkit;
import com.jadarstudios.rankcapes.bukkit.database.PlayerCape;

/**
 * Handles packets sent on the plugin message channel.
 * 
 * @author Jadar
 */
public class PluginPacketHandler implements PluginMessageListener
{
    /**
     * The instance of the RankCapes plugin.
     */
    private final RankCapesBukkit plugin;
    
    /**
     * The plays this class serves/updates.
     */
    private final List<Player> playersServing;
    
    /**
     * 
     * @param parPlugin
     *            instance of RankCapes.
     */
    public PluginPacketHandler(RankCapesBukkit parPlugin)
    {
        plugin = parPlugin;
        playersServing = new ArrayList<Player>();
    }
    
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes)
    {
        // get string from bytes.
        String message = new String(bytes);
        
        // handle cape change
        if (message.startsWith("changeCape"))
        {
            handleCapeChage(message, player);
        }
        // handler remove cape
        else if (message.startsWith("removeCape"))
        {
            handleCapeRemoval(player);
        }
        // unknown command.
        else
        {
            RankCapesBukkit.log.warning("Player " + player.getName() + "sent an unknown packet. Either he has a newer version or he is a hacker.");
        }
        
    }
    
    /**
     * Sends the port and available capes to the player registering a channel.
     * 
     * @param event
     */
    public void handlePlugnChannelRegister(PlayerRegisterChannelEvent event)
    {
        // we only want to have clients with RankCapes installed. If the
        // RankCapes channel is not registered, we should not try to server the
        // player.
        if (!event.getChannel().equals(RankCapesBukkit.PLUGIN_CHANNEL))
            return;
        
        // get player and add to serving list.
        Player player = event.getPlayer();
        playersServing.add(player);
        
        // send cape server port, so player will connect.
        sendCapeServerPort(player);
        // send all player's capes.
        sendAllPlayerCapes(player);
        // send capes the player has permissions to.
        sendAvailableCapes(player);
        
        // update the other players that there is a new one.
        sendCapeUpdateToClients(player);
    }
    
    /**
     * Called when the player changes worlds. Used to send update to clients in
     * that world.
     * 
     * @param event
     */
    public void changeWorld(PlayerChangedWorldEvent event)
    {
        // player that changed world
        Player player = event.getPlayer();
        
        sendCapeRemovalUpdateToClients(player, event.getFrom());
        
        // send all player capes. only sends in same world.
        sendAllPlayerCapes(player);
        
        // updates players in the world that there is a new player.
        sendCapeUpdateToClients(player);
    }
    
    /**
     * Called when "changeCape" is recieved from the client.
     * 
     * @param message
     *            the full message recieved.
     * @param player
     *            the player to update.
     */
    private void handleCapeChage(String message, Player player)
    {
        // gets cape name by splitting the string. format is command:capename.
        String[] data = message.split(":");
        
        // checks if the player has the permission to use the cape it wants to.
        if (player.hasPermission("rankcapes.cape.use." + data[1]))
        {
            // player cape from database.
            PlayerCape cape = plugin.getPlayerCape(player);
            
            // if null, make a new one
            if (cape == null)
            {
                cape = new PlayerCape();
                cape.setPlayer(player);
            }
            
            // set cape
            cape.setCapeName(data[1]);
            
            // save the cape in the database.
            plugin.getDatabase().save(cape);
            
            // sends update to clients, including the updated player.
            sendCapeUpdateToClients(player);
        }
        else
        {
            RankCapesBukkit.log.warning("Player" + player.getName() + " tried to set a cape that he does not have access to! ");
        }
    }
    
    /**
     * Called when "removeCape" is received from a player.
     * 
     * @param player
     *            whose cape will be removed
     */
    private void handleCapeRemoval(Player player)
    {
        // deletes player's entry in the database.
        plugin.getDatabase().delete(plugin.getDatabase().find(PlayerCape.class).where().ieq("playerName", player.getName()).findUnique());
        
        // sends update to clients, including the updated player.
        sendCapeRemovalUpdateToClients(player);
    }
    
    /**
     * Sends the cape pack server port to the given player.
     * 
     * @param player
     *            the player to send the port to.
     */
    private void sendCapeServerPort(Player player)
    {
        String message = "transmitPort:" + plugin.getCapeServerPort();
        
        // send message to player.
        player.sendPluginMessage(plugin, "RankCapes", message.getBytes());
    }
    
    /**
     * Sends cape removal update to clients in the specified world.
     * 
     * @param updatedPlayer
     *            the player whose cape was removed.
     * @param playerWorld
     *            the world in which players are to be updated.
     */
    public void sendCapeRemovalUpdateToClients(Player updatedPlayer, World world)
    {
        // message to send
        String message = String.format("removeCapeUpdate:%s", updatedPlayer.getName());
        // loop through the players this plugin is serving.
        for (Player iteratorPlayer : playersServing)
        {
            // player is the same world as the
            if (world.getPlayers().contains(iteratorPlayer))
            {
                // send message to player
                iteratorPlayer.sendPluginMessage(plugin, RankCapesBukkit.PLUGIN_CHANNEL, message.getBytes());
            }
        }
    }
    
    /**
     * Sends cape removal update to clients in the same world as the updated
     * player.
     * 
     * @param updatedPlayer
     *            the player whose cape was removed.
     */
    public void sendCapeRemovalUpdateToClients(Player updatedPlayer)
    {
        this.sendCapeRemovalUpdateToClients(updatedPlayer, updatedPlayer.getWorld());
    }
    
    /**
     * Sends an update to all the players in the same world as the updated
     * player.
     * 
     * @param updatedPlayer
     */
    public void sendCapeUpdateToClients(Player updatedPlayer)
    {
        // player cape entry form database.
        PlayerCape cape = plugin.getPlayerCape(updatedPlayer);
        
        // if its null then no use updating.
        if (cape == null)
            return;
        
        String message = String.format("playerCapeUpdate:%s,%s", updatedPlayer.getName(), cape.getCapeName());
        
        // loop through the players this plugin is serving.
        for (Player iteratorPlayer : playersServing)
        {
            // if iteratorPlayer is in the same world as updatedPlayer
            if (updatedPlayer.getWorld().getPlayers().contains(iteratorPlayer))
            {
                // send message to player
                iteratorPlayer.sendPluginMessage(plugin, RankCapesBukkit.PLUGIN_CHANNEL, message.getBytes());
            }
        }
    }
    
    /**
     * Sends all the current capes belonging to players in the given player's
     * world, to the given player.
     * 
     * @param player
     *            to send to
     */
    public void sendAllPlayerCapes(Player player)
    {
        String message = "allPlayerCapes:";
        
        // loop through the players this plugin is serving.
        for (Player iteratorPlayer : playersServing)
        {
            // if iteratorPlayer is in the same world as updatedPlayer
            if (player.getWorld().getPlayers().contains(iteratorPlayer))
            {
                // get cape of iteratorPlayer
                PlayerCape cape = plugin.getPlayerCape(iteratorPlayer);
                
                // if not null then add it to the list.
                if (cape != null)
                {
                    message += String.format("%s,%s|", iteratorPlayer.getName(), cape.getCapeName());
                }
            }
        }
        
        // send message to player.
        player.sendPluginMessage(plugin, RankCapesBukkit.PLUGIN_CHANNEL, message.getBytes());
    }
    
    /**
     * Sends the capes that a player is allowed to put on.
     * 
     * @param player
     */
    public void sendAvailableCapes(Player player)
    {
        String message = "availableCapes:";
        
        // available capes to player.
        List<String> capes = getAvailableCapes(player);
        
        // make list into comma separated string.
        for (String cape : capes)
        {
            message += String.format("%s,", cape);
        }
        
        // send message to player
        player.sendPluginMessage(plugin, RankCapesBukkit.PLUGIN_CHANNEL, message.getBytes());
    }
    
    /**
     * Gets the capes that a player is allowed to put on.
     * 
     * @param player
     * @return
     */
    public List<String> getAvailableCapes(Player player)
    {
        // initialize list
        List<String> capes = new ArrayList<String>();
        
        // loop through all available capes.
        for (String cape : plugin.getAvailableCapes())
        {
            // find if player has the permission node to use that cape.
            if (player.hasPermission("rankcapes.cape.use." + cape))
            {
                capes.add(cape);
            }
        }
        
        return capes;
    }
    
    /**
     * Removes player from serving list. Typically used when a player
     * disconnects.
     * 
     * @param player
     *            player to be removed.
     */
    public void removeServingPlayer(Player player)
    {
        playersServing.remove(player);
    }
}

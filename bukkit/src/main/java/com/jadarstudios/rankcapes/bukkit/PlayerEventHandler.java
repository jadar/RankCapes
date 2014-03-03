/**
 * RankCapes Bukkit Plugin.
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

import com.jadarstudios.rankcapes.bukkit.network.PluginPacketHandler;

/**
 * Used to detect when players do things.
 * 
 * @author Jadar
 */
public enum PlayerEventHandler implements Listener
{
    INSTANCE;
    
    private static final RankCapesBukkit plugin = RankCapesBukkit.instance();
    
    /**
     * Called when a player logs in.
     */
    @EventHandler
    public void onRegisterChannel(PlayerRegisterChannelEvent event)
    {
        plugin.getLogger().info(event.getChannel());
        if (event.getChannel().equals(RankCapesBukkit.PLUGIN_CHANNEL))
            PluginPacketHandler.INSTANCE.handlePlugnChannelRegister(event);
    }
    
    /**
     * Called when a player logs out.
     */
    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event)
    {
        PluginPacketHandler.INSTANCE.removeServingPlayer(event.getPlayer());
    }
    
    /**
     * Called when a player changes worlds.
     */
    @EventHandler
    public void changeWorld(PlayerChangedWorldEvent event)
    {
        PluginPacketHandler.INSTANCE.changeWorld(event);
    }
}

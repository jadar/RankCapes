/**
 * RankCapesBukkit Plugin.
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapesBukkit/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

/**
 * Used to detect when players do things.
 * 
 * @author Jadar
 */
public class PlayerEventHandler implements Listener
{
    
    private final RankCapesBukkit plugin;
    
    public PlayerEventHandler(RankCapesBukkit parPlugin)
    {
        plugin = parPlugin;
    }
    
    @EventHandler
    /**
     * Called when a player logs in.
     */
    public void onRegisterChannel(PlayerRegisterChannelEvent event)
    {
        if (event.getChannel().equals("RankCapes"))
        {
            plugin.getPacketHandler().handlePlugnChannelRegister(event);
        }
    }
    
    @EventHandler
    /**
     * Called when a player logs out.
     */
    public void onPlayerLogout(PlayerQuitEvent event)
    {
        plugin.getPacketHandler().removeServingPlayer(event.getPlayer());
    }
    
    @EventHandler
    /**
     * Called when a player changes worlds.
     */
    public void changeWorld(PlayerChangedWorldEvent event)
    {
        plugin.getPacketHandler().changeWorld(event);
    }
}

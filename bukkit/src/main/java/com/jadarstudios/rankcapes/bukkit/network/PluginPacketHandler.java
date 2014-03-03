/**
 * RankCapes Bukkit Plugin.
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.jadarstudios.rankcapes.bukkit.RankCapesBukkit;
import com.jadarstudios.rankcapes.bukkit.database.PlayerCape;
import com.jadarstudios.rankcapes.bukkit.network.packet.C4PacketUpdateCape;
import com.jadarstudios.rankcapes.bukkit.network.packet.PacketBase;
import com.jadarstudios.rankcapes.bukkit.network.packet.S0PacketPlayerCapesUpdate;
import com.jadarstudios.rankcapes.bukkit.network.packet.S0PacketPlayerCapesUpdate.Type;
import com.jadarstudios.rankcapes.bukkit.network.packet.S1PacketCapePack;
import com.jadarstudios.rankcapes.bukkit.network.packet.S2PacketAvailableCapes;
import com.jadarstudios.rankcapes.bukkit.network.packet.S3PacketTest;

/**
 * Handles packets sent on the plugin message channel.
 * 
 * @author Jadar
 */
public enum PluginPacketHandler implements PluginMessageListener
{
    INSTANCE;
    
    private static final RankCapesBukkit plugin = RankCapesBukkit.instance();
    
    private final List<Player> playersServing;
    
    private PluginPacketHandler()
    {
        this.playersServing = new ArrayList<Player>();
    }
    
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes)
    {
        PacketBase packet;
        try
        {
            packet = PacketManager.INSTANCE.getPacketFromBytes(bytes);
            
            // handle cape change
            if (packet instanceof C4PacketUpdateCape)
            {
                C4PacketUpdateCape updatePacket = (C4PacketUpdateCape) packet;
                
                switch (updatePacket.updateType)
                {
                    case UPDATE:
                    {
                        this.handleCapeChage((C4PacketUpdateCape) packet, player);
                        break;
                    }
                    case REMOVE:
                    {
                        this.handleCapeRemoval(player);
                        break;
                    }
                }
            }
            else if (packet instanceof S3PacketTest)
                RankCapesBukkit.log.info(String.format("Test packet from CLIENT: %s with PAYLOAD: %s", player.getName(), ((S3PacketTest) packet).payload));
            
        }
        catch (Exception e)
        {
            RankCapesBukkit.log.severe("Error while processing packet from player " + player.getName());
            e.printStackTrace();
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
        this.playersServing.add(player);
        
        S3PacketTest packet = new S3PacketTest("Hello there!");
        this.sendPacketToPlayer(player, packet);
        
        RankCapesBukkit.log.info("Sent test packet with payload: " + packet.payload);
        
        this.sendCapePack(player);
        // send all player's capes.
        this.sendAllPlayerCapes(player);
        // send capes the player has permissions to.
        this.sendAvailableCapes(player);
        //
        // // update the other players that there is a new one.
        // sendCapeUpdateToClients(player);
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
        
        S0PacketPlayerCapesUpdate packetRemove = new S0PacketPlayerCapesUpdate(Type.REMOVE).addPlayer(player.getName(), "");
        this.sendPacketToWorld(event.getFrom(), packetRemove);
        
        // send all player capes. only sends in same world.
        this.sendAllPlayerCapes(player);
        
        // updates players in the world that there is a new player.
        this.sendCapeUpdates(player, Type.UPDATE);
    }
    
    public void sendCapePack(Player player)
    {
        byte[] pack = plugin.getPack();
        
        // chunks the array if necessary.
        if (pack.length >= Messenger.MAX_MESSAGE_SIZE)
        {
            // the chunk size is the max message size minus the size of the 3 bytes used by the
            // packet.
            int chunkSize = Messenger.MAX_MESSAGE_SIZE - Byte.SIZE * 3;
            
            for (int pos = 0; pos < pack.length; pos += chunkSize)
            {
                // makes sure we don't get an overflow exception
                int toPos = pos + chunkSize > pack.length ? pack.length : pos + chunkSize;
                byte[] chunk = Arrays.copyOfRange(pack, pos, toPos);
                
                S1PacketCapePack packet = new S1PacketCapePack(pack.length, chunk);
                this.sendPacketToPlayer(player, packet);
            }
        }
        else
        {
            S1PacketCapePack packet = new S1PacketCapePack(pack.length, pack);
            this.sendPacketToPlayer(player, packet);
        }
    }
    
    private void handleCapeChage(C4PacketUpdateCape packet, Player player)
    {
        String capeName = packet.cape;
        
        // checks if the player has the permission to use the cape it wants to.
        if (player.hasPermission("rankcapes.cape.use." + capeName))
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
            cape.setCapeName(capeName);
            
            // save the cape in the database.
            plugin.getDatabase().save(cape);
            
            // sends update to clients, including the updated player.
            this.sendCapeUpdates(player, Type.UPDATE);
        }
        else
            RankCapesBukkit.log.warning("Player" + player.getName() + " tried to set a cape that he does not have access to! ");
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
        this.sendCapeUpdates(player, Type.REMOVE);
    }
    
    /**
     * Sends a cape update of the given player to the world the player is in.
     * 
     * @param updatedPlayer
     */
    public void sendCapeUpdates(Player updated, Type type)
    {
        // player cape entry form database.
        PlayerCape cape = plugin.getPlayerCape(updated);
        
        // if its null then no use updating.
        if (cape == null && type == Type.UPDATE)
            return;
        
        S0PacketPlayerCapesUpdate packet = new S0PacketPlayerCapesUpdate(type);
        packet.addPlayer(cape);
        
        this.sendPacketToWorld(updated.getWorld(), packet);
    }
    
    public void sendAllPlayerCapes(Player player)
    {
        S0PacketPlayerCapesUpdate packet = new S0PacketPlayerCapesUpdate(Type.UPDATE);
        
        for (Player iteratorPlayer : this.playersServing)
            // if iteratorPlayer is in the same world as updatedPlayer
            if (player.getWorld().getPlayers().contains(iteratorPlayer))
            {
                // get cape of iteratorPlayer
                PlayerCape cape = plugin.getPlayerCape(iteratorPlayer);
                
                // if not null then add it to the list.
                if (cape != null)
                    packet.addPlayer(cape);
            }
        
        this.sendPacketToPlayer(player, packet);
    }
    
    /**
     * Sends the capes that a player is allowed to put on.
     * 
     * @param player
     */
    public void sendAvailableCapes(Player player)
    {
        // available capes to player.
        List<String> capes = this.getAvailableCapes(player);
        S2PacketAvailableCapes packet = new S2PacketAvailableCapes(capes);
        
        // send message to player
        this.sendPacketToPlayer(player, packet);
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
            // find if player has the permission node to use that cape.
            if (player.hasPermission("rankcapes.cape.use." + cape))
                capes.add(cape);
        
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
        this.playersServing.remove(player);
    }
    
    public void sendPacketToPlayer(Player player, PacketBase packet)
    {
        try
        {
            PacketManager packetManager = PacketManager.INSTANCE;
            byte[] bytes = packetManager.getBytesFromPacket(packet);
            
            player.sendPluginMessage(plugin, RankCapesBukkit.PLUGIN_CHANNEL, bytes);
        }
        catch (Exception e)
        {
            RankCapesBukkit.log.severe(String.format("Exception while writing and sending %s packet to player %s", packet.getClass().getSimpleName(), player.getName()));
            e.printStackTrace();
        }
    }
    
    public void sendPacketToWorld(World world, PacketBase packet)
    {
        try
        {
            PacketManager packetManager = PacketManager.INSTANCE;
            byte[] bytes = packetManager.getBytesFromPacket(packet);
            
            world.sendPluginMessage(plugin, RankCapesBukkit.PLUGIN_CHANNEL, bytes);
        }
        catch (Exception e)
        {
            RankCapesBukkit.log.severe(String.format("Exception while writing and sending %s packet to world %s", packet.getClass().getSimpleName(), world.getName()));
            e.printStackTrace();
        }
    }
}

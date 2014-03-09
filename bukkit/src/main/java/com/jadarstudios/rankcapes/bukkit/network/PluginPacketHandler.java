/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.network;

import com.jadarstudios.rankcapes.bukkit.RankCapesBukkit;
import com.jadarstudios.rankcapes.bukkit.database.PlayerCape;
import com.jadarstudios.rankcapes.bukkit.network.packet.*;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            {
                plugin.getLogger().info(String.format("Test packet from CLIENT: %s with PAYLOAD: %s", player.getName(), ((S3PacketTest) packet).payload));
            }

        }
        catch (Exception e)
        {
            plugin.getLogger().severe("Error while processing packet from player " + player.getName());
            e.printStackTrace();
        }
    }

    public void newPlayerJoined(PlayerRegisterChannelEvent event)
    {
        // we only want to have clients with RankCapes installed.
        if (!event.getChannel().equals(RankCapesBukkit.PLUGIN_CHANNEL))
        {
            return;
        }

        // get player and add to serving list.
        Player player = event.getPlayer();
        this.playersServing.add(player);

        this.sendCapePack(player);
        // send all player's capes.
        this.sendAllPlayerCapes(player);
        // send capes the player has permissions to.
        this.sendAvailableCapes(player);

        // update the other players that there is a new one.
        this.sendCapeUpdates(player, CapeUpdateType.UPDATE);
    }

    /**
     * Called when the player changes worlds. Used to send update to clients in
     * that world.
     *
     * @param event the event being raised
     */
    public void playerChangedWorld(PlayerChangedWorldEvent event)
    {
        // player that changed world
        Player player = event.getPlayer();

        S0PacketPlayerCapesUpdate packetRemove = new S0PacketPlayerCapesUpdate(CapeUpdateType.REMOVE).addPlayer(player.getName(), "");
        this.sendPacketToWorld(event.getFrom(), packetRemove);

        // send all player capes. only sends in same world.
        this.sendAllPlayerCapes(player);

        // updates players in the world that there is a new player.
        this.sendCapeUpdates(player, CapeUpdateType.UPDATE);
    }

    protected void handleCapeChage(C4PacketUpdateCape packet, Player player)
    {
        String capeName = packet.cape;

        // checks if the player has the permission to use the cape it wants to.
        if (player.hasPermission(RankCapesBukkit.CAPE_PERMISSION_BASE + capeName))
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
            plugin.setPlayerCape(cape);

            // sends update to clients, including the updated player.
            this.sendCapeUpdates(player, CapeUpdateType.UPDATE);
        }
        else
        {
            plugin.getLogger().warning("Player" + player.getName() + " tried to set a cape that he does not have access to! ");
        }
    }

    /**
     * Handles a cape removal.
     *
     * @param player whose cape will be removed
     */
    protected void handleCapeRemoval(Player player)
    {
        // deletes player's entry in the database.
        boolean flag = plugin.deletePlayerCape(player);

        if (flag)
        {
            // sends update to clients, including the updated player.
            this.sendCapeUpdates(player, CapeUpdateType.REMOVE);
        }
    }

    /**
     * Sends a {@link S1PacketCapePack} to the given player.
     *
     * @param player the player to send to
     */
    public void sendCapePack(Player player)
    {
        byte[] pack = plugin.getCapePack();

        // the chunk size is the max message size minus the size of the 3 bytes used by the packet.
        int chunkSize = Messenger.MAX_MESSAGE_SIZE - Byte.SIZE * 3;

        // chunks the array if necessary.
        if (pack.length >= Messenger.MAX_MESSAGE_SIZE)
        {
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

    /**
     * Sends a cape update of the given player to the world the player is in.
     */
    public void sendCapeUpdates(Player updated, CapeUpdateType type)
    {
        S0PacketPlayerCapesUpdate packet = new S0PacketPlayerCapesUpdate(type);

        if (type == CapeUpdateType.UPDATE)
        {
            PlayerCape cape = plugin.getPlayerCape(updated);
            if (cape != null)
            {
                packet.addPlayer(cape);
            }
        }
        else if (type == CapeUpdateType.REMOVE)
        {
            packet.addPlayer(updated.getName(), "");
        }

        if (packet.getUpdateCount() > 0)
        {
            this.sendPacketToWorld(updated.getWorld(), packet);
        }
    }

    public void sendAllPlayerCapes(Player player)
    {
        S0PacketPlayerCapesUpdate packet = new S0PacketPlayerCapesUpdate(CapeUpdateType.UPDATE);

        for (Player iteratorPlayer : this.playersServing)
        {
            // if iteratorPlayer is in the same world as updatedPlayer
            if (player.getWorld().getPlayers().contains(iteratorPlayer))
            {
                // get cape of iteratorPlayer
                PlayerCape cape = plugin.getPlayerCape(iteratorPlayer);

                // if not null then add it to the list.
                if (cape != null)
                {
                    packet.addPlayer(cape);
                }
            }
        }

        if (packet.getUpdateCount() > 0)
        {
            this.sendPacketToPlayer(player, packet);
        }
    }

    /**
     * Sends the capes that a player is allowed to put on.
     *
     * @param player the player to send to
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
     * Sends a packet to a specific player.
     *
     * @param player the player to send to
     * @param packet the packet to send
     */
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
            plugin.getLogger().severe(String.format("Exception while writing and sending %s packet to player %s", packet.getClass().getSimpleName(), player.getName()));
            e.printStackTrace();
        }
    }

    /**
     * Sends a packet to all in a world.
     *
     * @param world  the world to send to
     * @param packet the packet to send
     */
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
            plugin.getLogger().severe(String.format("Exception while writing and sending %s packet to world %s", packet.getClass().getSimpleName(), world.getName()));
            e.printStackTrace();
        }
    }

    /**
     * Gets the capes that a player is allowed to put on.
     */
    public List<String> getAvailableCapes(Player player)
    {
        // initialize list
        List<String> capes = new ArrayList<String>();

        // loop through all available capes.
        for (String cape : plugin.getAvailableCapes())
        // find if player has the permission node to use that cape.
        {
            if (player.hasPermission(RankCapesBukkit.CAPE_PERMISSION_BASE + cape))
            {
                capes.add(cape);
            }
        }

        return capes;
    }

    /**
     * Returns a list of the players that are being served.
     *
     * @return player list
     */
    public List<Player> getPlayersServing()
    {
        return this.playersServing;
    }

    /**
     * Removes player from serving list. Typically used when a player disconnects.
     */
    public void removeServingPlayer(Player player)
    {
        this.playersServing.remove(player);
    }
}

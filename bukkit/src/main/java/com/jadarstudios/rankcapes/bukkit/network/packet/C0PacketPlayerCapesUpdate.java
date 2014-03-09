/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.network.packet;

import com.jadarstudios.rankcapes.bukkit.database.PlayerCape;
import com.jadarstudios.rankcapes.bukkit.network.CapeUpdateType;
import org.json.simple.JSONValue;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is a packet that sends cape updates in bulk to clients.
 *
 * @author Jadar
 */
public class C0PacketPlayerCapesUpdate extends PacketClient
{
    public CapeUpdateType type;

    /**
     * Players that are updated.
     */
    public Map<String, String> playersMap;

    public C0PacketPlayerCapesUpdate(CapeUpdateType type)
    {
        this.type = type;
        this.playersMap = new HashMap<String, String>();
    }

    @Override
    public void write(ByteBuffer data) throws BufferOverflowException, ReadOnlyBufferException
    {
        data.put((byte) this.type.ordinal());

        String players = JSONValue.toJSONString(this.playersMap);
        writeString(players, data);
    }

    /**
     * Adds a player by {@link PlayerCape} database entry.
     *
     * @param cape the database entry of the player to add
     *
     * @return the resulting {@link C0PacketPlayerCapesUpdate} instance for instantiation convenience
     */
    public C0PacketPlayerCapesUpdate addPlayer(PlayerCape cape)
    {
        return this.addPlayer(cape.getPlayerName(), cape.getCapeName());
    }

    /**
     * Adds a player to be updated.
     *
     * @param cape the database entry of the player to add
     *
     * @return the resulting {@link C0PacketPlayerCapesUpdate} instance for instantiation convenience
     */
    public C0PacketPlayerCapesUpdate addPlayer(String player, String cape)
    {
        if (this.playersMap == null)
        {
            this.playersMap = new HashMap<String, String>();
        }

        this.playersMap.put(player, cape);

        return this;
    }

    /**
     * The amount of players to be updated.
     */
    public int getUpdateCount()
    {
        return this.playersMap != null ? this.playersMap.size() : 0;
    }

    @Override
    public int getSize()
    {
        return Byte.SIZE + JSONValue.toJSONString(this.playersMap).getBytes().length;
    }
}

/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.network.packet;

import com.jadarstudios.rankcapes.bukkit.RankCapesBukkit;
import com.jadarstudios.rankcapes.bukkit.database.PlayerCape;
import org.json.simple.JSONValue;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class C0PacketPlayerCapesUpdate extends PacketClient
{
    public Type type;
    public Map<String, String> playersMap;

    public C0PacketPlayerCapesUpdate(Type type)
    {
        this.type = type;
        this.playersMap = new HashMap<String, String>();
    }

    @Override
    public void write(ByteBuffer data)
    {
        try
        {
            data.put((byte) this.type.ordinal());

            String players = JSONValue.toJSONString(this.playersMap);
            writeString(players, data);
        }
        catch (Exception e)
        {
            RankCapesBukkit.log.severe("Exception while writing PacketCapeUpdate packet.");
            e.printStackTrace();
        }
    }

    public C0PacketPlayerCapesUpdate addPlayer(PlayerCape cape)
    {
        return this.addPlayer(cape.getPlayerName(), cape.getCapeName());
    }

    public C0PacketPlayerCapesUpdate addPlayer(String player, String cape)
    {
        if (this.playersMap == null)
        {
            this.playersMap = new HashMap<String, String>();
        }

        this.playersMap.put(player, cape);

        return this;
    }

    public int getUpdateNumber()
    {
        return this.playersMap != null ? this.playersMap.size() : 0;
    }

    @Override
    public int getSize()
    {
        return Byte.SIZE + JSONValue.toJSONString(this.playersMap).getBytes().length;
    }

    public static enum Type
    {
        UPDATE, REMOVE
    }
}

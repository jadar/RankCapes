/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.network.packet;

import com.google.gson.Gson;
import com.jadarstudios.rankcapes.forge.network.CapeUpdateType;
import io.netty.buffer.ByteBuf;

import java.util.Map;

/**
 * This class is a packet that receives cape updates in bulk from the server.
 *
 * @author Jadar
 */
public class C0PacketPlayerCapesUpdate extends PacketClient
{
    public CapeUpdateType type;

    /**
     * Players that are updated.
     */
    protected String players;

    @Override
    public void read(ByteBuf data) throws IndexOutOfBoundsException
    {
        byte typeByte = data.readByte();
        this.type = CapeUpdateType.values()[typeByte];

        this.players = this.readString(data);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getPlayers()
    {
        return new Gson().fromJson(this.players, Map.class);
    }
}

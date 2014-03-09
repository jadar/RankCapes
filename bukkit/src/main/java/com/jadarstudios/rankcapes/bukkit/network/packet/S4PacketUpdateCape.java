/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.network.packet;

import com.jadarstudios.rankcapes.bukkit.network.CapeUpdateType;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * This packet is sent to the server to tell it a player wants to change or remove his cape.
 *
 * @author Jadar
 */
public class S4PacketUpdateCape extends PacketServer
{
    public CapeUpdateType updateType;
    public String cape;

    // empty constructor for instantiation
    public S4PacketUpdateCape() {}

    @Override
    public void read(ByteBuffer data) throws BufferUnderflowException
    {
        byte typeOrdinal = data.get();
        this.updateType = CapeUpdateType.values()[typeOrdinal];

        this.cape = readString(data);
    }
}

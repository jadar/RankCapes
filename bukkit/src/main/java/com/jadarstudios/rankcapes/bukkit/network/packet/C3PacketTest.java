/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.network.packet;

import java.nio.ByteBuffer;

public class C3PacketTest extends PacketBase
{

    public String payload = "";

    @SuppressWarnings("unused")
    public C3PacketTest()
    {
    }

    public C3PacketTest(String payload)
    {
        this.payload = payload;
    }

    @Override
    public void write(ByteBuffer data)
    {
        writeString(this.payload, data);
    }

    @Override
    public void read(ByteBuffer data)
    {
        this.payload = readString(data);
    }

    @Override
    public int getSize()
    {
        byte[] stringBytes = this.payload.getBytes();
        return stringBytes.length + Integer.bitCount(stringBytes.length);
    }
}

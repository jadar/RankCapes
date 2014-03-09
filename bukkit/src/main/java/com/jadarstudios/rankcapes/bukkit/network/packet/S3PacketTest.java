/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.network.packet;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

/**
 * Test packet capable of being sent or received.
 */
public class S3PacketTest extends PacketBase
{

    public String payload = "";

    // for instantiation
    @SuppressWarnings("unused")
    public S3PacketTest()
    {
    }

    public S3PacketTest(String payload)
    {
        this.payload = payload;
    }

    @Override
    public void write(ByteBuffer data) throws BufferOverflowException, ReadOnlyBufferException
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

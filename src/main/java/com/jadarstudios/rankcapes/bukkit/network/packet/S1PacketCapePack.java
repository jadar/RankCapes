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
 * This class is a packet that sends a Cape Pack to a client.
 */
public class S1PacketCapePack extends PacketServer
{

    /**
     * The full pack size. (This class can be used to chunk the Cape Pack.)
     */
    public int packSize;
    public byte[] packBytes;

    public S1PacketCapePack(int packSize, byte[] packBytes)
    {
        this.packSize = packSize;
        this.packBytes = packBytes;
    }

    @Override
    public void write(ByteBuffer data) throws BufferOverflowException, ReadOnlyBufferException
    {
        data.putInt(this.packSize);
        data.putInt(this.packBytes.length);
        data.put(this.packBytes);
    }

    @Override
    public int getSize()
    {
        return this.packBytes.length + Integer.bitCount(this.packSize);
    }
}

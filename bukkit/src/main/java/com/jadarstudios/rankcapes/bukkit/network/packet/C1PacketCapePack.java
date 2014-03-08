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

public class C1PacketCapePack extends PacketClient
{

    public int packSize;
    public byte[] packBytes;

    public C1PacketCapePack(int packSize, byte[] packBytes)
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

/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.network.packet;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

public abstract class PacketBase
{

    /**
     * Writes the packet to the given byte buffer.
     *
     * @param data the bytebuffer to write read packet data to
     */
    public abstract void write(ByteBuffer data) throws Exception;

    /**
     * Reads the packet to the given byte buffer.
     *
     * @param data the bytebuffer to read the packet data to
     */
    public abstract void read(ByteBuffer data) throws Exception;

    /**
     * The potential size of the packet in bits.
     */
    public abstract int getSize();

    public static void writeString(String string, ByteBuffer data) throws BufferOverflowException, ReadOnlyBufferException
    {
        byte[] stringBytes = string.getBytes();
        data.putInt(stringBytes.length);
        data.put(stringBytes);
    }

    public static String readString(ByteBuffer data) throws BufferUnderflowException
    {
        int length = data.getInt();
        byte[] stringBytes = new byte[length];
        data.get(stringBytes);

        return new String(stringBytes);
    }
}

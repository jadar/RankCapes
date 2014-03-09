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

/**
 * The base class of all the packets. Pretty much the same as the client PacketBase class.
 *
 * @author Jadar
 */
public abstract class PacketBase
{

    /**
     * Writes the packet to the given {@link ByteBuffer}.
     *
     * @param data the {@link ByteBuffer} to write read packet data to
     */
    public abstract void write(ByteBuffer data) throws BufferOverflowException;

    /**
     * Reads the packet to the given {@link ByteBuffer}.
     *
     * @param data the {@link ByteBuffer} to read the packet data to
     */
    public abstract void read(ByteBuffer data) throws BufferUnderflowException;

    /**
     * The potential size of the packet in bits.
     */
    public abstract int getSize();

    /**
     * Write a string to a byte buffer.
     *
     * @param string the string to write
     * @param data the buffer to write to
     *
     * @throws BufferOverflowException thrown if the buffer is too small for the data
     * @throws ReadOnlyBufferException thrown if the buffer is read only
     */
    public static void writeString(String string, ByteBuffer data) throws BufferOverflowException
    {
        byte[] stringBytes = string.getBytes();
        data.putInt(stringBytes.length);
        data.put(stringBytes);
    }

    /**
     * Reads a string from a byte buffer.
     *
     * @param data the buffer to read from
     *
     * @throws BufferUnderflowException thrown if the buffer does not have enough bytes from which to read
     */
    public static String readString(ByteBuffer data) throws BufferUnderflowException
    {
        int length = data.getInt();
        byte[] stringBytes = new byte[length];
        data.get(stringBytes);

        return new String(stringBytes);
    }
}

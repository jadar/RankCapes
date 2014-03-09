/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.network.packet;

import io.netty.buffer.ByteBuf;

/**
 * The base class of all the packets. Pretty much the same as the server PacketBase class.
 *
 * @author Jadar
 */
public abstract class PacketBase
{

    public PacketBase()
    {
    }

    /**
     * Writes the packet to the given {@link ByteBuf}.
     *
     * @param data the {@link ByteBuf} to write read packet data to
     */
    public abstract void write(ByteBuf data) throws IndexOutOfBoundsException;

    /**
     * Reads the packet to the given {@link ByteBuf}.
     *
     * @param data the {@link ByteBuf} to read the packet data to
     */
    public abstract void read(ByteBuf data) throws IndexOutOfBoundsException;

    /**
     * Write a string to a {@link ByteBuf}
     *
     * @param string the string to write
     * @param data   the buffer to write to
     *
     * @throws IndexOutOfBoundsException thrown if the buffer is too small for the data
     */
    public void writeString(String string, ByteBuf data) throws IndexOutOfBoundsException
    {
        byte[] stringBytes = string.getBytes();
        data.writeInt(stringBytes.length);
        data.writeBytes(stringBytes);
    }

    /**
     * Reads a string from a {@link ByteBuf}.
     *
     * @param data the buffer to read from
     *
     * @throws IndexOutOfBoundsException thrown if the buffer does not have enough bytes from which to read
     */
    public String readString(ByteBuf data) throws IndexOutOfBoundsException
    {
        int length = data.readInt();
        byte[] stringBytes = new byte[length];
        data.readBytes(stringBytes);

        return new String(stringBytes);
    }
}

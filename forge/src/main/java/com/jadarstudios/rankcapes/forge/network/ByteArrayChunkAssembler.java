/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.network;

import com.jadarstudios.rankcapes.forge.RankCapesForge;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

/**
 * This is a util class for assembling byte array chunks.
 */
public class ByteArrayChunkAssembler
{
    /**
     * The fill size of the Cape Pack.
     */
    int fullSize;

    /**
     * The buffer for the cape pack
     */
    ByteBuffer buffer;

    public ByteArrayChunkAssembler(int fullSize)
    {
        this.fullSize = fullSize;
        this.buffer = ByteBuffer.allocate(fullSize);
    }

    /**
     * Appends a chunk onto the buffer.
     *
     * @param chunk the chunk to append
     *
     * @return if the chunk was successfully appended
     */
    public boolean addChunk(byte[] chunk) //C1PacketCapePack packet)
    {
        try
        {
            this.buffer.put(chunk);
        }
        catch (BufferOverflowException e)
        {
            RankCapesForge.log.error(String.format("%s buffer is full!", this.getClass().getSimpleName()));
            return false;
        }

        return true;
    }

    /**
     * Returns the complete byte array. If it is not full it returns null.
     */
    public byte[] getFullArray()
    {
        return this.buffer.position() == this.fullSize ? this.buffer.array() : null;
    }

}

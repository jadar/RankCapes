/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.network;

import com.jadarstudios.rankcapes.forge.RankCapesForge;
import com.jadarstudios.rankcapes.forge.network.packet.S1PacketCapePack;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class CapePackAssembler
{

    int fullSize;
    ByteBuffer partialPack;

    public CapePackAssembler(int fullSize)
    {
        this.fullSize = fullSize;
        this.partialPack = ByteBuffer.allocate(fullSize);
    }

    public boolean addChunk(S1PacketCapePack packet)
    {
        int chunkFullSize = packet.packSize;
        byte[] chunk = packet.packBytes;

        if (chunkFullSize != this.fullSize)
        {
            return false;
        }

        try
        {
            this.partialPack.put(chunk);
        }
        catch (BufferOverflowException e)
        {
            RankCapesForge.log.warn("CapePackAssembler buffer is full!");
            return false;
        }

        return true;
    }

    public byte[] getFullPack()
    {
        return this.partialPack.position() == this.fullSize ? this.partialPack.array() : null;
    }

}

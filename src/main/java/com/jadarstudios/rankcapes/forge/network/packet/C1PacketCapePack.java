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
 * This class is a packet that receives a Cape Pack from the server.
 */
public class C1PacketCapePack extends PacketClient
{

    /**
     * The full pack size. (This class can be used to chunk the Cape Pack.)
     */
    public int packSize;
    public byte[] packBytes;

    // for instantiation
    public C1PacketCapePack()
    {
    }

    @Override
    public void read(ByteBuf data) throws IndexOutOfBoundsException
    {
        this.packSize = data.readInt();
        int length = data.readInt();
        this.packBytes = data.readBytes(length).array();
    }

    public boolean isFullPack()
    {
        return this.packBytes.length == this.packSize;
    }
}

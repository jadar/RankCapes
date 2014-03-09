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
 * Test packet capable of being sent or received.
 */
public class C3PacketTest extends PacketBase
{

    public String payload = "";

    public C3PacketTest()
    {
    }

    public C3PacketTest(String payload)
    {
        this.payload = payload;
    }

    @Override
    public void write(ByteBuf data)
    {
        this.writeString(this.payload, data);
    }

    @Override
    public void read(ByteBuf data)
    {
        this.payload = this.readString(data);
    }
}

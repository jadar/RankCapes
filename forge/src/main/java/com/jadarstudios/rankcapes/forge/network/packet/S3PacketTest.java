/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.network.packet;

import io.netty.buffer.ByteBuf;

public class S3PacketTest extends PacketBase
{

    public String payload = "";

    public S3PacketTest()
    {
    }

    public S3PacketTest(String payload)
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

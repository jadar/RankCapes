package com.jadarstudios.rankcapes.forge.network;

import io.netty.buffer.ByteBuf;

public class PacketCapePack extends PacketBase
{
 
    public byte[] packBytes;

    public PacketCapePack(byte[] packBytes)
    {
        this.packBytes = packBytes;
    }
    
    @Override
    public void write(ByteBuf data)
    {
        data.writeInt(packBytes.length);
        data.writeBytes(packBytes);
    }

    @Override
    public void read(ByteBuf data)
    {
        int length = data.readInt();
        packBytes = data.readBytes(length).array();
    }
}

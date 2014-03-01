package com.jadarstudios.rankcapes.bukkit.network.packet;

import io.netty.buffer.ByteBuf;

public class S1PacketCapePack extends PacketServer
{
 
    public byte[] packBytes;

    public S1PacketCapePack() {}
    
    public S1PacketCapePack(byte[] packBytes)
    {
        this.packBytes = packBytes;
    }

    @Override
    public void read(ByteBuf data)
    {
        int length = data.readInt();
        packBytes = data.readBytes(length).array();
    }
}

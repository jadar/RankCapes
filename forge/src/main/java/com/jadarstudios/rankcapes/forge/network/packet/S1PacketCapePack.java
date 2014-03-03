package com.jadarstudios.rankcapes.forge.network.packet;

import io.netty.buffer.ByteBuf;

public class S1PacketCapePack extends PacketServer
{
    
    public int packSize;
    public byte[] packBytes;
    
    public S1PacketCapePack()
    {
    }
    
    public S1PacketCapePack(int packSize, byte[] packBytes)
    {
        this.packSize = packSize;
        this.packBytes = packBytes;
    }
    
    @Override
    public void read(ByteBuf data)
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

package com.jadarstudios.rankcapes.bukkit.network.packet;

import java.nio.ByteBuffer;

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
    public void write(ByteBuffer data)
    {
        data.putInt(this.packSize);
        data.putInt(this.packBytes.length);
        data.put(this.packBytes);
    }
    
    @Override
    public int getSize()
    {
        return this.packBytes.length + Integer.bitCount(this.packSize);
    }
}

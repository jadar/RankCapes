package com.jadarstudios.rankcapes.bukkit.network.packet;

import java.nio.ByteBuffer;

public class C1PacketCapePack extends PacketClient
{
    
    public int packSize;
    public byte[] packBytes;
    
    public C1PacketCapePack()
    {
    }
    
    public C1PacketCapePack(int packSize, byte[] packBytes)
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

package com.jadarstudios.rankcapes.bukkit.network.packet;

import java.nio.ByteBuffer;

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
    public void write(ByteBuffer data)
    {
        writeString(this.payload, data);
    }
    
    @Override
    public void read(ByteBuffer data)
    {
        this.payload = readString(data);
    }
    
    @Override
    public int getSize()
    {
        byte[] stringBytes = this.payload.getBytes();
        return stringBytes.length + Integer.bitCount(stringBytes.length);
    }
}

package com.jadarstudios.rankcapes.forge.network;

import io.netty.buffer.ByteBuf;

public class PacketServerCommand extends PacketBase
{
 
    public byte command;
    public String payload = "";
    
    public PacketServerCommand(byte command, String payload)
    {
        this(command);
        this.payload = payload;
    }
    
    public PacketServerCommand(byte command)
    {
        this.command = command;
    }
    
    @Override
    public void write(ByteBuf data)
    {
        data.writeByte(command);
        
        byte[] payloadBytes = payload.getBytes();
        
        data.writeInt(payloadBytes.length);
        data.writeBytes(payloadBytes);
    }
    @Override
    public void read(ByteBuf data)
    {
        command = data.readByte();
        int length = data.readInt();
        payload = new String(data.readBytes(length).array());
    }
}

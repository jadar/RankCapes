package com.jadarstudios.rankcapes.bukkit.network.packet;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class S2PacketAvailableCapes extends PacketServer
{
    
    protected String capes;
    
    @Override
    public void read(ByteBuf data)
    {
        capes = readString(data);
    }
    
    public List<String> getCapes()
    {
        return (new Gson()).fromJson(this.capes, ArrayList.class); 
    }
    
}

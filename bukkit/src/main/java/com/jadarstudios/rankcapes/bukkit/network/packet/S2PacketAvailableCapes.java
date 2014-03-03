package com.jadarstudios.rankcapes.bukkit.network.packet;

import java.nio.ByteBuffer;
import java.util.List;

import org.json.simple.JSONArray;

public class S2PacketAvailableCapes extends PacketServer
{
    
    protected String capes;
    
    public S2PacketAvailableCapes(List<String> capes)
    {
        this.setCapes(capes);
    }
    
    @Override
    public void write(ByteBuffer data)
    {
        writeString(this.capes, data);
    }
    
    public void setCapes(List<String> capes)
    {
        this.capes = JSONArray.toJSONString(capes);
    }
    
    @Override
    public int getSize()
    {
        return this.capes.getBytes().length;
    }
    
}

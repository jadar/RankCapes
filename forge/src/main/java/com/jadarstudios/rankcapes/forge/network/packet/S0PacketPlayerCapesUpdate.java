package com.jadarstudios.rankcapes.forge.network.packet;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.jadarstudios.rankcapes.forge.RankCapesForge;

public class S0PacketPlayerCapesUpdate extends PacketServer
{
    public Type type;
    protected String players;
    
    @Override
    public void read(ByteBuf data)
    {
        try
        {
            byte typeByte = data.readByte();
            this.type = Type.values()[typeByte];
            
            this.players = readString(data);
        }
        catch(Exception e)
        {
            RankCapesForge.log.error("Exception while reading PacketCapeUpdate packet.");
            e.printStackTrace();
        }
    }
    
    public List<String> getPlayers()
    {
        return (new Gson()).fromJson(this.players, ArrayList.class);  
    }
    
    public static enum Type
    {
        UPDATE,
        REMOVE;
    }
    
}

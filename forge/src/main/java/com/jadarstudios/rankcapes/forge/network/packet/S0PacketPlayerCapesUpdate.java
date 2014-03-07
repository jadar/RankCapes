package com.jadarstudios.rankcapes.forge.network.packet;

import com.google.gson.Gson;
import com.jadarstudios.rankcapes.forge.RankCapesForge;
import io.netty.buffer.ByteBuf;

import java.util.Map;

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

            this.players = this.readString(data);
        }
        catch (Exception e)
        {
            RankCapesForge.log.error("Exception while reading PacketCapeUpdate packet.");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getPlayers()
    {
        return new Gson().fromJson(this.players, Map.class);
    }

    public static enum Type
    {
        UPDATE, REMOVE
    }

}

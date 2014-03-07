package com.jadarstudios.rankcapes.forge.network.packet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jadarstudios.rankcapes.forge.RankCapesForge;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class S2PacketAvailableCapes extends PacketServer
{

    protected String capes;

    @Override
    public void read(ByteBuf data)
    {
        this.capes = this.readString(data);
    }

    @SuppressWarnings("unchecked")
    public List<String> getCapes()
    {
        List<String> capes = null;

        try
        {
            capes = new Gson().fromJson(this.capes, ArrayList.class);
        }
        catch (JsonSyntaxException e)
        {
            RankCapesForge.log.error(String.format("Error while parsing JSON for packet %s", this.getClass().getSimpleName()));
            RankCapesForge.log.error(this.capes);
            e.printStackTrace();
        }

        return capes;
    }

}

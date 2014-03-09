/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.network.packet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jadarstudios.rankcapes.forge.RankCapesForge;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a packet that receives the capes that are available to a client.
 *
 * @author Jadar
 */
public class C2PacketAvailableCapes extends PacketClient
{

    /**
     * The capes formatted in JSON.
     */
    protected String capes;

    @Override
    public void read(ByteBuf data)
    {
        this.capes = this.readString(data);
    }

    @SuppressWarnings("unchecked")
    public List<String> getCapes() throws IndexOutOfBoundsException
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

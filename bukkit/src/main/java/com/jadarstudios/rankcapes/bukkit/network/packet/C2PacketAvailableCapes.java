/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.network.packet;

import org.json.simple.JSONArray;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * This class is a packet that sends the capes that are available to a client.
 *
 * @author Jadar
 */
public class C2PacketAvailableCapes extends PacketClient
{

    /**
     * The capes formatted in JSON.
     */
    protected String capes;

    public C2PacketAvailableCapes(List<String> capes)
    {
        this.setCapes(capes);
    }

    @Override
    public void write(ByteBuffer data) throws BufferOverflowException
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

/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.network.packet;

import java.nio.ByteBuffer;

/**
 * This packet is sent to the server to tell it
 * the client wants to change its cape.
 *
 * @author Jadar
 */
public class S4PacketUpdateCape extends PacketServer
{
    public Type updateType;
    public String cape;

    public S4PacketUpdateCape()
    {
    }

    /**
     * Creates the packet with {@link Type#UPDATE} as the update type.
     *
     * @param cape
     */
    public S4PacketUpdateCape(String cape)
    {
        this(Type.UPDATE, cape);
    }

    public S4PacketUpdateCape(Type updateType, String cape)
    {
        this.updateType = updateType;
        this.cape = cape;
    }

    @Override
    public void read(ByteBuffer data)
    {
        byte typeOrdinal = data.get();
        this.updateType = Type.values()[typeOrdinal];

        this.cape = readString(data);
    }

    public static enum Type
    {
        UPDATE, REMOVE;
    }

}

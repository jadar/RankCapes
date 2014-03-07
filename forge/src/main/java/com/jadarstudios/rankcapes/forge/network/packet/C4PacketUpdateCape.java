/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.network.packet;

import io.netty.buffer.ByteBuf;

/**
 * This packet is sent to the server to tell it
 * the client wants to change its cape.
 *
 * @author Jadar
 */
public class C4PacketUpdateCape extends PacketClient
{
    public Type updateType;
    public String cape = "";

    /**
     * Creates the packet with {@link Type#UPDATE} as the update type.
     *
     * @param cape
     */
    public C4PacketUpdateCape(String cape)
    {
        this(Type.UPDATE);
        this.cape = cape;
    }

    public C4PacketUpdateCape(Type updateType)
    {
        this.updateType = updateType;
    }

    @Override
    public void write(ByteBuf data)
    {
        data.writeByte(this.updateType.ordinal());
        this.writeString(this.cape, data);
    }

    public static enum Type
    {
        UPDATE, REMOVE
    }

}

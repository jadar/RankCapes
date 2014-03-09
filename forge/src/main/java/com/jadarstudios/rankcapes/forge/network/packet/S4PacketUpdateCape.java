/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.network.packet;

import com.jadarstudios.rankcapes.forge.network.CapeUpdateType;
import io.netty.buffer.ByteBuf;

/**
 * This packet is sent to the server to tell it a player wants to change or remove his cape.
 *
 * @author Jadar
 */
public class S4PacketUpdateCape extends PacketServer
{
    public CapeUpdateType updateType;
    public String cape = "";

    /**
     * Creates the packet with {@link CapeUpdateType#UPDATE} as the update type.
     *
     * @param cape
     */
    public S4PacketUpdateCape(String cape)
    {
        this(CapeUpdateType.UPDATE);
        this.cape = cape;
    }

    public S4PacketUpdateCape(CapeUpdateType updateType)
    {
        this.updateType = updateType;
    }

    @Override
    public void write(ByteBuf data) throws IndexOutOfBoundsException
    {
        data.writeByte(this.updateType.ordinal());
        this.writeString(this.cape, data);
    }
}

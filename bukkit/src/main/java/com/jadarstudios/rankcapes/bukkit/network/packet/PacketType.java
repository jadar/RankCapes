/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.network.packet;

/**
 * The packet types. The enum ordinal is the discriminator so the order is the same on the client and server.
 *
 * @author Jadar
 */
public enum PacketType
{
    // server packets
    PLAYER_CAPES_UPDATE(S0PacketPlayerCapesUpdate.class),
    CAPE_PACK(S1PacketCapePack.class),
    AVAILABLE_CAPES(S2PacketAvailableCapes.class),
    TEST_PACKET(S3PacketTest.class),

    // client packets
    UPDATE_CAPE(C4PacketUpdateCape.class);

    private Class<? extends PacketBase> packetClass;

    private PacketType(Class<? extends PacketBase> packetClass)
    {
        this.packetClass = packetClass;
    }

    public Class<? extends PacketBase> getPacketClass()
    {
        return this.packetClass;
    }

}

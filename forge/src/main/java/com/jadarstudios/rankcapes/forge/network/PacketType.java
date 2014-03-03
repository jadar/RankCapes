package com.jadarstudios.rankcapes.forge.network;

import com.jadarstudios.rankcapes.forge.network.packet.C4PacketUpdateCape;
import com.jadarstudios.rankcapes.forge.network.packet.PacketBase;
import com.jadarstudios.rankcapes.forge.network.packet.S0PacketPlayerCapesUpdate;
import com.jadarstudios.rankcapes.forge.network.packet.S1PacketCapePack;
import com.jadarstudios.rankcapes.forge.network.packet.S2PacketAvailableCapes;
import com.jadarstudios.rankcapes.forge.network.packet.S3PacketTest;

public enum PacketType
{
    // server packets
    PLAYER_CAPES_UPDATE(S0PacketPlayerCapesUpdate.class), CAPE_PACK(S1PacketCapePack.class), AVAILABLE_CAPES(S2PacketAvailableCapes.class), TEST_PACKET(S3PacketTest.class),
    
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

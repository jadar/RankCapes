package com.jadarstudios.rankcapes.bukkit.network.packet;

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

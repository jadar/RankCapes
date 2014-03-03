package com.jadarstudios.rankcapes.bukkit.network.packet;

public enum PacketType
{
    // server packets
    PLAYER_CAPES_UPDATE(C0PacketPlayerCapesUpdate.class), 
    CAPE_PACK(C1PacketCapePack.class), 
    AVAILABLE_CAPES(C2PacketAvailableCapes.class), 
    TEST_PACKET(C3PacketTest.class),
    
    // client packets
    UPDATE_CAPE(S4PacketUpdateCape.class);
    
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

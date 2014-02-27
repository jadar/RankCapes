
package com.jadarstudios.rankcapes.forge.network;

public enum PacketType
{
    SERVER_COMMAND(PacketServerCommand.class), 
    CAPE_PACK(PacketCapePack.class);
    
    public final Class<? extends PacketBase> packetClass;
    
    private PacketType(Class<? extends PacketBase> packetClass)
    {
        this.packetClass = packetClass;
    }
}

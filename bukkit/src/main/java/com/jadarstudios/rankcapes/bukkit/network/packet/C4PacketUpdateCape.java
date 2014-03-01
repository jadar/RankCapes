package com.jadarstudios.rankcapes.bukkit.network.packet;

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
    public String cape;
    
    /**
     * Creates the packet with {@link Type#UPDATE} as the update type.
     * @param cape
     */
    public C4PacketUpdateCape(String cape)
    {
        this(Type.UPDATE, cape);
    }
    
    public C4PacketUpdateCape(Type updateType, String cape)
    {
        this.updateType = updateType;
        this.cape = cape;
    }
    
    @Override
    public void write(ByteBuf data)
    {
        writeString(cape, data);
    }
    
    public static enum Type
    {
        UPDATE,
        REMOVE;
    }
    
}

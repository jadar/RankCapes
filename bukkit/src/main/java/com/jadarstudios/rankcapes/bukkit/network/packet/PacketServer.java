package com.jadarstudios.rankcapes.bukkit.network.packet;

import java.nio.ByteBuffer;

/**
 * Abstract class for packets coming from the server.
 * 
 * @author Jadar
 */
public abstract class PacketServer extends PacketBase
{
    
    // "implemets" this so that child classes dont need to.
    @Override
    public final void read(ByteBuffer data)
    {
    }
}

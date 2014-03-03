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
    public final void write(ByteBuffer data)
    {
    }
    
    // "implement" this so child classes don't have to.
    @Override
    public final int getSize()
    {
        return 0;
    }
}

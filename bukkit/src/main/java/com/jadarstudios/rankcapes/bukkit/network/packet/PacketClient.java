package com.jadarstudios.rankcapes.bukkit.network.packet;

import java.nio.ByteBuffer;

/**
 * From client to server;
 * 
 * @author Jadar
 */
public abstract class PacketClient extends PacketBase
{
    
    // "implement" this so child classes don't have to.
    @Override
    public final void read(ByteBuffer data)
    {
    }
}

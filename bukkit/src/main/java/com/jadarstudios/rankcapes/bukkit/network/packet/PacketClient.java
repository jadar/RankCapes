package com.jadarstudios.rankcapes.bukkit.network.packet;

import io.netty.buffer.ByteBuf;

public abstract class PacketClient extends PacketBase
{
    
    // "implement" this so child classes don't have to.
    @Override
    public final void read(ByteBuf data) {}
    
}

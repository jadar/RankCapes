package com.jadarstudios.rankcapes.bukkit.network.packet;

import java.nio.ByteBuffer;

public interface IPacket
{
    public void write(ByteBuffer data) throws Exception;
    public void read(ByteBuffer data) throws Exception;
    public int getSize();
}

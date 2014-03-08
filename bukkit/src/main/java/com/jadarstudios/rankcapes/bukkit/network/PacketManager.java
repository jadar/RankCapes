/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.network;

import com.jadarstudios.rankcapes.bukkit.network.packet.PacketBase;
import com.jadarstudios.rankcapes.bukkit.network.packet.PacketType;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.HashMap;

public enum PacketManager
{
    INSTANCE;

    private HashMap<Class<? extends PacketBase>, Byte> discriminators;
    private HashMap<Byte, Class<? extends PacketBase>> classes;

    private PacketManager()
    {
        this.discriminators = new HashMap<Class<? extends PacketBase>, Byte>();
        this.classes = new HashMap<Byte, Class<? extends PacketBase>>();

        this.initDiscriminators();
    }

    private void initDiscriminators()
    {
        for (PacketType type : PacketType.values())
        {
            this.addDiscriminator((byte) type.ordinal(), type.getPacketClass());
        }
    }

    public void addDiscriminator(byte discriminator, Class<? extends PacketBase> clazz)
    {
        if (!this.discriminators.containsKey(clazz) && !this.classes.containsKey(discriminator))
        {
            this.discriminators.put(clazz, discriminator);
            this.classes.put(discriminator, clazz);
        }
    }

    public byte getDiscriminator(Class<? extends PacketBase> clazz)
    {
        return this.discriminators.get(clazz);
    }

    public Class<? extends PacketBase> getDiscriminatorClass(byte discriminator)
    {
        return this.classes.get(discriminator);
    }

    public PacketBase getPacketFromBytes(byte[] bytes) throws BufferUnderflowException, InstantiationException, IllegalAccessException
    {
        ByteBuffer data = ByteBuffer.wrap(bytes);
        byte discriminator = data.get();

        Class<? extends PacketBase> packetClass = this.getDiscriminatorClass(discriminator);
        PacketBase packet = packetClass.newInstance();

        packet.read(data);

        return packet;
    }

    public byte[] getBytesFromPacket(PacketBase packet) throws BufferOverflowException, ReadOnlyBufferException
    {
        byte discriminator = this.getDiscriminator(packet.getClass());

        ByteBuffer buffer = ByteBuffer.allocate(packet.getSize() + Byte.SIZE);

        buffer.put(discriminator);
        packet.write(buffer);

        return buffer.array();
    }
}

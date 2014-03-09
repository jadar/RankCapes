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
import java.util.HashMap;

/**
 * This class turns {@link PacketBase} instances into bytes and bytes into {@link PacketBase} instances.
 *
 * @author Jadar
 */
public enum PacketManager
{
    INSTANCE;

    // maps packet classes to discriminator bytes
    private HashMap<Class<? extends PacketBase>, Byte> discriminators;

    // maps discriminator bytes to classes
    private HashMap<Byte, Class<? extends PacketBase>> classes;

    private PacketManager()
    {
        this.discriminators = new HashMap<Class<? extends PacketBase>, Byte>();
        this.classes = new HashMap<Byte, Class<? extends PacketBase>>();

        this.initDiscriminators();
    }

    /**
     * Initializes the packet discriminators.
     */
    private void initDiscriminators()
    {
        for (PacketType type : PacketType.values())
        {
            this.addDiscriminator((byte) type.ordinal(), type.getPacketClass());
        }
    }

    /**
     * Adds a discriminator to the maps.
     *
     * @param discriminator the discriminator to add
     * @param clazz         the class to add
     */
    public void addDiscriminator(byte discriminator, Class<? extends PacketBase> clazz)
    {
        if (!this.discriminators.containsKey(clazz) && !this.classes.containsKey(discriminator))
        {
            this.discriminators.put(clazz, discriminator);
            this.classes.put(discriminator, clazz);
        }
    }

    /**
     * Gets the discriminator of a class.
     *
     * @param clazz the class of whose discriminator to get
     *
     * @return the discriminator of the given class
     */
    public byte getDiscriminator(Class<? extends PacketBase> clazz)
    {
        return this.discriminators.get(clazz);
    }

    /**
     * Gets the class of the given discriminator
     *
     * @param discriminator the discriminator
     *
     * @return the class of the discriminator
     */
    public Class<? extends PacketBase> getDiscriminatorClass(byte discriminator)
    {
        return this.classes.get(discriminator);
    }

    /**
     * Creates a packet instance and fills it from the given bytes.
     *
     * @param bytes the bytes of the data
     *
     * @return an instance of a child class of {@link PacketBase}
     *
     * @throws BufferUnderflowException thrown if there is not enough data to fill the packet
     * @throws InstantiationException   thrown if the packet class can not be instantiated
     * @throws IllegalAccessException   thrown if the packet class' constructor cannot be called
     */
    public PacketBase getPacketFromBytes(byte[] bytes) throws BufferUnderflowException, InstantiationException, IllegalAccessException
    {
        ByteBuffer data = ByteBuffer.wrap(bytes).asReadOnlyBuffer();
        byte discriminator = data.get();

        Class<? extends PacketBase> packetClass = this.getDiscriminatorClass(discriminator);
        PacketBase packet = packetClass.newInstance();

        packet.read(data);

        return packet;
    }

    /**
     * Writes the given {@link PacketBase} instance to a byte array and returns it.
     *
     * @param packet the packet to write
     *
     * @return the bytes of the packet
     *
     * @throws BufferOverflowException thrown if the buffer is not large enough
     */
    public byte[] getBytesFromPacket(PacketBase packet) throws BufferOverflowException
    {
        byte discriminator = this.getDiscriminator(packet.getClass());

        ByteBuffer buffer = ByteBuffer.allocate(packet.getSize() + Byte.SIZE);

        buffer.put(discriminator);
        packet.write(buffer);

        return buffer.array();
    }
}

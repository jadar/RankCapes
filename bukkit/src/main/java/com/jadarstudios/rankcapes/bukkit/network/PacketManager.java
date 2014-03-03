package com.jadarstudios.rankcapes.bukkit.network;

import java.nio.ByteBuffer;
import java.util.HashMap;

import com.jadarstudios.rankcapes.bukkit.network.packet.PacketBase;
import com.jadarstudios.rankcapes.bukkit.network.packet.PacketType;

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
            this.addDiscriminator((byte) type.ordinal(), type.getPacketClass());
    }
    
    public void addDiscriminator(byte discriminator, Class<? extends PacketBase> clazz)
    {
        if (!this.discriminators.containsKey(this.discriminators) && !this.classes.containsKey(this.discriminators))
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
    
    public PacketBase getPacketFromBytes(byte[] bytes) throws Exception
    {
        ByteBuffer data = ByteBuffer.wrap(bytes);
        byte discriminator = data.get();
        
        Class<? extends PacketBase> packetClass = this.getDiscriminatorClass(discriminator);
        PacketBase packet = packetClass.newInstance();
        
        packet.read(data);
        
        return packet;
    }
    
    public byte[] getBytesFromPacket(PacketBase packet) throws Exception
    {
        byte discriminator = this.getDiscriminator(packet.getClass());
        
        ByteBuffer buffer = ByteBuffer.allocate(packet.getSize() + Byte.SIZE);
        
        // RankCapesBukkit.log.info(String.format("Packet %s of size %s",
        // packet.getClass().getSimpleName(), buffer.limit()));
        
        buffer.put(discriminator);
        packet.write(buffer);
        
        return buffer.array();
    }
}

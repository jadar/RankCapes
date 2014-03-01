package com.jadarstudios.rankcapes.bukkit.network;

import java.nio.ByteBuffer;
import java.util.HashMap;

import com.jadarstudios.rankcapes.bukkit.network.packet.IPacket;
import com.jadarstudios.rankcapes.bukkit.network.packet.PacketType;

public class PacketManager
{
    private static PacketManager INSTANCE;
    
    private HashMap<Class<? extends IPacket>, Byte> discriminators;
    private HashMap<Byte, Class<? extends IPacket>> classes;
    
    public PacketManager()
    {
        discriminators = new HashMap<Class<? extends IPacket>, Byte>();
        classes = new HashMap<Byte, Class<? extends IPacket>>();

        initDiscriminators();
    }

    public static PacketManager instance()
    {
        if(INSTANCE == null)
        {
            INSTANCE = new PacketManager();
        }
        
        return INSTANCE;
    }
    
    private void initDiscriminators()
    {
        for(PacketType type : PacketType.values())
        {
            addDiscriminator((byte)type.ordinal(), type.getPacketClass());
        }
    }
    
    public void addDiscriminator(byte discriminator, Class<? extends IPacket> clazz)
    {
        if(!discriminators.containsKey(discriminators) && !classes.containsKey(discriminators))
        {
            discriminators.put(clazz, discriminator);
            classes.put(discriminator, clazz);
        }
    }
    
    public byte getDiscriminator(Class<? extends IPacket> clazz)
    {
        return discriminators.get(clazz);
    }
    
    public Class<? extends IPacket> getDiscriminatorClass(byte discriminator)
    {
        return classes.get(discriminator);
    }
    
    public IPacket getPacketFromBytes(byte[] bytes) throws Exception 
    {
        ByteBuffer data = ByteBuffer.wrap(bytes);
        byte discriminator = data.get();
        
        Class<? extends IPacket> packetClass = this.getDiscriminatorClass(discriminator);
        IPacket packet = packetClass.newInstance();
        
        packet.read(data);
        
        return packet;
    }
    
    public byte[] getBytesFromPacket(IPacket packet) throws Exception
    {
        ByteBuffer buffer = ByteBuffer.allocate(packet.getSize());
        buffer.put(getDiscriminator(packet.getClass()));
        packet.write(buffer);
        
        return buffer.array();
    }
}

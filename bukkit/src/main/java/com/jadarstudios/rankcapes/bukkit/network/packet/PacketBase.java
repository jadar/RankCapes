package com.jadarstudios.rankcapes.bukkit.network.packet;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

public abstract class PacketBase implements IPacket
{

    /**
     * Writes the packet to the given byte buffer.
     * Strongly recommended to call super.write() as it 
     * encodes the packet discriminator to it.
     * @param data
     */
	public abstract void write(ByteBuffer data) throws Exception;
	public abstract void read(ByteBuffer data) throws Exception;
	
	/**
	 * The potential size of the packet in bits.
	 */
	public abstract int getSize();
	
	public static void putString(String string, ByteBuffer data) throws BufferOverflowException, ReadOnlyBufferException
	{
        byte[] stringBytes = string.getBytes();
        data.putInt(stringBytes.length);
        data.put(stringBytes);
    }
    
    public static String getString(ByteBuffer data) throws BufferUnderflowException
    {
        int length = data.getInt();
        byte[] stringBytes = new byte[length];
        data.get(stringBytes);
        
        return new String(stringBytes);
    } 
}

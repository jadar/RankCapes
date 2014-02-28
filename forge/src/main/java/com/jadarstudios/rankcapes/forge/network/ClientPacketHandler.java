/**
 * RankCapes Forge Mod
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.EnumMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;

import com.jadarstudios.rankcapes.forge.ModProperties;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.FMLOutboundHandler.OutboundTarget;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ClientPacketHandler
{
    private static ClientPacketHandler INSTANCE = new ClientPacketHandler();
    
    private EnumMap<Side, FMLEmbeddedChannel> channels;
    
    private ClientPacketHandler()
    {
        this.channels = NetworkRegistry.INSTANCE.newChannel(ModProperties.MOD_ID, new ChannelCodec());
        
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
            this.addClientHandler();
    }
    
    public static ClientPacketHandler getInstance()
    {
        return INSTANCE;
        
    }
    
    @SideOnly(Side.CLIENT)
    private void addClientHandler()
    {
        FMLEmbeddedChannel clientChannel = this.channels.get(Side.CLIENT);
        
        String codec = clientChannel.findChannelHandlerNameForType(ChannelCodec.class);
        clientChannel.pipeline().addAfter(codec, ModProperties.MOD_ID, new ChannelHandler());
    }
    
    /**
     * Wrapper method for {@link FMLEmbeddedChannel#generatePacketFrom(Object)}.
     * Must have a codec in place to transform it for it to return anything.
     * 
     * @param msg
     *            object to generate from
     * @param side
     *            channel to side being sent to
     * @return created packet
     */
    public Packet generatePacketFrom(PacketBase msg, Side side)
    {
        return this.channels.get(side).generatePacketFrom(msg);
    }
    
    @SideOnly(Side.CLIENT)
    public void sendPacketToServer(Packet packet)
    {
        this.sendPacketToTarget(packet, FMLOutboundHandler.OutboundTarget.TOSERVER);
    }
    
    public void sendPacketToPlayer(Packet packet, EntityPlayer player)
    {
        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        this.sendPacketToTarget(packet, FMLOutboundHandler.OutboundTarget.PLAYER);
    }
    
    public void sendPacketToAllPlayers(Packet packet, EntityPlayer player)
    {
        this.sendPacketToTarget(packet, FMLOutboundHandler.OutboundTarget.ALL);
    }
    
    protected void sendPacketToTarget(Packet packet, OutboundTarget target)
    {
        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(target);
        this.channels.get(Side.SERVER).writeOutbound(packet);
    }
    
    private static class ChannelCodec extends FMLIndexedMessageToMessageCodec<PacketBase>
    {
        
        public ChannelCodec()
        {
            for (PacketType type : PacketType.values())
                this.addDiscriminator(type.ordinal(), type.packetClass);
        }
        
        @Override
        public void encodeInto(ChannelHandlerContext ctx, PacketBase msg, ByteBuf target) throws Exception
        {
            msg.write(target);
        }
        
        @Override
        public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, PacketBase msg)
        {
            msg.read(source);
            
        }
    }
    
    @SideOnly(Side.CLIENT)
    private static class ChannelHandler extends SimpleChannelInboundHandler<FMLProxyPacket>
    {
        
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FMLProxyPacket packet) throws Exception
        {
            System.out.println("Got packet!");
        }
    }

    /*
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        String data = readByteArray(packet.data);
        
        if (debug)
        {
            RankCapesForge.log.info("Recieved Packet! Data: " + data);
        }
        
        if (!data.contains(":"))
            return;
        
        // should have 2 entries, command:args
        String[] command = data.split(":");
        
        // return if the command does not have args, or if the args are empty or
        // null.
        if (command.length < 2 || Strings.isNullOrEmpty(command[1]))
            return;
        
        if (command[0].equals("transmitPort"))
        {
            if (StringUtils.isNumeric(command[1]))
            {
                RankCapesForge.instance.connectReadThread(Integer.parseInt(command[1]));
            }
        }
        else if (command[0].equals("playerCapeUpdate"))
        {
            handlePlayerCapeUpdate(command[1]);
        }
        else if (command[0].equals("allPlayerCapes"))
        {
            HashMap<String, String> t = parsePlayerCapes(command[1]);
            RankCapesForge.instance.getCapeHandler().playerCapeNames = t;
            RankCapesForge.instance.getCapeHandler().capeChangeQue.addAll(t.keySet());
        }
        else if (command[0].equals("availableCapes"))
        {
            List<String> t = Arrays.asList(command[1].split(","));
            RankCapesForge.instance.availableCapes = t;
        }
        else if (command[0].equals("removeCapeUpdate"))
        {
            RankCapesForge.instance.getCapeHandler().playerCapeNames.remove(command[1]);
            RankCapesForge.instance.getCapeHandler().capeChangeQue.add(command[1]);
        }
        
    }
    /*
    /**
     * Handles single player cape update.
     * 
     * @param args
     *            from received command.
     */
/*    private void handlePlayerCapeUpdate(String args)
    {
        String[] t = args.split(",");
        if (t.length != 2)
            return;
        
        RankCapesForge.instance.getCapeHandler().playerCapeNames.put(t[0], t[1]);
        RankCapesForge.instance.getCapeHandler().capeChangeQue.add(t[0]);
    }
    */
    /**
     * Parses a String to a HashMap of the player capes.
     * 
     * @param data
     *            string of serialized hashmap
     * @return deserialized hashmap
     *//*
    public HashMap<String, String> parsePlayerCapes(String data)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        
        String[] splitData = data.split(Pattern.quote("|"));
        
        for (String playerData : splitData)
        {
            String[] splitPlayerData = playerData.split(",");
            map.put(splitPlayerData[0], splitPlayerData[1]);
        }
        
        return map;
    }
    
    /**
     * Requests the Cape Pack.
     *//*
    public void sendRequestPacket()
    {
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "RankCapes";
        packet.data = "REQUEST-PACK".getBytes();
        packet.length = packet.data.length;
        
        PacketDispatcher.sendPacketToServer(packet);
    }*/
    
    /**
     * Sends packet to change the cape.
     * 
     * @param capeName
     *            name of cape to change to.
     *//*
    public void sendCapeChangePacket(String capeName)
    {
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "RankCapes";
        packet.data = ("changeCape:" + capeName).getBytes();
        packet.length = packet.data.length;
        
        PacketDispatcher.sendPacketToServer(packet);
    }*/
    
    /**
     * Sends packet to remove cape.
     *//*
    public void sendCapeRemovePacket()
    {
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "RankCapes";
        packet.data = ("removeCape").getBytes();
        packet.length = packet.data.length;
        
        PacketDispatcher.sendPacketToServer(packet);
    }*/
    
    /**
     * Reads a byte aray to a String.
     * 
     * @param data
     *            byte array to read
     * @return string from bytes
     *//*
    private static String readByteArray(byte[] data)
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
            String rtrn = "";
            while (br.ready())
            {
                rtrn += br.readLine();
            }
            return rtrn;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }*/
}

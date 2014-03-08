/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.network;

import com.jadarstudios.rankcapes.forge.ModProperties;
import com.jadarstudios.rankcapes.forge.RankCapesForge;
import com.jadarstudios.rankcapes.forge.cape.AbstractCape;
import com.jadarstudios.rankcapes.forge.cape.CapePack;
import com.jadarstudios.rankcapes.forge.handler.CapeHandler;
import com.jadarstudios.rankcapes.forge.network.packet.*;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.network.Packet;
import net.minecraft.util.StringUtils;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

@SideOnly(Side.CLIENT)
public enum ClientPacketHandler
{
    INSTANCE;

    private static boolean debug = false;

    private EnumMap<Side, FMLEmbeddedChannel> channels;
    private CapePackAssembler packAssembler;

    private ClientPacketHandler()
    {
        this.channels = NetworkRegistry.INSTANCE.newChannel(ModProperties.NETWORK_CHANNEL, new ChannelCodec());

        FMLEmbeddedChannel clientChannel = this.channels.get(Side.CLIENT);
        String codec = clientChannel.findChannelHandlerNameForType(ChannelCodec.class);
        clientChannel.pipeline().addAfter(codec, ModProperties.NETWORK_CHANNEL, new ChannelHandler());
    }

    /**
     * Wrapper method for {@link FMLEmbeddedChannel#generatePacketFrom(Object)}.
     * Must have a codec in place to transform it for it to return anything.
     *
     * @param msg  object to generate from
     * @param side channel to side being sent to
     *
     * @return created packet
     */
    public Packet generatePacketFrom(PacketBase msg, Side side)
    {
        return this.channels.get(side).generatePacketFrom(msg);
    }

    public void sendPacketToServer(Packet packet)
    {
        this.channels.get(Side.CLIENT).writeOutbound(packet);
    }

    public void sendPacketToServer(PacketBase packet)
    {
        Packet generatedPacket = this.generatePacketFrom(packet, Side.SERVER);
        this.sendPacketToServer(generatedPacket);
    }

    private static class ChannelHandler extends SimpleChannelInboundHandler<PacketBase>
    {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, PacketBase packet) throws Exception
        {
            if (debug)
            {
                RankCapesForge.log.info("Got packet! Type: " + packet.getClass().getSimpleName());
            }

            try
            {
                ClientPacketHandler handler = ClientPacketHandler.INSTANCE;

                if (packet instanceof S0PacketPlayerCapesUpdate)
                {
                    handler.handleS0PacketPlayerCapesUpdate((S0PacketPlayerCapesUpdate) packet);
                }
                else if (packet instanceof S1PacketCapePack)
                {
                    handler.handleS1PacketCapePack((S1PacketCapePack) packet);
                }
                else if (packet instanceof S2PacketAvailableCapes)
                {
                    handler.handleS2PacketAvailableCapes((S2PacketAvailableCapes) packet);
                }
                else if (packet instanceof S3PacketTest)
                {
                    handler.handleS3PacketTest((S3PacketTest) packet);
                }
            }
            catch (Exception e)
            {
                RankCapesForge.log.error(String.format("Error while handling packet %s!", packet.getClass().getSimpleName()));
                e.printStackTrace();
            }
        }
    }

    private static class ChannelCodec extends FMLIndexedMessageToMessageCodec<PacketBase>
    {

        public ChannelCodec()
        {
            for (PacketType type : PacketType.values())
            {
                this.addDiscriminator(type.ordinal(), type.getPacketClass());
            }
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

    private void handleS0PacketPlayerCapesUpdate(S0PacketPlayerCapesUpdate packet)
    {
        CapePack capePack = CapeHandler.INSTANCE.getPack();
        if (capePack == null)
        {
            RankCapesForge.log.warn("Can't update cape because no cape pack.");
            return;
        }

        Map<String, String> changedCapes = packet.getPlayers();
        for (Entry<String, String> entry : changedCapes.entrySet())
        {
            AbstractClientPlayer player = (AbstractClientPlayer) Minecraft.getMinecraft().theWorld.getPlayerEntityByName(entry.getKey());

            switch (packet.type)
            {
                case UPDATE:
                {
                    String capeName = entry.getValue();
                    if (!StringUtils.isNullOrEmpty(capeName))
                    {
                        AbstractCape cape = capePack.getCape(capeName).clone();
                        CapeHandler.INSTANCE.setPlayerCape(cape, player);
                    }
                    break;
                }
                case REMOVE:
                {
                    CapeHandler.INSTANCE.resetPlayerCape(player);
                    break;
                }
            }
        }
    }

    private void handleS1PacketCapePack(S1PacketCapePack packet)
    {
        if (this.packAssembler == null || this.packAssembler.fullSize != packet.packSize)
        {
            this.packAssembler = new CapePackAssembler(packet.packSize);
        }

        boolean flag = this.packAssembler.addChunk(packet);
        if (!flag)
        {
            RankCapesForge.log.warn("Pack Assembler Failed!");
            this.packAssembler = null;
            return;
        }

        if (this.packAssembler.getFullPack() != null)
        {
            CapePack capePack = new CapePack(this.packAssembler.getFullPack());
            CapeHandler.INSTANCE.setPack(capePack);

            // deallocate so we can receive new pack later
            this.packAssembler = null;
        }
    }

    private void handleS2PacketAvailableCapes(S2PacketAvailableCapes packet)
    {
        CapeHandler.INSTANCE.availableCapes = packet.getCapes();
    }

    private void handleS3PacketTest(S3PacketTest packet)
    {
        if (debug)
        {
            RankCapesForge.log.info(String.format("Test from server. Payload: %s", packet.payload));
        }

        packet.payload += " back at ya";

        ClientPacketHandler handler = ClientPacketHandler.INSTANCE;
        this.sendPacketToServer(handler.generatePacketFrom(packet, Side.SERVER));
    }
}

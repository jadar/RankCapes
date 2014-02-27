package com.jadarstudios.rankcapes.forge.handler;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;

import com.jadarstudios.rankcapes.forge.RankCapesForge;
import com.jadarstudios.rankcapes.forge.cape.PlayerCapeProperties;
import com.jadarstudios.rankcapes.forge.network.CapePackClientReadThread;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.CustomPacketRegistrationEvent;

/**
 * This class listens for network events. Connection opening, connection
 * received, successfully logged in, etc.
 * 
 * @author Jadar
 */
public class PlayerEventHandler
{
    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event)
    {
        CapePackClientReadThread thread = RankCapesForge.instance.getReadThread();
        if (thread != null)
        {
            RankCapesForge.instance.getReadThread().stopThread();
        }
    }
    
    @SubscribeEvent
    public void onEntityConstruct(EntityConstructing event)
    {
        if (event.entity instanceof EntityPlayer)
            event.entity.registerExtendedProperties(PlayerCapeProperties.IDENTIFIER, new PlayerCapeProperties((AbstractClientPlayer) event.entity));
    }
    
    @SubscribeEvent
    public void onServerChannelRegister(CustomPacketRegistrationEvent<NetHandlerPlayServer> event)
    {
        for(String channel : event.registrations)
        {
            System.out.println("Channel Registered: "+ channel);
        }
    }
    
    @SubscribeEvent
    public void onClientChannelRegister(CustomPacketRegistrationEvent<NetHandlerPlayClient> event)
    {
        for(String channel : event.registrations)
        {
            System.out.println("Channel Registered: "+ channel);
        }
    }
}

/**
 * RankCapes Forge Mod
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jadarstudios.rankcapes.forge.cape.CapePack;
import com.jadarstudios.rankcapes.forge.handler.CapeHandler;
import com.jadarstudios.rankcapes.forge.handler.KeyEventHandler;
import com.jadarstudios.rankcapes.forge.handler.PlayerEventHandler;
import com.jadarstudios.rankcapes.forge.network.CapePackClientReadThread;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = ModProperties.MOD_ID, name = ModProperties.MOD_NAME, version = ModProperties.MOD_VERSION)
public class RankCapesForge
{
    
    @Instance
    public static RankCapesForge instance;
    
    private CapePackClientReadThread packReadThread;
    
    private CapePack capePack = null;
    
    private final CapeHandler capeHandler;
    public List<String> availableCapes;
    
    public static final Logger log = LogManager.getLogger(ModProperties.MOD_NAME);
    
    public RankCapesForge()
    {
        availableCapes = new ArrayList<String>();
        capeHandler = new CapeHandler();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        
        MinecraftForge.EVENT_BUS.register(new KeyEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
        MinecraftForge.EVENT_BUS.register(capeHandler);
    }
    
    @Deprecated
    public void connectReadThread(int port)
    {
        connectReadThread(getCurrentServerAddress(), port);
    }
    
    /**
     * Called to connect to new server.
     * 
     * @param serverAddress
     * @param port
     */
    @Deprecated
    public void connectReadThread(String serverAddress, int port)
    {
        if (packReadThread != null)
        {
            packReadThread.stopThread();
        }
        
        // create new read object.
        packReadThread = new CapePackClientReadThread(serverAddress, port);
        
        // make thread out of it.
        Thread t = new Thread(packReadThread);
        
        // makes it so java can exit without any problems from this thread.
        t.setDaemon(true);
        
        t.setName("RankCapes - Cape Pack Read Thread");
        t.start();
    }
    
    public CapeHandler getCapeHandler()
    {
        return capeHandler;
    }
    
    @Deprecated
    public CapePackClientReadThread getReadThread()
    {
        return packReadThread;
    }
    
    public CapePack getCapePack()
    {
        return capePack;
    }
    
    public synchronized void setCapePack(CapePack pack)
    {
        capePack = pack;
    }
    
    public static String getCurrentServerAddress()
    {
        String address = Minecraft.getMinecraft().getNetHandler().getNetworkManager().getSocketAddress().toString();
        return address.substring(0, address.lastIndexOf(':')).replace('/', ' ').trim();
    }
}

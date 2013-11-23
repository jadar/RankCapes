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
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.jadarstudios.rankcapes.forge.cape.CapePack;
import com.jadarstudios.rankcapes.forge.handler.CapeHandler;
import com.jadarstudios.rankcapes.forge.handler.CapeKeyHandler;
import com.jadarstudios.rankcapes.forge.network.CapePackClientReadThread;
import com.jadarstudios.rankcapes.forge.network.ClientPacketHandler;
import com.jadarstudios.rankcapes.forge.network.NetworkEventListener;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "rankcapesforge", name = "RankCapes", version = "1.0-BETA")
@NetworkMod(clientSideRequired = false, serverSideRequired = false, channels = { "RankCapes" }, packetHandler = ClientPacketHandler.class, connectionHandler = NetworkEventListener.class)
public class RankCapesForge
{
    
    @Instance
    public static RankCapesForge instance;
    
    private CapePackClientReadThread packReadThread;
    
    private CapePack capePack = null;
    
    private final CapeHandler capeHandler;
    public List<String> availableCapes;
    
    public static final Logger log = Logger.getLogger("RankCapes");
    
    public RankCapesForge()
    {
        availableCapes = new ArrayList<String>();
        capeHandler = new CapeHandler();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        TickRegistry.registerTickHandler(capeHandler, Side.CLIENT);
        
        KeyBinding[] key = { new KeyBinding("rankcapes.key.1", Keyboard.KEY_C) };
        boolean[] repeat = { false };
        KeyBindingRegistry.registerKeyBinding(new CapeKeyHandler(key, repeat));
        
        LanguageRegistry.instance().addStringLocalization("rankcapes.key.1", "Change Cape");
    }
    
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
    
    public String getPlayersCapeName(String username)
    {
        return capeHandler.playerCapeNames.get(username);
    }
    
    /**
     * Changes the player's cape client side.
     * 
     * @param capeName
     *            cape name to change to.
     */
    public void changePlayerCape(String capeName)
    {
        if (availableCapes.contains(capeName))
        {
            capeHandler.playerCapeNames.put(Minecraft.getMinecraft().thePlayer.username, capeName);
            capeHandler.capeChangeQue.add(Minecraft.getMinecraft().thePlayer.username);
        }
    }
    
    /**
     * Removes the player's cape client side.
     */
    public void removePlayerCape()
    {
        capeHandler.playerCapeNames.remove(Minecraft.getMinecraft().thePlayer.username);
        capeHandler.capeChangeQue.add(Minecraft.getMinecraft().thePlayer.username);
    }
    
    public static String getCurrentServerAddress()
    {
        String address = Minecraft.getMinecraft().getNetHandler().getNetManager().getSocketAddress().toString();
        return address.substring(0, address.lastIndexOf(':')).replace('/', ' ').trim();
    }
}

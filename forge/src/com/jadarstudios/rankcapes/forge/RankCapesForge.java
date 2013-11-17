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

@Mod(modid = "rankcapesforge", name = "RankCapes", version = "alpha1")
@NetworkMod(
        clientSideRequired = false, 
        serverSideRequired = false, 
        channels = { "RankCapes" }, 
        packetHandler = ClientPacketHandler.class, 
        connectionHandler = NetworkEventListener.class)
public class RankCapesForge
{
    
    @Instance
    public static RankCapesForge instance;
    
    private CapePackClientReadThread packDownload;
    
    private CapePack capePack = null;
    
    private final CapeHandler capeHandler;
    public List<String> availableCapes;
    
    public Logger log;
    
    public RankCapesForge()
    {
        availableCapes = new ArrayList<String>();
        capeHandler = new CapeHandler();
        log = Logger.getLogger("RankCapes");
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        TickRegistry.registerTickHandler(capeHandler, Side.CLIENT);
        
        KeyBinding[] key = {new KeyBinding("rankcapes.key.1", Keyboard.KEY_C)};
        boolean[] repeat = {false};
        KeyBindingRegistry.registerKeyBinding(new CapeKeyHandler(key, repeat));
        
        LanguageRegistry.instance().addStringLocalization("rankcapes.key.1", "Change Cape");
    }
    
    public void connectToServer(int port)
    {
        connectToServer(getCurrentServerAddress(), port);
    }
    
    public String getCurrentServerAddress()
    {
        String address = Minecraft.getMinecraft().getNetHandler().getNetManager().getSocketAddress().toString();
        return address.substring(0, address.lastIndexOf(':')).replace('/', ' ').trim();
    }
    
    /**
     * Called to connect to new server.
     * @param serverAddress
     * @param port
     */
    public void connectToServer(String serverAddress, int port)
    {
        if(packDownload != null)
        {
            packDownload.stopThread();
        }
        
        // create new read object.
        packDownload = new CapePackClientReadThread(serverAddress, port);

        // make thread out of it.
        Thread t = new Thread(packDownload);
        
        // makes it so java can exit without any problems from this thread.
        t.setDaemon(true);
        
        t.setName("RankCapes - Cape Pack Read Thread");
        t.start();
    }
    
    public String getPlayersCapeName(String username)
    {
        return capeHandler.playerCapeNames.get(username);
    }
    
    public CapeHandler getCapeHandler()
    {
        return this.capeHandler;
    }
    
    public synchronized CapePack getCapePack()
    {
        return this.capePack;
    }
    
    public synchronized void setCapePack(CapePack pack)
    {
        this.capePack = pack;
    }
    
    public void changePlayerCape(String capeName)
    {
        capeHandler.playerCapeNames.put(Minecraft.getMinecraft().thePlayer.username, capeName);
        capeHandler.capeChangeQue.add(Minecraft.getMinecraft().thePlayer.username);
    }
    
    public CapePackClientReadThread getPackDownload()
    {
        return packDownload;
    }

    public void removePlayerCape()
    {
        capeHandler.playerCapeNames.remove(Minecraft.getMinecraft().thePlayer.username);
        capeHandler.capeChangeQue.add(Minecraft.getMinecraft().thePlayer.username);
    }
    
    public synchronized Logger getLogger()
    {
        return log;
    }
}

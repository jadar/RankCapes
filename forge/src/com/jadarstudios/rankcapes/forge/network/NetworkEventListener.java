package com.jadarstudios.rankcapes.forge.network;

import com.jadarstudios.rankcapes.forge.RankCapesForge;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class NetworkEventListener implements IConnectionHandler
{

    @Override
    public void connectionClosed(INetworkManager manager)
    {
        System.out.println("Connection Closed!");
        CapePackClientReadThread thread = RankCapesForge.instance.getPackDownload();
        if(thread != null)
            RankCapesForge.instance.getPackDownload().stopThread();
    }
    
    // none of these are needed.
    
    @Override
    public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {}

    @Override
    public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) { return null; }

    @Override
    public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {}

    @Override
    public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {}

    @Override
    public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {}
    
}

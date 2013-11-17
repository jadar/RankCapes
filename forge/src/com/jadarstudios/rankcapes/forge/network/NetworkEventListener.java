/**
 * RankCapes Forge Mod
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.network;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;

import com.jadarstudios.rankcapes.forge.RankCapesForge;

import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

/**
 * This class listens for network events. Connection opening, connection
 * received, successfully logged in, etc.
 * 
 * @author Jadar
 */
public class NetworkEventListener implements IConnectionHandler
{
    
    @Override
    /**
     * Closes read thread when disconnected.
     */
    public void connectionClosed(INetworkManager manager)
    {
        CapePackClientReadThread thread = RankCapesForge.instance.getReadThread();
        if (thread != null)
        {
            RankCapesForge.instance.getReadThread().stopThread();
        }
    }
    
    // none of these are needed.
    
    @Override
    public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager)
    {
    }
    
    @Override
    public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager)
    {
        return null;
    }
    
    @Override
    public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager)
    {
    }
    
    @Override
    public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager)
    {
    }
    
    @Override
    public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login)
    {
    }
    
}

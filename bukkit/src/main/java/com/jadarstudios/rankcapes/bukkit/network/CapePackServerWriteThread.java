/**
 * RankCapes Bukkit Plugin.
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.jadarstudios.rankcapes.bukkit.RankCapesBukkit;

/**
 * Thread that writes the cape pack to the connected client's output stream.
 * 
 * @author Jadar
 * 
 */
public class CapePackServerWriteThread extends Thread
{
    
    /**
     * The instance of the RankCapes plugin.
     */
    private RankCapesBukkit plugin;
    
    /**
     * Tells the thread if it should stop or not. This is a soft stop.
     */
    private boolean stop = false;
    
    /**
     * The socket that is connected to the client.
     */
    private Socket socket;
    
    /**
     * The DataOutputStream for sending bytes.
     */
    private DataOutputStream out;
    
    /**
     * Used to tell running thread to send it's client a pack.
     */
    private boolean shouldSendPack = false;
    
    /**
     * Username of the player this thread is serving.
     */
    private final String username;
    
    /**
     * @param parPlugin
     *            The RankCapes plugin instance.
     * @param parSocket
     *            The Socket connected to the client.
     */
    public CapePackServerWriteThread(RankCapesBukkit parPlugin, Socket parSocket, String parUsername)
    {
        plugin = parPlugin;
        socket = parSocket;
        username = parUsername;
    }
    
    @Override
    public void run()
    {
        try
        {
            // create DataOutputStream
            out = new DataOutputStream(socket.getOutputStream());
            
            // send pack using the output stream.
            sendPack(out);
            
            // enter into loop that checks if it should send the pack again.
            while (!stop && stillConnected())
            {
                if (shouldSendPack)
                {
                    sendPack(out);
                    shouldSendPack = false;
                }
                
                // sleep to prevent huge lag. may not me necessary.
                Thread.sleep(2L);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            // do not print this exception for clean exit.
            ;
        }
        finally
        {
            closeConnection();
        }
        
        // remove self from the main server thread.
        plugin.getListenThread().removeUser(username);
    }
    
    /**
     * Checks if the socket is still connected by trying to read from it.
     * 
     * @return if the socket is connected or not
     */
    private boolean stillConnected()
    {
        try
        {
            int i = socket.getInputStream().read();
            if (i == -1)
                return false;
        }
        catch (IOException e)
        {
            return false;
        }
        
        return true;
    }
    
    /**
     * Sends a cape pack using the given DataOutputStream.
     * 
     * @param out
     *            the DataOutputStream to send using.
     */
    private void sendPack(DataOutputStream out)
    {
        // array from plugin
        byte[] array = plugin.getPack();
        try
        {
            // write the length first. needed to assemble on client.
            out.writeLong(array.length);
            
            // flush to the network.
            out.flush();
            
            // write the cape pack
            out.write(array);
            
            // flush to the network.
            out.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            RankCapesBukkit.log.severe(String.format("Error while sending cape pack to player %s.", username));
        }
    }
    
    /**
     * Requests a pack be sent to the client.
     */
    public synchronized void quePackSend()
    {
        shouldSendPack = true;
    }
    
    /**
     * Stops the thread loop.
     */
    public synchronized void stopThread()
    {
        stop = true;
    }
    
    /**
     * Closes the socket connection.
     */
    public synchronized void closeConnection()
    {
        try
        {
            if (out != null)
            {
                out.close();
            }
            
            if (socket != null)
            {
                socket.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

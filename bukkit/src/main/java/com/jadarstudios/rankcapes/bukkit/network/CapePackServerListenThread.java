/**
 * RankCapesBukkit Plugin.
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapesBukkit/blob/master/LICENSE
 */
 
package com.jadarstudios.rankcapes.bukkit.network;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import com.google.common.base.Strings;
import com.jadarstudios.rankcapes.bukkit.RankCapesBukkit;

/**
 * Thread that listens for incoming connections from modded RankCapes clients.
 * Creates CapePackServerWriteThread instance when it gets a connection.
 * 
 * @author Jadar
 */
public class CapePackServerListenThread extends Thread
{
    /**
     * The instance of the RankCapes plugin.
     */
    private final RankCapesBukkit plugin;
    
    /**
     * Tells the thread if it should stop or not. This is a soft stop.
     */
    private boolean stop = false;
    
    /**
     * Server socket that will listen for incoming connections.
     */
    private ServerSocket serverSocket;
    
    /**
     * Port to listen on.
     */
    private final int port;
    
    /**
     * All the client's write threads.
     */
    private final HashMap<String, CapePackServerWriteThread> writeThreads;
    
    /**
     * 
     * @param parPlugin
     *            RankCapes plugin instance.
     * @param parPort
     *            port to listen on.
     */
    public CapePackServerListenThread(RankCapesBukkit parPlugin, int parPort)
    {
        writeThreads = new HashMap<String, CapePackServerWriteThread>();
        plugin = parPlugin;
        port = parPort;
    }
    
    @Override
    public void run()
    {
        
        // set up server socket.
        try
        {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
        
        // initialize client socket.
        Socket socket = null;
        
        // initialize stream reader.
        InputStreamReader in = null;
        
        // start listening.
        while (!stop)
        {
            try
            {
                // block until connection is made
                socket = serverSocket.accept();
                
                RankCapesBukkit.log.info("Connected to client: " + socket.getInetAddress().getHostAddress());
                String username = "";
                
                in = new InputStreamReader(socket.getInputStream());
                
                // timeout timer.
                long startTime = System.currentTimeMillis();
                long elapsedTime = 0;
                // read while true.
                while (true)
                {
                    elapsedTime = System.currentTimeMillis() - startTime;
                    if (elapsedTime >= 10000)
                    {
                        RankCapesBukkit.log.severe("Client " + socket.getInetAddress().getHostAddress() + "took too long to respond. Closing connection.");
                        closeSocket();
                        
                        break;
                    }
                    
                    // read bytes for username.
                    if (in.ready())
                    {
                        while (in.ready())
                        {
                            username += (char) in.read();
                        }
                        
                        // break, we're done here.
                        break;
                    }
                }
                
                // if still connected and username is something, add user.
                if (socket.isConnected() && !Strings.isNullOrEmpty(username))
                {
                    addUser(username, socket);
                }
            }
            catch (IOException e)
            {
                if (!serverSocket.isClosed())
                {
                    e.printStackTrace();
                }
            }
        }
        
        closeSocket();
        
    }
    
    /**
     * Closes the socket.
     */
    public synchronized void closeSocket()
    {
        try
        {
            RankCapesBukkit.log.info("Closing Cape Server Listener socket.");
            
            if (serverSocket != null)
            {
                serverSocket.close();
            }
        }
        catch (Throwable e)
        {
            ;
        }
    }
    
    /**
     * Creates write thread, starts it, and adds it to the map.
     * 
     * @param username
     *            username of the player we're serving.
     * @param socket
     *            player's connection socket.
     */
    private void addUser(String username, Socket socket)
    {
        CapePackServerWriteThread thread = new CapePackServerWriteThread(plugin, socket, username);
        thread.setName("RankCapes Write Thread - " + username);
        thread.setDaemon(true);
        thread.start();
        writeThreads.put(username, thread);
    }
    
    /**
     * Removes write thread from map.
     * 
     * @param username
     *            key of write thread.
     */
    public void removeUser(String username)
    {
        writeThreads.remove(username);
    }
    
    /**
     * Stops the thread semi-peacefully.
     */
    public synchronized void stopThread()
    {
        stop = true;
        closeSocket();
        interrupt();
    }
    
    /**
     * Stops all the write thread semi-peacefully.
     */
    public synchronized void stopAllThreads()
    {
        for (CapePackServerWriteThread thread : writeThreads.values())
        {
            thread.stopThread();
            thread.closeConnection();
            thread.interrupt();
        }
    }
}

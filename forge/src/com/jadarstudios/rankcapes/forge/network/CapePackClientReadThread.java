package com.jadarstudios.rankcapes.forge.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import net.minecraft.client.Minecraft;

import com.jadarstudios.rankcapes.forge.RankCapesForge;
import com.jadarstudios.rankcapes.forge.cape.CapePack;

public class CapePackClientReadThread implements Runnable
{
    
    private static final RankCapesForge modInstance = RankCapesForge.instance;
    
    private String serverAddress;
    private int serverPort;
    private Socket socket;
    
    private DataInputStream in;
    private DataOutputStream out;
    
    private boolean stop = false;
    
    public CapePackClientReadThread(String server, int port)
    {
        serverAddress = server;
        serverPort = port;
    }
    
    @Override
    public void run()
    {
        try
        {
            socket = new Socket(serverAddress, serverPort);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            
            // sends username for handling
            out.write((Minecraft.getMinecraft().thePlayer.username).getBytes());
            out.flush();
            
            while (!stop && stillConnected())
            {
                if (in.available() > 0)
                {
                    
                    long size = in.readLong();
                    byte[] buffer = new byte[(int) size];
                    
                    // simple timer.
                    int bytesRead = 0;
                    final long startTime = System.currentTimeMillis();
                    float timeElapsed;
                    
                    // do read until full array has been read.
                    do
                    {
                        // timeout if elapsed more than 20 seconds.
                        timeElapsed = System.currentTimeMillis() - startTime;
                        if (timeElapsed >= 10000)
                        {
                            System.out.println("Transfor timed out.");
                            continue;
                        }
                        
                        // reads to the buffer and records the amount of bytes read.
                        bytesRead += in.read(buffer);
                    }
                    while (bytesRead != size);
                    
                    if (bytesRead == size)
                    {
                        // creates new pack from bytes.
                        CapePack pack = new CapePack(serverAddress, buffer);
                        
                        // sets pack in mod.
                        modInstance.setCapePack(pack);
                    }
                    else
                    {
                        modInstance.getLogger().severe("Cape Pack read failed.");
                    }
                    
                    timeElapsed = 0;
                }
                
                // sleep to prevent lag.
                Thread.sleep(2L);
            }
            
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (socket != null)
                    socket.close();
                System.out.println("Socket Closed");
            }
            catch (IOException f)
            {
                f.printStackTrace();
            }
        }
        
        System.out.println("PACK DOWNLOAD THREAD DONE!");
    }
    
    /**
     * Checks if the socket is still connected by trying to write to it.
     * 
     * @return if the socket is connected or not
     */
    private boolean stillConnected()
    {
        try
        {
            socket.getOutputStream().write(null);
        }
        catch (IOException e)
        {
            return false;
        }
        
        return true;
    }
    
    /**
     * Closes the socket connection.
     */
    public synchronized void closeConnection()
    {
        try
        {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
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
    
    public synchronized void stopThread()
    {
        this.stop = true;
    }
    
}

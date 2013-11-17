package com.jadarstudios.rankcapes.forge.network;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import joptsimple.internal.Strings;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import org.apache.commons.lang3.StringUtils;

import com.jadarstudios.rankcapes.forge.RankCapesForge;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

/**
 * Handles server packets.
 * 
 * @author Jadar
 */
public class ClientPacketHandler implements IPacketHandler
{
    
    public static final String[] RECIEVE_COMMANDS = {
        "acknowledge",
        "transmitPort",
        "playerCapeUpdate",
        "playerCapes",
        "availableCapes",
    };
    
    private static ClientPacketHandler instance;
    
    public ClientPacketHandler()
    {
        instance = this;
    }
    
    public static ClientPacketHandler getInstance()
    {
        return instance;
    }
    
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        //       System.out.println(packet.channel);
        
        String data = readByteArray(packet.data);
        System.out.println("***" + data + "***");
        
        if(!data.contains(":"))
        {
            return;
        }
        
        // should have 2 entries, command:args
        String[] command = data.split(":");
        
        // return if the command does not have args, or if the args are empty or null.
        if(command.length < 2 ||
                Strings.isNullOrEmpty(command[1]))
        {
            return;
        }
        
        
        if(command[0].equals("transmitPort"))
        {
            if(StringUtils.isNumeric(command[1]))
                RankCapesForge.instance.connectToServer(Integer.parseInt(command[1]));
        }
        else if(command[0].equals("playerCapeUpdate"))
        {
            handlePlayerCapeUpdate(command[1]);
        }
        else if(command[0].equals("allPlayerCapes"))
        {
            HashMap<String, String> t = parsePlayerCapes(command[1]);
            RankCapesForge.instance.getCapeHandler().playerCapeNames = t;
            RankCapesForge.instance.getCapeHandler().capeChangeQue.addAll(t.keySet());
        }
        else if(command[0].equals("availableCapes"))
        {
            List<String> t = Arrays.asList(command[1].split(","));
            RankCapesForge.instance.availableCapes = t;
        }
        else if(command[0].equals("removeCapeUpdate"))
        {
            RankCapesForge.instance.getCapeHandler().playerCapeNames.remove(command[1]);
            RankCapesForge.instance.getCapeHandler().capeChangeQue.add(command[1]);
            System.out.println(command[1]);
        }
        
    }
    
    /**
     * Handles single player cape update.
     * @param args from received command.
     */
    private void handlePlayerCapeUpdate(String args)
    {
        String[] t = args.split(",");
        if(t.length != 2)
            return;
        
        RankCapesForge.instance.getCapeHandler().playerCapeNames.put(t[0], t[1]);
        RankCapesForge.instance.getCapeHandler().capeChangeQue.add(t[0]);
    }
    
    public HashMap<String, String> parsePlayerCapes(String data)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        
        String[] splitData = data.split(Pattern.quote("|"));
        
        for(String playerData : splitData)
        {
            String[] splitPlayerData = playerData.split(",");
            map.put(splitPlayerData[0], splitPlayerData[1]);
        }
        
        return map;
    }
    
    public void sendRequestPacket()
    {
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "RankCapes";
        packet.data = "REQUEST-PACK".getBytes();
        packet.length = packet.data.length;
        
        PacketDispatcher.sendPacketToServer(packet);
    }
    
    public void sendCapeChangePacket(String capeName)
    {
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "RankCapes";
        packet.data = ("changeCape:" + capeName).getBytes();
        packet.length = packet.data.length;
        
        PacketDispatcher.sendPacketToServer(packet);
    }
    
    public void sendCapeRemovePacket()
    {
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "RankCapes";
        packet.data = ("removeCape").getBytes();
        packet.length = packet.data.length;
        
        PacketDispatcher.sendPacketToServer(packet);
    }
    
    private static String readByteArray(byte[] data)
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
            String rtrn = "";
            while (br.ready())
                rtrn += br.readLine();
            return rtrn;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}

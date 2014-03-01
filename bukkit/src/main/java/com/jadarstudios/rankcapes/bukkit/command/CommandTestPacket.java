package com.jadarstudios.rankcapes.bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jadarstudios.rankcapes.bukkit.RankCapesBukkit;
import com.jadarstudios.rankcapes.bukkit.network.PluginPacketHandler;
import com.jadarstudios.rankcapes.bukkit.network.packet.PacketBase;
import com.jadarstudios.rankcapes.bukkit.network.packet.PacketCommand;

public class CommandTestPacket implements CommandExecutor
{

    RankCapesBukkit plugin;
    
    public CommandTestPacket(RankCapesBukkit plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(args.length == 0)
        {
            sender.sendMessage("Not enough args!");
            return false;
        }
        
        if(sender instanceof Player)
        {
            Player player = (Player)sender;
            
            int c = Integer.parseInt(args[0]);
            String s = "";
            
            for(int i = 1; i < args.length; i++)
            {
                s += args[i] + " ";
            }
            
            PacketBase packet = new PacketCommand((byte)c, s);
            PluginPacketHandler.instance().sendPacketToPlayer(player, packet);
            
            return true;
        }
        
        return false;
    }
    
}

/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.command;

import com.jadarstudios.rankcapes.bukkit.RankCapesBukkit;
import com.jadarstudios.rankcapes.bukkit.network.PluginPacketHandler;
import com.jadarstudios.rankcapes.bukkit.network.packet.PacketBase;
import com.jadarstudios.rankcapes.bukkit.network.packet.S3PacketTest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        if (args.length == 0)
        {
            sender.sendMessage("Not enough args!");
            return false;
        }

        if (sender instanceof Player)
        {
            Player player = (Player) sender;

            String s = "";

            for (int i = 0; i < args.length; i++)
            {
                s += args[i] + " ";
            }

            PacketBase packet = new S3PacketTest(s);
            PluginPacketHandler.INSTANCE.sendPacketToPlayer(player, packet);

            return true;
        }

        return false;
    }

}

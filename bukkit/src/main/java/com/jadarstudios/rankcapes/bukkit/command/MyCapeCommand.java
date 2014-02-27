/**
 * RankCapes Bukkit Plugin.
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jadarstudios.rankcapes.bukkit.RankCapesBukkit;
import com.jadarstudios.rankcapes.bukkit.database.PlayerCape;

/**
 * This is a command that allows players to get their cape name in chat. Used for debug purposes.
 * 
 * @author Jadar
 */
public class MyCapeCommand implements CommandExecutor
{
    RankCapesBukkit instance;
    
    public MyCapeCommand(RankCapesBukkit parI)
    {
        instance = parI;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender instanceof Player))
        {
            return false;
        }
        
        Player player = (Player)sender;
        
        PlayerCape p = instance.getDatabase().find(PlayerCape.class).where().ieq("playerName", player.getName()).findUnique();
        if(p == null)
        {
            sender.sendMessage("You have no cape.");
        }
        else
            sender.sendMessage("You have the " + p.getCapeName() + " cape.");
        
        instance.getDatabase().save(p);
        return true;
    }
    
}

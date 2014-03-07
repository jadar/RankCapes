/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.database;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity()
@Table(name = "rc_playerCapes")
/**
 * Used to get and store data in the plugin database.
 *
 * @author Jadar
 */
public class PlayerCape
{
    @Id
    private int id;

    @NotNull
    @NotEmpty
    private String playerName;

    @NotNull
    @NotEmpty
    private String capeName;

    public void setId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }

    public String getPlayerName()
    {
        return this.playerName;
    }

    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
    }

    public Player getPlayer()
    {
        return Bukkit.getServer().getPlayer(this.playerName);
    }

    public void setPlayer(Player player)
    {
        this.playerName = player.getName();
    }

    public void setCapeName(String parName)
    {
        this.capeName = parName;
    }

    public String getCapeName()
    {
        return this.capeName;
    }
}

package com.jadarstudios.rankcapes.forge.event;

import com.jadarstudios.rankcapes.forge.cape.ICape;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EventPlayerCapeChange extends PlayerEvent
{
    public final ICape cape;
    
    public EventPlayerCapeChange(ICape cape, EntityPlayer player)
    {
        super(player);
        this.cape = cape;
    }
}

package com.jadarstudios.rankcapes.forge.event;

import com.jadarstudios.rankcapes.forge.cape.AbstractCape;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

@SideOnly(Side.CLIENT)
public class EventPlayerCapeChange extends PlayerEvent
{
    public final AbstractCape cape;

    public EventPlayerCapeChange(AbstractCape cape, EntityPlayer player)
    {
        super(player);
        this.cape = cape;
    }
}

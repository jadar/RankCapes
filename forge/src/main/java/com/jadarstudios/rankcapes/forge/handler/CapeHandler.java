/**
 * RankCapes Forge Mod
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import com.jadarstudios.rankcapes.forge.RankCapesForge;
import com.jadarstudios.rankcapes.forge.cape.AnimatedCape;
import com.jadarstudios.rankcapes.forge.cape.ICape;
import com.jadarstudios.rankcapes.forge.cape.PlayerCapeProperties;
import com.jadarstudios.rankcapes.forge.event.EventPlayerCapeChange;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
/**
 * This class changes the capes on players.
 * 
 * If getting errors about invisible fields, make them visible.
 * Forge makes everything visible when compiled at runtime.
 * 
 * @author Jadar
 */
public class CapeHandler
{
    // used to print debug code.
    private static final boolean debug = true;
    
    private static CapeHandler INSTNACE;
    
    public CapeHandler()
    {
    }
    
    public static CapeHandler instance()
    {
        if (INSTNACE == null)
            INSTNACE = new CapeHandler();
        
        return INSTNACE;
    }
    
    @SubscribeEvent
    public void renderPlayerEvent(RenderPlayerEvent.Specials.Pre event)
    {
        AbstractClientPlayer player = (AbstractClientPlayer) event.entityPlayer;
        
        // cape from current player.
        // ICape cape = currentPlayerCapes.get(player.getCommandSenderName());
        PlayerCapeProperties properties = (PlayerCapeProperties) player.getExtendedProperties(PlayerCapeProperties.IDENTIFIER);
        if (properties == null)
            return;
        
        ICape cape = properties.getCape();
        
        if (cape != null && cape instanceof AnimatedCape)
        {
            boolean flag = ((AnimatedCape) cape).update();
            if (flag)
                this.setPlayerCape(cape, player);
        }
    }
    
    public void setPlayerCape(ICape cape, AbstractClientPlayer player)
    {
        if (cape != null)
        {
            MinecraftForge.EVENT_BUS.post(new EventPlayerCapeChange(cape, player));
            
            if (debug && !(cape instanceof AnimatedCape))
                RankCapesForge.log.info("Changing the cape of: " + player.getCommandSenderName());
            
            // loads its texture to the player's resource location, setting the cape.
            cape.loadTexture(player);
            
            PlayerCapeProperties properties = (PlayerCapeProperties) player.getExtendedProperties(PlayerCapeProperties.IDENTIFIER);
            properties.setCape(cape);
        }
        else
            this.resetPlayerCape(player);
    }
    
    public void resetPlayerCape(AbstractClientPlayer player)
    {
        Minecraft.getMinecraft().getTextureManager().loadTexture(player.getLocationCape(), player.getTextureCape());
    }
    
}

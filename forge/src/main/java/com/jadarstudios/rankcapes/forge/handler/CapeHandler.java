/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.handler;

import com.jadarstudios.rankcapes.forge.RankCapesForge;
import com.jadarstudios.rankcapes.forge.cape.AbstractCape;
import com.jadarstudios.rankcapes.forge.cape.AnimatedCape;
import com.jadarstudios.rankcapes.forge.cape.CapePack;
import com.jadarstudios.rankcapes.forge.cape.PlayerCapeProperties;
import com.jadarstudios.rankcapes.forge.event.EventPlayerCapeChange;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
/**
 * This class changes the capes on players. It also ticks {@link AnimatedCape} instances.
 *
 * @author Jadar
 */
public enum CapeHandler
{
    INSTANCE;

    // used to print debug code.
    private static final boolean debug = false;

    private CapePack capePack = null;
    public List<String> availableCapes;

    private CapeHandler()
    {
        availableCapes = new ArrayList<String>();
    }

    /**
     * Ticks Animated capes so they animate.
     *
     * @param event the render event
     */
    @SubscribeEvent
    public void renderPlayerEvent(RenderPlayerEvent.Specials.Pre event)
    {
        AbstractClientPlayer player = (AbstractClientPlayer) event.entityPlayer;

        // cape from current player.
        PlayerCapeProperties properties = PlayerCapeProperties.forPlayer(player);
        if (properties == null)
        {
            return;
        }

        AbstractCape cape = properties.getCape();

        if (cape != null && cape instanceof AnimatedCape)
        {
            AnimatedCape animated = (AnimatedCape)cape;
            boolean flag = true;

            if(animated.animateWhenMoving())
            {
                flag = player.motionX != 0 || player.motionZ != 0 || Math.abs(player.motionY) > 0.09;
            }

            if(flag)
            {
                boolean updated = animated.update();
                if (updated)
                {
                    this.setPlayerCape(animated, player);
                }
            }
        }
    }

    /**
     * Sets a player cape.
     *
     * @param cape the cape to set
     * @param player the player whose cape to set
     */
    public void setPlayerCape(AbstractCape cape, AbstractClientPlayer player)
    {
        if (cape == null)
        {
            return;
        }

        MinecraftForge.EVENT_BUS.post(new EventPlayerCapeChange(cape, player));

        if (debug && !(cape instanceof AnimatedCape))
        {
            RankCapesForge.log.info("Changing the cape of: " + player.getCommandSenderName());
        }

        cape.loadTexture(player);

        PlayerCapeProperties properties = PlayerCapeProperties.forPlayer(player);
        properties.setCape(cape);

    }

    /**
     * Really awful cape removal method. Uses reflection to get the {@link ThreadDownloadImageData#loadTexture}
     * method to fully work.
     */
    public void resetPlayerCape(AbstractClientPlayer player)
    {
        ThreadDownloadImageData texture = player.getTextureCape();
        texture.deleteGlTexture();
        texture.setBufferedImage(null);
        for (Field f : texture.getClass().getDeclaredFields())
        {
            try
            {
                if (f.getGenericType().equals(Boolean.TYPE))
                {
                    f.setAccessible(true);
                    f.set(texture, false);

                }
                else if (f.getGenericType().equals(Thread.class))
                {
                    f.setAccessible(true);
                    f.set(texture, null);
                }
            }
            catch (IllegalArgumentException ignored)
            {
            }
            catch (IllegalAccessException ignored)
            {
            }
        }
        Minecraft.getMinecraft().getTextureManager().loadTexture(player.getLocationCape(), texture);

        PlayerCapeProperties properties = (PlayerCapeProperties) player.getExtendedProperties(PlayerCapeProperties.IDENTIFIER);
        properties.setCape(null);
    }

    /**
     * Gets a cape instance from the cape pack.
     *
     * @param capeName the name of the cape to get
     */
    public AbstractCape getCape(String capeName)
    {
        return this.capePack != null ? this.capePack.getCape(capeName) : null;
    }

    /**
     * Sets the current Cape Pack.
     *
     * @param pack the cape pack to set
     */
    public void setPack(CapePack pack)
    {
        this.capePack = pack;
    }

    /**
     * Gets the Cape Pack.
     */
    public CapePack getPack()
    {
        return this.capePack;
    }
}

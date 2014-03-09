/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.handler;

import com.jadarstudios.rankcapes.forge.cape.PlayerCapeProperties;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;

/**
 * This class listens for Forge events.
 *
 * @author Jadar
 */
public enum PlayerEventHandler
{
    INSTANCE;

    /**
     * Registers extended properties with player entities when they are constructed.
     *
     * @param event the {@link EntityConstructing} event
     */
    @SubscribeEvent
    public void onEntityConstruct(EntityConstructing event)
    {
        if (event.entity instanceof EntityPlayer)
        {
            event.entity.registerExtendedProperties(PlayerCapeProperties.IDENTIFIER, new PlayerCapeProperties());
        }
    }
}

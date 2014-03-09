/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge;

import com.jadarstudios.rankcapes.forge.handler.CapeHandler;
import com.jadarstudios.rankcapes.forge.handler.KeyEventHandler;
import com.jadarstudios.rankcapes.forge.handler.PlayerEventHandler;
import com.jadarstudios.rankcapes.forge.network.ClientPacketHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is the main class for the mod.
 */
@Mod(modid = ModProperties.MOD_ID, name = ModProperties.MOD_NAME, version = ModProperties.MOD_VERSION)
public class RankCapesForge
{
    @Instance(ModProperties.MOD_ID)
    public static RankCapesForge instance;

    public static final Logger log = LogManager.getLogger(ModProperties.MOD_NAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
        {
            throw new RuntimeException(String.format("%s (version %s) should only be run on the client!", ModProperties.MOD_ID, ModProperties.MOD_VERSION));
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        KeyEventHandler.INSTANCE.ordinal();
        PlayerEventHandler.INSTANCE.ordinal();
        CapeHandler.INSTANCE.ordinal();
        ClientPacketHandler.INSTANCE.ordinal();

        FMLCommonHandler.instance().bus().register(KeyEventHandler.INSTANCE);

        MinecraftForge.EVENT_BUS.register(PlayerEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(CapeHandler.INSTANCE);
    }
}

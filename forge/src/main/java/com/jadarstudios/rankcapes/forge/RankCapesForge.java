/**
 * RankCapes Forge Mod
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jadarstudios.rankcapes.forge.cape.CapePack;
import com.jadarstudios.rankcapes.forge.handler.CapeHandler;
import com.jadarstudios.rankcapes.forge.handler.KeyEventHandler;
import com.jadarstudios.rankcapes.forge.handler.PlayerEventHandler;
import com.jadarstudios.rankcapes.forge.network.ClientPacketHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = ModProperties.MOD_ID, name = ModProperties.MOD_NAME, version = ModProperties.MOD_VERSION)
public class RankCapesForge
{
    @Instance
    public static RankCapesForge instance;
    
    private CapePack capePack = null;
    public List<String> availableCapes;
    
    public static final Logger log = LogManager.getLogger(ModProperties.MOD_NAME);
    
    public RankCapesForge()
    {
        this.availableCapes = new ArrayList<String>();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        
        FMLCommonHandler.instance().bus().register(KeyEventHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(PlayerEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(PlayerEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(CapeHandler.instance());
        ClientPacketHandler.instance();
    }
    
    public CapePack getCapePack()
    {
        return this.capePack;
    }
    
    public void setCapePack(CapePack pack)
    {
        this.capePack = pack;
    }
}

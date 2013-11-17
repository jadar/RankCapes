package com.jadarstudios.rankcapes.forge.handler;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.StringUtils;

import com.jadarstudios.rankcapes.forge.RankCapesForge;
import com.jadarstudios.rankcapes.forge.cape.AnimatedCape;
import com.jadarstudios.rankcapes.forge.cape.ICape;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
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
public class CapeHandler implements ITickHandler
{
    // the minecraft instance.
    private static final Minecraft mc = Minecraft.getMinecraft();
    
    // used to print debug code.
    private boolean debug = false;
    
    // counter to count through player list.
    private int counter = 0;
    
    // player cape names. player is the key, cape is the mapped value.
    public HashMap<String, String> playerCapeNames;
    
    // current player capes for switching.
    public HashMap<String, ICape> currentPlayerCapes;
    public List<String> capeChangeQue;
    
    public CapeHandler()
    {
        currentPlayerCapes = new HashMap<String, ICape>();
        capeChangeQue = new ArrayList<String>();
        playerCapeNames = new HashMap<String, String>();
    }
    
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        // mod instance.
        RankCapesForge instance = RankCapesForge.instance;
        
        // if no cape pack, return.
        if (instance.getCapePack() == null)
            return;
        
        // if no world, return.
        if (mc.theWorld == null)
            return;
        
        @SuppressWarnings("unchecked")
        // list of players in the world.
        List<AbstractClientPlayer> players = mc.theWorld.playerEntities;
        
        // if no players, return.
        if (players == null)
            return;
        else if (players.size() <= 0)
            return;
        
        // resets counter for looping player list.
        if (counter >= players.size())
        {
            counter = 0;
        }
        
        // player to work on.
        AbstractClientPlayer player = players.get(counter);
        
        // if player is null, increment and return.
        if (player == null)
        {
            counter++;
            return;
        }

        // player username with no control codes.
        String username = StringUtils.stripControlCodes(player.username);
        
        // player cape name
        String capeName = instance.getPlayersCapeName(username);
        
        // if no rank, increment and return.
        if (Strings.isNullOrEmpty(capeName))
        {
            if (capeChangeQue.contains(username))
            {
                setDefaultPlayerCape(player);
                capeChangeQue.remove(username);
            }
            
            counter++;
            return;
        }
        
        // cape from current player.
        ICape cape = currentPlayerCapes.get(username);
        
        if (cape == null || capeChangeQue.contains(username))
        {
            cape = instance.getCapePack().getCape(capeName);
            
            // if cape still null, increment and return.
            if (cape == null)
            {
                counter++;
                return;
            }
            // change cape in map.
            currentPlayerCapes.put(username, cape);
            setPlayerCape(cape, player);
        }
        else if (cape instanceof AnimatedCape)
        {
            ((AnimatedCape) cape).update(mc.timer.elapsedPartialTicks);
            setPlayerCape(cape, player);
        }
        
        capeChangeQue.remove(username);
        
        // if not on the server the cape pack is from, set back to normal cape.
        if (!instance.getCurrentServerAddress().equals(instance.getCapePack().getServerAddress()))
        {
            setDefaultPlayerCape(player);
        }
        
        // increment counter.
        counter++;
    }
    
    private void setPlayerCape(ICape cape, AbstractClientPlayer player)
    {
        if (debug)
        {
            System.out.println("Changing the cape of: " + player.username);
        }
        
        player.locationCape = cape.getCapeResource();
        player.downloadImageCape = cape.getCapeData();
        cape.loadTexture();
        
        String username = StringUtils.stripControlCodes(player.username);
        currentPlayerCapes.put(username, cape);
    }
    
    private void setDefaultPlayerCape(AbstractClientPlayer player)
    {
        player.locationCape = AbstractClientPlayer.getLocationCape(player.username);
        player.downloadImageCape = AbstractClientPlayer.getDownloadImageCape(player.locationCape, player.username);
    }
    
    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        ;
    }
    
    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT);
    }
    
    @Override
    public String getLabel()
    {
        return "RankCapes Cape Handler (tick handler)";
    }
    
}

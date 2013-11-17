package com.jadarstudios.rankcapes.forge.handler;

import java.util.EnumSet;

import com.jadarstudios.rankcapes.forge.gui.GuiCapeSelect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class CapeKeyHandler extends KeyHandler
{
    
    private static final Minecraft mc = Minecraft.getMinecraft();
    
    public CapeKeyHandler(KeyBinding[] keyBindings, boolean[] repeatings)
    {
        super(keyBindings, repeatings);
    }

    @Override
    public String getLabel()
    {
        return "RankCapes";
    }
    
    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
    {
        ;
    }
    
    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
    {
        // calls twice otherwise. 
        if(tickEnd)
            return;
        
        // unlocalized name.
        if(kb.keyDescription.equals("rankcapes.key.1"))
        {
            // if no screen up, open gui.
            if(mc.currentScreen == null)
            {
                System.out.println("Open Screen");
                mc.displayGuiScreen(new GuiCapeSelect());
                return;
            }
            // if our gui is up, close it.
            if(mc.currentScreen instanceof GuiCapeSelect)
            {
//                System.out.println("Close Screen");
                mc.displayGuiScreen(null);
                return;
            }
        }
    }
    
    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT);
    }
    
}

/**
 * RankCapes Forge Mod
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.jadarstudios.rankcapes.forge.gui.GuiCapeSelect;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum KeyEventHandler
{
    INSTANCE;
    
    private static final Minecraft mc = Minecraft.getMinecraft();
    
    private List<KeyBinding> keyBindings;
    
    private KeyEventHandler()
    {
        this.keyBindings = new ArrayList<KeyBinding>();
        this.keyBindings.add(new KeyBinding("rankcapes.key.1", 'c', "key.categories.misc"));
        
        for (KeyBinding binding : this.keyBindings)
            ClientRegistry.registerKeyBinding(binding);
    }
    
    @SubscribeEvent
    public void key(KeyInputEvent event)
    {
        int key = Keyboard.getEventCharacter();
        
        for (KeyBinding binding : this.keyBindings)
            if (key == binding.getKeyCode())
                // if no screen up, open gui.
                if (mc.currentScreen == null)
                {
                    System.out.println("Open Screen");
                    mc.displayGuiScreen(new GuiCapeSelect());
                    return;
                }
                else if (mc.currentScreen instanceof GuiCapeSelect)
                {
                    // System.out.println("Close Screen");
                    mc.displayGuiScreen(null);
                    return;
                }
    }
    
}
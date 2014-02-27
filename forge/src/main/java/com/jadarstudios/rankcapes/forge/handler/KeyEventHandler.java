/**
 * RankCapes Forge Mod
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.handler;

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
public class KeyEventHandler
{
    
    private static final Minecraft mc = Minecraft.getMinecraft();
    
    private KeyBinding keyBinding;
    
    public KeyEventHandler()
    {
        keyBinding = new KeyBinding("rankcapes.key.1", Keyboard.KEY_C, "key.categories.misc");
        ClientRegistry.registerKeyBinding(keyBinding);
    }
    
    @SubscribeEvent
    public void keyUp(KeyInputEvent event)
    {
        char key = Keyboard.getEventCharacter();
        boolean keyState = Keyboard.getEventKeyState();

        if (key == keyBinding.getKeyCode())
        {
            if(!keyState)
            {
                // if no screen up, open gui.
                if (mc.currentScreen == null)
                {
                    System.out.println("Open Screen");
                    mc.displayGuiScreen(new GuiCapeSelect());
                    return;
                }
                // if our gui is up, close it.
                if (mc.currentScreen instanceof GuiCapeSelect)
                {
                    // System.out.println("Close Screen");
                    mc.displayGuiScreen(null);
                    return;
                }
            }
        }
    }
    
}
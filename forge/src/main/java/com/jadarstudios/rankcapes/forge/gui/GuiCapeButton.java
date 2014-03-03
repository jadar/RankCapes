/**
 * RankCapes Forge Mod
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.gui;

import net.minecraft.client.gui.GuiButton;

/**
 * Easiest way to have a id string.
 * 
 * @author Jadar
 */
public class GuiCapeButton extends GuiButton
{
    
    public String capeName;
    
    public GuiCapeButton(int id, int posX, int posY, String capeName)
    {
        this(id, posX, posY, 200, 20, capeName);
    }
    
    public GuiCapeButton(int id, int posX, int posY, int w, int h, String capeName)
    {
        super(id, posX, posY, w, h, capeName);
        this.capeName = capeName;
        this.displayString = Character.toUpperCase(capeName.charAt(0)) + capeName.substring(1);
    }
}

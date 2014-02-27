/**
 * RankCapes Forge Mod
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.jadarstudios.rankcapes.forge.RankCapesForge;
import com.jadarstudios.rankcapes.forge.cape.ICape;
import com.jadarstudios.rankcapes.forge.cape.PlayerCapeProperties;
import com.jadarstudios.rankcapes.forge.network.ClientPacketHandler;

import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * This is the GUI for cape selection.
 * 
 * @author Jadar
 */
public class GuiCapeSelect extends GuiScreen
{
    // constants
    private static final int BUTTONS_PER_PAGE = 5;
    private static final int BUTTON_HEIGHT = 20;
    
    /**
     * The image that is the background.
     */
    private static final ResourceLocation background = new ResourceLocation("rankcapes", "textures/gui/cape.png");
    
    /**
     * The Minecraft instance.
     */
    private static final Minecraft mc = Minecraft.getMinecraft();
    
    /**
     * The width of the GUI image.
     */
    private static final int xSize = 176;
    
    /**
     * The hight of the GUI image.
     */
    private static final int ySize = 166;
    
    /**
     * The top of the GUI relative to the window.
     */
    private int guiTop = 0;
    
    /**
     * The left of the GUI relative to the window.
     */
    private int guiLeft = 0;
    
    private GuiButton buttonSet;
    private GuiButton buttonPrevious;
    private GuiButton buttonNext;
    
    /**
     * The cape the player had before GUI opening.
     */
    private ICape playerCape;
    
    private GuiCapeButton selectedCapeButton = null;
    private int currentPage = 0;
    
    List<GuiButton[]> buttonPages = new ArrayList<GuiButton[]>();
    
    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        // clears the lists.
        buttonList.clear();
        buttonPages.clear();
        
        // calculates the left and top.
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
        
        // create normal buttons.
        buttonSet = new GuiButton(0, guiLeft + 31, guiTop + 138, 30, 20, LanguageRegistry.instance().getStringLocalization("rankcapes.button.set"));
        buttonPrevious = new GuiButton(1, guiLeft + 7, guiTop + 138, 21, 20, "<-");
        buttonNext = new GuiButton(2, guiLeft + 65, guiTop + 138, 21, 20, "->");
        
        // gets the current player cape name and sets it in the instance.
        playerCape = ((PlayerCapeProperties) mc.thePlayer.getExtendedProperties(PlayerCapeProperties.IDENTIFIER)).getCape();
        
        // gets the available capes.
        List<String> availableCapes = RankCapesForge.instance.availableCapes;
        
        // calculates the total pages.
        int totalPages = (int) Math.ceil(availableCapes.size() / (double) BUTTONS_PER_PAGE);

        // assembles the pages.
        for (int pageNumber = 0, capeNumber = 0; pageNumber < totalPages; pageNumber++)
        {
            // make a page
            GuiButton[] buttonPage = new GuiButton[BUTTONS_PER_PAGE];
            
            // assembles a single page
            for (int buttonNumber = 0; buttonNumber < BUTTONS_PER_PAGE; buttonNumber++)
            {
                // for making a default or no cape button.
                if (pageNumber == 0 && buttonNumber == 0)
                {
                    GuiCapeButton noCape = new GuiCapeButton(3, guiLeft + 6, guiTop + 8, 78, BUTTON_HEIGHT, "default");
                    noCape.enabled = false;
                    buttonPage[buttonNumber] = noCape;
                    selectedCapeButton = noCape;
                    buttonNumber++;
                }
                
                // makes sure we wont go over the size of the availableCapes
                // list.
                if (capeNumber < availableCapes.size())
                {
                    // gets the name of the cape.
                    String name = availableCapes.get(capeNumber);
                    
                    // creates the positioned button.
                    GuiCapeButton button = new GuiCapeButton(capeNumber + 4, guiLeft + 6, guiTop + 8 + (BUTTON_HEIGHT + 6) * buttonNumber, 78, BUTTON_HEIGHT, name);
                    
                    // sets button to selectedCapeButton if its name is the same
                    // as the player's cape.
                    if (name.equals(playerCape.getName()))
                    {
                        selectedCapeButton.enabled = true;
                        button.enabled = false;
                        selectedCapeButton = button;
                        gotoPage(pageNumber);
                    }
                    
                    // adds the button to the page.
                    buttonPage[buttonNumber] = button;
                }
                
                // increase cape.
                capeNumber++;
            }
            
            // add page to list.
            buttonPages.add(buttonPage);
        }
        
        // disables set button if no capes are available.
        if (availableCapes.isEmpty())
        {
            buttonSet.enabled = false;
        }
        
        // disables next and previous buttons if there are no pages to go to.
        if (totalPages <= 1)
        {
            buttonNext.enabled = false;
            buttonPrevious.enabled = false;
        }
        
        buttonList.add(buttonSet);
        buttonList.add(buttonNext);
        buttonList.add(buttonPrevious);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float delta)
    {
        // draws the black background
        drawDefaultBackground();
        
        // reset color
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        // set GUI texture
        mc.getTextureManager().bindTexture(background);
        
        // draw GUI texture
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 176, 166);
        
        // new GL matrix
        GL11.glPushMatrix();
        {
            // move matrix into position
            GL11.glTranslatef(guiLeft + 51, guiTop + 114, 130);
            
            // rotate matrix
            GL11.glRotatef(180, 0, 1, 0);
            
            // draw player
            GuiInventory.func_147046_a(-77, -5, 50, mouseX - (guiLeft + 127), (guiTop + 32) - mouseY, mc.thePlayer);
        }
        // end
        GL11.glPopMatrix();
        
        // draws buttons.
        if (buttonPages != null && buttonPages.size() > 0)
        {
            GuiButton[] buttonPage = buttonPages.get(currentPage);
            
            for (GuiButton bttn : buttonPage)
            {
                if (bttn != null)
                {
                    bttn.drawButton(mc, mouseX, mouseY);
                }
            }
        }
        
        // super call.
        super.drawScreen(mouseX, mouseY, delta);
    }
    
    @Override
    protected void mouseClicked(int x, int y, int button)
    {
        // update cape page buttons.
        if (button == 0)
        {
            GuiButton[] capeButtons = buttonPages.get(currentPage);
            
            for (GuiButton guibutton : capeButtons)
            {
                if (guibutton == null)
                {
                    continue;
                }
                
                if (guibutton.mousePressed(mc, x, y))
                {
                    //selectedButton = guibutton;
                    //mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
                    actionPerformed(guibutton);
                }
            }
        }
        
        super.mouseClicked(x, y, button);
    }
    
    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            // set cape
            if (button.id == 0)
            {
                // remove cape if selected button is default.
                if (selectedCapeButton.id == 3)
                {
                    ClientPacketHandler.getInstance().sendCapeRemovePacket();
                }
                // else send packet to server that we want to change.
                else
                {
                    ClientPacketHandler.getInstance().sendCapeChangePacket(selectedCapeButton.capeName);
                }
                
                // close the GUI
                mc.displayGuiScreen(null);
                
                return;
            }
            // previous button
            else if (button.id == 1)
            {
                gotoPage(currentPage - 1);
            }
            // next page
            else if (button.id == 2)
            {
                gotoPage(currentPage + 1);
            }
            // any cape button
            else if (button.id >= 3 && button instanceof GuiCapeButton)
            {
                GuiCapeButton capeButton = (GuiCapeButton) button;
                
                String capeName = capeButton.capeName;
                
                capeButton.enabled = false;
                selectedCapeButton.enabled = true;
                
                RankCapesForge mod = RankCapesForge.instance;
                
                if (button.id != 3)
                {
                    mod.getCapeHandler().setPlayerCape(mod.getCapePack().getCape(capeName), mc.thePlayer);
                }
                else
                {
                    mod.getCapeHandler().resetPlayerCape(mc.thePlayer);
                }
                
                selectedCapeButton = capeButton;
            }
        }
    }
    
    @Override
    public void onGuiClosed()
    {
        // sets player cape back to what is was before.
        if (playerCape != null)
        {
            RankCapesForge.instance.getCapeHandler().setPlayerCape(playerCape, mc.thePlayer);
        }
    }
    
    /**
     * Sets the page to the given integer if it is within the page ranges. 
     * Also sets buttons to enabled or not. 
     * @param pageNumber
     */
    public void gotoPage(int pageNumber)
    {   
        if(pageNumber >= 0 && pageNumber < buttonPages.size())
        {
            System.out.println(pageNumber);
            currentPage = pageNumber;
            if(pageNumber == 0)
            {
                buttonPrevious.enabled = false;
                buttonNext.enabled = true;
            }
            else if(pageNumber == buttonPages.size()-1)
            {
                buttonPrevious.enabled = true;
                buttonNext.enabled = false;
            }
            else
            {
                buttonPrevious.enabled = true;
                buttonNext.enabled = true;
            }
        }
    }
}

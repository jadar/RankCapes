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
import com.jadarstudios.rankcapes.forge.cape.AbstractCape;
import com.jadarstudios.rankcapes.forge.cape.PlayerCapeProperties;
import com.jadarstudios.rankcapes.forge.handler.CapeHandler;
import com.jadarstudios.rankcapes.forge.handler.KeyEventHandler;
import com.jadarstudios.rankcapes.forge.network.ClientPacketHandler;
import com.jadarstudios.rankcapes.forge.network.packet.C4PacketUpdateCape;
import com.jadarstudios.rankcapes.forge.network.packet.C4PacketUpdateCape.Type;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

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
    private AbstractCape playerCape;
    
    private GuiCapeButton selectedCapeButton = null;
    private int currentPage = 0;
    
    List<GuiButton[]> buttonPages = new ArrayList<GuiButton[]>();
    
    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        // clears the lists.
        this.buttonList.clear();
        this.buttonPages.clear();
        
        // calculates the left and top.
        this.guiLeft = (this.width - xSize) / 2;
        this.guiTop = (this.height - ySize) / 2;
        
        // create normal buttons.
        this.buttonSet = new GuiButton(0, this.guiLeft + 31, this.guiTop + 138, 30, 20, LanguageRegistry.instance().getStringLocalization("rankcapes.button.set"));
        this.buttonPrevious = new GuiButton(1, this.guiLeft + 7, this.guiTop + 138, 21, 20, "<-");
        this.buttonNext = new GuiButton(2, this.guiLeft + 65, this.guiTop + 138, 21, 20, "->");
        
        // gets the current player cape name and sets it in the instance.
        this.playerCape = ((PlayerCapeProperties) mc.thePlayer.getExtendedProperties(PlayerCapeProperties.IDENTIFIER)).getCape();
        
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
                    GuiCapeButton noCape = new GuiCapeButton(3, this.guiLeft + 6, this.guiTop + 8, 78, BUTTON_HEIGHT, "default");
                    noCape.enabled = false;
                    buttonPage[buttonNumber] = noCape;
                    this.selectedCapeButton = noCape;
                    buttonNumber++;
                }
                
                // makes sure we wont go over the size of the availableCapes
                // list.
                if (capeNumber < availableCapes.size())
                {
                    // gets the name of the cape.
                    String name = availableCapes.get(capeNumber);
                    
                    // creates the positioned button.
                    GuiCapeButton button = new GuiCapeButton(capeNumber + 4, this.guiLeft + 6, this.guiTop + 8 + (BUTTON_HEIGHT + 6) * buttonNumber, 78, BUTTON_HEIGHT, name);
                    
                    // sets button to selectedCapeButton if its name is the same
                    // as the player's cape.
                    if (this.playerCape != null && name.equals(this.playerCape.getName()))
                    {
                        this.selectedCapeButton.enabled = true;
                        button.enabled = false;
                        
                        this.selectedCapeButton = button;
                        this.currentPage = pageNumber;
                    }
                    
                    // adds the button to the page.
                    buttonPage[buttonNumber] = button;
                }
                
                // increase cape.
                capeNumber++;
            }
            
            // add page to list.
            this.buttonPages.add(buttonPage);
            this.gotoPage(this.currentPage);
        }
        
        // disables set button if no capes are available.
        if (availableCapes.isEmpty())
            this.buttonSet.enabled = false;
        
        // disables next and previous buttons if there are no pages to go to.
        if (totalPages <= 1)
        {
            this.buttonNext.enabled = false;
            this.buttonPrevious.enabled = false;
        }
        
        this.buttonList.add(this.buttonSet);
        this.buttonList.add(this.buttonNext);
        this.buttonList.add(this.buttonPrevious);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float delta)
    {
        // draws the black background
        this.drawDefaultBackground();
        
        // reset color
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        // set GUI texture
        mc.getTextureManager().bindTexture(background);
        
        // draw GUI texture
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 176, 166);
        
        // new GL matrix
        GL11.glPushMatrix();
        {
            // move matrix into position
            GL11.glTranslatef(this.guiLeft + 51, this.guiTop + 114, 130);
            
            // rotate matrix
            GL11.glRotatef(180, 0, 1, 0);
            
            // draw player
            GuiInventory.func_147046_a(-77, -5, 50, mouseX - (this.guiLeft + 127), this.guiTop + 32 - mouseY, mc.thePlayer);
        }
        // end
        GL11.glPopMatrix();
        
        // draws buttons.
        if (this.buttonPages != null && this.buttonPages.size() > 0)
        {
            GuiButton[] buttonPage = this.buttonPages.get(this.currentPage);
            
            for (GuiButton bttn : buttonPage)
                if (bttn != null)
                    bttn.drawButton(mc, mouseX, mouseY);
        }
        
        // super call.
        super.drawScreen(mouseX, mouseY, delta);
    }
    
    @Override
    protected void mouseClicked(int x, int y, int button)
    {
        // update cape page buttons.
        if (button == 0)
            if (this.buttonPages != null && this.buttonPages.size() > 0)
            {
                GuiButton[] capeButtons = this.buttonPages.get(this.currentPage);
                
                for (GuiButton guibutton : capeButtons)
                {
                    if (guibutton == null)
                        continue;
                    
                    if (guibutton.mousePressed(mc, x, y))
                    {
                        // selectedButton = guibutton;
                        guibutton.func_146113_a(mc.getSoundHandler());
                        this.actionPerformed(guibutton);
                    }
                }
            }
        
        super.mouseClicked(x, y, button);
    }
    
    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
            // set cape
            if (button.id == 0)
            {
                // remove cape if selected button is default.
                if (this.selectedCapeButton.id == 3)
                {
                    System.out.println("sending remove packet");
                    C4PacketUpdateCape packet = new C4PacketUpdateCape(Type.REMOVE);
                    ClientPacketHandler.instance().sendPacketToServer(packet);
                }
                // else send packet to server that we want to change.
                else
                {
                    C4PacketUpdateCape packet = new C4PacketUpdateCape(this.selectedCapeButton.capeName);
                    ClientPacketHandler.instance().sendPacketToServer(packet);
                }
                
                // close the GUI
                mc.displayGuiScreen(null);
                
                return;
            }
            // previous button
            else if (button.id == 1)
                this.gotoPage(this.currentPage - 1);
            else if (button.id == 2)
                this.gotoPage(this.currentPage + 1);
            else if (button instanceof GuiCapeButton)
            {
                GuiCapeButton capeButton = (GuiCapeButton) button;
                String capeName = capeButton.capeName;
                
                capeButton.enabled = false;
                this.selectedCapeButton.enabled = true;
                
                RankCapesForge mod = RankCapesForge.instance;
                
                if (button.id != 3)
                {
                    AbstractCape cape = mod.getCapePack().getCape(capeName);
                    CapeHandler.INSTANCE.setPlayerCape(cape, mc.thePlayer);
                }
                else
                    CapeHandler.INSTANCE.resetPlayerCape(mc.thePlayer);
                
                this.selectedCapeButton = capeButton;
            }
    }
    
    @Override
    public void onGuiClosed()
    {
        // sets player cape back to what is was before.
        if (this.playerCape != null)
            CapeHandler.INSTANCE.setPlayerCape(this.playerCape, mc.thePlayer);
    }
    
    @Override
    public void keyTyped(char keyChar, int key)
    {
        KeyEventHandler.INSTANCE.key(new KeyInputEvent());
        super.keyTyped(keyChar, key);
    }
    
    /**
     * Sets the page to the given integer if it is within the page ranges.
     * Also sets buttons to enabled or not.
     * 
     * @param pageNumber
     */
    public void gotoPage(int pageNumber)
    {
        if (pageNumber >= 0 && pageNumber < this.buttonPages.size())
        {
            this.currentPage = pageNumber;
            if (pageNumber == 0)
            {
                this.buttonPrevious.enabled = false;
                this.buttonNext.enabled = true;
            }
            else if (pageNumber == this.buttonPages.size() - 1)
            {
                this.buttonPrevious.enabled = true;
                this.buttonNext.enabled = false;
            }
            else
            {
                this.buttonPrevious.enabled = true;
                this.buttonNext.enabled = true;
            }
        }
    }
}

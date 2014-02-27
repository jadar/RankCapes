/**
 * RankCapes Forge Mod
 * 
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.cape;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.ITextureObject;

/**
 * This class implements Animated Capes. It keeps track of which frame it's on
 * and contains all the other frames in the form of StaticCapes.
 * 
 * @author Jadar
 */
public class AnimatedCape implements ICape
{
    
    List<StaticCape> capeFrames;
    
    int framesPerSecond = 0;
    int currentFrame = 0;
    
    // elapsed time in ticks.
    long elapsedTime = 0;
    // time since update in ticks.
    long lastElapsedTime = 0;
    
    boolean pause = false;
    
    protected String name;
    
    public AnimatedCape(String name)
    {
        capeFrames = new ArrayList<StaticCape>();
        this.name = name;
    }
    
    public AnimatedCape(String name, int parFramesPerSecond)
    {
        this(name);
        framesPerSecond = parFramesPerSecond;
    }
    
    @Override
    public ITextureObject getCapeTexture()
    {
        return getCurrentFrame().getCapeTexture();
    }
    
    @Override
    public void loadTexture(AbstractClientPlayer player)
    {
        getCurrentFrame().loadTexture(player);
    }
    
    public StaticCape getCurrentFrame()
    {
        return capeFrames.get(currentFrame);
    }
    
    public int addFrame(StaticCape frame)
    {
        capeFrames.add(frame);
        return capeFrames.size();
    }
    
    public void addFrame(StaticCape frame, int position)
    {
        capeFrames.add(position, frame);
    }
    
    public int getTotalFrames()
    {
        return capeFrames.size();
    }
    
    public void setFPS(int parFramesPerSecond)
    {
        framesPerSecond = parFramesPerSecond;
    }
    
    public int getFPS()
    {
        return framesPerSecond;
    }
    
    @Override
    public String getName()
    {
        return this.name;
    }
    
    public void update()
    {
        if (pause)
            return;
        
        this.elapsedTime = Minecraft.getSystemTime();
        
        // time since update is one tick + time between tick.
        long delta = elapsedTime - lastElapsedTime;
        
        if (delta >= 1 / framesPerSecond)
        {
            currentFrame++;
            lastElapsedTime = elapsedTime;
        }

        if (currentFrame > getTotalFrames() - 1)
        {
            currentFrame = 0;
        }
    }
    
    @Override
    public String toString()
    {
        return String.format("Animated cape with %s frames at %s FPS.", getTotalFrames(), framesPerSecond);
    }
}

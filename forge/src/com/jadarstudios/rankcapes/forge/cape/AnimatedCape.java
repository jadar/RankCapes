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

import net.minecraft.util.ResourceLocation;

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
    float elapsedTime = 0;
    // time since update in ticks.
    float timeSinceUpdate = 0;
    
    boolean pause = false;
    
    public AnimatedCape()
    {
        capeFrames = new ArrayList<StaticCape>();
    }
    
    public AnimatedCape(int parFramesPerSecond)
    {
        this();
        framesPerSecond = parFramesPerSecond;
    }
    
    @Override
    public LoadCapeData getCapeData()
    {
        return getCurrentFrame().getCapeData();
    }
    
    @Override
    public ResourceLocation getCapeResource()
    {
        return getCurrentFrame().getCapeResource();
    }
    
    @Override
    public void loadTexture()
    {
        getCurrentFrame().loadTexture();
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
    
    public void setFramesPerSecond(int parFramesPerSecond)
    {
        framesPerSecond = parFramesPerSecond;
    }
    
    public void update(float deltaTime)
    {
        if (pause)
            return;
        
        // time since update is one tick + time between tick.
        timeSinceUpdate += 1 + deltaTime;
        
        if (timeSinceUpdate / 20 >= 1 / framesPerSecond)
        {
            currentFrame++;
            timeSinceUpdate = 0;
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

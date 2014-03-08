/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.cape;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements Animated Capes. It keeps track of which frame it's on
 * and contains all the other frames in the form of StaticCapes.
 *
 * @author Jadar
 */
public class AnimatedCape extends AbstractCape
{

    List<StaticCape> capeFrames;

    int framesPerSecond = 0;
    int currentFrame = 0;

    // elapsed time in milliseconds.
    long elapsedTime = 0;
    long previousElapsedTime = 0;

    public boolean pause = false;

    protected String name;

    public AnimatedCape(String name)
    {
        this.capeFrames = new ArrayList<StaticCape>();
        this.name = name;
    }

    public AnimatedCape(String name, int parFramesPerSecond)
    {
        this(name);
        this.framesPerSecond = parFramesPerSecond;
    }

    @Override
    public BufferedImage getCapeTexture()
    {
        return this.getCurrentFrame().getCapeTexture();
    }

    @Override
    public void loadTexture(AbstractClientPlayer player)
    {
        this.getCurrentFrame().loadTexture(player);
    }

    public StaticCape getCurrentFrame()
    {
        return this.capeFrames.get(this.currentFrame);
    }

    public int addFrame(StaticCape frame)
    {
        this.capeFrames.add(frame);
        return this.capeFrames.size();
    }

    public int getTotalFrames()
    {
        return this.capeFrames.size();
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public boolean update()
    {
        if (this.pause)
        {
            return false;
        }

        boolean flag = false;
        this.elapsedTime = Minecraft.getSystemTime();

        // time since update is one tick + time between tick.
        long delta = this.elapsedTime - this.previousElapsedTime;

        if (delta >= (1 / this.framesPerSecond) * 1000)
        {
            flag = true;
            this.currentFrame++;
            this.previousElapsedTime = this.elapsedTime;
        }

        if (this.currentFrame > this.getTotalFrames() - 1)
        {
            this.currentFrame = 0;
        }

        return flag;
    }

    @Override
    public String toString()
    {
        return String.format("Animated cape with %s frames at %s FPS.", this.getTotalFrames(), this.framesPerSecond);
    }
}

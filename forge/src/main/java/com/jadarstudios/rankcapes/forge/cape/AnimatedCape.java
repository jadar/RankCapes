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
 * This class is an Animated cape. It contains all the other frames in the form of {@link StaticCape} instances and
 * keeps track of which frame it's on.
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

    protected String name = "";

    /**
     * <u>Only</u> animate the cape when moving.
     */
    boolean animateWhenMoving = false;

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

    public AnimatedCape(String name, int framesPerSecond, boolean animateWhenMoving)
    {
        this(name, framesPerSecond);
        this.animateWhenMoving = animateWhenMoving;
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

    @Override
    public String getName()
    {
        return this.name;
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

    public boolean animateWhenMoving()
    {
        return this.animateWhenMoving;
    }

    /**
     * Updated the cape.
     *
     * @return if the texture was changed.
     */
    public boolean update()
    {
        boolean flag = false;
        this.elapsedTime = Minecraft.getSystemTime();

        // time since update is one tick + time between tick.
        long delta = this.elapsedTime - this.previousElapsedTime;

        if (delta >= (1000 / this.framesPerSecond))
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

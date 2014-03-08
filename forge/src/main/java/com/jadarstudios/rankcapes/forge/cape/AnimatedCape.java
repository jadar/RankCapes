/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.cape;

import com.jadarstudios.rankcapes.forge.RankCapesForge;
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

    protected String name = "";

    boolean onlyAnimateWhenMoving = false;

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

    public AnimatedCape(String name, int framesPerSecond, boolean onlyAnimateWhenMoving)
    {
        this(name, framesPerSecond);
        this.onlyAnimateWhenMoving = onlyAnimateWhenMoving;
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
        boolean flag = false;
        this.elapsedTime = Minecraft.getSystemTime();

        // time since update is one tick + time between tick.
        long delta = this.elapsedTime - this.previousElapsedTime;

        if (delta >= (1000 / this.framesPerSecond))
        {
            flag = true;
            this.currentFrame++;
            this.previousElapsedTime = this.elapsedTime;
            RankCapesForge.log.info(this.currentFrame);
        }

        if (this.currentFrame > this.getTotalFrames() - 1)
        {
            this.currentFrame = 0;
        }

        return flag;
    }

    public boolean onlyAnimateWhenMoving()
    {
        return this.onlyAnimateWhenMoving;
    }

    @Override
    public String toString()
    {
        return String.format("Animated cape with %s frames at %s FPS.", this.getTotalFrames(), this.framesPerSecond);
    }
}

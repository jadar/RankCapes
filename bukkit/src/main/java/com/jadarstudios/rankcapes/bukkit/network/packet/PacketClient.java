/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.network.packet;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * A packet from client to server. Only meant to be read from.
 *
 * @author Jadar
 */
public abstract class PacketClient extends PacketBase
{

    // "implemets" this so that child classes dont need to.
    @Override
    public final void write(ByteBuffer data) throws BufferUnderflowException {}

    // "implement" this so child classes don't have to.
    @Override
    public final int getSize()
    {
        return 0;
    }
}

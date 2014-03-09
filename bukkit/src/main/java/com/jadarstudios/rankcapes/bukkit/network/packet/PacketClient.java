/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit.network.packet;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

/**
 * A packet from server to client. Only meant to be written to.
 *
 * @author Jadar
 */
public abstract class PacketClient extends PacketBase
{

    // "implement" this so child classes don't have to.
    @Override
    public final void read(ByteBuffer data) throws BufferOverflowException, ReadOnlyBufferException
    {
    }
}

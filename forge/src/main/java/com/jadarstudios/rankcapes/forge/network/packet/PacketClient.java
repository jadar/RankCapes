/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.network.packet;

import io.netty.buffer.ByteBuf;

/**
 * This class is a packet from server to client. Only meant to be read from.
 *
 * @author Jadar
 */
public abstract class PacketClient extends PacketBase
{

    // "implement" this so child classes don't have to.
    @Override
    public final void write(ByteBuf data)
    {
    }

}

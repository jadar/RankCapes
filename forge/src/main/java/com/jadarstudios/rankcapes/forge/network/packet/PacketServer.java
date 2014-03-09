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
 * A packet from client to server. Only meant to be written to.
 *
 * @author Jadar
 */
public abstract class PacketServer extends PacketBase
{

    // "implemets" this so that child classes dont need to.
    @Override
    public final void read(ByteBuf data)
    {
    }
}

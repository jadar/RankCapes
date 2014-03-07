/**
 * RankCapes Forge Mod
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.forge.network.packet;

import io.netty.buffer.ByteBuf;

public abstract class PacketClient extends PacketBase
{

    // "implement" this so child classes don't have to.
    @Override
    public final void read(ByteBuf data)
    {
    }

}

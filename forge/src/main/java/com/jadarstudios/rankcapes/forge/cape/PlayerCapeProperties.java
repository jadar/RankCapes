package com.jadarstudios.rankcapes.forge.cape;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerCapeProperties implements IExtendedEntityProperties
{
    
    public static final String IDENTIFIER = "rankcapes_data";
    
    AbstractCape playerCape;
    
    AbstractClientPlayer player;
    
    public static PlayerCapeProperties forPlayer(AbstractClientPlayer player)
    {
        return (PlayerCapeProperties) player.getExtendedProperties(IDENTIFIER);
    }
    
    public PlayerCapeProperties(AbstractClientPlayer player)
    {
        this.player = player;
    }
    
    @Override
    public void init(Entity entity, World world)
    {
    }
    
    public void setCape(AbstractCape cape)
    {
        this.playerCape = cape;
    }
    
    public AbstractCape getCape()
    {
        return this.playerCape;
    }
    
    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
    }
    
    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
    }
    
}

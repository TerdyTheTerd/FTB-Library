package com.feed_the_beast.ftbl.api;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbl.util.LMNBTUtils;
import com.mojang.authlib.GameProfile;
import latmod.lib.LMUtils;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 09.02.2016.
 */
@SideOnly(Side.CLIENT)
public final class ForgeWorldSP extends ForgeWorld
{
	public static ForgeWorldSP inst = null;
	public final ForgePlayerSPSelf clientPlayer;
	public List<String> serverDataIDs;
	
	public ForgeWorldSP(GameProfile p)
	{
		super(Side.CLIENT);
		clientPlayer = new ForgePlayerSPSelf(p);
		serverDataIDs = new ArrayList<>();
	}
	
	@Override
	public World getMCWorld()
	{ return FTBLibClient.mc.theWorld; }
	
	@Override
	public ForgeWorldMP toWorldMP()
	{ return null; }
	
	@Override
	public ForgeWorldSP toWorldSP()
	{ return this; }
	
	@Override
	public ForgePlayerSP getPlayer(Object o)
	{
		ForgePlayer p = super.getPlayer(o);
		return (p == null) ? null : p.toPlayerSP();
	}
	
	public void setModeRaw(String mode)
	{
		currentMode = new GameMode(mode);
	}
	
	public void readDataFromNet(NBTTagCompound tag, boolean login)
	{
		worldID = new UUID(tag.getLong("IDM"), tag.getLong("IDL"));
		currentMode = new GameMode(tag.getString("M"));
		
		if(login)
		{
			playerMap.clear();
			
			GameProfile gp = FTBLibClient.mc.getSession().getProfile();
			//TODO: Improve this
			clientPlayer.init();
			
			NBTTagCompound tag1 = tag.getCompoundTag("PM");
			
			for(String s : tag1.getKeySet())
			{
				UUID uuid = LMUtils.fromString(s);
				String name = tag1.getString(s);
				
				if(uuid.equals(clientPlayer.getProfile().getId())) { playerMap.put(uuid, clientPlayer); }
				else
				{
					ForgePlayerSP p = new ForgePlayerSP(new GameProfile(uuid, name));
					p.init();
					playerMap.put(uuid, p);
				}
			}
			
			FTBLib.dev_logger.info("Client player ID: " + clientPlayer.getProfile().getId() + " and " + (playerMap.size() - 1) + " other players");
			
			Map<String, NBTBase> map1 = LMNBTUtils.toMap(tag.getCompoundTag("PMD"));
			
			clientPlayer.readFromNet((NBTTagCompound) map1.get(clientPlayer.getStringUUID()), true);
			map1.remove(clientPlayer.getStringUUID());
			
			for(Map.Entry<String, NBTBase> e : map1.entrySet())
			{
				ForgePlayerSP p = playerMap.get(LMUtils.fromString(e.getKey())).toPlayerSP();
				p.readFromNet((NBTTagCompound) e.getValue(), false);
			}
		}
		
		serverDataIDs.clear();
		
		if(tag.hasKey("SFWD"))
		{
			NBTTagCompound tag1 = tag.getCompoundTag("SFWD");
			serverDataIDs.addAll(tag1.getKeySet());
			
			for(String s : serverDataIDs)
			{
				if(customData.containsKey(s))
				{
					NBTTagCompound tag2 = tag1.getCompoundTag(s);
					customData.get(s).readFromNet(tag2, login);
				}
			}
		}
		
		//customCommonData.read(io);
	}
}
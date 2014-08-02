/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public enum Chromabilities {

	REACH(),
	MAGNET();

	private static final String NBT_TAG = "chromabilities";
	private static final HashMap<String, Chromabilities> tagMap = new HashMap();

	public static final Chromabilities[] abilities = values();

	public void apply(EntityPlayer ep) {
		switch(this) {
		case REACH:
			this.setReachDistance(ep, 128);
			break;
		case MAGNET:
			this.attractItemsAndXP(ep, 24);
			break;
		default:
			break;
		}
	}

	public static ArrayList<Chromabilities> getFrom(EntityPlayer ep) {
		ArrayList<Chromabilities> li = new ArrayList();
		NBTTagCompound nbt = ep.getEntityData();
		NBTTagCompound abilities = nbt.getCompoundTag(NBT_TAG);
		if (abilities != null && !abilities.hasNoTags()) {
			Iterator<NBTBase> it = abilities.getTags().iterator();
			while (it.hasNext()) {
				NBTBase tag = it.next();
				String n = tag.getName();
				if (abilities.getBoolean(n)) {
					Chromabilities c = tagMap.get(n);
					li.add(c);
				}
			}
		}
		return li;
	}

	public void setToPlayer(EntityPlayer ep, boolean set) {
		NBTTagCompound nbt = ep.getEntityData();
		NBTTagCompound abilities = nbt.getCompoundTag(NBT_TAG);
		if (abilities == null) {
			abilities = new NBTTagCompound();
		}
		abilities.setBoolean(this.getNBTName(), set);
		nbt.setTag(NBT_TAG, abilities);
	}

	private String getNBTName() {
		return this.name().toLowerCase();
	}

	public boolean enabledOn(EntityPlayer ep) {
		NBTTagCompound nbt = ep.getEntityData();
		NBTTagCompound abilities = nbt.getCompoundTag(NBT_TAG);
		return abilities != null && abilities.getBoolean(this.getNBTName());
	}

	private static void attractItemsAndXP(EntityPlayer ep, int range) {
		World world = ep.worldObj;
		double x = ep.posX;
		double y = ep.posY+1.5;
		double z = ep.posZ;
		AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x, y, z).expand(range, range, range);
		List inbox = world.getEntitiesWithinAABB(EntityItem.class, box);
		for (int i = 0; i < inbox.size(); i++) {
			EntityItem ent = (EntityItem)inbox.get(i);
			ReikaEntityHelper.setInvulnerable(ent, true);
			if (ent.delayBeforeCanPickup == 0) {
				double dx = (x+0.5 - ent.posX);
				double dy = (y+0.5 - ent.posY);
				double dz = (z+0.5 - ent.posZ);
				double ddt = ReikaMathLibrary.py3d(dx, dy, dz);
				if (ReikaMathLibrary.py3d(dx, 0, dz) < 1) {
					ent.onCollideWithPlayer(ep);
				}
				else {
					ent.motionX += dx/ddt/ddt/1;
					ent.motionY += dy/ddt/ddt/2;
					ent.motionZ += dz/ddt/ddt/1;
					if (ent.posY < y)
						ent.motionY += 0.125;
					if (!world.isRemote)
						ent.velocityChanged = true;
				}
			}
		}
		List inbox2 = world.getEntitiesWithinAABB(EntityXPOrb.class, box);
		for (int i = 0; i < inbox2.size(); i++) {
			EntityXPOrb ent = (EntityXPOrb)inbox2.get(i);
			ReikaEntityHelper.setInvulnerable(ent, true);
			double dx = (x+0.5 - ent.posX);
			double dy = (y+0.5 - ent.posY);
			double dz = (z+0.5 - ent.posZ);
			double ddt = ReikaMathLibrary.py3d(dx, dy, dz);
			if (ReikaMathLibrary.py3d(dx, 0, dz) < 1) {
				ent.onCollideWithPlayer(ep);
			}
			else {
				ent.motionX += dx/ddt/ddt/2;
				ent.motionY += dy/ddt/ddt/2;
				ent.motionZ += dz/ddt/ddt/2;
				if (ent.posY < y)
					ent.motionY += 0.1;
				if (!world.isRemote)
					ent.velocityChanged = true;
			}
		}
	}

	private static void setReachDistance(EntityPlayer ep, int dist) {
		if (!ep.worldObj.isRemote && ep instanceof EntityPlayerMP) {
			((EntityPlayerMP)ep).theItemInWorldManager.setBlockReachDistance(dist);
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.REACH.ordinal(), ep, dist);
		}
	}

	static {
		for (int i = 0; i < abilities.length; i++) {
			Chromabilities c = abilities[i];
			tagMap.put(c.getNBTName(), c);
		}
	}
}

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

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Instantiable.MultiBlockBlueprint;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockBox;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public enum Chromabilities {

	REACH(true),
	MAGNET(true),
	SONIC(false),
	SHIFT(false);

	public final boolean tickBased;

	private Chromabilities(boolean tick) {
		tickBased = tick;
	}

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

	public void trigger(EntityPlayer ep, int... data) {
		switch(this) {
		case SONIC:
			this.destroyBlocksAround(ep, data[0]);
			break;
		case SHIFT:
			BlockBox box = new BlockBox(data[0], data[1], data[2], data[3], data[4], data[5]);
			this.shiftArea(ep.worldObj, box, ForgeDirection.VALID_DIRECTIONS[data[6]], data[7]);
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
			Iterator<String> it = abilities.func_150296_c().iterator();
			while (it.hasNext()) {
				String n = it.next();
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
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(range, range, range);
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

	private static void setReachDistance(EntityPlayer player, int dist) {
		if (!player.worldObj.isRemote && player instanceof EntityPlayerMP) {
			EntityPlayerMP ep = (EntityPlayerMP)player;
			ep.theItemInWorldManager.setBlockReachDistance(dist);
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.REACH.ordinal(), ep, dist);
		}
	}

	private static void destroyBlocksAround(EntityPlayer ep, int power) {
		int x = MathHelper.floor_double(ep.posX);
		int y = MathHelper.floor_double(ep.posY)+1;
		int z = MathHelper.floor_double(ep.posZ);
		int r = power;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					Block b = ep.worldObj.getBlock(dx, dy, dz);
					if (b != Blocks.air && b.isOpaqueCube()) {
						if (power > b.blockResistance/6F) {
							b.dropBlockAsItem(ep.worldObj, dx, dy, dz, ep.worldObj.getBlockMetadata(dx, dy, dz), 0);
							ep.worldObj.setBlockToAir(dx, dy, dz);
						}
					}
				}
			}
		}

	}

	private static void shiftArea(World world, BlockBox box, ForgeDirection dir, int dist) {
		MultiBlockBlueprint moved = new MultiBlockBlueprint(box.getSizeX(), box.getSizeY(), box.getSizeZ());
		BlockArray toDel = new BlockArray();
		toDel.setWorld(world);
		for (int i = 0; i <= box.getSizeX(); i++) {
			for (int j = 0; j <= box.getSizeY(); j++) {
				for (int k = 0; k <= box.getSizeZ(); k++) {
					int x = i+box.minY;;
					int y = j+box.minY;
					int z = k+box.minZ;
					int dx = x+dir.offsetX*dist;
					int dy = y+dir.offsetY*dist;
					int dz = z+dir.offsetZ*dist;
					Block b = world.getBlock(x, y, z);
					int meta = world.getBlockMetadata(x, y, z);
					if (!b.hasTileEntity(meta)) {
						//if (ReikaWorldHelper.softBlocks(world, dx, dy, dz)) {
						moved.addBlockAt(i, j, k, b, meta);
						toDel.addBlockCoordinate(x, y, z);
						//}
					}
				}
			}
		}
		int x0 = box.minX+dir.offsetX*dist;
		int y0 = box.minY+dir.offsetY*dist;
		int z0 = box.minZ+dir.offsetZ*dist;
		toDel.clearArea();
		moved.createInWorld(world, x0, y0, z0);
	}

	static {
		for (int i = 0; i < abilities.length; i++) {
			Chromabilities c = abilities[i];
			tagMap.put(c.getNBTName(), c);
		}
	}
}
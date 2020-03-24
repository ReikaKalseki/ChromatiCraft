/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityGuardianStone;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

public final class ProtectionZone {

	public final UUID creator;
	public final int dimensionID;
	public final int originX;
	public final int originY;
	public final int originZ;
	public final int range;

	public ProtectionZone(World world, EntityPlayer ep, int x, int y, int z, int r) {
		this(ep.getUniqueID(), world.provider.dimensionId, x, y, z, r);
	}

	private ProtectionZone(UUID player, int id, int x, int y, int z, int r) {
		creator = player;
		dimensionID = id;
		originX = x;
		originY = y;
		originZ = z;
		range = r;
	}

	public boolean hasTile(World world) {
		return ChromaTiles.getTile(world, originX, originY, originZ) == ChromaTiles.GUARDIAN;
	}

	public boolean canPlayerEditIn(EntityPlayer ep) {
		if (ep.getUniqueID().equals(creator))
			return true;
		return this.isPlayerOnAuxList(ep);
	}

	private boolean isPlayerOnAuxList(EntityPlayer ep) {
		TileEntityGuardianStone te = this.getControllingGuardianStone();
		return te != null ? te.isPlayerInList(ep) : false;
	}

	public boolean isBlockInZone(int x, int y, int z) {
		//double dd = ReikaMathLibrary.py3d(x-originX, y-originY, z-originZ);
		//return dd <= range+0.5;
		return Math.abs(x-originX) <= range && Math.abs(z-originZ) <= range && (ChromaOptions.GUARDCHUNK.getState() || Math.abs(y-originY) <= range);
	}

	@Override
	public String toString() {
		return "Zone by "+creator.toString()+" in world "+dimensionID+" at "+originX+", "+originY+", "+originZ+" (Radius "+range+")";
	}

	public TileEntityGuardianStone getControllingGuardianStone() {
		World world = DimensionManager.getWorld(dimensionID);
		int x = originX;
		int y = originY;
		int z = originZ;
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		if (c != ChromaTiles.GUARDIAN)
			return null;
		TileEntity te = world.getTileEntity(x, y, z);
		return (TileEntityGuardianStone)te;
	}

	public String getSerialString() {
		StringBuilder sb = new StringBuilder();
		sb.append("P:");
		sb.append(creator.toString());
		sb.append(";");

		sb.append("W:");
		sb.append(String.valueOf(dimensionID));
		sb.append(";");

		sb.append("X:");
		sb.append(String.valueOf(originX));
		sb.append(";");

		sb.append("Y:");
		sb.append(String.valueOf(originY));
		sb.append(";");

		sb.append("Z:");
		sb.append(String.valueOf(originZ));
		sb.append(";");

		sb.append("R:");
		sb.append(String.valueOf(range));
		return sb.toString();
	}

	protected static ProtectionZone getFromSerialString(String sg) {
		try {
			String[] s = sg.split(";");
			if (s == null || s.length != 6)
				return null;
			for (int i = 0; i < 6; i++)
				s[i] = s[i].substring(2);
			int w = Integer.parseInt(s[1]);
			int x = Integer.parseInt(s[2]);
			int y = Integer.parseInt(s[3]);
			int z = Integer.parseInt(s[4]);
			int r = Integer.parseInt(s[5]);
			return new ProtectionZone(UUID.fromString(s[0]), w, x, y, z, r);
		}
		catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ProtectionZone) {
			ProtectionZone p = (ProtectionZone)o;
			if (!creator.equals(p.creator))
				return false;
			if (p.dimensionID != dimensionID)
				return false;
			if (p.range != range)
				return false;
			if (p.originX != originX)
				return false;
			if (p.originY != originY)
				return false;
			if (p.originZ != originZ)
				return false;
			return true;
		}
		return false;
	}

	public static ProtectionZone readFromNBT(NBTTagCompound NBT) {
		String owner = NBT.getString("owner");
		WorldLocation loc = WorldLocation.readFromNBT("loc", NBT);
		return new ProtectionZone(UUID.fromString(owner), loc.dimensionID, loc.xCoord, loc.yCoord, loc.zCoord, NBT.getInteger("radius"));
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound NBT = new NBTTagCompound();
		new WorldLocation(dimensionID, originX, originY, originZ).writeToNBT("loc", NBT);
		NBT.setString("owner", creator.toString());
		NBT.setInteger("radius", range);
		return NBT;
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import net.minecraft.nbt.NBTTagCompound;

import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Network.PylonFinder;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

public class CrystalTarget {

	public final WorldLocation source;
	public final WorldLocation location;
	public final CrystalElement color;
	public final double endWidth;
	public final double offsetX;
	public final double offsetY;
	public final double offsetZ;

	public CrystalTarget(WorldLocation src, WorldLocation target, CrystalElement color, double w) {
		this(src, target, color, 0, 0, 0, w);
	}

	public CrystalTarget(CrystalNetworkTile src, WorldLocation target, CrystalElement color, double w) {
		this(PylonFinder.getLocation(src), target, color, 0, 0, 0, w);
	}

	public CrystalTarget(CrystalNetworkTile src, WorldLocation target, CrystalElement color, double dx, double dy, double dz, double w) {
		this(PylonFinder.getLocation(src), target, color, dx, dy, dz, w);
	}

	public CrystalTarget(WorldLocation src, WorldLocation target, CrystalElement color, double dx, double dy, double dz, double w) {
		if (src == null)
			throw new IllegalArgumentException("Cannot supply null source!");
		if (target == null)
			throw new IllegalArgumentException("Cannot supply null target!");
		if (color == null)
			throw new IllegalArgumentException("Cannot supply null color!");
		source = src;
		this.color = color;
		location = target;
		offsetX = dx;
		offsetY = dy;
		offsetZ = dz;
		endWidth = w;
	}

	public void writeToNBT(String name, NBTTagCompound NBT) {
		if (location == null || color == null)
			return;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("color", color.ordinal());
		tag.setDouble("dx", offsetX);
		tag.setDouble("dy", offsetY);
		tag.setDouble("dz", offsetZ);
		tag.setDouble("width", endWidth);
		location.writeToNBT("loc", tag);
		source.writeToNBT("src", tag);
		NBT.setTag(name, tag);
	}

	public static CrystalTarget readFromNBT(String name, NBTTagCompound NBT) {
		if (!NBT.hasKey(name))
			return null;
		NBTTagCompound tag = NBT.getCompoundTag(name);
		if (tag == null)
			return null;
		WorldLocation loc = WorldLocation.readFromNBT("loc", tag);
		WorldLocation src = WorldLocation.readFromNBT("src", tag);
		CrystalElement e = CrystalElement.elements[tag.getInteger("color")];
		double dx = tag.getDouble("dx");
		double dy = tag.getDouble("dy");
		double dz = tag.getDouble("dz");
		double w = tag.getDouble("width");
		return loc != null && src != null && e != null ? new CrystalTarget(src, loc, e, dx, dy, dz, w) : null;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CrystalTarget) {
			CrystalTarget t = (CrystalTarget)o;
			return t.location.equals(location) && t.color == color; //ignore render width
		}
		return false;
	}

	@Override
	public int hashCode() {
		return location.hashCode()+color.ordinal(); //ignore render width
	}

	@Override
	public String toString() {
		return color.name()+": "+location.getTileEntity()+" {"+offsetX+","+offsetY+","+offsetZ+"}";
	}

	public static class TickingCrystalTarget extends CrystalTarget {

		private final int lifespan;
		private int tick;

		public TickingCrystalTarget(CrystalNetworkTile src, WorldLocation target, CrystalElement color, double w, int l) {
			super(src, target, color, w);
			lifespan = l;
		}

		public TickingCrystalTarget(CrystalNetworkTile src, WorldLocation target, CrystalElement color, double dx, double dy, double dz, double w, int l) {
			super(src, target, color, dx, dy, dz, w);
			lifespan = l;
		}

		public boolean tick() {
			tick++;
			return tick >= lifespan;
		}

	}

}

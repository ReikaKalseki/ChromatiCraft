/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import net.minecraft.nbt.NBTTagCompound;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;

public class CrystalTarget {

	public final WorldLocation location;
	public final CrystalElement color;
	//public final double endWidth;
	public final double offsetX;
	public final double offsetY;
	public final double offsetZ;

	public CrystalTarget(WorldLocation target, CrystalElement color/*, double w*/) {
		this(target, color, 0, 0, 0/*, w*/);
	}

	public CrystalTarget(WorldLocation target, CrystalElement color, double dx, double dy, double dz/*, double w*/) {
		if (target == null)
			throw new IllegalArgumentException("Cannot supply null target!");
		if (color == null)
			throw new IllegalArgumentException("Cannot supply null color!");
		this.color = color;
		location = target;
		offsetX = dx;
		offsetY = dy;
		offsetZ = dz;
		//endWidth = w;
	}

	public void writeToNBT(String name, NBTTagCompound NBT) {
		if (location == null || color == null)
			return;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("color", color.ordinal());
		tag.setDouble("dx", offsetX);
		tag.setDouble("dy", offsetY);
		tag.setDouble("dz", offsetZ);
		//tag.setDouble("width", endWidth);
		location.writeToNBT("loc", tag);
		NBT.setTag(name, tag);
	}

	public static CrystalTarget readFromNBT(String name, NBTTagCompound NBT) {
		if (!NBT.hasKey(name))
			return null;
		NBTTagCompound tag = NBT.getCompoundTag(name);
		if (tag == null)
			return null;
		WorldLocation loc = WorldLocation.readFromNBT("loc", tag);
		CrystalElement e = CrystalElement.elements[tag.getInteger("color")];
		double dx = tag.getDouble("dx");
		double dy = tag.getDouble("dy");
		double dz = tag.getDouble("dz");
		double w = tag.getDouble("width");
		return loc != null && e != null ? new CrystalTarget(loc, e, dx, dy, dz/*, w*/) : null;
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
		return color.name()+": "+location.getTileEntity();
	}

}

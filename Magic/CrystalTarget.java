/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import net.minecraft.nbt.NBTTagCompound;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.WorldLocation;

public class CrystalTarget {

	public final WorldLocation location;
	public final CrystalElement color;

	public CrystalTarget(WorldLocation target, CrystalElement color) {
		if (target == null)
			throw new IllegalArgumentException("Cannot supply null target!");
		if (color == null)
			throw new IllegalArgumentException("Cannot supply null color!");
		this.color = color;
		location = target;
	}

	public void writeToNBT(String name, NBTTagCompound NBT) {
		if (location == null || color == null)
			return;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("color", color.ordinal());
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
		return loc != null && e != null ? new CrystalTarget(loc, e) : null;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CrystalTarget) {
			CrystalTarget t = (CrystalTarget)o;
			return t.location.equals(location) && t.color == color;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return location.hashCode()+color.ordinal();
	}

	@Override
	public String toString() {
		return color.displayName+": "+location.getTileEntity();
	}

}

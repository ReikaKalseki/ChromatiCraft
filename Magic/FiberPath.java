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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;

public class FiberPath {

	private LinkedList<WorldLocation> steps = new LinkedList();
	public final CrystalElement color;
	private int alpha = 0;

	FiberPath(CrystalElement e, LinkedList<TileEntityChromaticBase> li) {
		color = e;
		for (TileEntityChromaticBase te : li) {
			steps.add(new WorldLocation(te));
		}
	}

	void add(TileEntity te) {
		steps.add(new WorldLocation(te));
	}
	/*
	void breakPath() {

	}
	 */
	public void pulse() {
		alpha = 255;
	}

	public void tick() {
		if (alpha > 0) {
			alpha -= 8;
		}
	}

	public int getAlpha() {
		return alpha;
	}

	public List<WorldLocation> getSteps() {
		return Collections.unmodifiableList(steps);
	}

	@Override
	public String toString() {
		return color+": "+steps.toString();
	}

	public WorldLocation getSource() {
		return steps.getFirst();
	}

	public WorldLocation getSink() {
		return steps.getLast();
	}

	public static FiberPath readFromNBT(NBTTagCompound NBT) {
		CrystalElement e = CrystalElement.elements[NBT.getInteger("color")];
		int alpha = NBT.getInteger("alpha");
		LinkedList<WorldLocation> li = new LinkedList();
		NBTTagList tag = NBT.getTagList("coords", NBTTypes.COMPOUND.ID);
		for (Object o : tag.tagList) {
			NBTTagCompound b = (NBTTagCompound)o;
			WorldLocation loc = WorldLocation.readFromNBT(b);
			li.addLast(loc);
		}
		FiberPath p = new FiberPath(e, new LinkedList());
		p.steps = li;
		p.alpha = alpha;
		return p;
	}

	public void writeToNBT(NBTTagCompound NBT) {
		NBT.setInteger("color", color.ordinal());
		NBT.setInteger("alpha", alpha);
		NBTTagList li = new NBTTagList();
		for (WorldLocation loc : steps) {
			NBTTagCompound tag = new NBTTagCompound();
			loc.writeToNBT(tag);
			li.appendTag(tag);
		}
		NBT.setTag("coords", li);
	}

	public boolean isValid() {
		return steps.size() >= 2;
	}

}

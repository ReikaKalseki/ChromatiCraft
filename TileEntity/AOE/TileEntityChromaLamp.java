/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.Interfaces.BreakAction;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;

public class TileEntityChromaLamp extends TileEntityChromaticBase implements BreakAction {

	private static final HashMap<WorldLocation, Integer> cache = new HashMap();

	private final ElementTagCompound colors = new ElementTagCompound();

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.LAMP;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.cacheTile();
	}

	public int getRange() {
		return 8*colors.tagCount();
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		colors.readFromNBT("energy", NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		colors.writeToNBT("energy", NBT);
	}

	private void cacheTile() {
		cache.put(new WorldLocation(this), this.getRange());
	}

	@Override
	public void breakBlock() {
		cache.remove(new WorldLocation(this));
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public static boolean findLampFromXYZ(World world, double x, double z) {
		for (WorldLocation loc : cache.keySet()) {
			int max = cache.get(loc);
			double dx = Math.abs(x-loc.xCoord);
			double dz = Math.abs(z-loc.zCoord);
			if (dx <= max && dz <= max)
				return true;
		}
		return false;
	}

	public ElementTagCompound getColors() {
		return colors.copy();
	}

	public boolean addColor(CrystalElement e) {
		if (colors.contains(e))
			return false;
		colors.addValueToColor(e, 1);
		this.cacheTile();
		return true;
	}

}

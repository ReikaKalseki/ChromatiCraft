/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Defence;

import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.API.Interfaces.CustomRangeUpgrade.RangeUpgradeable;
import Reika.ChromatiCraft.Auxiliary.RangeTracker;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;

public class TileEntityChromaLamp extends TileEntityChromaticBase implements LocationCached, RangeUpgradeable {

	private static final ConcurrentHashMap<WorldLocation, Integer> cache = new ConcurrentHashMap();

	public static final int FACTOR = 8;

	private final ElementTagCompound colors = new ElementTagCompound();

	private RangeTracker range;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.LAMP;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (range.update(this)) {
			this.cacheTile();
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.initRange();
	}

	private void initRange() {
		range = new RangeTracker(this.getMaxRange());
		range.initialize(this);
		this.cacheTile();
	}

	private int getMaxRange() {
		return FACTOR*colors.tagCount();
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
		this.initRange();
		return true;
	}

	public static void clearCache() {
		cache.clear();
	}

	@Override
	public void upgradeRange(double r) {

	}

	@Override
	public int getRange() {
		return range.getRange();
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import java.util.HashSet;
import java.util.Iterator;

import Reika.ChromatiCraft.API.Interfaces.RangeUpgradeable;
import Reika.ChromatiCraft.Auxiliary.RangeTracker;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.TimerMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


public abstract class TileEntityAreaDistributor extends TileEntityChromaticBase implements RangeUpgradeable {

	public static final int SCAN_RADIUS_XZ = 16;

	private final RangeTracker range = new RangeTracker(SCAN_RADIUS_XZ);

	private final StepTimer cacheTimer = new StepTimer(40);

	private final HashSet<WorldLocation> storages = new HashSet();
	private final HashSet<WorldLocation> inputs = new HashSet();

	private final TimerMap<WorldLocation> particleCooldowns = new TimerMap();

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		cacheTimer.update();
		range.update(this);

		if (cacheTimer.checkCap() || this.getTicksExisted() == 0) {
			this.scanAndCache(world, x, y, z);
		}

		if (world.isRemote) {
			particleCooldowns.tick();
		}
	}

	@Override
	protected final void onFirstTick(World world, int x, int y, int z) {
		range.initialize(this);
	}

	protected final void addInput(WorldLocation loc) {
		inputs.add(loc);
		storages.remove(loc);
	}

	private void addStorage(WorldLocation loc) {
		if (!inputs.contains(loc)) {
			storages.add(loc);
		}
	}

	protected final Iterator<WorldLocation> getTargets() {
		return storages.iterator();
	}

	private void scanAndCache(World world, int x, int y, int z) {
		int r = this.getRange();
		int r2 = r/2;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				for (int j = -r2; j <= 0; j++) {
					TileEntity te = world.getTileEntity(x+i, y+j, z+k);
					if (this.isValidTarget(te)) {
						this.addStorage(new WorldLocation(te));
					}
				}
			}
		}
	}

	protected abstract boolean isValidTarget(TileEntity te);

	protected final boolean trySendParticle(WorldLocation loc) {
		if (!particleCooldowns.containsKey(loc)) {
			particleCooldowns.put(loc, 3+rand.nextInt(8));
			return true;
		}
		return false;
	}

	public final void upgradeRange(double r) {

	}

	public final int getRange() {
		return range.getRange();
	}

}

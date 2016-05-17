/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;


public abstract class TileEntityAreaDistributor extends TileEntityChromaticBase {

	public static final int SCAN_RADIUS_XZ = 16;

	private final StepTimer cacheTimer = new StepTimer(40);

	private final HashSet<WorldLocation> storages = new HashSet();
	private final HashSet<WorldLocation> inputs = new HashSet();

	private final HashMap<WorldLocation, Integer> particleCooldowns = new HashMap();

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		cacheTimer.update();

		if (cacheTimer.checkCap() || this.getTicksExisted() == 0) {
			this.scanAndCache(world, x, y, z);
		}

		if (world.isRemote) {
			HashSet<WorldLocation> remove = new HashSet();
			for (WorldLocation loc : particleCooldowns.keySet()) {
				int get = particleCooldowns.get(loc);
				if (get > 1) {
					particleCooldowns.put(loc, get-1);
				}
				else {
					remove.add(loc);
				}
			}
			for (WorldLocation loc : remove)
				particleCooldowns.remove(loc);
		}
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
		int r = SCAN_RADIUS_XZ;
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

}

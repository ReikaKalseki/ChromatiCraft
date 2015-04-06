package Reika.ChromatiCraft.Base.TileEntity;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.LocationCached;


// Shoot down hostile mobs,
public abstract class TileEntityLocusPoint extends TileEntityChromaticBase implements LocationCached {

	private static final Collection<WorldLocation> cache = new ArrayList();

	@Override
	public final void breakBlock() {
		cache.remove(new WorldLocation(this));
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.cacheTile();
	}

	private void cacheTile() {
		cache.add(new WorldLocation(this));
	}

}

package Reika.ChromatiCraft.TileEntity;

import java.util.HashMap;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.Interfaces.BreakAction;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;

public class TileEntityChromaLamp extends TileEntityChromaticBase implements BreakAction {

	private static final HashMap<WorldLocation, Integer> cache = new HashMap();

	private int range = 32;

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
		return range;
	}

	private void setRange(int r) {
		range = r;
		this.cacheTile();
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

	public static boolean findLampFromXYZ(World world, double x, double y, double z) {
		for (WorldLocation loc : cache.keySet()) {
			double d = loc.getDistanceTo(x, y, z);
			int max = cache.get(loc);
			if (d <= max)
				return true;
		}
		return false;
	}

}

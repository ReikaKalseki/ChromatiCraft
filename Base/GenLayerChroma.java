package Reika.ChromatiCraft.Base;

import net.minecraft.world.gen.layer.GenLayer;

public abstract class GenLayerChroma extends GenLayer {

	public GenLayerChroma(long seed) {
		super(seed);
	}

	@Override
	public int[] getInts(int x, int z, int width, int depth) {
		int[] ret = this.getProperties(x, z, width, depth);
		return ret != null ? ret : new int[width*depth];
	}

	protected abstract int[] getProperties(int x, int z, int width, int depth);

}

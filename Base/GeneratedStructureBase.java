package Reika.ChromatiCraft.Base;

import java.util.HashMap;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;

public abstract class GeneratedStructureBase extends ColoredStructureBase {

	private final HashMap<Coordinate, TileCallback> callbacks = new HashMap();

	@Override
	public void resetToDefaults() {
		super.resetToDefaults();
		callbacks.clear();
	}

	protected final void addCallback(int x, int y, int z, TileCallback c) {
		this.addCallback(new Coordinate(x, y, z), c);
	}

	protected final void addCallback(Coordinate c, TileCallback cl) {
		callbacks.put(c, cl);
	}

	/*private final MultiMap<Block, Coordinate> cache = new MultiMap(CollectionType.HASHSET);

	@Override
	public void resetToDefaults() {
		super.resetToDefaults();
		cache.clear();
	}
	 */
	public abstract int getStructureVersion();

}

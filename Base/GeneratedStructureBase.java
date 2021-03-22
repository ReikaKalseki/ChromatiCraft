package Reika.ChromatiCraft.Base;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionType;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;

public abstract class GeneratedStructureBase extends ColoredStructureBase {

	private final HashMap<Coordinate, TileCallback> callbacks = new HashMap();
	private final MultiMap<Block, Coordinate> cache = new MultiMap(CollectionType.HASHSET);

	@Override
	public void resetToDefaults() {
		super.resetToDefaults();
		callbacks.clear();
		cache.clear();
	}

	protected final void addCallback(int x, int y, int z, TileCallback c) {
		this.addCallback(new Coordinate(x, y, z), c);
	}

	protected final void addCallback(Coordinate c, TileCallback cl) {
		callbacks.put(c, cl);
	}

	protected final void cache(int x, int y, int z, Block b) {
		this.cache(new Coordinate(x, y, z), b);
	}

	protected final void cache(Coordinate c, Block b) {
		cache.addValue(b, c);
	}

	public final void runCallbacks(World world, Random rand) {
		for (Entry<Coordinate, TileCallback> e : callbacks.entrySet()) {
			Coordinate c = e.getKey();
			e.getValue().onTilePlaced(world, c.xCoord, c.yCoord, c.zCoord, c.getTileEntity(world));
		}
	}

	public abstract int getStructureVersion();

	public final Collection<Coordinate> getCachedBlocks(Block b) {
		return Collections.unmodifiableCollection(cache.get(b));
	}

}

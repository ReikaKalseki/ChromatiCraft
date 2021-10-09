package Reika.ChromatiCraft.Base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionType;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;

public abstract class GeneratedStructureBase extends ColoredStructureBase {

	private final HashMap<Coordinate, TileCallback> callbacks = new HashMap();
	private final MultiMap<Block, Coordinate> cache = new MultiMap(CollectionType.HASHSET);

	private boolean isPopulatingForWorldgen = false;

	private final ArrayList<Exception> failures = new ArrayList();

	@Override
	public void resetToDefaults() {
		super.resetToDefaults();
		callbacks.clear();
		cache.clear();
		isPopulatingForWorldgen = false;
		failures.clear();
	}

	public final void markForWorldgen() {
		if (this.isDisplay())
			throw new IllegalStateException("The structure cannot be both display and worldgen!");
		isPopulatingForWorldgen = true;
	}

	public final boolean isWorldgen() {
		return isPopulatingForWorldgen;
	}

	public final Collection<Exception> getErrors() {
		return Collections.unmodifiableCollection(failures);
	}

	public final void addError(Exception e) {
		failures.add(e);
	}

	protected final void addCallback(int x, int y, int z, TileCallback c) {
		this.addCallback(new Coordinate(x, y, z), c);
	}

	protected final void addCallback(Coordinate c, TileCallback cl) {
		if (this.isWorldgen()) {
			if (c.yCoord < 0 || c.yCoord > 255) {
				failures.add(new Exception("Tried to place a tile out of world bounds"));
				return;
			}
		}
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
			try {
				e.getValue().onTilePlaced(world, c.xCoord, c.yCoord, c.zCoord, c.getTileEntity(world));
			}
			catch (Exception ex) {
				failures.add(new Exception("Threw exception running tile callback @ "+e, ex));
			}
		}
	}

	public abstract int getStructureVersion();

	public final Collection<Coordinate> getCachedBlocks(Block b) {
		return Collections.unmodifiableCollection(cache.get(b));
	}

	public final void offset(int x, int y, int z, FilledBlockArray arr) {
		arr.offset(x, y, z);
		HashMap<Coordinate, TileCallback> map = new HashMap(callbacks);
		callbacks.clear();
		for (Entry<Coordinate, TileCallback> e : map.entrySet()) {
			callbacks.put(e.getKey().offset(x, y, z), e.getValue());
		}
		for (Block b : cache.keySet()) {
			Collection<Coordinate> li = cache.get(b);
			Collection<Coordinate> li2 = new ArrayList(li);
			li.clear();
			for (Coordinate c : li2) {
				li.add(c.offset(x, y, z));
			}
		}
	}

}

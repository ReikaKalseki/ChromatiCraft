package Reika.ChromatiCraft.Auxiliary.Structure;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaStructureBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockCliffStone.Variants;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray.BlockMatchFailCallback;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;


public class MusicTempleStructure extends ChromaStructureBase {

	private static final Block fluid = Blocks.water;//ChromaBlocks.LIFEWATER.getBlockInstance();

	private static final HashMap<Coordinate, BlockKey>[] pillars = new HashMap[8];

	private final HashMap<Coordinate, Integer> footprint = new HashMap();
	private final MultiMap<BlockKey, Coordinate> types = new MultiMap();

	private Coordinate origin;
	private FilledBlockArray array;

	static {
		for (int idx = 0; idx < 8; idx++) {
			pillars[idx] = new HashMap();
			Coordinate root = getPillarRoot(idx);
			for (int i = 0; i <= 6; i++) {
				StoneTypes s = i == 0 ? StoneTypes.EMBOSSED : (i == 6 ? StoneTypes.ENGRAVED : StoneTypes.COLUMN);
				pillars[idx].put(root.offset(0, i, 0), new BlockKey(crystalstone, s.ordinal()));
			}
			pillars[idx].put(root.offset(1, 6, 0), new BlockKey(crystalstone, StoneTypes.BEAM.ordinal()));
			pillars[idx].put(root.offset(-1, 6, 0), new BlockKey(crystalstone, StoneTypes.BEAM.ordinal()));
			pillars[idx].put(root.offset(0, 6, 1), new BlockKey(crystalstone, StoneTypes.BEAM.ordinal()));
			pillars[idx].put(root.offset(0, 6, -1), new BlockKey(crystalstone, StoneTypes.BEAM.ordinal()));
		}
	}

	public static Coordinate getPillarRoot(int idx) {
		CubeDirections dir = CubeDirections.list[idx];
		int dd = dir.isCardinal() ? 8 : 6;
		return new Coordinate(dir.directionX*dd, -3, dir.directionZ*dd);
	}

	public void setOrigin(World world, Coordinate ctr) {
		origin = ctr;
		array = this.getArray(world, ctr.xCoord, ctr.yCoord, ctr.zCoord);

		footprint.clear();
		types.clear();

		for (Coordinate c : array.keySet()) {
			Coordinate c2 = c.to2D();
			Integer get = footprint.get(c2);
			if (get == null || get.intValue() > c.yCoord)
				footprint.put(c2, c.yCoord);
			BlockKey bc = array.getBlockKeyAt(c.xCoord, c.yCoord, c.zCoord);
			types.addValue(bc, c);
		}
		for (Entry<Coordinate, Integer> e : footprint.entrySet()) {
			Coordinate c = e.getKey();
			for (int y = e.getValue(); y <= array.getMaxY(); y++) {
				if (array.hasBlock(c.xCoord, y, c.zCoord))
					continue;
				array.setEmpty(c.xCoord, y, c.zCoord, false, false);
				array.addBlock(c.xCoord, y, c.zCoord, fluid);
			}
		}

		prepareArray(array, ctr);
	}

	public static void prepareArray(FilledBlockArray array, Coordinate ctr) {
		array.setEmpty(ctr.xCoord+1, ctr.yCoord, ctr.zCoord, false, false);
		array.setEmpty(ctr.xCoord-1, ctr.yCoord, ctr.zCoord, false, false);
		array.setEmpty(ctr.xCoord, ctr.yCoord, ctr.zCoord-1, false, false);
		array.setEmpty(ctr.xCoord, ctr.yCoord, ctr.zCoord+1, false, false);
		array.addBlock(ctr.xCoord+1, ctr.yCoord, ctr.zCoord, Blocks.lever);
		array.addBlock(ctr.xCoord-1, ctr.yCoord, ctr.zCoord, Blocks.lever);
		array.addBlock(ctr.xCoord, ctr.yCoord, ctr.zCoord+1, Blocks.lever);
		array.addBlock(ctr.xCoord, ctr.yCoord, ctr.zCoord-1, Blocks.lever);

		int x = ctr.xCoord-11;
		int y = ctr.yCoord-4;
		int z = ctr.zCoord-11;
		array.setBlock(x + 11, y + 2, z + 11, fluid, 0);
		/*
		array.setBlock(x + 11, y + 2, z + 10, fluid, 1);
		array.setBlock(x + 11, y + 2, z + 12, fluid, 1);
		array.setBlock(x + 12, y + 2, z + 11, fluid, 1);
		array.setBlock(x + 10, y + 2, z + 11, fluid, 1);
		array.setBlock(x + 11, y + 2, z + 13, fluid, 2);
		array.setBlock(x + 13, y + 2, z + 11, fluid, 2);
		array.setBlock(x + 9, y + 2, z + 11, fluid, 2);
		array.setBlock(x + 11, y + 2, z + 9, fluid, 2);

		int mf = fluid == ChromaBlocks.LIFEWATER.getBlockInstance() ? 1 : 10;
		array.setBlock(x + 9, y + 1, z + 11, fluid, mf);
		array.setBlock(x + 11, y + 1, z + 9, fluid, mf);
		array.setBlock(x + 11, y + 1, z + 13, fluid, mf);
		array.setBlock(x + 13, y + 1, z + 11, fluid, mf);
		 */

		for (int n = 0; n < 8; n++) {
			Coordinate c = getPillarRoot(n).offset(ctr);
			for (int i = -1; i <= 1; i++) {
				for (int k = -1; k <= 1; k++) {
					if (i != 0 || k != 0) {
						array.setBlock(c.xCoord+i, c.yCoord, c.zCoord+k, fluid, 0);
					}
				}
			}
		}
	}

	public Collection<Coordinate> getLocations(BlockKey bk) {
		return Collections.unmodifiableCollection(types.get(bk));
	}

	public Map<Coordinate, BlockKey> getPillar(int idx) {
		return Collections.unmodifiableMap(pillars[idx]);
	}

	public boolean validate(BlockMatchFailCallback call) {
		return array.matchInWorld(call);
	}

	@Override
	public FilledBlockArray getArray(World w, int x, int y, int z) {
		FilledBlockArray world = new FilledBlockArray(w);

		x -= 11;
		y -= 4;
		z -= 11;

		world.setBlock(x + 11, y + 4, z + 11, ChromaTiles.MUSIC.getBlock(), ChromaTiles.MUSIC.getBlockMetadata());

		Block cliff = ChromaBlocks.CLIFFSTONE.getBlockInstance();
		int cliffm = Variants.STONE.getMeta(false, false);

		Block ringB = ChromaBlocks.STRUCTSHIELD.getBlockInstance();//Blocks.lapis_block;//ChromaBlocks.DIMGEN.getBlockInstance();
		int ringM = BlockType.CLOAK.ordinal();//0;//DimDecoTypes.GLOWCAVE.ordinal();

		world.setBlock(x + 0, y + 0, z + 8, cliff, cliffm);
		world.setBlock(x + 0, y + 0, z + 9, cliff, cliffm);
		world.setBlock(x + 0, y + 0, z + 10, cliff, cliffm);
		world.setBlock(x + 0, y + 0, z + 11, cliff, cliffm);
		world.setBlock(x + 0, y + 0, z + 12, cliff, cliffm);
		world.setBlock(x + 0, y + 0, z + 13, cliff, cliffm);
		world.setBlock(x + 0, y + 0, z + 14, cliff, cliffm);
		world.setBlock(x + 1, y + 0, z + 6, cliff, cliffm);
		world.setBlock(x + 1, y + 0, z + 7, cliff, cliffm);
		world.setBlock(x + 1, y + 0, z + 8, cliff, cliffm);
		world.setBlock(x + 1, y + 0, z + 11, crystalstone);
		world.setBlock(x + 1, y + 0, z + 14, cliff, cliffm);
		world.setBlock(x + 1, y + 0, z + 15, cliff, cliffm);
		world.setBlock(x + 1, y + 0, z + 16, cliff, cliffm);
		world.setBlock(x + 1, y + 1, z + 9, crystalstone, 6);
		world.setBlock(x + 1, y + 1, z + 10, crystalstone);
		world.setBlock(x + 1, y + 1, z + 12, crystalstone);
		world.setBlock(x + 1, y + 1, z + 13, crystalstone, 6);
		world.setBlock(x + 2, y + 0, z + 3, cliff, cliffm);
		world.setBlock(x + 2, y + 0, z + 4, cliff, cliffm);
		world.setBlock(x + 2, y + 0, z + 5, cliff, cliffm);
		world.setBlock(x + 2, y + 0, z + 6, cliff, cliffm);
		world.setBlock(x + 2, y + 0, z + 10, crystalstone, 6);
		world.setBlock(x + 2, y + 0, z + 11, crystalstone, 11);
		world.setBlock(x + 2, y + 0, z + 12, crystalstone, 6);
		world.setBlock(x + 2, y + 0, z + 16, cliff, cliffm);
		world.setBlock(x + 2, y + 0, z + 17, cliff, cliffm);
		world.setBlock(x + 2, y + 0, z + 18, cliff, cliffm);
		world.setBlock(x + 2, y + 0, z + 19, cliff, cliffm);
		world.setBlock(x + 2, y + 1, z + 7, cliff, cliffm);
		world.setBlock(x + 2, y + 1, z + 8, cliff, cliffm);
		world.setBlock(x + 2, y + 1, z + 9, crystalstone);
		world.setBlock(x + 2, y + 1, z + 13, crystalstone);
		world.setBlock(x + 2, y + 1, z + 14, cliff, cliffm);
		world.setBlock(x + 2, y + 1, z + 15, cliff, cliffm);
		world.setBlock(x + 2, y + 7, z + 11, crystalstone, 1);
		world.setBlock(x + 3, y + 0, z + 2, cliff, cliffm);
		world.setBlock(x + 3, y + 0, z + 3, cliff, cliffm);
		world.setBlock(x + 3, y + 0, z + 9, crystalstone);
		world.setBlock(x + 3, y + 0, z + 10, crystalstone, 10);
		world.setBlock(x + 3, y + 0, z + 11, crystalstone);
		world.setBlock(x + 3, y + 0, z + 12, crystalstone, 10);
		world.setBlock(x + 3, y + 0, z + 13, crystalstone);
		world.setBlock(x + 3, y + 0, z + 19, cliff, cliffm);
		world.setBlock(x + 3, y + 0, z + 20, cliff, cliffm);
		world.setBlock(x + 3, y + 1, z + 4, cliff, cliffm);
		world.setBlock(x + 3, y + 1, z + 5, cliff, cliffm);
		world.setBlock(x + 3, y + 1, z + 6, cliff, cliffm);
		world.setBlock(x + 3, y + 1, z + 7, crystalstone);
		world.setBlock(x + 3, y + 1, z + 8, crystalstone);
		world.setBlock(x + 3, y + 1, z + 9, crystalstone);
		world.setBlock(x + 3, y + 1, z + 11, crystalstone, 8);
		world.setBlock(x + 3, y + 1, z + 13, crystalstone);
		world.setBlock(x + 3, y + 1, z + 14, crystalstone);
		world.setBlock(x + 3, y + 1, z + 15, crystalstone);
		world.setBlock(x + 3, y + 1, z + 16, cliff, cliffm);
		world.setBlock(x + 3, y + 1, z + 17, cliff, cliffm);
		world.setBlock(x + 3, y + 1, z + 18, cliff, cliffm);
		world.setBlock(x + 3, y + 2, z + 11, crystalstone, 2);
		world.setBlock(x + 3, y + 3, z + 11, crystalstone, 2);
		world.setBlock(x + 3, y + 4, z + 11, crystalstone, 2);
		world.setBlock(x + 3, y + 5, z + 11, crystalstone, 2);
		world.setBlock(x + 3, y + 6, z + 11, crystalstone, 2);
		world.setBlock(x + 3, y + 7, z + 10, crystalstone, 1);
		world.setBlock(x + 3, y + 7, z + 11, crystalstone, 7);
		world.setBlock(x + 3, y + 7, z + 12, crystalstone, 1);
		world.setBlock(x + 4, y + 0, z + 2, cliff, cliffm);
		world.setBlock(x + 4, y + 0, z + 4, crystalstone, 6);
		world.setBlock(x + 4, y + 0, z + 5, crystalstone, 11);
		world.setBlock(x + 4, y + 0, z + 6, crystalstone, 6);
		world.setBlock(x + 4, y + 0, z + 10, crystalstone, 6);
		world.setBlock(x + 4, y + 0, z + 11, crystalstone, 11);
		world.setBlock(x + 4, y + 0, z + 12, crystalstone, 6);
		world.setBlock(x + 4, y + 0, z + 16, crystalstone, 6);
		world.setBlock(x + 4, y + 0, z + 17, crystalstone, 11);
		world.setBlock(x + 4, y + 0, z + 18, crystalstone, 6);
		world.setBlock(x + 4, y + 0, z + 20, cliff, cliffm);
		world.setBlock(x + 4, y + 1, z + 3, cliff, cliffm);
		world.setBlock(x + 4, y + 1, z + 7, Blocks.quartz_block);
		world.setBlock(x + 4, y + 1, z + 8, crystalstone);
		world.setBlock(x + 4, y + 1, z + 9, Blocks.quartz_block);
		world.setBlock(x + 4, y + 1, z + 13, Blocks.quartz_block);
		world.setBlock(x + 4, y + 1, z + 14, crystalstone);
		world.setBlock(x + 4, y + 1, z + 15, Blocks.quartz_block);
		world.setBlock(x + 4, y + 1, z + 19, cliff, cliffm);
		world.setBlock(x + 4, y + 7, z + 5, crystalstone, 1);
		world.setBlock(x + 4, y + 7, z + 11, crystalstone, 1);
		world.setBlock(x + 4, y + 7, z + 17, crystalstone, 1);
		world.setBlock(x + 5, y + 0, z + 2, cliff, cliffm);
		world.setBlock(x + 5, y + 0, z + 4, crystalstone, 10);
		world.setBlock(x + 5, y + 0, z + 5, crystalstone);
		world.setBlock(x + 5, y + 0, z + 6, crystalstone, 10);
		world.setBlock(x + 5, y + 0, z + 7, crystalstone);
		world.setBlock(x + 5, y + 0, z + 15, crystalstone);
		world.setBlock(x + 5, y + 0, z + 16, crystalstone, 10);
		world.setBlock(x + 5, y + 0, z + 17, crystalstone);
		world.setBlock(x + 5, y + 0, z + 18, crystalstone, 10);
		world.setBlock(x + 5, y + 0, z + 20, cliff, cliffm);
		world.setBlock(x + 5, y + 1, z + 3, cliff, cliffm);
		world.setBlock(x + 5, y + 1, z + 5, crystalstone, 8);
		world.setBlock(x + 5, y + 1, z + 7, crystalstone, 11);
		world.setBlock(x + 5, y + 1, z + 8, crystalstone);
		world.setBlock(x + 5, y + 1, z + 9, ringB, ringM);
		world.setBlock(x + 5, y + 1, z + 10, ringB, ringM);
		world.setBlock(x + 5, y + 1, z + 11, Blocks.quartz_block);
		world.setBlock(x + 5, y + 1, z + 12, ringB, ringM);
		world.setBlock(x + 5, y + 1, z + 13, ringB, ringM);
		world.setBlock(x + 5, y + 1, z + 14, crystalstone);
		world.setBlock(x + 5, y + 1, z + 15, crystalstone, 11);
		world.setBlock(x + 5, y + 1, z + 17, crystalstone, 8);
		world.setBlock(x + 5, y + 1, z + 19, cliff, cliffm);
		world.setBlock(x + 5, y + 2, z + 5, crystalstone, 2);
		world.setBlock(x + 5, y + 2, z + 17, crystalstone, 2);
		world.setBlock(x + 5, y + 3, z + 5, crystalstone, 2);
		world.setBlock(x + 5, y + 3, z + 17, crystalstone, 2);
		world.setBlock(x + 5, y + 4, z + 5, crystalstone, 2);
		world.setBlock(x + 5, y + 4, z + 17, crystalstone, 2);
		world.setBlock(x + 5, y + 5, z + 5, crystalstone, 2);
		world.setBlock(x + 5, y + 5, z + 17, crystalstone, 2);
		world.setBlock(x + 5, y + 6, z + 5, crystalstone, 2);
		world.setBlock(x + 5, y + 6, z + 17, crystalstone, 2);
		world.setBlock(x + 5, y + 7, z + 4, crystalstone, 1);
		world.setBlock(x + 5, y + 7, z + 5, crystalstone, 7);
		world.setBlock(x + 5, y + 7, z + 6, crystalstone, 1);
		world.setBlock(x + 5, y + 7, z + 16, crystalstone, 1);
		world.setBlock(x + 5, y + 7, z + 17, crystalstone, 7);
		world.setBlock(x + 5, y + 7, z + 18, crystalstone, 1);
		world.setBlock(x + 6, y + 0, z + 1, cliff, cliffm);
		world.setBlock(x + 6, y + 0, z + 2, cliff, cliffm);
		world.setBlock(x + 6, y + 0, z + 4, crystalstone, 6);
		world.setBlock(x + 6, y + 0, z + 5, crystalstone, 11);
		world.setBlock(x + 6, y + 0, z + 6, crystalstone, 6);
		world.setBlock(x + 6, y + 0, z + 16, crystalstone, 6);
		world.setBlock(x + 6, y + 0, z + 17, crystalstone, 11);
		world.setBlock(x + 6, y + 0, z + 18, crystalstone, 6);
		world.setBlock(x + 6, y + 0, z + 20, cliff, cliffm);
		world.setBlock(x + 6, y + 0, z + 21, cliff, cliffm);
		world.setBlock(x + 6, y + 1, z + 3, cliff, cliffm);
		world.setBlock(x + 6, y + 1, z + 7, ringB, ringM);
		world.setBlock(x + 6, y + 1, z + 8, crystalstone, 11);
		world.setBlock(x + 6, y + 1, z + 9, crystalstone, 11);
		world.setBlock(x + 6, y + 1, z + 10, crystalstone, 11);
		world.setBlock(x + 6, y + 1, z + 11, Blocks.quartz_block);
		world.setBlock(x + 6, y + 1, z + 12, crystalstone, 11);
		world.setBlock(x + 6, y + 1, z + 13, crystalstone, 11);
		world.setBlock(x + 6, y + 1, z + 14, crystalstone, 11);
		world.setBlock(x + 6, y + 1, z + 15, ringB, ringM);
		world.setBlock(x + 6, y + 1, z + 19, cliff, cliffm);
		world.setBlock(x + 6, y + 7, z + 5, crystalstone, 1);
		world.setBlock(x + 6, y + 7, z + 17, crystalstone, 1);
		world.setBlock(x + 7, y + 0, z + 1, cliff, cliffm);
		world.setBlock(x + 7, y + 0, z + 5, crystalstone);
		world.setBlock(x + 7, y + 0, z + 17, crystalstone);
		world.setBlock(x + 7, y + 0, z + 21, cliff, cliffm);
		world.setBlock(x + 7, y + 1, z + 2, cliff, cliffm);
		world.setBlock(x + 7, y + 1, z + 3, crystalstone);
		world.setBlock(x + 7, y + 1, z + 4, Blocks.quartz_block);
		world.setBlock(x + 7, y + 1, z + 5, crystalstone, 10);
		world.setBlock(x + 7, y + 1, z + 6, ringB, ringM);
		world.setBlock(x + 7, y + 1, z + 7, Blocks.quartz_block);
		world.setBlock(x + 7, y + 1, z + 8, crystalstone, 11);
		world.setBlock(x + 7, y + 1, z + 9, crystalstone, 11);
		world.setBlock(x + 7, y + 1, z + 10, ringB, ringM);
		world.setBlock(x + 7, y + 1, z + 11, ringB, ringM);
		world.setBlock(x + 7, y + 1, z + 12, ringB, ringM);
		world.setBlock(x + 7, y + 1, z + 13, crystalstone, 11);
		world.setBlock(x + 7, y + 1, z + 14, crystalstone, 11);
		world.setBlock(x + 7, y + 1, z + 15, Blocks.quartz_block);
		world.setBlock(x + 7, y + 1, z + 16, ringB, ringM);
		world.setBlock(x + 7, y + 1, z + 17, crystalstone, 10);
		world.setBlock(x + 7, y + 1, z + 18, Blocks.quartz_block);
		world.setBlock(x + 7, y + 1, z + 19, crystalstone);
		world.setBlock(x + 7, y + 1, z + 20, cliff, cliffm);
		world.setBlock(x + 8, y + 0, z + 0, cliff, cliffm);
		world.setBlock(x + 8, y + 0, z + 1, cliff, cliffm);
		world.setBlock(x + 8, y + 0, z + 21, cliff, cliffm);
		world.setBlock(x + 8, y + 0, z + 22, cliff, cliffm);
		world.setBlock(x + 8, y + 1, z + 2, cliff, cliffm);
		world.setBlock(x + 8, y + 1, z + 3, crystalstone);
		world.setBlock(x + 8, y + 1, z + 4, crystalstone);
		world.setBlock(x + 8, y + 1, z + 5, crystalstone);
		world.setBlock(x + 8, y + 1, z + 6, crystalstone, 10);
		world.setBlock(x + 8, y + 1, z + 7, crystalstone, 10);
		world.setBlock(x + 8, y + 1, z + 8, Blocks.quartz_block);
		world.setBlock(x + 8, y + 1, z + 9, crystalstone, 11);
		world.setBlock(x + 8, y + 1, z + 10, crystalstone, 11);
		world.setBlock(x + 8, y + 1, z + 11, Blocks.quartz_block);
		world.setBlock(x + 8, y + 1, z + 12, crystalstone, 11);
		world.setBlock(x + 8, y + 1, z + 13, crystalstone, 11);
		world.setBlock(x + 8, y + 1, z + 14, Blocks.quartz_block);
		world.setBlock(x + 8, y + 1, z + 15, crystalstone, 10);
		world.setBlock(x + 8, y + 1, z + 16, crystalstone, 10);
		world.setBlock(x + 8, y + 1, z + 17, crystalstone);
		world.setBlock(x + 8, y + 1, z + 18, crystalstone);
		world.setBlock(x + 8, y + 1, z + 19, crystalstone);
		world.setBlock(x + 8, y + 1, z + 20, cliff, cliffm);
		world.setBlock(x + 8, y + 2, z + 11, Blocks.quartz_stairs);
		world.setBlock(x + 9, y + 0, z + 0, cliff, cliffm);
		world.setBlock(x + 9, y + 0, z + 3, crystalstone);
		world.setBlock(x + 9, y + 0, z + 11, Blocks.quartz_block);
		world.setBlock(x + 9, y + 0, z + 19, crystalstone);
		world.setBlock(x + 9, y + 0, z + 22, cliff, cliffm);
		world.setBlock(x + 9, y + 1, z + 1, crystalstone, 6);
		world.setBlock(x + 9, y + 1, z + 2, crystalstone);
		world.setBlock(x + 9, y + 1, z + 3, crystalstone);
		world.setBlock(x + 9, y + 1, z + 4, Blocks.quartz_block);
		world.setBlock(x + 9, y + 1, z + 5, ringB, ringM);
		world.setBlock(x + 9, y + 1, z + 6, crystalstone, 10);
		world.setBlock(x + 9, y + 1, z + 7, crystalstone, 10);
		world.setBlock(x + 9, y + 1, z + 8, crystalstone, 10);
		world.setBlock(x + 9, y + 1, z + 9, Blocks.quartz_block);
		world.setBlock(x + 9, y + 1, z + 10, crystalstone);
		world.setBlock(x + 9, y + 1, z + 12, crystalstone);
		world.setBlock(x + 9, y + 1, z + 13, Blocks.quartz_block);
		world.setBlock(x + 9, y + 1, z + 14, crystalstone, 10);
		world.setBlock(x + 9, y + 1, z + 15, crystalstone, 10);
		world.setBlock(x + 9, y + 1, z + 16, crystalstone, 10);
		world.setBlock(x + 9, y + 1, z + 17, ringB, ringM);
		world.setBlock(x + 9, y + 1, z + 18, Blocks.quartz_block);
		world.setBlock(x + 9, y + 1, z + 19, crystalstone);
		world.setBlock(x + 9, y + 1, z + 20, crystalstone);
		world.setBlock(x + 9, y + 1, z + 21, crystalstone, 6);
		world.setBlock(x + 9, y + 3, z + 11, Blocks.quartz_stairs);
		world.setBlock(x + 10, y + 0, z + 0, cliff, cliffm);
		world.setBlock(x + 10, y + 0, z + 2, crystalstone, 6);
		world.setBlock(x + 10, y + 0, z + 3, crystalstone, 11);
		world.setBlock(x + 10, y + 0, z + 4, crystalstone, 6);
		world.setBlock(x + 10, y + 0, z + 18, crystalstone, 6);
		world.setBlock(x + 10, y + 0, z + 19, crystalstone, 11);
		world.setBlock(x + 10, y + 0, z + 20, crystalstone, 6);
		world.setBlock(x + 10, y + 0, z + 22, cliff, cliffm);
		world.setBlock(x + 10, y + 1, z + 1, crystalstone);
		world.setBlock(x + 10, y + 1, z + 5, ringB, ringM);
		world.setBlock(x + 10, y + 1, z + 6, crystalstone, 10);
		world.setBlock(x + 10, y + 1, z + 7, ringB, ringM);
		world.setBlock(x + 10, y + 1, z + 8, crystalstone, 10);
		world.setBlock(x + 10, y + 1, z + 9, crystalstone);
		world.setBlock(x + 10, y + 1, z + 10, crystalstone);
		world.setBlock(x + 10, y + 1, z + 11, Blocks.quartz_block);
		world.setBlock(x + 10, y + 1, z + 12, crystalstone);
		world.setBlock(x + 10, y + 1, z + 13, crystalstone);
		world.setBlock(x + 10, y + 1, z + 14, crystalstone, 10);
		world.setBlock(x + 10, y + 1, z + 15, ringB, ringM);
		world.setBlock(x + 10, y + 1, z + 16, crystalstone, 10);
		world.setBlock(x + 10, y + 1, z + 17, ringB, ringM);
		world.setBlock(x + 10, y + 1, z + 21, crystalstone);
		world.setBlock(x + 10, y + 2, z + 10, crystalstone, 2);
		world.setBlock(x + 10, y + 2, z + 12, crystalstone, 2);
		world.setBlock(x + 10, y + 3, z + 10, crystalstone, 6);
		world.setBlock(x + 10, y + 3, z + 11, crystalstone, 11);
		world.setBlock(x + 10, y + 3, z + 12, crystalstone, 6);
		world.setBlock(x + 10, y + 7, z + 3, crystalstone, 1);
		world.setBlock(x + 10, y + 7, z + 19, crystalstone, 1);
		world.setBlock(x + 11, y + 0, z + 0, cliff, cliffm);
		world.setBlock(x + 11, y + 0, z + 1, crystalstone);
		world.setBlock(x + 11, y + 0, z + 2, crystalstone, 10);
		world.setBlock(x + 11, y + 0, z + 3, crystalstone);
		world.setBlock(x + 11, y + 0, z + 4, crystalstone, 10);
		world.setBlock(x + 11, y + 0, z + 9, Blocks.quartz_block);
		world.setBlock(x + 11, y + 0, z + 13, Blocks.quartz_block);
		world.setBlock(x + 11, y + 0, z + 18, crystalstone, 10);
		world.setBlock(x + 11, y + 0, z + 19, crystalstone);
		world.setBlock(x + 11, y + 0, z + 20, crystalstone, 10);
		world.setBlock(x + 11, y + 0, z + 21, crystalstone);
		world.setBlock(x + 11, y + 0, z + 22, cliff, cliffm);
		world.setBlock(x + 11, y + 1, z + 3, crystalstone, 8);
		world.setBlock(x + 11, y + 1, z + 5, Blocks.quartz_block);
		world.setBlock(x + 11, y + 1, z + 6, Blocks.quartz_block);
		world.setBlock(x + 11, y + 1, z + 7, ringB, ringM);
		world.setBlock(x + 11, y + 1, z + 8, Blocks.quartz_block);
		world.setBlock(x + 11, y + 1, z + 10, Blocks.quartz_block);
		world.setBlock(x + 11, y + 1, z + 11, Blocks.quartz_block);
		world.setBlock(x + 11, y + 1, z + 12, Blocks.quartz_block);
		world.setBlock(x + 11, y + 1, z + 14, Blocks.quartz_block);
		world.setBlock(x + 11, y + 1, z + 15, ringB, ringM);
		world.setBlock(x + 11, y + 1, z + 16, Blocks.quartz_block);
		world.setBlock(x + 11, y + 1, z + 17, Blocks.quartz_block);
		world.setBlock(x + 11, y + 1, z + 19, crystalstone, 8);
		world.setBlock(x + 11, y + 2, z + 3, crystalstone, 2);
		world.setBlock(x + 11, y + 2, z + 8, Blocks.quartz_stairs, 2);
		world.setBlock(x + 11, y + 2, z + 14, Blocks.quartz_stairs, 3);
		world.setBlock(x + 11, y + 2, z + 19, crystalstone, 2);
		world.setBlock(x + 11, y + 3, z + 3, crystalstone, 2);
		world.setBlock(x + 11, y + 3, z + 9, Blocks.quartz_stairs, 2);
		world.setBlock(x + 11, y + 3, z + 10, crystalstone, 10);
		world.setBlock(x + 11, y + 3, z + 11, crystalstone);
		world.setBlock(x + 11, y + 3, z + 12, crystalstone, 10);
		world.setBlock(x + 11, y + 3, z + 13, Blocks.quartz_stairs, 3);
		world.setBlock(x + 11, y + 3, z + 19, crystalstone, 2);
		world.setBlock(x + 11, y + 4, z + 3, crystalstone, 2);
		world.setBlock(x + 11, y + 4, z + 19, crystalstone, 2);
		world.setBlock(x + 11, y + 5, z + 3, crystalstone, 2);
		world.setBlock(x + 11, y + 5, z + 19, crystalstone, 2);
		world.setBlock(x + 11, y + 6, z + 3, crystalstone, 2);
		world.setBlock(x + 11, y + 6, z + 19, crystalstone, 2);
		world.setBlock(x + 11, y + 7, z + 2, crystalstone, 1);
		world.setBlock(x + 11, y + 7, z + 3, crystalstone, 7);
		world.setBlock(x + 11, y + 7, z + 4, crystalstone, 1);
		world.setBlock(x + 11, y + 7, z + 18, crystalstone, 1);
		world.setBlock(x + 11, y + 7, z + 19, crystalstone, 7);
		world.setBlock(x + 11, y + 7, z + 20, crystalstone, 1);
		world.setBlock(x + 12, y + 0, z + 0, cliff, cliffm);
		world.setBlock(x + 12, y + 0, z + 2, crystalstone, 6);
		world.setBlock(x + 12, y + 0, z + 3, crystalstone, 11);
		world.setBlock(x + 12, y + 0, z + 4, crystalstone, 6);
		world.setBlock(x + 12, y + 0, z + 18, crystalstone, 6);
		world.setBlock(x + 12, y + 0, z + 19, crystalstone, 11);
		world.setBlock(x + 12, y + 0, z + 20, crystalstone, 6);
		world.setBlock(x + 12, y + 0, z + 22, cliff, cliffm);
		world.setBlock(x + 12, y + 1, z + 1, crystalstone);
		world.setBlock(x + 12, y + 1, z + 5, ringB, ringM);
		world.setBlock(x + 12, y + 1, z + 6, crystalstone, 10);
		world.setBlock(x + 12, y + 1, z + 7, ringB, ringM);
		world.setBlock(x + 12, y + 1, z + 8, crystalstone, 10);
		world.setBlock(x + 12, y + 1, z + 9, crystalstone);
		world.setBlock(x + 12, y + 1, z + 10, crystalstone);
		world.setBlock(x + 12, y + 1, z + 11, Blocks.quartz_block);
		world.setBlock(x + 12, y + 1, z + 12, crystalstone);
		world.setBlock(x + 12, y + 1, z + 13, crystalstone);
		world.setBlock(x + 12, y + 1, z + 14, crystalstone, 10);
		world.setBlock(x + 12, y + 1, z + 15, ringB, ringM);
		world.setBlock(x + 12, y + 1, z + 16, crystalstone, 10);
		world.setBlock(x + 12, y + 1, z + 17, ringB, ringM);
		world.setBlock(x + 12, y + 1, z + 21, crystalstone);
		world.setBlock(x + 12, y + 2, z + 10, crystalstone, 2);
		world.setBlock(x + 12, y + 2, z + 12, crystalstone, 2);
		world.setBlock(x + 12, y + 3, z + 10, crystalstone, 6);
		world.setBlock(x + 12, y + 3, z + 11, crystalstone, 11);
		world.setBlock(x + 12, y + 3, z + 12, crystalstone, 6);
		world.setBlock(x + 12, y + 7, z + 3, crystalstone, 1);
		world.setBlock(x + 12, y + 7, z + 19, crystalstone, 1);
		world.setBlock(x + 13, y + 0, z + 0, cliff, cliffm);
		world.setBlock(x + 13, y + 0, z + 3, crystalstone);
		world.setBlock(x + 13, y + 0, z + 11, Blocks.quartz_block);
		world.setBlock(x + 13, y + 0, z + 19, crystalstone);
		world.setBlock(x + 13, y + 0, z + 22, cliff, cliffm);
		world.setBlock(x + 13, y + 1, z + 1, crystalstone, 6);
		world.setBlock(x + 13, y + 1, z + 2, crystalstone);
		world.setBlock(x + 13, y + 1, z + 3, crystalstone);
		world.setBlock(x + 13, y + 1, z + 4, Blocks.quartz_block);
		world.setBlock(x + 13, y + 1, z + 5, ringB, ringM);
		world.setBlock(x + 13, y + 1, z + 6, crystalstone, 10);
		world.setBlock(x + 13, y + 1, z + 7, crystalstone, 10);
		world.setBlock(x + 13, y + 1, z + 8, crystalstone, 10);
		world.setBlock(x + 13, y + 1, z + 9, Blocks.quartz_block);
		world.setBlock(x + 13, y + 1, z + 10, crystalstone);
		world.setBlock(x + 13, y + 1, z + 12, crystalstone);
		world.setBlock(x + 13, y + 1, z + 13, Blocks.quartz_block);
		world.setBlock(x + 13, y + 1, z + 14, crystalstone, 10);
		world.setBlock(x + 13, y + 1, z + 15, crystalstone, 10);
		world.setBlock(x + 13, y + 1, z + 16, crystalstone, 10);
		world.setBlock(x + 13, y + 1, z + 17, ringB, ringM);
		world.setBlock(x + 13, y + 1, z + 18, Blocks.quartz_block);
		world.setBlock(x + 13, y + 1, z + 19, crystalstone);
		world.setBlock(x + 13, y + 1, z + 20, crystalstone);
		world.setBlock(x + 13, y + 1, z + 21, crystalstone, 6);
		world.setBlock(x + 13, y + 3, z + 11, Blocks.quartz_stairs, 1);
		world.setBlock(x + 14, y + 0, z + 0, cliff, cliffm);
		world.setBlock(x + 14, y + 0, z + 1, cliff, cliffm);
		world.setBlock(x + 14, y + 0, z + 21, cliff, cliffm);
		world.setBlock(x + 14, y + 0, z + 22, cliff, cliffm);
		world.setBlock(x + 14, y + 1, z + 2, cliff, cliffm);
		world.setBlock(x + 14, y + 1, z + 3, crystalstone);
		world.setBlock(x + 14, y + 1, z + 4, crystalstone);
		world.setBlock(x + 14, y + 1, z + 5, crystalstone);
		world.setBlock(x + 14, y + 1, z + 6, crystalstone, 10);
		world.setBlock(x + 14, y + 1, z + 7, crystalstone, 10);
		world.setBlock(x + 14, y + 1, z + 8, Blocks.quartz_block);
		world.setBlock(x + 14, y + 1, z + 9, crystalstone, 11);
		world.setBlock(x + 14, y + 1, z + 10, crystalstone, 11);
		world.setBlock(x + 14, y + 1, z + 11, Blocks.quartz_block);
		world.setBlock(x + 14, y + 1, z + 12, crystalstone, 11);
		world.setBlock(x + 14, y + 1, z + 13, crystalstone, 11);
		world.setBlock(x + 14, y + 1, z + 14, Blocks.quartz_block);
		world.setBlock(x + 14, y + 1, z + 15, crystalstone, 10);
		world.setBlock(x + 14, y + 1, z + 16, crystalstone, 10);
		world.setBlock(x + 14, y + 1, z + 17, crystalstone);
		world.setBlock(x + 14, y + 1, z + 18, crystalstone);
		world.setBlock(x + 14, y + 1, z + 19, crystalstone);
		world.setBlock(x + 14, y + 1, z + 20, cliff, cliffm);
		world.setBlock(x + 14, y + 2, z + 11, Blocks.quartz_stairs, 1);
		world.setBlock(x + 15, y + 0, z + 1, cliff, cliffm);
		world.setBlock(x + 15, y + 0, z + 5, crystalstone);
		world.setBlock(x + 15, y + 0, z + 17, crystalstone);
		world.setBlock(x + 15, y + 0, z + 21, cliff, cliffm);
		world.setBlock(x + 15, y + 1, z + 2, cliff, cliffm);
		world.setBlock(x + 15, y + 1, z + 3, crystalstone);
		world.setBlock(x + 15, y + 1, z + 4, Blocks.quartz_block);
		world.setBlock(x + 15, y + 1, z + 5, crystalstone, 10);
		world.setBlock(x + 15, y + 1, z + 6, ringB, ringM);
		world.setBlock(x + 15, y + 1, z + 7, Blocks.quartz_block);
		world.setBlock(x + 15, y + 1, z + 8, crystalstone, 11);
		world.setBlock(x + 15, y + 1, z + 9, crystalstone, 11);
		world.setBlock(x + 15, y + 1, z + 10, ringB, ringM);
		world.setBlock(x + 15, y + 1, z + 11, ringB, ringM);
		world.setBlock(x + 15, y + 1, z + 12, ringB, ringM);
		world.setBlock(x + 15, y + 1, z + 13, crystalstone, 11);
		world.setBlock(x + 15, y + 1, z + 14, crystalstone, 11);
		world.setBlock(x + 15, y + 1, z + 15, Blocks.quartz_block);
		world.setBlock(x + 15, y + 1, z + 16, ringB, ringM);
		world.setBlock(x + 15, y + 1, z + 17, crystalstone, 10);
		world.setBlock(x + 15, y + 1, z + 18, Blocks.quartz_block);
		world.setBlock(x + 15, y + 1, z + 19, crystalstone);
		world.setBlock(x + 15, y + 1, z + 20, cliff, cliffm);
		world.setBlock(x + 16, y + 0, z + 1, cliff, cliffm);
		world.setBlock(x + 16, y + 0, z + 2, cliff, cliffm);
		world.setBlock(x + 16, y + 0, z + 4, crystalstone, 6);
		world.setBlock(x + 16, y + 0, z + 5, crystalstone, 11);
		world.setBlock(x + 16, y + 0, z + 6, crystalstone, 6);
		world.setBlock(x + 16, y + 0, z + 16, crystalstone, 6);
		world.setBlock(x + 16, y + 0, z + 17, crystalstone, 11);
		world.setBlock(x + 16, y + 0, z + 18, crystalstone, 6);
		world.setBlock(x + 16, y + 0, z + 20, cliff, cliffm);
		world.setBlock(x + 16, y + 0, z + 21, cliff, cliffm);
		world.setBlock(x + 16, y + 1, z + 3, cliff, cliffm);
		world.setBlock(x + 16, y + 1, z + 7, ringB, ringM);
		world.setBlock(x + 16, y + 1, z + 8, crystalstone, 11);
		world.setBlock(x + 16, y + 1, z + 9, crystalstone, 11);
		world.setBlock(x + 16, y + 1, z + 10, crystalstone, 11);
		world.setBlock(x + 16, y + 1, z + 11, Blocks.quartz_block);
		world.setBlock(x + 16, y + 1, z + 12, crystalstone, 11);
		world.setBlock(x + 16, y + 1, z + 13, crystalstone, 11);
		world.setBlock(x + 16, y + 1, z + 14, crystalstone, 11);
		world.setBlock(x + 16, y + 1, z + 15, ringB, ringM);
		world.setBlock(x + 16, y + 1, z + 19, cliff, cliffm);
		world.setBlock(x + 16, y + 7, z + 5, crystalstone, 1);
		world.setBlock(x + 16, y + 7, z + 17, crystalstone, 1);
		world.setBlock(x + 17, y + 0, z + 2, cliff, cliffm);
		world.setBlock(x + 17, y + 0, z + 4, crystalstone, 10);
		world.setBlock(x + 17, y + 0, z + 5, crystalstone);
		world.setBlock(x + 17, y + 0, z + 6, crystalstone, 10);
		world.setBlock(x + 17, y + 0, z + 7, crystalstone);
		world.setBlock(x + 17, y + 0, z + 15, crystalstone);
		world.setBlock(x + 17, y + 0, z + 16, crystalstone, 10);
		world.setBlock(x + 17, y + 0, z + 17, crystalstone);
		world.setBlock(x + 17, y + 0, z + 18, crystalstone, 10);
		world.setBlock(x + 17, y + 0, z + 20, cliff, cliffm);
		world.setBlock(x + 17, y + 1, z + 3, cliff, cliffm);
		world.setBlock(x + 17, y + 1, z + 5, crystalstone, 8);
		world.setBlock(x + 17, y + 1, z + 7, crystalstone, 11);
		world.setBlock(x + 17, y + 1, z + 8, crystalstone);
		world.setBlock(x + 17, y + 1, z + 9, ringB, ringM);
		world.setBlock(x + 17, y + 1, z + 10, ringB, ringM);
		world.setBlock(x + 17, y + 1, z + 11, Blocks.quartz_block);
		world.setBlock(x + 17, y + 1, z + 12, ringB, ringM);
		world.setBlock(x + 17, y + 1, z + 13, ringB, ringM);
		world.setBlock(x + 17, y + 1, z + 14, crystalstone);
		world.setBlock(x + 17, y + 1, z + 15, crystalstone, 11);
		world.setBlock(x + 17, y + 1, z + 17, crystalstone, 8);
		world.setBlock(x + 17, y + 1, z + 19, cliff, cliffm);
		world.setBlock(x + 17, y + 2, z + 5, crystalstone, 2);
		world.setBlock(x + 17, y + 2, z + 17, crystalstone, 2);
		world.setBlock(x + 17, y + 3, z + 5, crystalstone, 2);
		world.setBlock(x + 17, y + 3, z + 17, crystalstone, 2);
		world.setBlock(x + 17, y + 4, z + 5, crystalstone, 2);
		world.setBlock(x + 17, y + 4, z + 17, crystalstone, 2);
		world.setBlock(x + 17, y + 5, z + 5, crystalstone, 2);
		world.setBlock(x + 17, y + 5, z + 17, crystalstone, 2);
		world.setBlock(x + 17, y + 6, z + 5, crystalstone, 2);
		world.setBlock(x + 17, y + 6, z + 17, crystalstone, 2);
		world.setBlock(x + 17, y + 7, z + 4, crystalstone, 1);
		world.setBlock(x + 17, y + 7, z + 5, crystalstone, 7);
		world.setBlock(x + 17, y + 7, z + 6, crystalstone, 1);
		world.setBlock(x + 17, y + 7, z + 16, crystalstone, 1);
		world.setBlock(x + 17, y + 7, z + 17, crystalstone, 7);
		world.setBlock(x + 17, y + 7, z + 18, crystalstone, 1);
		world.setBlock(x + 18, y + 0, z + 2, cliff, cliffm);
		world.setBlock(x + 18, y + 0, z + 4, crystalstone, 6);
		world.setBlock(x + 18, y + 0, z + 5, crystalstone, 11);
		world.setBlock(x + 18, y + 0, z + 6, crystalstone, 6);
		world.setBlock(x + 18, y + 0, z + 10, crystalstone, 6);
		world.setBlock(x + 18, y + 0, z + 11, crystalstone, 11);
		world.setBlock(x + 18, y + 0, z + 12, crystalstone, 6);
		world.setBlock(x + 18, y + 0, z + 16, crystalstone, 6);
		world.setBlock(x + 18, y + 0, z + 17, crystalstone, 11);
		world.setBlock(x + 18, y + 0, z + 18, crystalstone, 6);
		world.setBlock(x + 18, y + 0, z + 20, cliff, cliffm);
		world.setBlock(x + 18, y + 1, z + 3, cliff, cliffm);
		world.setBlock(x + 18, y + 1, z + 7, Blocks.quartz_block);
		world.setBlock(x + 18, y + 1, z + 8, crystalstone);
		world.setBlock(x + 18, y + 1, z + 9, Blocks.quartz_block);
		world.setBlock(x + 18, y + 1, z + 13, Blocks.quartz_block);
		world.setBlock(x + 18, y + 1, z + 14, crystalstone);
		world.setBlock(x + 18, y + 1, z + 15, Blocks.quartz_block);
		world.setBlock(x + 18, y + 1, z + 19, cliff, cliffm);
		world.setBlock(x + 18, y + 7, z + 5, crystalstone, 1);
		world.setBlock(x + 18, y + 7, z + 11, crystalstone, 1);
		world.setBlock(x + 18, y + 7, z + 17, crystalstone, 1);
		world.setBlock(x + 19, y + 0, z + 2, cliff, cliffm);
		world.setBlock(x + 19, y + 0, z + 3, cliff, cliffm);
		world.setBlock(x + 19, y + 0, z + 9, crystalstone);
		world.setBlock(x + 19, y + 0, z + 10, crystalstone, 10);
		world.setBlock(x + 19, y + 0, z + 11, crystalstone);
		world.setBlock(x + 19, y + 0, z + 12, crystalstone, 10);
		world.setBlock(x + 19, y + 0, z + 13, crystalstone);
		world.setBlock(x + 19, y + 0, z + 19, cliff, cliffm);
		world.setBlock(x + 19, y + 0, z + 20, cliff, cliffm);
		world.setBlock(x + 19, y + 1, z + 4, cliff, cliffm);
		world.setBlock(x + 19, y + 1, z + 5, cliff, cliffm);
		world.setBlock(x + 19, y + 1, z + 6, cliff, cliffm);
		world.setBlock(x + 19, y + 1, z + 7, crystalstone);
		world.setBlock(x + 19, y + 1, z + 8, crystalstone);
		world.setBlock(x + 19, y + 1, z + 9, crystalstone);
		world.setBlock(x + 19, y + 1, z + 11, crystalstone, 8);
		world.setBlock(x + 19, y + 1, z + 13, crystalstone);
		world.setBlock(x + 19, y + 1, z + 14, crystalstone);
		world.setBlock(x + 19, y + 1, z + 15, crystalstone);
		world.setBlock(x + 19, y + 1, z + 16, cliff, cliffm);
		world.setBlock(x + 19, y + 1, z + 17, cliff, cliffm);
		world.setBlock(x + 19, y + 1, z + 18, cliff, cliffm);
		world.setBlock(x + 19, y + 2, z + 11, crystalstone, 2);
		world.setBlock(x + 19, y + 3, z + 11, crystalstone, 2);
		world.setBlock(x + 19, y + 4, z + 11, crystalstone, 2);
		world.setBlock(x + 19, y + 5, z + 11, crystalstone, 2);
		world.setBlock(x + 19, y + 6, z + 11, crystalstone, 2);
		world.setBlock(x + 19, y + 7, z + 10, crystalstone, 1);
		world.setBlock(x + 19, y + 7, z + 11, crystalstone, 7);
		world.setBlock(x + 19, y + 7, z + 12, crystalstone, 1);
		world.setBlock(x + 20, y + 0, z + 3, cliff, cliffm);
		world.setBlock(x + 20, y + 0, z + 4, cliff, cliffm);
		world.setBlock(x + 20, y + 0, z + 5, cliff, cliffm);
		world.setBlock(x + 20, y + 0, z + 6, cliff, cliffm);
		world.setBlock(x + 20, y + 0, z + 10, crystalstone, 6);
		world.setBlock(x + 20, y + 0, z + 11, crystalstone, 11);
		world.setBlock(x + 20, y + 0, z + 12, crystalstone, 6);
		world.setBlock(x + 20, y + 0, z + 16, cliff, cliffm);
		world.setBlock(x + 20, y + 0, z + 17, cliff, cliffm);
		world.setBlock(x + 20, y + 0, z + 18, cliff, cliffm);
		world.setBlock(x + 20, y + 0, z + 19, cliff, cliffm);
		world.setBlock(x + 20, y + 1, z + 7, cliff, cliffm);
		world.setBlock(x + 20, y + 1, z + 8, cliff, cliffm);
		world.setBlock(x + 20, y + 1, z + 9, crystalstone);
		world.setBlock(x + 20, y + 1, z + 13, crystalstone);
		world.setBlock(x + 20, y + 1, z + 14, cliff, cliffm);
		world.setBlock(x + 20, y + 1, z + 15, cliff, cliffm);
		world.setBlock(x + 20, y + 7, z + 11, crystalstone, 1);
		world.setBlock(x + 21, y + 0, z + 6, cliff, cliffm);
		world.setBlock(x + 21, y + 0, z + 7, cliff, cliffm);
		world.setBlock(x + 21, y + 0, z + 8, cliff, cliffm);
		world.setBlock(x + 21, y + 0, z + 11, crystalstone);
		world.setBlock(x + 21, y + 0, z + 14, cliff, cliffm);
		world.setBlock(x + 21, y + 0, z + 15, cliff, cliffm);
		world.setBlock(x + 21, y + 0, z + 16, cliff, cliffm);
		world.setBlock(x + 21, y + 1, z + 9, crystalstone, 6);
		world.setBlock(x + 21, y + 1, z + 10, crystalstone);
		world.setBlock(x + 21, y + 1, z + 12, crystalstone);
		world.setBlock(x + 21, y + 1, z + 13, crystalstone, 6);
		world.setBlock(x + 22, y + 0, z + 8, cliff, cliffm);
		world.setBlock(x + 22, y + 0, z + 9, cliff, cliffm);
		world.setBlock(x + 22, y + 0, z + 10, cliff, cliffm);
		world.setBlock(x + 22, y + 0, z + 11, cliff, cliffm);
		world.setBlock(x + 22, y + 0, z + 12, cliff, cliffm);
		world.setBlock(x + 22, y + 0, z + 13, cliff, cliffm);
		world.setBlock(x + 22, y + 0, z + 14, cliff, cliffm);

		this.setEmpty(world, x + 11, y + 2, z + 11);
		this.setEmpty(world, x + 11, y + 2, z + 10);
		this.setEmpty(world, x + 11, y + 2, z + 12);
		this.setEmpty(world, x + 12, y + 2, z + 11);
		this.setEmpty(world, x + 10, y + 2, z + 11);
		this.setEmpty(world, x + 11, y + 2, z + 13);
		this.setEmpty(world, x + 13, y + 2, z + 11);
		this.setEmpty(world, x + 9, y + 2, z + 11);
		this.setEmpty(world, x + 11, y + 2, z + 9);
		this.setEmpty(world, x + 9, y + 1, z + 11);
		this.setEmpty(world, x + 11, y + 1, z + 9);
		this.setEmpty(world, x + 11, y + 1, z + 13);
		this.setEmpty(world, x + 13, y + 1, z + 11);

		world.addBlock(x + 9, y + 1, z + 11, Blocks.stonebrick);
		world.addBlock(x + 11, y + 1, z + 9, Blocks.stonebrick);
		world.addBlock(x + 11, y + 1, z + 13, Blocks.stonebrick);
		world.addBlock(x + 13, y + 1, z + 11, Blocks.stonebrick);

		this.setEmpty(world, x + 10, y + 1, z + 2);
		this.setEmpty(world, x + 10, y + 1, z + 3);
		this.setEmpty(world, x + 10, y + 1, z + 4);
		this.setEmpty(world, x + 6, y + 1, z + 4);
		this.setEmpty(world, x + 6, y + 1, z + 5);
		this.setEmpty(world, x + 6, y + 1, z + 6);
		this.setEmpty(world, x + 5, y + 1, z + 4);
		this.setEmpty(world, x + 3, y + 1, z + 10);
		this.setEmpty(world, x + 2, y + 1, z + 10);
		this.setEmpty(world, x + 2, y + 1, z + 11);
		this.setEmpty(world, x + 2, y + 1, z + 12);
		this.setEmpty(world, x + 3, y + 1, z + 12);
		this.setEmpty(world, x + 4, y + 1, z + 4);
		this.setEmpty(world, x + 4, y + 1, z + 5);
		this.setEmpty(world, x + 4, y + 1, z + 6);
		this.setEmpty(world, x + 4, y + 1, z + 10);
		this.setEmpty(world, x + 4, y + 1, z + 11);
		this.setEmpty(world, x + 4, y + 1, z + 12);
		this.setEmpty(world, x + 4, y + 1, z + 16);
		this.setEmpty(world, x + 4, y + 1, z + 17);
		this.setEmpty(world, x + 4, y + 1, z + 18);
		this.setEmpty(world, x + 5, y + 1, z + 6);
		this.setEmpty(world, x + 5, y + 1, z + 16);
		this.setEmpty(world, x + 5, y + 1, z + 18);
		this.setEmpty(world, x + 6, y + 1, z + 16);
		this.setEmpty(world, x + 6, y + 1, z + 17);
		this.setEmpty(world, x + 6, y + 1, z + 18);
		this.setEmpty(world, x + 10, y + 1, z + 18);
		this.setEmpty(world, x + 10, y + 1, z + 19);
		this.setEmpty(world, x + 10, y + 1, z + 20);
		this.setEmpty(world, x + 11, y + 1, z + 2);
		this.setEmpty(world, x + 11, y + 1, z + 4);
		this.setEmpty(world, x + 11, y + 1, z + 18);
		this.setEmpty(world, x + 11, y + 1, z + 20);
		this.setEmpty(world, x + 12, y + 1, z + 2);
		this.setEmpty(world, x + 12, y + 1, z + 3);
		this.setEmpty(world, x + 12, y + 1, z + 4);
		this.setEmpty(world, x + 12, y + 1, z + 18);
		this.setEmpty(world, x + 12, y + 1, z + 19);
		this.setEmpty(world, x + 12, y + 1, z + 20);
		this.setEmpty(world, x + 16, y + 1, z + 16);
		this.setEmpty(world, x + 16, y + 1, z + 17);
		this.setEmpty(world, x + 16, y + 1, z + 18);
		this.setEmpty(world, x + 16, y + 1, z + 4);
		this.setEmpty(world, x + 16, y + 1, z + 5);
		this.setEmpty(world, x + 16, y + 1, z + 6);
		this.setEmpty(world, x + 17, y + 1, z + 4);
		this.setEmpty(world, x + 17, y + 1, z + 6);
		this.setEmpty(world, x + 17, y + 1, z + 16);
		this.setEmpty(world, x + 17, y + 1, z + 18);
		this.setEmpty(world, x + 18, y + 1, z + 10);
		this.setEmpty(world, x + 18, y + 1, z + 11);
		this.setEmpty(world, x + 18, y + 1, z + 12);
		this.setEmpty(world, x + 18, y + 1, z + 4);
		this.setEmpty(world, x + 18, y + 1, z + 5);
		this.setEmpty(world, x + 18, y + 1, z + 6);
		this.setEmpty(world, x + 18, y + 1, z + 16);
		this.setEmpty(world, x + 18, y + 1, z + 17);
		this.setEmpty(world, x + 18, y + 1, z + 18);
		this.setEmpty(world, x + 19, y + 1, z + 10);
		this.setEmpty(world, x + 19, y + 1, z + 12);
		this.setEmpty(world, x + 20, y + 1, z + 10);
		this.setEmpty(world, x + 20, y + 1, z + 11);
		this.setEmpty(world, x + 20, y + 1, z + 12);

		this.setPlug(world, x + 1, y + 1, z + 11);
		this.setPlug(world, x + 21, y + 1, z + 11);
		this.setPlug(world, x + 11, y + 1, z + 1);
		this.setPlug(world, x + 11, y + 1, z + 21);

		return world;
	}

	private void setPlug(FilledBlockArray world, int x, int y, int z) {
		//this.setEmpty(world, x, y, z);
		world.setBlock(x, y, z, crystalstone, StoneTypes.MULTICHROMIC.ordinal());
	}

	private void setEmpty(FilledBlockArray world, int x, int y, int z) {
		world.setEmpty(x, y, z, false, false);
		world.addBlock(x, y, z, fluid);
	}

}

package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.EmitterTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.LaserEffectType;
import Reika.ChromatiCraft.Block.Dimension.Structure.PistonTape.BlockPistonTapeBit;
import Reika.ChromatiCraft.Block.Dimension.Structure.PistonTape.BlockPistonTarget.PistonEmitterTile;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.PistonTapeLoop.LoopDimensions;
import Reika.DragonAPI.Instantiable.RGBColorData;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;

public class PistonTapeSlice extends StructurePiece<PistonTapeGenerator> {

	private final ForgeDirection facing;
	public final int bitCount;

	private final PistonTapeLoop loop;
	private final LoopSliceDimensions dimensions;
	private final int busIndex;

	private final HashMap<ForgeDirection, Coordinate> pistons = new HashMap();
	Coordinate emitter;
	Coordinate target;
	Coordinate zeroBit;

	private final RGBColorData[] colorCycle;

	private boolean firedVerticalLast;
	private boolean needsReset;
	private int cooldown;
	private boolean isHalfCycled = false;

	protected PistonTapeSlice(PistonTapeGenerator g, ForgeDirection dir, int idx, PistonTapeLoop p, LoopDimensions size) {
		super(g);
		busIndex = idx;
		facing = dir;
		dimensions = size.slice(busIndex);
		bitCount = dimensions.bitLength;
		loop = p;
		colorCycle = new RGBColorData[bitCount];
	}

	/** Returns true if ready to handle another pulse */
	public boolean cycle(World world) {
		if (cooldown > 0) {
			cooldown--;
			return false;
		}
		else {
			cooldown = 6;
			if (needsReset) {
				for (Coordinate c : pistons.values()) {
					c.triggerBlockUpdate(world, false);
				}
				//ReikaJavaLibrary.pConsole("Resetting; "+isHalfCycled);
				needsReset = false;
				return !isHalfCycled;
			}
			else {
				needsReset = true;
				if (firedVerticalLast) {
					Coordinate c = pistons.get(facing);
					ReikaBlockHelper.extendPiston(world, c.xCoord, c.yCoord, c.zCoord);
					c = pistons.get(facing.getOpposite());
					ReikaBlockHelper.extendPiston(world, c.xCoord, c.yCoord, c.zCoord);
					//ReikaJavaLibrary.pConsole("Fired horizontal");
				}
				else {
					Coordinate c = pistons.get(ForgeDirection.UP);
					ReikaBlockHelper.extendPiston(world, c.xCoord, c.yCoord, c.zCoord);
					c = pistons.get(ForgeDirection.DOWN);
					ReikaBlockHelper.extendPiston(world, c.xCoord, c.yCoord, c.zCoord);
					//ReikaJavaLibrary.pConsole("Fired vertical");
				}
				firedVerticalLast = !firedVerticalLast;
				isHalfCycled = !isHalfCycled;
				return false;
			}
		}
	}

	void randomizeSolution(Random rand) {
		for (int i = 0; i < loop.doorCount(); i++) { //not entire array
			RGBColorData c = this.genRandomColor(i, rand);
			colorCycle[i] = c;
		}
	}

	RGBColorData getColor(int pos) {
		return colorCycle[pos];
	}

	/** Assuming "zeroed" piston loop "read head" */
	public Coordinate getNthBitBlock(int i) {
		return dimensions.getNthBitPosition(i);
	}

	private RGBColorData genRandomColor(int i, Random rand) {
		int i2 = i-this.getSecondFilterOffset();
		while (i2 < 0) {
			i2 += colorCycle.length;
		}
		RGBColorData mix = colorCycle[i2];
		if (mix == null)
			mix = RGBColorData.white();
		RGBColorData c = ReikaJavaLibrary.getRandomCollectionEntry(rand, mix.getReductiveChildren(true, true));
		if (c.matchColor(mix)) {
			return ReikaJavaLibrary.getRandomCollectionEntry(rand, mix.getAdditiveChildren(true, true));
		}
		else {
			return mix.getColorNeededToMake(c);
		}
	}

	private int getSecondFilterOffset() {
		return dimensions.totalDepth;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		for (int d = -1; d <= dimensions.totalDepth+2; d++) { //was from -2
			for (int h = -2; h <= dimensions.totalHeight+2; h++) {
				if (d == -1 && h < dimensions.totalHeight)
					continue;
				Block b = Blocks.air;
				int m = 0;
				if (h >= dimensions.totalHeight) {
					b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
					m = BlockType.STONE.metadata;
				}
				if (h < 0) {
					b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
					m = BlockType.CLOAK.metadata;
				}
				if (d == -2 || d > dimensions.totalDepth) {
					b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
					m = BlockType.STONE.metadata;
				}
				if (d == -2 && h >= 0 && h <= dimensions.totalHeight-1) {
					b = ChromaBlocks.SPECIALSHIELD.getBlockInstance();
					m = BlockType.GLASS.metadata;
				}
				if (d == 0 && h == dimensions.totalHeight) {
					b = Blocks.air;
					m = 0;
				}
				world.setBlock(x-d*facing.offsetX, y+h, z-d*facing.offsetZ, b, m);
			}
		}

		this.generateTape(world, x, y, z);

		this.placePiston(world, x, y-1, z, ForgeDirection.UP);
		this.placePiston(world, x+facing.offsetX, y+dimensions.totalHeight, z+facing.offsetZ, facing.getOpposite());
		this.placePiston(world, x-facing.offsetX*dimensions.totalDepth, y+dimensions.totalHeight+1, z-facing.offsetZ*dimensions.totalDepth, ForgeDirection.DOWN);
		this.placePiston(world, x-facing.offsetX*(dimensions.totalDepth+1), y, z-facing.offsetZ*(dimensions.totalDepth+1), facing);

		this.placeEmitter(world, x-facing.offsetX*(dimensions.totalDepth+1), y+1, z-facing.offsetZ*(dimensions.totalDepth+1));
		this.placeTarget(world, x+facing.offsetX*7, y+1, z+facing.offsetZ*7);
	}

	private void generateTape(ChunkSplicedGenerationCache world, int x, int y, int z) {
		zeroBit = new Coordinate(x, y, z);
		dimensions.calculatePositions(zeroBit.xCoord, zeroBit.yCoord, zeroBit.zCoord, 1, facing, true);

		for (int d = 0; d <= dimensions.totalDepth; d++) {
			for (int h = 0; h <= dimensions.totalHeight; h++) {
				if (d == 0 || d == dimensions.totalDepth || h == 0 || h == dimensions.totalHeight) {
					if (d == 0 && h == dimensions.totalHeight)
						continue;
					if (d == dimensions.totalDepth && h == 0)
						continue;
					this.placeBit(world, x-d*facing.offsetX, y+h, z-d*facing.offsetZ);
				}
			}
		}

		if (true || false) {
			for (int i = 0; i < dimensions.bitLength; i++) {
				if (colorCycle[i] == null)
					continue;
				Coordinate c = dimensions.getNthBitPosition(i);
				int m = BlockPistonTapeBit.getMetaFor(colorCycle[i], true);
				world.setBlock(c.xCoord, c.yCoord, c.zCoord, ChromaBlocks.PISTONBIT.getBlockInstance(), m);
			}
		}
	}

	private void placeBit(ChunkSplicedGenerationCache world, int x, int y, int z) {
		world.setBlock(x, y, z, ChromaBlocks.PISTONBIT.getBlockInstance(), 0);
	}

	private void placePiston(ChunkSplicedGenerationCache world, int x, int y, int z, ForgeDirection dir) {
		pistons.put(dir, new Coordinate(x, y, z));
		world.setBlock(x, y, z, Blocks.piston, dir.ordinal());
	}

	private void placeEmitter(ChunkSplicedGenerationCache world, int x, int y, int z) {
		emitter = new Coordinate(x, y, z);
		world.setTileEntity(x, y, z, ChromaBlocks.LASEREFFECT.getBlockInstance(), LaserEffectType.EMITTER.ordinal(), new PistonEmitterCallback(this));
	}

	private void placeTarget(ChunkSplicedGenerationCache world, int x, int y, int z) {
		target = new Coordinate(x, y, z);
		world.setTileEntity(x, y, z, ChromaBlocks.PISTONTARGET.getBlockInstance(), 0, new PistonTargetCallback(this));
	}

	private static class PistonTargetCallback implements TileCallback {

		private final UUID uid;
		private final ForgeDirection facing;
		private final int index;
		private final int width;

		private PistonTargetCallback(PistonTapeSlice p) {
			//ReikaJavaLibrary.spamConsole(p+" & "+p.facing);
			uid = p.parent.id;
			index = p.busIndex;
			facing = p.facing;
			width = p.loop.busWidth;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			PistonEmitterTile e = (PistonEmitterTile)te;
			e.setData(facing, index, width);
			e.uid = uid;
		}

	}

	private static class PistonEmitterCallback implements TileCallback {

		private final UUID uid;
		private final ForgeDirection facing;
		//private final int index;

		private PistonEmitterCallback(PistonTapeSlice p) {
			uid = p.parent.id;
			//index = p.busIndex;
			facing = p.facing;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			EmitterTile e = (EmitterTile)te;
			e.uid = uid;
			e.setDirection(CubeDirections.getFromForgeDirection(facing));
			e.setColor(new RGBColorData(true, true, true)); //always white, NOT colored
			e.speedFactor = 1.25;
			e.uid = uid;
			e.silent = true;
			e.renderAsFullBlock = true;
		}

	}

	static class LoopSliceDimensions extends LoopDimensions {

		public final int index;
		private final Coordinate[] positions;
		private Coordinate rootPosition;

		LoopSliceDimensions(int idx, int d, int h) {
			super(d, h);
			index = idx;
			positions = new Coordinate[bitLength];
		}

		public void calculatePositions(int x, int y, int z, int initialBitIndex, ForgeDirection facing, boolean skipFirstAndThirdCorners) {
			rootPosition = new Coordinate(x, y, z);
			Coordinate c = rootPosition;
			ForgeDirection dir = facing.getOpposite();
			int n = 0;
			for (int i = 0; i < bitLength; i++) {
				n++;
				c = c.offset(dir, 1);
				if (n == totalDepth) {
					dir = ForgeDirection.UP;
					if (skipFirstAndThirdCorners) {
						i--;
						continue;
					}
				}
				else if (n == totalDepth+totalHeight) {
					dir = facing;
					if (!skipFirstAndThirdCorners) {
						i--;
						continue;
					}
				}
				else if (n == totalDepth+totalHeight+totalDepth) {
					dir = ForgeDirection.DOWN;
					if (skipFirstAndThirdCorners) {
						i--;
						continue;
					}
				}
				else if (n == totalDepth+totalHeight+totalDepth+totalHeight) {
					dir = facing.getOpposite();
					if (!skipFirstAndThirdCorners) {
						i--;
						continue;
					}
				}
				positions[i] = c;
			}
			for (int i = 0; i <= initialBitIndex; i++) {
				ReikaArrayHelper.cycleArray(positions, positions[positions.length-1]);
			}
		}

		public Coordinate getNthBitPosition(int n) {
			return positions[n];
		}

	}

}

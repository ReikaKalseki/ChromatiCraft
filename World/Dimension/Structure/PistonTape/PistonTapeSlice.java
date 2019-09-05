package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.ColorData;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.EmitterTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.LaserEffectType;
import Reika.ChromatiCraft.Block.Dimension.Structure.PistonTape.BlockPistonTarget.PistonEmitterTile;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.PistonTapeLoop.LoopDimensions;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;

public class PistonTapeSlice extends StructurePiece<PistonTapeGenerator> {

	private final ForgeDirection facing;
	public final int bitCount;

	private final PistonTapeLoop loop;
	private final LoopDimensions dimensions;
	private final int busIndex;

	private final HashMap<ForgeDirection, Coordinate> pistons = new HashMap();
	Coordinate emitter;
	Coordinate target;

	private boolean firedVerticalLast;
	private boolean needsReset;
	private int cooldown;

	protected PistonTapeSlice(PistonTapeGenerator g, ForgeDirection dir, int idx, PistonTapeLoop p, LoopDimensions size) {
		super(g);
		busIndex = idx;
		facing = dir;
		dimensions = size;
		bitCount = dimensions.bitLength;
		loop = p;
	}

	public void cycle(World world) {
		if (cooldown > 0) {
			cooldown--;
			return;
		}
		cooldown = 6;
		if (needsReset) {
			for (Coordinate c : pistons.values()) {
				c.triggerBlockUpdate(world, false);
			}
		}
		else {
			if (firedVerticalLast) {
				Coordinate c = pistons.get(facing);
				ReikaBlockHelper.extendPiston(world, c.xCoord, c.yCoord, c.zCoord);
				c = pistons.get(facing.getOpposite());
				ReikaBlockHelper.extendPiston(world, c.xCoord, c.yCoord, c.zCoord);
			}
			else {
				Coordinate c = pistons.get(ForgeDirection.UP);
				ReikaBlockHelper.extendPiston(world, c.xCoord, c.yCoord, c.zCoord);
				c = pistons.get(ForgeDirection.DOWN);
				ReikaBlockHelper.extendPiston(world, c.xCoord, c.yCoord, c.zCoord);
			}
			firedVerticalLast = !firedVerticalLast;
		}
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
		this.placePiston(world, x, y-1, z, ForgeDirection.UP);
		this.placePiston(world, x+facing.offsetX, y+dimensions.totalHeight, z+facing.offsetZ, facing.getOpposite());
		this.placePiston(world, x-facing.offsetX*dimensions.totalDepth, y+dimensions.totalHeight+1, z-facing.offsetZ*dimensions.totalDepth, ForgeDirection.DOWN);
		this.placePiston(world, x-facing.offsetX*(dimensions.totalDepth+1), y, z-facing.offsetZ*(dimensions.totalDepth+1), facing);

		this.placeEmitter(world, x-facing.offsetX*(dimensions.totalDepth+1), y+1, z-facing.offsetZ*(dimensions.totalDepth+1));
		this.placeTarget(world, x+facing.offsetX*7, y+1, z+facing.offsetZ*7);
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
			e.setColor(new ColorData(true, true, true)); //always white, NOT colored
			e.speedFactor = 1.25;
			e.uid = uid;
			e.silent = true;
			e.renderAsFullBlock = true;
		}

	}

}

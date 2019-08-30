package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import java.util.HashSet;
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
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.PistonTapeLoop.LoopDimensions;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionType;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;

public class PistonTapeSlice extends StructurePiece<PistonTapeGenerator> {

	private final ForgeDirection facing;
	public final int bitCount;
	private PistonTapeLoop loop;
	private final LoopDimensions dimensions;

	private final MultiMap<ForgeDirection, Coordinate> pistons = new MultiMap(CollectionType.HASHSET);
	private final HashSet<Coordinate> emitters = new HashSet();

	protected PistonTapeSlice(ForgeDirection dir, PistonTapeGenerator g, PistonTapeLoop p, LoopDimensions size) {
		super(g);
		facing = dir;
		dimensions = size;
		bitCount = dimensions.bitLength;
	}

	public void cycle() {

	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		world.setBlock(x, y, z, Blocks.brick_block);
		for (int d = -2; d <= dimensions.totalDepth+2; d++) {
			for (int h = -2; h <= dimensions.totalHeight+2; h++) {
				Block b = Blocks.air;
				int m = 0;
				if (h == dimensions.totalHeight+2) {
					b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
					m = BlockType.STONE.metadata;
				}
				if (h == -2) {
					b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
					m = BlockType.CLOAK.metadata;
				}
				if (d == -2 || d == dimensions.totalDepth+2) {
					b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
					m = BlockType.STONE.metadata;
				}
				if (d == -2 && h >= 0 && h <= dimensions.totalHeight-1) {
					b = ChromaBlocks.SPECIALSHIELD.getBlockInstance();
					m = BlockType.GLASS.metadata;
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
	}

	private void placeBit(ChunkSplicedGenerationCache world, int x, int y, int z) {
		world.setBlock(x, y, z, ChromaBlocks.PISTONBIT.getBlockInstance(), 0);
	}

	private void placePiston(ChunkSplicedGenerationCache world, int x, int y, int z, ForgeDirection dir) {
		pistons.addValue(dir, new Coordinate(x, y, z));
		world.setBlock(x, y, z, Blocks.piston, dir.ordinal());
	}

	private void placeEmitter(ChunkSplicedGenerationCache world, int x, int y, int z) {
		emitters.add(new Coordinate(x, y, z));
		world.setTileEntity(x, y, z, ChromaBlocks.LASEREFFECT.getBlockInstance(), LaserEffectType.EMITTER.ordinal(), new PistonEmitterCallback(this));
	}

	private static class PistonEmitterCallback implements TileCallback {

		private final UUID uid;
		private final ForgeDirection direction;

		private PistonEmitterCallback(PistonTapeSlice p) {
			direction = p.facing;
			uid = p.parent.id;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			EmitterTile e = (EmitterTile)te;
			e.setDirection(CubeDirections.getFromForgeDirection(direction));
			e.setColor(new ColorData(true, true, true));
			e.speedFactor = 1.25;
			e.uid = uid;
			e.silent = true;
			e.renderAsFullBlock = true;
		}

	}

}

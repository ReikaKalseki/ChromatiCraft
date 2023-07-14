package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureTileCallback;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Dimension.Structure.PistonTape.BlockPistonController.TilePistonController;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class PistonTapeEntryArea extends StructurePiece<PistonTapeGenerator> {

	//public static final int WIDTH = 11;
	//public static final int HEIGHT = 6;
	public static final int DEPTH = 3;

	private final TapeArea tape;
	private final TapeStage level;

	public PistonTapeEntryArea(PistonTapeGenerator gen, TapeStage lvl, TapeArea t) {
		super(gen);
		level = lvl;
		tape = t;
	}

	public int getWidth() {
		return 7+tape.tape.dimensions.totalDepth;
	}

	public int getHeight() {
		return 2+tape.tape.dimensions.totalHeight;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		ForgeDirection dir = tape.hallDirection;
		ForgeDirection left = tape.tape.facing.getOpposite();

		int w = this.getWidth();
		int h = this.getHeight();

		for (int i = 0; i <= DEPTH; i++) {
			for (int k = 0; k <= w; k++) {
				for (int j = 0; j <= h; j++) {
					int dx = x+i*dir.offsetX+k*left.offsetX;
					int dz = z+i*dir.offsetZ+k*left.offsetZ;
					int dy = y+j;

					Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
					int m = BlockType.STONE.metadata;

					boolean air = k > 1 && k < w-1 && i > 0 && i < DEPTH && j > 1 && j < h;
					air |= (i == 0 || i == DEPTH) && k >= 1 && k <= 3 && j >= 1 && j <= 3;
					air |= k >= 1 && k <= 3 && j >= 1 && j < h && i > 0 && i < DEPTH;
					air |= i == DEPTH && k >= 5 && k < w-1 && j > 1 && j < h;
					air |= k > 0 && k <= 4 && j >= 2 && j < h && i == DEPTH;

					if (air) {
						b = Blocks.air;
						m = 0;
					}

					if (i == 2) {
						if (k == 0 && j == Math.max(4, 4+(h-4)/2))
							m = BlockType.LIGHT.metadata;
						else if (k == w-1 && j == 3)
							m = BlockType.LIGHT.metadata;
						else if (k == 4 && j == h)
							m = BlockType.LIGHT.metadata;
					}

					if (i > 0 && i <= DEPTH && j == 1) {
						if (k == 4) {
							b = Blocks.stone_slab;
							m = ReikaItemHelper.stonebrickSlab.metadata;
						}
						else if (k == 5) {
							b = Blocks.stonebrick;
							m = 0;
						}
					}

					if (level.index == 0 && j == 3) {
						if (i == 2 && k == w-1) {
							b = ChromaBlocks.DIMDATA.getBlockInstance();
						}
						else if (i == 0 && k == w-3) {
							b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
							m = BlockType.LIGHT.metadata;
						}
					}

					if (b == ChromaBlocks.DIMDATA.getBlockInstance())
						parent.generatePasswordTile(dx, dy, dz);
					else
						world.setBlock(dx, dy, dz, b, m);
				}
			}
		}

		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int m = BlockType.STONE.metadata;
		for (int k = 0; k <= 4; k++) {
			for (int j = 4; j <= h; j++) {
				int i = DEPTH+1;
				int dx = x+i*dir.offsetX+k*left.offsetX;
				int dz = z+i*dir.offsetZ+k*left.offsetZ;
				int dy = y+j;
				world.setBlock(dx, dy, dz, b, m);
			}
		}

		int dx = x+tape.hallDirection.offsetX*3;
		int dz = z+tape.hallDirection.offsetZ*3;

		int dx2 = dx-tape.hallDirection.offsetX*2;
		int dz2 = dz-tape.hallDirection.offsetZ*2;

		world.setTileEntity(dx, y+3, dz, ChromaBlocks.PISTONCONTROL.getBlockInstance(), 0, new PistonControlCallback(level));
		world.setTileEntity(dx, y+2, dz, ChromaBlocks.PISTONCONTROL.getBlockInstance(), 1, new PistonControlCallback(level));
		world.setTileEntity(dx, y+1, dz, ChromaBlocks.PISTONCONTROL.getBlockInstance(), 2, new PistonControlCallback(level));

		world.setTileEntity(dx2, y+2, dz2, ChromaBlocks.PISTONCONTROL.getBlockInstance(), 3, new PistonControlCallback(level));
	}

	private static class PistonControlCallback extends DimensionStructureTileCallback {

		private final UUID uid;
		private final int index;
		private final ForgeDirection direction;

		private PistonControlCallback(TapeStage t) {
			uid = t.getID();
			index = t.index;
			direction = ReikaDirectionHelper.getLeftBy90(t.mainDirection);
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			((TilePistonController)te).setData(index, direction);
			((TilePistonController)te).uid = uid;
		}

	}
}

package Reika.ChromatiCraft.Auxiliary.Structure.Worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.BiomeStructurePuzzle;
import Reika.ChromatiCraft.Base.FragmentStructureBase;
import Reika.ChromatiCraft.Block.BlockHoverBlock.HoverType;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl.InteractionDelegateTile;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;


public class BiomeStructure extends FragmentStructureBase {

	private BiomeStructurePuzzle puzzle;

	@Override
	public void resetToDefaults() {
		super.resetToDefaults();
		//puzzle = null;
	}

	@Override
	public void setRNG(Random r) {
		puzzle = new BiomeStructurePuzzle();
		puzzle.generate(r);
	}

	@Override
	public Coordinate getControllerRelativeLocation() {
		return new Coordinate(0, 0, 0);
	}

	@Override
	public int getStructureVersion() {
		return 0;
	}

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		if (puzzle == null)
			throw new RuntimeException("Puzzle not set!");
		FilledBlockArray array = new FilledBlockArray(world);
		Coordinate c = new Coordinate(x, y, z);
		array.setBlock(x, y, z, ChromaTiles.STRUCTCONTROL.getBlock(), ChromaTiles.STRUCTCONTROL.getBlockMetadata());
		//ControllerDelegateCallback del = new ControllerDelegateCallback(c);
		this.addCallback(c, new PuzzleCacheCallback(puzzle));
		for (int j = -2; j <= 6; j++) {
			int r = j == -2 ? 1 : 2;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					if (j == 0 && i == 0 && k == 0)
						continue;
					int d = Math.max(Math.abs(i), Math.abs(k));
					Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
					int m = BlockType.CLOAK.metadata;
					if (j == -1 && d <= 1) {
						b = ChromaBlocks.CHROMA.getBlockInstance();
						m = 0;
					}
					else if (j == 0) {
						if (d <= 1) {
							b = Blocks.air;
							m = 0;
						}
						else {
							m = BlockType.STONE.metadata;
						}
					}
					else if (j == 1) {
						if (d <= 1) {
							b = ChromaBlocks.DOOR.getBlockInstance();
							m = 0;
						}
						else {
							m = BlockType.COBBLE.metadata;
						}
					}
					else if (d <= 1) {
						b = ChromaBlocks.HOVER.getBlockInstance();
						m = HoverType.ELEVATE.getPermanentMeta();
					}
					else if (j > 0) {
						if (j == 6 && d > 1) {
							m = BlockType.COBBLE.metadata;
						}
						else {
							b = Blocks.air;
							m = 0;
						}
					}
					array.setBlock(x+i, y+j, z+k, b, m);
				}
			}
		}
		for (int j = 1; j <= 9; j++) {
			for (int i = -3; i <= 3; i++) {
				for (int k = -3; k <= 3; k++) {
					if (j > 6 || Math.abs(i) == 3 || Math.abs(k) == 3) {
						Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
						int m = BlockType.STONE.metadata;
						if (j > 6) {
							b = Blocks.air;
							m = 0;
						}
						else {
							if ((i == 0 || k == 0) && j > 1 && j < 6) {
								m = BlockType.COBBLE.metadata;
							}
							else if (j == 6 && (Math.abs(i) == 2 || Math.abs(k) == 2)) {
								m = BlockType.COBBLE.metadata;
							}
							else if (j == 3 && (Math.abs(i) == 1 || Math.abs(k) == 1)) {
								m = BlockType.GLASS.metadata;
							}
						}
						array.setBlock(x+i, y+j, z+k, b, m);
					}
				}
			}
		}
		for (int j = 6; j <= 9; j++) {
			for (int d = -4; d <= 4; d++) {
				Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
				int m = BlockType.COBBLE.metadata;
				if (Math.abs(d) <= 1) {
					b = j == 6 || j == 9 ? Blocks.stone_brick_stairs : Blocks.air;
					m = 0;
				}
				array.setBlock(x+d, y+j, z+4, b, m);
				array.setBlock(x+d, y+j, z-4, b, m);
				array.setBlock(x+4, y+j, z+d, b, m);
				array.setBlock(x-4, y+j, z+d, b, m);
			}
		}
		for (int j = 5; j <= 8; j++) {
			for (int d = -6; d <= 6; d++) {
				Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
				int m = BlockType.STONE.metadata;
				if (Math.abs(d) <= 1) {
					b = j == 5 ? Blocks.stone_brick_stairs : Blocks.air;
					m = 0;
				}
				else if (Math.abs(d) == 2) {
					m = BlockType.COBBLE.metadata;
				}
				else if ((j == 7 || j == 8) && Math.abs(d) >= 3 && Math.abs(d) <= 5) {
					m = BlockType.GLASS.metadata;
				}
				array.setBlock(x+d, y+j, z+7, b, m);
				array.setBlock(x+d, y+j, z-7, b, m);
				array.setBlock(x+7, y+j, z+d, b, m);
				array.setBlock(x-7, y+j, z+d, b, m);
			}
		}
		for (int i = -6; i <= 6; i++) {
			for (int k = -6; k <= 6; k++) {
				int d = Math.max(Math.abs(i), Math.abs(k));
				if (d >= 4 && d <= 6) {
					array.setBlock(x+i, y+5, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				}
			}
		}
		//this.addCallback(?, del);
		return array;
	}

	@Override
	public void onPlace(World world, TileEntityStructControl te) {
		puzzle.placeData(world, te);
	}

	private static class PuzzleCacheCallback implements TileCallback {

		private final BiomeStructurePuzzle puzzle;

		private PuzzleCacheCallback(BiomeStructurePuzzle p) {
			puzzle = p;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			((TileEntityStructControl)te).setStructureData(puzzle);
		}

	}

	private static class ControllerDelegateCallback implements TileCallback {

		private final Coordinate controllerLocation;

		private ControllerDelegateCallback(Coordinate c) {
			controllerLocation = c;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			((InteractionDelegateTile)te).setDelegate(controllerLocation);
		}

	}

}

package Reika.ChromatiCraft.Auxiliary.Structure.Worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.BiomeStructurePuzzle;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Base.FragmentStructureWithBonusLoot;
import Reika.ChromatiCraft.Block.BlockHoverBlock.HoverType;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl.InteractionDelegateTile;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;


public class BiomeStructure extends FragmentStructureWithBonusLoot {

	private BiomeStructurePuzzle puzzle;

	public BiomeStructure() {
		this.addBonusItem(new ItemStack(Items.ender_pearl), 2, 8, 25);
		this.addBonusItem(new ItemStack(Items.redstone), 12, 24, 20);
		this.addBonusItem(new ItemStack(Items.diamond), 1, 5, 10);
		this.addBonusItem(new ItemStack(Items.gold_ingot), 4, 18, 10);
		this.addBonusItem(new ItemStack(Items.iron_ingot), 12, 32, 20);
		this.addBonusItem(new ItemStack(Items.quartz), 2, 16, 10);
		this.addBonusItem(new ItemStack(Items.emerald), 1, 4, 5);
		this.addBonusItem(new ItemStack(Items.blaze_powder), 2, 8, 10);
		this.addBonusItem(new ItemStack(Items.gunpowder), 4, 12, 15);
		this.addBonusItem(new ItemStack(Items.slime_ball), 1, 4, 5);
		this.addBonusItem(new ItemStack(Items.glowstone_dust), 4, 32, 15);

		for (int i = 0; i < 32; i++) {
			boolean charge = i >= 16;
			int min = charge ? 1 : 4;
			int max = charge ? 6 : 24;
			this.addBonusItem(ChromaItems.SHARD.getStackOfMetadata(i), min, max, charge ? 2 : 6);
		}

		this.addBonusItem(ChromaStacks.chromaDust, 16, 48, 30);
		this.addBonusItem(ChromaStacks.auraDust, 8, 32, 25);
		this.addBonusItem(ChromaStacks.bindingCrystal, 4, 20, 18);
		this.addBonusItem(ChromaStacks.focusDust, 4, 16, 10);
		this.addBonusItem(ChromaStacks.purityDust, 12, 16, 10);
		this.addBonusItem(ChromaStacks.beaconDust, 16, 32, 25);
	}

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
	protected void initDisplayData() {
		Random r = new Random(0);
		r.nextBoolean();
		r.nextBoolean();
		this.setRNG(r);
	}

	@Override
	protected void finishDisplayCall() {
		puzzle = null;
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
						b = Blocks.air;//ChromaBlocks.CHROMA.getBlockInstance();
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
					else if (j > 0 && d <= 1) {
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
				if (j != 9 && Math.abs(d) == 4)
					continue;
				if (Math.abs(d) <= 1) {
					b = j == 6 || j == 9 ? Blocks.stone_brick_stairs : Blocks.air;
					m = 0;
				}
				if (m == BlockType.COBBLE.metadata && (j == 9 || Math.abs(d) > 2))
					m = BlockType.STONE.metadata;
				array.setBlock(x+d, y+j, z+4, b, b == Blocks.stone_brick_stairs ? this.getStairMeta(d, j, 4) : m);
				array.setBlock(x+d, y+j, z-4, b, b == Blocks.stone_brick_stairs ? this.getStairMeta(d, j, -4) : m);
				array.setBlock(x+4, y+j, z+d, b, b == Blocks.stone_brick_stairs ? this.getStairMeta(4, j, d) : m);
				array.setBlock(x-4, y+j, z+d, b, b == Blocks.stone_brick_stairs ? this.getStairMeta(-4, j, d) : m);
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
				array.setBlock(x+d, y+j, z+7, b, b == Blocks.stone_brick_stairs ? this.getStairMeta(d, j, 7) : m);
				array.setBlock(x+d, y+j, z-7, b, b == Blocks.stone_brick_stairs ? this.getStairMeta(d, j, -7) : m);
				array.setBlock(x+7, y+j, z+d, b, b == Blocks.stone_brick_stairs ? this.getStairMeta(7, j, d) : m);
				array.setBlock(x-7, y+j, z+d, b, b == Blocks.stone_brick_stairs ? this.getStairMeta(-7, j, d) : m);

				if (j == 4) {
					array.setBlock(x+d, y+j, z+7, Blocks.dirt);
					array.setBlock(x+d, y+j, z-7, Blocks.dirt);
					array.setBlock(x+7, y+j, z+d, Blocks.dirt);
					array.setBlock(x-7, y+j, z+d, Blocks.dirt);
				}
			}
		}
		for (int i = -6; i <= 6; i++) {
			for (int k = -6; k <= 6; k++) {
				int d = Math.max(Math.abs(i), Math.abs(k));
				if (d >= 4 && d <= 6) {
					int m = BlockType.STONE.metadata;
					if (Math.abs(i) == 5 && Math.abs(k) == 5)
						m = BlockType.LIGHT.metadata;
					array.setBlock(x+i, y+5, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m);
				}
			}
		}
		for (int i = -7; i <= 7; i++) {
			for (int k = -7; k <= 7; k++) {
				int d = Math.max(Math.abs(i), Math.abs(k));
				if (d >= 5 && d <= 7 && Math.abs(i)+Math.abs(k) <= 12) {
					array.setBlock(x+i, y+9, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				}
			}
		}
		array.setBlock(x+2, y+9, z+7, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
		array.setBlock(x-2, y+9, z+7, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
		array.setBlock(x+2, y+9, z-7, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
		array.setBlock(x-2, y+9, z-7, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
		array.setBlock(x+7, y+9, z+2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
		array.setBlock(x+7, y+9, z-2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
		array.setBlock(x-7, y+9, z+2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
		array.setBlock(x-7, y+9, z-2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
		for (int i = -4; i <= 4; i++) {
			for (int k = -4; k <= 4; k++) {
				if (Math.abs(i)+Math.abs(k) > 6)
					continue;
				int m = BlockType.STONE.metadata;
				if (i == 0 && Math.abs(k) == 2)
					m = BlockType.LIGHT.metadata;
				else if (k == 0 && Math.abs(i) == 2)
					m = BlockType.LIGHT.metadata;
				else if (Math.abs(i) == 4 || Math.abs(k) == 4)
					m = BlockType.COBBLE.metadata;
				else if (Math.abs(i) == 3 && Math.abs(k) == 3)
					m = BlockType.COBBLE.metadata;
				array.setBlock(x+i, y+10, z+k, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m);
			}
		}
		for (int j = 6; j <= 8; j++) {
			array.setBlock(x+6, y+j, z+6, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			array.setBlock(x-6, y+j, z+6, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			array.setBlock(x+6, y+j, z-6, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			array.setBlock(x-6, y+j, z-6, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
		}
		for (int d = 5; d <= 7; d++) {
			array.setBlock(x-d, y+10, z-2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
			array.setBlock(x-d, y+10, z+2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
			array.setBlock(x+d, y+10, z-2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
			array.setBlock(x+d, y+10, z+2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
			array.setBlock(x-2, y+10, z-d, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
			array.setBlock(x+2, y+10, z-d, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
			array.setBlock(x-2, y+10, z+d, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
			array.setBlock(x+2, y+10, z+d, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
		}
		for (int d = 3; d <= 5; d++) {
			array.setBlock(x-d, y+10, z-d, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
			array.setBlock(x+d, y+10, z-d, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
			array.setBlock(x-d, y+10, z+d, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
			array.setBlock(x+d, y+10, z+d, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.COBBLE.metadata);
		}
		for (int j = 1; j <= 4; j++) {
			for (int d = -6; d <= 6; d++) {
				Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
				int m = BlockType.STONE.metadata;
				array.setBlock(x+d, y+j, z+6, b, m);
				array.setBlock(x+d, y+j, z-6, b, m);
				array.setBlock(x+6, y+j, z+d, b, m);
				array.setBlock(x-6, y+j, z+d, b, m);
			}
		}
		for (int j = 1; j <= 4; j++) {
			for (int i = -5; i <= 5; i++) {
				for (int k = -5; k <= 5; k++) {
					int d = Math.max(Math.abs(i), Math.abs(k));
					if (d >= 4 && d <= 5) {
						Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
						int m = BlockType.STONE.metadata;
						if ((k > 0 && i == 2) || (k < 0 && i == -2) || (i < 0 && k == 2) || (i > 0 && k == -2)) {
							if (j == 3)
								m = BlockType.GLASS.metadata;
						}
						else if (j == 2 && ((k > 0 && i == 1) || (k < 0 && i == -1) || (i < 0 && k == 1) || (i > 0 && k == -1))) {
							b = ChromaBlocks.MUSICTRIGGER.getBlockInstance();
							m = 0;
						}
						else if (j > 1) {
							b = Blocks.air;
							m = 0;
						}
						array.setBlock(x+i, y+j, z+k, b, m);
					}
				}
			}
		}
		this.addLootChest(array, x-5, y+2, z+4, ForgeDirection.EAST);
		this.addLootChest(array, x+4, y+2, z+5, ForgeDirection.NORTH);
		this.addLootChest(array, x+5, y+2, z-4, ForgeDirection.WEST);
		this.addLootChest(array, x-4, y+2, z-5, ForgeDirection.SOUTH);

		this.addLootChest(array, x-4, y+6, z+4, ForgeDirection.WEST);
		this.addLootChest(array, x+4, y+6, z+4, ForgeDirection.SOUTH);
		this.addLootChest(array, x+4, y+6, z-4, ForgeDirection.EAST);
		this.addLootChest(array, x-4, y+6, z-4, ForgeDirection.NORTH);

		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
			for (int a = 3; a <= 5; a++) {
				for (int b = 4; b <= 5; b++) {
					array.setBlock(x+dir.offsetX*b+left.offsetX*a, y, z+left.offsetZ*a+dir.offsetZ*b, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);
				}
			}
		}
		//this.addCallback(?, del);

		if (this.isDisplay()) {
			puzzle.addToArray(array, x, y, z);
		}

		return array;
	}

	private int getStairMeta(int dx, int dy, int dz) {
		boolean invert = dy == 9;
		boolean x = Math.abs(dx) > Math.abs(dz);
		if (x) {
			if (dx > 0) {
				return invert ? 4 : 1;
			}
			else {
				return invert ? 5 : 0;
			}
		}
		else {
			if (dz > 0) {
				return invert ? 6 : 3;
			}
			else {
				return invert ? 7 : 2;
			}
		}
	}

	@Override
	public void onPlace(World world, TileEntityStructControl te) {
		puzzle.placeData(world, te);
	}

	@Override
	public String getChestLootTable(Coordinate c, TileEntityLootChest te, FilledBlockArray arr, Random r) {
		return ChestGenHooks.VILLAGE_BLACKSMITH;
	}

	@Override
	public int getChestYield(Coordinate c, TileEntityLootChest te, FilledBlockArray arr, Random r) {
		return te.yCoord-arr.getMinY() <= 4 ? 3 : 1;
	}

	@Override
	public int modifyLootCount(TileEntityLootChest tileEntityLootChest, String s, int bonus, Random r, int count) {
		return count*3/2;
	}

	@Override
	public float getFragmentChance(TileEntityLootChest te, String s, int bonus, Random r) {
		return 1F;
	}

	@Override
	public int getFragmentCount(TileEntityLootChest te, String s, int bonus, Random r) {
		if (bonus == 3) {
			int n = r.nextInt(12);
			if (n == 11)
				return 4;
			else if (n > 7)
				return 3;
			else if (n > 2)
				return 2;
			else
				return 1;
		}
		else {
			return r.nextInt(3) > 0 ? 2 : 1;
		}
	}

	public void setPuzzle(BiomeStructurePuzzle data) {
		puzzle = data;
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

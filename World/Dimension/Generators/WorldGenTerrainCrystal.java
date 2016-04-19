/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.DimDecoTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ItemMagicRegistry;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class WorldGenTerrainCrystal extends ChromaWorldGenerator {

	public WorldGenTerrainCrystal(DimensionGenerators g) {
		super(g);
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {

		int h = y+64+rand.nextInt(64);
		if (h > 250)
			return false;
		int n = 4+rand.nextInt(9);
		for (int i = 0; i < n; i++) {
			int dx = ReikaRandomHelper.getRandomPlusMinus(x, 24);
			int dz = ReikaRandomHelper.getRandomPlusMinus(z, 24);
			int dy = ReikaRandomHelper.getRandomPlusMinus(h, 12);
			int s = 3+rand.nextInt(6);
			float f = (float)ReikaRandomHelper.getRandomPlusMinus(1.5D, 0.5D);
			TerrainCrystal t = new TerrainCrystal(s, f);
			t.hasTreasure = rand.nextInt(18) == 0;
			t.hasCore = rand.nextInt(6) == 0;
			t.hasPool = rand.nextInt(3) == 0;
			t.hasResource = !t.hasTreasure && !t.hasCore && !t.hasPool && rand.nextInt(3) == 0;

			if (rand.nextInt(4) == 0)
				t.heightExponent = ReikaRandomHelper.getRandomBetween(0.25, rand.nextBoolean() ? 1.5 : 2D); //0.5 is a hemispherical bottom; 2 is pointed

			if (rand.nextInt(8) == 0) {
				t.stone = new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.ordinal());
				t.top = new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.MOSS.ordinal());
			}

			switch(rand.nextInt(12)) {
				case 0:
					t.core = new BlockKey(Blocks.diamond_ore);
					break;
				case 1:
					t.core = new BlockKey(Blocks.emerald_ore);
					break;
				case 2:
				case 3:
					t.core = new BlockKey(Blocks.obsidian);
					break;
			}

			switch(rand.nextInt(12)) {
				case 0:
				case 1:
					t.pool = new BlockKey(ChromaBlocks.CHROMA.getBlockInstance());
					break;
				case 2:
				case 3:
					t.pool = new BlockKey(FluidRegistry.getFluid("ender").getBlock());
					break;
				case 4: {
					Fluid fs = FluidRegistry.getFluid("ic2uumatter");
					if (fs != null && fs.canBePlacedInWorld())
						t.core = new BlockKey(fs.getBlock());
					break;
				}
				/*
				case 5: {
					Fluid fs = FluidRegistry.getFluid("springwater");
					if (fs != null && fs.canBePlacedInWorld())
						t.core = new BlockKey(fs.getBlock());
					break;
				}
				 */
				case 6: {
					Fluid fs = FluidRegistry.getFluid("fluidpure");
					if (fs != null && fs.canBePlacedInWorld())
						t.core = new BlockKey(fs.getBlock());
					break;
				}
			}

			FilledBlockArray b = t.generate(world, dx, dy, dz);
			int tries = 0;
			while (tries < 100 && !b.isAtLeastXPercent(world, 90, Blocks.air)) {
				dx = ReikaRandomHelper.getRandomPlusMinus(x, 16);
				dz = ReikaRandomHelper.getRandomPlusMinus(z, 16);
				dy = ReikaRandomHelper.getRandomPlusMinus(h, 6);
				b = t.generate(world, dx, dy, dz);
			}
			if (b.isAtLeastXPercent(world, 90, Blocks.air)) {
				b.place();
				if (t.hasTreasure) {
					TileEntityLootChest te = (TileEntityLootChest)world.getTileEntity(dx, dy-t.size/2, dz);
					this.generateLoot(te, rand);
				}
			}
		}

		//ChromatiCraft.logger.log("Generated @ "+x+", "+z);

		return true;
	}

	private void generateLoot(TileEntityLootChest te, Random rand) {
		int n = 16+rand.nextInt(24);
		ArrayList<ItemStack> li = ItemMagicRegistry.instance.getAllRegisteredItems();
		for (int i = 0; i < n; i++) {
			int idx = rand.nextInt(li.size());
			ItemStack in = li.get(idx);
			ElementTagCompound value = ItemMagicRegistry.instance.getItemValue(in);
			int max = Math.min(16, 24/value.getMaximumValue());
			int num = Math.min(1+rand.nextInt(max), in.getMaxStackSize());
			ItemStack is = ReikaItemHelper.getSizedItemStack(in, num);

			int slot = rand.nextInt(te.getSizeInventory());
			while (te.getStackInSlot(slot) != null) {
				slot = rand.nextInt(te.getSizeInventory());
			}
			te.setInventorySlotContents(slot, is);
		}
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.005F;
	}

	private static class TerrainCrystal {

		private final int size;
		private final float heightFactor;
		private double heightExponent = 1;

		private boolean hasCore = false;
		private boolean hasPool = false;
		private boolean hasTreasure = false;
		private boolean hasResource = false;

		private BlockKey top = new BlockKey(Blocks.grass);
		private BlockKey stone = new BlockKey(Blocks.stone);
		private BlockKey core = new BlockKey(Blocks.lava);
		private BlockKey pool = new BlockKey(Blocks.water);

		private TerrainCrystal(int s, float h) {
			size = s;
			heightFactor = h;
		}

		public FilledBlockArray generate(World world, int x, int y, int z) {
			FilledBlockArray arr = new FilledBlockArray(world);
			int dy = y;
			double r = size;
			double mh = size*heightFactor;
			int h = 0;
			while (r > 0) {
				int dr = MathHelper.ceiling_double_int(r);
				for (int i = -dr; i <= dr; i++) {
					for (int k = -dr; k <= dr; k++) {
						int d = Math.abs(i)+Math.abs(k);
						if (d <= dr) {
							int dx = x+i;
							int dz = z+k;
							BlockKey b = dy == y ? top : stone;
							if (hasCore && d < dr-1 && dy < y)
								b = core;
							if (hasPool && d < dr-1 && dy == y)
								b = pool;
							arr.setBlock(dx, dy, dz, b);
						}
					}
				}
				h++;
				r = size*Math.pow(1-h/mh, heightExponent);
				dy--;
			}

			if (hasTreasure) {
				arr.setBlock(x, y-size/2, z, ChromaBlocks.LOOTCHEST.getBlockInstance());
			}

			if (hasResource) {
				int y2 = y-size/2;
				arr.setBlock(x, y2, z, ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.LATTICE.ordinal());
				arr.setBlock(x, y2+1, z, ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.LATTICE.ordinal());
				arr.setBlock(x, y2-1, z, ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.LATTICE.ordinal());
				arr.setBlock(x+1, y2, z, ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.LATTICE.ordinal());
				arr.setBlock(x-1, y2, z, ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.LATTICE.ordinal());
				arr.setBlock(x, y2, z+1, ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.LATTICE.ordinal());
				arr.setBlock(x, y2, z-1, ChromaBlocks.DIMGEN.getBlockInstance(), DimDecoTypes.LATTICE.ordinal());
			}

			if (hasCore) {
				HashSet<Coordinate> set = new HashSet();
				for (Coordinate c : arr.keySet()) {
					if (c.yCoord == arr.getMinY())
						set.add(c);
				}
				for (Coordinate c : set) {
					arr.setBlock(c.xCoord, c.yCoord, c.zCoord, stone);
				}
			}

			return arr;
		}

	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World;

import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalPylon;
import Reika.DragonAPI.Instantiable.Data.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.StructuredBlockArray;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ExtraUtilsHandler;
import cpw.mods.fml.common.IWorldGenerator;

public class PylonGenerator implements IWorldGenerator {

	private final ForgeDirection[] dirs = ForgeDirection.values();

	//private static final int CHANCE = 40;

	private static final int avgDist = 8;
	private static final int maxDeviation = 3;
	private static final Random rand = new Random();

	private static final int GRIDSIZE = 256;

	private static final HashMap<Integer, boolean[][]> data = new HashMap();
	private static final HashMap<Integer, Boolean> init = new HashMap();

	private static void fillArray(World world) {
		int id = world.provider.dimensionId;
		init.put(id, true);

		rand.setSeed(world.getSeed() ^ id);
		boolean[][] grid = getGrid(id);
		for (int x = maxDeviation; x < GRIDSIZE-maxDeviation; x += avgDist) {
			for (int z = maxDeviation; z < GRIDSIZE-maxDeviation; z += avgDist) {
				int x2 = ReikaRandomHelper.getRandomPlusMinus(x, maxDeviation);
				int z2 = ReikaRandomHelper.getRandomPlusMinus(z, maxDeviation);
				grid[x2][z2] = true;
				//ChromatiCraft.logger.debug(x + ", " + z + " | " + x2 + ", " + z2);
			}
		}
		if (ChromatiCraft.logger.shouldDebug())
			ChromatiCraft.logger.debug("Dimension Pylon Generation Array: \n"+getDimensionString(id));
	}

	private static String getDimensionString(int id) {
		boolean[][] arr = getGrid(id);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < GRIDSIZE; i++) {
			for (int j = 0; j < GRIDSIZE; j++) {
				char c = arr[i][j] ? 'x' : 'o';
				sb.append(c);
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	private static boolean[][] getGrid(int dim) {
		boolean[][] arr = data.get(dim);
		if (arr == null) {
			arr = new boolean[GRIDSIZE][GRIDSIZE];
			data.put(dim, arr);
		}
		return arr;
	}

	private static boolean filledDim(World world) {
		return init.containsKey(world.provider.dimensionId);
	}

	private static boolean isGennableChunk(World world, int chunkX, int chunkZ) {
		boolean[][] arr = getGrid(world.provider.dimensionId);
		return arr[chunkX%128][chunkZ%128];
	}

	@Override
	public void generate(Random r, int chunkX, int chunkZ, World world, IChunkProvider gen, IChunkProvider p) {
		if (this.canGenerateIn(world)) {

			if (!this.filledDim(world)) {
				this.fillArray(world);
			}

			if (this.isGennableChunk(world, chunkX, chunkZ)) {
				chunkX *= 16;
				chunkZ *= 16;
				int x = chunkX+r.nextInt(16);
				int z = chunkZ+r.nextInt(16);

				int y = world.getTopSolidOrLiquidBlock(x, z)-1;
				if (this.canGenerateAt(world, x, y, z)) {
					ChromatiCraft.logger.debug("Generated pylon at "+x+", "+z);
					this.generatePylon(r, world, x, y, z);
				}
			}
		}
	}

	private boolean canGenerateIn(World world) {
		if (Math.abs(world.provider.dimensionId) == 1)
			return false;
		if (world.provider.isHellWorld || world.provider.hasNoSky)
			return false;
		if (world.provider.dimensionId == ExtraUtilsHandler.getInstance().darkID)
			return false;
		if (world.getWorldInfo().getTerrainType() == WorldType.FLAT)
			return false;
		return true;
	}

	private boolean canGenerateAt(World world, int x, int y, int z) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		if (BiomeDictionary.isBiomeOfType(biome, Type.NETHER))
			return false;

		Block origin = world.getBlock(x, y, z);
		if (origin == Blocks.log || origin == Blocks.log2)
			return false;
		if (origin == Blocks.leaves || origin == Blocks.leaves2)
			return false;

		for (int i = y+1; i < world.getHeight(); i++) {
			Block b = world.getBlock(x, i, z);
			if (b != Blocks.air && b != Blocks.leaves && b != Blocks.leaves2 && !ReikaWorldHelper.softBlocks(world, x, i, z))
				return false;
		}

		StructuredBlockArray blocks = new StructuredBlockArray(world);

		for (int n = 0; n <= 9; n++) {
			int dy = y+n;
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				for (int k = 0; k <= 3; k++) {
					int dx = x+dir.offsetX*k;
					int dz = z+dir.offsetZ*k;
					blocks.addBlockCoordinate(dx, dy, dz);
					if (dir.offsetX == 0) {
						blocks.addBlockCoordinate(dx+dir.offsetZ, dy, dz);
						blocks.addBlockCoordinate(dx-dir.offsetZ, dy, dz);
					}
					else if (dir.offsetZ == 0) {
						blocks.addBlockCoordinate(dx, dy, dz+dir.offsetX);
						blocks.addBlockCoordinate(dx, dy, dz-dir.offsetX);
					}
				}
			}
		}

		for (int i = 0; i < blocks.getSize(); i++) {
			int[] xyz = blocks.getNthBlock(i);
			int dx = xyz[0];
			int dy = xyz[1];
			int dz = xyz[2];
			Block b = world.getBlock(dx, dy, dz);
			if (b instanceof BlockLiquid || b instanceof BlockFluidBase)
				return false;
			if (!ReikaWorldHelper.softBlocks(world, dx, dy, dz)) {
				int meta = world.getBlockMetadata(dx, dy, dz);

				if (dy == blocks.getMinY()) {
					if (!this.isFloorReplaceable(b, meta))
						return false;
				}
				else {
					if (!this.isAirReplaceable(b, meta))
						return false;
				}
			}
		}

		return true;
	}

	private boolean isFloorReplaceable(Block b, int meta) {
		if (b == Blocks.stone)
			return true;
		if (b == Blocks.dirt)
			return true;
		if (b == Blocks.grass)
			return true;
		if (b == Blocks.gravel)
			return true;
		if (b == Blocks.sand)
			return true;
		if (b == Blocks.log || b == Blocks.log2)
			return true;
		return false;
	}

	private boolean isAirReplaceable(Block b, int meta) {
		if (b == Blocks.dirt)
			return true;
		if (b == Blocks.grass)
			return true;
		if (b == Blocks.gravel)
			return true;
		if (b == Blocks.log || b == Blocks.log2)
			return true;
		if (b == Blocks.leaves || b == Blocks.leaves2)
			return true;
		return false;
	}

	private void generatePylon(Random rand, World world, int x, int y, int z) {
		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		CrystalElement e = CrystalElement.elements[rand.nextInt(16)];
		FilledBlockArray array = ChromaStructures.getPylonStructure(world, x, y, z, e);

		for (int n = -4; n < 0; n++) {
			int dy = y+n;
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				for (int k = 0; k <= 3; k++) {
					int dx = x+dir.offsetX*k;
					int dz = z+dir.offsetZ*k;
					if (ReikaWorldHelper.softBlocks(world, dx, dy, dz))
						array.setBlock(dx, dy, dz, b, 0);
					if (dir.offsetX == 0) {
						if (ReikaWorldHelper.softBlocks(world, dx+dir.offsetZ, dy, dz))
							array.setBlock(dx+dir.offsetZ, dy, dz, b, 0);
						if (ReikaWorldHelper.softBlocks(world, dx-dir.offsetZ, dy, dz))
							array.setBlock(dx-dir.offsetZ, dy, dz, b, 0);
					}
					else if (dir.offsetZ == 0) {
						if (ReikaWorldHelper.softBlocks(world, dx, dy, dz+dir.offsetX))
							array.setBlock(dx, dy, dz+dir.offsetX, b, 0);
						if (ReikaWorldHelper.softBlocks(world, dx, dy, dz-dir.offsetX))
							array.setBlock(dx, dy, dz-dir.offsetX, b, 0);
					}
				}
			}
		}

		array.place();

		//TileEntity
		world.setBlock(x, y+9, z, ChromaTiles.PYLON.getBlock(), ChromaTiles.PYLON.getBlockMetadata(), 3);
		TileEntityCrystalPylon te = (TileEntityCrystalPylon)world.getTileEntity(x, y+9, z);
		te.setColor(e);
		te.validateMultiblock();
		world.func_147451_t(x, y+9, z);
	}

}

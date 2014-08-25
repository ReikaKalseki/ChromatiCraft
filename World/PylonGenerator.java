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
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalPylon;
import Reika.DragonAPI.Instantiable.Data.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.StructuredBlockArray;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ExtraUtilsHandler;
import cpw.mods.fml.common.IWorldGenerator;

public class PylonGenerator implements IWorldGenerator {

	private final ForgeDirection[] dirs = ForgeDirection.values();

	private static final int CHANCE = 40;

	@Override
	public void generate(Random r, int chunkX, int chunkZ, World world, IChunkProvider gen, IChunkProvider p) {
		if (!this.canGenerateIn(world))
			return;
		chunkX *= 16;
		chunkZ *= 16;
		int x = chunkX+r.nextInt(16);
		int z = chunkZ+r.nextInt(16);
		int chance = CHANCE;
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (BiomeDictionary.isBiomeOfType(b, Type.BEACH) || BiomeDictionary.isBiomeOfType(b, Type.NETHER))
			return;
		if (b == BiomeGenBase.desert || b == BiomeGenBase.icePlains)
			chance *= 2;
		else if (BiomeDictionary.isBiomeOfType(b, Type.WASTELAND) || BiomeDictionary.isBiomeOfType(b, Type.PLAINS))
			chance *= 2;
		else if (BiomeDictionary.isBiomeOfType(b, Type.JUNGLE))
			chance /= 1.5;
		else if (BiomeDictionary.isBiomeOfType(b, Type.FOREST))
			chance /= 1.25;
		int y = world.getTopSolidOrLiquidBlock(x, z)-1;

		if (world.getWorldInfo().getTerrainType() != WorldType.FLAT) {
			if (r.nextInt(chance) == 0 && this.canGenerateAt(world, x, y, z)) {
				ChromatiCraft.logger.log("Generated pylon at "+x+", "+z);
				this.generatePylon(r, world, x, y, z);
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
		return true;
	}

	private boolean canGenerateAt(World world, int x, int y, int z) {
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
		FilledBlockArray array = this.getPylonStructure(world, x, y, z, e);

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

	public static FilledBlockArray getPylonStructure(World world, int x, int y, int z, CrystalElement e) {
		FilledBlockArray array = new FilledBlockArray(world);
		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		for (int n = 0; n <= 9; n++) {
			int dy = y+n;
			Block b2 = n == 0 ? b : Blocks.air;
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				for (int k = 0; k <= 3; k++) {
					int dx = x+dir.offsetX*k;
					int dz = z+dir.offsetZ*k;
					array.setBlock(dx, dy, dz, b2, 0);
					if (dir.offsetX == 0) {
						array.setBlock(dx+dir.offsetZ, dy, dz, b2, 0);
						array.setBlock(dx-dir.offsetZ, dy, dz, b2, 0);
					}
					else if (dir.offsetZ == 0) {
						array.setBlock(dx, dy, dz+dir.offsetX, b2, 0);
						array.setBlock(dx, dy, dz-dir.offsetX, b2, 0);
					}
				}
			}
		}

		for (int i = 1; i <= 5; i++) {
			int dy = y+i;
			Block b2 = i < 5 ? b : ChromaBlocks.RUNE.getBlockInstance();
			int meta = (i == 2 || i == 3) ? 2 : (i == 4 ? 7 : 8);
			if (i == 5) //rune
				meta = e.ordinal();
			array.setBlock(x-3, dy, z+1, b2, meta);
			array.setBlock(x-3, dy, z-1, b2, meta);

			array.setBlock(x+3, dy, z+1, b2, meta);
			array.setBlock(x+3, dy, z-1, b2, meta);

			array.setBlock(x-1, dy, z+3, b2, meta);
			array.setBlock(x-1, dy, z-3, b2, meta);

			array.setBlock(x+1, dy, z+3, b2, meta);
			array.setBlock(x+1, dy, z-3, b2, meta);
		}

		for (int n = 1; n <= 7; n++) {
			int dy = y+n;
			for (int i = -1; i <= 1; i += 2) {
				int dx = x+i;
				for (int k = -1; k <= 1; k += 2) {
					int dz = z+k;
					int meta = n == 5 ? 3 : (n == 7 ? 5 : 2);
					array.setBlock(dx, dy, dz, b, meta);
				}
			}
		}

		array.setBlock(x-3, y+4, z, b, 4);
		array.setBlock(x+3, y+4, z, b, 4);
		array.setBlock(x, y+4, z-3, b, 4);
		array.setBlock(x, y+4, z+3, b, 4);


		array.setBlock(x-2, y+3, z+1, b, 1);
		array.setBlock(x-2, y+3, z-1, b, 1);

		array.setBlock(x+2, y+3, z+1, b, 1);
		array.setBlock(x+2, y+3, z-1, b, 1);

		array.setBlock(x-1, y+3, z+2, b, 1);
		array.setBlock(x-1, y+3, z-2, b, 1);

		array.setBlock(x+1, y+3, z+2, b, 1);
		array.setBlock(x+1, y+3, z-2, b, 1);

		array.remove(x, y+9, z);

		return array;
	}

}

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
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Aura.BaseAura;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalPylon;
import Reika.DragonAPI.Instantiable.Data.Coordinate;
import Reika.DragonAPI.Instantiable.Data.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.StructuredBlockArray;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ExtraUtilsHandler;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class PylonGenerator implements IWorldGenerator {

	public static final PylonGenerator instance = new PylonGenerator();

	private final ForgeDirection[] dirs = ForgeDirection.values();

	//private final int CHANCE = 40;

	private final int avgDist = 10; //16
	private final int maxDeviation = 4;
	private final Random rand = new Random();

	private final int GRIDSIZE = 256;

	private final HashMap<Integer, boolean[][]> data = new HashMap();

	private PylonGenerator() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void clearOnUnload(WorldEvent.Unload evt) {
		this.clear(evt.world);
	}

	private void clear(World world) {
		data.remove(world.provider.dimensionId);
	}

	private void fillArray(World world) {
		int id = world.provider.dimensionId;
		rand.setSeed(world.getSeed() ^ id);
		boolean[][] grid = this.getGrid(id);
		for (int x = maxDeviation; x < GRIDSIZE-maxDeviation; x += avgDist) {
			for (int z = maxDeviation; z < GRIDSIZE-maxDeviation; z += avgDist) {
				int x2 = ReikaRandomHelper.getRandomPlusMinus(x, maxDeviation);
				int z2 = ReikaRandomHelper.getRandomPlusMinus(z, maxDeviation);
				grid[x2][z2] = true;
				//ChromatiCraft.logger.debug(x + ", " + z + " | " + x2 + ", " + z2);
			}
		}
		//if (ChromatiCraft.logger.shouldDebug())
		//ChromatiCraft.logger.log("Dimension Pylon Generation Array: \n"+getDimensionString(id));
	}

	private String getDimensionString(int id) {
		boolean[][] arr = this.getGrid(id);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < GRIDSIZE; i++) {
			for (int j = 0; j < GRIDSIZE; j++) {
				String c = arr[i][j] ? "[#]" : "[ ]";
				sb.append(c);
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	private boolean[][] getGrid(int dim) {
		boolean[][] arr = data.get(dim);
		if (arr == null) {
			arr = new boolean[GRIDSIZE][GRIDSIZE];
			data.put(dim, arr);
		}
		return arr;
	}

	private boolean filledDim(World world) {
		return data.containsKey(world.provider.dimensionId);
	}

	public Coordinate getNearestValidChunk(World world, int x, int z) {
		if (!this.filledDim(world)) {
			this.fillArray(world);
		}

		return new Coordinate(332, 80, -141);
	}

	private boolean isGennableChunk(World world, int chunkX, int chunkZ) {
		boolean[][] arr = this.getGrid(world.provider.dimensionId);
		while (chunkX < 0)
			chunkX += GRIDSIZE;
		while (chunkZ < 0)
			chunkZ += GRIDSIZE;
		return arr[chunkX%GRIDSIZE][chunkZ%GRIDSIZE];
	}

	@Override
	public void generate(Random r, int chunkX, int chunkZ, World world, IChunkProvider gen, IChunkProvider p) {
		if (this.canGenerateIn(world)) {

			if (!this.filledDim(world)) {
				this.fillArray(world);
			}

			if (this.isGennableChunk(world, chunkX, chunkZ)) {
				this.tryForceGenerate(world, chunkX*16, chunkZ*16, r);
			}
		}
	}

	private void tryForceGenerate(World world, int cx, int cz, Random r) {
		int maxtries = 24;
		for (int i = 0; i < maxtries; i++) {
			int x = cx+r.nextInt(16);
			int z = cz+r.nextInt(16);

			//world.setBlock(x, 128, z, Blocks.flowing_lava);
			int y = world.getTopSolidOrLiquidBlock(x, z)-1;
			if (this.canGenerateAt(world, x, y, z)) {
				ChromatiCraft.logger.debug("Generated pylon at "+x+", "+z);
				this.generatePylon(r, world, x, y, z);
				break;
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
				;//return false;
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

		//precalc:
		for (int i = 0; i < blocks.getSize(); i++) {
			int[] xyz = blocks.getNthBlock(i);
			int dx = xyz[0];
			int dy = xyz[1];
			int dz = xyz[2];
			Block b = world.getBlock(dx, dy, dz);
			//if (b == Blocks.stone || b == Blocks.dirt || b == Blocks.grass) {
			//	blocks.offset(0, 1, 0);
			//	break precalc;
			//}
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
		if (b == Blocks.log || b == Blocks.log2 || ModWoodList.isModWood(b, meta))
			return true;
		if (b == Blocks.leaves || b == Blocks.leaves2 || ModWoodList.isModLeaf(b, meta))
			return true;
		if (b == Blocks.red_flower || b == Blocks.yellow_flower || b instanceof BlockFlower)
			return true;
		if (b == Blocks.red_mushroom || b == Blocks.brown_mushroom)
			return true;
		if (b instanceof BlockBush)
			return true;
		if (b == Blocks.reeds)
			return true;
		if (b == Blocks.cactus)
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
		if (b == Blocks.log || b == Blocks.log2 || ModWoodList.isModWood(b, meta))
			return true;
		if (b == Blocks.leaves || b == Blocks.leaves2 || ModWoodList.isModLeaf(b, meta))
			return true;
		if (b == Blocks.red_flower || b == Blocks.yellow_flower || b instanceof BlockFlower)
			return true;
		if (b == Blocks.red_mushroom || b == Blocks.brown_mushroom)
			return true;
		if (b instanceof BlockBush)
			return true;
		if (b == Blocks.reeds)
			return true;
		if (b == Blocks.cactus)
			return true;
		return false;
	}

	private void generatePylon(Random rand, World world, int x, int y, int z) {
		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		ElementTagCompound tag = BaseAura.getBaseAura(world, x, y, z);
		if (rand.nextInt(25) > 0)
			tag.clipToPrimaries();
		CrystalElement e = CrystalElement.randomElement();//tag.asWeightedRandom().getRandomEntry();
		FilledBlockArray array = ChromaStructures.getPylonStructure(world, x, y, z, e);

		boolean broken = ChromaOptions.BROKENPYLON.getState() && rand.nextInt(2) == 0;
		if (broken)
			this.breakPylon(array);

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
		if (broken)
			te.invalidateMultiblock();
		else
			te.validateMultiblock();
		world.func_147451_t(x, y+9, z);
	}

	private void breakPylon(FilledBlockArray array) {
		int n = 3+rand.nextInt(4);
		int i = 0;
		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		while (i < n) {
			int[] xyz = array.getRandomBlock();
			int x = xyz[0];
			int y = xyz[1];
			int z = xyz[2];
			if (array.hasBlockAt(x, y, z, b, 0) || array.hasBlockAt(x, y, z, b, 1) || array.hasBlockAt(x, y, z, b, 2) || array.hasBlockAt(x, y, z, b, 7) || array.hasBlockAt(x, y, z, b, 8)) {
				i++;
				array.setBlock(x, y, z, Blocks.air);
			}
		}
	}

}

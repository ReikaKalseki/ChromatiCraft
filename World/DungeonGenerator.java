/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures.Structures;
import Reika.ChromatiCraft.Auxiliary.DesertStructure;
import Reika.ChromatiCraft.Auxiliary.OceanStructure;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityStructControl;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.ReikaSpawnerHelper;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TwilightForestHandler;

public class DungeonGenerator implements RetroactiveGenerator {

	public static final DungeonGenerator instance = new DungeonGenerator();

	private final ForgeDirection[] dirs = ForgeDirection.values();

	private final Collection<Structures> structs = new ArrayList();

	private DungeonGenerator() {
		structs.add(Structures.CAVERN);
		structs.add(Structures.BURROW);
		structs.add(Structures.OCEAN);
		structs.add(Structures.DESERT);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (this.canGenerateIn(world)) {
			for (Structures s : structs) {
				if (this.isGennableChunk(world, chunkX*16, chunkZ*16, random, s)) {
					if (this.tryGenerate(world, chunkX*16, chunkZ*16, random, s)) {
						ChromatiCraft.logger.log("Successful generation of "+s.name()+" at "+chunkX*16+", "+chunkZ*16);
					}
				}
			}
		}
	}

	private boolean tryGenerate(World world, int cx, int cz, Random r, Structures s) {
		int x = cx + r.nextInt(16);
		int z = cz + r.nextInt(16);
		switch(s) {
			case CAVERN: {
				int y = 10+r.nextInt(40);
				int tries = 0;
				while (tries < 10 && !this.isValidCavernLocation(world, x, y, z, ChromaStructures.getCavernStructure(world, x, y, z))) {
					y = 10+r.nextInt(40);
					x = cx + r.nextInt(16);
					z = cz + r.nextInt(16);
					tries++;
				}
				FilledBlockArray struct = ChromaStructures.getCavernStructure(world, x, y, z);
				if (this.isValidCavernLocation(world, x, y, z, struct)) {
					struct.place();
					//generate tunnel
					for (int i = 7; i < 18; i++) {
						int dx = x+i;
						Block b = world.getBlock(dx, y, z);
						Block b2 = world.getBlock(dx, y-1, z);
						if (b.isAir(world, dx, y, z) && b2.isAir(world, dx, y-1, z)) {
							break;
						}
						else {
							world.setBlock(dx, y, z, Blocks.air);
							world.setBlock(dx, y-1, z, Blocks.air);
							//ReikaJavaLibrary.pConsole("Digging tunnel @ depth "+i);
						}
					}
					//ChromatiCraft.logger.log("Successful generation of "+s.name()+" at "+x+","+y+","+z);
					world.setBlock(x, y, z, ChromaTiles.STRUCTCONTROL.getBlock(), ChromaTiles.STRUCTCONTROL.getBlockMetadata(), 3);
					TileEntityStructControl te = (TileEntityStructControl)world.getTileEntity(x, y, z);
					te.generate(s, CrystalElement.WHITE);
					this.populateChests(s, struct, r);
					return true;
				}
				return false;
			}
			case BURROW: {
				int y = world.getTopSolidOrLiquidBlock(x, z)-1;
				CrystalElement e = CrystalElement.randomElement();
				FilledBlockArray arr = ChromaStructures.getBurrowStructure(world, x, y, z, e);
				if (this.isValidBurrowLocation(world, x, y, z, arr)) {
					arr.place();
					this.convertDirtToGrass(arr);
					//world.setBlockMetadataWithNotify(x-7, y-5, z-2, 5, 3); //that chest that never points right
					//ChromatiCraft.logger.log("Successful generation of "+s.name()+" at "+x+","+y+","+z);
					world.setBlock(x-5, y-8, z-2, ChromaTiles.STRUCTCONTROL.getBlock(), ChromaTiles.STRUCTCONTROL.getBlockMetadata(), 3);
					TileEntityStructControl te = (TileEntityStructControl)world.getTileEntity(x-5, y-8, z-2);
					te.generate(s, e);
					this.populateChests(s, arr, r);
					return true;
				}
				return false;
			}
			case OCEAN: {
				int d = 3;
				int y = world.getTopSolidOrLiquidBlock(x, z)-d;
				Block b = world.getBlock(x, y+d, z);
				int tries = 0;
				while (b != Blocks.water && b != Blocks.flowing_water && tries < 10) {
					x = cx + r.nextInt(16);
					z = cz + r.nextInt(16);
					b = world.getBlock(x, y+d, z);
					tries++;
				}
				if (b == Blocks.water || b == Blocks.flowing_water) {
					//ReikaJavaLibrary.pConsole("Attempting gen @ "+x+", "+y+", "+z);
					//while (b == Blocks.water || b == Blocks.flowing_water && y > 0) {
					//	y--;
					//	b = world.getBlock(x, y, z);
					//}
					FilledBlockArray struct = ChromaStructures.getOceanStructure(world, x, y, z);
					if (y > 0 && this.isValidOceanLocation(world, x, y, z, struct)) {
						struct.place();
						world.setBlock(x, y, z, ChromaTiles.STRUCTCONTROL.getBlock(), ChromaTiles.STRUCTCONTROL.getBlockMetadata(), 3);
						TileEntityStructControl te = (TileEntityStructControl)world.getTileEntity(x, y, z);
						te.generate(s, CrystalElement.WHITE);
						this.populateChests(s, struct, r);
						this.programSpawners(s, struct, (String)EntityList.classToStringMapping.get(EntityCreeper.class));
						this.mossify(s, struct, r);
						this.generatePit(world, x, y, z);
						return true;
					}
				}
				return false;
			}
			case DESERT: {
				int y = world.getTopSolidOrLiquidBlock(x, z);

				y -= 8;
				Block b = world.getBlock(x, y, z);
				BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
				if (b == Blocks.sand && BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SANDY)) {

					x -= 7;
					y -= 3;
					z -= 7;

					FilledBlockArray struct = ChromaStructures.getDesertStructure(world, x, y, z);
					DesertStructure.getTerrain(struct, x, y, z);
					if (this.isValidDesertLocation(world, x, y, z, struct)) {
						struct.place();

						world.setBlock(x+7, y+3, z+7, ChromaTiles.STRUCTCONTROL.getBlock(), ChromaTiles.STRUCTCONTROL.getBlockMetadata(), 3);
						TileEntityStructControl te = (TileEntityStructControl)world.getTileEntity(x+7, y+3, z+7);
						te.generate(s, CrystalElement.WHITE);
						this.populateChests(s, struct, r);
						//too dry for moss//this.mossify(s, struct, r);
						return true;
					}
				}
				return false;
			}
			default:
				return false;
		}
	}

	private boolean isValidDesertLocation(World world, int x, int y, int z, FilledBlockArray struct) {
		if (!BiomeDictionary.isBiomeOfType(world.getBiomeGenForCoords(struct.getMinX(), struct.getMinZ()), BiomeDictionary.Type.SANDY))
			return false;
		if (!BiomeDictionary.isBiomeOfType(world.getBiomeGenForCoords(struct.getMaxX(), struct.getMinZ()), BiomeDictionary.Type.SANDY))
			return false;
		if (!BiomeDictionary.isBiomeOfType(world.getBiomeGenForCoords(struct.getMinX(), struct.getMaxZ()), BiomeDictionary.Type.SANDY))
			return false;
		if (!BiomeDictionary.isBiomeOfType(world.getBiomeGenForCoords(struct.getMaxX(), struct.getMaxZ()), BiomeDictionary.Type.SANDY))
			return false;
		return true;
	}

	private static FilledBlockArray getPitSlice(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();

		x -= 3;
		z -= 3;

		array.setBlock(x+1, y+0, z+1, b, 8);
		array.setBlock(x+1, y+0, z+2, b, 8);
		array.setBlock(x+1, y+0, z+3, b, 8);
		array.setBlock(x+1, y+0, z+4, b, 8);
		array.setBlock(x+1, y+0, z+5, b, 8);
		array.setBlock(x+2, y+0, z+1, b, 8);
		array.setBlock(x+2, y+0, z+5, b, 8);
		array.setBlock(x+3, y+0, z+1, b, 8);
		array.setBlock(x+3, y+0, z+5, b, 8);
		array.setBlock(x+5, y+0, z+1, b, 8);
		array.setBlock(x+5, y+0, z+2, b, 8);
		array.setBlock(x+5, y+0, z+3, b, 8);
		array.setBlock(x+5, y+0, z+4, b, 8);
		array.setBlock(x+5, y+0, z+5, b, 8);
		array.setBlock(x+4, y+0, z+1, b, 8);
		array.setBlock(x+4, y+0, z+5, b, 8);

		array.setBlock(x+2, y+0, z+2, Blocks.air);
		array.setBlock(x+2, y+0, z+3, Blocks.air);
		array.setBlock(x+2, y+0, z+4, Blocks.air);
		array.setBlock(x+3, y+0, z+2, Blocks.air);
		array.setBlock(x+3, y+0, z+3, Blocks.air);
		array.setBlock(x+3, y+0, z+4, Blocks.air);
		array.setBlock(x+4, y+0, z+2, Blocks.air);
		array.setBlock(x+4, y+0, z+3, Blocks.air);
		array.setBlock(x+4, y+0, z+4, Blocks.air);

		return array;
	}

	private static FilledBlockArray getEndcap1(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();

		x -= 3;
		z -= 3;
		array.setBlock(x+30, y+8, z+2, Blocks.air);
		array.setBlock(x+30, y+8, z+3, Blocks.air);
		array.setBlock(x+30, y+8, z+4, Blocks.air);
		array.setBlock(x+30, y+9, z+2, Blocks.air);
		array.setBlock(x+30, y+9, z+3, Blocks.air);
		array.setBlock(x+30, y+9, z+4, Blocks.air);
		array.setBlock(x+30, y+10, z+2, Blocks.air);
		array.setBlock(x+30, y+10, z+3, Blocks.air);
		array.setBlock(x+30, y+10, z+4, Blocks.air);

		return array;
	}

	private static FilledBlockArray getEndcap2(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();

		x -= 3;
		z -= 3;

		array.setBlock(x+2, y+8, z+30, Blocks.air);
		array.setBlock(x+2, y+9, z+30, Blocks.air);
		array.setBlock(x+2, y+10, z+30, Blocks.air);
		array.setBlock(x+3, y+8, z+30, Blocks.air);
		array.setBlock(x+3, y+9, z+30, Blocks.air);
		array.setBlock(x+3, y+10, z+30, Blocks.air);
		array.setBlock(x+4, y+8, z+30, Blocks.air);
		array.setBlock(x+4, y+9, z+30, Blocks.air);
		array.setBlock(x+4, y+10, z+30, Blocks.air);

		return array;
	}

	private void generatePit(World world, int x, int y, int z) {
		for (int i = 3; i < 32; i++) {
			FilledBlockArray arr = this.getPitSlice(world, x, y-i, z);
			boolean flag = true;
			for (int k = 0; k < arr.getSize(); k++) {
				Coordinate c = arr.getNthBlock(k);
				Block b = c.getBlock(world);
				if (b != Blocks.air) {
					flag = false;
				}
			}
			if (flag && i > 6) {
				break;
			}
			else {
				arr.place();
			}
		}

		BlockArray arr = OceanStructure.getPitCover(x, y, z);
		for (int k = 0; k < arr.getSize(); k++) {
			Coordinate c = arr.getNthBlock(k);
			Block b = c.getBlock(world);
			c.setBlock(world, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.metadata);
		}
	}

	private void mossify(Structures s, FilledBlockArray arr, Random r) {
		Block b2 = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		for (int k = 0; k < arr.getSize(); k++) {
			Coordinate c = arr.getNthBlock(k);
			Block b = c.getBlock(arr.world);
			if (b == b2) {
				int meta = c.getBlockMetadata(arr.world);
				if (meta == BlockType.STONE.metadata) {
					int dy = c.yCoord-arr.getMinY();
					int ct = Math.max(1, dy*2-2);
					if (r.nextInt(ct) == 0) {
						arr.world.setBlockMetadataWithNotify(c.xCoord, c.yCoord, c.zCoord, BlockType.MOSS.metadata, 3);
					}
				}
			}
		}
	}

	private void programSpawners(Structures s, FilledBlockArray arr, String mob) {
		for (int k = 0; k < arr.getSize(); k++) {
			Coordinate c = arr.getNthBlock(k);
			Block b = c.getBlock(arr.world);
			if (b == Blocks.mob_spawner) {
				TileEntityMobSpawner te = (TileEntityMobSpawner)arr.world.getTileEntity(c.xCoord, c.yCoord, c.zCoord);
				ReikaSpawnerHelper.setMobSpawnerMob(te, mob);
			}
		}
	}

	private void populateChests(ChromaStructures.Structures struct, FilledBlockArray arr, Random r) {
		String s = null;
		switch (struct) {
			case CAVERN:
				s = ChestGenHooks.DUNGEON_CHEST;
				break;
			case BURROW:
				s = ChestGenHooks.BONUS_CHEST;
				break;
			case OCEAN:
				s = ChestGenHooks.PYRAMID_JUNGLE_CHEST;
				break;
			case DESERT:
				s = ChestGenHooks.PYRAMID_DESERT_CHEST;
			default:
				break;
		}
		if (s == null)
			return;
		for (int k = 0; k < arr.getSize(); k++) {
			Coordinate c = arr.getNthBlock(k);
			Block b = c.getBlock(arr.world);
			if (b == ChromaStructures.getChestGen()) {
				TileEntityLootChest te = (TileEntityLootChest)arr.world.getTileEntity(c.xCoord, c.yCoord, c.zCoord);
				int bonus = 0;
				if (struct == Structures.OCEAN && c.yCoord-arr.getMinY() == 4)
					bonus = 4;
				if (struct == Structures.DESERT && c.yCoord-arr.getMinY() < 4)
					bonus = 2;
				te.populateChest(s, struct, bonus, r);
			}
		}
	}

	private void convertDirtToGrass(FilledBlockArray arr) {
		for (int k = 0; k < arr.getSize(); k++) {
			Coordinate c = arr.getNthBlock(k);
			Block b = c.getBlock(arr.world);
			if (b == Blocks.dirt) {
				if (arr.world.getBlockLightValue(c.xCoord, c.yCoord+1, c.zCoord) > 8) {
					c.setBlock(arr.world, Blocks.grass);
				}
			}
		}
	}

	private boolean isValidCavernLocation(World world, int x, int y, int z, FilledBlockArray arr) {
		boolean flag = false;
		for (int i = 6; i < 18; i++) {
			int dx = x+i;
			Block b = world.getBlock(dx, y, z);
			Block b2 = world.getBlock(dx, y-1, z);
			if (b.isAir(world, dx, y, z) && b2.isAir(world, dx, y-1, z)) {
				flag = true;
				break;
			}
		}
		if (flag) {
			for (int k = 0; k < arr.getSize(); k++) {
				Coordinate c = arr.getNthBlock(k);
				Block b = c.getBlock(world);
				if (b.isAir(world, c.xCoord, c.yCoord, c.zCoord))
					return false;
			}
			return true;
		}
		return false;
	}

	private boolean isValidBurrowLocation(World world, int x, int y, int z, FilledBlockArray arr) {

		if (world.getBlock(x, y, z) != Blocks.grass)
			return false;

		//Surface visibility
		for (int i = 1; i <= 8; i++) {
			for (int k = -3; k <= 1; k++) {
				Block b = world.getBlock(x, y+i, z+k);
				if (!ReikaWorldHelper.softBlocks(world, x, y+i, z+k)) {
					return false;
				}
			}
		}

		//No lakes
		int r = 1;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					Block b = world.getBlock(x+i, y+j, z+k);
					if (b instanceof BlockLiquid || b instanceof BlockFluidBase) {
						return false;
					}
				}
			}
		}

		//No air exposure
		for (int k = 0; k < arr.getSize(); k++) {
			Coordinate c = arr.getNthBlock(k);
			int dx = c.xCoord;
			int dy = c.yCoord;
			int dz = c.zCoord;
			Block b = world.getBlock(dx, dy, dz);
			if (world.getTopSolidOrLiquidBlock(dx, dz) < y-2)
				return false;
			if (arr.hasBlockAt(dx, dy, dz, Blocks.stone) || arr.hasBlockAt(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance())) {
				if (b.isAir(world, dx, dy, dz) || ReikaWorldHelper.checkForAdjMaterial(world, dx, dy, dz, Material.air) != null) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isValidOceanLocation(World world, int x, int y, int z, FilledBlockArray struct) {
		//needs to be at least 8 blocks deep
		if (world.getBlock(x, y+8, z) != Blocks.water && world.getBlock(x, y+8, z) != Blocks.flowing_water)
			return false;

		//at least one end open
		boolean flag1 = true;
		boolean flag2 = true;
		FilledBlockArray cap = (FilledBlockArray)this.getEndcap1(world, x, y, z).offset(1, 0, 0);
		for (int k = 0; k < cap.getSize(); k++) {
			Coordinate c = cap.getNthBlock(k);
			Block b = c.getBlock(world);
			if (b != Blocks.water && b != Blocks.flowing_water) {
				flag1 = false;
			}
		}
		cap = (FilledBlockArray)this.getEndcap2(world, x, y, z).offset(0, 0, 1);
		for (int k = 0; k < cap.getSize(); k++) {
			Coordinate c = cap.getNthBlock(k);
			Block b = c.getBlock(world);
			if (b != Blocks.water && b != Blocks.flowing_water) {
				flag2 = false;
			}
		}
		if (!flag1 && !flag2)
			return false;

		//bury lower half, and ensure not near shore or intersecting another
		for (int k = 0; k < struct.getSize(); k++) {
			Coordinate c = struct.getNthBlock(k);
			Block b = c.getBlock(world);
			if (b == ChromaBlocks.STRUCTSHIELD.getBlockInstance())
				return false;
			if (world.getTopSolidOrLiquidBlock(c.xCoord, c.zCoord) <= y)
				return false;
			if (!ReikaBiomeHelper.isOcean(world.getBiomeGenForCoords(c.xCoord, c.zCoord)))
				return false;
		}

		//can generate pit to cave
		int consec = 0;
		for (int i = 3; i < y; i++) {
			FilledBlockArray slice = this.getPitSlice(world, x, y-i, z);
			boolean flag = true;
			for (int k = 0; k < slice.getSize(); k++) {
				Coordinate c = slice.getNthBlock(k);
				Block b = c.getBlock(world);
				if (b != Blocks.air) {
					flag = false;
				}
			}
			if (flag && i > 6) {
				consec++;
				if (consec >= 3)
					return true;
			}
			else {
				consec = 0;
			}
		}
		return false;
	}

	private boolean isVoidWorld(World world, int x, int z) {
		return world.getBlock(x, 0, z) == Blocks.air || world.canBlockSeeTheSky(x, 1, z);
	}

	private boolean isGennableChunk(World world, int x, int z, Random r, Structures s) {
		if (this.isVoidWorld(world, x, z))
			return false;
		switch(s) {
			case OCEAN:
				return r.nextInt(32) == 0 && ReikaBiomeHelper.isOcean(world.getBiomeGenForCoords(x, z));
			case CAVERN:
				return r.nextInt(64) == 0;
			case BURROW:
				return r.nextInt(64) == 0 && world.getBiomeGenForCoords(x, z).topBlock == Blocks.grass;
			case DESERT:
				return r.nextInt(128) == 0 && world.getBiomeGenForCoords(x, z).topBlock == Blocks.sand;
			default:
				return false;
		}
	}

	private boolean canGenerateIn(World world) {
		if (world.provider.dimensionId == 0)
			return true;
		if (Math.abs(world.provider.dimensionId) == 1)
			return false;
		if (world.provider.dimensionId == ExtraUtilsHandler.getInstance().darkID)
			return false;
		if (world.provider.dimensionId == TwilightForestHandler.getInstance().dimensionID)
			return false;
		if (world.getWorldInfo().getTerrainType() == WorldType.FLAT)
			return false;//ChromaOptions.FLATGEN.getState();
		return true;
	}

	@Override
	public boolean canGenerateAt(Random rand, World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ChromatiCraft Prefab Structures";
	}

}

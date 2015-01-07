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
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import Reika.ChromatiCraft.API.Event.StructureChestPopulationEvent;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures.Structures;
import Reika.ChromatiCraft.Block.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityStructControl;
import Reika.DragonAPI.Instantiable.Data.FilledBlockArray;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaSpawnerHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.TwilightForestHandler;
import cpw.mods.fml.common.IWorldGenerator;

public class DungeonGenerator implements IWorldGenerator {

	public static final DungeonGenerator instance = new DungeonGenerator();

	private ForgeDirection[] dirs = ForgeDirection.values();

	private Collection<Structures> structs = new ArrayList();

	private DungeonGenerator() {
		structs.add(Structures.CAVERN);
		structs.add(Structures.BURROW);
		structs.add(Structures.OCEAN);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (this.canGenerateIn(world)) {
			for (Structures s : structs) {
				if (this.isGennableChunk(world, chunkX*16, chunkZ*16, random, s)) {
					if (this.tryGenerate(world, chunkX*16, chunkZ*16, random, s)) {
						//ChromatiCraft.logger.log("Successful generation of "+s.name()+" at "+chunkX*16+", "+chunkZ*16);
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
					for (int i = y+8; i < 200; i++) {
						world.setBlock(x, i, z, Blocks.glass);
					}
					ReikaJavaLibrary.pConsole(te);
					return true;
				}
			}
			return false;
		}
		default:
			return false;
		}
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
				int[] xyz = arr.getNthBlock(k);
				Block b = arr.world.getBlock(xyz[0], xyz[1], xyz[2]);
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
	}

	private void mossify(Structures s, FilledBlockArray arr, Random r) {
		Block b2 = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		for (int k = 0; k < arr.getSize(); k++) {
			int[] xyz = arr.getNthBlock(k);
			Block b = arr.world.getBlock(xyz[0], xyz[1], xyz[2]);
			if (b == b2) {
				int meta = arr.world.getBlockMetadata(xyz[0], xyz[1], xyz[2]);
				if (meta == BlockType.STONE.metadata) {
					int dy = xyz[1]-arr.getMinY();
					int c = Math.max(1, dy*2-2);
					if (r.nextInt(c) == 0) {
						arr.world.setBlockMetadataWithNotify(xyz[0], xyz[1], xyz[2], BlockType.MOSS.metadata, 3);
					}
				}
			}
		}
	}

	private void programSpawners(Structures s, FilledBlockArray arr, String mob) {
		for (int k = 0; k < arr.getSize(); k++) {
			int[] xyz = arr.getNthBlock(k);
			Block b = arr.world.getBlock(xyz[0], xyz[1], xyz[2]);
			if (b == Blocks.mob_spawner) {
				TileEntityMobSpawner te = (TileEntityMobSpawner)arr.world.getTileEntity(xyz[0], xyz[1], xyz[2]);
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
		default:
			break;
		}
		if (s == null)
			return;
		for (int k = 0; k < arr.getSize(); k++) {
			int[] xyz = arr.getNthBlock(k);
			Block b = arr.world.getBlock(xyz[0], xyz[1], xyz[2]);
			if (b == ChromaStructures.getChestGen()) {
				IInventory te = (IInventory)arr.world.getTileEntity(xyz[0], xyz[1], xyz[2]);
				WeightedRandomChestContent[] loot = ChestGenHooks.getItems(s, r);
				int bonus = struct == Structures.OCEAN && xyz[1]-arr.getMinY() == 4 ? 4 : 0;
				WeightedRandomChestContent.generateChestContents(r, loot, te, ChestGenHooks.getCount(s, r));
				if (bonus > 0)
					ReikaInventoryHelper.generateMultipliedLoot(bonus, r, s, te);
				int n1 = struct == Structures.OCEAN ? r.nextInt(5) == 0 ? 3 : 1 : 3;
				int n2 = struct == Structures.OCEAN ? 8 : 3;
				if (r.nextInt(n1) > 0) {
					ReikaInventoryHelper.addToIInv(ChromaItems.FRAGMENT.getItemInstance(), te);
					if (r.nextInt(n2) == 0)
						ReikaInventoryHelper.addToIInv(ChromaItems.FRAGMENT.getItemInstance(), te);
				}

				StructureChestPopulationEvent evt = new StructureChestPopulationEvent(struct.name(), s, r);
				MinecraftForge.EVENT_BUS.post(evt);
				for (ItemStack is : evt.getItems()) {
					ReikaInventoryHelper.addToIInv(is, te);
				}
			}
		}
	}

	private void convertDirtToGrass(FilledBlockArray arr) {
		for (int k = 0; k < arr.getSize(); k++) {
			int[] xyz = arr.getNthBlock(k);
			Block b = arr.world.getBlock(xyz[0], xyz[1], xyz[2]);
			if (b == Blocks.dirt) {
				if (arr.world.getBlockLightValue(xyz[0], xyz[1]+1, xyz[2]) > 8) {
					arr.world.setBlock(xyz[0], xyz[1], xyz[2], Blocks.grass);
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
				int[] xyz = arr.getNthBlock(k);
				Block b = world.getBlock(xyz[0], xyz[1], xyz[2]);
				if (b.isAir(world, xyz[0], xyz[1], xyz[2]))
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
			int[] xyz = arr.getNthBlock(k);
			int dx = xyz[0];
			int dy = xyz[1];
			int dz = xyz[2];
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
			int[] xyz = cap.getNthBlock(k);
			Block b = world.getBlock(xyz[0], xyz[1], xyz[2]);
			if (b != Blocks.water && b != Blocks.flowing_water) {
				flag1 = false;
			}
		}
		cap = (FilledBlockArray)this.getEndcap2(world, x, y, z).offset(0, 0, 1);
		for (int k = 0; k < cap.getSize(); k++) {
			int[] xyz = cap.getNthBlock(k);
			Block b = world.getBlock(xyz[0], xyz[1], xyz[2]);
			if (b != Blocks.water && b != Blocks.flowing_water) {
				flag2 = false;
			}
		}
		if (!flag1 && !flag2)
			return false;

		//bury lower half, and ensure not near shore
		for (int k = 0; k < struct.getSize(); k++) {
			int[] xyz = struct.getNthBlock(k);
			Block b = world.getBlock(xyz[0], xyz[1], xyz[2]);
			if (world.getTopSolidOrLiquidBlock(xyz[0], xyz[2]) <= y) {
				return false;
			}
			if (!ReikaBiomeHelper.isOcean(world.getBiomeGenForCoords(xyz[0], xyz[2]))) {
				return false;
			}
		}

		//can generate pit to cave
		int consec = 0;
		for (int i = 3; i < y; i++) {
			FilledBlockArray slice = this.getPitSlice(world, x, y-i, z);
			boolean flag = true;
			for (int k = 0; k < slice.getSize(); k++) {
				int[] xyz = slice.getNthBlock(k);
				Block b = world.getBlock(xyz[0], xyz[1], xyz[2]);
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

	private boolean isGennableChunk(World world, int x, int z, Random r, Structures s) {
		switch(s) {
		case OCEAN:
			return ReikaBiomeHelper.isOcean(world.getBiomeGenForCoords(x, z));
		case CAVERN:
			return r.nextInt(32) == 0;
		case BURROW:
			return r.nextInt(16) == 0 && world.getBiomeGenForCoords(x, z).topBlock == Blocks.grass;
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

}

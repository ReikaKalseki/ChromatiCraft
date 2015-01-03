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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures.Structures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityStructControl;
import Reika.DragonAPI.Instantiable.Data.FilledBlockArray;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
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
				ChromatiCraft.logger.log("Successful generation of "+s.name()+" at "+x+","+y+","+z);
				world.setBlock(x, y, z, ChromaTiles.STRUCTCONTROL.getBlock(), ChromaTiles.STRUCTCONTROL.getBlockMetadata(), 3);
				TileEntityStructControl te = (TileEntityStructControl)world.getTileEntity(x, y, z);
				te.generate(Structures.CAVERN, CrystalElement.WHITE);
				return true;
			}
			return false;
		}
		case BURROW: {
			int y = world.getTopSolidOrLiquidBlock(x, z)-1;
			//ChromaStructures.getBurrowStructure(world, x, y, z).place();
			return false;
		}
		case OCEAN: {
			int y = world.getTopSolidOrLiquidBlock(x, z)-8; //needs to be at least 8 blocks deep
			Block b = world.getBlock(x, y, z);
			if (b == Blocks.water || b == Blocks.flowing_water) {
				while (b == Blocks.water || b == Blocks.flowing_water) {
					y--;
					b = world.getBlock(x, y, z);
				}
				ChromaStructures.getOceanStructure(world, x, y, z).place();
				return true;
			}
			return false;
		}
		default:
			return false;
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

	private boolean isGennableChunk(World world, int x, int z, Random r, Structures s) {
		switch(s) {
		case OCEAN:
			return ReikaBiomeHelper.isOcean(world.getBiomeGenForCoords(x, z));
		case CAVERN:
			return r.nextInt(32) == 0;
		case BURROW:
			return true;
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

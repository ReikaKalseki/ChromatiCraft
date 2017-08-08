/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.Bees;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidBase;
import Reika.ChromatiCraft.API.Event.CrystalGenEvent;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.IWG.CrystalGenerator;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class HiveGenerator implements IWorldGenerator {

	public static final HiveGenerator instance = new HiveGenerator();

	public static final int PER_CHUNK = 5;

	private HiveGenerator() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onGenCrystal(CrystalGenEvent evt) {
		int dim = evt.world.provider.dimensionId;
		if (dim != -1 && dim != 1) {
			if (evt.color == CrystalElement.WHITE.getAPIProxy()) {
				if (evt.getRandomInt(4) == 0) {
					Block idb = evt.world.getBlock(evt.x, evt.y-1, evt.z);
					if (idb.isReplaceableOreGen(evt.world, evt.x, evt.y-1, evt.z, Blocks.stone)) {
						evt.world.setBlock(evt.x, evt.y-1, evt.z, ChromaBlocks.HIVE.getBlockInstance(), 1, 3);
					}
				}
			}
		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.terrainType == WorldType.FLAT) //do not generate in superflat
			return;
		int dim = world.provider.dimensionId;
		if (dim != -1 && dim != 1) {
			for (int i = 0; i < PER_CHUNK; i++) {
				int x = chunkX*16+random.nextInt(16);
				int z = chunkZ*16+random.nextInt(16);
				int maxy = 64;
				if (world.provider.isHellWorld)
					maxy = 128;
				int posY = 4+random.nextInt(maxy-4);
				Block block = ChromaBlocks.HIVE.getBlockInstance();
				int meta = 0;
				if (this.canGenerateAt(world, x, posY, z)) {
					world.setBlock(x, posY, z, block, meta, 3);
				}
			}
		}
	}

	private boolean canGenerateAt(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b != Blocks.air && !ReikaWorldHelper.softBlocks(world, x, y, z))
			return false;
		if (b instanceof BlockLiquid)
			return false;
		if (b instanceof BlockFluidBase)
			return false;
		Block idb = world.getBlock(x, y-1, z);
		if (idb == Blocks.air)
			return false;
		int metab = world.getBlockMetadata(x, y-1, z);
		int metaa = world.getBlockMetadata(x, y+1, z);
		if (!CrystalGenerator.canGenerateOn(world, x, y-1, z))
			return false;
		if (idb instanceof BlockLiquid)
			return false;
		if (idb instanceof BlockFluidBase)
			return false;
		return ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Blocks.air) != null;
	}

}

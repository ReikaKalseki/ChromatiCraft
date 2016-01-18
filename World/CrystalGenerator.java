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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import Reika.ChromatiCraft.API.Event.CrystalGenEvent;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ChromaDecorator;
import Reika.ChromatiCraft.ModInterface.MystPages;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ReikaTwilightHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;

public class CrystalGenerator implements RetroactiveGenerator, ChromaDecorator {

	public static final CrystalGenerator instance = new CrystalGenerator();

	private static final int PER_CHUNK = 60; //calls per chunk; vast majority fail

	private CrystalGenerator() {

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		chunkX *= 16;
		chunkZ *= 16;
		for (int i = 0; i < PER_CHUNK*this.getDensityFactor(world, chunkX, chunkZ); i++) {
			int posX = chunkX + random.nextInt(16);
			int posZ = chunkZ + random.nextInt(16);
			int maxy = 64;
			if (world.provider.isHellWorld)
				maxy = 128;
			int posY = 4+random.nextInt(maxy-4);
			Block id = ChromaBlocks.CRYSTAL.getBlockInstance();
			int meta = random.nextInt(16);
			if (this.canGenerateAt(world, posX, posY, posZ)) {
				world.setBlock(posX, posY, posZ, id, meta, 3);
				MinecraftForge.EVENT_BUS.post(new CrystalGenEvent(world, posX, posY, posZ, random, meta));
				//ReikaJavaLibrary.pConsole("Generating "+ReikaDyeHelper.dyes[meta].getName()+" Crystal at "+posX+", "+posY+", "+posZ);
			}
			int r = 3;
			for (int k = -r; k <= r; k++) {
				for (int l = -r; l <= r; l++) {
					for (int m = -r; m <= r; m++) {
						world.func_147479_m(posX, posY, posZ);
					}
				}
			}
		}
	}

	public static boolean canGenerateAt(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		Block idb = world.getBlock(x, y-1, z);
		int metab = world.getBlockMetadata(x, y-1, z);
		if (b != Blocks.air && !ReikaWorldHelper.softBlocks(world, x, y, z))
			return false;
		if (b instanceof BlockLiquid)
			return false;
		if (!canGenerateOn(world, x, y-1, z))
			return false;
		return ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Blocks.air) != null;
	}

	public static boolean canGenerateOn(World world, int x, int y, int z) {
		Block id = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (id == Blocks.air)
			return false;
		if (id == Blocks.stone)
			return true;
		if (id == Blocks.dirt)
			return true;
		if (id == Blocks.gravel)
			return true;
		if (id == Blocks.planks) //mineshafts
			return true;
		if (id == Blocks.bedrock)
			return true;
		if (id == Blocks.obsidian)
			return true;
		if (id == Blocks.stonebrick) //strongholds
			return true;
		if (id == Blocks.monster_egg)
			return true;
		if (id == Blocks.cobblestone)
			return true;
		if (id == Blocks.mossy_cobblestone)
			return true;
		if (id == Blocks.netherrack)
			return true;
		if (ReikaBlockHelper.isOre(id, meta))
			return true;
		return id.isReplaceableOreGen(world, x, y, z, Blocks.stone);
	}

	public static float getDensityFactor(World world, int x, int z) {
		if (world.provider.terrainType == WorldType.FLAT) //do not generate in superflat
			return 0;
		if (world.provider.dimensionId == 1)
			return 0;
		if (ModList.MYSTCRAFT.isLoaded() && ReikaMystcraftHelper.isMystAge(world)) {
			if (!MystPages.Pages.CRYSTALS.existsInWorld(world)) {
				return 0;
			}
		}
		if (world.provider.isHellWorld)
			return ChromaOptions.NETHER.getState() ? 0.25F : 0;
		if (ModList.MYSTCRAFT.isLoaded() && ReikaMystcraftHelper.isMystAge(world)) {
			if (MystPages.Pages.DENSE.existsInWorld(world)) {
				return 1.75F;
			}
		}
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		if (world.provider.dimensionId == ReikaTwilightHelper.getDimensionID())
			return 2F;
		if (biome == BiomeGenBase.mushroomIsland || biome == BiomeGenBase.mushroomIslandShore)
			return 1.5F;
		if (biome == BiomeGenBase.ocean || biome == BiomeGenBase.frozenOcean)
			return 1.25F;
		if (biome == BiomeGenBase.extremeHills || biome == BiomeGenBase.extremeHillsEdge)
			return 1.125F;
		return 1F;
	}

	@Override
	public boolean canGenerateAt(Random rand, World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ChromatiCraft Crystals";
	}

	@Override
	public String getCommandID() {
		return "crystal";
	}

}

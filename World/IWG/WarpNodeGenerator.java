/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.IWG;

import java.util.HashMap;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.ChromatiCraft.World.GlowingCliffsColumnShaper;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.ShuffledGrid;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.Satisforestry.API.SFAPI;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WarpNodeGenerator implements RetroactiveGenerator {

	public static final WarpNodeGenerator instance = new WarpNodeGenerator();

	private final Random worldRand = new Random();
	private final int GRIDSIZE = 2048;
	private final HashMap<Integer, ShuffledGrid> chunkFilter = new HashMap();

	private WarpNodeGenerator() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void clearOnUnload(WorldEvent.Unload evt) {
		if (evt.world.isRemote) {
			//this.clear(evt.world);
		}
		else {
			this.clear(evt.world);
		}
	}

	private void clear(World world) {
		this.clearDimension(world.provider.dimensionId);
	}

	public void clearDimension(int dim) {
		chunkFilter.remove(dim);
	}

	private ShuffledGrid getGrid(World world) {
		int dim = world.provider.dimensionId;
		ShuffledGrid arr = chunkFilter.get(dim);
		if (arr == null) {
			arr = new ShuffledGrid(GRIDSIZE, 20, 55); //avg spacing every 880 blocks, with offsets up to 320m
			this.populateWorldData(world, arr);
			chunkFilter.put(dim, arr);
		}
		return arr;
	}

	private void populateWorldData(World world, ShuffledGrid grid) {
		int id = world.provider.dimensionId;
		worldRand.setSeed(-(id*11 - world.getSeed()*7));
		grid.calculate(worldRand);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (this.isValidWorld(world)) {
			if (this.isGennableChunk(world, chunkX, chunkZ)) {
				chunkX *= 16;
				chunkZ *= 16;
				int posX = chunkX + random.nextInt(16);
				int posZ = chunkZ + random.nextInt(16);
				BiomeGenBase b = world.getBiomeGenForCoords(posX, posZ);
				if (ReikaRandomHelper.doWithChance(this.getBiomeSuccessChance(b), random)) {
					int miny = this.getMinY(world, posX, posZ, b);
					int maxy = this.getMaxY(world, posX, posZ, b);
					if (miny >= maxy) {
						ChromatiCraft.logger.logError("Failed to generate a warp node @ "+posX+", "+posZ+" due to zero height range!");
						return;
					}
					int posY = miny+random.nextInt(maxy-miny+1);
					if (this.canGenerateAt(world, posX, posY, posZ)) {
						world.setBlock(posX, posY, posZ, ChromaBlocks.WARPNODE.getBlockInstance());
					}
				}
			}
		}
	}

	public boolean isValidWorld(World world) {
		return world.provider.dimensionId == 0 && world.getWorldInfo().getTerrainType() != WorldType.FLAT;
	}

	public boolean isGennableChunk(World world, int chunkX, int chunkZ) {
		return this.getGrid(world).isValid(chunkX, chunkZ);
	}

	private double getBiomeSuccessChance(BiomeGenBase b) {
		if (ChromatiCraft.isRainbowForest(b))
			return 0.8;
		else if (BiomeGlowingCliffs.isGlowingCliffs(b))
			return 0.667;
		else if (ChromatiCraft.isEnderForest(b))
			return 1;
		else if (ModList.SATISFORESTRY.isLoaded() && SFAPI.biomeHandler.isPinkForest(b))
			return 0.75F;
		else
			return 0.333;
	}

	private int getMinY(World world, int x, int z, BiomeGenBase b) {
		int min = ReikaWorldHelper.getTopNonAirBlock(world, x, z, true)+48;
		if (BiomeGlowingCliffs.isGlowingCliffs(b)) {
			min = Math.max(min, GlowingCliffsColumnShaper.MAX_MIDDLE_TOP_Y+16);
		}
		else if (ModList.SATISFORESTRY.isLoaded() && SFAPI.biomeHandler.isPinkForest(b)) {
			min = SFAPI.biomeHandler.getTrueTopAt(world, x, z)+40;//SFAPI.biomeHandler.getBaseTerrainHeight(b);
		}
		return min;
	}

	private int getMaxY(World world, int x, int z, BiomeGenBase b) {
		int max = Math.min(255, (int)(192*Math.max(1, b.rootHeight)));
		if (BiomeGlowingCliffs.isGlowingCliffs(b) || (ModList.SATISFORESTRY.isLoaded() && SFAPI.biomeHandler.isPinkForest(b))) {
			max = 255;
		}
		return Math.max(128, max);
	}

	private boolean canGenerateAt(World world, int x, int y, int z) {
		return world.getBlock(x, y, z).isAir(world, x, y, z) && world.canBlockSeeTheSky(x, y, z);
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ChromatiCraft Warp Nodes";
	}

}

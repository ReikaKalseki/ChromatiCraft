/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import Reika.ChromatiCraft.Magic.Lore.LoreEntry;
import Reika.ChromatiCraft.Magic.Lore.LoreManager;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;

public class DataTowerGenerator implements RetroactiveGenerator {

	public static final DataTowerGenerator instance = new DataTowerGenerator();

	private DataTowerGenerator() {

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (this.canGenerateIn(world)) {
			if (this.isGennableChunk(world, chunkX*16, chunkZ*16, random)) {
				if (this.tryGenerate(world, chunkX*16, chunkZ*16, random)) {
					//ChromatiCraft.logger.log("Successful generation of "+s.name()+" at "+chunkX*16+", "+chunkZ*16);
				}
			}
		}
	}

	private boolean tryGenerate(World world, int cx, int cz, Random r) {
		LoreEntry l = LoreManager.instance.getEntry(cx, cz);
		if (l == null)
			return false;
		int x = cx + r.nextInt(16);
		int z = cz + r.nextInt(16);
		return false;
	}

	private boolean isVoidWorld(World world, int x, int z) {
		return world.getBlock(x, 0, z) == Blocks.air || world.canBlockSeeTheSky(x, 1, z);
	}

	private boolean isGennableChunk(World world, int x, int z, Random r) {
		if (this.isVoidWorld(world, x, z))
			return false;
		return true;
	}

	private boolean canGenerateIn(World world) {
		/*
		if (ModList.MYSTCRAFT.isLoaded() && ReikaMystcraftHelper.isMystAge(world)) {
			if (!MystPages.Pages.STRUCTURES.existsInWorld(world)) {
				return false;
			}
		}
		if (world.getWorldInfo().getTerrainType() == WorldType.FLAT && !ChromaOptions.FLATGEN.getState()) {
			return ReikaWorldHelper.getSuperflatHeight(world) > 15;
		}
		if (world.provider.dimensionId == 0)
			return true;
		if (Math.abs(world.provider.dimensionId) == 1)
			return false;
		if (world.provider.dimensionId == ExtraUtilsHandler.getInstance().darkID)
			return false;
		if (world.provider.dimensionId == TwilightForestHandler.getInstance().dimensionID)
			return false;
		return true;
		 */
		return world.provider.dimensionId == 0;
	}

	@Override
	public boolean canGenerateAt(Random rand, World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ChromatiCraft Lore Towers";
	}

}

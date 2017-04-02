/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.IWG;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ChromaDecorator;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class CaveIndicatorGenerator implements RetroactiveGenerator, ChromaDecorator {

	public static final CaveIndicatorGenerator instance = new CaveIndicatorGenerator();

	private CaveIndicatorGenerator() {

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.getWorldInfo().getTerrainType() != WorldType.FLAT) {
			chunkX *= 16;
			chunkZ *= 16;
			for (int i = 0; i < 16; i++) {
				for (int k = 0; k < 16; k++) {
					int posX = chunkX + i;
					int posZ = chunkZ + k;
					if (BiomeGlowingCliffs.isGlowingCliffs(world.getBiomeGenForCoords(posX, posZ))) {
						int maxy = 64;
						for (int n = 0; n < 2; n++) {
							int posY = 4+random.nextInt(maxy-4);
							if (this.canGenerateAt(world, posX, posY, posZ)) {
								world.setBlock(posX, posY, posZ, ChromaBlocks.CAVEINDICATOR.getBlockInstance());
							}
							else {

							}
						}
					}
				}
			}
		}
	}

	public static boolean canGenerateAt(World world, int x, int y, int z) {
		Block ida = world.getBlock(x, y+1, z);
		if (ida != Blocks.air && !ReikaWorldHelper.softBlocks(world, x, y+1, z))
			return false;
		return canGenerateIn(world, x, y, z) && !world.canBlockSeeTheSky(x, y+1, z) && world.getBlockLightValue(x, y, z) < 8;// && ReikaWorldHelper.checkForAdjBlock(world, x, y, z, ChromaBlocks.CAVEINDICATOR.getBlockInstance()) == null;
	}

	public static boolean canGenerateIn(World world, int x, int y, int z) {
		Block id = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (id == Blocks.air)
			return false;
		if (id == Blocks.stone)
			return true;
		if (id == Blocks.dirt)
			;//return true;
		if (id == Blocks.gravel)
			;//return true;
		if (id == Blocks.cobblestone)
			;//return true;
		if (id == Blocks.mossy_cobblestone)
			;//return true;
		return id.isReplaceableOreGen(world, x, y, z, Blocks.stone);
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ChromatiCraft Piezo Crystals";
	}

	@Override
	public String getCommandID() {
		return "piezo";
	}

}

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

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ChromaDecorator;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class LumaGenerator implements RetroactiveGenerator, ChromaDecorator {

	public static final LumaGenerator instance = new LumaGenerator();

	private LumaGenerator() {

	}

	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		chunkX *= 16;
		chunkZ *= 16;

		if (this.generateIn(world)) {
			BiomeGenBase b = world.getBiomeGenForCoords(chunkX, chunkZ);
			int n = 1+rand.nextInt(ChromatiCraft.isRainbowForest(b) ? 6 : 2);
			for (int in = 0; in < n; in++) {
				int x = chunkX+rand.nextInt(16);
				int z = chunkZ+rand.nextInt(16);
				int y = 10+rand.nextInt(39);
				if (rand.nextBoolean())
					y = 10+(y-10)/2;

				int r = 2+rand.nextInt(5);
				int ry = 1+rand.nextInt(3);
				double dmax = ReikaMathLibrary.py3d(r, ry, r);
				for (int i = -r; i <= r; i++) {
					for (int k = -r; k <= r; k++) {
						for (int j = -ry; j <= ry; j++) {
							if (ReikaMathLibrary.isPointInsideEllipse(i, j, k, r, ry, r)) {
								double dd = ReikaMathLibrary.py3d(i, j, k);
								int dx = x+i;
								int dy = y+j;
								int dz = z+k;
								double c = 0.8-0.5*dd/dmax;
								if (ReikaRandomHelper.doWithChance(c) && this.isValidLocation(world, dx, dy, dz)) {
									world.setBlock(dx, dy, dz, ChromaBlocks.LUMA.getBlockInstance());
									//ReikaJavaLibrary.pConsole(dx+","+dy+","+dz);
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean isValidLocation(World world, int x, int y, int z) {
		if (!world.getBlock(x, y, z).isReplaceableOreGen(world, x, y, z, Blocks.stone) && !world.getBlock(x, y, z).isReplaceableOreGen(world, x, y, z, Blocks.dirt))
			return false;
		if (!world.getBlock(x, y+1, z).isAir(world, x, y+1, z))
			return false;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			if (dir != ForgeDirection.UP) {
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				Block b = world.getBlock(dx, dy, dz);
				if (b != ChromaBlocks.LUMA.getBlockInstance() && !b.getMaterial().isSolid())
					return false;
			}
		}
		return true;
	}

	private boolean generateIn(World world) {
		return Math.abs(world.provider.dimensionId) != 1 && (world.getWorldInfo().getTerrainType() != WorldType.FLAT || ChromaOptions.FLATGEN.getState());
	}

	@Override
	public boolean canGenerateAt(Random rand, World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ChromatiCraft Aether";
	}

	@Override
	public String getCommandID() {
		return "aether";
	}

}

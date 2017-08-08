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

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ChromaDecorator;
import Reika.ChromatiCraft.Block.Worldgen.BlockDecoFlower.Flowers;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class DecoFlowerGenerator implements RetroactiveGenerator, ChromaDecorator {

	public static final DecoFlowerGenerator instance = new DecoFlowerGenerator();

	private DecoFlowerGenerator() {

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		chunkX *= 16;
		chunkZ *= 16;

		if (this.generateIn(world)) {
			for (int i = 0; i < Flowers.list.length; i++) {
				Flowers p = Flowers.list[i];
				if (random.nextInt(p.getGenerationChance()) == 0) {
					int done = 0;
					int n = random.nextInt(5) == 0 ? 1+random.nextInt(4)+random.nextInt(6) : 1;
					int tries = 0;
					while (done < n && tries < 40) {
						tries++;
						int posX = chunkX + random.nextInt(16);
						int posZ = chunkZ + random.nextInt(16);
						BiomeGenBase b = world.getBiomeGenForCoords(posX, posZ);
						if (p.canGenerateIn(b)) {
							int posY = ReikaRandomHelper.getRandomPlusMinus(world.getTopSolidOrLiquidBlock(posX, posZ), 25);
							if (p != Flowers.FLOWIVY && p != Flowers.GLOWROOT) {
								while (world.getBlock(posX, posY-1, posZ).isAir(world, posX, posY-1, posZ) && posY > 0)
									posY--;
								if (posY <= 0)
									continue;
							}
							if (p == Flowers.GLOWROOT) {
								posY = ReikaRandomHelper.getRandomBetween(4, 80);
							}
							while (!world.getBlock(posX, posY, posZ).isAir(world, posX, posY, posZ) && posY < 255)
								posY++;
							if (p == Flowers.GLOWROOT) {
								while (posY < 255 && !Flowers.GLOWROOT.canPlantAt(world, posX, posY, posZ) && world.getBlock(posX, posY+1, posZ).isAir(world, posX, posY+1, posZ)) {
									posY++;
								}
							}
							if (world.getBlock(posX, posY, posZ).isAir(world, posX, posY, posZ) && p.canPlantAt(world, posX, posY, posZ)) {
								if (p == Flowers.FLOWIVY) {
									while(world.getBlock(posX, posY+1, posZ).isAir(world, posX, posY+1, posZ) && p.canPlantAt(world, posX, posY+1, posZ)) {
										posY++;
									}
								}
								if (p == Flowers.FLOWIVY && !world.getBlock(posX, posY-1, posZ).isAir(world, posX, posY-1, posZ))
									continue;
								world.setBlock(posX, posY, posZ, ChromaBlocks.DECOFLOWER.getBlockInstance(), p.ordinal(), 2);
								if (p == Flowers.FLOWIVY) {
									int g = random.nextInt(12);
									for (int h = 0; h < g; h++) {
										if (world.getBlock(posX, posY-h, posZ).isAir(world, posX, posY-h, posZ) && p.canPlantAt(world, posX, posY-h, posZ)) {
											world.setBlock(posX, posY-h, posZ, ChromaBlocks.DECOFLOWER.getBlockInstance(), p.ordinal(), 2);
										}
									}
								}
								else if (p == Flowers.VOIDREED) {
									int g = random.nextInt(4);
									for (int h = 0; h < g; h++) {
										if (world.getBlock(posX, posY+h, posZ).isAir(world, posX, posY+h, posZ) && p.canPlantAt(world, posX, posY+h, posZ)) {
											world.setBlock(posX, posY+h, posZ, ChromaBlocks.DECOFLOWER.getBlockInstance(), p.ordinal(), 2);
										}
									}
								}
								done++;
							}
						}
					}
				}
			}
		}
	}

	private boolean generateIn(World world) {
		/*
		if (Math.abs(world.provider.dimensionId) <= 1)
			return true;
		if (world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue())
			return true;
		if (ModList.MYSTCRAFT.isLoaded() && ReikaMystcraftHelper.isMystAge(world)) {
			if (!MystPages.Pages.PLANTS.existsInWorld(world)) {
				return false;
			}
		}*/
		return (world.getWorldInfo().getTerrainType() != WorldType.FLAT || ChromaOptions.FLATGEN.getState()) && !world.provider.hasNoSky;
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ChromatiCraft Flowers";
	}

	@Override
	public String getCommandID() {
		return "flowers";
	}

}

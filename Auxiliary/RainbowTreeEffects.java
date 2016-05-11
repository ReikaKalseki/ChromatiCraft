/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityBiomeReverter;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaChunkHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.MystCraftHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumIDHandler;


public class RainbowTreeEffects {

	public static void doRainbowTreeEffects(World world, int x, int y, int z, float chanceFactor, double rangeFactor, Random r, boolean spreadForest) {
		if (!world.isRemote) {
			if (ModList.THAUMCRAFT.isLoaded()) {
				if (ReikaRandomHelper.doWithChance(chanceFactor*100/25))
					fightTaint(world, x, y, z, rangeFactor);
				if (ReikaRandomHelper.doWithChance(chanceFactor*100/20))
					fightEerie(world, x, y, z, rangeFactor);
				if (ReikaRandomHelper.doWithChance(chanceFactor*100/10))
					convertPureNodeMagic(world, x, y, z, rangeFactor);
			}
			if (ModList.MYSTCRAFT.isLoaded() && ReikaMystcraftHelper.isMystAge(world)) {
				if (ReikaRandomHelper.doWithChance(chanceFactor*100/20))
					fightInstability(world, x, y, z);
				if (ReikaRandomHelper.doWithChance(chanceFactor*100/10))
					fightDecay(world, x, y, z, rangeFactor);
			}
			if (spreadForest && ChromaOptions.RAINBOWSPREAD.getState() && ReikaRandomHelper.doWithChance(chanceFactor*100/50) && world.getBiomeGenForCoords(x, z) == ChromatiCraft.rainbowforest)
				convertToRainbowForest(world, x, y, z, rangeFactor);
		}
	}

	public static void fightDecay(World world, int x, int y, int z, double rangeFactor) {
		if (MystCraftHandler.getInstance().decayID != null) {
			int r = (int)(64*rangeFactor);
			int rx = ReikaRandomHelper.getRandomPlusMinus(x, r);
			int rz = ReikaRandomHelper.getRandomPlusMinus(z, r);
			ReikaChunkHelper.removeBlocksFromChunk(world, rx, rz, MystCraftHandler.getInstance().decayID, -1);
		}
	}

	public static void fightInstability(World world, int x, int y, int z) {
		ReikaMystcraftHelper.decrInstabilityForAge(world, 1);
	}

	public static void addInstability(World world, int x, int y, int z) {
		ReikaMystcraftHelper.addInstabilityForAge(world, (short)1);
	}

	public static void convertPureNodeMagic(World world, int x, int y, int z, double rangeFactor) {
		int dr = (int)(64*rangeFactor);
		int rx = ReikaRandomHelper.getRandomPlusMinus(x, dr);
		int rz = ReikaRandomHelper.getRandomPlusMinus(z, dr);

		if (world.checkChunksExist(rx, 0, rz, rx, 255, rz)) {
			int r = 3;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = rx+i;
					int dz = rz+k;
					BiomeGenBase biome = world.getBiomeGenForCoords(dx, dz);
					int id = biome.biomeID;
					if (id == ThaumIDHandler.Biomes.MAGICFOREST.getID()) {
						BiomeGenBase natural = ReikaWorldHelper.getNaturalGennedBiomeAt(world, dx, dz);
						if (natural != null && ChromatiCraft.isRainbowForest(natural)) {
							ReikaWorldHelper.setBiomeForXZ(world, dx, dz, natural);
						}
					}
				}
			}
		}
	}

	public static void fightEerie(World world, int x, int y, int z, double rangeFactor) {
		int dr = (int)(32*rangeFactor);
		int rx = ReikaRandomHelper.getRandomPlusMinus(x, dr);
		int rz = ReikaRandomHelper.getRandomPlusMinus(z, dr);

		if (world.checkChunksExist(rx, 0, rz, rx, 255, rz)) {
			int r = 3;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = rx+i;
					int dz = rz+k;
					BiomeGenBase biome = world.getBiomeGenForCoords(dx, dz);
					int id = biome.biomeID;
					if (id == ThaumIDHandler.Biomes.EERIE.getID()) {
						BiomeGenBase natural = ReikaWorldHelper.getNaturalGennedBiomeAt(world, dx, dz);
						if (natural != null) {
							ReikaWorldHelper.setBiomeForXZ(world, dx, dz, natural);
						}
					}
				}
			}
		}
	}

	public static void fightTaint(World world, int x, int y, int z, double rangeFactor) {
		int dr = (int)(32*rangeFactor);
		int rx = ReikaRandomHelper.getRandomPlusMinus(x, dr);
		int rz = ReikaRandomHelper.getRandomPlusMinus(z, dr);

		if (world.checkChunksExist(rx, 0, rz, rx, 255, rz)) {
			int r = 3;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = rx+i;
					int dz = rz+k;
					BiomeGenBase biome = world.getBiomeGenForCoords(dx, dz);
					int id = biome.biomeID;
					if (id == ThaumIDHandler.Biomes.TAINT.getID()) {
						//ReikaJavaLibrary.pConsole(dx+", "+dz, Side.CLIENT);
						BiomeGenBase natural = ReikaWorldHelper.getNaturalGennedBiomeAt(world, dx, dz);
						if (natural != null) {
							ReikaWorldHelper.setBiomeForXZ(world, dx, dz, natural);
						}
					}
				}
			}
		}
	}

	public static void convertToRainbowForest(World world, int x, int y, int z, double rangeFactor) {
		int dr = (int)(32*rangeFactor);
		int rx = ReikaRandomHelper.getRandomPlusMinus(x, dr);
		int rz = ReikaRandomHelper.getRandomPlusMinus(z, dr);

		if (world.checkChunksExist(rx, 0, rz, rx, 255, rz)) {
			int r = 3;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					int dx = rx+i;
					int dz = rz+k;
					if (!TileEntityBiomeReverter.stopConversion(world, dx, dz)) {
						BiomeGenBase biome = world.getBiomeGenForCoords(dx, dz);
						int id = biome.biomeID;
						if (id != ChromatiCraft.rainbowforest.biomeID) {
							ReikaWorldHelper.setBiomeForXZ(world, dx, dz, ChromatiCraft.rainbowforest);
						}
					}
				}
			}
		}
	}
}

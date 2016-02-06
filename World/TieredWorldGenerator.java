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

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ChromaDecorator;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre.TieredOres;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredPlant.TieredPlants;
import Reika.ChromatiCraft.ModInterface.MystPages;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;

public class TieredWorldGenerator implements RetroactiveGenerator, ChromaDecorator {

	public static final TieredWorldGenerator instance = new TieredWorldGenerator();

	public boolean skipPlants = false;
	public boolean skipOres = false;

	private TieredWorldGenerator() {

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		chunkX *= 16;
		chunkZ *= 16;

		if (!skipPlants && this.generateIn(world, false)) {

			for (int i = 0; i < TieredPlants.list.length; i++) {
				TieredPlants p = TieredPlants.list[i];
				boolean flag = false;
				if (random.nextInt(p.getGenerationChance()) == 0) {
					int n = p.getGenerationCount();
					for (int k = 0; k < n; k++) {
						int posX = chunkX + random.nextInt(16);
						int posZ = chunkZ + random.nextInt(16);
						Coordinate c = p.generate(world, posX, posZ, random);
						if (c != null) {
							c.setBlock(world, p.getBlock(), p.ordinal());
						}
					}
				}
			}
		}

		if (!skipOres && this.generateIn(world, true)) {
			for (int i = 0; i < TieredOres.list.length; i++) {
				TieredOres p = TieredOres.list[i];
				boolean flag = false;
				if (random.nextInt(p.getGenerationChance()) == 0) {
					int n = p.getGenerationCount();
					for (int k = 0; k < n; k++) {
						int posX = chunkX + random.nextInt(16);
						int posZ = chunkZ + random.nextInt(16);
						flag |= p.generate(world, posX, posZ, random);
					}
				}
			}
		}

		skipOres = false;
		skipPlants = false;

	}

	private boolean generateIn(World world, boolean ore) {
		if (world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue())
			return true;
		if (world.getWorldInfo().getTerrainType() == WorldType.FLAT && !ChromaOptions.FLATGEN.getState())
			return false;
		if (Math.abs(world.provider.dimensionId) <= 1)
			return true;
		if (ModList.MYSTCRAFT.isLoaded() && ReikaMystcraftHelper.isMystAge(world)) {
			if (ore ? !MystPages.Pages.ORES.existsInWorld(world) : !MystPages.Pages.PLANTS.existsInWorld(world)) {
				return false;
			}
		}
		return !world.provider.hasNoSky;
	}

	@Override
	public boolean canGenerateAt(Random rand, World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ChromatiCraft Tiered Materials";
	}

	@Override
	public String getCommandID() {
		return "tiered";
	}

}

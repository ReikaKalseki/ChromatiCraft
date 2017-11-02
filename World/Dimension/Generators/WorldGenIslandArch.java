/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.World.Dimension.ArchCalculator.Arch;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Auxiliary.WorldGenInterceptionRegistry;
import Reika.DragonAPI.Instantiable.Event.SetBlockEvent;


public class WorldGenIslandArch extends ChromaWorldGenerator {

	public WorldGenIslandArch(DimensionGenerators g, Random r, long s) {
		super(g, r, s);
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.02F;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		y = 0;
		Block b = world.getBlock(x, y, z);
		while (b != Blocks.water && b != Blocks.air) {
			y++;
			b = world.getBlock(x, y, z);
		}
		if (b == Blocks.water) {
			Arch a = new Arch(x, y, z, rand.nextDouble()*360, 30+rand.nextDouble()*60, 0.5+rand.nextDouble(), 4+rand.nextInt(17), rand.nextInt(4), 1+rand.nextDouble()*2);
			a.calculate(rand);
			WorldGenInterceptionRegistry.skipLighting = true;
			SetBlockEvent.eventEnabledPre = false;
			SetBlockEvent.eventEnabledPost = false;
			a.generate(world, rand);
			WorldGenInterceptionRegistry.skipLighting = false;
			SetBlockEvent.eventEnabledPre = true;
			SetBlockEvent.eventEnabledPost = true;
			//ReikaJavaLibrary.pConsole("Generating arch @ "+x+", "+y+", "+z+" with angles "+a.compassAngle+", "+a.initAngle+", "+a.angleDelta);
			return true;
		}
		return false;
	}

}

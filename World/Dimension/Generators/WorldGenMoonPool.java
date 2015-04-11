package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.Random;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;

public class WorldGenMoonPool extends ChromaWorldGenerator {

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		return false;
	}

	@Override
	public float getGenerationChance(int cx, int cz) {
		return 0.002F;
	}

}

package Reika.ChromatiCraft.World;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;


public class BiomeGlowingCliffs extends BiomeGenBase {

	public BiomeGlowingCliffs(int id) {
		super(id);

		biomeName = "Luminous Cliffs";

		rootHeight = BiomeGenBase.forest.rootHeight+0.25F;
		heightVariation = 10;
	}

	@Override
	public void genTerrainBlocks(World world, Random rand, Block[] arr, byte[] m, int a, int b, double c) {
		super.genTerrainBlocks(world, rand, arr, m, a, b, c);
	}

}

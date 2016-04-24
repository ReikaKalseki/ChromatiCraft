package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco;
import Reika.ChromatiCraft.World.Dimension.BiomeDistributor;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.SubBiomes;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.SimplexNoiseGenerator;


public class WorldGenSkylandCanyons extends ChromaWorldGenerator {

	private final SimplexNoiseGenerator canyonDepth;
	private final SimplexNoiseGenerator canyonShape;

	private final SimplexNoiseGenerator bottomEdge;
	private final SimplexNoiseGenerator bottomLowerEdge;

	private final SimplexNoiseGenerator islandIntensity;

	private static final double MIN_DEPTH = 12;
	private static final double MAX_DEPTH = 64+ChunkProviderChroma.VERTICAL_OFFSET-10;

	public static final int LOW_FLOOR = 10;
	public static final int HIGH_FLOOR = 30;

	public static final int LOW_CEIL = 35;
	public static final int HIGH_CEIL = 50;

	public static final int BIOME_SEARCH = 24;

	public static final int VERTICAL_BUFFER = 12;

	public WorldGenSkylandCanyons(DimensionGenerators g, Random rand, long seed) {
		super(g, rand, seed);

		canyonDepth = new SimplexNoiseGenerator(seed);
		canyonShape = new SimplexNoiseGenerator(~seed);

		bottomEdge = new SimplexNoiseGenerator(-seed);
		bottomLowerEdge = new SimplexNoiseGenerator(~(-seed));

		islandIntensity = new SimplexNoiseGenerator(seed ^ -seed);
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 1;
	}

	@Override
	public boolean randomizePosition() {
		return false;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		int chunkX = x >> 4;
		int chunkZ = z >> 4;
		double innerScale = 1/16D;
		double mainScale = 2/3D;
		for (int i = 0; i < 16; i++) {
			for (int k = 0; k < 16; k++) {
				int dx = x+i;
				int dz = z+k;
				boolean isVoid = BiomeDistributor.getBiome(dx, dz).getExactType() == SubBiomes.VOIDLANDS;
				int dt = 1+rand.nextInt(4);
				double rx = this.calcR(chunkX, i, innerScale, mainScale);
				double rz = this.calcR(chunkZ, k, innerScale, mainScale);
				double raw = Math.pow(1-Math.abs(canyonShape.getValue(rx, rz)), 5D);
				double df = ReikaMathLibrary.normalizeToBounds(canyonDepth.getValue(rx, rz), MIN_DEPTH, MAX_DEPTH);
				double depth = MathHelper.clamp_double(raw*df, 0, MAX_DEPTH);
				depth *= this.getEdgeFactor(world, dx, dz);
				int h = (int)depth;
				for (int j = 0; j <= h+VERTICAL_BUFFER; j++) {
					int dy = 64+ChunkProviderChroma.VERTICAL_OFFSET+VERTICAL_BUFFER-j;
					this.cutBlock(world, dx, dy, dz);
				}
				world.setBlock(dx, 64+ChunkProviderChroma.VERTICAL_OFFSET-h-1, dz, Blocks.grass, 0, 2);
				for (int di = 0; di < dt; di++) {
					world.setBlock(dx, 64+ChunkProviderChroma.VERTICAL_OFFSET-h-2-di, dz, Blocks.dirt, 0, 2);
				}
				int h1 = isVoid ? 0 : (int)ReikaMathLibrary.normalizeToBounds(bottomLowerEdge.getValue(rx, rz), LOW_FLOOR, HIGH_FLOOR);
				int h2 = (int)ReikaMathLibrary.normalizeToBounds(bottomEdge.getValue(rx, rz), LOW_CEIL, HIGH_CEIL);
				for (int dy = h1; dy <= h2; dy++) {
					this.cutBlock(world, dx, dy, dz);
				}
				if (h1 > 0) {
					world.setBlock(dx, h1-1, dz, Blocks.grass, 0, 2);
					for (int di = 0; di < dt; di++) {
						world.setBlock(dx, h1-2-di, dz, Blocks.dirt, 0, 2);
					}
				}

				if (isVoid) {
					double densityControl = ReikaMathLibrary.normalizeToBounds(islandIntensity.getValue(rx, rz), -20, -3); //more negative for less
					double islandHeight = Math.max(0, ReikaMathLibrary.normalizeToBounds(bottomLowerEdge.getValue(rx, rz), densityControl, HIGH_FLOOR/2D));
					if (islandHeight > 0) {
						int i1 = (int)(HIGH_FLOOR/2D-islandHeight);
						int i2 = (int)(HIGH_FLOOR/2D+islandHeight);
						for (int dy = i1; dy <= i2; dy++) {
							Block b = Blocks.stone;
							if (dy == i2) {
								b = Blocks.grass;
							}
							else if (i2-dy < dt) {
								b = Blocks.dirt;
							}
							world.setBlock(dx, dy, dz,b, 0, 2);
						}
					}
				}
			}
		}
		return true;
	}

	private void cutBlock(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if ((b == Blocks.bedrock || WorldGenFissure.canCutInto(world, x, y, z)) && !(b instanceof BlockDimensionDeco)) {
			world.setBlock(x, y, z, Blocks.air, 0, 2);
		}
	}

	private double getEdgeFactor(World world, int x, int z) {
		if (world.getWorldInfo().getTerrainType() == WorldType.FLAT)
			return 1;
		int minDist = Integer.MAX_VALUE;
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			for (int d = 0; d < BIOME_SEARCH; d++) {
				int dx = x+d*dir.offsetX;
				int dz = z+d*dir.offsetZ;
				ChromaDimensionBiome b = BiomeDistributor.getBiome(dx, dz);
				if (b != SubBiomes.MOUNTAINS.getBiome()) {
					minDist = Math.min(minDist, d);
					break;
				}
			}
		}
		return minDist == Integer.MAX_VALUE ? 1 : minDist/(double)BIOME_SEARCH;
	}

	private double calcR(int chunk, int d, double innerScale, double mainScale) {
		return (chunk+d*innerScale)*mainScale;
	}


}

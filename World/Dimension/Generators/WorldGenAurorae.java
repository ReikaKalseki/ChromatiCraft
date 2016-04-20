package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.Random;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Entity.EntityAurora;
import Reika.ChromatiCraft.Entity.EntityAurora.AuroraData;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Maps.PairMap;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;


public class WorldGenAurorae extends ChromaWorldGenerator {

	private static final WeightedRandom<AuroraColor> auroraColors = new WeightedRandom();
	private static final PairMap<AuroraColor> disallowedCombinations = new PairMap();

	public WorldGenAurorae(DimensionGenerators g, Random rand, long seed) {
		super(g, rand, seed);
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.03125F/8;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		double ang = Math.toRadians(rand.nextDouble()*360);
		double sep = 8+rand.nextDouble()*24;
		double len = 60+rand.nextDouble()*120;
		double dx = sep*Math.cos(ang+Math.toRadians(90));
		double dz = sep*Math.sin(ang+Math.toRadians(90));
		AuroraColor c1 = auroraColors.getRandomEntry();
		AuroraColor c2 = auroraColors.getRandomEntry();
		while (disallowedCombinations.contains(c1, c2))
			c2 = auroraColors.getRandomEntry();
		int num = 1+rand.nextInt(12);
		for (int i = 0; i < num; i++) {
			double x1 = x+0.5+len/2*Math.cos(ang)+dx*(i-num/2D);
			double z1 = z+0.5+len/2*Math.sin(ang)+dz*(i-num/2D);
			double x2 = x+0.5-len/2*Math.cos(ang)+dx*(i-num/2D);
			double z2 = z+0.5-len/2*Math.sin(ang)+dz*(i-num/2D);
			double y1 = Math.max(ReikaWorldHelper.getTopSolidOrLiquidBlockForDouble(world, x1, z1)+40, 120+rand.nextDouble()*100);
			double y2 = Math.max(ReikaWorldHelper.getTopSolidOrLiquidBlockForDouble(world, x2, z2)+40, 120+rand.nextDouble()*100);
			double ymax = Math.max(y1, y2);
			y1 = ymax+rand.nextDouble()*20-10;
			y2 = ymax+rand.nextDouble()*20-10;
			double speed = 0.125+rand.nextDouble()*2.375;
			AuroraData dat = new AuroraData(x1, y1, z1, x2, y2, z2, c1.color, c2.color, speed);
			EntityAurora e = new EntityAurora(world, dat);
			world.spawnEntityInWorld(e);
		}
		return true;
	}

	private static enum AuroraColor {
		RED(0xff0000, 100),
		GREEN(0x00ff00, 100),
		BLUE(0x0000ff, 100),
		WHITE(0xffffff, 60),
		YELLOW(0xffff00, 40),
		CYAN(0x00ffff, 40),
		MAGENTA(0xff00ff, 40),
		ARGON(0x50BEFF, 30),
		ORANGE(0xFF8C00, 20),
		APPLE(0x9BFF00, 20),
		PURPLE(0x8930FF, 20),
		PINK(0xFF97AE, 10);

		public final int color;
		public final double weight;

		private static final AuroraColor[] list = values();

		private AuroraColor(int c, double w) {
			color = c;
			weight = w;
		}
	}

	static {
		for (int i = 0; i < AuroraColor.list.length; i++) {
			AuroraColor a = AuroraColor.list[i];
			auroraColors.addEntry(a, a.weight);
		}

		disallowedCombinations.add(AuroraColor.ARGON, AuroraColor.PINK);
		disallowedCombinations.add(AuroraColor.APPLE, AuroraColor.PINK);
		disallowedCombinations.add(AuroraColor.GREEN, AuroraColor.PINK);
	}

}

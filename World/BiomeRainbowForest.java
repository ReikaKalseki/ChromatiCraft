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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Worldgen.ModSpawnEntry;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BiomeRainbowForest extends BiomeGenBase {

	private final Random rand = new Random();

	public final int waterColor = new Color(0, 255, 255).getRGB();
	public final int skyColor = new Color(100, 140, 255).getRGB();

	private final int waterColorDamaged = new Color(64, 64, 64).getRGB();
	private final int skyColorDamaged = new Color(32, 48, 64).getRGB();

	private static final ArrayList<ModSpawnEntry> classes = new ArrayList();
	private static final ArrayList<ModSpawnEntry> caveClasses = new ArrayList();
	private static final ArrayList<ModSpawnEntry> monsterClasses = new ArrayList();

	public BiomeRainbowForest(int id) {
		super(id);
		this.setColor(16711935);
		this.setBiomeName("Rainbow Forest");
		this.func_76733_a(5159473); //not used in 1.6.4
		this.setTemperatureRainfall(0.7F, 0.8F);

		this.initSpawnRules();

		//topBlock = (byte)ChromaBlocks.GRASS.getBlock();
	}

	private void initSpawnRules() {
		spawnableMonsterList.clear();
		spawnableCreatureList.clear();
		spawnableCaveCreatureList.clear();
		spawnableWaterCreatureList.clear();

		spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 8, 4, 4));

		spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 3, 4, 4));
		spawnableCreatureList.add(new SpawnListEntry(EntitySheep.class, 6, 4, 4));
		spawnableCreatureList.add(new SpawnListEntry(EntityPig.class, 7, 4, 4));
		spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 6, 4, 4));
		spawnableCreatureList.add(new SpawnListEntry(EntityCow.class, 6, 4, 4));
		spawnableCreatureList.add(new SpawnListEntry(EntityHorse.class, 3, 1, 3));

		spawnableMonsterList.add(new SpawnListEntry(EntityBallLightning.class, 5, 1, 1));

		spawnableWaterCreatureList.add(new SpawnListEntry(EntitySquid.class, 10, 4, 4));

		//spawnableCaveCreatureList.add(new SpawnListEntry(EntityBat.class, 10, 8, 8));

		this.addModEntities();
	}

	private void addModEntities() {
		for (int i = 0; i < classes.size(); i++) {
			ModSpawnEntry mc = classes.get(i);
			if (mc.isLoadable()) {
				spawnableCreatureList.add(mc.getEntry());
			}
		}
		for (int i = 0; i < caveClasses.size(); i++) {
			ModSpawnEntry mc = caveClasses.get(i);
			if (mc.isLoadable()) {
				spawnableCaveCreatureList.add(mc.getEntry());
			}
		}
		for (int i = 0; i < monsterClasses.size(); i++) {
			ModSpawnEntry mc = monsterClasses.get(i);
			if (mc.isLoadable()) {
				spawnableMonsterList.add(mc.getEntry());
			}
		}
	}

	@Override
	public float getFloatTemperature(int x, int y, int z)
	{
		float base = super.getFloatTemperature(x, y, z);
		float min = 0.125F;
		if (y > 127) {
			base -= (y-127)/127F*base;
		}
		if (y > 191) {
			base = 0.15F+0.025F*(y-255)/64F;
		}
		if (y >= 255) {
			base = 0.1F;
		}
		return Math.max(base, min);
	}

	@Override
	public BiomeDecorator createBiomeDecorator()
	{
		return new RainbowForestDecorator();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getModdedBiomeGrassColor(int original)
	{
		Color c = ReikaDyeHelper.dyes[rand.nextInt(16)].getJavaColor();
		double bias = 0.0005;
		double d = 1D-bias;
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		int r2 = ReikaColorAPI.getRed(original);
		int g2 = ReikaColorAPI.getGreen(original);
		int b2 = ReikaColorAPI.getBlue(original);
		return new Color((int)(r2*d+bias*r), (int)(g2*d+bias*g), (int)(b2*d+bias*b)).getRGB();
	}

	@Override
	public int getBiomeGrassColor(int x, int y, int z) //use dyeGrass block, with metadata
	{
		int original = BiomeGenBase.forest.getBiomeGrassColor(x, y, z);
		Color c = ReikaDyeHelper.dyes[(Math.abs(x/16)+y+Math.abs(z/16))%16].getJavaColor().brighter();
		double bias = this.isDamaged(null, x, z) ? 0.5 : 0.00095;
		double d = 1D-bias;
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		int r2 = ReikaColorAPI.getRed(original);
		int g2 = ReikaColorAPI.getGreen(original);
		int b2 = ReikaColorAPI.getBlue(original);
		return new Color((int)(r2*d+bias*r), (int)(g2*d+bias*g), (int)(b2*d+bias*b)).getRGB();
	}

	@Override
	public float getSpawningChance()
	{
		return 0.1F*(Math.max(1, ChromaOptions.ANIMALSPAWN.getValue()));
	}

	@Override
	public int getSkyColorByTemp(float par1)
	{
		//return new Color(130, 170, 255).getRGB(); //original
		//return new Color(160, 130, 205).getRGB();
		//return new Color(0, 0, 0).getRGB();
		return isDamaged(null, 0, 0) ? skyColorDamaged : skyColor; //original
	}

	@Override
	public int getWaterColorMultiplier()
	{
		return isDamaged(null, 0, 0) ? waterColorDamaged : waterColor;
	}

	@Override
	public void plantFlower(World world, Random rand, int x, int y, int z) {
		if (rand.nextInt(8) > 0 && ChromaBlocks.DYEFLOWER.getBlockInstance().canBlockStay(world, x, y, z)) {
			int meta = rand.nextInt(16);
			world.setBlock(x, y, z, ChromaBlocks.DYEFLOWER.getBlockInstance(), meta, 3);
		}
		else {
			super.plantFlower(world, rand, x, y, z);
		}
	}

	public static boolean isMobAllowed(EntityLivingBase e) {
		if (e instanceof EntityBallLightning) {
			return true;
		}
		else if (e instanceof EntitySlime) {
			EntitySlime es = (EntitySlime)e;
			return es.getSlimeSize() <= 1;
		}
		else if (ModList.THAUMCRAFT.isLoaded() && e.getClass().getSimpleName().equalsIgnoreCase("EntityWisp")) {
			return true;
		}
		else if (ReikaEntityHelper.isHostile(e)) {
			return false;
		}
		else {
			return true;
		}
	}

	public static boolean isDamaged(IBlockAccess world, int x, int z) {
		return false;
	}

	static {
		classes.add(new ModSpawnEntry(ModList.TWILIGHT, "twilightforest.entity.passive.EntityTFBighorn", 3, 1, 3));
		classes.add(new ModSpawnEntry(ModList.TWILIGHT, "twilightforest.entity.passive.EntityTFBunny", 4, 1, 2));
		classes.add(new ModSpawnEntry(ModList.TWILIGHT, "twilightforest.entity.passive.EntityTFDeer", 1, 1, 8));
		classes.add(new ModSpawnEntry(ModList.TWILIGHT, "twilightforest.entity.passive.EntityTFRaven", 3, 1, 1));
		classes.add(new ModSpawnEntry(ModList.TWILIGHT, "twilightforest.entity.passive.EntityTFSquirrel", 5, 1, 2));
		classes.add(new ModSpawnEntry(ModList.TWILIGHT, "twilightforest.entity.passive.EntityTFTinyBird", 10, 1, 4));

		caveClasses.add(new ModSpawnEntry(ModList.TWILIGHT, "twilightforest.entity.passive.EntityTFMobileFirefly", 20, 1, 1));

		monsterClasses.add(new ModSpawnEntry(ModList.THAUMCRAFT, "thaumcraft.common.entities.monster.EntityWisp", 5, 1, 1));
	}

}

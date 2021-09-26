/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;

import Reika.DragonAPI.Instantiable.Math.Noise.NoiseGeneratorBase;
import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RainbowRiver extends BiomeRainbowForest {

	private static final NoiseGeneratorBase waterColorMix = new SimplexNoiseGenerator(System.currentTimeMillis()*2387).setFrequency(1/8D);

	public RainbowRiver(int id) {
		super(id);

		biomeName = "Rainbow Stream";

		rootHeight = BiomeGenBase.river.rootHeight;
		heightVariation = BiomeGenBase.river.heightVariation;
	}

	@Override
	public BiomeDecorator createBiomeDecorator() {
		return new DecoratorRainbowRiver();
	}

	@Override
	public float getFloatTemperature(int x, int y, int z) {
		return super.getFloatTemperature(x, y, z)-0.0625F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getMapColor(World world, int x, int z) {
		return ReikaColorAPI.mixColors(super.getMapColor(world, x, z), 0xffffff, 0.75F);
	}

	@Override
	protected void initSpawnRules() {
		super.initSpawnRules();
	}

	@Override
	public int getWaterColor(IBlockAccess world, int x, int y, int z, int l) {
		return ReikaColorAPI.mixColors(super.getWaterColor(world, x, y, z, l), 0x0000ff, this.getNoiseMix(waterColorMix, x, z));
	}

	private float getNoiseMix(NoiseGeneratorBase gen, int x, int z) {
		return (float)ReikaMathLibrary.normalizeToBounds(gen.getValue(x, z), 0.5, 1);
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;

public class CustomEndProvider extends WorldProviderEnd {

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new CustomEndChunkProvider(worldObj, worldObj.getSeed());
	}

	public static class CustomEndChunkProvider extends ChunkProviderEnd {

		public CustomEndChunkProvider(World world, long seed) {
			super(world, seed);
		}

		@Override
		protected double[] initializeNoiseField(double[] p_73187_1_, int p_73187_2_, int p_73187_3_, int p_73187_4_, int p_73187_5_, int p_73187_6_, int p_73187_7_)
		{
			ChunkProviderEvent.InitNoiseField event = new ChunkProviderEvent.InitNoiseField(this, p_73187_1_, p_73187_2_, p_73187_3_, p_73187_4_, p_73187_5_, p_73187_6_, p_73187_7_);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.getResult() == Result.DENY) return event.noisefield;

			if (p_73187_1_ == null)
			{
				p_73187_1_ = new double[p_73187_5_ * p_73187_6_ * p_73187_7_];
			}

			double d0 = 684.412D;
			double d1 = 684.412D;
			noiseData4 = noiseGen4.generateNoiseOctaves(noiseData4, p_73187_2_, p_73187_4_, p_73187_5_, p_73187_7_, 1.121D, 1.121D, 0.5D);
			noiseData5 = noiseGen5.generateNoiseOctaves(noiseData5, p_73187_2_, p_73187_4_, p_73187_5_, p_73187_7_, 200.0D, 200.0D, 0.5D);
			d0 *= 2.0D;
			noiseData1 = noiseGen3.generateNoiseOctaves(noiseData1, p_73187_2_, p_73187_3_, p_73187_4_, p_73187_5_, p_73187_6_, p_73187_7_, d0 / 80.0D, d1 / 160.0D, d0 / 80.0D);
			noiseData2 = noiseGen1.generateNoiseOctaves(noiseData2, p_73187_2_, p_73187_3_, p_73187_4_, p_73187_5_, p_73187_6_, p_73187_7_, d0, d1, d0);
			noiseData3 = noiseGen2.generateNoiseOctaves(noiseData3, p_73187_2_, p_73187_3_, p_73187_4_, p_73187_5_, p_73187_6_, p_73187_7_, d0, d1, d0);
			int k1 = 0;
			int l1 = 0;

			for (int i2 = 0; i2 < p_73187_5_; ++i2)
			{
				for (int j2 = 0; j2 < p_73187_7_; ++j2)
				{
					double d2 = (noiseData4[l1] + 256.0D) / 512.0D;

					if (d2 > 1.0D)
					{
						d2 = 1.0D;
					}

					double d3 = noiseData5[l1] / 8000.0D;

					if (d3 < 0.0D)
					{
						d3 = -d3 * 0.3D;
					}

					d3 = d3 * 3.0D - 2.0D;
					float f = (i2 + p_73187_2_ - 0) / 1.0F;
					float f1 = (j2 + p_73187_4_ - 0) / 1.0F;
					float f2 = 100.0F - MathHelper.sqrt_float(f * f + f1 * f1) * 8.0F;

					if (f2 > 80.0F)
					{
						f2 = 80.0F;
					}

					if (f2 < -100.0F)
					{
						f2 = -100.0F;
					}

					if (d3 > 1.0D)
					{
						d3 = 1.0D;
					}

					d3 /= 8.0D;
					d3 = 0.0D;

					if (d2 < 0.0D)
					{
						d2 = 0.0D;
					}

					d2 += 0.5D;
					d3 = d3 * p_73187_6_ / 16.0D;
					++l1;
					double d4 = p_73187_6_ / 2.0D;

					for (int k2 = 0; k2 < p_73187_6_; ++k2)
					{
						double d5 = 0.0D;
						double d6 = (k2 - d4) * 8.0D / d2;

						if (d6 < 0.0D)
						{
							d6 *= -1.0D;
						}

						double d7 = noiseData2[k1] / 512.0D;
						double d8 = noiseData3[k1] / 512.0D;
						double d9 = (noiseData1[k1] / 10.0D + 1.0D) / 2.0D;

						if (d9 < 0.0D)
						{
							d5 = d7;
						}
						else if (d9 > 1.0D)
						{
							d5 = d8;
						}
						else
						{
							d5 = d7 + (d8 - d7) * d9;
						}

						d5 -= 8.0D;

						//d5 += f2;
						//ReikaJavaLibrary.pConsole(f2);

						byte b0 = 2;
						double d10;

						if (k2 > p_73187_6_ / 2 - b0)
						{
							d10 = (k2 - (p_73187_6_ / 2 - b0)) / 64.0F;

							if (d10 < 0.0D)
							{
								d10 = 0.0D;
							}

							if (d10 > 1.0D)
							{
								d10 = 1.0D;
							}

							d5 = d5 * (1.0D - d10) + -3000.0D * d10;
						}

						b0 = 8;

						if (k2 < b0)
						{
							d10 = (b0 - k2) / (b0 - 1.0F);
							d5 = d5 * (1.0D - d10) + -30.0D * d10;
						}

						p_73187_1_[k1] = d5;
						++k1;
					}
				}
			}

			return p_73187_1_;
		}

	}
}

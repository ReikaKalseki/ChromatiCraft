/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.MapGen;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ChromaFeatureBase;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class MapGenCanyons extends ChromaFeatureBase {

	//private float[] field_75046_d = new float[1024];

	public MapGenCanyons() {
		range = 0;
	}

	@Override
	protected void generate(World world, int local_chunkX, int local_chunkZ, int chunkX, int chunkZ, Block[] data) {
		/*
		double ravineX = local_chunkX * 16 + rand.nextInt(16);
		double ravineY = rand.nextInt(rand.nextInt(40) + 8) + 20;
		double ravineZ = local_chunkZ * 16 + rand.nextInt(16);

		float f = rand.nextFloat() * (float)Math.PI * 2.0F;
		float f1 = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
		float f2 = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
		if (rand.nextInt(8) == 0)
			this.func_151540_a(rand.nextLong(), chunkX, chunkZ, data, ravineX, ravineY, ravineZ, f2, f, f1, 0, 0, 3.0D);
		 */

		if (rand.nextInt(32) == 0) {

			int dx = rand.nextInt(16);
			int dz = rand.nextInt(16);
			int x = local_chunkX*16+dx;
			int z = local_chunkZ*16+dz;
			int l = ReikaRandomHelper.getRandomPlusMinus(64, 32);
			int ymin = ReikaRandomHelper.getRandomPlusMinus(24, 12);
			for (int y = ymin; y < 255; y++) {
				this.digBlock(data, dx, y, dz);

				int dy = y-ymin;
				int r = 2+(int)(2*Math.sqrt(dy));

				for (int i = -r; i <= r; i++) {
					for (int k = -r; k <= r; k++) {
						this.digBlock(data, dx+i, y, dz+k);
					}
				}
			}

		}

	}

	private void digBlock(Block[] data, int x, int y, int z) {
		int idx = this.getIndex(x, y, z);
		if (idx >= 0 && idx < data.length)
			data[idx] = null;
	}

	/*
	protected void func_151540_a(long seed, int cx, int cz, Block[] data, double rx, double ry, double rz, float f2, float f, float f1, int i1, int i2, double d1)
	{
		Random random = new Random(seed);
		double lx = cx * 16 + 8;
		double lz = cz * 16 + 8;
		float f3 = 0.0F;
		float f4 = 0.0F;

		if (i2 <= 0)
		{
			int j1 = range * 16 - 16;
			i2 = j1 - random.nextInt(j1 / 4);
		}

		boolean flag1 = false;

		if (i1 == -1)
		{
			i1 = i2 / 2;
			flag1 = true;
		}

		float f5 = 1.0F;

		for (int k1 = 0; k1 < 256; ++k1)
		{
			if (k1 == 0 || random.nextInt(3) == 0)
			{
				f5 = 1.0F + random.nextFloat() * random.nextFloat() * 1.0F;
			}

			field_75046_d[k1] = f5 * f5;
		}

		for (; i1 < i2; ++i1)
		{
			double d12 = 1.5D + MathHelper.sin(i1 * (float)Math.PI / i2) * f2 * 1.0F;
			double d6 = d12 * d1;
			d12 *= random.nextFloat() * 0.25D + 0.75D;
			d6 *= random.nextFloat() * 0.25D + 0.75D;
			float f6 = MathHelper.cos(f1);
			float f7 = MathHelper.sin(f1);
			rx += MathHelper.cos(f) * f6;
			ry += f7;
			rz += MathHelper.sin(f) * f6;
			f1 *= 0.7F;
			f1 += f4 * 0.05F;
			f += f3 * 0.05F;
			f4 *= 0.8F;
			f3 *= 0.5F;
			f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

			if (flag1 || random.nextInt(4) != 0)
			{
				double dx = rx - lx;
				double dz = rz - lz;
				double d9 = i2 - i1;
				double d10 = f2 + 2.0F + 16.0F;

				if (dx * dx + dz * dz - d9 * d9 > d10 * d10)
				{
					return;
				}

				if (rx >= lx - 16.0D - d12 * 2.0D && rz >= lz - 16.0D - d12 * 2.0D && rx <= lx + 16.0D + d12 * 2.0D && rz <= lz + 16.0D + d12 * 2.0D)
				{
					int i4 = MathHelper.floor_double(rx - d12) - cx * 16 - 1;
					int l1 = MathHelper.floor_double(rx + d12) - cx * 16 + 1;
					int j4 = MathHelper.floor_double(ry - d6) - 1;
					int n2 = MathHelper.floor_double(ry + d6) + 1;
					int k4 = MathHelper.floor_double(rz - d12) - cz * 16 - 1;
					int j2 = MathHelper.floor_double(rz + d12) - cz * 16 + 1;

					if (i4 < 0)
					{
						i4 = 0;
					}

					if (l1 > 16)
					{
						l1 = 16;
					}

					if (j4 < 1)
					{
						j4 = 1;
					}

					if (n2 > 248)
					{
						n2 = 248;
					}

					if (k4 < 0)
					{
						k4 = 0;
					}

					if (j2 > 16)
					{
						j2 = 16;
					}

					boolean flag2 = false;
					int k2;
					int j3;

					for (k2 = i4; !flag2 && k2 < l1; ++k2)
					{
						for (int l2 = k4; !flag2 && l2 < j2; ++l2)
						{
							for (int i3 = n2 + 1; !flag2 && i3 >= j4 - 1; --i3)
							{
								j3 = (k2 * 16 + l2) * 256 + i3;

								if (i3 >= 0 && i3 < 256)
								{
									Block block = data[j3];

									if (i3 != j4 - 1 && k2 != i4 && k2 != l1 - 1 && l2 != k4 && l2 != j2 - 1)
									{
										i3 = j4;
									}
								}
							}
						}
					}

					if (!flag2)
					{
						for (k2 = i4; k2 < l1; ++k2)
						{
							double d13 = (k2 + cx * 16 + 0.5D - rx) / d12;

							for (j3 = k4; j3 < j2; ++j3)
							{
								double d14 = (j3 + cz * 16 + 0.5D - rz) / d12;
								int k3 = (k2 * 16 + j3) * 256 + n2;
								boolean flag = false;

								if (d13 * d13 + d14 * d14 < 1.0D)
								{
									for (int l3 = n2 - 1; l3 >= j4; --l3)
									{
										double d11 = (l3 + 0.5D - ry) / d6;

										if ((d13 * d13 + d14 * d14) * field_75046_d[l3] + d11 * d11 / 6.0D < 1.0D)
										{
											Block block1 = data[k3];

											this.digBlock(data, k3, k2, l3, j3, cx, cz, flag);
										}

										--k3;
									}
								}
							}
						}

						if (flag1)
						{
							break;
						}
					}
				}
			}
		}
	}

	private void digBlock(Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop) {
		data[index] = null;
	}
	 */

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.MapGen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.MapGenCaves;

public class MapGenTendrils extends MapGenCaves {

	@Override
	protected void digBlock(Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
	{
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
		Block top    = biome.topBlock;
		Block filler = biome.fillerBlock;
		Block block  = data[index];

		if (block == Blocks.air)
		{
			if (y < 10)
			{
				data[index] = Blocks.quartz_block;
			}
			else
			{
				data[index] = Blocks.stone;

				if (foundTop && data[index - 1] == filler)
				{
					data[index - 1] = top;
				}
			}
		}
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

public class ChromaChunkManager extends WorldChunkManager {

	public ChromaChunkManager(World world) {
		super(world.getSeed(), world.getWorldInfo().getTerrainType());
	}

	@Override
	public BiomeGenBase getBiomeGenAt(int x, int z)
	{
		//return Math.abs((z/32)%4) > 0 ? BiomeGenBase.deepOcean : Math.abs((x/32)%4) > 0 ? BiomeGenBase.extremeHills : BiomeGenBase.beach;
		return BiomeGenBase.icePlains;
	}

	@Override
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] data, int cx, int cz, int w, int l, boolean cache)
	{
		return this.getBiomesForGeneration(data, cx, cz, w, l);
	}

	@Override
	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] data, int cx, int cz, int w, int l)
	{
		int n = w*l;
		if (data == null || data.length < n)
		{
			data = new BiomeGenBase[n];
		}

		//ReikaJavaLibrary.pConsole(cx+","+cz+ " by "+w+"/"+l);
		for (int i = 0; i < n; i++) {
			data[i] = this.getBiomeGenAt(cx*16+i%16, cz*16+i/16);
		}

		return data;
	}

	private int[] getXZIndex(int idx) {
		return new int[]{0, 0};
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import Reika.ChromatiCraft.World.Dimension.Structure.Monument.MonumentHighlighter;
import Reika.ChromatiCraft.World.Dimension.Structure.Monument.MonumentMineralBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.Monument.MonumentStructure;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class MonumentGenerator {

	private final ChunkSplicedGenerationCache world = new ChunkSplicedGenerationCache();

	private final HashMap<Coordinate, Block> mineralBlocks = new HashMap();

	protected int posX;
	protected int posY;
	protected int posZ;

	public MonumentGenerator() {

	}

	public final int getPosX() {
		return posX;
	}

	public final int getPosY() {
		return posY;
	}

	public final int getPosZ() {
		return posZ;
	}

	public final void startCalculate(int x, int z, Random rand) {
		//genCore = new ChunkCoordIntPair(chunkX >> 4, chunkZ >> 4);
		posX = x;
		posZ = z;
		posY = 103;

		int r = 32;
		int r2 = 24;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				for (int j = 0; j <= r2; j++) {
					if (ReikaMathLibrary.isPointInsideEllipse(i, j, k, r, r2, r)) {
						world.setBlock(x+i, posY+j, z+k, Blocks.air);
					}
					world.setBlock(x+i, posY-1, z+k, Blocks.grass);
				}
			}
		}

		new MonumentStructure().generate(world, rand, x-21, posY, z-21);
		new MonumentMineralBlocks(this).generate(world, rand, x-21, posY, z-21);
		new MonumentHighlighter().generate(world, rand, x-21, posY, z-21);

		//center.generate(world, rand, x, posY, z, li);
		/*
		for (ForgeDirection dir : li) {
			MonumentTunnel mt = new MonumentTunnel(dir, 8+rand.nextInt(24));
			mt.generate(world, rand, x+14+dir.offsetX*15, posY+1, z+14+dir.offsetZ*15);
		}
		 */
	}

	public final void generateChunk(World w, ChunkCoordIntPair cp) {
		world.generate(w, cp);

		/*
		Collection<DynamicPieceLocation> c = dynamicParts.get(cp);
		if (c != null) {
			for (DynamicPieceLocation dsp : c) {
				int x = (cp.chunkXPos << 4)+dsp.relX;
				int z = (cp.chunkZPos << 4)+dsp.relZ;
				dsp.generator.generate(w, x, z);
			}
		}
		 */
	}

	public final void generateAll(World w) {
		world.generateAll(w);

		/*
		for (ChunkCoordIntPair cp : dynamicParts.keySet()) {
			Collection<DynamicPieceLocation> c = dynamicParts.get(cp);
			if (c != null) {
				for (DynamicPieceLocation dsp : c) {
					int x = (cp.chunkXPos << 4)+dsp.relX;
					int z = (cp.chunkZPos << 4)+dsp.relZ;
					dsp.generator.generate(w, x, z);
				}
			}
		}
		 */
	}


	public void clear() {
		world.clear();
		mineralBlocks.clear();
	}

	public void registerMineralBlock(int x, int y, int z, Block b) {
		mineralBlocks.put(new Coordinate(x, y, z), b);
	}

	public Map<Coordinate, Block> getMineralBlocks() {
		return Collections.unmodifiableMap(mineralBlocks);
	}

}

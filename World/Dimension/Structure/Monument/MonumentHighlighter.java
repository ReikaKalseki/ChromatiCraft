/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Monument;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;


public class MonumentHighlighter {

	public void generate(ChunkSplicedGenerationCache world, Random r, int i, int j, int k) {

		world.setTileEntity(i + 21, j + 5, k + 21, ChromaTiles.STRUCTCONTROL.getBlock(), ChromaTiles.STRUCTCONTROL.getBlockMetadata(), new MonumentPlace());

		Block rn = ChromaBlocks.RUNE.getBlockInstance();

		world.setBlock(i + 3, j + 11, k + 18, rn, 0);
		world.setBlock(i + 7, j + 11, k + 13, rn, 1);
		world.setBlock(i + 13, j + 11, k + 7, rn, 2);
		world.setBlock(i + 18, j + 11, k + 3, rn, 3);
		world.setBlock(i + 24, j + 11, k + 3, rn, 4);
		world.setBlock(i + 29, j + 11, k + 7, rn, 5);
		world.setBlock(i + 35, j + 11, k + 13, rn, 6);
		world.setBlock(i + 39, j + 11, k + 18, rn, 7);
		world.setBlock(i + 39, j + 11, k + 24, rn, 8);
		world.setBlock(i + 35, j + 11, k + 29, rn, 9);
		world.setBlock(i + 29, j + 11, k + 35, rn, 10);
		world.setBlock(i + 24, j + 11, k + 39, rn, 11);
		world.setBlock(i + 18, j + 11, k + 39, rn, 12);
		world.setBlock(i + 13, j + 11, k + 35, rn, 13);
		world.setBlock(i + 7, j + 11, k + 29, rn, 14);
		world.setBlock(i + 3, j + 11, k + 24, rn, 15);
	}

	private static class MonumentPlace implements TileCallback {

		private MonumentPlace() {

		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TileEntityStructControl) {
				TileEntityStructControl ts = (TileEntityStructControl)te;
				ts.setMonument();
			}
		}

	}

}

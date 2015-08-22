/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockGOLController.GOLController;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockGOLTile.GOLTile;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;

public class GOLGenerator extends DimensionStructureGenerator {

	public static final int SIZE = 16;

	protected int floorY;

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {
		posY = 200;
		floorY = posY+1;

		for (int i = -SIZE; i < SIZE; i++) {
			for (int k = -SIZE; k < SIZE; k++) {
				this.placeTile(i+posX, k+posZ, rand.nextInt(5) == 0);
			}
		}

		world.setTileEntity(posX-SIZE, floorY+2, posZ, ChromaBlocks.GOLCONTROL.getBlockInstance(), 0, new GOLTileCallback(this, false));
	}

	@Override
	protected int getCenterXOffset() {
		return 0;
	}

	@Override
	protected int getCenterZOffset() {
		return 0;
	}

	protected final void placeTile(int x, int z, boolean startOn) {
		world.setTileEntity(x, floorY, z, ChromaBlocks.GOL.getBlockInstance(), 0, new GOLTileCallback(this, startOn));
	}

	private static class GOLTileCallback implements TileCallback {

		private final boolean initOn;
		private final GOLGenerator generator;

		private GOLTileCallback(GOLGenerator gen, boolean on) {
			initOn = on;
			generator = gen;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof GOLTile) {
				((GOLTile)te).initialize(initOn);
			}
			if (te instanceof GOLController) {
				((GOLController)te).initialize(generator.posX-SIZE, generator.posX+SIZE, generator.posZ-SIZE, generator.posZ+SIZE, generator.floorY);
			}
		}

	}

	@Override
	public StructureData createDataStorage() {
		return null;
	}

}

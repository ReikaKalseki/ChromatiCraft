/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.ISBRH;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IBlockAccess;

import Reika.ChromatiCraft.Block.Dye.BlockDyeVine;
import Reika.DragonAPI.Base.ISBRH;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public class DyeVineRenderer extends ISBRH {

	private final Random renderRand = new Random();
	private final Random renderRandNoY = new Random();

	public DyeVineRenderer(int id) {
		super(id);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		renderRand.setSeed(ChunkCoordIntPair.chunkXZ2Int(x, z) ^ y);
		renderRand.nextBoolean();
		renderRand.nextBoolean();
		renderRandNoY.setSeed(ChunkCoordIntPair.chunkXZ2Int(x, z));
		renderRandNoY.nextBoolean();
		renderRandNoY.nextBoolean();
		float dx = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.25, renderRandNoY);
		float dz = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.25, renderRandNoY);
		float dy = (float)ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625, renderRandNoY);
		Tessellator.instance.addTranslation(dx, dy, dz);
		((BlockDyeVine)block).render(world, x, y, z, renderer, Tessellator.instance, renderRand);
		Tessellator.instance.addTranslation(-dx, -dy, -dz);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

}

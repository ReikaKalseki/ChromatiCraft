/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import Reika.ChromatiCraft.Block.BlockTieredPlant;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class TieredPlantRenderer implements ISimpleBlockRenderingHandler {

	public int renderPass;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int model, RenderBlocks rb) {
		BlockTieredPlant t = (BlockTieredPlant)b;
		if (t.isPlayerSufficientTier(world, x, y, z, Minecraft.getMinecraft().thePlayer)) {
			Tessellator v5 = Tessellator.instance;
			int meta = world.getBlockMetadata(x, y, z);
			if (renderPass == 0) {
				v5.setColorOpaque(255, 255, 255);
				v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y, z));
				rb.drawCrossedSquares(t.getBacking(meta), x, y, z, 1);
				v5.setBrightness(240);
				rb.drawCrossedSquares(t.getOverlay(meta), x, y, z, 1);
				return true;
			}/*
			else if (renderPass == 1) {
				IIcon ico = ChromaIcons.CENTER.getIcon();
				BlendMode.ADDITIVEDARK.apply();
				rb.drawCrossedSquares(ico, x, y, z, 1);
				BlendMode.DEFAULT.apply();
				return true;
			}*/
		}
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return 0;
	}

}

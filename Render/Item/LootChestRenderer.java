/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.Item;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Registry.ChromaBlocks;

public class LootChestRenderer implements IItemRenderer {

	private final TileEntityLootChest renderTile;


	public LootChestRenderer() {
		renderTile = new TileEntityLootChest();
		renderTile.blockType = ChromaBlocks.LOOTCHEST.getBlockInstance();
		renderTile.blockMetadata = 1;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		float a = 0; float b = 0;

		RenderBlocks rb = (RenderBlocks)data[0];
		if (type == type.ENTITY) {
			a = -0.5F;
			b = -0.5F;
		}
		else if (type == type.INVENTORY) {
			double s = 1.05;
			GL11.glScaled(s, s*0.95, s);
		}
		boolean entity = type == ItemRenderType.ENTITY || type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
		TileEntity te = renderTile;
		TileEntityRendererDispatcher.instance.renderTileEntityAt(te, a, 0, b, entity ? -1 : 0);
	}
}

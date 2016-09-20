/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;

public class TESRItemRenderer implements IItemRenderer {

	private final ItemHashMap<TileEntity> renderTiles = new ItemHashMap();

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
		Tessellator v5 = Tessellator.instance;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glTranslated(0, -0.125, 0);
		GL11.glDisable(GL11.GL_LIGHTING);
		float u = 0;
		float v = 0;
		float du = 1;
		float dv = 1;

		GL11.glTranslated(-0.5, -0.0625, -0.5);
		double s = 1.25;
		GL11.glScaled(s, s, s);

		if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			GL11.glTranslated(0.25, 0.25, 0.25);
		}
		else if (type == ItemRenderType.INVENTORY) {
			GL11.glTranslated(0, -0.1875, 0);
		}
		else if (type == ItemRenderType.ENTITY) {
			GL11.glTranslated(-0.125, 0.0625, -0.125);
		}

		TileEntity te = renderTiles.get(item);
		if (te == null) {
			Block b = Block.getBlockFromItem(item.getItem());
			if (b != null) {
				te = b.createTileEntity(Minecraft.getMinecraft().theWorld, item.getItemDamage());
				if (te != null) {
					renderTiles.put(item, te);
				}
			}
		}

		TileEntityRendererDispatcher.instance.renderTileEntityAt(te, 0, -0.1, 0, 0);

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

}

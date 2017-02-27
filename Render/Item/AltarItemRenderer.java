/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.Item;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Block.Crystal.BlockColoredAltar.TileEntityColoredAltar;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class AltarItemRenderer implements IItemRenderer {

	private final TileEntityColoredAltar renderTile = new TileEntityColoredAltar();

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

		renderTile.renderColor = CrystalElement.elements[item.getItemDamage()];
		TileEntityRendererDispatcher.instance.renderTileEntityAt(renderTile, 0, -0.1, 0, 0);

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

}

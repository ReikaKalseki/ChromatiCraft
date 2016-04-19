/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelCrystalCharger;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityCrystalCharger;
import Reika.DragonAPI.Interfaces.Item.IndexedItemSprites;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class RenderCrystalCharger extends ChromaRenderBase {

	private final ModelCrystalCharger model = new ModelCrystalCharger();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "charger.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityCrystalCharger te = (TileEntityCrystalCharger)tile;

		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		this.renderModel(te, model, te.getAngle());

		if (te.hasWorldObj() && te.hasItem()) {
			this.renderItem(te, par8);
		}
		GL11.glPopMatrix();
	}

	private void renderItem(TileEntityCrystalCharger te, float par8) {
		Tessellator v5 = Tessellator.instance;

		ItemStack is = te.getStackInSlot(0);
		IndexedItemSprites iis = (IndexedItemSprites)is.getItem();
		int index = iis.getItemSpriteIndex(is);
		float u = index/16F;
		float v = index%16F;
		float du = u+0.0625F;
		float dv = v+0.0625F;
		ReikaTextureHelper.bindTexture(iis.getTextureReferenceClass(), iis.getTexture(is));
		GL11.glPushMatrix();
		double ax = 0.5;
		GL11.glTranslated(ax, 0, ax);
		float angle = -te.getAngle();
		GL11.glRotated(angle, 0, 1, 0);
		GL11.glTranslated(-ax, 0, -ax);
		double s = 1.35;
		double s2 = 1.3;
		for (double d = 0.4; d <= 0.601; d += 0.2) {
			GL11.glPushMatrix();
			GL11.glTranslated(0, 0.01, d);
			double dx = 0.5;
			GL11.glTranslated(dx, 0, 0);
			GL11.glScaled(s, s2, s);
			GL11.glTranslated(-dx, 0, 0);
			GL11.glDisable(GL11.GL_CULL_FACE);
			v5.startDrawingQuads();
			v5.addVertexWithUV(0, 0, 0, u, v);
			v5.addVertexWithUV(1, 0, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(0, 1, 0, u, dv);
			v5.draw();
			GL11.glPopMatrix();
		}
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

}

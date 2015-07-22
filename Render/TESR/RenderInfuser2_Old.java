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

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.TileEntity.TileEntityAuraInfuser_Old2;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;

public class RenderInfuser2_Old extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityAuraInfuser_Old2 te = (TileEntityAuraInfuser_Old2)tile;

		GL11.glPushMatrix();

		if (te.hasWorldObj()) {
			this.renderItem(te, par2, par4, par6, par8);
		}
		GL11.glTranslated(par2, par4, par6);
		//this.renderModel(te, model);

		GL11.glPopMatrix();
	}

	private void renderItem(TileEntityAuraInfuser_Old2 te, double par2, double par4, double par6, float ptick) {
		for (int i = 0; i < 16; i++) {
			EntityItem ei = te.getItem(i);
			if (ei != null) {
				GL11.glPushMatrix();
				double a = ((te.getTicksExisted()+ptick)*3D)%360;
				double dy = 0.0625*Math.sin(Math.toRadians(a*2));
				GL11.glTranslated(0.5+ei.posX, 0.5+ei.posY, 0.5+ei.posZ);
				GL11.glTranslated(par2, par4, par6);
				GL11.glRotated(a, 0, 1, 0);
				GL11.glTranslated(-par2, -par4, -par6);

				Render r = RenderManager.instance.getEntityClassRenderObject(EntityItem.class);
				r.doRender(ei, par2, par4, par6, 0, 0);

				GL11.glPopMatrix();
			}
		}
	}

}

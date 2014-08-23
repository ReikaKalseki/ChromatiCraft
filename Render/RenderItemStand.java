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

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelItemStand;
import Reika.ChromatiCraft.TileEntity.TileEntityItemStand;
import Reika.DragonAPI.Interfaces.RenderFetcher;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

public class RenderItemStand extends ChromaRenderBase {

	private final ModelItemStand model = new ModelItemStand();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "itemstand.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityItemStand te = (TileEntityItemStand)tile;

		GL11.glPushMatrix();

		if (te.hasWorldObj()) {
			this.renderItem(te, par2, par4, par6, par8);
		}
		GL11.glTranslated(par2, par4, par6);
		this.renderModel(te, model);

		GL11.glPopMatrix();
	}

	private void renderItem(TileEntityItemStand te, double par2, double par4, double par6, float ptick) {
		EntityItem ei = te.getItem();
		if (ei != null) {
			GL11.glPushMatrix();
			double a = ((te.getTicksExisted()+ptick)*3D)%360;
			double dy = 0.0625*Math.sin(Math.toRadians(a*2));
			GL11.glTranslated(0.5, 0.625+dy, 0.5);
			GL11.glTranslated(par2, par4, par6);
			GL11.glRotated(a, 0, 1, 0);
			GL11.glTranslated(-par2, -par4, -par6);

			Render r = RenderManager.instance.getEntityClassRenderObject(EntityItem.class);
			r.doRender(ei, par2, par4, par6, 0, 0);

			GL11.glPopMatrix();
		}
	}

}

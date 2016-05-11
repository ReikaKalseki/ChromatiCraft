/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelInfuser2;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityAuraInfuser;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

public class RenderInfuser3 extends ChromaRenderBase {

	private final ModelInfuser2 model = new ModelInfuser2();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "infuser2.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityAuraInfuser te = (TileEntityAuraInfuser)tile;

		GL11.glPushMatrix();

		if (te.hasWorldObj()) {
			this.renderItem(te, par2, par4, par6, par8);
		}
		GL11.glTranslated(par2, par4, par6);
		this.renderModel(te, model);

		GL11.glPopMatrix();
	}

	private void renderItem(TileEntityAuraInfuser te, double par2, double par4, double par6, float ptick) {
		EntityItem ei = te.getItem();
		if (ei != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);
			GL11.glPushMatrix();
			double a = ((te.getTicksExisted()+ptick)*3D)%360;
			double dy = 0.0625*Math.sin(Math.toRadians(a*2));
			GL11.glTranslated(0.5+ei.posX, 0.5+ei.posY, 0.5+ei.posZ);
			GL11.glRotated(a, 0, 1, 0);
			GL11.glTranslated(-par2, -par4, -par6);

			Render r = RenderManager.instance.getEntityClassRenderObject(EntityItem.class);
			r.doRender(ei, par2, par4, par6, 0, 0);

			GL11.glPopMatrix();
			GL11.glPushMatrix();
			float mix = 1-(te.getTicksExisted()%4)/4F;
			float w = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
			double rad = 0.5;
			Tessellator v5 = Tessellator.instance;
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			ReikaRenderHelper.prepareGeoDraw(true);
			GL11.glEnable(GL11.GL_BLEND);
			for (int i = 0; i < 360; i += 45) {
				int idx = (te.getTicksExisted()/4+i/45)%16;
				int idx2 = (idx+1)%16;
				int color = ReikaColorAPI.mixColors(CrystalElement.elements[idx].getColor(), CrystalElement.elements[idx2].getColor(), mix);
				double ang = Math.toRadians(i);
				double dx = 0.5+rad*Math.cos(ang);
				double dz = 0.5+rad*Math.sin(ang);
				GL11.glLineWidth(w*6);
				v5.startDrawing(GL11.GL_LINES);
				v5.setColorRGBA_I(color, 70);
				v5.addVertex(0.5, 0.6875, 0.5);
				v5.addVertex(dx, 0.35, dz);
				v5.draw();
				GL11.glLineWidth(w*3);
				v5.startDrawing(GL11.GL_LINES);
				v5.setColorRGBA_I(color, 150);
				v5.addVertex(0.5, 0.6875, 0.5);
				v5.addVertex(dx, 0.35, dz);
				v5.draw();
				GL11.glLineWidth(w);
				v5.startDrawing(GL11.GL_LINES);
				v5.setColorOpaque_I(color);
				v5.addVertex(0.5, 0.6875, 0.5);
				v5.addVertex(dx, 0.35, dz);
				v5.draw();
			}
			ReikaRenderHelper.exitGeoDraw();
			GL11.glPopAttrib();
			GL11.glLineWidth(w);
			GL11.glPopMatrix();
			GL11.glPopMatrix();
		}
	}

}

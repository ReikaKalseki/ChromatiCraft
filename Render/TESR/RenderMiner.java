/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.TileEntity.TileEntityMiner;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderMiner extends ChromaRenderBase {

	//private final ModelMiner model = new ModelMiner();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "miner.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityMiner te = (TileEntityMiner)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		//this.renderModel(te, model);

		if (te.isInWorld()) {
			this.renderOrb(te);
			this.renderFire(te);
		}
		GL11.glPopMatrix();
	}

	private void renderOrb(TileEntityMiner te) {
		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);

		double angle = (System.currentTimeMillis()/15D)%360;

		double r = 0.5;
		double h = 0.1*r;
		double y = 0.5*(1+Math.sin(Math.toRadians(angle)));

		GL11.glTranslated(0, y, 0);

		GL11.glPushMatrix();

		GL11.glTranslated(0.5, 1.5, 0.5);
		GL11.glRotated(angle, 1, 0, 0);
		GL11.glTranslated(-0.5, -1.5, -0.5);

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/runebar.png");

		for (int i = 0; i <= 1; i++) {

			GL11.glPushMatrix();

			GL11.glTranslated(0.5, 1.5, 0.5);
			GL11.glRotated(angle*i, 0, 0, 1);
			GL11.glTranslated(-0.5, -1.5, -0.5);

			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glRotated(angle, 0, 1, 0);
			GL11.glTranslated(-0.5, -0.5, -0.5);

			v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
			v5.setBrightness(240);
			v5.setColorOpaque_I(0xffffff);
			for (int n = 0; n <= 360; n += 10) {
				double x = 0.5+r*Math.sin(Math.toRadians(n));
				double z = 0.5+r*Math.cos(Math.toRadians(n));
				double t = 2;
				double tu = t*n/360D;
				v5.addVertexWithUV(x, 1.5+h, z, tu, 0);
				v5.addVertexWithUV(x, 1.5-h, z, tu, 1);
			}
			v5.draw();
			GL11.glPopMatrix();

		}

		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	private void renderFire(TileEntityMiner te) {
		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		BlendMode.ADDITIVEDARK.apply();
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/temp/fire/done2.png");
		int r = 3;
		double u = 0;//(0.0001*System.currentTimeMillis())%8;
		double du = 1;//u+0.5;
		double v = ((System.currentTimeMillis()/64)%20)/20D;
		double dv = v+0.05;
		v5.startDrawingQuads();
		v5.setBrightness(240);

		v5.addVertexWithUV(-r+0.5, 1, r+0.5, u, v);
		v5.addVertexWithUV(-r+0.5, 0, r+0.5, u, dv);
		v5.addVertexWithUV(r+0.5, 0, r+0.5, du, dv);
		v5.addVertexWithUV(r+0.5, 1, r+0.5, du, v);

		v5.addVertexWithUV(-r+0.5, 1, -r+0.5, u, v);
		v5.addVertexWithUV(-r+0.5, 0, -r+0.5, u, dv);
		v5.addVertexWithUV(r+0.5, 0, -r+0.5, du, dv);
		v5.addVertexWithUV(r+0.5, 1, -r+0.5, du, v);

		v5.addVertexWithUV(r+0.5, 1, -r+0.5, u, v);
		v5.addVertexWithUV(r+0.5, 0, -r+0.5, u, dv);
		v5.addVertexWithUV(r+0.5, 0, r+0.5, du, dv);
		v5.addVertexWithUV(r+0.5, 1, r+0.5, du, v);

		v5.addVertexWithUV(-r+0.5, 1, -r+0.5, u, v);
		v5.addVertexWithUV(-r+0.5, 0, -r+0.5, u, dv);
		v5.addVertexWithUV(-r+0.5, 0, r+0.5, du, dv);
		v5.addVertexWithUV(-r+0.5, 1, r+0.5, du, v);

		v5.draw();

		BlendMode.DEFAULT.apply();
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

}

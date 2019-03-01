/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelFrame;
import Reika.ChromatiCraft.TileEntity.TileEntityBiomePainter;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderBiomePainter extends ChromaRenderBase {

	private final ModelFrame outerFrame = new ModelFrame(1);
	private final ModelFrame innerFrame = new ModelFrame(0.75);
	private final ModelFrame movingFrame = new ModelFrame(1);

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "biomepainterouter.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityBiomePainter te = (TileEntityBiomePainter)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		this.renderModel(te, outerFrame, this.getTextureFolder()+"biomepainterouter.png");
		this.renderInnerFrame(te, par8);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		for (int i = 0; i < 6; i++)
			this.renderGlow(te, par8, i);
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private void renderInnerFrame(TileEntityBiomePainter te, float par8) {
		GL11.glPushMatrix();
		double d = 0.5;
		//double ang = te.getTicksExisted()+par8;

		if (te.isInWorld()) {
			te.rotation.angX += te.rotation.rvX;
			te.rotation.angY += te.rotation.rvY;
			te.rotation.angZ += te.rotation.rvZ;

			double v = 0.375;
			double t = te.getTicksExisted()+par8;
			te.rotation.rvX = v*(1+0.5*Math.sin(Math.toRadians(t)))*(1+0.5*Math.cos(Math.toRadians(90+t)));
			te.rotation.rvY = v*(1+0.25*Math.sin(Math.toRadians(2*(t))))*(1+0.5*Math.cos(Math.toRadians(t)));
			te.rotation.rvZ = v*(1+0.5*Math.sin(Math.toRadians(90+t)))*(1+0.25*Math.cos(Math.toRadians(2*(t))));

			GL11.glTranslated(d, d, d);
			GL11.glRotated(te.rotation.angX, 1, 0, 0);
			GL11.glRotated(te.rotation.angY, 0, 1, 0);
			GL11.glRotated(te.rotation.angZ, 0, 0, 1);
			GL11.glTranslated(-d, -d, -d);
		}
		GL11.glTranslated(0, -0.25, 0);
		this.renderModel(te, innerFrame, this.getTextureFolder()+"biomepainterinner.png");
		GL11.glPopMatrix();
	}

	private void renderGlow(TileEntityBiomePainter te, float par8, int axis) {
		Tessellator v5 = Tessellator.instance;
		v5.setBrightness(240);
		int c = te.isInWorld() ? ReikaColorAPI.getModifiedHue(0xff0000, (te.getTicksExisted()*2+axis*10)%360) : 0xffffff;
		float r = ReikaColorAPI.getRed(c)/255F;
		float g = ReikaColorAPI.getGreen(c)/255F;
		float b = ReikaColorAPI.getBlue(c)/255F;
		GL11.glColor4f(r, g, b, 1);
		GL11.glPushMatrix();
		double s = 0.5;
		GL11.glTranslated(0.5, 0, 0.5);
		GL11.glScaled(s, s, s);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVE.apply();
		double dy = te.isInWorld() ? 0.5*Math.sin((te.getTicksExisted()+par8)/12D+axis/4D) : 0;
		if (axis >= 3) {
			dy = -dy;
		}
		double rx = 0;
		double ry = 0;
		double rz = 0;

		GL11.glPushMatrix();
		switch(axis) {
			case 0:
			case 3:
				GL11.glTranslated(0, dy, 0);
				GL11.glRotated(rx, 1, 0, 0);
				GL11.glRotated(ry, 0, 1, 0);
				GL11.glRotated(rz, 0, 0, 1);
				movingFrame.renderAll(te, null);
				break;
			case 1:
			case 4:
				GL11.glTranslated(dy, 0, 0);
				GL11.glRotated(rx, 1, 0, 0);
				GL11.glRotated(ry, 0, 1, 0);
				GL11.glRotated(rz, 0, 0, 1);
				movingFrame.renderAll(te, null);
				break;
			case 2:
			case 5:
				GL11.glTranslated(0, 0, dy);
				GL11.glRotated(rx, 1, 0, 0);
				GL11.glRotated(ry, 0, 1, 0);
				GL11.glRotated(rz, 0, 0, 1);
				movingFrame.renderAll(te, null);
				break;
		}
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}

}

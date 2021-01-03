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
import Reika.ChromatiCraft.Block.Decoration.BlockAvoLamp.TileEntityAvoLamp;
import Reika.ChromatiCraft.Models.ModelAvoLamp;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;


public class RenderAvoLamp extends ChromaRenderBase {

	private final ModelAvoLamp model = new ModelAvoLamp();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "avolamp.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityAvoLamp te = (TileEntityAvoLamp)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		this.bindTextureByName(this.getTextureFolder()+this.getImageFileName(null));

		GL11.glPushMatrix();
		if (te.hasWorldObj()) {
			int rot = 0;
			int rotx = 0;
			int rotz = 0;
			int dx = 0;
			int dz = 0;
			int dy = 0;
			switch(te.getDirection()) {
				case WEST:
					rotz = 90;
					dx = 1;
					break;
				case EAST:
					rotz = -90;
					dy = 1;
					break;
				case NORTH:
					rot = 180;
					rotx = 90;
					dy = 1;
					dx = 1;
					dz = 1;
					break;
				case SOUTH:
					rot = 180;
					rotx = -90;
					dx = 1;
					break;
				case UP:
					break;
				case DOWN:
					rotx = 180;
					dz = 1;
					dy = 1;
					break;
				case UNKNOWN:
					break;
			}
			GL11.glTranslated(dx, dy, dz);
			GL11.glRotated(rot, 0, 1, 0);
			GL11.glRotated(rotx, 1, 0, 0);
			GL11.glRotated(rotz, 0, 0, 1);
		}

		this.renderModel(te, model);

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		ReikaRenderHelper.disableEntityLighting();

		GL11.glPushMatrix();
		GL11.glTranslated(0.5, 0.5, 0.5);
		this.renderCrystal(te, par8);
		GL11.glPopMatrix();


		GL11.glPopAttrib();
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	private void renderCrystal(TileEntityAvoLamp te, float par8) {
		Tessellator v5 = Tessellator.instance;

		long t = System.currentTimeMillis();
		double hash = te.hashCode()/Integer.MAX_VALUE;

		double my = 0.0625+Math.abs(0.03125*hash);
		double dy = 0.125+my*Math.sin(te.hashCode()+t/600D);
		double sy = 0.5+hash;
		GL11.glTranslated(0, dy, 0);
		GL11.glRotated((-te.hashCode()+t*sy/20D)%360D, 0, 1, 0);

		v5.startDrawing(GL11.GL_LINES);

		double h1 = -0.25;
		double h2 = 0;
		double h3 = 0.125;
		double h4 = 0.1875;

		double r1 = 0.0;
		double r2 = 0.25;
		double r3 = 0.1875;

		int n = 60;
		for (int i = 0; i < 360; i += n) {
			float br = 0.5F*(0.75F+0.25F*(float)Math.sin(Math.toRadians(i*2+t/4D)));
			int c = ReikaColorAPI.mixColors(0xff0000, 0xffffff, br+0.125F);
			v5.setColorOpaque_I(c);
			double a1 = Math.toRadians(i);
			double a2 = Math.toRadians(i+n);
			double x1 = Math.cos(a1);
			double x2 = Math.cos(a2);
			double z1 = Math.sin(a1);
			double z2 = Math.sin(a2);

			double x11 = r1*x1;
			double x12 = r2*x1;
			double x13 = r3*x1;
			double x21 = r1*x2;
			double x22 = r2*x2;
			double x23 = r3*x2;

			double z11 = r1*z1;
			double z12 = r2*z1;
			double z13 = r3*z1;
			double z21 = r1*z2;
			double z22 = r2*z2;
			double z23 = r3*z2;

			v5.addVertex(x11, h1, z11);
			v5.addVertex(x12, h2, z12);

			v5.addVertex(x12, h2, z12);
			v5.addVertex(x22, h2, z22);

			v5.addVertex(x12, h2, z12);
			v5.addVertex(x12, h3, z12);

			v5.addVertex(x12, h3, z12);
			v5.addVertex(x22, h3, z22);

			v5.addVertex(x12, h3, z12);
			v5.addVertex(x13, h4, z13);

			v5.addVertex(x13, h4, z13);
			v5.addVertex(x23, h4, z23);
		}

		v5.draw();
		v5.startDrawingQuads();

		for (int i = 0; i < 360; i += n) {

			float br = 0.375F*(0.75F+0.25F*(float)Math.sin(hash*Math.PI*2+Math.toRadians(i*2+t/80D)));
			int c = ReikaColorAPI.mixColors(0xff0000, 0xffffff, br+0.625F);
			int a = 224+(int)(15*Math.sin(-hash*Math.PI*2+i+t/180D));
			v5.setColorRGBA_I(c, a);

			double a1 = Math.toRadians(i);
			double a2 = Math.toRadians(i+n);
			double x1 = Math.cos(a1);
			double x2 = Math.cos(a2);
			double z1 = Math.sin(a1);
			double z2 = Math.sin(a2);

			double x11 = r1*x1;
			double x12 = r2*x1;
			double x13 = r3*x1;
			double x21 = r1*x2;
			double x22 = r2*x2;
			double x23 = r3*x2;

			double z11 = r1*z1;
			double z12 = r2*z1;
			double z13 = r3*z1;
			double z21 = r1*z2;
			double z22 = r2*z2;
			double z23 = r3*z2;

			v5.addVertex(x11, h1, z11);
			v5.addVertex(x12, h2, z12);
			v5.addVertex(x22, h2, z22);
			v5.addVertex(x22, h2, z22);


			v5.addVertex(x12, h3, z12);
			v5.addVertex(x22, h3, z22);
			v5.addVertex(x22, h2, z22);
			v5.addVertex(x12, h2, z12);


			v5.addVertex(x12, h3, z12);
			v5.addVertex(x13, h4, z13);
			v5.addVertex(x23, h4, z23);
			v5.addVertex(x22, h3, z22);

			v5.addVertex(x11, h4, z11);
			v5.addVertex(x11, h4, z11);
			v5.addVertex(x23, h4, z23);
			v5.addVertex(x13, h4, z13);
		}

		v5.draw();
	}

}

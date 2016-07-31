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

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityGuardianStone;
import Reika.DragonAPI.Instantiable.Orbit;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class GuardianStoneRenderer extends ChromaRenderBase {

	private static final ArrayList<ParticleOrbit> orbits = new ArrayList();
	private static final Random rand = new Random();

	private static class ParticleOrbit {

		private final Orbit orbit;
		private final int color;

		private ParticleOrbit(Orbit o, int c) {
			orbit = o;
			color = c;
		}

	}

	static {
		int a = 0;
		int b = 120;
		double r = 0.4;

		addParticle(r, 0.5, 0, 0, 0, 0);
		addParticle(r, 0.6, 45, 60, 180, 90);
		addParticle(r, 0.4, 90, 40, 20, 270);
		addParticle(r, 0.6, 135, 120, 220, 30);
		addParticle(r, 0.7, 180, 330, 60, 120);
		addParticle(r, 0.4, 225, 90, 60, 240);
		addParticle(r, 0.6, 270, 260, 0, 0);
		addParticle(r, 0.5, 315, 100, 120, 100);

		addParticle(r, 0.5, 0, 40, 30, 20);
		addParticle(r, 0.6, 45, 120, 160, 40);
		addParticle(r, 0.4, 90, 90, 20, 120);
		addParticle(r, 0.6, 135, 320, 120, 30);
		addParticle(r, 0.7, 180, 170, 60, 240);
		addParticle(r, 0.4, 225, 40, 90, 60);
		addParticle(r, 0.6, 270, 110, 0, 90);
		addParticle(r, 0.5, 315, 60, 0, 30);

		addParticle(r, 0.5, 0, 80, 90, 340);
		addParticle(r, 0.6, 45, 240, 220, 320);
		addParticle(r, 0.4, 90, 180, 80, 240);
		addParticle(r, 0.6, 135, 80, 180, 330);
		addParticle(r, 0.7, 180, 20, 120, 120);
		addParticle(r, 0.4, 225, 80, 150, 300);
		addParticle(r, 0.6, 270, 220, 60, 270);
		addParticle(r, 0.5, 315, 120, 60, 330);

		addParticle(r, 0.5, 0, 80, 180, 320);
		addParticle(r, 0.6, 45, 240, 80, 280);
		addParticle(r, 0.4, 90, 180, 160, 120);
		addParticle(r, 0.6, 135, 80, 0, 300);
		addParticle(r, 0.7, 180, 20, 240, 240);
		addParticle(r, 0.4, 225, 80, 300, 240);
		addParticle(r, 0.6, 270, 220, 120, 90);
		addParticle(r, 0.5, 315, 120, 120, 300);
	}

	private static void addParticle(double r, double e, double theta, double i, double raan, double arg) {
		orbits.add(new ParticleOrbit(new Orbit(r, e, theta, i, raan, arg), ReikaColorAPI.RGBtoHex(rand.nextInt(255), 120, 255)));
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityGuardianStone te = (TileEntityGuardianStone)tile;

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)par2, (float)par4 + 1.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glDepthMask(false);

		if (tile.hasWorldObj())
			this.drawMiddle(te);
		this.drawInner(te);
		if (!tile.hasWorldObj()) {
			GL11.glTranslated(0, 0, 1);
			GL11.glRotated(90, 0, 1, 0);
			//this.drawInner(te);
			GL11.glRotated(-90, 0, 1, 0);
			GL11.glTranslated(0, 0, -1);
			this.drawOuter(te);
		}

		if (te.hasWorldObj()) {
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		else
			RenderHelper.enableGUIStandardItemLighting();

		GL11.glPopAttrib();
		if (te.hasWorldObj())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void drawInner(TileEntityGuardianStone te) {
		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.GUARDIANINNER.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		float uu = du-u;
		float vv = dv-v;

		float r = 16;
		u += uu/r;
		du -= uu/r;
		v += vv/r;
		dv -= vv/r;

		Tessellator v5 = Tessellator.instance;
		BlendMode.ADDITIVEDARK.apply();

		GL11.glPushMatrix();

		GL11.glDisable(GL11.GL_CULL_FACE);
		double s = 0.33;
		if (te.hasWorldObj()) {
			GL11.glScaled(s, s, s);
			RenderManager rm = RenderManager.instance;
			GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		}
		else {
			s = 0.5;
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glRotated(-45, 0, 1, 0);
			GL11.glRotated(-30, 1, 0, 0);
			GL11.glScaled(s, s, s);
		}
		//double ang = (System.currentTimeMillis()/20D)%360;
		//GL11.glRotated(ang, 0, 0, 1);
		v5.startDrawingQuads();
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.draw();
		GL11.glEnable(GL11.GL_CULL_FACE);
		BlendMode.DEFAULT.apply();

		GL11.glPopMatrix();
	}

	private void drawMiddle(TileEntityGuardianStone te) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glTranslated(0.5, 0.5, 0.5);
		double theta = (System.currentTimeMillis()/20D)%360;
		for (ParticleOrbit p : orbits) {
			this.renderOrbitingParticle(p, theta);
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	private void renderOrbitingParticle(ParticleOrbit p, double dtheta) {
		//r = (int)(128+128*Math.sin(Math.toRadians(theta)));
		Tessellator v5 = Tessellator.instance;
		//v5.startDrawing(GL11.GL_LINE_LOOP);
		//v5.setColorOpaque(r, g, b);
		//for (theta = 0; theta < 360; theta += 5) {
		DecimalPosition pos = p.orbit.getPosition(0, 0, 0, dtheta);
		//GL11.glPointSize(3F);
		RenderManager rm = RenderManager.instance;
		GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		double d = 0.008;
		v5.startDrawingQuads();
		v5.setColorRGBA_I(p.color, 255);
		v5.addVertex(pos.xCoord-d, pos.yCoord-d, pos.zCoord);
		v5.addVertex(pos.xCoord+d, pos.yCoord-d, pos.zCoord);
		v5.addVertex(pos.xCoord+d, pos.yCoord+d, pos.zCoord);
		v5.addVertex(pos.xCoord-d, pos.yCoord+d, pos.zCoord);

		d = 0.005;
		v5.setColorRGBA_I(ReikaColorAPI.mixColors(0xffffff, p.color, 0.25F), 255);
		v5.addVertex(pos.xCoord-d, pos.yCoord-d, pos.zCoord);
		v5.addVertex(pos.xCoord+d, pos.yCoord-d, pos.zCoord);
		v5.addVertex(pos.xCoord+d, pos.yCoord+d, pos.zCoord);
		v5.addVertex(pos.xCoord-d, pos.yCoord+d, pos.zCoord);
		//}
		v5.draw();
		GL11.glRotatef(-rm.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
	}

	private void drawOuter(TileEntityGuardianStone te) {
		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.GUARDIANOUTER.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		this.drawBox(1, 0, u, v, du, dv);
	}

	private void drawCore() {
		ReikaTextureHelper.bindTerrainTexture();
		Block b = ChromaBlocks.CRYSTAL.getBlockInstance();
		IIcon ico = b.getIcon(0, 0);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		this.drawBox(0.5F, 0.25, u, v, du, dv);
		this.drawBox(0.25F, 0.125, u, v, du, dv);
		this.drawBox(0.125F, 0, u, v, du, dv);
	}

	private void drawBox(float a, double s, float u, float v, float du, float dv) {
		Tessellator v5 = Tessellator.instance;
		GL11.glColor4f(1, 1, 1, a);
		v5.startDrawingQuads();
		v5.addVertexWithUV(s, s, s, u, v);
		v5.addVertexWithUV(1-s, s, s, du, v);
		v5.addVertexWithUV(1-s, s, 1-s, du, dv);
		v5.addVertexWithUV(s, s, 1-s, u, dv);

		v5.addVertexWithUV(s, 1-s, 1-s, u, dv);
		v5.addVertexWithUV(1-s, 1-s, 1-s, du, dv);
		v5.addVertexWithUV(1-s, 1-s, s, du, v);
		v5.addVertexWithUV(s, 1-s, s, u, v);

		v5.addVertexWithUV(s, s, s, u, v);
		v5.addVertexWithUV(s, 1-s, s, u, dv);
		v5.addVertexWithUV(1-s, 1-s, s, du, dv);
		v5.addVertexWithUV(1-s, s, s, du, v);

		v5.addVertexWithUV(1-s, s, 1-s, du, v);
		v5.addVertexWithUV(1-s, 1-s, 1-s, du, dv);
		v5.addVertexWithUV(s, 1-s, 1-s, u, dv);
		v5.addVertexWithUV(s, s, 1-s, u, v);

		v5.addVertexWithUV(s, s, 1-s, du, v);
		v5.addVertexWithUV(s, 1-s, 1-s, du, dv);
		v5.addVertexWithUV(s, 1-s, s, u, dv);
		v5.addVertexWithUV(s, s, s, u, v);

		v5.addVertexWithUV(1-s, s, s, u, v);
		v5.addVertexWithUV(1-s, 1-s, s, u, dv);
		v5.addVertexWithUV(1-s, 1-s, 1-s, du, dv);
		v5.addVertexWithUV(1-s, s, 1-s, du, v);
		v5.draw();
	}

	private void drawLines() {
		int a = 190;
		double ang = (System.currentTimeMillis()/40D)%360;

		double r = 0.25*(1+Math.sin(Math.toRadians(ang+0)));
		this.drawLineRing(r, 0.2, 0.125, 1, 255, a, 255);

		r = 0.25*(1+Math.sin(Math.toRadians(ang+60)));
		this.drawLineRing(r, 0.2, 0.25, -0.75F, a, a, 255);

		r = 0.25*(1+Math.sin(Math.toRadians(ang+120)));
		this.drawLineRing(r, 0.2, 0.5, 1.5F, 255, 255, 255);

		r = 0.25*(1+Math.sin(Math.toRadians(ang+180)));
		this.drawLineRing(r, 0.2, 0.675, -1, a, 255, a);

		r = 0.25*(1+Math.sin(Math.toRadians(ang+240)));
		this.drawLineRing(r, 0.2, 0.75, 0.75F, 255, a, a);

		r = 0.25*(1+Math.sin(Math.toRadians(ang+300)));
		this.drawLineRing(r, 0.2, 0.875, -1.5F, 255, 255, a);
	}

	private void drawLineRing(double r, double h, double y, float speed, int red, int green, int blue) {
		Tessellator v5 = Tessellator.instance;
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		double ang = (System.currentTimeMillis()/20D*speed)%360;
		v5.startDrawing(GL11.GL_LINES);
		v5.setColorOpaque(red, green, blue);
		float da = r > 0.4 ? 12 : r > 0.3 ? 16 : r > 0.25 ? 20 : r > 0.125 ? 30 : 40;
		for (int i = 0; i < 360; i += da) {
			double ang2 = ang+i;
			double x = 0.5+r*Math.cos(Math.toRadians(ang2));
			double z = 0.5+r*Math.sin(Math.toRadians(ang2));
			v5.addVertex(x, y-h/2, z);
			v5.addVertex(x, y+h/2, z);
		}
		v5.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	private void drawLine(double r, double h, double y, float speed, int red, int green, int blue) {
		Tessellator v5 = Tessellator.instance;
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		double ang = (System.currentTimeMillis()/20D*speed)%360;
		double ang2 = (System.currentTimeMillis()/800D/speed)%360;
		r *= Math.max(Math.abs(Math.sin(ang2)), 0.5);
		double x = 0.5+r*Math.cos(Math.toRadians(ang));
		double z = 0.5+r*Math.sin(Math.toRadians(ang));
		v5.startDrawing(GL11.GL_LINES);
		v5.setColorOpaque(red, green, blue);
		v5.addVertex(x, y-h/2, z);
		v5.addVertex(x, y+h/2, z);
		v5.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}

}

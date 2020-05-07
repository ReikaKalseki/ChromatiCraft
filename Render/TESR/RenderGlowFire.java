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
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityGlowFire;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderGlowFire extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityGlowFire te = (TileEntityGlowFire)tile;

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDepthMask(false);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslated(par2, par4, par6);
		GL11.glPushMatrix();
		GL11.glTranslatef(0, 1F, 1F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);

		if (MinecraftForgeClient.getRenderPass() == 1 || !te.isInWorld())
			this.drawInner(te);

		if (te.hasWorldObj())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopAttrib();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();

		if (MinecraftForgeClient.getRenderPass() == 1)
			te.particles.render(true);

		GL11.glPopMatrix();
	}

	private void drawInner(TileEntityGlowFire te) {
		ReikaTextureHelper.bindTerrainTexture();

		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);

		double s = te.isInWorld() ? 0.4375 : 0.75;
		if (te.hasWorldObj()) {
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glScaled(s, s, s);
			RenderManager rm = RenderManager.instance;
			GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		}
		else {
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glRotated(-45, 0, 1, 0);
			GL11.glRotated(-30, 1, 0, 0);
			GL11.glScaled(s, s, s);
		}

		//double ang = (System.currentTimeMillis()/20D)%360;
		//GL11.glRotated(ang, 0, 0, 1);
		GL11.glPushMatrix();
		double s2 = 1.5;
		GL11.glScaled(s2, s2, s2);

		IIcon ico = ChromaIcons.BLUEFIRE.getIcon();//ChromaIcons.GUARDIANMIDDLE.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		float uu = du-u;
		float vv = dv-v;

		float mu = ico.getInterpolatedU(8);
		float mv = ico.getInterpolatedV(8);

		//v5.startDrawingQuads();
		v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
		int c = ReikaColorAPI.mixColors(0xff7020, 0xffffff, te.isSmothered());
		v5.setColorOpaque_I(c);

		/*
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		 */

		//v5.addVertexWithUV(0, 0, 0, u, dv);

		for (double a = 0; a <= 360; a += 2.5) {
			double da = Math.toRadians(a);
			double f = a/360D;
			double x = Math.cos(da);
			double y = Math.sin(da);
			v5.addVertexWithUV(0, 0, 0, u+f*uu, dv);
			v5.addVertexWithUV(x, y, 0, u+f*uu, v);
		}

		v5.draw();

		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_CULL_FACE);
		BlendMode.DEFAULT.apply();

		GL11.glPopMatrix();
	}

}

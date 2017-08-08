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

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.AdjacencyUpgrades;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class AdjacencyUpgradeRenderer extends ChromaRenderBase {

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityAdjacencyUpgrade te = (TileEntityAdjacencyUpgrade)tile;

		if (te.isInWorld() && MinecraftForgeClient.getRenderPass() != 1)
			return;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_LIGHTING);
		//ReikaRenderHelper.disableLighting();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslated(par2, par4, par6);
		GL11.glScaled(1, -1, -1);

		if (!tile.hasWorldObj()) {
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDisable(GL11.GL_CULL_FACE);
			double a = 0.5;
			double b = -0.4;
			double c = -0.5;
			GL11.glTranslated(a, b, c);
			this.drawInner(te);
			GL11.glRotated(90, 0, 1, 0);
			this.drawInner(te);
			GL11.glRotated(-90, 0, 1, 0);
			GL11.glTranslated(-a, -b, -c);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
		else {
			//GL11.glDisable(GL11.GL_LIGHTING);
			//ReikaRenderHelper.disableEntityLighting();
		}

		this.drawMiddle(te);

		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDisable(GL11.GL_CULL_FACE);


		if (te.hasWorldObj()) {
			this.drawInner(te);
		}
		else {
			//GL11.glEnable(GL11.GL_LIGHTING);
			//RenderHelper.enableGUIStandardItemLighting();
		}
		//ReikaRenderHelper.enableEntityLighting();
		GL11.glEnable(GL11.GL_LIGHTING);

		//this.drawSparkle(te);

		GL11.glEnable(GL11.GL_CULL_FACE);
		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_BLEND);
		if (te.hasWorldObj())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void drawMiddle(TileEntityAdjacencyUpgrade te) {
		GL11.glLineWidth(4);
		GL11.glTranslated(0.5, -0.5, -0.5);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		double s = 1.5;
		if (!te.isInWorld()) {
			GL11.glScaled(s, s, s);
		}

		boolean dis = te.worldObj != null && !te.canRun(te.worldObj, te.xCoord, te.yCoord, te.zCoord);

		Tessellator v5 = Tessellator.instance;
		double time = System.currentTimeMillis()+(te.xCoord+te.yCoord*4+te.zCoord*2)*48D;
		if (dis) {
			time = time/6D;
		}
		double rsc = 0.2+te.getTier()/4D;
		double rx = (time*rsc/4)%360;
		double ry = (time*rsc/3)%360;
		double rz = (time*rsc/5)%360;

		double d = 7200-te.getTier()*975;
		double d2 = d/2;
		double d3 = d/3;

		double t1 = time%d;
		if (dis) {
			t1 = t1/12;
		}

		double b1 = t1 >= d2 ? (d-t1)/d2 : t1/d2;
		double t2 = (time+d3)%d;
		double b2 = t2 >= d2 ? (d-t2)/d2 : t2/d2;
		double t3 = (time+2*d3)%d;
		double b3 = t3 >= d2 ? (d-t3)/d2 : t3/d2;

		int ca = AdjacencyUpgrades.upgrades[te.getColor().ordinal()].color1;
		int cb = AdjacencyUpgrades.upgrades[te.getColor().ordinal()].color2;

		int c1 = ReikaColorAPI.mixColors(ca, cb, (float)b1);
		int c2 = ReikaColorAPI.mixColors(ca, cb, (float)b2);
		int c3 = ReikaColorAPI.mixColors(ca, cb, (float)b3);

		if (dis) {
			c1 = ReikaColorAPI.mixColors(0x000000, 0xffffff, (float)b1);
			c2 = ReikaColorAPI.mixColors(0x000000, 0xffffff, (float)b2);
			c3 = ReikaColorAPI.mixColors(0x000000, 0xffffff, (float)b3);
		}

		c1 = c1 | 0xff000000;
		c2 = c2 | 0xff000000;
		c3 = c3 | 0xff000000;

		GL11.glRotated(rx, 1, 0, 0);
		ReikaRenderHelper.renderVCircle(0.25, 0, 0, 0, c1, 90, 15);
		GL11.glRotated(ry, 0, 1, 0);
		ReikaRenderHelper.renderVCircle(0.28125, 0, 0, 0, c2, 0, 15);
		GL11.glRotated(rz, 0, 0, 1);
		ReikaRenderHelper.renderCircle(0.3125, 0, 0, 0, c3, 15);
		GL11.glRotated(-rz, 0, 0, 1);
		GL11.glRotated(-ry, 0, 1, 0);
		GL11.glRotated(-rx, 1, 0, 0);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glTranslated(-0.5, 0.5, 0.5);
		GL11.glLineWidth(2);

		if (!te.isInWorld()) {
			GL11.glScaled(1/s, 1/s, 1/s);
		}
	}

	private void drawSparkle(TileEntityAdjacencyUpgrade tile) {
		Tessellator v5 = Tessellator.instance;
		IIcon ico = ChromaIcons.GLOWSECTION.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		v5.startDrawingQuads();
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.draw();
	}

	private void drawInner(TileEntityAdjacencyUpgrade te) {
		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.GLOWSECTION.getIcon();
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

		double s = 0.1875;

		if (te.hasWorldObj()) {
			GL11.glTranslated(0.5, -0.5, -0.5);
			GL11.glScaled(s, s, s);
			RenderManager rm = RenderManager.instance;
			GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		}
		else {
			GL11.glTranslated(0, -0.125, 0);
			GL11.glRotated(-45, 0, 1, 0);
			GL11.glRotated(-30, 1, 0, 0);
			GL11.glScaled(s, s, s);
		}
		v5.startDrawingQuads();
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.draw();

		if (!te.hasWorldObj()) {
			GL11.glScaled(1/s, 1/s, 1/s);
			GL11.glRotated(30, 1, 0, 0);
			GL11.glRotated(45, 0, 1, 0);
			GL11.glTranslated(0, 0.125, 0);
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}

}

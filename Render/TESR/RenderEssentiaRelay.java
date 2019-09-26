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

import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Auxiliary.HoldingChecks;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.TileEntityEssentiaRelay;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderEssentiaRelay extends ChromaRenderBase {

	private long lastNetworkRenderTick;
	private float lastNetworkRenderPTick;

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityEssentiaRelay te = (TileEntityEssentiaRelay)tile;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		BlendMode.ADDITIVEDARK.apply();
		ReikaRenderHelper.disableEntityLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslated(par2, par4, par6);

		if (te.isInWorld() && !StructureRenderer.isRenderingTiles()) {
			float f = HoldingChecks.MANIPULATOR.getFade();
			if (f > 0 && (par8 != lastNetworkRenderPTick || lastNetworkRenderTick != te.worldObj.getTotalWorldTime())) {
				lastNetworkRenderPTick = par8;
				lastNetworkRenderTick = te.worldObj.getTotalWorldTime();
				this.renderNetwork(te, f, par8);
			}
		}

		GL11.glTranslatef(0, 1, 1);
		GL11.glScalef(1.0F, -1.0F, -1.0F);

		if (MinecraftForgeClient.getRenderPass() == 1 || !te.isInWorld()) {
			this.drawInner(te, par8);
		}

		GL11.glDisable(GL11.GL_BLEND);
		if (te.hasWorldObj())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderNetwork(TileEntityEssentiaRelay te, float fade, float par8) {
		Map<Coordinate, Boolean> map = te.getNetworkTiles();
		if (map.isEmpty())
			return;
		BlendMode.DEFAULT.apply();
		GL11.glColor4f(1, 1, 1, 1);
		float w = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		GL11.glLineWidth(3);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Tessellator v5 = Tessellator.instance;
		v5.setBrightness(240);
		v5.startDrawing(GL11.GL_LINES);
		double o = 0.125;
		for (Entry<Coordinate, Boolean> e : map.entrySet()) {
			Coordinate c = e.getKey();
			float f = (float)(0.5+0.5*Math.sin((te.getTicksExisted()+par8)/10D+System.identityHashCode(c)));
			int c1 = e.getValue() ? 0x0000e0 : 0x00e000;
			int c2 = e.getValue() ? 0x7f7fff : 0x7fff7f;
			int clr = ReikaColorAPI.mixColors(c1, c2, f);
			v5.setColorRGBA_I(clr, (int)(fade*255));
			v5.addVertex(c.xCoord-te.xCoord+o, c.yCoord-te.yCoord+o, c.zCoord-te.zCoord+o);
			v5.addVertex(c.xCoord-te.xCoord+o, c.yCoord-te.yCoord+1-o, c.zCoord-te.zCoord+o);
			v5.addVertex(c.xCoord-te.xCoord+1-o, c.yCoord-te.yCoord+o, c.zCoord-te.zCoord+o);
			v5.addVertex(c.xCoord-te.xCoord+1-o, c.yCoord-te.yCoord+1-o, c.zCoord-te.zCoord+o);
			v5.addVertex(c.xCoord-te.xCoord+o, c.yCoord-te.yCoord+o, c.zCoord-te.zCoord+1-o);
			v5.addVertex(c.xCoord-te.xCoord+o, c.yCoord-te.yCoord+1-o, c.zCoord-te.zCoord+1-o);
			v5.addVertex(c.xCoord-te.xCoord+1-o, c.yCoord-te.yCoord+o, c.zCoord-te.zCoord+1-o);
			v5.addVertex(c.xCoord-te.xCoord+1-o, c.yCoord-te.yCoord+1-o, c.zCoord-te.zCoord+1-o);

			v5.addVertex(c.xCoord-te.xCoord+o, c.yCoord-te.yCoord+o, c.zCoord-te.zCoord+o);
			v5.addVertex(c.xCoord-te.xCoord+1-o, c.yCoord-te.yCoord+o, c.zCoord-te.zCoord+o);
			v5.addVertex(c.xCoord-te.xCoord+o, c.yCoord-te.yCoord+1-o, c.zCoord-te.zCoord+o);
			v5.addVertex(c.xCoord-te.xCoord+1-o, c.yCoord-te.yCoord+1-o, c.zCoord-te.zCoord+o);
			v5.addVertex(c.xCoord-te.xCoord+o, c.yCoord-te.yCoord+o, c.zCoord-te.zCoord+1-o);
			v5.addVertex(c.xCoord-te.xCoord+1-o, c.yCoord-te.yCoord+o, c.zCoord-te.zCoord+1-o);
			v5.addVertex(c.xCoord-te.xCoord+o, c.yCoord-te.yCoord+1-o, c.zCoord-te.zCoord+1-o);
			v5.addVertex(c.xCoord-te.xCoord+1-o, c.yCoord-te.yCoord+1-o, c.zCoord-te.zCoord+1-o);

			v5.addVertex(c.xCoord-te.xCoord+o, c.yCoord-te.yCoord+o, c.zCoord-te.zCoord+o);
			v5.addVertex(c.xCoord-te.xCoord+o, c.yCoord-te.yCoord+o, c.zCoord-te.zCoord+1-o);
			v5.addVertex(c.xCoord-te.xCoord+1-o, c.yCoord-te.yCoord+o, c.zCoord-te.zCoord+o);
			v5.addVertex(c.xCoord-te.xCoord+1-o, c.yCoord-te.yCoord+o, c.zCoord-te.zCoord+1-o);
			v5.addVertex(c.xCoord-te.xCoord+o, c.yCoord-te.yCoord+1-o, c.zCoord-te.zCoord+o);
			v5.addVertex(c.xCoord-te.xCoord+o, c.yCoord-te.yCoord+1-o, c.zCoord-te.zCoord+1-o);
			v5.addVertex(c.xCoord-te.xCoord+1-o, c.yCoord-te.yCoord+1-o, c.zCoord-te.zCoord+o);
			v5.addVertex(c.xCoord-te.xCoord+1-o, c.yCoord-te.yCoord+1-o, c.zCoord-te.zCoord+1-o);
		}
		v5.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glLineWidth(w);
		BlendMode.ADDITIVEDARK.apply();
	}

	private void drawInner(TileEntityEssentiaRelay te, float par8) {
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
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);

		double s = te.isInWorld() ? 0.4375 : 0.33;
		if (te.hasWorldObj()) {
			GL11.glTranslated(0.5, 0.5, 0.5);
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

		double t = te.isInWorld() ? (te.getTicksExisted()+par8) : System.currentTimeMillis()/50D;

		double s2 = 0.75+0.0625*(0.25*Math.sin(t)+1*Math.sin(6*t)+1*Math.cos(t/2D)+0.75*Math.cos(2*t)+2.5*Math.sin(t/64D));
		if (!te.isInWorld())
			s2 *= 1.875;

		GL11.glPushMatrix();
		GL11.glScaled(s2, s2, s2);
		v5.startDrawingQuads();
		v5.setColorOpaque_I(0xffffff);
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.draw();
		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_CULL_FACE);
		BlendMode.DEFAULT.apply();

		GL11.glPopMatrix();
	}

}

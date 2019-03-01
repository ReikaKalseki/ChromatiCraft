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
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityWirelessSource;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderWirelessSource extends ChromaRenderBase {

	private final ChromaIcons[] icons = new ChromaIcons[]{
			ChromaIcons.BLURFLARE,
			ChromaIcons.TURBO,
			ChromaIcons.CONCENTRIC2,
	};

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityWirelessSource te = (TileEntityWirelessSource)tile;

		if (!tile.hasWorldObj() || (tile.hasWorldObj() && (MinecraftForgeClient.getRenderPass() == 1 || StructureRenderer.isRenderingTiles()))) {
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LIGHTING);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDepthMask(false);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef((float)par2, (float)par4 + 1.0F, (float)par6 + 1.0F);
			GL11.glScalef(1.0F, -1.0F, -1.0F);

			this.drawInner(te, par8);

			double dx = tile.xCoord;
			double dy = tile.yCoord;
			double dz = tile.zCoord;
			if (StructureRenderer.isRenderingTiles())
				te.tendril.update();
			if (te.hasWorldObj() && te.isBeacon())
				te.tendril.render(dx, dy, dz, te.getRenderColor(par8), par8);

			if (te.hasWorldObj())
				GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
			GL11.glPopAttrib();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	private void drawInner(TileEntityWirelessSource te, float par8) {
		ReikaTextureHelper.bindTerrainTexture();

		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);

		double s = StructureRenderer.isRenderingTiles() ? 0.625 : te.isInWorld() ? 0.4375 : 0.33;
		if (te.hasWorldObj()) {
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glScaled(s, s, s);
			if (StructureRenderer.isRenderingTiles()) {
				GL11.glRotated(StructureRenderer.getRenderRY(), 0, 1, 0);
				GL11.glRotated(-StructureRenderer.getRenderRX(), 1, 0, 0);
			}
			else {
				RenderManager rm = RenderManager.instance;
				GL11.glRotatef(rm.playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
			}
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
		GL11.glPushMatrix();
		double s2 = te.isInWorld() ? 1 : 1.5;
		GL11.glScaled(s2, s2, s2);
		int c = te.getRenderColor(par8);

		for (int i = 0; i < icons.length; i++) {
			IIcon ico = icons[i].getIcon();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();

			v5.startDrawingQuads();
			v5.setColorOpaque_I(c);
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();
		}
		if (StructureRenderer.isRenderingTiles()) {
			this.drawFilaments(te, c);
		}
		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_CULL_FACE);
		BlendMode.DEFAULT.apply();

		GL11.glPopMatrix();
	}

	private void drawFilaments(TileEntityWirelessSource te, int c) {
		//beziers with start point in center of tile, one at random point fixed outer radius, 2 more randomly placed along axis between with some deg offset

	}

}

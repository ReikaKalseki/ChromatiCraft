/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockLaserEffector.TargetTile;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Instantiable.Effects.TruncatedCube;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderLaserTarget extends ChromaRenderBase {

	private static final TruncatedCube cube = new TruncatedCube(0.045, 0.125);

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();

		TargetTile te = (TargetTile)tile;
		GL11.glTranslated(par2, par4, par6);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);
		ReikaRenderHelper.disableEntityLighting();
		BlendMode.ADDITIVEDARK.apply();

		double t = System.currentTimeMillis();
		if (te.isTriggered())
			t *= 2;
		double s = (te.isTriggered() ? 0.625 : 0.5)+0.0625*Math.sin(t/400D)+0.125*Math.sin(t/2000D);
		double dy = (te.isTriggered() ? 0.1875 : 0.125)+0.0625*Math.cos(t/1200D)+0.125*Math.cos(t/4000D);

		int c = (te.getRenderColor() & 0xffffff) | (192 << 24);
		int c2 = ReikaColorAPI.mixColors(c, 0xffffff | (192 << 24), 0.75F);
		if ((te.getRenderColor() & 0xffffff) == 0xffffff)
			c = (c&0xff000000) | 0xbfbfbf;

		GL11.glPushMatrix();
		GL11.glTranslated(0.5, 0.5+dy, 0.5);
		GL11.glRotated((t/20D)%360, 1, 0, 0);
		GL11.glRotated((t/20D+te.hashCode())%360, 0, 1, 0);
		GL11.glRotated((t/20D-te.hashCode())%360, 0, 0, 1);
		double s2 = Math.sqrt(s*1.75);
		GL11.glScaled(s2, s2, s2);
		float pdist = (float)Minecraft.getMinecraft().thePlayer.getDistance(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5);
		cube.render(0, 0, 0, c, c2, true, pdist);
		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		c = te.getRenderColor();

		if (te.isTriggered()) {
			GL11.glTranslated(0.5, 0.5+dy, 0.5);
			GL11.glScaled(s, s, s);
			RenderManager rm = RenderManager.instance;
			GL11.glRotatef(180-rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-rm.playerViewX, 1.0F, 0.0F, 0.0F);

			ReikaTextureHelper.bindTerrainTexture();
			IIcon ico = ChromaIcons.SPINFLARE.getIcon();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();

			Tessellator v5 = Tessellator.instance;
			v5.startDrawingQuads();
			v5.setColorOpaque_I(c);
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();

		}

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

}

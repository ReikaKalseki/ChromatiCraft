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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.Worldgen.BlockWarpNode.TileEntityWarpNode;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderWarpNode extends ChromaRenderBase {

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityWarpNode te = (TileEntityWarpNode)tile;

		if (!tile.hasWorldObj() || MinecraftForgeClient.getRenderPass() == 1 || StructureRenderer.isRenderingTiles()) {
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/warpnode-small.png");
			int idx = (int)(System.currentTimeMillis()/20%64);
			double u = idx%8/8D;
			double v = idx/8/8D;
			double du = u+1/8D;
			double dv = v+1/8D;
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(false);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);

			Tessellator v5 = Tessellator.instance;

			GL11.glTranslated(0.5, 0.5, 0.5);

			GL11.glPushMatrix();
			double t = (System.currentTimeMillis()/2000D+te.hashCode())%360;
			double d = Math.max(0, Minecraft.getMinecraft().thePlayer.getDistance(te.xCoord+0.5, te.yCoord+0.5+1.62, te.zCoord+0.5)-8);
			boolean flag = false;
			float f = flag ? 1 : 0.5F+0.5F*(float)(1-d/8D);
			if (!flag) {
				f += 0.125F*(float)Math.sin(1.7*t);
				/*
			if (d > 16) {
				d -= 16;
				f += Math.max(0, d*4*Math.sin(2*t)-1.95);
			}
				 */
				f = Math.max(f, 4*(float)Math.sin(1*t)-3F);
			}
			if (ChromaItems.TOOL.matchWith(Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem())) {
				f += 0.75;
			}
			f = MathHelper.clamp_float(f, 0, 1);
			double s = 0.125+0.5*f+0.125*Math.sin(t);
			if (flag)
				s = 1.5;
			s *= 4;
			GL11.glScaled(s, s, s);
			if (StructureRenderer.isRenderingTiles()) {
				GL11.glRotated(-StructureRenderer.getRenderRY(), 0, 1, 0);
				GL11.glRotated(-StructureRenderer.getRenderRX(), 1, 0, 0);
			}
			else {
				RenderManager rm = RenderManager.instance;
				GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
			}

			int c = ReikaColorAPI.mixColors(0xffffff, 0x000000, f);
			v5.startDrawingQuads();
			v5.setColorOpaque_I(c);
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();

			GL11.glPopMatrix();

			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

}

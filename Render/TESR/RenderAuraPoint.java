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
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Base.RenderLocusPoint;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityLocusPoint;
import Reika.ChromatiCraft.GUI.Book.GuiMachineDescription;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderAuraPoint extends RenderLocusPoint {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	protected void doOtherRendering(TileEntityLocusPoint tile, float par8) {
		if (!tile.isInWorld() || MinecraftForgeClient.getRenderPass() == 1) {
			TileEntityAuraPoint te = (TileEntityAuraPoint)tile;
			double d = 1.25+0.25*(Math.sin(System.currentTimeMillis()/1000D));
			GL11.glPushMatrix();
			//GL11.glTranslated(0.5, 0.5, 0.5);
			//GL11.glScaled(d, d, d);
			//GL11.glTranslated(-0.5, -0.5, -0.5);
			double dx = tile.xCoord+0.5;
			double dy = tile.yCoord+0.5;
			double dz = tile.zCoord+0.5;
			int color = 0xff000000 | ReikaColorAPI.getModifiedSat(tile.getRenderColor(), 0.875F);
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDepthMask(false);

			if (te.doPvP() && te.isInWorld()) {
				ReikaTextureHelper.bindTerrainTexture();
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				Tessellator v5 = Tessellator.instance;
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				BlendMode.ADDITIVEDARK.apply();
				GL11.glPushMatrix();
				GL11.glTranslated(0.5, 0.5, 0.5);
				RenderManager rm = RenderManager.instance;
				GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);

				double t = Math.sin((te.getTicksExisted()+par8)/32D);

				v5.startDrawingQuads();
				v5.setBrightness(240);
				IIcon ico = ChromaIcons.STARFLARE.getIcon();
				double s = 3.5+1*t;
				float u = ico.getMinU();
				float v = ico.getMinV();
				float du = ico.getMaxU();
				float dv = ico.getMaxV();
				v5.setColorOpaque_I(color);
				v5.addVertexWithUV(-s, -s, 0, u, v);
				v5.addVertexWithUV(s, -s, 0, du, v);
				v5.addVertexWithUV(s, s, 0, du, dv);
				v5.addVertexWithUV(-s, s, 0, u, dv);
				v5.draw();

				GL11.glPopAttrib();
				GL11.glPopMatrix();
			}

			float w = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
			if (!tile.isInWorld()) {
				dx = dy = dz = 0;
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				float f = 0.5F;
				GL11.glColor4f(f, f, f, f);
				int hue = (int)((System.currentTimeMillis()/100)%360);
				//color = ((int)(f*255) << 24) | ReikaColorAPI.getModifiedHue(0xff9090, hue);
				GL11.glLineWidth(1.5F);
			}
			if (GuiMachineDescription.runningRender) {
				GL11.glLineWidth(2.5F);
				GL11.glTranslated(0, 0.1875, 0);
			}
			te.knot.render(dx, dy, dz, color, tile.isInWorld());
			GL11.glLineWidth(w);
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
	}

}

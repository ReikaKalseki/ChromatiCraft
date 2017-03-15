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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Render.InWorldScriptRenderer;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderStructControl extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityStructControl te = (TileEntityStructControl)tile;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		if (!te.isInWorld() || (te.isInWorld() && te.isVisible() && MinecraftForgeClient.getRenderPass() == 1)) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			//GL11.glDisable(GL11.GL_ALPHA_TEST);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);

			Tessellator v5 = Tessellator.instance;

			this.renderFlare(te, v5);

			if (te.isInWorld() && Minecraft.getMinecraft().thePlayer.getDistanceSq(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5) < 576) {
				this.renderScript(te, par8, v5);
			}

			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
		GL11.glPopAttrib();
	}

	private void renderScript(TileEntityStructControl te, float par8, Tessellator v5) {
		double sc = 0.03125/2;
		GL11.glPushMatrix();
		if (te.getStructureType() != null) {
			switch(te.getStructureType()) {
				case BURROW:
					InWorldScriptRenderer.renderBurrowScript(te, par8, v5, sc);
					break;
				case CAVERN:
					InWorldScriptRenderer.renderCavernScript(te, par8, v5, sc);
					break;
				case DESERT:
					InWorldScriptRenderer.renderDesertScript(te, par8, v5, sc);
					break;
				case OCEAN:
					InWorldScriptRenderer.renderOceanScript(te, par8, v5, sc);
					break;
				default:
					break;
			}
		}
		GL11.glPopMatrix();
	}

	private void renderFlare(TileEntityStructControl te, Tessellator v5) {
		GL11.glPushMatrix();
		GL11.glTranslated(0.5, 0.5, 0.5);
		double s = 0.5;
		GL11.glScaled(s, s, s);
		RenderManager rm = RenderManager.instance;
		if (te.isInWorld()) {
			GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		}
		else {
			GL11.glRotatef(45, 0, 1, 0);
			GL11.glRotatef(-45, 1, 0, 0);
		}

		int alpha = 255;//te.getEnergy()*255/te.MAX_ENERGY;
		//ReikaJavaLibrary.pConsole(te.getEnergy());

		int color = te.getColor().getColor();

		IIcon ico = ChromaIcons.SPINFLARE.getIcon();
		ReikaTextureHelper.bindTerrainTexture();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		v5.startDrawingQuads();
		v5.setColorRGBA_I(color, alpha);
		v5.addVertexWithUV(-1, -1, 0, u, v);
		v5.addVertexWithUV(1, -1, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(-1, 1, 0, u, dv);
		v5.draw();
		GL11.glPopMatrix();
	}

}

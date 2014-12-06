/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import Reika.ChromatiCraft.Base.CrystalTransmitterRender;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCreativeSource;

public class RenderCreativePylon extends CrystalTransmitterRender {

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		super.renderTileEntityAt(tile, par2, par4, par6, par8);
		TileEntityCreativeSource te = (TileEntityCreativeSource)tile;

		if (tile.hasWorldObj() && MinecraftForgeClient.getRenderPass() == 1) {
			/*
			IIcon ico = ChromaIcons.ROUNDFLARE.getIcon();
			ReikaTextureHelper.bindTerrainTexture();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			GL11.glDisable(GL11.GL_LIGHTING);
			//GL11.glDisable(GL11.GL_ALPHA_TEST);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);

			Tessellator v5 = Tessellator.instance;
			GL11.glTranslated(0.5, 0.5, 0.5);
			double t = (te.randomOffset+System.currentTimeMillis()/2000D)%360;
			double s = 2.5+0.5*Math.sin(t);
			if (!te.getTargets().isEmpty()) {
				s += 1;
			}
			if (!te.canConduct()) {
				s = 0.75;
			}
			GL11.glScaled(s, s, s);
			RenderManager rm = RenderManager.instance;
			GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);

			int alpha = 255;//te.getEnergy()*255/te.MAX_ENERGY;
			//ReikaJavaLibrary.pConsole(te.getEnergy());

			int color = te.getRenderColor();

			v5.startDrawingQuads();
			v5.setColorRGBA_I(color, alpha);
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();

			GL11.glPopMatrix();
			BlendMode.DEFAULT.apply();
			//GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			ReikaRenderHelper.enableEntityLighting();
			GL11.glEnable(GL11.GL_LIGHTING);
			 */
		}
	}

}

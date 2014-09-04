/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.CrystalTransmitterRender;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalPylon;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderCrystalPylon extends CrystalTransmitterRender {

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		super.renderTileEntityAt(tile, par2, par4, par6, par8);
		TileEntityCrystalPylon te = (TileEntityCrystalPylon)tile;

		if (tile.hasWorldObj() && MinecraftForgeClient.getRenderPass() == 1) {
			IIcon ico = ChromaIcons.ROUNDFLARE.getIcon();
			ReikaTextureHelper.bindTerrainTexture();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			GL11.glDisable(GL11.GL_LIGHTING);
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

			CrystalElement c = te.getColor();

			v5.startDrawingQuads();
			v5.setColorOpaque(c.getRed(), c.getGreen(), c.getBlue());
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();

			GL11.glPopMatrix();
			BlendMode.DEFAULT.apply();
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			ReikaRenderHelper.enableEntityLighting();
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		else if (!tile.hasWorldObj()) {
			IIcon ico = ChromaIcons.ROUNDFLARE.getIcon();
			ReikaTextureHelper.bindTerrainTexture();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			GL11.glDisable(GL11.GL_LIGHTING);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glPushMatrix();
			GL11.glRotated(45, 0, 1, 0);
			GL11.glRotated(-45, 1, 0, 0);
			Tessellator v5 = Tessellator.instance;
			CrystalElement c = te.getColor();
			v5.startDrawingQuads();
			v5.setColorOpaque(c.getRed(), c.getGreen(), c.getBlue());
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();

			GL11.glPopMatrix();
			BlendMode.DEFAULT.apply();
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			RenderHelper.enableStandardItemLighting();
			GL11.glEnable(GL11.GL_LIGHTING);
		}
	}

}

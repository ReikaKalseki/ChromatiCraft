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

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelTurret;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityLumenTurret;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderLumenTurret extends ChromaRenderBase {

	private final ModelTurret model = new ModelTurret();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "turret.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityLumenTurret te = (TileEntityLumenTurret)tile;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

		GL11.glTranslated(par2, par4, par6);

		if (!te.isInWorld()) {
			double d = 1.75;
			GL11.glScaled(d, d, d);
			GL11.glTranslated(0, 0.1875, 0);
		}

		switch(MinecraftForgeClient.getRenderPass()) {
			case 0:
			case -1:
				this.renderModel(te, model);
				break;
			case 1:
				this.renderSparkle(te, par8);
				break;
		}


		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private void renderSparkle(TileEntityLumenTurret te, float par8) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		BlendMode.ADDITIVEDARK.apply();
		double d = 0.5;
		GL11.glTranslated(d, d+0.125, d);

		ReikaTextureHelper.bindTerrainTexture();

		RenderManager rm = RenderManager.instance;
		GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);

		Tessellator v5 = Tessellator.instance;

		ChromaIcons[] icons = {
				ChromaIcons.GLOWSECTION,
				ChromaIcons.CENTER
		};

		for (int i = 0; i < icons.length; i++) {
			IIcon ico = icons[i].getIcon();

			double s = 0.125-0.03125*i;

			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();

			v5.startDrawingQuads();
			v5.setBrightness(240);
			v5.addVertexWithUV(-s, s, 0, u, dv);
			v5.addVertexWithUV(s, s, 0, du, dv);
			v5.addVertexWithUV(s, -s, 0, du, v);
			v5.addVertexWithUV(-s, -s, 0, u, v);
			v5.draw();
		}
	}

}

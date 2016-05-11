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
import Reika.ChromatiCraft.Models.ModelLampControl;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityLampController;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderLampControl extends ChromaRenderBase {

	private final ModelLampControl model = new ModelLampControl();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "lampcontrol.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityLampController te = (TileEntityLampController)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		this.renderModel(te, model);
		if (te.isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			this.renderInterface(te, par8);
			this.renderGlow(te, par8);
		}
		GL11.glPopMatrix();
	}

	private void renderGlow(TileEntityLampController te, float par8) {
		Tessellator v5 = Tessellator.instance;
		ReikaRenderHelper.disableLighting();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		v5.startDrawingQuads();
		v5.setBrightness(240);
		double tick = (te.getTicksExisted()+par8)*8;
		double tick2 = (te.getTicksExisted()+par8)*4;
		double ang = 0.5*(1+Math.sin(Math.toRadians(tick%360)));
		double ang2 = 0.5*(1+Math.sin(Math.toRadians(tick2%360)));
		int color = ReikaColorAPI.RGBtoHex(255, (int)(ang2*255), 0);
		v5.setColorRGBA_I(color, 127+(int)(ang*64));
		double s = 0.0625;
		double o = 0.001;

		v5.addVertex(0.5-s-o, 0.875-o, 0.5+s+o);
		v5.addVertex(0.5+s+o, 0.875-o, 0.5+s+o);
		v5.addVertex(0.5+s+o, 1+o, 0.5+s+o);
		v5.addVertex(0.5-s-o, 1+o, 0.5+s+o);

		v5.addVertex(0.5-s-o, 1+o, 0.5-s-o);
		v5.addVertex(0.5+s+o, 1+o, 0.5-s-o);
		v5.addVertex(0.5+s+o, 0.875-o, 0.5-s-o);
		v5.addVertex(0.5-s-o, 0.875-o, 0.5-s-o);

		v5.addVertex(0.5+s+o, 1+o, 0.5-s-o);
		v5.addVertex(0.5+s+o, 1+o, 0.5+s+o);
		v5.addVertex(0.5+s+o, 0.875-o, 0.5+s+o);
		v5.addVertex(0.5+s+o, 0.875-o, 0.5-s-o);

		v5.addVertex(0.5-s-o, 0.875-o, 0.5-s-o);
		v5.addVertex(0.5-s-o, 0.875-o, 0.5+s+o);
		v5.addVertex(0.5-s-o, 1+o, 0.5+s+o);
		v5.addVertex(0.5-s-o, 1+o, 0.5-s-o);

		v5.addVertex(0.5-s-o, 1+o, 0.5+s+o);
		v5.addVertex(0.5+s+o, 1+o, 0.5+s+o);
		v5.addVertex(0.5+s+o, 1+o, 0.5-s-o);
		v5.addVertex(0.5-s-o, 1+o, 0.5-s-o);

		v5.draw();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();

		BlendMode.ADDITIVEDARK.apply();
		RenderManager rm = RenderManager.instance;
		double s2 = 0.25;
		double r = -0.125;
		GL11.glTranslated(0.5, 0.9375, 0.5);
		GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);

		ReikaTextureHelper.bindTerrainTexture();
		v5.startDrawingQuads();
		v5.setBrightness(240);
		v5.setColorRGBA_I(color, 127+(int)(ang*64));
		IIcon ico = ChromaIcons.BIGFLARE.getIcon();
		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();
		v5.addVertexWithUV(-s2, s2, r, u, dv);
		v5.addVertexWithUV(s2, s2, r, du, dv);
		v5.addVertexWithUV(s2, -s2, r, du, v);
		v5.addVertexWithUV(-s2, -s2, r, u, v);

		v5.draw();

		GL11.glPopMatrix();
		BlendMode.DEFAULT.apply();

		GL11.glDisable(GL11.GL_BLEND);
		ReikaRenderHelper.enableLighting();
	}

	private void renderInterface(TileEntityLampController te, float par8) {
		Tessellator v5 = Tessellator.instance;
		ReikaRenderHelper.disableLighting();
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		double s = 0.375;
		double o = 0.0625;
		v5.startDrawingQuads();
		v5.setBrightness(240);
		double tick = (te.getTicksExisted()+par8)*2;
		double ang = 0.5*(1+Math.sin(Math.toRadians(tick%360)));
		int color = ReikaColorAPI.RGBtoHex(0, (int)(ang*255), 255);
		v5.setColorRGBA_I(color, 170);
		IIcon ico = ChromaIcons.RINGS.getIcon();
		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();
		v5.addVertexWithUV(0.5-s, 0.5+s, o, u, dv);
		v5.addVertexWithUV(0.5+s, 0.5+s, o, du, dv);
		v5.addVertexWithUV(0.5+s, 0.5-s, o, du, v);
		v5.addVertexWithUV(0.5-s, 0.5-s, o, u, v);

		v5.addVertexWithUV(0.5-s, 0.5-s, 1-o, u, v);
		v5.addVertexWithUV(0.5+s, 0.5-s, 1-o, du, v);
		v5.addVertexWithUV(0.5+s, 0.5+s, 1-o, du, dv);
		v5.addVertexWithUV(0.5-s, 0.5+s, 1-o, u, dv);

		v5.addVertexWithUV(o, 0.5-s, 0.5-s, u, v);
		v5.addVertexWithUV(o, 0.5-s, 0.5+s, du, v);
		v5.addVertexWithUV(o, 0.5+s, 0.5+s, du, dv);
		v5.addVertexWithUV(o, 0.5+s, 0.5-s, u, dv);

		v5.addVertexWithUV(1-o, 0.5+s, 0.5-s, u, dv);
		v5.addVertexWithUV(1-o, 0.5+s, 0.5+s, du, dv);
		v5.addVertexWithUV(1-o, 0.5-s, 0.5+s, du, v);
		v5.addVertexWithUV(1-o, 0.5-s, 0.5-s, u, v);

		v5.draw();

		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_BLEND);
		ReikaRenderHelper.enableLighting();
	}

}

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

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.CrystalTransmitterRender;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalRepeater;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderCrystalRepeater extends CrystalTransmitterRender {

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		super.renderTileEntityAt(tile, par2, par4, par6, par8);
		TileEntityCrystalRepeater te = (TileEntityCrystalRepeater)tile;

		ChromaTiles c = te.getTile();
		if (tile.hasWorldObj() && MinecraftForgeClient.getRenderPass() == 1 && te.canConduct()) {
			//TileEntityCrystalRepeater te = (TileEntityCrystalRepeater)tile;
			IIcon ico = ChromaIcons.SPARKLE.getIcon();
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
			double s = 0.75;
			GL11.glScaled(s, s, s);
			RenderManager rm = RenderManager.instance;
			GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);

			v5.startDrawingQuads();
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();

			if (te.canConduct() && te.isTurbocharged()) {
				this.renderHalo(te);
			}

			GL11.glPopMatrix();
			BlendMode.DEFAULT.apply();
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			ReikaRenderHelper.enableEntityLighting();
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		else if (!tile.hasWorldObj()) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LIGHTING);
			if (par8 < 0)
				ReikaRenderHelper.disableEntityLighting();
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glPushMatrix();
			RenderBlocks.getInstance().renderBlockAsItem(c.getBlock(), c.getBlockMetadata(), 1);
			GL11.glPopMatrix();

			IIcon ico = ChromaIcons.SPARKLE.getIcon();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			GL11.glDisable(GL11.GL_CULL_FACE);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glPushMatrix();
			GL11.glRotated(45, 0, 1, 0);
			GL11.glRotated(-45, 1, 0, 0);
			double s = 0.8;
			GL11.glScaled(s, s, s);
			Tessellator v5 = Tessellator.instance;
			v5.startDrawingQuads();
			v5.addVertexWithUV(-1, -1, 0, u, v);
			v5.addVertexWithUV(1, -1, 0, du, v);
			v5.addVertexWithUV(1, 1, 0, du, dv);
			v5.addVertexWithUV(-1, 1, 0, u, dv);
			v5.draw();

			if (te.isTurbocharged()) {
				GL11.glPushMatrix();
				this.renderHalo(te);
				GL11.glPopMatrix();
			}

			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_CULL_FACE);
			BlendMode.DEFAULT.apply();

			GL11.glDisable(GL11.GL_BLEND);
			if (par8 < 0)
				ReikaRenderHelper.enableEntityLighting();
			RenderHelper.enableStandardItemLighting();
			//GL11.glEnable(GL11.GL_LIGHTING);
		}
	}

	private void renderHalo(TileEntityCrystalRepeater te) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		int c = te.worldObj != null ? this.getHaloRenderColor(te) : 0xffffff;
		if (te.worldObj == null)
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		Tessellator v5 = Tessellator.instance;

		int step = 15;
		double d = (System.currentTimeMillis()/50D)%360;
		int a = te.worldObj != null ? 90 : 30;
		int n = 0;
		for (int i = 0; i < a; i += step) {
			float u = 0;//ico.getMinU();
			float v = 0;//ico.getMinV();
			float du = 1;//ico.getMaxU();
			float dv = 1;//ico.getMaxV();

			double z = -i/(double)step*0.01;
			double z2 = z-0.005;

			GL11.glRotated(i+d, 0, 0, 1);

			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/Turbo/sections.png");
			double s = te.worldObj != null ? 1.5+0.5*Math.sin(Math.toRadians(4*d+i*2)) : 1.75;
			v5.startDrawingQuads();
			v5.setColorOpaque_I(c);
			v5.addVertexWithUV(-s, -s, z, u, v);
			v5.addVertexWithUV(s, -s, z, du, v);
			v5.addVertexWithUV(s, s, z, du, dv);
			v5.addVertexWithUV(-s, s, z, u, dv);
			v5.draw();

			if (te.worldObj != null) {
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/Turbo/radiate.png");
				s = 3;//2+1*Math.sin(Math.toRadians(4*d+i*2));
				u = (te.getTicksExisted()+n*2)%18/18F;
				du = u+1/18F;
				v5.startDrawingQuads();
				v5.setColorOpaque_I(c);
				v5.addVertexWithUV(-s, -s, z2, u, v);
				v5.addVertexWithUV(s, -s, z2, du, v);
				v5.addVertexWithUV(s, s, z2, du, dv);
				v5.addVertexWithUV(-s, s, z2, u, dv);
				v5.draw();
			}
			n++;
		}
		GL11.glPopAttrib();
	}

	protected int getHaloRenderColor(TileEntityCrystalRepeater te) {
		return te.getActiveColor().getColor();
	}

}

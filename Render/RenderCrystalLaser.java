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

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelCrystalLaser;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalLaser;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

import java.awt.Color;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

public class RenderCrystalLaser extends ChromaRenderBase {

	private final ModelCrystalLaser model = new ModelCrystalLaser();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "lasertex.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityCrystalLaser te = (TileEntityCrystalLaser)tile;
		if (te.hasWorldObj()) {
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);
			int rot = 0;
			int rotx = 0;
			switch(tile.getBlockMetadata()) {
			case 0:
				rot = 180;
				break;
			case 1:
				break;
			case 2:
				rot = 90;
				break;
			case 3:
				rot = 270;
				break;
			case 4:
				rotx = 90;
				break;
			case 5:
				rotx = 270;
				break;
			}
			GL11.glTranslated(0.5, 0, 0.5);
			GL11.glRotated(rot, 0, 1, 0);
			GL11.glRotated(rotx, 0, 0, 1);

			if (te.isActive() && MinecraftForgeClient.getRenderPass() == 1) {
				GL11.glPushMatrix();
				float ry = 0;
				if (te.getFacing().offsetY == 0) {
					float rx = RenderManager.instance.playerViewX;
					boolean lx = RenderManager.instance.viewerPosX < te.xCoord+0.5;
					boolean lz = RenderManager.instance.viewerPosZ < te.zCoord+0.5;
					boolean flip = te.getFacing().offsetX == 0 ? lx : lz;
					if (te.getFacing().offsetZ > 0 || te.getFacing().offsetX < 0)
						flip = !flip;
					ry = flip ? rx : -rx;
				}
				else {
					ry = -RenderManager.instance.playerViewY*te.getFacing().offsetY;
					GL11.glTranslated(0.5*te.getFacing().offsetY, -0.5, 0);
				}

				GL11.glPushMatrix();
				GL11.glTranslated(0, 0.5, 0);
				GL11.glRotated(ry, 1, 0, 0);
				GL11.glTranslated(0, -0.5, 0);
				this.renderBeam(te);
				GL11.glPopMatrix();
				GL11.glPopMatrix();
			}

			GL11.glTranslated(-0.5, 0, -0.5);
			this.renderModel(te, model);

			GL11.glPopMatrix();
		}
	}

	private void renderBeam(TileEntityCrystalLaser te) {
		Tessellator v5 = Tessellator.instance;
		ReikaRenderHelper.disableLighting();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.LASER.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		IIcon ico2 = ChromaIcons.LASEREND.getIcon();
		float u2 = ico2.getMinU();
		float v2 = ico2.getMinV();
		float du2 = ico2.getMaxU();
		float dv2 = ico2.getMaxV();

		int r = te.getRange();
		v5.startDrawingQuads();
		Color c = te.getColor().color.getJavaColor().brighter();
		v5.setColorOpaque(c.getRed(), c.getGreen(), c.getBlue());
		for (int i = 0; i < r-1; i++) {
			v5.addVertexWithUV(i, 0, 0, u, v);
			v5.addVertexWithUV(i+1, 0, 0, du, v);
			v5.addVertexWithUV(i+1, 1, 0, du, dv);
			v5.addVertexWithUV(i, 1, 0, u, dv);
		}

		v5.addVertexWithUV(r-1, 0, 0, u2, v2);
		v5.addVertexWithUV(r, 0, 0, du2, v2);
		v5.addVertexWithUV(r, 1, 0, du2, dv2);
		v5.addVertexWithUV(r-1, 1, 0, u2, dv2);

		v5.draw();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		v5.startDrawing(GL11.GL_LINES);
		v5.setColorRGBA(255, 255, 255, 92);
		v5.addVertex(0, 0.5, 0);
		v5.addVertex(r-0.25, 0.5, 0);
		v5.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		ReikaRenderHelper.enableLighting();
	}

}

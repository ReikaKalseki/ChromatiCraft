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

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.BlockChromaPortal.TileEntityCrystalPortal;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderCrystalPortal extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityCrystalPortal te = (TileEntityCrystalPortal)tile;
		int p = te.getPortalPosition();
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glTranslated(par2, par4, par6);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glEnable(GL11.GL_BLEND);
		Tessellator v5 = Tessellator.instance;
		if (MinecraftForgeClient.getRenderPass() == 1) {
			if (p == 5) {
				ReikaTextureHelper.bindTerrainTexture();
				IIcon ico = ChromaIcons.RIFT.getIcon();
				double u = ico.getMinU();
				double v = ico.getMinV();
				double du = ico.getMaxU();
				double dv = ico.getMaxV();
				v5.startDrawingQuads();
				v5.setColorOpaque_I(ReikaColorAPI.getModifiedHue(0x0000ff, (int)(240+40*Math.sin((te.getTicks()+par8)/8F))));
				v5.addVertexWithUV(-1, 1, 2, u, dv);
				v5.addVertexWithUV(2, 1, 2, du, dv);
				v5.addVertexWithUV(2, 1, -1, du, v);
				v5.addVertexWithUV(-1, 1, -1, u, v);
				v5.draw();

				if (te.isComplete()) {
					BlendMode.ADDITIVEDARK.apply();


					ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/beam2.png");
					u = 2*Math.sin(System.currentTimeMillis()/3200D);
					v = Math.cos(90+System.currentTimeMillis()/1600D);
					du = u+1;
					dv = v+1;
					v5.startDrawingQuads();
					v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(CrystalElement.getBlendedColor(te.getTicks(), 20), 0.0625F), 255);
					v5.addVertexWithUV(-1, 1, 2, u, dv);
					v5.addVertexWithUV(2, 1, 2, du, dv);
					v5.addVertexWithUV(2, 1, -1, du, v);
					v5.addVertexWithUV(-1, 1, -1, u, v);
					v5.draw();

					boolean half = te.getCharge() >= te.MINCHARGE/2;

					v5.startDrawingQuads();
					ReikaTextureHelper.bindTerrainTexture();
					ico = ChromaIcons.RINGS.getIcon();
					u = ico.getMinU();
					v = ico.getMinV();
					du = ico.getMaxU();
					dv = ico.getMaxV();
					double dh = 0.25;
					double n = half ? 2D : 2D*te.getCharge()*2/te.MINCHARGE;
					for (double i = 0; i <= n; i += dh) {
						double s = 1.5-i/2;
						double a = 255-64*i;
						v5.setColorRGBA_I(ReikaColorAPI.GStoHex((int)(a*(0.75+0.25*Math.sin((te.getTicks()+par8+i*96)/4F)))), 255);
						v5.addVertexWithUV(0.5-s, i+1, 0.5+s, u, dv);
						v5.addVertexWithUV(0.5+s, i+1, 0.5+s, du, dv);
						v5.addVertexWithUV(0.5+s, i+1, 0.5-s, du, v);
						v5.addVertexWithUV(0.5-s, i+1, 0.5-s, u, v);
					}
					v5.draw();

					if (half) {
						GL11.glPushMatrix();
						GL11.glTranslated(0.5, 0, 0.5);
						for (double a = 0; a < 180; a += 60) {
							GL11.glRotated(a-RenderManager.instance.playerViewY, 0, 1, 0);
							v5.startDrawingQuads();
							ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/arches2.png");
							for (double ux = 0; ux <= 0.5; ux += 0.5) {
								//v5.setColorRGBA_I(CrystalElement.getBlendedColor(te.getTicks()+(int)(8*ux), 20), 255);
								u = ux+((System.currentTimeMillis()/50)%32)/32D;
								v = 0;
								du = u+1/32D;
								dv = v+1;
								double h = 9;
								//v5.addVertexWithUV(0, 2, -0.5, u, dv);
								//v5.addVertexWithUV(1, 2, -0.5, du, dv);
								//v5.addVertexWithUV(1, h, -0.5, du, v);
								//v5.addVertexWithUV(0, h, -0.5, u, v);

								v5.addVertexWithUV(-0.5, 2, 0, u, dv);
								v5.addVertexWithUV(0.5, 2, 0, du, dv);
								v5.addVertexWithUV(0.5, h, 0, du, v);
								v5.addVertexWithUV(-0.5, h, 0, u, v);
							}
							v5.draw();
						}
						GL11.glPopMatrix();
					}
				}
			}
		}
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

}

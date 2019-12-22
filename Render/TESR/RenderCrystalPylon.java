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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Auxiliary.Potions.PotionVoidGaze.VoidGazeLevels;
import Reika.ChromatiCraft.Base.CrystalTransmitterRender;
import Reika.ChromatiCraft.Magic.Lore.LoreScripts.ScriptLocations;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.InWorldScriptRenderer;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Auxiliary.Trackers.SpecialDayTracker;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderCrystalPylon extends CrystalTransmitterRender {

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		super.renderTileEntityAt(tile, par2, par4, par6, par8);
		TileEntityCrystalPylon te = (TileEntityCrystalPylon)tile;

		if (tile.hasWorldObj() && (MinecraftForgeClient.getRenderPass() == 1 || StructureRenderer.isRenderingTiles())) {
			ReikaTextureHelper.bindTerrainTexture();
			IIcon ico = ChromaIcons.ROUNDFLARE.getIcon();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			ico = ChromaIcons.SUNFLARE.getIcon();
			float u2 = ico.getMinU();
			float v2 = ico.getMinV();
			float du2 = ico.getMaxU();
			float dv2 = ico.getMaxV();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(false);
			BlendMode.ADDITIVEDARK.apply();
			if (VoidGazeLevels.PYLONXRAY.isActiveOnPlayer(Minecraft.getMinecraft().thePlayer))
				GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);

			Tessellator v5 = Tessellator.instance;

			if (MinecraftForgeClient.getRenderPass() == 1 && ScriptLocations.PYLON.isEnabled() && Minecraft.getMinecraft().thePlayer.getDistanceSq(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5) < 576) {
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				InWorldScriptRenderer.renderPylonScript(te, par8, v5, 0.03125/2);
				GL11.glPopAttrib();
			}

			GL11.glTranslated(0.5, 0.5, 0.5);

			int count = te.isEnhanced() ? 2 : 1;
			if (te.isUnstable())
				count++;
			for (int di = 0; di < count; di++) {
				int i = di;
				boolean flag = false;
				GL11.glPushMatrix();
				double s0 = 0;
				if (te.isUnstable()) {
					if (i == 0) {
						flag = true;
						s0 = 0.25;
					}
					else {
						i--;
						s0 = -0.125+0.25*te.getRandom().nextDouble();
					}
				}
				double t = (i*60+te.randomOffset+System.currentTimeMillis()/2000D*(1+3*i))%360;
				double s = s0+i*0.5+2.5+0.5*Math.sin(t);
				if (!te.getTargets().isEmpty()) {
					s += 1;
				}
				if (SpecialDayTracker.instance.loadXmasTextures()) {
					s *= 0.7;
				}
				if (!te.canConduct() && !StructureRenderer.isRenderingTiles()) {
					s = 0.75;
				}
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

				int alpha = 255;//te.getEnergy()*255/te.MAX_ENERGY;
				//ReikaJavaLibrary.pConsole(te.getEnergy());

				int color = te.getRenderColor();

				if (StructureRenderer.isRenderingTiles() && !StructureRenderer.isRenderingRealTiles()) {
					color = CrystalElement.elements[(int)((System.currentTimeMillis()/4000)%16)].getColor();
				}

				v5.startDrawingQuads();
				v5.setColorRGBA_I(color, alpha);
				v5.addVertexWithUV(-1, -1, 0, flag ? u2 : u, flag ? v2 : v);
				v5.addVertexWithUV(1, -1, 0, flag ? du2 : du, flag ? v2 : v);
				v5.addVertexWithUV(1, 1, 0, flag ? du2 : du, flag ? dv2 : dv);
				v5.addVertexWithUV(-1, 1, 0, flag ? u2 : u, flag ? dv2 : dv);
				v5.draw();
				GL11.glPopMatrix();
			}

			if (te.canConduct() && te.isEnhanced()) {
				GL11.glPushMatrix();
				RenderManager rm = RenderManager.instance;
				GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
				this.renderBoostedHalo(te, par8);
				GL11.glPopMatrix();
			}

			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
		else if (!tile.hasWorldObj()) {
			IIcon ico = ChromaIcons.ROUNDFLARE.getIcon();
			ReikaTextureHelper.bindTerrainTexture();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			GL11.glDisable(GL11.GL_LIGHTING);
			//ReikaRenderHelper.disableEntityLighting();
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
			//RenderHelper.enableStandardItemLighting();
			GL11.glEnable(GL11.GL_LIGHTING);
		}
	}

	private void renderBoostedHalo(TileEntityCrystalPylon te, float ptick) {
		int c = te.getRenderColor();
		GL11.glAlphaFunc(GL11.GL_GEQUAL, 1/255F);
		Tessellator v5 = Tessellator.instance;

		double d = ((System.currentTimeMillis())/50D)%360;
		int a = (int)(127+92*Math.sin(Math.toRadians(d/2D)));
		c = ReikaColorAPI.mixColors(c, 0, a/255F);

		IIcon ico = ChromaIcons.TURBO.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		GL11.glRotated(-d, 0, 0, 1);

		double z = -0.01;

		double s = 3+1*Math.sin(Math.toRadians(d));
		//u = 0;//(te.getTicksExisted()+n*2)%18/18F;
		//du = u+1/18F;
		v5.startDrawingQuads();
		v5.setColorOpaque_I(c);
		v5.addVertexWithUV(-s, -s, z, u, v);
		v5.addVertexWithUV(s, -s, z, du, v);
		v5.addVertexWithUV(s, s, z, du, dv);
		v5.addVertexWithUV(-s, s, z, u, dv);
		v5.draw();
		/*

		int step = 15;
		double d = (System.currentTimeMillis()/50D)%360;
		int n = 0;
		int mn = 90/step;

		IIcon ico = ChromaIcons.TRIDOT.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		for (int i = 0; i < 90; i += step) {
			GL11.glRotated(i+d, 0, 0, 1);

			double s = 2+4*Math.sin(Math.toRadians(4*d+i*2));
			//u = 0;//(te.getTicksExisted()+n*2)%18/18F;
			//du = u+1/18F;
			v5.startDrawingQuads();
			v5.setColorOpaque_I(c);
			v5.addVertexWithUV(-s, -s, 0, u, v);
			v5.addVertexWithUV(s, -s, 0, du, v);
			v5.addVertexWithUV(s, s, 0, du, dv);
			v5.addVertexWithUV(-s, s, 0, u, dv);
			v5.draw();
			n++;
		}
		 */
		GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.1F);
	}

}

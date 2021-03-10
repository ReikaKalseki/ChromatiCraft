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

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Magic.MonumentCompletionRitual;
import Reika.ChromatiCraft.Magic.Lore.LoreScripts.ScriptLocations;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.InWorldScriptRenderer;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

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

			double dd = Minecraft.getMinecraft().thePlayer.getDistanceSq(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5);
			if (te.isInWorld()) {

				if (dd < 576)
					this.renderScript(te, par8, v5);

				this.activateShaders(te, par8);

				if (te.isMonument()) {
					GL11.glTranslated(0, 0.005-4, 0);

					double sz = 16;
					GL11.glPushMatrix();
					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					GL11.glShadeModel(GL11.GL_SMOOTH);
					ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/monument_lines.png");
					//v5.startDrawingQuads();
					v5.startDrawing(GL11.GL_TRIANGLE_FAN);
					v5.setBrightness(240);

					/*
					v5.setColorOpaque_I(c);
					v5.addVertexWithUV(-sz, 0, sz+1, 0, 1);
					v5.addVertexWithUV(sz+1, 0, sz+1, 1, 1);
					v5.addVertexWithUV(sz+1, 0, -sz, 1, 0);
					v5.addVertexWithUV(-sz, 0, -sz, 0, 0);
					 */
					v5.setColorOpaque_I(0xffffff);
					v5.addVertexWithUV(0.5, 0, 0.5, 0.5, 0.5);
					double r0 = 18.5;
					for (int i = 0; i <= 16; i++) {
						CrystalElement e = CrystalElement.elements[i%16];
						double a0 = (i-8)*22.5+11.25;
						if (i%4 == 1)
							a0 -= 5;
						else if (i%4 == 2)
							a0 += 5;
						double d = 6;//4;
						if (e == CrystalElement.BLACK)
							;//ReikaJavaLibrary.pConsole(a0+" > "+(a0-d)+" - "+(a0+d));
						double min = i == 0 ? a0 : a0-d;
						double max = i == 16 ? a0 : a0+d;
						for (double a = min; a <= max; a += d) {
							double ang = Math.toRadians(a);
							double r = r0;//Math.min(r0, r0+3*(1-Math.cos(ang*4)));
							double cs = Math.cos(ang);
							double ss = Math.sin(ang);
							double dx = 0.5+r*cs;
							double dz = 0.5+r*ss;
							double u = 0.5+0.5*cs;
							double v = 0.5+0.5*ss;
							//v5.setColorOpaque_I(CrystalElement.getBlendedColor((int)((a+180-12.25)*2), 45));
							int c = e.getColor();
							v5.setColorOpaque_I(c);
							v5.addVertexWithUV(dx, 0, dz, u, v);
						}
					}

					v5.draw();
					GL11.glPopAttrib();
					GL11.glPopMatrix();
				}
			}

			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
		GL11.glPopAttrib();
	}

	private void activateShaders(TileEntityStructControl te, float par8) {
		if (te.isMonument() && MonumentCompletionRitual.areRitualsRunning()) {
			ChromaShaders.MONUMENT$GENERAL.rampUpIntensity(0.05F, 1.1F);
			ChromaShaders.MONUMENT$GENERAL.refresh();
			ChromaShaders.MONUMENT$GENERAL.lingerTime = 120;
			ChromaShaders.MONUMENT$GENERAL.rampDownAmount = 0.0025F;
			ChromaShaders.MONUMENT$GENERAL.rampDownFactor = 0.995F;
			ChromaShaders.MONUMENT$GENERAL.getShader().setMatricesToCurrent();
		}
	}

	private void renderScript(TileEntityStructControl te, float par8, Tessellator v5) {
		double sc = 0.03125/2;
		GL11.glPushMatrix();
		if (te.getStructureType() != null) {
			switch(te.getStructureType()) {
				case BURROW:
					if (ScriptLocations.BURROW.isEnabled())
						InWorldScriptRenderer.renderBurrowScript(te, par8, v5, sc);
					break;
				case CAVERN:
					if (ScriptLocations.CAVERN1.isEnabled())
						InWorldScriptRenderer.renderCavernScript(te, par8, v5, sc);
					break;
				case DESERT:
					if (ScriptLocations.DESERT.isEnabled())
						InWorldScriptRenderer.renderDesertScript(te, par8, v5, sc);
					break;
				case OCEAN:
					if (ScriptLocations.OCEAN.isEnabled())
						InWorldScriptRenderer.renderOceanScript(te, par8, v5, sc);
					break;
				case SNOWSTRUCT:
					if (ScriptLocations.SNOWSTRUCT.isEnabled())
						InWorldScriptRenderer.renderSnowScript(te, par8, v5, sc);
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

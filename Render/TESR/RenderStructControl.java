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

import java.util.Collection;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
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
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.RayTracer.RayTracerWithCache;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

public class RenderStructControl extends ChromaRenderBase {

	protected static final RayTracerWithCache LOS = RayTracer.getVisualLOSForRenderCulling();

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
			boolean flag = te.isInWorld() && !te.isMonument();
			if (flag)
				BlendMode.INVERTEDADD.apply();
			else
				BlendMode.ADDITIVEDARK.apply();
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);

			Tessellator v5 = Tessellator.instance;

			this.renderFlare(te, v5);

			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			te.renderDelegate();
			GL11.glPopAttrib();
			GL11.glPopMatrix();

			BlendMode.ADDITIVEDARK.apply();

			if (te.isInWorld()) {
				if (flag) {
					EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
					double dd = ep.getDistanceSq(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5);
					LOS.setOrigins(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5, ep.posX, ep.posY, ep.posZ);
					if (LOS.isClearLineOfSight(te)) {
						float f = 0;
						if (dd <= 16) {
							f = 1;
						}
						else if (dd <= 112) {
							f = 1-(float)((dd-16D)/96D);
						}
						if (f > 0) {
							ChromaShaders.STRUCTCONTROL.clearOnRender = true;
							ChromaShaders.STRUCTCONTROL.setIntensity(f);
							ChromaShaders.STRUCTCONTROL.getShader().setFocus(te);
							ChromaShaders.STRUCTCONTROL.getShader().setMatricesToCurrent();
							ChromaShaders.STRUCTCONTROL.getShader().setField("distance", dd);
						}
					}

					if (dd < 576)
						this.renderScript(te, par8, v5);
				}

				this.activateShaders(te, par8);

				if (te.isMonument()) {
					float br = this.getMonumentBrightness();
					if (br > 0) {
						GL11.glTranslated(0, 0.005-4, 0);

						double sz = 16;
						GL11.glPushMatrix();
						GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
						//GL11.glDisable(GL11.GL_TEXTURE_2D);
						GL11.glShadeModel(GL11.GL_SMOOTH);
						ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/monument_lines_big.png");
						//v5.startDrawingQuads();
						v5.startDrawing(GL11.GL_TRIANGLE_FAN);
						v5.setBrightness(240);
						v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, br));
						v5.addVertexWithUV(0.5, 0.01, 0.5, 0.5, 0.5);
						double r0 = 18.5;
						double r1 = 12;
						double dt = r1/r0*0.5;
						for (int i = 0; i <= 16; i++) {
							double a0 = (i-8)*22.5+11.25;
							if (i%4 == 1)
								a0 -= 5;
							else if (i%4 == 2)
								a0 += 5;
							double d = 6;//4;
							double min = i == 0 ? a0 : a0-d;
							double max = i == 16 ? a0 : a0+d;
							for (double a = min; a <= max; a += d) {
								double ang = Math.toRadians(a);
								double cs = Math.cos(ang);
								double ss = Math.sin(ang);
								double dx = 0.5+r1*cs;
								double dz = 0.5+r1*ss;
								double u = 0.5+dt*cs;
								double v = 0.5+dt*ss;
								//v5.setColorOpaque_I(CrystalElement.getBlendedColor((int)((a+180-12.25)*2), 45));
								//v5.setColorOpaque_I(mix.isEmpty() ? 0 : this.getColorMix(mix));
								CrystalElement e = CrystalElement.elements[i%16];
								int c = e.getColor();
								c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, MonumentCompletionRitual.getIntensity(e));
								v5.setColorOpaque_I(c);
								v5.addVertexWithUV(dx, 0.01, dz, u, v);
							}
						}

						v5.draw();
						v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
						v5.setBrightness(240);
						for (int i = 0; i <= 16; i++) {
							double a0 = (i-8)*22.5+11.25;
							if (i%4 == 1)
								a0 -= 5;
							else if (i%4 == 2)
								a0 += 5;
							double d = 6;//4;
							double min = i == 0 ? a0 : a0-d;
							double max = i == 16 ? a0 : a0+d;
							//TODO make the color fade sharper with a triangle strip ring
							for (double a = min; a <= max; a += d) {
								double ang = Math.toRadians(a);
								double cs = Math.cos(ang);
								double ss = Math.sin(ang);
								double dx1 = 0.5+r1*cs;
								double dz1 = 0.5+r1*ss;
								double dx2 = 0.5+r0*cs;
								double dz2 = 0.5+r0*ss;
								double u1 = 0.5+dt*cs;
								double v1 = 0.5+dt*ss;
								double u2 = 0.5+0.5*cs;
								double v2 = 0.5+0.5*ss;
								CrystalElement e = CrystalElement.elements[i%16];
								int c = e.getColor();
								c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, MonumentCompletionRitual.getIntensity(e));
								v5.setColorOpaque_I(c);
								v5.addVertexWithUV(dx1, 0, dz1, u1, v1);
								v5.addVertexWithUV(dx2, 0, dz2, u2, v2);
							}
						}

						v5.draw();
						GL11.glPopAttrib();
						GL11.glPopMatrix();
					}
				}
			}

			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
		GL11.glPopAttrib();
	}

	private float getMonumentBrightness() {
		float max = 0;
		for (int i = 0; i < 16; i++) {
			max = Math.max(max, MonumentCompletionRitual.getIntensity(CrystalElement.elements[i]));
		}
		return max;
	}

	private int getColorMix(Collection<CrystalElement> mix) {
		int r = 0;
		int g = 0;
		int b = 0;
		float n = 0;
		for (CrystalElement e : mix) {
			float f = MonumentCompletionRitual.getIntensity(e);
			if (f > 0) {
				r += f*e.getRed();
				g += f*e.getGreen();
				b += f*e.getBlue();
				n += f;
			}
		}
		return n == 0 ? 0 : ReikaColorAPI.RGBtoHex((int)(r/n), (int)(g/n), (int)(b/n));
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
				case BIOMEFRAG:
					if (ScriptLocations.BIOMESTRUCT.isEnabled())
						InWorldScriptRenderer.renderBiomeScript(te, par8, v5, sc);
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

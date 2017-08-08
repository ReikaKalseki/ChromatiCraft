/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Rendering;

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.RenderManager;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.World.Dimension.SkyRiverGenerator;
import Reika.ChromatiCraft.World.Dimension.SkyRiverGenerator.RiverPoint;
import Reika.DragonAPI.Instantiable.ParticleController.CollectingPositionController;
import Reika.DragonAPI.Interfaces.PositionController;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkyRiverRenderer {

	public static final SkyRiverRenderer instance = new SkyRiverRenderer();

	private static final double STEP_PER_POINT = 0.75;

	private SkyRiverRenderer() {

	}

	public void render() {
		GL11.glPushMatrix();
		GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDepthMask(false);
		Collection<RiverPoint> c = SkyRiverGenerator.getPointsWithin(Minecraft.getMinecraft().thePlayer, 512, false);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "/Reika/ChromatiCraft/Textures/SkyRiver2.png");
		for (RiverPoint p : c) {
			if (p.nextRiverPoint == null)
				continue;
			GL11.glPushMatrix();
			GL11.glTranslated(p.position.xCoord, p.position.yCoord, p.position.zCoord);

			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glPushMatrix();
			GL11.glTranslated(-System.currentTimeMillis()%4000D/4000D, 0, 0);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);

			double t = System.currentTimeMillis()/1250D;
			double r1 = SkyRiverGenerator.RIVER_TUNNEL_RADIUS+4*Math.sin(t-p.positionID*STEP_PER_POINT);
			double r2 = SkyRiverGenerator.RIVER_TUNNEL_RADIUS+4*Math.sin(t-p.nextRiverPoint.positionID*STEP_PER_POINT);
			int clr = ReikaColorAPI.getModifiedHue(0xff0000, (int)((p.positionID*4-System.currentTimeMillis()/500D)%360D));
			int c1 = clr;
			int c2 = ReikaColorAPI.getModifiedHue(0xff0000, (int)(((p.positionID+1)*4-System.currentTimeMillis()/500D)%360D));
			if (p.positionID == 1) {
				c1 = ReikaColorAPI.getColorWithBrightnessMultiplier(c1, 0.01F);
			}
			else if (p.nextRiverPoint.nextRiverPoint == null) {
				c2 = ReikaColorAPI.getColorWithBrightnessMultiplier(c2, 0.01F);
			}
			ChromaFX.drawEnergyTransferBeam(p.position, p.next, c1, c2, r1, r2, 36, t/4500D, true);

			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);

			GL11.glPopMatrix();

			if (!Minecraft.getMinecraft().isGamePaused()) {
				if (p.positionID == 1) {
					int n = ReikaRandomHelper.getRandomBetween(1, 3);

					double lx = p.next.xCoord-p.position.xCoord;
					double ly = p.next.yCoord-p.position.yCoord;
					double lz = p.next.zCoord-p.position.zCoord;

					for (int i = 0; i < n; i++) {
						double d = ReikaRandomHelper.getRandomBetween(0.5, 0.9375);

						double px = p.position.xCoord-lx*d;
						double py = p.position.yCoord-ly*d;
						double pz = p.position.zCoord-lz*d;

						double tx = p.position.xCoord+lx/32D;
						double ty = p.position.yCoord+ly/32D;
						double tz = p.position.zCoord+lz/32D;

						double dx = ReikaRandomHelper.getRandomPlusMinus(0, r1/2);
						double dy = ReikaRandomHelper.getRandomPlusMinus(0, r1/2);
						double dz = ReikaRandomHelper.getRandomPlusMinus(0, r1/2);

						double dd = ReikaRandomHelper.getRandomBetween(1.25, 2);

						px += dx*dd;
						py += dy*dd;
						pz += dz*dd;

						tx += dx;
						ty += dy;
						tz += dz;

						int l = ReikaRandomHelper.getRandomBetween(15, 30);
						PositionController pc = new CollectingPositionController(px, py, pz, tx, ty, tz, l);
						EntityFX fx = new EntityBlurFX(Minecraft.getMinecraft().theWorld, px, py, pz).setLife(l).setScale(14).setPositionController(pc).setColor(clr).setIcon(ChromaIcons.FADE_GENTLE);

						//ReikaJavaLibrary.pConsole(fx);

						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
				}
				else if (p.nextRiverPoint.nextRiverPoint == null) {
					int n = ReikaRandomHelper.getRandomBetween(1, 3);

					double lx = p.next.xCoord-p.position.xCoord;
					double ly = p.next.yCoord-p.position.yCoord;
					double lz = p.next.zCoord-p.position.zCoord;

					for (int i = 0; i < n; i++) {
						double d = ReikaRandomHelper.getRandomBetween(0.03125, 0.375);

						double px = p.position.xCoord+lx*d;
						double py = p.position.yCoord+ly*d;
						double pz = p.position.zCoord+lz*d;

						double dx = ReikaRandomHelper.getRandomPlusMinus(0, r1/4);
						double dy = ReikaRandomHelper.getRandomPlusMinus(0, r1/4);
						double dz = ReikaRandomHelper.getRandomPlusMinus(0, r1/4);

						double dd = ReikaRandomHelper.getRandomBetween(1.25, 2);

						px += dx*dd;
						py += dy*dd;
						pz += dz*dd;

						double v = ReikaRandomHelper.getRandomBetween(0.03125, 0.25)/8D;
						double vx = v*lx;
						double vy = v*ly;
						double vz = v*lz;

						vx = ReikaRandomHelper.getRandomPlusMinus(vx, 0.125);
						vy = ReikaRandomHelper.getRandomPlusMinus(vy, 0.125);
						vz = ReikaRandomHelper.getRandomPlusMinus(vz, 0.125);

						int l = ReikaRandomHelper.getRandomBetween(30, 80);
						EntityFX fx = new EntityBlurFX(Minecraft.getMinecraft().theWorld, px, py, pz, vx, vy, vz).setLife(l).setScale(14).setColor(clr).setIcon(ChromaIcons.FADE_GENTLE);

						//ReikaJavaLibrary.pConsole(fx);

						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
				}
			}
		}
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private void doRenderRiver() {

	}

}

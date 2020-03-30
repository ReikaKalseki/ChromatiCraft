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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityTeleportGate;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.RayTracer.MultipointChecker;
import Reika.DragonAPI.Instantiable.RayTracer.RayTracerWithCache;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;


public class RenderTeleportGate extends ChromaRenderBase implements MultipointChecker<TileEntityTeleportGate> {

	private static final double TRACE_RADIUS = 1.5;
	private final RayTracerWithCache trace = RayTracer.getMultipointVisualLOSForRenderCulling(this);
	private static boolean cachedRaytrace;
	private static long lastTraceTick;
	private static int lastTraceTileHash;

	private static final double[][] RAYTRACES = {
			{0.5-TRACE_RADIUS, 0, 0.5-TRACE_RADIUS},
			{0.5+TRACE_RADIUS, 0, 0.5-TRACE_RADIUS},
			{0.5-TRACE_RADIUS, 0, 0.5+TRACE_RADIUS},
			{0.5+TRACE_RADIUS, 0, 0.5+TRACE_RADIUS},
			{0.5, 1.5, 0.5}
	};

	public boolean isClearLineOfSight(TileEntityTeleportGate te, RayTracer trace, World world) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		for (int i = 0; i < RAYTRACES.length; i++) {
			double[] xyz = RAYTRACES[i];
			trace.setOrigins(te.xCoord+xyz[0], te.yCoord+xyz[1], te.zCoord+xyz[2], ep.posX, ep.posY, ep.posZ);
			if (trace.isClearLineOfSight(world))
				return true;
		}
		return false;
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityTeleportGate te = (TileEntityTeleportGate)tile;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();

		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glTranslated(par2, par4, par6);

		GL11.glPushMatrix();

		if (!te.isInWorld() || (!te.hasStructure() && MinecraftForgeClient.getRenderPass() == 1)) {
			if (te.isInWorld()) {
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glTranslated(0.5, 0.5, 0.5);
				double s = 0.5;
				GL11.glScaled(s, s, s);
				RenderManager rm = RenderManager.instance;
				GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
			}
			else {
				GL11.glRotated(45, 0, 1, 0);
				GL11.glRotated(-30, 1, 0, 0);
				GL11.glTranslated(0.04, 0.0625, 0);
			}
			Tessellator v5 = Tessellator.instance;
			IIcon ico = ChromaIcons.CONDENSEFLARE.getIcon();
			double u = ico.getMinU();
			double v = ico.getMinV();
			double du = ico.getMaxU();
			double dv = ico.getMaxV();
			ReikaTextureHelper.bindTerrainTexture();
			v5.startDrawingQuads();
			v5.setBrightness(240);
			int c = ReikaColorAPI.mixColors(te.PRIVATE_COLOR, te.PUBLIC_COLOR, 0.5F+0.5F*(float)Math.sin(System.currentTimeMillis()/400D));
			v5.setColorOpaque_I(c);
			double s = 1;
			v5.addVertexWithUV(-s, -s, 0, u, v);
			v5.addVertexWithUV(+s, -s, 0, du, v);
			v5.addVertexWithUV(+s, s, 0, du, dv);
			v5.addVertexWithUV(-s, s, 0, u, dv);
			v5.draw();
		}

		if ((MinecraftForgeClient.getRenderPass() == 1 && te.hasStructure()) || StructureRenderer.isRenderingTiles()) {

			if (!te.isInWorld()) {
				double is = 0.25;
				GL11.glScaled(is, is, is);
			}

			int c = te.getRenderColor();
			double s = 5;
			GL11.glTranslated(0.5, 0.005, 0.5);
			double t = !StructureRenderer.isRenderingTiles() ? (te.getTicksExisted()+par8)/4D : (System.currentTimeMillis()%200000)/100D;

			GL11.glPushMatrix();
			GL11.glScaled(s, s, s);
			GL11.glRotated(t, 0, 1, 0);
			Tessellator v5 = Tessellator.instance;
			IIcon ico = ChromaIcons.PORTALRING.getIcon();
			double u = ico.getMinU();
			double v = ico.getMinV();
			double du = ico.getMaxU();
			double dv = ico.getMaxV();
			ReikaTextureHelper.bindTerrainTexture();
			v5.startDrawingQuads();
			v5.setBrightness(240);
			v5.setColorOpaque_I(c);

			for (double sz = 0.9375; sz <= 1.125; sz += 0.0625) {
				v5.addVertexWithUV(-sz, 0.03125-(sz-0.9375)*0.125, sz, u, dv);
				v5.addVertexWithUV(sz, 0.03125-(sz-0.9375)*0.125, sz, du, dv);
				v5.addVertexWithUV(sz, 0.03125-(sz-0.9375)*0.125, -sz, du, v);
				v5.addVertexWithUV(-sz, 0.03125-(sz-0.9375)*0.125, -sz, u, v);
			}

			v5.draw();
			GL11.glPopMatrix();

			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/gate_g.png");
			double h = 3.5;
			double r = 1.5;

			double[][] pts = {
					{0, r, 0.5},
					{h*0.5, r*0.75, 1},
					{h*0.75, r/2, 1.5},
					{h*0.9375, r/8, 2},
			};

			v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
			v5.setBrightness(240);
			v5.setColorOpaque_I(c);
			t *= 6;

			GL11.glPushMatrix();
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glPushMatrix();
			GL11.glTranslated(0, -t/64D, 0);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			for (int i = 0; i < pts.length-1; i++) {
				for (double a = 0; a <= 360; a += 5) {
					double dx = pts[i][1]*Math.cos(Math.toRadians(-a));
					double dz = pts[i][1]*Math.sin(Math.toRadians(-a));
					double dx2 = pts[i+1][1]*Math.cos(Math.toRadians(-a));
					double dz2 = pts[i+1][1]*Math.sin(Math.toRadians(-a));
					double tr1 = pts[i][1];
					double tr2 = pts[i+1][1];
					double ta1 = Math.toRadians(a-t*pts[i][2]);
					double ta2 = Math.toRadians(a-t*pts[i+1][2]);
					double tx = tr1*Math.cos(ta1);
					double tz = tr1*Math.sin(ta1);
					double tx2 = tr2*Math.cos(ta2);
					double tz2 = tr2*Math.sin(ta2);
					v5.addVertexWithUV(dx2, pts[i+1][0], dz2, tx2, tz2);
					v5.addVertexWithUV(dx, pts[i][0], dz, tx, tz);
				}
			}
			v5.draw();

			v5.startDrawing(GL11.GL_TRIANGLE_FAN);
			v5.setBrightness(240);
			v5.setColorOpaque_I(c);
			v5.addVertexWithUV(0, h*0.975, 0, 0.5, 0.5);
			int i = pts.length-1;
			for (double a = 0; a <= 360; a += 5) {
				double dx = pts[i][1]*Math.cos(Math.toRadians(-a));
				double dz = pts[i][1]*Math.sin(Math.toRadians(-a));
				double tr1 = pts[i][1];
				double ta1 = Math.toRadians(a-t*pts[i][2]);
				double tx = tr1*Math.cos(ta1);
				double tz = tr1*Math.sin(ta1);
				v5.addVertexWithUV(dx, pts[i][0], dz, tx, tz);
			}
			v5.draw();

			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPopMatrix();

			if (te.isInWorld() || StructureRenderer.isRenderingTiles()) {

				if (StructureRenderer.isRenderingTiles() || trace.isClearLineOfSight(te)) {
					GL11.glPushMatrix();
					GL11.glTranslated(0, 1.5, 0);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					ReikaTextureHelper.bindTerrainTexture();
					ico = ChromaIcons.CONDENSEFLARE.getIcon();
					RenderManager rm = RenderManager.instance;
					if (StructureRenderer.isRenderingTiles()) {
						GL11.glRotated(-StructureRenderer.getRenderRY(), 0, 1, 0);
						GL11.glRotated(-StructureRenderer.getRenderRX(), 1, 0, 0);
					}
					else {
						GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
						GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
					}
					u = ico.getMinU();
					v = ico.getMinV();
					du = ico.getMaxU();
					dv = ico.getMaxV();
					v5.startDrawingQuads();
					v5.setBrightness(240);

					s = 10;
					v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.625F));
					v5.addVertexWithUV(-s, s, 0, u, dv);
					v5.addVertexWithUV(+s, s, 0, du, dv);
					v5.addVertexWithUV(+s, -s, 0, du, v);
					v5.addVertexWithUV(-s, -s, 0, u, v);
					v5.draw();
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					GL11.glPopMatrix();
				}

				GL11.glPushMatrix();
				for (double a = 0; a < 180; a += 60) {
					GL11.glRotated(a-RenderManager.instance.playerViewY, 0, 1, 0);
					v5.startDrawingQuads();
					v5.setColorOpaque_I(c);
					v5.setBrightness(240);
					ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/arches2.png");
					for (double ux = 0; ux <= 0.5; ux += 0.5) {
						//v5.setColorRGBA_I(CrystalElement.getBlendedColor(te.getTicks()+(int)(8*ux), 20), 255);
						u = ux+((System.currentTimeMillis()/50)%32)/32D;
						v = 0;
						du = u+1/32D;
						dv = v+1;
						h = 9;
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

				int ta = te.getActivationTick();
				if (ta > 0) {
					float f = ta > 8 ? 1 : ta/8F;
					float f2 = ta > te.ACTIVATION_DURATION-20 ? (te.ACTIVATION_DURATION-ta)/20F : 1;
					float f3 = ta <= 8 ? 1-((ta-1)/8F) : 0;
					ReikaTextureHelper.bindTerrainTexture();
					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					GL11.glPushMatrix();
					GL11.glDisable(GL11.GL_CULL_FACE);
					ico = ChromaIcons.CONCENTRIC2REV.getIcon();
					v5.startDrawingQuads();
					v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2));
					v5.setBrightness(240);
					u = ico.getMinU();
					v = ico.getMinV();
					du = ico.getMaxU();
					dv = ico.getMaxV();
					double sz = 8*f;
					v5.addVertexWithUV(-sz, 1.5, sz, u, dv);
					v5.addVertexWithUV(sz, 1.5, sz, du, dv);
					v5.addVertexWithUV(sz, 1.5, -sz, du, v);
					v5.addVertexWithUV(-sz, 1.5, -sz, u, v);
					v5.draw();

					if (f3 > 0) {
						GL11.glPushMatrix();
						double ang = ReikaPhysicsHelper.cartesianToPolar(te.xCoord+0.5-RenderManager.renderPosX, te.yCoord+0.5-RenderManager.renderPosY, te.zCoord+0.5-RenderManager.renderPosZ)[2];
						GL11.glRotated(ang, 0, 1, 0);
						sz = 3;
						v5.startDrawingQuads();
						v5.setBrightness(240);
						float f4 = Math.min(1, f3*1.375F);
						v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(c, f4));
						int mi = (int)(f3*24);
						for (i = 0; i < mi; i++) {
							ico = i == mi-1 ? ChromaIcons.LASEREND.getIcon() : ChromaIcons.LASER.getIcon();
							u = ico.getMinU();
							v = ico.getMinV();
							du = ico.getMaxU();
							dv = ico.getMaxV();
							v5.addVertexWithUV(-sz, i*4, 0, u, v);
							v5.addVertexWithUV(sz, i*4, 0, u, dv);
							v5.addVertexWithUV(sz, i*4+4, 0, du, dv);
							v5.addVertexWithUV(-sz, i*4+4, 0, du, v);
						}
						v5.draw();
						GL11.glPopMatrix();
					}

					GL11.glPopAttrib();
					GL11.glPopMatrix();
				}
			}

		}
		GL11.glPopAttrib();

		GL11.glPopMatrix();

		//if (StructureRenderer.isRenderingTiles())
		//	te.tickFX();

		if (MinecraftForgeClient.getRenderPass() == 1)
			te.particles.render(true);

		GL11.glPopMatrix();
	}

}

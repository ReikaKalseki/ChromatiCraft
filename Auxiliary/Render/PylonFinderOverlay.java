/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Items.Tools.ItemBottleneckFinder.WarningLevels;
import Reika.ChromatiCraft.Items.Tools.ItemPylonFinder;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.IWG.PylonGenerator;
import Reika.ChromatiCraft.World.IWG.PylonGenerator.PylonEntry;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PylonFinderOverlay {

	public static final PylonFinderOverlay instance = new PylonFinderOverlay();

	private static final ColorBlendList powerCrystalDiamondColor = new ColorBlendList(200).addAll(ChromaFX.getChromaColorTiles());

	private Collection<RenderEntry> renders = new ArrayList();

	private PylonFinderOverlay() {

	}


	@SubscribeEvent
	public void renderPylonFinderHUD(RenderGameOverlayEvent evt) {
		if (evt.type == ElementType.HELMET) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			if (PylonGenerator.instance.canGenerateIn(ep.worldObj)) {
				boolean compass = ChromaItems.FINDER.matchWith(ep.getCurrentEquippedItem()) || (ep.getEntityData().hasKey(ItemPylonFinder.NBT_KEY) && ep.getEntityData().getLong(ItemPylonFinder.NBT_KEY) >= ep.worldObj.getTotalWorldTime()-20);
				if (compass || !renders.isEmpty()) {
					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					Tessellator v5 = Tessellator.instance;

					//ArrayList<CrystalElement> left = new ArrayList();
					//ArrayList<CrystalElement> right = new ArrayList();

					//int x = MathHelper.floor_double(ep.posX);
					//int y = MathHelper.floor_double(ep.posY);
					//int z = MathHelper.floor_double(ep.posZ);
					int h = evt.resolution.getScaledHeight()/2;
					float yaw = ep.rotationYawHead%360;
					float pitch = ep.rotationPitch+90;
					if (yaw < 0)
						yaw += 360;
					int fov = ReikaRenderHelper.getRealFOV();
					GL11.glPushMatrix();
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					if (compass) {
						for (int i = 0; i < CrystalElement.elements.length; i++) {
							CrystalElement e = CrystalElement.elements[i];
							PylonEntry c = PylonGenerator.instance.getNearestPylonSpawn(ep.worldObj, ep.posX, ep.posY+ep.getEyeHeight(), ep.posZ, e);
							if (c != null) {
								RenderPosition pos = new RenderPosition(c.location, ep, yaw, pitch, fov, h);
								new PylonDisplay(c).render(v5, ep, pos, fov, h, evt.resolution);
							}
						}
					}
					Iterator<RenderEntry> it = renders.iterator();
					while (it.hasNext()) {
						RenderEntry e = it.next();
						RenderPosition pos = new RenderPosition(e.location, ep, yaw, pitch, fov, h);
						e.render(v5, ep, pos, fov, h, evt.resolution);
						if (e.tick())
							it.remove();
					}
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					//GL11.glDisable(GL11.GL_BLEND);
					/*
				int i = 0;
				FontRenderer f = Minecraft.getMinecraft().fontRenderer;
				for (CrystalElement e : left) {
					String s = e.displayName;
					int y = (int)(h+(i-left.size()/2D)*f.FONT_HEIGHT);
					f.drawString(s, 24, y, e.getColor());
					i++;
				}

				i = 0;
				for (CrystalElement e : right) {
					String s = e.displayName;
					int y = (int)(h+(i-left.size()/2D)*f.FONT_HEIGHT);
					f.drawString(s, evt.resolution.getScaledWidth()-24-f.getStringWidth(s), y, e.getColor());
					i++;
				}*/

					GL11.glPopAttrib();
					GL11.glPopMatrix();
				}
			}
		}
	}

	public void addBottleneckWarning(WorldLocation loc, CrystalElement e, WarningLevels w, boolean isThroughput) {
		BottleneckWarning bw = new BottleneckWarning(loc, e, w, isThroughput);
		renders.add(bw);
	}

	private static abstract class RenderEntry {

		protected final WorldLocation location;

		protected final int lifespan;
		protected int age;

		protected RenderEntry(WorldLocation loc, int life) {
			location = loc;
			lifespan = life;
		}

		private final boolean tick() {
			age++;
			return age >= lifespan;
		}

		protected abstract void render(Tessellator v5, EntityPlayer ep, RenderPosition pos, int fov, int h, ScaledResolution resolution);

	}

	private static class PylonDisplay extends RenderEntry {

		private PylonEntry loc;

		private PylonDisplay(PylonEntry c) {
			super(c.location, Integer.MAX_VALUE);
			loc = c;
		}

		@Override
		protected void render(Tessellator v5, EntityPlayer ep, RenderPosition pos, int fov, int h, ScaledResolution resolution) {
			CrystalElement e = loc.color;
			//ReikaJavaLibrary.pConsole(e+": "+c);

			float u = e.getFaceRune().getMinU();
			float v = e.getFaceRune().getMinV();
			float du = e.getFaceRune().getMaxU();
			float dv = e.getFaceRune().getMaxV();

			float u2 = ChromaIcons.DIAMOND.getIcon().getMinU();
			float v2 = ChromaIcons.DIAMOND.getIcon().getMinV();
			float du2 = ChromaIcons.DIAMOND.getIcon().getMaxU();
			float dv2 = ChromaIcons.DIAMOND.getIcon().getMaxV();

			if (pos.phi >= 180 && 360-fov > pos.phi) {
				int cx = 10;
				v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
				v5.setColorRGBA_I(e.getColor(), 96);
				v5.setBrightness(240);
				v5.addVertex(cx+10, pos.cy+10, 0);
				v5.addVertex(cx+10, pos.cy-10, 0);
				v5.addVertex(cx, pos.cy, 0);
				v5.draw();

				v5.startDrawing(GL11.GL_LINE_LOOP);
				v5.setColorOpaque_I(e.getColor());
				v5.setBrightness(240);
				v5.addVertex(cx, pos.cy, 0);
				v5.addVertex(cx+10, pos.cy-10, 0);
				v5.addVertex(cx+10, pos.cy+10, 0);
				v5.draw();
				//left.add(e);

				ReikaTextureHelper.bindTerrainTexture();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				v5.startDrawingQuads();
				v5.setColorOpaque_I(e.getColor());
				v5.setBrightness(240);
				v5.addVertexWithUV(cx+10, pos.cy+8, 0, u, dv);
				v5.addVertexWithUV(cx+26, pos.cy+8, 0, du, dv);
				v5.addVertexWithUV(cx+26, pos.cy-8, 0, du, v);
				v5.addVertexWithUV(cx+10, pos.cy-8, 0, u, v);
				v5.draw();
				GL11.glDisable(GL11.GL_TEXTURE_2D);
			}
			else if (pos.phi < 180 && pos.phi > fov) {
				int cx = resolution.getScaledWidth()-10;
				v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
				v5.setColorRGBA_I(e.getColor(), 96);
				v5.setBrightness(240);
				v5.addVertex(cx, pos.cy, 0);
				v5.addVertex(cx-10, pos.cy-10, 0);
				v5.addVertex(cx-10, pos.cy+10, 0);
				v5.draw();

				v5.startDrawing(GL11.GL_LINE_LOOP);
				v5.setColorOpaque_I(e.getColor());
				v5.setBrightness(240);
				v5.addVertex(cx, pos.cy, 0);
				v5.addVertex(cx-10, pos.cy-10, 0);
				v5.addVertex(cx-10, pos.cy+10, 0);
				v5.draw();
				//right.add(e);

				ReikaTextureHelper.bindTerrainTexture();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				v5.startDrawingQuads();
				v5.setColorOpaque_I(e.getColor());
				v5.setBrightness(240);
				v5.addVertexWithUV(cx-26, pos.cy+8, 0, u, dv);
				v5.addVertexWithUV(cx-10, pos.cy+8, 0, du, dv);
				v5.addVertexWithUV(cx-10, pos.cy-8, 0, du, v);
				v5.addVertexWithUV(cx-26, pos.cy-8, 0, u, v);
				v5.draw();
				GL11.glDisable(GL11.GL_TEXTURE_2D);
			}
			else { //on screen, in FOV
				v5.startDrawingQuads();
				v5.setColorRGBA_I(e.getColor(), 32);
				v5.setBrightness(240);
				double w = resolution.getScaledWidth()/2D;
				double cx = w+1*w*Math.sin(Math.toRadians(pos.phi));
				//ReikaJavaLibrary.pConsole(cx, c.zCoord == 1184 && c.xCoord == -1047);
				v5.addVertex(cx-8, pos.cy+8, 0);
				v5.addVertex(cx+8, pos.cy+8, 0);
				v5.addVertex(cx+8, pos.cy-8, 0);
				v5.addVertex(cx-8, pos.cy-8, 0);
				v5.draw();

				v5.startDrawing(GL11.GL_LINE_LOOP);
				v5.setColorOpaque_I(e.getColor());
				v5.setBrightness(240);
				v5.addVertex(cx-8, pos.cy+8, 0);
				v5.addVertex(cx+8, pos.cy+8, 0);
				v5.addVertex(cx+8, pos.cy-8, 0);
				v5.addVertex(cx-8, pos.cy-8, 0);
				v5.draw();

				GL11.glShadeModel(GL11.GL_SMOOTH);

				boolean link = loc.playerLink != null && loc.playerLink.equals(ep.getUniqueID());

				if (link) {
					v5.startDrawing(GL11.GL_LINE_LOOP);
					v5.setBrightness(240);
					double t = (System.currentTimeMillis()/250D)%360;
					double f1 = 0.5+0.5*Math.sin(t);
					double f2 = 0.5+0.5*Math.sin(t+90);
					double f3 = 0.5+0.5*Math.sin(t+180);
					double f4 = 0.5+0.5*Math.sin(t+270);
					int c1 = ReikaColorAPI.mixColors(e.getColor(), 0, (float)f1);
					int c2 = ReikaColorAPI.mixColors(e.getColor(), 0, (float)f2);
					int c3 = ReikaColorAPI.mixColors(e.getColor(), 0, (float)f3);
					int c4 = ReikaColorAPI.mixColors(e.getColor(), 0, (float)f4);
					v5.setColorOpaque_I(c1);
					v5.addVertex(cx-12, pos.cy+12, 0);
					v5.setColorOpaque_I(c2);
					v5.addVertex(cx+12, pos.cy+12, 0);
					v5.setColorOpaque_I(c3);
					v5.addVertex(cx+12, pos.cy-12, 0);
					v5.setColorOpaque_I(c4);
					v5.addVertex(cx-12, pos.cy-12, 0);
					v5.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					int count = loc.getLinkedPylons();
					String s = String.valueOf(count);
					int sw = ChromaFontRenderer.FontType.HUD.renderer.getStringWidth(s);
					ChromaFontRenderer.FontType.HUD.drawString(s, (int)cx+1-sw/2, (int)pos.cy+16, 0xffffff);
				}

				ReikaTextureHelper.bindTerrainTexture();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				v5.startDrawingQuads();
				v5.setColorOpaque_I(e.getColor());
				v5.setBrightness(240);
				v5.addVertexWithUV(cx-8, pos.cy+8, 0, u, dv);
				v5.addVertexWithUV(cx+8, pos.cy+8, 0, du, dv);
				v5.addVertexWithUV(cx+8, pos.cy-8, 0, du, v);
				v5.addVertexWithUV(cx-8, pos.cy-8, 0, u, v);
				v5.draw();

				if (!loc.hasStructure) {
					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					GL11.glDepthMask(false);
					BlendMode.DEFAULT.apply();
					/*
					IIcon ico = ChromaIcons.NOENTER.getIcon();
					u = ico.getMinU();
					v = ico.getMinV();
					du = ico.getMaxU();
					dv = ico.getMaxV();

					v5.startDrawingQuads();
					v5.setColorRGBA_I(0xffffff, 72);
					v5.setBrightness(240);
					int d = 18;
					v5.addVertexWithUV(cx-d, pos.cy+d, 1, u, dv);
					v5.addVertexWithUV(cx+d, pos.cy+d, 1, du, dv);
					v5.addVertexWithUV(cx+d, pos.cy-d, 1, du, v);
					v5.addVertexWithUV(cx-d, pos.cy-d, 1, u, v);
					v5.draw();
					 */

					float lw = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
					GL11.glLineWidth(3.5F*resolution.getScaleFactor());

					ReikaRenderHelper.prepareGeoDraw(true);
					v5.startDrawing(GL11.GL_LINE_LOOP);
					v5.setColorRGBA_I(0xff0000, 72);
					v5.setBrightness(240);
					double r = 15;
					for (double d = 0; d <= 360; d += 5) {
						double a = Math.toRadians(d);
						double dx = cx+r*Math.cos(a);
						double dy = pos.cy+r*Math.sin(a);
						v5.addVertex(dx, dy, 1);
					}
					v5.draw();

					double r2 = r*0.707;
					v5.startDrawing(GL11.GL_LINES);
					v5.setColorRGBA_I(0xff0000, 72);
					v5.setBrightness(240);
					v5.addVertex(cx-r2, pos.cy-r2, 1);
					v5.addVertex(cx+r2, pos.cy+r2, 1);
					v5.draw();
					GL11.glLineWidth(lw);
					GL11.glPopAttrib();
				}

				if (loc.isTurboCharged) {
					GL11.glDepthMask(false);
					BlendMode.ADDITIVEDARK.apply();
					IIcon ico = ChromaIcons.ECLIPSEFLARE.getIcon();
					u = ico.getMinU();
					v = ico.getMinV();
					du = ico.getMaxU();
					dv = ico.getMaxV();

					v5.startDrawingQuads();
					v5.setColorOpaque_I(e.getColor());
					v5.setBrightness(240);
					v5.addVertexWithUV(cx-36, pos.cy+36, -1, u, dv);
					v5.addVertexWithUV(cx+36, pos.cy+36, -1, du, dv);
					v5.addVertexWithUV(cx+36, pos.cy-36, -1, du, v);
					v5.addVertexWithUV(cx-36, pos.cy-36, -1, u, v);
					v5.draw();
				}

				for (Coordinate c2 : loc.getCrystals()) {
					int px = (c2.xCoord-location.xCoord)*4;
					int pz = (c2.zCoord-location.zCoord)*4;
					v5.startDrawingQuads();
					v5.setColorOpaque_I(powerCrystalDiamondColor.getColor(System.currentTimeMillis()+c2.hashCode()/2));
					v5.setBrightness(240);
					v5.addVertexWithUV(cx+px-4, pos.cy+pz+4, 0, u2, dv2);
					v5.addVertexWithUV(cx+px+4, pos.cy+pz+4, 0, du2, dv2);
					v5.addVertexWithUV(cx+px+4, pos.cy+pz-4, 0, du2, v2);
					v5.addVertexWithUV(cx+px-4, pos.cy+pz-4, 0, u2, v2);
					v5.draw();
				}

				FontRenderer fr = ChromaFontRenderer.FontType.HUD.renderer;
				int base = ReikaMathLibrary.intpow2(10, (int)ReikaMathLibrary.logbase(pos.dl, 10));
				int dist = ReikaMathLibrary.roundToNearestX(base, (int)Math.round(pos.dl));
				String unit = ReikaEngLibrary.getSIPrefix(dist);
				String s = String.format("%.0f%sm", ReikaMathLibrary.getThousandBase(dist), unit);
				//ReikaJavaLibrary.pConsole(dl+" >> "+base+" = "+dist+" [[ "+s);
				int d = link ? 4 : 0;
				fr.drawString(s, (int)cx+11+d, (int)pos.cy+11-d, ReikaColorAPI.mixColors(0, e.getColor(), 0.67F));
				fr.drawString(s, (int)cx+10+d, (int)pos.cy+10-d, ReikaColorAPI.mixColors(0xffffff, e.getColor(), 0.8F));

				GL11.glDisable(GL11.GL_TEXTURE_2D);
			}
		}

	}

	private static class BottleneckWarning extends RenderEntry {

		private final boolean isThroughput;
		private final WarningLevels level;
		private final CrystalElement color;

		private BottleneckWarning(WorldLocation loc, CrystalElement e, WarningLevels w, boolean thru) {
			super(loc, 6000);
			level = w;
			isThroughput = thru;
			color = e;
		}

		@Override
		protected void render(Tessellator v5, EntityPlayer ep, RenderPosition pos, int fov, int h, ScaledResolution resolution) {
			//CrystalElement e = color;
			//ReikaJavaLibrary.pConsole(e+": "+c);

			//float u = e.getFaceRune().getMinU();
			//float v = e.getFaceRune().getMinV();
			//float du = e.getFaceRune().getMaxU();
			//float dv = e.getFaceRune().getMaxV();

			if (pos.phi >= 180 && 360-fov > pos.phi) {

			}
			else if (pos.phi < 180 && pos.phi > fov) {

			}
			else { //on screen, in FOV
				double w = resolution.getScaledWidth()/2D;
				double cx = w+1*w*Math.sin(Math.toRadians(pos.phi));
				double cy = pos.cy;
				double s = 8;

				float u = isThroughput ? 0 : 0.5F;
				float du = u+0.5F;
				float v = level.ordinal()/4F;
				float dv = v+0.25F;

				if (isThroughput) {
					cx -= 12;
					cy -= 12;
				}
				else {
					cx += 12;
					cy += 12;
				}

				GL11.glShadeModel(GL11.GL_SMOOTH);
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/pylonbottleneck.png");
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				BlendMode.DEFAULT.apply();
				GL11.glEnable(GL11.GL_BLEND);
				v5.startDrawingQuads();
				int a = 192;
				if (age*2 >= lifespan) {
					a *= 2-age/lifespan;
				}
				v5.setColorRGBA_I(ReikaColorAPI.mixColors(color.getColor(), 0xffffff, 0.675F), a);
				v5.setBrightness(240);
				v5.addVertexWithUV(cx-s, cy+s, 0, u, dv);
				v5.addVertexWithUV(cx+s, cy+s, 0, du, dv);
				v5.addVertexWithUV(cx+s, cy-s, 0, du, v);
				v5.addVertexWithUV(cx-s, cy-s, 0, u, v);
				v5.draw();

				GL11.glDisable(GL11.GL_TEXTURE_2D);
			}

		}
	}

	private static class RenderPosition {

		private double phi;
		private double dl;
		private double cy;

		private RenderPosition(WorldLocation loc, EntityPlayer ep, float yaw, float pitch, int fov, int h) {
			double dx = loc.xCoord+0.5-ep.posX;
			double dy = loc.yCoord+0.5-ep.posY;
			double dz = loc.zCoord+0.5-ep.posZ;

			dl = ReikaMathLibrary.py3d(dx, 0, dz);
			double arel = -Math.toDegrees(Math.atan2(dx, dz));
			double prel = 90-Math.toDegrees(Math.atan2(dy, dl));
			if (arel < 0)
				arel += 360;
			//ReikaJavaLibrary.pConsole(arel, c.zCoord == 1184 && c.xCoord == -1047);
			//ReikaJavaLibrary.pConsole(prel, c.zCoord == 1184 && c.xCoord == -1047);
			phi = arel-yaw;
			double theta = prel-pitch;
			if (phi < 0)
				phi += 360;
			//ReikaJavaLibrary.pConsole(phi, c.zCoord == 1184 && c.xCoord == -1047);
			//ReikaJavaLibrary.pConsole(theta, c.zCoord == 1184 && c.xCoord == -1047);
			cy = h+h*2*Math.sin(Math.toRadians(theta));
		}

	}
}

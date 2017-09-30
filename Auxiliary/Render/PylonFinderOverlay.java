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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.IWG.PylonGenerator;
import Reika.ChromatiCraft.World.IWG.PylonGenerator.PylonEntry;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PylonFinderOverlay {

	public static final PylonFinderOverlay instance = new PylonFinderOverlay();

	private ColorBlendList powerCrystalDiamondColor = new ColorBlendList(200).addAll(ChromaFX.getChromaColorTiles());

	private PylonFinderOverlay() {

	}


	@SubscribeEvent
	public void renderPylonFinderArrow(RenderGameOverlayEvent evt) {
		if (evt.type == ElementType.HELMET) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			if (PylonGenerator.instance.canGenerateIn(ep.worldObj)) {
				if ((ep.getEntityData().hasKey("pylonoverlay") && ep.getEntityData().getLong("pylonoverlay") >= ep.worldObj.getTotalWorldTime()-20) || ChromaItems.FINDER.matchWith(ep.getCurrentEquippedItem())) {
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
					for (int i = 0; i < CrystalElement.elements.length; i++) {
						CrystalElement e = CrystalElement.elements[i];
						PylonEntry c = PylonGenerator.instance.getNearestPylonSpawn(ep.worldObj, ep.posX, ep.posY+ep.getEyeHeight(), ep.posZ, e);
						if (c != null) {
							//ReikaJavaLibrary.pConsole(e+": "+c);
							double dx = c.location.xCoord+0.5-ep.posX;
							double dy = c.location.yCoord+0.5-ep.posY;
							double dz = c.location.zCoord+0.5-ep.posZ;

							float u = e.getFaceRune().getMinU();
							float v = e.getFaceRune().getMinV();
							float du = e.getFaceRune().getMaxU();
							float dv = e.getFaceRune().getMaxV();

							float u2 = ChromaIcons.DIAMOND.getIcon().getMinU();
							float v2 = ChromaIcons.DIAMOND.getIcon().getMinV();
							float du2 = ChromaIcons.DIAMOND.getIcon().getMaxU();
							float dv2 = ChromaIcons.DIAMOND.getIcon().getMaxV();

							double dl = ReikaMathLibrary.py3d(dx, 0, dz);
							double arel = -Math.toDegrees(Math.atan2(dx, dz));
							double prel = 90-Math.toDegrees(Math.atan2(dy, dl));
							if (arel < 0)
								arel += 360;
							//ReikaJavaLibrary.pConsole(arel, c.zCoord == 1184 && c.xCoord == -1047);
							//ReikaJavaLibrary.pConsole(prel, c.zCoord == 1184 && c.xCoord == -1047);
							double phi = arel-yaw;
							double theta = prel-pitch;
							if (phi < 0)
								phi += 360;
							//ReikaJavaLibrary.pConsole(phi, c.zCoord == 1184 && c.xCoord == -1047);
							//ReikaJavaLibrary.pConsole(theta, c.zCoord == 1184 && c.xCoord == -1047);
							int cy = h+(int)(h*2*Math.sin(Math.toRadians(theta)));
							if (phi >= 180 && 360-fov > phi) {
								int cx = 10;
								v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
								v5.setColorRGBA_I(e.getColor(), 96);
								v5.setBrightness(240);
								v5.addVertex(cx+10, cy+10, 0);
								v5.addVertex(cx+10, cy-10, 0);
								v5.addVertex(cx, cy, 0);
								v5.draw();

								v5.startDrawing(GL11.GL_LINE_LOOP);
								v5.setColorOpaque_I(e.getColor());
								v5.setBrightness(240);
								v5.addVertex(cx, cy, 0);
								v5.addVertex(cx+10, cy-10, 0);
								v5.addVertex(cx+10, cy+10, 0);
								v5.draw();
								//left.add(e);

								ReikaTextureHelper.bindTerrainTexture();
								GL11.glEnable(GL11.GL_TEXTURE_2D);
								v5.startDrawingQuads();
								v5.setColorOpaque_I(e.getColor());
								v5.setBrightness(240);
								v5.addVertexWithUV(cx+10, cy+8, 0, u, dv);
								v5.addVertexWithUV(cx+26, cy+8, 0, du, dv);
								v5.addVertexWithUV(cx+26, cy-8, 0, du, v);
								v5.addVertexWithUV(cx+10, cy-8, 0, u, v);
								v5.draw();
								GL11.glDisable(GL11.GL_TEXTURE_2D);
							}
							else if (phi < 180 && phi > fov) {
								int cx = evt.resolution.getScaledWidth()-10;
								v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
								v5.setColorRGBA_I(e.getColor(), 96);
								v5.setBrightness(240);
								v5.addVertex(cx, cy, 0);
								v5.addVertex(cx-10, cy-10, 0);
								v5.addVertex(cx-10, cy+10, 0);
								v5.draw();

								v5.startDrawing(GL11.GL_LINE_LOOP);
								v5.setColorOpaque_I(e.getColor());
								v5.setBrightness(240);
								v5.addVertex(cx, cy, 0);
								v5.addVertex(cx-10, cy-10, 0);
								v5.addVertex(cx-10, cy+10, 0);
								v5.draw();
								//right.add(e);

								ReikaTextureHelper.bindTerrainTexture();
								GL11.glEnable(GL11.GL_TEXTURE_2D);
								v5.startDrawingQuads();
								v5.setColorOpaque_I(e.getColor());
								v5.setBrightness(240);
								v5.addVertexWithUV(cx-26, cy+8, 0, u, dv);
								v5.addVertexWithUV(cx-10, cy+8, 0, du, dv);
								v5.addVertexWithUV(cx-10, cy-8, 0, du, v);
								v5.addVertexWithUV(cx-26, cy-8, 0, u, v);
								v5.draw();
								GL11.glDisable(GL11.GL_TEXTURE_2D);
							}
							else { //on screen, in FOV
								v5.startDrawingQuads();
								v5.setColorRGBA_I(e.getColor(), 32);
								v5.setBrightness(240);
								int w = evt.resolution.getScaledWidth()/2;
								int cx = (int)(w+1*w*Math.sin(Math.toRadians(phi)));
								//ReikaJavaLibrary.pConsole(cx, c.zCoord == 1184 && c.xCoord == -1047);
								v5.addVertex(cx-8, cy+8, 0);
								v5.addVertex(cx+8, cy+8, 0);
								v5.addVertex(cx+8, cy-8, 0);
								v5.addVertex(cx-8, cy-8, 0);
								v5.draw();

								v5.startDrawing(GL11.GL_LINE_LOOP);
								v5.setColorOpaque_I(e.getColor());
								v5.setBrightness(240);
								v5.addVertex(cx-8, cy+8, 0);
								v5.addVertex(cx+8, cy+8, 0);
								v5.addVertex(cx+8, cy-8, 0);
								v5.addVertex(cx-8, cy-8, 0);
								v5.draw();

								ReikaTextureHelper.bindTerrainTexture();
								GL11.glEnable(GL11.GL_TEXTURE_2D);
								v5.startDrawingQuads();
								v5.setColorOpaque_I(e.getColor());
								v5.setBrightness(240);
								v5.addVertexWithUV(cx-8, cy+8, 0, u, dv);
								v5.addVertexWithUV(cx+8, cy+8, 0, du, dv);
								v5.addVertexWithUV(cx+8, cy-8, 0, du, v);
								v5.addVertexWithUV(cx-8, cy-8, 0, u, v);
								v5.draw();

								for (Coordinate c2 : c.getCrystals()) {
									int px = (c2.xCoord-c.location.xCoord)*4;
									int pz = (c2.zCoord-c.location.zCoord)*4;
									v5.startDrawingQuads();
									v5.setColorOpaque_I(powerCrystalDiamondColor.getColor(System.currentTimeMillis()+c2.hashCode()/2));
									v5.setBrightness(240);
									v5.addVertexWithUV(cx+px-4, cy+pz+4, 0, u2, dv2);
									v5.addVertexWithUV(cx+px+4, cy+pz+4, 0, du2, dv2);
									v5.addVertexWithUV(cx+px+4, cy+pz-4, 0, du2, v2);
									v5.addVertexWithUV(cx+px-4, cy+pz-4, 0, u2, v2);
									v5.draw();
								}

								FontRenderer fr = ChromaFontRenderer.FontType.HUD.renderer;
								int base = ReikaMathLibrary.intpow2(10, (int)ReikaMathLibrary.logbase(dl, 10));
								int dist = ReikaMathLibrary.roundToNearestX(base, (int)Math.round(dl));
								String unit = ReikaEngLibrary.getSIPrefix(dist);
								String s = String.format("%.0f%sm", ReikaMathLibrary.getThousandBase(dist), unit);
								//ReikaJavaLibrary.pConsole(dl+" >> "+base+" = "+dist+" [[ "+s);
								fr.drawString(s, cx+11, cy+11, ReikaColorAPI.mixColors(0, e.getColor(), 0.67F));
								fr.drawString(s, cx+10, cy+10, ReikaColorAPI.mixColors(0xffffff, e.getColor(), 0.8F));

								GL11.glDisable(GL11.GL_TEXTURE_2D);
							}
						}
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

					GL11.glPopMatrix();
				}
			}
		}
	}
}

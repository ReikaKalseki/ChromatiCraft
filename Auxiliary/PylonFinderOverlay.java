/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.PylonGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PylonFinderOverlay {

	public static final PylonFinderOverlay instance = new PylonFinderOverlay();

	private PylonFinderOverlay() {

	}


	@SubscribeEvent
	public void renderPylonFinderArrow(RenderGameOverlayEvent evt) {
		if (evt.type == ElementType.HELMET) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			ItemStack is = ep.getCurrentEquippedItem();
			if (is != null && ChromaItems.FINDER.matchWith(is)) {
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
					Coordinate c = PylonGenerator.instance.getNearestPylonSpawn(ep.worldObj, ep.posX, ep.posY+ep.getEyeHeight(), ep.posZ, e);
					if (c != null) {
						//ReikaJavaLibrary.pConsole(e+": "+c);
						double dx = c.xCoord+0.5-ep.posX;
						double dy = c.yCoord+0.5-ep.posY;
						double dz = c.zCoord+0.5-ep.posZ;

						float u = e.getFaceRune().getMinU();
						float v = e.getFaceRune().getMinV();
						float du = e.getFaceRune().getMaxU();
						float dv = e.getFaceRune().getMaxV();

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
						else {
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

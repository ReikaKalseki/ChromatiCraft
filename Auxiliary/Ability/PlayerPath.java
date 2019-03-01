/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Ability;

import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;

import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


class PlayerPath {

	private int maxLength = 32;
	private boolean renderLock = false;
	private LinkedList<DecimalPosition> points = new LinkedList();

	void addPoint(EntityPlayer ep) {
		this.addPoint(ep.posX, ep.posY-1.62, ep.posZ);
	}

	private void addPoint(double x, double y, double z) {
		if (maxLength > 0 && !renderLock) {
			DecimalPosition dec = new DecimalPosition(x, y, z);
			points.addLast(dec);
			if (points.size() > maxLength + 8) {
				points.removeFirst();
			}
		}
	}

	public void setLength(int len) {
		if (len == 0)
			points.clear();
		else if (len < points.size())
			points = new LinkedList(points.subList(points.size()-len, points.size()));
		maxLength = len;
	}

	@SideOnly(Side.CLIENT)
	public void render() {
		if (maxLength > 0) {
			renderLock = true;
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			//GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_CULL_FACE);
			BlendMode.ADDITIVEDARK.apply();
			ReikaRenderHelper.disableEntityLighting();
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glDisable(GL11.GL_LIGHTING);
			//Tessellator.instance.startDrawing(GL11.GL_LINE_STRIP);
			float f = 0;
			int i = 0;
			for (DecimalPosition d : points) {
				if (i < points.size()-8) {
					int c = f >= 0.5F ? ReikaColorAPI.mixColors(0xffffff, 0x00ffff, (f-0.5F)*2) : ReikaColorAPI.mixColors(0x00ffff, 0x0000ff, f*2);
					c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.5F);
					double s = 0.25;
					/*
					//Tessellator.instance.addVertex(d.xCoord, d.yCoord, d.zCoord);
					Tessellator.instance.addVertex(d.xCoord-s, d.yCoord-s, d.zCoord-s);
					Tessellator.instance.addVertex(d.xCoord+s, d.yCoord-s, d.zCoord-s);
					Tessellator.instance.addVertex(d.xCoord+s, d.yCoord+s, d.zCoord-s);
					Tessellator.instance.addVertex(d.xCoord-s, d.yCoord+s, d.zCoord-s);

					Tessellator.instance.addVertex(d.xCoord-s, d.yCoord-s, d.zCoord+s);
					Tessellator.instance.addVertex(d.xCoord+s, d.yCoord-s, d.zCoord+s);
					Tessellator.instance.addVertex(d.xCoord+s, d.yCoord+s, d.zCoord+s);
					Tessellator.instance.addVertex(d.xCoord-s, d.yCoord+s, d.zCoord+s);

					Tessellator.instance.addVertex(d.xCoord-s, d.yCoord-s, d.zCoord-s);
					Tessellator.instance.addVertex(d.xCoord-s, d.yCoord-s, d.zCoord+s);
					Tessellator.instance.addVertex(d.xCoord-s, d.yCoord+s, d.zCoord+s);
					Tessellator.instance.addVertex(d.xCoord-s, d.yCoord+s, d.zCoord-s);

					Tessellator.instance.addVertex(d.xCoord+s, d.yCoord-s, d.zCoord-s);
					Tessellator.instance.addVertex(d.xCoord+s, d.yCoord-s, d.zCoord+s);
					Tessellator.instance.addVertex(d.xCoord+s, d.yCoord+s, d.zCoord+s);
					Tessellator.instance.addVertex(d.xCoord+s, d.yCoord+s, d.zCoord-s);

					Tessellator.instance.addVertex(d.xCoord-s, d.yCoord-s, d.zCoord-s);
					Tessellator.instance.addVertex(d.xCoord+s, d.yCoord-s, d.zCoord-s);
					Tessellator.instance.addVertex(d.xCoord+s, d.yCoord-s, d.zCoord+s);
					Tessellator.instance.addVertex(d.xCoord-s, d.yCoord-s, d.zCoord+s);

					Tessellator.instance.addVertex(d.xCoord-s, d.yCoord+s, d.zCoord-s);
					Tessellator.instance.addVertex(d.xCoord+s, d.yCoord+s, d.zCoord-s);
					Tessellator.instance.addVertex(d.xCoord+s, d.yCoord+s, d.zCoord+s);
					Tessellator.instance.addVertex(d.xCoord-s, d.yCoord+s, d.zCoord+s);
					 */

					IIcon ico = ChromaIcons.FADE.getIcon();

					float u = ico.getMinU();
					float v = ico.getMinV();
					float du = ico.getMaxU();
					float dv = ico.getMaxV();

					GL11.glPushMatrix();

					GL11.glTranslated(d.xCoord-RenderManager.renderPosX, d.yCoord-RenderManager.renderPosY, d.zCoord-RenderManager.renderPosZ);
					//GL11.glTranslated(-271-RenderManager.renderPosX, 8-RenderManager.renderPosY, 157-RenderManager.renderPosZ);

					RenderManager rm = RenderManager.instance;
					GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
					Tessellator.instance.startDrawingQuads();

					Tessellator.instance.setColorOpaque_I(c);

					Tessellator.instance.addVertexWithUV(-s, -s, 0, u, v);
					Tessellator.instance.addVertexWithUV(s, -s, 0, du, v);
					Tessellator.instance.addVertexWithUV(s, s, 0, du, dv);
					Tessellator.instance.addVertexWithUV(-s, s, 0, u, dv);

					Tessellator.instance.draw();

					GL11.glPopMatrix();

					/*
					Tessellator.instance.addVertexWithUV(d.xCoord-s, d.yCoord, d.zCoord-s, u, v);
					Tessellator.instance.addVertexWithUV(d.xCoord+s, d.yCoord, d.zCoord-s, du, v);
					Tessellator.instance.addVertexWithUV(d.xCoord+s, d.yCoord, d.zCoord+s, du, dv);
					Tessellator.instance.addVertexWithUV(d.xCoord-s, d.yCoord, d.zCoord+s, u, dv);

					Tessellator.instance.addVertexWithUV(d.xCoord, d.yCoord-s, d.zCoord-s, u, v);
					Tessellator.instance.addVertexWithUV(d.xCoord, d.yCoord-s, d.zCoord+s, du, v);
					Tessellator.instance.addVertexWithUV(d.xCoord, d.yCoord+s, d.zCoord+s, du, dv);
					Tessellator.instance.addVertexWithUV(d.xCoord, d.yCoord+s, d.zCoord-s, u, dv);
					 */
				}
				f += 1F/(points.size()-8);
				i++;
			}
			ReikaRenderHelper.enableEntityLighting();
			BlendMode.DEFAULT.apply();
			GL11.glPopMatrix();
			GL11.glPopAttrib();
			renderLock = false;
		}
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.awt.Color;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChromaOverlays {

	public static ChromaOverlays instance = new ChromaOverlays();

	private ChromaOverlays() {

	}

	@SubscribeEvent
	public void renderHUD(RenderGameOverlayEvent evt) {
		if (evt.type == ElementType.HELMET) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			ItemStack is = ep.getCurrentEquippedItem();
			if (ChromaItems.TOOL.matchWith(is)) {
				this.renderElementPie(ep);
			}
			this.renderAbilityStatus(ep);
		}
	}

	private void renderAbilityStatus(EntityPlayer ep) {
		ArrayList<Chromabilities> li = Chromabilities.getFrom(ep);
		int i = 0;
		for (Chromabilities c : li) {
			String tex = "Textures/Ability/"+c.name().toLowerCase()+".png";
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, tex);
			Tessellator v5 = Tessellator.instance;
			v5.startDrawingQuads();
			int x = Minecraft.getMinecraft().displayWidth/2-20;
			int y = Minecraft.getMinecraft().displayHeight/4-8-(int)(li.size()/2F*20)+i*20;
			v5.addVertexWithUV(x+0, y+16, 0, 0, 1);
			v5.addVertexWithUV(x+16, y+16, 0, 1, 1);
			v5.addVertexWithUV(x+16, y+0, 0, 1, 0);
			v5.addVertexWithUV(x+0, y+0, 0, 0, 0);
			v5.draw();
			ElementTagCompound tag = c.getTickCost();
			if (tag != null) {
				ReikaTextureHelper.bindTerrainTexture();
				int k = 0;

				int s = tag.tagCount();
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				v5.startDrawingQuads();
				v5.setColorOpaque_I(0x666666);
				int px = x-s*8;
				int py = y+4;
				v5.addVertex(px-1, py+8+1, 0);
				v5.addVertex(x, py+8+1, 0);
				v5.addVertex(x, py-1, 0);
				v5.addVertex(px-1, py-1, 0);

				v5.setColorOpaque_I(0x000000);
				v5.addVertex(px, py+8, 0);
				v5.addVertex(x, py+8, 0);
				v5.addVertex(x, py, 0);
				v5.addVertex(px, py, 0);
				v5.draw();
				GL11.glEnable(GL11.GL_TEXTURE_2D);

				for (CrystalElement e : tag.elementSet()) {
					IIcon ico = e.getFaceRune();
					float u = ico.getMinU();
					float v = ico.getMinV();
					float du = ico.getMaxU();
					float dv = ico.getMaxV();
					v5.startDrawingQuads();
					int dx = x-(k+1)*8;
					int dy = y+4;
					v5.addVertexWithUV(dx+0, dy+8, 0, u, dv);
					v5.addVertexWithUV(dx+8, dy+8, 0, du, dv);
					v5.addVertexWithUV(dx+8, dy+0, 0, du, v);
					v5.addVertexWithUV(dx+0, dy+0, 0, u, v);
					v5.draw();
					k++;
				}
			}
			i++;
		}
	}

	private void renderElementPie(EntityPlayer ep) {
		GL11.glEnable(GL11.GL_BLEND);

		Tessellator v5 = Tessellator.instance;
		int w = 4;
		int r = 32;
		int rb = r;
		int ox = 36;
		int oy = 36;

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/wheelback.png");
		v5.startDrawingQuads();
		v5.setColorOpaque_I(0xffffff);
		v5.addVertexWithUV(oy-r*2, oy+r*2, 0, 0, 1);
		v5.addVertexWithUV(ox+r*2, oy+r*2, 0, 1, 1);
		v5.addVertexWithUV(ox+r*2, oy-r*2, 0, 1, 0);
		v5.addVertexWithUV(ox-r*2, oy-r*2, 0, 0, 0);
		v5.draw();

		GL11.glDisable(GL11.GL_TEXTURE_2D);

		float flag = PlayerElementBuffer.instance.getAndDecrUpgradeTick(ep);

		if (flag > 0) {
			v5.startDrawing(GL11.GL_LINE_LOOP);
			v5.setColorOpaque_I(0xffffff);
			v5.setBrightness(240);
			double tr = r*2*(1-flag);
			if (tr <= r) {
				for (double a = 0; a <= 360; a += 5) {
					double x = ox+tr*Math.cos(Math.toRadians(a));
					double y = oy+tr*Math.sin(Math.toRadians(a));
					//ReikaJavaLibrary.pConsole(x+", "+y);
					v5.addVertex(x, y, 0);
				}
			}
			v5.draw();
		}

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			double min = e.ordinal()*22.5;
			double max = (e.ordinal()+1)*22.5;
			/*
			v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
			v5.setColorOpaque_I(e.getJavaColor().darker().darker().darker().darker().getRGB());
			v5.setBrightness(240);
			for (double a = min; a <= max; a += 2) {
				double x = ox+r*Math.cos(Math.toRadians(a));
				double y = oy+r*Math.sin(Math.toRadians(a));
				//ReikaJavaLibrary.pConsole(x+", "+y);
				v5.addVertex(x, y, 0);
				v5.addVertex(ox, oy, 0);
			}
			v5.draw();
			 */

			v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
			int color = e.getColor();
			if (flag > 0) {
				int red = ReikaColorAPI.getRedFromInteger(color);
				int green = ReikaColorAPI.getGreenFromInteger(color);
				int blue = ReikaColorAPI.getBlueFromInteger(color);
				float[] hsb = Color.RGBtoHSB(red, green, blue, null);
				int deg = (int)((System.currentTimeMillis()/2)%360);
				hsb[2] *= 0.75+0.25*Math.sin(Math.toRadians(deg));
				color = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
			}
			v5.setColorOpaque_I(color);
			v5.setBrightness(240);
			double dr = r*PlayerElementBuffer.instance.getPlayerContent(ep, e)/PlayerElementBuffer.instance.getElementCap(ep);
			for (double a = min; a <= max; a += 2) {
				double x = ox+dr*Math.cos(Math.toRadians(a));
				double y = oy+dr*Math.sin(Math.toRadians(a));
				//ReikaJavaLibrary.pConsole(x+", "+y);
				v5.addVertex(x, y, 0);
				v5.addVertex(ox, oy, 0);
			}
			v5.draw();

			float wide = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
			GL11.glLineWidth(2);
			v5.startDrawing(GL11.GL_LINES);
			v5.setColorOpaque_I(0x000000);
			v5.setBrightness(240);
			for (double a = 0; a < 360; a += 22.5) {
				double x = ox+rb*Math.cos(Math.toRadians(a));
				double y = oy+rb*Math.sin(Math.toRadians(a));
				//ReikaJavaLibrary.pConsole(x+", "+y);
				v5.addVertex(x, y, 0);
				v5.addVertex(ox, oy, 0);
			}
			v5.draw();
			GL11.glLineWidth(wide);
			/*
			v5.startDrawing(GL11.GL_LINE_LOOP);
			v5.setColorOpaque_I(0x000000);
			v5.setBrightness(240);
			for (double a = 0; a <= 360; a += 5) {
				double x = ox+rb*Math.cos(Math.toRadians(a));
				double y = oy+rb*Math.sin(Math.toRadians(a));
				//ReikaJavaLibrary.pConsole(x+", "+y);
				v5.addVertex(x, y, 0);
			}
			v5.draw();
			 */
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/wheelfront2.png");
		v5.startDrawingQuads();
		v5.setColorOpaque_I(0xffffff);
		v5.addVertexWithUV(oy-r*2, oy+r*2, 0, 0, 1);
		v5.addVertexWithUV(ox+r*2, oy+r*2, 0, 1, 1);
		v5.addVertexWithUV(ox+r*2, oy-r*2, 0, 1, 0);
		v5.addVertexWithUV(ox-r*2, oy-r*2, 0, 0, 0);
		v5.draw();
		GL11.glDisable(GL11.GL_BLEND);
	}
}

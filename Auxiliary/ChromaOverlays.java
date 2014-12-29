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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Magic.Interfaces.LumenRequestingTile;
import Reika.ChromatiCraft.Magic.Interfaces.LumenTile;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.PylonGenerator;
import Reika.DragonAPI.Instantiable.Data.Coordinate;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChromaOverlays {

	public static ChromaOverlays instance = new ChromaOverlays();

	private boolean holding = false;
	private int tick = 0;

	private ChromaOverlays() {

	}

	@SubscribeEvent(priority = EventPriority.HIGH) //Not highest because of Dualhotbar
	public void renderHUD(RenderGameOverlayEvent.Pre evt) {
		tick++;
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		ItemStack is = ep.getCurrentEquippedItem();
		if (evt.type == ElementType.HELMET) {
			int gsc = evt.resolution.getScaleFactor();
			if (ChromaItems.TOOL.matchWith(is)) {
				if (!holding)
					this.syncBuffer(ep);
				holding = true;
				this.renderElementPie(ep, gsc);
				this.renderStorageOverlay(ep, gsc);
			}
			else {
				holding = false;
			}
			this.renderAbilityStatus(ep, gsc);
			this.renderPylonAura(ep, gsc);
		}
		else if (evt.type == ElementType.CROSSHAIRS && ChromaItems.TOOL.matchWith(is)) {
			this.renderCustomCrosshair(evt);
		}
		else if (evt.type == ElementType.HEALTH && Chromabilities.HEALTH.enabledOn(ep)) {
			this.renderBoostedHealthBar(evt, ep);
		}
	}

	private void renderPylonAura(EntityPlayer ep, int gsc) {
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			Coordinate c = PylonGenerator.instance.getNearestPylonSpawn(ep.worldObj, ep.posX, ep.posY, ep.posZ, e);
			double dd = c != null ? c.getDistanceTo(ep.posX, ep.posY, ep.posZ) : Double.POSITIVE_INFINITY;
			if (dd < 24) {

			}
		}
	}

	private void renderCustomCrosshair(RenderGameOverlayEvent.Pre evt) {
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/crosshair.png");
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		Tessellator v5 = Tessellator.instance;
		int w = 16;
		int x = evt.resolution.getScaledWidth()/2;
		int y = evt.resolution.getScaledHeight()/2;
		v5.startDrawingQuads();
		double u = (System.currentTimeMillis()/16%64)/64D;
		double du = u+1/64D;
		double v = (System.currentTimeMillis()/128%16)/16D;
		double dv = v+1/16D;
		v5.addVertexWithUV(x-w/2, y+w/2, 0, u, dv);
		v5.addVertexWithUV(x+w/2, y+w/2, 0, du, dv);
		v5.addVertexWithUV(x+w/2, y-w/2, 0, du, v);
		v5.addVertexWithUV(x-w/2, y-w/2, 0, u, v);
		v5.draw();
		BlendMode.DEFAULT.apply();
		//GL11.glDisable(GL11.GL_BLEND);
		evt.setCanceled(true);
	}

	private void renderBoostedHealthBar(RenderGameOverlayEvent.Pre evt, EntityPlayer ep) {
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/health.png");

		Tessellator v5 = Tessellator.instance;
		int h = 9;
		int w = 4;
		int left = evt.resolution.getScaledWidth()/2 - 91;
		int top = evt.resolution.getScaledHeight()-GuiIngameForge.left_height;

		int regen = -1;
		if (ep.isPotionActive(Potion.regeneration)) {
			int rl = ep.getActivePotionEffect(Potion.regeneration).getAmplifier();
			regen = (int)(tick/300D*(1+0.33*rl)%30);
		}

		v5.startDrawingQuads();
		boolean highlight = ep.hurtResistantTime >= 10 && ep.hurtResistantTime / 3 % 2 == 1;
		for (int i = 29; i >= 0; i--) {
			double u = 16/128D+(i*3)/128D;
			double du = u+w/128D;
			double v = 9/128D;
			if (ep.getMaxHealth()-1 < i*2) {
				v = 27/128D;
			}
			double dv = v+h/128D;
			if (highlight)
				v += 18/128D;
			int x = left+i*3;
			int dx = x+w;
			int y = top+0;
			if (i == regen)
				y -= 2;
			int dy = y+h;
			v5.addVertexWithUV(x, dy, 0, u, dv);
			v5.addVertexWithUV(dx, dy, 0, du, dv);
			v5.addVertexWithUV(dx, y, 0, du, v);
			v5.addVertexWithUV(x, y, 0, u, v);

			boolean heart = ep.getHealth()-1 >= i*2;
			if (heart) {
				boolean half = ep.getHealth()-1 == i*2;
				x = left+i*3+1;
				dx = x+w-2;
				y = top+1;
				if (i == regen)
					y -= 2;
				dy = y+h-2;
				u = 17/128D+(i*3)/128D;
				du = u+(w-2)/128D;
				v = 1/128D;
				if (ep.isPotionActive(Potion.poison)) {
					v = 37/128D;
				}
				else if (ep.isPotionActive(Potion.wither)) {
					v = 46/128D;
				}
				dv = v+(h-2)/128D;
				if (half) {
					dx = x+(w-2)/2;
					du = u+(w-2)/(2*128D);
				}
				v5.addVertexWithUV(x, dy, 0, u, dv);
				v5.addVertexWithUV(dx, dy, 0, du, dv);
				v5.addVertexWithUV(dx, y, 0, du, v);
				v5.addVertexWithUV(x, y, 0, u, v);
			}
		}
		v5.draw();

		GuiIngameForge.left_height += h+1;
		ReikaTextureHelper.bindHUDTexture();
		evt.setCanceled(true);
	}

	private void renderStorageOverlay(EntityPlayer ep, int gsc) {
		MovingObjectPosition pos = ReikaPlayerAPI.getLookedAtBlock(ep, 4, false);
		if (pos != null) {
			TileEntity te = ep.worldObj.getTileEntity(pos.blockX, pos.blockY, pos.blockZ);
			if (te instanceof LumenTile) {
				LumenTile lt = (LumenTile)te;
				ElementTagCompound tag = lt.getEnergy();
				if (lt instanceof LumenRequestingTile) {
					LumenRequestingTile lrt = (LumenRequestingTile)lt;
					tag = lrt.getRequestedTotal();
					if (tag == null)
						return;
				}
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_BLEND);

				Tessellator v5 = Tessellator.instance;
				int r = 12;
				int rb = r;
				int ox = Minecraft.getMinecraft().displayWidth/(gsc*2)-r-8;
				int oy = Minecraft.getMinecraft().displayHeight/(gsc*2)-r-8;

				int n = tag.tagCount();
				int i = 0;
				for (CrystalElement e : tag.elementSet()) {
					double min = i*360D/n;
					double max = (i+1)*360D/n;
					double maxe = lt.getMaxStorage(e);
					if (lt instanceof LumenRequestingTile) {
						maxe = ((LumenRequestingTile)lt).getRequestedTotal().getValue(e);
					}

					v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
					int color = ReikaColorAPI.mixColors(e.getColor(), 0, 0.25F);
					v5.setColorOpaque_I(color);
					v5.setBrightness(240);
					for (double a = min; a <= max; a += 2) {
						double x = ox+r*Math.cos(Math.toRadians(a));
						double y = oy+r*Math.sin(Math.toRadians(a));
						//ReikaJavaLibrary.pConsole(x+", "+y);
						v5.addVertex(x, y, 0);
						v5.addVertex(ox, oy, 0);
					}
					v5.draw();

					v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
					color = e.getColor();
					v5.setColorOpaque_I(color);
					v5.setBrightness(240);
					double dr = Math.min(r, r*lt.getEnergy(e)/maxe);
					for (double a = min; a <= max; a += 2) {
						double x = ox+dr*Math.cos(Math.toRadians(a));
						double y = oy+dr*Math.sin(Math.toRadians(a));
						//ReikaJavaLibrary.pConsole(x+", "+y);
						v5.addVertex(x, y, 0);
						v5.addVertex(ox, oy, 0);
					}
					v5.draw();
					i++;
				}

				float wide = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
				GL11.glLineWidth(1);
				if (n > 1) {
					v5.startDrawing(GL11.GL_LINES);
					v5.setColorOpaque_I(0x000000);
					v5.setBrightness(240);
					for (double a = 0; a < 360; a += 360D/n) {
						double x = ox+rb*Math.cos(Math.toRadians(a));
						double y = oy+rb*Math.sin(Math.toRadians(a));
						//ReikaJavaLibrary.pConsole(x+", "+y);
						v5.addVertex(x, y, 0);
						v5.addVertex(ox, oy, 0);
					}
					v5.draw();
				}

				v5.startDrawing(GL11.GL_LINE_LOOP);
				v5.setColorOpaque_I(0x000000);
				v5.setBrightness(240);
				for (double a = 0; a <= 360; a += 5) {
					double x = ox+r*Math.cos(Math.toRadians(a));
					double y = oy+r*Math.sin(Math.toRadians(a));
					//ReikaJavaLibrary.pConsole(x+", "+y);
					v5.addVertex(x, y, 0);
				}
				v5.draw();

				GL11.glLineWidth(2);
				if (n > 1) {
					v5.startDrawing(GL11.GL_LINES);
					v5.setColorRGBA_I(0x000000, 180);
					v5.setBrightness(240);
					for (double a = 0; a < 360; a += 360D/n) {
						double x = ox+rb*Math.cos(Math.toRadians(a));
						double y = oy+rb*Math.sin(Math.toRadians(a));
						//ReikaJavaLibrary.pConsole(x+", "+y);
						v5.addVertex(x, y, 0);
						v5.addVertex(ox, oy, 0);
					}
					v5.draw();
				}

				v5.startDrawing(GL11.GL_LINE_LOOP);
				v5.setColorRGBA_I(0x000000, 180);
				v5.setBrightness(240);
				for (double a = 0; a <= 360; a += 5) {
					double x = ox+r*Math.cos(Math.toRadians(a));
					double y = oy+r*Math.sin(Math.toRadians(a));
					//ReikaJavaLibrary.pConsole(x+", "+y);
					v5.addVertex(x, y, 0);
				}
				v5.draw();

				GL11.glLineWidth(wide);

				GL11.glEnable(GL11.GL_TEXTURE_2D);
				//GL11.glDisable(GL11.GL_BLEND);
				/*
				CrystalElement e = CrystalElement.elements[(int)(System.currentTimeMillis()/500%16)];
				int amt = tag.getValue(e);
				String s = String.format("%.0f%s", ReikaMathLibrary.getThousandBase(amt), ReikaEngLibrary.getSIPrefix(amt));
				Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(s, ox, oy+r/2, ReikaColorAPI.mixColors(e.getColor(), 0xffffff, 0.5F));
				 */
			}
		}

	}

	private void syncBuffer(EntityPlayer ep) {
		ReikaPlayerAPI.syncCustomDataFromClient(ep);
	}

	private void renderAbilityStatus(EntityPlayer ep, int gsc) {
		ArrayList<Chromabilities> li = Chromabilities.getFrom(ep);
		int i = 0;
		for (Chromabilities c : li) {
			String tex = "Textures/Ability/"+c.name().toLowerCase()+".png";
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, tex);
			Tessellator v5 = Tessellator.instance;
			v5.startDrawingQuads();
			int x = Minecraft.getMinecraft().displayWidth/gsc-20;
			int y = Minecraft.getMinecraft().displayHeight/gsc/2-8-(int)(li.size()/2F*20)+i*20;
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

	private void renderElementPie(EntityPlayer ep, int gsc) {
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

		GL11.glEnable(GL11.GL_TEXTURE_2D);

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/wheelfront2.png");
		v5.startDrawingQuads();
		v5.setColorOpaque_I(0xffffff);
		v5.addVertexWithUV(oy-r*2, oy+r*2, 0, 0, 1);
		v5.addVertexWithUV(ox+r*2, oy+r*2, 0, 1, 1);
		v5.addVertexWithUV(ox+r*2, oy-r*2, 0, 1, 0);
		v5.addVertexWithUV(ox-r*2, oy-r*2, 0, 0, 0);
		v5.draw();
		//GL11.glDisable(GL11.GL_BLEND);

		int cap = PlayerElementBuffer.instance.getElementCap(ep);
		String s = "Cap: "+cap;
		FontRenderer f = Minecraft.getMinecraft().fontRenderer;
		ReikaGuiAPI.instance.drawCenteredString(f, s, ox, oy+r+f.FONT_HEIGHT-4, 0xffffff);
	}
}

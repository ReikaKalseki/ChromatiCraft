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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.API.Interfaces.OrePings.OrePingDelegate;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHelper;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Items.Tools.ItemBottleneckFinder.WarningLevels;
import Reika.ChromatiCraft.Items.Tools.ItemKillAuraGun;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemTransitionWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemTransitionWand.TransitionMode;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Magic.Lore.Towers;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager.ProgressElement;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.IWG.PylonGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Event.Client.EntityRenderingLoopEvent;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChromaOverlays {

	public static final ChromaOverlays instance = new ChromaOverlays();

	private boolean holding = false;
	private int tick = 0;

	private static final int PING_LENGTH = 512;
	private static final int FADEIN = 16;
	private static final int FADEOUT = 64;

	private final EnumMap<CrystalElement, Integer> pings = new EnumMap(CrystalElement.class);
	private final EnumMap<CrystalElement, Integer> pingAng = new EnumMap(CrystalElement.class);
	private final EnumMap<CrystalElement, Integer> pingDist = new EnumMap(CrystalElement.class);

	private final ArrayList<FlareMessage> flareMessages = new ArrayList();

	private String structureText = null;
	private long structureTextTick = -1;

	static final double FRONT_TRANSLATE = 930;

	private ChromaOverlays() {

	}

	@SubscribeEvent
	public void renderInWorld(EntityRenderingLoopEvent evt) {
		/*
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();

		GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);

		//ReikaRenderHelper.renderTube(-279, 8, 724, -283, 8, 724, 0xff0000ff, 0xff77aaff, 0.5, 0.5);

		GL11.glPopMatrix();
		GL11.glPopAttrib();
		 */
	}

	@SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true) //Not highest because of Dualhotbar
	public void renderHUD(RenderGameOverlayEvent.Pre evt) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		tick++;
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		ItemStack is = ep.getCurrentEquippedItem();

		boolean renderCursor = true;
		boolean fullScreenRendersActive = FullScreenOverlayRenderer.instance.isRenderingHexGroups();
		if (fullScreenRendersActive)
			renderCursor = false;
		if (evt.type == ElementType.HELMET) {
			if (FullScreenOverlayRenderer.instance.isWashoutActive()) {
				FullScreenOverlayRenderer.instance.renderWashout(evt, tick);
				//evt.setCanceled(true);
				GL11.glPopAttrib();
				GL11.glPopMatrix();
				return;
			}
			renderCursor = !FullScreenOverlayRenderer.instance.renderLoreHexes(evt, tick);
			if (!renderCursor) {
				evt.setCanceled(true);
				GL11.glPopAttrib();
				GL11.glPopMatrix();
				return;
			}
		}

		int gsc = evt.resolution.getScaleFactor();
		if (evt.type == ElementType.HELMET) {
			if (ChromaItems.TOOL.matchWith(is)) {
				MouseoverOverlayRenderer.instance.renderTileOverlays(ep, gsc);
			}

			if (this.isEnergyDisplayTool(is)) {
				if (!holding)
					this.syncBuffer(ep);
				holding = true;
				this.renderElementPie(ep, gsc);
			}
			else {
				Collection<Ability> li = Chromabilities.getFrom(ep);
				if (!li.isEmpty()) {
					this.renderElementWarnings(ep, gsc, li);
				}
				holding = false;
			}

			if (ChromaItems.OREPICK.matchWith(is)) {
				this.renderOreHUD(ep, evt.resolution, is);
			}
			else if (ChromaItems.TRANSITION.matchWith(is)) {
				this.renderTransitionHUD(ep, evt.resolution, is);
			}
			GL11.glPushMatrix();
			GL11.glTranslated(0, 0, FRONT_TRANSLATE);
			this.renderAbilityStatus(ep, gsc);
			GL11.glPopMatrix();
			if (PylonGenerator.instance.canGenerateIn(ep.worldObj) || FullScreenOverlayRenderer.instance.isPylonOverlayForced())
				FullScreenOverlayRenderer.instance.renderPylonAura(ep, gsc);
			this.renderPingOverlays(ep, gsc);
			GL11.glPushMatrix();
			GL11.glTranslated(0, 0, FRONT_TRANSLATE);
			if (!fullScreenRendersActive) {
				ProgressOverlayRenderer.instance.renderProgressOverlays(ep, gsc);
				ProbeInfoOverlayRenderer.instance.renderConnectivityOverlays(ep, gsc);
			}
			this.renderStructureText(ep, gsc);
			this.renderFlareMessages(gsc);
			GL11.glPopMatrix();
		}
		else if (evt.type == ElementType.CROSSHAIRS && (ChromaItems.TOOL.matchWith(is) || !renderCursor)) {
			if (renderCursor)
				this.renderCustomCrosshair(evt.resolution);
			evt.setCanceled(true);
		}
		else if (evt.type == ElementType.CROSSHAIRS && ChromaItems.KILLAURAGUN.matchWith(is)) {
			this.renderKillAuraCrosshair(evt, gsc);
		}
		else if (evt.type == ElementType.HEALTH && Chromabilities.HEALTH.enabledOn(ep)) {
			this.renderBoostedHealthBar(evt, ep);
		}
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	public boolean isWashoutActive() {
		return FullScreenOverlayRenderer.instance.isWashoutActive();
	}

	public void triggerPylonEffect(CrystalElement color) {
		FullScreenOverlayRenderer.instance.triggerPylonEffect(color);
	}

	public void triggerWashout(CrystalElement e) {
		FullScreenOverlayRenderer.instance.triggerWashout(e);
	}

	private boolean isEnergyDisplayTool(ItemStack is) {
		return is != null && (ChromaItems.TOOL.matchWith(is) || is.getItem() instanceof ItemWandBase);
	}

	private void renderStructureText(EntityPlayer ep, int gsc) {
		/*
		structureText = DimensionStructureType.LOCKS.getDisplayText();
		if (structureTextTick < ep.worldObj.getTotalWorldTime()-300)
			structureTextTick = ep.worldObj.getTotalWorldTime();
		 */
		if (structureText != null) {
			int x = Minecraft.getMinecraft().displayWidth/2/gsc;
			int y = Minecraft.getMinecraft().displayHeight/2/gsc;
			float frac = (ep.worldObj.getTotalWorldTime()-structureTextTick+ReikaRenderHelper.getPartialTickTime())/50F;
			ChromaFontRenderer.FontType.GUI.renderer.drawTitleScroll(structureText, x, y, frac, 0xff888888, 0xff3090ff, 0xff000000, 0xffffffff);
			if (frac >= 2) {
				structureText = null;
			}
		}
	}

	private void renderFlareMessages(int gsc) {
		/*
		structureText = DimensionStructureType.LOCKS.getDisplayText();
		if (structureTextTick < ep.worldObj.getTotalWorldTime()-300)
			structureTextTick = ep.worldObj.getTotalWorldTime();
		 */
		int x = 16/gsc;
		int dy = ChromaFontRenderer.FontType.GUI.renderer.FONT_HEIGHT+2;
		int y = (Minecraft.getMinecraft().displayHeight-ChromaFontRenderer.FontType.GUI.renderer.FONT_HEIGHT-16)/gsc-dy*flareMessages.size();
		Iterator<FlareMessage> it = flareMessages.iterator();
		int i = 0;
		while (it.hasNext()) {
			FlareMessage f = it.next();
			f.render(gsc, x, y+i*dy*gsc, 0xffffff);
			y -= dy;
			if (f.update())
				it.remove();
			i++;
		}
	}

	private void renderPingOverlays(EntityPlayer ep, int gsc) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		HashMap<CrystalElement, Integer> map = new HashMap();
		int i = 0;
		int k = 0;


		boolean renderCircle = false;

		for (int c = 0; c < 16; c++) {
			CrystalElement e = CrystalElement.elements[c];

			Integer tick = pings.get(e);
			if (tick != null) {
				float alpha = tick >= (PING_LENGTH-FADEIN) ? (PING_LENGTH-tick)/(float)FADEIN : tick < FADEOUT ? tick/(float)FADEOUT : 1;
				GL11.glColor4f(alpha, alpha, alpha, alpha);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				BlendMode.ADDITIVEDARK.apply();

				FontRenderer fr = ChromaFontRenderer.FontType.HUD.renderer;

				int w = Minecraft.getMinecraft().displayWidth/gsc;
				int h = Minecraft.getMinecraft().displayHeight/gsc;

				int x = w/2;
				int y = h/2/*-12*/; //crosshair misalign is confusing

				double r = h/2*0.75;//0.875;

				int s = 16;
				int d = 40;

				if (!renderCircle) {
					Tessellator v5 = Tessellator.instance;

					/*
					v5.startDrawing(GL11.GL_LINE_STRIP);
					v5.setColorOpaque_I(0xffffff);
					for (int a = 0; a <= 360; a += 5) {
						double ax = r*Math.cos(Math.toRadians(a));
						double ay = r*Math.sin(Math.toRadians(a));
						v5.addVertex(x+ax, y+ay, 0);
						if (a%15 == 0) {
							double dr = r*0.95;
							double dax = dr*Math.cos(Math.toRadians(a));
							double day = dr*Math.sin(Math.toRadians(a));
							v5.addVertex(x+dax, y+day, 0);
							v5.addVertex(x+ax, y+ay, 0);
						}
					}
					v5.draw();

					v5.startDrawing(GL11.GL_LINE_STRIP);
					v5.setColorOpaque_I(0xffffff);
					for (int a = 0; a <= 360; a += 5) {
						double ax = r*0.0625*Math.cos(Math.toRadians(a));
						double ay = r*0.0625*Math.sin(Math.toRadians(a));
						v5.addVertex(x+ax, y+ay, 0);
						if (a%15 == 0) {
							double dr = r*0.0625*1.25;
							double dax = dr*Math.cos(Math.toRadians(a));
							double day = dr*Math.sin(Math.toRadians(a));
							v5.addVertex(x+dax, y+day, 0);
							v5.addVertex(x+ax, y+ay, 0);
						}
					}
					v5.draw();
					 */

					ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/dimping.png");

					v5.startDrawingQuads();
					v5.setBrightness(240);
					v5.setColorOpaque_I(0xa0a0a0);

					v5.addVertexWithUV(x-r, y+r, 0, 0, 1);
					v5.addVertexWithUV(x+r, y+r, 0, 1, 1);
					v5.addVertexWithUV(x+r, y-r, 0, 1, 0);
					v5.addVertexWithUV(x-r, y-r, 0, 0, 0);

					v5.draw();

					renderCircle = true;
				}

				ReikaTextureHelper.bindTerrainTexture();

				double ang = pingAng.get(e);
				double dist = pingDist.get(e);

				double dr = MathHelper.clamp_double(r*Math.pow(dist, 1.5)/1000000D, 2, r);

				double ax = dr*Math.cos(Math.toRadians(ang))-s/2;
				double ay = dr*Math.sin(Math.toRadians(ang))-s/2;

				int dx = x+(int)ax;
				int dy = y+(int)ay;

				ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(dx, dy, e.getGlowRune(), s, s);

				if (ProgressionManager.instance.hasPlayerCompletedStructureColor(ep, e)) {
					ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(dx+8, dy+10, ChromaIcons.CHECK.getIcon(), s/2, s/2);
				}

				BlendMode.DEFAULT.apply();

				int ox = dx+s/2;
				int oy = dy+s/2;

				if (tick > 1) {
					map.put(e, tick-1);
				}
				else {
					pingDist.remove(e);
					pingAng.remove(e);
				}

				i++;
				if (i >= 4) {
					i = 0;
					k++;
				}
			}
		}
		GL11.glPopAttrib();
		pings.clear();
		pings.putAll(map);
	}

	public void addPingOverlay(CrystalElement e, int dist, int ang) {
		pings.put(e, PING_LENGTH);
		pingDist.put(e, dist);
		pingAng.put(e, ang);

		if (true) { //refresh all others
			for (CrystalElement key : pings.keySet())
				pings.put(key, Math.max(pings.get(key), PING_LENGTH-FADEIN));
		}
	}

	public void addStructureText(DimensionStructureType type) {
		String s = type.getDisplayText();
		structureText = s;
		structureTextTick = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
	}

	public void addFlareMessage(String s) {
		flareMessages.add(new FlareMessage(s));
	}

	private void renderTransitionHUD(EntityPlayer ep, ScaledResolution sr, ItemStack is) {
		ItemTransitionWand itw = (ItemTransitionWand)is.getItem();
		ItemStack place = itw.getStoredItem(is);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/transitionhud.png");
		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		int x = 2;
		int y = 2;
		int w = 256;
		int h = 32;
		v5.addVertexWithUV(x, y+h, 0, 0, 1);
		v5.addVertexWithUV(x+w, y+h, 0, 1, 1);
		v5.addVertexWithUV(x+w, y, 0, 1, 0);
		v5.addVertexWithUV(x, y, 0, 0, 0);
		v5.draw();
		x = 8;
		y = 8;
		ReikaGuiAPI.instance.drawItemStack(new RenderItem(), place, x, y);
		TransitionMode mode = itw.getMode(is);
		ChromaFontRenderer.FontType.HUD.renderer.drawString(mode.desc, x+20, y+4, 0xffffff, true);
		GL11.glDisable(GL11.GL_LIGHTING);
	}

	private void renderOreHUD(EntityPlayer ep, ScaledResolution sr, ItemStack is) {
		OrePingDelegate otype = OreOverlayRenderer.instance.getOreTypeByData(is);
		if (otype == null)
			return;
		IIcon ico = otype.getIcon();
		if (ico == null) {
			ChromatiCraft.logger.logError("Ore Ping Delegate "+otype+" for "+otype.getPrimary()+" has a null icon!");
			ico = Blocks.bedrock.blockIcon;
		}
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		Tessellator v5 = Tessellator.instance;
		int s = 16;
		int x = sr.getScaledWidth()/2-s*5/4;
		int y = sr.getScaledHeight()/2-s*5/4;
		ReikaTextureHelper.bindTerrainTexture();
		v5.startDrawingQuads();
		v5.addVertexWithUV(x, y+s, 0, u, dv);
		v5.addVertexWithUV(x+s, y+s, 0, du, dv);
		v5.addVertexWithUV(x+s, y, 0, du, v);
		v5.addVertexWithUV(x, y, 0, u, v);
		v5.draw();
	}

	private void renderCustomCrosshair(ScaledResolution res) {
		ReikaTextureHelper.bindFinalTexture(ChromatiCraft.class, "Textures/crosshair.png");
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		Tessellator v5 = Tessellator.instance;
		int w = 16;
		int x = res.getScaledWidth()/2;
		int y = res.getScaledHeight()/2;
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
	}

	private void renderKillAuraCrosshair(RenderGameOverlayEvent.Pre evt, int gsc) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glAlphaFunc(GL11.GL_GREATER, 1/255F);
		Tessellator v5 = Tessellator.instance;
		double w = Minecraft.getMinecraft().displayWidth/gsc;
		double h = Minecraft.getMinecraft().displayHeight/gsc;
		double z = -1000;

		int n = 4;
		int tick = ItemKillAuraGun.getUseTick();
		double t = (double)n*tick/ItemKillAuraGun.CHARGE_TIME;
		//t++; //basic frame
		int p = MathHelper.ceiling_double_int(t);
		int m = (int)t;

		//ReikaJavaLibrary.pConsole(t+">"+m+">"+p);

		for (int i = -1; i <= p; i++) {
			GL11.glPushMatrix();
			double a = 0;
			if (i == 1 || i == -1) {
				int t2 = ItemKillAuraGun.getUnboundedUseTick();
				if (t2 > ItemKillAuraGun.CHARGE_TIME) {
					t2 = ItemKillAuraGun.CHARGE_TIME+t2%ItemKillAuraGun.CHARGE_TIME;
				}
				a = -Math.pow(t2*0.5, 2);
			}
			if (a != 0) {
				GL11.glTranslated(w/2, h/2, 0);
				GL11.glRotated(a, 0, 0, 1);
				GL11.glTranslated(-w/2, -h/2, 0);
			}
			String i2 = String.valueOf(i);
			if (i == -1)
				i2 = "0b";
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/killlaura_"+i2+".png");
			int alpha = 255;
			float f = (float)(t-m);
			//ReikaJavaLibrary.pConsole(i <= m ? (i+">"+1) : (i+">"+f));
			int color = i <= m ? 0xffffff : ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, f);
			v5.startDrawingQuads();
			v5.setBrightness(240);
			v5.setColorRGBA_I(color, alpha);
			v5.addVertexWithUV(0, h, z, 0, 1);
			v5.addVertexWithUV(w, h, z, 1, 1);
			v5.addVertexWithUV(w, 0, z, 1, 0);
			v5.addVertexWithUV(0, 0, z, 0, 0);
			v5.draw();
			GL11.glPopMatrix();
		}

		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		BlendMode.DEFAULT.apply();
		//GL11.glDisable(GL11.GL_DEPTH_TEST); //turn off depth testing to avoid this occluding other elements

		evt.setCanceled(true);
		GL11.glPopAttrib();
	}

	private void renderBoostedHealthBar(RenderGameOverlayEvent.Pre evt, EntityPlayer ep) {
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/health.png");

		Tessellator v5 = Tessellator.instance;
		int h = 9;
		int w = 4;
		int left = evt.resolution.getScaledWidth()/2 - 91;
		int top = evt.resolution.getScaledHeight()-GuiIngameForge.left_height;

		int regen = -100;
		if (ep.isPotionActive(Potion.regeneration) || ep.isPotionActive(ChromatiCraft.betterRegen)) {
			PotionEffect eff1 = ep.getActivePotionEffect(Potion.regeneration);
			PotionEffect eff2 = ep.getActivePotionEffect(ChromatiCraft.betterRegen);
			int r1 = eff1 != null ? eff1.getAmplifier() : 0;
			int r2 = eff2 != null ? eff2.getAmplifier() : 0;
			int rl = Math.max(r1, r2);
			regen = (int)(System.currentTimeMillis()/120D*(1+0.33*rl)%34)-2;
		}

		v5.startDrawingQuads();
		double boost = AbilityHelper.instance.getBoostedHealth(ep);
		boolean highlight = ep.hurtResistantTime >= 10 && ep.hurtResistantTime / 3 % 2 == 1;
		int health = Math.round(ep.getHealth()/2F);
		int maxhealth = Math.round(ep.getMaxHealth()/2F);
		for (int i = 29; i >= 0; i--) {
			double u = 16/128D+(i*3)/128D;
			double du = u+w/128D;
			double v = 9/128D;
			float f1 = (i/29F);
			float f2 = (float)(boost/5D);
			if (f1 > f2) {
				v = 27/128D;
			}
			int roff = 0;
			if (i == regen+2)
				roff = -1;
			else if (i == regen+1)
				roff = -2;
			else if (i == regen-1)
				roff = 2;
			else if (i == regen-2)
				roff = 1;
			double dv = v+h/128D;
			if (highlight)
				v += 18/128D;
			int x = left+i*3;
			int dx = x+w;
			int y = top+0+roff;
			int dy = y+h;
			v5.addVertexWithUV(x, dy, 0, u, dv);
			v5.addVertexWithUV(dx, dy, 0, du, dv);
			v5.addVertexWithUV(dx, y, 0, du, v);
			v5.addVertexWithUV(x, y, 0, u, v);

			int bars = health*60/maxhealth;

			boolean heart = bars >= i*2+1;
			if (heart) {
				boolean half = bars == i*2+1;
				x = left+i*3+1;
				dx = x+w-2;
				y = top+1+roff;
				dy = y+h-2;
				u = 17/128D+(i*3)/128D;
				du = u+(w-2)/128D;
				v = 1/128D;
				if (f1 > f2) {
					v = 28/128D;
				}
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
		FontRenderer f = ChromaFontRenderer.FontType.HUD.renderer;
		int n = ep.getTotalArmorValue() > 0 ? f.FONT_HEIGHT+1 : 0;
		f.drawString(String.format("Health: %d/%d", health, maxhealth), left, top-f.FONT_HEIGHT-n, 0xffffff);
		ReikaTextureHelper.bindHUDTexture();
		evt.setCanceled(true);
	}

	private void syncBuffer(EntityPlayer ep) {
		ReikaPlayerAPI.syncCustomDataFromClient(ep);
	}

	private void renderAbilityStatus(EntityPlayer ep, int gsc) {
		ArrayList<Ability> li = Chromabilities.getFrom(ep);
		GL11.glColor4f(1, 1, 1, 1);
		int i = 0;
		for (Ability c : li) {
			boolean flag = c.isFunctioningOn(ep);
			ReikaTextureHelper.bindTexture(c.getTextureReferenceClass(), c.getTexturePath(!flag));
			Tessellator v5 = Tessellator.instance;
			v5.startDrawingQuads();
			v5.setColorOpaque_I(0xffffff);
			int x = Minecraft.getMinecraft().displayWidth/gsc-20;
			int y = Minecraft.getMinecraft().displayHeight/gsc/2-8-(int)(li.size()/2F*20)+i*20;
			v5.addVertexWithUV(x+0, y+16, 0, 0, 1);
			v5.addVertexWithUV(x+16, y+16, 0, 1, 1);
			v5.addVertexWithUV(x+16, y+0, 0, 1, 0);
			v5.addVertexWithUV(x+0, y+0, 0, 0, 0);
			v5.draw();
			if (!flag) {
				ReikaTextureHelper.bindTerrainTexture();
				ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x, y, ChromaIcons.NOENTER.getIcon(), 16, 16);
			}
			else {
				ElementTagCompound tag = Chromabilities.getTickCost(c, ep);
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
			}
			i++;
		}
	}

	private int getPieX(int r, int space, int gsc) {
		return ChromaOptions.PIELOC.getValue() < 2 ? r+space : Minecraft.getMinecraft().displayWidth/gsc-r-space;
	}

	private int getPieY(int r, int space, int gsc) {
		return ChromaOptions.PIELOC.getValue()%2 == 0 ? r+space : Minecraft.getMinecraft().displayHeight/gsc-r-space-16;
	}

	private void renderElementWarnings(EntityPlayer ep, int gsc, Collection<Ability> c) {
		HashSet<CrystalElement> li = new HashSet();
		for (Ability a : c) {
			ElementTagCompound tag = Chromabilities.getTickCost(a, ep);
			if (tag != null) {
				for (CrystalElement e : tag.elementSet()) {
					li.add(e);
				}
			}
		}
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();

		Tessellator v5 = Tessellator.instance;
		int w = 4;
		int r = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 48 : 32;
		int rb = r;
		int sp = 4;
		int ox = this.getPieX(r, sp, gsc)-r;
		int oy = this.getPieY(r, sp, gsc)-r;

		int i = 0;
		ReikaTextureHelper.bindTerrainTexture();
		int cap = PlayerElementBuffer.instance.getElementCap(ep);
		v5.startDrawingQuads();
		int s = 16;
		for (CrystalElement e : li) {
			int amt = PlayerElementBuffer.instance.getPlayerContent(ep, e);
			if ((float)amt/cap < 0.125) {
				IIcon ico = e.getGlowRune();
				float u = ico.getMinU();
				float v = ico.getMinV();
				float du = ico.getMaxU();
				float dv = ico.getMaxV();
				double dx = ox+(i%4)*(s);
				double dy = oy+(i/4)*(s);
				v5.addVertexWithUV(dx+0, dy+s, 0, u, dv);
				v5.addVertexWithUV(dx+s, dy+s, 0, du, dv);
				v5.addVertexWithUV(dx+s, dy+0, 0, du, v);
				v5.addVertexWithUV(dx+0, dy+0, 0, u, v);
				i++;
			}
		}
		v5.draw();
		BlendMode.DEFAULT.apply();
		if (i > 0) {
			double dx = ox+(i%4)*(s);
			double dy = oy+(i/4)*(s);
			ReikaTextureHelper.bindFinalTexture(DragonAPICore.class, "Resources/warning.png");
			GL11.glEnable(GL11.GL_BLEND);
			v5.startDrawingQuads();
			v5.setColorRGBA_I(0xffffff, 255);
			v5.addVertexWithUV(dx-2, dy+24-3, 0, 0, 1);
			v5.addVertexWithUV(dx+24-2, dy+24-3, 0, 1, 1);
			v5.addVertexWithUV(dx+24-2, dy-3, 0, 1, 0);
			v5.addVertexWithUV(dx-2, dy-3, 0, 0, 0);
			v5.draw();
			GL11.glDisable(GL11.GL_BLEND);
		}

		//GL11.glDisable(GL11.GL_BLEND);
	}

	private void renderElementPie(EntityPlayer ep, int gsc) {
		GL11.glEnable(GL11.GL_BLEND);

		Tessellator v5 = Tessellator.instance;
		int w = 4;
		int r = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 48 : 32;
		int rb = r;
		int sp = 4;
		int ox = this.getPieX(r, sp, gsc);
		int oy = this.getPieY(r, sp, gsc);

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/wheelback_2.png");
		v5.startDrawingQuads();
		v5.setColorOpaque_I(0xffffff);
		v5.addVertexWithUV(ox-r*2, oy+r*2, 0, 0, 1);
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
				int red = ReikaColorAPI.getRed(color);
				int green = ReikaColorAPI.getGreen(color);
				int blue = ReikaColorAPI.getBlue(color);
				float[] hsb = Color.RGBtoHSB(red, green, blue, null);
				int deg = (int)((System.currentTimeMillis()/2)%360);
				hsb[2] *= 0.75+0.25*Math.sin(Math.toRadians(deg));
				color = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
			}
			v5.setColorOpaque_I(color);
			v5.setBrightness(240);
			int amt = PlayerElementBuffer.instance.getPlayerContent(ep, e);
			int cap = PlayerElementBuffer.instance.getElementCap(ep);
			double b = 2;
			double dr = r*Math.pow((double)amt/cap, 0.675);
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

			IIcon ico = e.getOutlineRune();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			int s = 8;
			double rr = 0.8125*r;
			double dx = ox-s/2+rr*Math.cos(Math.toRadians(11.125+i*22.5));
			double dy = oy-s/2+rr*Math.sin(Math.toRadians(11.125+i*22.5));
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			ReikaTextureHelper.bindTerrainTexture();
			v5.startDrawingQuads();
			v5.addVertexWithUV(dx+0, dy+s, 0, u, dv);
			v5.addVertexWithUV(dx+s, dy+s, 0, du, dv);
			v5.addVertexWithUV(dx+s, dy+0, 0, du, v);
			v5.addVertexWithUV(dx+0, dy+0, 0, u, v);
			v5.draw();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
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
		v5.addVertexWithUV(ox-r*2, oy+r*2, 0, 0, 1);
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

	public void addProgressionNote(ProgressElement p) {
		ProgressOverlayRenderer.instance.addProgressionNote(p);
	}

	public void addLoreNote(EntityPlayer ep, Towers t) {
		FullScreenOverlayRenderer.instance.addLoreNote(ep, t);
	}

	public void addStructurePasswordNote(EntityPlayer ep, int hex) {
		FullScreenOverlayRenderer.instance.addStructurePasswordNote(ep, hex);
	}

	public void addBottleneckWarning(int dim, int x, int y, int z, int level, boolean isThroughput, CrystalElement e) {
		PylonFinderOverlay.instance.addBottleneckWarning(new WorldLocation(dim, x, y, z), e, WarningLevels.list[level], isThroughput);
	}

	private static class FlareMessage {

		private static final int OBF_FRAMES = 25;
		private static final int FRAMES_PER_CHAR = 2;
		private static final int DEOBF_FRAMES = 200;

		private final String text;
		private int age = 0;
		private final int deobfTicks;
		private int charSplit = -1;
		private final int lifespan;

		private FlareMessage(String s) {
			text = s;
			deobfTicks = OBF_FRAMES+FRAMES_PER_CHAR*text.length();
			lifespan = deobfTicks+DEOBF_FRAMES;
		}

		private boolean update() {
			age++;
			if (age >= OBF_FRAMES) {
				if (age >= deobfTicks) {
					charSplit = text.length();
				}
				else {
					charSplit = (age-OBF_FRAMES)/FRAMES_PER_CHAR;
				}
				return age >= lifespan;
			}
			return false;
		}

		private void render(int gsc, int x, int y, int color) {
			int c = 0xffffff;
			if (charSplit >= 0) {
				if (charSplit >= text.length()) {
					float f = (age-deobfTicks)/(float)(lifespan-deobfTicks);
					if (f >= 0.5) {
						c = ReikaColorAPI.mixColors(0xffffff, 0x000000, 1-((f-0.5F)*2));
						c = 0xff000000 | c;
					}
					ChromaFontRenderer.FontType.GUI.renderer.drawString(text, x, y, c);
				}
				else {
					String s1 = text.substring(0, charSplit);
					String s2 = text.substring(charSplit);
					ChromaFontRenderer.FontType.GUI.renderer.drawString(s1, x, y, c);
					int x2 = x+ChromaFontRenderer.FontType.GUI.renderer.getStringWidth(s1);
					ChromaFontRenderer.FontType.OBFUSCATED.renderer.drawString(s2, x2, y, c);
				}
			}
			else {
				ChromaFontRenderer.FontType.OBFUSCATED.renderer.drawString(text, x, y, c);
			}
		}
	}
}

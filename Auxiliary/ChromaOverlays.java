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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ColorDiscovery;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.StructureComplete;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Items.Tools.ItemKillAuraGun;
import Reika.ChromatiCraft.Items.Tools.ItemOrePick;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemTransitionWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemTransitionWand.TransitionMode;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Magic.Interfaces.LumenRequestingTile;
import Reika.ChromatiCraft.Magic.Interfaces.LumenTile;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ProgressElement;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.PylonGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Event.Client.EntityRenderingLoopEvent;
import Reika.DragonAPI.Interfaces.Registry.OreType;
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

	private static final RenderItem itemRender = new RenderItem();

	private boolean holding = false;
	private int tick = 0;

	private final EnumMap<CrystalElement, Float> factors = new EnumMap(CrystalElement.class);

	private static final int PING_LENGTH = 512;
	private static final int FADEIN = 16;
	private static final int FADEOUT = 64;

	private final EnumMap<CrystalElement, Integer> pings = new EnumMap(CrystalElement.class);
	private final EnumMap<CrystalElement, Integer> pingAng = new EnumMap(CrystalElement.class);
	private final EnumMap<CrystalElement, Integer> pingDist = new EnumMap(CrystalElement.class);

	private final TreeMap<ProgressElement, Integer> progressFlags = new TreeMap(new ProgressComparator());

	private String structureText = null;
	private long structureTextTick = -1;

	private static final int WASHOUT_LENGTH = 312;
	private static final int WASHOUT_FACTOR = 2;
	private static final int FLASH_FADE = 4;
	private boolean rehideGui;
	private int washout;
	private CrystalElement washoutColor;

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

		if (evt.type == ElementType.HELMET) {
			if (washout > 0) {
				this.renderWashout(evt);
				//evt.setCanceled(true);

				return;
			}
		}

		int gsc = evt.resolution.getScaleFactor();
		if (evt.type == ElementType.HELMET) {
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
			if (PylonGenerator.instance.canGenerateIn(ep.worldObj))
				this.renderPylonAura(ep, gsc);
			this.renderPingOverlays(ep, gsc);
			GL11.glPushMatrix();
			GL11.glTranslated(0, 0, FRONT_TRANSLATE);
			this.renderProgressOverlays(ep, gsc);
			this.renderStructureText(ep, gsc);
			GL11.glPopMatrix();
		}
		else if (evt.type == ElementType.CROSSHAIRS && ChromaItems.TOOL.matchWith(is)) {
			this.renderCustomCrosshair(evt);
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
		return washout > 0;
	}

	private void renderWashout(RenderGameOverlayEvent.Pre evt) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		BlendMode.DEFAULT.apply();

		int mx = Minecraft.getMinecraft().displayWidth/evt.resolution.getScaleFactor();
		int my = Minecraft.getMinecraft().displayHeight/evt.resolution.getScaleFactor();

		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		v5.setBrightness(240);
		int a = (int)(washout > WASHOUT_LENGTH-FLASH_FADE ? (255F*(WASHOUT_LENGTH-washout)/FLASH_FADE) : 255F*Math.min(1, washout/255F));
		int c1 = ReikaColorAPI.mixColors(washoutColor.getColor(), 0xffffff, 0.5F);
		int c = ReikaColorAPI.mixColors(0xffffff, c1, Math.min(0.95F, a/255F));
		//ReikaJavaLibrary.pConsole(washout+" > A="+a+", cfrac = "+(a/255F)+", C="+Integer.toHexString(c));
		v5.setColorRGBA_I(c, Math.min(255, a));
		v5.addVertex(0, 0, 0);
		v5.addVertex(mx, 0, 0);
		v5.addVertex(mx, my, 0);
		v5.addVertex(0, my, 0);
		v5.draw();

		if (!Minecraft.getMinecraft().isGamePaused()) {
			if (washout >= WASHOUT_LENGTH-FLASH_FADE || tick%WASHOUT_FACTOR == 0)
				washout -= Math.max(1, 90/Math.max(1, ReikaRenderHelper.getFPS()));
			if (washout == 0) {
				if (rehideGui) {
					Minecraft.getMinecraft().gameSettings.hideGUI = true;
				}
			}
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopAttrib();
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

	public void triggerWashout(CrystalElement e) {
		rehideGui = Minecraft.getMinecraft().gameSettings.hideGUI;
		Minecraft.getMinecraft().gameSettings.hideGUI = false;
		washout = Math.max(washout, WASHOUT_LENGTH-FLASH_FADE);
		washoutColor = e;
	}

	private void renderProgressOverlays(EntityPlayer ep, int gsc) {
		HashMap<ProgressElement, Integer> map = new HashMap();
		int dy = 0;
		//ReikaJavaLibrary.pConsole(progressFlags.keySet());
		for (ProgressElement p : progressFlags.keySet()) {
			int tick = progressFlags.get(p);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);

			FontRenderer fr = ChromaFontRenderer.FontType.HUD.renderer;
			int sw = Math.max(40, fr.getStringWidth(p.getTitle()));
			int sh = 24+(fr.listFormattedStringToWidth(p.getShortDesc(), sw*2).size()-1)*4;//24;
			int w = sw+28;//144;
			int h = tick > 800-sh ? 800-tick : tick < sh ? tick : sh;

			int x = Minecraft.getMinecraft().displayWidth/gsc-w-1;

			ReikaGuiAPI.instance.drawRect(x, dy, x+w, dy+h, 0xff444444);
			ReikaGuiAPI.instance.drawRectFrame(x+1, dy+1, w-2, h-2, 0xcccccc);
			ReikaGuiAPI.instance.drawRectFrame(x+2, dy+2, w-4, h-4, 0xcccccc);

			GL11.glEnable(GL11.GL_TEXTURE_2D);

			if (h == sh) {

				fr.drawString(p.getTitle(), x+w-4-sw, dy+8-4, 0xffffff);
				GL11.glPushMatrix();
				double s = 0.5;
				GL11.glScaled(s, s, s);
				GL11.glTranslated(x+16+8, dy+16-1, 0);
				fr.drawSplitString(p.getShortDesc(), x+w-4-sw, dy+8+4, sw*2, 0xffffff);
				GL11.glPopMatrix();

				GL11.glEnable(GL11.GL_LIGHTING);

				p.renderIcon(itemRender, fr, x+4, dy+4);

			}

			GL11.glEnable(GL11.GL_LIGHTING);

			if (tick > 1) {
				map.put(p, tick-(DragonAPICore.debugtest ? 32 : 1));
			}
			dy += h+4;
			if (dy > Minecraft.getMinecraft().displayHeight/gsc-h) {
				//break;
				map.put(p, tick);
			}
		}
		//if (map.isEmpty())
		progressFlags.clear();
		//else
		//	progressFlags.keySet().removeAll(map.keySet());
		progressFlags.putAll(map);
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
		OreType otype = ItemOrePick.getOreTypeByMetadata(is);
		if (otype == null)
			return;
		ItemStack ore = otype.getFirstOreBlock();
		IIcon ico = Block.getBlockFromItem(ore.getItem()).getIcon(0, ore.getItemDamage());
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

	public void triggerPylonEffect(CrystalElement e) {
		factors.put(e, 2F);
	}

	private void renderPylonAura(EntityPlayer ep, int gsc) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		String tex = "Textures/aura-bar-half-grid.png";//ChromaOptions.SMALLAURA.getState() ? "Textures/aura-bar-quarter.png" : "Textures/aura-bar-half.png";
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, tex);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glAlphaFunc(GL11.GL_GREATER, 1/255F);
		Tessellator v5 = Tessellator.instance;
		double w = Minecraft.getMinecraft().displayWidth/gsc;
		double h = Minecraft.getMinecraft().displayHeight/gsc;
		double z = -1000;
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			Coordinate c = PylonGenerator.instance.getNearestPylonSpawn(ep.worldObj, ep.posX, ep.posY, ep.posZ, e);
			double dd = c != null ? c.getDistanceTo(ep.posX, ep.posY, ep.posZ) : Double.POSITIVE_INFINITY;
			if (dd < 32) {
				int step = 40;
				int frame = (int)((System.currentTimeMillis()/step)%20+e.ordinal()*1.25F)%20;
				int imgw = 4;//20;
				int imgh = 5;//1;
				double u = frame%imgw/(double)imgw;
				double du = u+1D/imgw;
				double v = frame/imgw/(double)imgh;
				double dv = v+1D/imgh;
				int alpha = 255;
				float cache = factors.containsKey(e) ? factors.get(e) : 0;
				float bright = Math.min(1, (float)(1.5-dd/24));
				float res = Math.max(cache, bright);
				factors.put(e, cache*0.9975F);
				if (res > 0) {
					int color = ReikaColorAPI.getColorWithBrightnessMultiplier(e.getColor(), Math.min(1, res));
					v5.startDrawingQuads();
					v5.setBrightness(240);
					v5.setColorRGBA_I(color, alpha);
					v5.addVertexWithUV(0, h, z, u, dv);
					v5.addVertexWithUV(w, h, z, du, dv);
					v5.addVertexWithUV(w, 0, z, du, v);
					v5.addVertexWithUV(0, 0, z, u, v);
					v5.draw();
				}
			}
		}
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		BlendMode.DEFAULT.apply();
		//GL11.glDisable(GL11.GL_DEPTH_TEST); //turn off depth testing to avoid this occluding other elements
		GL11.glPopAttrib();
	}

	private void renderCustomCrosshair(RenderGameOverlayEvent.Pre evt) {
		ReikaTextureHelper.bindFinalTexture(ChromatiCraft.class, "Textures/crosshair.png");
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
				int ar = 12;
				int r = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? ar*2 : ar;
				int rb = r;
				int ox = Minecraft.getMinecraft().displayWidth/(gsc*2)-ar-8;
				int oy = Minecraft.getMinecraft().displayHeight/(gsc*2)-ar-8;

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
		ArrayList<Ability> li = Chromabilities.getFrom(ep);
		GL11.glColor4f(1, 1, 1, 1);
		int i = 0;
		for (Ability c : li) {
			ReikaTextureHelper.bindTexture(c.getTextureReferenceClass(), c.getTexturePath(false));
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
			i++;
		}
	}

	private int getPieX(int r, int space, int gsc) {
		return ChromaOptions.PIELOC.getValue() < 2 ? r+space : Minecraft.getMinecraft().displayWidth/gsc-r-space;
	}

	private int getPieY(int r, int space, int gsc) {
		return ChromaOptions.PIELOC.getValue()%2 == 0 ? r+space : Minecraft.getMinecraft().displayHeight/gsc-r-space-16;
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
		progressFlags.put(p, 800);
		//ReikaJavaLibrary.pConsole("Adding "+p+" to map ("+progressFlags.keySet().contains(p)+"), set is "+progressFlags.keySet());
	}

	private static final class ProgressComparator implements Comparator<ProgressElement> {

		/** General order:
			ProgressStage - 0 by ordinal
			ColorDiscovery - 1 by color ordinal
			ResearchLevel - 2 by ordinal
			ChromaResearch - 3 by research level by ordinal
			CastingRecipe - 4 by ID
		 */

		@Override
		public int compare(ProgressElement o1, ProgressElement o2) {
			return this.getIndex(o1)-this.getIndex(o2);
		}

		private int getIndex(ProgressElement e) {
			if (e instanceof ColorDiscovery) {
				return ((ColorDiscovery)e).color.ordinal();
			}
			if (e instanceof StructureComplete) {
				return 500000+((StructureComplete)e).color.ordinal();
			}
			else if (e instanceof ProgressStage) {
				return 1000000+((ProgressStage)e).ordinal();
			}
			else if (e instanceof ResearchLevel) {
				return 2000000+((ResearchLevel)e).ordinal();
			}
			else if (e instanceof ChromaResearch) {
				return 3000000+1000*((ChromaResearch)e).level.ordinal()+((ChromaResearch)e).ordinal();
			}
			else if (e instanceof CastingRecipe) {
				return 3000000+RecipesCastingTable.instance.getIDForRecipe((CastingRecipe)e);
			}
			else
				return -1;
		}

	}
}

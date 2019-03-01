/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.Magic.Lore.LoreScriptRenderer;
import Reika.ChromatiCraft.Magic.Lore.LoreScripts;
import Reika.ChromatiCraft.Magic.Lore.LoreScripts.LoreLine;
import Reika.ChromatiCraft.Magic.Lore.LoreScripts.LorePanel;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary;
import Reika.ChromatiCraft.TileEntity.TileEntityDataNode;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public class InWorldScriptRenderer {

	private static final Random rand = new Random();

	public static void renderPylonScript(TileEntityCrystalPylon te, float par8, Tessellator v5, double sc) {
		int ca = te.getColor().getColor();
		int c1 = ReikaColorAPI.mixColors(ca, 0xffffff, 0.75F);
		int c2 = ReikaColorAPI.mixColors(ca, 0xffffff, 0.5F);

		//GL11.glDisable(GL11.GL_DEPTH_TEST);

		seedRandom(te);

		GL11.glPushMatrix();
		GL11.glTranslated(0, -5-0.225, 0);

		GL11.glPushMatrix();
		ArrayList<LorePanel> li = LoreScripts.ScriptLocations.PYLON.getUniqueRandomPanels(rand, 8);

		for (int r = 0; r < 4; r++) {
			GL11.glPushMatrix();
			GL11.glRotated(90*r, 0, 1, 0);
			GL11.glTranslated(2.5, 0, 2+0.005);
			switch(r) {
				case 1:
					GL11.glTranslated(-1, 0, 0);
					break;
				case 2:
					GL11.glTranslated(-1, 0, -1);
					break;
				case 3:
					GL11.glTranslated(0, 0, -1);
					break;
			}
			GL11.glTranslated(0.25, 0, 0);
			GL11.glScaled(sc, sc, sc);
			int i = 0;
			LorePanel p = li.get(r);
			int w = 4*p.size.maxLength*2;
			double x = 4*(p.size.maxLength+1.5);
			for (int k = 0; k < p.lineCount; k++) {
				LoreLine l = p.getLine(k);
				double tick = -i+0.125*(te.getTicksExisted()+par8);
				float f1 = (float)(0.5+0.5*Math.sin(tick));
				float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
				int c = ReikaColorAPI.mixColors(c1, c2, f1);
				c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
				LoreScriptRenderer.instance.startRendering(true, c);
				LoreScriptRenderer.instance.renderLine(l.text, x, i, w, true);
				LoreScriptRenderer.instance.stopRendering();
				i -= 11;
			}

			i = 0;
			p = li.get(r+1);
			w = 4*p.size.maxLength*2;
			x = 4*(p.size.maxLength+1.5);
			x -= 320;
			for (int k = 0; k < p.lineCount; k++) {
				LoreLine l = p.getLine(k);
				double tick = -i+0.125*(te.getTicksExisted()+par8);
				float f1 = (float)(0.5+0.5*Math.sin(tick));
				float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
				int c = ReikaColorAPI.mixColors(c1, c2, f1);
				c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
				LoreScriptRenderer.instance.startRendering(true, c);
				LoreScriptRenderer.instance.renderLine(l.text, x, i, w, true);
				LoreScriptRenderer.instance.stopRendering();
				i -= 11;
			}
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}

	public static void renderAlvearyScript(TileEntityLumenAlveary te, float par8, Tessellator v5, double sc, double maxDSq) {
		int c1 = 0xFFFFFF;
		int c2 = 0x73DCFF;

		//GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glTranslated(1.5, 2.5, 1.5);

		seedRandom(te);

		GL11.glPushMatrix();
		GL11.glTranslated(-0.5, -0.5, -0.5);
		GL11.glTranslated(0, 0-0.225, 0);

		GL11.glPushMatrix();
		ArrayList<LorePanel> li = LoreScripts.ScriptLocations.ALVEARY.getUniqueRandomPanels(rand, 4);

		double d = Minecraft.getMinecraft().thePlayer.getDistanceSq(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5);
		double da = 1;
		if (d >= maxDSq*0.5) {
			da = 2*(1-d/maxDSq);
		}

		for (int r = 0; r < 4; r++) {
			GL11.glPushMatrix();
			GL11.glRotated(90*r, 0, 1, 0);
			GL11.glTranslated(0, 0, 2+0.005);
			switch(r) {
				case 1:
					GL11.glTranslated(-1, 0, 0);
					break;
				case 2:
					GL11.glTranslated(-1, 0, -1);
					break;
				case 3:
					GL11.glTranslated(0, 0, -1);
					break;
			}
			GL11.glTranslated(0.25, 0, 0);
			GL11.glScaled(sc, sc, sc);
			int i = 0;
			LorePanel p = li.get(r);
			int w = 4*p.size.maxLength*2;
			double x = 4*(p.size.maxLength+1.5)-4;
			for (int k = 0; k < p.lineCount; k++) {
				LoreLine l = p.getLine(k);
				double tick = -i+0.125*(te.getTicksExisted()+par8);
				float f1 = (float)(0.5+0.5*Math.sin(tick));
				float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
				f2 *= da;
				int c = ReikaColorAPI.mixColors(c1, c2, f1);
				c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
				LoreScriptRenderer.instance.startRendering(true, c);
				LoreScriptRenderer.instance.renderLine(l.text, x, i, w, true);
				LoreScriptRenderer.instance.stopRendering();
				i -= 11;
			}
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}

	public static void renderTowerScript(TileEntityDataNode te, float par8, Tessellator v5, double sc, double maxDSq) {
		int c1 = 0xFFFFFF;
		int c2 = 0x73DCFF;

		//GL11.glDisable(GL11.GL_DEPTH_TEST);

		seedRandom(te);

		GL11.glPushMatrix();
		GL11.glTranslated(-0.5, -0.5, -0.5);
		GL11.glTranslated(0, 0-0.225, 0);

		GL11.glPushMatrix();
		ArrayList<LorePanel> li = LoreScripts.ScriptLocations.TOWER.getUniqueRandomPanels(rand, 4);

		double d = Minecraft.getMinecraft().thePlayer.getDistanceSq(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5);
		double da = 1;
		if (d >= maxDSq*0.5) {
			da = 2*(1-d/maxDSq);
		}

		for (int r = 0; r < 4; r++) {
			GL11.glPushMatrix();
			GL11.glRotated(90*r, 0, 1, 0);
			GL11.glTranslated(0, 0, 2+0.005);
			switch(r) {
				case 1:
					GL11.glTranslated(-1, 0, 0);
					break;
				case 2:
					GL11.glTranslated(-1, 0, -1);
					break;
				case 3:
					GL11.glTranslated(0, 0, -1);
					break;
			}
			GL11.glTranslated(0.25, 0, 0);
			GL11.glScaled(sc, sc, sc);
			int i = 0;
			LorePanel p = li.get(r);
			int w = 4*p.size.maxLength*2;
			double x = 4*(p.size.maxLength+1.5);
			for (int k = 0; k < p.lineCount; k++) {
				LoreLine l = p.getLine(k);
				double tick = -i+0.125*(te.getTicksExisted()+par8);
				float f1 = (float)(0.5+0.5*Math.sin(tick));
				float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
				f2 *= da;
				int c = ReikaColorAPI.mixColors(c1, c2, f1);
				c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
				LoreScriptRenderer.instance.startRendering(true, c);
				LoreScriptRenderer.instance.renderLine(l.text, x, i, w, true);
				LoreScriptRenderer.instance.stopRendering();
				i -= 11;
			}
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}

	public static void renderDesertScript(TileEntityStructControl te, float par8, Tessellator v5, double sc) {
		//GL11.glDisable(GL11.GL_DEPTH_TEST);

		seedRandom(te);

		GL11.glPushMatrix();
		GL11.glTranslated(0, 2-0.175, 0);

		GL11.glPushMatrix();

		int c1 = 0xFFAA42;
		int c2 = 0xFFFF70;

		ArrayList<LorePanel> li = LoreScripts.ScriptLocations.DESERT.getUniqueRandomPanels(rand, 4);

		for (int r = 0; r < 4; r++) {
			GL11.glPushMatrix();
			GL11.glRotated(90*r, 0, 1, 0);
			GL11.glTranslated(0, 0, -5+0.005);
			switch(r) {
				case 1:
					GL11.glTranslated(-1, 0, 0);
					break;
				case 2:
					GL11.glTranslated(-1, 0, -1);
					break;
				case 3:
					GL11.glTranslated(0, 0, -1);
					break;
			}
			GL11.glTranslated(0.25, 0, 0);
			GL11.glScaled(sc, sc, sc);
			int i = 0;
			LorePanel p = li.get(r);
			int w = 4*p.size.maxLength*2;
			double x = 4*(p.size.maxLength+1.5);
			for (int k = 0; k < p.lineCount; k++) {
				LoreLine l = p.getLine(k);
				double tick = -i+0.125*(te.getTicksExisted()+par8);
				float f1 = (float)(0.5+0.5*Math.sin(tick));
				float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
				int c = ReikaColorAPI.mixColors(c1, c2, f1);
				c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
				LoreScriptRenderer.instance.startRendering(true, c);
				LoreScriptRenderer.instance.renderLine(l.text, x, i, w, true);
				LoreScriptRenderer.instance.stopRendering();
				i -= 11;
			}
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}

	public static void renderOceanScript(TileEntityStructControl te, float par8, Tessellator v5, double sc) {
		//GL11.glDisable(GL11.GL_DEPTH_TEST);

		seedRandom(te);

		GL11.glPushMatrix();
		GL11.glTranslated(0, 1-0.175, 0);

		GL11.glPushMatrix();
		ArrayList<LorePanel> li = LoreScripts.ScriptLocations.OCEAN.getUniqueRandomPanels(rand, 2);

		int c1 = 0x266EFF;
		int c2 = 0x7199E8;

		for (int r = 2; r < 4; r++) {
			GL11.glPushMatrix();
			GL11.glRotated(90*r, 0, 1, 0);
			GL11.glTranslated(0, 0, -21+0.005);
			switch(r) {
				case 2:
					GL11.glTranslated(-3.5, 0, -1);
					break;
				case 3:
					GL11.glTranslated(2.5, 0, -1);
					break;
			}
			GL11.glTranslated(0.25, 0, 0);
			GL11.glScaled(sc, sc, sc);
			int i = 0;
			LorePanel p = li.get(r-2);
			int w = 4*p.size.maxLength*2;
			double x = 4*(p.size.maxLength+1.5);
			for (int k = 0; k < p.lineCount; k++) {
				LoreLine l = p.getLine(k);
				double tick = -i+0.125*(te.getTicksExisted()+par8);
				float f1 = (float)(0.5+0.5*Math.sin(tick));
				float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
				int c = ReikaColorAPI.mixColors(c1, c2, f1);
				c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
				LoreScriptRenderer.instance.startRendering(true, c);
				LoreScriptRenderer.instance.renderLine(l.text, x, i, w, true);
				LoreScriptRenderer.instance.stopRendering();
				i -= 11;
			}
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}

	public static void renderBurrowScript(TileEntityStructControl te, float par8, Tessellator v5, double sc) {
		//GL11.glDisable(GL11.GL_DEPTH_TEST);

		seedRandom(te);

		GL11.glPushMatrix();
		GL11.glTranslated(0, -1-0.175, 0);

		GL11.glPushMatrix();
		ArrayList<LorePanel> li = LoreScripts.ScriptLocations.BURROW.getUniqueRandomPanels(rand, 3);
		GL11.glTranslated(0, 0, -1+0.005);
		GL11.glTranslated(0.25, 0, 0);
		GL11.glScaled(sc, sc, sc);
		int ca = te.getColor().getColor();
		int cb = ReikaColorAPI.mixColors(ca, 0xffffff, 0.75F);
		int c1 = ReikaColorAPI.getShiftedHue(cb, -8F);
		int c2 = ReikaColorAPI.getShiftedHue(cb, 8F);
		int i = 0;
		LorePanel p = li.get(0);
		int w = 4*p.size.maxLength*2;
		double x = 4*(p.size.maxLength+1.5);
		for (int k = 0; k < p.lineCount; k++) {
			LoreLine l = p.getLine(k);
			double tick = -i+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(l.text, x, i, w, true);
			LoreScriptRenderer.instance.stopRendering();
			i -= 11;
		}
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		//li = ReikaJavaLibrary.makeListFrom("AnotHER 8024", "sh OR t sTG", "a 5 2 hF a 7", "test num 4", "a FIFTH line");
		GL11.glRotated(180, 0, 1, 0);
		//GL11.glTranslated(0, 0, 2-0.005);
		GL11.glTranslated(-0.75, 0, -2+0.005);
		GL11.glScaled(sc, sc, sc);
		i = 0;
		p = li.get(1);
		w = 4*p.size.maxLength*2;
		x = 4*(p.size.maxLength+1.5);
		for (int k = 0; k < p.lineCount; k++) {
			LoreLine l = p.getLine(k);
			double tick = -i-76+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(l.text, x, i, w, true);
			LoreScriptRenderer.instance.stopRendering();
			i -= 11;
		}
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		//li = ReikaJavaLibrary.makeListFrom("type 102 7", "trest the 81", " 23iig kgj", "chAr gROUp", "number FIVE 5");
		GL11.glRotated(90, 0, 1, 0);
		GL11.glTranslated(-0.75, 0, -1+0.005);
		GL11.glScaled(sc, sc, sc);
		i = 0;
		p = li.get(2);
		w = 4*p.size.maxLength*2;
		x = 4*(p.size.maxLength+1.5);
		for (int k = 0; k < p.lineCount; k++) {
			LoreLine l = p.getLine(k);
			double tick = -i+23+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(l.text, x, i, w, true);
			LoreScriptRenderer.instance.stopRendering();
			i -= 11;
		}
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}

	public static void renderCavernScript(TileEntityStructControl te, float par8, Tessellator v5, double sc) {
		//GL11.glDisable(GL11.GL_DEPTH_TEST);

		seedRandom(te);

		GL11.glPushMatrix();
		GL11.glTranslated(0, 3-0.235, 0);

		GL11.glPushMatrix();
		ArrayList<LorePanel> li = LoreScripts.ScriptLocations.CAVERN1.getUniqueRandomPanels(rand, 2);
		GL11.glTranslated(0, 0, -1+0.005);
		GL11.glTranslated(0.25, 0, 0);
		GL11.glScaled(sc, sc, sc);
		int c1 = 0x70f0ff;
		int c2 = 0x22aaff;
		int i = 0;
		LorePanel p = li.get(0);
		int w = 4*p.size.maxLength*2;
		double x = 4*(p.size.maxLength+1.5);
		for (int k = 0; k < p.lineCount; k++) {
			LoreLine l = p.getLine(k);
			double tick = -i+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(l.text, x, i, w, true);
			LoreScriptRenderer.instance.stopRendering();
			i -= 11;
		}
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		//li = ReikaJavaLibrary.makeListFrom("AnotHER 8024", "sh OR t sTG", "a 5 2 hF a 7", "test num 4", "a FIFTH line");
		GL11.glRotated(180, 0, 1, 0);
		//GL11.glTranslated(0, 0, 2-0.005);
		GL11.glTranslated(-0.75, 0, -2+0.005);
		GL11.glScaled(sc, sc, sc);
		i = 0;
		p = li.get(1);
		w = 4*p.size.maxLength*2;
		x = 4*(p.size.maxLength+1.5);
		for (int k = 0; k < p.lineCount; k++) {
			LoreLine l = p.getLine(k);
			double tick = -i-76+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(l.text, x, i, w, true);
			LoreScriptRenderer.instance.stopRendering();
			i -= 11;
		}
		GL11.glPopMatrix();

		GL11.glTranslated(0, -1, 0);

		GL11.glPushMatrix();
		li = LoreScripts.ScriptLocations.CAVERN2.getUniqueRandomPanels(rand, 2);
		GL11.glTranslated(0, 0, -2+0.005);
		GL11.glTranslated(0.25+2.5, 0, 0);
		GL11.glScaled(sc, sc, sc);
		i = 0;
		p = li.get(0);
		w = 4*p.size.maxLength*2;
		x = 4*(p.size.maxLength+1.5);
		for (int k = 0; k < p.lineCount; k++) {
			LoreLine l = p.getLine(k);
			double tick = -i+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(l.text, x, i, w, true);
			LoreScriptRenderer.instance.stopRendering();
			i -= 11;
		}
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		//li = ReikaJavaLibrary.makeListFrom("AnotHER 8024", "sh OR t sTG", "a 5 2 hF a 7", "test num 4", "a FIFTH line");
		GL11.glRotated(180, 0, 1, 0);
		//GL11.glTranslated(0, 0, 2-0.005);
		GL11.glTranslated(-0.75-2.5, 0, -3+0.005);
		GL11.glScaled(sc, sc, sc);
		i = 0;
		p = li.get(1);
		w = 4*p.size.maxLength*2;
		x = 4*(p.size.maxLength+1.5);
		for (int k = 0; k < p.lineCount; k++) {
			LoreLine l = p.getLine(k);
			double tick = -i-76+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(l.text, x, i, w, true);
			LoreScriptRenderer.instance.stopRendering();
			i -= 11;
		}
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		li = LoreScripts.ScriptLocations.CAVERN3.getUniqueRandomPanels(rand, 2);
		GL11.glTranslated(0, 0, -2+0.005);
		GL11.glTranslated(0.25-2.5, 0, 0);
		GL11.glScaled(sc, sc, sc);
		i = 0;
		p = li.get(0);
		w = 4*p.size.maxLength*2;
		x = 4*(p.size.maxLength+1.5);
		for (int k = 0; k < p.lineCount; k++) {
			LoreLine l = p.getLine(k);
			double tick = -i+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(l.text, x, i, w, true);
			LoreScriptRenderer.instance.stopRendering();
			i -= 11;
		}
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		//li = ReikaJavaLibrary.makeListFrom("AnotHER 8024", "sh OR t sTG", "a 5 2 hF a 7", "test num 4", "a FIFTH line");
		GL11.glRotated(180, 0, 1, 0);
		//GL11.glTranslated(0, 0, 2-0.005);
		GL11.glTranslated(-0.75+2.5, 0, -3+0.005);
		GL11.glScaled(sc, sc, sc);
		i = 0;
		p = li.get(1);
		w = 4*p.size.maxLength*2;
		x = 4*(p.size.maxLength+1.5);
		for (int k = 0; k < p.lineCount; k++) {
			LoreLine l = p.getLine(k);
			double tick = -i-76+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(l.text, x, i, w, true);
			LoreScriptRenderer.instance.stopRendering();
			i -= 11;
		}
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}

	private static void seedRandom(TileEntity te) {
		rand.setSeed(te.xCoord+(te.yCoord << 16)+(te.zCoord << 8));
	}
}

package Reika.ChromatiCraft.Render;

import java.util.ArrayList;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Magic.Lore.LoreScriptRenderer;
import Reika.ChromatiCraft.TileEntity.TileEntityDataNode;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class InWorldScriptRenderer {

	public static void renderPylonScript(TileEntityCrystalPylon te, float par8, Tessellator v5, double sc) {
		int ca = te.getColor().getColor();
		int c1 = ReikaColorAPI.mixColors(ca, 0xffffff, 0.75F);
		int c2 = ReikaColorAPI.mixColors(ca, 0xffffff, 0.5F);

		//GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glPushMatrix();
		GL11.glTranslated(0, -5-0.225, 0);

		GL11.glPushMatrix();
		ArrayList<String> li = ReikaJavaLibrary.makeListFrom("Only three 3", "LINES this", "time OK?", "actualy YOU can", "have FIVE 5");

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
			int w = 0;
			double x = 0;
			for (String s : li) {
				w = Math.max(w, 4*s.length()*2);
				x = Math.max(x, 4*(s.length()+1.5));
			}
			for (String s : li) {
				double tick = -i+0.125*(te.getTicksExisted()+par8);
				float f1 = (float)(0.5+0.5*Math.sin(tick));
				float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
				int c = ReikaColorAPI.mixColors(c1, c2, f1);
				c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
				LoreScriptRenderer.instance.startRendering(true, c);
				LoreScriptRenderer.instance.renderLine(s, x, i, w, true);
				LoreScriptRenderer.instance.stopRendering();
				i -= 11;
			}

			i = 0;
			x -= 320;
			for (String s : li) {
				double tick = -i+0.125*(te.getTicksExisted()+par8);
				float f1 = (float)(0.5+0.5*Math.sin(tick));
				float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
				int c = ReikaColorAPI.mixColors(c1, c2, f1);
				c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
				LoreScriptRenderer.instance.startRendering(true, c);
				LoreScriptRenderer.instance.renderLine(s, x, i, w, true);
				LoreScriptRenderer.instance.stopRendering();
				i -= 11;
			}
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}

	public static void renderTowerScript(TileEntityDataNode te, float par8, Tessellator v5, double sc) {
		int c1 = 0xFFFFFF;
		int c2 = 0x73DCFF;

		//GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glPushMatrix();
		GL11.glTranslated(-0.5, -0.5, -0.5);
		GL11.glTranslated(0, 0-0.225, 0);

		GL11.glPushMatrix();
		ArrayList<String> li = ReikaJavaLibrary.makeListFrom("Only three THAT iS 3 32", "LINES this", "time OK?", "actually YOU can fitand", "have FIVE 5 lines cause");

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
			int w = 0;
			double x = 0;
			for (String s : li) {
				w = Math.max(w, 4*s.length()*2);
				x = Math.max(x, 4*(s.length()+1.5));
			}
			for (String s : li) {
				double tick = -i+0.125*(te.getTicksExisted()+par8);
				float f1 = (float)(0.5+0.5*Math.sin(tick));
				float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
				int c = ReikaColorAPI.mixColors(c1, c2, f1);
				c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
				LoreScriptRenderer.instance.startRendering(true, c);
				LoreScriptRenderer.instance.renderLine(s, x, i, w, true);
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
		GL11.glPushMatrix();
		GL11.glTranslated(0, 2-0.175, 0);

		GL11.glPushMatrix();
		ArrayList<String> li = ReikaJavaLibrary.makeListFrom("Testing THIS strinG ok..", "2 POTATOES. more words.", "27 tiMes OK? ok fiNE", "fourth LINE", "line 5 five LONGER LIne", "I can FIT", "probABLY almoST", "TWEnty LiNEs hERe.", "thaT is A LOT", "and ITs onLY", "HAlf of THe", "DESerT stRUCtuREs", "CAPaciTy ok.", "we NEED aBOuT", "SIX more 6 lINEs", "There DONe?", "onE mORe THere WE go noW");

		int c1 = 0xFFAA42;
		int c2 = 0xFFFF70;

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
			int w = 0;
			double x = 0;
			for (String s : li) {
				w = Math.max(w, 4*s.length()*2);
				x = Math.max(x, 4*(s.length()+1.5));
			}
			for (String s : li) {
				double tick = -i+0.125*(te.getTicksExisted()+par8);
				float f1 = (float)(0.5+0.5*Math.sin(tick));
				float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
				int c = ReikaColorAPI.mixColors(c1, c2, f1);
				c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
				LoreScriptRenderer.instance.startRendering(true, c);
				LoreScriptRenderer.instance.renderLine(s, x, i, w, true);
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
		GL11.glPushMatrix();
		GL11.glTranslated(0, 1-0.175, 0);

		GL11.glPushMatrix();
		ArrayList<String> li = ReikaJavaLibrary.makeListFrom("Testing THIS strinG ok.. but IT needS to BE LOnger. like this???", "I have 2 POTATOES. more words. HOW ABOUT MORE potatoes. YAYYy", "27 tiMes OK? ok fiNE", "fourth LINE", "line 5 five LONGER LIne", "I can FIT", "probABLY almoST", "TWEnty LiNEs hERe.", "thaT is A LOT", "and ITs onLY", "HAlf of THe", "DESerT stRUCtuREs", "CAPaciTy ok.", "we NEED aBOuT", "SIX more 6 lINEs", "There DONe?", "onE mORe THere WE go noW");

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
			int w = 0;
			double x = 0;
			for (String s : li) {
				w = Math.max(w, 4*s.length()*2);
				x = Math.max(x, 4*(s.length()+1.5));
			}
			for (String s : li) {
				double tick = -i+0.125*(te.getTicksExisted()+par8);
				float f1 = (float)(0.5+0.5*Math.sin(tick));
				float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
				int c = ReikaColorAPI.mixColors(c1, c2, f1);
				c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
				LoreScriptRenderer.instance.startRendering(true, c);
				LoreScriptRenderer.instance.renderLine(s, x, i, w, true);
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
		GL11.glPushMatrix();
		GL11.glTranslated(0, -1-0.175, 0);

		GL11.glPushMatrix();
		ArrayList<String> li = ReikaJavaLibrary.makeListFrom("Testing THIS strinG ok..", "2 POTATOES. more words.", "27 tiMes OK? ok fiNE", "fourth LINE", "line 5 five LONGER LIne");
		GL11.glTranslated(0, 0, -1+0.005);
		GL11.glTranslated(0.25, 0, 0);
		GL11.glScaled(sc, sc, sc);
		int ca = te.getColor().getColor();
		int cb = ReikaColorAPI.mixColors(ca, 0xffffff, 0.75F);
		int c1 = ReikaColorAPI.getShiftedHue(cb, -8F);
		int c2 = ReikaColorAPI.getShiftedHue(cb, 8F);
		int i = 0;
		int w = 0;
		double x = 0;
		for (String s : li) {
			w = Math.max(w, 4*s.length()*2);
			x = Math.max(x, 4*(s.length()+1.5));
		}
		for (String s : li) {
			double tick = -i+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(s, x, i, w, true);
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
		w = 0;
		x = 0;
		for (String s : li) {
			w = Math.max(w, 4*s.length()*2);
			x = Math.max(x, 4*(s.length()+1.5));
		}
		for (String s : li) {
			double tick = -i-76+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(s, x, i, w, true);
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
		w = 0;
		x = 0;
		for (String s : li) {
			w = Math.max(w, 4*s.length()*2);
			x = Math.max(x, 4*(s.length()+1.5));
		}
		for (String s : li) {
			double tick = -i+23+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(s, x, i, w, true);
			LoreScriptRenderer.instance.stopRendering();
			i -= 11;
		}
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}

	public static void renderCavernScript(TileEntityStructControl te, float par8, Tessellator v5, double sc) {
		//GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glPushMatrix();
		GL11.glTranslated(0, 3-0.235, 0);

		GL11.glPushMatrix();
		ArrayList<String> li = ReikaJavaLibrary.makeListFrom("Testing THIS strinG ok.. its LONGer now?", "2 POTATOES. more words.", "27 tiMes OK? ok fiNE", "fourth LINE", "line 5 five LONGER LIne");
		GL11.glTranslated(0, 0, -1+0.005);
		GL11.glTranslated(0.25, 0, 0);
		GL11.glScaled(sc, sc, sc);
		int c1 = 0x70f0ff;
		int c2 = 0x22aaff;
		int i = 0;
		int w = 0;
		double x = 0;
		for (String s : li) {
			w = Math.max(w, 4*s.length()*2);
			x = Math.max(x, 4*(s.length()+1.5));
		}
		for (String s : li) {
			double tick = -i+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(s, x, i, w, true);
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
		w = 0;
		x = 0;
		for (String s : li) {
			w = Math.max(w, 4*s.length()*2);
			x = Math.max(x, 4*(s.length()+1.5));
		}
		for (String s : li) {
			double tick = -i-76+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(s, x, i, w, true);
			LoreScriptRenderer.instance.stopRendering();
			i -= 11;
		}
		GL11.glPopMatrix();

		GL11.glTranslated(0, -1, 0);

		GL11.glPushMatrix();
		li = ReikaJavaLibrary.makeListFrom("short lines", "and WORDS", "ONly oK", "HeRe? 2766 char.", "another LINE", "and it JUST", "kEePs ON GOing", "UP TO A", "MAXimUM of", "tweLVE 12 LINES", "i thINk?", "will thiS FIT?");
		GL11.glTranslated(0, 0, -2+0.005);
		GL11.glTranslated(0.25+2.5, 0, 0);
		GL11.glScaled(sc, sc, sc);
		i = 0;
		w = 0;
		x = 0;
		for (String s : li) {
			w = Math.max(w, 4*s.length()*2);
			x = Math.max(x, 4*(s.length()+1.5));
		}
		for (String s : li) {
			double tick = -i+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(s, x, i, w, true);
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
		w = 0;
		x = 0;
		for (String s : li) {
			w = Math.max(w, 4*s.length()*2);
			x = Math.max(x, 4*(s.length()+1.5));
		}
		for (String s : li) {
			double tick = -i-76+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(s, x, i, w, true);
			LoreScriptRenderer.instance.stopRendering();
			i -= 11;
		}
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		li = ReikaJavaLibrary.makeListFrom("short lines", "and WORDS", "ONly oK", "HeRe? 2766 char.", "another LINE", "and it JUST", "kEePs ON GOing", "UP TO A", "MAXimUM of", "tweLVE 12 LINES", "i thINk?", "buT tHiS time", "ITS More.", "ALl thE wAY", "doWn TO thE", "floor.", "last LINE 288");
		GL11.glTranslated(0, 0, -2+0.005);
		GL11.glTranslated(0.25-2.5, 0, 0);
		GL11.glScaled(sc, sc, sc);
		i = 0;
		w = 0;
		x = 0;
		for (String s : li) {
			w = Math.max(w, 4*s.length()*2);
			x = Math.max(x, 4*(s.length()+1.5));
		}
		for (String s : li) {
			double tick = -i+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(s, x, i, w, true);
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
		w = 0;
		x = 0;
		for (String s : li) {
			w = Math.max(w, 4*s.length()*2);
			x = Math.max(x, 4*(s.length()+1.5));
		}
		for (String s : li) {
			double tick = -i-76+0.125*(te.getTicksExisted()+par8);
			float f1 = (float)(0.5+0.5*Math.sin(tick));
			float f2 = (float)Math.min(1, (0.75+0.25*Math.sin(tick*0.25-4.7)+0.03125*Math.sin(tick*5-1)));
			int c = ReikaColorAPI.mixColors(c1, c2, f1);
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, f2);
			LoreScriptRenderer.instance.startRendering(true, c);
			LoreScriptRenderer.instance.renderLine(s, x, i, w, true);
			LoreScriptRenderer.instance.stopRendering();
			i -= 11;
		}
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}
}

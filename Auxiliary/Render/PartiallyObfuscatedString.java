package Reika.ChromatiCraft.Auxiliary.Render;

import java.util.Arrays;

import net.minecraft.client.gui.FontRenderer;

import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;

public abstract class PartiallyObfuscatedString {

	public final String text;

	public PartiallyObfuscatedString(String s) {
		text = s;
	}

	public abstract void render(int x, int y, int c);

	public static class BasicObfuscatedString extends PartiallyObfuscatedString {

		private final boolean[] obfuscate;
		private int obfuscationCount = 0;

		/** Set to nonzero to override char spacing */
		public int monospace = 0;

		public BasicObfuscatedString(String s) {
			super(s);
			obfuscate = new boolean[s.length()];
		}

		@Override
		public void render(int x, int y, int c) {
			if (obfuscationCount == text.length()) {
				ChromaFontRenderer.FontType.OBFUSCATED.renderer.drawString(text, x, y, c);
			}
			else if (obfuscationCount == 0) {
				ChromaFontRenderer.FontType.GUI.renderer.drawString(text, x, y, c);
			}
			else {
				int dx = x;
				char[] arr = text.toCharArray();
				for (int i = 0; i < arr.length; i++) {
					String s = String.valueOf(arr[i]);
					FontRenderer fr = obfuscate[i] ? ChromaFontRenderer.FontType.OBFUSCATED.renderer : ChromaFontRenderer.FontType.GUI.renderer;
					fr.drawString(s, dx, y, c);
					dx += monospace > 0 ? monospace : fr.getStringWidth(s);
				}
			}
		}

		public void setObf(int idx, boolean obf) {
			boolean was = obfuscate[idx];
			obfuscate[idx] = obf;
			if (was != obf) {
				if (obf) {
					obfuscationCount++;
				}
				else {
					obfuscationCount--;
				}
			}
		}

		public void cycleRight(boolean smear) {
			boolean[] cp = smear ? Arrays.copyOf(obfuscate, obfuscate.length) : null;
			ReikaArrayHelper.cycleArray(obfuscate, false);
			if (cp != null) {
				for (int i = 0; i < cp.length; i++) {
					if (cp[i])
						obfuscate[i] = true;
				}
			}
			obfuscationCount = ReikaArrayHelper.countTrue(obfuscate);
		}

		public void cycleLeft(boolean smear) {
			boolean[] cp = smear ? Arrays.copyOf(obfuscate, obfuscate.length) : null;
			ReikaArrayHelper.cycleArrayReverse(obfuscate, false);
			if (cp != null) {
				for (int i = 0; i < cp.length; i++) {
					if (cp[i])
						obfuscate[i] = true;
				}
			}
			obfuscationCount = ReikaArrayHelper.countTrue(obfuscate);
		}
	}

}

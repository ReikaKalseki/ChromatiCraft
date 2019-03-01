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

import java.awt.image.BufferedImage;
import java.util.HashSet;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.client.event.GuiOpenEvent;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.IO.DelegateFontRenderer;
import Reika.DragonAPI.IO.ReikaImageLoader;
import Reika.DragonAPI.Instantiable.Rendering.BasicFontRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChromaFontRenderer extends BasicFontRenderer {

	private final FontType type;
	//private String currentString;
	private final int[] offsets;
	private long lastReload = 0;

	private char preChar;
	private char postChar;

	private ChromaFontRenderer(FontType f) {
		super(false);
		offsets = new int[charWidth.length];
		type = f;
		//parseBBCode = true;
	}

	@Override
	protected float renderCharFraction(int charIndex, boolean italic, float fraction) {
		if (type == FontType.OBFUSCATED) {
			//charIndex = (charWidth.length+charIndex/*+(int)posX/16%16-(int)posY/16%16*/+offsets[charIndex])%charWidth.length;
			charIndex += offsets[charIndex];
			int min = getCharGridIndex(' ');
			int max = getCharGridIndex('~');
			if (preChar != 0)
				charIndex -= getCharGridIndex(preChar);
			if (postChar != 0)
				charIndex += getCharGridIndex(postChar);
			charIndex += currentString.hashCode();
			charIndex = min+(charWidth.length+charIndex%charWidth.length)%(max-min+1);
		}
		return super.renderCharFraction(charIndex, italic, fraction);
	}

	@Override
	protected void bindTexture() {
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, type.texture);
	}

	@Override
	protected boolean needsGLBlending() {
		return true;
	}

	@Override
	protected boolean renderCharInString(String sg, int idx, boolean shadow) {
		if (type == FontType.OBFUSCATED) {
			preChar = idx > 0 ? sg.charAt(idx-1) : 0;
			postChar = idx < sg.length()-1 ? sg.charAt(idx+1) : 0;
		}
		return super.renderCharInString(sg, idx, shadow);
	}

	@Override
	protected void onReload() {
		super.onReload();
		this.rerandomize();
	}

	public void rerandomize() {
		for (int i = 0; i < offsets.length; i++) {
			offsets[i] = ReikaRandomHelper.getRandomPlusMinus(0, 4);
		}
	}

	@SubscribeEvent
	public void onOpenGui(GuiOpenEvent evt) {
		this.rerandomize();
	}

	@Override
	protected void readFontTexture() {
		if (System.currentTimeMillis()-lastReload < 250)
			return;
		BufferedImage buf = ReikaImageLoader.readImage(ChromatiCraft.class, type.texture, null);
		int w = buf.getWidth();
		int h = buf.getHeight();
		int[] rgb = new int[w*h];
		buf.getRGB(0, 0, w, h, rgb, 0, w);
		int slotHeight = h/16;
		int slotWidth = w/16;
		byte b0 = 1;
		float f = 8F/slotWidth;
		int index = 0;

		while (index < 256) {
			int col = index%16;
			int row = index/16;

			if (index == 32)
				charWidth[index] = 3+b0;

			int xOffset = slotWidth-1;

			while (true) {
				if (xOffset >= 0) {
					int xPos = col*slotWidth+xOffset;
					boolean flag = true;

					for (int yOffset = 0; yOffset < slotHeight && flag; yOffset++) {
						int yPosByCol = (row*slotWidth+yOffset)*w;
						int color = rgb[xPos+yPosByCol];
						if ((color >> 24 & 255) != 0) //alpha > 0
							//if (ReikaColorAPI.isRGBNonZero(color))
							flag = false;
					}

					if (flag) {
						xOffset--;
						continue;
					}
				}

				xOffset++;
				charWidth[index] = (int)(0.5D+xOffset*f)+b0;
				index++;
				break;
			}
		}
		lastReload = System.currentTimeMillis();
	}

	public void drawTitleScroll(String sg, int x, int y, float fraction, int c1, int c2, int cflash1, int cflash2) {
		boolean over = fraction >= 1+1F/sg.length();
		fraction = Math.min(1, fraction);

		float s = 2;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glScaled(s, s, s);

		ChromaFontRenderer fr = ChromaFontRenderer.FontType.GUI.renderer;

		float sx = x/s-fr.getStringWidth(sg)/2F;
		float sy = y/s-fr.FONT_HEIGHT/2F;

		for (int d = 2; d <= 6; d++) {
			int fade = 0x70/d << 24;
			int c1o = (c1 & 0xffffff) | fade;
			int c2o = (c2 & 0xffffff) | fade;
			int cflash1o = (cflash1 & 0xffffff) | fade;
			int cflash2o = (c2 & 0xffffff) | fade;
			this.drawTitleText(sg, sx+d/s, sy+d/s, fraction, over, c1o, c2o, cflash1o, cflash2o);
		}

		this.drawTitleText(sg, sx, sy, fraction, over, c1, c2, cflash1, cflash2);

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private void drawTitleText(String sg, float sx, float sy, float fraction, boolean over, int c1, int c2, int cflash1, int cflash2) {
		ChromaFontRenderer fr = ChromaFontRenderer.FontType.GUI.renderer;

		/*
		int len = sg.length();//ReikaStringParser.stripSpaces(sg).length();
		String pre = EnumChatFormatting.BOLD.toString()+EnumChatFormatting.UNDERLINE.toString();
		int idx = (int)(fraction*2*len);
		if (idx >= len) {
			String sg2 = pre+sg;
			String clip = sg2.substring(0, sg2.length()-1);
			this.drawString(clip, sx, sy, c1);
			this.drawString(sg2.substring(clip.length()), sx+(int)(fr.getStringWidth(clip)/s), sy, cflash1);
			idx -= len;
		}
		else {
			c2 = c1;
		}

		this.drawString(pre+sg.substring(0, idx), sx, sy, c2);

		 */

		//fr.drawFractionalString(sg, sx, sy, c1, cflash1, false, fraction);

		HashSet<Integer> spaces = new HashSet();
		int o = 0;
		for (int i = 0; i < sg.length(); i++) {
			char c = sg.charAt(i);
			if (c == ' ') {
				spaces.add(i-o-1);
				o++;
			}
		}
		sg = ReikaStringParser.stripSpaces(sg);

		int len = sg.length();
		int pos1 = fraction < 0.5 ? (int)(fraction*2*len) : len;
		int pos2 = fraction >= 0.5 ? (int)((fraction-0.5)*2*len) : 0;

		for (int i = 0; i < pos1; i++) {
			int c = i == pos1-1 && fraction < 0.5 ? cflash1 : c1;
			float px = i == 0 ? sx : sx+fr.getStringWidth(sg.substring(0, i));
			for (int a = i-1; a >= 0; a--) {
				if (spaces.contains(a)) {
					px += fr.getCharWidth(' ');
				}
			}
			int ret = fr.drawStringFloatPos(String.valueOf(sg.charAt(i)), px, sy, c, false);
		}

		for (int i = 0; i < pos2; i++) {
			int c = i == pos2-1 && !over ? cflash2 : c2;
			float px = i == 0 ? sx : sx+fr.getStringWidth(sg.substring(0, i));
			for (int a = i-1; a >= 0; a--) {
				if (spaces.contains(a)) {
					px += fr.getCharWidth(' ');
				}
			}
			int ret = fr.drawStringFloatPos(String.valueOf(sg.charAt(i)), px, sy, c, false);
		}
	}

	public static enum FontType {
		GUI("gui_font.png"),
		LEXICON("lexicon_font.png"),
		HUD("hud_font.png"),
		OBFUSCATED("obf_font.png"); //replaces the scramble char

		private final String texture;
		public final String id;
		public final ChromaFontRenderer renderer;

		private FontType(String s) {
			texture = "Textures/Font/"+s;
			renderer = new ChromaFontRenderer(this);
			id = DelegateFontRenderer.getRegisteredInstance().addRenderer(renderer);
		}

		public int drawString(String s, int x, int y, int color) {
			return this.drawString(s, x, y, color, false);
		}

		public int drawString(String s, int x, int y, int color, boolean shadow) {
			return renderer.drawString(s, x, y, color, shadow);
		}

		public void drawSplitString(String s, int x, int y, int split, int color) {
			renderer.drawSplitString(s, x, y, split, color);
		}
	}

}

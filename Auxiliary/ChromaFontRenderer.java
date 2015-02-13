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

import java.awt.image.BufferedImage;

import net.minecraftforge.client.event.GuiOpenEvent;

import org.lwjgl.input.Mouse;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.IO.DelegateFontRenderer;
import Reika.DragonAPI.IO.ReikaImageLoader;
import Reika.DragonAPI.Instantiable.Rendering.BasicFontRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
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
	}

	@Override
	protected float renderDefaultChar(int charIndex, boolean italic) {
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
			if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1))
				this.rerandomize();
		}
		return super.renderDefaultChar(charIndex, italic);
	}

	@Override
	protected void bindTexture() {
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, type.texture);
	}

	@Override
	protected void renderCharInString(String sg, int idx, boolean shadow) {
		if (type == FontType.OBFUSCATED) {
			preChar = idx > 0 ? sg.charAt(idx-1) : 0;
			postChar = idx < sg.length()-1 ? sg.charAt(idx+1) : 0;
		}
		super.renderCharInString(sg, idx, shadow);
	}

	@Override
	protected void onReload() {
		super.onReload();
		this.rerandomize();
	}

	private void rerandomize() {
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

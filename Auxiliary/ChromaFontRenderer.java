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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.IO.ReikaImageLoader;
import Reika.DragonAPI.Instantiable.Event.TextureReloadEvent;
import Reika.DragonAPI.Instantiable.IO.DelegateFontRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChromaFontRenderer extends FontRenderer {

	private final FontType type;
	private Kerning kerning = Kerning.NORMAL;
	//private String currentString;
	private final int[] offsets;
	private long lastReload = 0;

	/** Shadow for Optifine compat */
	private int[] charWidth = new int[256];

	private char preChar;
	private char postChar;
	private String currentString;

	private ChromaFontRenderer(FontType f) {
		super(Minecraft.getMinecraft().gameSettings, ReikaTextureHelper.font, Minecraft.getMinecraft().renderEngine, false);
		offsets = new int[charWidth.length];
		type = f;
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected float renderDefaultChar(int charIndex, boolean italic) {
		if (type == FontType.OBFUSCATED) {
			//charIndex = (charWidth.length+charIndex/*+(int)posX/16%16-(int)posY/16%16*/+offsets[charIndex])%charWidth.length;
			charIndex += offsets[charIndex];
			int min = DelegateFontRenderer.getCharGridIndex(' ');
			int max = DelegateFontRenderer.getCharGridIndex('~');
			if (preChar != 0)
				charIndex -= DelegateFontRenderer.getCharGridIndex(preChar);
			if (postChar != 0)
				charIndex += DelegateFontRenderer.getCharGridIndex(postChar);
			charIndex += currentString.hashCode();
			charIndex = min+(charWidth.length+charIndex%charWidth.length)%(max-min+1);
			if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1))
				this.rerandomize();
		}

		float f = charIndex%16*8;
		float f1 = charIndex/16*8;
		float f2 = italic ? 1F : 0F;
		//GL11.glEnable(GL11.GL_BLEND);
		//BlendMode.ADDITIVE.apply();
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, type.texture);
		float f3 = charWidth[charIndex]-0.01F;
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glTexCoord2f(f/128F, f1/128F);
		GL11.glVertex3f(posX+f2, posY, 0F);
		GL11.glTexCoord2f(f/128F, (f1+7.99F)/128F);
		GL11.glVertex3f(posX-f2, posY+7.99F, 0F);
		GL11.glTexCoord2f((f+f3-1F)/128F, f1/128F);
		GL11.glVertex3f(posX+f3-1F+f2, posY, 0F);
		GL11.glTexCoord2f((f+f3-1F)/128F, (f1+7.99F)/128F);
		GL11.glVertex3f(posX+f3-1F-f2, posY+7.99F, 0F);
		GL11.glEnd();
		//BlendMode.DEFAULT.apply();
		//GL11.glDisable(GL11.GL_BLEND);
		return charWidth[charIndex]+kerning.spaceMod;
	}

	/** Included for Optifine Compat */
	@Override
	protected void renderStringAtPos(String sg, boolean shadow) {
		currentString = sg;
		for (int i = 0; i < sg.length(); ++i) {
			if (type == FontType.OBFUSCATED) {
				preChar = i > 0 ? sg.charAt(i-1) : 0;
				postChar = i < sg.length()-1 ? sg.charAt(i+1) : 0;
			}
			char c0 = sg.charAt(i);
			int j;
			int k;

			if (c0 == '\u00A7' && i+1 < sg.length()) {
				j = "0123456789abcdefklmnor".indexOf(sg.toLowerCase().charAt(i+1));

				if (j < 16) {
					randomStyle = false;
					boldStyle = false;
					strikethroughStyle = false;
					underlineStyle = false;
					italicStyle = false;

					if (j < 0 || j > 15)
						j = 15;

					if (shadow)
						j += 16;

					k = colorCode[j];
					textColor = k;
					GL11.glColor4f((k >> 16)/255F, (k >> 8 & 255)/255F, (k & 255)/255F, alpha);
				}
				else if (j == 16)
					randomStyle = true;
				else if (j == 17)
					boldStyle = true;
				else if (j == 18)
					strikethroughStyle = true;
				else if (j == 19)
					underlineStyle = true;
				else if (j == 20)
					italicStyle = true;
				else if (j == 21) {
					randomStyle = false;
					boldStyle = false;
					strikethroughStyle = false;
					underlineStyle = false;
					italicStyle = false;
					GL11.glColor4f(red, blue, green, alpha);
				}

				i++;
			}
			else {
				j = DelegateFontRenderer.getCharGridIndex(c0);

				if (randomStyle && j != -1) {
					do {
						k = fontRandom.nextInt(charWidth.length);
					}
					while (charWidth[j] != charWidth[k]);

					j = k;
				}

				float f1 = this.getUnicodeFlag() ? 0.5F : 1F;
				boolean flag1 = (c0 == 0 || j == -1 || this.getUnicodeFlag()) && shadow;

				if (flag1) {
					posX -= f1;
					posY -= f1;
				}

				float f = this.renderCharAtPos(j, c0, italicStyle);

				if (flag1) {
					posX += f1;
					posY += f1;
				}

				if (boldStyle) {
					posX += f1;

					if (flag1) {
						posX -= f1;
						posY -= f1;
					}

					this.renderCharAtPos(j, c0, italicStyle);
					posX -= f1;

					if (flag1) {
						posX += f1;
						posY += f1;
					}

					f++;
				}

				Tessellator v5 = Tessellator.instance;

				if (strikethroughStyle) {
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					v5.startDrawingQuads();
					v5.addVertex(posX, posY+FONT_HEIGHT/2, 0);
					v5.addVertex(posX+f, posY+FONT_HEIGHT/2, 0);
					v5.addVertex(posX+f, posY+FONT_HEIGHT/2-1F, 0);
					v5.addVertex(posX, posY+FONT_HEIGHT/2-1F, 0);
					v5.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
				}

				if (underlineStyle) {
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					v5.startDrawingQuads();
					int l = underlineStyle ? -1 : 0;
					v5.addVertex(posX+l, posY+FONT_HEIGHT, 0);
					v5.addVertex(posX+f, posY+FONT_HEIGHT, 0);
					v5.addVertex(posX+f, posY+FONT_HEIGHT-1F, 0);
					v5.addVertex(posX+l, posY+FONT_HEIGHT-1F, 0);
					v5.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
				}

				posX += ((int)f);
			}
		}
	}

	/** Included for Optifine Compat */
	@Override
	public int getCharWidth(char c) {
		if (c == 167)
			return -1;
		else if (c == 32)
			return 4;
		else {
			int i = DelegateFontRenderer.getCharGridIndex(c);

			if (c > 0 && i != -1 && !this.getUnicodeFlag()) {
				return charWidth[i];
			}
			else if (glyphWidth[c] != 0) {
				int j = glyphWidth[c] >>> 4;
				int k = glyphWidth[c] & 15;

				if (k > 7) {
					k = 15;
					j = 0;
				}

				++k;
				return (k-j)/2+1;
			}
			else {
				return 0;
			}
		}
	}

	@SubscribeEvent
	public void reloadTextures(TextureReloadEvent evt) {
		this.onReload();
	}

	@Override
	public void onResourceManagerReload(IResourceManager irm) {
		this.onReload();
	}

	private void onReload() {
		this.readFontTexture();
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

	/*
	@Override
	public int drawString(String s, int x, int y, int color, boolean shadow) {
		this.checkAndFlagString(s);
		return super.drawString(s, x, y, color, shadow);
	}

	@Override
	public void drawSplitString(String s, int x, int y, int split, int color) {
		this.checkAndFlagString(s);
		super.drawSplitString(s, x, y, split, color);
	}

	private void checkAndFlagString(String s) {
		if (!s.equals(currentString)) {
			offset = ReikaRandomHelper.getRandomPlusMinus(0, 3);
		}
		currentString = s;
	}
	 */
	private void readFontTexture() {
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

	private static enum Kerning {
		VERYNARROW(-2),
		NARROW(-1),
		NORMAL(0),
		WIDE(1),
		VERYWIDE(2);

		private final int spaceMod;

		private Kerning(int s) {
			spaceMod = s;
		}
	}

}

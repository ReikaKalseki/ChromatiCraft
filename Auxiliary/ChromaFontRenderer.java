package Reika.ChromatiCraft.Auxiliary;

import java.awt.image.BufferedImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.IO.ReikaImageLoader;
import Reika.DragonAPI.Instantiable.Event.TextureReloadEvent;
import Reika.DragonAPI.Instantiable.IO.DelegateFontRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChromaFontRenderer extends FontRenderer {

	private final FontType type;
	private Kerning kerning = Kerning.NORMAL;
	private final int[] offsets;

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
			charIndex = (charWidth.length+charIndex+(int)posX/4%4-(int)posY/4%4+offsets[charIndex])%charWidth.length;
		}

		float f = charIndex%16*8;
		float f1 = charIndex/16*8;
		float f2 = italic ? 1.0F : 0.0F;
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVE.apply();
		//renderEngine.bindTexture(locationFontTexture);
		//ReikaTextureHelper.bindFontTexture();
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, type.texture);
		float f3 = charWidth[charIndex]-0.01F;
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glTexCoord2f(f/128.0F, f1/128.0F);
		GL11.glVertex3f(posX+f2, posY, 0.0F);
		GL11.glTexCoord2f(f/128.0F, (f1+7.99F)/128.0F);
		GL11.glVertex3f(posX-f2, posY+7.99F, 0.0F);
		GL11.glTexCoord2f((f+f3-1.0F)/128.0F, f1/128.0F);
		GL11.glVertex3f(posX+f3-1.0F+f2, posY, 0.0F);
		GL11.glTexCoord2f((f+f3-1.0F)/128.0F, (f1+7.99F)/128.0F);
		GL11.glVertex3f(posX+f3-1.0F-f2, posY+7.99F, 0.0F);
		GL11.glEnd();
		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_BLEND);
		return charWidth[charIndex]+kerning.spaceMod;
	}
	/*
	private float renderUnicodeChar(char p_78277_1_, boolean p_78277_2_)
	{
		if (glyphWidth[p_78277_1_] == 0)
		{
			return 0.0F;
		}
		else
		{
			int i = p_78277_1_/256;
			this.loadGlyphTexture(i);
			int j = glyphWidth[p_78277_1_] >>> 4;
			int k = glyphWidth[p_78277_1_] & 15;
			float f = j;
			float f1 = k+1;
			float f2 = p_78277_1_%16*16+f;
			float f3 = (p_78277_1_ & 255)/16*16;
			float f4 = f1-f-0.02F;
			float f5 = p_78277_2_ ? 1.0F : 0.0F;
			GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
			GL11.glTexCoord2f(f2/256.0F, f3/256.0F);
			GL11.glVertex3f(posX+f5, posY, 0.0F);
			GL11.glTexCoord2f(f2/256.0F, (f3+15.98F)/256.0F);
			GL11.glVertex3f(posX-f5, posY+7.99F, 0.0F);
			GL11.glTexCoord2f((f2+f4)/256.0F, f3/256.0F);
			GL11.glVertex3f(posX+f4/2.0F+f5, posY, 0.0F);
			GL11.glTexCoord2f((f2+f4)/256.0F, (f3+15.98F)/256.0F);
			GL11.glVertex3f(posX+f4/2.0F-f5, posY+7.99F, 0.0F);
			GL11.glEnd();
			return (f1-f)/2.0F+1.0F;
		}
	}

	private void loadGlyphTexture(int p_78257_1_)
	{
		renderEngine.bindTexture(this.getUnicodePageLocation(p_78257_1_));
	}*/

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
		for (int i = 0; i < offsets.length; i++) {
			offsets[i] = ReikaRandomHelper.getRandomPlusMinus(0, 4);
		}
	}

	private void readFontTexture() {
		BufferedImage buf = ReikaImageLoader.readImage(ChromatiCraft.class, type.texture);
		int w = buf.getWidth();
		int h = buf.getHeight();
		int[] rgb = new int[w*h];
		buf.getRGB(0, 0, w, h, rgb, 0, w);
		int slotHeight = h/16;
		int slotWidth = w/16;
		byte b0 = 1;
		float f = 8.0F/slotWidth;
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
						//if ((color >> 24 & 255) != 0) //alpha > 0
						if (ReikaColorAPI.isRGBNonZero(color))
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
	}

	public static enum FontType {
		GUI("Textures/gui_font.png"),
		LEXICON("Textures/lexicon_font.png"),
		HUD("Textures/hud_font.png"),
		OBFUSCATED("Textures/obf_font.png"); //replaces the scramble char

		private final String texture;
		public final String id;
		public final ChromaFontRenderer renderer;

		private FontType(String s) {
			texture = s;
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

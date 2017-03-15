package Reika.ChromatiCraft.Magic.Lore;

import java.util.HashMap;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LoreScriptRenderer {

	public static final LoreScriptRenderer instance = new LoreScriptRenderer();

	static final int CHAR_WIDTH = 10;
	static final int CHAR_SPACING = -2;

	private final String texture = "Textures/precursorscript.png";
	private final HashMap<Character, Integer> characters = new HashMap();

	private int currentColor;

	private LoreScriptRenderer() {
		int k = 0;
		for (int i = 0; i < 26; i++) {
			char c = (char)('a'+i);
			characters.put(c, k);
			k++;
		}
		for (int i = 0; i < 26; i++) {
			char c = (char)('A'+i);
			characters.put(c, k);
			k++;
		}
		for (int i = 0; i < 10; i++) {
			char c = (char)('0'+i);
			characters.put(c, k);
			k++;
		}
		characters.put('.', k+1);
		characters.put('?', k+2);
	}

	public void startRendering(boolean additive, int color) {
		ReikaTextureHelper.bindFinalTexture(ChromatiCraft.class, texture);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glEnable(GL11.GL_BLEND);
		if (additive)
			BlendMode.ADDITIVEDARK.apply();
		else
			BlendMode.DEFAULT.apply();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		Tessellator.instance.startDrawingQuads();
		Tessellator.instance.setColorOpaque_I(color);
		currentColor = color;
		Tessellator.instance.setBrightness(240);
	}

	public void stopRendering() {
		Tessellator.instance.draw();
		GL11.glPopAttrib();
	}

	/** Does not call start or stop rendering! */
	public void renderCharacter(char c, double x, double y, boolean flipVertical) {
		if (c == ' ')
			return;
		if (!characters.containsKey(c)) {
			throw new IllegalArgumentException("Cannot render char '"+c+"'!");
		}
		int idx = characters.get(c);
		double u = (idx%8)/8D;
		double v = (idx/8)/8D;
		double du = u+1/8D;
		double dv = v+1/8D;
		if (flipVertical) {
			y += 1.1;
		}
		double y1 = flipVertical ? y+CHAR_WIDTH : y;
		double y2 = flipVertical ? y : y+CHAR_WIDTH;
		Tessellator.instance.addVertexWithUV(x,				y2,	0, u, dv);
		Tessellator.instance.addVertexWithUV(x+CHAR_WIDTH,	y2,	0, du, dv);
		Tessellator.instance.addVertexWithUV(x+CHAR_WIDTH,	y1,	0, du, v);
		Tessellator.instance.addVertexWithUV(x,				y1,	0, u, v);
	}

	public void renderLine(String s, double x, double y, double width, boolean flipVertical) {
		String[] parts = s.split(" ");
		if (parts.length <= 1) {
			this.renderString(s, x, y, flipVertical);
		}
		else {
			double dx = x;
			int chars = 0;
			for (int i = 0; i < parts.length; i++) {
				chars += parts[i].length();
			}
			double sp = (width-chars*(CHAR_WIDTH+CHAR_SPACING))/(parts.length-1);
			for (int i = 0; i < parts.length; i++) {
				this.renderString(parts[i], dx, y, flipVertical);
				double dxp = dx;
				dx -= (CHAR_WIDTH+CHAR_SPACING)*parts[i].length();
				dx -= sp;
				if (i != parts.length-1) {
					float f = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
					GL11.glLineWidth(5*4/5/*12*/);
					//ReikaGuiAPI.instance.drawRectFrame((int)dx+CHAR_WIDTH+1, (int)y+2, (int)sp-1, CHAR_WIDTH-3, 0xff000000);
					double dx2 = dx+CHAR_WIDTH+0.5;
					double dy2 = y+4;
					int h = CHAR_WIDTH-3;
					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					/*
					for (int dw = 0; dw < sp-2; dw += 4) {
						ReikaGuiAPI.instance.drawLine(dx2+dw, dy2, dx2+dw, dy2+h, 0xff000000);
					}
					 */
					int c = 0xff000000 | ReikaColorAPI.getColorWithBrightnessMultiplier(currentColor, 0.25F);//0xff2a2a2a;
					ReikaGuiAPI.instance.drawLine_Double(dx2, dy2-1, dx2+sp-2, dy2-1, c);
					ReikaGuiAPI.instance.drawLine_Double(dx2, dy2+h/2+1+0.225, dx2+sp-2, dy2+h/2+1+0.225, c);
					//ReikaGuiAPI.instance.drawLine_Double(dx2, dy2+h/4+0.5, dx2+sp-2, dy2+h/4+0.5, c);
					GL11.glPopAttrib();
					GL11.glLineWidth(f);
				}
			}
		}
		int c = 0xff000000 | ReikaColorAPI.getColorWithBrightnessMultiplier(currentColor, 0.375F);//0xff7a7a7a;
		float f = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		GL11.glLineWidth(2);
		ReikaGuiAPI.instance.drawLine_Double(x+CHAR_WIDTH, y+1.5, x-width+CHAR_WIDTH-0.5, y+1.5, c);
		ReikaGuiAPI.instance.drawLine_Double(x+CHAR_WIDTH, y-0.5+CHAR_WIDTH, x-width+CHAR_WIDTH-0.5, y-0.5+CHAR_WIDTH, c);
		GL11.glLineWidth(f);
	}

	public void renderString(String s, double x, double y, boolean flipVertical) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			this.renderCharacter(c, x-(CHAR_WIDTH+CHAR_SPACING)*i, y, flipVertical);
		}
	}

}

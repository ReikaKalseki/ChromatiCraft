package Reika.ChromatiCraft.Magic.Lore;

import java.util.HashMap;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LoreScriptRenderer {

	public static final LoreScriptRenderer instance = new LoreScriptRenderer();

	static final int CHAR_WIDTH = 5;
	static final int CHAR_SPACING = 1;

	private final String texture = "Textures/precursorscript.png";
	private final HashMap<Character, Integer> characters = new HashMap();

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

	public void startRendering() {
		ReikaTextureHelper.bindFinalTexture(ChromatiCraft.class, texture);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Tessellator.instance.startDrawingQuads();
	}

	public void stopRendering() {
		Tessellator.instance.draw();
		GL11.glPopAttrib();
	}

	/** Does not call start or stop rendering! */
	public void renderCharacter(char c, int x, int y) {
		int idx = characters.get(c);
		double u = (idx%8)/8D;
		double v = (idx/8)/8D;
		double du = u+1/8D;
		double dv = v+1/8D;
		Tessellator.instance.addVertexWithUV(x,				y+CHAR_WIDTH,	0, u, dv);
		Tessellator.instance.addVertexWithUV(x+CHAR_WIDTH,	y+CHAR_WIDTH,	0, du, dv);
		Tessellator.instance.addVertexWithUV(x+CHAR_WIDTH,	y,				0, du, v);
		Tessellator.instance.addVertexWithUV(x,				y,				0, u, v);
	}

	public void renderString(String s, int x, int y) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			this.renderCharacter(c, x+(CHAR_WIDTH+CHAR_SPACING)*i, y);
		}
	}

}

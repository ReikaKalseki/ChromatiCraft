package Reika.ChromatiCraft.Magic.Lore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.IO.ReikaFileReader;


public class RosettaStone {

	private static final HashSet<Character> decodableCharacters = new HashSet();
	private static final String PATH = "Resources/rosetta.txt";

	private final Random rand = new Random();

	static {
		for (int i = 0; i < 26; i++) {
			char c = (char)('a'+i);
			if (c != 'o' && c != 'f' && c != 'l' && c != 'r' && c != 'c')
				decodableCharacters.add(c);
		}
	}

	private final ArrayList<String> text;

	public RosettaStone() {
		text = new ArrayList();
		for (String s : ReikaFileReader.getFileAsLines(ChromatiCraft.class.getResourceAsStream(PATH), true)) {
			if (s.isEmpty() || s.equals(System.lineSeparator()))
				continue;
			text.add(s);
		}
	}

	public void render(ScaledResolution res, double w2, double h2) {
		double w = res.getScaledWidth_double();
		int inset = 12;
		double x = w-inset;

		rand.setSeed(System.currentTimeMillis()/250);

		int dy = 0;
		LoreScriptRenderer.instance.startRendering(false, 0xffffff);
		for (String s : text) {
			int y = dy;
			LoreScriptRenderer.instance.renderLine(s, x, y, w-inset/2D, false);
			dy += LoreScriptRenderer.CHAR_WIDTH-1;
		}
		LoreScriptRenderer.instance.stopRendering();

		dy += 10;

		x = inset/2;
		FontRenderer f = Minecraft.getMinecraft().standardGalacticFontRenderer;
		for (String s : text) {
			int y = dy;
			int dx = (int)x;
			//int sp = 8;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c == ' ') {
					dx += 9;
				}
				else {
					if (decodableCharacters.contains(c)) {
						f.drawString(String.valueOf(c), dx, y, 0x000000);
						dx += f.getCharWidth(c);
					}
					else {
						this.renderSmudge(c, dx, y);
						dx += 8;
					}
				}
			}
			//f.drawString(s, x, y, 0x000000);
			dy += f.FONT_HEIGHT;
		}
	}

	private void renderSmudge(char c, int x, int y) {
		//Minecraft.getMinecraft().fontRenderer.drawString(EnumChatFormatting.OBFUSCATED.toString()+c, x, y, 0x000000);
		c = (char)('a'+rand.nextInt(26));
		Minecraft.getMinecraft().standardGalacticFontRenderer.drawString(String.valueOf(c), x, y, 0x000000);
	}

}

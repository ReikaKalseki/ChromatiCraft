package Reika.ChromatiCraft.Magic.Lore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;
import Reika.DragonAPI.IO.ReikaFileReader;


public class RosettaStone {

	private static final HashSet<Character> decodableCharacters = new HashSet();
	private static final String PATH = "Resources/rosetta.txt";

	static {
		for (int i = 0; i < 26; i++) {
			char c = (char)('a'+i);
			if (c != 'o' && c != 'f' && c != 'l' && c != 'r' && c != 'c')
				decodableCharacters.add(c);
		}
	}

	private final ArrayList<String> text;

	public RosettaStone() {
		text = ReikaFileReader.getFileAsLines(new File(PATH), true);
	}

	public void render() {
		int x = 20;

		int ty = 0;
		int dy = 0;
		for (String s : text) {
			int y = ty+dy;
			LoreScriptRenderer.instance.renderString(s, x, y);
			dy += LoreScriptRenderer.CHAR_WIDTH+2;
		}

		ty = 80;
		dy = 0;
		FontRenderer f = Minecraft.getMinecraft().standardGalacticFontRenderer;
		for (String s : text) {
			int y = ty+dy;
			int dx = x;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (decodableCharacters.contains(c)) {
					f.drawString(String.valueOf(c), dx, y, 0x000000);
					dx += f.getCharWidth(c);
				}
				else {
					this.renderSmudge(c, dx, y);
					dx += 8;
				}
			}
			//f.drawString(s, x, y, 0x000000);
			dy += f.FONT_HEIGHT;
		}
	}

	private void renderSmudge(char c, int x, int y) {
		Minecraft.getMinecraft().fontRenderer.drawString(EnumChatFormatting.OBFUSCATED.toString()+c, x, y, 0x000000);
	}

}

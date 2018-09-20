/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Lore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;


public class RosettaStone {

	private static final HashSet<Character> decodableCharacters = new HashSet();

	private final long seed;
	private final Random rand;

	private int alpha = 1;

	static {
		for (int i = 0; i < 26; i++) {
			char c = (char)('a'+i);
			if (c != 'o' && c != 'f' && c != 'l' && c != 'r' && c != 'c' && c != 'p')
				decodableCharacters.add(c);
		}
	}

	private final ArrayList<String> text;

	public RosettaStone(EntityPlayer ep) {
		text = new ArrayList();
		this.loadText();

		seed = ep.getUniqueID().hashCode();
		rand = new Random(seed);
	}

	public void loadText() {
		text.clear();
		for (String s : this.getData()) {
			if (s.isEmpty() || s.equals(System.lineSeparator()) || s.charAt(0) == '#')
				continue;
			text.add(s);
		}
	}

	private ArrayList<String> getData() {
		if (LoreScripts.instance.hasReroutePath()) {
			return ReikaFileReader.getFileAsLines(LoreScripts.instance.getReroutedRosettaFile(), true);
		}
		else {
			return ReikaFileReader.getFileAsLines(ChromatiCraft.class.getResourceAsStream("Resources/rosetta.txt"), true);
		}
	}

	public void render(ScaledResolution res, double w2, double h2) {
		alpha = Math.min(alpha+2, 255);
		double w = res.getScaledWidth_double();
		int inset = 12;
		double x = w-inset-1;

		Minecraft.getMinecraft().gameSettings.showDebugInfo = false;
		rand.setSeed(seed + System.currentTimeMillis()/100);

		int color = 0xffffff;//ReikaColorAPI.mixColors(0xb0e0ff, 0x7acfff, (float)(0.5+0.5*Math.sin(System.currentTimeMillis()/2000D)));

		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/GUIs/all-back.png");
		Tessellator.instance.startDrawingQuads();
		Tessellator.instance.setColorRGBA_I(0xffffff, Math.min(255, alpha*2));
		Tessellator.instance.addVertexWithUV(0, h2*2, 0, 0, 1);
		Tessellator.instance.addVertexWithUV(w2*2, h2*2, 0, 1, 1);
		Tessellator.instance.addVertexWithUV(w2*2, 0, 0, 1, 0);
		Tessellator.instance.addVertexWithUV(0, 0, 0, 0, 0);
		Tessellator.instance.draw();

		int dy = 0;
		LoreScriptRenderer.instance.startRendering(false, color | (alpha << 24));
		for (String s : text) {
			int y = dy;
			LoreScriptRenderer.instance.renderLine(s, x, y, w-inset/2D, false);
			dy += LoreScriptRenderer.CHAR_WIDTH-1;
		}
		LoreScriptRenderer.instance.stopRendering();

		dy += 10;

		x = inset/2-2;
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
					if (decodableCharacters.contains(c) && rand.nextInt(20) > 0) { //only render some chars, and still scramble a handful of decodables
						f.drawString(String.valueOf(c), dx, y, color | (Math.max(4, alpha) << 24));
						dx += 8;//f.getCharWidth(c);
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
		for (int i = 0; i < 4; i++) {
			c = (char)('a'+rand.nextInt(26));
			int a = Math.max(4, (int)(alpha*(0.5+0.5*rand.nextDouble())));
			Minecraft.getMinecraft().standardGalacticFontRenderer.drawString(String.valueOf(c), x, y, 0xffffff | (a << 24));
		}
	}

}

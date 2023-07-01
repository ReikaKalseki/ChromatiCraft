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
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.google.common.base.Charsets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;


public class RosettaStone {

	public static final RosettaStone init = new RosettaStone(0);

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
		this(ep.getUniqueID());
	}

	private RosettaStone(UUID uid) {
		this(uid.getMostSignificantBits() ^ uid.getLeastSignificantBits());
	}

	private RosettaStone(long seed) {
		text = new ArrayList();
		this.loadText();

		this.seed = seed;
		rand = new Random(seed);
	}

	public void loadText() {
		text.clear();
		List<String> li = this.getData();
		for (String s : li) {
			if (s.isEmpty() || s.equals(System.lineSeparator()) || s.charAt(0) == '#')
				continue;
			text.add(s);
		}
	}

	private List<String> getData() {
		/*
		InputStream in1 = ChromatiCraft.class.getResourceAsStream("Resources/rosetta.txt");
		InputStream in2 = ChromatiCraft.class.getResourceAsStream("Resources/lore.xml");
		File f1 = new File(DragonAPICore.getMinecraftDirectory(), "flippedrosetta.txt");
		File f2 = new File(DragonAPICore.getMinecraftDirectory(), "flippedlore.xml");
		File f3 = new File(DragonAPICore.getMinecraftDirectory(), "flippedunflipped.xml");
		ArrayList<String> li1 = ReikaFileReader.encryptFileBytes(in1);
		ArrayList<String> li2 = ReikaFileReader.encryptFileBytes(in2);
		ReikaFileReader.writeLinesToFile(f1, li1, true);
		ReikaFileReader.writeLinesToFile(f2, li2, true);

		ArrayList<String> test = new ArrayList(li2);
		ArrayList<Byte> test2 = ReikaFileReader.decryptByteList(test);
		ReikaFileReader.writeDataToFile(f3, test2, true);
		 */
		if (LoreScripts.instance.hasReroutePath()) {
			return ReikaFileReader.getFileAsLines(LoreScripts.instance.getReroutedRosettaFile(), true, Charsets.UTF_8);
		}
		else {
			try {
				return this.loadInternalRosettaFile();
			}
			catch (Exception e) {
				throw new RegistrationException(ChromatiCraft.instance, "Could not load rosetta text", e);
			}
		}
	}

	private List<String> loadInternalRosettaFile() throws Exception {
		return null;
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

	public void test() {
		this.loadText();
		if (text.isEmpty())
			throw new RegistrationException(ChromatiCraft.instance, "Could not load loretext!");
	}

	public void clear() {
		alpha = 1;
	}

}

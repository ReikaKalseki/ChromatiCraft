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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import cpw.mods.fml.common.FMLLog;


public class LoreScripts {

	public static final LoreScripts instance = new LoreScripts();

	private final File reroutePath;

	private final Collection<LorePanel> data = new ArrayList();

	private LoreScripts() {
		File f = new File("C:/ChromatiCraftLoreDev");
		reroutePath = f.exists() && f.isDirectory() && f.listFiles().length == 2 ? f : null;
	}

	public boolean hasReroutePath() {
		return reroutePath != null;
	}

	public File getReroutedLoreFile() {
		return new File(reroutePath, "lore.xml");
	}

	public File getReroutedRosettaFile() {
		return new File(reroutePath, "rosetta.txt");
	}

	public Collection<LorePanel> getData() {
		return Collections.unmodifiableCollection(data);
	}

	public static final class LorePanel {

		public final PanelSize size;
		private final LoreLine[] data;
		public final int longestStringLength;
		public final int lineCount;

		private LorePanel(ScriptLocations loc, PanelSize s, String[] text) {
			size = s;
			if (text.length == 0)
				FMLLog.bigWarning("Warning: Declared a lore panel for "+loc+" of size "+s+" with no text!");
			else if (text.length > size.lineCount)
				throw new RegistrationException(ChromatiCraft.instance, "Invalid lore panel for "+loc+"! Size ("+size+") is too small for supplied string array: "+Arrays.toString(text));
			else if (text.length < size.lineCount)
				if (instance.reroutePath != null)
					FMLLog.bigWarning("Warning: Declared a lore panel for "+loc+" of size "+s+" but with not enough text ("+text.length+" lines) to fill it: "+Arrays.toString(text));
			lineCount = Math.min(text.length, size.lineCount);
			data = new LoreLine[lineCount];
			//System.arraycopy(text, 0, data, 0, size);
			for (int i = 0; i < text.length; i++) {
				data[i] = new LoreLine(text[i]);
			}
			int l = 0;
			for (int i = 0; i < text.length; i++) {
				l = Math.max(l, text[i].length());
			}
			longestStringLength = l;
			if (longestStringLength > size.maxLength)
				throw new RegistrationException(ChromatiCraft.instance, "Invalid lore panel for "+loc+"! Longest string is length "+longestStringLength+", but the panel only allows up to "+size.maxLength);
		}

		@Override
		public String toString() {
			return size+" x "+longestStringLength+": "+Arrays.toString(data);
		}

		public LoreLine getLine(int i) {
			return data[i];
		}

	}

	public static final class LoreLine {

		public final String text;
		public final int length;

		private LoreLine(String s) {
			text = s;
			length = s.length();
		}

		@Override
		public String toString() {
			return "L"+length+": "+text;
		}

	}

	public static final class PanelSize {

		public final int lineCount;
		public final int maxLength;

		private PanelSize(int l, int c) {
			lineCount = c;
			maxLength = l;
		}

		@Override
		public String toString() {
			return maxLength+" chars x "+lineCount+" lines";
		}

	}

	/*Text sizes:
	cavern: 40 chars x 5 lines x 2 panels + 16 chars x 12 lines x 2 panels + 16 chars x 17 lines x 2 panels
	burrow: 24 chars x 5 lines x 3 panels
	Ocean: 64 chars x 17 lines x 2 panels
	Desert Struct: 24 chars x 17 lines x 4 panels
	Towers: 23 chars x 5 lines x 4 panels
	pylons: 15 chars x 5 lines x 8 panels
	alvearies: 23 chars x 17 lines x 4 panels
	snowstruct:
	 */
	public static enum ScriptLocations {
		BURROW(new PanelSize(24, 5), 3),
		CAVERN1(new PanelSize(40, 5), 2),
		CAVERN2(new PanelSize(16, 12), 2),
		CAVERN3(new PanelSize(16, 17), 2),
		DESERT(new PanelSize(24, 17), 4),
		OCEAN(new PanelSize(64, 17), 2),
		PYLON(new PanelSize(15, 5), 8),
		TOWER(new PanelSize(23, 5), 4),
		ALVEARY(new PanelSize(17, 7), 4),
		SNOWSTRUCT(new PanelSize(24, 5), 4);

		public final PanelSize size;
		public final int panelCount;

		private final ArrayList<LorePanel> panels = new ArrayList();

		public static final ScriptLocations[] list = values();

		private ScriptLocations(PanelSize s, int c) {
			panelCount = c;
			size = s;
		}

		public LorePanel getPanel(int i) {
			return panels.get(i);
		}

		public LorePanel getRandomPanel(Random rand) {
			return this.getPanel(rand.nextInt(panels.size()));
		}

		public ArrayList<LorePanel> getUniqueRandomPanels(Random rand, int number) {
			if (number > panels.size())
				throw new IllegalArgumentException("Cannot get "+number+" unique panels from a list of "+panels.size());
			ArrayList<LorePanel> li = new ArrayList();
			ArrayList<Integer> indices = ReikaJavaLibrary.makeIntListFromArray(ReikaArrayHelper.getLinearArray(panels.size()));
			for (int i = 0; i < number; i++) {
				int idx0 = rand.nextInt(indices.size());
				int idx = indices.get(idx0);
				li.add(panels.get(idx));
				indices.remove(idx0);
			}
			return li;
		}
		/*
		private Collection<String> getTexts() {
			return ChromaDescriptions.getScriptTexts(this);
		}
		 */
		public void reload() {
			panels.clear();
		}

		public void loadText(String s) {
			try {
				panels.add(new LorePanel(this, size, s.split("\\n")));
			}
			catch (Exception e) {
				throw new RuntimeException("Could not load lore panel for "+this+" with data:\n----------\n"+s+"\n----------", e);
			}
		}

		public boolean isEnabled() {
			return this != PYLON;
		}
	}

}

package Reika.ChromatiCraft.Magic.Lore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;


public class LoreScripts {

	public static final LoreScripts instance = new LoreScripts();

	private final Collection<LoreEntry> data = new ArrayList();

	private LoreScripts() {

	}

	public Collection<LoreEntry> getData() {
		return Collections.unmodifiableCollection(data);
	}

	public static final class LoreEntry {

		public final String text;

		private LoreEntry(String s) {
			text = s;
		}

	}

	public static enum ScriptLocations {
		BURROW(),
		CAVERN(),
		DESERT(),
		OCEAN(),
		PYLON(),
		TOWER();

		public static final ScriptLocations[] list = values();

		public Collection<String> getTexts() {
			return ChromaDescriptions.getScriptTexts(this);
		}
	}

}

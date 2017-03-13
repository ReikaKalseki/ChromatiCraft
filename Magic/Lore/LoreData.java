package Reika.ChromatiCraft.Magic.Lore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import Reika.DragonAPI.Instantiable.RadialTree.RadialNodeEntry;


public class LoreData {

	public static final LoreData instance = new LoreData();

	private final Collection<LoreEntry> data = new ArrayList();

	private LoreEntry root;

	private LoreData() {

	}

	public LoreEntry getRoot() {
		return root;
	}

	public Collection<LoreEntry> getData() {
		return Collections.unmodifiableCollection(data);
	}

	public static final class LoreEntry implements RadialNodeEntry {

		public final LoreEntry parent;
		private final Collection<LoreEntry> children = new ArrayList();
		public final String text;

		private LoreEntry(LoreEntry e, String s) {
			text = s;
			parent = e;
		}

		public Collection<RadialNodeEntry> getChildren() {
			return (Collection)Collections.unmodifiableCollection(children);
		}

		public Collection<LoreEntry> getActualChildren() {
			return Collections.unmodifiableCollection(children);
		}

		@Override
		public RadialNodeEntry getParent() {
			return parent;
		}

	}

}

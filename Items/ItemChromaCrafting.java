package Reika.ChromatiCraft.Items;

import Reika.ChromatiCraft.ChromaNames;
import Reika.ChromatiCraft.Base.ItemChromaMulti;

public class ItemChromaCrafting extends ItemChromaMulti {

	public ItemChromaCrafting(int tex) {
		super(tex);
	}

	@Override
	public int getNumberTypes() {
		return ChromaNames.craftingNames.length;
	}

}

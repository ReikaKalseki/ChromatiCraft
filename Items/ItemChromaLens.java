package Reika.ChromatiCraft.Items;

import Reika.ChromatiCraft.ChromaNames;
import Reika.ChromatiCraft.Base.ItemChromaMulti;

public class ItemChromaLens extends ItemChromaMulti {

	public ItemChromaLens(int tex) {
		super(tex);
	}

	@Override
	public int getNumberTypes() {
		return ChromaNames.lensNames.length;
	}

}

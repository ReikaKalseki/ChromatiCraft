/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import Reika.ChromatiCraft.Base.ItemChromaMulti;
import Reika.ChromatiCraft.Registry.ChromaItems;

import net.minecraft.util.IIcon;

public class ItemCluster extends ItemChromaMulti {

	private final IIcon[] icons = new IIcon[this.getNumberTypes()];

	public ItemCluster(int tex) {
		super(tex);
		hasSubtypes = true;
	}

	@Override
	public int getNumberTypes() {
		return ChromaItems.CLUSTER.getNumberMetadatas();
	}
}

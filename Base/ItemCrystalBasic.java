/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Registry.CrystalElement;

public class ItemCrystalBasic extends ItemChromaMulti {

	public ItemCrystalBasic(int tex) {
		super(tex);
	}

	@Override
	public int getNumberTypes() {
		return CrystalElement.elements.length;
	}

	@Override
	public String getTexture(ItemStack is) {
		return "/Reika/ChromatiCraft/Textures/Items/items_color.png";
	}
}

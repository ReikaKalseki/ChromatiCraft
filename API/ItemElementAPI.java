/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import java.util.Collection;

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;

/** Use this to add custom recipe objects that ChromatiCraft can use for item element value calculation. */
public interface ItemElementAPI {

	/** Returns the value of a given item, for each of the 16 colors. */
	public int[] getValueForItemAPI(ItemStack is);

	/** Use this to register a custom recipe handler. */
	public void addHandler(ItemInOutHandler handler);

	public static interface ItemInOutHandler {

		/** All items that go into making the given item, through this recipe handler. Return null or empty if the handler cannot make this item. */
		public Collection<ItemStack> getInputItemsFor(ItemStack out);

		/** These represent the elements added by the process itself, eg smelting adds orange, for fire. Return null or empty for none. */
		public Collection<CrystalElementProxy> getBonusElements();

	}

}

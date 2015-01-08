/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.Event;

/** Fired when a chest generates in a "prefab" ChromatiCraft structure. You can use it to add custom loot; note that if you fill the inventory,
 * the loot may fail to be added, but the inventory is the size of a double chest, making this unlikely. */
public class StructureChestPopulationEvent extends Event {

	/** The enum name of the structure type */
	public final String type;

	/** The dungeon loot table used to generate the vanilla-type loot */
	public final String lootTable;

	/** The actual items to be added. */
	private final Collection<ItemStack> items = new ArrayList();

	/** This is the random instance used in worldgen. DO NOT alter its seed. */
	public final Random rand;

	public StructureChestPopulationEvent(String s, String l, Random r) {
		type = s;
		lootTable = l;
		rand = r;
	}

	public void addItem(ItemStack is) {
		if (is == null || is.getItem() == null)
			throw new IllegalArgumentException("Cannot add a null item to the chest!");
		items.add(is);
	}

	public Collection<ItemStack> getItems() {
		return Collections.unmodifiableCollection(items);
	}

}

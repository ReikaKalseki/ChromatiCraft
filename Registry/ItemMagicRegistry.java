/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import Reika.ChromatiCraft.Magic.ElementTagCompound;

/** Specifies the magic elements present in each "fundamental" item. */
public class ItemMagicRegistry {

	public static final ItemMagicRegistry instance = new ItemMagicRegistry();

	private final HashMap<Item, ElementTagCompound> data = new HashMap();

	private ItemMagicRegistry() {
		this.addTag(Block.stone, CrystalElement.RED, 10);

		this.addTag(Item.coal, CrystalElement.ORANGE, 10);
		this.addTag(Item.coal, CrystalElement.YELLOW, 10);

		this.addTag(Item.ingotIron, CrystalElement.RED, 10);
		this.addTag(Item.ingotIron, CrystalElement.ORANGE, 3);
	}

	private void addTag(Block b, CrystalElement color, int value) {
		this.addTag(Item.itemsList[b.blockID], color, value);
	}

	private void addTag(Item item, CrystalElement color, int value) {
		if (data.containsKey(item)) {
			ElementTagCompound tag = data.get(item);
			tag.addTag(color, value);
		}
		else {
			data.put(item, new ElementTagCompound(color, value));
		}
	}

}

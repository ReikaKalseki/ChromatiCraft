/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Interfaces.IDRegistry;

public enum ExtraChromaIDs implements IDRegistry {

	GROWTHID("Other IDs", "Growth Hormone ID", 36, Potion.class),
	SATID("Other IDs", "Saturation ID", 38, Potion.class);

	private String name;
	private String category;
	private int defaultID;
	private Class type;

	public static final ExtraChromaIDs[] idList = values();

	private ExtraChromaIDs(String cat, String n, int d, Class c) {
		name = n;
		category = cat;
		defaultID = d;
		type = c;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public int getDefaultID() {
		return defaultID;
	}

	public boolean isBlock() {
		return type == Blocks.class;
	}

	public boolean isItem() {
		return type == Item.class;
	}

	public int getValue() {
		return ChromatiCraft.config.getOtherID(this.ordinal());
	}

	@Override
	public String getConfigName() {
		return this.getName();
	}

	public boolean isDummiedOut() {
		return type == null;
	}

}

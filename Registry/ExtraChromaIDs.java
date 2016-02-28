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

import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Potion;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Interfaces.Registry.IDRegistry;

public enum ExtraChromaIDs implements IDRegistry {

	GROWTHID(		"Potion IDs", 		"Growth Hormone ID", 		36, 	Potion.class),
	SATID(			"Potion IDs", 		"Saturation ID", 			38, 	Potion.class),
	REGENID(		"Potion IDs", 		"Regen ID", 				39, 	Potion.class),
	RAINBOWFOREST(	"Biome IDs", 		"Rainbow Forest Biome ID", 	48, 	BiomeGenBase.class),
	ENDERFOREST(	"Biome IDs", 		"Ender Forest Biome ID", 	47, 	BiomeGenBase.class),
	ISLANDS(		"Biome IDs", 		"Skyland Biome ID", 		100, 	BiomeGenBase.class),
	SKYLANDS(		"Biome IDs", 		"Island Biome ID", 			101, 	BiomeGenBase.class),
	PLAINS(			"Biome IDs", 		"Plains Biome ID", 			102, 	BiomeGenBase.class),
	WEAPONAOEID(	"Enchantment IDs", 	"Weapon AOE ID", 			90, 	Enchantment.class),
	ENDERLOCKID(	"Enchantment IDs", 	"Ender Lock ID", 			91, 	Enchantment.class),
	AGGROMASKID(	"Enchantment IDs", 	"Aggro Mask ID", 			92, 	Enchantment.class),
	USEREPAIRID(	"Enchantment IDs", 	"Use Repair ID", 			93, 	Enchantment.class),
	DIMID(			"Other IDs",		"Dimension ID",				60,		WorldProvider.class),
	;

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

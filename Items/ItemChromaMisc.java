/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Base.ItemChromaMulti;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;

public class ItemChromaMisc extends ItemChromaMulti {

	public ItemChromaMisc(int tex) {
		super(tex);
	}

	public static enum FilterMatch {
		STONE(),
		GROUND(),
		ORE(),
		SAPLING(),
		MOBDROPS(),
		SEED(),
		FLOWER(),
		;

		public boolean match(ItemStack is) {
			Item i = is.getItem();
			switch(this) {
				case GROUND:
					if (ReikaItemHelper.matchStackWithBlock(is, Blocks.dirt) || ReikaItemHelper.matchStackWithBlock(is, Blocks.grass))
						return true;
					if (ReikaItemHelper.matchStackWithBlock(is, Blocks.sand) || ReikaItemHelper.matchStackWithBlock(is, Blocks.gravel))
						return true;
				case MOBDROPS:
					if (i == Items.feather || i == Items.porkchop || i == Items.beef || i == Items.chicken || i == Items.leather)
						return true;
					if (i == Items.string || i == Items.spider_eye || i == Items.rotten_flesh || i == Items.gunpowder || i == Items.bone || i == Items.arrow)
						return true;
					if (i == Items.bow || i == Items.ender_pearl || i == Items.skull || i == Items.blaze_rod || i == Items.ghast_tear || i == Items.magma_cream)
						return true;
					if (ReikaItemHelper.matchStacks(is, ReikaItemHelper.inksac))
						return true;
					if (ModList.THAUMCRAFT.isLoaded()) {
						ItemStack brain = ReikaItemHelper.lookupItem(ModList.THAUMCRAFT, "ItemZombieBrain", 0);
						if (ReikaItemHelper.matchStacks(is, brain))
							return true;
					}
					if (ModList.TINKERER.isLoaded()) {
						ItemStack bone = ReikaItemHelper.lookupItem(ModList.TINKERER, "materials", 8);
						if (ReikaItemHelper.matchStacks(is, bone))
							return true;
					}
				case ORE:
					return ReikaBlockHelper.isOre(is) || ReikaItemHelper.isOreDrop(is);
				case SAPLING:
					return ReikaItemHelper.isInOreTag(is, "treeSapling");
				case STONE:
					return ReikaItemHelper.isInOreTag(is, "stone");
				case FLOWER:
					return ReikaItemHelper.isInOreTag(is, "flower");
				case SEED:
					return ReikaItemHelper.isInOreTag(is, "seed");
			}
			return false;
		}
	}

}

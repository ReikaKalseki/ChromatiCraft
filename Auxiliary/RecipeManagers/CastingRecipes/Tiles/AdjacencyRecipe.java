/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class AdjacencyRecipe extends PylonRecipe {

	private static final Item[] upgrade = {Items.iron_ingot, Items.iron_ingot, Items.gold_ingot, Items.gold_ingot,
		Items.diamond, Items.emerald, Items.nether_star};

	public AdjacencyRecipe(CrystalElement e, int tier) {
		super(getItem(e, tier), getMainItem(e, tier));

		ItemStack corner = tier == 0 ? new ItemStack(Items.diamond) : new ItemStack(upgrade[tier-1]);
		this.addAuxItem(corner, -2, -2);
		this.addAuxItem(corner, 2, -2);
		this.addAuxItem(corner, 2, 2);
		this.addAuxItem(corner, -2, 2);

		ItemStack shard = this.getChargedShard(e);
		this.addAuxItem(shard, 0, -2);
		this.addAuxItem(shard, 2, 0);
		this.addAuxItem(shard, 0, 2);
		this.addAuxItem(shard, -2, 0);

		this.addAuraRequirement(e, 20000*(tier+1));
		this.addAuraRequirement(CrystalElement.YELLOW, 500*(tier+1));
	}

	private static ItemStack getMainItem(CrystalElement e, int tier) {
		return tier == 0 ? ChromaStacks.crystalStar : getItem(e, tier-1);
	}

	private static ItemStack getItem(CrystalElement e, int tier) {
		ItemStack is = ChromaItems.ADJACENCY.getStackOfMetadata(e.ordinal());
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("tier", tier);
		return is;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 8;
	}

}

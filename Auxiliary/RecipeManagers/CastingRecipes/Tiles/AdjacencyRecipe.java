/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import java.util.Collection;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.AdjacencyUpgrades;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class AdjacencyRecipe extends PylonCastingRecipe {

	private static final Item[] upgrade = {Items.iron_ingot, Items.iron_ingot, Items.gold_ingot, Items.gold_ingot,
			Items.diamond, Items.emerald, Items.nether_star};

	private final int tier;

	public AdjacencyRecipe(CrystalElement e, int n) {
		super(getItem(e, n), getMainItem(e, n));
		tier = n;

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

		this.addAuraRequirement(e, 20000*(tier*2+1));
		this.addAuraRequirement(CrystalElement.YELLOW, 500*(tier+1));

		this.addRuneRingRune(e);
	}

	@Override
	public int getDuration() {
		return super.getDuration()*(int)(1+tier/2D);
	}

	private static ItemStack getMainItem(CrystalElement e, int tier) {
		return tier == 0 ? ChromaStacks.crystalStar : getItem(e, tier-1);
	}

	private static ItemStack getItem(CrystalElement e, int tier) {
		return AdjacencyUpgrades.upgrades[e.ordinal()].getStackOfTier(tier);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 32;
	}

	@Override
	public void getRequiredProgress(Collection<ProgressStage> c) {
		switch(tier) {
			case TileEntityAdjacencyUpgrade.MAX_TIER-1:
				c.add(ProgressStage.CTM);
				break;
			case TileEntityAdjacencyUpgrade.MAX_TIER-2:
				c.add(ProgressStage.ALLCORES);
				break;
			case TileEntityAdjacencyUpgrade.MAX_TIER-3:
				c.add(ProgressStage.STRUCTCOMPLETE);
				break;
			case TileEntityAdjacencyUpgrade.MAX_TIER-4:
				c.add(ProgressStage.DIMENSION);
				break;
		}
	}

}

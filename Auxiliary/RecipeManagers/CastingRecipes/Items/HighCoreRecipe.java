/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ShardGroupingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public abstract class HighCoreRecipe extends PylonCastingRecipe implements ShardGroupingRecipe {

	protected HighCoreRecipe(ItemStack out, CrystalElement primary, CrystalElement secondary, Coordinate rune1, Coordinate rune2, ItemStack dust) {
		super(out, ChromaStacks.crystalStar);

		this.addAuxItem(this.getChargedShard(primary), -2, -2);
		this.addAuxItem(this.getChargedShard(primary), -4, -4);

		this.addAuxItem(this.getChargedShard(primary), 2, -2);
		this.addAuxItem(this.getChargedShard(primary), 4, -4);

		this.addAuxItem(this.getChargedShard(primary), -2, 2);
		this.addAuxItem(this.getChargedShard(primary), -4, 4);

		this.addAuxItem(this.getChargedShard(primary), 2, 2);
		this.addAuxItem(this.getChargedShard(primary), 4, 4);

		this.addAuxItem(this.getChargedShard(secondary), 2, 0);
		this.addAuxItem(this.getChargedShard(secondary), -2, 0);
		this.addAuxItem(this.getChargedShard(secondary), 0, 2);
		this.addAuxItem(this.getChargedShard(secondary), 0, -2);

		this.addAuxItem(ChromaStacks.iridCrystal, -4, -4);
		this.addAuxItem(ChromaStacks.iridCrystal, 4, -4);
		this.addAuxItem(ChromaStacks.iridCrystal, -4, 4);
		this.addAuxItem(ChromaStacks.iridCrystal, 4, 4);

		this.addAuxItem(ChromaStacks.fireEssence, -2, -4);
		this.addAuxItem(dust, 4, -2);
		this.addAuxItem(ChromaStacks.fireEssence, 2, 4);
		this.addAuxItem(dust, -4, 2);

		this.addAuxItem(ChromaStacks.enderDust, 2, -4);
		this.addAuxItem(ChromaStacks.spaceDust, -4, -2);
		this.addAuxItem(ChromaStacks.enderDust, -2, 4);
		this.addAuxItem(ChromaStacks.spaceDust, 4, 2);

		this.addAuxItem(Items.diamond, -4, 0);
		this.addAuxItem(Items.ender_pearl, 4, 0);
		this.addAuxItem(Items.emerald, 0, -4);
		this.addAuxItem(Items.gunpowder, 0, 4);

		this.addRune(primary, rune1.xCoord, rune1.yCoord, rune1.zCoord);
		this.addRune(primary, rune2.xCoord, rune2.yCoord, rune2.zCoord);

		this.addAuraRequirement(primary, 5000);
	}

	@Override
	public final boolean canBeSimpleAutomated() {
		return true;
	}

	@Override
	public boolean canGiveDoubleOutput() {
		return true;
	}

}

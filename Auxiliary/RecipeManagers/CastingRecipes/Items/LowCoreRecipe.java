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

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ShardGroupingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class LowCoreRecipe extends MultiBlockCastingRecipe implements ShardGroupingRecipe {

	protected LowCoreRecipe(ItemStack out, CrystalElement primary, CrystalElement secondary, Coordinate rune1, Coordinate rune2, ItemStack dust) {
		super(out, ChromaStacks.crystalCore);

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

		this.addAuxItem(dust, 4, 0);
		this.addAuxItem(dust, -4, 0);
		this.addAuxItem(dust, 0, 4);
		this.addAuxItem(dust, 0, -4);

		this.addRune(primary, rune1.xCoord, rune1.yCoord, rune1.zCoord);
		this.addRune(primary, rune2.xCoord, rune2.yCoord, rune2.zCoord);
	}

	@Override
	public final boolean canBeSimpleAutomated() {
		return true;
	}

	@Override
	public final boolean canGiveDoubleOutput() {
		return true;
	}

}

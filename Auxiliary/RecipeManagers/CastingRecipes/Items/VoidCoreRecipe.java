/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ShardGroupingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class VoidCoreRecipe extends MultiBlockCastingRecipe implements ShardGroupingRecipe {

	public VoidCoreRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), -2, -2);
		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), -4, -4);

		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), 2, -2);
		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), 4, -4);

		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), -2, 2);
		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), -4, 4);

		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), 2, 2);
		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), 4, 4);

		this.addAuxItem(this.getChargedShard(CrystalElement.WHITE), 2, 0);
		this.addAuxItem(this.getChargedShard(CrystalElement.WHITE), -2, 0);
		this.addAuxItem(this.getChargedShard(CrystalElement.WHITE), 0, 2);
		this.addAuxItem(this.getChargedShard(CrystalElement.WHITE), 0, -2);

		this.addRune(CrystalElement.BLACK, 3, -1, -2);
		this.addRune(CrystalElement.BLACK, -3, -1, 2);
	}

}

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

public class EnergyCoreRecipe extends MultiBlockCastingRecipe implements ShardGroupingRecipe {

	public EnergyCoreRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(this.getChargedShard(CrystalElement.YELLOW), -2, -2);
		this.addAuxItem(this.getChargedShard(CrystalElement.YELLOW), -4, -4);

		this.addAuxItem(this.getChargedShard(CrystalElement.YELLOW), 2, -2);
		this.addAuxItem(this.getChargedShard(CrystalElement.YELLOW), 4, -4);

		this.addAuxItem(this.getChargedShard(CrystalElement.YELLOW), -2, 2);
		this.addAuxItem(this.getChargedShard(CrystalElement.YELLOW), -4, 4);

		this.addAuxItem(this.getChargedShard(CrystalElement.YELLOW), 2, 2);
		this.addAuxItem(this.getChargedShard(CrystalElement.YELLOW), 4, 4);

		this.addAuxItem(this.getChargedShard(CrystalElement.WHITE), 2, 0);
		this.addAuxItem(this.getChargedShard(CrystalElement.WHITE), -2, 0);
		this.addAuxItem(this.getChargedShard(CrystalElement.WHITE), 0, 2);
		this.addAuxItem(this.getChargedShard(CrystalElement.WHITE), 0, -2);

		this.addRune(CrystalElement.YELLOW, -3, 0, -2);
		this.addRune(CrystalElement.YELLOW, 3, 0, 2);
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class CrystalFurnaceRecipe extends PylonRecipe {

	public CrystalFurnaceRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuraRequirement(CrystalElement.ORANGE, 8000);
		this.addAuraRequirement(CrystalElement.WHITE, 3000);
		this.addAuraRequirement(CrystalElement.PURPLE, 2000);

		this.addAuxItem(new ItemStack(Blocks.furnace), 2, 0);
		this.addAuxItem(new ItemStack(Blocks.furnace), -2, 0);
		this.addAuxItem(new ItemStack(Blocks.furnace), 0, 2);
		this.addAuxItem(new ItemStack(Blocks.furnace), 0, -2);
	}

}

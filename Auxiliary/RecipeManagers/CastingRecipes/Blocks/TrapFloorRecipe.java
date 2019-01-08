/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class TrapFloorRecipe extends TempleCastingRecipe {

	public TrapFloorRecipe(ItemStack out, int amt, IRecipe recipe) {
		super(ReikaItemHelper.getSizedItemStack(out, amt), recipe);

		this.addRune(CrystalElement.BROWN, 5, 0, -1);
		this.addRune(CrystalElement.LIGHTGRAY, -5, 0, 1);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 5;
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class MultiToolRecipe extends TempleCastingRecipe {

	public MultiToolRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);

		int[] xyz = runeRing.getNthBlock(CrystalElement.PURPLE.ordinal());
		this.addRune(CrystalElement.PURPLE, xyz[0], xyz[1], xyz[2]);

		xyz = runeRing.getNthBlock(CrystalElement.BROWN.ordinal());
		this.addRune(CrystalElement.BROWN, xyz[0], xyz[1], xyz[2]);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 2;
	}

}

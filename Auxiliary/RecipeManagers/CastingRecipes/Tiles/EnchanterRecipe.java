/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class EnchanterRecipe extends TempleCastingRecipe {

	public EnchanterRecipe(ItemStack out, IRecipe in) {
		super(out, in);

		this.addRune(CrystalElement.BLACK, -4, 0, -3);
		this.addRune(CrystalElement.PURPLE, -3, 0, -4);
		this.addRune(CrystalElement.BLACK, 4, 0, 3);
		this.addRune(CrystalElement.PURPLE, 3, 0, 4);
	}

}

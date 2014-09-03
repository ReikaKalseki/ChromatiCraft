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

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

public class CrystalGroupRecipe extends TempleCastingRecipe {

	public CrystalGroupRecipe(ItemStack out, Object... o) {
		super(out, getRecipe(out, o));

		ItemStack[] items = this.getArrayForDisplay();
		int dy = Math.min(1, out.getItemDamage());
		int dl = out.getItemDamage() == 2 ? -2 : (out.getItemDamage() == 3 ? 2 : 0);
		this.addRune(CrystalElement.elements[items[1].getItemDamage()], 0+dl, dy, -3);
		this.addRune(CrystalElement.elements[items[3].getItemDamage()], -3, dy, 0-dl);
		this.addRune(CrystalElement.elements[items[5].getItemDamage()], 3, dy, 0+dl);
		this.addRune(CrystalElement.elements[items[7].getItemDamage()], 0-dl, dy, 3);
	}

	private static IRecipe getRecipe(ItemStack out, Object... o) {
		for (int i = 0; i < o.length; i++) {
			if (o[i] instanceof CrystalElement)
				o[i] = getShard((CrystalElement)o[i]);
		}
		return ReikaRecipeHelper.getShapedRecipeFor(out, o);
	}



}

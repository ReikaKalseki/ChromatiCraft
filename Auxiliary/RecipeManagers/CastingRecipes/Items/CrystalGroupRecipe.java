/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

public class CrystalGroupRecipe extends TempleCastingRecipe {

	public CrystalGroupRecipe(ItemStack out, Object... o) {
		super(out, getRecipe(out, o));

		ItemStack[] items = this.getArrayForDisplay();
		int r = out.getItemDamage() == 0 ? 3 : 4;
		int dl = out.getItemDamage() == 2 ? -2 : (out.getItemDamage() == 3 ? 2 : 0);
		this.addRune(CrystalElement.elements[items[1].getItemDamage()], 0+dl, 0, -r);
		this.addRune(CrystalElement.elements[items[3].getItemDamage()], -r, 0, 0-dl);
		this.addRune(CrystalElement.elements[items[5].getItemDamage()], r, 0, 0+dl);
		this.addRune(CrystalElement.elements[items[7].getItemDamage()], 0-dl, 0, r);
	}

	private static IRecipe getRecipe(ItemStack out, Object... o) {
		for (int i = 0; i < o.length; i++) {
			if (o[i] instanceof CrystalElement)
				o[i] = getShard((CrystalElement)o[i]);
		}
		return ReikaRecipeHelper.getShapedRecipeFor(out, o);
	}



}

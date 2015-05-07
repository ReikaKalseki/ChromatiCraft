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

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ShardGroupingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

public class CrystalGroupRecipe extends TempleCastingRecipe implements ShardGroupingRecipe {

	public CrystalGroupRecipe(ItemStack out, Object... o) {
		super(out, getRecipe(out, o));

		List<ItemStack>[] items = this.getRecipeArray();
		int r = out.getItemDamage() == 0 ? 3 : 4;
		int dl = out.getItemDamage() == 2 ? -2 : (out.getItemDamage() == 3 ? 2 : 0);
		this.addRune(items[1].get(0).getItemDamage(), 0+dl, 0, -r);
		this.addRune(items[3].get(0).getItemDamage(), -r, 0, 0-dl);
		this.addRune(items[5].get(0).getItemDamage(), r, 0, 0+dl);
		this.addRune(items[7].get(0).getItemDamage(), 0-dl, 0, r);
	}

	private static IRecipe getRecipe(ItemStack out, Object... o) {
		for (int i = 0; i < o.length; i++) {
			if (o[i] instanceof CrystalElement)
				o[i] = getShard((CrystalElement)o[i]);
		}
		return ReikaRecipeHelper.getShapedRecipeFor(out, o);
	}



}

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

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ShardGroupingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;

public class CrystalGroupRecipe extends TempleCastingRecipe implements ShardGroupingRecipe {

	public CrystalGroupRecipe(ItemStack out, CrystalElement e1, CrystalElement e2, CrystalElement e3, CrystalElement e4, ItemStack ctr) {
		super(out, getRecipe(out, e1, e2, e3, e4, ctr));

		List<ItemStack>[] items = this.getRecipeArray();
		int r = out.getItemDamage() == 0 ? 3 : 4;
		int dl = out.getItemDamage() == 2 ? -2 : (out.getItemDamage() == 3 ? 2 : 0);
		this.addRune(items[1].get(0).getItemDamage(), 0+dl, 0, -r);
		this.addRune(items[3].get(0).getItemDamage(), -r, 0, 0-dl);
		this.addRune(items[5].get(0).getItemDamage(), r, 0, 0+dl);
		this.addRune(items[7].get(0).getItemDamage(), 0-dl, 0, r);
	}

	private static IRecipe getRecipe(ItemStack out, CrystalElement e1, CrystalElement e2, CrystalElement e3, CrystalElement e4, ItemStack ctr) {
		return ReikaRecipeHelper.getShapedRecipeFor(out, " A ", "BIC", " D ", 'A', getShard(e1), 'B', getShard(e2), 'C', getShard(e3), 'D', getShard(e4), 'I', ctr);
	}



}

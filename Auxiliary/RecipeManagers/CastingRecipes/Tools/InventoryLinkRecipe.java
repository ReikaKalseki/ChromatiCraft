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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class InventoryLinkRecipe extends MultiBlockCastingRecipe {

	public InventoryLinkRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(Items.ender_pearl, -2, 0);
		this.addAuxItem(Items.ender_pearl, 2, 0);
		this.addAuxItem(Items.ender_pearl, 0, 2);
		this.addAuxItem(Items.ender_pearl, 0, -2);

		this.addAuxItem(ReikaItemHelper.lapisDye, -4, 0);
		this.addAuxItem(ReikaItemHelper.lapisDye, 4, 0);
		this.addAuxItem(ReikaItemHelper.lapisDye, 0, 4);
		this.addAuxItem(ReikaItemHelper.lapisDye, 0, -4);

		//this.addAuraRequirement(CrystalElement.LIME, 5000);
		//this.addAuraRequirement(CrystalElement.BLACK, 1000);

		this.addRune(CrystalElement.LIME, -3, -1, -4);
		this.addRune(CrystalElement.BLACK, 3, -1, -4);
	}

}

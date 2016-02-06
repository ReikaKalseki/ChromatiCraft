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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.DragonAPI.ModInteract.ItemHandlers.AppEngHandler;


public class CrystalCellRecipe extends MultiBlockCastingRecipe {

	public CrystalCellRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(new ItemStack(AppEngHandler.getInstance().quartzGlass), -2, -2);
		this.addAuxItem(Items.redstone, 0, -2);
		this.addAuxItem(new ItemStack(AppEngHandler.getInstance().quartzGlass), 2, -2);

		this.addAuxItem(Items.redstone, -2, 0);
		this.addAuxItem(Items.redstone, 2, 0);

		this.addAuxItem(Items.iron_ingot, -2, 2);
		this.addAuxItem(Items.iron_ingot, 0, 2);
		this.addAuxItem(Items.iron_ingot, 2, 2);

		this.addAuxItem(AppEngHandler.getInstance().getGoldProcessor(), 0, 4);
		this.addAuxItem(AppEngHandler.getInstance().getQuartzProcessor(), 0, -4);

		this.addAuxItem(ChromaStacks.energyPowder, -2, -4);
		this.addAuxItem(ChromaStacks.elementDust, 2, -4);

		this.addAuxItem(ChromaStacks.energyPowder, 2, 4);
		this.addAuxItem(ChromaStacks.elementDust, -2, 4);

		this.addAuxItem(ChromaStacks.icyDust, 4, -2);
		this.addAuxItem(ChromaStacks.energyPowder, -4, -2);

		this.addAuxItem(ChromaStacks.icyDust, -4, 2);
		this.addAuxItem(ChromaStacks.energyPowder, 4, 2);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 18; //18 types of compatible items
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.DragonAPI.ModInteract.ItemHandlers.AppEngHandler;

public class MEDistributorRecipe extends MultiBlockCastingRecipe {

	public MEDistributorRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(AppEngHandler.getInstance().getQuartzProcessor(), 0, -4);
		this.addAuxItem(AppEngHandler.getInstance().getQuartzProcessor(), -2, -2);
		this.addAuxItem(AppEngHandler.getInstance().getQuartzProcessor(), 2, -2);

		this.addAuxItem(AppEngHandler.getInstance().getDiamondProcessor(), 0, -2);

		this.addAuxItem(AppEngHandler.getInstance().getGoldProcessor(), -2, 2);
		this.addAuxItem(AppEngHandler.getInstance().getGoldProcessor(), 2, 2);
		this.addAuxItem(AppEngHandler.getInstance().getGoldProcessor(), -2, 4);
		this.addAuxItem(AppEngHandler.getInstance().getGoldProcessor(), 2, 4);

		this.addAuxItem(Items.iron_ingot, -4, 0);
		this.addAuxItem(Items.iron_ingot, -4, 2);
		this.addAuxItem(Items.iron_ingot, -4, 4);
		this.addAuxItem(Items.iron_ingot, 4, 0);
		this.addAuxItem(Items.iron_ingot, 4, 2);
		this.addAuxItem(Items.iron_ingot, 4, 4);

		this.addAuxItem(ChromaStacks.enderIngot, -2, 0);
		this.addAuxItem(ChromaStacks.enderIngot, 2, 0);

		this.addAuxItem(ChromaStacks.auraIngot, -4, -2);
		this.addAuxItem(ChromaStacks.auraIngot, 4, -2);
		this.addAuxItem(ChromaStacks.auraIngot, -2, -4);
		this.addAuxItem(ChromaStacks.auraIngot, 2, -4);
	}

}

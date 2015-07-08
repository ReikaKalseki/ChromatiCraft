/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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

public class TransportWindowRecipe extends MultiBlockCastingRecipe {

	public TransportWindowRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(Items.iron_ingot, -4, -4);
		this.addAuxItem(Items.iron_ingot, -2, -4);
		this.addAuxItem(Items.iron_ingot, 0, -4);
		this.addAuxItem(Items.iron_ingot, 2, -4);
		this.addAuxItem(Items.iron_ingot, 4, -4);

		this.addAuxItem(Items.iron_ingot, -4, 4);
		this.addAuxItem(Items.iron_ingot, -2, 4);
		this.addAuxItem(Items.iron_ingot, 0, 4);
		this.addAuxItem(Items.iron_ingot, 2, 4);
		this.addAuxItem(Items.iron_ingot, 4, 4);

		this.addAuxItem(Items.iron_ingot, -4, -2);
		this.addAuxItem(Items.iron_ingot, 4, -2);
		this.addAuxItem(Items.iron_ingot, -4, 0);
		this.addAuxItem(Items.iron_ingot, 4, 0);
		this.addAuxItem(Items.iron_ingot, -4, 2);
		this.addAuxItem(Items.iron_ingot, 4, 2);

		this.addAuxItem(ChromaStacks.spaceDust, -2, -2);
		this.addAuxItem(ChromaStacks.spaceDust, 2, -2);
		this.addAuxItem(ChromaStacks.spaceDust, -2, 2);
		this.addAuxItem(ChromaStacks.spaceDust, 2, 2);

		this.addAuxItem(ChromaStacks.auraDust, 0, -2);
		this.addAuxItem(ChromaStacks.auraDust, 0, 2);
		this.addAuxItem(ChromaStacks.auraDust, -2, 0);
		this.addAuxItem(ChromaStacks.auraDust, 2, 0);
	}

	@Override
	public int getNumberProduced() {
		return 2;
	}

}

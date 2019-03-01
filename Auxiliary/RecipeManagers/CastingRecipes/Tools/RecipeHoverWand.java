/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class RecipeHoverWand extends PylonCastingRecipe {

	public RecipeHoverWand(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem("stickWood", -2, -2);
		this.addAuxItem("stickWood", 2, -2);
		this.addAuxItem("stickWood", -2, 2);
		this.addAuxItem("stickWood", 2, 2);

		this.addAuxItem(Items.iron_ingot, -2, 0);
		this.addAuxItem(Items.iron_ingot, 2, 0);
		this.addAuxItem(Items.iron_ingot, 0, 2);
		this.addAuxItem(Items.iron_ingot, 0, -2);

		this.addAuxItem(ChromaStacks.getChargedShard(CrystalElement.LIME), -4, -4);
		this.addAuxItem(ChromaStacks.getChargedShard(CrystalElement.LIME), 4, -4);
		this.addAuxItem(ChromaStacks.getChargedShard(CrystalElement.LIME), -4, 4);
		this.addAuxItem(ChromaStacks.getChargedShard(CrystalElement.LIME), 4, 4);

		this.addAuxItem(ChromaStacks.spaceDust, -4, 0);
		this.addAuxItem(ChromaStacks.spaceDust, 4, 0);
		this.addAuxItem(ChromaStacks.spaceDust, 0, 4);
		this.addAuxItem(ChromaStacks.spaceDust, 0, -4);

		this.addAuxItem(ChromaStacks.enderDust, -4, -2);
		this.addAuxItem(ChromaStacks.enderDust, -4, 2);
		this.addAuxItem(ChromaStacks.enderDust, 4, -2);
		this.addAuxItem(ChromaStacks.enderDust, 4, 2);
		this.addAuxItem(ChromaStacks.enderDust, -2, -4);
		this.addAuxItem(ChromaStacks.enderDust, 2, -4);
		this.addAuxItem(ChromaStacks.enderDust, -2, 4);
		this.addAuxItem(ChromaStacks.enderDust, 2, 4);

		this.addAuraRequirement(CrystalElement.BLACK, 2000);
		this.addAuraRequirement(CrystalElement.LIME, 10000);
	}

}

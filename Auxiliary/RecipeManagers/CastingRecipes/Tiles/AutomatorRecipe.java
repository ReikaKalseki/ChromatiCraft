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
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class AutomatorRecipe extends PylonCastingRecipe {

	public AutomatorRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.complexIngot, -4, -4);
		this.addAuxItem(ChromaStacks.complexIngot, 4, -4);
		this.addAuxItem(ChromaStacks.complexIngot, -4, 4);
		this.addAuxItem(ChromaStacks.complexIngot, 4, 4);

		this.addAuxItem(Items.diamond, -2, -4);
		this.addAuxItem(Items.diamond, 2, -4);

		this.addAuxItem(Items.diamond, -2, 4);
		this.addAuxItem(Items.diamond, 2, 4);

		this.addAuxItem(Items.diamond, -4, -2);
		this.addAuxItem(Items.diamond, -4, 2);

		this.addAuxItem(Items.diamond, 4, -2);
		this.addAuxItem(Items.diamond, 4, 2);

		this.addAuxItem(ChromaStacks.chargedWhiteShard, 0, -4);
		this.addAuxItem(ChromaStacks.chargedWhiteShard, 0, 4);
		this.addAuxItem(ChromaStacks.chargedWhiteShard, 4, 0);
		this.addAuxItem(ChromaStacks.chargedWhiteShard, -4, 0);

		this.addAuxItem(ChromaStacks.spaceDust, -2, -2);
		this.addAuxItem(ChromaStacks.spaceDust, -2, 2);
		this.addAuxItem(ChromaStacks.spaceDust, 2, -2);
		this.addAuxItem(ChromaStacks.spaceDust, 2, 2);

		this.addAuxItem(ChromaStacks.enderDust, 0, -2);
		this.addAuxItem(ChromaStacks.enderDust, 0, 2);
		this.addAuxItem(ChromaStacks.enderDust, -2, 0);
		this.addAuxItem(ChromaStacks.enderDust, 2, 0);

		this.addAuraRequirement(CrystalElement.BLACK, 25000);
		this.addAuraRequirement(CrystalElement.PURPLE, 20000);
		this.addAuraRequirement(CrystalElement.WHITE, 10000);
		this.addAuraRequirement(CrystalElement.LIGHTGRAY, 5000);
	}

}

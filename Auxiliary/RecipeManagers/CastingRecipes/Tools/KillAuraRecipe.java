/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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


public class KillAuraRecipe extends PylonCastingRecipe {

	public KillAuraRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		//this.addAuxItem(ChromaItems.LENS.getStackOf(CrystalElement.PINK), 0, -4);

		this.addAuxItem(Items.diamond, -2, -2);
		this.addAuxItem(Items.diamond, -4, -4);

		this.addAuxItem(ChromaStacks.focusDust, -4, -2);
		this.addAuxItem(ChromaStacks.focusDust, -2, -4);

		this.addAuxItem(ChromaStacks.auraDust, 0, -2);
		this.addAuxItem(ChromaStacks.auraDust, -2, 0);

		this.addAuxItem(Items.emerald, -2, 2);
		this.addAuxItem(Items.emerald, 2, -2);

		this.addAuxItem(Items.iron_ingot, 4, 2);
		this.addAuxItem(Items.iron_ingot, 4, 0);
		this.addAuxItem(Items.iron_ingot, 4, -2);
		this.addAuxItem(Items.iron_ingot, 2, 4);
		this.addAuxItem(Items.iron_ingot, 0, 4);
		this.addAuxItem(Items.iron_ingot, -2, 4);

		this.addAuxItem(ChromaStacks.chromaIngot, 2, 2);

		this.addAuxItem(ChromaStacks.chargedBlackShard, -4, 0);
		this.addAuxItem(ChromaStacks.chargedBlackShard, -4, 2);
		this.addAuxItem(ChromaStacks.chargedBlackShard, 0, -4);
		this.addAuxItem(ChromaStacks.chargedBlackShard, 2, -4);

		this.addAuxItem(ChromaStacks.conductiveIngot, 0, 2);
		this.addAuxItem(ChromaStacks.conductiveIngot, 2, 0);

		this.addAuraRequirement(CrystalElement.YELLOW, 60000);
		this.addAuraRequirement(CrystalElement.BLACK, 30000);
		this.addAuraRequirement(CrystalElement.PINK, 80000);
		this.addAuraRequirement(CrystalElement.LIME, 16000);
	}

}

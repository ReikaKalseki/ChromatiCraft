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

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class CloakTowerRecipe extends MultiBlockCastingRecipe {

	public CloakTowerRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.conductiveIngot, 0, -4);
		this.addAuxItem(ChromaStacks.conductiveIngot, 0, 4);

		this.addAuxItem(ChromaStacks.bindingCrystal, 0, -2);
		this.addAuxItem(ChromaStacks.resonanceDust, 0, 2);

		this.addAuxItem(ChromaStacks.focusDust, -2, 0);
		this.addAuxItem(ChromaStacks.focusDust, 2, 0);

		this.addAuxItem(ChromaStacks.auraDust, -2, -4);
		this.addAuxItem(ChromaStacks.auraDust, -2, -2);
		this.addAuxItem(ChromaStacks.auraDust, -2, 2);
		this.addAuxItem(ChromaStacks.auraDust, -2, 4);

		this.addAuxItem(ChromaStacks.auraDust, 2, -4);
		this.addAuxItem(ChromaStacks.auraDust, 2, -2);
		this.addAuxItem(ChromaStacks.auraDust, 2, 2);
		this.addAuxItem(ChromaStacks.auraDust, 2, 4);

		this.addRune(CrystalElement.RED, 4, 0, -3);
		this.addRune(CrystalElement.LIGHTGRAY, -3, 0, 4);
		this.addRune(CrystalElement.BLUE, -4, 0, 3);
		this.addRune(CrystalElement.PINK, 3, 0, -4);
	}

}

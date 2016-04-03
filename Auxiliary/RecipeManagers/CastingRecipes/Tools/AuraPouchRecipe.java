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
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class AuraPouchRecipe extends MultiBlockCastingRecipe {

	public AuraPouchRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(Items.leather, -2, -2);
		this.addAuxItem(Items.leather, 0, -2);
		this.addAuxItem(Items.leather, 2, -2);
		this.addAuxItem(Items.leather, -2, 0);
		this.addAuxItem(Items.leather, 2, 0);
		this.addAuxItem(Items.leather, -2, 2);
		this.addAuxItem(Items.leather, 0, 2);
		this.addAuxItem(Items.leather, 2, 2);

		this.addAuxItem(ChromaStacks.auraDust, -4, 0);
		this.addAuxItem(ChromaStacks.auraDust, 4, 0);
		this.addAuxItem(ChromaStacks.auraDust, 0, -4);
		this.addAuxItem(ChromaStacks.auraDust, 0, 4);

		this.addAuxItem(ChromaStacks.chromaDust, -4, 2);
		this.addAuxItem(ChromaStacks.chromaDust, -4, -2);
		this.addAuxItem(ChromaStacks.chromaDust, 4, 2);
		this.addAuxItem(ChromaStacks.chromaDust, 4, -2);

		this.addAuxItem(ChromaStacks.chromaDust, -2, 4);
		this.addAuxItem(ChromaStacks.chromaDust, -2, -4);
		this.addAuxItem(ChromaStacks.chromaDust, 2, 4);
		this.addAuxItem(ChromaStacks.chromaDust, 2, -4);

		this.addAuxItem(ChromaStacks.chromaIngot, -4, 4);
		this.addAuxItem(ChromaStacks.chromaIngot, -4, -4);
		this.addAuxItem(ChromaStacks.chromaIngot, 4, 4);
		this.addAuxItem(ChromaStacks.chromaIngot, 4, -4);

		this.addRune(CrystalElement.WHITE, -4, -1, 2);
		this.addRune(CrystalElement.BLACK, -4, -1, -2);
		this.addRune(CrystalElement.LIGHTGRAY, 4, -1, 2);
		this.addRune(CrystalElement.PURPLE, 4, -1, -2);
	}

}

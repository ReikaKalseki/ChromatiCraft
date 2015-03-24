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

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class AspectJarRecipe extends PylonRecipe {

	public AspectJarRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(Blocks.glass, -2, 0);
		this.addAuxItem(Blocks.glass, -2, 2);
		this.addAuxItem(Blocks.glass, 2, 0);
		this.addAuxItem(Blocks.glass, 2, 2);

		this.addAuxItem(ChromaStacks.chromaIngot, 0, 2);

		this.addAuxItem(Blocks.wooden_slab, -2, -2);
		this.addAuxItem(Blocks.wooden_slab, 2, -2);

		this.addAuxItem(ChromaStacks.chargedWhiteShard, 0, -2);

		this.addAuxItem(ChromaStacks.chromaDust, -2, -4);
		this.addAuxItem(ChromaStacks.chromaDust, 0, -4);
		this.addAuxItem(ChromaStacks.chromaDust, 2, -4);

		this.addAuraRequirement(CrystalElement.BLACK, 15000);
		this.addAuraRequirement(CrystalElement.WHITE, 5000);
		this.addAuraRequirement(CrystalElement.GRAY, 1000);
		this.addAuraRequirement(CrystalElement.CYAN, 2000);
	}

}

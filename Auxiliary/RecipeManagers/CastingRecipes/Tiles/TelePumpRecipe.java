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

public class TelePumpRecipe extends PylonRecipe {

	public TelePumpRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(new ItemStack(Blocks.glass), -2, 2);
		this.addAuxItem(new ItemStack(Blocks.glass), 0, 2);
		this.addAuxItem(new ItemStack(Blocks.glass), 2, 2);

		this.addAuxItem(new ItemStack(Blocks.glass), -2, -2);
		this.addAuxItem(new ItemStack(Blocks.glass), 0, -2);
		this.addAuxItem(new ItemStack(Blocks.glass), 2, -2);

		this.addAuxItem(ChromaStacks.beaconDust, 2, 0);
		this.addAuxItem(ChromaStacks.beaconDust, -2, 0);

		this.addAuraRequirement(CrystalElement.BLACK, 8000);
		this.addAuraRequirement(CrystalElement.CYAN, 16000);
		this.addAuraRequirement(CrystalElement.LIME, 32000);
	}

}

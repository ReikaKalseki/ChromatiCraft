/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaBlocks;

public class CompoundRelayRecipe extends MultiBlockCastingRecipe {

	public CompoundRelayRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.elementDust, -2, -2);
		this.addAuxItem(ChromaStacks.elementDust, 2, -2);
		this.addAuxItem(ChromaStacks.elementDust, -2, 2);
		this.addAuxItem(ChromaStacks.elementDust, 2, 2);

		this.addAuxItem(ChromaStacks.beaconDust, -2, 0);
		this.addAuxItem(ChromaStacks.beaconDust, 2, 0);
		this.addAuxItem(ChromaStacks.beaconDust, 0, -2);

		this.addAuxItem(new ItemStack(ChromaBlocks.RELAY.getBlockInstance(), 1, OreDictionary.WILDCARD_VALUE), 0, 2);
	}

}

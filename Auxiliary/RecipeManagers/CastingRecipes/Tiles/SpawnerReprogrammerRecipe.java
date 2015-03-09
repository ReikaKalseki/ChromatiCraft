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
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class SpawnerReprogrammerRecipe extends PylonRecipe {

	public SpawnerReprogrammerRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.magicIngot, 0, 2);
		this.addAuxItem(ChromaStacks.magicIngot, -2, 0);
		this.addAuxItem(ChromaStacks.magicIngot, 2, 0);
		this.addAuxItem(ChromaStacks.magicIngot, -4, 0);
		this.addAuxItem(ChromaStacks.magicIngot, 4, 0);

		this.addAuraRequirement(CrystalElement.BLACK, 4000);
		this.addAuraRequirement(CrystalElement.PINK, 16000);
		this.addAuraRequirement(CrystalElement.GRAY, 8000);

		//want more
	}

}

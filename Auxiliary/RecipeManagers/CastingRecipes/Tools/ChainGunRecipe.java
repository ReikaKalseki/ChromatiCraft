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
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class ChainGunRecipe extends MultiBlockCastingRecipe {

	public ChainGunRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(Items.glowstone_dust, -2, -2);
		this.addAuxItem(Items.glowstone_dust, -2, -4);
		this.addAuxItem(Items.glowstone_dust, 2, -2);
		this.addAuxItem(Items.glowstone_dust, 2, -4);

		this.addAuxItem(ChromaItems.LENS.getStackOf(CrystalElement.BLACK), 0, -4);

		this.addAuxItem(Items.diamond, 0, -2);

		this.addAuxItem(ChromaStacks.auraIngot, -2, 0);
		this.addAuxItem(ChromaStacks.auraIngot, 2, 0);
		this.addAuxItem(ChromaStacks.auraIngot, 0, 2);

		this.addAuxItem("stickWood", 0, 4);
	}

}

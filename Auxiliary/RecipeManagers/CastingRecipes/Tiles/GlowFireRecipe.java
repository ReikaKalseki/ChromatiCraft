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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class GlowFireRecipe extends MultiBlockCastingRecipe {

	public GlowFireRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addRuneRingRune(CrystalElement.BLUE);
		this.addRuneRingRune(CrystalElement.ORANGE);
		this.addRuneRingRune(CrystalElement.GRAY);

		this.addAuxItem(Items.glowstone_dust, -2, -2);
		this.addAuxItem(Items.glowstone_dust, 2, -2);
		this.addAuxItem(Items.glowstone_dust, -2, 2);
		this.addAuxItem(Items.glowstone_dust, 2, 2);

		this.addAuxItem(ChromaStacks.firaxite, 0, 2);
		this.addAuxItem(ChromaStacks.firaxite, 0, -2);

		this.addAuxItem(ChromaStacks.energyPowder, 2, 0);
		this.addAuxItem(ChromaStacks.energyPowder, -2, 0);
	}

}

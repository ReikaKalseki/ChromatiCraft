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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.ModInteract.ReikaThaumHelper;

public class AspectFormerRecipe extends MultiBlockCastingRecipe {

	public AspectFormerRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(Items.iron_ingot, -2, -2);
		this.addAuxItem(Items.iron_ingot, 0, -2);
		this.addAuxItem(Items.iron_ingot, 2, -2);

		this.addAuxItem(ChromaStacks.chromaDust, -2, 0);
		this.addAuxItem(ChromaStacks.chromaDust, 2, 0);

		this.addAuxItem(Items.iron_ingot, 2, 2);
		this.addAuxItem(Items.iron_ingot, 2, 4);

		this.addAuxItem(Items.iron_ingot, -2, 2);
		this.addAuxItem(Items.iron_ingot, -2, 4);
	}

	@Override
	public boolean canRunRecipe(EntityPlayer ep) {
		return super.canRunRecipe(ep) && (!ChromaOptions.HARDTHAUM.getState() || ReikaThaumHelper.isResearchComplete(ep, "INFUSION"));
	}

}

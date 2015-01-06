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

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class AuraCleanerRecipe extends PylonRecipe {

	public AuraCleanerRecipe(ItemStack out, ItemStack in) {
		super(out, in);

		this.addAuxItem(new ItemStack(Blocks.glowstone), 0, -2);

		this.addAuxItem(ChromaStacks.getChargedShard(CrystalElement.WHITE), -2, 0);
		this.addAuxItem(ChromaStacks.getChargedShard(CrystalElement.WHITE), 2, 0);
		this.addAuxItem(ChromaStacks.getChargedShard(CrystalElement.WHITE), -2, 2);
		this.addAuxItem(ChromaStacks.getChargedShard(CrystalElement.WHITE), 0, 2);
		this.addAuxItem(ChromaStacks.getChargedShard(CrystalElement.WHITE), 2, 2);

		this.addRune(CrystalElement.BLACK, -3, -1, 3);
		this.addRune(CrystalElement.BLACK, 3, -1, -3);

		this.addRune(CrystalElement.WHITE, 0, -1, -2);
		this.addRune(CrystalElement.WHITE, 0, -1, 2);

		this.addAuxItem(new ItemStack(Items.diamond), -2, 4);
		this.addAuxItem(new ItemStack(Items.diamond), 2, 4);
		this.addAuxItem(new ItemStack(Items.emerald), 0, -4);

		this.addAuraRequirement(CrystalElement.BLACK, 5000);
		this.addAuraRequirement(CrystalElement.WHITE, 5000);
		this.addAuraRequirement(CrystalElement.GRAY, 5000);
		this.addAuraRequirement(CrystalElement.PINK, 5000);
	}

}

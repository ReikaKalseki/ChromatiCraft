/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class RiftRecipe extends PylonCastingRecipe {

	public RiftRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuraRequirement(CrystalElement.BLACK, 5000);
		this.addAuraRequirement(CrystalElement.LIME, 5000);
		this.addAuraRequirement(CrystalElement.YELLOW, 2000);

		this.addAuxItem(ChromaStacks.spaceDust, -2, 0);
		this.addAuxItem(ChromaStacks.spaceDust, 2, 0);
		this.addAuxItem(ChromaStacks.spaceDust, 0, 2);
		this.addAuxItem(ChromaStacks.spaceDust, 0, -2);

		this.addAuxItem(ChromaStacks.spaceDust, -2, -2);
		this.addAuxItem(ChromaStacks.spaceDust, -2, 2);
		this.addAuxItem(ChromaStacks.spaceDust, 2, -2);
		this.addAuxItem(ChromaStacks.spaceDust, 2, 2);

		this.addAuxItem(this.getChargedShard(CrystalElement.LIME), -4, -4);
		this.addAuxItem(this.getChargedShard(CrystalElement.YELLOW), 4, -4);
		this.addAuxItem(this.getChargedShard(CrystalElement.BLUE), -4, 4);
		this.addAuxItem(this.getChargedShard(CrystalElement.LIME), 4, 4);

		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), 0, 4);
		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), 0, -4);
		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), 4, 0);
		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), -4, 0);
	}

	@Override
	public int getDuration() {
		return 200+super.getDuration();
	}

	@Override
	public int getNumberProduced() {
		return 4;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 8;
	}

}

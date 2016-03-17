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
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class VacuumGunRecipe extends PylonRecipe {

	public VacuumGunRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.spaceDust, -2, -2);
		this.addAuxItem(ChromaStacks.spaceDust, -2, -4);
		this.addAuxItem(ChromaStacks.spaceDust, 2, -2);
		this.addAuxItem(ChromaStacks.spaceDust, 2, -4);

		this.addAuxItem(ChromaItems.LENS.getStackOf(CrystalElement.BLACK), 0, -4);

		this.addAuxItem(Items.emerald, 0, -2);

		this.addAuxItem(ChromaStacks.complexIngot, -2, 0);
		this.addAuxItem(ChromaStacks.complexIngot, 2, 0);
		this.addAuxItem(ChromaStacks.complexIngot, 0, 2);

		this.addAuxItem("stickWood", 0, 4);

		this.addAuraRequirement(CrystalElement.BLACK, 10000);
		this.addAuraRequirement(CrystalElement.LIME, 20000);
		this.addAuraRequirement(CrystalElement.PINK, 40000);
	}

}

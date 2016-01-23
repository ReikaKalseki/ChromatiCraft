/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class EnergyCoreRecipe extends LowCoreRecipe {

	public EnergyCoreRecipe(ItemStack out) {
		super(out, CrystalElement.YELLOW, CrystalElement.WHITE, new Coordinate(-3, 0, -2), new Coordinate(3, 0, 2), ChromaStacks.energyPowder);
	}

}

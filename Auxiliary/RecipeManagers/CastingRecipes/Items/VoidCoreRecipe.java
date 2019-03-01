/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
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

public class VoidCoreRecipe extends LowCoreRecipe {

	public VoidCoreRecipe(ItemStack out) {
		super(out, CrystalElement.BLACK, CrystalElement.WHITE, new Coordinate(3, -1, -2), new Coordinate(-3, -1, 2), ChromaStacks.voidDust);
	}

}

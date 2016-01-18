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
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class HighTransformationCoreRecipe extends HighCoreRecipe {

	public HighTransformationCoreRecipe(ItemStack out) {
		super(out, CrystalElement.GRAY, CrystalElement.BLACK, new Coordinate(3, 0, -2), new Coordinate(-3, 0, 2));
	}

}

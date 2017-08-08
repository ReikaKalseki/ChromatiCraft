/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import java.util.Collection;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class RecipeEnderTNT extends PylonCastingRecipe {

	public RecipeEnderTNT(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(Blocks.tnt, 2, 0);
		this.addAuxItem(Blocks.tnt, -2, 0);
		this.addAuxItem(Blocks.tnt, 0, 2);
		this.addAuxItem(Blocks.tnt, 0, -2);

		this.addAuxItem(ReikaItemHelper.stoneBricks, 2, 2);
		this.addAuxItem(ReikaItemHelper.stoneBricks, -2, 2);
		this.addAuxItem(ReikaItemHelper.stoneBricks, 2, -2);
		this.addAuxItem(ReikaItemHelper.stoneBricks, -2, -2);

		Fluid f = FluidRegistry.getFluid("ender");
		Collection<ItemStack> li = ReikaFluidHelper.getAllContainersFor(f);
		if (li.isEmpty()) {
			this.addAuxItem(ChromaItems.BUCKET.getStackOfMetadata(1), 0, 4);
			this.addAuxItem(ChromaItems.BUCKET.getStackOfMetadata(1), 4, 0);
			this.addAuxItem(ChromaItems.BUCKET.getStackOfMetadata(1), -4, 0);
			this.addAuxItem(ChromaItems.BUCKET.getStackOfMetadata(1), 0, -4);
		}
		else {
			this.addAuxItem(f, 0, 4);
			this.addAuxItem(f, 4, 0);
			this.addAuxItem(f, -4, 0);
			this.addAuxItem(f, 0, -4);
		}

		this.addAuxItem(ChromaStacks.enderIngot, 4, 4);
		this.addAuxItem(ChromaStacks.enderIngot, 2, 4);
		this.addAuxItem(ChromaStacks.enderIngot, -2, 4);
		this.addAuxItem(ChromaStacks.enderIngot, -4, 4);

		this.addAuxItem(ChromaStacks.enderIngot, 4, -2);
		this.addAuxItem(ChromaStacks.enderIngot, 4, 2);

		this.addAuxItem(ChromaStacks.enderIngot, -4, -2);
		this.addAuxItem(ChromaStacks.enderIngot, -4, 2);

		this.addAuxItem(ChromaStacks.enderIngot, 4, -4);
		this.addAuxItem(ChromaStacks.enderIngot, 2, -4);
		this.addAuxItem(ChromaStacks.enderIngot, -2, -4);
		this.addAuxItem(ChromaStacks.enderIngot, -4, -4);

		this.addAuraRequirement(CrystalElement.PINK, 500);
		this.addAuraRequirement(CrystalElement.BLACK, 500);
		this.addAuraRequirement(CrystalElement.YELLOW, 1000);
		this.addAuraRequirement(CrystalElement.LIME, 500);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 64;
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class PurifyCrystalRecipe extends PylonRecipe {

	public PurifyCrystalRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addRune(CrystalElement.WHITE, -4, -1, 2);
		this.addRune(CrystalElement.BLACK, 3, -1, 2);
		this.addRune(CrystalElement.BLACK, 0, -1, -3);
		this.addRune(CrystalElement.WHITE, 0, -1, -4);
		this.addRuneRingRune(CrystalElement.RED);
		this.addRuneRingRune(CrystalElement.MAGENTA);
		this.addRuneRingRune(CrystalElement.BLUE);

		this.addAuxItem(ChromaStacks.purityDust, -2, 0);
		this.addAuxItem(ChromaStacks.purityDust, 2, 0);
		this.addAuxItem(ChromaStacks.purityDust, 0, 2);
		this.addAuxItem(ChromaStacks.purityDust, 0, -2);

		this.addAuxItem(ChromaStacks.rawCrystal, 2, -2);
		this.addAuxItem(ChromaStacks.rawCrystal, -2, 2);

		this.addAuxItem(ChromaStacks.purityDust, 2, -4);
		this.addAuxItem(ChromaStacks.purityDust, -2, 4);
		this.addAuxItem(ChromaStacks.purityDust, 4, -2);
		this.addAuxItem(ChromaStacks.purityDust, -4, 2);

		this.addAuxItem(ReikaItemHelper.lapisDye, 0, -4);
		this.addAuxItem(ReikaItemHelper.lapisDye, 0, 4);
		this.addAuxItem(ReikaItemHelper.lapisDye, 4, 0);
		this.addAuxItem(ReikaItemHelper.lapisDye, -4, 0);
		this.addAuxItem(ReikaItemHelper.lapisDye, -2, -2);
		this.addAuxItem(ReikaItemHelper.lapisDye, 2, 2);

		this.addAuxItem(ChromaStacks.energyPowder, -4, -2);
		this.addAuxItem(ChromaStacks.energyPowder, -2, -4);
		this.addAuxItem(ChromaStacks.energyPowder, 4, 2);
		this.addAuxItem(ChromaStacks.energyPowder, 2, 4);

		this.addAuxItem(new ItemStack(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 1, StoneTypes.MULTICHROMIC.ordinal()), -4, -4);
		this.addAuxItem(new ItemStack(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 1, StoneTypes.STABILIZER.ordinal()), 4, 4);

		this.addAuxItem(this.getChargedShard(CrystalElement.BLACK), -4, 4);
		this.addAuxItem(this.getChargedShard(CrystalElement.WHITE), 4, -4);

		this.addAuraRequirement(CrystalElement.BLACK, 40000);
		this.addAuraRequirement(CrystalElement.WHITE, 60000);
		this.addAuraRequirement(CrystalElement.RED, 20000);
		this.addAuraRequirement(CrystalElement.MAGENTA, 20000);
		this.addAuraRequirement(CrystalElement.BLUE, 10000);
	}

	@Override
	public int getDuration() {
		return super.getDuration()*2;
	}

	@Override
	public float getPenaltyMultiplier() {
		return 0;
	}

	@Override
	public int getExperience() {
		return 3*super.getExperience();
	}

}

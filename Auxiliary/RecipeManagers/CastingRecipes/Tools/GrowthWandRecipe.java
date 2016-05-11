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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class GrowthWandRecipe extends MultiBlockCastingRecipe {

	public GrowthWandRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		//for now
		this.addAuxItem("stickWood", -2, -2);
		this.addAuxItem("stickWood", 2, 2);
		this.addAuxItem("stickWood", -2, 2);
		this.addAuxItem("stickWood", 2, -2);

		this.addAuxItem(ChromaStacks.chromaIngot, -2, 0);
		this.addAuxItem(ChromaStacks.chromaIngot, 2, 0);
		this.addAuxItem(ChromaStacks.chromaIngot, 0, 2);
		this.addAuxItem(ChromaStacks.chromaIngot, 0, -2);

		this.addAuxItem(Items.water_bucket, -4, 0);
		this.addAuxItem(Items.water_bucket, 4, 0);

		this.addAuxItem(ChromaStacks.auraDust, 0, -4);
		this.addAuxItem(ChromaStacks.auraDust, 0, 4);

		this.addAuxItem(ChromaStacks.greenShard, -4, -2);
		this.addAuxItem(ChromaStacks.greenShard, 4, 2);
		this.addAuxItem(ChromaStacks.greenShard, -2, -4);
		this.addAuxItem(ChromaStacks.greenShard, 2, 4);

		this.addAuxItem(ChromaStacks.livingEssence, -4, -2);
		this.addAuxItem(ChromaStacks.livingEssence, -2, 4);
		this.addAuxItem(ChromaStacks.livingEssence, 4, 2);
		this.addAuxItem(ChromaStacks.livingEssence, 2, -4);

		this.addAuxItem(ReikaItemHelper.bonemeal, -4, 4);
		this.addAuxItem(ReikaItemHelper.bonemeal, -4, -4);
		this.addAuxItem(ReikaItemHelper.bonemeal, 4, 4);
		this.addAuxItem(ReikaItemHelper.bonemeal, 4, -4);
	}

}

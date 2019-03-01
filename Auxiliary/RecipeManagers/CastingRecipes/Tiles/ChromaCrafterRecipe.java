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

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class ChromaCrafterRecipe extends PylonCastingRecipe {

	public ChromaCrafterRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.GLASS.ordinal()), -2, -4);
		this.addAuxItem(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.GLASS.ordinal()), 0, -4);
		this.addAuxItem(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.GLASS.ordinal()), 2, -4);
		this.addAuxItem(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.GLASS.ordinal()), -2, -2);
		this.addAuxItem(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.GLASS.ordinal()), 2, -2);
		this.addAuxItem(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.GLASS.ordinal()), -2, 0);
		this.addAuxItem(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.GLASS.ordinal()), 2, 0);
		this.addAuxItem(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.GLASS.ordinal()), -2, 2);
		this.addAuxItem(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.GLASS.ordinal()), 0, 2);
		this.addAuxItem(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.GLASS.ordinal()), 2, 2);

		this.addAuxItem(Blocks.hopper, 0, -4);
		this.addAuxItem(ChromaStacks.iridChunk, 0, -2);

		for (int i = -4; i <= 4; i += 2) {
			this.addAuxItem(ChromaStacks.waterDust, 4, i);
			this.addAuxItem(ChromaStacks.waterDust, -4, i);
		}

		this.addAuxItem(ChromaStacks.complexIngot, 0, 4);
		this.addAuxItem(Blocks.obsidian, -2, 4);
		this.addAuxItem(Blocks.obsidian, 2, 4);

		this.addAuraRequirement(CrystalElement.BLACK, 20000);
		this.addAuraRequirement(CrystalElement.PURPLE, 25000);
		this.addAuraRequirement(CrystalElement.GRAY, 10000);

		this.addRuneRingRune(CrystalElement.LIGHTGRAY);
		this.addRune(CrystalElement.WHITE, -4, -1, 2);
		this.addRune(CrystalElement.PURPLE, 0, -1,  -1);
		this.addRuneRingRune(CrystalElement.BLACK);
		this.addRuneRingRune(CrystalElement.BROWN);
	}

}

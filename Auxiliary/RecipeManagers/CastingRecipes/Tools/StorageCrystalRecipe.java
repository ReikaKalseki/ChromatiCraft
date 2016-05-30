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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class StorageCrystalRecipe extends MultiBlockCastingRecipe {

	public StorageCrystalRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+0), -4, -4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+1), -2, -4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+2), 0, -4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+3), 2, -4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+4), 4, -4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+5), 4, -2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+6), 4, 0);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+7), 4, 2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+8), 4, 4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+9), 2, 4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+10), 0, 4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+11), -2, 4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+12), -4, 4);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+13), -4, 2);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+14), -4, 0);
		this.addAuxItem(ChromaItems.SHARD.getStackOfMetadata(16+15), -4, -2);

		if (out.getItemDamage() > 0) {
			this.addAuxItem(ChromaStacks.chromaDust, -4, -4);
			this.addAuxItem(ChromaStacks.chromaDust, -2, -4);
			this.addAuxItem(ChromaStacks.chromaDust, 0, -4);
			this.addAuxItem(ChromaStacks.chromaDust, 2, -4);
			this.addAuxItem(ChromaStacks.chromaDust, 4, -4);
			this.addAuxItem(ChromaStacks.chromaDust, 4, -2);
			this.addAuxItem(ChromaStacks.chromaDust, 4, 0);
			this.addAuxItem(ChromaStacks.chromaDust, 4, 2);
			this.addAuxItem(ChromaStacks.chromaDust, 4, 4);
			this.addAuxItem(ChromaStacks.chromaDust, 2, 4);
			this.addAuxItem(ChromaStacks.chromaDust, 0, 4);
			this.addAuxItem(ChromaStacks.chromaDust, -2, 4);
			this.addAuxItem(ChromaStacks.chromaDust, -4, 4);
			this.addAuxItem(ChromaStacks.chromaDust, -4, 2);
			this.addAuxItem(ChromaStacks.chromaDust, -4, 0);
			this.addAuxItem(ChromaStacks.chromaDust, -4, -2);

			this.addAuxItem(ChromaStacks.resonanceDust, -2, -2);
			this.addAuxItem(ChromaStacks.resonanceDust, 2, -2);
			this.addAuxItem(ChromaStacks.resonanceDust, -2, 2);
			this.addAuxItem(ChromaStacks.resonanceDust, 2, 2);
			this.addAuxItem(ChromaStacks.resonanceDust, 0, -2);
			this.addAuxItem(ChromaStacks.resonanceDust, 0, 2);
			this.addAuxItem(ChromaStacks.resonanceDust, 2, 0);
			this.addAuxItem(ChromaStacks.resonanceDust, -2, 0);
		}
		else {
			this.addAuxItem(ChromaStacks.elementDust, -2, -2);
			this.addAuxItem(ChromaStacks.elementDust, 2, -2);
			this.addAuxItem(ChromaStacks.elementDust, -2, 2);
			this.addAuxItem(ChromaStacks.elementDust, 2, 2);
			this.addAuxItem(ChromaStacks.elementDust, 0, -2);
			this.addAuxItem(ChromaStacks.elementDust, 0, 2);
			this.addAuxItem(ChromaStacks.elementDust, 2, 0);
			this.addAuxItem(ChromaStacks.elementDust, -2, 0);
		}

		this.addRune(CrystalElement.BLUE, 4, 0, 1);
		this.addRune(CrystalElement.BLUE, 4, 0, -1);
		this.addRune(CrystalElement.GREEN, -4, 0, 1);
		this.addRune(CrystalElement.GREEN, -4, 0, -1);
		this.addRune(CrystalElement.RED, 1, 0, -4);
		this.addRune(CrystalElement.RED, -1, 0, -4);
		this.addRune(CrystalElement.YELLOW, 1, 0, 4);
		this.addRune(CrystalElement.YELLOW, -1, 0, 4);
		this.addRune(CrystalElement.WHITE, 4, 0, -4);
		this.addRune(CrystalElement.WHITE, -4, 0, 4);
		this.addRune(CrystalElement.BLACK, -4, 0, -4);
		this.addRune(CrystalElement.BLACK, 4, 0, 4);
	}

	@Override
	public int getDuration() {
		int n = ReikaMathLibrary.intpow2(2, this.getOutput().getItemDamage());
		return n*super.getDuration()/2;
	}

	@Override
	public void onCrafted(TileEntityCastingTable te, EntityPlayer ep) {
		super.onCrafted(te, ep);
		ProgressStage.STORAGE.stepPlayerTo(ep);
	}

	@Override
	public NBTTagCompound getOutputTag(NBTTagCompound input) {
		return input;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 4;
	}

}

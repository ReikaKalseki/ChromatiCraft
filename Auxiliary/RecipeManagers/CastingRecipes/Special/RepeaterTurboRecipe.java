/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;

public class RepeaterTurboRecipe extends PylonRecipe {

	public RepeaterTurboRecipe() {
		super(ChromaTiles.REPEATER.getCraftedProduct(), getOutputItem());

		this.addAuraRequirement(CrystalElement.BLACK, 25000);
		this.addAuraRequirement(CrystalElement.WHITE, 20000);
		this.addAuraRequirement(CrystalElement.PURPLE, 40000);
		this.addAuraRequirement(CrystalElement.BLUE, 50000);
		this.addAuraRequirement(CrystalElement.YELLOW, 75000);
		this.addAuraRequirement(CrystalElement.GRAY, 10000);
	}

	private static ItemStack getOutputItem() {
		ItemStack is = ChromaTiles.REPEATER.getCraftedProduct();
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setBoolean("boosted", true);
		return is;
	}

	@Override
	public boolean isIndexed() {
		return false;
	}

	@Override
	public void onRecipeTick(TileEntityCastingTable te) {

	}

	@Override
	public ChromaSounds getSoundOverride(int soundTimer) {
		return null;
	}

	@Override
	protected void getRequiredProgress(Collection<ProgressStage> c) {
		c.add(ProgressStage.CTM);
	}

	@Override
	public int getExperience() {
		return super.getExperience()*20;
	}

	@Override
	public int getDuration() {
		return super.getDuration()*8;
	}

}

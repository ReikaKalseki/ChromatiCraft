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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.EnergyLinkingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;

public class RepeaterTurboRecipe extends PylonRecipe implements EnergyLinkingRecipe {

	public RepeaterTurboRecipe(ChromaTiles rpt, int baseAura) {
		super(getOutputItem(rpt), rpt.getCraftedProduct());

		this.addAuraRequirement(CrystalElement.BLACK, 5*baseAura);
		this.addAuraRequirement(CrystalElement.WHITE, 4*baseAura);
		this.addAuraRequirement(CrystalElement.PURPLE, 8*baseAura);
		this.addAuraRequirement(CrystalElement.BLUE, 10*baseAura);
		this.addAuraRequirement(CrystalElement.YELLOW, 15*baseAura);
		this.addAuraRequirement(CrystalElement.GRAY, 2*baseAura);

		this.addAuxItem(ChromaStacks.glowbeans, -2, -2);
		this.addAuxItem(ChromaStacks.glowbeans, 2, -2);
		this.addAuxItem(ChromaStacks.glowbeans, -2, 2);
		this.addAuxItem(ChromaStacks.glowbeans, 2, 2);

		this.addAuxItem(ChromaStacks.boostroot, 0, 2);
		this.addAuxItem(ChromaStacks.boostroot, 0, -2);
		this.addAuxItem(ChromaStacks.boostroot, 2, 0);
		this.addAuxItem(ChromaStacks.boostroot, -2, 0);

		this.addAuxItem(ChromaStacks.beaconDust, -4, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 4);
		this.addAuxItem(ChromaStacks.beaconDust, -4, 4);
		this.addAuxItem(ChromaStacks.beaconDust, 4, -4);

		this.addAuxItem(ChromaStacks.focusDust, 0, -4);
		this.addAuxItem(ChromaStacks.focusDust, 4, 0);
		this.addAuxItem(ChromaStacks.focusDust, 0, 4);
		this.addAuxItem(ChromaStacks.focusDust, -4, 0);

		this.addAuxItem(Items.glowstone_dust, -2, -4);
		this.addAuxItem(Items.glowstone_dust, 2, -4);
		this.addAuxItem(Items.glowstone_dust, 4, -2);
		this.addAuxItem(Items.glowstone_dust, 4, 2);
		this.addAuxItem(Items.glowstone_dust, 2, 4);
		this.addAuxItem(Items.glowstone_dust, -2, 4);
		this.addAuxItem(Items.glowstone_dust, -4, 2);
		this.addAuxItem(Items.glowstone_dust, -4, -2);
	}

	private static ItemStack getOutputItem(ChromaTiles rpt) {
		ItemStack is = rpt.getCraftedProduct();
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

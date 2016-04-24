/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.EnergyLinkingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;


public class LumenBroadcastRecipe extends MultiBlockCastingRecipe implements EnergyLinkingRecipe {

	public LumenBroadcastRecipe(ItemStack out, ItemStack main, RecipeCrystalRepeater repeater) {
		super(out, main);

		this.addAuxItem(ChromaTiles.COMPOUND.getCraftedProduct(), -2, -2);
		this.addAuxItem(ChromaTiles.COMPOUND.getCraftedProduct(), 0, -2);
		this.addAuxItem(ChromaTiles.COMPOUND.getCraftedProduct(), 2, -2);

		this.addAuxItem(ChromaTiles.COMPOUND.getCraftedProduct(), -2, 2);
		this.addAuxItem(ChromaTiles.COMPOUND.getCraftedProduct(), 2, 2);

		this.addAuxItem(ChromaTiles.COMPOUND.getCraftedProduct(), -2, 2);
		this.addAuxItem(ChromaTiles.COMPOUND.getCraftedProduct(), 0, 2);
		this.addAuxItem(ChromaTiles.COMPOUND.getCraftedProduct(), 2, 2);

		this.addAuxItem(ChromaStacks.beaconDust, -4, -4);
		this.addAuxItem(ChromaStacks.beaconDust, -2, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 0, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 2, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 4, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 4, -2);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 0);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 2);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 4);
		this.addAuxItem(ChromaStacks.beaconDust, 2, 4);
		this.addAuxItem(ChromaStacks.beaconDust, 0, 4);
		this.addAuxItem(ChromaStacks.beaconDust, -2, 4);
		this.addAuxItem(ChromaStacks.beaconDust, -4, 4);
		this.addAuxItem(ChromaStacks.beaconDust, -4, 2);
		this.addAuxItem(ChromaStacks.beaconDust, -4, 0);
		this.addAuxItem(ChromaStacks.beaconDust, -4, -2);

		this.addRunes(repeater.getRunes());
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 2;
	}

	@Override
	public int getDuration() {
		return 8*super.getDuration();
	}

	@Override
	public int getExperience() {
		return super.getExperience();
	}

	@Override
	public boolean canRunRecipe(EntityPlayer ep) {
		return super.canRunRecipe(ep) && ChromaResearchManager.instance.getPlayerResearchLevel(ep).ordinal() >= ResearchLevel.NETWORKING.ordinal();
	}

	@Override
	public void onCrafted(TileEntityCastingTable te, EntityPlayer ep) {
		super.onCrafted(te, ep);
	}

}

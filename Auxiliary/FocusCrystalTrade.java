/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFocusCrystal.CrystalTier;
import Reika.DragonAPI.Interfaces.PlayerSpecificTrade;


public class FocusCrystalTrade extends MerchantRecipe implements PlayerSpecificTrade {

	public FocusCrystalTrade() {
		super(new ItemStack(Items.emerald, 1, 0), CrystalTier.FLAWED.getCraftedItem());
	}

	@Override
	public void incrementToolUses() {
		//No-op to prevent expiry
	}

	@Override
	public boolean isValid(EntityPlayer ep) {
		return ProgressionManager.ProgressStage.CRYSTALS.isPlayerAtStage(ep);
	}

}

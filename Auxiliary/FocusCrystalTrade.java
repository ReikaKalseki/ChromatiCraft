package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFocusCrystal.CrystalTier;


public class FocusCrystalTrade extends MerchantRecipe {

	public FocusCrystalTrade() {
		super(new ItemStack(Items.emerald, 1, 0), CrystalTier.FLAWED.getCraftedItem());
	}

	@Override
	public void incrementToolUses() {
		//No-op to prevent expiry
	}

}

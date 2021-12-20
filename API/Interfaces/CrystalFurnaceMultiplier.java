package Reika.ChromatiCraft.API.Interfaces;

import net.minecraft.item.ItemStack;

public interface CrystalFurnaceMultiplier {

	public int getMultiplyRateAsInput(ItemStack is, ItemStack to);
	public int getMultiplyRateAsOutput(ItemStack is, ItemStack from);

}

package Reika.ChromatiCraft.Auxiliary.Interfaces;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface ItemOnRightClick extends IInventory {

	public void onRightClickWith(ItemStack item);

}

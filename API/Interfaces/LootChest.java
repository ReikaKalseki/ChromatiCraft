package Reika.ChromatiCraft.API.Interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;

public interface LootChest extends ISidedInventory {


	public boolean isOwnedBy(EntityPlayer ep);

	public boolean isAccessibleBy(EntityPlayer ep);

	public boolean isUntouchedWorldgen();

}

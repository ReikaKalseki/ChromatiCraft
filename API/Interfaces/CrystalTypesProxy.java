package Reika.ChromatiCraft.API.Interfaces;

import net.minecraft.item.ItemStack;


/** Implement this on an item class in order to mark it as "crystal element types" (16 colors, tied to the elements, etc)
 * and to allow the crystal cell to store it. */
public interface CrystalTypesProxy {

	public boolean isCrystalType(ItemStack is);

}

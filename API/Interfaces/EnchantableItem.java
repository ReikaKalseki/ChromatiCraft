package Reika.ChromatiCraft.API.Interfaces;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;


public interface EnchantableItem {

	public boolean isEnchantValid(Enchantment e, ItemStack is);

}

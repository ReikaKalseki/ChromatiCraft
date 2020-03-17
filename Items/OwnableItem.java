package Reika.ChromatiCraft.Items;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface OwnableItem {

	public boolean isUsableBy(ItemStack is, EntityPlayer ep);

	public boolean isCollectableBy(EntityItem ei, EntityPlayer ep);

}

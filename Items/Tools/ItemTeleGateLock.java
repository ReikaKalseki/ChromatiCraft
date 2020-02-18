package Reika.ChromatiCraft.Items.Tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ItemChromaTool;

public class ItemTeleGateLock extends ItemChromaTool {

	public ItemTeleGateLock(int index) {
		super(index);

		maxStackSize = 32;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		return is;
	}

}

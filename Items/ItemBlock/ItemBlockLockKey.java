package Reika.ChromatiCraft.Items.ItemBlock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Block.Dimension.BlockLockKey;

public class ItemBlockLockKey extends ItemBlockMultiType {

	public ItemBlockLockKey(Block b) {
		super(b);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		li.add("Channel: "+BlockLockKey.LockChannel.lockList[is.getItemDamage()].name);
	}

}

package Reika.ChromatiCraft.Base;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockMultiType;
import Reika.ChromatiCraft.Registry.ChromaTiles;


public class ItemBlockTileRegistry extends ItemBlockMultiType {

	public ItemBlockTileRegistry(Block b) {
		super(b);
	}

	@Override
	public final String getItemStackDisplayName(ItemStack is) {
		ChromaTiles c = ChromaTiles.getTileByCraftedItem(is);
		if (c == null)
			c = ChromaTiles.getTileFromIDandMetadata(field_150939_a, is.getItemDamage());
		return c.getName();
	}

}

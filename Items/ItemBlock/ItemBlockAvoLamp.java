package Reika.ChromatiCraft.Items.ItemBlock;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Block.BlockAvoLamp;
import Reika.ChromatiCraft.Registry.ChromaBlocks;


public class ItemBlockAvoLamp extends ItemBlock {

	public ItemBlockAvoLamp(Block b) {
		super(b);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		boolean flag = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		if (flag) {
			((BlockAvoLamp)field_150939_a).placeOnSide(world, x, y, z, side);
		}
		return flag;
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		return ChromaBlocks.AVOLAMP.getBasicName();
	}

}

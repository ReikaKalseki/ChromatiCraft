package Reika.ChromatiCraft.Items.ItemBlock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Block.Decoration.BlockRangedLamp.TileEntityRangedLamp;

public class ItemBlockRangedLamp extends ItemBlockDyeTypes {

	public ItemBlockRangedLamp(Block b) {
		super(b);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (is.getItemDamage() >= 16) {
			li.add("Flat Panel");
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer ep, World world, int x, int y, int z, int side, float a, float b, float c, int metadata) {
		if (!world.setBlock(x, y, z, field_150939_a, metadata, 3))
			return false;

		if (world.getBlock(x, y, z) == field_150939_a) {
			if (stack.getItemDamage() >= 16)
				((TileEntityRangedLamp)world.getTileEntity(x, y, z)).setPanel(ForgeDirection.VALID_DIRECTIONS[side].getOpposite());
			field_150939_a.onBlockPlacedBy(world, x, y, z, ep, stack);
			field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
		}

		return true;
	}

}

package Reika.ChromatiCraft.Items.ItemBlock;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Block.BlockCrystalTank.CrystalTankAuxTile;

public class ItemBlockCrystalTank extends ItemBlockMultiType {

	public ItemBlockCrystalTank(Block b) {
		super(b);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer ep, World world, int x, int y, int z, int side, float a, float b, float c, int metadata) {
		if (!world.setBlock(x, y, z, field_150939_a, metadata, 3))
			return false;

		if (world.getBlock(x, y, z) == field_150939_a) {
			field_150939_a.onBlockPlacedBy(world, x, y, z, ep, stack);
			field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
			CrystalTankAuxTile te = (CrystalTankAuxTile)world.getTileEntity(x, y, z);
			te.setFlags(stack);
		}

		return true;
	}

}

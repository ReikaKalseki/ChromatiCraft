package Reika.ChromatiCraft.TileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;

public class TileEntityInventoryTicker extends InventoriedChromaticBase {

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return side == ForgeDirection.DOWN.ordinal();
	}

	@Override
	public int getSizeInventory() {
		return 36;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return true;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.TICKER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.updateItems();
	}

	private void updateItems() {
		EntityPlayer ep = this.getPlacer();
		if (ep != null) {
			for (int i = 0; i < this.getSizeInventory(); i++) {
				ItemStack is = inv[i];
				if (is != null) {
					is.getItem().onUpdate(is, worldObj, ep, 0, true); //trick into thinking is slot 0 (hotbar) and held, to force activation
				}
			}
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}

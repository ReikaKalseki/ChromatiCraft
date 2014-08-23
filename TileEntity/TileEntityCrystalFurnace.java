package Reika.ChromatiCraft.TileEntity;

import Reika.ChromatiCraft.Base.TileEntity.ChargedCrystalPowered;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TileEntityCrystalFurnace extends ChargedCrystalPowered {

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		if (slot == 0)
			return this.getStoredEnergy() == 0;
		return slot == 2;
	}

	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		if (slot == 0)
			return ChromaItems.STORAGE.matchWith(is);
		return false;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FURNACE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}

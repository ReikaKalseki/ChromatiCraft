package Reika.ChromatiCraft.TileEntity.Recipe;

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Magic.ElementBufferCapacityBoost;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class TileEntityPlayerInfuser extends TileEntityAuraInfuser {

	@Override
	public int getInventoryStackLimit() {
		return 8;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.PLAYERINFUSER;
	}

	@Override
	protected ChromaStructures getStructure() {
		return ChromaStructures.PLAYERINFUSION;
	}

	@Override
	protected void collectFocusCrystalLocations(FilledBlockArray arr) {
		for (Coordinate c : arr.keySet()) {
			if (arr.getBlockAt(c.xCoord, c.yCoord, c.zCoord) == ChromaBlocks.PYLONSTRUCT.getBlockInstance()) {
				if (arr.getMetaAt(c.xCoord, c.yCoord, c.zCoord) == StoneTypes.STABILIZER.ordinal()) {
					Coordinate c2 = c.offset(0, 1, 0);
					focusCrystalSpots.add(c2);
				}
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return this.getEffect(is) != null;
	}

	@Override
	protected boolean isReady() {
		if (inv[0] == null || inv[0].stackSize < 8)
			return false;
		ElementBufferCapacityBoost e = this.getEffect(inv[0]);
		return e != null && ElementBufferCapacityBoost.getAvailableBoosts(this.getCraftingPlayer()).contains(e);
	}

	private ElementBufferCapacityBoost getEffect(ItemStack is) {
		for (ElementBufferCapacityBoost e : ElementBufferCapacityBoost.list) {
			ItemStack is2 = e.getIngredient();
			if (is2 != null) {
				if (ReikaItemHelper.matchStacks(is, is2))
					return e;
			}
		}
		return null;
	}

	@Override
	protected void onCraft() {
		inv[0] = null;
	}

}

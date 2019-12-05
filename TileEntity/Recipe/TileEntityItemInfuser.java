package Reika.ChromatiCraft.TileEntity.Recipe;

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class TileEntityItemInfuser extends TileEntityAuraInfuser {

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.INFUSER;
	}

	@Override
	protected ChromaStructures getStructure() {
		return ChromaStructures.INFUSION;
	}

	@Override
	protected void collectFocusCrystalLocations(FilledBlockArray arr) {
		for (Coordinate c : arr.keySet()) {
			if (c.yCoord == yCoord-1 && c.getTaxicabDistanceTo(new Coordinate(this)) > 2) {
				if (arr.getBlockAt(c.xCoord, c.yCoord, c.zCoord) == ChromaBlocks.PYLONSTRUCT.getBlockInstance()) {
					Coordinate c2 = c.offset(0, 1, 0);
					focusCrystalSpots.add(c2);
				}
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return ReikaItemHelper.matchStacks(is, ChromaStacks.rawCrystal);
	}

	@Override
	protected boolean isReady() {
		return ReikaItemHelper.matchStacks(inv[0], ChromaStacks.rawCrystal);
	}

	@Override
	protected void onCraft() {
		inv[0] = ReikaItemHelper.getSizedItemStack(ChromaStacks.iridCrystal, inv[0].stackSize);
		ProgressStage.INFUSE.stepPlayerTo(this.getCraftingPlayer());
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

}

package Reika.ChromatiCraft.API;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;
import Reika.ChromatiCraft.API.Interfaces.AdjacencyCheckHandler;
import Reika.ChromatiCraft.API.Interfaces.CustomAcceleration;
import Reika.ChromatiCraft.API.Interfaces.CustomHealing.CustomBlockHealing;
import Reika.ChromatiCraft.API.Interfaces.CustomHealing.CustomTileHealing;
import Reika.ChromatiCraft.API.Interfaces.CustomRangeUpgrade;
import Reika.ChromatiCraft.API.Interfaces.CustomRangeUpgrade.RangeUpgradeable;

public interface AdjacencyUpgradeAPI {

	/** Fetch a {@link AdjacencyCheckHandler}, for a given color. Supply what the effect will do, and the item versions of the blocks it applies to. */
	public AdjacencyCheckHandler createCheckHandler(CrystalElementProxy color, String desc, ItemStack... items);

	public double getFactor(CrystalElementProxy e, int tier);

	public void addCustomAcceleration(Class<? extends TileEntity> c, CustomAcceleration a);

	public void addCustomRangeBoost(Class<? extends TileEntity> c, CustomRangeUpgrade a);
	/** The ItemStacks here are the item forms of the relevant machines, used the same way as the "relevant items" on the full handlers */
	public void addBasicRangeBoost(Class<? extends RangeUpgradeable> c, ItemStack... items);

	public void addCustomHealing(Block b, CustomBlockHealing h);
	public void addCustomHealing(Block b, int meta, CustomBlockHealing h);
	public void addCustomHealing(Class<? extends TileEntity> c, CustomTileHealing h);
}

package Reika.ChromatiCraft.API;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;
import Reika.ChromatiCraft.API.Interfaces.CustomAcceleration;
import Reika.ChromatiCraft.API.Interfaces.CustomHealing.CustomBlockHealing;
import Reika.ChromatiCraft.API.Interfaces.CustomHealing.CustomTileHealing;
import Reika.ChromatiCraft.API.Interfaces.CustomRangeUpgrade;
import Reika.ChromatiCraft.API.Interfaces.CustomRangeUpgrade.RangeUpgradeable;

public interface AdjacencyUpgradeAPI {

	public Map<CrystalElementProxy, Integer> getAdjacentUpgrades(World world, int x, int y, int z);

	/** Returns 1 though 8 for adjacent upgrades tiers 0-7 and 0 for none. */
	public int getAdjacentUpgradeTier(World world, int x, int y, int z, CrystalElementProxy e);

	public double getFactor(CrystalElementProxy e, int tier);

	public double getFactorSimple(World world, int x, int y, int z, int color);

	public double getFactorSimple(World world, int x, int y, int z, String color);

	public void addCustomAcceleration(Class<? extends TileEntity> c, CustomAcceleration a);

	public void addCustomRangeBoost(Class<? extends TileEntity> c, CustomRangeUpgrade a);
	/** The ItemStacks here are the item forms of the relevant machines, used the same way as the "relevant items" on the full handlers */
	public void addBasicRangeBoost(Class<? extends RangeUpgradeable> c, ItemStack... items);

	public void addCustomHealing(Block b, CustomBlockHealing h);
	public void addCustomHealing(Block b, int meta, CustomBlockHealing h);
	public void addCustomHealing(Class<? extends TileEntity> c, CustomTileHealing h);
}

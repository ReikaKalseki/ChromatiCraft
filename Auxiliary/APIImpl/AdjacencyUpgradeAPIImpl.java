package Reika.ChromatiCraft.Auxiliary.APIImpl;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AdjacencyUpgradeAPI;
import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;
import Reika.ChromatiCraft.API.Interfaces.AdjacencyCheckHandler;
import Reika.ChromatiCraft.API.Interfaces.CustomAcceleration;
import Reika.ChromatiCraft.API.Interfaces.CustomHealing.CustomBlockHealing;
import Reika.ChromatiCraft.API.Interfaces.CustomHealing.CustomTileHealing;
import Reika.ChromatiCraft.API.Interfaces.CustomRangeUpgrade;
import Reika.ChromatiCraft.API.Interfaces.CustomRangeUpgrade.RangeUpgradeable;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.AdjacencyUpgrades;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityAccelerator;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityHealingCore;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityRangeBoost;


public class AdjacencyUpgradeAPIImpl implements AdjacencyUpgradeAPI {
	/*
	@Override
	public Map<CrystalElementProxy, Integer> getAdjacentUpgrades(World world, int x, int y, int z) {
		Map<CrystalElement, Integer> map = TileEntityAdjacencyUpgrade.getAdjacentUpgrades(world, x, y, z);
		Map<CrystalElementProxy, Integer> ret = new HashMap();
		for (Entry<CrystalElement, Integer> e : map.entrySet()) {
			ret.put(e.getKey(), e.getValue());
		}
		return ret;
	}

	public int getAdjacentUpgradeTier(World world, int x, int y, int z, CrystalElementProxy e) {
		Integer get = this.getAdjacentUpgrades(world, x, y, z).get(e);
		return get != null ? get.intValue() : 0;
	}
	 */
	public double getFactor(CrystalElementProxy e, int tier) {
		return AdjacencyUpgrades.upgrades[e.ordinal()].getFactor(tier-1);
	}
	/*
	public double getFactorSimple(World world, int x, int y, int z, int color) {
		CrystalElementProxy e = CrystalElementAccessor.getByIndex(color);
		int tier = this.getAdjacentUpgradeTier(world, x, y, z, e);
		return tier > 0 ? this.getFactor(e, tier) : 1;
	}

	public double getFactorSimple(World world, int x, int y, int z, String color) {
		CrystalElementProxy e = CrystalElementAccessor.getByEnum(color);
		int tier = this.getAdjacentUpgradeTier(world, x, y, z, e);
		return tier > 0 ? this.getFactor(e, tier) : 1;
	}
	 */
	@Override
	public void addCustomAcceleration(Class<? extends TileEntity> c, CustomAcceleration a) {
		TileEntityAccelerator.customizeTile(c, a);
	}

	@Override
	public void addCustomRangeBoost(Class<? extends TileEntity> c, CustomRangeUpgrade a) {
		TileEntityRangeBoost.customizeTile(c, a);
	}

	@Override
	public void addBasicRangeBoost(Class<? extends RangeUpgradeable> c, ItemStack... items) {
		TileEntityRangeBoost.addBasicHandling(c, items);
	}

	@Override
	public void addCustomHealing(Block b, CustomBlockHealing h) {
		TileEntityHealingCore.addBlockHandler(b, h);
	}

	@Override
	public void addCustomHealing(Block b, int meta, CustomBlockHealing h) {
		TileEntityHealingCore.addBlockHandler(b, meta, h);
	}

	@Override
	public void addCustomHealing(Class<? extends TileEntity> c, CustomTileHealing h) {
		TileEntityHealingCore.addTileHandler(c, h);
	}

	@Override
	public AdjacencyCheckHandler createCheckHandler(CrystalElementProxy color, String desc, ItemStack... items) {
		return TileEntityAdjacencyUpgrade.createAdjacencyCheckHandler(color, desc, items);
	}

	public void addAcceleratorBlacklist(Class<? extends TileEntity> cl, String name, ItemStack item, BlacklistReason r) {
		TileEntityAccelerator.blacklistTile(cl, item);
		ChromatiCraft.logger.log("TileEntity \""+name+"\" has been blacklisted from the TileEntity Accelerator, because "+r.message);
	}

	public void addAcceleratorBlacklist(Class<? extends TileEntity> cl, ItemStack item, BlacklistReason r) {
		this.addAcceleratorBlacklist(cl, cl.getSimpleName(), item, r);
	}

}

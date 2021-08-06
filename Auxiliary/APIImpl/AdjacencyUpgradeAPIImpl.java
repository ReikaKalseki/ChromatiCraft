package Reika.ChromatiCraft.Auxiliary.APIImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.world.World;

import Reika.ChromatiCraft.API.AdjacencyUpgradeAPI;
import Reika.ChromatiCraft.API.CrystalElementAccessor;
import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.AdjacencyUpgrades;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class AdjacencyUpgradeAPIImpl implements AdjacencyUpgradeAPI {

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

	public double getFactor(CrystalElementProxy e, int tier) {
		return AdjacencyUpgrades.upgrades[e.ordinal()].getFactor(tier-1);
	}

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

}

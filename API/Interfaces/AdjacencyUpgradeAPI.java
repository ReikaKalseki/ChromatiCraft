package Reika.ChromatiCraft.API.Interfaces;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.World;

import Reika.ChromatiCraft.API.CrystalElementAccessor;
import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;

public class AdjacencyUpgradeAPI {

	private static Class adjacencyTile;
	private static Method getAdjUpgrades;

	private static Class adjacencyUpgrades;
	private static Method getTierFactor;

	public static Map<CrystalElementProxy, Integer> getAdjacentUpgrades(World world, int x, int y, int z) {
		try {
			return (Map)getAdjUpgrades.invoke(null, world, x, y, z);
		}
		catch (Exception e) {
			e.printStackTrace();
			return new HashMap();
		}
	}

	public static int getAdjacentUpgradeTier(World world, int x, int y, int z, CrystalElementProxy e) {
		Integer get = getAdjacentUpgrades(world, x, y, z).get(e);
		return get != null ? get.intValue() : 0;
	}

	public static double getFactor(CrystalElementProxy e, int tier) {
		try {
			Object upgrade = Enum.valueOf(adjacencyUpgrades, ((Enum)e).name());
			return (double)getTierFactor.invoke(upgrade, tier-1);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return 1;
		}
	}

	public static double getFactorSimple(World world, int x, int y, int z, int color) {
		CrystalElementProxy e = CrystalElementAccessor.getByIndex(color);
		int tier = getAdjacentUpgradeTier(world, x, y, z, e);
		return tier > 0 ? getFactor(e, tier) : 1;
	}

	public static double getFactorSimple(World world, int x, int y, int z, String color) {
		CrystalElementProxy e = CrystalElementAccessor.getByEnum(color);
		int tier = getAdjacentUpgradeTier(world, x, y, z, e);
		return tier > 0 ? getFactor(e, tier) : 1;
	}

	static {
		try {
			adjacencyTile = Class.forName("Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade");
			getAdjUpgrades = adjacencyTile.getDeclaredMethod("getAdjacentUpgrades", World.class, int.class, int.class, int.class);

			adjacencyUpgrades = Class.forName("Reika.ChromatiCraft.Registry.AdjacencyUpgrades");
			getTierFactor = adjacencyUpgrades.getDeclaredMethod("getFactor", int.class);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

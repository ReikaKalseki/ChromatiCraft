package Reika.ChromatiCraft.API;

import java.util.Map;

import net.minecraft.world.World;

import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;

public interface AdjacencyUpgradeAPI {

	public Map<CrystalElementProxy, Integer> getAdjacentUpgrades(World world, int x, int y, int z);

	public int getAdjacentUpgradeTier(World world, int x, int y, int z, CrystalElementProxy e);

	public double getFactor(CrystalElementProxy e, int tier);

	public double getFactorSimple(World world, int x, int y, int z, int color);

	public double getFactorSimple(World world, int x, int y, int z, String color);
}

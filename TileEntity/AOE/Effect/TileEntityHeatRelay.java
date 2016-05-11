/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Effect;

import java.util.HashSet;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Maps.BlockMap;
import Reika.DragonAPI.Interfaces.TileEntity.ThermalTile;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.ReactorCraft.TileEntities.Processing.TileEntityElectrolyzer;
import Reika.ReactorCraft.TileEntities.Processing.TileEntitySynthesizer;
import Reika.RotaryCraft.TileEntities.Farming.TileEntityComposter;
import Reika.RotaryCraft.TileEntities.Processing.TileEntityCompactor;
import Reika.RotaryCraft.TileEntities.Processing.TileEntityCrystallizer;
import Reika.RotaryCraft.TileEntities.Production.TileEntityBlastFurnace;
import Reika.RotaryCraft.TileEntities.Production.TileEntityFermenter;


public class TileEntityHeatRelay extends TileEntityAdjacencyUpgrade {

	private static final BlockMap<Integer> blockTemps = new BlockMap();
	private static final HashSet<String> tileList = new HashSet();

	private static final double[] factors = {
		0.03125,
		0.0625,
		0.125,
		0.25,
		0.375,
		0.5,
		0.75,
		1,
	};

	static {
		blockTemps.put(Blocks.ice, 0);
		blockTemps.put(Blocks.water, 15);
		blockTemps.put(Blocks.fire, 90);

		if (ModList.ROTARYCRAFT.isLoaded()) {
			addRC();
			if (ModList.REACTORCRAFT.isLoaded()) {
				addReC();
			}
		}
	}

	@ModDependent(ModList.ROTARYCRAFT)
	private static void addRC() {
		tileList.add(TileEntityBlastFurnace.class.getName());
		tileList.add(TileEntityCompactor.class.getName());
		tileList.add(TileEntityComposter.class.getName());
		tileList.add(TileEntityCrystallizer.class.getName());
		tileList.add(TileEntityFermenter.class.getName());
	}

	@ModDependent(ModList.REACTORCRAFT)
	private static void addReC() {
		for (int i = 0; i < ReactorTiles.TEList.length; i++) {
			ReactorTiles r = ReactorTiles.TEList[i];
			if (r.isReactorCore()) {
				tileList.add(r.getTEClass().getName());
			}
		}

		tileList.add(TileEntitySynthesizer.class.getName());
		tileList.add(TileEntityElectrolyzer.class.getName());
	}

	@Override
	protected boolean ticksIndividually() {
		return false;
	}

	@Override
	protected void doCollectiveTick(World world, int x, int y, int z) {
		int Tavg = 0;
		int n = 0;
		HashSet<ThermalTile> set = new HashSet();
		double f = this.getEqualizationFactor();
		for (int i = 0; i < 6; i++) {
			BlockKey bk = BlockKey.getAt(world, x+dirs[i].offsetX, y+dirs[i].offsetY, z+dirs[i].offsetZ);
			TileEntity te = this.getAdjacentTileEntity(dirs[i]);
			if (te instanceof ThermalTile && tileList.contains(te.getClass().getName())) {
				n++;
				set.add((ThermalTile)te);
				Tavg += ((ThermalTile)te).getTemperature();
			}
			else if (blockTemps.get(bk) != null) {
				n++;
				Tavg += blockTemps.get(bk);
			}
		}
		if (n <= 1)
			return;
		Tavg = Tavg/n;
		for (ThermalTile te : set) {
			int t1 = te.getTemperature();
			int t2 = (int)(this.getEqualizationFactor()*Tavg+(1-this.getEqualizationFactor())*t1);
			te.setTemperature(t2);
		}
	}

	private double getEqualizationFactor() {
		return factors[this.getTier()];
	}

	@Override
	protected boolean tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		return false;
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.ORANGE;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public static double getFactor(int tier) {
		return factors[tier];
	}

}

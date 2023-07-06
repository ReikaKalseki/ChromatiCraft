/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Effect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Maps.BlockMap;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay.GuiStackDisplay;
import Reika.DragonAPI.Interfaces.Registry.TileEnum;
import Reika.DragonAPI.Interfaces.TileEntity.ThermalTile;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.Registry.MachineRegistry;

import ic2.api.energy.tile.IHeatSource;

@Strippable(value = "ic2.api.energy.tile.IHeatSource")
public class TileEntityHeatRelay extends TileEntityAdjacencyUpgrade implements IHeatSource {

	private static final BlockMap<Integer> blockTemps = new BlockMap();
	private static final HashMap<Class, HeatTier> tileList = new HashMap();

	private static enum HeatTier {
		MACHINE,
		TIERED,
		REACTOR,
		;

		public String getDescription() {
			switch(this) {
				case MACHINE:
					return "Basic";
				default:
					return ReikaStringParser.capFirstChar(this.name());
			}
		}

		public int getTierValue() {
			switch(this) {
				case REACTOR:
					return 10;
				default:
					return this.ordinal();
			}
		}
	}

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

	private static final int[] ic2HeatMax = {
			2,
			5,
			10,
			20,
			50,
			100,
			250,
			1000,
	};

	private static final double[] ic2HeatEff = {
			0.1,
			0.25,
			0.5,
			1,
			1.5,
			2.5,
			5,
			10,
	};

	private static final HeatBalancingEffect[] heatBalance = new HeatBalancingEffect[3];

	static {
		for (int i = 0; i < heatBalance.length; i++)
			heatBalance[i] = new HeatBalancingEffect(HeatTier.values()[i]);

		setBlockTemp(Blocks.ice, 0);
		setBlockTemp(Blocks.water, 15);
		setBlockTemp(Blocks.fire, 90);

		if (ModList.ROTARYCRAFT.isLoaded()) {
			addRC();
			if (ModList.REACTORCRAFT.isLoaded()) {
				addReC();
			}
		}

		if (ModList.IC2.isLoaded()) {
			new SpecificAdjacencyEffect(CrystalElement.ORANGE) {

				@Override
				public String getDescription() {
					return "Transfers HU";
				}

				@Override
				public void getRelevantItems(ArrayList<GuiItemDisplay> li) {
					li.add(new GuiStackDisplay("IC2:blockKineticGenerator:5"));
					for (int i = 0; i <= 3; i++)
						li.add(new GuiStackDisplay("IC2:blockHeatGenerator:"+i));
					li.add(new GuiStackDisplay("IC2:blockGenerator:8"));
					li.add(new GuiStackDisplay("IC2:blockMachine3"));
					li.add(new GuiStackDisplay("IC2:blockMachine3:1"));
					li.add(new GuiStackDisplay("IC2:blockMachine2:12"));
					li.add(new GuiStackDisplay("IC2:blockMachine2:13"));
				}

				@Override
				protected boolean isActive() {
					return true;
				}

			};
		}

		for (int i = 0; i < heatBalance.length; i++)
			Collections.sort(heatBalance[i].items, ReikaItemHelper.comparator);
	}

	private static class HeatBalancingEffect extends SpecificAdjacencyEffect {

		private final ArrayList<ItemStack> items = new ArrayList();
		private final HeatTier tier;

		private HeatBalancingEffect(HeatTier tier) {
			super(CrystalElement.ORANGE);
			this.tier = tier;
		}

		@Override
		public String getDescription() {
			return "Balances heat between "+tier.getDescription()+" machines";
		}

		@Override
		public void getRelevantItems(ArrayList<GuiItemDisplay> li) {
			for (ItemStack is : items)
				li.add(new GuiStackDisplay(is));
		}

		@Override
		protected boolean isActive() {
			return !items.isEmpty();
		}

	}

	@ModDependent(ModList.ROTARYCRAFT)
	private static void addRC() {
		addHeatEffect(MachineRegistry.BLASTFURNACE, HeatTier.TIERED);
		addHeatEffect(MachineRegistry.COMPACTOR, HeatTier.MACHINE);
		addHeatEffect(MachineRegistry.COMPOSTER, HeatTier.MACHINE);
		addHeatEffect(MachineRegistry.CRYSTALLIZER, HeatTier.MACHINE);
		addHeatEffect(MachineRegistry.FERMENTER, HeatTier.MACHINE);
	}

	private static void setBlockTemp(Block b, int temp) {
		blockTemps.put(b, temp);

		//for (int i = 0; i < heatBalance.length; i++)
		//	heatBalance[i].items.add(new ItemStack(b));
	}

	@ModDependent(ModList.REACTORCRAFT)
	private static void addReC() {
		for (int i = 0; i < ReactorTiles.TEList.length; i++) {
			ReactorTiles r = ReactorTiles.TEList[i];
			if (r.isReactorCore()) {
				addHeatEffect(r, HeatTier.REACTOR);
			}
		}

		addHeatEffect(ReactorTiles.SYNTHESIZER, HeatTier.MACHINE);
		addHeatEffect(ReactorTiles.ELECTROLYZER, HeatTier.MACHINE);
	}

	private static void addHeatEffect(TileEnum c, HeatTier tier) {
		tileList.put(c.getTEClass(), tier);
		//for (int i = 0; i <= tier; i++)
		//	heatBalance[i].items.add(c.getCraftedProduct());
		heatBalance[tier.ordinal()].items.add(c.getCraftedProduct());
	}

	@Override
	protected boolean ticksIndividually() {
		return false;
	}

	@Override
	protected void doCollectiveTick(World world, int x, int y, int z) {
		int Tavg = 0;
		int n = 0;
		HashMap<ThermalTile, Integer> set = new HashMap();
		int tierThisCycle = Integer.MAX_VALUE;
		double f = this.getEqualizationFactor();
		for (int i = 0; i < 6; i++) {
			BlockKey bk = BlockKey.getAt(world, x+dirs[i].offsetX, y+dirs[i].offsetY, z+dirs[i].offsetZ);
			TileEntity te = this.getEffectiveTileOnSide(dirs[i]);
			if (te instanceof ThermalTile && tileList.containsKey(te.getClass())) {
				n++;
				HeatTier ht = tileList.get(te.getClass());
				int tier = ht.getTierValue();
				set.put((ThermalTile)te, tier);
				tierThisCycle = Math.min(tierThisCycle, tier);
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
		for (ThermalTile te : set.keySet()) {
			int hr = set.get(te);
			if (hr <= tierThisCycle) {
				int t1 = te.getTemperature();
				int t2 = (int)(this.getEqualizationFactor()*Tavg+(1-this.getEqualizationFactor())*t1);
				te.setTemperature(t2);
			}
		}
	}

	private double getEqualizationFactor() {
		return factors[this.getTier()];
	}

	@Override
	protected EffectResult tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		return EffectResult.STOP;
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

	@Override
	@ModDependent(ModList.IC2)
	public int maxrequestHeatTick(ForgeDirection dir) {
		return !this.hasRedstoneSignal() && this.hasSufficientEnergy() ? ic2HeatMax[this.getTier()] : 0;
	}

	@Override
	@ModDependent(ModList.IC2)
	public int requestHeat(ForgeDirection dir, int amt) {
		return this.collectHeat(dir, Math.min(this.maxrequestHeatTick(dir), amt));
	}

	@ModDependent(ModList.IC2)
	private int collectHeat(ForgeDirection skip, int seek) {
		if (seek <= 0)
			return 0;
		//ReikaJavaLibrary.pConsole(this.getWorldObj().getTotalWorldTime()+": "+seek+" from "+skip+" = "+this.getAdjacentTileEntity(skip));
		int has = 0;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			if (dir == skip)
				continue;
			TileEntity te = this.getEffectiveTileOnSide(dir);
			if (te instanceof IHeatSource) {
				ForgeDirection opp = dir.getOpposite();
				IHeatSource hs = (IHeatSource)te;
				int max = hs.maxrequestHeatTick(opp);
				int take = hs.requestHeat(opp, Math.min(max, seek));
				if (take > 0) {
					//ReikaJavaLibrary.pConsole("Took "+take+" from "+dir+" = "+te);
					seek -= take;
					has += take;
					if (seek == 0)
						break;
				}
			}
		}
		//ReikaJavaLibrary.pConsole(this.getWorldObj().getTotalWorldTime()+": got "+(int)(has*ic2HeatEff[this.getTier()]));
		return (int)(has*ic2HeatEff[this.getTier()]);
	}

	public static int getIc2Max(int tier) {
		return ic2HeatMax[tier];
	}

	public static double getIc2Eff(int tier) {
		return ic2HeatEff[tier];
	}

}

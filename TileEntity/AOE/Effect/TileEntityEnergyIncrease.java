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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;

import buildcraft.core.lib.engines.TileEngineBase;
import cofh.api.energy.EnergyStorage;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;


public class TileEntityEnergyIncrease extends TileEntityAdjacencyUpgrade implements BreakAction {

	private static final HashMap<Class, EnergyInterface> interactions = new HashMap();

	private static double[] factors = {
			0.125,
			0.25,
			0.5,
			1,
			3,
			7,
			15,
			31,
	};

	static {
		new TEDynamoInterface();
		new RailCraftTurbineInterface();
		new RailCraftEngineInterface();
		//new ExUGeneratorInterface(); Cannot check if on
		new BCEngineInterface();
		new ForestryEngineInterface();
		new IC2GeneratorInterface();
		new IC2ReactorInterface();
		//new HexGeneratorInterface();
	}

	protected Object cachedValue = null;

	@Override
	protected EffectResult tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		TileEntity te = this.getEffectiveTileOnSide(dir);
		if (te != null) {
			EnergyInterface e = this.getInterface(te);
			if (e != NoInterface.instance) {
				double boost = this.getBoostFactor();
				double f = e.getMultiplier();
				if (f != 1) {
					boost = Math.pow(1+boost, f)-1;
				}
				try {
					e.tick(this, te, boost);
				}
				catch (Exception ex) {
					ChromatiCraft.logger.logError("Could not tick energy interface "+e+" for "+te+" @ "+this);
					this.writeError(ex);
				}
				return EffectResult.ACTION;
			}
		}
		return EffectResult.CONTINUE;
	}

	public double getBoostFactor() {
		return factors[this.getTier()];
	}

	private EnergyInterface getInterface(TileEntity te) {
		Class c = te.getClass();
		Class c2 = c;
		EnergyInterface e = interactions.get(c2);
		while (e == null && c2 != TileEntity.class) {
			c2 = c2.getSuperclass();
			e = interactions.get(c2);
		}
		if (e == null)
			e = NoInterface.instance;
		interactions.put(c, e);
		return e;
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.YELLOW;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void breakBlock() {
		for (int i = 0; i < 6; i++) {
			TileEntity te = this.getAdjacentTileEntity(dirs[i]);
			if (te != null) {
				EnergyInterface e = this.getInterface(te);
				if (e != NoInterface.instance) {
					e.onBoosterBroken(te);
				}
			}
		}
	}

	private static abstract class EnergyInterface {

		protected EnergyInterface() {
			if (this.getMod() == null || this.getMod().isLoaded()) {
				try {
					String[] cs = this.getClasses();
					for (int i = 0; i < cs.length; i++) {
						try {
							Class c = Class.forName(cs[i]);
							interactions.put(c, this);
						}
						catch (Exception e) {
							ChromatiCraft.logger.logError("Could not find class "+cs[i]+" for "+this.getMod());
						}
					}
					this.init();
					ChromatiCraft.logger.log("Loaded "+this+" for "+this.getMod());
				}
				catch (Exception e) {
					ChromatiCraft.logger.logError("Could not load "+this+" for "+this.getMod()+":");
					e.printStackTrace();
					ReflectiveFailureTracker.instance.logModReflectiveFailure(this.getMod(), e);
				}
			}
			else {
				ChromatiCraft.logger.log("Not loading "+this+" for "+this.getMod()+"; Mod not present.");
			}
		}

		protected abstract void tick(TileEntityEnergyIncrease booster, TileEntity te, double boost) throws Exception;

		protected abstract void init() throws Exception;

		protected abstract ModList getMod();

		protected abstract String[] getClasses();

		protected TileEntity getActingTileEntity(TileEntity te) throws Exception {
			return te;
		}

		public double getMultiplier() {
			return 1;
		}

		protected void onBoosterBroken(TileEntity te) {

		}

		protected final void scaleNumberInField(TileEntity te, Field f, double boost) throws Exception {
			Number get = (Number)f.get(te);
			double val = (1+boost)*get.doubleValue();
			this.setNumberToField(te, f, get, val);
		}

		protected final void setNumberToField(TileEntity te, Field f, Number prev, double val) throws Exception {
			Object set = null;
			if (prev instanceof Integer || prev.getClass() == int.class) {
				set = (int)val;
			}
			else if (prev instanceof Double || prev.getClass() == double.class) {
				set = (double)val;
			}
			else if (prev instanceof Float || prev.getClass() == float.class) {
				set = (float)val;
			}
			else if (prev instanceof Short || prev.getClass() == short.class) {
				set = (short)val;
			}
			else if (prev instanceof Byte || prev.getClass() == byte.class) {
				set = (byte)val;
			}
			f.set(te, set);
		}
	}

	private static abstract class BasicEnergyInterface extends EnergyInterface {

		protected abstract int getBaseGeneration(TileEntity te) throws Exception;

	}

	private static abstract class EnergyInjectionInterface extends BasicEnergyInterface {

		@Override
		protected final void tick(TileEntityEnergyIncrease booster, TileEntity te, double boost) throws Exception {
			double gen = this.getBaseGeneration(te)*boost; //not 1+ since tile is still doing its own 1x
			this.inject(te, gen);
		}

		protected abstract void inject(TileEntity te, double gen) throws Exception;

	}

	private static abstract class EnergyFieldSetInterface extends EnergyInterface {

		@Override
		protected final void tick(TileEntityEnergyIncrease booster, TileEntity te, double boost) throws Exception {
			te = this.getActingTileEntity(te);
			Field f = this.getOutputField(te);
			this.scaleNumberInField(te, f, boost);
		}

		protected abstract Field getOutputField(TileEntity te);

	}

	private static abstract class EnergyDumpInterface extends BasicEnergyInterface {

		@Override
		protected final void tick(TileEntityEnergyIncrease booster, TileEntity te, double boost) throws Exception {
			double gen = this.getBaseGeneration(te)*boost; //not 1+ since tile is still doing its own 1x
			ForgeDirection dir = this.getFacing(te);
			TileEntity rec = te.worldObj.getTileEntity(te.xCoord+dir.offsetX, te.yCoord+dir.offsetY, te.zCoord+dir.offsetZ);
			if (rec != null)
				this.dumpEnergy(rec, gen);
		}

		protected abstract ForgeDirection getFacing(TileEntity te) throws Exception;

		protected abstract void dumpEnergy(TileEntity receiver, double gen) throws Exception;

	}

	private static class NoInterface extends EnergyInterface { //Used for null

		private static final NoInterface instance = new NoInterface();

		@Override
		protected void tick(TileEntityEnergyIncrease booster, TileEntity te, double boost) throws Exception {

		}

		@Override
		protected void init() throws Exception {

		}

		@Override
		protected ModList getMod() {
			return null;
		}

		@Override
		protected String[] getClasses() {
			return new String[0];
		}

	}

	private static class IC2GeneratorInterface extends EnergyInjectionInterface {

		private static final IC2GeneratorInterface instance = new IC2GeneratorInterface();

		private Field storage;
		private Field production;
		private Field maxStorage;

		@Override
		protected void init() throws Exception {
			Class c = Class.forName("ic2.core.block.generator.tileentity.TileEntityBaseGenerator");
			storage = c.getDeclaredField("storage");
			storage.setAccessible(true);
			production = c.getDeclaredField("production");
			production.setAccessible(true);
			maxStorage = c.getDeclaredField("maxStorage");
			maxStorage.setAccessible(true);
		}

		@Override
		protected ModList getMod() {
			return ModList.IC2;
		}

		@Override
		protected String[] getClasses() {
			return new String[]{"ic2.core.block.generator.tileentity.TileEntityBaseGenerator"};
		}

		@Override
		protected void inject(TileEntity te, double gen) throws Exception {
			storage.set(te, Math.min(storage.getDouble(te)+gen, maxStorage.getShort(te)));
		}

		@Override
		protected int getBaseGeneration(TileEntity te) throws Exception {
			return production.getInt(te);
		}

	}

	private static class IC2ReactorInterface extends EnergyInterface {

		private static final IC2ReactorInterface instance = new IC2ReactorInterface();

		//private Field output;
		//private Method getReactor;
		//private Method processChambers;
		private Field updateTicker;

		@Override
		protected void init() throws Exception {

			Class c = Class.forName("ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric");
			/*output = c.getDeclaredField("output");
			output.setAccessible(true);
			Class c2 = Class.forName("ic2.core.block.reactor.tileentity.TileEntityReactorChamberElectric");
			getReactor = c2.getDeclaredMethod("getReactor");
			getReactor.setAccessible(true);
			 */
			//processChambers = c.getDeclaredMethod("processChambers");
			//processChambers.setAccessible(true);
			updateTicker = c.getDeclaredField("updateTicker");
			updateTicker.setAccessible(true);
		}

		@Override
		protected ModList getMod() {
			return ModList.IC2;
		}

		@Override
		protected String[] getClasses() {
			return new String[]{"ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric", "ic2.core.block.reactor.tileentity.TileEntityReactorChamberElectric"};
		}
		/*
		@Override
		protected Field getOutputField(TileEntity te) {
			return output;
		}
		 */
		@Override
		@ModDependent(ModList.IC2)
		protected TileEntity getActingTileEntity(TileEntity te) throws Exception {
			if (te instanceof IReactorChamber) {
				return (TileEntity)((IReactorChamber)te).getReactor();
			}
			return te;
		}

		@Override
		@ModDependent(ModList.IC2)
		protected void tick(TileEntityEnergyIncrease booster, TileEntity te, double boost) throws Exception {
			te = this.getActingTileEntity(te);
			if (te == null && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				return;
			IReactor ir = (IReactor)te;
			//processChambers.invoke(te);
			if (updateTicker.getInt(te)%20 == 1) { //one tick after reactor recalculates
				float defaultval = ir.getReactorEnergyOutput();
				/*
				if (booster.cachedValue != Double.NaN) {
					defaultval = (float)booster.cachedValue;
				}
				 */
				ir.addOutput((float)(boost*defaultval));
			}
		}

		@Override
		public double getMultiplier() {
			return 0.75;
		}

	}

	private static class TEDynamoInterface extends EnergyInjectionInterface {

		private static final TEDynamoInterface instance = new TEDynamoInterface();

		private Field energyStorage;
		private Field config;
		private Field maxPower;
		private Field isActive;

		@Override
		protected void init() throws Exception {
			Class c = Class.forName("cofh.thermalexpansion.block.dynamo.TileDynamoBase");
			energyStorage = c.getDeclaredField("energyStorage");
			energyStorage.setAccessible(true);
			config = c.getDeclaredField("config");
			config.setAccessible(true);
			Class c2 = Class.forName("cofh.thermalexpansion.block.TileTEBase$EnergyConfig");
			maxPower = c2.getDeclaredField("maxPower");
			maxPower.setAccessible(true);
			Class c3 = Class.forName("cofh.thermalexpansion.block.TileRSControl");
			isActive = c3.getDeclaredField("isActive");
			isActive.setAccessible(true);
		}

		@Override
		protected ModList getMod() {
			return ModList.THERMALEXPANSION;
		}

		@Override
		protected String[] getClasses() {
			return new String[]{"cofh.thermalexpansion.block.dynamo.TileDynamoBase"};
		}

		@Override
		protected void inject(TileEntity te, double gen) throws Exception {
			EnergyStorage es = (EnergyStorage)energyStorage.get(te);
			es.setMaxTransfer((int)((this.getBaseGeneration(te) + gen)*2));
			es.modifyEnergyStored((int)gen);
		}

		@Override
		protected int getBaseGeneration(TileEntity te) throws Exception {
			return isActive.getBoolean(te) ? maxPower.getInt(config.get(te)) : 0;
		}

		@Override
		protected void onBoosterBroken(TileEntity te) {
			try {
				EnergyStorage es = (EnergyStorage)energyStorage.get(te);
				es.setMaxTransfer(this.getBaseGeneration(te)*2);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	/* Implementation differs by machine type
	private static class EiOGeneratorInterface extends EnergyInterface {

		private static final EiOGeneratorInterface instance = new EiOGeneratorInterface();

		@Override
		protected void tick(TileEntity te, double boost) {

		}

		@Override
		protected void init() throws Exception {

		}

		@Override
		protected ModList getMod() {
			return ModList.ENDERIO;
		}

		@Override
		protected String[] getClasses() {
			return new String[]{"crazypants.enderio.machine.generator.AbstractGeneratorEntity"};
		}

	}
	 */
	private static class BCEngineInterface extends EnergyInjectionInterface {

		private static final BCEngineInterface instance = new BCEngineInterface();

		@Override
		@ModDependent(ModList.BCENERGY)
		protected int getBaseGeneration(TileEntity te) throws Exception {
			return ((TileEngineBase)te).currentOutput;
		}

		@Override
		@ModDependent(ModList.BCENERGY)
		protected void inject(TileEntity te, double gen) throws Exception {
			((TileEngineBase)te).energy += (int)gen;
		}

		@Override
		protected void init() throws Exception {

		}

		@Override
		protected ModList getMod() {
			return ModList.BCENERGY;
		}

		@Override
		protected String[] getClasses() {
			return new String[]{"buildcraft.core.lib.engines.TileEngineBase"};
		}

	}

	private static class ForestryEngineInterface extends EnergyInjectionInterface {

		private Field currentOutput;
		private Field energyManager;
		private Field energyStorage;

		private static final ForestryEngineInterface instance = new ForestryEngineInterface();

		@Override
		protected void init() throws Exception {
			Class c = Class.forName("forestry.core.tiles.TileEngine");
			currentOutput = c.getDeclaredField("currentOutput");
			currentOutput.setAccessible(true);
			energyManager = c.getDeclaredField("energyManager");
			energyManager.setAccessible(true);
			Class c2 = Class.forName("forestry.energy.EnergyManager");
			energyStorage = c2.getDeclaredField("energyStorage");
			energyStorage.setAccessible(true);
		}

		@Override
		protected ModList getMod() {
			return ModList.FORESTRY;
		}

		@Override
		protected int getBaseGeneration(TileEntity te) throws Exception {
			return currentOutput.getInt(te);
		}

		@Override
		protected void inject(TileEntity te, double gen) throws Exception {
			EnergyStorage es = (EnergyStorage)energyStorage.get(energyManager.get(te));
			es.modifyEnergyStored((int)gen);
		}

		@Override
		protected String[] getClasses() {
			return new String[]{"forestry.core.tiles.TileEngine"};
		}

	}

	private static class RailCraftEngineInterface extends EnergyInjectionInterface {

		private Method getMaxOutputRF;
		private Method maxEnergy;
		private Field energy;

		private static final RailCraftEngineInterface instance = new RailCraftEngineInterface();

		@Override
		protected void init() throws Exception {
			Class c = Class.forName("mods.railcraft.common.blocks.machine.beta.TileEngineSteam");
			getMaxOutputRF = c.getDeclaredMethod("getMaxOutputRF");
			getMaxOutputRF.setAccessible(true);
			Class c2 = Class.forName("mods.railcraft.common.blocks.machine.beta.TileEngine");
			energy = c2.getDeclaredField("energy");
			energy.setAccessible(true);
			maxEnergy = c2.getDeclaredMethod("maxEnergy");
			maxEnergy.setAccessible(true);
		}

		@Override
		protected ModList getMod() {
			return ModList.RAILCRAFT;
		}

		@Override
		protected int getBaseGeneration(TileEntity te) throws Exception {
			return (int)getMaxOutputRF.invoke(te);
		}

		@Override
		protected void inject(TileEntity te, double gen) throws Exception {
			energy.setInt(te, Math.min(energy.getInt(te)+(int)gen, (int)maxEnergy.invoke(te)));
		}

		@Override
		protected String[] getClasses() {
			return new String[]{"mods.railcraft.common.blocks.machine.beta.TileEngineSteam"};
		}

	}

	private static class RailCraftTurbineInterface extends EnergyInjectionInterface {

		private Field output;
		private Field energy;
		private Method getMasterBlock;

		private static final RailCraftTurbineInterface instance = new RailCraftTurbineInterface();

		@Override
		protected void init() throws Exception {
			Class c = Class.forName("mods.railcraft.common.blocks.machine.alpha.TileSteamTurbine");
			output = c.getDeclaredField("output");
			output.setAccessible(true);
			energy = c.getDeclaredField("energy");
			energy.setAccessible(true);
			Class c2 = Class.forName("mods.railcraft.common.blocks.machine.TileMultiBlock");
			getMasterBlock = c2.getDeclaredMethod("getMasterBlock");
			getMasterBlock.setAccessible(true);
		}

		@Override
		protected ModList getMod() {
			return ModList.RAILCRAFT;
		}

		@Override
		protected int getBaseGeneration(TileEntity te) throws Exception {
			return (int)output.getFloat(te);
		}

		@Override
		protected void inject(TileEntity te, double gen) throws Exception {
			energy.setDouble(te, energy.getDouble(te)+(int)gen);
		}

		@Override
		protected String[] getClasses() {
			return new String[]{"mods.railcraft.common.blocks.machine.alpha.TileSteamTurbine"};
		}

		@Override
		protected TileEntity getActingTileEntity(TileEntity te) throws Exception {
			return (TileEntity)getMasterBlock.invoke(te);
		}

	}

	private static class ExUGeneratorInterface extends EnergyInjectionInterface {

		private Method genLevel;
		private Field storage;

		private static final ExUGeneratorInterface instance = new ExUGeneratorInterface();

		@Override
		protected void init() throws Exception {
			Class c = Class.forName("com.rwtema.extrautils.tileentity.generators.TileEntityGenerator");
			genLevel = c.getDeclaredMethod("genLevel");
			genLevel.setAccessible(true);
			storage = c.getDeclaredField("storage");
			storage.setAccessible(true);
		}

		@Override
		protected ModList getMod() {
			return ModList.EXTRAUTILS;
		}

		@Override
		protected int getBaseGeneration(TileEntity te) throws Exception {
			return ((Double)genLevel.invoke(te)).intValue();
		}

		@Override
		protected void inject(TileEntity te, double gen) throws Exception {
			EnergyStorage es = (EnergyStorage)storage.get(te);
			es.modifyEnergyStored((int)gen);
		}

		@Override
		protected String[] getClasses() {
			return new String[]{"com.rwtema.extrautils.tileentity.generators.TileEntityGenerator"};
		}

	}
	/*
	private static class HexGeneratorInterface extends EnergyInterface {

		private Field perTick;
		private Field producibleAmount;
		private Field producibleAmount2;

		private static final HexGeneratorInterface instance = new HexGeneratorInterface();

		@Override
		protected void init() throws Exception {
			for (String n : this.getClasses()) {
				try {
					Class c = Class.forName(n);
					perTick = c.getDeclaredField("energyPerTick");
					perTick.setAccessible(true);
					producibleAmount = c.getDeclaredField("energyTotal");
					producibleAmount.setAccessible(true);
					producibleAmount2 = c.getDeclaredField("energyTotalLeft");
					producibleAmount2.setAccessible(true);
					break;
				}
				catch (Exception e) {
					if (n == this.getClasses()[this.getClasses().length-1])
						Throwables.propagate(e);
				}
			}
		}

		@Override
		protected ModList getMod() {
			return ModList.HEXCRAFT;
		}

		@Override
		protected String[] getClasses() {
			return new String[]{"com.celestek.hexcraft.tileentity.TileHexoriumGenerator", "com.celestek.hexcraft.tileentity.HexEnergySource"};
		}

		@Override
		protected final void tick(TileEntityEnergyIncrease booster, TileEntity te, double boost) throws Exception {
			te = this.getActingTileEntity(te);
			if (booster.cachedValue != null)
			booster.cachedValue = new ImmutableTriple();
			this.scaleNumberInField(te, perTick, boost);
			this.scaleNumberInField(te, producibleAmount, boost);
			this.scaleNumberInField(te, producibleAmount2, boost);
		}

	}
	 */
	public static double getFactor(int tier) {
		return factors[tier];
	}

}

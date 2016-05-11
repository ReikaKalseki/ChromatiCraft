/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Effect;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;


public class TileEntityStabilityCore extends TileEntityAdjacencyUpgrade {

	private static final HashMap<Class, StabilityInterface> interactions = new HashMap();

	static {
		new InfusionStabilityInterface();
	}

	@Override
	protected boolean tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		TileEntity te = this.getAdjacentTileEntity(dir);
		if (te != null) {
			StabilityInterface s = this.getInterface(te);
			if (s != NoInterface.instance) {
				try {
					s.tick(te, this.getTier());
				}
				catch (Exception ex) {
					ChromatiCraft.logger.logError("Could not tick stability interface "+s+" for "+te+" @ "+this);
					this.writeError(ex);
				}
			}
		}
		return true;
	}

	private StabilityInterface getInterface(TileEntity te) {
		Class c = te.getClass();
		Class c2 = c;
		StabilityInterface e = interactions.get(c2);
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
		return CrystalElement.WHITE;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	private static abstract class StabilityInterface {

		protected StabilityInterface() {
			if (this.getMod() == null || this.getMod().isLoaded()) {
				try {
					String[] cs = this.getClasses();
					for (int i = 0; i < cs.length; i++) {
						Class c = Class.forName(cs[i]);
						interactions.put(c, this);
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

		protected abstract void tick(TileEntity te, int tier) throws Exception;

		protected abstract void init() throws Exception;

		protected abstract ModList getMod();

		protected abstract String[] getClasses();

		protected TileEntity getActingTileEntity(TileEntity te) throws Exception {
			return te;
		}
	}

	private static class NoInterface extends StabilityInterface { //Used for null

		private static final NoInterface instance = new NoInterface();

		@Override
		protected void tick(TileEntity te, int tier) throws Exception {

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

	private static abstract class FieldSetStabilityInterface extends StabilityInterface {

		@Override
		protected final void tick(TileEntity te, int tier) throws Exception {
			te = this.getActingTileEntity(te);
			Field f = this.getSetField(te);
			Number get = (Number)f.get(te);
			double val = this.getReplacedValue(te, tier, get);
			Object set = null;
			if (get instanceof Integer || get.getClass() == int.class) {
				set = (int)val;
			}
			else if (get instanceof Double || get.getClass() == double.class) {
				set = (double)val;
			}
			else if (get instanceof Float || get.getClass() == float.class) {
				set = (float)val;
			}
			else if (get instanceof Short || get.getClass() == short.class) {
				set = (short)val;
			}
			else if (get instanceof Byte || get.getClass() == byte.class) {
				set = (byte)val;
			}
			f.set(te, set);
		}

		protected abstract double getReplacedValue(TileEntity te, int tier, Number original) throws Exception;

		protected abstract Field getSetField(TileEntity te) throws Exception;

	}

	private static class InfusionStabilityInterface extends FieldSetStabilityInterface {

		private Field instability;
		private Field recipeInstability;

		@Override
		protected void init() throws Exception {
			Class c = Class.forName("thaumcraft.common.tiles.TileInfusionMatrix");
			instability = c.getDeclaredField("instability");
			instability.setAccessible(true);
			recipeInstability = c.getDeclaredField("recipeInstability");
			recipeInstability.setAccessible(true);
		}

		@Override
		protected ModList getMod() {
			return ModList.THAUMCRAFT;
		}

		@Override
		protected String[] getClasses() {
			return new String[]{"thaumcraft.common.tiles.TileInfusionMatrix"};
		}

		@Override
		protected Field getSetField(TileEntity te) throws Exception {
			return instability;
		}

		@Override
		protected double getReplacedValue(TileEntity te, int tier, Number original) throws Exception {
			return recipeInstability.getInt(te)*this.getReductionFactor(tier);
		}

		private double getReductionFactor(int tier) {
			return Math.pow(0.67, 1+tier);
		}

	}

}

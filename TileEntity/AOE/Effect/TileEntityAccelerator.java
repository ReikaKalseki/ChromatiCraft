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

import java.util.HashMap;
import java.util.Locale;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AcceleratorBlacklist.BlacklistReason;
import Reika.ChromatiCraft.API.Interfaces.Accelerator;
import Reika.ChromatiCraft.API.Interfaces.CustomAcceleration;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class TileEntityAccelerator extends TileEntityAdjacencyUpgrade implements Accelerator {

	public static final long MAX_LAG = calculateLagLimit();

	private static HashMap<Class<? extends TileEntity>, Acceleration> actions = new HashMap();

	public static int debugLevel = 0;

	private int ticksSinceLagPausedWarning = 100000;

	private static final Acceleration blacklistKey = new Acceleration() {
		@Override
		public void tick(TileEntity te, int factor, TileEntity accelerator) {}

		@Override
		public boolean usesParentClasses() {
			return false;
		}
	};

	private static boolean doRecursiveChecks = false;

	static {
		blacklistTile("icbm.sentry.turret.Blocks.TileTurret", ModList.ICBM, BlacklistReason.BUGS); //by request
		blacklistTile("bluedart.tile.decor.TileForceTorch", ModList.DARTCRAFT, BlacklistReason.CRASH); //StackOverflow
		blacklistTile("com.sci.torcherino.tile.TileTorcherino", null, BlacklistReason.CRASH); //StackOverflow

		blacklistTile("mrtjp.projectred.integration.Timer", ModList.PROJRED, BlacklistReason.BUGS);
		blacklistTile("mrtjp.projectred.integration.Sequencer", ModList.PROJRED, BlacklistReason.BUGS);
		blacklistTile("mrtjp.projectred.integration.Repeater", ModList.PROJRED, BlacklistReason.BUGS);
		blacklistTile("mrtjp.projectred.integration.StateCell", ModList.PROJRED, BlacklistReason.BUGS);

		for (int i = 0; i < ChromaTiles.TEList.length; i++) {
			ChromaTiles c = ChromaTiles.TEList[i];
			if (!c.allowsAcceleration())
				blacklistTile(c.getTEClass());
		}
	}

	public static void blacklistTile(Class<? extends TileEntity> cl) {
		if (cl == TileEntity.class)
			throw new IllegalArgumentException("You cannot blacklist the core TE class!");
		actions.put(cl, blacklistKey);
	}

	public static void customizeTile(Class c, Acceleration a) {
		actions.put(c, a);
		doRecursiveChecks = a.usesParentClasses();
	}

	private static void blacklistTile(String name, ModList mod, BlacklistReason r) {
		Class cl;
		if (mod != null && !mod.isLoaded())
			return;
		try {
			cl = Class.forName(name);
			if (cl == TileEntity.class)
				throw new IllegalArgumentException("You cannot blacklist the core TE class!");
			ChromatiCraft.logger.log("TileEntity \""+name+"\" has been blacklisted from the TileEntity Accelerator, because "+r.message);
			actions.put(cl, blacklistKey);
		}
		catch (ClassNotFoundException e) {
			ChromatiCraft.logger.log("Could not add "+name+" to the Accelerator blacklist: Class not found!");
		}
	}

	private static long calculateLagLimit() {
		int base = ChromaOptions.TILELAG.getValue();
		return base >= 0 ? Math.max(base, 100000L) : -1;
	}

	public static int getAccelFromTier(int tier) {
		return ReikaMathLibrary.intpow2(2, tier+1)-1;
	}

	public int getAccel() {
		return getAccelFromTier(this.getTier());
	}

	@Override
	public boolean canRun(World world, int x, int y, int z) {
		return super.canRun(world, x, y, z);
	}

	@Override
	protected EffectResult tickDirection(World world, int x, int y, int z, ForgeDirection dir, long time) {
		TileEntity te = this.getAdjacentTileEntity(dir);
		Acceleration a = this.getAccelerate(te);
		if (debugLevel > 0) {
			ReikaJavaLibrary.pConsole(te+": "+(a != null ? a.getClass() : null));
		}
		ticksSinceLagPausedWarning++;
		if (a != blacklistKey) {
			int max = this.getAccel();
			if (debugLevel > 1) {
				ReikaJavaLibrary.pConsole(this+" > "+max);
			}
			if (a != null) {
				try {
					te = a.getActingTileEntity(te);
					a.tick(te, max, this);
				}
				catch (Exception e) {
					e.printStackTrace();
					this.writeError(e);
				}
				if (MAX_LAG > 0 && System.nanoTime()-time >= MAX_LAG) {
					this.logLagWarning(time, te);
					return EffectResult.FINAL_ACTION;
				}
			}
			else {
				for (int k = 0; k < max; k++) {
					if (debugLevel > 2) {
						ReikaJavaLibrary.pConsole("Ticked "+te+" "+k+"th time");
					}
					te.updateEntity();
					if (MAX_LAG > 0 && System.nanoTime()-time >= MAX_LAG) {
						this.logLagWarning(time, te);
						return EffectResult.FINAL_ACTION;
					}
				}
			}
			return EffectResult.ACTION;
		}
		return EffectResult.CONTINUE;
	}

	private void logLagWarning(long time, TileEntity te) {
		if (ticksSinceLagPausedWarning >= 20) {
			long dur = System.nanoTime()-time;
			String s = "Tile Accelerator "+this+" is self-throttling due to lag ("+dur+" ns), accelerating "+te;
			ChromatiCraft.logger.log(s);
			ticksSinceLagPausedWarning = 0;
		}
	}

	private Acceleration getAccelerate(TileEntity te) {
		if (te == null)
			return blacklistKey;
		if (te instanceof TileEntityAdjacencyUpgrade)
			return blacklistKey;
		if (te.isInvalid())
			return blacklistKey;
		Class c = te.getClass();
		while (c.isAnonymousClass())
			c = c.getSuperclass();
		Acceleration a = actions.get(c);
		while (a == null && c != TileEntity.class) {
			c = c.getSuperclass();
			a = actions.get(c);
		}
		if (a != null)
			return a;
		if (!te.canUpdate())
			return blacklistKey;
		String s = c.getSimpleName().toLowerCase(Locale.ENGLISH);
		if (s.contains("conduit") || s.contains("wire") || s.contains("cable")) { //almost always part of a network object
			actions.put(c, blacklistKey);
			return blacklistKey;
		}
		if (s.contains("solar")) { //power exploit
			actions.put(c, blacklistKey);
			return blacklistKey;
		}
		if (s.contains("windturbine") || s.contains("wind turbine") || s.contains("windmill") || s.contains("wind mill")) { //power exploit
			actions.put(c, blacklistKey);
			return blacklistKey;
		}
		if (s.contains("watermill") || s.contains("water mill")) { //power exploit
			actions.put(c, blacklistKey);
			return blacklistKey;
		}
		if (te.getClass().getName().contains("appeng")) { //AE will crash
			actions.put(c, blacklistKey);
			return blacklistKey;
		}
		return null;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getRedstoneOverride() {
		return 0;
	}

	@Override
	public int getAccelerationFactor() {
		return this.getAccel();
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.LIGHTBLUE;
	}

	public static abstract class Acceleration {

		protected abstract void tick(TileEntity te, int factor, TileEntity accelerator) throws Exception;

		public abstract boolean usesParentClasses();

		protected final void registerClass(String sg) {
			Class c = null;
			try {
				c = Class.forName(sg);
				this.registerClass(c);
			}
			catch (Exception e) {
				//e.printStackTrace();
			}
		}

		protected final void registerClass(Class c) {
			TileEntityAccelerator.customizeTile(c, this);
		}

		protected TileEntity getActingTileEntity(TileEntity root) throws Exception {
			return root;
		}

	}

	private static final class APIAcceleration extends Acceleration {

		private final CustomAcceleration accel;

		private APIAcceleration(CustomAcceleration acc) {
			accel = acc;
		}

		@Override
		protected void tick(TileEntity te, int factor, TileEntity accelerator) {
			accel.tick(factor);
		}

		@Override
		public boolean usesParentClasses() {
			return accel.usesParentClasses();
		}

	}

}

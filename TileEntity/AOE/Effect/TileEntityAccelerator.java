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
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class TileEntityAccelerator extends TileEntityAdjacencyUpgrade implements Accelerator {

	public static final long MAX_LAG = calculateLagLimit();

	private static HashMap<Class<? extends TileEntity>, Acceleration> actions = new HashMap();

	private static final Acceleration blacklistKey = new Acceleration() {
		@Override
		public void tick(TileEntity te, int factor) {}
	};

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
		actions.put(cl, blacklistKey);
	}

	public static void customizeTile(Class c, Acceleration a) {
		actions.put(c, a);
	}

	private static void blacklistTile(String name, ModList mod, BlacklistReason r) {
		Class cl;
		if (mod != null && !mod.isLoaded())
			return;
		try {
			cl = Class.forName(name);
			ChromatiCraft.logger.log("TileEntity \""+name+"\" has been blacklisted from the TileEntity Accelerator, because "+r.message);
			actions.put(cl, blacklistKey);
		}
		catch (ClassNotFoundException e) {
			ChromatiCraft.logger.logError("Could not add "+name+" to the Accelerator blacklist: Class not found!");
		}
	}

	private static long calculateLagLimit() {
		return Math.max(ChromaOptions.TILELAG.getValue(), 100000L);
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
	protected boolean tickDirection(World world, int x, int y, int z, ForgeDirection dir, long time) {
		TileEntity te = this.getAdjacentTileEntity(dir);
		Acceleration a = this.getAccelerate(te);
		if (a != blacklistKey) {
			int max = this.getAccel();
			if (a != null) {
				a.tick(te, max);
				if (System.nanoTime()-time >= MAX_LAG)
					return false;
			}
			else {
				for (int k = 0; k < max; k++) {
					te.updateEntity();
					if (System.nanoTime()-time >= MAX_LAG)
						return false;
				}
			}
		}
		return true;
	}

	private Acceleration getAccelerate(TileEntity te) {
		if (te == null)
			return blacklistKey;
		if (te instanceof TileEntityAccelerator)
			return blacklistKey;
		if (!te.canUpdate() || te.isInvalid())
			return blacklistKey;
		Class c = te.getClass();
		Acceleration a = actions.get(c);
		if (a != null)
			return a;
		String s = c.getSimpleName().toLowerCase(Locale.ENGLISH);
		if (s.contains("conduit") || s.contains("wire") || s.contains("cable")) { //almost always part of a network object
			actions.put(c, blacklistKey);
			return blacklistKey;
		}
		if (s.contains("solar") || s.contains("windmill") || s.contains("watermill")) { //power exploit
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

		protected abstract void tick(TileEntity te, int factor);

	}

	private static final class APIAcceleration extends Acceleration {

		private final CustomAcceleration accel;

		private APIAcceleration(CustomAcceleration acc) {
			accel = acc;
		}

		@Override
		protected void tick(TileEntity te, int factor) {
			accel.tick(factor);
		}

	}

}

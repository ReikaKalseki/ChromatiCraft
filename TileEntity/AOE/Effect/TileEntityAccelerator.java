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
import java.util.HashMap;
import java.util.Locale;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AcceleratorBlacklist.BlacklistReason;
import Reika.ChromatiCraft.API.Interfaces.CustomAcceleration;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay.GuiIconDisplay;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay.GuiStackDisplay;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityAccelerator extends TileEntityAdjacencyUpgrade {

	public static final long MAX_LAG = calculateLagLimit();

	private static HashMap<Class<? extends TileEntity>, Acceleration> actions = new HashMap();

	public static int debugLevel = 0;

	private int[] lagTimer = new int[6];

	private static final Acceleration blacklistKey = new Acceleration() {
		@Override
		public void tick(TileEntity te, int factor, TileEntity accelerator) {}

		@Override
		public boolean usesParentClasses() {
			return false;
		}

		@Override
		public String getDescription() {
			return "Does nothing";
		}

		@Override
		public void getRelevantItems(ArrayList<GuiItemDisplay> li) {

		}
	};

	private static final Acceleration defaultKey = new Acceleration() {
		@Override
		public void tick(TileEntity te, int factor, TileEntity accelerator) {}

		@Override
		public boolean usesParentClasses() {
			return false;
		}

		@Override
		public String getDescription() {
			return "Accelerates operations";
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void getRelevantItems(ArrayList<GuiItemDisplay> li) {
			li.add(new GuiIconDisplay(ChromaIcons.QUESTION.getIcon()));
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
		//ChromatiCraft.logger.log("Blacklisting "+cl+" from the accelerator");
		actions.put(cl, blacklistKey);
	}

	public static void customizeTile(Class c, Acceleration a) {
		actions.put(c, a);
		doRecursiveChecks |= a.usesParentClasses();
	}

	public static void customizeTile(Class c, CustomAcceleration a) {
		actions.put(c, new APIAcceleration(a));
		doRecursiveChecks |= a.usesParentClasses();
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
		if (lagTimer[dir.ordinal()] > 0) {
			ReikaParticleHelper.CLOUD.spawnAroundBlock(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, 4);
			lagTimer[dir.ordinal()]--;
		}
		TileEntity te = this.getEffectiveTileOnSide(dir);
		Acceleration a = this.getAccelerate(te);
		if (debugLevel > 0) {
			ReikaJavaLibrary.pConsole(te+": "+(a != null ? a.getClass() : null));
		}
		if (a != blacklistKey) {
			int max = this.getAccel();
			if (debugLevel > 1) {
				ReikaJavaLibrary.pConsole(this+" > "+max);
			}
			if (a != defaultKey) {
				try {
					te = a.getActingTileEntity(te);
					a.tick(te, max, this);
				}
				catch (Exception e) {
					e.printStackTrace();
					this.writeError(e);
				}
				if (MAX_LAG > 0 && System.nanoTime()-time >= MAX_LAG) {
					this.logLagWarning(time, dir, te);
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
						this.logLagWarning(time, dir, te);
						return EffectResult.FINAL_ACTION;
					}
				}
			}
			return EffectResult.ACTION;
		}
		return EffectResult.CONTINUE;
	}

	private void logLagWarning(long time, ForgeDirection dir, TileEntity te) {
		/*
		if (ticksSinceLagPausedWarning >= 20) {
			long dur = System.nanoTime()-time;
			String s = "Tile Accelerator "+this+" is self-throttling due to lag ("+dur+" ns), accelerating "+te;
			ChromatiCraft.logger.log(s);
			ticksSinceLagPausedWarning = 0;
		}*/
		lagTimer[dir.ordinal()] = 20;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		if (NBT.hasKey("lag"))
			lagTimer = NBT.getIntArray("lag");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setIntArray("lag", lagTimer);
	}

	private static Acceleration getAccelerate(TileEntity te) {
		if (te == null)
			return blacklistKey;
		if (te instanceof TileEntityAdjacencyUpgrade)
			return blacklistKey;
		if (te.isInvalid())
			return blacklistKey;
		Acceleration a = getAccelerate(te.getClass());
		if (a == null && !te.canUpdate())
			return blacklistKey;
		return a;
	}

	private static Acceleration getAccelerate(Class<? extends TileEntity> c) {
		Acceleration a = actions.get(c);
		if (a != null)
			return a;
		a = calculateAccelerate(c);
		if (a != null) {
			if (a == blacklistKey)
				ChromatiCraft.logger.log("Calculated acceleration blacklist "+a+" for tile class "+c);
			else
				ChromatiCraft.logger.log("Calculated acceleration mode "+a+" for tile class "+c);
		}
		actions.put(c, a);
		return a;
	}

	private static Acceleration calculateAccelerate(Class<? extends TileEntity> c) {
		if (TileEntityChromaticBase.class.isAssignableFrom(c))
			return defaultKey;
		Class parent = c.getSuperclass();
		if (parent == TileEntity.class)
			return defaultKey;
		Acceleration a = getAccelerate(parent);
		if (a != null)
			return a;
		String s = c.getSimpleName().toLowerCase(Locale.ENGLISH);
		if (s.contains("conduit") || (s.contains("wire") && !s.contains("wireless")) || s.contains("cable")) { //almost always part of a network object
			return blacklistKey;
		}
		if (s.contains("solar")) { //power exploit
			return blacklistKey;
		}
		if (s.contains("windturbine") || s.contains("wind turbine") || s.contains("windmill") || s.contains("wind mill")) { //power exploit
			return blacklistKey;
		}
		if (s.contains("watermill") || s.contains("water mill")) { //power exploit
			return blacklistKey;
		}
		if (c.getName().contains("appeng")) { //AE will crash
			return blacklistKey;
		}
		return defaultKey;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getRedstoneOverride() {
		return 0;
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.LIGHTBLUE;
	}

	public static abstract class Acceleration extends SpecificAdjacencyEffect {

		protected Acceleration() {
			super(CrystalElement.LIGHTBLUE);
		}

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

		@Override
		public boolean isActive() {
			return true;
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

		@Override
		public String getDescription() {
			return accel.getDescription();
		}

		@Override
		public void getRelevantItems(ArrayList<GuiItemDisplay> li) {
			for (ItemStack is : accel.getItems())
				li.add(new GuiStackDisplay(is));
		}

	}

}

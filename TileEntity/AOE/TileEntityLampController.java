/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE;

import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade.AdjacencyCheckHandlerImpl;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.Decoration.BlockRangedLamp.TileEntityRangedLamp;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityRangeBoost;
import Reika.DragonAPI.Instantiable.Data.Collections.ThreadSafeSet;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionType;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.ConcurrencyDeterminator;
import Reika.DragonAPI.Interfaces.TileEntity.GuiController;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.ModRegistry.PowerTypes;
import Reika.RotaryCraft.API.Power.PowerTransferHelper;
import Reika.RotaryCraft.API.Power.SimpleShaftPowerReceiver;

import cofh.api.energy.IEnergyHandler;

public class TileEntityLampController extends TileEntityChromaticBase implements LocationCached, GuiController, SimpleShaftPowerReceiver {

	private static final MultiMap<Integer, LightSource> map = new MultiMap(CollectionType.CONCURRENTSET, new ConcurrencyDeterminator());
	private static final MultiMap<Integer, WorldLocation> lights = new MultiMap(CollectionType.CONCURRENTSET, new ConcurrencyDeterminator());

	private static final AdjacencyCheckHandlerImpl adjacency = TileEntityAdjacencyUpgrade.getOrCreateAdjacencyCheckHandler(CrystalElement.LIME, TileEntityRangeBoost.basicRangeUpgradeable.getDescription(), ChromaTiles.LAMPCONTROL);

	public static final int MAXRANGE = 64;
	public static final int MAXCHANNEL = 999;

	public static enum Control {
		MANUAL(),
		REDSTONE(),
		RFSTORAGE(),
		SHAFTPOWER();

		private static final Control[] list = values();

		public Control next() {
			Control c = list[(this.ordinal()+1)%list.length];
			while (!c.isValid()) {
				c = list[(c.ordinal()+1)%list.length];
			}
			return c;
		}

		private boolean isValid() {
			switch(this) {
				case RFSTORAGE:
					return PowerTypes.RF.isLoaded();
				case SHAFTPOWER:
					return PowerTypes.ROTARYCRAFT.isLoaded();
				default:
					return true;
			}
		}
	}

	private static class LightSource {

		public final WorldLocation location;
		public final int range;
		private boolean isActive;

		public LightSource(TileEntityLampController te) {
			location = new WorldLocation(te);
			range = te.getRange();
			isActive = te.isActive();
		}

		@Override
		public int hashCode() {
			return location.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof LightSource && ((LightSource)o).location.equals(location);
		}
	}

	private int channel = 0;
	private Control control = Control.MANUAL;
	private boolean active = false;
	private boolean shaftpower;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.LAMPCONTROL;
	}

	public boolean isActive() {
		return active;
	}

	public int getChannel() {
		return channel;
	}

	public Control getControlType() {
		return control;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		control = Control.list[NBT.getInteger("control")];
		channel = NBT.getInteger("channel");
		active = NBT.getBoolean("active");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("control", control.ordinal());
		NBT.setInteger("channel", channel);
		NBT.setBoolean("active", active);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			boolean changed = false;
			boolean on = active;
			switch(control) {
				case REDSTONE:
					on = this.hasRedstoneSignal();
					break;
				case MANUAL:
					break;
				case RFSTORAGE:
					on = this.hasAdjacentRF();
					break;
				case SHAFTPOWER:
					shaftpower &= PowerTransferHelper.checkPowerFromAllSides(this, true);
					on = shaftpower;
					break;
			}
			if (on != active) {
				changed = true;
			}
			active = on;
			if (changed)
				this.updateLightAt(this);
		}
	}

	private boolean hasAdjacentRF() {
		if (!PowerTypes.RF.isLoaded()) {
			return false;
		}
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			TileEntity te = this.getAdjacentTileEntity(dir);
			if (te instanceof IEnergyHandler && ((IEnergyHandler)te).getEnergyStored(dir.getOpposite()) > 0)
				return true;
		}
		return false;
	}

	@Override
	public void onFirstTick(World world, int x, int y, int z) {
		for (int i = -this.getRange(); i <= this.getRange(); i++) {
			for (int j = -this.getRange(); j <= this.getRange(); j++) {
				for (int k = -this.getRange(); k <= this.getRange(); k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					if (world.getBlock(dx, dy, dz) == ChromaBlocks.LAMPBLOCK.getBlockInstance()) {
						this.addLight((TileEntityRangedLamp)world.getTileEntity(dx, dy, dz));
					}
				}
			}
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public void setChannel(int ch) {
		if (ch < 0) {
			return;
		}
		this.removeSource(this);
		channel = ch;
		this.addSource(this);
	}

	@Override
	public void breakBlock() {
		this.removeSource(this);
	}

	public void incrementMode() {
		control = control.next();
	}

	public void toggleState() {
		active = !active;
		this.updateLightAt(this);
	}

	private static void addSource(TileEntityLampController te) {
		LightSource ls = new LightSource(te);
		map.addValue(te.channel, ls);
		onLightsChanged(te.channel);
	}

	private static void removeSource(TileEntityLampController te) {
		LightSource ls = new LightSource(te);
		map.remove(te.channel, ls);
		onLightsChanged(te.channel);
	}

	public static void addLight(TileEntityRangedLamp te) {
		lights.addValue(te.getChannel(), new WorldLocation(te));
	}

	public static void removeLight(TileEntityRangedLamp te) {
		lights.remove(te.getChannel(), new WorldLocation(te));
	}

	private static void onLightsChanged(int channel) {
		ThreadSafeSet<WorldLocation> c = (ThreadSafeSet<WorldLocation>)lights.get(channel);
		c.simpleIterate((WorldLocation loc) -> {
			TileEntityRangedLamp te = (TileEntityRangedLamp)loc.getTileEntity();
			te.setLit(activeSourceInRange(te));
		});
	}

	private static void updateLightAt(TileEntityLampController te) {
		LightSource ls = new LightSource(te);
		map.remove(te.channel, ls);
		map.addValue(te.channel, ls);
		onLightsChanged(te.channel);
	}

	public static boolean activeSourceInRange(TileEntityRangedLamp te) {
		ThreadSafeSet<LightSource> c = (ThreadSafeSet<LightSource>)map.get(te.getChannel());
		WorldLocation loc = new WorldLocation(te);
		boolean ret = c.iterate((Iterator<LightSource> it) -> {
			while (it.hasNext()) {
				LightSource l = it.next();
				if (l.isActive && l.location.getDistanceTo(loc) <= l.range)
					return true;
			}
			return false;
		});
		return ret;
	}

	private int getRange() {
		int base = MAXRANGE;
		int lvl = adjacency.getAdjacentUpgrade(this);
		double fac = lvl > 0 ? TileEntityRangeBoost.getFactor(lvl-1) : 1;
		if (fac > 1) {
			base = ReikaMathLibrary.ceilPseudo2Exp((int)(base*fac));
		}
		return base;
	}

	@Override
	public void setPowered(boolean power) {
		shaftpower = power;
	}

	@Override
	public boolean canReadFrom(ForgeDirection dir) {
		return true;
	}

	public static void clearCache() {
		map.clear();
		lights.clear();
	}

}

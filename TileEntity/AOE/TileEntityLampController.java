/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE;

import java.util.Collection;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.BlockRangeLamp.TileEntityRangedLamp;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Interfaces.GuiController;
import Reika.DragonAPI.Interfaces.LocationCached;
import Reika.DragonAPI.ModRegistry.PowerTypes;
import Reika.RotaryCraft.API.Power.PowerTransferHelper;
import Reika.RotaryCraft.API.Power.SimpleShaftPowerReceiver;
import cofh.api.energy.IEnergyHandler;

public class TileEntityLampController extends TileEntityChromaticBase implements LocationCached, GuiController, SimpleShaftPowerReceiver {

	private static final MultiMap<Integer, LightSource> map = new MultiMap(new MultiMap.HashSetFactory());
	private static final MultiMap<Integer, WorldLocation> lights = new MultiMap(new MultiMap.HashSetFactory());

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
				return PowerTypes.RF.exists();
			case SHAFTPOWER:
				return PowerTypes.ROTARYCRAFT.exists();
			default:
				return true;
			}
		}
	}

	private static class LightSource {

		public final WorldLocation location;
		private boolean isActive;

		private LightSource(WorldLocation loc) {
			location = loc;
		}

		public LightSource(TileEntityLampController te) {
			this(new WorldLocation(te));
			isActive = te.isActive();
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
				on = world.isBlockIndirectlyGettingPowered(x, y, z);
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
		if (!PowerTypes.RF.exists()) {
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
		Collection<WorldLocation> c = lights.get(channel);
		for (WorldLocation loc : c) {
			TileEntityRangedLamp te = (TileEntityRangedLamp)loc.getTileEntity();
			te.setLit(activeSourceInRange(te));
		}
	}

	private static void updateLightAt(TileEntityLampController te) {
		LightSource ls = new LightSource(te);
		map.remove(te.channel, ls);
		map.addValue(te.channel, ls);
		onLightsChanged(te.channel);
	}

	public static boolean activeSourceInRange(TileEntityRangedLamp te) {
		Collection<LightSource> c = map.get(te.getChannel());
		WorldLocation loc = new WorldLocation(te);
		for (LightSource l : c) {
			if (l.isActive && l.location.getDistanceTo(loc) <= getRange())
				return true;
		}
		return false;
	}

	private static int getRange() {
		return MAXRANGE;
	}

	@Override
	public void setPowered(boolean power) {
		shaftpower = power;
	}

	@Override
	public boolean canReadFrom(ForgeDirection dir) {
		return true;
	}

}

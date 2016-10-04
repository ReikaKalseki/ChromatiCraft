/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityFluidRelay;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class FluidNetwork {

	private static final PushSorter pushSorter = new PushSorter();
	private static final SuckSorter suckSorter = new SuckSorter();

	private final ArrayList<PressureData> relays = new ArrayList();
	private final ArrayList<PressureData> pushers = new ArrayList();
	private final ArrayList<PressureData> suckers = new ArrayList();

	private long lastUpdate;

	public void merge(FluidNetwork net, World world) {
		if (net != null && net != this) {
			for (PressureData p : net.relays) {
				TileEntityFluidRelay te = p.getTile(world);
				this.add(te);
				te.assignNetwork(this);
			}
			net.clear();
		}
	}

	private void clear() {
		relays.clear();
		pushers.clear();
		suckers.clear();
	}

	public void add(TileEntityFluidRelay te) {
		PressureData p = new PressureData(te);
		if (relays.contains(p))
			return;
		relays.add(p);
		Collections.sort(relays, suckSorter);
		if (p.pressure > 0) {
			pushers.add(p);
			Collections.sort(pushers, pushSorter);
		}
		else if (p.pressure < 0) {
			suckers.add(p);
			Collections.sort(suckers, suckSorter);
		}
	}

	public void remove(TileEntityFluidRelay te) {
		PressureData p = new PressureData(te);
		relays.remove(p);
		pushers.remove(p);
		suckers.remove(p);
	}

	public void updateState(TileEntityFluidRelay te) {
		this.remove(te);
		this.add(te);
	}

	public void update(World world) {
		if (lastUpdate != world.getTotalWorldTime()) {
			this.doUpdate(world);
		}
		lastUpdate = world.getTotalWorldTime();
	}

	private void doUpdate(World world) {
		Iterator<PressureData> it = pushers.iterator();
		while(it.hasNext()) {
			PressureData p = it.next();
			TileEntityFluidRelay te = p.getTile(world);
			if (te == null) {
				it.remove();
				continue;
			}
			this.tryPush(te);
		}

		it = suckers.iterator();
		while(it.hasNext()) {
			PressureData p = it.next();
			TileEntityFluidRelay te = p.getTile(world);
			if (te == null) {
				it.remove();
				continue;
			}
			this.trySuck(te);
		}
	}

	private void tryPush(TileEntityFluidRelay te) {
		te.pushFluids();
		/*
		for (Fluid f : map.keySet()) {
			int supply = this.suck(te.worldObj, f, map.get(f), false);
			this.push(te.worldObj, f, supply, false);
		}
		 */
	}

	private void trySuck(TileEntityFluidRelay te) {
		te.suckFluids();
		/*
		for (Fluid f : map.keySet()) {
			int space = this.push(te.worldObj, f, map.get(f), false);
			this.suck(te.worldObj, f, space, false);
		}
		 */
	}

	public int suck(World world, Fluid f, int max) {
		int ret = 0;
		Iterator<PressureData> it = relays.iterator();
		while(it.hasNext()) {
			PressureData p = it.next();
			if (p.pressure >= 0) {
				TileEntityFluidRelay te = p.getTile(world);

				if (te == null) {
					it.remove();
					continue;
				}

				int amt = te.suckFluid(f, max);
				if (amt > 0) {
					max -= amt;
					ret += amt;
					if (max == 0)
						break;
				}
			}
		}
		return ret;
	}

	public int push(World world, Fluid f, int max) {
		int ret = 0;
		Iterator<PressureData> it = relays.iterator();
		while(it.hasNext()) {
			PressureData p = it.next();
			if (p.pressure <= 0) {
				TileEntityFluidRelay te = p.getTile(world);

				if (te == null) {
					it.remove();
					continue;
				}

				int amt = te.pushFluid(f, max);
				if (amt > 0) {
					max -= amt;
					ret += amt;
					if (max == 0)
						break;
				}
			}
		}
		return ret;
	}

	private static class PressureData {

		private final Coordinate location;
		private final int pressure;

		private PressureData(TileEntityFluidRelay te) {
			location = new Coordinate(te);
			pressure = te.getPressure();
		}

		private TileEntityFluidRelay getTile(World world) {
			return (TileEntityFluidRelay)location.getTileEntity(world);
		}

		@Override
		public int hashCode() {
			return location.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof PressureData && ((PressureData)o).location.equals(location);
		}

		@Override
		public String toString() {
			return location.toString()+": "+pressure;
		}

	}

	private static class SuckSorter implements Comparator<PressureData> {

		@Override
		public int compare(PressureData o1, PressureData o2) {
			return Integer.compare(o1.pressure, o2.pressure);
		}

	}

	private static class PushSorter implements Comparator<PressureData> {

		@Override
		public int compare(PressureData o1, PressureData o2) {
			return -Integer.compare(o1.pressure, o2.pressure);
		}

	}

}

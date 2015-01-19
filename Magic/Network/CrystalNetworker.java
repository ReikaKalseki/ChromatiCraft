/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.EnumMap;
import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger;
import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.FlowFail;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.TileEntityCache;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;

public class CrystalNetworker implements TickHandler {

	public static final CrystalNetworker instance = new CrystalNetworker();

	private static final String NBT_TAG = "crystalnet";

	private final TileEntityCache<CrystalNetworkTile> tiles = new TileEntityCache();
	private final EnumMap<CrystalElement, TileEntityCache<TileEntityCrystalPylon>> pylons = new EnumMap(CrystalElement.class);
	private final MultiMap<Integer, CrystalFlow> flows = new MultiMap();

	private int losTimer = 0;

	private CrystalNetworker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void clearOnUnload(WorldEvent.Unload evt) {
		PylonFinder.stopAllSearches();
		int dim = evt.world.provider.dimensionId;
		try {
			this.clear(dim);
			for (WorldLocation c : tiles.keySet()) {
				CrystalNetworkTile te = tiles.get(c);
				PylonFinder.removePathsWithTile(te);
				if (te instanceof CrystalTransmitter)
					((CrystalTransmitter)te).clearTargets(true);
			}
			tiles.removeWorld(evt.world);
			for (TileEntityCache c : pylons.values())
				c.removeWorld(evt.world);
		}
		catch (ConcurrentModificationException e) {
			ChromatiCraft.logger.logError("Clearing the crystal network on world unload caused a CME. This is indicative of a deeper problem.");
			e.printStackTrace();
		}
	}

	private void save(NBTTagCompound NBT) {
		NBTTagCompound tag = NBT.getCompoundTag(NBT_TAG);
		tiles.writeToNBT(tag);
		NBT.setTag(NBT_TAG, tag);
		//ReikaJavaLibrary.pConsole(tiles+" to "+tag, Side.SERVER);
	}

	private void load(NBTTagCompound NBT) {
		NBTTagCompound tag = NBT.getCompoundTag(NBT_TAG);
		tiles.readFromNBT(tag);

		for (CrystalNetworkTile te : tiles.values()) {
			if (te instanceof TileEntityCrystalPylon) {
				TileEntityCrystalPylon tile = (TileEntityCrystalPylon)te;
				this.addPylon(tile);
			}
		}
		//ReikaJavaLibrary.pConsole(tiles+" from "+tag, Side.SERVER);
	}

	public boolean checkConnectivity(CrystalElement e, CrystalReceiver r) {
		CrystalPath p = new PylonFinder(e, r).findPylon();
		return p != null && p.canTransmit();
	}

	public CrystalSource getConnectivity(CrystalElement e, CrystalReceiver r) {
		CrystalPath p = new PylonFinder(e, r).findPylon();
		return p != null && p.canTransmit() ? p.transmitter : null;
	}

	public boolean makeRequest(CrystalReceiver r, CrystalElement e, int amount, int range) {
		return this.makeRequest(r, e, amount, r.getWorld(), range, Integer.MAX_VALUE);
	}

	public boolean makeRequest(CrystalReceiver r, CrystalElement e, int amount, int range, int maxthru) {
		return this.makeRequest(r, e, amount, r.getWorld(), range, maxthru);
	}

	public boolean makeRequest(CrystalReceiver r, CrystalElement e, int amount, World world, int range, int maxthru) {
		if (amount <= 0)
			return false;
		if (this.hasFlowTo(r, e, world))
			return false;
		CrystalFlow p = new PylonFinder(e, r).findPylon(amount, maxthru);
		//ReikaJavaLibrary.pConsole(p, Side.SERVER);
		CrystalNetworkLogger.logRequest(r, e, amount, p);
		if (p != null) {
			flows.addValue(world.provider.dimensionId, p);
			return true;
		}
		return false;
	}

	public boolean hasFlowTo(CrystalReceiver r, CrystalElement e, World world) {
		Collection<CrystalFlow> li = flows.get(world.provider.dimensionId);
		for (CrystalFlow f : li) {
			if (f.element == e && f.receiver == r)
				return true;
		}
		return false;
	}

	public void tick(TickType type, Object... data) {
		losTimer++;
		boolean doCheckLOS = false;
		if (losTimer >= 40) {
			losTimer = 0;
			doCheckLOS = true;
		}
		for (int dim : flows.keySet()) {
			Iterator<CrystalFlow> it = flows.get(dim).iterator();
			while (it.hasNext()) {
				CrystalFlow p = it.next();
				if (p.transmitter.canConduct() && p.canTransmit()) {
					int amt = p.drain();
					if (amt > 0) {
						p.receiver.receiveElement(p.element, amt);
						p.transmitter.drain(p.element, amt);
						if (p.isComplete()) {
							p.resetTiles();
							it.remove();
							CrystalNetworkLogger.logFlowSatisfy(p);
						}
					}
				}
				else {
					CrystalNetworkLogger.logFlowBreak(p, FlowFail.ENERGY);
					p.receiver.onPathBroken(p.element);
					p.resetTiles();
					it.remove();
				}
			}

			if (doCheckLOS) {
				it = flows.get(dim).iterator();
				while (it.hasNext()) {
					CrystalFlow p = it.next();
					if (!p.checkLineOfSight()) {
						CrystalNetworkLogger.logFlowBreak(p, FlowFail.SIGHT);
						p.receiver.onPathBroken(p.element);
						p.resetTiles();
						it.remove();
					}
				}
			}
		}
	}

	public void clear(int dim) {
		//do not clear tiles!
		Collection<CrystalFlow> li = flows.get(dim);
		for (CrystalFlow f : li) {
			f.resetTiles();
		}
		flows.remove(dim);
	}

	public void addTile(CrystalNetworkTile te) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			WorldLocation loc = PylonFinder.getLocation(te);
			CrystalNetworkTile old = tiles.get(loc);
			if (old != null) { //cache cleaning; old TEs may get out of sync for things like charge
				this.removeTile(old);
			}
			tiles.put(loc, te);
			if (te instanceof TileEntityCrystalPylon) {
				this.addPylon((TileEntityCrystalPylon)te);
			}

			WorldCrystalNetworkData.initNetworkData(te.getWorld()).setDirty(true);
		}
	}

	private void addPylon(TileEntityCrystalPylon te) {
		CrystalElement e = te.getColor();
		TileEntityCache<TileEntityCrystalPylon> c = pylons.get(e);
		if (c == null) {
			c = new TileEntityCache();
			pylons.put(e, c);
		}
		c.put(te);
	}

	public Collection<TileEntityCrystalPylon> getNearbyPylons(World world, int x, int y, int z, CrystalElement e, int range, boolean LOS) {
		TileEntityCache<TileEntityCrystalPylon> c = pylons.get(e);
		Collection<TileEntityCrystalPylon> li = new ArrayList();
		if (c != null) {
			for (WorldLocation loc : c.keySet()) {
				if (loc.dimensionID == world.provider.dimensionId && loc.getDistanceTo(x, y, z) <= range) {
					if (!LOS || PylonFinder.lineOfSight(world, x, y, z, loc.xCoord, loc.yCoord, loc.zCoord)) {
						li.add(c.get(loc));
					}
				}
			}
		}
		return li;
	}

	public void removeTile(CrystalNetworkTile te) {
		tiles.remove(PylonFinder.getLocation(te));
		if (te instanceof TileEntityCrystalPylon) {
			TileEntityCrystalPylon tile = (TileEntityCrystalPylon)te;
			TileEntityCache<TileEntityCrystalPylon> c = pylons.get(tile.getColor());
			if (c != null)
				c.remove(tile);
		}
		Collection<CrystalFlow> li = flows.get(te.getWorld().provider.dimensionId);
		Iterator<CrystalFlow> it = li.iterator();
		while (it.hasNext()) {
			CrystalFlow p = it.next();
			if (p.contains(te)) {
				CrystalNetworkLogger.logFlowBreak(p, FlowFail.TILE);
				p.resetTiles();
				p.receiver.onPathBroken(p.element);
				it.remove();
			}
		}
		PylonFinder.removePathsWithTile(te);
		WorldCrystalNetworkData.initNetworkData(te.getWorld()).setDirty(false); //true?
	}

	public void breakPaths(CrystalNetworkTile te) {
		Collection<CrystalFlow> li = flows.get(te.getWorld().provider.dimensionId);
		Iterator<CrystalFlow> it = li.iterator();
		while (it.hasNext()) {
			CrystalFlow p = it.next();
			if (p.contains(te)) {
				CrystalNetworkLogger.logFlowBreak(p, FlowFail.TILE);
				p.receiver.onPathBroken(p.element);
				p.resetTiles();
				it.remove();
			}
		}
	}

	ArrayList<CrystalTransmitter> getTransmittersTo(CrystalReceiver r, CrystalElement e) {
		ArrayList<CrystalTransmitter> li = new ArrayList();
		for (WorldLocation c : tiles.keySet()) {
			if (c.dimensionID == r.getWorld().provider.dimensionId) {
				CrystalNetworkTile tile = tiles.get(c);
				if (tile instanceof CrystalTransmitter && r != tile) {
					CrystalTransmitter te = (CrystalTransmitter)tile;
					if (te.canConduct()) {
						if (e == null || te.isConductingElement(e)) {
							double d = te.getDistanceSqTo(r.getX(), r.getY(), r.getZ());
							//ReikaJavaLibrary.pConsole(e+": "+d+": "+te);
							double send = te.getSendRange();
							double dist = r.getReceiveRange();
							if (d <= Math.min(dist*dist, send*send)) {
								li.add(te);
							}
						}
					}
				}
			}
		}
		return li;
	}

	ArrayList<CrystalReceiver> getNearbyReceivers(CrystalTransmitter r, CrystalElement e) {
		ArrayList<CrystalReceiver> li = new ArrayList();
		for (WorldLocation c : tiles.keySet()) {
			if (c.dimensionID == r.getWorld().provider.dimensionId) {
				CrystalNetworkTile tile = tiles.get(c);
				if (tile instanceof CrystalReceiver && r != tile) {
					CrystalReceiver te = (CrystalReceiver)tile;
					if (te.canConduct()) {
						if (e == null || te.isConductingElement(e)) {
							double d = te.getDistanceSqTo(r.getX(), r.getY(), r.getZ());
							//ReikaJavaLibrary.pConsole(e+": "+d+": "+te);
							double send = r.getSendRange();
							double dist = te.getReceiveRange();
							if (d <= Math.min(dist*dist, send*send)) {
								li.add(te);
							}
						}
					}
				}
			}
		}
		return li;
	}

	ArrayList<CrystalSource> getAllSourcesFor(CrystalElement e, boolean activeOnly) {
		ArrayList<CrystalSource> li = new ArrayList();
		for (WorldLocation c : tiles.keySet()) {
			CrystalNetworkTile tile = tiles.get(c);
			if (tile instanceof CrystalSource) {
				CrystalSource te = (CrystalSource)tile;
				if (te.canConduct() || !activeOnly) {
					if (e == null || te.isConductingElement(e)) {
						li.add(te);
					}
				}
			}
		}
		return li;
	}

	@Override
	public TickType getType() {
		return TickType.SERVER;
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.START;
	}

	@Override
	public String getLabel() {
		return "Crystal Networker";
	}

	public static class WorldCrystalNetworkData extends WorldSavedData {

		private static final String IDENTIFIER = NBT_TAG;

		public WorldCrystalNetworkData() {
			super(IDENTIFIER);
		}

		public WorldCrystalNetworkData(String s) {
			super(s);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			instance.load(NBT);
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			instance.save(NBT);
		}

		private static WorldCrystalNetworkData initNetworkData(World world) {
			WorldCrystalNetworkData data = (WorldCrystalNetworkData)world.loadItemData(WorldCrystalNetworkData.class, IDENTIFIER);
			if (data == null) {
				data = new WorldCrystalNetworkData();
				world.setItemData(IDENTIFIER, data);
			}
			return data;
		}
	}

}

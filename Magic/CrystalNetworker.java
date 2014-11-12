/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.TileEntityCache;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;

public class CrystalNetworker implements TickHandler {

	public static final CrystalNetworker instance = new CrystalNetworker();

	private static final String NBT_TAG = "crystalnet";

	private final TileEntityCache<CrystalNetworkTile> tiles = new TileEntityCache();
	private final HashMap<Integer, ArrayList<CrystalFlow>> flows = new HashMap();

	private int losTimer = 0;

	private CrystalNetworker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void clearOnUnload(WorldEvent.Unload evt) {
		int dim = evt.world.provider.dimensionId;
		this.clear(dim);
		for (WorldLocation c : tiles.keySet()) {
			CrystalNetworkTile te = tiles.get(c);
			if (te instanceof CrystalTransmitter)
				((CrystalTransmitter)te).clearTargets();
		}
		tiles.removeWorld(evt.world);
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

	public void makeRequest(CrystalReceiver r, CrystalElement e, int amount, int range) {
		this.makeRequest(r, e, amount, r.getWorld(), r.getX(), r.getY(), r.getZ(), range);
	}

	public void makeRequest(CrystalReceiver r, CrystalElement e, int amount, World world, int x, int y, int z, int range) {
		if (amount <= 0)
			return;
		if (this.hasFlowTo(r, e, world))
			return;
		CrystalFlow p = new PylonFinder(e, r).findPylon(amount);
		//ReikaJavaLibrary.pConsole(p, Side.SERVER);
		CrystalNetworkLogger.logRequest(r, e, amount, p);
		if (p != null) {
			this.addFlow(world, p);
		}
	}

	public boolean hasFlowTo(CrystalReceiver r, CrystalElement e, World world) {
		ArrayList<CrystalFlow> li = flows.get(world.provider.dimensionId);
		if (li == null)
			return false;
		for (CrystalFlow f : li) {
			if (f.element == e && f.receiver == r)
				return true;
		}
		return false;
	}

	private void addFlow(World world, CrystalFlow p) {
		ArrayList<CrystalFlow> li = flows.get(world.provider.dimensionId);
		if (li == null) {
			li = new ArrayList();
			flows.put(world.provider.dimensionId, li);
		}
		li.add(p);
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
						}
					}
				}
				else {
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
		ArrayList<CrystalFlow> li = flows.get(dim);
		if (li != null) {
			for (CrystalFlow f : li) {
				f.resetTiles();
			}
			flows.remove(dim);
		}
	}

	public void addTile(CrystalNetworkTile te) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			tiles.put(new WorldLocation(te.getWorld(), te.getX(), te.getY(), te.getZ()), te);
			WorldCrystalNetworkData.initNetworkData(te.getWorld()).setDirty(true);
		}
	}

	public void removeTile(CrystalNetworkTile te) {
		tiles.remove(new WorldLocation(te.getWorld(), te.getX(), te.getY(), te.getZ()));
		ArrayList<CrystalFlow> li = flows.get(te.getWorld().provider.dimensionId);
		if (li != null) {
			Iterator<CrystalFlow> it = li.iterator();
			while (it.hasNext()) {
				CrystalFlow p = it.next();
				if (p.contains(te)) {
					p.resetTiles();
					p.receiver.onPathBroken(p.element);
					it.remove();
				}
			}
		}
		WorldCrystalNetworkData.initNetworkData(te.getWorld()).setDirty(false);
	}

	public void breakPaths(CrystalNetworkTile te) {
		ArrayList<CrystalFlow> li = flows.get(te.getWorld().provider.dimensionId);
		if (li != null) {
			Iterator<CrystalFlow> it = li.iterator();
			while (it.hasNext()) {
				CrystalFlow p = it.next();
				if (p.contains(te)) {
					p.receiver.onPathBroken(p.element);
					p.resetTiles();
					it.remove();
				}
			}
		}
	}

	ArrayList<CrystalTransmitter> getTransmittersTo(CrystalReceiver r, CrystalElement e) {
		ArrayList<CrystalTransmitter> li = new ArrayList();
		for (WorldLocation c : tiles.keySet()) {
			if (c.dimensionID == r.getWorld().provider.dimensionId) {
				CrystalNetworkTile tile = tiles.get(c);
				if (tile instanceof CrystalTransmitter) {
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
	/*
	ArrayList<CrystalNetworkTile> getTilesWithinDofXYZ(World world, int x, int y, int z, double dist) {
		return this.getTransmittersWithinDofXYZ(world, x, y, z, dist, null);
	}*/

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

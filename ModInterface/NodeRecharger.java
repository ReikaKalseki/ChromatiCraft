/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.event.world.WorldEvent;
import thaumcraft.api.nodes.INode;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class NodeRecharger implements TickHandler {

	public static final NodeRecharger instance = new NodeRecharger();

	private static final String NBT_TAG = "PYLON_RECHARGE_DATA";

	private final HashMap<Integer, HashMap<WorldLocation, NodeReceiverWrapper>> nodes = new HashMap();

	private final HashSet<Integer> ticked = new HashSet();

	private NodeRecharger() {

	}

	public boolean addNode(INode n) {
		WorldLocation loc = new WorldLocation((TileEntity)n);
		return this.addLocation(loc, n, true);
	}

	private HashMap<WorldLocation, NodeReceiverWrapper> getOrCreateMap(int id) {
		HashMap<WorldLocation, NodeReceiverWrapper> map = nodes.get(id);
		if (map == null) {
			map = new HashMap();
			nodes.put(id, map);
		}
		return map;
	}

	@Override
	public void tick(TickType type, Object... tickData) {
		World world = (World)tickData[0];

		int id = world.provider.dimensionId;
		HashMap<WorldLocation, NodeReceiverWrapper> map = nodes.get(id);
		//if (!ticked.contains(id)) {
		NodeRechargeData.initNetworkData(world).setDirty(true);
		//ticked.add(id);
		//}

		if (map != null) {
			HashSet<WorldLocation> remove = new HashSet();

			for (WorldLocation loc : map.keySet()) {
				NodeReceiverWrapper node = map.get(loc);
				if (loc.getTileEntity() instanceof INode) {
					node.tick();
				}
				else {
					remove.add(loc);
				}
			}
			for (WorldLocation loc : remove) {
				this.removeLocation(loc, world, true);
			}
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload evt) {
		ticked.remove(evt.world.provider.dimensionId);
	}

	private boolean addLocation(WorldLocation loc, INode n, boolean save) {
		HashMap<WorldLocation, NodeReceiverWrapper> map = this.getOrCreateMap(loc.dimensionID);
		//if (!map.containsKey(loc)) {
		NodeReceiverWrapper wrap = new NodeReceiverWrapper(n);
		//CrystalNetworker.instance.addTile(wrap);
		map.put(loc, wrap);
		if (save)
			NodeRechargeData.initNetworkData(((TileEntity)n).worldObj).setDirty(true);
		return true;
		//}
		//return false;
	}

	private boolean removeLocation(WorldLocation loc, World world, boolean save) {
		//CrystalNetworker.instance.removeTile(nodes.get(loc));
		HashMap<WorldLocation, NodeReceiverWrapper> map = nodes.get(world.provider.dimensionId);
		if (map != null) {
			map.remove(loc);
			if (save)
				NodeRechargeData.initNetworkData(world).setDirty(false);
			return true;
		}
		return false;
	}

	public NodeReceiverWrapper getWrapper(WorldLocation loc) {
		HashMap<WorldLocation, NodeReceiverWrapper> map = this.getOrCreateMap(loc.dimensionID);
		NodeReceiverWrapper wrap = map.get(loc);
		if (wrap == null) {
			TileEntity te = loc.getTileEntity();
			if (te instanceof INode) {
				this.addLocation(loc, (INode)te, true);
			}
		}
		return wrap;
	}

	@Override
	public EnumSet<TickType> getType() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.END;
	}

	@Override
	public String getLabel() {
		return "CC-TC Node Crystal Net Recharging";
	}

	private void save(NBTTagCompound NBT) {
		NBTTagCompound tag = NBT.getCompoundTag(NBT_TAG);

		NBTTagList li = new NBTTagList();
		for (HashMap<WorldLocation, NodeReceiverWrapper> map : nodes.values()) {
			for (WorldLocation loc : map.keySet()) {
				NBTTagCompound nbt = new NBTTagCompound();
				loc.writeToNBT(nbt);
				li.appendTag(nbt);

				//ReikaJavaLibrary.pConsole("Saving node at "+loc);
			}
		}

		//ReikaJavaLibrary.pConsole("Saved nodes "+nodes+" to "+li);
		tag.setTag("list", li);
		NBT.setTag(NBT_TAG, tag);
		//ReikaJavaLibrary.pConsole(tiles+" to "+tag, Side.SERVER);
	}

	private void load(NBTTagCompound NBT) {
		NBTTagCompound tag = NBT.getCompoundTag(NBT_TAG);

		NBTTagList li = tag.getTagList("list", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			NBTTagCompound nbt = (NBTTagCompound)o;
			WorldLocation loc = WorldLocation.readFromNBT(nbt);
			TileEntity te = loc.getTileEntity();
			if (te instanceof INode) {
				this.addLocation(loc, (INode)te, false);
				//ReikaJavaLibrary.pConsole("Loading node at "+loc);
			}
			else {
				ChromatiCraft.logger.logError("Data saved a TC aura node at "+loc+", but the node was deleted between world save and reload?!");
			}
		}
		//ReikaJavaLibrary.pConsole("Loaded nodes "+nodes+" from "+li);
	}

	/*

	 	private void save(NBTTagCompound NBT) {
		NBTTagCompound tag = NBT.getCompoundTag(NBT_TAG);

		HashMap<Integer, NBTTagList> lim = new HashMap();
		for (HashMap<WorldLocation, NodeReceiverWrapper> map : nodes.values()) {
			for (WorldLocation loc : map.keySet()) {
				NBTTagCompound nbt = new NBTTagCompound();
				loc.writeToNBT(nbt);
				NBTTagList li = lim.get(loc.dimensionID);
				if (li == null) {
					li = new NBTTagList();
					lim.put(loc.dimensionID, li);
				}
				li.appendTag(nbt);
				//ReikaJavaLibrary.pConsole("Saving node at "+loc);
			}
		}

		for (Integer dim : lim.keySet()) {
			tag.setTag("list_"+dim, lim.get(dim));
		}
		NBT.setTag(NBT_TAG, tag);
		//ReikaJavaLibrary.pConsole(tiles+" to "+tag, Side.SERVER);
	}

	private void load(NBTTagCompound NBT) {
		NBTTagCompound tag = NBT.getCompoundTag(NBT_TAG);

		for (String s : ((Set<String>)tag.func_150296_c())) {
			NBTTagList li = tag.getTagList(s, NBTTypes.COMPOUND.ID);
			for (Object o : li.tagList) {
				NBTTagCompound nbt = (NBTTagCompound)o;
				WorldLocation loc = WorldLocation.readFromNBT(nbt);
				TileEntity te = loc.getTileEntity();
				if (te instanceof INode) {
					this.addLocation(loc, (INode)te, false);
					//ReikaJavaLibrary.pConsole("Loading node at "+loc);
				}
				else {
					ChromatiCraft.logger.logError("Data saved a TC aura node at "+loc+", but the node was deleted between world save and reload?!");
				}
			}
		}
	}

	 */

	public static class NodeRechargeData extends WorldSavedData {

		private static final String IDENTIFIER = NBT_TAG;

		public NodeRechargeData() {
			super(IDENTIFIER);
		}

		public NodeRechargeData(String s) {
			super(s);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			if (ModList.THAUMCRAFT.isLoaded())
				instance.load(NBT);
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			instance.save(NBT);
		}

		private static NodeRechargeData initNetworkData(World world) {
			NodeRechargeData data = (NodeRechargeData)world.loadItemData(NodeRechargeData.class, IDENTIFIER);
			if (data == null) {
				data = new NodeRechargeData();
				world.setItemData(IDENTIFIER, data);
			}
			return data;
		}
	}

}

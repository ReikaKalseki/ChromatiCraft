/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.ThaumCraft;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.event.world.WorldEvent;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.nodes.INode;

public class NodeRecharger implements TickHandler {

	public static final NodeRecharger instance = new NodeRecharger();

	private static final String NBT_TAG = "PYLON_RECHARGE_DATA";
	private static final String EXTRA_TAG = "EXTRA_NODE_DATA";

	private final HashMap<Integer, HashMap<WorldLocation, NodeReceiverWrapper>> nodes = new HashMap();

	private final HashSet<BlockKey> blacklist = new HashSet();

	private final HashSet<Integer> ticked = new HashSet();

	private NodeRecharger() {
		Block b = GameRegistry.findBlock("ThaumicHorizons", "synthNode");
		if (b != null)
			blacklist.add(new BlockKey(b));
	}

	public boolean addNode(INode n, EntityPlayer ep) {
		TileEntity te = (TileEntity)n;
		if (blacklist.contains(new BlockKey(te.getBlockType(), te.getBlockMetadata())))
			return false;
		WorldLocation loc = new WorldLocation(te);
		this.addLocation(loc, n, ep);
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void updateClient(WorldLocation loc, NBTTagCompound data) {
		World world = Minecraft.getMinecraft().theWorld;
		HashMap<WorldLocation, NodeReceiverWrapper> map = this.getOrCreateMap(world.provider.dimensionId);
		NodeReceiverWrapper wrap = map.get(loc);
		if (wrap == null) {
			TileEntity te = loc.getTileEntity(world);
			if (te instanceof INode) {
				wrap = new NodeReceiverWrapper((INode)te);
				map.put(loc, wrap);
			}
		}
		if (wrap != null) {
			wrap.load(data, true);
		}
	}

	private HashMap<WorldLocation, NodeReceiverWrapper> getOrCreateMap(int id) {
		HashMap<WorldLocation, NodeReceiverWrapper> map = nodes.get(id);
		if (map == null) {
			map = new HashMap();
			nodes.put(id, map);
		}
		return map;
	}

	public boolean isValidNode(INode tile) { //energized nodes do not implement INode
		return !(tile instanceof CrystalNetworkTile) && !tile.getAspectsBase().aspects.isEmpty();
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

	private void loadLocation(NBTTagCompound tag) {
		WorldLocation loc = WorldLocation.readTag(tag);
		TileEntity te = loc.getTileEntity();
		if (!(te instanceof INode)) {
			ChromatiCraft.logger.logError("Data saved a TC aura node at "+loc+", but the node was deleted between world save and reload?!");
			return;
		}
		INode n = (INode)te;
		NodeReceiverWrapper wrap = new NodeReceiverWrapper(n);
		if (tag.hasKey(EXTRA_TAG))
			wrap.load(tag.getCompoundTag(EXTRA_TAG), false);
		this.register(loc, wrap);
	}

	private void addLocation(WorldLocation loc, INode n, EntityPlayer ep) {
		NodeReceiverWrapper wrap = new NodeReceiverWrapper(n);
		if (ep != null)
			wrap.setOwner(ep);
		this.register(loc, wrap);
		NodeRechargeData.initNetworkData(((TileEntity)n).worldObj).setDirty(true);
	}

	private void register(WorldLocation loc, NodeReceiverWrapper wrap) {
		HashMap<WorldLocation, NodeReceiverWrapper> map = this.getOrCreateMap(loc.dimensionID);
		map.put(loc, wrap);
	}

	public boolean hasLocation(WorldLocation loc) {
		HashMap<WorldLocation, NodeReceiverWrapper> map = nodes.get(loc.dimensionID);
		return map != null && map.containsKey(loc);
	}

	@SideOnly(Side.CLIENT)
	public void renderNodeOverlay(EntityPlayer ep, int gsc, INode te) {
		HashMap<WorldLocation, NodeReceiverWrapper> map = nodes.get(ep.worldObj.provider.dimensionId);
		if (map == null)
			return;
		NodeReceiverWrapper w = map.get(new WorldLocation((TileEntity)te));
		if (w != null) {
			w.renderOverlay(ep, gsc);
		}
	}

	private boolean removeLocation(WorldLocation loc, World world, boolean save) {
		//CrystalNetworker.instance.removeTile(nodes.get(loc));
		HashMap<WorldLocation, NodeReceiverWrapper> map = nodes.get(world.provider.dimensionId);
		if (map != null) {
			CrystalNetworker.instance.breakPaths(map.get(loc));
			map.remove(loc);
			if (save)
				NodeRechargeData.initNetworkData(world).setDirty(false);
			return true;
		}
		return false;
	}

	public NodeReceiverWrapper getWrapper(WorldLocation loc, boolean create) {
		HashMap<WorldLocation, NodeReceiverWrapper> map = create ? this.getOrCreateMap(loc.dimensionID) : nodes.get(loc.dimensionID);
		NodeReceiverWrapper wrap = map.get(loc);
		if (wrap == null && create) {
			TileEntity te = loc.getTileEntity();
			if (te instanceof INode) {
				this.addLocation(loc, (INode)te, null);
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
			for (Entry<WorldLocation, NodeReceiverWrapper> e : map.entrySet()) {
				NBTTagCompound nbt = new NBTTagCompound();
				e.getKey().writeToTag(nbt);
				NBTTagCompound meta = new NBTTagCompound();
				e.getValue().write(meta);
				nbt.setTag(EXTRA_TAG, meta);
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
			this.loadLocation(nbt);
			//ReikaJavaLibrary.pConsole("Loading node at "+loc);
		}
		//ReikaJavaLibrary.pConsole("Loaded nodes "+nodes+" from "+li);
	}

	public ArrayList<String> debug(WorldLocation loc) {
		ArrayList<String> li = new ArrayList();
		NodeReceiverWrapper wrap = this.getWrapper(loc, false);
		if (wrap != null) {
			wrap.debug(li);
		}
		return li;
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

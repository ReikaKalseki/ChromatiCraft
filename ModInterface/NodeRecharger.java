package Reika.ChromatiCraft.ModInterface;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import thaumcraft.api.nodes.INode;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class NodeRecharger implements TickHandler {

	public static final NodeRecharger instance = new NodeRecharger();

	private static final String NBT_TAG = "PYLON_RECHARGE_DATA";

	private final HashMap<WorldLocation, NodeReceiverWrapper> nodes = new HashMap();

	private NodeRecharger() {

	}

	public boolean addNode(INode n) {
		WorldLocation loc = new WorldLocation((TileEntity)n);
		if (!nodes.containsKey(loc)) {
			this.addLocation(loc, n, true);
			return true;
		}
		return false;
	}

	@Override
	public void tick(TickType type, Object... tickData) {
		HashSet<WorldLocation> remove = new HashSet();

		for (WorldLocation loc : nodes.keySet()) {
			NodeReceiverWrapper node = nodes.get(loc);
			if (loc.getTileEntity() instanceof INode) {
				node.tick();
			}
			else {
				remove.add(loc);
			}
		}
		for (WorldLocation loc : remove) {
			this.removeLocation(loc, (World)tickData[0], true);
		}
	}

	private void addLocation(WorldLocation loc, INode n, boolean save) {
		NodeReceiverWrapper wrap = new NodeReceiverWrapper(n);
		//CrystalNetworker.instance.addTile(wrap);
		nodes.put(loc, wrap);
		if (save)
			NodeRechargeData.initNetworkData(((TileEntity)n).worldObj).setDirty(true);
	}

	private void removeLocation(WorldLocation loc, World world, boolean save) {
		//CrystalNetworker.instance.removeTile(nodes.get(loc));
		nodes.remove(loc);
		if (save)
			NodeRechargeData.initNetworkData(world).setDirty(false);
	}

	@Override
	public TickType getType() {
		return TickType.WORLD;
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
		for (WorldLocation loc : nodes.keySet()) {
			NBTTagCompound nbt = new NBTTagCompound();
			loc.writeToNBT(nbt);
			li.appendTag(nbt);

			//ReikaJavaLibrary.pConsole("Saving node at "+loc);
		}

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
	}

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

/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityPylonLink;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.IO.PacketTarget.PlayerTarget;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;


public class PylonLinkNetwork {

	private static final String NBT_TAG = "pylonlinknet";

	public static final PylonLinkNetwork instance = new PylonLinkNetwork();

	private long lastUpdate;
	private final HashMap<UUID, PylonWeb> links = new HashMap();

	private PylonLinkNetwork() {

	}

	public PylonNode addLocation(TileEntityPylonLink te, TileEntityCrystalPylon p) {
		if (te.worldObj.isRemote)
			return null;
		PylonLinkData.initNetworkData(te.worldObj).setDirty(true);
		PylonWeb web = this.getOrCreateWeb(te.getUUID());
		PylonSubweb sw = web.getSubweb(p.getColor());
		PylonNode connection = sw.addNode(te, p);
		p.link(te);
		this.sync(null);
		return connection;
	}

	public void removeLocation(World world, PylonNode connection) {
		if (world.isRemote)
			return;
		PylonLinkData.initNetworkData(world).setDirty(true);
		TileEntity te = connection.pylon.getTileEntity();
		if (te instanceof TileEntityCrystalPylon) {
			((TileEntityCrystalPylon)te).link(null);
		}
		connection.parent.remove(connection);
		this.sync(null);
	}

	private PylonWeb getOrCreateWeb(UUID uid) {
		PylonWeb web = links.get(uid);
		if (web == null) {
			web = new PylonWeb(uid);
			links.put(uid, web);
		}
		return web;
	}

	public Collection<WorldLocation> getLinkedPylons(World world, UUID uid, CrystalElement color) {
		if (!world.isRemote && world.getTotalWorldTime()-lastUpdate > 30*20)
			PylonLinkData.initNetworkData(world).setDirty(true);
		ArrayList<WorldLocation> li = new ArrayList();
		PylonWeb w = links.get(uid);
		if (w == null) {
			//ChromatiCraft.logger.logError("Tried to get link list for unregistered ID "+uid);
			return li;
		}
		if (w.data[color.ordinal()] != null) {
			li.addAll(w.data[color.ordinal()].pylonSet.keySet());
		}
		return li;
	}

	public void clear() {
		links.clear();
	}

	public void load(NBTTagCompound NBT) {
		ChromatiCraft.logger.log("Reloading pylon link data...");
		this.clear();
		NBTTagList li = NBT.getTagList("entries", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			NBTTagCompound tag = (NBTTagCompound)o;
			PylonWeb pw = PylonWeb.readFromNBT(tag);
			links.put(pw.owner, pw);
		}
		this.sync(null);
	}

	public NBTTagCompound save() {
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList li = new NBTTagList();
		tag.setTag("entries", li);
		for (PylonWeb pw : links.values()) {
			li.appendTag(pw.writeToNBT());
		}
		return tag;
	}

	public void sync(EntityPlayerMP ep) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			return;
		PacketTarget pt = ep != null ? new PlayerTarget(ep) : PacketTarget.allPlayers;
		NBTTagCompound nbt = this.save();
		ReikaPacketHelper.sendNBTPacket(ChromatiCraft.packetChannel, ChromaPackets.PYLONLINKCACHE.ordinal(), nbt, pt);
	}

	public static class PylonLinkData extends WorldSavedData {

		private static final String IDENTIFIER = NBT_TAG;

		public PylonLinkData() {
			super(IDENTIFIER);
		}

		public PylonLinkData(String s) {
			super(s);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			instance.load(NBT.getCompoundTag("data"));
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			NBT.setTag("data", instance.save());
		}

		private static PylonLinkData initNetworkData(World world) {
			PylonLinkData data = (PylonLinkData)world.loadItemData(PylonLinkData.class, IDENTIFIER);
			if (data == null) {
				data = new PylonLinkData();
				world.setItemData(IDENTIFIER, data);
			}
			return data;
		}
	}

	private static class PylonWeb {

		private final UUID owner;
		private final PylonSubweb[] data = new PylonSubweb[16];

		private PylonWeb(UUID uid) {
			owner = uid;
		}

		private PylonSubweb getSubweb(CrystalElement e) {
			if (data[e.ordinal()] == null) {
				data[e.ordinal()] = new PylonSubweb(this, e);
			}
			return data[e.ordinal()];
		}

		private static PylonWeb readFromNBT(NBTTagCompound NBT) {
			String uid = NBT.getString("owner");
			PylonWeb pw = new PylonWeb(UUID.fromString(uid));
			for (int i = 0; i < 16; i++) {
				String key = "sub_"+i;
				if (NBT.hasKey(key)) {
					pw.data[i] = PylonSubweb.readFromNBT(pw, NBT.getCompoundTag(key));
				}
			}
			return pw;
		}

		private NBTTagCompound writeToNBT() {
			NBTTagCompound NBT = new NBTTagCompound();
			NBT.setString("owner", owner.toString());
			for (int i = 0; i < 16; i++) {
				if (data[i] != null) {
					String key = "sub_"+i;
					NBT.setTag(key, data[i].writeToNBT());
				}
			}
			return NBT;
		}

	}

	private static class PylonSubweb {

		private final PylonWeb parent;
		private final CrystalElement color;
		private final HashMap<WorldLocation, PylonNode> pylonSet = new HashMap();
		private final HashMap<WorldLocation, PylonNode> linkSet = new HashMap();

		private PylonSubweb(PylonWeb w, CrystalElement e) {
			parent = w;
			color = e;
		}

		private PylonNode addNode(TileEntityPylonLink te, TileEntityCrystalPylon p) {
			return this.addNode(new WorldLocation(te), new WorldLocation(p));
		}

		private PylonNode addNode(WorldLocation loc, WorldLocation py) {
			PylonNode ret = linkSet.get(loc);
			if (ret != null)
				return ret;
			ret = new PylonNode(this, loc, py);
			linkSet.put(loc, ret);
			pylonSet.put(py, ret);
			return ret;
		}

		private void remove(PylonNode pn) {
			pylonSet.remove(pn.pylon);
			linkSet.remove(pn.tile);
		}

		private static PylonSubweb readFromNBT(PylonWeb pw, NBTTagCompound NBT) {
			int color = NBT.getInteger("color");
			PylonSubweb ps = new PylonSubweb(pw, CrystalElement.elements[color]);
			NBTTagList li = NBT.getTagList("nodes", NBTTypes.COMPOUND.ID);
			for (Object o : li.tagList) {
				NBTTagCompound tag = (NBTTagCompound)o;
				PylonNode pn = PylonNode.readFromNBT(ps, tag);
				ps.pylonSet.put(pn.pylon, pn);
				ps.linkSet.put(pn.tile, pn);
			}
			return ps;
		}

		private NBTTagCompound writeToNBT() {
			NBTTagCompound NBT = new NBTTagCompound();
			NBT.setInteger("color", color.ordinal());
			NBTTagList li = new NBTTagList();
			for (PylonNode pn : linkSet.values()) {
				li.appendTag(pn.writeToNBT());
			}
			NBT.setTag("nodes", li);
			return NBT;
		}
	}

	public static class PylonNode {

		private final PylonSubweb parent;
		private final WorldLocation tile;
		private final WorldLocation pylon;

		private PylonNode(PylonSubweb w, WorldLocation loc, WorldLocation py) {
			parent = w;
			tile = loc;
			pylon = py;
		}

		private static PylonNode readFromNBT(PylonSubweb pw, NBTTagCompound NBT) {
			WorldLocation loc = WorldLocation.readFromNBT("loc", NBT);
			WorldLocation py = WorldLocation.readFromNBT("pylon", NBT);
			return new PylonNode(pw, loc, py);
		}

		private NBTTagCompound writeToNBT() {
			NBTTagCompound NBT = new NBTTagCompound();
			tile.writeToNBT("loc", NBT);
			pylon.writeToNBT("pylon", NBT);
			return NBT;
		}

		@Override
		public int hashCode() {
			return tile.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof PylonNode) {
				PylonNode pn = (PylonNode)o;
				return pn.tile.equals(tile) && pn.pylon.equals(pylon) && pn.getColor() == this.getColor();
			}
			return false;
		}

		public CrystalElement getColor() {
			return parent.color;
		}

		public void sync(NBTTagCompound tag) {
			tag.setInteger("color", parent.color.ordinal());
			tag.setString("id", parent.parent.owner.toString());
			tile.writeToNBT("loc", tag);
		}

		public static PylonNode fromSync(NBTTagCompound tag) {
			CrystalElement e = CrystalElement.elements[tag.getInteger("color")];
			UUID uid = UUID.fromString(tag.getString("id"));
			WorldLocation loc = WorldLocation.readFromNBT("loc", tag);
			PylonWeb pw = instance.getOrCreateWeb(uid);
			PylonSubweb ps = pw.getSubweb(e);
			return ps.linkSet.get(loc);
		}

	}

}

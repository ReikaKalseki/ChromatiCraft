/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.Interfaces.Linkable;
import Reika.ChromatiCraft.Auxiliary.Interfaces.LinkedTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.LinkerCallback;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Block.Dimension.Structure.NonEuclid.BlockTeleport.TileEntityTeleport;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityTransportWindow;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockVector;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.CopyableSettings;
import Reika.DragonAPI.Interfaces.TileEntity.NBTCopyable;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;

public class ItemConnector extends ItemChromaTool {

	public ItemConnector(int index) {
		super(index);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		if (world.isRemote)
			return true;
		TileEntity te = world.getTileEntity(x, y, z);

		if (te instanceof NBTCopyable) {
			if (is.stackTagCompound != null && is.stackTagCompound.hasKey("NBT_transfer")) {
				((NBTCopyable)te).readCopyableData(is.stackTagCompound.getCompoundTag("NBT_transfer"));
				is.stackTagCompound = null;
			}
			else {
				if (is.stackTagCompound == null)
					is.stackTagCompound = new NBTTagCompound();
				NBTTagCompound tag = new NBTTagCompound();
				((NBTCopyable)te).writeCopyableData(tag);
				is.stackTagCompound.setTag("NBT_transfer", tag);
			}
			return true;
		}
		else if (te instanceof LinkerCallback) {
			is.stackTagCompound = new NBTTagCompound();
			NBTTagCompound tag = new NBTTagCompound();
			new WorldLocation(te).writeToTag(tag);
			is.stackTagCompound.setTag("callback", tag);
			return true;
		}
		else if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment() && ep.capabilities.isCreativeMode) {
			if (is.stackTagCompound != null && is.stackTagCompound.getBoolean("noneuclid")) {

				int dx = is.stackTagCompound.getInteger("x1");
				int dy = is.stackTagCompound.getInteger("y1");
				int dz = is.stackTagCompound.getInteger("z1");
				int face = is.stackTagCompound.getInteger("facing");

				TileEntity tile = world.getTileEntity(dx, dy, dz);
				if (tile instanceof TileEntityTeleport) {
					((TileEntityTeleport)tile).facing = ForgeDirection.VALID_DIRECTIONS[face].getOpposite();
					((TileEntityTeleport)tile).destination = new BlockVector(x-dx, y+1-dy, z-dz, ForgeDirection.VALID_DIRECTIONS[s].getOpposite());
					ReikaJavaLibrary.pConsole(((TileEntityTeleport)tile).destination);
				}

				is.stackTagCompound = null;
				return true;
			}
			else if (te instanceof TileEntityTeleport) {
				if (is.stackTagCompound == null)
					is.stackTagCompound = new NBTTagCompound();
				is.stackTagCompound.setInteger("x1", x);
				is.stackTagCompound.setInteger("y1", y);
				is.stackTagCompound.setInteger("z1", z);
				is.stackTagCompound.setInteger("facing", s);
				is.stackTagCompound.setBoolean("noneuclid", true);
				ReikaJavaLibrary.pConsole(is.stackTagCompound);
				return true;
			}
		}
		else if (is.stackTagCompound != null && is.stackTagCompound.hasKey("callback")) {
			WorldLocation loc = WorldLocation.readTag(is.stackTagCompound.getCompoundTag("callback"));
			if (loc != null) {
				TileEntity tile = loc.getTileEntity();
				if (tile instanceof LinkerCallback) {
					((LinkerCallback)tile).linkTo(world, x, y, z);
					is.stackTagCompound = null;
					return true;
				}
			}
		}

		{
			Linkage l = Linkage.getLink(te);
			if (l == null) {
				ReikaChatHelper.sendChatToPlayer(ep, "Invalid tile.");
				is.stackTagCompound = null;
				return false;
			}
			Linkage[] ls = this.getLinks(is);
			if (ls == null || ls[0] == null || ls[1] == null || ls[0] != ls[1]) {
				//ReikaChatHelper.sendChatToPlayer(ep, "Invalid tiles.");
				//is.stackTagCompound = null;
				//return false;
			}
			boolean flag = l.tryLink(is, ep, te);
			ChromaSounds sn = flag ? ChromaSounds.USE : ChromaSounds.ERROR;
			sn.playSound(ep);
			return flag;
		}
	}

	private Linkage[] getLinks(ItemStack is) {
		if (is.stackTagCompound == null)
			return null;
		TileEntity te1 = null;
		TileEntity te2 = null;
		if (is.stackTagCompound.hasKey("x1")) {
			int x1 = is.stackTagCompound.getInteger("x1");
			int y1 = is.stackTagCompound.getInteger("y1");
			int z1 = is.stackTagCompound.getInteger("z1");
			int dim1 = is.stackTagCompound.getInteger("w1");
			World w1 = DimensionManager.getWorld(dim1);
			if (w1 != null)
				te1 = w1.getTileEntity(x1, y1, z1);
		}
		if (is.stackTagCompound.hasKey("x2")) {
			int x2 = is.stackTagCompound.getInteger("x2");
			int y2 = is.stackTagCompound.getInteger("y2");
			int z2 = is.stackTagCompound.getInteger("z2");
			int dim2 = is.stackTagCompound.getInteger("w2");
			World w2 = DimensionManager.getWorld(dim2);
			if (w2 != null)
				te2 = w2.getTileEntity(x2, y2, z2);
		}
		return new Linkage[]{Linkage.getLink(te1), Linkage.getLink(te2)};
	}

	private static boolean connectRift(LinkedTile te, World world, int x, int y, int z, ItemStack is, EntityPlayer ep) {
		if (is.stackTagCompound == null) {
			is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setInteger("x1", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("y1", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("z1", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("x2", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("y2", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("z2", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("w1", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("w2", Integer.MIN_VALUE);
		}
		if (is.stackTagCompound.getInteger("x1") == Integer.MIN_VALUE) {
			is.stackTagCompound.setInteger("x1", x);
			is.stackTagCompound.setInteger("y1", y);
			is.stackTagCompound.setInteger("z1", z);
			is.stackTagCompound.setInteger("w1", world.provider.dimensionId);
		}
		else {
			is.stackTagCompound.setInteger("x2", x);
			is.stackTagCompound.setInteger("y2", y);
			is.stackTagCompound.setInteger("z2", z);
			is.stackTagCompound.setInteger("w2", world.provider.dimensionId);
		}
		int x1 = is.stackTagCompound.getInteger("x1");
		int y1 = is.stackTagCompound.getInteger("y1");
		int z1 = is.stackTagCompound.getInteger("z1");
		int x2 = is.stackTagCompound.getInteger("x2");
		int y2 = is.stackTagCompound.getInteger("y2");
		int z2 = is.stackTagCompound.getInteger("z2");
		int dim1 = is.stackTagCompound.getInteger("w1");
		int dim2 = is.stackTagCompound.getInteger("w2");
		World w1 = DimensionManager.getWorld(dim1);
		World w2 = DimensionManager.getWorld(dim2);

		if (x1 != Integer.MIN_VALUE && y1 != Integer.MIN_VALUE && z1 != Integer.MIN_VALUE) {
			if (x1 != Integer.MIN_VALUE && y2 != Integer.MIN_VALUE && z2 != Integer.MIN_VALUE) {
				TileEntity te1 = world.getTileEntity(x1, y1, z1);
				TileEntity te2 = world.getTileEntity(x2, y2, z2);
				if (!(te1 instanceof LinkedTile && te2 instanceof LinkedTile)) {
					ReikaChatHelper.sendChatToPlayer(ep, "Link failed.");
					is.stackTagCompound = null;
					return false;
				}
				LinkedTile rf1 = (LinkedTile)te1;
				LinkedTile rf2 = (LinkedTile)te2;

				//ReikaJavaLibrary.pConsole(rec+"\n"+em);
				if (rf1 == rf2) {
					ReikaChatHelper.writeString("Cannot link a rift to itself!");
					is.stackTagCompound = null;
					return false;
				}
				rf1.reset();
				rf2.reset();
				boolean flag = rf1.linkTo(w2, x2, y2, z2);
				if (flag) {
					ReikaChatHelper.sendChatToPlayer(ep, "Linked "+rf1+" and "+rf2);
					rf1.setPrimary(true);
				}
				else
					ReikaChatHelper.sendChatToPlayer(ep, "Link failed.");
				is.stackTagCompound = null;
			}
		}
		return false;
	}

	private static boolean connectWindow(TileEntityTransportWindow te, World world, int x, int y, int z, ItemStack is, EntityPlayer ep) {
		if (is.stackTagCompound == null) {
			is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setInteger("x1", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("y1", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("z1", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("x2", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("y2", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("z2", Integer.MIN_VALUE);
		}
		if (is.stackTagCompound.getInteger("x1") == Integer.MIN_VALUE) {
			is.stackTagCompound.setInteger("x1", x);
			is.stackTagCompound.setInteger("y1", y);
			is.stackTagCompound.setInteger("z1", z);
		}
		else {
			is.stackTagCompound.setInteger("x2", x);
			is.stackTagCompound.setInteger("y2", y);
			is.stackTagCompound.setInteger("z2", z);
		}
		int x1 = is.stackTagCompound.getInteger("x1");
		int y1 = is.stackTagCompound.getInteger("y1");
		int z1 = is.stackTagCompound.getInteger("z1");
		int x2 = is.stackTagCompound.getInteger("x2");
		int y2 = is.stackTagCompound.getInteger("y2");
		int z2 = is.stackTagCompound.getInteger("z2");

		if (x1 != Integer.MIN_VALUE && y1 != Integer.MIN_VALUE && z1 != Integer.MIN_VALUE) {
			if (x1 != Integer.MIN_VALUE && y2 != Integer.MIN_VALUE && z2 != Integer.MIN_VALUE) {
				TileEntityTransportWindow rf1 = (TileEntityTransportWindow)world.getTileEntity(x1, y1, z1);
				TileEntityTransportWindow rf2 = (TileEntityTransportWindow)world.getTileEntity(x2, y2, z2);

				//ReikaJavaLibrary.pConsole(rec+"\n"+em);
				if (rf1 == null) {
					ReikaChatHelper.writeString("Tile missing at "+x1+", "+y1+", "+z1);
					is.stackTagCompound = null;
					return false;
				}
				if (rf2 == null) {
					ReikaChatHelper.writeString("Tile missing at "+x2+", "+y2+", "+z2);
					is.stackTagCompound = null;
					return false;
				}
				else if (rf1 == rf2) {
					ReikaChatHelper.writeString("Cannot link a window to itself!");
					is.stackTagCompound = null;
					return false;
				}
				if (rf1.canLinkTo(rf2)) {
					//rf1.reset();
					//rf2.reset();
					rf1.linkTo(rf2);
					ReikaChatHelper.sendChatToPlayer(ep, "Linked "+rf1+" and "+rf2);
				}
				else {
					ReikaChatHelper.sendChatToPlayer(ep, "Cannot link windows; states/frames do not match.");
				}
				is.stackTagCompound = null;
			}
		}
		return false;
	}

	private static boolean tryConnection(Linkable te, World world, int x, int y, int z, ItemStack is, EntityPlayer ep) {
		if (is.stackTagCompound == null) {
			is.stackTagCompound = new NBTTagCompound();
			new WorldLocation((TileEntity)te).writeToNBT("end1", is.stackTagCompound);
			return true;
		}
		WorldLocation c1 = WorldLocation.readFromNBT("end1", is.stackTagCompound);
		if (c1 == null) {
			ReikaChatHelper.writeString("No valid other end found");
			return false;
		}
		TileEntity te0 = c1.getTileEntity(world);
		if (!(te0 instanceof Linkable)) {
			ReikaChatHelper.writeString("Tile at other end is invalid");
			return false;
		}
		int dl = c1.getTaxicabDistanceTo(x, y, z)-1;

		//ReikaJavaLibrary.pConsole(is.stackTagCompound);
		//ReikaJavaLibrary.pConsole(dl);
		//if (is.stackSize >= dl || ep.capabilities.isCreativeMode) {
		is.stackTagCompound = null;
		Linkable bb = (Linkable)te0;
		bb.resetOther();
		te.resetOther();
		bb.reset();
		te.reset();
		//ReikaJavaLibrary.pConsole(src+":"+tg, Side.SERVER);
		if (te.tryConnect(world, te0.xCoord, te0.yCoord, te0.zCoord) && bb.tryConnect(world, x, y, z)) {
			//ReikaJavaLibrary.pConsole("connected", Side.SERVER);
			if (!ep.capabilities.isCreativeMode)
				is.stackSize -= dl;
			return true;
		}
		else {
			ReikaChatHelper.writeString("Connection is invalid");
		}
		//}
		return false;
	}

	private static <T> boolean tryCopySettings(CopyableSettings<T> te, World world, int x, int y, int z, ItemStack is, EntityPlayer ep) {
		if (is.stackTagCompound == null) {
			is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setInteger("x1", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("y1", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("z1", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("x2", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("y2", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("z2", Integer.MIN_VALUE);
		}
		if (is.stackTagCompound.getInteger("x1") == Integer.MIN_VALUE) {
			is.stackTagCompound.setInteger("x1", x);
			is.stackTagCompound.setInteger("y1", y);
			is.stackTagCompound.setInteger("z1", z);
			is.stackTagCompound.setInteger("w1", world.provider.dimensionId);
		}
		else {
			is.stackTagCompound.setInteger("x2", x);
			is.stackTagCompound.setInteger("y2", y);
			is.stackTagCompound.setInteger("z2", z);
			is.stackTagCompound.setInteger("w2", world.provider.dimensionId);
		}
		int ex = is.stackTagCompound.getInteger("x1");
		int ey = is.stackTagCompound.getInteger("y1");
		int ez = is.stackTagCompound.getInteger("z1");
		int rx = is.stackTagCompound.getInteger("x2");
		int ry = is.stackTagCompound.getInteger("y2");
		int rz = is.stackTagCompound.getInteger("z2");
		World w1 = DimensionManager.getWorld(is.stackTagCompound.getInteger("w1"));
		World w2 = DimensionManager.getWorld(is.stackTagCompound.getInteger("w2"));

		int dl = Math.abs(ex-rx+ey-ry+ez-rz)-1;

		//ReikaJavaLibrary.pConsole(is.stackTagCompound);
		//ReikaJavaLibrary.pConsole(dl);
		//if (is.stackSize >= dl || ep.capabilities.isCreativeMode) {
		if (rx != Integer.MIN_VALUE && ry != Integer.MIN_VALUE && rz != Integer.MIN_VALUE) {
			if (ex != Integer.MIN_VALUE && ey != Integer.MIN_VALUE && ez != Integer.MIN_VALUE) {
				CopyableSettings<T> em = (CopyableSettings<T>)w1.getTileEntity(ex, ey, ez);
				CopyableSettings<T> rec = (CopyableSettings<T>)w2.getTileEntity(rx, ry, rz);

				//ReikaJavaLibrary.pConsole(rec+"\n"+em);
				if (em == null) {
					ReikaChatHelper.writeString("Tile missing at "+ex+", "+ey+", "+ez);
					is.stackTagCompound = null;
					return false;
				}
				if (rec == null) {
					ReikaChatHelper.writeString("Tile missing at "+rx+", "+ry+", "+rz);
					is.stackTagCompound = null;
					return false;
				}
				else if (rec == em) {
					ReikaChatHelper.writeString("Cannot copy a tile to itself!");
					is.stackTagCompound = null;
					return false;
				}
				//ReikaJavaLibrary.pConsole(src+":"+tg, Side.SERVER);
				if (rec.copySettingsFrom((T)em)) {
					//ReikaJavaLibrary.pConsole("connected", Side.SERVER);
					ReikaChatHelper.sendChatToPlayer(ep, "Copied settings from "+em+" to "+rec);
				}
				else {
					ReikaChatHelper.sendChatToPlayer(ep, "Copy Failed.");
				}
				is.stackTagCompound = null;
			}
		}
		//}
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		is.stackTagCompound = null;
		return is;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (is.stackTagCompound != null) {
			int x1 = is.stackTagCompound.getInteger("x1");
			int y1 = is.stackTagCompound.getInteger("y1");
			int z1 = is.stackTagCompound.getInteger("z1");
			int x2 = is.stackTagCompound.getInteger("x2");
			int y2 = is.stackTagCompound.getInteger("y2");
			int z2 = is.stackTagCompound.getInteger("z2");
			int w1 = is.stackTagCompound.getInteger("w1");
			int w2 = is.stackTagCompound.getInteger("w2");
			if (x1 != Integer.MIN_VALUE)
				li.add(String.format("Connected to %d, %d, %d in DIM%d", x1, y1, z1, w1));
			//li.add(String.format("Anchor 1: %d, %d, %d in DIM%d", x1, y1, z1, w1));
			//if (x2 != Integer.MIN_VALUE)
			//	li.add(String.format("Anchor 2: %d, %d, %d in DIM%d", x2, y2, z2, w2));
		}
	}

	private static enum CopyMode {
		COPY(),
		BROADCAST();
	}

	private static enum Linkage {
		RIFT,
		WINDOW,
		LINKABLE,
		SETTINGS;

		private Linkage() {

		}

		private static Linkage getLink(TileEntity te) {
			if (te == null)
				return null;
			if (te instanceof LinkedTile) {
				return RIFT;
			}
			else if (te instanceof TileEntityTransportWindow) {
				return WINDOW;
			}
			else if (te instanceof Linkable) {
				return LINKABLE;
			}
			else if (te instanceof CopyableSettings) {
				return SETTINGS;
			}
			return null;
		}

		private boolean tryLink(ItemStack is, EntityPlayer ep, TileEntity te) {
			switch(this) {
				case RIFT: {
					return connectRift((LinkedTile)te, te.worldObj, te.xCoord, te.yCoord, te.zCoord, is, ep);
				}
				case WINDOW: {
					return connectWindow((TileEntityTransportWindow)te, te.worldObj, te.xCoord, te.yCoord, te.zCoord, is, ep);
				}
				case LINKABLE: {
					return tryConnection((Linkable)te, te.worldObj, te.xCoord, te.yCoord, te.zCoord, is, ep);
				}
				case SETTINGS: {
					return tryCopySettings((CopyableSettings)te, te.worldObj, te.xCoord, te.yCoord, te.zCoord, is, ep);
				}
			}
			return false;
		}
	}

}

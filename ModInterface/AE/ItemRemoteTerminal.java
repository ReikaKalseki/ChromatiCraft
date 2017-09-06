/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.AE;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;

//@Strippable(value={"appeng.api.features.IWirelessTermHandler"})
public class ItemRemoteTerminal extends ItemChromaTool /*implements IWirelessTermHandler*/ {

	//private static boolean loaded = false;
	//private static Method openGui;
	//private static Object terminalGui;

	public ItemRemoteTerminal(int index) {
		super(index);
	}

	@Override
	@ModDependent(ModList.APPENG)
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IGridHost) {
			IGridNode ign = ((IGridHost)te).getGridNode(ForgeDirection.VALID_DIRECTIONS[s]);
			if (ign != null) {
				this.linkTo(is, ign, new WorldLocation(te), s);
			}
			return true;
		}
		return false;
	}

	@ModDependent(ModList.APPENG)
	private void linkTo(ItemStack is, IGridNode ign, WorldLocation loc, int dir) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		loc.writeToNBT("loc", is.stackTagCompound);
		is.stackTagCompound.setInteger("direction", dir);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		ep.openGui(ChromatiCraft.instance, ChromaGuis.REMOTETERMINAL.ordinal(), world, 0, 0, 0);
		//AEApi.instance().registries().wireless().openWirelessTerminalGui(is, world, ep);
		/*
		if (loaded)
			this.openTerminalGui(ep, world);
		else
			ReikaChatHelper.sendChatToPlayer(ep, "Cannot open terminal GUI; interfacing code errored.");
		 */
		return is;
	}
	/*
	private void openTerminalGui(EntityPlayer ep, World world) {
		try {
			openGui.invoke(ep, null, null, terminalGui);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	 */
	@Override
	@ModDependent(ModList.APPENG)
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		IGridHost ign = this.getLink(is, Minecraft.getMinecraft().theWorld);
		if (ign != null) {
			WorldLocation loc = WorldLocation.readFromNBT("loc", is.stackTagCompound);
			li.add("Linked to network at "+loc);
		}
		else {
			li.add("No link");
		}
	}

	@ModDependent(ModList.APPENG)
	public IGridHost getLink(ItemStack is, World world) {
		if (is.stackTagCompound == null)
			return null;
		WorldLocation loc = WorldLocation.readFromNBT("loc", is.stackTagCompound);
		if (loc == null)
			return null;
		TileEntity te = loc.getTileEntity(world);
		if (te instanceof IGridHost) {
			IGridHost igh = (IGridHost)te;
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[is.stackTagCompound.getInteger("dir")];
			return igh;
		}
		return null;
	}
	/*
	@Override
	public String getEncryptionKey(ItemStack item) {
		if (item.stackTagCompound == null)
			return null;
		NBTTagCompound tag = item.stackTagCompound.getCompoundTag("encrypt");
		return tag.getString("enc");
	}

	@Override
	public void setEncryptionKey(ItemStack item, String encKey, String name) {
		if (item.stackTagCompound == null)
			item.stackTagCompound = new NBTTagCompound();
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("name", name);
		tag.setString("enc", encKey);
		item.stackTagCompound.setTag("encrypt_", tag);
	}

	@Override
	public boolean canHandle(ItemStack is) {
		return is.getItem() == this;
	}

	@Override
	public boolean usePower(EntityPlayer player, double amount, ItemStack is) {
		return false;
	}

	@Override
	public boolean hasPower(EntityPlayer player, double amount, ItemStack is) {
		return false;
	}

	@Override
	public IConfigManager getConfigManager(ItemStack is) {
		return null;
	}*/
	/*
	static {
		try {
			Class plat = Class.forName("appeng.util.Platform");
			Class bridge = Class.forName("appeng.core.sync.GuiBridge");
			openGui = plat.getMethod("openGui", EntityPlayer.class, TileEntity.class, ForgeDirection.class, bridge);
			Field enu = bridge.getField("GUI_WIRELESS_TERM");
			terminalGui = enu.get(null);
			loaded = true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	 */
}

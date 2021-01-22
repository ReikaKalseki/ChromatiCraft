package Reika.ChromatiCraft.TileEntity.Transport;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedCrystalTransmitter;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Network.SourceValidityRule;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityNetworkItemTransporter extends InventoriedCrystalTransmitter implements CrystalSource, CrystalReceiver, NBTTile {

	private Request[] requestFilters = new Request[12];

	private Request currentRequest;
	private Request lastRequest;

	private EntityItem entity;

	public TileEntityNetworkItemTransporter() {
		for (int i = 0; i < requestFilters.length; i++) {
			requestFilters[i] = new Request(i);
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote) {
			for (int i = 0; i < requestFilters.length; i++) {
				if (requestFilters[i].cooldown > 0) {
					requestFilters[i].cooldown--;
				}
			}
			if (currentRequest == null) {
				Request req = this.getNextSlotToRequest();
				if (req != null) {
					boolean success = this.tryFindItem(world, x, y, z, req);
					req.cooldown = success ? 3000*0 : 200;
				}
				else {
					currentRequest = null;
				}
				if (currentRequest != lastRequest) {
					if (currentRequest == null)
						CrystalNetworker.instance.breakPaths(this);
					this.syncAllData(true);
				}
			}
			lastRequest = currentRequest;
		}
		else {
			if (currentRequest != lastRequest) {
				entity = currentRequest != null ? new InertItem(world, ReikaItemHelper.getSizedItemStack(currentRequest.item, 1)) : null;
			}
			lastRequest = currentRequest;
		}
	}

	private Request getNextSlotToRequest() {
		for (int i = 0; i < requestFilters.length; i++) {
			Request r = requestFilters[i];
			if (r.item != null && r.cooldown == 0) {
				if (inv[r.inventorySlot] == null || inv[r.inventorySlot].stackSize < requestFilters[i].requestAmount) {
					return r;
				}
			}
		}
		return null;
	}

	private boolean tryFindItem(World world, int x, int y, int z, Request slot) {
		int num = slot.requestAmount;
		if (inv[slot.inventorySlot] != null)
			num -= inv[slot.inventorySlot].stackSize;
		if (num > 0) {
			currentRequest = slot;
			slot.currentAmount = num;
			Set<CrystalElement> c = this.getRequiredColorsForItem(currentRequest.item);
			for (CrystalElement e : c) {
				if (!CrystalNetworker.instance.checkConnectivity(e, this)) {
					currentRequest = null;
					return false;
				}
			}
			int amt = currentRequest.currentAmount*1000;
			for (CrystalElement e : c)
				if (!CrystalNetworker.instance.makeRequest(this, e, amt, world, this.getReceiveRange(), amt/4, SourceValidityRule.ALWAYS))
					return false;
			return true;
		}
		return false;
	}

	private int getSlotToSendTo(TileEntityNetworkItemTransporter requester) {
		ItemStack look = requester.currentRequest != null ? requester.currentRequest.item : null;
		if (look == null)
			return -1;
		for (int i = 0; i < 12; i++) {
			if (inv[i] != null && ReikaItemHelper.matchStacks(look, inv[i])) {
				return i;
			}
		}
		return -1;
	}

	private boolean hasItemsToSendTo(TileEntityNetworkItemTransporter te) {
		return this.getSlotToSendTo(te) >= 0;
	}

	public static Set<CrystalElement> getRequiredColorsForItem(ItemStack is) {
		Set<CrystalElement> ret = new HashSet(ItemElementCalculator.instance.getValueForItem(is).elementSet());
		ret.add(CrystalElement.LIME);
		return ret;
	}

	@Override
	public int getSendRange() {
		return 32;
	}

	@Override
	public boolean needsLineOfSightToReceiver(CrystalReceiver r) {
		return true;
	}

	@Override
	public boolean canTransmitTo(CrystalReceiver r) {
		return r instanceof CrystalRepeater;
	}

	private boolean isValidReceiver(CrystalReceiver r) {
		return r instanceof TileEntityNetworkItemTransporter && this.hasItemsToSendTo((TileEntityNetworkItemTransporter)r);
	}

	@Override
	public int getPathPriority() {
		return 0;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return true;
	}

	@Override
	public int maxThroughput() {
		return 64000;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getReceiveRange() {
		return 32;
	}

	@Override
	public boolean drain(CrystalElement e, int amt) {
		return true;
	}

	@Override
	public boolean canSupply(CrystalReceiver te, CrystalElement e) {
		return te instanceof TileEntityNetworkItemTransporter && this.hasItemsToSendTo((TileEntityNetworkItemTransporter)te);
	}

	public boolean canBeSuppliedBy(CrystalSource te, CrystalElement e) {
		return currentRequest != null && te instanceof TileEntityNetworkItemTransporter;
	}

	@Override
	public void onUsedBy(EntityPlayer ep, CrystalElement e) {

	}

	@Override
	public boolean playerCanUse(EntityPlayer ep) {
		return true;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 100000;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.NETWORKITEM;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getEnergy(CrystalElement e) {
		return 1000*this.getInventoryStackLimit();
	}

	@Override
	public ElementTagCompound getEnergy() {
		return new ElementTagCompound();
	}

	@Override
	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset(CrystalElement e) {
		return null;
	}

	@Override
	public int receiveElement(CrystalSource src, CrystalElement e, int amt) {
		if (!(src instanceof TileEntityNetworkItemTransporter))
			return 0;
		TileEntityNetworkItemTransporter sender = (TileEntityNetworkItemTransporter)src;
		int slot = sender.getSlotToSendTo(this);
		if (slot >= 0) {
			ItemStack is = sender.inv[slot];
			if (is != null) {
				int items = Math.max(1, amt/1000);
				ItemStack is2 = ReikaItemHelper.getSizedItemStack(is, items);
				//int added = ReikaInventoryHelper.addStackAndReturnCount(is2, this, 12, inv.length-1);
				int added = ReikaInventoryHelper.addStackAndReturnCount(is2, this, currentRequest.inventorySlot, currentRequest.inventorySlot);
				//ReikaJavaLibrary.pConsole("Added "+added+" to "+this+" at "+worldObj.getTotalWorldTime()+", inv: "+Arrays.toString(inv));
				if (added > 0) {
					ReikaInventoryHelper.decrStack(slot, sender.inv, added);
					//ReikaJavaLibrary.pConsole("Removed "+added+" from "+sender+" at "+worldObj.getTotalWorldTime()+", inv: "+Arrays.toString(sender.inv));
					currentRequest.currentAmount -= added;
					if (currentRequest.currentAmount <= 0) {
						currentRequest = null;
					}
				}
				return added*1000;
			}
		}
		return 0;
	}

	@Override
	public boolean canReceiveFrom(CrystalTransmitter r) {
		return currentRequest != null && r instanceof CrystalRepeater;
	}

	@Override
	public boolean needsLineOfSightFromTransmitter(CrystalTransmitter r) {
		return true;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return slot >= 12;
	}

	@Override
	public int getSizeInventory() {
		return 24;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		return i < 12;
	}

	@Override
	public double getIncomingBeamRadius() {
		return 0.125;
	}

	@Override
	public double getOutgoingBeamRadius() {
		return this.getIncomingBeamRadius();
	}

	public double getMaximumBeamRadius() {
		return this.getOutgoingBeamRadius();
	}

	@SideOnly(Side.CLIENT)
	public EntityItem getEntityItem() {
		return entity;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		this.saveFilters(NBT);

		NBT.setInteger("request", currentRequest != null ? currentRequest.index : -1);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		this.readFilters(NBT);
		int idx = NBT.getInteger("request");
		currentRequest = idx >= 0 ? requestFilters[idx] : null;
	}

	private void saveFilters(NBTTagCompound NBT) {
		NBTTagCompound fil = new NBTTagCompound();
		for (int i = 0; i < requestFilters.length; i++) {
			Request is = requestFilters[i];
			NBTTagCompound tag = new NBTTagCompound();
			is.writeToNBT(tag);
			fil.setTag("filter_"+i, tag);
		}
		NBT.setTag("filter", fil);
	}

	private void readFilters(NBTTagCompound NBT) {
		NBTTagCompound fil = NBT.getCompoundTag("filter");
		for (int i = 0; i < requestFilters.length; i++) {
			String name = "filter_"+i;
			NBTTagCompound tag = fil.getCompoundTag(name);
			requestFilters[i].readFromNBT(tag);
		}
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		this.saveFilters(NBT);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		if (is.stackTagCompound == null)
			return;
		this.readFilters(is.stackTagCompound);
	}

	public void setFilter(int slot, ItemStack is) {
		requestFilters[slot].setItem(is);
		if (!worldObj.isRemote)
			this.syncAllData(true);
	}

	public ItemStack getFilter(int slot) {
		return requestFilters[slot].item != null ? requestFilters[slot].item.copy() : null;
	}

	private class Request {

		public final int index;
		public final int inventorySlot;

		private ItemStack item;
		private int cooldown;
		private int requestAmount;

		private int currentAmount;

		private Request(int i) {
			inventorySlot = i+12;
			index = i;
		}

		public void readFromNBT(NBTTagCompound tag) {
			cooldown = tag.getInteger("cool");
			requestAmount = tag.getInteger("req");
			currentAmount = tag.getInteger("amt");
			if (tag.hasKey("item")) {
				NBTTagCompound val = tag.getCompoundTag("item");
				item = ItemStack.loadItemStackFromNBT(val);
			}
			else {
				item = null;
			}
		}

		public void writeToNBT(NBTTagCompound tag) {
			tag.setInteger("cool", cooldown);
			tag.setInteger("req", requestAmount);
			tag.setInteger("amt", currentAmount);
			if (item != null) {
				NBTTagCompound val = new NBTTagCompound();
				item.writeToNBT(val);
				tag.setTag("item", val);
			}
		}

		private void setItem(ItemStack is) {
			item = is != null ? is.copy() : null;
			requestAmount = is != null ? Math.min(is.getMaxStackSize(), TileEntityNetworkItemTransporter.this.getInventoryStackLimit()) : 0;
		}

	}

}

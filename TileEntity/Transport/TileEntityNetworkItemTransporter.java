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

	private ItemStack[] requestFilters = new ItemStack[12];

	private ItemStack currentRequest;
	private ItemStack lastRequest;

	private EntityItem entity;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote) {
			int slotToSend = this.getNextSlotToRequest();
			if (slotToSend >= 0) {
				this.tryFindItem(world, x, y, z, slotToSend);
			}
			else {
				currentRequest = null;
			}
			if (!ReikaItemHelper.matchStacks(currentRequest, lastRequest)) {
				CrystalNetworker.instance.breakPaths(this);
				this.syncAllData(true);
			}
			lastRequest = currentRequest;
		}
		else {
			if (!ReikaItemHelper.matchStacks(currentRequest, lastRequest)) {
				entity = currentRequest != null ? new InertItem(world, ReikaItemHelper.getSizedItemStack(currentRequest, 1)) : null;
			}
			lastRequest = currentRequest;
		}
	}

	private void tryFindItem(World world, int x, int y, int z, int slot) {
		int num = Math.min(this.getInventoryStackLimit(), requestFilters[slot-12].getMaxStackSize());
		if (inv[slot] != null)
			num -= inv[slot].stackSize;
		currentRequest = ReikaItemHelper.getSizedItemStack(requestFilters[slot-12], num);
		Set<CrystalElement> c = this.getRequiredColorsForItem(currentRequest);
		for (CrystalElement e : c) {
			if (!CrystalNetworker.instance.checkConnectivity(e, this)) {
				currentRequest = null;
				return;
			}
		}
		int amt = currentRequest.stackSize*1000;
		for (CrystalElement e : c)
			CrystalNetworker.instance.makeRequest(this, e, amt, world, this.getReceiveRange(), amt/4, SourceValidityRule.ALWAYS);
	}

	private int[] getSlotsToRequest() {
		for (int i = 0; i < requestFilters.length; i++) {
			if (requestFilters[i] != null) {
				int slot = i+12;
				if (inv[slot] == null || inv[slot].stackSize < inv[slot].getMaxStackSize()) {
					return slot;
				}
			}
		}
		return -1;
	}

	private int getSlotToSendTo(TileEntityNetworkItemTransporter requester) {
		ItemStack look = requester.currentRequest;
		for (int i = 0; i < 12; i++) {
			if (ReikaItemHelper.matchStacks(look, inv[i])) {
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
				int added = ReikaInventoryHelper.addStackAndReturnCount(is2, this, 12, inv.length-1);
				//ReikaJavaLibrary.pConsole("Added "+added+" to "+this+" at "+worldObj.getTotalWorldTime()+", inv: "+Arrays.toString(inv));
				if (added > 0) {
					ReikaInventoryHelper.decrStack(slot, sender.inv, added);
					//ReikaJavaLibrary.pConsole("Removed "+added+" from "+sender+" at "+worldObj.getTotalWorldTime()+", inv: "+Arrays.toString(sender.inv));
					currentRequest.stackSize -= added;
					if (currentRequest.stackSize <= 0) {
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

		if (currentRequest != null) {
			NBTTagCompound req = new NBTTagCompound();
			currentRequest.writeToNBT(req);
			NBT.setTag("request", req);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		this.readFilters(NBT);
		if (NBT.hasKey("request"))
			currentRequest = ItemStack.loadItemStackFromNBT(NBT.getCompoundTag("request"));
		else
			currentRequest = null;
	}

	private void saveFilters(NBTTagCompound NBT) {
		NBTTagCompound fil = new NBTTagCompound();
		for (int i = 0; i < requestFilters.length; i++) {
			ItemStack is = requestFilters[i];
			if (is != null) {
				NBTTagCompound tag = new NBTTagCompound();
				is.writeToNBT(tag);
				fil.setTag("filter_"+i, tag);
			}
		}
		NBT.setTag("filter", fil);
	}

	private void readFilters(NBTTagCompound NBT) {
		requestFilters = new ItemStack[requestFilters.length];
		NBTTagCompound fil = NBT.getCompoundTag("filter");
		for (int i = 0; i < requestFilters.length; i++) {
			String name = "filter_"+i;
			if (fil.hasKey(name)) {
				NBTTagCompound tag = fil.getCompoundTag(name);
				ItemStack is = ItemStack.loadItemStackFromNBT(tag);
				requestFilters[i] = is;
			}
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
		requestFilters[slot] = is;
		this.syncAllData(true);
	}

	public ItemStack getFilter(int slot) {
		return requestFilters[slot] != null ? requestFilters[slot].copy() : null;
	}

}

package Reika.ChromatiCraft.TileEntity.Transport;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.TileEntity.InventoriedCrystalTransmitter;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Network.SourceValidityRule;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class TileEntityNetworkItemTransporter extends InventoriedCrystalTransmitter implements CrystalSource, CrystalReceiver {

	private final ItemStack[] requestFilters = new ItemStack[12];

	private ItemStack currentRequest;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote) {
			if (z == 396)
				requestFilters[0] = new ItemStack(Items.redstone);
			int slotToSend = this.getNextSlotToRequest();
			if (slotToSend >= 0) {
				this.tryFindItem(world, x, y, z, slotToSend);
			}
		}
	}

	private void tryFindItem(World world, int x, int y, int z, int slot) {
		int num = Math.min(this.getInventoryStackLimit(), requestFilters[slot-12].getMaxStackSize());
		if (inv[slot] != null)
			num -= inv[slot].stackSize;
		currentRequest = ReikaItemHelper.getSizedItemStack(requestFilters[slot-12], num);
		int amt = currentRequest.stackSize*1000;
		CrystalNetworker.instance.makeRequest(this, CrystalElement.LIME, amt, world, this.getReceiveRange(), amt/4, SourceValidityRule.ALWAYS);
	}

	private int getNextSlotToRequest() {
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
		return this.isValidReceiver(r) || r instanceof CrystalRepeater;
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
		return e == CrystalElement.LIME;
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
		return te instanceof TileEntityNetworkItemTransporter;
	}

	public boolean canBeSuppliedBy(CrystalSource te, CrystalElement e) {
		return te instanceof TileEntityNetworkItemTransporter;
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
		return e == CrystalElement.LIME ? 1000*this.getInventoryStackLimit() : 0;
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
				if (added > 0) {
					ReikaInventoryHelper.decrStack(slot, sender.inv, added);
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
		return currentRequest != null && (r instanceof TileEntityNetworkItemTransporter || r instanceof CrystalRepeater);
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

}

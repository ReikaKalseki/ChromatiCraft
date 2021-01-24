package Reika.ChromatiCraft.TileEntity.Transport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityNetworkItemTransporter extends InventoriedCrystalTransmitter implements CrystalSource, CrystalReceiver, NBTTile, BreakAction {

	private static final int EXCLUSION_RANGE = 64;

	private Request[] requestFilters = new Request[12];

	private Request currentRequest;
	private Request lastRequest;
	private int lastItemInput = 3000;

	private HashSet<Coordinate> nearby = new HashSet();
	private boolean isBlocked;

	private EntityItem entity;

	@SideOnly(Side.CLIENT)
	public double itemRotation;
	@SideOnly(Side.CLIENT)
	public double itemOffset;

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
				requestFilters[i].tick();
			}
			lastItemInput++;

			isBlocked = this.isBlocked(world);
			if (isBlocked) {

			}
			else {
				if ((currentRequest == null && lastItemInput >= 200) || (currentRequest != null && currentRequest.ticksSinceReceived > 5 && currentRequest.currentAmount > 0)) {
					Request req = currentRequest != null ? currentRequest : this.getNextSlotToRequest();
					//ReikaJavaLibrary.pConsole("Request slot is "+req, Side.SERVER, req != null);
					if (req != null) {
						boolean success = this.tryFindItem(world, x, y, z, req);
						req.cooldown = success ? 3000*0 : 200;
					}
					if (currentRequest != lastRequest) {
						if (currentRequest == null)
							CrystalNetworker.instance.breakPaths(this);
						this.syncAllData(true);
					}
				}
				else {
					//ReikaJavaLibrary.pConsole("Request still active");
				}
			}
			lastRequest = currentRequest;
		}
		else {
			if (currentRequest != lastRequest) {
				entity = currentRequest != null ? new InertItem(world, ReikaItemHelper.getSizedItemStack(currentRequest.item, 1)) : null;
			}
			lastRequest = currentRequest;
			this.doFX(world, x, y, z);
			itemOffset = Math.pow(Math.max(0, 1+Math.sin(this.getTicksExisted()*0.1)), 4)*0.03125;
			itemRotation += 9*Math.max(0, (0.625-itemOffset)/0.5);
		}
	}

	private boolean isBlocked(World world) {
		for (Coordinate c : nearby) {
			TileEntity te = c.getTileEntity(world);
			if (te instanceof TileEntityNetworkItemTransporter) {
				TileEntityNetworkItemTransporter net = (TileEntityNetworkItemTransporter)te;
				if (this.sharesItemRequest(net))
					return true;
			}
		}
		return false;
	}

	private boolean sharesItemRequest(TileEntityNetworkItemTransporter net) {
		HashSet<KeyedItemStack> set = new HashSet();
		for (int i = 0; i < requestFilters.length; i++) {
			if (requestFilters[i].item != null) {
				set.add(new KeyedItemStack(requestFilters[i].item).setIgnoreNBT(true).setSized(false).setSimpleHash(true));
			}
		}
		if (set.isEmpty())
			return false;
		for (int i = 0; i < net.requestFilters.length; i++) {
			if (net.requestFilters[i].item != null) {
				KeyedItemStack ks = new KeyedItemStack(net.		requestFilters[i].item).setIgnoreNBT(true).setSized(false).setSimpleHash(true);
				if (set.contains(ks))
					return true;
			}
		}
		return false;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);

		if (!world.isRemote) {
			Coordinate here = new Coordinate(this);
			Collection<TileEntityNetworkItemTransporter> c = CrystalNetworker.instance.getNearTilesOfType(this, TileEntityNetworkItemTransporter.class, EXCLUSION_RANGE);
			for (TileEntityNetworkItemTransporter te : c) {
				if (te == this)
					continue;
				nearby.add(new Coordinate(te));
				te.nearby.add(here);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void doFX(World world, int x, int y, int z) {
		double a = (this.getTicksExisted()*12)%360;
		double r = 0.125;
		double dh = (this.getTicksExisted()%20)/100D;
		ArrayList<CrystalElement> e = currentRequest != null ? new ArrayList(this.getRequiredColorsForItem(currentRequest.item)) : null;
		for (int i = 0; i < 3; i++) {
			double h0 = i*0.125;
			double h = h0+dh;
			double ang = Math.toRadians(a-i*180);
			double px = x+0.5+r*Math.cos(ang);
			double py = y+0.25+h0;
			double pz = z+0.5+r*Math.sin(ang);
			EntityCCBlurFX fx = new EntityCCBlurFX(world, px, py, pz);
			int c = e == null || e.isEmpty() ? 0x22aaff : e.get(i%e.size()).getColor();
			fx.setScale(0.6F).setColor(c).setLife(30).setAlphaFading();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		if (isBlocked) {
			double px = rand.nextBoolean() ? 0.3125 : 0.6875;
			double pz = rand.nextBoolean() ? 0.3125 : 0.6875;
			double py = 0.875;
			EntityCCBlurFX fx = new EntityCCBlurFX(world, x+px, y+py, z+pz);
			float s = (float)ReikaRandomHelper.getRandomBetween(0.8, 1.2);
			float g = -(float)ReikaRandomHelper.getRandomBetween(0.03125, 0.0625);
			int l = ReikaRandomHelper.getRandomBetween(90, 150);
			fx.setIcon(ChromaIcons.FADE_CLOUD).setScale(s).setColor(0xff0000).setLife(l).setGravity(g).setRapidExpand().setAlphaFading();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
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
			//ReikaJavaLibrary.pConsole("Current seek request set to "+slot, Side.SERVER);
			Set<CrystalElement> c = this.getRequiredColorsForItem(currentRequest.item);
			for (CrystalElement e : c) {
				if (!CrystalNetworker.instance.checkConnectivity(e, this)) {
					currentRequest = null;
					//ReikaJavaLibrary.pConsole("Request nulled, connectivity failed "+e, Side.SERVER);
					return false;
				}
			}
			int amt = currentRequest.currentAmount*1000;
			for (CrystalElement e : c)
				if (!CrystalNetworker.instance.makeRequest(this, e, amt, world, this.getReceiveRange(), amt/4, SourceValidityRule.ALWAYS))
					return false;
			//ReikaJavaLibrary.pConsole("Request for "+currentRequest+" success", Side.SERVER);
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
		return 4000; //max 4 items/tick per node
	}

	@Override
	public boolean canConduct() {
		return !isBlocked;
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
		return new ImmutableTriple(0D, 0.1875, 0D);
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
				int added = ReikaInventoryHelper.addStackAndReturnCount(is2.copy(), this, currentRequest.inventorySlot, currentRequest.inventorySlot);
				//ReikaJavaLibrary.pConsole("Attempting to satisfy "+currentRequest+" with "+is2+" = "+added, Side.SERVER);
				//ReikaJavaLibrary.pConsole("Added "+added+" to "+this+" at "+worldObj.getTotalWorldTime()+", inv: "+Arrays.toString(inv));
				if (added > 0) {
					ReikaInventoryHelper.decrStack(slot, sender.inv, added);
					//ReikaJavaLibrary.pConsole("Removed "+added+" from "+sender+" at "+worldObj.getTotalWorldTime()+", inv: "+Arrays.toString(sender.inv));
					currentRequest.currentAmount -= added;
					currentRequest.ticksSinceReceived = 0;
					lastItemInput = 0;
					if (currentRequest.currentAmount <= 0) {
						currentRequest.cooldown = 600;
						currentRequest = null;
					}
					//ReikaJavaLibrary.pConsole("Request set to "+currentRequest+" because of add of "+added, Side.SERVER);
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

		isBlocked = NBT.getBoolean("blocked");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("blocked", isBlocked);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		this.saveFilters(NBT);

		NBT.setInteger("request", currentRequest != null ? currentRequest.index : -1);
		NBT.setInteger("lastin", lastItemInput);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		this.readFilters(NBT);
		int idx = NBT.getInteger("request");
		currentRequest = idx >= 0 ? requestFilters[idx] : null;
		//ReikaJavaLibrary.pConsole("Request "+currentRequest+" set via tile NBT", Side.SERVER);

		lastItemInput = NBT.getInteger("lastin");
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

	@Override
	public void breakBlock() {
		Coordinate here = new Coordinate(this);
		for (Coordinate c : nearby) {
			TileEntity te = c.getTileEntity(worldObj);
			if (te instanceof TileEntityNetworkItemTransporter) {
				((TileEntityNetworkItemTransporter)te).nearby.remove(here);
			}
		}
		nearby.clear();
	}

	private class Request {

		public final int index;
		public final int inventorySlot;

		private ItemStack item;
		private int cooldown;
		private int ticksSinceReceived;
		private int requestAmount;

		private int currentAmount;

		private Request(int i) {
			inventorySlot = i+12;
			index = i;
		}

		private void tick() {
			if (cooldown > 0) {
				cooldown--;
			}
			ticksSinceReceived++;
		}

		public void readFromNBT(NBTTagCompound tag) {
			cooldown = tag.getInteger("cool");
			requestAmount = tag.getInteger("req");
			currentAmount = tag.getInteger("amt");
			ticksSinceReceived = tag.getInteger("lastrec");
			if (tag.hasKey("item")) {
				NBTTagCompound val = tag.getCompoundTag("item");
				//ReikaJavaLibrary.pConsole(this+" item set to "+item+" from NBT", Side.SERVER);
				item = ItemStack.loadItemStackFromNBT(val);
			}
			else {
				//ReikaJavaLibrary.pConsole(this+" item nulled from NBT", Side.SERVER);
				item = null;
			}
		}

		public void writeToNBT(NBTTagCompound tag) {
			tag.setInteger("cool", cooldown);
			tag.setInteger("req", requestAmount);
			tag.setInteger("amt", currentAmount);
			tag.setInteger("lastrec", ticksSinceReceived);
			if (item != null) {
				NBTTagCompound val = new NBTTagCompound();
				item.writeToNBT(val);
				tag.setTag("item", val);
			}
		}

		private void setItem(ItemStack is) {
			//ReikaJavaLibrary.pConsole(this+" item set to "+is+" direct", Side.SERVER);
			item = is != null ? is.copy() : null;
			requestAmount = is != null ? Math.min(is.getMaxStackSize(), TileEntityNetworkItemTransporter.this.getInventoryStackLimit()) : 0;
		}

		@Override
		public String toString() {
			return index+"/"+inventorySlot+" = "+item+" #"+currentAmount+"/"+requestAmount+" @ "+cooldown+"/"+ticksSinceReceived;
		}

	}

}

package Reika.ChromatiCraft.Base.TileEntity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Maps.CountMap;
import Reika.DragonAPI.Instantiable.ModInteract.BasicAEInterface;
import Reika.DragonAPI.Instantiable.ModInteract.MEWorkTracker;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Interfaces.TileEntity.ConditionBreakDropsInventory;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.KeyedItemStackConverter;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader;

import appeng.api.AEApi;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.storage.ICellProvider;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;


@Strippable(value={"appeng.api.networking.security.IActionHost", "appeng.api.storage.ICellContainer", "appeng.api.storage.IMEInventoryHandler"})
public abstract class TileEntityMassStorage extends TileEntityChromaticBase implements IInventory, IActionHost/*, ICellContainer, IMEInventoryHandler*/, BreakAction, ConditionBreakDropsInventory {

	private static final HashMap<UUID, ArrayList<ItemStack>> itemData = new HashMap();

	private final CountMap<KeyedItemStack> types = new CountMap();
	private ItemStack pendingInput;
	private UUID identifier = UUID.randomUUID();

	@ModDependent(ModList.APPENG)
	private MESystemReader network;
	private Object aeGridBlock;
	private Object aeGridNode;
	private MEWorkTracker hasWork = new MEWorkTracker();
	private final ArrayList<ItemStack> MEStacks = new ArrayList();
	private final StepTimer updateTimer = new StepTimer(200);

	private static Class storageBusClass;
	private static Method storageBusSide;
	private static Method storageBusHost;

	static {
		if (ModList.APPENG.isLoaded()) {
			try {
				storageBusClass = Class.forName("appeng.parts.misc.PartStorageBus");
				storageBusSide = storageBusClass.getMethod("getSide");
				storageBusHost = storageBusClass.getMethod("getHost");
			}
			catch (Exception e) {
				e.printStackTrace();
				ChromatiCraft.logger.logError("Could not load storage bus methods!");
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.APPENG, e);
			}
		}
	}

	protected TileEntityMassStorage() {
		if (ModList.APPENG.isLoaded()) {
			aeGridBlock = new BasicAEInterface(this, this.getTile().getCraftedProduct());
			aeGridNode = FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? AEApi.instance().createGridNode((IGridBlock)aeGridBlock) : null;

			//for (int i = 0; i < lock.length; i++) {
			//	lock[i] = new CraftingLock();
			//}
		}
	}

	private static void loadItemData(NBTTagCompound data) {
		//ChromatiCraft.logger.log("Loading tool crate data from disk: "+data);
		itemData.clear();
		NBTTagList list = data.getTagList("data", NBTTypes.COMPOUND.ID);
		for (Object o : list.tagList) {
			NBTTagCompound NBT = (NBTTagCompound)o;
			NBTTagList items = NBT.getTagList("Items", NBTTypes.COMPOUND.ID);
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < items.tagCount(); i++) {
				NBTTagCompound tag = items.getCompoundTagAt(i);
				li.add(ItemStack.loadItemStackFromNBT(tag));
			}
			UUID key = UUID.fromString(NBT.getString("id"));
			itemData.put(key, li);
		}
	}

	private static void saveItemData(NBTTagCompound data) {
		NBTTagList list = new NBTTagList();
		for (Entry<UUID, ArrayList<ItemStack>> e : itemData.entrySet()) {
			NBTTagCompound NBT = new NBTTagCompound();
			UUID key = e.getKey();
			ArrayList<ItemStack> li = e.getValue();
			NBTTagList nbttaglist = new NBTTagList();
			for (ItemStack is : li) {
				NBTTagCompound tag = new NBTTagCompound();
				is.writeToNBT(tag);
				nbttaglist.appendTag(tag);
			}
			NBT.setTag("Items", nbttaglist);
			NBT.setString("id", key.toString());
			list.appendTag(NBT);
		}
		data.setTag("data", list);
	}

	protected final ArrayList<ItemStack> getItems() {
		return this.getOrCreateItemList();
	}

	private ArrayList<ItemStack> getOrCreateItemList() {
		ArrayList<ItemStack> ret = itemData.get(identifier);
		if (ret == null) {
			ret = new ArrayList();
			itemData.put(identifier, ret);
		}
		return ret;
	}

	@Override
	public final int getSizeInventory() {
		return this.getItems().size()+1;
	}

	@Override
	public final ItemStack getStackInSlot(int slot) {
		ArrayList<ItemStack> li = this.getItems();
		if (slot > li.size()) {
			ChromatiCraft.logger.logError("Something tried pulling from an off-list slot #"+slot+"!");
			Thread.dumpStack();
			return null;
		}
		return slot == 0 ? pendingInput : li.get(slot-1);
	}

	public final ItemStack decrStackSize(int par1, int par2) {
		return ReikaInventoryHelper.decrStackSize(this, par1, par2);
	}

	public final ItemStack getStackInSlotOnClosing(int par1) {
		return ReikaInventoryHelper.getStackInSlotOnClosing(this, par1);
	}

	@Override
	public final void setInventorySlotContents(int slot, ItemStack is) {
		if (slot == 0) {
			pendingInput = is;
		}
		else if (is == null) {
			this.removeItem(slot-1);
		}
		else {
			throw new IllegalArgumentException("Something tried to insert into an invalid slot!");
		}
	}

	@Override
	public final String getInventoryName() {
		return this.getTEName();
	}

	@Override
	public final boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public final int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public final boolean isUseableByPlayer(EntityPlayer ep) {
		return this.isPlayerAccessible(ep);
	}

	@Override
	public final void openInventory() {}

	@Override
	public final void closeInventory() {}

	@Override
	public final boolean isItemValidForSlot(int slot, ItemStack is) {
		return slot == 0 && (pendingInput == null || ReikaItemHelper.areStacksCombinable(pendingInput, is, is.getMaxStackSize())) && this.isItemValid(is) && this.getItems().size() < this.maxItemCount();
	}

	public abstract InertItem getFilterItemRender();

	public abstract boolean isItemValid(ItemStack is);
	public abstract int maxItemCount();

	protected final ItemStack getPendingInput() {
		return pendingInput;
	}

	@Override
	public final void updateEntity(World world, int x, int y, int z, int meta) {
		if (pendingInput != null && !world.isRemote) {
			//ReikaJavaLibrary.pConsole(pendingInput+" > "+pendingInput.getDisplayName());
			this.addItem(pendingInput);
			pendingInput = null;
		}

		this.onTick(world, x, y, z);

		updateTimer.update();
		if (updateTimer.checkCap() && !world.isRemote) {
			this.buildCache();
		}

		if (ModList.APPENG.isLoaded()) {
			if (network != null)
				network.tick();
			if (aeGridBlock != null && !world.isRemote) {
				((BasicAEInterface)aeGridBlock).setPowerCost(1);
			}

			//ReikaJavaLibrary.pConsole(MEStacks, Side.SERVER);
			if (!world.isRemote && network != null && !MEStacks.isEmpty() && pendingInput == null) {
				hasWork.tick();
				if (hasWork.hasWork()) {
					//ReikaJavaLibrary.pConsole("Executing tick");
					if (ModList.APPENG.isLoaded() && network != null && !network.isEmpty)
						hasWork.reset();
					this.injectItems();
				}
			}
		}
	}

	protected void onTick(World world, int x, int y, int z) {

	}

	@Override
	protected final void onFirstTick(World world, int x, int y, int z) {
		WorldToolCrateData.initItemData(world).setDirty(true);
		this.initTypeData();
	}

	private void initTypeData() {
		types.clear();
		for (ItemStack is : this.getItems()) {
			KeyedItemStack ks = this.key(is);
			types.increment(ks, is.stackSize);
			this.onAddItem(is);
		}
	}

	private void injectItems() {
		//ReikaJavaLibrary.pConsole("Injecting");
		int idx = rand.nextInt(MEStacks.size());
		ItemStack is = MEStacks.get(idx);
		ItemStack copy = is.copy();
		is = ReikaItemHelper.getSizedItemStack(is, is.getMaxStackSize());
		int ret = (int)network.removeItem(is, false, true);
		if (ret > 0) {
			pendingInput = ReikaItemHelper.getSizedItemStack(is, ret);
			if (ret >= copy.stackSize)
				MEStacks.remove(idx);
			else
				MEStacks.get(idx).stackSize -= ret;
		}
		//ReikaJavaLibrary.pConsole(MEStacks+" after removing "+copy);
	}

	protected final void resetWorkTimer() {
		MEStacks.clear();
		updateTimer.setTick(updateTimer.getCap()+2);
	}

	private void buildCache() {
		if (ModList.APPENG.isLoaded()) {
			Object oldNode = aeGridNode;
			if (aeGridNode == null) {
				aeGridNode = FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? AEApi.instance().createGridNode((IGridBlock)aeGridBlock) : null;
			}
			if (aeGridNode != null)
				((IGridNode)aeGridNode).updateState();

			if (oldNode != aeGridNode || network == null) {
				if (aeGridNode == null)
					network = null;
				else if (network == null)
					network = new MESystemReader((IGridNode)aeGridNode, this);
				else
					network = new MESystemReader((IGridNode)aeGridNode, network);
			}

			if (network != null && this.canPullFromME()) {
				network.clearCallbacks();
				network.addGlobalCallback(hasWork);

				//ReikaJavaLibrary.pConsole("Rebuilding");
				MEStacks.clear();
				Collection<ItemStack> li = new ArrayList();//network.getRawMESystemContents();

				IStorageGrid isg = (IStorageGrid)this.getActionableNode().getGrid().getCache(IStorageGrid.class);
				HashSet<ICellProvider> set = MESystemReader.getAllCellContainers(isg);
				if (set != null) {
					for (ICellProvider icp : set) {
						if (this.isStorageBusToSelf(icp))
							continue;
						List<IMEInventoryHandler> invs = icp.getCellArray(StorageChannel.ITEMS);
						for (IMEInventoryHandler inv : invs) {
							IItemList<IAEItemStack> items = inv.getAvailableItems(StorageChannel.ITEMS.createList());
							for (IAEItemStack iae : items) {
								if (iae.isItem() && iae.isMeaningful()) {
									ItemStack is = iae.getItemStack();
									if (this.isItemValid(is)) {
										MEStacks.add(is);
									}
								}
							}
						}
					}
				}
			}
			//network.setRequester(this);
		}
	}

	protected boolean canPullFromME() {
		return true;
	}

	private final boolean isStorageBusToSelf(ICellProvider icp) {
		try {
			if (icp.getClass() == storageBusClass) {
				IPart part = (IPart)icp;
				IPartHost block = (IPartHost)storageBusHost.invoke(part);
				ForgeDirection dir = (ForgeDirection)storageBusSide.invoke(icp);
				DimensionalCoord loc = block.getLocation();
				TileEntity tile = block.getTile().worldObj.getTileEntity(loc.x+dir.offsetX, loc.y+dir.offsetY, loc.z+dir.offsetZ);
				return tile == this;
			}
			return false;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected final void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {
		super.onInvalidateOrUnload(world, x, y, z, invalid);
		if (ModList.APPENG.isLoaded() && aeGridNode != null)
			((IGridNode)aeGridNode).destroy();
	}

	private void addItem(ItemStack is) {
		if (worldObj.isRemote)
			return;
		this.getItems().add(is);
		if (worldObj != null)
			WorldToolCrateData.initItemData(worldObj).setDirty(true);
		KeyedItemStack ks = this.key(is);
		types.increment(ks, is.stackSize);
		this.onAddItem(is);
		if (worldObj != null)
			this.syncAllData(false);
	}

	protected void onAddItem(ItemStack is) {

	}

	protected void onRemoveItem(ItemStack is) {

	}

	private void removeItem(int slot) {
		if (worldObj.isRemote)
			return;
		ItemStack is = this.getItems().remove(slot);
		if (is == null)
			return;
		if (worldObj != null)
			WorldToolCrateData.initItemData(worldObj).setDirty(true);
		KeyedItemStack ks = this.key(is);
		types.subtract(ks, is.stackSize);
		this.onRemoveItem(is);
		if (worldObj != null)
			this.syncAllData(false);
	}

	public final boolean isEmpty() {
		return this.getItems().isEmpty();
	}

	protected abstract KeyedItemStack key(ItemStack is);

	public final Map<KeyedItemStack, Integer> getItemTypes() {
		return types.view();
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		if (pendingInput != null) {
			NBTTagCompound tag = new NBTTagCompound();
			pendingInput.writeToNBT(tag);
			NBT.setTag("pending", tag);
		}

		NBT.setString("boxid", identifier.toString());
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		if (NBT.hasKey("boxid")) {
			identifier = UUID.fromString(NBT.getString("boxid"));
		}

		if (NBT.hasKey("Items")) {
			NBTTagList li = NBT.getTagList("Items", NBTTypes.COMPOUND.ID);
			//ChromatiCraft.logger.log("Loading legacy tool crate data from NBT: "+li);
			this.getItems().clear();
			types.clear();

			for (int i = 0; i < li.tagCount(); i++) {
				NBTTagCompound tag = li.getCompoundTagAt(i);
				this.addItem(ItemStack.loadItemStackFromNBT(tag));
			}
		}

		if (NBT.hasKey("pending")) {
			NBTTagCompound tag = NBT.getCompoundTag("pending");
			pendingInput = ItemStack.loadItemStackFromNBT(tag);
		}
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBTTagCompound tag = new NBTTagCompound();
		types.writeToNBT(tag, KeyedItemStackConverter.instance);
		NBT.setTag("types", tag);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		if (NBT.hasKey("types")) {
			types.readFromNBT(NBT.getCompoundTag("types"), KeyedItemStackConverter.instance);
		}
	}

	@Override
	@ModDependent(ModList.APPENG)
	public final IGridNode getGridNode(ForgeDirection dir) {
		return (IGridNode)aeGridNode;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public final IGridNode getActionableNode() {
		return (IGridNode)aeGridNode;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public final AECableType getCableConnectionType(ForgeDirection dir) {
		return AECableType.COVERED;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public final void securityBreak() {

	}
	/*
	@Override
	@ModDependent(ModList.APPENG)
	public final void blinkCell(int slot) {
		TO DO maybe a packet to spawn some particles or something
	}

	@Override
	@ModDependent(ModList.APPENG)
	public final List<IMEInventoryHandler> getCellArray(StorageChannel channel) {
		return Arrays.asList(this);
	}

	@Override
	@ModDependent(ModList.APPENG)
	public final int getPriority() {
		return 60000;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public final void saveChanges(IMEInventory cellInventory) {

	}

	@Override
	public final AccessRestriction getAccess() {
		return AccessRestriction.READ_WRITE;
	}

	@Override
	public boolean isPrioritized(IAEStack input) {
		return this.canAccept(input);
	}

	@Override
	@ModDependent(ModList.APPENG)
	public final boolean canAccept(IAEStack input) {
		return input.isMeaningful() && input.isItem() && this.isItemValid(((IAEItemStack)input).getItemStack());
	}

	@Override
	@ModDependent(ModList.APPENG)
	public final int getSlot() {
		return 0;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public boolean validForPass(int i) {
		return true;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public final IAEStack injectItems(IAEStack input, Actionable type, BaseActionSource src) { FIX ME this disobeys maxItemCount
		if (!input.isItem())
			return input;
		ItemStack ref = ((IAEItemStack)input).getItemStack();
		if (ref == null || ref.getItem() == null)
			return input;
		if (type == Actionable.SIMULATE)
			return null;
		long amt = input.getStackSize();
		long added = 0;
		while (amt > 0) {
			ItemStack put = ReikaItemHelper.getSizedItemStack(ref, Math.min((int)Math.min(Integer.MAX_VALUE, amt), ref.getMaxStackSize()));
			this.addItem(put);
			amt -= put.stackSize;
			added += put.stackSize;
		}
		return null; //add can never fail
	}

	@Override
	@ModDependent(ModList.APPENG)
	public final IAEStack extractItems(IAEStack request, Actionable mode, BaseActionSource src) {
		if (!request.isItem())
			return null;
		ItemStack seek = ((IAEItemStack)request).getItemStack();
		int amt = this.countExactItem(seek);
		if (amt == 0)
			return null;
		long get = Math.min(request.getStackSize(), amt);
		if (mode == Actionable.MODULATE) {
			long take = get;
			Iterator<ItemStack> it = this.getItems().iterator();
			while (it.hasNext()) {
				ItemStack at = it.next();
				if (ReikaItemHelper.matchStacks(at, seek) && ItemStack.areItemStackTagsEqual(seek, at)) {
					if (take >= at.stackSize) {
						take -= at.stackSize;
						it.remove();
					}
					else {
						take = 0;
						at.stackSize -= take;
					}
					if (take <= 0)
						break;
				}
			}
		}
		IAEItemStack ret = (IAEItemStack)request.copy();
		ret.setStackSize(get);
		return ret;
	}

	private int countExactItem(ItemStack seek) {
		int amt = 0;
		for (ItemStack is : this.getItems()) {
			if (ReikaItemHelper.matchStacks(is, seek) && ItemStack.areItemStackTagsEqual(is, seek)) {
				amt += is.stackSize;
			}
		}
		return amt;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public final IItemList getAvailableItems(IItemList out) {
		IItemList li = AEApi.instance().storage().createItemList();
		for (ItemStack is : this.getItems()) {
			li.add(MESystemReader.createAEStack(is));
		}
		return li;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public final StorageChannel getChannel() {
		return StorageChannel.ITEMS;
	}
	 */
	@Override
	public final void breakBlock() {
		ReikaItemHelper.dropItems(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, this.getItems());
		itemData.remove(identifier);
	}

	public final boolean dropsInventoryOnBroken() {
		return false;
	}

	public final ItemStack removeLastItem() {
		ArrayList<ItemStack> li = this.getItems();
		if (li.isEmpty())
			return null;
		ItemStack is = li.get(li.size()-1);
		this.removeItem(li.size()-1);
		return is;
	}

	public static class WorldToolCrateData extends WorldSavedData {

		private static final String IDENTIFIER = "ToolCrateItems";

		public WorldToolCrateData() {
			super(IDENTIFIER);
		}

		public WorldToolCrateData(String s) {
			super(s);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			loadItemData(NBT);
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			saveItemData(NBT);
		}

		private static WorldToolCrateData initItemData(World world) {
			WorldToolCrateData data = (WorldToolCrateData)world.loadItemData(WorldToolCrateData.class, IDENTIFIER);
			if (data == null) {
				data = new WorldToolCrateData();
				world.setItemData(IDENTIFIER, data);
			}
			return data;
		}
	}

}

package Reika.ChromatiCraft.TileEntity.Storage;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.CountMap;
import Reika.DragonAPI.Instantiable.ModInteract.BasicAEInterface;
import Reika.DragonAPI.Instantiable.ModInteract.MEWorkTracker;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.KeyedItemStackConverter;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader;
import Reika.DragonAPI.ModInteract.ItemHandlers.AppEngHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler;
import Reika.DragonAPI.ModRegistry.InterfaceCache;

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
import cpw.mods.fml.relauncher.SideOnly;


@Strippable(value={"appeng.api.networking.security.IActionHost"})
public class TileEntityToolStorage extends TileEntityChromaticBase implements IInventory, IActionHost, BreakAction {

	private static final int MAX_COUNT = 360000;

	private static final HashMap<UUID, ArrayList<ItemStack>> itemData = new HashMap();

	private final CountMap<KeyedItemStack> types = new CountMap();
	private ItemStack pendingInput;
	private ToolType filter = ToolType.OTHER;
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

	public TileEntityToolStorage() {
		if (ModList.APPENG.isLoaded()) {
			aeGridBlock = new BasicAEInterface(this, this.getTile().getCraftedProduct());
			aeGridNode = FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? AEApi.instance().createGridNode((IGridBlock)aeGridBlock) : null;

			//for (int i = 0; i < lock.length; i++) {
			//	lock[i] = new CraftingLock();
			//}
		}
	}

	private static void loadItemData(NBTTagCompound data) {
		ChromatiCraft.logger.log("Loading tool crate data from disk: "+data);
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

	private ArrayList<ItemStack> getItems() {
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
	public int getSizeInventory() {
		return this.getItems().size()+1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
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
	public void setInventorySlotContents(int slot, ItemStack is) {
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
	public String getInventoryName() {
		return this.getTEName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer ep) {
		return this.isPlayerAccessible(ep);
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return slot == 0 && filter.isItemValid(is) && this.getItems().size() < MAX_COUNT;
	}

	public boolean stepMode() {
		if (pendingInput == null && this.getItems().isEmpty()) {
			filter = ToolType.list[(filter.ordinal()+1)%ToolType.list.length];
			MEStacks.clear();
			updateTimer.setTick(updateTimer.getCap()+2);
			return true;
		}
		return false;
	}

	public ToolType getFilter() {
		return filter;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.TOOLSTORAGE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (pendingInput != null) {
			//ReikaJavaLibrary.pConsole(pendingInput+" > "+pendingInput.getDisplayName());
			this.addItem(pendingInput);
			pendingInput = null;
		}

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

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		WorldToolCrateData.initItemData(world).setDirty(true);
		this.initTypeData();
		ReikaJavaLibrary.pConsole("types");
	}

	private void initTypeData() {
		types.clear();
		for (ItemStack is : this.getItems()) {
			KeyedItemStack ks = this.key(is);
			types.increment(ks, is.stackSize);
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

			if (network != null) {
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
									if (filter.isItemValid(is)) {
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

	private boolean isStorageBusToSelf(ICellProvider icp) {
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
	protected void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {
		super.onInvalidateOrUnload(world, x, y, z, invalid);
		if (ModList.APPENG.isLoaded() && aeGridNode != null)
			((IGridNode)aeGridNode).destroy();
	}

	private void addItem(ItemStack is) {
		this.getItems().add(is);
		if (worldObj != null)
			WorldToolCrateData.initItemData(worldObj).setDirty(true);
		KeyedItemStack ks = this.key(is);
		types.increment(ks, is.stackSize);
		if (worldObj != null)
			this.syncAllData(false);
	}

	private void removeItem(int slot) {
		ItemStack is = this.getItems().remove(slot);
		if (is == null)
			return;
		if (worldObj != null)
			WorldToolCrateData.initItemData(worldObj).setDirty(true);
		KeyedItemStack ks = this.key(is);
		types.subtract(ks, is.stackSize);
		if (worldObj != null)
			this.syncAllData(false);
	}

	private KeyedItemStack key(ItemStack is) {
		return new KeyedItemStack(is).setIgnoreMetadata(!filter.isDamageImportant()).setIgnoreNBT(!filter.isNBTImportant()).setSized(false).setSimpleHash(true);
	}

	public Map<KeyedItemStack, Integer> getItemTypes() {
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

		NBT.setInteger("mode", filter.ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		if (NBT.hasKey("boxid")) {
			identifier = UUID.fromString(NBT.getString("boxid"));
		}

		if (NBT.hasKey("Items")) {
			NBTTagList li = NBT.getTagList("Items", NBTTypes.COMPOUND.ID);
			ChromatiCraft.logger.log("Loading legacy tool crate data from NBT: "+li);
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

		filter = ToolType.list[NBT.getInteger("mode")];
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
	public IGridNode getGridNode(ForgeDirection dir) {
		return (IGridNode)aeGridNode;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public IGridNode getActionableNode() {
		return (IGridNode)aeGridNode;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public AECableType getCableConnectionType(ForgeDirection dir) {
		return AECableType.COVERED;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public void securityBreak() {

	}

	@Override
	public void breakBlock() {
		itemData.remove(new WorldLocation(this));
	}

	public static enum ToolType {
		PICK(Items.diamond_pickaxe),
		AXE(Items.iron_axe),
		SHOVEL(Items.golden_shovel),
		SWORD(Items.diamond_sword),
		BOW(Items.bow),
		SHEARS(Items.shears),
		HELMET(Items.diamond_helmet),
		CHESTPLATE(Items.diamond_chestplate),
		LEGS(Items.diamond_leggings),
		BOOTS(Items.diamond_boots),
		BOOK(Items.enchanted_book),
		HORSEARMOR(Items.golden_horse_armor),
		POTION(ReikaPotionHelper.getPotionItem(Potion.regeneration, false, false, false)),
		TINKER(ModList.TINKERER.isLoaded() ? TinkerToolHandler.Tools.HAMMER.getToolOfMaterials(1, 1, 1, 1) : (ItemStack)null),
		OTHER((ItemStack)null);

		private static final ToolType[] list = values();

		private final ItemStack icon;
		private InertItem render;

		@SideOnly(Side.CLIENT)
		public InertItem getRenderItem() {
			if (render == null && icon != null) {
				render = new InertItem(Minecraft.getMinecraft().theWorld, icon);
			}
			return render;
		}

		public boolean isDamageImportant() {
			switch(this) {
				case POTION:
				case OTHER:
					return true;
				default:
					return false;
			}
		}

		public boolean isNBTImportant() {
			switch(this) {
				case TINKER:
				case BOOK:
				case OTHER:
					return true;
				default:
					return false;
			}
		}

		private ToolType(Item i) {
			this(new ItemStack(i));
		}

		private ToolType(ItemStack is) {
			icon = is;
		}

		public boolean isItemValid(ItemStack is) {
			return this.getTypeForTool(is) == this;
			/*
			if (this.isTinker(is))
				return this == TINKER;
			int type = -1;
			if (is.getItem() instanceof ItemArmor) {
				type = ((ItemArmor)is.getIt	em()).armorType;
			}
			switch(this) {
				case AXE:
					return is.getItem() instanceof ItemAxe;
				case BOW:
					return is.getItem() instanceof ItemBow;
				case PICK:
					return is.getItem() instanceof ItemPickaxe;
				case SHEARS:
					return is.getItem() instanceof ItemShears;
				case SHOVEL:
					return is.getItem() instanceof ItemSpade;
				case SWORD:
					return is.getItem() instanceof ItemSword;
				case HELMET:
					return type == 0;
				case CHESTPLATE:
					return type == 1;
				case LEGS:
					return type == 2;
				case BOOTS:
					return type == 3;
				case TINKER:
					return this.isTinker(is);
				default:
					break;
			}
			return this == OTHER;*/
		}

		private static ToolType getTypeForTool(ItemStack is) {
			if (isTinker(is))
				return TINKER;
			if (is.getItem() instanceof ItemArmor) {
				int type = ((ItemArmor)is.getItem()).armorType;
				switch(type) {
					case 0:
						return HELMET;
					case 1:
						return CHESTPLATE;
					case 2:
						return LEGS;
					case 3:
						return BOOTS;
				}
			}
			if (is.getItem() instanceof ItemAxe)
				return AXE;
			if (is.getItem() instanceof ItemBow)
				return BOW;
			if (is.getItem() instanceof ItemPickaxe)
				return PICK;
			if (is.getItem() instanceof ItemShears)
				return SHEARS;
			if (is.getItem() instanceof ItemSpade)
				return SHOVEL;
			if (is.getItem() instanceof ItemSword)
				return SWORD;
			if (is.getItem() instanceof ItemEnchantedBook)
				return BOOK;
			if (is.getItem() == Items.iron_horse_armor || is.getItem() == Items.golden_horse_armor || is.getItem() == Items.diamond_horse_armor)
				return HORSEARMOR;
			if (is.getItem() instanceof ItemPotion)
				return POTION;
			if (ModList.BOTANIA.isLoaded() && InterfaceCache.BREWITEM.instanceOf(is.getItem()))
				return POTION;
			return isValidMiscToolItem(is) ? OTHER : null;
		}

		private static boolean isValidMiscToolItem(ItemStack is) {
			if (ModList.APPENG.isLoaded()) {
				Item i = is.getItem();
				if (i == AppEngHandler.getInstance().get1KCell() || i == AppEngHandler.getInstance().get4KCell())
					return false;
				if (i == AppEngHandler.getInstance().get16KCell() || i == AppEngHandler.getInstance().get64KCell())
					return false;
			}
			if (ModList.FORESTRY.isLoaded() && ReikaBeeHelper.isGenedItem(is))
				return false;
			return is.getMaxStackSize() == 1;
		}

		private static boolean isTinker(ItemStack is) {
			return ModList.TINKERER.isLoaded() && (TinkerToolHandler.getInstance().isTool(is) || TinkerToolHandler.getInstance().isWeapon(is));
		}

		public static String getTypesAsString() {
			StringBuilder sb = new StringBuilder();
			for (ToolType type : list) {
				sb.append(type.displayName());
				if (type != ToolType.OTHER)
					sb.append(", ");
			}
			return sb.toString();
		}

		public String displayName() {
			return ReikaStringParser.capFirstChar(this.name());
		}
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

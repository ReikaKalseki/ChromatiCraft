package Reika.ChromatiCraft.TileEntity.Storage;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
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
import Reika.DragonAPI.Instantiable.Data.Maps.CountMap;
import Reika.DragonAPI.Instantiable.ModInteract.BasicAEInterface;
import Reika.DragonAPI.Instantiable.ModInteract.MEWorkTracker;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader;
import Reika.DragonAPI.ModInteract.ItemHandlers.AppEngHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler;

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
public class TileEntityToolStorage extends TileEntityChromaticBase implements IInventory, IActionHost {

	private static final int MAX_COUNT = 360000;

	private final ArrayList<ItemStack> allItems = new ArrayList();
	private final CountMap<KeyedItemStack> types = new CountMap();
	private ItemStack pendingInput;
	private ToolType filter = ToolType.OTHER;

	@ModDependent(ModList.APPENG)
	private MESystemReader network;
	private Object aeGridBlock;
	private Object aeGridNode;
	private MEWorkTracker hasWork = new MEWorkTracker();
	private final ArrayList<ItemStack> MEStacks = new ArrayList();
	private final StepTimer updateTimer = new StepTimer(50);

	private static Class storageBusClass;
	private static Method storageBusSide;
	private static Method storageBusHost;

	static {
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

	public TileEntityToolStorage() {
		if (ModList.APPENG.isLoaded()) {
			aeGridBlock = new BasicAEInterface(this, this.getTile().getCraftedProduct());
			aeGridNode = FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? AEApi.instance().createGridNode((IGridBlock)aeGridBlock) : null;

			//for (int i = 0; i < lock.length; i++) {
			//	lock[i] = new CraftingLock();
			//}
		}
	}

	@Override
	public int getSizeInventory() {
		return allItems.size()+1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot == 0 ? pendingInput : allItems.get(slot-1);
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
			this.removeItem(slot-1, true);
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
		return slot == 0 && filter.isItemValid(is) && allItems.size() < MAX_COUNT;
	}

	public boolean stepMode() {
		if (pendingInput == null && allItems.isEmpty()) {
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
			this.addItem(pendingInput, true);
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

	private void addItem(ItemStack is, boolean doSync) {
		allItems.add(is);
		KeyedItemStack ks = this.key(is);
		types.increment(ks, is.stackSize);
		if (doSync)
			this.syncAllData(true);
	}

	private void removeItem(int slot, boolean doSync) {
		ItemStack is = allItems.remove(slot);
		if (is == null)
			return;
		KeyedItemStack ks = this.key(is);
		types.subtract(ks, is.stackSize);
		if (doSync)
			this.syncAllData(true);
	}

	private KeyedItemStack key(ItemStack is) {
		return new KeyedItemStack(is).setIgnoreMetadata(true).setIgnoreNBT(true).setSized(false).setSimpleHash(true);
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

		NBTTagList nbttaglist = new NBTTagList();

		for (ItemStack is : allItems) {
			NBTTagCompound tag = new NBTTagCompound();
			is.writeToNBT(tag);
			nbttaglist.appendTag(tag);
		}

		NBT.setTag("Items", nbttaglist);

		if (pendingInput != null) {
			NBTTagCompound tag = new NBTTagCompound();
			pendingInput.writeToNBT(tag);
			NBT.setTag("pending", tag);
		}

		NBT.setInteger("mode", filter.ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		NBTTagList li = NBT.getTagList("Items", NBTTypes.COMPOUND.ID);
		allItems.clear();
		types.clear();

		for (int i = 0; i < li.tagCount(); i++) {
			NBTTagCompound tag = li.getCompoundTagAt(i);
			this.addItem(ItemStack.loadItemStackFromNBT(tag), false);
		}

		if (NBT.hasKey("pending")) {
			NBTTagCompound tag = NBT.getCompoundTag("pending");
			pendingInput = ItemStack.loadItemStackFromNBT(tag);
		}

		filter = ToolType.list[NBT.getInteger("mode")];
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
			return is.getMaxStackSize() == 1;
		}

		private static boolean isTinker(ItemStack is) {
			return ModList.TINKERER.isLoaded() && (TinkerToolHandler.getInstance().isTool(is) || TinkerToolHandler.getInstance().isWeapon(is));
		}

		public String displayName() {
			return ReikaStringParser.capFirstChar(this.name());
		}
	}

}

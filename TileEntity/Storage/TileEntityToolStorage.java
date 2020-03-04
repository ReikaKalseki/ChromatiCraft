package Reika.ChromatiCraft.TileEntity.Storage;

import java.util.ArrayList;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
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
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Maps.CountMap;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler;


public class TileEntityToolStorage extends TileEntityChromaticBase implements IInventory {

	private static final int MAX_COUNT = 360000;

	private final ArrayList<ItemStack> allItems = new ArrayList();
	private final CountMap<KeyedItemStack> types = new CountMap();
	private ItemStack pendingInput;
	private ToolType filter = ToolType.OTHER;

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
		return slot == 0 && filter.isItemValid(is);
	}

	public boolean stepMode() {
		if (pendingInput == null && allItems.isEmpty()) {
			filter = ToolType.list[(filter.ordinal()+1)%ToolType.list.length];
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

	public static enum ToolType {
		PICK,
		AXE,
		SHOVEL,
		SWORD,
		BOW,
		SHEARS,
		HELMET,
		CHESTPLATE,
		LEGS,
		BOOTS,
		TINKER,
		OTHER;

		private static final ToolType[] list = values();

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
			return OTHER;
		}

		private static boolean isTinker(ItemStack is) {
			return ModList.TINKERER.isLoaded() && (TinkerToolHandler.getInstance().isTool(is) || TinkerToolHandler.getInstance().isWeapon(is));
		}
	}

}

package Reika.ChromatiCraft.TileEntity.Storage;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
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
import net.minecraft.potion.Potion;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityMassStorage;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.AppEngHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler;
import Reika.DragonAPI.ModRegistry.InterfaceCache;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityToolStorage extends TileEntityMassStorage {

	private static final int MAX_COUNT = 360000;

	private ToolType filter = ToolType.OTHER;

	public boolean stepMode() {
		if (this.getPendingInput() == null && this.getItems().isEmpty()) {
			filter = filter.next();
			while (!filter.isValid())
				filter = filter.next();
			this.resetWorkTimer();
			return true;
		}
		return false;
	}

	@Override
	public InertItem getFilterItemRender() {
		return filter.getRenderItem();
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.TOOLSTORAGE;
	}

	@Override
	protected KeyedItemStack key(ItemStack is) {
		return new KeyedItemStack(is).setIgnoreMetadata(!filter.isDamageImportant()).setIgnoreNBT(!filter.isNBTImportant()).setSized(false).setSimpleHash(true);
	}

	@Override
	public boolean isItemValid(ItemStack is) {
		return filter.isItemValid(is);
	}

	@Override
	public int maxItemCount() {
		return MAX_COUNT;
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("mode", filter.ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		filter = ToolType.list[NBT.getInteger("mode")];
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);
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

		public ToolType next() {
			return list[(this.ordinal()+1)%list.length];
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

		public boolean isValid() {
			switch(this) {
				case TINKER:
					return ModList.TINKERER.isLoaded();
				default:
					return true;
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

}

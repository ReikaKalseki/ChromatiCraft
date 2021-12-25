package Reika.ChromatiCraft.ModInterface.Bees;

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityMassStorage;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.genetics.ISpeciesType;

public class TileEntityBeeStorage extends TileEntityMassStorage {

	@ModDependent(ModList.FORESTRY)
	private GeneticsType filter;

	@SideOnly(Side.CLIENT)
	private InertItem render;

	public TileEntityBeeStorage() {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			this.initClient();
		}
	}

	@SideOnly(Side.CLIENT)
	private void initClient() {
		render = new InertItem(Minecraft.getMinecraft().theWorld, new ItemStack(Blocks.stone));
	}

	@SideOnly(Side.CLIENT)
	@ModDependent(ModList.FORESTRY)
	public void setFilter(GeneticsType s) {
		filter = s;
	}

	@Override
	protected void onAddItem(ItemStack is) {
		if (ModList.FORESTRY.isLoaded()) {
			if (filter == null) {
				filter = this.getGeneItemType(is);
			}
		}
	}

	@Override
	protected void onRemoveItem(ItemStack is) {
		if (this.isEmpty()) {
			filter = null;
		}
	}

	@Override
	protected void onTick(World world, int x, int y, int z) {
		if (world.isRemote && this.getTicksExisted()%20 == Math.abs(System.identityHashCode(this))%20) {
			Collection<KeyedItemStack> li = this.getItemTypes().keySet();
			KeyedItemStack is = li.isEmpty() ? null : ReikaJavaLibrary.getRandomCollectionEntry(rand, li);
			render.setEntityItemStack(is != null ? is.getItemStack() : new ItemStack(Blocks.stone));
		}
	}

	@Override
	public InertItem getFilterItemRender() {
		return render;
	}

	@Override
	protected KeyedItemStack key(ItemStack is) {
		return new KeyedItemStack(this.filterKey(is)).setIgnoreMetadata(true).setIgnoreNBT(false).setSized(false).setSimpleHash(true);
	}

	private ItemStack filterKey(ItemStack is) {
		is = is.copy();
		if (is.stackTagCompound != null) {
			is = ReikaBeeHelper.convertToBasicSpeciesTemplate(is);
		}
		return is;
	}

	@Override
	public boolean isItemValid(ItemStack is) {
		GeneticsType f = this.getGeneItemType(is);
		if (f == null)
			return false;
		return filter == null || f.comparator == filter.comparator;
	}

	private GeneticsType getGeneItemType(ItemStack is) {
		ISpeciesType type = ReikaBeeHelper.getSpeciesType(is);
		if (type != null) {
			return new SpeciesType(type);
		}
		else if (is.getItem() == ReikaBeeHelper.getGendustrySampleItem() || is.getItem() == ReikaBeeHelper.getBinnieSampleItem()) {
			return new ItemType(is.getItem());
		}
		else {
			return null;
		}
	}

	@Override
	public int maxItemCount() {
		return 1000000;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.BEESTORAGE;
	}

	@Override
	protected boolean canPullFromME() {
		return filter != null;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		if (filter != null) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("class", filter.getClass().getName());
			tag.setString("field", filter.lookupKey());
			NBT.setTag("filtertype", tag);
		}
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		if (NBT.hasKey("filtertype")) {
			NBTTagCompound tag = NBT.getCompoundTag("filtertype");
			String cl = tag.getString("class");
			String f = tag.getString("field");
			Class c = null;
			try {
				c = Class.forName(cl);
				if (c == SpeciesType.class) {
					filter = SpeciesType.construct(f);
				}
				else if (ISpeciesType.class.isAssignableFrom(c)) { //legacy
					filter = new SpeciesType((ISpeciesType)Enum.valueOf(c, f));
				}
				else if (c == ItemType.class) {
					filter = ItemType.construct(f);
				}
			}
			catch (Exception e) {
				ChromatiCraft.logger.logError("Failed to load genetics storage data from disk");
				e.printStackTrace();
			}
		}
	}

	private static class SpeciesType extends GeneticsType<ISpeciesType> {

		protected SpeciesType(ISpeciesType obj) {
			super(obj);
		}

		@Override
		protected String lookupKey() {
			return comparator.getClass().getName()+"#"+((Enum)comparator).name();
		}

		private static SpeciesType construct(String s) throws Exception {
			String[] parts = s.split("#");
			return new SpeciesType((ISpeciesType)Enum.valueOf((Class<Enum>)Class.forName(parts[0]), parts[1]));
		}


	}

	private static class ItemType extends GeneticsType<Item> {

		protected ItemType(Item obj) {
			super(obj);
		}

		@Override
		protected String lookupKey() {
			return Item.itemRegistry.getNameForObject(comparator);
		}

		private static ItemType construct(String s) {
			return new ItemType((Item)Item.itemRegistry.getObject(s));
		}

	}

	private static abstract class GeneticsType<T> {

		protected final T comparator;

		protected GeneticsType(T obj) {
			this.comparator = obj;
		}

		protected abstract String lookupKey();

		@Override
		public final boolean equals(Object o) {
			return o != null && o.getClass() == this.getClass() && this.comparator == ((GeneticsType)o).comparator;
		}

	}
}

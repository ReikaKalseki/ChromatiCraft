package Reika.ChromatiCraft.ModInterface.Bees;

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

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
	private ISpeciesType filter;

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
	public void setFilter(ISpeciesType s) {
		filter = s;
	}

	@Override
	protected void onAddItem(ItemStack is) {
		if (ModList.FORESTRY.isLoaded()) {
			if (filter == null) {
				filter = ReikaBeeHelper.getSpeciesType(is);
			}
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
		ISpeciesType f = ReikaBeeHelper.getSpeciesType(is);
		if (f == null)
			return false;
		return filter == null || f == filter;
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
			tag.setString("field", ((Enum)filter).name());
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
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if (c != null)
				filter = (ISpeciesType)Enum.valueOf(c, f);
		}
	}

	/*
	@ModDependent(ModList.FORESTRY)
	private Module getModule() {
		ISpeciesType type = filter;
		if (type instanceof EnumBeeType)
			return Module.BEES;
		if (type instanceof EnumGermlingType)
			return Module.TREES;
		if (type instanceof EnumFlutterType)
			return Module.FLIES;
		return null;
	}

	private static enum Module {
		BEES,
		TREES,
		FLIES;
	}
	 */
}

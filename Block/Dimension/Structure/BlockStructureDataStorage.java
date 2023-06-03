/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ElementEncodedNumber;
import Reika.ChromatiCraft.Base.BlockDimensionStructureTile;
import Reika.ChromatiCraft.Base.CrystalTypeBlock;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.ItemCrystalBasic;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Interfaces.TileEntity.InertIInv;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

public class BlockStructureDataStorage extends BlockDimensionStructureTile {

	private final IIcon[][] icons = new IIcon[2][2];

	public BlockStructureDataStorage(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		switch(meta) {
			case 0:
				return new TileEntityStructureDataStorage();
			case 1:
				return new TileEntityStructurePassword();
			default:
				return null;
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		icons[0][0] = ico.registerIcon("chromaticraft:dimstruct/dimdata");
		icons[1][0] = ico.registerIcon("chromaticraft:dimstruct/dimdata_side");
		icons[0][1] = ico.registerIcon("chromaticraft:dimstruct/dimpassword");
		icons[1][1] = ico.registerIcon("chromaticraft:dimstruct/dimpassword_side");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return /*s <= 1 ? icons[0] : */icons[1][meta];
	}

	@Override
	public boolean onRightClicked(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (!world.isRemote) {
			((StructureInterfaceTile)world.getTileEntity(x, y, z)).onRightClick(ep, s);
		}
		return true;
	}

	public static abstract class StructureInterfaceTile extends TileEntity {

		private UUID structureUID;

		protected abstract void onRightClick(EntityPlayer ep, int s);

		public final void loadData(DimensionStructureGenerator gen, HashMap<String, Object> map) {
			if (gen == null)
				return;
			structureUID = gen.id;
			this.onDataLoad(gen, map);
		}

		protected abstract void onDataLoad(DimensionStructureGenerator gen, HashMap<String, Object> map);

		protected final DimensionStructureGenerator getStructure() {
			return DimensionStructureGenerator.getGeneratorByID(structureUID);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			if (NBT.hasKey("uid")) {
				structureUID = UUID.fromString(NBT.getString("uid"));
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			if (structureUID != null)
				NBT.setString("uid", structureUID.toString());
		}

	}

	public static class TileEntityStructurePassword extends StructureInterfaceTile implements InertIInv {

		private ItemStack[] inv = new ItemStack[8];

		@Override
		public void onDataLoad(DimensionStructureGenerator gen, HashMap<String, Object> map) {

		}

		@Override
		protected void onRightClick(EntityPlayer ep, int s) {
			if (!this.checkPassword(ep) && ChromaOptions.canPlayerUseStructureBypass(ep)) {
				ep.openGui(ChromatiCraft.instance, ChromaGuis.STRUCTUREPASS.ordinal(), worldObj, xCoord, yCoord, zCoord);
			}
			//if (ReikaPlayerAPI.isReika(ep)) {
			//	this.getStructure().forceOpen(worldObj, ep);
			//}
		}

		@Override
		public int getSizeInventory() {
			return 8;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return inv[slot];
		}

		@Override
		public ItemStack decrStackSize(int slot, int decr) {
			return ReikaInventoryHelper.decrStackSize(this, slot, decr);
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int slot) {
			return ReikaInventoryHelper.getStackInSlotOnClosing(this, slot);
		}

		@Override
		public void setInventorySlotContents(int slot, ItemStack is) {
			inv[slot] = is;
		}

		@Override
		public String getInventoryName() {
			DimensionStructureGenerator gen = this.getStructure();
			if (gen != null) {
				return gen.getType().getDisplayText()+" "+ReikaStringParser.parseRomanRumeral(gen.getGenerationIndex())+" Bypass";
			}
			else { //always true on client
				return "Structure Bypass";//"[no struct]";
			}
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
			return true;
		}

		@Override
		public void openInventory() {

		}

		@Override
		public void closeInventory() {

		}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack is) {
			return this.isValidItem(is.getItem());
		}

		private boolean isValidItem(Item i) {
			return i instanceof ItemCrystalBasic || (i instanceof ItemBlock && ((ItemBlock)i).field_150939_a instanceof CrystalTypeBlock);
		}

		public boolean checkPassword(EntityPlayer ep) {
			DimensionStructureGenerator gen = this.getStructure();
			if (gen == null) {
				ChromaSounds.ERROR.playSoundAtBlock(this);
				return false;
			}
			if (gen.forcedOpen()) {
				ChromaSounds.CRAFTDONE.playSoundAtBlock(this);
				return true;
			}
			ElementEncodedNumber pass = new ElementEncodedNumber(gen.getPassword(ep));
			if (ReikaPlayerAPI.isReika(ep) || ProgressionManager.instance.hasPlayerCompletedStructureColor(ep, gen.getCoreColor())) {
				ChromaSounds.CRAFTDONE.playSoundAtBlock(this);
				gen.forceOpen(worldObj, ep);
				return true;
			}
			//ReikaJavaLibrary.pConsole(Arrays.toString(ReikaJavaLibrary.splitIntToHexChars(pass)));
			byte[] chars = new byte[8];
			for (int i = 0; i < 8; i++) {
				if (inv[i] == null) {
					//ChromaSounds.ERROR.playSoundAtBlock(this);
					return false;
				}
				if (!this.isItemValidForSlot(i, inv[i])) {
					//ChromaSounds.ERROR.playSoundAtBlock(this);
					return false;
				}
				chars[i] = (byte)(inv[i].getItemDamage()%16);
			}
			//ReikaJavaLibrary.pConsole(pass+" , "+given+" > "+Arrays.toString(ReikaJavaLibrary.splitIntToHexChars(pass)));
			boolean match = pass.match(chars);
			if (match) {
				ChromaSounds.CRAFTDONE.playSoundAtBlock(this);
				gen.forceOpen(worldObj, ep);
				for (int i = 0; i < 8; i++) {
					ItemStack is = inv[i];
					ReikaPlayerAPI.addOrDropItem(is, ep);
				}
			}
			else {
				ChromaSounds.ERROR.playSoundAtBlock(this);
			}
			return match;
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			NBTTagList nbttaglist = new NBTTagList();

			for (int i = 0; i < inv.length; i++) {
				if (inv[i] != null) {
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					nbttagcompound.setByte("Slot", (byte)i);
					inv[i].writeToNBT(nbttagcompound);
					nbttaglist.appendTag(nbttagcompound);
				}
			}

			NBT.setTag("Items", nbttaglist);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			NBTTagList nbttaglist = NBT.getTagList("Items", NBTTypes.COMPOUND.ID);
			inv = new ItemStack[this.getSizeInventory()];

			for (int i = 0; i < nbttaglist.tagCount(); i++)
			{
				NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
				byte byte0 = nbttagcompound.getByte("Slot");

				if (byte0 >= 0 && byte0 < inv.length) {
					inv[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound);
				}
			}
		}

	}

	public static class TileEntityStructureDataStorage extends StructureInterfaceTile {

		private StructureData data;
		private HashMap<String, Object> extraData;

		@Override
		protected void onDataLoad(DimensionStructureGenerator gen, HashMap<String, Object> map) {
			data = gen.createDataStorage();
			if (data != null)
				data.load(map);
			extraData = map;
		}

		@Override
		public boolean canUpdate() {
			return true;
		}

		@Override
		public void updateEntity() {
			if (data != null)
				data.onTileTick(this);
		}

		@Override
		protected void onRightClick(EntityPlayer ep, int s) {
			if (data != null)
				data.onInteract(worldObj, xCoord, yCoord, zCoord, ep, s, extraData);
		}

		public void setData(StructureData data) {
			this.data = data;
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			//extraData = new HashMap();
			NBTTagCompound dat = NBT.getCompoundTag("extra");
			/*
			for (Object o : dat.func_150296_c()) {
				String s = (String)o;
				extraData.put(s, ReikaNBTHelper.getValue(dat.getTag(s)));
			}
			 */
			extraData = dat != null && !dat.hasNoTags() ? (HashMap<String, Object>)ReikaNBTHelper.getValue(dat) : null;

			this.loadData(this.getStructure(), extraData);
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			if (extraData != null && !extraData.isEmpty()) {
				/*
				NBTTagCompound dat = new NBTTagCompound();
				for (String s : extraData.keySet()) {
					dat.setTag(s, ReikaNBTHelper.getTagForObject(extraData.get(s)));
				}
				NBT.setTag("extra", dat);
				 */
				NBT.setTag("extra", ReikaNBTHelper.getTagForObject(extraData));
			}
		}

	}

}

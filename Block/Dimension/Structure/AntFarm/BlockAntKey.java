/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.AntFarm;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.World.Dimension.Structure.AntFarmGenerator;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Interfaces.TileEntity.InertIInv;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class BlockAntKey extends BlockContainer {

	public BlockAntKey(Material mat) {
		super(mat);
		this.setResistance(600000);
		this.setBlockUnbreakable();
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs c, List li)
	{
		int[] size = {1, 2, 3, 4, 6, 8, 16};
		for (int i = 0; i < size.length; i++) {
			li.add(new ItemStack(this, 1, size[i]-1));
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new AntKeyTile(meta+1);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ItemStack is = ((AntKeyTile)world.getTileEntity(x, y, z)).onRightClickWith(ep.getCurrentEquippedItem(), ep);
		ep.setCurrentItemOrArmor(0, is);
		ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.pop", 1, 1);
		return true;
	}

	public static class AntKeyTile extends StructureBlockTile<AntFarmGenerator> implements InertIInv, ItemOnRightClick {

		private int invSize;
		private ItemStack[] inv;
		private InertItem[] items;

		private boolean ticked = false;

		public AntKeyTile() {
			this(0);
		}

		public AntKeyTile(int size) {
			invSize = size;
			inv = new ItemStack[invSize];
			items = new InertItem[invSize];
		}

		@Override
		public void updateEntity() {
			if (!ticked) {
				this.markDirty();
				ticked = true;
			}
		}

		@Override
		public DimensionStructureType getType() {
			return DimensionStructureType.ANTFARM;
		}

		@Override
		public int getSizeInventory() {
			return invSize;
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
			this.markDirty();
		}

		@Override
		public String getInventoryName() {
			return "Ant Key";
		}

		@Override
		public boolean hasCustomInventoryName() {
			return false;
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer ep) {
			return TileEntityBase.isStandard8mReach(ep, this);
		}

		@Override
		public void openInventory() {

		}

		@Override
		public void closeInventory() {

		}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack is) {
			return false;
		}

		@Override
		public ItemStack onRightClickWith(ItemStack item, EntityPlayer ep) {
			boolean incr = item != null;
			for (int i = incr ? 0 : invSize-1; incr ? i < invSize : i >= 0; i += incr ? 1 : -1) {
				boolean flag = false;
				if (item == null && inv[i] != null) {
					flag = true;
				}
				else if (item != null && inv[i] == null) {
					flag = true;
				}
				if (flag) {
					ItemStack old = inv[i];
					inv[i] = item;
					this.markDirty();
					return old;
				}
			}
			return item;
		}

		public InertItem getItem(int slot) {
			return items[slot];
		}

		@Override
		public void markDirty() {
			super.markDirty();
			for (int i = 0; i < invSize; i++) {
				items[i] = inv[i] != null ? new InertItem(worldObj, ReikaItemHelper.getSizedItemStack(inv[i], 1)) : null;
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT)
		{
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

			NBT.setInteger("size", invSize);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT)
		{
			super.readFromNBT(NBT);

			invSize = NBT.getInteger("size");

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

			items = new InertItem[invSize];
		}

	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Block.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLootChest extends BlockContainer {

	private static final Random rand = new Random();

	public BlockLootChest(Material mat) {
		super(mat);
		this.setHardness(6);
		this.setResistance(60);
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z)
	{
		this.setBlockBounds(0.0625F, 0, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block b, int meta)
	{
		ReikaItemHelper.dropInventory(world, x, y, z);
		super.breakBlock(world, x, y, z, b, meta);
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer ep, int x, int y, int z, boolean harvest) {
		fireEvent(world, x, y, z, ep, true);
		return super.removedByPlayer(world, ep, x, y, z, harvest);
	}

	private static void fireEvent(World world, int x, int y, int z, EntityPlayer ep, boolean b) {
		MinecraftForge.EVENT_BUS.post(new LootChestAccessEvent(world, x, y, z, ep, b));
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c)
	{
		if (world.isRemote)
			return true;
		else {
			boolean open = this.canOpen(world, x, y, z, ep);
			fireEvent(world, x, y, z, ep, open);
			if (open) {
				ep.displayGUIChest((IInventory)world.getTileEntity(x, y, z));
			}
			else
				ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.stone);
			return true;
		}
	}

	public boolean canOpen(World world, int x, int y, int z, EntityPlayer ep) {
		if (world.getBlockMetadata(x, y, z) >= 8 || world.isSideSolid(x, y+1, z, DOWN))
			return false;
		TileEntity te = world.getTileEntity(x, y, z);
		return te instanceof TileEntityLootChest && ((TileEntityLootChest)te).isUseableByPlayer(ep);
	}

	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityLootChest();
	}

	@Override
	public boolean canProvidePower()
	{
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess iba, int x, int y, int z, int p_149709_5_)
	{
		return ((TileEntityLootChest)iba.getTileEntity(x, y, z)).numPlayersUsing > 0 ? 15 : 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess iba, int x, int y, int z, int s)
	{
		return s == 1 ? this.isProvidingWeakPower(iba, x, y, z, s) : 0;
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int s)
	{
		return Container.calcRedstoneFromInventory((IInventory)world.getTileEntity(x, y, z));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int s, int meta) {
		return ChromaBlocks.STRUCTSHIELD.getBlockInstance().getIcon(s, BlockType.STONE.metadata);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is)
	{
		world.setBlockMetadataWithNotify(x, y, z, ChromaAux.get4SidedMetadataFromPlayerLook(e), 3);
	}

	public static void setMaxReach(World world, int x, int y, int z, int max) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityLootChest) {
			((TileEntityLootChest)te).maxReachAccess = max;
		}
	}

	public static final class LootChestAccessEvent extends Event {

		public final World world;
		public final int x;
		public final int y;
		public final int z;
		public final EntityPlayer player;
		public final boolean success;

		private LootChestAccessEvent(World world, int x, int y, int z, EntityPlayer ep, boolean b) {
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
			player = ep;
			success = b;
		}

	}

	public static final class TileEntityLootChest extends TileEntity implements IInventory {

		protected ItemStack[] inv = new ItemStack[this.getSizeInventory()];

		private int numPlayersUsing = 0;

		public float prevLidAngle;
		public float lidAngle;

		private int ticksSinceSync;

		private int maxReachAccess = 8;

		@Override
		public void updateEntity() {
			super.updateEntity();
			ticksSinceSync++;

			if (!worldObj.isRemote && numPlayersUsing != 0 && (ticksSinceSync+xCoord+yCoord+zCoord)%200 == 0) {
				numPlayersUsing = 0;
				double r = 5;
				List list = worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(xCoord-r, yCoord-r, zCoord-r, xCoord+1+r, yCoord+1+r, zCoord+1+r));
				Iterator iterator = list.iterator();

				while (iterator.hasNext()) {
					EntityPlayer entityplayer = (EntityPlayer)iterator.next();

					if (entityplayer.openContainer instanceof ContainerChest) {
						IInventory iinventory = ((ContainerChest)entityplayer.openContainer).getLowerChestInventory();

						if (iinventory == this) {
							++numPlayersUsing;
						}
					}
				}
			}

			prevLidAngle = lidAngle;
			float f = 0.1F;
			double d2;

			if (numPlayersUsing > 0 && lidAngle == 0) {
				double d1 = xCoord+0.5D;
				d2 = zCoord+0.5D;

				worldObj.playSoundEffect(d1, yCoord+0.5D, d2, "random.chestopen", 0.5F, 0.25F);
			}

			if (numPlayersUsing == 0 && lidAngle > 0 || numPlayersUsing > 0 && lidAngle < 1) {
				float f1 = lidAngle;

				if (numPlayersUsing > 0)
					lidAngle += f;
				else
					lidAngle -= f;

				if (lidAngle > 1)
					lidAngle = 1;

				float f2 = 0.5F;

				if (lidAngle < f2 && f1 >= f2) {
					d2 = xCoord+0.5D;
					double d0 = zCoord+0.5D;

					worldObj.playSoundEffect(d2, yCoord+0.5D, d0, "random.chestclosed", 0.5F, 0.25F);
				}

				if (lidAngle < 0)
					lidAngle = 0;
			}
		}

		public final ItemStack getStackInSlot(int par1) {
			return inv[par1];
		}

		public final void setInventorySlotContents(int par1, ItemStack is) {
			inv[par1] = is;
		}

		public void openInventory() {
			numPlayersUsing++;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			ReikaWorldHelper.causeAdjacentUpdates(worldObj, xCoord, yCoord, zCoord);
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, this.getBlockType(), 1, numPlayersUsing);
		}

		public void closeInventory() {
			numPlayersUsing--;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			ReikaWorldHelper.causeAdjacentUpdates(worldObj, xCoord, yCoord, zCoord);
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, this.getBlockType(), 1, numPlayersUsing);
		}

		@Override
		public final boolean hasCustomInventoryName() {
			return true;
		}

		public final String getInventoryName() {
			return "Loot Chest";
		}

		@Override
		public void markDirty() {
			blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this);

			if (this.getBlockType() != Blocks.air)
			{
				worldObj.func_147453_f(xCoord, yCoord, zCoord, this.getBlockType());
			}
		}

		public boolean isUseableByPlayer(EntityPlayer ep) {
			return ep.getDistance(xCoord+0.5, yCoord+0.5, zCoord+0.5) <= maxReachAccess;
		}

		public final ItemStack decrStackSize(int par1, int par2)
		{
			return ReikaInventoryHelper.decrStackSize(this, par1, par2);
		}

		public final ItemStack getStackInSlotOnClosing(int par1)
		{
			return ReikaInventoryHelper.getStackInSlotOnClosing(this, par1);
		}

		@Override
		public int getSizeInventory() {
			return 54;
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack is) {
			return true;
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

			//NBT.setInteger("using", numPlayersUsing);

			NBT.setInteger("maxreach", maxReachAccess);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT)
		{
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

			//numPlayersUsing = NBT.getInteger("using");

			maxReachAccess = NBT.getInteger("maxreach");
		}

		@Override
		public boolean receiveClientEvent(int id, int data)
		{
			if (id == 1) {
				numPlayersUsing = data;
				return true;
			}
			else {
				return super.receiveClientEvent(id, data);
			}
		}
	}

}

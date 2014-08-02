/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import java.awt.Color;

import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedPeripheral;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import Reika.ChromatiCraft.API.SpaceRift;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Base.BlockTEBase;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.WorldLocation;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import cofh.api.energy.IEnergyHandler;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dan200.computer.api.IPeripheral;

public class TileEntityRift extends TileEntityChromaticBase implements SpaceRift, IFluidHandler, IPowerReceptor, IEnergyHandler,
IEssentiaTransport, IAspectContainer, ISidedInventory, IPeripheral, Environment, ManagedPeripheral  {

	private WorldLocation target;
	private int color = 0xffffff;
	private int[] redstoneCache = new int[6];
	private ForgeDirection singleDirection;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.RIFT;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	public void setDirection(ForgeDirection dir) {
		if (target != null) {
			singleDirection = dir;
			this.getOther().singleDirection = dir.getOpposite();
		}
	}

	public ForgeDirection getSingleDirection() {
		return singleDirection;
	}

	public void passThrough() {
		if (target != null) {
			TileEntityRift te = this.getOther();
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				ForgeDirection opp = dir.getOpposite();
				int dx = target.xCoord+dir.offsetX;
				int dy = target.yCoord+dir.offsetY;
				int dz = target.zCoord+dir.offsetZ;
				int ddx = xCoord-dir.offsetX;
				int ddy = yCoord-dir.offsetY;
				int ddz = zCoord-dir.offsetZ;
				int id = worldObj.getBlockId(dx, dy, dz);
				int id2 = worldObj.getBlockId(ddx, ddy, ddz);
				int pwr = worldObj.getIndirectPowerLevelTo(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ, opp.ordinal());
				te.redstoneCache[i] = pwr;
				if (id != 0) {
					Block b = Block.blocksList[id];
					b.onNeighborBlockChange(worldObj, dx, dy, dz, id2);
				}
				TileEntity tile = this.getAdjacentTileEntity(dir);
				if (tile instanceof TileEntityBase) {
					((TileEntityBase)tile).updateCache(dir.getOpposite());
				}
			}
			target.triggerBlockUpdate(true);
		}
	}

	public int getRedstoneLevel(ForgeDirection side) {
		return Math.max(redstoneCache[side.ordinal()]-1, 0);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public boolean isLinked() {
		return target != null;
	}

	public boolean linkTo(World world, int x, int y, int z) {
		if (!world.isRemote && this.canLinkTo(world, x, y, z)) {
			this.resetOther();
			target = new WorldLocation(world, x, y, z);
			color = this.getRandomColor();
			TileEntityRift te = this.getOther();
			te.target = new WorldLocation(worldObj, xCoord, yCoord, zCoord);
			te.color = color;
			this.onLink(true);
			return true;
		}
		return false;
	}

	private void onLink(boolean other) {
		ChromaSounds.RIFT.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = xCoord+dir.offsetX;
			int dy = yCoord+dir.offsetY;
			int dz = zCoord+dir.offsetZ;
			Block b = Block.blocksList[worldObj.getBlockId(dx, dy, dz)];
			if (b instanceof BlockTEBase) {
				((BlockTEBase)b).updateTileCache(worldObj, dx, dy, dz);
			}
		}
		if (other) {
			this.getOther().onLink(false);
		}
	}

	public boolean linkTo(WorldLocation loc) {
		return !loc.equals(worldObj, xCoord, yCoord, zCoord) && this.linkTo(loc.getWorld(), loc.xCoord, loc.yCoord, loc.zCoord);
	}

	public void reset() {
		this.resetOther();
		target = null;
		color = 0xffffff;
		redstoneCache = new int[6];
		singleDirection = null;
		this.onLink(true);
	}

	public void resetOther() {
		if (target != null) {
			TileEntityRift te = this.getOther();
			if (te != null) {
				te.target = null;
				te.color = 0xffffff;
				this.onLink(true);
			}
		}
	}

	public int getColor() {
		return this.isInWorld() ? color : Color.HSBtoRGB((System.currentTimeMillis()%15000)/15000F, 1, 1);
	}

	private int getRandomColor() {
		return Color.HSBtoRGB(rand.nextFloat(), 1, 1);
	}

	private boolean canLinkTo(World world, int x, int y, int z) {
		return new WorldLocation(world, x, y, z).getBlockID() == ChromaBlocks.RIFT.getBlockID();
	}

	private TileEntityRift getOther() {
		return target != null ? ((TileEntityRift)target.getTileEntity()) : null;
	}

	public WorldLocation getBlockFrom(ForgeDirection from) {
		return target != null ? target.move(from.getOpposite(), 1) : null;
	}

	private TileEntity getAdjacentTargetTile(ForgeDirection dir) {
		return target != null && this.getOther() != null ? this.getOther().getAdjacentTileEntity(dir) : null;
	}

	private TileEntity getSingleDirTile() {
		return singleDirection != null && target != null ? this.getOther().getAdjacentTileEntity(singleDirection.getOpposite()) : null;
	}

	@Override
	public int getBlockIDFrom(ForgeDirection dir) {
		return target != null ? this.getBlockFrom(dir).getBlockID() : -1;
	}

	@Override
	public int getBlockMetadataFrom(ForgeDirection dir) {
		return target != null ? this.getBlockFrom(dir).getBlockMetadata() : -1;
	}

	@Override
	public TileEntity getTileEntityFrom(ForgeDirection dir) {
		return target != null ? this.getAdjacentTargetTile(dir) : null;
	}

	@Override
	public WorldLocation getLinkTarget() {
		return target;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		if (target != null)
			target.writeToNBT("target", NBT);
		NBT.setInteger("color", color);
		NBT.setIntArray("redstone", redstoneCache);
		NBT.setInteger("dir", singleDirection != null ? singleDirection.ordinal() : -1);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		if (NBT.hasKey("target"))
			target = WorldLocation.readFromNBT("target", NBT);
		color = NBT.getInteger("color");
		redstoneCache = NBT.getIntArray("redstone");
		int dir = NBT.getInteger("dir");
		singleDirection = dir != -1 ? dirs[dir] : null;
	}

	@Override
	public boolean isConnectable(ForgeDirection face) {
		if (this.getOther() != null && this.getAdjacentTargetTile(face.getOpposite()) instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getAdjacentTargetTile(face.getOpposite())).isConnectable(face);
		}
		return false;
	}

	@Override
	public boolean canInputFrom(ForgeDirection face) {
		if (this.getOther() != null && this.getAdjacentTargetTile(face.getOpposite()) instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getAdjacentTargetTile(face.getOpposite())).canInputFrom(face);
		}
		return false;
	}

	@Override
	public boolean canOutputTo(ForgeDirection face) {
		if (this.getOther() != null && this.getAdjacentTargetTile(face.getOpposite()) instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getAdjacentTargetTile(face.getOpposite())).canOutputTo(face);
		}
		return false;
	}

	@Override
	public void setSuction(AspectList suction) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IEssentiaTransport) {
			((IEssentiaTransport)this.getSingleDirTile()).setSuction(suction);
		}
	}

	@Override
	public void setSuction(Aspect aspect, int amount) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IEssentiaTransport) {
			((IEssentiaTransport)this.getSingleDirTile()).setSuction(aspect, amount);
		}
	}

	@Override
	public AspectList getSuction(ForgeDirection face) {
		if (this.getOther() != null && this.getAdjacentTargetTile(face.getOpposite()) instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getAdjacentTargetTile(face.getOpposite())).getSuction(face);
		}
		return null;
	}

	@Override
	public int takeVis(Aspect aspect, int amount) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getSingleDirTile()).takeVis(aspect, amount);
		}
		return 0;
	}

	@Override
	public AspectList getEssentia(ForgeDirection face) {
		if (this.getOther() != null && this.getAdjacentTargetTile(face.getOpposite()) instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getAdjacentTargetTile(face.getOpposite())).getEssentia(face);
		}
		return null;
	}

	@Override
	public int getMinimumSuction() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IEssentiaTransport) {
			return ((IEssentiaTransport)this.getSingleDirTile()).getMinimumSuction();
		}
		return 0;
	}

	@Override
	public boolean renderExtendedTube() {
		return false;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IEnergyHandler) {
			return ((IEnergyHandler)this.getAdjacentTargetTile(from.getOpposite())).receiveEnergy(from, maxReceive, simulate);
		}
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IEnergyHandler) {
			return ((IEnergyHandler)this.getAdjacentTargetTile(from.getOpposite())).extractEnergy(from, maxExtract, simulate);
		}
		return 0;
	}

	@Override
	public boolean canInterface(ForgeDirection from) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IEnergyHandler) {
			return ((IEnergyHandler)this.getAdjacentTargetTile(from.getOpposite())).canInterface(from);
		}
		return false;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IEnergyHandler) {
			return ((IEnergyHandler)this.getAdjacentTargetTile(from.getOpposite())).getEnergyStored(from);
		}
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IEnergyHandler) {
			return ((IEnergyHandler)this.getAdjacentTargetTile(from.getOpposite())).getMaxEnergyStored(from);
		}
		return 0;
	}

	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		if (this.getOther() != null && this.getAdjacentTargetTile(side.getOpposite()) instanceof IPowerReceptor) {
			return ((IPowerReceptor)this.getAdjacentTargetTile(side.getOpposite())).getPowerReceiver(side);
		}
		return null;
	}

	@Override
	public void doWork(PowerHandler workProvider) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IPowerReceptor) {
			((IPowerReceptor)this.getSingleDirTile()).doWork(workProvider);
		}
	}

	@Override
	public World getWorld() {
		return target != null ? target.getWorld() : null;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IFluidHandler) {
			return ((IFluidHandler)this.getAdjacentTargetTile(from.getOpposite())).fill(from, resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IFluidHandler) {
			return ((IFluidHandler)this.getAdjacentTargetTile(from.getOpposite())).drain(from, resource, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IFluidHandler) {
			return ((IFluidHandler)this.getAdjacentTargetTile(from.getOpposite())).drain(from, maxDrain, doDrain);
		}
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IFluidHandler) {
			return ((IFluidHandler)this.getAdjacentTargetTile(from.getOpposite())).canFill(from, fluid);
		}
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IFluidHandler) {
			return ((IFluidHandler)this.getAdjacentTargetTile(from.getOpposite())).canDrain(from, fluid);
		}
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if (this.getOther() != null && this.getAdjacentTargetTile(from.getOpposite()) instanceof IFluidHandler) {
			return ((IFluidHandler)this.getAdjacentTargetTile(from.getOpposite())).getTankInfo(from);
		}
		return null;
	}

	@Override
	public int getSizeInventory() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).getSizeInventory();
		}
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).getStackInSlot(i);
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).decrStackSize(i, j);
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).getStackInSlotOnClosing(i);
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			((IInventory)this.getSingleDirTile()).setInventorySlotContents(i, itemstack);
		}
	}

	@Override
	public String getInvName() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).getInvName();
		}
		return null;
	}

	@Override
	public boolean isInvNameLocalized() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).isInvNameLocalized();
		}
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).getInventoryStackLimit();
		}
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).isUseableByPlayer(entityplayer);
		}
		return false;
	}

	@Override
	public void openChest() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			((IInventory)this.getSingleDirTile()).openChest();
		}
	}

	@Override
	public void closeChest() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			((IInventory)this.getSingleDirTile()).closeChest();
		}
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IInventory) {
			return ((IInventory)this.getSingleDirTile()).isItemValidForSlot(i, itemstack);
		}
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		if (this.getOther() != null) {
			if (this.getSingleDirTile() instanceof ISidedInventory) {
				return ((ISidedInventory)this.getSingleDirTile()).getAccessibleSlotsFromSide(var1);
			}
			else if (this.getSingleDirTile() instanceof IInventory) {
				int size = ((IInventory)this.getSingleDirTile()).getSizeInventory();
				int[] sides = new int[size];
				for (int i = 0; i < size; i++) {
					sides[i] = i;
				}
				return sides;
			}
		}
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		if (this.getOther() != null) {
			if (this.getSingleDirTile() instanceof ISidedInventory) {
				return ((ISidedInventory)this.getSingleDirTile()).canInsertItem(i, itemstack, j);
			}
			else if (this.getSingleDirTile() instanceof IInventory) {
				return ((IInventory)this.getSingleDirTile()).isItemValidForSlot(i, itemstack);
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof ISidedInventory) {
			return ((ISidedInventory)this.getSingleDirTile()).canExtractItem(i, itemstack, j);
		}
		return false;
	}

	@Override
	public AspectList getAspects() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			return ((IAspectContainer)this.getSingleDirTile()).getAspects();
		}
		return new AspectList();
	}

	@Override
	public void setAspects(AspectList aspects) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			((IAspectContainer)this.getSingleDirTile()).setAspects(aspects);
		}
	}

	@Override
	public boolean doesContainerAccept(Aspect tag) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			return ((IAspectContainer)this.getSingleDirTile()).doesContainerAccept(tag);
		}
		return false;
	}

	@Override
	public int addToContainer(Aspect tag, int amount) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			return ((IAspectContainer)this.getSingleDirTile()).addToContainer(tag, amount);
		}
		return 0;
	}

	@Override
	public boolean takeFromContainer(Aspect tag, int amount) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			return ((IAspectContainer)this.getSingleDirTile()).takeFromContainer(tag, amount);
		}
		return false;
	}

	@Override
	public boolean takeFromContainer(AspectList ot) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			return ((IAspectContainer)this.getSingleDirTile()).takeFromContainer(ot);
		}
		return false;
	}

	@Override
	public boolean doesContainerContainAmount(Aspect tag, int amount) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			return ((IAspectContainer)this.getSingleDirTile()).doesContainerContainAmount(tag, amount);
		}
		return false;
	}

	@Override
	public boolean doesContainerContain(AspectList ot) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			return ((IAspectContainer)this.getSingleDirTile()).doesContainerContain(ot);
		}
		return false;
	}

	@Override
	public int containerContains(Aspect tag) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IAspectContainer) {
			return ((IAspectContainer)this.getSingleDirTile()).containerContains(tag);
		}
		return 0;
	}

	@Override
	public String getType() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IPeripheral) {
			return ((IPeripheral)this.getSingleDirTile()).getType();
		}
		return "No connection";
	}

	@Override
	public String[] getMethodNames() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IPeripheral) {
			return ((IPeripheral)this.getSingleDirTile()).getMethodNames();
		}
		return new String[0];
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IPeripheral) {
			return ((IPeripheral)this.getSingleDirTile()).callMethod(computer, context, method, arguments);
		}
		return null;
	}

	@Override
	public boolean canAttachToSide(int side) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IPeripheral) {
			return ((IPeripheral)this.getSingleDirTile()).canAttachToSide(side);
		}
		return false;
	}

	@Override
	public void attach(IComputerAccess computer) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IPeripheral) {
			((IPeripheral)this.getSingleDirTile()).attach(computer);
		}
	}

	@Override
	public void detach(IComputerAccess computer) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof IPeripheral) {
			((IPeripheral)this.getSingleDirTile()).detach(computer);
		}
	}

	@Override
	public String[] methods() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof ManagedPeripheral) {
			return ((ManagedPeripheral)this.getSingleDirTile()).methods();
		}
		return new String[0];
	}

	@Override
	public Object[] invoke(String method, Context context, Arguments args) throws Exception {
		if (this.getOther() != null && this.getSingleDirTile() instanceof ManagedPeripheral) {
			return ((ManagedPeripheral)this.getSingleDirTile()).invoke(method, context, args);
		}
		return null;
	}

	@Override
	public final Node node() {
		if (this.getOther() != null && this.getSingleDirTile() instanceof Environment) {
			return ((Environment)this.getSingleDirTile()).node();
		}
		return null;
	}

	@Override
	public final void onConnect(Node node) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof Environment) {
			((Environment)this.getSingleDirTile()).onConnect(node);
		}
	}

	@Override
	public final void onDisconnect(Node node) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof Environment) {
			((Environment)this.getSingleDirTile()).onDisconnect(node);
		}
	}

	@Override
	public final void onMessage(Message message) {
		if (this.getOther() != null && this.getSingleDirTile() instanceof Environment) {
			((Environment)this.getSingleDirTile()).onMessage(message);
		}
	}

}

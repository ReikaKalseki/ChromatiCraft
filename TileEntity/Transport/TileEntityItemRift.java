/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Transport;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntitySparkleFX;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Interfaces.TileEntity.Connectable;
import Reika.DragonAPI.Interfaces.TileEntity.SidePlacedTile;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityItemRift extends TileEntityChromaticBase implements SidePlacedTile, Connectable {

	private Coordinate otherEnd;
	private ForgeDirection facing;
	private boolean isEmitting;
	private boolean isFunctioning;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ITEMRIFT;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		boolean wasFunctioning = isFunctioning;
		isFunctioning = false;
		if (!world.isRemote && isEmitting && otherEnd != null) {
			IInventory src = this.getAttachment();
			if (src != null) {
				ChromaTiles c = ChromaTiles.getTile(world, otherEnd.xCoord, otherEnd.yCoord, otherEnd.zCoord);
				if (c == this.getTile()) {
					TileEntityItemRift tile = (TileEntityItemRift)otherEnd.getTileEntity(world);
					IInventory tgt = tile.getAttachment();
					if (tgt != null) {
						this.transferItems(src, tgt, this.getFacing().getOpposite());
						isFunctioning = true;
					}
				}
			}
		}
		if (isFunctioning != wasFunctioning) {
			this.syncAllData(false);
		}
		if (world.isRemote && isFunctioning) {
			this.spawnParticles(world, x, y, z);
		}
	}

	private IInventory getAttachment() {
		TileEntity te = this.getAdjacentTileEntity(this.getFacing());
		return te instanceof IInventory ? (IInventory)te : null;
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		ForgeDirection dir = this.getFacing().getOpposite();
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
		int len = 1+otherEnd.getTaxicabDistanceTo(x, y, z);
		double r = 0.25;
		int n = Math.max(1, len/2-1);
		for (int i = 0; i < n; i++) {
			double d = (this.getTicksExisted()/2D+i*len*16D/n)%(len*16)/16D-0.5;
			double ang = Math.toRadians((this.getTicksExisted()*16D)%360);
			double sin = r*Math.sin(ang);
			double cos = r*Math.cos(ang);
			double px = x+0.5+dir.offsetX*d+left.offsetX*cos;
			double py = y+0.5+dir.offsetY*d+sin;
			double pz = z+0.5+dir.offsetZ*d+left.offsetZ*cos;
			EntityBlurFX fx = new EntityCCBlurFX(CrystalElement.LIME, world, px, py, pz, 0, 0, 0);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		double d = (this.getTicksExisted()*4)%(len*16)/16D-0.5;
		double px = x+0.5+d*dir.offsetX;
		double py = y+0.5+d*dir.offsetY;
		double pz = z+0.5+d*dir.offsetZ;
		EntitySparkleFX fx = new EntitySparkleFX(world, px, py, pz, 0, 0, 0).setScale(1).setLife(15);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	private static void transferItems(IInventory src, IInventory tgt, ForgeDirection move) {
		//ReikaJavaLibrary.pConsole(src+" >> "+tgt);
		int[] from = ReikaArrayHelper.getLinearArray(src.getSizeInventory());
		if (src instanceof ISidedInventory) {
			from = ((ISidedInventory)src).getAccessibleSlotsFromSide(move.ordinal());
		}
		for (int i = 0; i < from.length; i++) {
			int slotfrom = from[i];
			ItemStack in = src.getStackInSlot(slotfrom);
			if (in != null) {
				boolean extract = src instanceof ISidedInventory ? ((ISidedInventory)src).canExtractItem(slotfrom, in, move.ordinal()) : true;
				if (extract) {
					ItemStack in2 = ReikaItemHelper.getSizedItemStack(in, getMaxTransferRate()); use relay energy to boost
					int added = ReikaInventoryHelper.addStackAndReturnCount(in2, tgt, move.getOpposite());
					if (added > 0) {
						in.stackSize -= added;
						if (in.stackSize <= 0) {
							src.setInventorySlotContents(slotfrom, null);
						}
					}
				}
			}
		}
	}

	public final void reset() {
		otherEnd = null;
	}

	public final void resetOther() {
		ChromaTiles m = ChromaTiles.getTile(worldObj, otherEnd.xCoord, otherEnd.yCoord, otherEnd.zCoord);
		if (m == this.getTile()) {
			TileEntityItemRift te = (TileEntityItemRift)otherEnd.getTileEntity(worldObj);
			te.reset();
		}
	}

	public final boolean canConnectTo(World world, int x, int y, int z) {
		int dx = x-xCoord;
		int dy = y-yCoord;
		int dz = z-zCoord;

		//ReikaJavaLibrary.pConsole(isEmitting ? Arrays.toString(source) : Arrays.toString(target));

		if (!ReikaMathLibrary.nBoolsAreTrue(1, dx != 0, dy != 0, dz != 0))
			return false;

		ForgeDirection dir = null;

		if (dx > 0)
			dir = ForgeDirection.EAST;
		if (dx < 0)
			dir = ForgeDirection.WEST;
		if (dy > 0)
			dir = ForgeDirection.UP;
		if (dy < 0)
			dir = ForgeDirection.DOWN;
		if (dz > 0)
			dir = ForgeDirection.SOUTH;
		if (dz < 0)
			dir = ForgeDirection.NORTH;

		if (dir == null)
			return false;
		if (!this.isValidDirection(dir))
			return false;

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityItemRift) {
			if (((TileEntityItemRift)te).isEmitting == isEmitting)
				return false;
			for (int i = 1; i < Math.abs(dx+dy+dz); i++) {
				int xi = xCoord+dir.offsetX*i;
				int yi = yCoord+dir.offsetY*i;
				int zi = zCoord+dir.offsetZ*i;
				if (!this.isPassableBlock(world, xi, yi, zi)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private boolean isPassableBlock(World world, int x, int y, int z) {
		return ReikaWorldHelper.softBlocks(world, x, y, z) || RayTracer.getTransparentBlocks().contains(BlockKey.getAt(world, x, y, z)) || BCPIPE;
	}

	private boolean isValidDirection(ForgeDirection dir) {
		return true;
	}

	public final boolean tryConnect(World world, int x, int y, int z) {
		if (!this.canConnectTo(world, x, y, z))
			return false;
		otherEnd = new Coordinate(x, y, z);
		return true;
	}

	public Coordinate getConnection() {
		return otherEnd;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("emit", isEmitting);

		if (otherEnd != null)
			otherEnd.writeToNBT("endpoint", NBT);

		NBT.setInteger("dir", this.getFacing().ordinal());
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		isEmitting = NBT.getBoolean("emit");

		otherEnd = Coordinate.readFromNBT("endpoint", NBT);

		facing = dirs[NBT.getInteger("dir")];
	}

	public ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.UP;
	}

	@Override
	public void placeOnSide(int s) {
		facing = ForgeDirection.VALID_DIRECTIONS[s].getOpposite();
	}

	public void flip() {
		this.flip(true);
	}

	private void flip(boolean other) {
		isEmitting = !isEmitting;
		this.syncAllData(false);
		if (other && otherEnd != null) {
			TileEntity te = otherEnd.getTileEntity(worldObj);
			if (te instanceof TileEntityItemRift) {
				((TileEntityItemRift)te).flip(false);
			}
		}
	}

	@Override
	public boolean isEmitting() {
		return isEmitting;
	}

	@Override
	public boolean checkLocationValidity() {
		WorldLocation loc = this.getAdjacentLocation(this.getFacing());
		return loc.getBlock().isSideSolid(worldObj, loc.xCoord, loc.yCoord, loc.zCoord, this.getFacing().getOpposite());
	}

	public void drop() {
		ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, this.getTile().getCraftedProduct());
		this.delete();
	}

}

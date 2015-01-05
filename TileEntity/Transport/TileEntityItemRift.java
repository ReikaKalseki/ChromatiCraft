/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Transport;

import net.minecraft.block.Block;
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
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntitySparkleFX;
import Reika.DragonAPI.Interfaces.Connectable;
import Reika.DragonAPI.Interfaces.SidePlacedTile;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.RotaryCraft.API.Interfaces.Screwdriverable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityItemRift extends TileEntityChromaticBase implements Screwdriverable, SidePlacedTile, Connectable {

	private int[] source = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
	private int[] target = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
	private ForgeDirection facing;
	public boolean isEmitting;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ITEMRIFT;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		boolean connected = false;
		if (!isEmitting) {
			TileEntity src = this.getAdjacentTileEntity(this.getFacing());
			if (src instanceof IInventory) {
				ChromaTiles c = ChromaTiles.getTile(world, target[0], target[1], target[2]);
				if (c == this.getTile()) {
					TileEntityItemRift tile = (TileEntityItemRift)world.getTileEntity(target[0], target[1], target[2]);
					TileEntity tgt = tile.getAdjacentTileEntity(tile.getFacing());
					if (tgt instanceof IInventory) {
						if (!world.isRemote)
							this.transferItems((IInventory)src, this.getFacing(), (IInventory)tgt, tile.getFacing());
						connected = true;
					}
				}
			}
		}
		if (!world.isRemote && connected) {
			this.spawnParticles(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		ForgeDirection dir = this.getFacing().getOpposite();
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
		int index = ReikaDirectionHelper.getDirectionIndex(dir);
		int[] c = new int[]{x, y, z};
		int len = 1+Math.abs(c[index] - target[index]);
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
			EntityBlurFX fx = new EntityBlurFX(CrystalElement.LIGHTBLUE, world, px, py, pz, 0, 0, 0);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		double d = (this.getTicksExisted()*4)%(len*16)/16D-0.5;
		double px = x+0.5+d*dir.offsetX;
		double py = y+0.5+d*dir.offsetY;
		double pz = z+0.5+d*dir.offsetZ;
		EntitySparkleFX fx = new EntitySparkleFX(world, px, py, pz, 0, 0, 0).setScale(1).setLife(15);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	private void transferItems(IInventory src, ForgeDirection dir1, IInventory tgt, ForgeDirection dir2) {
		//ReikaJavaLibrary.pConsole(src+" >> "+tgt);
		int d1 = dir1.getOpposite().ordinal();
		int d2 = dir2.getOpposite().ordinal();
		int[] from = ReikaArrayHelper.getLinearArray(src.getSizeInventory());
		if (src instanceof ISidedInventory) {
			from = ((ISidedInventory)src).getAccessibleSlotsFromSide(d1);
		}
		int[] to = ReikaArrayHelper.getLinearArray(src.getSizeInventory());
		if (src instanceof ISidedInventory) {
			to = ((ISidedInventory)src).getAccessibleSlotsFromSide(d2);
		}
		for (int i = 0; i < from.length; i++) {
			int slotfrom = from[i];
			for (int k = 0; k < to.length; k++) {
				int slotto = to[k];
				ItemStack in = src.getStackInSlot(slotfrom);
				if (in != null) {
					boolean extract = src instanceof ISidedInventory ? ((ISidedInventory)src).canExtractItem(slotfrom, in, d1) : true;
					if (extract) {
						boolean valid  = tgt.isItemValidForSlot(slotto, in);
						boolean insert = tgt instanceof ISidedInventory ? ((ISidedInventory)src).canInsertItem(slotto, in, d2) : true;
						if (insert && valid) {
							this.transferItem(src, slotfrom, tgt, slotto, in);
						}
					}
				}
			}
		}
	}

	private void transferItem(IInventory src, int slotfrom, IInventory tgt, int slotto, ItemStack transfer) {
		ItemStack already = tgt.getStackInSlot(slotto);
		int maxsize = Math.min(transfer.getMaxStackSize(), tgt.getInventoryStackLimit());
		int space = already == null ? maxsize : maxsize-already.stackSize;
		if (space > 0) {
			int amt = Math.min(space, transfer.stackSize);
			if (amt > 0) {
				if (already != null) {
					already.stackSize += amt;
				}
				else {
					ItemStack put = ReikaItemHelper.getSizedItemStack(transfer, amt);
					tgt.setInventorySlotContents(slotto, put);
				}
				transfer.stackSize -= amt;
				if (transfer.stackSize == 0) {
					src.setInventorySlotContents(slotfrom, null);
				}
			}
		}
	}

	public final void reset() {
		source = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
		target = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
	}

	public final void resetOther() {
		if (!isEmitting) {
			ChromaTiles m = ChromaTiles.getTile(worldObj, target[0], target[1], target[2]);
			if (m == this.getTile()) {
				TileEntityItemRift te = (TileEntityItemRift)worldObj.getTileEntity(target[0], target[1], target[2]);
				te.reset();
			}
		}
		else {
			ChromaTiles m = ChromaTiles.getTile(worldObj, source[0], source[1], source[2]);
			if (m == this.getTile()) {
				TileEntityItemRift te = (TileEntityItemRift)worldObj.getTileEntity(source[0], source[1], source[2]);
				te.reset();
			}
		}
	}

	public final boolean canConnect(int x, int y, int z) {
		int dx = x-xCoord;
		int dy = y-yCoord;
		int dz = z-zCoord;

		//ReikaJavaLibrary.pConsole(isEmitting ? Arrays.toString(source) : Arrays.toString(target));

		if (!ReikaMathLibrary.nBoolsAreTrue(1, dx != 0, dy != 0, dz != 0))
			return false;

		ForgeDirection dir = ForgeDirection.UNKNOWN;

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

		for (int i = 1; i < Math.abs(dx+dy+dz); i++) {
			int xi = xCoord+dir.offsetX*i;
			int yi = yCoord+dir.offsetY*i;
			int zi = zCoord+dir.offsetZ*i;
			Block id = worldObj.getBlock(xi, yi, zi);
			//ReikaJavaLibrary.pConsole(xi+", "+yi+", "+zi+" -> "+id, Side.SERVER);
			if (!ReikaWorldHelper.softBlocks(worldObj, xi, yi, zi)) {
				return false;
			}
		}
		return true;
	}

	private boolean isValidDirection(ForgeDirection dir) {
		return true;
	}

	public final boolean setTarget(World world, int x, int y, int z) {
		if (!this.canConnect(x, y, z))
			return false;
		target[0] = x;
		target[1] = y;
		target[2] = z;
		//ReikaJavaLibrary.pConsole(this, Side.SERVER);
		return true;
	}

	public final boolean setSource(World world, int x, int y, int z) {
		if (!this.canConnect(x, y, z))
			return false;
		source[0] = x;
		source[1] = y;
		source[2] = z;
		//ReikaJavaLibrary.pConsole(this, Side.SERVER);
		return true;
	}

	public int[] getTarget() {
		int[] tg = new int[3];
		System.arraycopy(target, 0, tg, 0, 3);
		return tg;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setBoolean("emit", isEmitting);

		NBT.setIntArray("tg", target);
		NBT.setIntArray("src", source);

		NBT.setInteger("dir", this.getFacing().ordinal());
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		isEmitting = NBT.getBoolean("emit");

		source = NBT.getIntArray("src");
		target = NBT.getIntArray("tg");

		facing = dirs[NBT.getInteger("dir")];
	}

	public ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.UP;
	}

	@Override
	public boolean onShiftRightClick(World world, int x, int y, int z, ForgeDirection side) {
		return false;
	}

	@Override
	public boolean onRightClick(World world, int x, int y, int z, ForgeDirection side) {
		int o = this.getFacing().ordinal();
		ForgeDirection next = o == 5 ? ForgeDirection.DOWN : ForgeDirection.VALID_DIRECTIONS[o+1];
		this.setFacing(next);
		return true;
	}

	public void setFacing(ForgeDirection next) {
		facing = next;
	}

	@Override
	public void placeOnSide(int s) {
		facing = ForgeDirection.VALID_DIRECTIONS[s].getOpposite();
	}

	@Override
	public boolean isEmitting() {
		return isEmitting;
	}

}

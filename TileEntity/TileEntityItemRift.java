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

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Interfaces.SidePlacedTile;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.RotaryCraft.API.Screwdriverable;
import Reika.RotaryCraft.TileEntities.Transmission.TileEntityBeltHub;

public class TileEntityItemRift extends TileEntityChromaticBase implements Screwdriverable, SidePlacedTile {

	private int[] source = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
	private int[] target = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
	private ForgeDirection facing;
	private boolean isEmitting;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ITEMRIFT;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (target != null) {
			TileEntity src = this.getAdjacentTileEntity(this.getFacing());
			if (src instanceof IInventory) {
				ChromaTiles c = ChromaTiles.getTile(world, target[0], target[1], target[2]);
				if (c == this.getTile()) {
					TileEntityItemRift tile = (TileEntityItemRift)world.getTileEntity(target[0], target[1], target[2]);
					TileEntity tgt = tile.getAdjacentTileEntity(tile.getFacing());
					if (tgt instanceof IInventory) {
						this.transferItems(src, tgt);
					}
				}
			}
		}
	}

	private void transferItems(TileEntity src, TileEntity tgt) {

	}

	public final void reset() {
		target = null;
	}

	public final void resetOther() {
		if (!isEmitting) {
			ChromaTiles m = ChromaTiles.getTile(worldObj, target[0], target[1], target[2]);
			if (m == this.getTile()) {
				TileEntityBeltHub te = (TileEntityBeltHub)worldObj.getTileEntity(target[0], target[1], target[2]);
				te.reset();
			}
		}
		else {
			ChromaTiles m = ChromaTiles.getTile(worldObj, source[0], source[1], source[2]);
			if (m == this.getTile()) {
				TileEntityBeltHub te = (TileEntityBeltHub)worldObj.getTileEntity(source[0], source[1], source[2]);
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
		return false;
	}

	public final boolean setTarget(int x, int y, int z) {
		if (!this.canConnect(x, y, z))
			return false;
		target[0] = x;
		target[1] = y;
		target[2] = z;
		//ReikaJavaLibrary.pConsole(this, Side.SERVER);
		return true;
	}

	public final boolean setSource(int x, int y, int z) {
		if (!this.canConnect(x, y, z))
			return false;
		source[0] = x;
		source[1] = y;
		source[2] = z;
		//ReikaJavaLibrary.pConsole(this, Side.SERVER);
		return true;
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
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		isEmitting = NBT.getBoolean("emit");

		source = NBT.getIntArray("src");
		target = NBT.getIntArray("tg");
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

}

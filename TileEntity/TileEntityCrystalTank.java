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

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.BlockCrystalTank.CrystalTankAuxTile;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.FlaggedTank;
import Reika.DragonAPI.Instantiable.FlaggedTank.TankWatcher;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class TileEntityCrystalTank extends TileEntityChromaticBase implements IFluidHandler, TankWatcher {

	private final FlaggedTank tank = new FlaggedTank(this, "crystaltank", 1000000000);

	private final BlockArray blocks = new BlockArray();
	private int size = 1;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		//ReikaJavaLibrary.pConsole(this.getCapacity()/1000+":"+tank);
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.initCoords(world, x, y, z);
		this.update();
	}

	private void update() {
		for (int i = 0; i < blocks.getSize(); i++) {
			int[] a = blocks.getNthBlock(i);
			worldObj.markBlockForUpdate(a[0], a[1], a[2]);
		}
	}

	private void initCoords(World world, int x, int y, int z) {
		if (blocks.isEmpty()) {
			BlockArray toadd = new BlockArray();
			List<Block> li = Arrays.asList(ChromaBlocks.TANK.getBlockInstance(), ChromaTiles.TANK.getBlock());
			toadd.recursiveAddMultipleWithBounds(world, x, y, z, li, x-32, y-32, z-32, x+32, y+32, z+32);

			for (int i = 0; i < toadd.getSize(); i++) {
				int[] xyz = toadd.getNthBlock(i);
				int dx = xyz[0];
				int dy = xyz[1];
				int dz = xyz[2];
				ChromaTiles c = ChromaTiles.getTile(world, dx, dy, dz);
				if (c == ChromaTiles.TANK && (dx != x || dy != y || dz != z)) {
					return; //max 1 controller
				}
			}

			for (int i = 0; i < toadd.getSize(); i++) {
				int[] xyz = toadd.getNthBlock(i);
				int dx = xyz[0];
				int dy = xyz[1];
				int dz = xyz[2];
				if (world.getBlock(dx, dy, dz) == ChromaBlocks.TANK.getBlockInstance()) {
					CrystalTankAuxTile te = (CrystalTankAuxTile)world.getTileEntity(dx, dy, dz);

					te.setTile(this);
					world.setBlockMetadataWithNotify(dx, dy, dz, 1, 3);
					te.addToTank();
				}
			}
		}
		blocks.addBlockCoordinate(x, y, z);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		int add = Math.min(this.getCapacity()-this.getLevel(), resource.amount);
		if (add > 0) {
			FluidStack fs = new FluidStack(resource.getFluid(), add);
			return tank.fill(fs, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.TANK;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public int getCapacity() {
		int base = Math.min(size*size, 500000);
		int lin = base*4000;
		double fac = Math.pow(1.005, size-1);
		int bucket = (int)(lin*fac/1000D);
		int rnd = 1;
		if (bucket > 100)
			rnd = 10;
		if (bucket > 1000)
			rnd = 100;
		if (bucket > 10000)
			rnd = 1000;
		if (rnd > 1)
			bucket = ReikaMathLibrary.roundUpToX(rnd, bucket);
		return bucket*1000;
	}

	public Fluid getFluid() {
		return tank.getActualFluid();
	}

	public int getLevel() {
		return tank.getLevel();
	}

	public BlockArray getBlocks() {
		return blocks.copy();
	}

	public void addCoordinate(int x, int y, int z) {
		if (blocks.addBlockCoordinate(x, y, z))
			size++;
	}

	public void removeCoordinate(int x, int y, int z) {
		if (blocks.hasBlock(x, y, z))
			size--;
		blocks.remove(x, y, z);
		if (tank.getLevel() > this.getCapacity())
			tank.setContents(this.getCapacity(), tank.getActualFluid());
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		tank.writeToNBT(NBT);
		NBT.setInteger("size", size);

		blocks.writeToNBT("blocks", NBT);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		tank.readFromNBT(NBT);
		size = NBT.getInteger("size");

		blocks.readFromNBT("blocks", NBT);
		blocks.addBlockCoordinate(xCoord, yCoord, zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return blocks.asAABB();
	}

	public double getFillLevelForY(int y) {
		int height = blocks.getSizeY();
		int min = blocks.getMinY();
		int max = blocks.getMaxY();
		double per = this.getFillPercentage();
		int fy = (int)(min+height*per);
		//ReikaJavaLibrary.pConsole(per+":"+fy);
		if (y < fy)
			return 1;
		else if (y > fy)
			return 0;
		else {
			double fracfull = (fy-min);
			return per*height-fracfull;
		}
	}

	public double getFillPercentage() {
		return (double)tank.getLevel()/this.getCapacity();
	}

	@Override
	public void onTankChangeFluidType(String tank, Fluid from, Fluid to) {
		this.update();
	}

	public boolean isEmpty() {
		return tank.isEmpty();
	}

}

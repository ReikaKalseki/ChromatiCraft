/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Storage;

import java.util.Set;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import Reika.ChromatiCraft.API.Interfaces.CrystalTank;
import Reika.ChromatiCraft.Auxiliary.Render.TankRunes;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.BlockCrystalTank.CrystalTankAuxTile;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityTankCapacityUpgrade;
import Reika.DragonAPI.Instantiable.FlaggedTank;
import Reika.DragonAPI.Instantiable.FlaggedTank.TankWatcher;
import Reika.DragonAPI.Instantiable.LightingCache;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.TileEntity.AdjacentUpdateWatcher;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCrystalTank extends TileEntityChromaticBase implements CrystalTank, TankWatcher, BreakAction, AdjacentUpdateWatcher {

	public static final int MAXCAPACITY = 2000000000;

	private final FlaggedTank tank = new FlaggedTank(this, "crystaltank", MAXCAPACITY);
	private int scheduledUpdate = 0;
	public static final int MAX_UPDATE_RATE = 4;

	private final BlockArray blocks = new BlockArray();
	private int size = 1;
	private Fluid fluidType;

	//Render only-------
	public final TankRunes runes = new TankRunes();
	public final LightingCache lighting = new LightingCache(10);
	public int ptick = -1;
	public int lastptick = -1;
	//------------------

	public static final int FACTOR = 4000;

	private double capacityIncreaseFactor = 0;

	private boolean capacityUpdatePaused = false;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		//ReikaJavaLibrary.pConsole(this.getCapacity()/1000+":"+tank);

		if (scheduledUpdate > 0) {
			scheduledUpdate--;
			if (scheduledUpdate == 0)
				this.doUpdate();
		}

		if (world.isRemote && this.getTicksExisted()%1024 == 0)
			lighting.update(world);
	}

	public int getViscosity() {
		return fluidType.getViscosity(tank.getFluid());
	}

	public int getDensity() {
		return fluidType.getDensity(tank.getFluid());
	}

	public boolean isInvertedFilled() {
		return false;//this.getDensity() < 0;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.initCoords(world, x, y, z);
		this.doUpdate();
	}

	private void doUpdate() {
		fluidType = tank.getActualFluid();
		//ReikaJavaLibrary.pConsole(this);
		for (int i = 0; i < blocks.getSize(); i++) {
			Coordinate c = blocks.getNthBlock(i);
			c.triggerBlockUpdate(worldObj, false);
		}
	}

	private void scheduleUpdate() {
		if (scheduledUpdate == 0) {
			scheduledUpdate = MAX_UPDATE_RATE;
		}
	}

	private void initCoords(World world, int x, int y, int z) {
		if (blocks.isEmpty()) {
			BlockArray toadd = new BlockArray();
			Set<BlockKey> li = ReikaJavaLibrary.getSet(new BlockKey(ChromaBlocks.TANK.getBlockInstance()));
			toadd.recursiveAddMultipleWithBounds(world, x, y, z, li, x-32, y-32, z-32, x+32, y+32, z+32);

			for (int i = 0; i < toadd.getSize(); i++) {
				Coordinate c = toadd.getNthBlock(i);
				int dx = c.xCoord;
				int dy = c.yCoord;
				int dz = c.zCoord;
				ChromaTiles ct = ChromaTiles.getTile(world, dx, dy, dz);
				if (ct == ChromaTiles.TANK && (dx != x || dy != y || dz != z)) {
					return; //max 1 controller
				}
			}

			for (int i = 0; i < toadd.getSize(); i++) {
				Coordinate c = toadd.getNthBlock(i);
				int dx = c.xCoord;
				int dy = c.yCoord;
				int dz = c.zCoord;
				if (world.getBlock(dx, dy, dz) == ChromaBlocks.TANK.getBlockInstance()) {
					CrystalTankAuxTile te = (CrystalTankAuxTile)world.getTileEntity(dx, dy, dz);

					te.setTile(this);
					world.setBlockMetadataWithNotify(dx, dy, dz, 1, 3);
					te.addToTank();
				}
			}
		}
		blocks.addBlockCoordinate(x, y, z);
		blocks.recalcLimits();
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		int add = Math.min(this.getCapacity()-this.getCurrentFluidLevel(), resource.amount);
		if (add > 0) {
			FluidStack fs = new FluidStack(resource.getFluid(), add);
			return tank.fill(fs, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return this.canDrain(from, resource.getFluid()) ? tank.drain(resource.amount, doDrain) : null;
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
		return ReikaFluidHelper.isFluidDrainableFromTank(fluid, tank);
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
		int base = Math.min((size+3)*(size+3), 500000);
		int lin = base*FACTOR;
		double fac = Math.pow(1.006, size-1);
		if (capacityIncreaseFactor > 1) {
			fac *= capacityIncreaseFactor;
		}
		int bucket = (int)Math.min(2000000D, (lin*fac/1000D));
		int rnd = 1;
		if (bucket > 100)
			rnd = 10;
		if (bucket > 1000)
			rnd = 100;
		if (bucket > 10000)
			rnd = 1000;
		if (rnd > 1)
			bucket = ReikaMathLibrary.roundUpToX(rnd, bucket);
		return Math.min(bucket*1000, MAXCAPACITY);
	}

	@SideOnly(Side.CLIENT)
	public BlockArray getBlocks() {
		return blocks;//.copy();
	}

	public void addCoordinate(int x, int y, int z, int meta) {
		if (blocks.addBlockCoordinate(x, y, z)) {
			size += meta == 2 ? 2 : 1;

			if (worldObj.isRemote)
				lighting.setArray(blocks);
		}
		this.updateBoostFactor();
	}

	public void removeCoordinate(int x, int y, int z, int meta) {
		if (blocks.hasBlock(x, y, z)) {
			blocks.remove(x, y, z);
			size -= meta == 2 ? 2 : 1;

			if (worldObj.isRemote)
				lighting.setArray(blocks);
		}
		this.updateBoostFactor();
		if (!capacityUpdatePaused)
			this.applyTankCapacity();
	}

	public void applyTankCapacity() {
		if (tank.getLevel() > this.getCapacity())
			tank.setContents(this.getCapacity(), fluidType);
	}

	public void pauseCapacityUpdate() {
		capacityUpdatePaused = true;
	}

	public void unpauseCapacityUpdate() {
		capacityUpdatePaused = false;
		this.applyTankCapacity();
	}

	@Override
	public void onAdjacentUpdate(World world, int x, int y, int z, Block b) {
		this.updateBoostFactor();
	}

	private void updateBoostFactor() {
		int lvl = TileEntityAdjacencyUpgrade.getAdjacentUpgrade(this, CrystalElement.CYAN);
		capacityIncreaseFactor = lvl <= 0 ? 0 : TileEntityTankCapacityUpgrade.getCapacityFactor(lvl-1);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		tank.writeToNBT(NBT);
		NBT.setInteger("size", size);

		blocks.writeToNBT("blocks", NBT);

		NBT.setDouble("factor", capacityIncreaseFactor);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		tank.readFromNBT(NBT);
		size = NBT.getInteger("size");

		blocks.readFromNBT("blocks", NBT);
		blocks.addBlockCoordinate(xCoord, yCoord, zCoord);

		capacityIncreaseFactor = NBT.getDouble("factor");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);
	}

	@SideOnly(Side.CLIENT)
	public double getFillLevelForY(int y) {
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4))
			return 1;
		int height = blocks.getSizeY();
		int min = blocks.getMinY();
		int max = blocks.getMaxY();
		double per = this.getFillPercentage();
		int fy = (int)(min+height*per);
		boolean flip = this.isInvertedFilled();
		//ReikaJavaLibrary.pConsole(per+":"+fy);
		if (y < fy)
			return flip ? 0 : 1;
		else if (y > fy)
			return flip ? 1 : 0;
		else {
			double fracfull = (fy-min);
			double ret = per*height-fracfull;
			return flip ? 1-ret : ret;
		}
	}

	@SideOnly(Side.CLIENT)
	public double getHeightOffsetAtCorner(int x, int y, int z, int dx, int dz, double h, float ptick) {
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD1))
			return 0;
		if (h == 1 || h == 0)
			return 0;
		Fluid f = fluidType;
		FluidStack fs = tank.getFluid();
		int visc = f.getViscosity(fs);
		double idx = 4D*(this.getTicksExisted()+ptick)+48*((x+z+(dx+dz)*0.5)%16);
		double idx2 = 4D*(this.getTicksExisted()+ptick)+128*((x+z+(dx+dz)*0.5)%32);
		double pow = visc < 1000 ? 0.5 : 0.5;
		double fac = Math.pow(1000D/visc, pow);
		if (f.isGaseous(fs)) {
			fac *= 0.75;
			//idx2 = 0;
		}
		idx *= fac;
		idx2 *= fac;
		double offset = 0.075+0.05*Math.sin(Math.toRadians(idx))+0.05*Math.sin(Math.toRadians(idx2));
		if (f.isGaseous(fs))
			offset *= 1.25;
		if (this.isInvertedFilled()) {
			if (!blocks.hasBlock(x, y-1, z)) {
				offset = -offset;
				offset = Math.max(-h+0.005, offset);
			}
		}
		else {
			if (!blocks.hasBlock(x, y+1, z)) {
				offset = Math.min(offset, 1-h);
			}
		}
		offset *= Math.min(1, 8F*tank.getLevel()/this.getCapacity());
		return offset;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return blocks.asAABB();
	}

	public double getFillPercentage() {
		return (double)tank.getLevel()/this.getCapacity();
	}

	@Override
	public void onTankChangeFluidType(String tank, Fluid from, Fluid to) {
		this.scheduleUpdate();
	}

	public boolean isEmpty() {
		return tank.isEmpty();
	}

	@Override
	public void breakBlock() {
		for (int i = 0; i < blocks.getSize(); i++) {
			Coordinate c = blocks.getNthBlock(i);
			int dx = c.xCoord;
			int dy = c.yCoord;
			int dz = c.zCoord;
			TileEntity te = worldObj.getTileEntity(dx, dy, dz);
			if (te instanceof CrystalTankAuxTile)
				((CrystalTankAuxTile)te).reset();
		}
	}

	@Override
	public final int getRedstoneOverride() {
		return (int)Math.round(15*this.getFillPercentage());
	}

	@Override
	public CrystalTank getController() {
		return this;
	}

	@Override
	public boolean isController() {
		return true;
	}

	@Override
	public Fluid getCurrentFluid() {
		return fluidType;
	}

	@Override
	public int getCurrentFluidLevel() {
		return tank.getLevel();
	}

	@Override
	public int addFluid(Fluid f, int amt) {
		if (!tank.isEmpty() && tank.getActualFluid() != f)
			return 0;
		int add = Math.min(amt, this.getCapacity()-tank.getLevel());
		if (add > 0)
			tank.addLiquid(add, f);
		return add;
	}

}

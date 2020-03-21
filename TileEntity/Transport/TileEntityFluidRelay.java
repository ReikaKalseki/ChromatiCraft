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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.FluidNetwork;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CustomHitbox;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Effects.EntityFluidFX;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Interfaces.TileEntity.SidePlacedTile;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityFluidRelay extends TileEntityChromaticBase implements BreakAction, SidePlacedTile, CustomHitbox {

	private final StepTimer cacheTimer = new StepTimer(100);

	public static final int RELAY_RANGE = 16;
	public static final int MAX_THROUGHPUT = 2000;

	private final HashSet<Coordinate> connections = new HashSet();

	private FluidNetwork network;
	private ForgeDirection facing;

	private final HashSet<Fluid> fluidTypes = new HashSet();
	private final Fluid[] fluidAccess = new Fluid[7];

	private int basePressure;
	private int pressureFunction;

	private final StepTimer pressureUpdate = new StepTimer(8);
	private int lastPressure;
	private int currentPressure;

	public boolean autoFilter = false;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			cacheTimer.setCap(connections.isEmpty() ? 100 : 500);
			cacheTimer.update();
		}
		if (cacheTimer.checkCap() || this.getTicksExisted() == 0) {
			this.scanAndCache(world, x, y, z);
		}

		if (!world.isRemote) {
			lastPressure = currentPressure;
			currentPressure = this.getBasePressure()+this.getDynamicPressure();
			if (this.getFunctionPressure() != 0) {
				pressureUpdate.update();
				if (pressureUpdate.checkCap()) {
					if (currentPressure != lastPressure) {
						network.updateState(this);
					}
				}
			}
			if (autoFilter) {
				if (this.getTicksExisted()%10 == 0) {
					this.copyFilters();
				}
			}
			network.update(world);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		if (!world.isRemote) {
			network = new FluidNetwork();
			network.add(this);
		}
	}

	public ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.DOWN;
	}

	private void scanAndCache(World world, int x, int y, int z) {
		connections.clear();
		int r = RELAY_RANGE;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				for (int j = -r; j <= r; j++) {
					if (i != 0 || k != 0 || j != 0) {
						int dx = x+i;
						int dy = y+j;
						int dz = z+k;
						if (ChromaTiles.getTile(world, dx, dy, dz) == this.getTile() && ReikaMathLibrary.py3d(i, j, k) <= r) {
							connections.add(new Coordinate(dx, dy, dz));
							TileEntityFluidRelay te = (TileEntityFluidRelay)world.getTileEntity(dx, dy, dz);
							te.connections.add(new Coordinate(this));
							if (!world.isRemote) {
								network.merge(te.network, world);
							}
						}
					}
				}
			}
		}
	}

	public Collection<Coordinate> getConnections() {
		return Collections.unmodifiableCollection(connections);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FLUIDRELAY;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("face", this.getFacing().ordinal());
		NBT.setInteger("press", basePressure);
		NBT.setInteger("func", pressureFunction);

		NBT.setBoolean("auto", autoFilter);

		NBTTagList li = new NBTTagList();
		for (int i = 0; i < fluidAccess.length; i++) {
			Fluid f = fluidAccess[i];
			String s = f != null ? f.getName() : "empty";
			li.appendTag(new NBTTagString(s));
		}
		NBT.setTag("fluids", li);

		li = new NBTTagList();
		for (Coordinate c : connections) {
			NBTTagCompound tag = c.writeToTag();
			li.appendTag(tag);
		}
		NBT.setTag("connections", li);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		basePressure = NBT.getInteger("press");
		pressureFunction = NBT.getInteger("func");
		facing = dirs[NBT.getInteger("face")];

		autoFilter = NBT.getBoolean("auto");

		fluidTypes.clear();
		NBTTagList li = NBT.getTagList("fluids", NBTTypes.STRING.ID);
		for (int i = 0; i < li.tagList.size(); i++) {
			String s = li.getStringTagAt(i);
			Fluid f = FluidRegistry.getFluid(s);
			fluidAccess[i] = f;
			if (f != null)
				fluidTypes.add(f);
		}

		connections.clear();
		li = NBT.getTagList("connections", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			Coordinate c = Coordinate.readTag((NBTTagCompound)o);
			connections.add(c);
		}
	}

	public int getBasePressure() {
		return basePressure;
	}

	public int getFunctionPressure() {
		return pressureFunction;
	}

	private int getDynamicPressure() {
		IFluidHandler ifl = this.getTank();
		if (ifl == null)
			return 0;
		FluidTankInfo[] info = ifl.getTankInfo(this.getFacing().getOpposite());
		int sum = 0;
		for (int i = 0; i < info.length; i++) {
			if (info[i] != null && info[i].fluid != null) {
				if (fluidTypes.contains(info[i].fluid.getFluid())) {
					sum += info[i].fluid.amount;
				}
			}
		}
		return this.getFunctionPressure()*sum/1000;
	}

	private void renderFluid(Fluid f) {
		if (!worldObj.isRemote) {
			ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.RELAYFLUID.ordinal(), this, 24, f.getID());
		}
	}

	@SideOnly(Side.CLIENT)
	public void sendFluidParticles(World world, int x, int y, int z, Fluid f) {
		double d = 0.0625;
		double px = x+0.5+d*this.getFacing().offsetX;
		double py = y+0.5+d*this.getFacing().offsetY;
		double pz = z+0.5+d*this.getFacing().offsetZ;

		for (Coordinate c : connections) {
			TileEntityFluidRelay te2 = (TileEntityFluidRelay)c.getTileEntity(world);
			double x2 = c.xCoord+0.5+d*te2.getFacing().offsetX;
			double y2 = c.yCoord+0.5+d*te2.getFacing().offsetY;
			double z2 = c.zCoord+0.5+d*te2.getFacing().offsetZ;

			double dx = x2-px;
			double dy = y2-py;
			double dz = z2-pz;

			double dd = ReikaMathLibrary.py3d(dx, dy, dz);
			double v = ReikaRandomHelper.getRandomBetween(0.0625, 0.25);
			double vx = v*dx/dd;
			double vy = v*dy/dd;
			double vz = v*dz/dd;

			double dv = 0.03125/2;

			vx = ReikaRandomHelper.getRandomPlusMinus(vx, dv);
			vy = ReikaRandomHelper.getRandomPlusMinus(vy, dv);
			vz = ReikaRandomHelper.getRandomPlusMinus(vz, dv);

			EntityFluidFX fx = new EntityFluidFX(world, px, py, pz, vx, vy, vz, f);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public void pushFluids() {
		if (network == null)
			return;
		IFluidHandler ifl = this.getTank();
		if (ifl == null)
			return;
		for (Fluid f : fluidTypes) {
			FluidStack max = ifl.drain(this.getFacing().getOpposite(), new FluidStack(f, Integer.MAX_VALUE), false);
			if (max != null) {
				int ret = network.push(worldObj, f, Math.min(max.amount, this.getThroughput()));
				if (ret > 0) {
					ifl.drain(this.getFacing().getOpposite(), new FluidStack(f, ret), true);
					this.renderFluid(f);
				}
			}
		}
	}

	public void suckFluids() {
		if (network == null)
			return;
		IFluidHandler ifl = this.getTank();
		if (ifl == null)
			return;
		for (Fluid f : fluidTypes) {
			int max = ifl.fill(this.getFacing().getOpposite(), new FluidStack(f, Integer.MAX_VALUE), false);
			if (max > 0) {
				max = Math.min(this.getThroughput(), max);
				int ret = network.suck(worldObj, f, max);
				if (ret > 0) {
					ifl.fill(this.getFacing().getOpposite(), new FluidStack(f, ret), true);
					this.renderFluid(f);
				}
			}
		}
	}

	public Fluid[] getFluidTypes() {
		return Arrays.copyOf(fluidAccess, fluidAccess.length);
	}

	public IFluidHandler getTank() {
		TileEntity te = this.getAdjacentTileEntity(this.getFacing());
		return te instanceof IFluidHandler ? (IFluidHandler)te : null;
	}

	public int pushFluid(Fluid f, int amt) {
		if (!fluidTypes.contains(f))
			return 0;
		IFluidHandler ifl = this.getTank();
		if (ifl != null) {
			int ret = ifl.fill(this.getFacing().getOpposite(), new FluidStack(f, amt), true);
			if (ret > 0)
				this.renderFluid(f);
			return ret;
		}
		else {
			return 0;
		}
	}

	public int suckFluid(Fluid f, int amt) {
		if (!fluidTypes.contains(f))
			return 0;
		IFluidHandler ifl = this.getTank();
		if (ifl != null) {
			FluidStack ret = ifl.drain(this.getFacing().getOpposite(), new FluidStack(f, amt), true);
			if (ret != null)
				this.renderFluid(f);
			return ret != null ? ret.amount : 0;
		}
		else {
			return 0;
		}
	}

	@Override
	public void breakBlock() {
		if (network != null)
			network.remove(this);
	}

	@Override
	public void placeOnSide(int s) {
		facing = dirs[s].getOpposite();
	}

	@Override
	public boolean checkLocationValidity() {
		ForgeDirection dir = this.getFacing();
		Block b = worldObj.getBlock(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
		if (b.getMaterial().isSolid()) {
			b.setBlockBoundsBasedOnState(worldObj, xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
			switch(dir.getOpposite()) {
				case DOWN:
					return b.getBlockBoundsMinY() == 0;
				case UP:
					return b.getBlockBoundsMaxY() == 1;
				case WEST:
					return b.getBlockBoundsMinX() == 0;
				case EAST:
					return b.getBlockBoundsMaxX() == 1;
				case NORTH:
					return b.getBlockBoundsMinZ() == 0;
				case SOUTH:
					return b.getBlockBoundsMaxZ() == 1;
				default:
					break;
			}
		}
		return false;
	}

	@Override
	public void drop() {
		ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, this.getTile().getCraftedProduct());
		this.delete();
	}

	@Override
	public AxisAlignedBB getHitbox() {
		ForgeDirection dir = this.getFacing();
		double d = 0.3125;
		return ReikaAABBHelper.getBlockAABB(this).contract(d, d, d).offset(dir.offsetX*d, dir.offsetY*d, dir.offsetZ*d);
	}

	public void assignNetwork(FluidNetwork net) {
		network = net;
	}

	public int getThroughput() {
		return Math.min(MAX_THROUGHPUT, 200+Math.abs(4*this.getCurrentPressure()));
	}

	public int getCurrentPressure() {
		return currentPressure;
	}

	public void changeBasePressure(int dp) {
		basePressure += dp;
		currentPressure = this.getBasePressure()+this.getDynamicPressure();
		network.updateState(this);
	}

	public void changeFunctionPressure(int dp) {
		pressureFunction += dp;
		currentPressure = this.getBasePressure()+this.getDynamicPressure();
		network.updateState(this);
	}

	public void clearFilters() {
		for (int i = 0; i < 7; i++) {
			fluidAccess[i] = null;
		}
		this.rebuildFluidLists();
	}

	public void copyFilters() {
		for (int i = 0; i < 7; i++) {
			fluidAccess[i] = null;
		}
		IFluidHandler ifl = this.getTank();
		if (ifl != null) {
			int idx = 0;
			FluidTankInfo[] info = ifl.getTankInfo(this.getFacing().getOpposite());
			for (int i = 0; i < info.length; i++) {
				if (info[i] != null && info[i].fluid != null) {
					fluidAccess[idx] = info[i].fluid.getFluid();
					idx++;
					if (idx >= 7)
						break;
				}
			}
		}
		this.rebuildFluidLists();
	}

	public void setFluid(int slot, Fluid f) {
		if (fluidAccess[slot] != f) {
			fluidAccess[slot] = f;
			this.rebuildFluidLists();
		}
	}

	private void rebuildFluidLists() {
		HashSet<Fluid> old = new HashSet(fluidTypes);
		fluidTypes.clear();
		for (int i = 0; i < fluidAccess.length; i++) {
			Fluid in = fluidAccess[i];
			if (in != null)
				fluidTypes.add(in);
		}
		if (network != null && !old.equals(fluidTypes)) {
			currentPressure = this.getBasePressure()+this.getDynamicPressure();
			network.updateState(this);
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return ReikaAABBHelper.getBlockAABB(this).expand(RELAY_RANGE, RELAY_RANGE, RELAY_RANGE);
	}

}

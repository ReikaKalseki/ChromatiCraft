/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Acquisition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Base.TileEntity.ChargedCrystalPowered;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityRangeBoost;
import Reika.DragonAPI.Auxiliary.ChunkManager;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.CountMap;
import Reika.DragonAPI.Interfaces.TileEntity.ChunkLoadingTile;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class TileEntityTeleportationPump extends ChargedCrystalPowered implements IFluidHandler, OwnedTile, ChunkLoadingTile {

	public static final int MAXRANGE = 256;

	private final HybridTank tank = new HybridTank("telepump", 4000);
	private HashMap<Fluid, ArrayList<FluidSource>> fluids = new HashMap();
	private CountMap<Fluid> counts = new CountMap();

	private Fluid selected = null;

	private boolean scanning = true;
	private boolean fastscan = false;
	private int scanY = 0;

	private int range = MAXRANGE;

	private static final ElementTagCompound required = new ElementTagCompound();

	private static final HashMap<Fluid, ProgressStage> liquidProgress = new HashMap();

	private static final Comparator comparator = new PosComparator();

	private static class PosComparator implements Comparator<FluidSource> {

		private PosComparator() {

		}

		@Override
		public int compare(FluidSource o1, FluidSource o2) {
			return o2.location.yCoord-o1.location.yCoord; //higher first
		}

	}

	static {
		required.addTag(CrystalElement.CYAN, 250);
		required.addTag(CrystalElement.BLACK, 100);
		required.addTag(CrystalElement.LIME, 500);
	}

	public static void buildProgressionMap() {
		liquidProgress.put(FluidRegistry.getFluid("luma"), ProgressStage.LUMA);
	}

	public static boolean isFluidDiscovered(Fluid f, EntityPlayer ep) {
		ProgressStage p = liquidProgress.get(f);
		return p == null || p.isPlayerAtStage(ep);
	}

	@Override
	public ElementTagCompound getRequiredEnergy() {
		return required.copy();
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		this.updateRange();
	}

	@Override
	public void onAdjacentUpdate(World world, int x, int y, int z, Block b) {
		this.updateRange();
		super.onAdjacentUpdate(world, x, y, z, b);
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
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
		return false;
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
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		switch(slot) {
			case 0:
				return this.getStoredEnergy() == 0;
			case 1:
				return FluidContainerRegistry.isFilledContainer(is);
			default:
				return false;
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		switch(slot) {
			case 0:
				return ChromaItems.STORAGE.matchWith(is);
			case 1:
				return is.getItem() == Items.bucket;
			default:
				return false;
		}
	}

	public Set<Fluid> getFluids() {
		return Collections.unmodifiableSet(counts.keySet());
	}

	public int getFluidCount(Fluid f) {
		return counts.containsKey(f) ? counts.get(f) : 0;//fluids.containsKey(f) ? fluids.get(f).size() : 0;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.TELEPUMP;
	}

	public void setTargetedFluid(int fid) {
		selected = FluidRegistry.getFluid(fid);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (scanning) {
			this.onScan(world, x, y, z);
		}
		if (world.isRemote)
			return;
		if (!scanning && fluids.isEmpty() && this.getTicksExisted() == 0) {
			fastscan = true;
		}
		if (scanning || fastscan) {
			int n = 0;
			while (n == 0 || (fastscan && n < 32)) {
				int r = this.getRange();
				for (int i = -r; i <= r; i++) {
					for (int k = -r; k <= r; k++) {
						int dx = x+i;
						int dy = scanY;
						int dz = z+k;
						Block b = world.getBlock(dx, dy, dz);
						FluidStack fs = ReikaWorldHelper.getDrainableFluid(world, dx, dy, dz);
						if (fs != null && fs.amount >= FluidContainerRegistry.BUCKET_VOLUME) {
							this.addFluidBlock(dx, dy, dz, fs);
							//ReikaJavaLibrary.pConsole(f.getName()+" @ "+dx+","+dy+","+dz, Side.SERVER, f.getName().startsWith("o"));
						}
					}
				}
				scanY++;
				if (scanY > 255) {
					scanning = false;
					fastscan = false;
					Collection<ArrayList<FluidSource>> vals = fluids.values();
					for (ArrayList<FluidSource> li : vals) {
						Collections.shuffle(li);
						Collections.sort(li, comparator);
					}
				}
				n++;
			}
		}
		else {
			if (selected != null) {
				ArrayList<FluidSource> li = fluids.get(selected);
				if (li != null && !li.isEmpty()) {
					FluidSource src = li.get(0);
					if (src.matchInWorld(world)) {
						if (this.canAddFluid(src.fluid.amount, selected) && this.hasEnergy(required)) {
							tank.addLiquid(src.fluid.amount, selected);
							src.location.setBlock(world, Blocks.air);
							this.useEnergy(required.copy().scale(this.getEnergyCostScale()));
							li.remove(0);
							this.decrFluid(src.fluid.amount, selected);
						}
					}
				}

				Fluid f = tank.getActualFluid();
				if (f != null) {
					if (tank.getLevel() >= 1000) {
						this.fillBucket(f);
					}
					if (!tank.isEmpty()) {
						for (int k = 0; k < 6; k++) {
							ForgeDirection dir = dirs[k];
							TileEntity te = this.getAdjacentTileEntity(dir);
							if (te instanceof IFluidHandler) {
								IFluidHandler ifl = (IFluidHandler)te;
								int amt = ifl.fill(dir.getOpposite(), tank.getFluid(), true);
								if (amt > 0) {
									tank.removeLiquid(amt);
									if (tank.isEmpty())
										break;
								}
							}
						}
					}
				}
			}
		}
	}

	private void fillBucket(Fluid f) {
		if (inv[1] != null) {
			int amt = FluidContainerRegistry.getContainerCapacity(inv[1]);
			ItemStack is = FluidContainerRegistry.fillFluidContainer(new FluidStack(f, amt), inv[1]);
			if (is != null) {
				inv[1] = is;
				tank.removeLiquid(amt);
			}
		}
	}

	private void decrFluid(int rem, Fluid f) {
		counts.increment(f, -rem);
		//ReikaJavaLibrary.pConsole(counts.get(f));
	}

	@Override
	public boolean isPlayerAccessible(EntityPlayer var1) {
		return !scanning && super.isPlayerAccessible(var1);
	}

	private void onScan(World world, int x, int y, int z) {
		ReikaParticleHelper.PORTAL.spawnAroundBlock(world, x, y, z, 8);
	}

	public int getRange() {
		return range;
	}

	private void addFluidBlock(int x, int y, int z, FluidStack fs) {
		Fluid f = fs.getFluid();
		ArrayList<FluidSource> li = fluids.get(f);
		if (li == null) {
			li = new ArrayList();
			fluids.put(f, li);
			counts.set(f, 0);
		}
		li.add(new FluidSource(x, y, z, fs));
		counts.increment(f, fs.amount);
		//ReikaJavaLibrary.pConsole("Found "+fs.amount+" of "+f.getName()+" @ "+x+", "+y+", "+z+", now have "+counts.get(f)+" mB");
	}

	public int getLiquidScaled(int a) {
		return a * tank.getLevel() / tank.getCapacity();
	}

	public Fluid getTankFluid() {
		return tank.getActualFluid();
	}

	private boolean canAddFluid(int amt, Fluid f) {
		return tank.canTakeIn(amt);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		tank.writeToNBT(NBT);

		NBT.setBoolean("scan", scanning);
		NBT.setString("fluid", selected != null ? selected.getName() : "null");

		NBTTagCompound tag = new NBTTagCompound();
		for (Fluid f : counts.keySet()) {
			String s = f.getName();
			int c = counts.get(f);
			tag.setInteger(s, c);
		}
		NBT.setTag("counts", tag);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		tank.readFromNBT(NBT);

		scanning = NBT.getBoolean("scan");
		String fid = NBT.hasKey("fluid") ? NBT.getString("fluid") : null;
		selected = fid == null || fid.equals("null") ? null : FluidRegistry.getFluid(fid);

		NBTTagCompound tag = NBT.getCompoundTag("counts");
		for (Object o : tag.func_150296_c()) {
			String s = (String)o;
			Fluid f = FluidRegistry.getFluid(s);
			if (f != null) {
				counts.set(f, tag.getInteger(s));
			}
		}
		//ReikaJavaLibrary.pConsole(counts);
	}

	@Override
	public float getCostModifier() {
		return 1;
	}

	@Override
	public boolean usesColor(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	public void breakBlock() {
		ChunkManager.instance.unloadChunks(this);
	}

	@Override
	public Collection<ChunkCoordIntPair> getChunksToLoad() {
		return ChunkManager.getChunkSquare(xCoord, zCoord, range >> 4);
	}

	private static class FluidSource {

		private final Coordinate location;
		private final FluidStack fluid;

		private FluidSource(int x, int y, int z, FluidStack fs) {
			location = new Coordinate(x, y, z);
			fluid = fs;
		}

		public boolean matchInWorld(World world) {
			return FluidRegistry.lookupFluidForBlock(location.getBlock(world)) == fluid.getFluid();
		}

	}

	private void updateRange() {
		int oldrange = range;
		double r = 1;
		int val = TileEntityAdjacencyUpgrade.getAdjacentUpgrade(this, CrystalElement.LIME);
		if (val > 0)
			r = TileEntityRangeBoost.getFactor(val-1);
		range = (int)(MAXRANGE*r);
		if (scanning && oldrange != range) {
			scanY = 0;
			fluids.clear();
			counts.clear();
		}
	}

}

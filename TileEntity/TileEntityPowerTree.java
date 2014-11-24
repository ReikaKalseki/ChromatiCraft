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

import java.util.ArrayList;
import java.util.EnumMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Block.BlockPowerTree.TileEntityPowerTreeAux;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalBattery;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.BlockVector;
import Reika.DragonAPI.Instantiable.Data.Coordinate;
import Reika.DragonAPI.Instantiable.Data.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class TileEntityPowerTree extends CrystalReceiverBase implements CrystalBattery {

	private static final EnumMap<CrystalElement, BlockVector> origins = new EnumMap(CrystalElement.class);
	private static final EnumMap<CrystalElement, ArrayList<Coordinate>> locations = new EnumMap(CrystalElement.class);
	private static final ArrayList<Integer> directions = new ArrayList();

	private final EnumMap<CrystalElement, Integer> growth = new EnumMap(CrystalElement.class);
	private final EnumMap<CrystalElement, Integer> steps = new EnumMap(CrystalElement.class);

	private ArrayList<CrystalTarget> targets = new ArrayList(); //need to reset some way

	private boolean hasMultiblock = false;

	static {
		origins.put(CrystalElement.WHITE, new BlockVector(ForgeDirection.NORTH, 1, -3, -2));
		origins.put(CrystalElement.BLACK, new BlockVector(ForgeDirection.NORTH, 1, -9, -2));
		origins.put(CrystalElement.RED, new BlockVector(ForgeDirection.EAST, 2, -9, 0));
		origins.put(CrystalElement.GREEN, new BlockVector(ForgeDirection.SOUTH, 0, -5, 1));
		origins.put(CrystalElement.BROWN, new BlockVector(ForgeDirection.WEST, -1, -7, -1));
		origins.put(CrystalElement.BLUE, new BlockVector(ForgeDirection.EAST, 2, -3, 0));
		origins.put(CrystalElement.PURPLE, new BlockVector(ForgeDirection.EAST, 2, -5, 0));
		origins.put(CrystalElement.CYAN, new BlockVector(ForgeDirection.SOUTH, 0, -3, 1));
		origins.put(CrystalElement.LIGHTGRAY, new BlockVector(ForgeDirection.NORTH, 1, -5, -2));
		origins.put(CrystalElement.GRAY, new BlockVector(ForgeDirection.NORTH, 1, -7, -2));
		origins.put(CrystalElement.PINK, new BlockVector(ForgeDirection.WEST, -1, -5, -1));
		origins.put(CrystalElement.LIME, new BlockVector(ForgeDirection.SOUTH, 0, -7, 1));
		origins.put(CrystalElement.YELLOW, new BlockVector(ForgeDirection.SOUTH, 0, -9, 1));
		origins.put(CrystalElement.LIGHTBLUE, new BlockVector(ForgeDirection.WEST, -1, -3, -1));
		origins.put(CrystalElement.MAGENTA, new BlockVector(ForgeDirection.EAST, 2, -7, 0));
		origins.put(CrystalElement.ORANGE, new BlockVector(ForgeDirection.WEST, -1, -9, -1));

		directions.add(0);
		directions.add(0);
		directions.add(0);
		directions.add(0);
		directions.add(0);
		directions.add(0);
		directions.add(0);
		directions.add(0);
		directions.add(90); //grows rightward
		directions.add(0);
		directions.add(90);
		directions.add(0);
		directions.add(0);
		directions.add(90); //grows rightward

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];

			BlockVector bv = origins.get(e);
			ForgeDirection dir = bv.direction;
			ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
			int x = bv.xCoord;
			int y = bv.yCoord;
			int z = bv.zCoord;
			addLeaf(e, x, y, z);

			x += left.offsetX;
			y += left.offsetY;
			z += left.offsetZ;
			addLeaf(e, x, y, z);

			x -= left.offsetX;
			y -= left.offsetY;
			z -= left.offsetZ;
			y++;
			addLeaf(e, x, y, z);

			x += left.offsetX;
			y += left.offsetY;
			z += left.offsetZ;
			addLeaf(e, x, y, z);

			x = bv.xCoord;
			y = bv.yCoord;
			z = bv.zCoord;
			x += dir.offsetX;
			y += dir.offsetY;
			z += dir.offsetZ;
			addLeaf(e, x, y, z);

			x += left.offsetX;
			y += left.offsetY;
			z += left.offsetZ;
			addLeaf(e, x, y, z);

			x -= left.offsetX;
			y -= left.offsetY;
			z -= left.offsetZ;
			y++;
			addLeaf(e, x, y, z);

			x += left.offsetX;
			y += left.offsetY;
			z += left.offsetZ;
			addLeaf(e, x, y, z);

			x = bv.xCoord;
			y = bv.yCoord;
			z = bv.zCoord;
			x -= left.offsetX;
			y -= left.offsetY;
			z -= left.offsetZ;
			addLeaf(e, x, y, z);

			x += dir.offsetX;
			y += dir.offsetY;
			z += dir.offsetZ;
			addLeaf(e, x, y, z);

			x -= dir.offsetX;
			y -= dir.offsetY;
			z -= dir.offsetZ;
			y++;
			addLeaf(e, x, y, z);

			x = bv.xCoord;
			y = bv.yCoord;
			z = bv.zCoord;
			x += dir.offsetX*2;
			y += dir.offsetY*2;
			z += dir.offsetZ*2;
			addLeaf(e, x, y, z);

			x += left.offsetX;
			y += left.offsetY;
			z += left.offsetZ;
			addLeaf(e, x, y, z);

			x = bv.xCoord;
			y = bv.yCoord;
			z = bv.zCoord;
			x -= left.offsetX*2;
			y -= left.offsetY*2;
			z -= left.offsetZ*2;
			addLeaf(e, x, y, z);
		}
	}

	public TileEntityPowerTree() {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			growth.put(e, 0);
		}
	}

	private static void addLeaf(CrystalElement e, int x, int y, int z) {
		ArrayList<Coordinate> li = locations.get(e);
		if (li == null) {
			li = new ArrayList();
			locations.put(e, li);
		}
		li.add(new Coordinate(x, y, z));
	}

	public static ForgeDirection getDirection(CrystalElement e) {
		return origins.get(e).direction;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (hasMultiblock && !world.isRemote && this.canConduct()) {
			if (rand.nextInt(150) == 0)
				this.grow();

			if (rand.nextInt(100) == 0) {
				for (int i = 0; i < CrystalElement.elements.length; i++) {
					CrystalElement e = CrystalElement.elements[i];
					if (this.getRemainingSpace(e) > 0) {
						this.requestEnergy(e, this.getRemainingSpace(e));
					}
				}
			}
		}
	}

	@Override
	public void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		if (!world.isRemote)
			this.validateStructure();
		targets.clear();
	}

	public void validateStructure() {
		FilledBlockArray f = ChromaStructures.getTreeStructure(worldObj, xCoord, yCoord, zCoord);
		boolean flag = f.matchInWorld();
		if (!flag && hasMultiblock) {
			this.onBreakMultiblock();
		}
		hasMultiblock = flag;
		this.syncAllData(true);
	}

	public boolean hasMultiBlock() {
		return hasMultiblock;
	}

	private void onBreakMultiblock() {
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			BlockVector c = origins.get(e);
			worldObj.setBlock(xCoord+c.xCoord, yCoord+c.yCoord, zCoord+c.zCoord, Blocks.air);
		}
		energy.clear();
	}

	private void grow() {
		CrystalElement e = CrystalElement.randomElement();
		int stage = growth.get(e);
		ArrayList<Coordinate> li = locations.get(e);
		if (this.getRemainingSpace(e) == 0) {
			//ReikaJavaLibrary.pConsole(e+":"+growth.get(e)+">"+this.getMaxStorage(e));
			if (stage < li.size()) {
				Coordinate c = li.get(stage).offset(xCoord, yCoord, zCoord);
				Block b = c.getBlock(worldObj);
				//ReikaJavaLibrary.pConsole(e+": "+stage+" > "+b);
				if (b == ChromaBlocks.POWERTREE.getBlockInstance()) {
					TileEntityPowerTreeAux te = (TileEntityPowerTreeAux)c.getTileEntity(worldObj);
					if (te.grow()) {
						steps.put(e, te.getGrowth());
					}
					else {
						stage++;
						growth.put(e, stage);
						steps.put(e, 0);
					}
				}
				else if (ReikaWorldHelper.softBlocks(worldObj, c.xCoord, c.yCoord, c.zCoord)) {
					c.setBlock(worldObj, ChromaBlocks.POWERTREE.getBlockInstance(), e.ordinal());
					TileEntityPowerTreeAux te = (TileEntityPowerTreeAux)c.getTileEntity(worldObj);
					int dir = stage < directions.size() ? directions.get(stage) : -1;
					if (dir >= 0) {
						ForgeDirection direc = origins.get(e).direction;
						if (dir == 90) {
							direc = ReikaDirectionHelper.getRightBy90(direc);
						}
						else if (dir == -90) {
							direc = ReikaDirectionHelper.getLeftBy90(direc);
						}
						else if (dir == 180) {
							direc = direc.getOpposite();
						}
						te.setDirection(direc);
					}
					te.setOrigin(this);
					steps.put(e, 0);
				}
			}
			else {
				steps.put(e, 0);
			}
		}
		else if (stage > 0 ? energy.getValue(e) <= this.getStorageForStep(e, stage-1) : energy.getValue(e) <= 1000) {
			if (stage < li.size()) {
				Coordinate c = li.get(stage).offset(xCoord, yCoord, zCoord);
				Block b = c.getBlock(worldObj);
				//ReikaJavaLibrary.pConsole(e+": "+stage+" > "+b);
				if (b == ChromaBlocks.POWERTREE.getBlockInstance()) {
					TileEntityPowerTreeAux te = (TileEntityPowerTreeAux)c.getTileEntity(worldObj);
					if (te.ungrow()) {
						steps.put(e, te.getGrowth());
					}
					else {
						c.setBlock(worldObj, Blocks.air);
						stage--;
						growth.put(e, stage);
						steps.put(e, 0);
					}
				}
			}
			else {
				if (stage > 0)
					stage--;
				growth.put(e, stage);
			}
		}
	}

	@Override
	public void onPathBroken(CrystalElement e) {

	}

	@Override
	public int getReceiveRange() {
		return 32;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null;
	}

	@Override
	public int maxThroughput() {
		return 5000;
	}

	@Override
	public boolean canConduct() {
		return hasMultiblock && this.isOnTop();
	}

	private boolean isOnTop() {
		return worldObj.getBlock(xCoord, yCoord+1, zCoord).isAir(worldObj, xCoord, yCoord+1, zCoord);
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		if (!this.canConduct())
			return 0;
		int s = growth.get(e);
		return this.getStorageForStep(e, s);
	}

	private int getStorageForStep(CrystalElement e, int step) {
		return 1000+step*step*step*4000;
	}

	/*
	private float getSize(CrystalElement e) {
		return growth.get(e)+(float)steps.get(e)/TileEntityPowerTreeAux.MAX_GROWTH;
	}
	 */
	private int getMaxRadius() {
		return 5;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.POWERTREE;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getTransmissionStrength() {
		return 50;
	}

	@Override
	public boolean drain(CrystalElement e, int amt) {
		if (energy.containsAtLeast(e, amt)) {
			this.drainEnergy(e, amt);
			this.grow();
			return true;
		}
		return false;
	}

	@Override
	public int getSendRange() {
		return this.getReceiveRange();
	}

	@Override
	public final void addTarget(WorldLocation loc, CrystalElement e, double dx, double dy, double dz) {
		CrystalTarget tg = new CrystalTarget(loc, e, dx, dy, dz);
		if (!worldObj.isRemote) {
			if (!targets.contains(tg))
				targets.add(tg);
			this.onTargetChanged();
		}
	}

	public final void removeTarget(WorldLocation loc, CrystalElement e) {
		if (!worldObj.isRemote) {
			//ReikaJavaLibrary.pConsole(this+":"+targets.size()+":"+targets);
			targets.remove(new CrystalTarget(loc, e));
			this.onTargetChanged();
			//ReikaJavaLibrary.pConsole(this+":"+targets.size()+":"+targets);
		}
	}

	public final void clearTargets() {
		if (!worldObj.isRemote) {
			targets.clear();
			this.onTargetChanged();
		}
	}

	private void onTargetChanged() {
		this.syncAllData(true);
	}

	@Override
	public boolean needsLineOfSight() {
		return true;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasMultiblock = NBT.getBoolean("multi");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("multi", hasMultiblock);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			steps.put(e, NBT.getInteger("step"+i));
			growth.put(e, NBT.getInteger("grow"+i));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			NBT.setInteger("step"+i, steps.containsKey(e) ? steps.get(e) : 0);
			NBT.setInteger("grow"+i, growth.containsKey(e) ? growth.get(e) : 0);
		}
	}

	public void onBreakLeaf(World world, int x, int y, int z, CrystalElement e) {
		Coordinate c = new Coordinate(x-xCoord, y-yCoord, z-zCoord);
		ArrayList<Coordinate> li = locations.get(e);
		int index = li.indexOf(c);
		if (index >= 0) {
			growth.put(e, Math.min(growth.get(e), index));
			steps.put(e, 0);
			this.clamp(e);
			for (int i = index+1; i < li.size(); i++) {
				Coordinate c2 = li.get(i).offset(xCoord, yCoord, zCoord);
				ArrayList<ItemStack> items = c2.getBlock(world).getDrops(world, c2.xCoord, c2.yCoord, c2.zCoord, c2.getBlockMetadata(world), 0);
				ReikaItemHelper.dropItems(world, c2.xCoord+rand.nextDouble(), c2.yCoord+rand.nextDouble(), c2.zCoord+rand.nextDouble(), items);
				c2.setBlock(world, Blocks.air);
				ReikaSoundHelper.playBreakSound(world, c2.xCoord, c2.yCoord, c2.zCoord, Blocks.glass);
			}
		}
		else {

		}
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.LockLevel;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Block.Dimension.Structure.Locks.BlockColoredLock.TileEntityColorLock;
import Reika.ChromatiCraft.Block.Dimension.Structure.Locks.BlockLockKey;
import Reika.ChromatiCraft.Block.Dimension.Structure.Locks.BlockLockKey.LockChannel;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LockRoomConnector;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksEntrance;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksLoot;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class LocksGenerator extends DimensionStructureGenerator {

	private final ArrayList<LockLevel> genOrder = new ArrayList();

	private int[][] keyCodes = new int[BlockLockKey.LockChannel.lockList.length][16];
	private int[] gateCodes = new int[BlockLockKey.LockChannel.lockList.length];
	private int[] whiteLock = new int[BlockLockKey.LockChannel.lockList.length];

	private HashSet<Coordinate> lockCache = new HashSet();

	@Override
	public void calculate(int x, int z, Random rand) {
		posY = 40;

		this.resetColorCaches();

		ForgeDirection dir = ForgeDirection.SOUTH;//ReikaDirectionHelper.getRandomDirection(false, rand);
		this.genMaps(rand, dir);
		int len = 4+rand.nextInt(8);
		int radius = 6+rand.nextInt(5);
		this.addDynamicStructure(new LocksEntrance(this, dir, radius, len), x, z);

		int len2 = 4+rand.nextInt(6);
		//int[] lens = new int[4];
		//lens[dir.ordinal()-2] = len2; //far side
		//lens[dir.getOpposite().ordinal()-2] = len; //near side

		int d = radius+3+len;
		//ReikaJavaLibrary.pConsole("R="+enter.radius+", LEN="+len+"; pos $ ("+x+", "+(x+d)+")");
		x += d*dir.offsetX;
		z += d*dir.offsetZ;

		//new LockRoomConnector(this, lens).setWindowed().setOpenFloor(15+rand.nextInt(40)).generate(world, x, y, z);

		//x += (len+len2+7)*dir.offsetX;
		//z += (len+len2+7)*dir.offsetZ;

		new LockRoomConnector(this, 0, 0, 0, 0).setLength(dir, len2).setOpenCeiling().generate(world, x, posY, z);

		x += (len2+3)*dir.offsetX;
		z += (len2+3)*dir.offsetZ;

		Coordinate c = this.genRooms(x, posY, z, dir, rand);

		new LocksLoot(this).generate(world, c.xCoord-dir.offsetZ*4+dir.offsetX*6, c.yCoord, c.zCoord-dir.offsetX*4+dir.offsetZ*6); //6 was 7
	}

	private Coordinate genRooms(int x, int y, int z, ForgeDirection dir, Random rand) {
		int dx = x;
		int dy = y;
		int dz = z;

		ForgeDirection dir2 = dir;
		ForgeDirection turn = ReikaDirectionHelper.getLeftBy90(dir);

		int n = genOrder.size();
		for (int i = 0; i < n; i++) {
			LockLevel l = genOrder.get(i);
			LockLevel prev = i > 0 ? genOrder.get(i-1) : null;
			LockLevel next = i < n-1 ? genOrder.get(i+1) : null;
			int d1 = l.getInitialOffset();
			l.generate(world, dx+turn.offsetX*d1, dy, dz+turn.offsetZ*d1);

			int out = 2+rand.nextInt(3);

			dx += l.getEnterExitDL()*dir.offsetX+l.getEnterExitDT()*turn.offsetX;
			dz += l.getEnterExitDL()*dir.offsetZ+l.getEnterExitDT()*turn.offsetZ;

			//world.setBlock(dx, dy+10, dz, Blocks.brick_block);

			int dx2 = dx+(2+out)*dir2.offsetX;
			int dz2 = dz+(2+out)*dir2.offsetZ;
			int d = 0;//-l.getEnterExitDT();//(-l.getWidth()/2+l.getEnterExitDT());
			dx2 += turn.offsetX*d;
			dz2 += turn.offsetZ*d;
			//world.setBlock(dx2, dy+10, dz2, Blocks.clay);

			dx += dir2.offsetX*(5+out*2);
			dz += dir2.offsetZ*(5+out*2);
			//world.setBlock(dx, dy+10, dz, Blocks.brick_block);

			LockRoomConnector con = new LockRoomConnector(this, 0, 0, 0, 0);
			con.setLength(dir, out);
			con.setLength(dir.getOpposite(), out);
			if (next == null) {
				con.setLength(dir, out*3);
				dx += dir.offsetX*out*3;
				dz += dir.offsetZ*out*3;
			}
			/*
			else if (i%2 == 0 && false) {
				dir2 = dir2.getOpposite();
				turn = turn.getOpposite();

				int step = (l.getWidth()+(next != null ? next.getWidth() : 0))/2+7+out;
				dx += step*turn.offsetX;
				dz += step*turn.offsetZ;

				con.setLength(dir.getOpposite(), 0);
				con.setLength(turn, step);
			}
			 */

			con.generate(world, dx2, dy, dz2);
		}

		return new Coordinate(dx, dy, dz);
	}

	private void genMaps(Random rand, ForgeDirection dir) {
		for (int i = 0; i < BlockLockKey.LockChannel.lockList.length; i++) {
			LockLevel l = this.genNewLockLevel(i, rand);
			if (l.canGenerate()) {
				l.setDirection(dir);
				genOrder.add(l);
			}
		}
		Collections.shuffle(genOrder);
		Collections.sort(genOrder);
	}

	@Override
	protected int getCenterXOffset() {
		return 0;
	}

	@Override
	protected int getCenterZOffset() {
		return 0;
	}

	/** The number of "exit gates" for a given room */
	public final int getNumberGates(int i) {
		return BlockLockKey.LockChannel.lockList[i].numberKeys;
	}

	@Override
	protected void clearCaches() {
		genOrder.clear();
		lockCache.clear();
		this.resetColorCaches();
	}

	private void resetColorCaches() {
		int n = BlockLockKey.LockChannel.lockList.length;
		keyCodes = new int[n][16];
		for (int i = 0; i < n; i++) {
			gateCodes[i] = this.getNumberGates(i);
		}
		whiteLock = new int[n];
	}

	//Reflective
	private LockLevel genNewLockLevel(int i, Random rand) {
		try {
			LockChannel ch = LockChannel.lockList[i];
			LockLevel l = ch.genRoom(this);
			l.permute(rand);
			return l;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RegistrationException(ChromatiCraft.instance, "Could not instantiate lock level "+i+"!");
		}
	}

	@Override
	public StructureData createDataStorage() {
		return null;
	}

	public void markOpenGate(World world, int structIndex) {
		gateCodes[structIndex]--;
		genOrder.get(structIndex).isSolved = this.isGateOpen(structIndex);
		this.updateTiles(world, -1);
	}

	public void markClosedGate(World world, int structIndex) {
		gateCodes[structIndex]++;
		genOrder.get(structIndex).isSolved = false;
		this.updateTiles(world, -1);
	}

	public boolean isOpen(CrystalElement e, int structIndex) {
		return keyCodes[structIndex][e.ordinal()] > 0 || whiteLock[structIndex] > 0;
	}

	public boolean isGateOpen(int structIndex) {
		return gateCodes[structIndex] == 0;
	}

	public void openColor(CrystalElement e, World world, int structIndex) {
		//ReikaJavaLibrary.pConsole("add "+e+" @ "+structIndex);
		if (e == CrystalElement.WHITE) {
			whiteLock[structIndex]++;
		}
		else {
			keyCodes[structIndex][e.ordinal()]++;
		}
		//ReikaJavaLibrary.pConsole(Arrays.deepToString(keyCodes));
		this.updateTiles(world, -1);
	}

	public void closeColor(CrystalElement e, World world, int structIndex) {
		//ReikaJavaLibrary.pConsole("remove "+e+" @ "+structIndex);
		if (e == CrystalElement.WHITE) {
			whiteLock[structIndex]--;
		}
		else {
			keyCodes[structIndex][e.ordinal()]--;
		}
		//ReikaJavaLibrary.pConsole(Arrays.deepToString(keyCodes));
		this.updateTiles(world, -1);
	}

	public int getWhiteLock(int channel) {
		return whiteLock[channel];
	}

	public int getColorCode(int channel, CrystalElement e) {
		return keyCodes[channel][e.ordinal()];
	}

	public int getGateCode(int channel) {
		return gateCodes[channel];
	}

	public void freezeLocks(World world, int structIndex, int time) {
		this.updateTiles(world, time);
	}

	private void updateTiles(World world, int time) {
		for (Coordinate loc : lockCache) {
			TileEntityColorLock te = (TileEntityColorLock)loc.getTileEntity(world);
			if (te == null) {
				ChromatiCraft.logger.logError("Colored lock @ "+loc+" in DIM"+world.provider.dimensionId+" has no TileEntity!!");
				Block b = loc.getBlock(world);
				ReikaJavaLibrary.pConsole("Present block ID: "+Block.getIdFromBlock(b)+" = "+b.getClass());
				//loc.setBlock(world, Blocks.brick_block);
				continue;
			}
			if (time >= 0)
				te.queueTick(time);
			else
				te.recalc();
		}
	}

	public void addLock(int x, int y, int z) {
		lockCache.add(new Coordinate(x, y, z));
	}

	@Override
	public boolean hasBeenSolved(World world) {
		for (LockLevel l : genOrder) {
			if (!l.isSolved) {
				return false;
			}
		}
		return true;
	}
}

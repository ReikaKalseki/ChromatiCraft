/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.LockLevel;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockColoredLock;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockLockKey;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockLockKey.LockChannel;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LockRoomConnector;
import Reika.ChromatiCraft.World.Dimension.Structure.Locks.LocksEntrance;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class LocksGenerator extends DimensionStructureGenerator {

	private ArrayList<LockLevel> genOrder = new ArrayList();

	@Override
	public void calculate(int x, int z, CrystalElement e, Random rand) {
		BlockColoredLock.resetCaches(this);
		this.genMaps(rand);
		int y = 40;

		ForgeDirection dir = ReikaDirectionHelper.getRandomDirection(false, rand);
		int len = 4+rand.nextInt(8);
		int radius = 6+rand.nextInt(5);
		this.addDynamicStructure(new LocksEntrance(this, dir, radius, y+5, len), x, z);

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

		new LockRoomConnector(this, 0, 0, 0, 0).setLength(dir, len2).setOpenCeiling().generate(world, x, y, z);

		x += (len2+7)*dir.offsetX;
		z += (len2+7)*dir.offsetZ;

		Coordinate c = this.genRooms(x, y, z, dir, rand);
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
			l.generate(world, dx, dy, dz);

			dx += l.getEnterExitDL()+dir2.offsetX*5;
			dz += l.getEnterExitDT()+dir2.offsetZ*5;

			LockRoomConnector con = new LockRoomConnector(this, 0, 0, 0, 0);
			int out = 2+rand.nextInt(3);
			con.setLength(dir, out);
			con.setLength(dir.getOpposite(), out);
			if (i%2 == 0) {
				dir2 = dir2.getOpposite();
				turn = turn.getOpposite();

				int step = (l.getWidth()+(next != null ? next.getWidth() : 0))/2+7+out;
				dx += step*turn.offsetX;
				dz += step*turn.offsetZ;

				con.setLength(dir.getOpposite(), 0);
				con.setLength(turn, step);
			}
		}

		return new Coordinate(dx, dy, dz);
	}

	private void genMaps(Random rand) {
		for (int i = 0; i < BlockLockKey.LockChannel.lockList.length; i++) {
			LockLevel l = this.genNewLockLevel(i, rand);
			genOrder.add(l);
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
			throw new RegistrationException(ChromatiCraft.instance, "Could not instantiate lock level "+i+"!");
		}
	}
}

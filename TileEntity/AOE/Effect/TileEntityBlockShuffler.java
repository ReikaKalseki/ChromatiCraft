/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Effect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;


public class TileEntityBlockShuffler extends TileEntityAdjacencyUpgrade {

	/** Tick delays */
	private static final int[] timing = {
		600,
		100,
		40,
		20,
		8,
		4,
		2,
		1
	};

	private int tick;

	@Override
	protected boolean ticksIndividually() {
		return false;
	}

	@Override
	protected void doCollectiveTick(World world, int x, int y, int z) {
		tick++;
		if (tick > timing[this.getTier()] && !world.isRemote) {
			this.doShuffle(world, x, y, z);
			tick = 0;
		}
	}

	private void doShuffle(World world, int x, int y, int z) {
		HashMap<Coordinate, BlockKey> blocks = new HashMap();
		ArrayList<Coordinate> li = new ArrayList();
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			Coordinate c = new Coordinate(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
			BlockKey bk = c.getBlockKey(world);
			if (this.canMove(world, c.xCoord, c.yCoord, c.zCoord, bk.blockID, bk.metadata)) {
				blocks.put(c, bk);
				li.add(c);
			}
		}
		if (li.size() > 1) {
			ArrayList<Coordinate> keys = new ArrayList(blocks.keySet());
			Collections.shuffle(keys);
			Collections.shuffle(li);
			while (!li.isEmpty() && !keys.isEmpty()) {
				Coordinate from = keys.remove(0);
				BlockKey bk = blocks.get(from);
				Coordinate dest = li.remove(0);
				dest.setBlock(world, bk.blockID, bk.metadata);
				ReikaSoundHelper.playBreakSound(world, dest.xCoord, dest.yCoord, dest.zCoord, bk.blockID);
				ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.BREAKPARTICLES.ordinal(), this, 16, bk.getBlockID(), bk.metadata);
			}
		}
	}

	private boolean canMove(World world, int x, int y, int z, Block b, int meta) {
		if (b.isAir(world, x, y, z))
			return false;
		if (b.hasTileEntity(meta))
			return false;
		if (ReikaBlockHelper.isUnbreakable(world, x, y, z, b, meta, this.getPlacer()))
			return false;
		return true;
	}

	@Override
	protected boolean tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		return false;
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.GRAY;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public static double getOperationsPerSecond(int tier) {
		return 20D/timing[tier];
	}

}

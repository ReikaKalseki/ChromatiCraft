/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.ArrayList;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Block.BlockChromaTrail.TileChromaTrail;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search.PropagationCondition;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search.TerminationCondition;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;


public class ItemCaveExitFinder extends ItemChromaTool {

	private static final TerminationCondition skyFinder = new TerminationCondition() {

		@Override
		public boolean isValidTerminus(World world, int x, int y, int z) {
			return world.canBlockSeeTheSky(x, y, z);
		}

	};

	private static final PropagationCondition pathFinder = new PropagationCondition() {

		@Override
		public boolean isValidLocation(World world, int x, int y, int z) {
			Block b = world.getBlock(x, y, z);
			return b.isAir(world, x, y, z) || b == ChromaBlocks.HOVER.getBlockInstance() || b == Blocks.wooden_door || b == Blocks.iron_door || b == Blocks.iron_bars || b == ChromaBlocks.TRAIL.getBlockInstance() || ReikaBlockHelper.isLiquid(b) || ReikaWorldHelper.softBlocks(world, x, y, z) || ReikaBlockHelper.isLeaf(world, x, y, z);
		}

	};

	private static final class DownwardsFinder implements PropagationCondition {

		private final int startY;

		private DownwardsFinder(int y) {
			startY = y;
		}

		@Override
		public boolean isValidLocation(World world, int x, int y, int z) {
			return pathFinder.isValidLocation(world, x, y, z) && y <= startY;
		}

	};

	private static final class LowYFinder implements TerminationCondition {

		private final int endY;

		private LowYFinder(int y) {
			endY = y;
		}

		@Override
		public boolean isValidTerminus(World world, int x, int y, int z) {
			return y == endY;
		}

	};

	public ItemCaveExitFinder(int index) {
		super(index);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (world.isRemote)
			return is;
		int x = MathHelper.floor_double(ep.posX);
		int y = MathHelper.floor_double(ep.posY);
		int z = MathHelper.floor_double(ep.posZ);
		boolean deep = ep.isSneaking();
		if (!deep && skyFinder.isValidTerminus(world, x, y, z))
			return is;
		Search s = new Search(x, y, z);
		PropagationCondition f = deep ? new DownwardsFinder(y) : pathFinder;
		TerminationCondition t = skyFinder;
		if (deep) {
			s.limit = new BlockBox(x, y, z, x, y, z).expand(400, 256, 400);
			BlockArray b = new BlockArray();
			b.recursiveAddCallbackWithBounds(world, x, y, z, s.limit.minX, 0, s.limit.minZ, s.limit.maxX, 256, s.limit.maxZ, f);
			t = new LowYFinder(b.getMinY());
		}
		while (!s.tick(world, f, t)) {

		}
		LinkedList<Coordinate> li = s.getResult();
		if (!li.isEmpty()) {
			int n = 0;
			ArrayList<Coordinate> li2 = new ArrayList();
			for (Coordinate c : li) {
				if (n%4 == 0 && !c.equals(li.getLast())) {
					li2.add(c);
				}
				n++;
			}
			li2.add(li.getLast());
			for (int i = 0; i < li2.size(); i++) {
				Coordinate c = li2.get(i);
				Coordinate next = i < li2.size()-1 ? li2.get(i+1) : null;
				Coordinate prev = i > 0 ? li2.get(i-1) : null;
				if (c.getBlock(world).isAir(world, c.xCoord, c.yCoord, c.zCoord)) { //do not overwrite liquids, leaves, etc
					c.setBlock(world, ChromaBlocks.TRAIL.getBlockInstance());
					TileChromaTrail te = (TileChromaTrail)c.getTileEntity(world);
					if (te == null) {
						te = new TileChromaTrail();
						te.worldObj = world;
						te.xCoord = c.xCoord;
						te.yCoord = c.yCoord;
						te.zCoord = c.zCoord;
						world.setTileEntity(c.xCoord, c.yCoord, c.zCoord, te);
					}
					te.setData(prev, next);
					//ReikaJavaLibrary.pConsole(c);
				}
			}
		}
		return is;
	}

}

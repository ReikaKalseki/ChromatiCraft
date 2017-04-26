package Reika.ChromatiCraft.Items.Tools;

import java.util.ArrayList;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Block.BlockChromaTrail.TileChromaTrail;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockPath.PropagationCondition;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockPath.Search;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockPath.TerminationCondition;
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
			return b.isAir(world, x, y, z) || b == ChromaBlocks.TRAIL.getBlockInstance() || ReikaBlockHelper.isLiquid(b) || ReikaWorldHelper.softBlocks(world, x, y, z) || ReikaBlockHelper.isLeaf(world, x, y, z);
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
		if (skyFinder.isValidTerminus(world, x, y, z))
			return is;
		Search s = new Search(x, y, z);
		while (!s.tick(world, pathFinder, skyFinder)) {

		}
		LinkedList<Coordinate> li = s.getResult();
		if (!li.isEmpty()) {
			int n = 0;
			ArrayList<Coordinate> li2 = new ArrayList();
			for (Coordinate c : li) {
				if (n%4 == 0) {
					li2.add(c);
				}
				n++;
			}
			if (!li2.get(li2.size()-1).equals(li.getLast()))
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

package Reika.ChromatiCraft.TileEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.BlockVector;
import Reika.DragonAPI.Instantiable.Data.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class TileEntityPowerTree extends CrystalReceiverBase {

	private static final EnumMap<CrystalElement, BlockVector> origins = new EnumMap(CrystalElement.class);
	private static final EnumMap<CrystalElement, ArrayList<Coordinate>> locations = new EnumMap(CrystalElement.class);

	static {
		/*
		LIGHTBLUE:	[[-4, -3, -1], [-4, -3, 0], [-3, -3, -2], [-3, -3, -1], [-3, -2, -1], [-3, -3, 0], [-3, -2, 0], [-2, -3, -3], [-2, -3, -2], [-2, -2, -2], [-2, -3, -1], [-2, -2, -1], [-2, -3, 0], [-2, -2, 0]]
				MAGENTA:	[[1, -7, -1], [1, -6, -1], [1, -7, 0], [1, -6, 0], [1, -7, 1], [1, -6, 1], [1, -7, 2], [2, -7, -1], [2, -6, -1], [2, -7, 0], [2, -6, 0], [2, -7, 1], [3, -7, -1], [3, -7, 0]]
						ORANGE:		[[-4, -9, -1], [-4, -9, 0], [-3, -9, -2], [-3, -9, -1], [-3, -8, -1], [-3, -9, 0], [-3, -8, 0], [-2, -9, -3], [-2, -9, -2], [-2, -8, -2], [-2, -9, -1], [-2, -8, -1], [-2, -9, 0], [-2, -8, 0]]
								WHITE:		[[-1, -3, -4], [-1, -3, -3], [-1, -2, -3], [-1, -3, -2], [-1, -2, -2], [0, -3, -4], [0, -3, -3], [0, -2, -3], [0, -3, -2], [0, -2, -2], [1, -3, -3], [1, -3, -2], [1, -2, -2], [2, -3, -2]]
										BLACK:		[[-1, -9, -4], [-1, -9, -3], [-1, -8, -3], [-1, -9, -2], [-1, -8, -2], [0, -9, -4], [0, -9, -3], [0, -8, -3], [0, -9, -2], [0, -8, -2], [1, -9, -3], [1, -9, -2], [1, -8, -2], [2, -9, -2]]
												RED:		[[1, -9, -1], [1, -8, -1], [1, -9, 0], [1, -8, 0], [1, -9, 1], [1, -8, 1], [1, -9, 2], [2, -9, -1], [2, -8, -1], [2, -9, 0], [2, -8, 0], [2, -9, 1], [3, -9, -1], [3, -9, 0]]
														GREEN:		[[-3, -5, 1], [-2, -5, 1], [-2, -4, 1], [-2, -5, 2], [-1, -5, 1], [-1, -4, 1], [-1, -5, 2], [-1, -4, 2], [-1, -5, 3], [0, -5, 1], [0, -4, 1], [0, -5, 2], [0, -4, 2], [0, -5, 3]]
																BROWN:		[[-4, -7, -1], [-4, -7, 0], [-3, -7, -2], [-3, -7, -1], [-3, -6, -1], [-3, -7, 0], [-3, -6, 0], [-2, -7, -3], [-2, -7, -2], [-2, -6, -2], [-2, -7, -1], [-2, -6, -1], [-2, -7, 0], [-2, -6, 0]]
																		BLUE:		[[1, -3, -1], [1, -2, -1], [1, -3, 0], [1, -2, 0], [1, -3, 1], [1, -2, 1], [1, -3, 2], [2, -3, -1], [2, -2, -1], [2, -3, 0], [2, -2, 0], [2, -3, 1], [3, -3, -1], [3, -3, 0]]
																				PURPLE:		[[1, -5, -1], [1, -4, -1], [1, -5, 0], [1, -4, 0], [1, -5, 1], [1, -4, 1], [1, -5, 2], [2, -5, -1], [2, -4, -1], [2, -5, 0], [2, -4, 0], [2, -5, 1], [3, -5, -1], [3, -5, 0]]
																						CYAN:		[[-3, -3, 1], [-2, -3, 1], [-2, -2, 1], [-2, -3, 2], [-1, -3, 1], [-1, -2, 1], [-1, -3, 2], [-1, -2, 2], [-1, -3, 3], [0, -3, 1], [0, -2, 1], [0, -3, 2], [0, -2, 2], [0, -3, 3]]
																								LIGHTGRAY:	[[-1, -5, -4], [-1, -5, -3], [-1, -4, -3], [-1, -5, -2], [-1, -4, -2], [0, -5, -4], [0, -5, -3], [0, -4, -3], [0, -5, -2], [0, -4, -2], [1, -5, -3], [1, -5, -2], [1, -4, -2], [2, -5, -2]]
																										GRAY:		[[-1, -7, -4], [-1, -7, -3], [-1, -6, -3], [-1, -7, -2], [-1, -6, -2], [0, -7, -4], [0, -7, -3], [0, -6, -3], [0, -7, -2], [0, -6, -2], [1, -7, -3], [1, -7, -2], [1, -6, -2], [2, -7, -2]]
																												PINK:		[[-4, -5, -1], [-4, -5, 0], [-3, -5, -2], [-3, -5, -1], [-3, -4, -1], [-3, -5, 0], [-3, -4, 0], [-2, -5, -3], [-2, -5, -2], [-2, -4, -2], [-2, -5, -1], [-2, -4, -1], [-2, -5, 0], [-2, -4, 0]]
																														LIME:		[[-3, -7, 1], [-2, -7, 1], [-2, -6, 1], [-2, -7, 2], [-1, -7, 1], [-1, -6, 1], [-1, -7, 2], [-1, -6, 2], [-1, -7, 3], [0, -7, 1], [0, -6, 1], [0, -7, 2], [0, -6, 2], [0, -7, 3]]
																																YELLOW:		[[-3, -9, 1], [-2, -9, 1], [-2, -8, 1], [-2, -9, 2], [-1, -9, 1], [-1, -8, 1], [-1, -9, 2], [-1, -8, 2], [-1, -9, 3], [0, -9, 1], [0, -8, 1], [0, -9, 2], [0, -8, 2], [0, -9, 3]]
		 */
		//addLeaf(CrystalElement.LIGHTBLUE, -4, -3, -1);

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

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			BlockVector bv = origins.get(e);
			ForgeDirection dir = bv.direction;
			ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
			int x = bv.xCoord;
			int y = bv.yCoord;
			int z = bv.zCoord;
			addLeaf(e, x, y, z);

			x += dir.offsetX;
			y += dir.offsetY;
			z += dir.offsetZ;
			addLeaf(e, x, y, z);

			x += dir.offsetX;
			y += dir.offsetY;
			z += dir.offsetZ;
			addLeaf(e, x, y, z);

			x += left.offsetX;
			y += left.offsetY;
			z += left.offsetZ;
			addLeaf(e, x, y, z);

			x -= dir.offsetX;
			y -= dir.offsetY;
			z -= dir.offsetZ;
			addLeaf(e, x, y, z);

			x -= dir.offsetX;
			y -= dir.offsetY;
			z -= dir.offsetZ;
			addLeaf(e, x, y, z);

			x -= left.offsetX*2;
			y -= left.offsetY*2;
			z -= left.offsetZ*2;
			addLeaf(e, x, y, z);

			x -= left.offsetX;
			y -= left.offsetY;
			z -= left.offsetZ;
			addLeaf(e, x, y, z);

			x += left.offsetX;
			y += left.offsetY;
			z += left.offsetZ;
			x += dir.offsetX;
			y += dir.offsetY;
			z += dir.offsetZ;
			addLeaf(e, x, y, z);

			x = bv.xCoord;
			y = bv.yCoord+1;
			z = bv.zCoord;
			addLeaf(e, x, y, z);

			x += dir.offsetX;
			y += dir.offsetY;
			z += dir.offsetZ;
			addLeaf(e, x, y, z);

			x += left.offsetX;
			y += left.offsetY;
			z += left.offsetZ;
			addLeaf(e, x, y, z);

			x -= dir.offsetX;
			y -= dir.offsetY;
			z -= dir.offsetZ;
			addLeaf(e, x, y, z);

			x -= left.offsetX*2;
			y -= left.offsetY*2;
			z -= left.offsetZ*2;
			addLeaf(e, x, y, z);
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

		if (this.isOnTop() && rand.nextInt(50) == 0)
			this.grow();

		if (rand.nextInt(100) == 0 && false) {
			CrystalElement e = CrystalElement.randomElement();
			if (this.getFillFraction(e) < 0.8) {
				this.requestEnergy(e, this.getRemainingSpace(e));
			}
		}

		/*
		if (!world.isRemote) { // TEMPORARY, to test tree growth
			for (CrystalElement e : CrystalElement.elements) {
				int tick = this.getTicksExisted()/20;
				ArrayList<Coordinate> li = locations.get(e);
				int step = tick%(li.size()+1);
				if (step == li.size()) {
					for (Coordinate c : li) {
						world.setBlockToAir(x+c.xCoord, y+c.yCoord, z+c.zCoord);
					}
				}
				else {
					Coordinate c = li.get(step);
					world.setBlock(x+c.xCoord, y+c.yCoord, z+c.zCoord, ChromaBlocks.POWERTREE.getBlockInstance(), e.ordinal(), 3);
				}
			}
		}*/

	}

	public static class CoordinateSorter implements Comparator<Coordinate> {

		@Override
		public int compare(Coordinate o1, Coordinate o2) {
			return Math.abs(o1.xCoord-o2.xCoord)+Math.abs(o1.zCoord-o2.zCoord);
		}

	}

	private void grow() {
		CrystalElement e = CrystalElement.randomElement();
		if (this.getRemainingSpace(e) == 0) {

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
		return this.isOnTop();
	}

	private boolean isOnTop() {
		return false;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		int s = this.getSize(e);
		return 1000+s*s*s*4000;
	}

	private int getSize(CrystalElement e) {
		BlockVector bv = origins.get(e);
		int x = xCoord+bv.xCoord;
		int y = yCoord+bv.yCoord;
		int z = zCoord+bv.zCoord;
		int c = 0;
		int r = this.getMaxRadius();
		for (int i = 1; i < r; i++) {
			int dx = x+i*bv.xCoord;
			int dy = y+i*bv.yCoord;
			int dz = z+i*bv.zCoord;
			Block b = worldObj.getBlock(dx, dy, dz);
			int meta = worldObj.getBlockMetadata(dx, dy, dz);
			if (b == ChromaBlocks.POWERTREE.getBlockInstance() && meta == e.ordinal())
				c++;
			else
				break;
		}
		return c;
	}

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

}

package Reika.ChromatiCraft.Block;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;


public class BlockFakeSky extends Block {

	private static final SkyCache skyCache = new SkyCache();

	public BlockFakeSky(Material mat) {
		super(mat);

		this.setCreativeTab(ChromatiCraft.tabChroma);
		this.setHardness(0.5F);
		this.setResistance(600000);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return meta == 1;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return meta == 1 ? new TileEntityFakeSkyController() : null;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		world.setBlockMetadataWithNotify(x, y, z, 1, 3);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldb, int oldmeta) {
		super.breakBlock(world, x, y, z, oldb, oldmeta);

		skyCache.remove(new WorldLocation(world, x, y, z));

		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			BlockArray arr = new BlockArray();
			arr.recursiveAddWithBounds(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, this, x-64, y-12, z-64, x+64, y+12, z+64);
			for (Coordinate c : arr.keySet()) {
				c.setBlockMetadata(world, 0);
			}
			if (arr.isEmpty())
				continue;
			arr.getNthBlock(0).setBlockMetadata(world, 1);
		}

		world.markBlocksDirtyVertical(x, z, 0, y);
	}

	public static void updateColumn(World world, int x, int y, int z) {
		skyCache.updateColumn(world, x, y, z);
	}

	public static boolean isForcedSky(World world, int x, int y, int z) {
		return skyCache.isForcedSky(world, x, y, z);
	}

	public static void clearCache() {
		skyCache.data.clear();
	}

	public static class TileEntityFakeSkyController extends TileEntity {

		private boolean cached = false;

		@Override
		public void updateEntity() {
			if (!cached) {
				this.addToCache();
				cached = true;
			}
		}

		private void addToCache() {
			BlockArray arr = new BlockArray();
			arr.recursiveAddWithBounds(worldObj, xCoord, yCoord, zCoord, this.getBlockType(), xCoord-64, yCoord-12, zCoord-64, xCoord+64, yCoord+12, zCoord+64);
			for (Coordinate c : arr.keySet()) {
				if (!c.equals(xCoord, yCoord, zCoord))
					c.setBlockMetadata(worldObj, 0);
				skyCache.add(c, worldObj);
			}
		}

	}

	private static class SkyCache {

		private final MultiMap<WorldLocation, SkyShunt> data = new MultiMap().setNullEmpty();

		public boolean isForcedSky(World world, int x, int y, int z) {
			WorldLocation c = new WorldLocation(world, x, 0, z);
			Collection<SkyShunt> li = data.get(c);
			if (li == null)
				return false;
			for (SkyShunt s : li) {
				if (s.includesY(y)) {
					return true;
				}
			}
			return false;
		}

		public void updateColumn(World world, int x, int y, int z) {
			WorldLocation c = new WorldLocation(world, x, 0, z);
			Collection<SkyShunt> li = data.get(c);
			if (li == null)
				return;
			for (SkyShunt s : li) {
				if (s.includesY(y)) {
					s.update(world);
				}
			}
		}

		public void remove(WorldLocation c) {
			Collection<SkyShunt> li = data.get(c.to2D());
			if (li != null) {
				Iterator<SkyShunt> it = li.iterator();
				while (it.hasNext()) {
					SkyShunt s = it.next();
					if (s.skyBlock.equals(new Coordinate(c))) {
						it.remove();
						break;
					}
				}
			}
		}

		public void add(Coordinate c, World world) {
			data.addValue(new WorldLocation(world, c.to2D()), new SkyShunt(c, world));
		}

	}

	private static class SkyShunt {

		private final Coordinate skyBlock;
		private int minY;

		private SkyShunt(Coordinate c, World world) {
			skyBlock = c;
			this.calcMinY(world);
		}

		public boolean includesY(int y) {
			return y >= minY && y < skyBlock.yCoord;
		}

		private void calcMinY(World world) {
			int y = skyBlock.yCoord-1;
			while (world.getBlock(skyBlock.xCoord, y, skyBlock.zCoord).getLightOpacity(world, skyBlock.xCoord, y, skyBlock.zCoord) == 0) {
				y--;
			}
			minY = y+1;
		}

		private void update(World world) {
			this.calcMinY(world);
		}

		@Override
		public int hashCode() {
			return skyBlock.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof SkyShunt && ((SkyShunt)o).skyBlock.equals(skyBlock);
		}

	}

}

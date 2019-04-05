package Reika.ChromatiCraft.Block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;

public class BlockDistortingEffect extends BlockContainer {

	public BlockDistortingEffect(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityDistorting();
	}

	private static boolean canReplace(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		return b.isOpaqueCube() && b.getRenderType() == 0 && !b.hasTileEntity(world.getBlockMetadata(x, y, z));
	}

	public static class TileEntityDistorting extends TileEntity {

		private BlockKey previous;

		public void tick() {

		}

		public void reset() {
			worldObj.setBlock(xCoord, yCoord, zCoord, previous.blockID, previous.metadata, 2);
		}

	}

}

package Reika.ChromatiCraft.Block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.CubePoints;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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

		public DistortedBox render;

		public void loadBlock() {
			render = new DistortedBox(BlockKey.getAt(worldObj, xCoord, yCoord, zCoord), new WorldLocation(this));
		}

		@Override
		public boolean canUpdate() {
			return false;
		}

		public void reset() {
			worldObj.setBlock(xCoord, yCoord, zCoord, previous.blockID, previous.metadata, 2);
		}

	}

	private static class DistortedBox {

		private final WorldLocation location;
		private final BlockKey block;
		private final CubePoints box;
		private final CubePoints renderBox;
		private final AxisAlignedBB bounds;

		private DistortedBox(BlockKey bk, WorldLocation loc) {
			location = loc;
			block = bk;
			box = CubePoints.fullBlock();
			renderBox = box.copy();
			renderBox.setRandomVelocities(0.3);
			double r = 0.5;
			bounds = ReikaAABBHelper.getBlockAABB(0, 0, 0).expand(r, r, r);
		}

		@SideOnly(Side.CLIENT)
		public void renderArea(Tessellator v5) {
			ReikaRenderHelper.renderBlockPieceNonCuboid(block.blockID, block.metadata, v5, renderBox);
			renderBox.applyVelocities(bounds);
			if (bounds.maxX > 1) {
				ReikaAABBHelper.compressAABB(bounds, 0.0125, 0.0125, 0.0125);
				ReikaAABBHelper.fillAABB(bounds, 0, 0, 0, 1, 1, 1);
			}
		}

	}

}

package Reika.ChromatiCraft.Block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.InWorldGui;

public class BlockInWorldGui extends Block {

	public BlockInWorldGui(Material mat) {
		super(mat);

		this.setLightOpacity(0);
		this.setBlockUnbreakable();
		this.setResistance(60000F);
		this.setStepSound(soundTypeGlass);
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityInWorldGui();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return 0;//-1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		return true;//false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		TileEntityInWorldGui te = (TileEntityInWorldGui)world.getTileEntity(x, y, z);
		float fx = -1;
		float fy = -1;
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
		if (dir.offsetY == 0) {
			fy = b;
			fx = dir.offsetX == 0 ? a : c;
			if (Math.signum(dir.offsetX+dir.offsetZ) > 0)
				fx = 1-fx;
		}
		else {
			//no valid operation
		}
		//ReikaJavaLibrary.pConsole(String.format("%.2f, %.2f, %.2f -> %.2f, %.2f", a, b, c, fx, fy));
		if (fx >= 0 && fy >= 0) {
			te.activateArea(fx, fy);
			return true;
		}
		return false;
	}

	public static final class TileEntityInWorldGui extends TileEntity {

		private InWorldGui gui;
		private float xmin; //
		private float ymin; //
		private float width; //fraction of GUI width
		private float height; //fraction of GUI height

		public void setGui(InWorldGui gui, float x, float y, float w, float h) {
			this.gui = gui;
			xmin = x;
			ymin = y;
			width = w;
			height = h;
		}

		private void activateArea(float fx, float fy) {
			if (gui != null) {
				float rx = xmin+fx*width;
				float ry = ymin+fy*height;
				int px = (int)(rx*gui.xSize);
				int py = (int)(ry*gui.ySize);
				//ReikaJavaLibrary.pConsole(px+", "+py);
				gui.click(px, py, 1);
			}
		}

	}

}

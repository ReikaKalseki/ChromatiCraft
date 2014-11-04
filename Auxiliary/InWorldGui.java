package Reika.ChromatiCraft.Auxiliary;

import Reika.ChromatiCraft.Block.BlockInWorldGui.TileEntityInWorldGui;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class InWorldGui {

	public final int xSize;
	public final int ySize;

	public InWorldGui(int x, int y) {
		xSize = x;
		ySize = y;
	}

	public void click(int x, int y, int button) {

	}

	@SideOnly(Side.CLIENT)
	public void render(TileEntityInWorldGui te, float ptick) {

	}

}

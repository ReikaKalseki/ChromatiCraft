package Reika.ChromatiCraft.Render;

import net.minecraft.tileentity.TileEntity;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.DragonAPI.Interfaces.RenderFetcher;

public class RenderCrystalFence extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "fence";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {

	}

}

package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.BlockPolyCrystal.TilePolyCrystal;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;


public class RenderPolyCrystal extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		GL11.glPushMatrix();
		GL11.glTranslated(par2-tile.xCoord, par4-tile.yCoord, par6-tile.zCoord);
		((TilePolyCrystal)tile).renderCrystal(Tessellator.instance, par8);
		GL11.glPopMatrix();
	}

}

package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelTransportWindow;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityTransportWindow;
import Reika.DragonAPI.Interfaces.RenderFetcher;

public class RenderTransportWindow extends ChromaRenderBase {

	private final ModelTransportWindow model = new ModelTransportWindow();

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		GL11.glPushMatrix();

		TileEntityTransportWindow te = (TileEntityTransportWindow)tile;

		GL11.glTranslated(par2, par4, par6);
		this.renderModel(te, model, te.isInWorld());
		if (te.hasWorldObj()) {
			this.renderTexture(te, par2, par4, par6, par8);
		}

		GL11.glPopMatrix();

	}

	private void renderTexture(TileEntity te, double par2, double par4, double par6, float par8) {
		//render end-portal-like texture through the holes, or maybe a tunnel-like render
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "window.png";
	}

}

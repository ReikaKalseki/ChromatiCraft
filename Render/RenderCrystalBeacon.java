package Reika.ChromatiCraft.Render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelCrystalBeacon;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalBeacon;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

public class RenderCrystalBeacon extends ChromaRenderBase {

	private final ModelCrystalBeacon model = new ModelCrystalBeacon();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "beacon.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityCrystalBeacon te = (TileEntityCrystalBeacon)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		this.renderModel(te, model);
		if (te.isInWorld() && MinecraftForgeClient.getRenderPass() == 1)
			this.renderGlow(te, par2, par4, par6, par8);
		GL11.glPopMatrix();
	}

	private void renderGlow(TileEntityCrystalBeacon te, double par2, double par4, double par6, float par8) {
		Tessellator v5 = Tessellator.instance;
		ReikaRenderHelper.prepareGeoDraw(true);
		double o = 0.025;
		double w = 0.125;
		/*
		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.addVertex(0.5-w-o, 1+o, 0.5+w+o);
		v5.addVertex(0.5+w+o, 1+o, 0.5+w+o);
		v5.addVertex(0.5+w+o, 1+o, 0.5-w-o);
		v5.addVertex(0.5-w-o, 1+o, 0.5-w-o);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.addVertex(0.5-w-o, 0+o, 0.5+w+o);
		v5.addVertex(0.5+w+o, 0+o, 0.5+w+o);
		v5.addVertex(0.5+w+o, 1+o, 0.5+w+o);
		v5.addVertex(0.5-w-o, 1+o, 0.5+w+o);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.addVertex(0.5-w-o, 1+o, 0.5-w-o);
		v5.addVertex(0.5+w+o, 1+o, 0.5-w-o);
		v5.addVertex(0.5+w+o, 0+o, 0.5-w-o);
		v5.addVertex(0.5-w-o, 0+o, 0.5-w-o);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.addVertex(0.5+w+o, 1+o, 0.5-w-o);
		v5.addVertex(0.5+w+o, 1+o, 0.5+w+o);
		v5.addVertex(0.5+w+o, 0+o, 0.5+w+o);
		v5.addVertex(0.5+w+o, 0+o, 0.5-w-o);
		v5.draw();

		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.addVertex(0.5-w-o, 0+o, 0.5-w-o);
		v5.addVertex(0.5-w-o, 0+o, 0.5+w+o);
		v5.addVertex(0.5-w-o, 1+o, 0.5+w+o);
		v5.addVertex(0.5-w-o, 1+o, 0.5-w-o);
		v5.draw();*/

		v5.startDrawingQuads();
		v5.setColorRGBA(255, 255, 255, 127);
		v5.addVertex(0.5-w-o, 1+o, 0.5+w+o);
		v5.addVertex(0.5+w+o, 1+o, 0.5+w+o);
		v5.addVertex(0.5+w+o, 1+o, 0.5-w-o);
		v5.addVertex(0.5-w-o, 1+o, 0.5-w-o);

		v5.addVertex(0.5-w-o, 0+o, 0.5+w+o);
		v5.addVertex(0.5+w+o, 0+o, 0.5+w+o);
		v5.addVertex(0.5+w+o, 1+o, 0.5+w+o);
		v5.addVertex(0.5-w-o, 1+o, 0.5+w+o);

		v5.addVertex(0.5-w-o, 1+o, 0.5-w-o);
		v5.addVertex(0.5+w+o, 1+o, 0.5-w-o);
		v5.addVertex(0.5+w+o, 0+o, 0.5-w-o);
		v5.addVertex(0.5-w-o, 0+o, 0.5-w-o);

		v5.addVertex(0.5+w+o, 1+o, 0.5-w-o);
		v5.addVertex(0.5+w+o, 1+o, 0.5+w+o);
		v5.addVertex(0.5+w+o, 0+o, 0.5+w+o);
		v5.addVertex(0.5+w+o, 0+o, 0.5-w-o);

		v5.addVertex(0.5-w-o, 0+o, 0.5-w-o);
		v5.addVertex(0.5-w-o, 0+o, 0.5+w+o);
		v5.addVertex(0.5-w-o, 1+o, 0.5+w+o);
		v5.addVertex(0.5-w-o, 1+o, 0.5-w-o);
		v5.draw();
		ReikaRenderHelper.exitGeoDraw();
	}

}

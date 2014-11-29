/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import java.util.Collection;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Magic.FiberPath;
import Reika.ChromatiCraft.Models.ModelFiberReceiver;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityFiberReceiver;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

public class RenderFiberReceiver extends ChromaRenderBase {

	private final ModelFiberReceiver model = new ModelFiberReceiver();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "receiver.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityFiberReceiver te = (TileEntityFiberReceiver)tile;

		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);

		GL11.glPushMatrix();
		this.renderModel(te, model, te.getColor());
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		if (MinecraftForgeClient.getRenderPass() == 1)
			this.renderPaths(te);
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}

	private void renderPaths(TileEntityFiberReceiver te) {
		Collection<FiberPath> c = te.getActivePaths();
		float w = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		Tessellator v5 = Tessellator.instance;
		for (FiberPath p : c) {
			int a = p.getAlpha();
			if (a > 0) {
				List<WorldLocation> li = p.getSteps();
				ReikaRenderHelper.prepareGeoDraw(true);
				GL11.glLineWidth(w*4);
				v5.startDrawing(GL11.GL_LINE_STRIP);
				v5.setColorRGBA_I(p.color.getColor(), a/2);
				for (WorldLocation loc : li) {
					v5.addVertex(loc.xCoord-te.xCoord+0.5, loc.yCoord-te.yCoord+0.5, loc.zCoord-te.zCoord+0.5);
				}
				//ReikaJavaLibrary.pConsole(li);
				v5.draw();

				GL11.glLineWidth(w*1.5F);
				v5.startDrawing(GL11.GL_LINE_STRIP);
				v5.setColorRGBA_I(p.color.getColor(), a);
				for (WorldLocation loc : li) {
					v5.addVertex(loc.xCoord-te.xCoord+0.5, loc.yCoord-te.yCoord+0.5, loc.zCoord-te.zCoord+0.5);
				}
				//ReikaJavaLibrary.pConsole(li);
				v5.draw();
				ReikaRenderHelper.exitGeoDraw();
			}
		}
		GL11.glLineWidth(w);
	}


}

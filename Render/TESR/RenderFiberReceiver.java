/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelFiberReceiver;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityFiberReceiver;
import Reika.DragonAPI.Interfaces.RenderFetcher;

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
		this.renderModel(te, model);
		GL11.glPopMatrix();
		
		//this.renderEdges(te);

		GL11.glPushMatrix();
		if (MinecraftForgeClient.getRenderPass() == 1)
			;//this.renderPaths(te);
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}


}

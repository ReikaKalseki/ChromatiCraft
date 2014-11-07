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

import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelReceiver;
import Reika.ChromatiCraft.TileEntity.TileEntityFiberReceiver;
import Reika.DragonAPI.Interfaces.RenderFetcher;

public class RenderReceiver extends ChromaRenderBase {

	private final ModelReceiver model = new ModelReceiver();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "receiver.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityFiberReceiver te = (TileEntityFiberReceiver)tile;

		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		this.renderModel(te, model, te.getColor());
		GL11.glPopMatrix();
	}

}

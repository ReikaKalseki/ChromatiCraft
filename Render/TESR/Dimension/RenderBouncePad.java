/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR.Dimension;

import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.Dimension.Structure.Pinball.BlockPinballTile.TileBouncePad;
import Reika.ChromatiCraft.Models.ModelPinball;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;


public class RenderBouncePad extends ChromaRenderBase {

	private final ModelPinball model = new ModelPinball();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileBouncePad te = (TileBouncePad)tile;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		this.bindTextureByName(this.getTextureFolder()+"pinball.png");
		float tick = te.lightTick;
		if (tick > 0) {
			tick -= par8;
		}
		this.renderModel(tile, model, tick);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

}

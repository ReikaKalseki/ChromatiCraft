/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.TileEntity.TileEntityHelpBlock;
import Reika.DragonAPI.Interfaces.RenderFetcher;

public class HelpBlockRenderer extends ChromaRenderBase {

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityHelpBlock te = (TileEntityHelpBlock)tile;
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_LIGHTING);
		//GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glTranslated(par2, par4, par6+1);
		te.renderGui(par8);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		//GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

}

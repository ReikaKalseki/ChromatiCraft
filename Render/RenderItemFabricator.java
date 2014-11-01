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

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelFabricator;
import Reika.ChromatiCraft.TileEntity.TileEntityItemFabricator;
import Reika.DragonAPI.Interfaces.RenderFetcher;

public class RenderItemFabricator extends ChromaRenderBase {

	private final ModelFabricator model = new ModelFabricator();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "fabricator.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityItemFabricator te = (TileEntityItemFabricator)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		this.renderModel(te, model);
		GL11.glPopMatrix();
	}

}

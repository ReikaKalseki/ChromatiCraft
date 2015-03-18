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

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelFarmer;
import Reika.ChromatiCraft.TileEntity.TileEntityFarmer;
import Reika.DragonAPI.Interfaces.RenderFetcher;

public class RenderFarmer extends ChromaRenderBase {

	private final ModelFarmer model = new ModelFarmer();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "farmer.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityFarmer te = (TileEntityFarmer)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		if (te.isInWorld()) {
			int a = 0;
			switch (te.getFacing()) {
			case EAST:
				a = 90;
				GL11.glTranslated(0, 0, 1);
				break;
			case NORTH:
				a = 180;
				GL11.glTranslated(1, 0, 1);
				break;
			case SOUTH:
				break;
			case WEST:
				a = -90;
				GL11.glTranslated(1, 0, 0);
				break;
			default:
				break;
			}
			GL11.glRotated(a, 0, 1, 0);
		}
		this.renderModel(te, model);
		GL11.glPopMatrix();
	}

}

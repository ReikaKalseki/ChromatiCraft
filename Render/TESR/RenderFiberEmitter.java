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
import Reika.ChromatiCraft.Models.ModelFiberEmitter;
import Reika.ChromatiCraft.TileEntity.TileEntityFiberTransmitter;
import Reika.DragonAPI.Interfaces.RenderFetcher;

public class RenderFiberEmitter extends ChromaRenderBase {

	private final ModelFiberEmitter model = new ModelFiberEmitter();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "emitter.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityFiberTransmitter te = (TileEntityFiberTransmitter)tile;
		if (te.hasWorldObj()) {
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);

			int rot = 0;
			int rotx = 0;

			GL11.glPushMatrix();
			int dx = 0;
			int dz = 0;
			int dy = 0;
			switch(te.getFacing()) {
			case WEST:
				rot = 270;
				dx = 1;
				break;
			case EAST:
				rot = 90;
				dz = 1;
				break;
			case NORTH:
				rot = 180;
				dx = 1;
				dz = 1;
				break;
			case SOUTH:
				rot = 0;
				break;
			case UP:
				rotx = 270;
				dz = 1;
				break;
			case DOWN:
				rotx = 90;
				dy = 1;
				break;
			default:
				break;
			}
			GL11.glTranslated(dx, dy, dz);
			GL11.glRotated(rot, 0, 1, 0);
			GL11.glRotated(rotx, 1, 0, 0);
			this.renderModel(te, model, te.getColor());

			GL11.glPopMatrix();

			GL11.glPopMatrix();
		}
		else {
			GL11.glPushMatrix();
			GL11.glTranslated(-0.5, -0.6, -0.5);
			this.renderModel(te, model);
			GL11.glPopMatrix();
		}
	}
}

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
import Reika.ChromatiCraft.Models.ModelLumenWire;
import Reika.ChromatiCraft.TileEntity.TileEntityLumenWire;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;

public class RenderLumenWire extends ChromaRenderBase {

	private ModelLumenWire model = new ModelLumenWire();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "lumenwire.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityLumenWire te = (TileEntityLumenWire)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);

		int rot = 0;
		int rotx = 0;

		GL11.glPushMatrix();

		if (!te.isInWorld()) {
			double s = 2.5;
			GL11.glScaled(s, s, s);
			GL11.glTranslated(0, 0.3125, 0);
		}

		if (te.isInWorld()) {
			int dx = 0;
			int dz = 0;
			int dy = 1;
			switch(te.getFacing().getOpposite()) {
				case EAST:
					rot = 270;
					dx = 1;
					break;
				case WEST:
					rot = 90;
					dz = 1;
					break;
				case SOUTH:
					rot = 180;
					dx = 1;
					dz = 1;
					break;
				case NORTH:
					rot = 0;
					break;
				case DOWN:
					rotx = 270;
					dy = 0;
					break;
				case UP:
					rotx = 90;
					dy = 1;
					dz = 1;
					break;
				default:
					break;
			}
			GL11.glTranslated(dx, dy, dz);
			GL11.glRotated(rot, 0, 1, 0);
			GL11.glRotated(90, 1, 0, 0);
			GL11.glRotated(rotx, 1, 0, 0);
		}
		this.renderModel(te, model);
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}

}

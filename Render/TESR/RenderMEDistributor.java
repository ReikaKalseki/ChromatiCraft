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

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.ModInterface.TileEntityMEDistributor;
import Reika.ChromatiCraft.Models.ModelMEDistributor;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;

public class RenderMEDistributor extends ChromaRenderBase {

	private final ModelMEDistributor model = new ModelMEDistributor();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "medistrib.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityMEDistributor te = (TileEntityMEDistributor) tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		if (!te.isInWorld() || MinecraftForgeClient.getRenderPass() == 0) {
			if (te.isInWorld()) {
				switch(te.getBlockMetadata()) {
				case 0:
					GL11.glTranslated(0, 1, 1);
					GL11.glRotated(180, 1, 0, 0);
					break;
				case 2:
					GL11.glTranslated(0, 0, 1);
					GL11.glRotated(-90, 1, 0, 0);
					break;
				case 3:
					GL11.glTranslated(0, 1, 0);
					GL11.glRotated(90, 1, 0, 0);
					break;
				case 4:
					GL11.glTranslated(1, 0, 0);
					GL11.glRotated(90, 0, 0, 1);
					break;
				case 5:
					GL11.glTranslated(0, 1, 0);
					GL11.glRotated(-90, 0, 0, 1);
					break;
				}
			}
			this.renderModel(te, model);
		}
		else {
			this.renderFX(te);
		}
		GL11.glPopMatrix();
	}

	private void renderFX(TileEntityMEDistributor te) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_LIGHTING);
		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		v5.setBrightness(240);
		v5.draw();
		GL11.glPopAttrib();
	}

}

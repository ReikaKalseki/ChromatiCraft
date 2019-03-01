/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.TileEntity.Decoration.TileEntityAreaHologram;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class RenderAreaHologram extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityAreaHologram te = (TileEntityAreaHologram)tile;

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)par2, (float)par4 + 1.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);

		if (tile.hasWorldObj() && !StructureRenderer.isRenderingTiles()) { //check to prevent infinite recursion issue (Droste effect)
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();
			//GL11.glFrontFace(GL11.GL_CW);
			ReikaRenderHelper.disableEntityLighting();
			BlendMode.DEFAULT.apply();//BlendMode.ADDITIVEDARK.apply();
			double s = 1/16D;
			GL11.glTranslated(0.5, -0.25, 0.5);
			GL11.glRotated(180, 1, 0, 0);
			GL11.glRotated((te.getTicksExisted()+par8)*2, 0, 1, 0);
			GL11.glTranslated(-s/2, 0, -s/2);
			GL11.glScaled(s, s, s);
			te.renderStructure(par8);
			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}

		if (te.hasWorldObj())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

}

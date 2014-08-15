/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.DragonAPI.Base.TileEntityRenderBase;
import Reika.DragonAPI.Interfaces.TextureFetcher;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public abstract class ChromaRenderBase extends TileEntityRenderBase implements TextureFetcher {

	@Override
	public final String getTextureFolder() {
		return "/Reika/ChromatiCraft/Textures/TileEntity/";
	}

	@Override
	protected Class getModClass() {
		return ChromatiCraft.class;
	}

	protected final void renderModel(TileEntityChromaticBase tile, ChromaModelBase model, Object... args) {
		this.bindTextureByName(this.getTextureFolder()+this.getImageFileName(tile));
		GL11.glPushMatrix();
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, -1.5F, -0.5F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		if (args.length > 0) {
			ArrayList li = new ArrayList();
			for (int i = 0; i < args.length; i++)
				li.add(args[i]);
			model.renderAll(tile, li);
		}
		else {
			model.renderAll(tile, null);
		}
		if (tile.isInWorld())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

}
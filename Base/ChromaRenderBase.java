/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Base.TileEntityRenderBase;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Interfaces.TextureFetcher;

public abstract class ChromaRenderBase extends TileEntityRenderBase implements TextureFetcher {

	@Override
	public final String getTextureFolder() {
		return "/Reika/ChromatiCraft/Textures/TileEntity/";
	}

	@Override
	protected Class getModClass() {
		return ChromatiCraft.class;
	}

	protected final void renderModel(TileEntity tile, ChromaModelBase model, Object... args) {
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
		if (tile.hasWorldObj())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	protected final void renderModel(TileEntityChromaticBase tile, ChromaModelBase model, Object... args) {
		this.renderModel(tile, model, this.getTextureFolder()+this.getImageFileName(tile), args);
	}

	protected final void renderModel(TileEntityChromaticBase tile, ChromaModelBase model, String tex, Object... args) {
		if (!tile.renderModelsInPass1() && (MinecraftForgeClient.getRenderPass() != 0 && !StructureRenderer.isRenderingTiles()) && tile.isInWorld())
			return;
		this.bindTextureByName(tex);
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

	@Override
	protected final boolean doRenderModel(TileEntityBase te) {
		return this.isValidMachineRenderPass(te);
	}

}

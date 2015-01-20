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
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelRelaySource;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityRelaySource;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

public class RenderRelaySource extends ChromaRenderBase {

	private final ModelRelaySource model = new ModelRelaySource();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "receiver.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityRelaySource te = (TileEntityRelaySource)tile;

		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);

		GL11.glPushMatrix();
		this.renderModel(te, model);

		GL11.glPushMatrix();
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, -1.5F, -0.5F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		ReikaRenderHelper.prepareGeoDraw(false);
		int color = CrystalElement.getBlendedColor(te.getTicksExisted(), 50);
		GL11.glColor4f(ReikaColorAPI.getRedFromInteger(color)/255F, ReikaColorAPI.getGreenFromInteger(color)/255F, ReikaColorAPI.getBlueFromInteger(color)/255F, 1);
		model.renderEdges(te);
		GL11.glPopAttrib();
		GL11.glPopMatrix();

		GL11.glPopMatrix();

		GL11.glPushMatrix();
		if (MinecraftForgeClient.getRenderPass() == 1)
			;//this.renderPaths(te);
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}


}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Render;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerSpecificRenderer.PlayerRotationData;
import Reika.DragonAPI.Interfaces.PlayerRenderObj;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

public class DonatorPylonRender implements PlayerRenderObj {

	public static final DonatorPylonRender instance = new DonatorPylonRender();

	private DonatorPylonRender() {

	}

	@Override
	public void render(EntityPlayer ep, float ptick, PlayerRotationData dat) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glTranslated(0, 2.25, 0);
		//GL11.glRotated(-dat.getRenderYaw(), 0, 1, 0);
		GL11.glRotated(45, 1, 0, 0);
		GL11.glRotated(dat.getRenderPitch(), 1, 0, 0);
		GL11.glRotated(-45, 0, 1, 0);
		double s = 0.375;
		GL11.glScaled(s, s, s);
		//GL11.glRotated(45-ep.rotationPitch+90, 1, 0, 0);
		//GL11.glRotated(RenderManager.instance.playerViewY-ep.rotationYawHead-45, 0, 1, 0);
		ReikaRenderHelper.disableEntityLighting();
		ReikaRenderHelper.disableLighting();
		TileEntityCrystalPylon te = (TileEntityCrystalPylon)ChromaTiles.PYLON.createTEInstanceForRender(0);
		TileEntityRendererDispatcher.instance.renderTileEntityAt(te, 0, 0, 0, ptick);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	@Override
	public int getRenderPriority() {
		return Integer.MAX_VALUE;
	}

}

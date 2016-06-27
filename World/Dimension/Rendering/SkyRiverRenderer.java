/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkyRiverRenderer {

	public static final SkyRiverRenderer instance = new SkyRiverRenderer();

	private SkyRiverRenderer() {

	}

	public void render() {
		if (true)
			return;
		if (MinecraftForgeClient.getRenderPass() == 1) {

			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

			GL11.glDisable(GL11.GL_LIGHTING);
			BlendMode.DEFAULT.apply();
			ReikaRenderHelper.disableEntityLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(false);

			double p2 = -TileEntityRendererDispatcher.staticPlayerX;
			double p4 = -TileEntityRendererDispatcher.staticPlayerY;
			double p6 = -TileEntityRendererDispatcher.staticPlayerZ;

			GL11.glPushMatrix();

			GL11.glTranslated(p2, p4, p6);

			Tessellator v5 = Tessellator.instance;
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			double x = ep.posX;
			double y = ep.posY;
			double z = ep.posZ;

			this.doRenderRiver();

			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
	}

	private void doRenderRiver() {

	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.TimerMap;
import Reika.DragonAPI.Instantiable.Event.Client.EntityRenderingLoopEvent;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class OreOverlayRenderer {

	public static final OreOverlayRenderer instance = new OreOverlayRenderer();

	private final TimerMap<WorldLocation> coords = new TimerMap();

	private static final int DURATION = 160;

	private OreOverlayRenderer() {

	}

	public void addCoordinate(World world, int x, int y, int z) {
		coords.put(new WorldLocation(world, x, y, z), DURATION);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void tick(ClientTickEvent evt) {
		coords.tick();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void render(EntityRenderingLoopEvent evt) {
		if (!coords.isEmpty()) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			ReikaTextureHelper.bindTerrainTexture();
			int dim = Minecraft.getMinecraft().theWorld.provider.dimensionId;
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			for (WorldLocation loc : coords.keySet()) {
				if (loc.dimensionID == dim) {
					this.renderPoint(loc, coords.get(loc), ep);
				}
			}
			GL11.glPopAttrib();
		}
	}

	@SideOnly(Side.CLIENT)
	private void renderPoint(WorldLocation loc, int ticks, EntityPlayer ep) {
		GL11.glPushMatrix();

		RenderManager rm = RenderManager.instance;
		GL11.glTranslated(loc.xCoord+0.5-rm.renderPosX, loc.yCoord+0.5-rm.renderPosY, loc.zCoord+0.5-rm.renderPosZ);

		GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		Tessellator v5 = Tessellator.instance;
		IIcon ico = ChromaIcons.FADE_CLOUD.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		v5.startDrawingQuads();
		v5.setBrightness(240);

		float f1 = (float)ticks/DURATION;
		float f = Math.min(1, f1+0.375F);
		double s = 1.5*Math.sqrt(f);
		int c = ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, f1);
		v5.setColorOpaque_I(c);
		v5.addVertexWithUV(-s, s, 0, u, dv);
		v5.addVertexWithUV(+s, s, 0, du, dv);
		v5.addVertexWithUV(+s, -s, 0, du, v);
		v5.addVertexWithUV(-s, -s, 0, u, v);
		v5.draw();
		GL11.glPopMatrix();
	}

}

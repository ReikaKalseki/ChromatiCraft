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

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.CubePoints;
import Reika.DragonAPI.Instantiable.CubePoints.CubeVertex;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray.BlockMatchFailCallback;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Event.Client.EntityRenderingLoopEvent;
import Reika.DragonAPI.Interfaces.BlockCheck;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class StructureErrorOverlays implements BlockMatchFailCallback {

	public static final StructureErrorOverlays instance = new StructureErrorOverlays();

	private final ArrayList<ErrorOverlay> coords = new ArrayList();

	private static final int DURATION = 720*2;

	private StructureErrorOverlays() {

	}

	public void addCoordinate(World world, int x, int y, int z, BlockKey bk) {
		coords.add(new ErrorOverlay(new WorldLocation(world, x, y, z)));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void render(EntityRenderingLoopEvent evt) {
		if (!coords.isEmpty()) {
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			RenderManager rm = RenderManager.instance;
			GL11.glTranslated(-rm.renderPosX, -rm.renderPosY, -rm.renderPosZ);
			GL11.glDisable(GL11.GL_LIGHTING);
			//GL11.glDisable(GL11.GL_TEXTURE_2D);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDepthMask(false);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			//BlendMode.DEFAULT.apply();
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			Tessellator.instance.startDrawingQuads();
			int dim = Minecraft.getMinecraft().theWorld.provider.dimensionId;
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			Iterator<ErrorOverlay> it = coords.iterator();
			while (it.hasNext()) {
				ErrorOverlay loc = it.next();
				if (loc.location.dimensionID == dim) {
					this.renderPoint(loc, ep);
					loc.age--;
					if (loc.age <= 0)
						it.remove();
				}
			}
			Tessellator.instance.draw();
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
	}

	@SideOnly(Side.CLIENT)
	private void renderPoint(ErrorOverlay ticks, EntityPlayer ep) {
		double o = 0.03125;
		Tessellator v5 = Tessellator.instance;
		//int a = ReikaRandomHelper.getRandomBetween(96, 160);
		//int br = 255;//ReikaRandomHelper.getRandomBetween(127, 220);
		int a = 255;
		int br = ReikaRandomHelper.getRandomBetween(212, 255);
		if (ticks.age < DURATION/4) {
			float f = ticks.age*4F/DURATION;
			br *= f;
		}
		/*
		IIcon ico = ChromaIcons.STATIC.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();*/
		v5.setColorRGBA_I(ReikaColorAPI.GStoHex(br), a);
		v5.setBrightness(240);
		ticks.renderArea(v5);
	}

	@Override
	public void onBlockFailure(World world, int x, int y, int z, BlockCheck seek) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			this.addCoordinate(world, x, y, z, seek.asBlockKey());
		}
		else {
			BlockKey bk = seek.asBlockKey();
			ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.STRUCTUREERROR.ordinal(), world, x, y, z, 32, Block.getIdFromBlock(bk.blockID), bk.metadata);
		}
	}

	private static class ErrorOverlay {

		private final WorldLocation location;

		private final CubePoints box; //make randomly stretch a bit
		private final CubePoints renderBox;

		private int age;
		private String currentEdgeStretch;

		private ErrorOverlay(WorldLocation loc) {
			location = loc;
			box = CubePoints.fullBlock();
			box.expand(0.03125);
			renderBox = box.copy();

			age = DURATION;
		}

		@SideOnly(Side.CLIENT)
		public void renderArea(Tessellator v5) {
			/*
			v5.addVertexWithUV(c.xCoord, c.yCoord, c.zCoord, u, v);
			v5.addVertexWithUV(c.xCoord+1, c.yCoord, c.zCoord, du, v);
			v5.addVertexWithUV(c.xCoord+1, c.yCoord, c.zCoord+1, du, dv);
			v5.addVertexWithUV(c.xCoord, c.yCoord, c.zCoord+1, u, dv);

			v5.addVertexWithUV(c.xCoord, c.yCoord+1, c.zCoord+1, u, v);
			v5.addVertexWithUV(c.xCoord+1, c.yCoord+1, c.zCoord+1, du, v);
			v5.addVertexWithUV(c.xCoord+1, c.yCoord+1, c.zCoord, du, dv);
			v5.addVertexWithUV(c.xCoord, c.yCoord+1, c.zCoord, u, dv);

			v5.addVertexWithUV(c.xCoord+1, c.yCoord, c.zCoord, u, v);
			v5.addVertexWithUV(c.xCoord+1, c.yCoord+1, c.zCoord, du, v);
			v5.addVertexWithUV(c.xCoord+1, c.yCoord+1, c.zCoord+1, du, dv);
			v5.addVertexWithUV(c.xCoord+1, c.yCoord, c.zCoord+1, u, dv);

			v5.addVertexWithUV(c.xCoord, c.yCoord, c.zCoord+1, u, v);
			v5.addVertexWithUV(c.xCoord, c.yCoord+1, c.zCoord+1, du, v);
			v5.addVertexWithUV(c.xCoord, c.yCoord+1, c.zCoord, du, dv);
			v5.addVertexWithUV(c.xCoord, c.yCoord, c.zCoord, u, dv);

			v5.addVertexWithUV(c.xCoord+1, c.yCoord, c.zCoord+1, u, v);
			v5.addVertexWithUV(c.xCoord+1, c.yCoord+1, c.zCoord+1, du, v);
			v5.addVertexWithUV(c.xCoord, c.yCoord+1, c.zCoord+1, du, dv);
			v5.addVertexWithUV(c.xCoord, c.yCoord, c.zCoord+1, u, dv);

			v5.addVertexWithUV(c.xCoord, c.yCoord, c.zCoord, u, v);
			v5.addVertexWithUV(c.xCoord, c.yCoord+1, c.zCoord, du, v);
			v5.addVertexWithUV(c.xCoord+1, c.yCoord+1, c.zCoord, du, dv);
			v5.addVertexWithUV(c.xCoord+1, c.yCoord, c.zCoord, u, dv);
			 */
			IIcon ico = ChromaIcons.STATIC.getIcon();
			renderBox.renderIconOnSides(Minecraft.getMinecraft().theWorld, location.xCoord, location.yCoord, location.zCoord, ico, v5);
			if (age%5 == 0) {
				if (age%40 == 0)
					currentEdgeStretch = null;
				double r = 0.03125*1.5;
				String off = ReikaRandomHelper.doWithChance(0.033) ? ReikaJavaLibrary.getRandomCollectionEntry(DragonAPICore.rand, box.getVertices()).ID : null;
				if (off != null) {
					currentEdgeStretch = off;
					ReikaSoundHelper.playClientSound(ChromaSounds.ERROR, location.xCoord, location.yCoord, location.zCoord, 0.5F, 2F);
				}
				this.jitter(off, r);
			}
		}

		public void jitter(String bigOffset, double r) {
			for (CubeVertex cv : renderBox.getVertices()) {
				if (!cv.ID.equals(currentEdgeStretch)) {
					cv.setPosition(box.getVertex(cv.ID));
				}
				double dr = cv.ID.equals(bigOffset) ? r*8 : r;
				cv.offset(ReikaRandomHelper.getRandomPlusMinus(0, dr), ReikaRandomHelper.getRandomPlusMinus(0, dr), ReikaRandomHelper.getRandomPlusMinus(0, dr));
			}
		}

	}

}

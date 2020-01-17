/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityFluidDistributor;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class RenderSmelteryDistributor extends ChromaRenderBase {

	//private final ModelFluidDistributor model = new ModelFluidDistributor();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "fluiddistrib.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityFluidDistributor te = (TileEntityFluidDistributor)tile;

		GL11.glPushMatrix();

		if (MinecraftForgeClient.getRenderPass() == 1 || !te.isInWorld()) {
			this.renderHalo(te, par2, par4, par6, par8);
		}
		GL11.glTranslated(par2, par4, par6);
		//this.renderModel(te, model, te.isInWorld());

		GL11.glPopMatrix();
	}

	private void renderHalo(TileEntityFluidDistributor te, double par2, double par4, double par6, float ptick) {
		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		GL11.glPushMatrix();

		double h = 0.2875;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = ChromaIcons.LATTICE.getIcon();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
		BlendMode.ADDITIVEDARK.apply();
		GL11.glDisable(GL11.GL_LIGHTING);
		//RenderManager rm = RenderManager.instance;
		//GL11.glTranslated(0.5, h, 0.5);
		//GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
		//GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		v5.startDrawingQuads();
		v5.setBrightness(240);

		v5.setColorOpaque_I(0x2255ff);

		double[] out = new double[6];
		for (int i = 0; i < 6; i++) {
			out[i] = 0.0125+0.0125*Math.sin(Math.toRadians((te.getTicksExisted()+ptick)*3D+i*60D));
		}

		v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], u, dv);
		v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], du, dv);
		v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], du, v);
		v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], u, v);

		v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], u, dv);
		v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], du, dv);
		v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], du, v);
		v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], u, v);

		v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], u, dv);
		v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], du, dv);
		v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], du, v);
		v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], u, v);

		v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], u, dv);
		v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], du, dv);
		v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], du, v);
		v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], u, v);

		v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], u, dv);
		v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], du, dv);
		v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], du, v);
		v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 1+out[ForgeDirection.UP.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], u, v);

		v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], u, dv);
		v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 1+out[ForgeDirection.SOUTH.ordinal()], du, dv);
		v5.addVertexWithUV(1+out[ForgeDirection.EAST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], du, v);
		v5.addVertexWithUV(0-out[ForgeDirection.WEST.ordinal()], 0-out[ForgeDirection.DOWN.ordinal()], 0-out[ForgeDirection.NORTH.ordinal()], u, v);

		v5.draw();

		BlendMode.DEFAULT.apply();
		GL11.glPopAttrib();

		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}

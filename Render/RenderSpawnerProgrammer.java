/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Models.ModelSpawnerProgram;
import Reika.ChromatiCraft.TileEntity.TileEntitySpawnerReprogrammer;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class RenderSpawnerProgrammer extends ChromaRenderBase {

	private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	private final ModelSpawnerProgram model = new ModelSpawnerProgram();

	public static void bindGlint() {
		Minecraft.getMinecraft().renderEngine.bindTexture(RES_ITEM_GLINT);
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "spawnerprogram.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntitySpawnerReprogrammer te = (TileEntitySpawnerReprogrammer)tile;

		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		this.renderModel(te, model);
		if (te.hasSpawner()) {
			this.renderSpawner();
		}
		GL11.glPopMatrix();
	}

	private void renderSpawner() {
		Tessellator v5 = Tessellator.instance;
		ReikaTextureHelper.bindTerrainTexture();
		IIcon ico = Blocks.mob_spawner.getIcon(0, 0);
		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();

		float h = 0.0125F;
		float t = 1;
		float f = 0.125F;

		v5.startDrawingQuads();
		v5.addVertexWithUV(f, t-h, f, u, v);
		v5.addVertexWithUV(1-f, t-h, f, du, v);
		v5.addVertexWithUV(1-f, f-h, f, du, dv);
		v5.addVertexWithUV(f, f-h, f, u, dv);

		v5.addVertexWithUV(f, f-h, 1-f, u, v);
		v5.addVertexWithUV(1-f, f-h, 1-f, du, v);
		v5.addVertexWithUV(1-f, t-h, 1-f, du, dv);
		v5.addVertexWithUV(f, t-h, 1-f, u, dv);

		v5.addVertexWithUV(f, f-h, f, u, v);
		v5.addVertexWithUV(1-f, f-h, f, du, v);
		v5.addVertexWithUV(1-f, f-h, 1-f, du, dv);
		v5.addVertexWithUV(f, f-h, 1-f, u, dv);

		v5.addVertexWithUV(f, t-h, 1-f, u, v);
		v5.addVertexWithUV(1-f, t-h, 1-f, du, v);
		v5.addVertexWithUV(1-f, t-h, f, du, dv);
		v5.addVertexWithUV(f, t-h, f, u, dv);

		v5.addVertexWithUV(f, f-h, f, u, v);
		v5.addVertexWithUV(f, f-h, 1-f, du, v);
		v5.addVertexWithUV(f, t-h, 1-f, du, dv);
		v5.addVertexWithUV(f, t-h, f, u, dv);

		v5.addVertexWithUV(1-f, t-h, f, u, v);
		v5.addVertexWithUV(1-f, t-h, 1-f, du, v);
		v5.addVertexWithUV(1-f, f-h, 1-f, du, dv);
		v5.addVertexWithUV(1-f, f-h, f, u, dv);
		v5.draw();

		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		this.bindGlint();
		f = 0.12F;
		h = 0;
		float r = 4000;
		float dt = (float)Math.sin(Math.toRadians((System.currentTimeMillis()/50)%360));//(System.currentTimeMillis()%(int)r)/r;
		u = dt;
		du = 1+dt;
		v = dt;
		dv = 1+dt;

		v5.startDrawingQuads();
		v5.setColorOpaque(160, 255, 0);
		v5.addVertexWithUV(f, t-h, f, u, v);
		v5.addVertexWithUV(1-f, t-h, f, du, v);
		v5.addVertexWithUV(1-f, f-h, f, du, dv);
		v5.addVertexWithUV(f, f-h, f, u, dv);

		v5.addVertexWithUV(f, f-h, 1-f, u, v);
		v5.addVertexWithUV(1-f, f-h, 1-f, du, v);
		v5.addVertexWithUV(1-f, t-h, 1-f, du, dv);
		v5.addVertexWithUV(f, t-h, 1-f, u, dv);

		v5.addVertexWithUV(f, f-h, f, u, v);
		v5.addVertexWithUV(1-f, f-h, f, du, v);
		v5.addVertexWithUV(1-f, f-h, 1-f, du, dv);
		v5.addVertexWithUV(f, f-h, 1-f, u, dv);

		v5.addVertexWithUV(f, t-h, 1-f, u, v);
		v5.addVertexWithUV(1-f, t-h, 1-f, du, v);
		v5.addVertexWithUV(1-f, t-h, f, du, dv);
		v5.addVertexWithUV(f, t-h, f, u, dv);

		v5.addVertexWithUV(f, f-h, f, u, v);
		v5.addVertexWithUV(f, f-h, 1-f, du, v);
		v5.addVertexWithUV(f, t-h, 1-f, du, dv);
		v5.addVertexWithUV(f, t-h, f, u, dv);

		v5.addVertexWithUV(1-f, t-h, f, u, v);
		v5.addVertexWithUV(1-f, t-h, 1-f, du, v);
		v5.addVertexWithUV(1-f, f-h, 1-f, du, dv);
		v5.addVertexWithUV(1-f, f-h, f, u, dv);
		v5.draw();

		dt = (float)Math.sin(Math.toRadians((System.currentTimeMillis()/80+120)%360));
		u = dt;
		du = 1+dt;
		v = dt;
		dv = 1+dt;

		v5.startDrawingQuads();
		v5.setColorOpaque(160, 255, 0);
		v5.addVertexWithUV(f, t-h, f, u, dv);
		v5.addVertexWithUV(1-f, t-h, f, u, v);
		v5.addVertexWithUV(1-f, f-h, f, du, v);
		v5.addVertexWithUV(f, f-h, f, du, dv);

		v5.addVertexWithUV(f, f-h, 1-f, u, dv);
		v5.addVertexWithUV(1-f, f-h, 1-f, u, v);
		v5.addVertexWithUV(1-f, t-h, 1-f, du, v);
		v5.addVertexWithUV(f, t-h, 1-f, du, dv);

		v5.addVertexWithUV(f, f-h, f, u, dv);
		v5.addVertexWithUV(1-f, f-h, f, u, v);
		v5.addVertexWithUV(1-f, f-h, 1-f, du, v);
		v5.addVertexWithUV(f, f-h, 1-f, du, dv);

		v5.addVertexWithUV(f, t-h, 1-f, u, dv);
		v5.addVertexWithUV(1-f, t-h, 1-f, u, v);
		v5.addVertexWithUV(1-f, t-h, f, du, v);
		v5.addVertexWithUV(f, t-h, f, du, dv);

		v5.addVertexWithUV(f, f-h, f, u, dv);
		v5.addVertexWithUV(f, f-h, 1-f, u, v);
		v5.addVertexWithUV(f, t-h, 1-f, du, v);
		v5.addVertexWithUV(f, t-h, f, du, dv);

		v5.addVertexWithUV(1-f, t-h, f, u, dv);
		v5.addVertexWithUV(1-f, t-h, 1-f, u, v);
		v5.addVertexWithUV(1-f, f-h, 1-f, du, v);
		v5.addVertexWithUV(1-f, f-h, f, du, dv);
		v5.draw();

		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_BLEND);
	}

}
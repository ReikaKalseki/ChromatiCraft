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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Models.ModelLootChest;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

public class RenderLootChest extends ChromaRenderBase {

	private final ModelLootChest model = new ModelLootChest();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityLootChest te = (TileEntityLootChest)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);

		int meta = te.getBlockMetadata();
		String tex = "Textures/TileEntity/lootchest.png";
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, tex);
		float f1 = te.prevLidAngle+(te.lidAngle-te.prevLidAngle)*par8;
		f1 = 1.0F-f1;
		f1 = 1.0F-f1*f1*f1;
		float rot = 0;
		GL11.glTranslated(-0.5, -0.5, -0.5);

		GL11.glPushMatrix();
		switch(meta%8) {
			case 2:
				rot = 180;
				GL11.glTranslated(2, 0, 1);
				break;
			case 0:
				rot = -90;
				GL11.glTranslated(1, 0, 0);
				break;
			case 1:
				rot = 90;
				GL11.glTranslated(1, 0, 2);
				break;
			case 3:
				rot = 0;
				GL11.glTranslated(0, 0, 1);
				break;
		}

		GL11.glRotatef(rot, 0.0F, 1.0F, 0.0F);
		model.setLidRotation(-(f1*(float)Math.PI/2.0F));
		if (MinecraftForgeClient.getRenderPass() == 0 || !te.hasWorldObj())
			this.renderModel(te, model);
		GL11.glPopMatrix();

		if (meta >= 8) {
			GL11.glPushMatrix();
			switch(meta%8) {
				case 2:
					GL11.glTranslated(1, 0, -1);
					break;
				case 0:
					GL11.glTranslated(1, 0, 2);
					GL11.glRotated(180, 0, 1, 0);
					break;
				case 1:
					GL11.glTranslated(2, 0, 3);
					GL11.glRotated(180, 0, 1, 0);
					break;
				case 3:
					GL11.glTranslated(0, 0, 0);
					break;
			}
			GL11.glTranslated(1/256D, 0, -1/256D);
			ReikaRenderHelper.renderEnchantedModel(te, model, null, rot);
			GL11.glPopMatrix();
		}

		if (te.hasMarker && MinecraftForgeClient.getRenderPass() == 1) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDepthMask(false);
			ReikaRenderHelper.disableEntityLighting();
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDisable(GL11.GL_CULL_FACE);

			GL11.glTranslated(1, 1.25, 1);
			double s = 2;
			GL11.glScaled(s, s*2, s);
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/lightcone-foggy.png");
			long tick = Minecraft.getMinecraft().theWorld.getTotalWorldTime()+te.hashCode();
			int idx = (int)(tick%48);
			double u = (idx%12)/12D;
			double v = (idx/12)/4D;
			double du = u+1/12D;
			double dv = v+1/4D;
			Tessellator v5 = Tessellator.instance;

			double h = 2.5;
			double f = 0.5+0.5*Math.sin(tick/8D)+0.125*Math.sin((tick+300)/3D)+0.25*Math.cos((tick+20)/7D);
			int c1 = 0x22aaff;
			int c2 = 0x5588ff;
			int c = ReikaColorAPI.mixColors(c1, c2, (float)MathHelper.clamp_double(f, 0, 1));

			//for (double a = 0; a < 360; a += 120) {
			GL11.glPushMatrix();
			//GL11.glRotated(a, 0, 1, 0);
			GL11.glRotated(-RenderManager.instance.playerViewY, 0, 1, 0);
			v5.startDrawingQuads();
			v5.setColorOpaque_I(c);
			v5.addVertexWithUV(-0.5, h, 0, u, v);
			v5.addVertexWithUV(0.5, h, 0, du, v);
			v5.addVertexWithUV(0.5, 0, 0, du, dv);
			v5.addVertexWithUV(-0.5, 0, 0, u, dv);
			v5.draw();
			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}

		GL11.glPopMatrix();
	}

}

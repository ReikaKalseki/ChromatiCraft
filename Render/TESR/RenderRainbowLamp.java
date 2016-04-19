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

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Models.ModelLamp;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityChromaLamp;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class RenderRainbowLamp extends ChromaRenderBase {

	private static final double[] map = new double[CrystalElement.elements.length];

	private final ModelLamp model = new ModelLamp();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "lamp.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityChromaLamp te = (TileEntityChromaLamp)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		this.renderModel(te, model);
		if (MinecraftForgeClient.getRenderPass() == 1 && te.isInWorld()) {
			this.renderGlow(te, par2, par4, par6, par8);
		}
		GL11.glPopMatrix();
	}

	private void renderGlow(TileEntityChromaLamp te, double par2, double par4, double par6, float par8) {
		ElementTagCompound tag = te.getColors();
		Tessellator v5 = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		BlendMode.ADDITIVEDARK.apply();
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/clouds/bubble.png");
		int tick = te.getTicksExisted();
		double s = 0.3875;
		double h = 0.875;

		for (CrystalElement e : tag.elementSet()) {
			GL11.glPushMatrix();
			double ang = Math.toRadians(map[e.ordinal()]);
			double ang2 = e.ordinal()*22.5;
			double vu = Math.cos(ang);
			double vv = Math.sin(ang);
			int alpha = Math.max(0, (int)(255*Math.sin(Math.toRadians(ang2+tick+par8))));
			double u = (vu*(tick)%32)/32D;
			double v = (vv*(tick)%32)/32D;
			double du = 1+u;
			double dv = 1+v;
			int color = ReikaColorAPI.mixColors(e.getColor(), 0, alpha/255F);
			v5.startDrawingQuads();
			v5.setColorOpaque_I(color);
			v5.setBrightness(240);
			v5.addVertexWithUV(0.5-s, h, 0.5-s, u, dv);
			v5.addVertexWithUV(0.5+s, h, 0.5-s, du, dv);
			v5.addVertexWithUV(0.5+s, 0, 0.5-s, du, v);
			v5.addVertexWithUV(0.5-s, 0, 0.5-s, u, v);

			v5.addVertexWithUV(0.5-s, 0, 0.5+s, du, v);
			v5.addVertexWithUV(0.5+s, 0, 0.5+s, u, v);
			v5.addVertexWithUV(0.5+s, h, 0.5+s, u, dv);
			v5.addVertexWithUV(0.5-s, h, 0.5+s, du, dv);

			v5.addVertexWithUV(0.5+s, h, 0.5-s, u, dv);
			v5.addVertexWithUV(0.5+s, h, 0.5+s, du, dv);
			v5.addVertexWithUV(0.5+s, 0, 0.5+s, du, v);
			v5.addVertexWithUV(0.5+s, 0, 0.5-s, u, v);

			v5.addVertexWithUV(0.5-s, 0, 0.5-s, du, v);
			v5.addVertexWithUV(0.5-s, 0, 0.5+s, u, v);
			v5.addVertexWithUV(0.5-s, h, 0.5+s, u, dv);
			v5.addVertexWithUV(0.5-s, h, 0.5-s, du, dv);
			v5.draw();
			GL11.glPopMatrix();
		}
		BlendMode.DEFAULT.apply();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
	}

	static {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			map[i] = 5+ReikaRandomHelper.getRandomPlusMinus(i*22.5, 5);
		}
		ReikaArrayHelper.shuffleArray(map);
	}

}

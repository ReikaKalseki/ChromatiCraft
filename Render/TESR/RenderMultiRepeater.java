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

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCompoundRepeater;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalRepeater;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class RenderMultiRepeater extends RenderCrystalRepeater {

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		super.renderTileEntityAt(tile, par2, par4, par6, par8);
	}

	@Override
	protected void doAuxRendering(TileEntityCrystalRepeater te, float par8) {
		TileEntityCompoundRepeater tc = (TileEntityCompoundRepeater)te;
		if (tc.connectedToPylon()) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_ALPHA_TEST);

			Tessellator v5 = Tessellator.instance;
			double s = 3.5;

			IIcon ico = ChromaIcons.STARFLARE.getIcon();
			ReikaTextureHelper.bindTerrainTexture();
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();

			v5.startDrawingQuads();
			v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(this.getOffsetColor(tc, 16), 0.75F));
			v5.addVertexWithUV(-s, -s, 0, u, v);
			v5.addVertexWithUV(s, -s, 0, du, v);
			v5.addVertexWithUV(s, s, 0, du, dv);
			v5.addVertexWithUV(-s, s, 0, u, dv);

			s = s*1.5;
			v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(this.getOffsetColor(tc, 8), 0.25F));
			v5.addVertexWithUV(-s, -s, 0, u, v);
			v5.addVertexWithUV(s, -s, 0, du, v);
			v5.addVertexWithUV(s, s, 0, du, dv);
			v5.addVertexWithUV(-s, s, 0, u, dv);

			s = s*1.5;
			v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(this.getOffsetColor(tc, 0), 0.125F));
			v5.addVertexWithUV(-s, -s, 0, u, v);
			v5.addVertexWithUV(s, -s, 0, du, v);
			v5.addVertexWithUV(s, s, 0, du, dv);
			v5.addVertexWithUV(-s, s, 0, u, dv);

			s = 1.5;
			ico = ChromaIcons.CONCENTRIC2REV.getIcon();
			u = ico.getMinU();
			v = ico.getMinV();
			du = ico.getMaxU();
			dv = ico.getMaxV();

			v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(this.getOffsetColor(tc, 0), 0.125F));
			v5.addVertexWithUV(-s, -s, 0, u, v);
			v5.addVertexWithUV(s, -s, 0, du, v);
			v5.addVertexWithUV(s, s, 0, du, dv);
			v5.addVertexWithUV(-s, s, 0, u, dv);
			v5.draw();

			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
	}

	private int getOffsetColor(TileEntityCompoundRepeater te, int offset) {
		float mod = 32F;
		TileEntityCompoundRepeater tc = te;
		int t = tc.getColorCycleTick()+offset;
		int tick = (1+(int)((t/(double)mod)%16))%16;
		CrystalElement e1 = CrystalElement.elements[tick];
		CrystalElement e2 = CrystalElement.elements[(tick+1)%16];
		float mix = (float)(t%(double)mod)/mod;
		mix = Math.min(mix*2, 1);
		int c1 = e1.getColor();
		int c2 = e2.getColor();
		int color = ReikaColorAPI.mixColors(c2, c1, mix);
		return color;
	}

	@Override
	protected int getHaloRenderColor(TileEntityCrystalRepeater te) {
		//CrystalElement e = ((TileEntityCompoundRepeater)te).getRenderColorWithOffset(-8);
		//CrystalElement next = CrystalElement.elements[(e.ordinal()+1)%16];
		//float f = te.getTicksExisted()%32/32F;
		//ReikaJavaLibrary.pConsole(f+" from "+e+" to "+next);
		//return CrystalElement.getBlendedColor(te.getTicksExisted(), 32);//ReikaColorAPI.mixColors(next.getColor(), e.getColor(), f);

		return this.getOffsetColor((TileEntityCompoundRepeater)te, 0);
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.ISBRH;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.LaserEffectTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.LaserEffectType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Rendering.TessellatorVertexList;
import Reika.DragonAPI.Interfaces.ISBRH;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;


public class LaserEffectorRenderer implements ISBRH {

	public static int renderPass;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		int meta = world.getBlockMetadata(x, y, z);
		LaserEffectType type = LaserEffectType.list[meta];
		Tessellator v5 = Tessellator.instance;
		v5.setColorOpaque_I(0xffffff);
		v5.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		double slab = 0.33;
		double h = slab+(type == LaserEffectType.TARGET || type == LaserEffectType.TARGET_THRU ? 0.55 : 0.33);
		double w = 0.0625;
		double out = 0.1875;
		rb.renderMaxY = slab;
		if (renderPass == 0) {
			for (int i = 0; i < 6; i++) {
				IIcon ico = Blocks.stone_slab.getIcon(i, 0);
				switch(i) {
					case 0:
						v5.setColorOpaque_F(0.4F, 0.4F, 0.4F);
						rb.renderFaceYNeg(block, x, y, z, ico);
						break;
					case 1:
						v5.setColorOpaque_F(1, 1, 1);
						rb.renderFaceYPos(block, x, y, z, ico);
						break;
					case 2:
						v5.setColorOpaque_F(0.7F, 0.7F, 0.7F);
						rb.renderFaceZNeg(block, x, y, z, ico);
						break;
					case 3:
						v5.setColorOpaque_F(0.7F, 0.7F, 0.7F);
						rb.renderFaceZPos(block, x, y, z, ico);
						break;
					case 4:
						v5.setColorOpaque_F(0.55F, 0.55F, 0.55F);
						rb.renderFaceXNeg(block, x, y, z, ico);
						break;
					case 5:
						v5.setColorOpaque_F(0.55F, 0.55F, 0.55F);
						rb.renderFaceXPos(block, x, y, z, ico);
						break;
				}
			}
			v5.setColorOpaque_I(0xffffff);
			IIcon ico = Blocks.obsidian.getIcon(0, 0);
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			v5.addTranslation(x, y, z);

			v5.addTranslation(0.5F, 0, 0.5F);

			for (int i = -1; i <= 1; i += 2) {
				for (int k = -1; k <= 1; k += 2) {
					boolean invert = Math.signum(i) != Math.signum(k);
					TessellatorVertexList vt5 = new TessellatorVertexList();
					vt5.addVertexWithUV((out)*i, h, (out+w)*k, u, dv);
					vt5.addVertexWithUV((out+w)*i, h, (out+w)*k, du, dv);
					vt5.addVertexWithUV((out+w)*i, h, (out)*k, du, v);
					vt5.addVertexWithUV((out)*i, h, (out)*k, u, v);

					vt5.addVertexWithUV((out+w)*i, h, (out)*k, du, dv);
					vt5.addVertexWithUV((out+w)*i, slab, (out)*k, du, v);
					vt5.addVertexWithUV((out)*i, slab, (out)*k, u, v);
					vt5.addVertexWithUV((out)*i, h, (out)*k, u, dv);

					vt5.addVertexWithUV((out)*i, h, (out+w)*k, u, dv);
					vt5.addVertexWithUV((out)*i, slab, (out+w)*k, u, v);
					vt5.addVertexWithUV((out+w)*i, slab, (out+w)*k, du, v);
					vt5.addVertexWithUV((out+w)*i, h, (out+w)*k, du, dv);

					vt5.addVertexWithUV((out)*i, h, (out)*k, u, dv);
					vt5.addVertexWithUV((out)*i, slab, (out)*k, u, v);
					vt5.addVertexWithUV((out)*i, slab, (out+w)*k, du, v);
					vt5.addVertexWithUV((out)*i, h, (out+w)*k, du, dv);

					vt5.addVertexWithUV((out+w)*i, h, (out+w)*k, du, dv);
					vt5.addVertexWithUV((out+w)*i, slab, (out+w)*k, du, v);
					vt5.addVertexWithUV((out+w)*i, slab, (out)*k, u, v);
					vt5.addVertexWithUV((out+w)*i, h, (out)*k, u, dv);

					if (invert)
						vt5.reverse();

					vt5.render();
				}
			}

			this.renderCentralItem(world, x, y, z, meta, v5, slab, h, out);

			v5.addTranslation(-0.5F, 0, -0.5F);

			v5.addTranslation(-x, -y, -z);
		}
		/*
		else {
			v5.addTranslation(x, y, z);

			v5.addTranslation(0.5F, 0, 0.5F);

			this.renderCentralItem(world, x, y, z, meta, v5, slab, h, out);

			v5.addTranslation(-0.5F, 0, -0.5F);

			v5.addTranslation(-x, -y, -z);
		}
		 */
		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		return true;
	}

	private void renderCentralItem(IBlockAccess world, int x, int y, int z, int meta, Tessellator v5, double slab, double h, double out) {
		LaserEffectTile te = (LaserEffectTile)world.getTileEntity(x, y, z);
		//v5.setBrightness(240);
		int a = 255;
		out += 0.03125;
		h -= 0.03125;
		int c = te.getRenderColor();
		int ang = -te.getFacing().angle;
		LaserEffectType type = LaserEffectType.list[meta];
		IIcon ico = ChromaBlocks.CRYSTAL.getBlockInstance().getIcon(0, CrystalElement.WHITE.ordinal());
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		IIcon ico2 = Blocks.planks.getIcon(0, 0);
		float u2 = ico2.getMinU();
		float v2 = ico2.getMinV();
		float du2 = ico2.getMaxU();
		float dv2 = ico2.getMaxV();
		TessellatorVertexList vt5 = new TessellatorVertexList();
		switch(type) {
			case COLORIZER:
				a = 210;
				c = ReikaColorAPI.mixColors(c, 0xffffff, 0.75F);
				vt5.addVertexWithUV(-out, h, out, u, dv);
				vt5.addVertexWithUV(out, h, out, du, dv);
				vt5.addVertexWithUV(out, h, -out, du, v);
				vt5.addVertexWithUV(-out, h, -out, u, v);

				vt5.addVertexWithUV(-out, h, -out, u, dv);
				vt5.addVertexWithUV(out, h, -out, du, dv);
				vt5.addVertexWithUV(out, slab, -out, du, v);
				vt5.addVertexWithUV(-out, slab, -out, u, v);

				vt5.addVertexWithUV(-out, slab, out, u, v);
				vt5.addVertexWithUV(out, slab, out, du, v);
				vt5.addVertexWithUV(out, h, out, du, dv);
				vt5.addVertexWithUV(-out, h, out, u, dv);

				vt5.addVertexWithUV(-out, slab, -out, u, v);
				vt5.addVertexWithUV(-out, slab, out, du, v);
				vt5.addVertexWithUV(-out, h, out, du, dv);
				vt5.addVertexWithUV(-out, h, -out, u, dv);

				vt5.addVertexWithUV(out, h, -out, u, dv);
				vt5.addVertexWithUV(out, h, out, du, dv);
				vt5.addVertexWithUV(out, slab, out, du, v);
				vt5.addVertexWithUV(out, slab, -out, u, v);
				break;
			case EMITTER:
				int c2 = ReikaColorAPI.mixColors(c, 0xffffff, 0.675F);
				c = 0xffffff;
				vt5.addVertexWithUV(-out, h, out, u2, dv2);
				vt5.addVertexWithUV(out, h, out, du2, dv2);
				vt5.addVertexWithUV(out, h, -out, du2, v2);
				vt5.addVertexWithUV(-out, h, -out, u2, v2);

				vt5.addVertexWithUV(-out, h, -out, u2, dv2);
				vt5.addVertexWithUV(out, h, -out, du2, dv2);
				vt5.addVertexWithUV(out, slab, -out, du2, v2);
				vt5.addVertexWithUV(-out, slab, -out, u2, v2);

				vt5.addVertexWithUV(-out, slab, out, u2, v2);
				vt5.addVertexWithUV(out, slab, out, du2, v2);
				vt5.addVertexWithUV(out, h, out, du2, dv2);
				vt5.addVertexWithUV(-out, h, out, u2, dv2);

				vt5.addVertexWithUV(-out, slab, -out, u2, v2);
				vt5.addVertexWithUV(-out, slab, out, du2, v2);
				vt5.addVertexWithUV(-out, h, out, du2, dv2);
				vt5.addVertexWithUV(-out, h, -out, u2, dv2);

				vt5.addVertexWithUVColor(out, h, -out, u, dv, c2);
				vt5.addVertexWithUVColor(out, h, out, du, dv, c2);
				vt5.addVertexWithUVColor(out, slab, out, du, v, c2);
				vt5.addVertexWithUVColor(out, slab, -out, u, v, c2);
				break;
			case MIRROR:
			case DOUBLEMIRROR:
			case SLITMIRROR: {
				ang = ang-90;
				double t = 0.03125;
				double sp = type == LaserEffectType.SLITMIRROR ? 0.09375 : 0;

				float bu = type == LaserEffectType.DOUBLEMIRROR ? u : u2;
				float bv = type == LaserEffectType.DOUBLEMIRROR ? v : v2;
				float dbu = type == LaserEffectType.DOUBLEMIRROR ? du : du2;
				float dbv = type == LaserEffectType.DOUBLEMIRROR ? dv : dv2;

				vt5.addVertexWithUV(-out, slab, t+sp, u, v);
				vt5.addVertexWithUV(out, slab, t+sp, du, v);
				vt5.addVertexWithUV(out, h, t+sp, du, dv);
				vt5.addVertexWithUV(-out, h, t+sp, u, dv);

				vt5.addVertexWithUV(-out, h, -t+sp, bu, dbv);
				vt5.addVertexWithUV(out, h, -t+sp, dbu, dbv);
				vt5.addVertexWithUV(out, slab, -t+sp, dbu, bv);
				vt5.addVertexWithUV(-out, slab, -t+sp, bu, bv);

				vt5.addVertexWithUV(-out, h, t+sp, u2, dv2);
				vt5.addVertexWithUV(out, h, t+sp, du2, dv2);
				vt5.addVertexWithUV(out, h, -t+sp, du2, v2);
				vt5.addVertexWithUV(-out, h, -t+sp, u2, v2);

				vt5.addVertexWithUV(out, slab, -t+sp, u2, v2);
				vt5.addVertexWithUV(out, h, -t+sp, u2, dv2);
				vt5.addVertexWithUV(out, h, t+sp, du2, dv2);
				vt5.addVertexWithUV(out, slab, t+sp, du2, v2);

				vt5.addVertexWithUV(-out, slab, t+sp, du2, v2);
				vt5.addVertexWithUV(-out, h, t+sp, du2, dv2);
				vt5.addVertexWithUV(-out, h, -t+sp, u2, dv2);
				vt5.addVertexWithUV(-out, slab, -t+sp, u2, v2);

				if (type == LaserEffectType.SLITMIRROR) {
					sp = -sp;
					vt5.addVertexWithUV(-out, slab, t+sp, bu, bv);
					vt5.addVertexWithUV(out, slab, t+sp, dbu, bv);
					vt5.addVertexWithUV(out, h, t+sp, dbu, dbv);
					vt5.addVertexWithUV(-out, h, t+sp, bu, dbv);

					vt5.addVertexWithUV(-out, h, -t+sp, u, dv);
					vt5.addVertexWithUV(out, h, -t+sp, du, dv);
					vt5.addVertexWithUV(out, slab, -t+sp, du, v);
					vt5.addVertexWithUV(-out, slab, -t+sp, u, v);

					vt5.addVertexWithUV(-out, h, t+sp, u2, dv2);
					vt5.addVertexWithUV(out, h, t+sp, du2, dv2);
					vt5.addVertexWithUV(out, h, -t+sp, du2, v2);
					vt5.addVertexWithUV(-out, h, -t+sp, u2, v2);

					vt5.addVertexWithUV(out, slab, -t+sp, u2, v2);
					vt5.addVertexWithUV(out, h, -t+sp, u2, dv2);
					vt5.addVertexWithUV(out, h, t+sp, du2, dv2);
					vt5.addVertexWithUV(out, slab, t+sp, du2, v2);

					vt5.addVertexWithUV(-out, slab, t+sp, du2, v2);
					vt5.addVertexWithUV(-out, h, t+sp, du2, dv2);
					vt5.addVertexWithUV(-out, h, -t+sp, u2, dv2);
					vt5.addVertexWithUV(-out, slab, -t+sp, u2, v2);
				}
				break;
			}
			case ONEWAY:
				a = 210;
				vt5.addVertexWithUV(-out, h, out, u, dv);
				vt5.addVertexWithUV(out, h, out/2, du, dv);
				vt5.addVertexWithUV(out, h, -out/2, du, v);
				vt5.addVertexWithUV(-out, h, -out, u, v);

				vt5.addVertexWithUV(out, slab, -out/2, u, v);
				vt5.addVertexWithUV(out, h, -out/2, du, v);
				vt5.addVertexWithUV(out, h, out/2, du, dv);
				vt5.addVertexWithUV(out, slab, out/2, u, dv);

				vt5.addVertexWithUV(-out, slab, out, u, dv);
				vt5.addVertexWithUV(-out, h, out, du, dv);
				vt5.addVertexWithUV(-out, h, -out, du, v);
				vt5.addVertexWithUV(-out, slab, -out, u, v);

				vt5.addVertexWithUV(-out, h, out, u2, dv2);
				vt5.addVertexWithUV(-out, slab, out, u2, v2);
				vt5.addVertexWithUV(out, slab, out/2, du2, v2);
				vt5.addVertexWithUV(out, h, out/2, du2, dv2);

				vt5.addVertexWithUV(out, h, -out/2, du2, dv2);
				vt5.addVertexWithUV(out, slab, -out/2, du2, v2);
				vt5.addVertexWithUV(-out, slab, -out, u2, v2);
				vt5.addVertexWithUV(-out, h, -out, u2, dv2);
				break;
			case POLARIZER: {
				double t = 0.03125/2;
				double spm = 0.1875;
				for (double sp = -spm; sp <= spm; sp += spm/2) {
					vt5.addVertexWithUV(-out, slab, t+sp, u2, v2);
					vt5.addVertexWithUV(out, slab, t+sp, du2, v2);
					vt5.addVertexWithUV(out, h, t+sp, du2, dv2);
					vt5.addVertexWithUV(-out, h, t+sp, u2, dv2);

					vt5.addVertexWithUV(-out, h, -t+sp, u2, dv2);
					vt5.addVertexWithUV(out, h, -t+sp, du2, dv2);
					vt5.addVertexWithUV(out, slab, -t+sp, du2, v2);
					vt5.addVertexWithUV(-out, slab, -t+sp, u2, v2);

					vt5.addVertexWithUV(-out, h, t+sp, u2, dv2);
					vt5.addVertexWithUV(out, h, t+sp, du2, dv2);
					vt5.addVertexWithUV(out, h, -t+sp, du2, v2);
					vt5.addVertexWithUV(-out, h, -t+sp, u2, v2);

					vt5.addVertexWithUV(out, slab, -t+sp, u2, v2);
					vt5.addVertexWithUV(out, h, -t+sp, u2, dv2);
					vt5.addVertexWithUV(out, h, t+sp, du2, dv2);
					vt5.addVertexWithUV(out, slab, t+sp, du2, v2);

					vt5.addVertexWithUV(-out, slab, t+sp, du2, v2);
					vt5.addVertexWithUV(-out, h, t+sp, du2, dv2);
					vt5.addVertexWithUV(-out, h, -t+sp, u2, dv2);
					vt5.addVertexWithUV(-out, slab, -t+sp, u2, v2);
				}
				break;
			}
			case PRISM:
				a = 210;
				ang = ang-90;
				for (int i = 0; i < 360; i += 90) {
					TessellatorVertexList vt5b = new TessellatorVertexList();
					float pu = i == 0 ? u : u2;
					float pv = i == 0 ? v : v2;
					float dpu = i == 0 ? du : du2;
					float dpv = i == 0 ? dv : dv2;
					double outl = out/1.25;
					double outi = out/6;
					vt5b.addVertexWithUVColor(0, h, out, pu, pv, 0xffffff);
					vt5b.addVertexWithUVColor(outl, h, out, dpu, pv, 0xffffff);
					vt5b.addVertexWithUVColor(0, h, outi, dpu, dpv, 0xffffff);
					vt5b.addVertexWithUVColor(-outl, h, out, pu, dpv, 0xffffff);

					int sc = 0xffffff;
					switch(i) {
						case 90:
							sc = 0xa0a0ff;
							break;
						case 180:
							sc = 0xa0ffa0;
							break;
						case 270:
							sc = 0xffa0a0;
							break;
					}
					vt5b.addVertexWithUVColor(-outl, h, out, u, dv, sc);
					vt5b.addVertexWithUVColor(-outl, slab, out, u, v, sc);
					vt5b.addVertexWithUVColor(outl, slab, out, du, v, sc);
					vt5b.addVertexWithUVColor(outl, h, out, du, dv, sc);

					vt5b.addVertexWithUVColor(outl, h, out, du, dv, 0xffffff);
					vt5b.addVertexWithUVColor(outl, slab, out, du, v, 0xffffff);
					vt5b.addVertexWithUVColor(0, slab, outi, u, v, 0xffffff);
					vt5b.addVertexWithUVColor(0, h, outi, u, dv, 0xffffff);

					vt5b.addVertexWithUVColor(0, h, outi, u, dv, 0xffffff);
					vt5b.addVertexWithUVColor(0, slab, outi, u, v, 0xffffff);
					vt5b.addVertexWithUVColor(-outl, slab, out, du, v, 0xffffff);
					vt5b.addVertexWithUVColor(-outl, h, out, du, dv, 0xffffff);
					if (i > 0) {
						vt5b.rotateNonOrthogonal(0, i, 0);
					}
					vt5.addAll(vt5b);
				}
				break;
			case REFRACTOR:
				a = 210;
				ang = ang+180;
				vt5.addVertexWithUV(-out, h, out, u, dv);
				vt5.addVertexWithUV(0, h, 0, du, dv);
				vt5.addVertexWithUV(out, h, -out, du, v);
				vt5.addVertexWithUV(-out, h, -out, u, v);

				vt5.addVertexWithUV(-out, h, -out, u, dv);
				vt5.addVertexWithUV(out, h, -out, du, dv);
				vt5.addVertexWithUV(out, slab, -out, du, v);
				vt5.addVertexWithUV(-out, slab, -out, u, v);

				vt5.addVertexWithUV(-out, slab, -out, u2, v2);
				vt5.addVertexWithUV(-out, slab, out, du2, v2);
				vt5.addVertexWithUV(-out, h, out, du2, dv2);
				vt5.addVertexWithUV(-out, h, -out, u2, dv2);

				vt5.addVertexWithUV(out, h, -out, u, dv);
				vt5.addVertexWithUV(-out, h, out, du, dv);
				vt5.addVertexWithUV(-out, slab, out, du, v);
				vt5.addVertexWithUV(out, slab, -out, u, v);
				break;
			case SPLITTER:
				ang = ang-90;
				a = 210;
				vt5.addVertexWithUV(-out, h, -out, u, v);
				vt5.addVertexWithUV(-out, h, 0, u, dv);
				vt5.addVertexWithUV(0, h, out, du, dv);
				vt5.addVertexWithUV(0, h, -out, du, v);

				vt5.addVertexWithUV(0, h, -out, du, v);
				vt5.addVertexWithUV(0, h, out, du, dv);
				vt5.addVertexWithUV(out, h, 0, u, dv);
				vt5.addVertexWithUV(out, h, -out, u, v);

				vt5.addVertexWithUV(-out, h, -out, u, dv);
				vt5.addVertexWithUV(out, h, -out, du, dv);
				vt5.addVertexWithUV(out, slab, -out, du, v);
				vt5.addVertexWithUV(-out, slab, -out, u, v);

				vt5.addVertexWithUV(0, slab, out, u, v);
				vt5.addVertexWithUV(out, slab, 0, du, v);
				vt5.addVertexWithUV(out, h, 0, du, dv);
				vt5.addVertexWithUV(0, h, out, u, dv);

				vt5.addVertexWithUV(0, h, out, u, dv);
				vt5.addVertexWithUV(-out, h, 0, du, dv);
				vt5.addVertexWithUV(-out, slab, 0, du, v);
				vt5.addVertexWithUV(0, slab, out, u, v);

				vt5.addVertexWithUV(-out, h, 0, du2, dv2);
				vt5.addVertexWithUV(-out, h, -out, u2, dv2);
				vt5.addVertexWithUV(-out, slab, -out, u2, v2);
				vt5.addVertexWithUV(-out, slab, 0, du2, v2);

				vt5.addVertexWithUV(out, slab, 0, du2, v2);
				vt5.addVertexWithUV(out, slab, -out, u2, v2);
				vt5.addVertexWithUV(out, h, -out, u2, dv2);
				vt5.addVertexWithUV(out, h, 0, du2, dv2);
				break;
			case TARGET:
				c = 0xffffff;
				vt5.addVertexWithUV(out, h, -out, u2, dv2);
				vt5.addVertexWithUV(out, h, out, du2, dv2);
				vt5.addVertexWithUV(out, slab, out, du2, v2);
				vt5.addVertexWithUV(out, slab, -out, u2, v2);

				vt5.addVertexWithUV(out, slab, -out, u2, v2);
				vt5.addVertexWithUV(out, slab, out, du2, v2);
				vt5.addVertexWithUV(out, h, out, du2, dv2);
				vt5.addVertexWithUV(out, h, -out, u2, dv2);

				vt5.addVertexWithUV(-out, h, -out, u2, dv2);
				vt5.addVertexWithUV(out, h, -out, du2, dv2);
				vt5.addVertexWithUV(out, slab, -out, du2, v2);
				vt5.addVertexWithUV(-out, slab, -out, u2, v2);

				vt5.addVertexWithUV(-out, slab, -out, u2, v2);
				vt5.addVertexWithUV(out, slab, -out, du2, v2);
				vt5.addVertexWithUV(out, h, -out, du2, dv2);
				vt5.addVertexWithUV(-out, h, -out, u2, dv2);

				vt5.addVertexWithUV(-out, slab, out, u2, v2);
				vt5.addVertexWithUV(out, slab, out, du2, v2);
				vt5.addVertexWithUV(out, h, out, du2, dv2);
				vt5.addVertexWithUV(-out, h, out, u2, dv2);

				vt5.addVertexWithUV(-out, h, out, u2, dv2);
				vt5.addVertexWithUV(out, h, out, du2, dv2);
				vt5.addVertexWithUV(out, slab, out, du2, v2);
				vt5.addVertexWithUV(-out, slab, out, u2, v2);
				break;
			case TARGET_THRU:
				/*
				a = 210;
				double my = slab+(h-slab)/2;
				double py = h+(h-slab)/4;
				vt5.addVertexWithUV(-out/2, py, out/2, u, dv);
				vt5.addVertexWithUV(out/2, py, out/2, du, dv);
				vt5.addVertexWithUV(out/2, py, -out/2, du, v);
				vt5.addVertexWithUV(-out/2, py, -out/2, u, v);

				vt5.addVertexWithUV(-out/2, py, -out/2, u, dv);
				vt5.addVertexWithUV(out/2, py, -out/2, du, dv);
				vt5.addVertexWithUV(out/2, my, -out/2, du, v);
				vt5.addVertexWithUV(-out/2, my, -out/2, u, v);

				vt5.addVertexWithUV(-out/2, my, out/2, u, v);
				vt5.addVertexWithUV(out/2, my, out/2, du, v);
				vt5.addVertexWithUV(out/2, py, out/2, du, dv);
				vt5.addVertexWithUV(-out/2, py, out/2, u, dv);

				vt5.addVertexWithUV(-out/2, my, -out/2, u, v);
				vt5.addVertexWithUV(-out/2, my, out/2, du, v);
				vt5.addVertexWithUV(-out/2, py, out/2, du, dv);
				vt5.addVertexWithUV(-out/2, py, -out/2, u, dv);

				vt5.addVertexWithUV(out/2, py, -out/2, u, dv);
				vt5.addVertexWithUV(out/2, py, out/2, du, dv);
				vt5.addVertexWithUV(out/2, my, out/2, du, v);
				vt5.addVertexWithUV(out/2, my, -out/2, u, v);
				 */
				break;
		}
		v5.setColorRGBA_I(c, a);
		if (!type.isOmniDirectional()) {
			if (te.isRotateable()) {
				double th = 0.0625;
				vt5.offset(0, th, 0);
				double s = 0.225;

				IIcon ico3 = Blocks.stonebrick.getIcon(0, 3);
				float u3 = ico3.getMinU();
				float v3 = ico3.getMinV();
				float du3 = ico3.getMaxU();
				float dv3 = ico3.getMaxV();

				vt5.addVertexWithUVColor(-s, slab+th, s, u3, dv3, 0xffffff);
				vt5.addVertexWithUVColor(s, slab+th, s, du3, dv3, 0xffffff);
				vt5.addVertexWithUVColor(s, slab+th, -s, du3, v3, 0xffffff);
				vt5.addVertexWithUVColor(-s, slab+th, -s, u3, v3, 0xffffff);

				vt5.addVertexWithUV(-s, slab+th, -s, u3, dv3);
				vt5.addVertexWithUV(s, slab+th, -s, du3, dv3);
				vt5.addVertexWithUV(s, slab, -s, du3, v3);
				vt5.addVertexWithUV(-s, slab, -s, u3, v3);

				vt5.addVertexWithUV(-s, slab, s, u3, v3);
				vt5.addVertexWithUV(s, slab, s, du3, v3);
				vt5.addVertexWithUV(s, slab+th, s, du3, dv3);
				vt5.addVertexWithUV(-s, slab+th, s, u3, dv3);

				vt5.addVertexWithUV(-s, slab, -s, u3, v3);
				vt5.addVertexWithUV(-s, slab, s, du3, v3);
				vt5.addVertexWithUV(-s, slab+th, s, du3, dv3);
				vt5.addVertexWithUV(-s, slab+th, -s, u3, dv3);

				vt5.addVertexWithUV(s, slab+th, -s, u3, dv3);
				vt5.addVertexWithUV(s, slab+th, s, du3, dv3);
				vt5.addVertexWithUV(s, slab, s, du3, v3);
				vt5.addVertexWithUV(s, slab, -s, u3, v3);
			}
			vt5.rotateNonOrthogonal(0, ang, 0);
		}
		vt5.render();
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return 0;
	}

}

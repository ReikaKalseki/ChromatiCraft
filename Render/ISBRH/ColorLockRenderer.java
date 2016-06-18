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

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.Dimension.Structure.Locks.BlockColoredLock.TileEntityColorLock;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Interfaces.ISBRH;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public class ColorLockRenderer implements ISBRH {

	public static int renderPass;

	@Override
	public void renderInventoryBlock(Block b, int metadata, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;

		rb.renderMaxX = 1;
		rb.renderMinY = 0;
		rb.renderMaxZ = 1;
		rb.renderMinX = 0;
		rb.renderMinZ = 0;
		rb.renderMaxY = 1;

		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		v5.startDrawingQuads();
		v5.setNormal(0.0F, -1.0F, 0.0F);
		rb.renderFaceYNeg(b, 0.0D, 0.0D, 0.0D, b.getIcon(0, metadata));
		v5.draw();

		v5.startDrawingQuads();
		v5.setNormal(0.0F, 1.0F, 0.0F);
		rb.renderFaceYPos(b, 0.0D, 0.0D, 0.0D, b.getIcon(1, metadata));
		v5.draw();

		v5.startDrawingQuads();
		v5.setNormal(0.0F, 0.0F, -1.0F);
		rb.renderFaceZNeg(b, 0.0D, 0.0D, 0.0D, b.getIcon(2, metadata));
		v5.draw();
		v5.startDrawingQuads();
		v5.setNormal(0.0F, 0.0F, 1.0F);
		rb.renderFaceZPos(b, 0.0D, 0.0D, 0.0D, b.getIcon(3, metadata));
		v5.draw();
		v5.startDrawingQuads();
		v5.setNormal(-1.0F, 0.0F, 0.0F);
		rb.renderFaceXNeg(b, 0.0D, 0.0D, 0.0D, b.getIcon(4, metadata));
		v5.draw();
		v5.startDrawingQuads();
		v5.setNormal(1.0F, 0.0F, 0.0F);
		rb.renderFaceXPos(b, 0.0D, 0.0D, 0.0D, b.getIcon(5, metadata));
		v5.draw();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;
		int meta = world.getBlockMetadata(x, y, z);
		int color = b.colorMultiplier(world, x, y, z);
		float red = ReikaColorAPI.getRed(color)/255F;
		float grn = ReikaColorAPI.getGreen(color)/255F;
		float blu = ReikaColorAPI.getBlue(color)/255F;
		rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, red, grn, blu);
		TileEntityColorLock te = (TileEntityColorLock)world.getTileEntity(x, y, z);

		if (!te.isHeldOpen()) {
			Collection<CrystalElement> c = te.getClosedColors();
			if (!c.isEmpty()) {
				v5.addTranslation(x, y, z);
				IIcon[] ico = new IIcon[]{ChromaIcons.BASICFADE.getIcon(), ChromaIcons.FRAME.getIcon()};
				int s = c.size();
				double iy = 0.0625;
				double dx = 0.0625;
				//s will never be more than 4
				double spc = 0.03125;
				double w = (1D-(dx*2D)-spc*(s-1))/s;
				double sp = spc+w;
				double o = 0.005;
				for (CrystalElement e : c) {
					//if (!BlockColoredLock.isOpen(e, te.getChannel())) { does not work on servers
					for (int i = 0; i < ico.length; i++) {
						if (i == 0) {
							int clr = ReikaColorAPI.getColorWithBrightnessMultiplier(ReikaColorAPI.getModifiedSat(e.getColor(), 0.85F), 0.85F);
							v5.setColorRGBA_I(clr, 192);
							v5.setBrightness(240);
						}
						else {
							v5.setColorOpaque_I(0xffffff);
							v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y, z));
						}

						for (int k = 0; k < 4; k++) {

							float u = ico[i].getMinU();
							float v = ico[i].getMinV();
							float du = ico[i].getMaxU();
							float dv = ico[i].getMaxV();

							if (k == 0 || k == 3) {
								float scr = v;
								v = dv;
								dv = scr;
							}

							switch(k) {
								case 0:
									v5.addVertexWithUV(1+o, dx, iy, du, v);
									v5.addVertexWithUV(1+o, dx+w, iy, u, v);
									v5.addVertexWithUV(1+o, dx+w, 1-iy, u, dv);
									v5.addVertexWithUV(1+o, dx, 1-iy, du, dv);
									break;
								case 1:
									v5.addVertexWithUV(1-iy, dx, 1+o, du, dv);
									v5.addVertexWithUV(1-iy, dx+w, 1+o, u, dv);
									v5.addVertexWithUV(iy, dx+w, 1+o, u, v);
									v5.addVertexWithUV(iy, dx, 1+o, du, v);
									break;
								case 2:
									v5.addVertexWithUV(-o, dx, 1-iy, du, dv);
									v5.addVertexWithUV(-o, dx+w, 1-iy, u, dv);
									v5.addVertexWithUV(-o, dx+w, iy, u, v);
									v5.addVertexWithUV(-o, dx, iy, du, v);
									break;
								case 3:
									v5.addVertexWithUV(iy, dx, -o, du, v);
									v5.addVertexWithUV(iy, dx+w, -o, u, v);
									v5.addVertexWithUV(1-iy, dx+w, -o, u, dv);
									v5.addVertexWithUV(1-iy, dx, -o, du, dv);
									break;
							}
						}
					}

					dx += sp;
					//}
				}
				v5.addTranslation(-x, -y, -z);
			}
		}

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ChromatiCraft.proxy.colorLockRender;
	}



}

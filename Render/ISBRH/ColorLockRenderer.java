/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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
import Reika.ChromatiCraft.Block.Dimension.BlockColoredLock;
import Reika.ChromatiCraft.Block.Dimension.BlockColoredLock.TileEntityColorLock;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class ColorLockRenderer implements ISimpleBlockRenderingHandler {

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

		Collection<CrystalElement> c = te.getColors();
		if (!c.isEmpty()) {
			v5.addTranslation(x, y, z);
			v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y, z));
			IIcon ico = ChromaBlocks.CRYSTAL.getBlockInstance().getIcon(0, 0);
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();
			int s = (int)Math.ceil(Math.sqrt(c.size()));
			double dx = s == 1 ? 0.25 : 0.0625;
			double dy = s == 1 ? 0.25 : 0.0625;
			//s will never be more than 4
			double w = s == 1 ? 0.5 : s == 2 ? 0.375 : s == 3 ? 0.25 : 0.1875;
			double sp = s == 2 ? 0.5 : s == 3 ? 0.3125 : 0.2275;
			double o = 0.001;
			for (CrystalElement e : c) {
				if (!BlockColoredLock.isOpen(e, te.getChannel())) {
					v5.setColorRGBA_I(ReikaColorAPI.getColorWithBrightnessMultiplier(ReikaColorAPI.getModifiedSat(e.getColor(), 0.85F), 0.85F), 192);
					v5.addVertexWithUV(1+o, dx, dy, u, v);
					v5.addVertexWithUV(1+o, dx+w, dy, du, v);
					v5.addVertexWithUV(1+o, dx+w, dy+w, du, dv);
					v5.addVertexWithUV(1+o, dx, dy+w, u, dv);

					v5.addVertexWithUV(-o, dx, dy+w, u, dv);
					v5.addVertexWithUV(-o, dx+w, dy+w, du, dv);
					v5.addVertexWithUV(-o, dx+w, dy, du, v);
					v5.addVertexWithUV(-o, dx, dy, u, v);

					v5.addVertexWithUV(dy+w, dx, 1+o, u, dv);
					v5.addVertexWithUV(dy+w, dx+w, 1+o, du, dv);
					v5.addVertexWithUV(dy, dx+w, 1+o, du, v);
					v5.addVertexWithUV(dy, dx, 1+o, u, v);

					v5.addVertexWithUV(dy, dx, -o, u, v);
					v5.addVertexWithUV(dy, dx+w, -o, du, v);
					v5.addVertexWithUV(dy+w, dx+w, -o, du, dv);
					v5.addVertexWithUV(dy+w, dx, -o, u, dv);

					dx += sp;
					if (dx+w >= 1) {
						dx = 0.0625;
						dy += sp;
					}
				}
			}
			v5.addTranslation(-x, -y, -z);
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

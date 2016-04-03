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

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Block.BlockLumenRelay.TileEntityLumenRelay;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Instantiable.Rendering.TessellatorVertexList;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RelayRenderer implements ISimpleBlockRenderingHandler {

	public static int renderPass;

	public RelayRenderer() {

	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		//NO-OP
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks renderer) {
		Tessellator v5 = Tessellator.instance;

		double w = 0;
		double h = 0;

		v5.addTranslation(x, y, z);
		v5.setColorRGBA_I(0xffffff, renderPass > 0 ? 212 : 255);
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[world.getBlockMetadata(x, y, z)];
		switch(dir) {
			case UP:
				w = 0.5-b.getBlockBoundsMinX();
				h = (0.625-b.getBlockBoundsMinY())/2;
				break;
			case DOWN:
				w = 0.5-b.getBlockBoundsMinX();
				h = (0.625-b.getBlockBoundsMinY())/2;
				break;
			case EAST:
				w = 0.5-b.getBlockBoundsMinY();
				h = (0.625-b.getBlockBoundsMinX())/2;
				break;
			case WEST:
				w = 0.5-b.getBlockBoundsMinY();
				h = (0.625-b.getBlockBoundsMinX())/2;
				break;
			case NORTH:
				w = 0.5-b.getBlockBoundsMinY();
				h = (0.625-b.getBlockBoundsMinZ())/2;
				break;
			case SOUTH:
				w = 0.5-b.getBlockBoundsMinY();
				h = (0.625-b.getBlockBoundsMinZ())/2;
				break;
			default:
				return false;
		}
		this.renderDir(v5, dir, 0, 0, 0, w, h);
		if (renderPass == 1) {
			v5.setColorRGBA_I(0xffffff, 255);
			v5.setBrightness(240);
			this.renderConnectivity(v5, ((TileEntityLumenRelay)world.getTileEntity(x, y, z)).getInput(), dir);
		}

		v5.addTranslation(-x, -y, -z);
		return true;
	}

	private void renderConnectivity(Tessellator v5, ForgeDirection in, ForgeDirection face) {
		double w = 0.1875;
		double h = 0.25;

		if (face.offsetX+face.offsetY+face.offsetZ < 0)
			h = h-0.125;

		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			IIcon ico = ChromaIcons.GLOWFRAME_TRANS.getIcon();
			if (dir == in) {
				ico = ChromaIcons.GLOWFRAMEDOT_TRANS.getIcon();
			}
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();

			switch(dir) {
				case EAST:
					v5.addVertexWithUV(h*face.offsetX+0.5+w, h*face.offsetY+0.5-w, h*face.offsetZ+0.5-w, u, v);
					v5.addVertexWithUV(h*face.offsetX+0.5+w, h*face.offsetY+0.5+w, h*face.offsetZ+0.5-w, du, v);
					v5.addVertexWithUV(h*face.offsetX+0.5+w, h*face.offsetY+0.5+w, h*face.offsetZ+0.5+w, du, dv);
					v5.addVertexWithUV(h*face.offsetX+0.5+w, h*face.offsetY+0.5-w, h*face.offsetZ+0.5+w, u, dv);
					break;
				case WEST:
					v5.addVertexWithUV(h*face.offsetX+0.5-w, h*face.offsetY+0.5-w, h*face.offsetZ+0.5+w, u, dv);
					v5.addVertexWithUV(h*face.offsetX+0.5-w, h*face.offsetY+0.5+w, h*face.offsetZ+0.5+w, du, dv);
					v5.addVertexWithUV(h*face.offsetX+0.5-w, h*face.offsetY+0.5+w, h*face.offsetZ+0.5-w, du, v);
					v5.addVertexWithUV(h*face.offsetX+0.5-w, h*face.offsetY+0.5-w, h*face.offsetZ+0.5-w, u, v);
					break;
				case NORTH:
					v5.addVertexWithUV(h*face.offsetX+0.5-w, h*face.offsetY+0.5-w, h*face.offsetZ+0.5-w, u, v);
					v5.addVertexWithUV(h*face.offsetX+0.5-w, h*face.offsetY+0.5+w, h*face.offsetZ+0.5-w, du, v);
					v5.addVertexWithUV(h*face.offsetX+0.5+w, h*face.offsetY+0.5+w, h*face.offsetZ+0.5-w, du, dv);
					v5.addVertexWithUV(h*face.offsetX+0.5+w, h*face.offsetY+0.5-w, h*face.offsetZ+0.5-w, u, dv);
					break;
				case SOUTH:
					v5.addVertexWithUV(h*face.offsetX+0.5+w, h*face.offsetY+0.5-w, h*face.offsetZ+0.5+w, u, dv);
					v5.addVertexWithUV(h*face.offsetX+0.5+w, h*face.offsetY+0.5+w, h*face.offsetZ+0.5+w, du, dv);
					v5.addVertexWithUV(h*face.offsetX+0.5-w, h*face.offsetY+0.5+w, h*face.offsetZ+0.5+w, du, v);
					v5.addVertexWithUV(h*face.offsetX+0.5-w, h*face.offsetY+0.5-w, h*face.offsetZ+0.5+w, u, v);
					break;
				case DOWN:
					v5.addVertexWithUV(h*face.offsetX+0.5-w, h*face.offsetY+0.5-w, h*face.offsetZ+0.5-w, u, v);
					v5.addVertexWithUV(h*face.offsetX+0.5+w, h*face.offsetY+0.5-w, h*face.offsetZ+0.5-w, du, v);
					v5.addVertexWithUV(h*face.offsetX+0.5+w, h*face.offsetY+0.5-w, h*face.offsetZ+0.5+w, du, dv);
					v5.addVertexWithUV(h*face.offsetX+0.5-w, h*face.offsetY+0.5-w, h*face.offsetZ+0.5+w, u, dv);
					break;
				case UP:
					v5.addVertexWithUV(h*face.offsetX+0.5-w, h*face.offsetY+0.5+w, h*face.offsetZ+0.5+w, u, dv);
					v5.addVertexWithUV(h*face.offsetX+0.5+w, h*face.offsetY+0.5+w, h*face.offsetZ+0.5+w, du, dv);
					v5.addVertexWithUV(h*face.offsetX+0.5+w, h*face.offsetY+0.5+w, h*face.offsetZ+0.5-w, du, v);
					v5.addVertexWithUV(h*face.offsetX+0.5-w, h*face.offsetY+0.5+w, h*face.offsetZ+0.5-w, u, v);
					break;
				default:
					break;
			}
		}
	}

	private void renderDir(Tessellator t, ForgeDirection dir, double minx, double miny, double minz, double w, double h) {

		IIcon ico = renderPass == 1 ? ChromaBlocks.CRYSTAL.getBlockInstance().getIcon(0, 0) : ChromaBlocks.PYLONSTRUCT.getBlockInstance().getIcon(0, 0);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		/*
		v5.addVertexWithUV(minx, 									miny, 			minz, 									u, v);
		v5.addVertexWithUV(minx+w*2*dir.offsetX, 					miny, 			minz+w*2*dir.offsetZ, 					du, v);
		v5.addVertexWithUV(minx+w*2*dir.offsetX+w*2*dir.offsetZ, 	miny, 			minz+w*2*dir.offsetX+w*2*dir.offsetZ, 	du, dv);
		v5.addVertexWithUV(minx+w*2*dir.offsetZ, 					miny, 			minz+w*2*dir.offsetX, 					u, dv);
		 */
		TessellatorVertexList v5 = new TessellatorVertexList(0.5-Math.abs(dir.offsetX)*0.5, 0.5-Math.abs(dir.offsetY)*0.5, 0.5-Math.abs(dir.offsetZ)*0.5);

		if (renderPass == 0) {
			v5.addVertexWithUV(minx, 		miny, 			minz, 			u, v);
			v5.addVertexWithUV(minx+w*2, 	miny, 			minz, 			du, v);
			v5.addVertexWithUV(minx+w*2, 	miny, 			minz+w*2, 		du, dv);
			v5.addVertexWithUV(minx, 		miny, 			minz+w*2, 		u, dv);

			v5.addVertexWithUV(minx, 		miny+h*2, 		minz+w*2, 		u, dv);
			v5.addVertexWithUV(minx+w*2, 	miny+h*2, 		minz+w*2, 		du, dv);
			v5.addVertexWithUV(minx+w*2, 	miny+h*2, 		minz, 			du, v);
			v5.addVertexWithUV(minx, 		miny+h*2, 		minz, 			u, v);

			v5.addVertexWithUV(minx, 		miny+h, 		minz+w*2, 		u, dv);
			v5.addVertexWithUV(minx+w*2, 	miny+h, 		minz+w*2, 		du, dv);
			v5.addVertexWithUV(minx+w*2, 	miny+h, 		minz, 			du, v);
			v5.addVertexWithUV(minx, 		miny+h, 		minz, 			u, v);

			v5.addVertexWithUV(minx, 		miny, 			minz+w*2, 		u, dv);
			v5.addVertexWithUV(minx+w*2, 	miny, 			minz+w*2, 		du, dv);
			v5.addVertexWithUV(minx+w*2, 	miny+h, 		minz+w*2, 		du, v);
			v5.addVertexWithUV(minx, 		miny+h, 		minz+w*2, 		u, v);

			v5.addVertexWithUV(minx, 		miny+h, 		minz, 			u, v);
			v5.addVertexWithUV(minx+w*2, 	miny+h, 		minz, 			du, v);
			v5.addVertexWithUV(minx+w*2, 	miny, 			minz, 			du, dv);
			v5.addVertexWithUV(minx, 		miny, 			minz, 			u, dv);

			v5.addVertexWithUV(minx, 		miny, 			minz, 			u, dv);
			v5.addVertexWithUV(minx, 		miny, 			minz+w*2, 		du, dv);
			v5.addVertexWithUV(minx, 		miny+h, 		minz+w*2, 		du, v);
			v5.addVertexWithUV(minx, 		miny+h, 		minz, 			u, v);

			v5.addVertexWithUV(minx+w*2, 	miny+h, 		minz, 			u, v);
			v5.addVertexWithUV(minx+w*2, 	miny+h, 		minz+w*2, 		du, v);
			v5.addVertexWithUV(minx+w*2, 	miny, 			minz+w*2, 		du, dv);
			v5.addVertexWithUV(minx+w*2, 	miny, 			minz, 			u, dv);


			v5.addVertexWithUV(minx, 		miny+h*2, 		minz+w*2, 		u, dv);
			v5.addVertexWithUV(minx+w*2, 	miny+h*2, 		minz+w*2, 		du, dv);
			v5.addVertexWithUV(minx+w*2, 	miny+h*2, 		minz, 			du, v);
			v5.addVertexWithUV(minx, 		miny+h*2, 		minz, 			u, v);

			v5.addVertexWithUV(minx, 		miny+h, 		minz+w*2, 		u, dv);
			v5.addVertexWithUV(minx+w*2, 	miny+h, 		minz+w*2, 		du, dv);
			v5.addVertexWithUV(minx+w*2, 	miny+h*2, 		minz+w*2, 		du, v);
			v5.addVertexWithUV(minx, 		miny+h*2, 		minz+w*2, 		u, v);

			v5.addVertexWithUV(minx, 		miny+h*2, 		minz, 			u, v);
			v5.addVertexWithUV(minx+w*2, 	miny+h*2, 		minz, 			du, v);
			v5.addVertexWithUV(minx+w*2, 	miny+h, 		minz, 			du, dv);
			v5.addVertexWithUV(minx, 		miny+h, 		minz, 			u, dv);

			v5.addVertexWithUV(minx, 		miny+h, 		minz, 			u, dv);
			v5.addVertexWithUV(minx, 		miny+h, 		minz+w*2, 		du, dv);
			v5.addVertexWithUV(minx, 		miny+h*2, 		minz+w*2, 		du, v);
			v5.addVertexWithUV(minx, 		miny+h*2, 		minz, 			u, v);

			v5.addVertexWithUV(minx+w*2, 	miny+h*2, 		minz, 			u, v);
			v5.addVertexWithUV(minx+w*2, 	miny+h*2, 		minz+w*2, 		du, v);
			v5.addVertexWithUV(minx+w*2, 	miny+h, 		minz+w*2, 		du, dv);
			v5.addVertexWithUV(minx+w*2, 	miny+h, 		minz, 			u, dv);
		}

		if (renderPass == 1) {
			v5.addVertexWithUV(minx, 		miny+h*2+w*2, 	minz+w*2, 		u, dv);
			v5.addVertexWithUV(minx+w*2, 	miny+h*2+w*2, 	minz+w*2, 		du, dv);
			v5.addVertexWithUV(minx+w*2, 	miny+h*2+w*2, 	minz, 			du, v);
			v5.addVertexWithUV(minx, 		miny+h*2+w*2, 	minz, 			u, v);

			v5.addVertexWithUV(minx, 		miny+h*2, 		minz+w*2, 		u, dv);
			v5.addVertexWithUV(minx+w*2, 	miny+h*2, 		minz+w*2, 		du, dv);
			v5.addVertexWithUV(minx+w*2, 	miny+h*2+w*2, 	minz+w*2, 		du, v);
			v5.addVertexWithUV(minx, 		miny+h*2+w*2, 	minz+w*2, 		u, v);

			v5.addVertexWithUV(minx, 		miny+h*2+w*2, 	minz, 			u, v);
			v5.addVertexWithUV(minx+w*2, 	miny+h*2+w*2, 	minz, 			du, v);
			v5.addVertexWithUV(minx+w*2, 	miny+h*2, 		minz, 			du, dv);
			v5.addVertexWithUV(minx, 		miny+h*2, 		minz, 			u, dv);

			v5.addVertexWithUV(minx, 		miny+h*2, 		minz, 			u, dv);
			v5.addVertexWithUV(minx, 		miny+h*2, 		minz+w*2, 		du, dv);
			v5.addVertexWithUV(minx, 		miny+h*2+w*2, 	minz+w*2, 		du, v);
			v5.addVertexWithUV(minx, 		miny+h*2+w*2, 	minz, 			u, v);

			v5.addVertexWithUV(minx+w*2, 	miny+h*2+w*2, 	minz, 			u, v);
			v5.addVertexWithUV(minx+w*2, 	miny+h*2+w*2, 	minz+w*2, 		du, v);
			v5.addVertexWithUV(minx+w*2, 	miny+h*2, 		minz+w*2, 		du, dv);
			v5.addVertexWithUV(minx+w*2, 	miny+h*2, 		minz, 			u, dv);
		}

		if (dir.offsetX+dir.offsetY+dir.offsetZ < 0) {
			v5.invertY();
			//v5.offset(0, (1-h)/2, 0);
		}

		if (dir.offsetX != 0) {
			v5.rotateYtoX();
			v5.offset(0, -w, -w);
		}
		else if (dir.offsetZ != 0) {
			v5.rotateYtoZ();
			v5.offset(-w, -w, 0);
		}
		else {
			v5.offset(-w, 0, -w);
		}

		if (dir.offsetX < 0)
			;//v5.invertX();
		if (dir.offsetY < 0)
			;//v5.invertY();
		if (dir.offsetZ < 0)
			;//v5.invertZ();

		v5.offset(0.5, 0, 0.5);

		t.setBrightness(240);
		v5.render();
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

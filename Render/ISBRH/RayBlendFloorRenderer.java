/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.ISBRH;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import Reika.ChromatiCraft.Auxiliary.OverlayColor;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockRayblendFloor.TileEntityRayblendFloor;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockSpecialShield;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Base.ISBRH;
import Reika.DragonAPI.Instantiable.Rendering.EdgeDetectionRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;


public class RayBlendFloorRenderer extends ISBRH {

	private final RayBlendGridRenderer edge = (RayBlendGridRenderer)new RayBlendGridRenderer().setIcons(BlockSpecialShield.edgeIcons);

	public RayBlendFloorRenderer(int id) {
		super(id);
	}

	@Override
	public void renderInventoryBlock(Block b, int metadata, int modelId, RenderBlocks rb) {
		Tessellator tessellator = Tessellator.instance;

		rb.renderMaxX = 1;
		rb.renderMinY = 0;
		rb.renderMaxZ = 1;
		rb.renderMinX = 0;
		rb.renderMinZ = 0;
		rb.renderMaxY = 1;

		IIcon ico = b.getIcon(0, metadata);

		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		rb.renderFaceYNeg(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		rb.renderFaceYPos(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		rb.renderFaceZNeg(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		rb.renderFaceZPos(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		rb.renderFaceXNeg(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		rb.renderFaceXPos(b, 0.0D, 0.0D, 0.0D, ico);
		tessellator.draw();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {

		Tessellator v5 = Tessellator.instance;
		edge.renderOnOcclusion = false;

		v5.setBrightness(240);
		v5.setColorOpaque_I(ReikaColorAPI.getColorWithBrightnessMultiplier(b.colorMultiplier(world, x, y, z), 1));

		IIcon ico = b.getIcon(world, x, y, z, 0);
		if (b.shouldSideBeRendered(world, x, y-1, z, 0))
			rb.renderFaceYNeg(b, x, y, z, ico);

		ico = b.getIcon(world, x, y, z, 1);
		if (b.shouldSideBeRendered(world, x, y+1, z, 1)) {
			rb.renderFaceYPos(b, x, y, z, ico);

			v5.setColorOpaque_I(0xffffff);
			TileEntityRayblendFloor te = (TileEntityRayblendFloor)world.getTileEntity(x, y, z);
			if (te.isBlocked()) {
				rb.renderFaceYPos(b, x, y+0.01, z, ChromaIcons.X.getIcon());
			}
			else {
				OverlayColor clr = te.getOverlayColor();
				if (clr instanceof CrystalElement) {
					rb.renderFaceYPos(b, x, y+0.01, z, ((CrystalElement)clr).getEngravingRune());
				}
			}
		}

		ico = b.getIcon(world, x, y, z, 2);
		if (b.shouldSideBeRendered(world, x, y, z-1, 2))
			rb.renderFaceZNeg(b, x, y, z, ico);

		ico = b.getIcon(world, x, y, z, 3);
		if (b.shouldSideBeRendered(world, x, y, z+1, 3))
			rb.renderFaceZPos(b, x, y, z, ico);

		ico = b.getIcon(world, x, y, z, 4);
		if (b.shouldSideBeRendered(world, x-1, y, z, 4))
			rb.renderFaceXNeg(b, x, y, z, ico);

		ico = b.getIcon(world, x, y, z, 5);
		if (b.shouldSideBeRendered(world, x+1, y, z, 5))
			rb.renderFaceXPos(b, x, y, z, ico);

		v5.addTranslation(x, y, z);
		v5.setColorOpaque_I(0xffffff);
		v5.setBrightness(240);
		edge.renderBlock(world, x, y, z, rb);
		v5.addTranslation(-x, -y, -z);

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	private static class RayBlendGridRenderer extends EdgeDetectionRenderer {

		private RayBlendGridRenderer() {
			super(ChromaBlocks.RAYBLEND.getBlockInstance());
		}

		@Override
		protected boolean match(IBlockAccess world, int x0, int y0, int z0, int x, int y, int z) {
			return super.match(world, x0, y0, z0, x, y, z) && this.matchGrids(world, x0, y0, z0, x, y, z);
		}

		private boolean matchGrids(IBlockAccess world, int x1, int y1, int z1, int x2, int y2, int z2) {
			TileEntityRayblendFloor te1 = (TileEntityRayblendFloor)world.getTileEntity(x1, y1, z1);
			TileEntityRayblendFloor te2 = (TileEntityRayblendFloor)world.getTileEntity(x2, y2, z2);
			//ReikaJavaLibrary.pConsole(te1.getGridID()+" & "+te2.getGridID());
			return te1.getGridID().equals(te2.getGridID());
		}

	}

}

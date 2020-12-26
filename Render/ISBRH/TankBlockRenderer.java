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

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Block.BlockCrystalTank.CrystalTankAuxTile;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.TileEntity.Storage.TileEntityCrystalTank;
import Reika.DragonAPI.Base.ISBRH;
import Reika.DragonAPI.Interfaces.Block.ConnectedTextureGlass;


public class TankBlockRenderer extends ISBRH {

	public TankBlockRenderer(int id) {
		super(id);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;

		GL11.glColor3f(1, 1, 1);
		v5.startDrawingQuads();

		ConnectedTextureGlass b = (ConnectedTextureGlass)block;

		boolean render5 = b.renderCentralTextureForItem(metadata);
		boolean expand = metadata == 2;

		IIcon ico = b.getIconForEdge(metadata, 0);
		IIcon ico2 = b.getIconForEdge(metadata, 5);
		IIcon ico3 = ChromaIcons.CAUSTICS_TINY_ALPHA.getIcon();
		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();

		float u2 = ico2.getMinU();
		float du2 = ico2.getMaxU();
		float v2 = ico2.getMinV();
		float dv2 = ico2.getMaxV();

		float u3 = ico3.getMinU();
		float du3 = ico3.getMaxU();
		float v3 = ico3.getMinV();
		float dv3 = ico3.getMaxV();

		float dx = -0.5F;
		float dy = -0.5F;
		float dz = -0.5F;
		v5.addTranslation(dx, dy, dz);

		this.setFaceBrightness(v5, ForgeDirection.UP, 240);
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(1, 1, 0, u, v);
		v5.addVertexWithUV(0, 1, 0, du, v);
		v5.addVertexWithUV(0, 1, 1, du, dv);
		v5.addVertexWithUV(1, 1, 1, u, dv);

		if (render5) {
			v5.addVertexWithUV(1, 1, 0, u2, v2);
			v5.addVertexWithUV(0, 1, 0, du2, v2);
			v5.addVertexWithUV(0, 1, 1, du2, dv2);
			v5.addVertexWithUV(1, 1, 1, u2, dv2);
		}
		if (expand) {
			v5.addVertexWithUV(1, 1, 0, u3, v3);
			v5.addVertexWithUV(0, 1, 0, du3, v3);
			v5.addVertexWithUV(0, 1, 1, du3, dv3);
			v5.addVertexWithUV(1, 1, 1, u3, dv3);
		}

		this.setFaceBrightness(v5, ForgeDirection.DOWN, 240);
		v5.addVertexWithUV(0, 0, 0, du, v);
		v5.addVertexWithUV(1, 0, 0, u, v);
		v5.addVertexWithUV(1, 0, 1, u, dv);
		v5.addVertexWithUV(0, 0, 1, du, dv);

		if (render5) {
			v5.addVertexWithUV(0, 0, 0, du2, v2);
			v5.addVertexWithUV(1, 0, 0, u2, v2);
			v5.addVertexWithUV(1, 0, 1, u2, dv2);
			v5.addVertexWithUV(0, 0, 1, du2, dv2);
		}
		if (expand) {
			v5.addVertexWithUV(0, 0, 0, du3, v3);
			v5.addVertexWithUV(1, 0, 0, u3, v3);
			v5.addVertexWithUV(1, 0, 1, u3, dv3);
			v5.addVertexWithUV(0, 0, 1, du3, dv3);
		}

		this.setFaceBrightness(v5, ForgeDirection.EAST, 240);
		v5.addVertexWithUV(1, 0, 0, du, v);
		v5.addVertexWithUV(1, 1, 0, u, v);
		v5.addVertexWithUV(1, 1, 1, u, dv);
		v5.addVertexWithUV(1, 0, 1, du, dv);

		if (render5) {
			v5.addVertexWithUV(1, 0, 0, du2, v2);
			v5.addVertexWithUV(1, 1, 0, u2, v2);
			v5.addVertexWithUV(1, 1, 1, u2, dv2);
			v5.addVertexWithUV(1, 0, 1, du2, dv2);
		}
		if (expand) {
			v5.addVertexWithUV(1, 0, 0, du3, v3);
			v5.addVertexWithUV(1, 1, 0, u3, v3);
			v5.addVertexWithUV(1, 1, 1, u3, dv3);
			v5.addVertexWithUV(1, 0, 1, du3, dv3);
		}

		this.setFaceBrightness(v5, ForgeDirection.WEST, 240);
		v5.addVertexWithUV(0, 1, 0, u, v);
		v5.addVertexWithUV(0, 0, 0, du, v);
		v5.addVertexWithUV(0, 0, 1, du, dv);
		v5.addVertexWithUV(0, 1, 1, u, dv);

		if (render5) {
			v5.addVertexWithUV(0, 1, 0, u2, v2);
			v5.addVertexWithUV(0, 0, 0, du2, v2);
			v5.addVertexWithUV(0, 0, 1, du2, dv2);
			v5.addVertexWithUV(0, 1, 1, u2, dv2);
		}
		if (expand) {
			v5.addVertexWithUV(0, 1, 0, u3, v3);
			v5.addVertexWithUV(0, 0, 0, du3, v3);
			v5.addVertexWithUV(0, 0, 1, du3, dv3);
			v5.addVertexWithUV(0, 1, 1, u3, dv3);
		}

		this.setFaceBrightness(v5, ForgeDirection.SOUTH, 240);
		v5.addVertexWithUV(0, 1, 1, u, v);
		v5.addVertexWithUV(0, 0, 1, du, v);
		v5.addVertexWithUV(1, 0, 1, du, dv);
		v5.addVertexWithUV(1, 1, 1, u, dv);

		if (render5) {
			v5.addVertexWithUV(0, 1, 1, u2, v2);
			v5.addVertexWithUV(0, 0, 1, du2, v2);
			v5.addVertexWithUV(1, 0, 1, du2, dv2);
			v5.addVertexWithUV(1, 1, 1, u2, dv2);
		}
		if (expand) {
			v5.addVertexWithUV(0, 1, 1, u3, v3);
			v5.addVertexWithUV(0, 0, 1, du3, v3);
			v5.addVertexWithUV(1, 0, 1, du3, dv3);
			v5.addVertexWithUV(1, 1, 1, u3, dv3);
		}

		this.setFaceBrightness(v5, ForgeDirection.NORTH, 240);
		v5.addVertexWithUV(0, 0, 0, du, v);
		v5.addVertexWithUV(0, 1, 0, u, v);
		v5.addVertexWithUV(1, 1, 0, u, dv);
		v5.addVertexWithUV(1, 0, 0, du, dv);

		if (render5) {
			v5.addVertexWithUV(0, 0, 0, du2, v2);
			v5.addVertexWithUV(0, 1, 0, u2, v2);
			v5.addVertexWithUV(1, 1, 0, u2, dv2);
			v5.addVertexWithUV(1, 0, 0, du2, dv2);
		}
		if (expand) {
			v5.addVertexWithUV(0, 0, 0, du3, v3);
			v5.addVertexWithUV(0, 1, 0, u3, v3);
			v5.addVertexWithUV(1, 1, 0, u3, dv3);
			v5.addVertexWithUV(1, 0, 0, du3, dv3);
		}

		v5.addTranslation(-dx, -dy, -dz);

		v5.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		Tessellator v5 = Tessellator.instance;
		ConnectedTextureGlass b = (ConnectedTextureGlass)block;
		v5.addTranslation(x, y, z);

		int mix = block.getMixedBrightnessForBlock(world, x, y, z);
		if (renderPass == 1) {
			v5.setBrightness(240);
		}
		else {
			v5.setBrightness(mix);
		}
		v5.setNormal(0, 1, 0);

		CrystalTankAuxTile te = (CrystalTankAuxTile)world.getTileEntity(x, y, z);
		int meta = te.getBaseMetadata();
		TileEntityCrystalTank te2 = te.getTankController();
		if (te2 != null) {
			te2.lighting.update(te2.worldObj, x, y, z);
		}

		int d = 2;
		int dx = ((x%d)+d)%d;
		int dy = ((y%d)+d)%d;
		int dz = ((z%d)+d)%d;

		ArrayList<IIcon> li = new ArrayList();

		if (block.shouldSideBeRendered(world, x, y, z, ForgeDirection.UP.ordinal())) {
			this.buildIconList(world, x, y, z, b, meta, ForgeDirection.UP, li);
			this.setFaceBrightness(v5, ForgeDirection.UP, mix);
			for (IIcon ico : li) {
				float u = ico.getMinU();
				float du = ico.getMaxU();
				float v = ico.getMinV();
				float dv = ico.getMaxV();
				if (renderPass == 1) {
					u = ico.getInterpolatedU(16D*dx/d);
					v = ico.getInterpolatedV(16D*dz/d);
					du = ico.getInterpolatedU(16D*(dx+1)/d);
					dv = ico.getInterpolatedV(16D*(dz+1)/d);
					//ReikaJavaLibrary.pConsole(dx+">"+(u-ico.getMinU())/(ico.getMaxU()-ico.getMinU()));
				}
				v5.addVertexWithUV(1, 1, 0, u, v);
				v5.addVertexWithUV(0, 1, 0, du, v);
				v5.addVertexWithUV(0, 1, 1, du, dv);
				v5.addVertexWithUV(1, 1, 1, u, dv);
			}
		}

		if (block.shouldSideBeRendered(world, x, y, z, ForgeDirection.DOWN.ordinal())) {
			this.buildIconList(world, x, y, z, b, meta, ForgeDirection.DOWN, li);
			this.setFaceBrightness(v5, ForgeDirection.DOWN, mix);
			for (IIcon ico : li) {
				float u = ico.getMinU();
				float du = ico.getMaxU();
				float v = ico.getMinV();
				float dv = ico.getMaxV();
				if (renderPass == 1) {
					u = ico.getInterpolatedU(16D*dx/d);
					v = ico.getInterpolatedV(16D*dz/d);
					du = ico.getInterpolatedU(16D*(dx+1)/d);
					dv = ico.getInterpolatedV(16D*(dz+1)/d);
				}
				v5.addVertexWithUV(0, 0, 0, du, v);
				v5.addVertexWithUV(1, 0, 0, u, v);
				v5.addVertexWithUV(1, 0, 1, u, dv);
				v5.addVertexWithUV(0, 0, 1, du, dv);
			}
		}

		if (block.shouldSideBeRendered(world, x, y, z, ForgeDirection.EAST.ordinal())) {
			this.buildIconList(world, x, y, z, b, meta, ForgeDirection.EAST, li);
			this.setFaceBrightness(v5, ForgeDirection.EAST, mix);
			for (IIcon ico : li) {
				float u = ico.getMinU();
				float du = ico.getMaxU();
				float v = ico.getMinV();
				float dv = ico.getMaxV();
				if (renderPass == 1) {
					u = ico.getInterpolatedU(16D*dy/d);
					v = ico.getInterpolatedV(16D*dz/d);
					du = ico.getInterpolatedU(16D*(dy+1)/d);
					dv = ico.getInterpolatedV(16D*(dz+1)/d);
				}
				v5.addVertexWithUV(1, 0, 0, du, v);
				v5.addVertexWithUV(1, 1, 0, u, v);
				v5.addVertexWithUV(1, 1, 1, u, dv);
				v5.addVertexWithUV(1, 0, 1, du, dv);
			}
		}

		if (block.shouldSideBeRendered(world, x, y, z, ForgeDirection.WEST.ordinal())) {
			this.buildIconList(world, x, y, z, b, meta, ForgeDirection.WEST, li);
			this.setFaceBrightness(v5, ForgeDirection.WEST, mix);
			for (IIcon ico : li) {
				float u = ico.getMinU();
				float du = ico.getMaxU();
				float v = ico.getMinV();
				float dv = ico.getMaxV();
				if (renderPass == 1) {
					u = ico.getInterpolatedU(16D*dy/d);
					v = ico.getInterpolatedV(16D*dz/d);
					du = ico.getInterpolatedU(16D*(dy+1)/d);
					dv = ico.getInterpolatedV(16D*(dz+1)/d);
				}
				v5.addVertexWithUV(0, 1, 0, u, v);
				v5.addVertexWithUV(0, 0, 0, du, v);
				v5.addVertexWithUV(0, 0, 1, du, dv);
				v5.addVertexWithUV(0, 1, 1, u, dv);
			}
		}

		if (block.shouldSideBeRendered(world, x, y, z, ForgeDirection.SOUTH.ordinal())) {
			this.buildIconList(world, x, y, z, b, meta, ForgeDirection.SOUTH, li);
			this.setFaceBrightness(v5, ForgeDirection.SOUTH, mix);
			for (IIcon ico : li) {
				float u = ico.getMinU();
				float du = ico.getMaxU();
				float v = ico.getMinV();
				float dv = ico.getMaxV();
				if (renderPass == 1) {
					u = ico.getInterpolatedU(16D*dx/d);
					v = ico.getInterpolatedV(16D*dy/d);
					du = ico.getInterpolatedU(16D*(dx+1)/d);
					dv = ico.getInterpolatedV(16D*(dy+1)/d);
				}
				v5.addVertexWithUV(0, 1, 1, u, v);
				v5.addVertexWithUV(0, 0, 1, du, v);
				v5.addVertexWithUV(1, 0, 1, du, dv);
				v5.addVertexWithUV(1, 1, 1, u, dv);
			}
		}

		if (block.shouldSideBeRendered(world, x, y, z, ForgeDirection.NORTH.ordinal())) {
			this.buildIconList(world, x, y, z, b, meta, ForgeDirection.NORTH, li);
			this.setFaceBrightness(v5, ForgeDirection.NORTH, mix);
			for (IIcon ico : li) {
				float u = ico.getMinU();
				float du = ico.getMaxU();
				float v = ico.getMinV();
				float dv = ico.getMaxV();
				if (renderPass == 1) {
					u = ico.getInterpolatedU(16D*dx/d);
					v = ico.getInterpolatedV(16D*dy/d);
					du = ico.getInterpolatedU(16D*(dx+1)/d);
					dv = ico.getInterpolatedV(16D*(dy+1)/d);
				}
				v5.addVertexWithUV(0, 0, 0, du, v);
				v5.addVertexWithUV(0, 1, 0, u, v);
				v5.addVertexWithUV(1, 1, 0, u, dv);
				v5.addVertexWithUV(1, 0, 0, du, dv);
			}
		}

		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		v5.addVertex(0, 0, 0);
		v5.addTranslation(-x, -y, -z);
		return true;
	}

	private void buildIconList(IBlockAccess world, int x, int y, int z, ConnectedTextureGlass b, int meta, ForgeDirection side, ArrayList<IIcon> li) {
		li.clear();
		if (renderPass == 0) {
			for (int edge : b.getEdgesForFace(world, x, y, z, side)) {
				li.add(b.getIconForEdge(world, x, y, z, edge));
			}
		}
		if (renderPass == 1) {
			if (meta == 2) {
				li.add(ChromaIcons.CAUSTICS_TINY_ALPHA.getIcon());
			}
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int model) {
		return true;
	}

	private void setFaceBrightness(Tessellator v5, ForgeDirection dir, int mix) {
		float f = 1;
		switch(dir) {
			case DOWN:
				f = 0.4F;
				break;
			case EAST:
				f = 0.5F;
				break;
			case NORTH:
				f = 0.65F;
				break;
			case SOUTH:
				f = 0.65F;
				break;
			case UP:
				f = 1F;
				break;
			case WEST:
				f = 0.5F;
				break;
			default:
				break;
		}
		v5.setColorOpaque_F(f, f, f);
	}

}

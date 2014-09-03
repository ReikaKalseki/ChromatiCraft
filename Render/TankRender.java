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

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalTank;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class TankRender extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityCrystalTank te = (TileEntityCrystalTank)tile;
		Fluid f = te.getFluid();
		if (te.hasWorldObj() && f != null && te.getLevel() > 0) {
			Tessellator v5 = Tessellator.instance;
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);

			ReikaTextureHelper.bindTerrainTexture();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LIGHTING);
			IIcon ico = f.getStillIcon();
			if (ico == null) {
				ChromatiCraft.logger.logError("Fluid "+f.getID()+" ("+f.getLocalizedName()+") exists (block ID "+f.getBlock()+") but has no icon! Registering bedrock texture as a placeholder!");
				f.setIcons(Blocks.bedrock.getIcon(0, 0));
				ico = Blocks.bedrock.getIcon(0, 0);
			}
			float u = ico.getMinU();
			float v = ico.getMinV();
			float du = ico.getMaxU();
			float dv = ico.getMaxV();

			Block b = ChromaBlocks.TANK.getBlockInstance();
			GL11.glColor4f(1, 1, 1, 1);
			v5.startDrawingQuads();

			BlockArray blocks = te.getBlocks();
			for (int i = 0; i < blocks.getSize(); i++) {
				int[] xyz = blocks.getNthBlock(i);
				int x = xyz[0];
				int y = xyz[1];
				int z = xyz[2];

				double h = te.getFillLevelForY(y);

				if (h > 0) {
					v5.addTranslation(x-te.xCoord, y-te.yCoord, z-te.zCoord);
					double o = 0.0001;

					v5.setBrightness(f.getLuminosity() > 10 ? 240 : te.getBlockType().getMixedBrightnessForBlock(te.worldObj, x, y, z));

					if (h < 1 || b.shouldSideBeRendered(tile.worldObj, x, y, z, ForgeDirection.UP.ordinal())) {
						this.setFaceBrightness(v5, ForgeDirection.UP);
						v5.addVertexWithUV(0+o, h-o, 1-o, u, dv);
						v5.addVertexWithUV(1-o, h-o, 1-o, du, dv);
						v5.addVertexWithUV(1-o, h-o, 0+o, du, v);
						v5.addVertexWithUV(0+o, h-o, 0+o, u, v);
					}

					if (b.shouldSideBeRendered(tile.worldObj, x, y, z, ForgeDirection.DOWN.ordinal())) {
						this.setFaceBrightness(v5, ForgeDirection.DOWN);
						v5.addVertexWithUV(0+o, 0, 0+o, u, v);
						v5.addVertexWithUV(1-o, 0, 0+o, du, v);
						v5.addVertexWithUV(1-o, 0, 1-o, du, dv);
						v5.addVertexWithUV(0+o, 0, 1-o, u, dv);
					}

					if (b.shouldSideBeRendered(tile.worldObj, x, y, z, ForgeDirection.NORTH.ordinal())) {
						this.setFaceBrightness(v5, ForgeDirection.NORTH);
						v5.addVertexWithUV(0+o, h-o, 0+o, u, dv);
						v5.addVertexWithUV(1-o, h-o, 0+o, du, dv);
						v5.addVertexWithUV(1-o, 0, 0+o, du, v);
						v5.addVertexWithUV(0+o, 0, 0+o, u, v);
					}

					if (b.shouldSideBeRendered(tile.worldObj, x, y, z, ForgeDirection.SOUTH.ordinal())) {
						this.setFaceBrightness(v5, ForgeDirection.SOUTH);
						v5.addVertexWithUV(0+o, 0, 1-o, u, v);
						v5.addVertexWithUV(1-o, 0, 1-o, du, v);
						v5.addVertexWithUV(1-o, h-o, 1-o, du, dv);
						v5.addVertexWithUV(0+o, h-o, 1-o, u, dv);
					}

					if (b.shouldSideBeRendered(tile.worldObj, x, y, z, ForgeDirection.WEST.ordinal())) {
						this.setFaceBrightness(v5, ForgeDirection.WEST);
						v5.addVertexWithUV(0+o, 0, 1-o, u, v);
						v5.addVertexWithUV(0+o, h-o, 1-o, u, dv);
						v5.addVertexWithUV(0+o, h-o, 0+o, du, dv);
						v5.addVertexWithUV(0+o, 0, 0+o, du, v);
					}

					if (b.shouldSideBeRendered(tile.worldObj, x, y, z, ForgeDirection.EAST.ordinal())) {
						this.setFaceBrightness(v5, ForgeDirection.EAST);
						v5.addVertexWithUV(1-o, 0, 0+o, u, dv);
						v5.addVertexWithUV(1-o, h-o, 0+o, u, v);
						v5.addVertexWithUV(1-o, h-o, 1-o, du, v);
						v5.addVertexWithUV(1-o, 0, 1-o, du, dv);
					}
					v5.addTranslation(-x+te.xCoord, -y+te.yCoord, -z+te.zCoord);
				}

			}

			v5.draw();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);

			GL11.glPopMatrix();
		}
		else {

		}
	}

	private void setFaceBrightness(Tessellator v5, ForgeDirection dir) {
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

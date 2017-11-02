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

import java.awt.Color;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Storage.TileEntityCrystalTank;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaLiquidRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class TankRender extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5))
			return;
		TileEntityCrystalTank te = (TileEntityCrystalTank)tile;
		Fluid f = te.getFluid();
		if (te.hasWorldObj()) {
			if (f != null && te.getLevel() > 0) {
				GL11.glAlphaFunc(GL11.GL_GREATER, 1/255F);
				if (!Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2))
					this.renderLiquid(te, par2, par4, par6, par8, f);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
				if (!Keyboard.isKeyDown(Keyboard.KEY_NUMPAD3))
					this.renderRunes(te, par2, par4, par6, par8);
			}
			//this.renderGlint(te, par2, par4, par6, par8);
		}
		else {

		}
	}

	private void renderRunes(TileEntityCrystalTank te, double par2, double par4, double par6, float par8) {
		WorldLocation loc = new WorldLocation(te);
		te.ptick = te.getTicksExisted();
		BlockArray blocks = te.getBlocks();
		int d = Math.max(5, 100-blocks.getSize()/2);
		int tick = te.getTicksExisted()/d;
		CrystalElement et = ChromaAux.getRune(te.getFluid());
		int last = te.lastptick;
		if (et != null && te.getTicksExisted() != last && te.getTicksExisted()%d == 0 || (et != null && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD6))) {
			Coordinate dat = blocks.getRandomBlock();
			if (dat != null) {
				te.runes.addRune(dat.xCoord, dat.yCoord, dat.zCoord, et);
				te.lastptick = te.getTicksExisted();
			}
		}
		if (!Keyboard.isKeyDown(Keyboard.KEY_NUMPAD9)) {
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);
			te.runes.updateAndRender(Tessellator.instance, te, par8);
			GL11.glPopMatrix();
		}
	}

	private void renderGlint(TileEntityCrystalTank te, double par2, double par4, double par6, float par8) {
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		v5.setBrightness(240);
		float hue = (290+20*(float)Math.sin(Math.toRadians(te.getTicksExisted()+par8)))/360F;
		int color = new Color(Color.HSBtoRGB(hue, 1, 0.5F)).getRGB();
		v5.setColorOpaque_I(color);
		BlockArray blocks = te.getBlocks();
		blocks.remove(te.xCoord, te.yCoord, te.zCoord);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		//ReikaTextureHelper.bindEnchantmentTexture();
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/clouds/cloud2.png");
		GL11.glDisable(GL11.GL_LIGHTING);
		double o = 0.001;
		double u = (System.currentTimeMillis()%5000)/200D+Math.sin(System.currentTimeMillis()/500D);
		double v = u*1+0.5*Math.sin(u);
		double d = 1;
		for (int i = 0; i < blocks.getSize(); i++) {
			Coordinate c = blocks.getNthBlock(i);
			int x = c.xCoord;
			int y = c.yCoord;
			int z = c.zCoord;
			ArrayList<ForgeDirection> li = ReikaJavaLibrary.makeListFromArray(ForgeDirection.VALID_DIRECTIONS);
			for (int k = 0; k < 6; k++) {
				int dx = x+dirs[k].offsetX;
				int dy = y+dirs[k].offsetY;
				int dz = z+dirs[k].offsetZ;
				Block b = te.worldObj.getBlock(dx, dy, dz);
				if (b == ChromaBlocks.TANK.getBlockInstance())
					li.remove(dirs[k]);
			}
			int dx = x-te.xCoord;
			int dy = y-te.yCoord;
			int dz = z-te.zCoord;
			if (li.contains(ForgeDirection.SOUTH)) {
				v5.addVertexWithUV(dx-0-o, dy-0-o, dz+1+o, u, v);
				v5.addVertexWithUV(dx+1+o, dy-0-o, dz+1+o, u+d, v);
				v5.addVertexWithUV(dx+1+o, dy+1+o, dz+1+o, u+d, v+d);
				v5.addVertexWithUV(dx-0-o, dy+1+o, dz+1+o, u, v+d);
			}
			if (li.contains(ForgeDirection.NORTH)) {
				v5.addVertexWithUV(dx-0-o, dy+1+o, dz-0-o, u, v+d);
				v5.addVertexWithUV(dx+1+o, dy+1+o, dz-0-o, u+d, v+d);
				v5.addVertexWithUV(dx+1+o, dy-0-o, dz-0-o, u+d, v);
				v5.addVertexWithUV(dx-0-o, dy-0-o, dz-0-o, u, v);
			}
			if (li.contains(ForgeDirection.WEST)) {
				v5.addVertexWithUV(dx-0-o, dy-0-o, dz-0-o, u, v);
				v5.addVertexWithUV(dx-0-o, dy-0-o, dz+1+o, u+d, v);
				v5.addVertexWithUV(dx-0-o, dy+1+o, dz+1+o, u+d, v+d);
				v5.addVertexWithUV(dx-0-o, dy+1+o, dz-0-o, u, v+d);
			}
			if (li.contains(ForgeDirection.EAST)) {
				v5.addVertexWithUV(dx+1+o, dy+1+o, dz-0-o, u, v+d);
				v5.addVertexWithUV(dx+1+o, dy+1+o, dz+1+o, u+d, v+d);
				v5.addVertexWithUV(dx+1+o, dy-0-o, dz+1+o, u+d, v);
				v5.addVertexWithUV(dx+1+o, dy-0-o, dz-0-o, u, v);
			}
			if (li.contains(ForgeDirection.UP)) {
				v5.addVertexWithUV(dx-0-o, dy+1+o, dz+1+o, u, v+d);
				v5.addVertexWithUV(dx+1+o, dy+1+o, dz+1+o, u+d, v+d);
				v5.addVertexWithUV(dx+1+o, dy+1+o, dz-0-o, u+d, v);
				v5.addVertexWithUV(dx-0-o, dy+1+o, dz-0-o, u, v);
			}
			if (li.contains(ForgeDirection.DOWN)) {
				v5.addVertexWithUV(dx-0-o, dy-0-o, dz-0-o, u, v);
				v5.addVertexWithUV(dx+1+o, dy-0-o, dz-0-o, u+d, v);
				v5.addVertexWithUV(dx+1+o, dy-0-o, dz+1+o, u+d, v+d);
				v5.addVertexWithUV(dx-0-o, dy-0-o, dz+1+o, u, v+d);
			}
		}
		v5.draw();
		GL11.glDisable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	private void renderLiquid(TileEntityCrystalTank te, double par2, double par4, double par6, float par8, Fluid f) {
		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);

		ReikaTextureHelper.bindTerrainTexture();
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_LIGHTING);
		IIcon ico = ReikaLiquidRenderer.getFluidIconSafe(f);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		Block b = ChromaBlocks.TANK.getBlockInstance();
		GL11.glColor4f(1, 1, 1, 1);
		v5.startDrawingQuads();

		BlockArray blocks = te.getBlocks();
		boolean canRenderInTank = blocks.hasBlock(te.xCoord, te.yCoord+1, te.zCoord);

		boolean flip = te.isInvertedFilled();
		if (flip)
			GL11.glFrontFace(GL11.GL_CW);

		//RenderBlocks rb = RenderBlocks.getInstance();

		for (int i = 0; i < blocks.getSize(); i++) {
			Coordinate c = blocks.getNthBlock(i);
			int x = c.xCoord;
			int y = c.yCoord;
			int z = c.zCoord;
			int dx = x-te.xCoord;
			int dy = y-te.yCoord;
			int dz = z-te.zCoord;

			double h = te.getFillLevelForY(y);
			boolean disp = flip ? (h < 1 || te.getFillLevelForY(y+1) == 1) : (h > 0 || te.getFillLevelForY(y-1) == 1);

			if (disp) {

				int br = f.getLuminosity() > 10 ? 240 : (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD7) ? 240 : te.lighting.getCachedBrightness(te.worldObj, c));/*te.getBlockType().getMixedBrightnessForBlock(te.worldObj, x, y, z));*/
				/*
				int[] brs = new int[6];
				for (int k = 0; k < 6; k++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[k];
					brs[k] = b.getMixedBrightnessForBlock(te.worldObj, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
				}
				 */

				if (x == te.xCoord && y == te.yCoord && z == te.zCoord) {
					if (!canRenderInTank)
						continue;
					br = te.lighting.getCachedBrightness(te.worldObj, c.offset(0, 1, 0));//te.worldObj.getBlock(x, y+1, z).getMixedBrightnessForBlock(te.worldObj, x, y+1, z);
				}

				v5.addTranslation(dx, dy, dz);
				double o = 0.0025;
				h = Math.max(o, h);
				double min = flip ? 1-o : 0+o;
				double max = flip ? h+o : h-o;

				v5.setBrightness(br);
				int clr = 0xffffffff;
				if (f.canBePlacedInWorld()) {
					clr = f.getBlock().colorMultiplier(te.worldObj, x*2, y*2, z*2);
				}

				if (h < 1 || b.shouldSideBeRendered(te.worldObj, x, y, z, ForgeDirection.UP.ordinal())) {
					this.setFaceBrightness(v5, ForgeDirection.UP, f.getLuminosity(), clr);
					double ee = blocks.hasBlock(x+1, y, z) ? 1 : 1-o;
					double ew = blocks.hasBlock(x-1, y, z) ? 0 : 0+o;
					double es = blocks.hasBlock(x, y, z+1) ? 1 : 1-o;
					double en = blocks.hasBlock(x, y, z-1) ? 0 : 0+o;

					v5.addVertexWithUV(ew, max+te.getHeightOffsetAtCorner(x, y, z, -1, 1, h, par8), es, u, dv);
					v5.addVertexWithUV(ee, max+te.getHeightOffsetAtCorner(x, y, z, 1, 1, h, par8), es, du, dv);
					v5.addVertexWithUV(ee, max+te.getHeightOffsetAtCorner(x, y, z, 1, -1, h, par8), en, du, v);
					v5.addVertexWithUV(ew, max+te.getHeightOffsetAtCorner(x, y, z, -1, -1, h, par8), en, u, v);
				}

				if (b.shouldSideBeRendered(te.worldObj, x, y, z, ForgeDirection.DOWN.ordinal())) {
					this.setFaceBrightness(v5, ForgeDirection.DOWN, f.getLuminosity(), clr);
					double ee = blocks.hasBlock(x+1, y, z) ? 1 : 1-o;
					double ew = blocks.hasBlock(x-1, y, z) ? 0 : 0+o;
					double es = blocks.hasBlock(x, y, z+1) ? 1 : 1-o;
					double en = blocks.hasBlock(x, y, z-1) ? 0 : 0+o;
					v5.addVertexWithUV(ew, min, en, u, v);
					v5.addVertexWithUV(ee, min, en, du, v);
					v5.addVertexWithUV(ee, min, es, du, dv);
					v5.addVertexWithUV(ew, min, es, u, dv);
				}

				if (b.shouldSideBeRendered(te.worldObj, x, y, z, ForgeDirection.NORTH.ordinal())) {
					this.setFaceBrightness(v5, ForgeDirection.NORTH, f.getLuminosity(), clr);
					double ee = blocks.hasBlock(x+1, y, z) ? 1 : 1-o;
					double ew = blocks.hasBlock(x-1, y, z) ? 0 : 0+o;
					double eu = blocks.hasBlock(x, y+1, z) ? h : max;
					double ed = blocks.hasBlock(x, y-1, z) ? 0 : min;
					v5.addVertexWithUV(ew, eu+te.getHeightOffsetAtCorner(x, y, z, -1, -1, h, par8), 0+o, u, dv);
					v5.addVertexWithUV(ee, eu+te.getHeightOffsetAtCorner(x, y, z, 1, -1, h, par8), 0+o, du, dv);
					v5.addVertexWithUV(ee, ed, 0+o, du, v);
					v5.addVertexWithUV(ew, ed, 0+o, u, v);
				}

				if (b.shouldSideBeRendered(te.worldObj, x, y, z, ForgeDirection.SOUTH.ordinal())) {
					this.setFaceBrightness(v5, ForgeDirection.SOUTH, f.getLuminosity(), clr);
					double ee = blocks.hasBlock(x+1, y, z) ? 1 : 1-o;
					double ew = blocks.hasBlock(x-1, y, z) ? 0 : 0+o;
					double eu = blocks.hasBlock(x, y+1, z) ? h : max;
					double ed = blocks.hasBlock(x, y-1, z) ? 0 : min;
					v5.addVertexWithUV(ew, ed, 1-o, u, v);
					v5.addVertexWithUV(ee, ed, 1-o, du, v);
					v5.addVertexWithUV(ee, eu+te.getHeightOffsetAtCorner(x, y, z, 1, 1, h, par8), 1-o, du, dv);
					v5.addVertexWithUV(ew, eu+te.getHeightOffsetAtCorner(x, y, z, -1, 1, h, par8), 1-o, u, dv);
				}

				if (b.shouldSideBeRendered(te.worldObj, x, y, z, ForgeDirection.WEST.ordinal())) {
					this.setFaceBrightness(v5, ForgeDirection.WEST, f.getLuminosity(), clr);
					double es = blocks.hasBlock(x, y, z+1) ? 1 : 1-o;
					double en = blocks.hasBlock(x, y, z-1) ? 0 : 0+o;
					double eu = blocks.hasBlock(x, y+1, z) ? h : max;
					double ed = blocks.hasBlock(x, y-1, z) ? 0 : min;
					v5.addVertexWithUV(0+o, ed, es, u, v);
					v5.addVertexWithUV(0+o, eu+te.getHeightOffsetAtCorner(x, y, z, -1, 1, h, par8), es, u, dv);
					v5.addVertexWithUV(0+o, eu+te.getHeightOffsetAtCorner(x, y, z, -1, -1, h, par8), en, du, dv);
					v5.addVertexWithUV(0+o, ed, en, du, v);
				}

				if (b.shouldSideBeRendered(te.worldObj, x, y, z, ForgeDirection.EAST.ordinal())) {
					this.setFaceBrightness(v5, ForgeDirection.EAST, f.getLuminosity(), clr);
					double es = blocks.hasBlock(x, y, z+1) ? 1 : 1-o;
					double en = blocks.hasBlock(x, y, z-1) ? 0 : 0+o;
					double eu = blocks.hasBlock(x, y+1, z) ? h : max;
					double ed = blocks.hasBlock(x, y-1, z) ? 0 : min;
					v5.addVertexWithUV(1-o, ed, en, u, dv);
					v5.addVertexWithUV(1-o, eu+te.getHeightOffsetAtCorner(x, y, z, 1, -1, h, par8), en, u, v);
					v5.addVertexWithUV(1-o, eu+te.getHeightOffsetAtCorner(x, y, z, 1, 1, h, par8), es, du, v);
					v5.addVertexWithUV(1-o, ed, es, du, dv);
				}
				v5.addTranslation(-dx, -dy, -dz);
			}
		}

		v5.draw();
		//GL11.glFrontFace(GL11.GL_CCW);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);

		GL11.glPopMatrix();
	}

	private void setFaceBrightness(Tessellator v5, ForgeDirection dir, int brightness, int color) {
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD8))
			return;
		float f = 1;
		float sub = 0;
		switch(dir) {
			case DOWN:
				sub = 0.4F;
				break;
			case EAST:
				sub = 0.5F;
				break;
			case NORTH:
				sub = 0.65F;
				break;
			case SOUTH:
				sub = 0.65F;
				break;
			case UP:
				sub = 0F;
				break;
			case WEST:
				sub = 0.5F;
				break;
			default:
				break;
		}
		float osub = sub;
		sub *= (16-brightness)/4F;
		sub = Math.min(sub, osub);
		f -= sub*0.75F;
		v5.setColorOpaque((int)(f*ReikaColorAPI.getRed(color)), (int)(f*ReikaColorAPI.getGreen(color)), (int)(f*ReikaColorAPI.getBlue(color)));
	}

}

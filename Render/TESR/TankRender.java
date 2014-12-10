/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalTank;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class TankRender extends ChromaRenderBase {

	private HashMap<WorldLocation, HashMap<List<Float>, CrystalElement>> runes = new HashMap();
	private HashMap<WorldLocation, Integer> ptick = new HashMap();
	private HashMap<WorldLocation, Integer> lastptick = new HashMap();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityCrystalTank te = (TileEntityCrystalTank)tile;
		Fluid f = te.getFluid();
		if (te.hasWorldObj()) {
			if (f != null && te.getLevel() > 0) {
				this.renderLiquid(te, par2, par4, par6, par8, f);
				this.renderRunes(te, par2, par4, par6, par8);
			}
			//this.renderGlint(te, par2, par4, par6, par8);
		}
		else {

		}
	}

	private void renderRunes(TileEntityCrystalTank te, double par2, double par4, double par6, float par8) {
		WorldLocation loc = new WorldLocation(te);
		ptick.put(loc, te.getTicksExisted());
		BlockArray blocks = te.getBlocks();
		int d = Math.max(5, 100-blocks.getSize()/2);
		int tick = te.getTicksExisted()/d;
		HashMap<List<Float>, CrystalElement> colors = runes.get(loc);
		if (colors == null) {
			colors = new HashMap();
			runes.put(loc, colors);
		}
		CrystalElement et = getRune(tick, te.getFluid());
		Integer lastp = lastptick.get(loc);
		int last = lastp != null ? lastp.intValue() : -1;
		if (et != null && te.getTicksExisted() != last && te.getTicksExisted()%d == 0) {
			int[] dat = blocks.getRandomBlock();
			colors.put(Arrays.asList((float)dat[0], (float)dat[1], (float)dat[2]), et);
			lastptick.put(loc, te.getTicksExisted());
		}
		if (!colors.isEmpty()) {
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);
			Tessellator v5 = Tessellator.instance;
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			ReikaTextureHelper.bindTerrainTexture();
			v5.startDrawingQuads();
			v5.setBrightness(240);
			v5.setColorOpaque_I(0xffffff);
			float sp = Math.min(0.005F, 0.5F/ReikaRenderHelper.getFPS());
			HashMap<List<Float>, CrystalElement> add = new HashMap();
			ArrayList<List<Float>> remove = new ArrayList();
			for (List<Float> key : colors.keySet()) {
				CrystalElement e = colors.get(key);
				//ReikaJavaLibrary.pConsole(gs+" @ "+(te.getTicksExisted()%d));
				float f1 = key.get(0);
				float f2 = key.get(1);
				float f3 = key.get(2);
				int x = Math.round(f1);
				int y = Math.round(f2);
				int z = Math.round(f3);
				ArrayList<ForgeDirection> li = ReikaJavaLibrary.makeListFromArray(ForgeDirection.VALID_DIRECTIONS);
				for (int k = 0; k < 6; k++) {
					int dx = x+dirs[k].offsetX;
					int dy = y+dirs[k].offsetY;
					int dz = z+dirs[k].offsetZ;
					Block b = te.worldObj.getBlock(dx, dy, dz);
					if (b == ChromaBlocks.TANK.getBlockInstance())
						li.remove(dirs[k]);
				}
				//ReikaJavaLibrary.pConsole(li+" @ "+Arrays.toString(xyz));

				IIcon ico = e.getGlowRune();
				float u = ico.getMinU();
				float du = ico.getMaxU();
				float v = ico.getMinV();
				float dv = ico.getMaxV();
				double o = 0.001;
				double dx = f1-te.xCoord;
				double dy = f2-te.yCoord;
				double dz = f3-te.zCoord;

				for (int i = 0; i < 4; i++) {
					if (li.contains(ForgeDirection.SOUTH)) {
						v5.addVertexWithUV(dx-0-o, dy-0-o, dz+1+o, u, dv);
						v5.addVertexWithUV(dx+1+o, dy-0-o, dz+1+o, du, dv);
						v5.addVertexWithUV(dx+1+o, dy+1+o, dz+1+o, du, v);
						v5.addVertexWithUV(dx-0-o, dy+1+o, dz+1+o, u, v);
					}
					if (li.contains(ForgeDirection.NORTH)) {
						v5.addVertexWithUV(dx-0-o, dy+1+o, dz-0-o, du, v);
						v5.addVertexWithUV(dx+1+o, dy+1+o, dz-0-o, u, v);
						v5.addVertexWithUV(dx+1+o, dy-0-o, dz-0-o, u, dv);
						v5.addVertexWithUV(dx-0-o, dy-0-o, dz-0-o, du, dv);
					}
					if (li.contains(ForgeDirection.WEST)) {
						v5.addVertexWithUV(dx-0-o, dy-0-o, dz-0-o, u, dv);
						v5.addVertexWithUV(dx-0-o, dy-0-o, dz+1+o, du, dv);
						v5.addVertexWithUV(dx-0-o, dy+1+o, dz+1+o, du, v);
						v5.addVertexWithUV(dx-0-o, dy+1+o, dz-0-o, u, v);
					}
					if (li.contains(ForgeDirection.EAST)) {
						v5.addVertexWithUV(dx+1+o, dy+1+o, dz-0-o, u, v);
						v5.addVertexWithUV(dx+1+o, dy+1+o, dz+1+o, du, v);
						v5.addVertexWithUV(dx+1+o, dy-0-o, dz+1+o, du, dv);
						v5.addVertexWithUV(dx+1+o, dy-0-o, dz-0-o, u, dv);
					}
					/*
				if (li.contains(ForgeDirection.UP)) {
					v5.addVertexWithUV(dx-0-o, dy+1+o, dz+1+o, u, dv);
					v5.addVertexWithUV(dx+1+o, dy+1+o, dz+1+o, du, dv);
					v5.addVertexWithUV(dx+1+o, dy+1+o, dz-0-o, du, v);
					v5.addVertexWithUV(dx-0-o, dy+1+o, dz-0-o, u, v);
				}
				if (li.contains(ForgeDirection.DOWN)) {
					v5.addVertexWithUV(dx-0-o, dy-0-o, dz-0-o, u, v);
					v5.addVertexWithUV(dx+1+o, dy-0-o, dz-0-o, du, v);
					v5.addVertexWithUV(dx+1+o, dy-0-o, dz+1+o, du, dv);
					v5.addVertexWithUV(dx-0-o, dy-0-o, dz+1+o, u, dv);
				}*/
				}

				f2 -= sp;
				int y2 = Math.round(f2);
				if (blocks.hasBlock(x, y2, z)) {
					add.put(Arrays.asList(f1, f2, f3), e);
				}
				remove.add(key);
			}
			for (List<Float> key : add.keySet())
				colors.put(key, add.get(key));
			for (List<Float> key : remove)
				colors.remove(key);

			v5.draw();
			GL11.glDisable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
	}

	private static CrystalElement getRune(int tick, Fluid fluid) {
		ArrayList<CrystalElement> li = new ArrayList();
		li.add(CrystalElement.CYAN);
		if (fluid.getTemperature() > 900) //900K
			li.add(CrystalElement.ORANGE);
		if (fluid.getTemperature() < 270)
			li.add(CrystalElement.WHITE);
		if (fluid.isGaseous())
			li.add(CrystalElement.LIME);
		if (fluid.getLuminosity() > 0)
			li.add(CrystalElement.BLUE);
		if (fluid.getDensity() > 4000)
			li.add(CrystalElement.RED);
		if (fluid.getName().toLowerCase().contains("oil"))
			li.add(CrystalElement.BROWN);
		if (fluid.getName().toLowerCase().contains("fuel"))
			li.add(CrystalElement.YELLOW);
		if (fluid.getName().toLowerCase().contains("xp"))
			li.add(CrystalElement.PURPLE);
		if (fluid.getName().toLowerCase().contains("bio") || fluid.getName().toLowerCase().contains("honey"))
			li.add(CrystalElement.GREEN);
		return li.get(ReikaRandomHelper.getSafeRandomInt(li.size()));
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
			int[] xyz = blocks.getNthBlock(i);
			int x = xyz[0];
			int y = xyz[1];
			int z = xyz[2];
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
		blocks.remove(te.xCoord, te.yCoord, te.zCoord);

		boolean flip = te.isInvertedFilled();
		if (flip)
			GL11.glFrontFace(GL11.GL_CW);
		for (int i = 0; i < blocks.getSize(); i++) {
			int[] xyz = blocks.getNthBlock(i);
			int x = xyz[0];
			int y = xyz[1];
			int z = xyz[2];

			double h = te.getFillLevelForY(y);
			//boolean disp = flip ? h < 1 : h > 0;

			//if (disp) {
			v5.addTranslation(x-te.xCoord, y-te.yCoord, z-te.zCoord);
			double o = 0.0025;
			h = Math.max(o, h);
			double min = flip ? 1-o : 0+o;
			double max = flip ? h+o : h-o;

			v5.setBrightness(f.getLuminosity() > 10 ? 240 : te.getBlockType().getMixedBrightnessForBlock(te.worldObj, x, y, z));

			if (h < 1 || b.shouldSideBeRendered(te.worldObj, x, y, z, ForgeDirection.UP.ordinal())) {
				this.setFaceBrightness(v5, ForgeDirection.UP, f.getLuminosity());
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
				this.setFaceBrightness(v5, ForgeDirection.DOWN, f.getLuminosity());
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
				this.setFaceBrightness(v5, ForgeDirection.NORTH, f.getLuminosity());
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
				this.setFaceBrightness(v5, ForgeDirection.SOUTH, f.getLuminosity());
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
				this.setFaceBrightness(v5, ForgeDirection.WEST, f.getLuminosity());
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
				this.setFaceBrightness(v5, ForgeDirection.EAST, f.getLuminosity());
				double es = blocks.hasBlock(x, y, z+1) ? 1 : 1-o;
				double en = blocks.hasBlock(x, y, z-1) ? 0 : 0+o;
				double eu = blocks.hasBlock(x, y+1, z) ? h : max;
				double ed = blocks.hasBlock(x, y-1, z) ? 0 : min;
				v5.addVertexWithUV(1-o, ed, en, u, dv);
				v5.addVertexWithUV(1-o, eu+te.getHeightOffsetAtCorner(x, y, z, 1, -1, h, par8), en, u, v);
				v5.addVertexWithUV(1-o, eu+te.getHeightOffsetAtCorner(x, y, z, 1, 1, h, par8), es, du, v);
				v5.addVertexWithUV(1-o, ed, es, du, dv);
			}
			v5.addTranslation(-x+te.xCoord, -y+te.yCoord, -z+te.zCoord);
		}
		//}

		v5.draw();
		GL11.glFrontFace(GL11.GL_CCW);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);

		GL11.glPopMatrix();
	}

	private void setFaceBrightness(Tessellator v5, ForgeDirection dir, int brightness) {
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
		v5.setColorOpaque_F(f, f, f);
	}

}

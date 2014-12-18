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

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Coordinate;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class RenderCastingTable extends ChromaRenderBase {

	private HashMap<WorldLocation, HashMap<Coordinate, Location>> runes = new HashMap();
	private HashMap<WorldLocation, Integer> ptick = new HashMap();
	private HashMap<WorldLocation, Integer> lastptick = new HashMap();

	private static class Location {

		private final CrystalElement color;
		private int age = 0;

		private Location(CrystalElement e) {
			color = e;
		}

	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityCastingTable te = (TileEntityCastingTable)tile;
		GL11.glPushMatrix();
		this.renderRunes(te, par2, par4, par6, par8);
		GL11.glPopMatrix();
	}

	private void renderRunes(TileEntityCastingTable te, double par2, double par4, double par6, float par8) {
		BlockArray blocks = te.getBlocks();
		if (blocks == null)
			return;
		WorldLocation loc = new WorldLocation(te);
		ptick.put(loc, te.getTicksExisted());
		int d = 50;
		int tick = te.getTicksExisted()/d;
		HashMap<Coordinate, Location> colors = runes.get(loc);
		if (colors == null) {
			colors = new HashMap();
			runes.put(loc, colors);
		}
		Integer lastp = lastptick.get(loc);
		int last = lastp != null ? lastp.intValue() : -1;
		if (te.getTicksExisted() != last && te.getTicksExisted()%d == 0) {
			int[] dat = blocks.getRandomBlock();
			Coordinate c = new Coordinate(dat[0], dat[1], dat[2]);
			while (c.getBlock(te.worldObj) != ChromaBlocks.PYLONSTRUCT.getBlockInstance() || c.getBlockMetadata(te.worldObj) > 2) {
				dat = blocks.getRandomBlock();
				c = new Coordinate(dat[0], dat[1], dat[2]);
			}
			colors.put(c, new Location(CrystalElement.randomElement()));
			lastptick.put(loc, te.getTicksExisted());
		}
		if (!colors.isEmpty()) {
			GL11.glPushMatrix();
			GL11.glTranslated(par2, par4, par6);
			Tessellator v5 = Tessellator.instance;
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();
			ReikaTextureHelper.bindTerrainTexture();
			v5.startDrawingQuads();
			v5.setBrightness(240);
			GL11.glAlphaFunc(GL11.GL_GREATER, 1/255F);
			v5.setColorRGBA_I(0xffffff, 40);
			float sp = Math.min(0.005F, 0.5F/ReikaRenderHelper.getFPS());
			ArrayList<Coordinate> remove = new ArrayList();
			for (Coordinate key : colors.keySet()) {
				Location l = colors.get(key);
				CrystalElement e = l.color;
				//ReikaJavaLibrary.pConsole(gs+" @ "+(te.getTicksExisted()%d));
				int x = key.xCoord;
				int y = key.yCoord;
				int z = key.zCoord;
				ArrayList<ForgeDirection> li = ReikaJavaLibrary.makeListFromArray(ForgeDirection.VALID_DIRECTIONS);
				//ReikaJavaLibrary.pConsole(li+" @ "+Arrays.toString(xyz));

				IIcon ico = e.getEngravingRune();

				float u = ico.getMinU();
				float du = ico.getMaxU();
				float v = ico.getMinV();
				float dv = ico.getMaxV();
				double o = 0.001;
				int dx = x-te.xCoord;
				int dy = y-te.yCoord;
				int dz = z-te.zCoord;

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
					}
				}

				l.age++;
				if (l.age > 2000)
					remove.add(key);
			}
			for (Coordinate key : remove)
				colors.remove(key);

			v5.draw();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			//GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
	}

}

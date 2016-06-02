/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Storage.TileEntityCrystalTank;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class TankRunes {

	private final ArrayList<TankRune> runes = new ArrayList();
	private static final ArrayList<ForgeDirection> dirSet = ReikaJavaLibrary.makeListFrom(ForgeDirection.SOUTH, ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.WEST);

	public void addRune(float x, float y, float z, CrystalElement e) {
		TankRune r = new TankRune(e);
		r.px = x;
		r.py = y;
		r.pz = z;
		runes.add(r);
	}

	public void updateAndRender(Tessellator v5, TileEntityCrystalTank te, float ptick) {
		if (!runes.isEmpty()) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDepthMask(false);
			ReikaTextureHelper.bindTerrainTexture();
			v5.startDrawingQuads();
			v5.setBrightness(240);
			v5.setColorOpaque_I(0xffffff);

			Iterator<TankRune> it = runes.iterator();
			BlockArray blocks = te.getBlocks();
			float sp = Math.min(0.005F, 0.5F/ReikaRenderHelper.getFPS())*3;
			while (it.hasNext()) {
				TankRune r = it.next();
				if (r.updateAndRender(v5, te, blocks, sp, ptick)) {
					it.remove();
				}
			}

			v5.draw();
			GL11.glPopAttrib();
		}
	}

	private static class TankRune {

		private final CrystalElement color;

		private float px;
		private float py;
		private float pz;

		private TankRune(CrystalElement e) {
			color = e;
		}

		private boolean updateAndRender(Tessellator v5, TileEntityCrystalTank te, BlockArray blocks, double sp, float ptick) {
			//ReikaJavaLibrary.pConsole(gs+" @ "+(te.getTicksExisted()%d));
			int x = Math.round(px);
			int y = Math.round(py);
			int z = Math.round(pz);
			HashSet<ForgeDirection> li = new HashSet(dirSet);
			for (int k = 2; k < 6; k++) {
				int dx = x+ForgeDirection.VALID_DIRECTIONS[k].offsetX;
				int dy = y+ForgeDirection.VALID_DIRECTIONS[k].offsetY;
				int dz = z+ForgeDirection.VALID_DIRECTIONS[k].offsetZ;
				Block b = te.worldObj.getBlock(dx, dy, dz);
				if (b == ChromaBlocks.TANK.getBlockInstance())
					li.remove(ForgeDirection.VALID_DIRECTIONS[k]);
			}
			//ReikaJavaLibrary.pConsole(li+" @ "+Arrays.toString(xyz));

			IIcon ico = color.getGlowRune();
			float u = ico.getMinU();
			float du = ico.getMaxU();
			float v = ico.getMinV();
			float dv = ico.getMaxV();
			double o = 0.004;
			double dx = px-te.xCoord;
			double dy = py-te.yCoord;
			double dz = pz-te.zCoord;

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
					v5.addVertexWithUV(dx+1+o, dy-0-o, dz+1-o, u, dv);
					v5.addVertexWithUV(dx+1+o, dy-0-o, dz-0+o, du, dv);
					v5.addVertexWithUV(dx+1+o, dy+1+o, dz-0+o, du, v);
					v5.addVertexWithUV(dx+1+o, dy+1+o, dz+1-o, u, v);
				}
			}

			py -= sp;
			int y2 = MathHelper.floor_double(py);
			if (blocks.hasBlock(x, y2, z)) {
				return false;
			}
			else {
				return true;
			}
		}

	}

}

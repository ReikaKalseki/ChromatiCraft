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

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Block.Crystal.BlockCrystalGlow.Bases;
import Reika.ChromatiCraft.Block.Crystal.BlockCrystalGlow.TileEntityCrystalGlow;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Rendering.TessellatorVertexList;
import Reika.DragonAPI.Interfaces.ISBRH;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class CrystalGlowRenderer implements ISBRH {

	public static int renderPass;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		Bases b = Bases.baseList[metadata/16];
		CrystalElement e = CrystalElement.elements[metadata%16];
		IIcon ico = b.texture.getIcon(0, 0);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		Tessellator v5 = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glRotated(45, 0, 1, 0);
		GL11.glRotated(-30, 1, 0, 0);
		double s = 1.6;
		GL11.glScaled(s, s, s);
		double x = -0.5;
		double y = -0.5;
		double z = 0;
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslated(x, y, z);
		v5.startDrawingQuads();

		double w = 0.2875;
		double h = 0.4375;

		v5.addVertexWithUV(0.5-w, 0.5-h, 0, u, v);
		v5.addVertexWithUV(0.5+w, 0.5-h, 0, du, v);
		v5.addVertexWithUV(0.5+w, 0.5+h, 0, du, dv);
		v5.addVertexWithUV(0.5-w, 0.5+h, 0, u, dv);

		IIcon ico2 = e.getOverbrightIcon();
		float u2 = ico2.getMinU();
		float v2 = ico2.getMinV();
		float du2 = ico2.getMaxU();
		float dv2 = ico2.getMaxV();

		w = 0.1875;
		h = 0.325;

		v5.addVertexWithUV(0.5-w, 0.5-h, 0, u2, v2);
		v5.addVertexWithUV(0.5+w, 0.5-h, 0, du2, v2);
		v5.addVertexWithUV(0.5+w, 0.5+h, 0, du2, dv2);
		v5.addVertexWithUV(0.5-w, 0.5+h, 0, u2, dv2);

		v5.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		TessellatorVertexList v5 = new TessellatorVertexList();
		TileEntityCrystalGlow te = (TileEntityCrystalGlow)world.getTileEntity(x, y, z);
		ForgeDirection dir = te.getDirection();
		ForgeDirection longAxis = te.getLongAxis();
		ForgeDirection p = ReikaDirectionHelper.getPerpendicularDirections(dir).get(0);
		int meta = world.getBlockMetadata(x, y, z);
		CrystalElement e = CrystalElement.elements[meta];
		Tessellator.instance.addTranslation(x, y, z);
		IIcon ico = te.base.texture.getIcon(0, 0);
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		double h = 0.03125;
		double h2 = 0.0625;

		Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		Tessellator.instance.setColorOpaque_I(0xffffff);
		//------------Base--------------------

		double l = 0.1875;
		double w = 0.0875;
		double el = 0.075;
		double ew = 0.075;
		if (longAxis == p || longAxis == p.getOpposite()) {
			double d = l;
			l = w;
			w = d;

			d = el;
			el = ew;
			ew = d;
		}

		//-------------------Top face -------------
		v5.addVertexWithUV(0.5-w-ew, h, 0.5+l, u, dv);
		v5.addVertexWithUV(0.5+w+ew, h, 0.5+l, du, dv);
		v5.addVertexWithUV(0.5+w+ew, h, 0.5-l, du, v);
		v5.addVertexWithUV(0.5-w-ew, h, 0.5-l, u, v);

		v5.addVertexWithUV(0.5-w-ew, h, 0.5-l, u, dv);
		v5.addVertexWithUV(0.5+w+ew, h, 0.5-l, du, dv);
		v5.addVertexWithUV(0.5+w, h, 0.5-l-el, du, v);
		v5.addVertexWithUV(0.5-w, h, 0.5-l-el, u, v);

		v5.addVertexWithUV(0.5-w, h, 0.5+l+el, u, v);
		v5.addVertexWithUV(0.5+w, h, 0.5+l+el, du, v);
		v5.addVertexWithUV(0.5+w+ew, h, 0.5+l, du, dv);
		v5.addVertexWithUV(0.5-w-ew, h, 0.5+l, u, dv);
		//--------------------------------------------

		v5.addVertexWithUV(0.5+w+ew, 0, 0.5-l, u, v);
		v5.addVertexWithUV(0.5+w+ew, h, 0.5-l, du, v);
		v5.addVertexWithUV(0.5+w+ew, h, 0.5+l, du, dv);
		v5.addVertexWithUV(0.5+w+ew, 0, 0.5+l, u, dv);

		v5.addVertexWithUV(0.5-w-ew, 0, 0.5+l, u, dv);
		v5.addVertexWithUV(0.5-w-ew, h, 0.5+l, du, dv);
		v5.addVertexWithUV(0.5-w-ew, h, 0.5-l, du, v);
		v5.addVertexWithUV(0.5-w-ew, 0, 0.5-l, u, v);

		v5.addVertexWithUV(0.5-w, 0, 0.5+l+el, u, v);
		v5.addVertexWithUV(0.5+w, 0, 0.5+l+el, du, v);
		v5.addVertexWithUV(0.5+w, h, 0.5+l+el, du, dv);
		v5.addVertexWithUV(0.5-w, h, 0.5+l+el, u, dv);

		v5.addVertexWithUV(0.5-w, h, 0.5-l-el, u, v);
		v5.addVertexWithUV(0.5+w, h, 0.5-l-el, du, v);
		v5.addVertexWithUV(0.5+w, 0, 0.5-l-el, du, dv);
		v5.addVertexWithUV(0.5-w, 0, 0.5-l-el, u, dv);

		v5.addVertexWithUV(0.5+w, h, 0.5+l+el, u, v);
		v5.addVertexWithUV(0.5+w, 0, 0.5+l+el, du, v);
		v5.addVertexWithUV(0.5+w+ew, 0, 0.5+l, du, dv);
		v5.addVertexWithUV(0.5+w+ew, h, 0.5+l, u, dv);

		v5.addVertexWithUV(0.5-w-ew, h, 0.5+l, u, dv);
		v5.addVertexWithUV(0.5-w-ew, 0, 0.5+l, du, dv);
		v5.addVertexWithUV(0.5-w, 0, 0.5+l+el, du, v);
		v5.addVertexWithUV(0.5-w, h, 0.5+l+el, u, v);

		v5.addVertexWithUV(0.5+w+ew, h, 0.5-l, u, dv);
		v5.addVertexWithUV(0.5+w+ew, 0, 0.5-l, du, dv);
		v5.addVertexWithUV(0.5+w, 0, 0.5-l-el, du, v);
		v5.addVertexWithUV(0.5+w, h, 0.5-l-el, u, v);

		v5.addVertexWithUV(0.5-w, h, 0.5-l-el, u, v);
		v5.addVertexWithUV(0.5-w, 0, 0.5-l-el, du, v);
		v5.addVertexWithUV(0.5-w-ew, 0, 0.5-l, du, dv);
		v5.addVertexWithUV(0.5-w-ew, h, 0.5-l, u, dv);

		switch(dir) {
		case DOWN:
			v5.invertY();
			break;
		case EAST:
			v5.rotateYtoX();
			break;
		case NORTH:
			v5.rotateYtoZ();
			v5.invertZ();
			break;
		case SOUTH:
			v5.rotateYtoZ();
			break;
		case WEST:
			v5.rotateYtoX();
			v5.invertX();
			break;
		default:
			break;
		}

		v5.render();
		v5.clear();

		ico = e.getOverbrightIcon();
		if (te.isIridescent)
			ico = ChromaBlocks.RAINBOWCRYSTAL.getBlockInstance().blockIcon;
		else if (te.isRainbow)
			ico = ChromaIcons.ALLCOLORS.getIcon();
		float u2 = ico.getMinU();
		float v2 = ico.getMinV();
		float du2 = ico.getMaxU();
		float dv2 = ico.getMaxV();

		//Tessellator.instance.setColorOpaque_I(ReikaColorAPI.mixColors(e.getColor(), 0xffffff, 0.5F));
		Tessellator.instance.setBrightness(240);
		//------------------Lamp----------------
		l = 0.1875;
		w = 0.09375;
		el = 0.03125;
		ew = 0.03125;

		if (longAxis == p || longAxis == p.getOpposite()) {
			double d = l;
			l = w;
			w = d;

			d = el;
			el = ew;
			ew = d;
		}

		//-------------------Top face -------------
		v5.addVertexWithUV(0.5-w-ew, h+h2, 0.5+l, u2, dv2);
		v5.addVertexWithUV(0.5+w+ew, h+h2, 0.5+l, du2, dv2);
		v5.addVertexWithUV(0.5+w+ew, h+h2, 0.5-l, du2, v2);
		v5.addVertexWithUV(0.5-w-ew, h+h2, 0.5-l, u2, v2);

		v5.addVertexWithUV(0.5-w-ew, h+h2, 0.5-l, u2, dv2);
		v5.addVertexWithUV(0.5+w+ew, h+h2, 0.5-l, du2, dv2);
		v5.addVertexWithUV(0.5+w, h+h2, 0.5-l-el, du2, v2);
		v5.addVertexWithUV(0.5-w, h+h2, 0.5-l-el, u2, v2);

		v5.addVertexWithUV(0.5-w, h+h2, 0.5+l+el, u2, v2);
		v5.addVertexWithUV(0.5+w, h+h2, 0.5+l+el, du2, v2);
		v5.addVertexWithUV(0.5+w+ew, h+h2, 0.5+l, du2, dv2);
		v5.addVertexWithUV(0.5-w-ew, h+h2, 0.5+l, u2, dv2);
		//--------------------------------------------

		v5.addVertexWithUV(0.5+w+ew, h, 0.5-l, u2, v2);
		v5.addVertexWithUV(0.5+w+ew, h+h2, 0.5-l, du2, v2);
		v5.addVertexWithUV(0.5+w+ew, h+h2, 0.5+l, du2, dv2);
		v5.addVertexWithUV(0.5+w+ew, h, 0.5+l, u2, dv2);

		v5.addVertexWithUV(0.5-w-ew, h, 0.5+l, u2, dv2);
		v5.addVertexWithUV(0.5-w-ew, h+h2, 0.5+l, du2, dv2);
		v5.addVertexWithUV(0.5-w-ew, h+h2, 0.5-l, du2, v2);
		v5.addVertexWithUV(0.5-w-ew, h, 0.5-l, u2, v2);

		v5.addVertexWithUV(0.5-w, h, 0.5+l+el, u2, v2);
		v5.addVertexWithUV(0.5+w, h, 0.5+l+el, du2, v2);
		v5.addVertexWithUV(0.5+w, h+h2, 0.5+l+el, du2, dv2);
		v5.addVertexWithUV(0.5-w, h+h2, 0.5+l+el, u2, dv2);

		v5.addVertexWithUV(0.5-w, h+h2, 0.5-l-el, u2, v2);
		v5.addVertexWithUV(0.5+w, h+h2, 0.5-l-el, du2, v2);
		v5.addVertexWithUV(0.5+w, h, 0.5-l-el, du2, dv2);
		v5.addVertexWithUV(0.5-w, h, 0.5-l-el, u2, dv2);

		v5.addVertexWithUV(0.5+w, h+h2, 0.5+l+el, u2, v2);
		v5.addVertexWithUV(0.5+w, h, 0.5+l+el, du2, v2);
		v5.addVertexWithUV(0.5+w+ew, h, 0.5+l, du2, dv2);
		v5.addVertexWithUV(0.5+w+ew, h+h2, 0.5+l, u2, dv2);

		v5.addVertexWithUV(0.5-w-ew, h+h2, 0.5+l, u2, dv2);
		v5.addVertexWithUV(0.5-w-ew, h, 0.5+l, du2, dv2);
		v5.addVertexWithUV(0.5-w, h, 0.5+l+el, du2, v2);
		v5.addVertexWithUV(0.5-w, h+h2, 0.5+l+el, u2, v2);

		v5.addVertexWithUV(0.5+w+ew, h+h2, 0.5-l, u2, dv2);
		v5.addVertexWithUV(0.5+w+ew, h, 0.5-l, du2, dv2);
		v5.addVertexWithUV(0.5+w, h, 0.5-l-el, du2, v2);
		v5.addVertexWithUV(0.5+w, h+h2, 0.5-l-el, u2, v2);

		v5.addVertexWithUV(0.5-w, h+h2, 0.5-l-el, u2, v2);
		v5.addVertexWithUV(0.5-w, h, 0.5-l-el, du2, v2);
		v5.addVertexWithUV(0.5-w-ew, h, 0.5-l, du2, dv2);
		v5.addVertexWithUV(0.5-w-ew, h+h2, 0.5-l, u2, dv2);


		switch(dir) {
		case DOWN:
			v5.invertY();
			break;
		case EAST:
			v5.rotateYtoX();
			break;
		case NORTH:
			v5.rotateYtoZ();
			v5.invertZ();
			break;
		case SOUTH:
			v5.rotateYtoZ();
			break;
		case WEST:
			v5.rotateYtoX();
			v5.invertX();
			break;
		default:
			break;
		}

		v5.render();

		Tessellator.instance.addTranslation(-x, -y, -z);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return 0;
	}

}

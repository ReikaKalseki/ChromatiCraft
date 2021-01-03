/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import java.util.Collection;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Instantiable.Rendering.RenderPolygon;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.BlockPolygonizer;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PolyCrystal {

	private final BlockArray blocks;

	private Collection<RenderPolygon> polygons;

	private static final ColorBlendList colors;

	static {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			colors = createColorList();
		else
			colors = null;
	}

	@SideOnly(Side.CLIENT)
	private static ColorBlendList createColorList() {
		return new ColorBlendList(80, ChromaFX.getChromaColorTiles());
	}

	public PolyCrystal() {
		this(new BlockArray());
	}

	public PolyCrystal(BlockArray arr) {
		blocks = arr;
	}

	public PolyCrystal addBlock(int x, int y, int z) {
		blocks.addBlockCoordinate(x, y, z);
		this.update();
		return this;
	}

	public PolyCrystal removeBlock(int x, int y, int z) {
		blocks.remove(x, y, z);
		this.update();
		return this;
	}

	public void update() {
		polygons = BlockPolygonizer.calcVerticesForBlocks(blocks);
	}

	public void writeToNBT(String label, NBTTagCompound NBT) {
		blocks.writeToNBT(label, NBT);
	}

	public void readFromNBT(String label, NBTTagCompound NBT) {
		blocks.readFromNBT(label, NBT);
		this.update();
	}

	public AxisAlignedBB asAABB() {
		return blocks.asAABB();
	}

	@SideOnly(Side.CLIENT)
	public void render(Tessellator v5, float ptick) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDepthMask(false);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_ALPHA_TEST);

		if (polygons.isEmpty() && !blocks.isEmpty()) {
			this.update();
		}

		v5.startDrawing(GL11.GL_TRIANGLES);
		v5.setBrightness(240);
		v5.setNormal(0, 1, 0);
		for (RenderPolygon p : polygons) {
			int c = colors.getColor(System.currentTimeMillis()+p.locationHash()/40D);
			c = ReikaColorAPI.mixColors(c, 0xffffff, 0.125F);
			v5.setColorRGBA_I(c, 192);
			v5.addVertex(p.pos1.xCoord, p.pos1.yCoord, p.pos1.zCoord);
			v5.addVertex(p.pos2.xCoord, p.pos2.yCoord, p.pos2.zCoord);
			v5.addVertex(p.pos3.xCoord, p.pos3.yCoord, p.pos3.zCoord);
		}
		v5.draw();

		/*
		v5.startDrawing(GL11.GL_LINE_STRIP);
		v5.setBrightness(240);
		v5.setNormal(0, 1, 0);

		for (RenderPolygon p : polygons) {
			int c = colors.getColor(System.currentTimeMillis()+p.locationHash()/40D);
			c = ReikaColorAPI.mixColors(c, 0xffffff, 0.125F);
			v5.setColorRGBA_I(c, 255);
			v5.addVertex(p.pos1.xCoord, p.pos1.yCoord, p.pos1.zCoord);
			v5.addVertex(p.pos2.xCoord, p.pos2.yCoord, p.pos2.zCoord);
			v5.addVertex(p.pos3.xCoord, p.pos3.yCoord, p.pos3.zCoord);
		}
		v5.draw();
		 */

		GL11.glPopAttrib();
	}

}

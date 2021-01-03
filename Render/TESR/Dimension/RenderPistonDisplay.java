/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR.Dimension;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.Dimension.Structure.PistonTape.BlockPistonController.TilePistonDisplay;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.DoorKey;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

public class RenderPistonDisplay extends ChromaRenderBase {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TilePistonDisplay te = (TilePistonDisplay)tile;
		if (MinecraftForgeClient.getRenderPass() != 1)
			return;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaRenderHelper.disableEntityLighting();
		//GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		this.renderDisplay(te);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	private void renderDisplay(TilePistonDisplay te) {
		ForgeDirection dir = te.getFacing();
		List<DoorKey> li = te.getDisplayList();
		if (li.isEmpty())
			return;
		//double s = 0.5;//0.75;//1.5;
		double tw = 1.25;//1;
		double th = 1.25;//0.5;
		double o = 0.03125/2;
		double y2 = +th-o;
		double y1 = -th+o;
		double dy = (y2-y1)/li.size();
		double y = y2;
		GL11.glTranslated(0.5, 0.5, -o);
		GL11.glRotated(180, 0, 0, 1);
		switch(dir) {
			case NORTH:
				break;
			case SOUTH:
				GL11.glRotated(180, 0, 1, 0);
				GL11.glTranslated(0, 0, -1-o*2);
				break;
			case WEST:
				GL11.glRotated(270, 0, 1, 0);
				GL11.glTranslated(tw/2, 0, -0.5-o);
				break;
			case EAST:
				GL11.glRotated(90, 0, 1, 0);
				GL11.glTranslated(-tw/2, 0, -0.5-o);
				break;
			default:
				break;
		}
		Tessellator v5 = Tessellator.instance;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		v5.startDrawingQuads();
		v5.setColorRGBA_I(0x0, 255);
		v5.addVertex(-tw, th, 0);
		v5.addVertex(tw, th, 0);
		v5.addVertex(tw, -th, 0);
		v5.addVertex(-tw, -th, 0);
		v5.draw();
		double pad = o*0.5;
		double gap = 0.03125/2;
		for (int door = 0; door < li.size(); door++) {
			DoorKey d = li.get(door);
			boolean active = te.isActive() && te.getActiveDoor() == door;
			GL11.glPushMatrix();
			GL11.glTranslated(0, -y, 0);
			double sw = 0.0625+0.03125*(d.colorCount-1);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			v5.startDrawingQuads();
			double x = 0;
			double h = th*2/li.size()-gap;
			double w = (tw*2/d.colorCount)*(1-sw)-pad/(d.colorCount*2)-gap;
			for (int i = 0; i < d.colorCount; i++) {
				double ox = pad+i*w-tw+gap*d.colorCount/2D;
				double t = System.identityHashCode(d)*2.8734+i*3.47820+System.currentTimeMillis()/300D;
				int a = active ? 255 : 230+(int)(25*Math.sin(t));
				v5.setColorRGBA_I(d.getValue(i).getRenderColor(), a);
				v5.addVertex(ox, h, 0);
				v5.addVertex(w+ox, h, 0);
				v5.addVertex(w+ox, 0, 0);
				v5.addVertex(ox, 0, 0);
			}
			v5.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(1, 1, 1, 1);
			double fs = 0.03125*Math.min(tw, th)/1.5/2;
			GL11.glScaled(fs, fs, fs);
			FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
			String sg = String.valueOf(d.value);
			while(sg.length() < d.colorCount) {
				sg = "0"+sg;
			}
			double ds = Math.max(tw, th);
			double ox = -(d.colorCount-3)/fs*0.275-0.25/fs;
			if (d.colorCount == 4) {
				ox += 0.175/fs;
			}
			GL11.glTranslated(-fr.getStringWidth(sg)/2D+(1-sw)*w/fs+sw*1.35/fs*ds-ox, fr.FONT_HEIGHT/2D*ds*1.35, 0);
			//GL11.glTranslated((1-sw)/fs*0.9375-ox, fr.FONT_HEIGHT/2D*ds, 0);
			fr.drawString(sg, 0, 0, active ? 0xffffff : 0xb0b0b0);
			y -= dy;
			GL11.glPopMatrix();
		}
	}
}

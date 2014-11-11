/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderLivingEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Items.Tools.ItemExcavator;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Models.ColorizableSlimeModel;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ItemMagicRegistry;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Instantiable.Event.RenderItemInSlotEvent;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChromaClientEventController {

	public static final ChromaClientEventController instance = new ChromaClientEventController();

	protected static final Random rand = new Random();

	private boolean editedSlimeModel = false;

	private ChromaClientEventController() {

	}

	@SubscribeEvent
	public void slimeColorizer(RenderLivingEvent.Pre ev) {
		EntityLivingBase e = ev.entity;
		RendererLivingEntity r = ev.renderer;
		if (!editedSlimeModel && e.getClass() == EntitySlime.class) {
			r.mainModel = new ColorizableSlimeModel(16);
			editedSlimeModel = true;
			ChromatiCraft.instance.logger.log("Overriding Slime Renderer Core Model.");
		}
	}

	@SubscribeEvent
	public void drawHighlight(DrawBlockHighlightEvent evt) {
		if (evt.target != null && evt.target.typeOfHit == MovingObjectType.BLOCK) {
			if (evt.currentItem != null && ChromaItems.EXCAVATOR.matchWith(evt.currentItem)) {
				int x = evt.target.blockX;
				int y = evt.target.blockY;
				int z = evt.target.blockZ;
				World world = Minecraft.getMinecraft().theWorld;
				Block id = world.getBlock(x, y, z);
				if (id != Blocks.air) {
					int meta = world.getBlockMetadata(x, y, z);
					GL11.glPushMatrix();
					double p2 = x-TileEntityRendererDispatcher.staticPlayerX;
					double p4 = y-TileEntityRendererDispatcher.staticPlayerY;
					double p6 = z-TileEntityRendererDispatcher.staticPlayerZ;
					GL11.glTranslated(p2, p4, p6);
					BlockArray blocks = new BlockArray();
					blocks.maxDepth = ItemExcavator.MAX_DEPTH-1;
					blocks.recursiveAddWithMetadata(world, x, y, z, id, meta);
					ReikaRenderHelper.prepareGeoDraw(true);
					Tessellator v5 = Tessellator.instance;
					double o = 0.0125;
					int r = 255;
					int g = 255;
					int b = 255;
					for (int i = 0; i < blocks.getSize(); i++) {
						int[] xyz = blocks.getNthBlock(i);
						int dx = xyz[0]-x;
						int dy = xyz[1]-y;
						int dz = xyz[2]-z;
						v5.addTranslation(dx, dy, dz);
						v5.startDrawing(GL11.GL_LINE_LOOP);
						v5.setBrightness(240);
						v5.setColorRGBA(r, g, b, 255);
						v5.addVertex(0-o, 0-o, 0-o);
						v5.addVertex(1+o, 0-o, 0-o);
						v5.addVertex(1+o, 0-o, 1+o);
						v5.addVertex(0-o, 0-o, 1+o);
						v5.draw();

						v5.startDrawing(GL11.GL_LINE_LOOP);
						v5.setBrightness(240);
						v5.setColorRGBA(r, g, b, 255);
						v5.addVertex(0-o, 1+o, 0-o);
						v5.addVertex(1+o, 1+o, 0-o);
						v5.addVertex(1+o, 1+o, 1+o);
						v5.addVertex(0-o, 1+o, 1+o);
						v5.draw();

						v5.startDrawing(GL11.GL_LINES);
						v5.setBrightness(240);
						v5.setColorRGBA(r, g, b, 255);
						v5.addVertex(0-o, 0-o, 0-o);
						v5.addVertex(0-o, 1+o, 0-o);

						v5.addVertex(1+o, 0-o, 0-o);
						v5.addVertex(1+o, 1+o, 0-o);

						v5.addVertex(0-o, 0-o, 1+o);
						v5.addVertex(0-o, 1+o, 1+o);

						v5.addVertex(1+o, 0-o, 1+o);
						v5.addVertex(1+o, 1+o, 1+o);
						v5.draw();

						v5.startDrawingQuads();
						v5.setBrightness(240);
						v5.setColorRGBA(r, g, b, 64);
						v5.addVertex(0-o, 0-o, 0-o);
						v5.addVertex(1+o, 0-o, 0-o);
						v5.addVertex(1+o, 0-o, 1+o);
						v5.addVertex(0-o, 0-o, 1+o);

						v5.addVertex(0-o, 1+o, 1+o);
						v5.addVertex(1+o, 1+o, 1+o);
						v5.addVertex(1+o, 1+o, 0-o);
						v5.addVertex(0-o, 1+o, 0-o);

						v5.addVertex(1+o, 0-o, 0-o);
						v5.addVertex(1+o, 1+o, 0-o);
						v5.addVertex(1+o, 1+o, 1+o);
						v5.addVertex(1+o, 0-o, 1+o);

						v5.addVertex(0-o, 0-o, 1+o);
						v5.addVertex(0-o, 1+o, 1+o);
						v5.addVertex(0-o, 1+o, 0-o);
						v5.addVertex(0-o, 0-o, 0-o);

						v5.addVertex(1+o, 0-o, 1+o);
						v5.addVertex(1+o, 1+o, 1+o);
						v5.addVertex(0-o, 1+o, 1+o);
						v5.addVertex(0-o, 0-o, 1+o);

						v5.addVertex(0-o, 0-o, 0-o);
						v5.addVertex(0-o, 1+o, 0-o);
						v5.addVertex(1+o, 1+o, 0-o);
						v5.addVertex(1+o, 0-o, 0-o);
						v5.draw();
						v5.addTranslation(-dx, -dy, -dz);
					}
					ReikaRenderHelper.exitGeoDraw();
					GL11.glPopMatrix();
				}
			}
		}
	}

	@SubscribeEvent
	public void renderItemTags(RenderItemInSlotEvent evt) {
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			if (evt.hasItem() && evt.isHovered()) {
				ItemStack is = evt.getItem();
				ElementTagCompound tag = ItemMagicRegistry.instance.getItemValue(is);
				if (tag != null) {
					Tessellator v5 = Tessellator.instance;
					int i = tag.tagCount();
					GL11.glDisable(GL11.GL_CULL_FACE);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glEnable(GL11.GL_BLEND);
					double z = 0;
					int w = 8;
					int h = 8;
					int mx = 0;//evt.getRelativeMouseX();
					int my = 0;//evt.getRelativeMouseY();
					int x2 = evt.slotX-i*w+mx;
					int y2 = evt.slotY-w+my;
					//if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
					//	w = 16;
					//	x2 -= 8;
					//}
					int r = 1;
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					v5.startDrawingQuads();
					v5.setColorRGBA(127, 0, 255, 255);
					v5.addVertex(x2-r, y2-r, z);
					v5.addVertex(x2+w*i+r, y2-r, z);
					v5.addVertex(x2+w*i+r, y2+h+r, z);
					v5.addVertex(x2-r, y2+h+r, z);
					v5.draw();
					v5.startDrawingQuads();
					v5.setColorRGBA(0, 0, 0, 255);
					v5.addVertex(x2, y2, z);
					v5.addVertex(x2+w*i, y2, z);
					v5.addVertex(x2+w*i, y2+h, z);
					v5.addVertex(x2, y2+h, z);
					v5.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					for (CrystalElement e : tag.elementSet()) {
						IIcon ico = e.getFaceRune();
						float u = ico.getMinU();
						float v = ico.getMinV();
						float du = ico.getMaxU();
						float dv = ico.getMaxV();
						int x = evt.slotX-i*w+mx;
						int y = evt.slotY-w+my;
						i--;/*
						if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
							GL11.glPushMatrix();
							double sc = 0.5;
							GL11.glScaled(sc, sc, sc);
							String s = Integer.toString(tag.getValue(e), 10).toUpperCase();//String.format("%d", tag.getValue(e));
							int color = e.getColor() | 0xff000000;
							FontRenderer f = Minecraft.getMinecraft().fontRenderer;
							ReikaGuiAPI.instance.drawCenteredStringNoShadow(f, s, (int)((x+w-0)/sc), (int)((y+w-6)/sc), color);
							GL11.glTranslated(1, 0, 0);
							ReikaGuiAPI.instance.drawCenteredStringNoShadow(f, s, (int)((x+w-0)/sc), (int)((y+w-6)/sc), color);
							GL11.glPopMatrix();
						}
						else {*/
						ReikaTextureHelper.bindTerrainTexture();
						v5.startDrawingQuads();
						v5.setColorOpaque_I(0xffffff);
						v5.addVertexWithUV(x, y, z, u, v);
						v5.addVertexWithUV(x+w, y, z, du, v);
						v5.addVertexWithUV(x+w, y+w, z, du, dv);
						v5.addVertexWithUV(x, y+w, z, u, dv);
						v5.draw();
						//}
					}
					GL11.glEnable(GL11.GL_CULL_FACE);
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glDisable(GL11.GL_BLEND);
				}
			}
		}
	}
}

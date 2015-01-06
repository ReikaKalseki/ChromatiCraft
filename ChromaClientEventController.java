/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import thaumcraft.api.research.ResearchItem;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemBuilderWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemCaptureWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemDuplicationWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemExcavator;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Models.ColorizableSlimeModel;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ItemMagicRegistry;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.KeybindHandler.KeyPressEvent;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Coordinate;
import Reika.DragonAPI.Instantiable.Data.StructuredBlockArray;
import Reika.DragonAPI.Instantiable.Event.NEIRecipeCheckEvent;
import Reika.DragonAPI.Instantiable.Event.RenderFirstPersonItemEvent;
import Reika.DragonAPI.Instantiable.Event.RenderItemInSlotEvent;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
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
	public void addAuraCleanerScribbles(DrawScreenEvent.Post evt) {
		if (evt.gui != null && evt.gui.getClass().getSimpleName().equals("GuiResearchRecipe")) {
			try {
				Class c = evt.gui.getClass();
				Field res = c.getDeclaredField("research");
				res.setAccessible(true);
				ResearchItem item = (ResearchItem)res.get(evt.gui);
				if (item.key.equals("WARPPROOF")) {
					int j = (evt.gui.width - 256) / 2;
					int k = (evt.gui.height - 181) / 2;

					ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/eldritch_s.png");
					//Tessellator v5 = Tessellator.instance;
					//v5.startDrawingQuads();
					int x = j-20;
					int x2 = j+133;
					int y = k-20;
					int y2 = k+140;
					int w = 146;
					int h = 212;
					int h2 = 65;
					int u = 0;
					int v = 0;
					GL11.glAlphaFunc(GL11.GL_GREATER, 1/255F);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					GL11.glDisable(GL11.GL_CULL_FACE);
					BlendMode.DEFAULT.apply();
					GL11.glColor4f(1, 1, 1, 0.35F);
					evt.gui.drawTexturedModalRect(x, y, u, v, w, h);
					GL11.glColor4f(1, 1, 1, 0.4F);
					evt.gui.drawTexturedModalRect(x2, y, u+64, v, w, h);
					ResourceLocation loc = new ResourceLocation("thaumcraft:textures/misc/eldritchajor2.png");
					Minecraft.getMinecraft().renderEngine.bindTexture(loc);
					GL11.glColor4f(1, 1, 1, 0.95F);
					evt.gui.drawTexturedModalRect(x2, y2, u, 146, w, h2);
					GL11.glColor4f(1, 1, 1, 0.6F);
					loc = new ResourceLocation("thaumcraft:textures/misc/eldritchajor1.png");
					Minecraft.getMinecraft().renderEngine.bindTexture(loc);
					evt.gui.drawTexturedModalRect(x, y+48, u, v, w, 148);
					GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
					//v5.addVertexWithUV(x, y+w, 0, 0, 1);
					//v5.addVertexWithUV(x+w, y+w, 0, 1, 1);
					//v5.addVertexWithUV(x+w, y, 0, 1, 0);
					//v5.addVertexWithUV(x, y, 0, 0, 0);
					//v5.draw();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	@ModDependent(ModList.NEI)
	public void interceptNEI(NEIRecipeCheckEvent evt) {
		if (this.loadLexiconRecipe(evt.gui, evt.getItem()))
			evt.setCanceled(true);
	}

	private boolean loadLexiconRecipe(GuiContainer gui, ItemStack is) {
		if (is != null && is.getItem() != null) {
			UniqueIdentifier uid = GameRegistry.findUniqueIdentifierFor(is.getItem());
			if (uid != null && uid.modId.equals(ModList.CHROMATICRAFT.modLabel)) {
				EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
				ChromaResearch r = ChromaResearch.getPageFor(is);
				if (r != null && r.playerCanSee(ep) && r.isCrafting() && !r.isVanillaRecipe()) {
					ep.openGui(ChromatiCraft.instance, ChromaGuis.RECIPE.ordinal(), null, r.ordinal(), r.getRecipeIndex(is), 1);
					return true;
				}
			}
		}
		return false;
	}

	@SubscribeEvent
	public void openAbilityGui(KeyPressEvent evt) {
		if (ChromaOptions.KEYBINDABILITY.getState() && evt.key == ChromaClient.key_ability && !evt.lastPressed) {
			Minecraft mc = Minecraft.getMinecraft();
			mc.thePlayer.openGui(ChromatiCraft.instance, ChromaGuis.ABILITY.ordinal(), mc.theWorld, 0, 0, 0);
		}
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
	public void heldMobFirstPerson(RenderFirstPersonItemEvent evt) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		if (ChromaItems.CAPTURE.matchWith(ep.getCurrentEquippedItem())) {
			EntityLiving e = ItemCaptureWand.getMob(ep.getCurrentEquippedItem(), ep.worldObj);
			if (e != null) {
				GL11.glPushMatrix();
				GL11.glTranslated(0, -ep.height+ep.getEyeHeight()+1, -2);
				GL11.glRotated(45, 0, 1, 0);
				GL11.glColor4f(1, 1, 1, 1);
				//GL11.glEnable(GL11.GL_LIGHTING);
				e.setLocationAndAngles(ep.posX, ep.posY, ep.posZ, 0, 0);
				Render r = ReikaEntityHelper.getEntityRenderer(e.getClass());
				if (r == null)
					;//ChromatiCraft.logger.logError("Cannot render "+e+", has no renderer!");
				else
					r.doRender(e, 0, 0, 0, 0, 0);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glPopMatrix();
			}
		}
	}

	@SubscribeEvent
	public void heldMob(RenderLivingEvent.Pre evt) {
		if (evt.entity == Minecraft.getMinecraft().thePlayer) {
			EntityPlayer ep = (EntityPlayer)evt.entity;
			if (ChromaItems.CAPTURE.matchWith(ep.getCurrentEquippedItem())) {
				EntityLiving e = ItemCaptureWand.getMob(ep.getCurrentEquippedItem(), ep.worldObj);
				if (e != null) {
					GL11.glPushMatrix();
					GL11.glRotated(215-ep.rotationYaw, 0, 1, 0);
					GL11.glTranslated(1, -ep.height+ep.getEyeHeight()+0.5, -1);
					Render r = ReikaEntityHelper.getEntityRenderer(e.getClass());
					if (r == null)
						;//ChromatiCraft.logger.logError("Cannot render "+e+", has no renderer!");
					else
						r.doRender(e, 0, 0, 0, 0, 0);
					GL11.glPopMatrix();
				}
			}
		}
	}

	@SubscribeEvent
	public void drawBuilderFrame(DrawBlockHighlightEvent evt) {
		if (evt.target != null && evt.target.typeOfHit == MovingObjectType.BLOCK) {
			if (evt.currentItem != null && ChromaItems.BUILDER.matchWith(evt.currentItem)) {
				ForgeDirection dir = evt.target.sideHit >= 0 ? ForgeDirection.VALID_DIRECTIONS[evt.target.sideHit] : null;
				if (dir != null) {
					World world = evt.player.worldObj;
					int x = evt.target.blockX;
					int y = evt.target.blockY;
					int z = evt.target.blockZ;

					GL11.glPushMatrix();
					double p2 = x-TileEntityRendererDispatcher.staticPlayerX;
					double p4 = y-TileEntityRendererDispatcher.staticPlayerY;
					double p6 = z-TileEntityRendererDispatcher.staticPlayerZ;
					GL11.glTranslated(p2, p4, p6);
					ReikaRenderHelper.prepareGeoDraw(true);
					Tessellator v5 = Tessellator.instance;
					double o = 0;//0.0125;
					int red = 255;
					int green = 255;
					int blue = 255;

					ArrayList<Coordinate> li = ItemBuilderWand.getCoordinatesFor(world, x, y, z, dir);
					for (Coordinate c : li) {
						int dx = c.xCoord-x;
						int dy = c.yCoord-y;
						int dz = c.zCoord-z;
						v5.addTranslation(dx, dy, dz);
						v5.startDrawing(GL11.GL_LINE_LOOP);
						v5.setBrightness(240);
						v5.setColorRGBA(red, green, blue, 255);
						v5.addVertex(0-o, 0-o, 0-o);
						v5.addVertex(1+o, 0-o, 0-o);
						v5.addVertex(1+o, 0-o, 1+o);
						v5.addVertex(0-o, 0-o, 1+o);
						v5.draw();

						v5.startDrawing(GL11.GL_LINE_LOOP);
						v5.setBrightness(240);
						v5.setColorRGBA(red, green, blue, 255);
						v5.addVertex(0-o, 1+o, 0-o);
						v5.addVertex(1+o, 1+o, 0-o);
						v5.addVertex(1+o, 1+o, 1+o);
						v5.addVertex(0-o, 1+o, 1+o);
						v5.draw();

						v5.startDrawing(GL11.GL_LINES);
						v5.setBrightness(240);
						v5.setColorRGBA(red, green, blue, 255);
						v5.addVertex(0-o, 0-o, 0-o);
						v5.addVertex(0-o, 1+o, 0-o);

						v5.addVertex(1+o, 0-o, 0-o);
						v5.addVertex(1+o, 1+o, 0-o);

						v5.addVertex(0-o, 0-o, 1+o);
						v5.addVertex(0-o, 1+o, 1+o);

						v5.addVertex(1+o, 0-o, 1+o);
						v5.addVertex(1+o, 1+o, 1+o);
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
	public void drawPlacerHighlight(DrawBlockHighlightEvent evt) {
		if (evt.target != null && evt.target.typeOfHit == MovingObjectType.BLOCK) {
			if (evt.currentItem != null && ChromaItems.DUPLICATOR.matchWith(evt.currentItem)) {
				StructuredBlockArray blocks = ItemDuplicationWand.getStructureFor(Minecraft.getMinecraft().thePlayer);
				if (blocks != null) {
					blocks.offset(ForgeDirection.VALID_DIRECTIONS[evt.target.sideHit], 1);
					GL11.glPushMatrix();
					int x = evt.target.blockX;
					int y = evt.target.blockY;
					int z = evt.target.blockZ;
					double p2 = x-TileEntityRendererDispatcher.staticPlayerX;
					double p4 = y-TileEntityRendererDispatcher.staticPlayerY;
					double p6 = z-TileEntityRendererDispatcher.staticPlayerZ;
					//GL11.glTranslated(p2, p4, p6);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glEnable(GL11.GL_BLEND);
					Tessellator v5 = Tessellator.instance;
					double o = 0.0125;
					int r = 255;
					int g = 255;
					int b = 255;
					ReikaTextureHelper.bindTerrainTexture();
					for (int i = 0; i < blocks.getSize(); i++) {
						int[] xyz = blocks.getNthBlock(i);
						float dx = xyz[0]+x-(float)TileEntityRendererDispatcher.staticPlayerX;
						float dy = xyz[1]+y-(float)TileEntityRendererDispatcher.staticPlayerY;
						float dz = xyz[2]+z-(float)TileEntityRendererDispatcher.staticPlayerZ;
						Block bk = blocks.getBlockAt(xyz[0], xyz[1], xyz[2]);
						if (bk != null && bk != Blocks.air && bk.getMaterial() != Material.air) {
							v5.addTranslation(dx, dy, dz);
							/*
							GL11.glDisable(GL11.GL_TEXTURE_2D);
							v5.startDrawing(GL11.GL_LINE_LOOP);
							v5.setBrightness(240);
							v5.setColorRGBA(r, g, b, 96);
							v5.addVertex(0, 0, 0);
							v5.addVertex(1, 0, 0);
							v5.addVertex(1, 0, 1);
							v5.addVertex(0, 0, 1);
							v5.draw();

							v5.startDrawing(GL11.GL_LINE_LOOP);
							v5.setBrightness(240);
							v5.setColorRGBA(r, g, b, 96);
							v5.addVertex(0, 1, 0);
							v5.addVertex(1, 1, 0);
							v5.addVertex(1, 1, 1);
							v5.addVertex(0, 1, 1);
							v5.draw();

							v5.startDrawing(GL11.GL_LINES);
							v5.setBrightness(240);
							v5.setColorRGBA(r, g, b, 96);
							v5.addVertex(0, 0, 0);
							v5.addVertex(0, 1, 0);
							v5.addVertex(1, 0, 0);
							v5.addVertex(1, 1, 0);
							v5.addVertex(0, 0, 1);
							v5.addVertex(0, 1, 1);
							v5.addVertex(1, 0, 1);
							v5.addVertex(1, 1, 1);
							v5.draw();
							GL11.glEnable(GL11.GL_TEXTURE_2D);
							 */
							v5.startDrawingQuads();
							v5.setBrightness(240);
							v5.setColorRGBA(r, g, b, 96);
							int meta = blocks.getMetaAt(xyz[0], xyz[1], xyz[2]);
							if (!blocks.hasNonAirBlock(xyz[0], xyz[1]-1, xyz[2])) {
								IIcon ico = RenderBlocks.getInstance().getIconSafe(bk.getIcon(ForgeDirection.DOWN.ordinal(), meta));
								float u = ico.getMinU();
								float du = ico.getMaxU();
								float v = ico.getMinV();
								float dv = ico.getMaxV();
								double en = blocks.hasNonAirBlock(xyz[0], xyz[1], xyz[2]-1) ? 0 : 0-o;
								double es = blocks.hasNonAirBlock(xyz[0], xyz[1], xyz[2]+1) ? 1 : 1+o;
								double ew = blocks.hasNonAirBlock(xyz[0]-1, xyz[1], xyz[2]) ? 0 : 0-o;
								double ee = blocks.hasNonAirBlock(xyz[0]+1, xyz[1], xyz[2]) ? 1 : 1+o;
								v5.addVertexWithUV(ew, 0-o, en, u, v);
								v5.addVertexWithUV(ee, 0-o, en, du, v);
								v5.addVertexWithUV(ee, 0-o, es, du, dv);
								v5.addVertexWithUV(ew, 0-o, es, u, dv);
							}

							if (!blocks.hasNonAirBlock(xyz[0], xyz[1]+1, xyz[2])) {
								IIcon ico = RenderBlocks.getInstance().getIconSafe(bk.getIcon(ForgeDirection.UP.ordinal(), meta));
								float u = ico.getMinU();
								float du = ico.getMaxU();
								float v = ico.getMinV();
								float dv = ico.getMaxV();
								double en = blocks.hasNonAirBlock(xyz[0], xyz[1], xyz[2]-1) ? 0 : 0-o;
								double es = blocks.hasNonAirBlock(xyz[0], xyz[1], xyz[2]+1) ? 1 : 1+o;
								double ew = blocks.hasNonAirBlock(xyz[0]-1, xyz[1], xyz[2]) ? 0 : 0-o;
								double ee = blocks.hasNonAirBlock(xyz[0]+1, xyz[1], xyz[2]) ? 1 : 1+o;
								v5.addVertexWithUV(ew, 1+o, es, u, dv);
								v5.addVertexWithUV(ee, 1+o, es, du, dv);
								v5.addVertexWithUV(ee, 1+o, en, du, v);
								v5.addVertexWithUV(ew, 1+o, en, u, v);
							}

							if (!blocks.hasNonAirBlock(xyz[0]+1, xyz[1], xyz[2])) {
								IIcon ico = RenderBlocks.getInstance().getIconSafe(bk.getIcon(ForgeDirection.EAST.ordinal(), meta));
								float u = ico.getMinU();
								float du = ico.getMaxU();
								float v = ico.getMinV();
								float dv = ico.getMaxV();
								double en = blocks.hasNonAirBlock(xyz[0], xyz[1], xyz[2]-1) ? 0 : 0-o;
								double es = blocks.hasNonAirBlock(xyz[0], xyz[1], xyz[2]+1) ? 1 : 1+o;
								double ed = blocks.hasNonAirBlock(xyz[0], xyz[1]-1, xyz[2]) ? 0 : 0-o;
								double eu = blocks.hasNonAirBlock(xyz[0], xyz[1]+1, xyz[2]) ? 1 : 1+o;
								v5.addVertexWithUV(1+o, ed, en, du, dv);
								v5.addVertexWithUV(1+o, eu, en, du, v);
								v5.addVertexWithUV(1+o, eu, es, u, v);
								v5.addVertexWithUV(1+o, ed, es, u, dv);
							}

							if (!blocks.hasNonAirBlock(xyz[0]-1, xyz[1], xyz[2])) {
								IIcon ico = RenderBlocks.getInstance().getIconSafe(bk.getIcon(ForgeDirection.WEST.ordinal(), meta));
								float u = ico.getMinU();
								float du = ico.getMaxU();
								float v = ico.getMinV();
								float dv = ico.getMaxV();
								double en = blocks.hasNonAirBlock(xyz[0], xyz[1], xyz[2]-1) ? 0 : 0-o;
								double es = blocks.hasNonAirBlock(xyz[0], xyz[1], xyz[2]+1) ? 1 : 1+o;
								double ed = blocks.hasNonAirBlock(xyz[0], xyz[1]-1, xyz[2]) ? 0 : 0-o;
								double eu = blocks.hasNonAirBlock(xyz[0], xyz[1]+1, xyz[2]) ? 1 : 1+o;
								v5.addVertexWithUV(0-o, ed, es, u, dv);
								v5.addVertexWithUV(0-o, eu, es, u, v);
								v5.addVertexWithUV(0-o, eu, en, du, v);
								v5.addVertexWithUV(0-o, ed, en, du, dv);
							}

							if (!blocks.hasNonAirBlock(xyz[0], xyz[1], xyz[2]-1)) {
								IIcon ico = RenderBlocks.getInstance().getIconSafe(bk.getIcon(ForgeDirection.NORTH.ordinal(), meta));
								float u = ico.getMinU();
								float du = ico.getMaxU();
								float v = ico.getMinV();
								float dv = ico.getMaxV();
								double ew = blocks.hasNonAirBlock(xyz[0]-1, xyz[1], xyz[2]) ? 0 : 0-o;
								double ee = blocks.hasNonAirBlock(xyz[0]+1, xyz[1], xyz[2]) ? 1 : 1+o;
								double ed = blocks.hasNonAirBlock(xyz[0], xyz[1]-1, xyz[2]) ? 0 : 0-o;
								double eu = blocks.hasNonAirBlock(xyz[0], xyz[1]+1, xyz[2]) ? 1 : 1+o;
								v5.addVertexWithUV(ew, ed, 0-o, u, dv);
								v5.addVertexWithUV(ew, eu, 0-o, u, v);
								v5.addVertexWithUV(ee, eu, 0-o, du, v);
								v5.addVertexWithUV(ee, ed, 0-o, du, dv);
							}

							if (!blocks.hasNonAirBlock(xyz[0], xyz[1], xyz[2]+1)) {
								IIcon ico = RenderBlocks.getInstance().getIconSafe(bk.getIcon(ForgeDirection.SOUTH.ordinal(), meta));
								float u = ico.getMinU();
								float du = ico.getMaxU();
								float v = ico.getMinV();
								float dv = ico.getMaxV();
								double ew = blocks.hasNonAirBlock(xyz[0]-1, xyz[1], xyz[2]) ? 0 : 0-o;
								double ee = blocks.hasNonAirBlock(xyz[0]+1, xyz[1], xyz[2]) ? 1 : 1+o;
								double ed = blocks.hasNonAirBlock(xyz[0], xyz[1]-1, xyz[2]) ? 0 : 0-o;
								double eu = blocks.hasNonAirBlock(xyz[0], xyz[1]+1, xyz[2]) ? 1 : 1+o;
								v5.addVertexWithUV(ee, ed, 1+o, du, dv);
								v5.addVertexWithUV(ee, eu, 1+o, du, v);
								v5.addVertexWithUV(ew, eu, 1+o, u, v);
								v5.addVertexWithUV(ew, ed, 1+o, u, dv);
							}
							v5.draw();
							v5.addTranslation(-dx, -dy, -dz);
						}
					}
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glPopMatrix();
				}
			}
		}
	}

	@SubscribeEvent
	public void drawExcavatorHighlight(DrawBlockHighlightEvent evt) {
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

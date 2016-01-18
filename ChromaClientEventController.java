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
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import pneumaticCraft.api.client.pneumaticHelmet.BlockTrackEvent;
import pneumaticCraft.api.client.pneumaticHelmet.InventoryTrackEvent;
import thaumcraft.api.research.ResearchItem;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper.TileXRays;
import Reika.ChromatiCraft.Auxiliary.ChromaFontRenderer;
import Reika.ChromatiCraft.Auxiliary.ChromaOverlays;
import Reika.ChromatiCraft.Auxiliary.FragmentTab;
import Reika.ChromatiCraft.Auxiliary.MusicLoader;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.TabChromatiCraft;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemBuilderWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemCaptureWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemDuplicationWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemExcavationWand;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Models.ColorizableSlimeModel;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.Registry.ItemElementCalculator;
import Reika.ChromatiCraft.TileEntity.TileEntityStructControl;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.KeybindHandler.KeyPressEvent;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructuredBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Event.NEIRecipeCheckEvent;
import Reika.DragonAPI.Instantiable.Event.ProfileEvent;
import Reika.DragonAPI.Instantiable.Event.Client.CloudRenderEvent;
import Reika.DragonAPI.Instantiable.Event.Client.CreativeTabGuiRenderEvent;
import Reika.DragonAPI.Instantiable.Event.Client.EntityRenderingLoopEvent;
import Reika.DragonAPI.Instantiable.Event.Client.FarClippingPlaneEvent;
import Reika.DragonAPI.Instantiable.Event.Client.NightVisionBrightnessEvent;
import Reika.DragonAPI.Instantiable.Event.Client.PlayMusicEvent;
import Reika.DragonAPI.Instantiable.Event.Client.RenderFirstPersonItemEvent;
import Reika.DragonAPI.Instantiable.Event.Client.RenderItemInSlotEvent;
import Reika.DragonAPI.Instantiable.Event.Client.SoundVolumeEvent;
import Reika.DragonAPI.Instantiable.Event.Client.TileEntityRenderEvent;
import Reika.DragonAPI.Instantiable.IO.CustomMusic;
import Reika.DragonAPI.Instantiable.IO.EnumSound;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChromaClientEventController {

	public static final ChromaClientEventController instance = new ChromaClientEventController();

	protected static final Random rand = new Random();
	protected static final RenderItem itemRender = new RenderItem();

	private boolean editedSlimeModel = false;

	private ChromaClientEventController() {

	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void ensureWhiteout(ProfileEvent evt) {
		if (evt.sectionName.equals("gui")) {
			if (ChromaOverlays.instance.isWashoutActive()) {
				Minecraft.getMinecraft().gameSettings.hideGUI = false;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void ensureMusic(CloudRenderEvent evt) {
		if (Minecraft.getMinecraft().theWorld.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			evt.setResult(Result.ALLOW);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void ensureMusic(SoundVolumeEvent evt) {
		if (evt.sound instanceof CustomMusic) {
			CustomMusic cm = (CustomMusic)evt.sound;
			if (cm.path.toLowerCase().contains("chromaticraft") && cm.path.contains(MusicLoader.instance.musicPath)) {
				evt.volume = Math.max(0.1F, Minecraft.getMinecraft().gameSettings.getSoundLevel(ChromaClient.chromaCategory));
			}
		}
	}

	/* Does not work (Sound Engine cannot handle values outside [0, 2.0])
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void crystalPitchDing(SoundPitchEvent evt) {
		if (evt.sound instanceof EnumSound) {
			EnumSound es = (EnumSound)evt.sound;
			if (es.sound == ChromaSounds.DING) {
				evt.pitch = (float)evt.unclampedPitch;
			}
		}
	}
	 */


	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void crystalPitchDing(PlaySoundEvent17 evt) {
		if (evt.sound instanceof EnumSound) {
			EnumSound es = (EnumSound)evt.sound;
			if (es.sound == ChromaSounds.DING) {
				if (es.getPitch() > 2) {
					evt.result = new EnumSound(ChromaSounds.DING_HI, es.posX, es.posY, es.posZ, es.volume, es.pitch/4F, es.attenuate);
				}
				else if (es.getPitch() < 0.5) {
					evt.result = new EnumSound(ChromaSounds.DING_LO, es.posX, es.posY, es.posZ, es.volume, es.pitch*4F, es.attenuate);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void overrideMusic(PlayMusicEvent evt) {
		if (Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.provider.dimensionId == ExtraChromaIDs.DIMID.getValue())
			evt.setCanceled(true);
	}

	@ModDependent(ModList.PNEUMATICRAFT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void hideStructures(BlockTrackEvent evt) {
		if (evt.world.getBlock(evt.x, evt.y, evt.z) instanceof BlockStructureShield) {
			evt.setCanceled(true);
		}
	}

	@ModDependent(ModList.PNEUMATICRAFT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void hideStructures(InventoryTrackEvent evt) {
		if (evt.getTileEntity() instanceof TileEntityLootChest) {
			evt.setCanceled(true);
		}
		else if (evt.getTileEntity() instanceof TileEntityStructControl) {
			evt.setCanceled(true);
		}
		else if (ReikaInventoryHelper.checkForItem(ChromaItems.FRAGMENT.getItemInstance(), evt.getInventory())) {
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void customCreativeTab(CreativeTabGuiRenderEvent evt) {
		if (evt.tab instanceof TabChromatiCraft || evt.tab instanceof FragmentTab) {
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ALPHA.apply();
			String s = evt.tab.hasSearchBar() ? "Textures/GUIs/creativetab_search.png" : "Textures/GUIs/creativetab.png";
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, s);
			evt.gui.drawTexturedModalRect(evt.gui.guiLeft, evt.gui.guiTop, 0, 0, evt.guiXSize, evt.guiYSize);
			BlendMode.DEFAULT.apply();
			evt.gui.drawTexturedModalRect(evt.gui.guiLeft, evt.gui.guiTop, 0, 0, evt.guiXSize, evt.guiYSize);
			ChromaFontRenderer.FontType.GUI.drawString(evt.tab.getTabLabel(), evt.gui.guiLeft+4, evt.gui.guiTop+5, 0xffffff);
			GL11.glDisable(GL11.GL_BLEND);
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void noDimClipping(FarClippingPlaneEvent evt) {
		if (Minecraft.getMinecraft().theWorld.provider.dimensionId == ExtraChromaIDs.DIMID.getValue())
			evt.farClippingPlaneDistance = 200000F;
	}

	@SubscribeEvent
	public void cancelBoostingNightVision(NightVisionBrightnessEvent evt) {
		PotionEffect pot = evt.player.getActivePotionEffect(Potion.nightVision);
		if (pot != null && pot.getAmplifier() == 4) {
			evt.brightness = 0;
		}
	}

	@SubscribeEvent
	public void cancelDimNightVision(NightVisionBrightnessEvent evt) {
		if (evt.player.worldObj.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			evt.brightness = 0;
		}
	}

	@SubscribeEvent
	public void renderBreadcrumb(EntityRenderingLoopEvent evt) {
		AbilityHelper.instance.renderPath(Minecraft.getMinecraft().thePlayer);
	}

	@SubscribeEvent
	public void renderSpawners(TileEntityRenderEvent evt) {
		if (evt.tileEntity.worldObj != null && Chromabilities.SPAWNERSEE.enabledOn(Minecraft.getMinecraft().thePlayer)) {
			TileXRays tx = AbilityHelper.instance.getTileEntityXRay(evt.tileEntity);
			if (tx != null) {
				TileEntity te = evt.tileEntity;
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_LIGHTING);
				ReikaRenderHelper.disableEntityLighting();
				GL11.glEnable(GL11.GL_BLEND);
				BlendMode.DEFAULT.apply();
				GL11.glAlphaFunc(GL11.GL_GREATER, 1/255F);
				GL11.glTranslated(evt.renderPosX, evt.renderPosY, evt.renderPosZ);

				Tessellator v5 = Tessellator.instance;

				TileEntityRendererDispatcher.instance.getSpecialRenderer(te).renderTileEntityAt(te, 0, 0, 0, evt.partialTickTime);

				IIcon ico = tx.getTexture();
				if (ico != null) {
					ReikaTextureHelper.bindTerrainTexture();
					int a = 255;//(int)(64+64*Math.sin(System.currentTimeMillis()/400D));
					float u = ico.getMinU();
					float du = ico.getMaxU();
					float v = ico.getMinV();
					float dv = ico.getMaxV();

					v5.startDrawingQuads();
					v5.setColorRGBA_I(0xffffff, a);
					v5.addVertexWithUV(0, 0, 1, u, v);
					v5.addVertexWithUV(1, 0, 1, du, v);
					v5.addVertexWithUV(1, 1, 1, du, dv);
					v5.addVertexWithUV(0, 1, 1, u, dv);

					v5.addVertexWithUV(0, 1, 0, u, v);
					v5.addVertexWithUV(1, 1, 0, du, v);
					v5.addVertexWithUV(1, 0, 0, du, dv);
					v5.addVertexWithUV(0, 0, 0, u, dv);

					v5.addVertexWithUV(1, 1, 0, u, v);
					v5.addVertexWithUV(1, 1, 1, du, v);
					v5.addVertexWithUV(1, 0, 1, du, dv);
					v5.addVertexWithUV(1, 0, 0, u, dv);

					v5.addVertexWithUV(0, 0, 0, u, v);
					v5.addVertexWithUV(0, 0, 1, du, v);
					v5.addVertexWithUV(0, 1, 1, du, dv);
					v5.addVertexWithUV(0, 1, 0, u, dv);

					v5.addVertexWithUV(1, 0, 0, u, v);
					v5.addVertexWithUV(1, 0, 1, du, v);
					v5.addVertexWithUV(0, 0, 1, du, dv);
					v5.addVertexWithUV(0, 0, 0, u, dv);

					v5.addVertexWithUV(0, 1, 0, u, v);
					v5.addVertexWithUV(0, 1, 1, du, v);
					v5.addVertexWithUV(1, 1, 1, du, dv);
					v5.addVertexWithUV(1, 1, 0, u, dv);
					v5.draw();
				}

				GL11.glDisable(GL11.GL_TEXTURE_2D);
				int a = (int)(192+64*Math.sin(System.currentTimeMillis()/400D));
				int c = tx.highlightColor;//0xffffff;

				v5.startDrawing(GL11.GL_LINE_LOOP);
				v5.setColorRGBA_I(c, a);
				v5.addVertex(0, 0, 0);
				v5.addVertex(1, 0, 0);
				v5.addVertex(1, 0, 1);
				v5.addVertex(0, 0, 1);
				v5.draw();

				v5.startDrawing(GL11.GL_LINE_LOOP);
				v5.setColorRGBA_I(c, a);
				v5.addVertex(0, 1, 0);
				v5.addVertex(1, 1, 0);
				v5.addVertex(1, 1, 1);
				v5.addVertex(0, 1, 1);
				v5.draw();

				v5.startDrawing(GL11.GL_LINES);
				v5.setColorRGBA_I(c, a);
				v5.addVertex(0, 0, 0);
				v5.addVertex(0, 1, 0);
				v5.draw();

				v5.startDrawing(GL11.GL_LINES);
				v5.setColorRGBA_I(c, a);
				v5.addVertex(1, 0, 0);
				v5.addVertex(1, 1, 0);
				v5.draw();

				v5.startDrawing(GL11.GL_LINES);
				v5.setColorRGBA_I(c, a);
				v5.addVertex(1, 0, 1);
				v5.addVertex(1, 1, 1);
				v5.draw();

				v5.startDrawing(GL11.GL_LINES);
				v5.setColorRGBA_I(c, a);
				v5.addVertex(0, 0, 1);
				v5.addVertex(0, 1, 1);
				v5.draw();

				ReikaRenderHelper.enableEntityLighting();
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
				GL11.glPopMatrix();
			}
		}
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
			//if (uid != null && uid.modId.equals(ModList.CHROMATICRAFT.modLabel)) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			ChromaResearch r = ChromaResearch.getPageFor(is);
			if (r != null && r.playerCanSee(ep) && r.isCrafting() && r.isCraftable() && !r.isVanillaRecipe()) {
				ep.openGui(ChromatiCraft.instance, r.getCraftingType().ordinal(), null, r.ordinal(), r.getRecipeIndex(is), 1);
				return true;
			}
			//}
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

	@SubscribeEvent(receiveCanceled = true)
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

					ArrayList<Coordinate> li = ItemBuilderWand.getCoordinatesFor(world, x, y, z, dir, Minecraft.getMinecraft().thePlayer);
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

	@SubscribeEvent(receiveCanceled = true)
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
						Coordinate c = blocks.getNthBlock(i);
						float dx = c.xCoord+x-(float)TileEntityRendererDispatcher.staticPlayerX;
						float dy = c.yCoord+y-(float)TileEntityRendererDispatcher.staticPlayerY;
						float dz = c.zCoord+z-(float)TileEntityRendererDispatcher.staticPlayerZ;
						Block bk = blocks.getBlockAt(c.xCoord, c.yCoord, c.zCoord);
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
							int meta = blocks.getMetaAt(c.xCoord, c.yCoord, c.zCoord);
							if (!blocks.hasNonAirBlock(c.xCoord, c.yCoord-1, c.zCoord)) {
								IIcon ico = RenderBlocks.getInstance().getIconSafe(bk.getIcon(ForgeDirection.DOWN.ordinal(), meta));
								float u = ico.getMinU();
								float du = ico.getMaxU();
								float v = ico.getMinV();
								float dv = ico.getMaxV();
								double en = blocks.hasNonAirBlock(c.xCoord, c.yCoord, c.zCoord-1) ? 0 : 0-o;
								double es = blocks.hasNonAirBlock(c.xCoord, c.yCoord, c.zCoord+1) ? 1 : 1+o;
								double ew = blocks.hasNonAirBlock(c.xCoord-1, c.yCoord, c.zCoord) ? 0 : 0-o;
								double ee = blocks.hasNonAirBlock(c.xCoord+1, c.yCoord, c.zCoord) ? 1 : 1+o;
								v5.addVertexWithUV(ew, 0-o, en, u, v);
								v5.addVertexWithUV(ee, 0-o, en, du, v);
								v5.addVertexWithUV(ee, 0-o, es, du, dv);
								v5.addVertexWithUV(ew, 0-o, es, u, dv);
							}

							if (!blocks.hasNonAirBlock(c.xCoord, c.yCoord+1, c.zCoord)) {
								IIcon ico = RenderBlocks.getInstance().getIconSafe(bk.getIcon(ForgeDirection.UP.ordinal(), meta));
								float u = ico.getMinU();
								float du = ico.getMaxU();
								float v = ico.getMinV();
								float dv = ico.getMaxV();
								double en = blocks.hasNonAirBlock(c.xCoord, c.yCoord, c.zCoord-1) ? 0 : 0-o;
								double es = blocks.hasNonAirBlock(c.xCoord, c.yCoord, c.zCoord+1) ? 1 : 1+o;
								double ew = blocks.hasNonAirBlock(c.xCoord-1, c.yCoord, c.zCoord) ? 0 : 0-o;
								double ee = blocks.hasNonAirBlock(c.xCoord+1, c.yCoord, c.zCoord) ? 1 : 1+o;
								v5.addVertexWithUV(ew, 1+o, es, u, dv);
								v5.addVertexWithUV(ee, 1+o, es, du, dv);
								v5.addVertexWithUV(ee, 1+o, en, du, v);
								v5.addVertexWithUV(ew, 1+o, en, u, v);
							}

							if (!blocks.hasNonAirBlock(c.xCoord+1, c.yCoord, c.zCoord)) {
								IIcon ico = RenderBlocks.getInstance().getIconSafe(bk.getIcon(ForgeDirection.EAST.ordinal(), meta));
								float u = ico.getMinU();
								float du = ico.getMaxU();
								float v = ico.getMinV();
								float dv = ico.getMaxV();
								double en = blocks.hasNonAirBlock(c.xCoord, c.yCoord, c.zCoord-1) ? 0 : 0-o;
								double es = blocks.hasNonAirBlock(c.xCoord, c.yCoord, c.zCoord+1) ? 1 : 1+o;
								double ed = blocks.hasNonAirBlock(c.xCoord, c.yCoord-1, c.zCoord) ? 0 : 0-o;
								double eu = blocks.hasNonAirBlock(c.xCoord, c.yCoord+1, c.zCoord) ? 1 : 1+o;
								v5.addVertexWithUV(1+o, ed, en, du, dv);
								v5.addVertexWithUV(1+o, eu, en, du, v);
								v5.addVertexWithUV(1+o, eu, es, u, v);
								v5.addVertexWithUV(1+o, ed, es, u, dv);
							}

							if (!blocks.hasNonAirBlock(c.xCoord-1, c.yCoord, c.zCoord)) {
								IIcon ico = RenderBlocks.getInstance().getIconSafe(bk.getIcon(ForgeDirection.WEST.ordinal(), meta));
								float u = ico.getMinU();
								float du = ico.getMaxU();
								float v = ico.getMinV();
								float dv = ico.getMaxV();
								double en = blocks.hasNonAirBlock(c.xCoord, c.yCoord, c.zCoord-1) ? 0 : 0-o;
								double es = blocks.hasNonAirBlock(c.xCoord, c.yCoord, c.zCoord+1) ? 1 : 1+o;
								double ed = blocks.hasNonAirBlock(c.xCoord, c.yCoord-1, c.zCoord) ? 0 : 0-o;
								double eu = blocks.hasNonAirBlock(c.xCoord, c.yCoord+1, c.zCoord) ? 1 : 1+o;
								v5.addVertexWithUV(0-o, ed, es, u, dv);
								v5.addVertexWithUV(0-o, eu, es, u, v);
								v5.addVertexWithUV(0-o, eu, en, du, v);
								v5.addVertexWithUV(0-o, ed, en, du, dv);
							}

							if (!blocks.hasNonAirBlock(c.xCoord, c.yCoord, c.zCoord-1)) {
								IIcon ico = RenderBlocks.getInstance().getIconSafe(bk.getIcon(ForgeDirection.NORTH.ordinal(), meta));
								float u = ico.getMinU();
								float du = ico.getMaxU();
								float v = ico.getMinV();
								float dv = ico.getMaxV();
								double ew = blocks.hasNonAirBlock(c.xCoord-1, c.yCoord, c.zCoord) ? 0 : 0-o;
								double ee = blocks.hasNonAirBlock(c.xCoord+1, c.yCoord, c.zCoord) ? 1 : 1+o;
								double ed = blocks.hasNonAirBlock(c.xCoord, c.yCoord-1, c.zCoord) ? 0 : 0-o;
								double eu = blocks.hasNonAirBlock(c.xCoord, c.yCoord+1, c.zCoord) ? 1 : 1+o;
								v5.addVertexWithUV(ew, ed, 0-o, u, dv);
								v5.addVertexWithUV(ew, eu, 0-o, u, v);
								v5.addVertexWithUV(ee, eu, 0-o, du, v);
								v5.addVertexWithUV(ee, ed, 0-o, du, dv);
							}

							if (!blocks.hasNonAirBlock(c.xCoord, c.yCoord, c.zCoord+1)) {
								IIcon ico = RenderBlocks.getInstance().getIconSafe(bk.getIcon(ForgeDirection.SOUTH.ordinal(), meta));
								float u = ico.getMinU();
								float du = ico.getMaxU();
								float v = ico.getMinV();
								float dv = ico.getMaxV();
								double ew = blocks.hasNonAirBlock(c.xCoord-1, c.yCoord, c.zCoord) ? 0 : 0-o;
								double ee = blocks.hasNonAirBlock(c.xCoord+1, c.yCoord, c.zCoord) ? 1 : 1+o;
								double ed = blocks.hasNonAirBlock(c.xCoord, c.yCoord-1, c.zCoord) ? 0 : 0-o;
								double eu = blocks.hasNonAirBlock(c.xCoord, c.yCoord+1, c.zCoord) ? 1 : 1+o;
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

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void stopPylonBoxHighlight(DrawBlockHighlightEvent evt) {
		if (evt.target != null && evt.target.typeOfHit == MovingObjectType.BLOCK) {
			World world = Minecraft.getMinecraft().theWorld;
			int x = evt.target.blockX;
			int y = evt.target.blockY;
			int z = evt.target.blockZ;
			if (ChromaTiles.getTile(world, x, y, z) == ChromaTiles.PYLON)
				evt.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void reachBoostHighlight(RenderGameOverlayEvent evt) {
		if (evt.type == ElementType.HELMET) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			if (Chromabilities.REACH.enabledOn(ep)) {
				MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlock(ep, 192, false);
				if (mov != null) {
					int x = mov.blockX;
					int y = mov.blockY;
					int z = mov.blockZ;
					double dd = ReikaMathLibrary.py3d(x+0.5-ep.posX, y+0.5-ep.posY, z+0.5-ep.posZ);
					GL11.glPushMatrix();
					double s = 1.5;
					GL11.glScaled(s, s, s);
					String sg = String.format("%.3fm", dd);
					FontRenderer f = ChromaFontRenderer.FontType.HUD.renderer;
					f.drawString(sg, evt.resolution.getScaledWidth()/3+4, evt.resolution.getScaledHeight()/3-9, 0xffffff);
					GL11.glPopMatrix();
					Block b = Minecraft.getMinecraft().theWorld.getBlock(x, y, z);
					if (b != null && b != Blocks.air) {
						IIcon ico = b.getIcon(1, Minecraft.getMinecraft().theWorld.getBlockMetadata(x, y, z));
						if (ico != null) {
							float u = ico.getMinU();
							float v = ico.getMinV();
							float du = ico.getMaxU();
							float dv = ico.getMaxV();
							Tessellator v5 = Tessellator.instance;
							int sz = 16;
							int dx = evt.resolution.getScaledWidth()/2-sz*5/4;
							int dy = evt.resolution.getScaledHeight()/2-sz*5/4;
							ReikaTextureHelper.bindTerrainTexture();
							v5.startDrawingQuads();
							v5.addVertexWithUV(dx, dy+sz, 0, u, dv);
							v5.addVertexWithUV(dx+sz, dy+sz, 0, du, dv);
							v5.addVertexWithUV(dx+sz, dy, 0, du, v);
							v5.addVertexWithUV(dx, dy, 0, u, v);
							v5.draw();
						}
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void reachBoostHighlight(DrawBlockHighlightEvent evt) {
		if (evt.target != null && evt.target.typeOfHit == MovingObjectType.BLOCK) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			if (Chromabilities.REACH.enabledOn(ep)) {
				GL11.glPushMatrix();
				World world = Minecraft.getMinecraft().theWorld;
				int x = evt.target.blockX;
				int y = evt.target.blockY;
				int z = evt.target.blockZ;
				double p2 = x-TileEntityRendererDispatcher.staticPlayerX;
				double p4 = y-TileEntityRendererDispatcher.staticPlayerY;
				double p6 = z-TileEntityRendererDispatcher.staticPlayerZ;
				GL11.glTranslated(p2, p4, p6);
				//GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
				GL11.glLineWidth(3.0F);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDepthMask(false);
				float f1 = 0.002F;
				double d = 0.005;
				AxisAlignedBB box = world.getBlock(x, y, z).getSelectedBoundingBoxFromPool(world, x, y, z);
				box = box.offset(-x, -y, -z).expand(d, d, d);
				float r = (float)(0.5+Math.sin(System.currentTimeMillis()/500D));
				r = Math.max(0, Math.min(1, r));
				int c = ReikaColorAPI.mixColors(CrystalElement.LIME.getColor(), CrystalElement.PURPLE.getColor(), r);
				RenderGlobal.drawOutlinedBoundingBox(box, c);


				GL11.glEnable(GL11.GL_TEXTURE_2D);

				GL11.glPopMatrix();

				GL11.glDepthMask(true);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glLineWidth(2.0F);

				evt.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(receiveCanceled = true)
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
					blocks.maxDepth = ItemExcavationWand.getDepth(Minecraft.getMinecraft().thePlayer)-1;
					Set<BlockKey> set = new HashSet();
					set.add(new BlockKey(id, meta));
					if (id == Blocks.lit_redstone_ore)
						set.add(new BlockKey(Blocks.redstone_ore));
					else if (id == Blocks.redstone_ore)
						set.add(new BlockKey(Blocks.lit_redstone_ore));
					blocks.recursiveAddMultipleWithBounds(world, x, y, z, set, x-32, y-32, z-32, x+32, y+32, z+32);
					ReikaRenderHelper.prepareGeoDraw(true);
					BlendMode.DEFAULT.apply();
					Tessellator v5 = Tessellator.instance;
					double o = 0.0125;
					int r = 255;
					int g = 255;
					int b = 255;
					for (int i = 0; i < blocks.getSize(); i++) {
						Coordinate c = blocks.getNthBlock(i);
						int dx = c.xCoord-x;
						int dy = c.yCoord-y;
						int dz = c.zCoord-z;
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
		if (GuiScreen.isCtrlKeyDown()) {
			if (evt.hasItem() && evt.isHovered()) {
				if (ProgressStage.ALLCOLORS.isPlayerAtStage(Minecraft.getMinecraft().thePlayer)) {
					ItemStack is = evt.getItem();
					ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(is);
					if (tag != null && !tag.isEmpty()) {
						Tessellator v5 = Tessellator.instance;
						int i = tag.tagCount();
						int n = 8;
						int iw = i >= n ? n : i;
						int ih = Math.round(0.49F+(float)i/n);
						GL11.glDisable(GL11.GL_CULL_FACE);
						GL11.glDisable(GL11.GL_DEPTH_TEST);
						GL11.glDisable(GL11.GL_LIGHTING);
						GL11.glEnable(GL11.GL_BLEND);
						BlendMode.DEFAULT.apply();
						GL11.glColor4f(1, 1, 1, 1);
						double z = 0;
						int s = 8;
						int w = s*iw;
						int h = s*ih;
						int mx = 0;//evt.getRelativeMouseX();
						int my = 0;//evt.getRelativeMouseY();
						int x = evt.slotX-w+mx;
						int y = evt.slotY-h+my;
						//if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
						//	w = 16;
						//	x2 -= 8;
						//}
						int r = 1;
						GL11.glDisable(GL11.GL_TEXTURE_2D);
						v5.startDrawingQuads();
						v5.setColorRGBA(127, 0, 255, 255);
						v5.addVertex(x-r, y-r, z);
						v5.addVertex(x+w+r, y-r, z);
						v5.addVertex(x+w+r, y+h+r, z);
						v5.addVertex(x-r, y+h+r, z);
						v5.draw();
						v5.startDrawingQuads();
						v5.setColorRGBA(0, 0, 0, 255);
						v5.addVertex(x, y, z);
						v5.addVertex(x+w, y, z);
						v5.addVertex(x+w, y+h, z);
						v5.addVertex(x, y+h, z);
						v5.draw();
						GL11.glEnable(GL11.GL_TEXTURE_2D);
						int in = 0;
						for (CrystalElement e : tag.elementSet()) {
							IIcon ico = e.getFaceRune();
							float u = ico.getMinU();
							float v = ico.getMinV();
							float du = ico.getMaxU();
							float dv = ico.getMaxV();
							int ex = x+(in%n)*s;
							int ey = y+(in/n)*s;
							in++;/*
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
							v5.addVertexWithUV(ex, ey, z, u, v);
							v5.addVertexWithUV(ex+s, ey, z, du, v);
							v5.addVertexWithUV(ex+s, ey+s, z, du, dv);
							v5.addVertexWithUV(ex, ey+s, z, u, dv);
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
}

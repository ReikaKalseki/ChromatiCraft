/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHotkeys;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityXRays;
import Reika.ChromatiCraft.Auxiliary.Potions.PotionVoidGaze.VoidGazeLevels;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaOverlays;
import Reika.ChromatiCraft.Auxiliary.Tab.FragmentTab;
import Reika.ChromatiCraft.Auxiliary.Tab.TabChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaBookGui;
import Reika.ChromatiCraft.Block.BlockDummyAux.TileEntityDummyAux;
import Reika.ChromatiCraft.Block.BlockDummyAux.TileEntityDummyAux.Flags;
import Reika.ChromatiCraft.Block.BlockFakeSky;
import Reika.ChromatiCraft.Block.BlockPylonStructure;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Entity.EntityGlowCloud;
import Reika.ChromatiCraft.GUI.GuiAuraPouch;
import Reika.ChromatiCraft.GUI.GuiInventoryLinker;
import Reika.ChromatiCraft.GUI.GuiItemBurner.ButtonItemBurner;
import Reika.ChromatiCraft.Items.Tools.ItemFloatstoneBoots;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemBuilderWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemCaptureWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemDuplicationWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemExcavationWand;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Models.ColorizableSlimeModel;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.Render.BiomeFXRenderer;
import Reika.ChromatiCraft.Render.CliffFogRenderer;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.TESR.RenderAlveary;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager;
import Reika.ChromatiCraft.World.Dimension.DimensionTuningManager;
import Reika.ChromatiCraft.World.Dimension.SkyRiverManagerClient;
import Reika.ChromatiCraft.World.Dimension.Rendering.ChromaCloudRenderer;
import Reika.ChromatiCraft.World.Dimension.Rendering.SkyRiverRenderer;
import Reika.ChromatiCraft.World.Dimension.Structure.AntFarmGenerator;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.KeybindHandler.KeyPressEvent;
import Reika.DragonAPI.Auxiliary.Trackers.ModLockController.ModReVerifyEvent;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructuredBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Event.NEIRecipeCheckEvent;
import Reika.DragonAPI.Instantiable.Event.ProfileEvent;
import Reika.DragonAPI.Instantiable.Event.ProfileEvent.ProfileEventWatcher;
import Reika.DragonAPI.Instantiable.Event.Client.ClientLoginEvent;
import Reika.DragonAPI.Instantiable.Event.Client.ClientLogoutEvent;
import Reika.DragonAPI.Instantiable.Event.Client.CloudRenderEvent;
import Reika.DragonAPI.Instantiable.Event.Client.CreativeTabGuiRenderEvent;
import Reika.DragonAPI.Instantiable.Event.Client.EntityRenderEvent;
import Reika.DragonAPI.Instantiable.Event.Client.EntityRenderingLoopEvent;
import Reika.DragonAPI.Instantiable.Event.Client.FarClippingPlaneEvent;
import Reika.DragonAPI.Instantiable.Event.Client.GetMouseoverEvent;
import Reika.DragonAPI.Instantiable.Event.Client.HotbarKeyEvent;
import Reika.DragonAPI.Instantiable.Event.Client.ItemEffectRenderEvent;
import Reika.DragonAPI.Instantiable.Event.Client.LightmapEvent;
import Reika.DragonAPI.Instantiable.Event.Client.NightVisionBrightnessEvent;
import Reika.DragonAPI.Instantiable.Event.Client.PlayMusicEvent;
import Reika.DragonAPI.Instantiable.Event.Client.RenderBlockAtPosEvent;
import Reika.DragonAPI.Instantiable.Event.Client.RenderFirstPersonItemEvent;
import Reika.DragonAPI.Instantiable.Event.Client.RenderItemInSlotEvent;
import Reika.DragonAPI.Instantiable.Event.Client.SinglePlayerLogoutEvent;
import Reika.DragonAPI.Instantiable.Event.Client.TileEntityRenderEvent;
import Reika.DragonAPI.Instantiable.Event.Client.WaterColorEvent;
import Reika.DragonAPI.Instantiable.Event.Client.WeatherSkyStrengthEvent;
import Reika.DragonAPI.Instantiable.IO.EnumSound;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Interfaces.Block.MachineRegistryBlock;
import Reika.DragonAPI.Interfaces.Registry.TileEnum;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pneumaticCraft.api.client.pneumaticHelmet.BlockTrackEvent;
import pneumaticCraft.api.client.pneumaticHelmet.InventoryTrackEvent;
import thaumcraft.api.research.ResearchItem;

@SideOnly(Side.CLIENT)
public class ChromaClientEventController implements ProfileEventWatcher {

	public static final ChromaClientEventController instance = new ChromaClientEventController();

	protected static final Random rand = new Random();
	protected static final RenderItem itemRender = new RenderItem();

	private static RayTracer visualLOS;

	private boolean editedSlimeModel = false;

	//public boolean textureLoadingComplete = false;

	private Coordinate excavatorOverlayOrigin;
	private BlockKey excavatorOverlayBlock;
	private boolean excavatorOverlaySpread;
	private BlockArray cachedExcavatorOverlay;

	private ChromaClientEventController() {
		/*
		if (ChromaOptions.BIOMEFX.getState()) {
			BiomeFXRenderer.instance.initialize();
		}
		else {
			textureLoadingComplete = true;
		}
		 */
		ProfileEvent.registerHandler("gui", this);
	}

	public void onCall(String tag) {
		switch(tag) {
			case "gui": {
				if (ChromaOverlays.instance.isWashoutActive()) {
					Minecraft.getMinecraft().gameSettings.hideGUI = false;
				}
				break;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void adjustLightMap(LightmapEvent evt) {
		if (Minecraft.getMinecraft().theWorld.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			int[] colors = Minecraft.getMinecraft().entityRenderer.lightmapColors;
			for (int i = 0; i < colors.length; i++) {
				int color = colors[i];
				int[] c = ReikaColorAPI.HexToRGB(color);
				int avg = (c[0]+c[1]+c[2])/3;
				color = 0xff000000 | ReikaColorAPI.GStoHex(avg);
				colors[i] = color;
			}
		}
	}

	@SubscribeEvent
	public void updateRedstoneTorchIcons(RenderBlockAtPosEvent evt) {
		if (evt.block == Blocks.redstone_torch || evt.block == Blocks.unlit_redstone_torch) {
			if (evt.world.getBlockMetadata(evt.xCoord, evt.yCoord, evt.zCoord) == 5) {
				Block b2 = evt.world.getBlock(evt.xCoord, evt.yCoord-1, evt.zCoord);
				if (b2 == ChromaBlocks.PYLONSTRUCT.getBlockInstance() || b2 instanceof BlockStructureShield) {
					Tessellator.instance.setBrightness(evt.block.getMixedBrightnessForBlock(evt.world, evt.xCoord, evt.yCoord, evt.zCoord));
					Tessellator.instance.setColorOpaque_I(0xffffff);
					evt.continueRendering = true;
					evt.setCanceled(true);
					ReikaRenderHelper.renderTorch(evt.world, evt.xCoord, evt.yCoord, evt.zCoord, BlockPylonStructure.getIconOverride(evt.block), Tessellator.instance, evt.render, 0.625, 0.0625);
				}
			}
		}
	}

	@SubscribeEvent
	public void handleUntunedDimension(RenderBlockAtPosEvent evt) {
		if (true)
			return;
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		float f = 1;
		/*
		int tuning = DimensionTuningManager.instance.getPlayerTuning(ep);
		if (tuning == 0) {
			f = 0.7F;
			if (evt.block instanceof BlockDimensionDeco) {
				f = 0.5F;
			}
		}
		 */
		if (ChromaDimensionManager.isStructureBiome(evt.world.getBiomeGenForCoords(evt.xCoord, evt.zCoord))) {
			f = Math.max(0.005F, DimensionTuningManager.TuningThresholds.STRUCTUREBIOMES.getTuningFraction(ep));
		}
		Random r = new Random(new Coordinate(evt.xCoord, evt.yCoord, evt.zCoord).hashCode());
		r.nextBoolean();
		if (r.nextFloat() > f) {
			evt.setCanceled(true);
			Block b = evt.block;
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				IIcon ico = evt.render.getIconSafe(ChromaIcons.BLANK.getIcon());
				Tessellator.instance.setColorOpaque_I(0xffffff);
				Tessellator.instance.setBrightness(evt.block.getMixedBrightnessForBlock(evt.world, evt.xCoord, evt.yCoord, evt.zCoord));
				boolean side = b.shouldSideBeRendered(evt.world, evt.xCoord+dir.offsetX, evt.yCoord+dir.offsetY, evt.zCoord+dir.offsetZ, i);
				if (side) {
					evt.continueRendering = true;
					//ChromatiCraft.logger.debug("Rendering side "+dir);
					switch(dir) {
						case DOWN:
							Tessellator.instance.setColorOpaque_F(0.5F, 0.5F, 0.5F);
							evt.render.renderFaceYNeg(b, evt.xCoord, evt.yCoord, evt.zCoord, ico);
							break;
						case UP:
							Tessellator.instance.setColorOpaque_F(1, 1, 1);
							evt.render.renderFaceYPos(b, evt.xCoord, evt.yCoord, evt.zCoord, ico);
							break;
						case WEST:
							Tessellator.instance.setColorOpaque_F(0.65F, 0.65F, 0.65F);
							evt.render.renderFaceXNeg(b, evt.xCoord, evt.yCoord, evt.zCoord, ico);
							break;
						case EAST:
							Tessellator.instance.setColorOpaque_F(0.65F, 0.65F, 0.65F);
							evt.render.renderFaceXPos(b, evt.xCoord, evt.yCoord, evt.zCoord, ico);
							break;
						case NORTH:
							Tessellator.instance.setColorOpaque_F(0.8F, 0.8F, 0.8F);
							evt.render.renderFaceZNeg(b, evt.xCoord, evt.yCoord, evt.zCoord, ico);
							break;
						case SOUTH:
							Tessellator.instance.setColorOpaque_F(0.8F, 0.8F, 0.8F);
							evt.render.renderFaceZPos(b, evt.xCoord, evt.yCoord, evt.zCoord, ico);
							break;
						default:
							break;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void reverify(ModReVerifyEvent evt) {
		ChromatiCraft.proxy.registerRenderers();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void tickAbilityHotkeys(ClientTickEvent evt) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		GuiScreen scr = Minecraft.getMinecraft().currentScreen;
		/*
		if (scr instanceof GuiAbilitySelect) {
			GuiAbilitySelect gui = (GuiAbilitySelect)scr;
			AbilityHotkeys.tick(ep, gui.getSelectedAbility(), gui.getSelectedData());
		}
		else {*/
		AbilityHotkeys.tick(ep, null, 0);
		//}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void addLumenBurnButton(GuiScreenEvent.InitGuiEvent.Post evt) {
		if (evt.gui instanceof GuiInventory && ProgressStage.ALLCOLORS.isPlayerAtStage(Minecraft.getMinecraft().thePlayer) && ProgressStage.CHARGE.isPlayerAtStage(Minecraft.getMinecraft().thePlayer)) {
			GuiContainer gui = (GuiContainer)evt.gui;
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			evt.buttonList.add(new ButtonItemBurner(gui, gui.guiLeft+(ep.getActivePotionEffects().isEmpty() ? 142 : 82), gui.guiTop+56));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void handleLumenBurnButton(GuiScreenEvent.ActionPerformedEvent.Post evt) {
		if (evt.button.id == ButtonItemBurner.BUTTON_ID && evt.gui instanceof GuiInventory) {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.BURNERINV.ordinal(), PacketTarget.server, 1);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void floatBootParticlesForOthers(PlayerTickEvent evt) {
		if (evt.side == Side.CLIENT && evt.phase == Phase.START && evt.player instanceof EntityOtherPlayerMP) { //other players only
			if (ChromaItems.FLOATBOOTS.matchWith(evt.player.getCurrentArmor(0))) {
				ItemFloatstoneBoots.addParticles(evt.player.worldObj, evt.player);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void updateGlowCliffRendering(ClientTickEvent evt) {
		BiomeGlowingCliffs.updateRenderFactor(Minecraft.getMinecraft().thePlayer);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void preventCliffWeatherSky(WeatherSkyStrengthEvent evt) {
		/*
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		if (ep != null) {
			//ReikaJavaLibrary.pConsole(ep.worldObj.getWorldInfo().isRaining());
			if (BiomeGlowingCliffs.isGlowingCliffs(ep.worldObj.getBiomeGenForCoords(MathHelper.floor_double(ep.posX), MathHelper.floor_double(ep.posZ)))) {
		 */evt.returnValue *= 1-BiomeGlowingCliffs.renderFactor;/*
			}
		}
		  */
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void preventCliffWeatherSky(FogColors evt) {
		if (Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null)
			return;
		if (Minecraft.getMinecraft().thePlayer.isInsideOfMaterial(Material.lava) || Minecraft.getMinecraft().thePlayer.isInsideOfMaterial(Material.water))
			return;
		float f = BiomeGlowingCliffs.renderFactor*(float)ReikaMathLibrary.normalizeToBounds(ReikaWorldHelper.getSunIntensity(Minecraft.getMinecraft().theWorld, false, (float)evt.renderPartialTicks), 0, 1, 0.2, 0.8);
		if (f > 0) {
			evt.red = evt.red*(1-f)+f*0.9F;
			evt.green = evt.green*(1-f)+f*0.7F;
			evt.blue = evt.blue*(1-f)+f*1F;
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void preventCliffThunder(PlaySoundEvent17 evt) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		if (ep != null && BiomeGlowingCliffs.isGlowingCliffs(ep.worldObj.getBiomeGenForCoords(MathHelper.floor_double(ep.posX), MathHelper.floor_double(ep.posZ)))) {
			if (evt.name.contains("ambient.weather.thunder")) {
				evt.result = null;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void preventCliffAmbience(PlaySoundEvent17 evt) {
		//if (evt.sound.getYPosF() >= GlowingCliffsColumnShaper.SEA_LEVEL) {
		if (Minecraft.getMinecraft().theWorld != null && BiomeGlowingCliffs.isGlowingCliffs(Minecraft.getMinecraft().theWorld.getBiomeGenForCoords(MathHelper.floor_float(evt.sound.getXPosF()), MathHelper.floor_float(evt.sound.getZPosF())))) {
			if (evt.name.contains("ambient.cave.cave")) {
				int n = rand.nextInt(4);
				ChromaSounds s = null;
				switch(n) {
					case 0:
						s = ChromaSounds.CLIFFSOUND;
						break;
					case 1:
						s = ChromaSounds.CLIFFSOUND2;
						break;
					case 2:
						s = ChromaSounds.CLIFFSOUND3;
						break;
					case 3:
						s = ChromaSounds.CLIFFSOUND4;
						break;
				}
				evt.result = new EnumSound(s, evt.sound, false);
			}
		}
		//}
	}

	@SubscribeEvent
	public void noLumaWaterFog(RenderBlockOverlayEvent evt) {
		//ReikaJavaLibrary.pConsole(evt.density);
		if (evt.overlayType == OverlayType.WATER && (evt.player.worldObj.getBlock(evt.blockX, evt.blockY, evt.blockZ) == ChromaBlocks.LUMA.getBlockInstance() || evt.player.worldObj.getBlock(evt.blockX, evt.blockY+1, evt.blockZ) == ChromaBlocks.LUMA.getBlockInstance())) {
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void noLumaWaterFog(EntityViewRenderEvent.FogDensity evt) {
		//ReikaJavaLibrary.pConsole(evt.density);
		if (evt.block == ChromaBlocks.LUMA.getBlockInstance()) {
			evt.density = 0;
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void noLumaWaterFog(EntityViewRenderEvent.FogColors evt) {
		//ReikaJavaLibrary.pConsole(evt.density);
		if (evt.block == ChromaBlocks.LUMA.getBlockInstance()) {
			evt.red = evt.green = evt.blue = 1;
		}
	}

	/*
	@SubscribeEvent
	public void glowingCliffsFog(EntityViewRenderEvent.FogColors evt) {
		//ReikaJavaLibrary.pConsole(evt.density);
		Entity e = evt.entity;
		BiomeGenBase b = e.worldObj.getBiomeGenForCoords(MathHelper.floor_double(e.posX), MathHelper.floor_double(e.posZ));
		if (BiomeGlowingCliffs.isGlowingCliffs(b))
			evt.red = evt.green = evt.blue = 1;
	}

	@SubscribeEvent
	public void glowingCliffsFog(FogDistanceEvent evt) {
		//ReikaJavaLibrary.pConsole(evt.density);
		Entity e = Minecraft.getMinecraft().thePlayer;
		BiomeGenBase b = e.worldObj.getBiomeGenForCoords(MathHelper.floor_double(e.posX), MathHelper.floor_double(e.posZ));
		float mind = 10;
		if (BiomeGlowingCliffs.isGlowingCliffs(b)) {
			float f = ChromatiCraft.glowingcliffs.getFogDensity(e.posX, e.posY, e.posZ);
			if (f > 0)
				evt.fogDistance = mind+(evt.originalDistance-mind)*(1-f);
		}
	}
	 */
	@SubscribeEvent
	public void cliffWaterColor(WaterColorEvent evt) {
		if (BiomeGlowingCliffs.isGlowingCliffs(evt.getBiome())) {
			evt.color = ChromatiCraft.glowingcliffs.getWaterColor(evt.world, evt.x, evt.y, evt.z, evt.getLightLevel());
		}
	}

	@SubscribeEvent
	public void cancelBeeGlintForTESR(ItemEffectRenderEvent evt) {
		if (!RenderAlveary.renderBeeGlint)
			evt.setResult(Result.DENY);
	}

	@SubscribeEvent
	public void flipFaces(RenderWorldEvent.Pre evt) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		if (VoidGazeLevels.FACEFLIP.isActiveOnPlayer(Minecraft.getMinecraft().thePlayer))
			GL11.glFrontFace(GL11.GL_CW);
	}

	@SubscribeEvent
	public void flipFaces(RenderWorldEvent.Post evt) {
		GL11.glPopAttrib();
	}

	@SubscribeEvent
	public void createCaveParticles(ClientTickEvent evt) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		if (!Minecraft.getMinecraft().isGamePaused() && ep != null && VoidGazeLevels.CAVEPARTICLES.isActiveOnPlayer(ep)) {
			int n = 9-Minecraft.getMinecraft().gameSettings.particleSetting*2;
			for (int i = 0; i < n; i++) {
				double dx = ReikaRandomHelper.getRandomPlusMinus(ep.posX, 12);
				double dz = ReikaRandomHelper.getRandomPlusMinus(ep.posZ, 12);
				double dy = ReikaRandomHelper.getRandomPlusMinus(ep.posY, 8);
				int mdx = MathHelper.floor_double(dx);
				int mdy = MathHelper.floor_double(dy);
				int mdz = MathHelper.floor_double(dz);
				if (ep.worldObj.getBlock(mdx, mdy, mdz).isAir(ep.worldObj, mdx, mdy, mdz)) {
					EntityFX fx = new EntityBlurFX(ep.worldObj, dx, dy, dz).setLife(8).setAlphaFading().setIcon(ChromaIcons.FLARE).setScale(1.5F);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
			}
		}
	}

	/*
	@SubscribeEvent
	public void makeSomeBlocksOpaque(ClientLoginEvent evt) {
		if (Chromabilities.ORECLIP.enabledOn(Minecraft.getMinecraft().thePlayer) && evt.newLogin) {
			AbilityHelper.instance.onNoClipEnable();
		}
	}

	@SubscribeEvent
	public void makeSomeBlocksOpaque(ClientDisconnectionFromServerEvent evt) {
		if (Chromabilities.ORECLIP.enabledOn(Minecraft.getMinecraft().thePlayer)) {
			AbilityHelper.instance.onNoClipDisable();
		}
	}

	@SubscribeEvent
	public void makeSomeBlocksOpaque(SinglePlayerLogoutEvent evt) {
		if (Chromabilities.ORECLIP.enabledOn(Minecraft.getMinecraft().thePlayer)) {
			AbilityHelper.instance.onNoClipDisable();
		}
	}
	 */

	@SubscribeEvent
	public void clearOnLogout(ClientDisconnectionFromServerEvent evt) {
		SkyRiverManagerClient.handleRayClearPacket();
	}

	@SubscribeEvent
	public void clearOnLogout(ClientLogoutEvent evt) {
		SkyRiverManagerClient.handleRayClearPacket();
	}

	@SubscribeEvent
	public void clearOnLogout(SinglePlayerLogoutEvent evt) {
		SkyRiverManagerClient.handleRayClearPacket();
	}

	@SubscribeEvent
	public void renderDimensionSkyriver(EntityRenderingLoopEvent evt) {
		if (Minecraft.getMinecraft().theWorld.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			if (MinecraftForgeClient.getRenderPass() == 1) {
				SkyRiverRenderer.instance.render();
				ChromaDimensionManager.renderAurorae();
			}
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			ChromaCloudRenderer.instance.drawVoidFog(ep);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void applyAntFarmLighting(NightVisionBrightnessEvent evt) {
		PotionEffect p = evt.player.getActivePotionEffect(Potion.nightVision);
		if (p.getAmplifier() == 3) {
			float minValue = -5F;
			float maxValue = 2;//.5F;
			float fade = AntFarmGenerator.LIGHT_DURATION;
			float t = p.getDuration()-evt.partialTickTime;//fade-evt.player.ticksExisted+evt.partialTickTime;
			evt.brightness = minValue+(maxValue-minValue)*(t%fade)/fade;
		}
	}

	@SubscribeEvent
	public void applyOreXRay(GetMouseoverEvent evt) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		if (AbilityHelper.instance.isNoClipEnabled) {
			Vec3 vec = Vec3.createVectorHelper(ep.posX, (ep.posY + 1.62) - ep.yOffset, ep.posZ);
			Vec3 vec2 = ep.getLook(1.0F);
			double reach = Minecraft.getMinecraft().playerController.getBlockReachDistance();
			Vec3 vec3 = vec.addVector(vec2.xCoord*reach, vec2.yCoord*reach, vec2.zCoord*reach);
			MovingObjectPosition hit = AbilityHelper.instance.doOreClipRayTrace(Minecraft.getMinecraft().theWorld, vec, vec3, false);
			evt.newLook = hit != null && hit.typeOfHit == MovingObjectType.BLOCK ? hit : new MovingObjectPosition(0, 0, 0, 0, Vec3.createVectorHelper(0, 0, 0), false);//new MovingObjectPosition(ep);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void applyOreXRay(RenderBlockOverlayEvent evt) {
		if (AbilityHelper.instance.isNoClipEnabled) {
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void applyOreXRay(RenderBlockAtPosEvent evt) {
		//ReikaJavaLibrary.pConsole("CLIENT RENDER NOCLIP "+AbilityHelper.instance.isNoClipEnabled);
		if (AbilityHelper.instance.isNoClipEnabled) {
			Block b = evt.block;
			int meta = evt.world.getBlockMetadata(evt.xCoord, evt.yCoord, evt.zCoord);
			//ChromatiCraft.logger.debug("Checking render of "+b+":"+meta+" @ "+evt.xCoord+"."+evt.yCoord+","+evt.zCoord);
			if (!AbilityHelper.instance.isBlockOreclippable(Minecraft.getMinecraft().theWorld, evt.xCoord, evt.yCoord, evt.zCoord, b, meta)) {
				//Tessellator.instance.setColorRGBA_I(b.colorMultiplier(evt.world, evt.xCoord, evt.yCoord, evt.zCoord), 96);
				/*
					for (int i = 0; i < 6; i++) {
						ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
						Tessellator.instance.setBrightness(240);
						IIcon ico = b.getIcon(i, meta);
						if (b.shouldSideBeRendered(evt.world, evt.xCoord+dir.offsetX, evt.yCoord+dir.offsetY, evt.zCoord+dir.offsetZ, i)) {
							switch(dir) {
								case DOWN:
									evt.render.renderFaceYNeg(evt.block, evt.xCoord, evt.yCoord, evt.zCoord, ico);
									break;
								case UP:
									evt.render.renderFaceYPos(evt.block, evt.xCoord, evt.yCoord, evt.zCoord, ico);
									break;
								case WEST:
									evt.render.renderFaceXNeg(evt.block, evt.xCoord, evt.yCoord, evt.zCoord, ico);
									break;
								case EAST:
									evt.render.renderFaceXPos(evt.block, evt.xCoord, evt.yCoord, evt.zCoord, ico);
									break;
								case NORTH:
									evt.render.renderFaceZNeg(evt.block, evt.xCoord, evt.yCoord, evt.zCoord, ico);
									break;
								case SOUTH:
									evt.render.renderFaceZPos(evt.block, evt.xCoord, evt.yCoord, evt.zCoord, ico);
									break;
								default:
									break;
							}
						}
					}
				 */
				evt.setCanceled(true);
			}
			else {
				//evt.render.setRenderAllFaces(true);
				//ChromatiCraft.logger.debug("Checking render pass of "+b+":"+meta+" @ "+evt.xCoord+"."+evt.yCoord+","+evt.zCoord+" for pass "+evt.renderPass+": "+b.canRenderInPass(evt.renderPass));
				if (b.canRenderInPass(evt.renderPass)) {
					int type = b.getRenderType();
					if (type == 0 || type == ChromatiCraft.proxy.oreRender || ReikaBlockHelper.isOre(b, meta) || b.renderAsNormalBlock() || b.isOpaqueCube()) {
						//ChromatiCraft.logger.debug("Rendering "+b+":"+meta+" @ "+evt.xCoord+"."+evt.yCoord+","+evt.zCoord);
						evt.render.enableAO = false;
						Tessellator.instance.setBrightness(240);
						Tessellator.instance.setColorRGBA_I(0xffffff, 255);
						for (int i = 0; i < 6; i++) {
							ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
							Tessellator.instance.setBrightness(240);
							IIcon ico = evt.render.getIconSafe(b.getIcon(evt.world, evt.xCoord, evt.yCoord, evt.zCoord, i));
							boolean side = b.shouldSideBeRendered(evt.world, evt.xCoord+dir.offsetX, evt.yCoord+dir.offsetY, evt.zCoord+dir.offsetZ, i);
							double o = side ? 0.001 : 0;
							if ((side || (dir == ForgeDirection.UP && evt.yCoord == 0)) || b != Blocks.bedrock) {
								//ChromatiCraft.logger.debug("Rendering side "+dir);
								switch(dir) {
									case DOWN:
										Tessellator.instance.setColorOpaque_F(0.5F, 0.5F, 0.5F);
										evt.render.renderFaceYNeg(b, evt.xCoord, evt.yCoord-o, evt.zCoord, ico);
										break;
									case UP:
										Tessellator.instance.setColorOpaque_F(1, 1, 1);
										evt.render.renderFaceYPos(b, evt.xCoord, evt.yCoord+o, evt.zCoord, ico);
										break;
									case WEST:
										Tessellator.instance.setColorOpaque_F(0.65F, 0.65F, 0.65F);
										evt.render.renderFaceXNeg(b, evt.xCoord-o, evt.yCoord, evt.zCoord, ico);
										break;
									case EAST:
										Tessellator.instance.setColorOpaque_F(0.65F, 0.65F, 0.65F);
										evt.render.renderFaceXPos(b, evt.xCoord+o, evt.yCoord, evt.zCoord, ico);
										break;
									case NORTH:
										Tessellator.instance.setColorOpaque_F(0.8F, 0.8F, 0.8F);
										evt.render.renderFaceZNeg(b, evt.xCoord, evt.yCoord, evt.zCoord-o, ico);
										break;
									case SOUTH:
										Tessellator.instance.setColorOpaque_F(0.8F, 0.8F, 0.8F);
										evt.render.renderFaceZPos(b, evt.xCoord, evt.yCoord, evt.zCoord+o, ico);
										break;
									default:
										break;
								}
							}
						}
					}
				}
				//evt.setCanceled(true);
				//evt.setResult(Result.ALLOW);
			}
		}
	}
	/*
	@SubscribeEvent
	public void makeWorldTranslucentA(RenderWorldEvent.Pre evt) {
		if (Chromabilities.ORECLIP.enabledOn(Minecraft.getMinecraft().thePlayer)) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();
			GL11.glDisable(GL11.GL_ALPHA_TEST);
		}
	}

	@SubscribeEvent
	public void makeWorldTranslucentB(RenderWorldEvent.Post evt) {
		if (Chromabilities.ORECLIP.enabledOn(Minecraft.getMinecraft().thePlayer)) {
			GL11.glPopAttrib();
		}
	}
	 */
	@SubscribeEvent
	public void clearSavedGui(ClientLoginEvent evt) {
		ChromaBookGui.lastGui = null;
	}

	@SubscribeEvent
	public void clearSavedGui(LivingDeathEvent evt) {
		if (evt.entityLiving == Minecraft.getMinecraft().thePlayer)
			ChromaBookGui.lastGui = null;
	}

	@SubscribeEvent
	public void stopSwapOutofGUIItem(HotbarKeyEvent evt) {
		ItemStack is = evt.getItem();
		if (ChromaItems.AURAPOUCH.matchWith(is) && evt.gui instanceof GuiAuraPouch) {
			evt.setCanceled(true);
		}
		if (ChromaItems.LINK.matchWith(is) && evt.gui instanceof GuiInventoryLinker) {
			evt.setCanceled(true);
		}
		/*
		if (ChromaItems.HELP.matchWith(is) && evt.gui instanceof ChromaBookGui) {
			evt.setCanceled(true);
		}
		 */
	}

	/*
	@SubscribeEvent
	public void waitForTextures(GameFinishedLoadingEvent evt) throws InterruptedException {
		long dur = 0;
		while (!textureLoadingComplete) {
			Thread.sleep(100);
			dur += 100;
			if (dur%5000 == 0)
				ChromatiCraft.logger.log("Waiting for texture loading to complete...");
		}
	}
	 */
	/*
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void controlBrightness(LightmapEvent evt) {

	}
	 */
	@SubscribeEvent
	public void biomeFX(EntityRenderingLoopEvent evt) {
		//if (ChromaOptions.BIOMEFX.getState() && Minecraft.getMinecraft().theWorld.provider.dimensionId != ExtraChromaIDs.DIMID.getValue())
		BiomeFXRenderer.instance.render();
		CliffFogRenderer.instance.render();
	}

	@SubscribeEvent
	public void renderFakeSky(EntityRenderingLoopEvent evt) {
		if (MinecraftForgeClient.getRenderPass() == 0) {
			BlockFakeSky.doSkyRendering();
		}
	}

	@SubscribeEvent
	public void clearLexiconCache(SinglePlayerLogoutEvent evt) {
		ChromaBookGui.lastGui = null;
	}

	@SubscribeEvent
	public void clearLexiconCache(ClientLogoutEvent evt) {
		ChromaBookGui.lastGui = null;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void ensureMusic(CloudRenderEvent evt) {
		if (Minecraft.getMinecraft().theWorld.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			evt.setResult(Result.ALLOW);
		}
	}

	/*
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void ensureMusic(SoundVolumeEvent evt) {
		if (evt.sound instanceof CustomMusic) {
			CustomMusic cm = (CustomMusic)evt.sound;
			if (cm.path.toLowerCase(Locale.ENGLISH).contains("chromaticraft") && cm.path.contains(MusicLoader.instance.musicPath)) {
				evt.volume = Math.max(0.05F, Minecraft.getMinecraft().gameSettings.getSoundLevel(ChromaClient.chromaCategory));
			}
		}
	}
	 */

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
			if (es.sound instanceof ChromaSounds) {
				ChromaSounds c = (ChromaSounds)es.sound;
				if (c.hasWiderPitchRange()) {
					if (es.getPitch() > 2) {
						evt.result = new EnumSound(c.getUpshiftedPitch(), es.posX, es.posY, es.posZ, es.volume, es.pitch/4F, es.attenuate);
					}
					else if (es.getPitch() < 0.5) {
						evt.result = new EnumSound(c.getDownshiftedPitch(), es.posX, es.posY, es.posZ, es.volume, es.pitch*4F, es.attenuate);
					}
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
	public void renderChestCollectionFX(EntityRenderEvent evt) {
		if (evt.entity.worldObj != null && evt.entity instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)evt.entity;
			if (Chromabilities.CHESTCLEAR.enabledOn(ep)) {
				AbilityHelper.instance.renderChestCollectionFX(ep, ReikaRenderHelper.getPartialTickTime());
			}
		}
	}

	@SubscribeEvent
	public void renderMobsThroughWalls(EntityRenderEvent evt) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		if (evt.entity.worldObj != null && Chromabilities.MOBSEEK.enabledOn(ep)) {
			if (AbilityHelper.instance.canRenderEntityXRay(evt.entity)) {
				Entity te = evt.entity;
				GL11.glPushMatrix();
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_LIGHTING);
				ReikaRenderHelper.disableEntityLighting();
				GL11.glEnable(GL11.GL_BLEND);
				BlendMode.DEFAULT.apply();
				GL11.glAlphaFunc(GL11.GL_GREATER, 1/255F);
				GL11.glTranslated(evt.renderPosX, evt.renderPosY, evt.renderPosZ);

				Tessellator v5 = Tessellator.instance;

				RenderManager.instance.getEntityRenderObject(te).doRender(te, 0, 0, 0, evt.partialTickTime, 0);

				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_LIGHTING);
				ReikaRenderHelper.disableEntityLighting();
				GL11.glEnable(GL11.GL_BLEND);
				BlendMode.DEFAULT.apply();
				int a = (int)(96+64*Math.sin(System.currentTimeMillis()/400D+te.getEntityId()));

				if (ReikaEntityHelper.isInWorld(te)) {
					if (visualLOS == null) //lazyload
						visualLOS = RayTracer.getVisualLOS();
					visualLOS.setOrigins(ep.posX, ep.posY, ep.posZ, te.posX, te.posY+te.height/2, te.posZ);
					int c = visualLOS.isClearLineOfSight(ep.worldObj) ? 0x00ff00 : 0xff0000;//ReikaEntityHelper.mobToColor((EntityLivingBase)evt.entity);

					AxisAlignedBB box = te.boundingBox.copy().offset(-te.posX, -te.posY, -te.posZ);//AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1).expand(te.width/4, te.height/4, te.width/4).offset(-te.width/2, -te.height/2, -te.width/2);
					box.maxY = Math.min(box.maxY, box.minY+te.height);
					if (te instanceof EntityGlowCloud) {
						box = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0).expand(0.75, 0.75, 0.75);
					}
					else if (te instanceof EntitySilverfish) {
						box = box.expand(te.width/2, 0, te.width/2);
						box.maxY = box.minY+(box.maxY-box.minY)/2;
					}

					double mx = box.minX;
					double px = box.maxX;
					double my = box.minY;
					double py = box.maxY;
					double mz = box.minZ;
					double pz = box.maxZ;

					v5.startDrawing(GL11.GL_LINE_LOOP);
					v5.setColorRGBA_I(c, a);
					v5.addVertex(mx, my, mz);
					v5.addVertex(px, my, mz);
					v5.addVertex(px, my, pz);
					v5.addVertex(mx, my, pz);
					v5.draw();

					v5.startDrawing(GL11.GL_LINE_LOOP);
					v5.setColorRGBA_I(c, a);
					v5.addVertex(mx, py, mz);
					v5.addVertex(px, py, mz);
					v5.addVertex(px, py, pz);
					v5.addVertex(mx, py, pz);
					v5.draw();

					v5.startDrawing(GL11.GL_LINES);
					v5.setColorRGBA_I(c, a);
					v5.addVertex(mx, my, mz);
					v5.addVertex(mx, py, mz);
					v5.draw();

					v5.startDrawing(GL11.GL_LINES);
					v5.setColorRGBA_I(c, a);
					v5.addVertex(px, my, mz);
					v5.addVertex(px, py, mz);
					v5.draw();

					v5.startDrawing(GL11.GL_LINES);
					v5.setColorRGBA_I(c, a);
					v5.addVertex(px, my, pz);
					v5.addVertex(px, py, pz);
					v5.draw();

					v5.startDrawing(GL11.GL_LINES);
					v5.setColorRGBA_I(c, a);
					v5.addVertex(mx, my, pz);
					v5.addVertex(mx, py, pz);
					v5.draw();
				}

				GL11.glPopAttrib();
				GL11.glPopMatrix();
			}
		}
	}

	@SubscribeEvent
	public void renderSpawners(EntityRenderEvent evt) {
		if (evt.entity.worldObj != null && Chromabilities.SPAWNERSEE.enabledOn(Minecraft.getMinecraft().thePlayer)) {
			AbilityXRays tx = AbilityHelper.instance.getAbilityXRay(evt.entity);
			if (tx != null) {
				Entity te = evt.entity;
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_LIGHTING);
				ReikaRenderHelper.disableEntityLighting();
				GL11.glEnable(GL11.GL_BLEND);
				BlendMode.DEFAULT.apply();
				GL11.glAlphaFunc(GL11.GL_GREATER, 1/255F);
				GL11.glTranslated(evt.renderPosX, evt.renderPosY, evt.renderPosZ);

				Tessellator v5 = Tessellator.instance;

				RenderManager.instance.getEntityRenderObject(te).doRender(te, 0, 0, 0, evt.partialTickTime, 0);

				GL11.glDisable(GL11.GL_TEXTURE_2D);
				int a = (int)(192+64*Math.sin(System.currentTimeMillis()/400D));
				int c = tx.highlightColor;//0xffffff;

				AxisAlignedBB box = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1).expand(te.width/4, te.height/4, te.width/4).offset(-te.width/2, -te.height/2, -te.width/2);

				double mx = box.minX;
				double px = box.maxX;
				double my = box.minY;
				double py = box.maxY;
				double mz = box.minZ;
				double pz = box.maxZ;

				v5.startDrawing(GL11.GL_LINE_LOOP);
				v5.setColorRGBA_I(c, a);
				v5.addVertex(mx, my, mz);
				v5.addVertex(px, my, mz);
				v5.addVertex(px, my, pz);
				v5.addVertex(mx, my, pz);
				v5.draw();

				v5.startDrawing(GL11.GL_LINE_LOOP);
				v5.setColorRGBA_I(c, a);
				v5.addVertex(mx, py, mz);
				v5.addVertex(px, py, mz);
				v5.addVertex(px, py, pz);
				v5.addVertex(mx, py, pz);
				v5.draw();

				v5.startDrawing(GL11.GL_LINES);
				v5.setColorRGBA_I(c, a);
				v5.addVertex(mx, my, mz);
				v5.addVertex(mx, py, mz);
				v5.draw();

				v5.startDrawing(GL11.GL_LINES);
				v5.setColorRGBA_I(c, a);
				v5.addVertex(px, my, mz);
				v5.addVertex(px, py, mz);
				v5.draw();

				v5.startDrawing(GL11.GL_LINES);
				v5.setColorRGBA_I(c, a);
				v5.addVertex(px, my, pz);
				v5.addVertex(px, py, pz);
				v5.draw();

				v5.startDrawing(GL11.GL_LINES);
				v5.setColorRGBA_I(c, a);
				v5.addVertex(mx, my, pz);
				v5.addVertex(mx, py, pz);
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
	public void renderSpawners(TileEntityRenderEvent evt) {
		if (evt.tileEntity.worldObj != null && Chromabilities.SPAWNERSEE.enabledOn(Minecraft.getMinecraft().thePlayer)) {
			AbilityXRays tx = AbilityHelper.instance.getAbilityXRay(evt.tileEntity);
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
		if (!Keyboard.isKeyDown(Keyboard.KEY_LMENU) && this.loadLexiconRecipe(evt.gui, evt.getItem()))
			evt.setCanceled(true);
	}

	private boolean loadLexiconRecipe(GuiContainer gui, ItemStack is) {
		if (is != null && is.getItem() != null) {
			UniqueIdentifier uid = GameRegistry.findUniqueIdentifierFor(is.getItem());
			//if (uid != null && uid.modId.equals(ModList.CHROMATICRAFT.modLabel)) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			ChromaResearch r = ChromaResearch.getPageFor(is);
			if (is.stackTagCompound != null && is.stackTagCompound.getBoolean("boosted") && r.getMachine() != null && r.getMachine().isRepeater()) {
				r = ChromaResearch.TURBOREPEATER;
			}
			if (r != null && r.playerCanSee(ep) && r.isCrafting() && r.isCraftable() && !r.isVanillaRecipe() && r.playerCanSeeRecipe(is, ep) && r.crafts(is)) {
				ep.openGui(ChromatiCraft.instance, r.getCraftingType().ordinal(), null, r.ordinal(), r.getRecipeIndex(is, ep), 1);
				return true;
			}
			else if (ReikaItemHelper.collectionContainsItemStack(ChromaResearch.APIRECIPES.getItemStacks(), is)) {
				ep.openGui(ChromatiCraft.instance, ChromaResearch.APIRECIPES.getCraftingType().ordinal(), null, ChromaResearch.APIRECIPES.ordinal(), ChromaResearch.APIRECIPES.getRecipeIndex(is, ep), 1);
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
	public void stopAuxTileHighlight(DrawBlockHighlightEvent evt) {
		if (evt.target != null && evt.target.typeOfHit == MovingObjectType.BLOCK) {
			World world = Minecraft.getMinecraft().theWorld;
			int x = evt.target.blockX;
			int y = evt.target.blockY;
			int z = evt.target.blockZ;
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileEntityDummyAux) {
				TileEntityDummyAux aux = (TileEntityDummyAux)te;
				if (!aux.getFlag(Flags.HITBOX) && !aux.getFlag(Flags.MOUSEOVER))
					evt.setCanceled(true);
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
			if (ChromaTiles.getTile(world, x, y, z) == ChromaTiles.PYLON || ChromaTiles.getTile(world, x, y, z) == ChromaTiles.SKYPEATER)
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
					if (AbilityHelper.instance.canReachBoostSelect(ep.worldObj, x, y, z, ep)) {
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
							int sz = 16;
							int dx = evt.resolution.getScaledWidth()/2-sz*5/4;
							int dy = evt.resolution.getScaledHeight()/2-sz*5/4;
							/*
						IIcon ico = b.getIcon(Minecraft.getMinecraft().theWorld, x, y, z, 1);
						if (ico != null) {
							float u = ico.getMinU();
							float v = ico.getMinV();
							float du = ico.getMaxU();
							float dv = ico.getMaxV();
							Tessellator v5 = Tessellator.instance;
							ReikaTextureHelper.bindTerrainTexture();
							v5.startDrawingQuads();
							v5.addVertexWithUV(dx, dy+sz, 0, u, dv);
							v5.addVertexWithUV(dx+sz, dy+sz, 0, du, dv);
							v5.addVertexWithUV(dx+sz, dy, 0, du, v);
							v5.addVertexWithUV(dx, dy, 0, u, v);
							v5.draw();
						}
							 */
							GL11.glPushMatrix();
							GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
							GL11.glDepthMask(false);

							boolean back = b == Blocks.chest;
							if (back)
								GL11.glFrontFace(GL11.GL_CW);
							int meta = Minecraft.getMinecraft().theWorld.getBlockMetadata(x, y, z);
							ReikaTextureHelper.bindTerrainTexture();
							//ReikaJavaLibrary.pConsole(b+":"+b.getRenderType());
							if (b instanceof MachineRegistryBlock) {
								TileEnum t = ((MachineRegistryBlock)b).getMachine(Minecraft.getMinecraft().theWorld, x, y, z);
								if (t != null) {
									ItemStack is = t.getCraftedProduct();
									if (is != null) {
										ReikaGuiAPI.instance.drawItemStack(itemRender, f, is, dx, dy);
									}
								}
							}
							else if (b.getRenderType() >= 0) {
								RenderHelper.enableGUIStandardItemLighting();
								double sc = 11;
								GL11.glTranslated(dx+8, dy+8, 0);
								GL11.glScaled(sc, sc, sc);
								GL11.glRotated(180, 0, 0, 1);
								GL11.glRotated(back ? 30 : -30, 1, 0, 0);
								GL11.glRotated(135, 0, 1, 0);
								GL11.glEnable(GL12.GL_RESCALE_NORMAL);
								GL11.glEnable(GL11.GL_LIGHTING);
								//ReikaRenderHelper.enableEntityLighting();
								RenderBlocks.getInstance().renderBlockAsItem(b, meta, 1);
							}
							else {
								/*
							if (b.hasTileEntity(meta)) {
								TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(x, y, z);
								TileEntitySpecialRenderer tesr = TileEntityRendererDispatcher.instance.getSpecialRenderer(te);
								if (tesr != null) {
									dx *= 1.375;
									dy *= 1.375;
									GL11.glTranslated(dx-54, dy-16, 0);
									GL11.glRotated(180, 0, 0, 1);
									GL11.glRotated(45, 0, 1, 0);
									double d = 16;
									GL11.glScaled(d, d, d);
									tesr.renderTileEntityAt(te, 0, 0, 0, ReikaRenderHelper.getPartialTickTime());
									GL11.glPopAttrib();
									GL11.glPopMatrix();
									return;
								}
							}
								 */
								try {
									ArrayList<ItemStack> li = b.getDrops(Minecraft.getMinecraft().theWorld, x, y, z, meta, 0);
									if (!li.isEmpty()) {
										ItemStack is = li.get((int)((System.currentTimeMillis()/1000)%li.size()));
										if (is.getItem() != null)
											ReikaGuiAPI.instance.drawItemStack(itemRender, f, is, dx, dy);
									}
									else {
										ItemStack is = new ItemStack(b, 1, meta);
										if (is.getItem() != null)
											ReikaGuiAPI.instance.drawItemStack(itemRender, f, is, dx, dy);
									}
								}
								catch (Exception e) { //because some mods are derps and make getDrops crash on the client or use it to set states
									ItemStack is = new ItemStack(b, 1, meta);
									if (is.getItem() != null)
										ReikaGuiAPI.instance.drawItemStack(itemRender, f, is, dx, dy);
								}
							}
						}
						GL11.glPopAttrib();
						GL11.glPopMatrix();
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
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				World world = Minecraft.getMinecraft().theWorld;
				int x = evt.target.blockX;
				int y = evt.target.blockY;
				int z = evt.target.blockZ;

				if (AbilityHelper.instance.canReachBoostSelect(world, x, y, z, ep)) {
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
					Block b = world.getBlock(x, y, z);
					b.setBlockBoundsBasedOnState(world, x, y, z);
					AxisAlignedBB box = b.getSelectedBoundingBoxFromPool(world, x, y, z);
					box = box.offset(-x, -y, -z).expand(d, d, d);
					float r = (float)(0.5+Math.sin(System.currentTimeMillis()/500D));
					r = Math.max(0, Math.min(1, r));
					int c = ReikaColorAPI.mixColors(CrystalElement.LIME.getColor(), CrystalElement.PURPLE.getColor(), r);
					RenderGlobal.drawOutlinedBoundingBox(box, c);

					GL11.glPopAttrib();
					GL11.glPopMatrix();

					GL11.glDepthMask(true);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glLineWidth(2.0F);

					evt.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent(receiveCanceled = true)
	public void drawExcavatorHighlight(DrawBlockHighlightEvent evt) {
		if (evt.target != null && evt.target.typeOfHit == MovingObjectType.BLOCK) {
			if (evt.currentItem != null && ChromaItems.EXCAVATOR.matchWith(evt.currentItem)) {
				World world = Minecraft.getMinecraft().theWorld;
				int x = evt.target.blockX;
				int y = evt.target.blockY;
				int z = evt.target.blockZ;
				BlockKey bk = BlockKey.getAt(world, x, y, z);

				if (!ItemExcavationWand.spreadOn(world, x, y, z, bk.blockID, bk.metadata))
					return;

				Coordinate loc = new Coordinate(x, y, z);
				boolean sp = Minecraft.getMinecraft().thePlayer.isSneaking();

				if (!loc.equals(excavatorOverlayOrigin) || !bk.equals(excavatorOverlayBlock) || (excavatorOverlaySpread != sp)) {
					cachedExcavatorOverlay = null;
				}
				excavatorOverlayBlock = bk;
				excavatorOverlayOrigin = loc;
				excavatorOverlaySpread = sp;

				if (bk.blockID != Blocks.air) {
					GL11.glPushMatrix();
					double p2 = x-TileEntityRendererDispatcher.staticPlayerX;
					double p4 = y-TileEntityRendererDispatcher.staticPlayerY;
					double p6 = z-TileEntityRendererDispatcher.staticPlayerZ;
					GL11.glTranslated(p2, p4, p6);
					BlockArray blocks = this.getCachedExcavatorOverlay(world, x, y, z, bk.blockID, bk.metadata);
					ReikaRenderHelper.prepareGeoDraw(true);
					GL11.glDepthMask(false);
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

	private BlockArray getCachedExcavatorOverlay(World world, int x, int y, int z, Block id, int meta) {
		if (cachedExcavatorOverlay == null) {
			cachedExcavatorOverlay = new BlockArray();
			cachedExcavatorOverlay.maxDepth = ItemExcavationWand.getDepth(Minecraft.getMinecraft().thePlayer)-1;
			Set<BlockKey> set = ItemExcavationWand.getSpreadBlocks(world, x, y, z);
			set.add(new BlockKey(id, meta));
			if (Minecraft.getMinecraft().thePlayer.isSneaking())
				cachedExcavatorOverlay.extraSpread = true;
			cachedExcavatorOverlay.taxiCabDistance = true;
			cachedExcavatorOverlay.recursiveAddMultipleWithBounds(world, x, y, z, set, x-32, y-32, z-32, x+32, y+32, z+32);
		}
		return cachedExcavatorOverlay;
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

	public static void doDimensionSpellFailParticles(World world, double x, double y, double z) {
		int n = 32+world.rand.nextInt(32);
		for (int i = 0; i < n; i++) {
			double rx = ReikaRandomHelper.getRandomPlusMinus(x, 0.25);
			double ry = ReikaRandomHelper.getRandomPlusMinus(y, 0.25);
			double rz = ReikaRandomHelper.getRandomPlusMinus(z, 0.25);
			double v = 0.25+world.rand.nextDouble()*0.25;
			double[] vp = ReikaPhysicsHelper.polarToCartesian(v, world.rand.nextDouble()*360, world.rand.nextDouble()*360);
			float s = 5F+world.rand.nextFloat()*10;
			int l = 20+world.rand.nextInt(20);
			CrystalElement c = CrystalElement.randomElement();
			int clr = ReikaColorAPI.getModifiedSat(ReikaColorAPI.getColorWithBrightnessMultiplier(c.getColor(), 0.4F), 2);
			EntityBlurFX fx = new EntityBlurFX(world, rx, ry, rz, vp[0], vp[1], vp[2]);
			fx.setRapidExpand().setAlphaFading().setScale(s).setLife(l).setColor(clr).setColliding().setDrag(0.875);
			fx.noClip = false;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}
}

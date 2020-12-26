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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.renderer.entity.RenderFireball;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHotkeys;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaRenderList;
import Reika.ChromatiCraft.Auxiliary.Render.DonatorPylonRender;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.BlockChromaPortal.TileEntityCrystalPortal;
import Reika.ChromatiCraft.Block.BlockPolyCrystal.TilePolyCrystal;
import Reika.ChromatiCraft.Block.Decoration.BlockAvoLamp.TileEntityAvoLamp;
import Reika.ChromatiCraft.Block.Decoration.BlockColoredAltar.TileEntityColoredAltar;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDecoTile.TileGlowingCracks;
import Reika.ChromatiCraft.Block.Dimension.BlockVoidRift.TileEntityVoidRift;
import Reika.ChromatiCraft.Block.Dimension.Structure.AntFarm.BlockAntKey.AntKeyTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.Gravity.BlockGravityTile.GravityTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.TargetTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.Pinball.BlockPinballTile.TileBouncePad;
import Reika.ChromatiCraft.Block.Dimension.Structure.PistonTape.BlockPistonController.TilePistonDisplay;
import Reika.ChromatiCraft.Block.Dimension.Structure.Water.BlockRotatingLock.TileEntityRotatingLock;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockWarpNode.TileEntityWarpNode;
import Reika.ChromatiCraft.Entity.EntityAbilityFireball;
import Reika.ChromatiCraft.Entity.EntityAurora;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Entity.EntityChainGunShot;
import Reika.ChromatiCraft.Entity.EntityChromaEnderCrystal;
import Reika.ChromatiCraft.Entity.EntityDeathFog;
import Reika.ChromatiCraft.Entity.EntityDimensionFlare;
import Reika.ChromatiCraft.Entity.EntityGlowCloud;
import Reika.ChromatiCraft.Entity.EntityGluon;
import Reika.ChromatiCraft.Entity.EntityLaserPulse;
import Reika.ChromatiCraft.Entity.EntityLightShot;
import Reika.ChromatiCraft.Entity.EntityLumaBurst;
import Reika.ChromatiCraft.Entity.EntityMeteorShot;
import Reika.ChromatiCraft.Entity.EntityMonsterBait;
import Reika.ChromatiCraft.Entity.EntityNukerBall;
import Reika.ChromatiCraft.Entity.EntityOverloadingPylonShock;
import Reika.ChromatiCraft.Entity.EntityParticleCluster;
import Reika.ChromatiCraft.Entity.EntityPistonSpline;
import Reika.ChromatiCraft.Entity.EntitySplashGunShot;
import Reika.ChromatiCraft.Entity.EntityTNTPinball;
import Reika.ChromatiCraft.Entity.EntityThrownGem;
import Reika.ChromatiCraft.Entity.EntityTunnelNuker;
import Reika.ChromatiCraft.Entity.EntityVacuum;
import Reika.ChromatiCraft.Items.Tools.ItemDataCrystal.EntityDataCrystal;
import Reika.ChromatiCraft.ModInterface.EntityChromaManaBurst;
import Reika.ChromatiCraft.Models.ColorizableSlimeModel;
import Reika.ChromatiCraft.Registry.AdjacencyUpgrades;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaISBRH;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Entity.RenderAurora;
import Reika.ChromatiCraft.Render.Entity.RenderBallLightning;
import Reika.ChromatiCraft.Render.Entity.RenderChainGunShot;
import Reika.ChromatiCraft.Render.Entity.RenderChromaManaBurst;
import Reika.ChromatiCraft.Render.Entity.RenderDataCrystal;
import Reika.ChromatiCraft.Render.Entity.RenderDeathFog;
import Reika.ChromatiCraft.Render.Entity.RenderDimensionFlare;
import Reika.ChromatiCraft.Render.Entity.RenderGlowCloud;
import Reika.ChromatiCraft.Render.Entity.RenderGluon;
import Reika.ChromatiCraft.Render.Entity.RenderLaserPulse;
import Reika.ChromatiCraft.Render.Entity.RenderLightShot;
import Reika.ChromatiCraft.Render.Entity.RenderLumaBurst;
import Reika.ChromatiCraft.Render.Entity.RenderMeteorShot;
import Reika.ChromatiCraft.Render.Entity.RenderMonsterBait;
import Reika.ChromatiCraft.Render.Entity.RenderNukerBall;
import Reika.ChromatiCraft.Render.Entity.RenderOverloadingPylonShock;
import Reika.ChromatiCraft.Render.Entity.RenderParticleCluster;
import Reika.ChromatiCraft.Render.Entity.RenderPistonSpline;
import Reika.ChromatiCraft.Render.Entity.RenderSplashGunShot;
import Reika.ChromatiCraft.Render.Entity.RenderTNTPinball;
import Reika.ChromatiCraft.Render.Entity.RenderThrownGem;
import Reika.ChromatiCraft.Render.Entity.RenderTunnelNuker;
import Reika.ChromatiCraft.Render.Entity.RenderVacuum;
import Reika.ChromatiCraft.Render.Item.AltarItemRenderer;
import Reika.ChromatiCraft.Render.Item.ChromaItemRenderer;
import Reika.ChromatiCraft.Render.Item.DataCrystalRenderer;
import Reika.ChromatiCraft.Render.Item.EnderCrystalRenderer;
import Reika.ChromatiCraft.Render.Item.LootChestRenderer;
import Reika.ChromatiCraft.Render.Item.PortalItemRenderer;
import Reika.ChromatiCraft.Render.Item.StructureMapRenderer;
import Reika.ChromatiCraft.Render.TESR.CrystalPlantRenderer;
import Reika.ChromatiCraft.Render.TESR.RenderAvoLamp;
import Reika.ChromatiCraft.Render.TESR.RenderColoredAltar;
import Reika.ChromatiCraft.Render.TESR.RenderCrystalPortal;
import Reika.ChromatiCraft.Render.TESR.RenderLootChest;
import Reika.ChromatiCraft.Render.TESR.RenderPolyCrystal;
import Reika.ChromatiCraft.Render.TESR.RenderWarpNode;
import Reika.ChromatiCraft.Render.TESR.Dimension.RenderAntKey;
import Reika.ChromatiCraft.Render.TESR.Dimension.RenderBouncePad;
import Reika.ChromatiCraft.Render.TESR.Dimension.RenderGlowingCracks;
import Reika.ChromatiCraft.Render.TESR.Dimension.RenderGravityTile;
import Reika.ChromatiCraft.Render.TESR.Dimension.RenderLaserTarget;
import Reika.ChromatiCraft.Render.TESR.Dimension.RenderPistonDisplay;
import Reika.ChromatiCraft.Render.TESR.Dimension.RenderVoidRift;
import Reika.ChromatiCraft.Render.TESR.Dimension.RenderWaterLock;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant;
import Reika.ChromatiCraft.World.Dimension.Rendering.ChromaCloudRenderer;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.DonatorController;
import Reika.DragonAPI.Auxiliary.Trackers.DonatorController.Donator;
import Reika.DragonAPI.Auxiliary.Trackers.KeybindHandler;
import Reika.DragonAPI.Auxiliary.Trackers.PatreonController;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerSpecificRenderer;
import Reika.DragonAPI.Auxiliary.Trackers.SettingInterferenceTracker;
import Reika.DragonAPI.Instantiable.IO.DynamicSoundLoader;
import Reika.DragonAPI.Instantiable.IO.RemoteSourcedAsset.RemoteSourcedAssetRepository;
import Reika.DragonAPI.Instantiable.Rendering.ForcedTextureArmorModel;
import Reika.DragonAPI.Instantiable.Rendering.MultiSheetItemRenderer;
import Reika.DragonAPI.Instantiable.Rendering.TESRItemRenderer;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ChromaClient extends ChromaCommon {

	public static final MultiSheetItemRenderer items = new MultiSheetItemRenderer(ChromatiCraft.instance, ChromatiCraft.class);

	//public static final ItemMachineRenderer machineItems = new ItemMachineRenderer();

	private static final HashMap<ChromaItems, ForcedTextureArmorModel> armorTextures = new HashMap();
	private static final HashMap<ChromaItems, String> armorAssets = new HashMap();

	private static final ChromaItemRenderer placer = new ChromaItemRenderer();

	private static final EnderCrystalRenderer csr = new EnderCrystalRenderer();

	public static KeyBinding key_ability;

	public static SoundCategory chromaCategory;

	public static final RemoteSourcedAssetRepository dynamicAssets = new RemoteSourcedAssetRepository(ChromatiCraft.instance, ChromatiCraft.class, "https://raw.githubusercontent.com/ReikaKalseki/ChromatiCraft/master", "Reika/ChromatiCraft/AssetDL");
	public static final DynamicSoundLoader soundLoader = new DynamicSoundLoader(ChromaSounds.soundList, dynamicAssets);

	@Override
	public void initAssetLoaders() {
		dynamicAssets.addToAssetLoader();
	}

	@Override
	public void registerSounds() {
		soundLoader.register();
		chromaCategory = ReikaRegistryHelper.addSoundCategory("CHROMA", "ChromatiCraft");

		SettingInterferenceTracker.instance.registerSettingHandler(SettingInterferenceTracker.muteInterference);
	}

	@Override
	public void registerRenderers() {
		if (DragonOptions.NORENDERS.getState()) {
			ChromatiCraft.logger.log("Disabling all machine renders for FPS and lag profiling.");
		}
		else {
			this.loadModels();
		}

		ChromaShaders.registerAll();

		ReikaJavaLibrary.initClass(ChromaCloudRenderer.class);

		RenderingRegistry.registerEntityRenderingHandler(EntityBallLightning.class, new RenderBallLightning());
		RenderingRegistry.registerEntityRenderingHandler(EntityAbilityFireball.class, new RenderFireball(2));
		RenderingRegistry.registerEntityRenderingHandler(EntityGluon.class, new RenderGluon());
		RenderingRegistry.registerEntityRenderingHandler(EntityChainGunShot.class, new RenderChainGunShot());
		RenderingRegistry.registerEntityRenderingHandler(EntitySplashGunShot.class, new RenderSplashGunShot());
		RenderingRegistry.registerEntityRenderingHandler(EntityVacuum.class, new RenderVacuum());
		RenderingRegistry.registerEntityRenderingHandler(EntityChromaEnderCrystal.class, new RenderEnderCrystal());
		RenderingRegistry.registerEntityRenderingHandler(EntityMeteorShot.class, new RenderMeteorShot());
		RenderingRegistry.registerEntityRenderingHandler(EntityAurora.class, new RenderAurora());
		RenderingRegistry.registerEntityRenderingHandler(EntityThrownGem.class, new RenderThrownGem());
		RenderingRegistry.registerEntityRenderingHandler(EntityLaserPulse.class, new RenderLaserPulse());
		RenderingRegistry.registerEntityRenderingHandler(EntityPistonSpline.class, new RenderPistonSpline());
		RenderingRegistry.registerEntityRenderingHandler(EntityTNTPinball.class, new RenderTNTPinball());
		RenderingRegistry.registerEntityRenderingHandler(EntityDimensionFlare.class, new RenderDimensionFlare());
		RenderingRegistry.registerEntityRenderingHandler(EntityLumaBurst.class, new RenderLumaBurst());
		RenderingRegistry.registerEntityRenderingHandler(EntityParticleCluster.class, new RenderParticleCluster());
		RenderingRegistry.registerEntityRenderingHandler(EntityNukerBall.class, new RenderNukerBall());
		RenderingRegistry.registerEntityRenderingHandler(EntityGlowCloud.class, new RenderGlowCloud());
		RenderingRegistry.registerEntityRenderingHandler(EntityDataCrystal.class, new RenderDataCrystal());
		RenderingRegistry.registerEntityRenderingHandler(EntityOverloadingPylonShock.class, new RenderOverloadingPylonShock());
		RenderingRegistry.registerEntityRenderingHandler(EntityMonsterBait.class, new RenderMonsterBait());
		RenderingRegistry.registerEntityRenderingHandler(EntityTunnelNuker.class, new RenderTunnelNuker());
		if (ModList.BOTANIA.isLoaded())
			RenderingRegistry.registerEntityRenderingHandler(EntityChromaManaBurst.class, new RenderChromaManaBurst());
		RenderingRegistry.registerEntityRenderingHandler(EntityLightShot.class, new RenderLightShot());
		RenderingRegistry.registerEntityRenderingHandler(EntityDeathFog.class, new RenderDeathFog());

		this.registerSpriteSheets();
		this.registerBlockSheets();

		MinecraftForgeClient.registerItemRenderer(ChromaItems.DATACRYSTAL.getItemInstance(), new DataCrystalRenderer());
		MinecraftForgeClient.registerItemRenderer(ChromaItems.STRUCTMAP.getItemInstance(), new StructureMapRenderer(items));

		RenderSlime slimeRenderer = (RenderSlime)RenderManager.instance.entityRenderMap.get(EntitySlime.class);
		slimeRenderer.scaleAmount = new ColorizableSlimeModel(0);
		ChromatiCraft.logger.log("Overriding Slime Renderer Edge Model.");
	}

	@Override
	public void registerKeys() {
		if (ChromaOptions.KEYBINDABILITY.getState()) {
			key_ability = new KeyBinding("Use Ability", -98, "ChromatiCraft"); //Middle mouse
			//ClientRegistry.registerKeyBinding(key_ability);
			KeybindHandler.instance.addKeybind(key_ability);
		}

		for (int i = 0; i < AbilityHotkeys.SLOTS; i++) {
			AbilityHotkeys.keys[i] = new KeyBinding("Fire Ability "+i, Keyboard.KEY_NUMPAD1+i, "ChromatiCraft"); //defaults to numpad 1,2,3,0
			KeybindHandler.instance.addKeybind(AbilityHotkeys.keys[i]);
		}
	}

	@Override
	public void addArmorRenders() {
		//NVHelmet = RenderingRegistry.addNewArmourRendererPrefix("NVHelmet");
		armor = RenderingRegistry.addNewArmourRendererPrefix("CC");
		/*
		ReikaTextureHelper.forceArmorTexturePath("/Reika/RotaryCraft/Textures/Misc/bedrock_1.png");*/

		addArmorTexture(ChromaItems.FLOATBOOTS, "/Reika/ChromatiCraft/Textures/Misc/floatboots.png");
	}

	private static void addArmorTexture(ChromaItems item, String tex) {
		ChromatiCraft.logger.log("Adding armor texture for "+item+": "+tex);
		armorTextures.put(item, new ForcedTextureArmorModel(ChromatiCraft.class, tex, item.getArmorType()));
		String[] s = tex.split("/");
		String file = s[s.length-1];
		String defaultTex = "chromaticraft:textures/models/armor/"+file;
		//ReikaJavaLibrary.pConsole(defaultTex);
		armorAssets.put(item, defaultTex);
	}

	public static ForcedTextureArmorModel getArmorRenderer(ChromaItems item) {
		return armorTextures.get(item);
	}

	public static String getArmorTextureAsset(ChromaItems item) {
		return armorAssets.get(item);
	}

	public void loadModels() {
		for (int i = 0; i < ChromaTiles.TEList.length; i++) {
			ChromaTiles m = ChromaTiles.TEList[i];
			if (m.hasRender()) {
				ChromaRenderBase render = (ChromaRenderBase)ChromaRenderList.instantiateRenderer(m);
				//int[] renderLists = render.createLists();
				//GLListData.addListData(m, renderLists);
				ClientRegistry.bindTileEntitySpecialRenderer(m.getTEClass(), render);

				if (m == ChromaTiles.ADJACENCY) {
					for (int k = 0; k < 16; k++) {
						if (AdjacencyUpgrades.upgrades[k].isImplemented()) {
							ClientRegistry.bindTileEntitySpecialRenderer(AdjacencyUpgrades.upgrades[k].getTileClass(), render);
						}
					}
				}
			}
		}

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrystalPlant.class, new CrystalPlantRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLootChest.class, new RenderLootChest());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrystalPortal.class, new RenderCrystalPortal());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVoidRift.class, new RenderVoidRift());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityColoredAltar.class, new RenderColoredAltar());
		ClientRegistry.bindTileEntitySpecialRenderer(AntKeyTile.class, new RenderAntKey());
		ClientRegistry.bindTileEntitySpecialRenderer(TargetTile.class, new RenderLaserTarget());
		ClientRegistry.bindTileEntitySpecialRenderer(TileBouncePad.class, new RenderBouncePad());
		ClientRegistry.bindTileEntitySpecialRenderer(GravityTile.class, new RenderGravityTile());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAvoLamp.class, new RenderAvoLamp());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRotatingLock.class, new RenderWaterLock());
		ClientRegistry.bindTileEntitySpecialRenderer(TileGlowingCracks.class, new RenderGlowingCracks());
		ClientRegistry.bindTileEntitySpecialRenderer(TilePolyCrystal.class, new RenderPolyCrystal());
		ClientRegistry.bindTileEntitySpecialRenderer(TilePistonDisplay.class, new RenderPistonDisplay());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWarpNode.class, new RenderWarpNode());

		MinecraftForgeClient.registerItemRenderer(ChromaItems.PLACER.getItemInstance(), ChromatiCraft.instance.isLocked() ? null : placer);
		MinecraftForgeClient.registerItemRenderer(ChromaItems.RIFT.getItemInstance(), ChromatiCraft.instance.isLocked() ? null : placer);
		MinecraftForgeClient.registerItemRenderer(ChromaItems.ADJACENCY.getItemInstance(), ChromatiCraft.instance.isLocked() ? null : placer);

		if (!ChromatiCraft.instance.isLocked())

		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGuardianStone.class, new GuardianStoneRenderer());
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrystalPlant.class, new CrystalPlantRenderer());
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAccelerator.class, new AcceleratorRenderer());

		//MinecraftForgeClient.registerItemRenderer(ChromaBlocks.GUARDIAN.getItem(), teibr);
		//MinecraftForgeClient.registerItemRenderer(ChromaBlocks.ACCELERATOR.getItem(), teibr);

		MinecraftForgeClient.registerItemRenderer(ChromaItems.ENDERCRYSTAL.getItemInstance(), csr);

		MinecraftForgeClient.registerItemRenderer(ChromaBlocks.PORTAL.getItem(), new PortalItemRenderer());
		MinecraftForgeClient.registerItemRenderer(ChromaBlocks.COLORALTAR.getItem(), new AltarItemRenderer());
		MinecraftForgeClient.registerItemRenderer(ChromaBlocks.LOOTCHEST.getItem(), new LootChestRenderer());
		MinecraftForgeClient.registerItemRenderer(ChromaBlocks.AVOLAMP.getItem(), new TESRItemRenderer());
	}

	private void registerBlockSheets() {
		//RenderingRegistry.registerBlockHandler(BlockSheetTexRenderID, block);
	}

	private void registerSpriteSheets() {
		for (int i = 0; i < ChromaItems.itemList.length; i++) {
			ChromaItems c = ChromaItems.itemList[i];
			if (!c.isPlacer() && c != ChromaItems.POTION && c != ChromaItems.MANIPFOCUS)
				MinecraftForgeClient.registerItemRenderer(ChromaItems.itemList[i].getItemInstance(), items);
		}
	}

	// Override any other methods that need to be handled differently client side.

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

	@Override
	public void addDonatorRender() {
		Collection<Donator> donators = new ArrayList();
		donators.addAll(DonatorController.instance.getReikasDonators());
		donators.addAll(PatreonController.instance.getModPatrons("Reika"));
		for (Donator s : donators) {
			if (s.ingameName != null)
				PlayerSpecificRenderer.instance.registerRenderer(s.ingameName, DonatorPylonRender.instance);
			else
				ChromatiCraft.logger.logError("Donator "+s.displayName+" UUID could not be found! Cannot give special render!");
		}
	}

}

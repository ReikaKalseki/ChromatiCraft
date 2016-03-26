/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CustomHitbox;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.BlockChromaPlantTile;
import Reika.ChromatiCraft.Block.BlockCrystalTile;
import Reika.ChromatiCraft.Block.BlockDecoPlant;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Interfaces.LumenTile;
import Reika.ChromatiCraft.ModInterface.TileEntityAspectFormer;
import Reika.ChromatiCraft.ModInterface.TileEntityAspectJar;
import Reika.ChromatiCraft.ModInterface.TileEntityEssentiaRelay;
import Reika.ChromatiCraft.ModInterface.TileEntityLifeEmitter;
import Reika.ChromatiCraft.ModInterface.TileEntityMEDistributor;
import Reika.ChromatiCraft.ModInterface.TileEntityPatternCache;
import Reika.ChromatiCraft.TileEntity.TileEntityAreaHologram;
import Reika.ChromatiCraft.TileEntity.TileEntityAuraLiquifier;
import Reika.ChromatiCraft.TileEntity.TileEntityBiomePainter;
import Reika.ChromatiCraft.TileEntity.TileEntityChromaCrystal;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalCharger;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalConsole;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalFence;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalMusic;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalTank;
import Reika.ChromatiCraft.TileEntity.TileEntityDimensionCore;
import Reika.ChromatiCraft.TileEntity.TileEntityDisplayPoint;
import Reika.ChromatiCraft.TileEntity.TileEntityFarmer;
import Reika.ChromatiCraft.TileEntity.TileEntityLumenWire;
import Reika.ChromatiCraft.TileEntity.TileEntityMultiStorage;
import Reika.ChromatiCraft.TileEntity.TileEntityParticleSpawner;
import Reika.ChromatiCraft.TileEntity.TileEntityPersonalCharger;
import Reika.ChromatiCraft.TileEntity.TileEntityPowerTree;
import Reika.ChromatiCraft.TileEntity.TileEntityPylonTurboCharger;
import Reika.ChromatiCraft.TileEntity.TileEntityStructControl;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAIShutdown;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAccelerator;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityCaveLighter;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityChromaLamp;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityCloakingTower;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityCrystalBeacon;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityCrystalLaser;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityGuardianStone;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemCollector;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemInserter;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityLampController;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityLumenTurret;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityRFDistributor;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityCollector;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityItemFabricator;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityMiner;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityTeleportationPump;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCompoundRepeater;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCreativeSource;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalBroadcaster;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalRepeater;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityRelaySource;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityWeakRepeater;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityAccelerationPlant;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityBiomeReverter;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityChromaFlower;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCobbleGen;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCropSpeedPlant;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityHeatLily;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityAutoEnchanter;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityCrystalFurnace;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityEnchantDecomposer;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityGlowFire;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityInventoryTicker;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntitySpawnerReprogrammer;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityAuraInfuser;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingAuto;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityChromaCrafter;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCrystalBrewer;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityItemStand;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityRitualTable;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityItemRift;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRift;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityTransportWindow;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.Maps.BlockMap;
import Reika.DragonAPI.Interfaces.Registry.TileEnum;
import Reika.DragonAPI.Interfaces.TileEntity.RedstoneTile;
import Reika.DragonAPI.Interfaces.TileEntity.SidePlacedTile;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum ChromaTiles implements TileEnum {

	CHROMAFLOWER("chroma.flower", 		ChromaBlocks.TILEPLANT, 	TileEntityChromaFlower.class, 		0, "ChromaFlowerRenderer"),
	ENCHANTER("chroma.enchanter", 		ChromaBlocks.TILEENTITY, 	TileEntityAutoEnchanter.class, 		0),
	LIQUIFIER("chroma.liquifier", 		ChromaBlocks.TILEENTITY, 	TileEntityAuraLiquifier.class, 		1),
	REPROGRAMMER("chroma.reprogrammer", ChromaBlocks.TILEMODELLED, 	TileEntitySpawnerReprogrammer.class, 0, "RenderSpawnerProgrammer"),
	COLLECTOR("chroma.collector", 		ChromaBlocks.TILEENTITY, 	TileEntityCollector.class, 			4),
	TABLE("chroma.table", 				ChromaBlocks.TILEENTITY, 	TileEntityCastingTable.class, 		5),
	RIFT("chroma.rift", 				ChromaBlocks.RIFT, 			TileEntityRift.class, 				0, "RenderRift"),
	BREWER("chroma.brewer", 			ChromaBlocks.TILEENTITY, 	TileEntityCrystalBrewer.class, 		6),
	GUARDIAN("chroma.guardian", 		ChromaBlocks.TILECRYSTAL, 	TileEntityGuardianStone.class, 		0, "GuardianStoneRenderer"),
	ACCELERATOR("chroma.accelerator", 	ChromaBlocks.TILECRYSTALNONCUBE, TileEntityAccelerator.class, 	0, "AcceleratorRenderer"),
	PYLON("chroma.pylon", 				ChromaBlocks.PYLON, 		TileEntityCrystalPylon.class, 		0, "RenderCrystalPylon"),
	REPEATER("chroma.repeater", 		ChromaBlocks.PYLON, 		TileEntityCrystalRepeater.class, 	1, "RenderCrystalRepeater"),
	LASER("chroma.laser", 				ChromaBlocks.TILEMODELLED, 	TileEntityCrystalLaser.class, 		1, "RenderCrystalLaser"),
	STAND("chroma.stand", 				ChromaBlocks.TILEMODELLED, 	TileEntityItemStand.class, 			2, "RenderItemStand"),
	CHARGER("chroma.charger", 			ChromaBlocks.TILEMODELLED, 	TileEntityCrystalCharger.class, 	3, "RenderCrystalCharger"),
	FURNACE("chroma.furnace", 			ChromaBlocks.TILEENTITY, 	TileEntityCrystalFurnace.class, 	7),
	RITUAL("chroma.ritual", 			ChromaBlocks.TILEENTITY, 	TileEntityRitualTable.class, 		8),
	TANK("chroma.tank", 				ChromaBlocks.TILEENTITY, 	TileEntityCrystalTank.class, 		9, "TankRender"),
	FENCE("chroma.fence", 				ChromaBlocks.TILEMODELLED, 	TileEntityCrystalFence.class, 		4, "RenderCrystalFence"),
	BEACON("chroma.beacon", 			ChromaBlocks.TILEMODELLED, 	TileEntityCrystalBeacon.class, 		5, "RenderCrystalBeacon"),
	ITEMRIFT("chroma.itemrift", 		ChromaBlocks.TILEMODELLED, 	TileEntityItemRift.class, 			6, "RenderItemRift"),
	CRYSTAL("chroma.chromacrystal", 	ChromaBlocks.RAINBOWCRYSTAL, TileEntityChromaCrystal.class, 	0),
	INFUSER("chroma.infuser", 			ChromaBlocks.TILEMODELLED, 	TileEntityAuraInfuser.class, 		7, "RenderInfuser3"),
	FABRICATOR("chroma.fabricator",		ChromaBlocks.TILEMODELLED, 	TileEntityItemFabricator.class, 	8, "RenderItemFabricator"),
	MINER("chroma.miner",				ChromaBlocks.TILEMODELLED, 	TileEntityMiner.class, 				9, "RenderMiner"),
	HEATLILY("chroma.heatlily",			ChromaBlocks.DECOPLANT, 	TileEntityHeatLily.class, 			0),
	ITEMCOLLECTOR("chroma.itemcoll",	ChromaBlocks.TILEENTITY, 	TileEntityItemCollector.class, 		10),
	TICKER("chroma.ticker",				ChromaBlocks.TILEENTITY, 	TileEntityInventoryTicker.class, 	11),
	AISHUTDOWN("chroma.aishutdown",		ChromaBlocks.TILEMODELLED, 	TileEntityAIShutdown.class, 		10),
	TELEPUMP("chroma.telepump",			ChromaBlocks.TILEENTITY, 	TileEntityTeleportationPump.class, 	12),
	COMPOUND("chroma.compound",			ChromaBlocks.PYLON,			TileEntityCompoundRepeater.class, 	2, "RenderMultiRepeater"),
	RELAYSOURCE("chroma.fibersource", 	ChromaBlocks.TILEMODELLED, 	TileEntityRelaySource.class, 		12, "RenderRelaySource"),
	ASPECTJAR("chroma.aspectjar",		ChromaBlocks.TILEMODELLED2, TileEntityAspectJar.class,			2, "AspectJarRenderer", ModList.THAUMCRAFT),
	ASPECT("chroma.aspect", 			ChromaBlocks.TILEMODELLED, 	TileEntityAspectFormer.class,		14, "RenderAspectFormer", ModList.THAUMCRAFT),
	BIOMEPAINTER("chroma.biomechg",		ChromaBlocks.TILEMODELLED2, TileEntityBiomePainter.class,		3, "RenderBiomePainter"),
	LAMP("chroma.lamp",					ChromaBlocks.TILEMODELLED, 	TileEntityChromaLamp.class, 		15, "RenderRainbowLamp"),
	POWERTREE("chroma.powertree",		ChromaBlocks.PYLON, 		TileEntityPowerTree.class, 			3, "PowerTreeRender"),
	LAMPCONTROL("chroma.lampcontrol",	ChromaBlocks.TILEMODELLED2, TileEntityLampController.class, 	0, "RenderLampControl"),
	CREATIVEPYLON("chroma.creativepylon",ChromaBlocks.PYLON, 		TileEntityCreativeSource.class, 	4, "RenderCreativePylon"),
	STRUCTCONTROL("chroma.structcontrol",ChromaBlocks.PYLON,		TileEntityStructControl.class,		5, "RenderStructControl"),
	LIFEEMITTER("chroma.lifeemitter",	ChromaBlocks.TILEMODELLED2, TileEntityLifeEmitter.class,		1/*, "RenderLifeEmitter"*/),
	FARMER("chroma.farmer",				ChromaBlocks.TILEMODELLED2,	TileEntityFarmer.class,				4, "RenderFarmer"),
	AURAPOINT("chroma.aurapoint",		ChromaBlocks.TILECRYSTALNONCUBE, TileEntityAuraPoint.class,		1, "RenderAuraPoint"),
	DIMENSIONCORE("chroma.dimcore",		ChromaBlocks.PYLON,			TileEntityDimensionCore.class,		6, "RenderDimensionCore"),
	AUTOMATOR("chroma.automator",		ChromaBlocks.TILECRYSTALNONCUBE, TileEntityCastingAuto.class,	2, "RenderCastingAuto"),
	DISPLAY("chroma.display",			ChromaBlocks.TILECRYSTAL,	TileEntityDisplayPoint.class,		1),
	MEDISTRIBUTOR("chroma.medistrib",	ChromaBlocks.TILEMODELLED2,	TileEntityMEDistributor.class,		5, "RenderMEDistributor", ModList.APPENG),
	WINDOW("chroma.window",				ChromaBlocks.TILEMODELLED2, TileEntityTransportWindow.class,	6, "RenderTransportWindow"),
	RFDISTRIBUTOR("chroma.rfdistrib",	ChromaBlocks.TILEMODELLED2, TileEntityRFDistributor.class,		7, "RenderRFDistributor"),
	PERSONAL("chroma.personal",			ChromaBlocks.PYLON,			TileEntityPersonalCharger.class,	7, "RenderPersonalCharger"),
	MUSIC("chroma.music",				ChromaBlocks.TILECRYSTAL,	TileEntityCrystalMusic.class,		2, "RenderCrystalMusic"),
	PATTERNS("chroma.patterns",			ChromaBlocks.TILEENTITY,	TileEntityPatternCache.class,		13, ModList.APPENG),
	PYLONTURBO("chroma.turbo", 			ChromaBlocks.TILEMODELLED2,	TileEntityPylonTurboCharger.class,	8, "RenderPylonTurboCharger"),
	TURRET("chroma.turret",				ChromaBlocks.TILEMODELLED2,	TileEntityLumenTurret.class,		9, "RenderLumenTurret"),
	CONSOLE("chroma.console",			ChromaBlocks.CONSOLE,		TileEntityCrystalConsole.class,		0, "RenderCrystalConsole"),
	BROADCAST("chroma.broadcast",		ChromaBlocks.PYLON,			TileEntityCrystalBroadcaster.class,	8, "RenderCrystalBroadcast"),
	CLOAKING("chroma.tower",			ChromaBlocks.TILEMODELLED2, TileEntityCloakingTower.class,		10, "RenderCloakingTower"),
	//POSLINK("chroma.poslink",			ChromaBlocks.TILEMODELLED2,	TileEntityPositionRelay.class,		10),
	HOLOGRAM("chroma.hologram",			ChromaBlocks.TILEMODELLED2, TileEntityAreaHologram.class,		11, "RenderAreaHologram"),
	LIGHTER("chroma.lighter",			ChromaBlocks.TILEMODELLED2,	TileEntityCaveLighter.class,		12, "RenderCaveLighter"),
	STORAGE("chroma.itemstorage",		ChromaBlocks.TILEENTITY,	TileEntityMultiStorage.class,		14),
	GLOWFIRE("chroma.glowfire",			ChromaBlocks.TILEMODELLED2,	TileEntityGlowFire.class,			13),
	ESSENTIARELAY("chroma.essentia",	ChromaBlocks.TILEMODELLED2,	TileEntityEssentiaRelay.class,		14, "RenderEssentiaRelay"),
	INSERTER("chroma.inserter",			ChromaBlocks.TILEENTITY,	TileEntityItemInserter.class,		15),
	REVERTER("chroma.reverter",			ChromaBlocks.DECOPLANT,		TileEntityBiomeReverter.class,		1),
	COBBLEGEN("chroma.cobblegen",		ChromaBlocks.DECOPLANT,		TileEntityCobbleGen.class,			2),
	PLANTACCEL("chroma.plantaccel",		ChromaBlocks.DECOPLANT,		TileEntityAccelerationPlant.class,	3),
	CROPSPEED("chroma.cropspeed",		ChromaBlocks.DECOPLANT,		TileEntityCropSpeedPlant.class,		4),
	CHROMACRAFTER("chroma.chcrafter",	ChromaBlocks.TILEMODELLED2,	TileEntityChromaCrafter.class,		15),
	WEAKREPEATER("chroma.weakrepeater",	ChromaBlocks.PYLON,			TileEntityWeakRepeater.class,		9, "RenderWeakRepeater"),
	ENCHANTDECOMP("chroma.enchantdecomp",ChromaBlocks.TILEMODELLED3,TileEntityEnchantDecomposer.class,	0, "RenderEnchantDecomposer"),
	LUMENWIRE("chroma.lumenwire",		ChromaBlocks.TILEMODELLED3,	TileEntityLumenWire.class,			1, "RenderLumenWire"),
	PARTICLES("chroma.particles",		ChromaBlocks.TILEMODELLED3,	TileEntityParticleSpawner.class,	2, "RenderParticleSpawner");

	private final Class tile;
	private final String name;
	private final String renderer;
	private final int metadata;
	private final ChromaBlocks block;
	private TileEntity renderInstance;
	private final ModList dependency;

	public static final ChromaTiles[] TEList = values();

	private static final BlockMap<ChromaTiles> chromaMappings = new BlockMap();

	private ChromaTiles(String n, ChromaBlocks b, Class <? extends TileEntityChromaticBase> c, int meta) {
		this(n, b, c, meta, null, null);
	}

	private ChromaTiles(String n, ChromaBlocks b, Class <? extends TileEntityChromaticBase> c, int meta, String r) {
		this(n, b, c, meta, r, null);
	}

	private ChromaTiles(String n, ChromaBlocks b, Class <? extends TileEntityChromaticBase> c, int meta, ModList mod) {
		this(n, b, c, meta, null, mod);
	}

	private ChromaTiles(String n, ChromaBlocks b, Class <? extends TileEntityChromaticBase> c, int meta, String r, ModList mod) {
		tile = c;
		name = n;
		block = b;
		metadata = meta;
		renderer = r;
		dependency = mod;
	}

	public Block getBlock() {
		return block.getBlockInstance();
	}

	public int getBlockMetadata() {
		return metadata%16;
	}

	public String getName() {
		return StatCollector.translateToLocal(name);
	}

	public String getUnlocalizedName() {
		return name;
	}

	public boolean renderInPass1() {
		switch(this) {
			case ACCELERATOR:
			case RIFT:
			case PYLON:
			case REPEATER:
			case COMPOUND:
			case LASER:
			case BEACON:
			case FENCE:
			case STAND:
			case RELAYSOURCE:
			case ASPECT:
			case LAMP:
			case MINER:
			case POWERTREE:
			case LAMPCONTROL:
			case STRUCTCONTROL:
			case CHROMAFLOWER:
			case DIMENSIONCORE:
			case AURAPOINT:
			case AUTOMATOR:
			case MEDISTRIBUTOR:
			case RFDISTRIBUTOR:
			case PERSONAL:
			case PYLONTURBO:
			case TURRET:
			case CONSOLE:
			case BROADCAST:
			case CLOAKING:
				//case TANK:
				//case ITEMRIFT:
			case HOLOGRAM:
			case ESSENTIARELAY:
			case WEAKREPEATER:
			case ENCHANTDECOMP:
			case FABRICATOR:
			case PARTICLES:
				return true;
			default:
				return false;
		}
	}

	public boolean hasRender() {
		return renderer != null;
	}

	public String getRenderer() {
		if (!this.hasRender())
			throw new RuntimeException("Tile "+name+" has no render to call!");
		return "Reika.ChromatiCraft.Render.TESR."+renderer;
	}

	public TileEntity createTEInstanceForRender() {
		if (renderInstance == null) {
			try {
				renderInstance = (TileEntity)tile.newInstance();
			}
			catch (InstantiationException e) {
				e.printStackTrace();
				throw new RegistrationException(ChromatiCraft.instance, "Could not create TE instance to render "+this);
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new RegistrationException(ChromatiCraft.instance, "Could not create TE instance to render "+this);
			}
		}
		((TileEntityChromaticBase)renderInstance).animateItem();
		return renderInstance;
	}

	public Class<? extends TileEntity> getTEClass() {
		return tile;
	}

	public static ChromaTiles getTile(IBlockAccess iba, int x, int y, int z) {
		Block id = iba.getBlock(x, y, z);
		int meta = iba.getBlockMetadata(x, y, z);
		return getTileFromIDandMetadata(id, meta);
	}

	public static ChromaTiles getTileFromIDandMetadata(Block id, int meta) {
		return chromaMappings.get(id, meta);
	}

	public static TileEntity createTEFromIDAndMetadata(Block id, int meta) {
		ChromaTiles index = getTileFromIDandMetadata(id, meta);
		if (index == null) {
			ChromatiCraft.logger.logError("ID "+id+" and metadata "+meta+" are not a valid tile identification pair!");
			return null;
		}
		Class TEClass = index.tile;
		try {
			return (TileEntity)TEClass.newInstance();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
			throw new RegistrationException(ChromatiCraft.instance, "ID "+id+" and Metadata "+meta+" failed to instantiate its TileEntity of "+TEClass);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RegistrationException(ChromatiCraft.instance, "ID "+id+" and Metadata "+meta+" failed; illegally accessed a member its TileEntity of "+TEClass);
		}
	}

	public static ArrayList<ChromaTiles> getTilesForBlock(Block b) {
		ArrayList<ChromaTiles> li = new ArrayList();
		for (int i = 0; i < 16; i++) {
			ChromaTiles c = getTileFromIDandMetadata(b, i);
			if (c != null)
				li.add(c);
		}
		return li;
	}

	public static void loadMappings() {
		for (int i = 0; i < TEList.length; i++) {
			ChromaTiles r = TEList[i];
			Block id = r.getBlock();
			int meta = r.getBlockMetadata();
			chromaMappings.put(id, meta, r);
		}
	}

	public boolean hasSneakActions() {
		if (this == STAND)
			return true;
		return false;
	}

	public boolean allowsAcceleration() {
		if (this.isRepeater())
			return false;
		switch(this) {
			case TABLE:
			case RITUAL:
			case POWERTREE:
			case PYLON:
			case AUTOMATOR:
			case INFUSER:
			case AURAPOINT:
			case RELAYSOURCE:
			case PERSONAL:
			case PYLONTURBO:
			case CLOAKING:
			case MINER:
				return false;
			default:
				return true;
		}
	}

	public ItemStack getCraftedProduct() {
		if (this == RIFT)
			return ChromaItems.RIFT.getStackOf();
		if (this == CREATIVEPYLON || this == STRUCTCONTROL)
			return null;
		return ChromaItems.PLACER.getStackOfMetadata(this.ordinal());
	}

	public ItemStack getCraftedNBTProduct(Object... args) {
		ItemStack is = this.getCraftedProduct();
		is.stackTagCompound = new NBTTagCompound();
		for (int i = 0; i < args.length; i += 2) {
			String s = (String)args[i];
			Object o = args[i+1];
			if (o instanceof String) {
				is.stackTagCompound.setString(s, (String)o);
			}
			else if (o instanceof Integer) {
				is.stackTagCompound.setInteger(s, (Integer)o);
			}
			else if (o instanceof Double) {
				is.stackTagCompound.setDouble(s, (Double)o);
			}
			else if (o instanceof Boolean) {
				is.stackTagCompound.setBoolean(s, (Boolean)o);
			}
		}
		return is;
	}

	public boolean isAvailableInCreativeInventory() {
		if (this == RIFT)
			return false;
		if (DragonAPICore.isReikasComputer())
			return true;
		if (dependency != null && !dependency.isLoaded())
			return false;
		return true;
	}

	public boolean canBeVertical() {
		switch(this) {
			case LASER:
				return true;
			default:
				return false;
		}
	}

	public boolean hasNBTVariants() {
		return NBTTile.class.isAssignableFrom(tile);
	}

	public boolean isSidePlaced() {
		return SidePlacedTile.class.isAssignableFrom(tile);
	}

	public double getMinX(TileEntityChromaticBase te) {
		switch(this) {
			case CHARGER:
				return 0.125;
			case ASPECTJAR:
				return 0.1875;
			case WINDOW:
				return ((TileEntityTransportWindow)te).getFacing().offsetX == 0 ? 0 : 0.4375;
			case TURRET:
				return 0.25;
			default:
				return 0;
		}
	}

	public double getMinY(TileEntityChromaticBase te) {
		return 0;
	}

	public double getMinZ(TileEntityChromaticBase te) {
		switch(this) {
			case CHARGER:
				return 0.125;
			case ASPECTJAR:
				return 0.1875;
			case WINDOW:
				return ((TileEntityTransportWindow)te).getFacing().offsetZ == 0 ? 0 : 0.4375;
			case TURRET:
				return 0.25;
			default:
				return 0;
		}
	}

	public double getMaxX(TileEntityChromaticBase te) {
		switch(this) {
			case CHARGER:
				return 0.875;
			case ASPECTJAR:
				return 0.8125;
			case WINDOW:
				return ((TileEntityTransportWindow)te).getFacing().offsetX == 0 ? 1 : 0.5625;
			case TURRET:
				return 0.75;
			default:
				return 1;
		}
	}

	public double getMaxY(TileEntityChromaticBase te) {
		switch(this) {
			case RELAYSOURCE:
				return 0.625;
			case INFUSER:
				return 0.5;
			case ASPECTJAR:
				return 0.875;
			case PYLONTURBO:
				return 0.5;
			case TURRET:
				return 0.625;
			case ENCHANTDECOMP:
				return 0.5625;
			default:
				return 1;
		}
	}

	public double getMaxZ(TileEntityChromaticBase te) {
		switch(this) {
			case CHARGER:
				return 0.875;
			case ASPECTJAR:
				return 0.8125;
			case WINDOW:
				return ((TileEntityTransportWindow)te).getFacing().offsetZ == 0 ? 1 : 0.5625;
			case TURRET:
				return 0.75;
			default:
				return 1;
		}
	}

	@SideOnly(Side.CLIENT)
	public int getRenderOffset() {
		switch(this) {
			case LASER:
			case REPEATER:
			case COMPOUND:
			case BROADCAST:
			case WEAKREPEATER:
				return 0;
			case CHARGER:
				return 29;
			case CLOAKING:
				return 27;
			default:
				return 21;
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean hasBlockRender() {
		return !this.hasRender() || this == TANK || this == TABLE || this == CONSOLE;
	}

	public boolean isPlant() {
		return this.getBlock() instanceof BlockChromaPlantTile || this.getBlock() instanceof BlockDecoPlant;
	}

	public boolean isDummiedOut() {
		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment())
			return false;
		if (this.hasPrerequisite() && !this.getPrerequisite().isLoaded())
			return true;
		return false;
	}

	public boolean hasPrerequisite() {
		return dependency != null;
	}

	public ModList getPrerequisite() {
		return dependency;
	}

	public boolean isIncomplete() {
		return block.hasModel() && !this.hasRender();
	}

	public boolean isConfigDisabled() {
		return false;
	}

	public boolean allowFakePlacer() {
		if (this.isCrystalNetworkTile())
			return false;
		if (this == TABLE || this == RITUAL || this == STAND || this == AUTOMATOR)
			return false;
		if (this == AURAPOINT || this == DIMENSIONCORE)
			return false;
		return true;
	}

	public boolean isCrystalNetworkTile() {
		return CrystalNetworkTile.class.isAssignableFrom(tile);
	}

	public boolean isRepeater() {
		return CrystalRepeater.class.isAssignableFrom(tile);
	}

	public boolean needsRenderOffset() {
		return this == TURRET || this == PYLONTURBO;
	}

	public boolean isTextureFace() {
		return this == PERSONAL || this == AUTOMATOR || this == CLOAKING || this == LIGHTER || this == ESSENTIARELAY;
	}

	public boolean needsSilkTouch() {
		if (this == DIMENSIONCORE)
			return false;
		return block.getBlockInstance() instanceof BlockCrystalTile;
	}

	public boolean isLumenTile() {
		return LumenTile.class.isAssignableFrom(tile);
	}

	public boolean isIntangible() {
		switch(this) {
			case GLOWFIRE:
			case ESSENTIARELAY:
			case LUMENWIRE:
			case PARTICLES:
				return true;
			default:
				return false;
		}
	}

	public boolean providesCustomHitbox() {
		return CustomHitbox.class.isAssignableFrom(tile);
	}

	public boolean suppliesRedstone() {
		return RedstoneTile.class.isAssignableFrom(tile);
	}

}

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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.BlockChromaPlantTile;
import Reika.ChromatiCraft.Block.BlockDecoPlant;
import Reika.ChromatiCraft.ModInterface.TileEntityAspectFormer;
import Reika.ChromatiCraft.TileEntity.TileEntityAuraLiquifier;
import Reika.ChromatiCraft.TileEntity.TileEntityChromaCrystal;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalCharger;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalFence;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalTank;
import Reika.ChromatiCraft.TileEntity.TileEntityPowerTree;
import Reika.ChromatiCraft.TileEntity.TileEntityStructControl;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAIShutdown;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAccelerator;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityChromaLamp;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityCrystalBeacon;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityCrystalLaser;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityGuardianStone;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemCollector;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityLampController;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityCollector;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityItemFabricator;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityMiner;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityTeleportationPump;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCompoundRepeater;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCreativeSource;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalRepeater;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityFiberOptic;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityFiberReceiver;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityFiberTransmitter;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityChromaFlower;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityHeatLily;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityAutoEnchanter;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityCrystalFurnace;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityInventoryTicker;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntitySpawnerReprogrammer;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityAuraInfuser;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCrystalBrewer;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityItemStand;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityRitualTable;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityItemRift;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRift;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.BlockMap;
import Reika.DragonAPI.Interfaces.SidePlacedTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum ChromaTiles {

	CHROMAFLOWER("chroma.flower", 		ChromaBlocks.TILEPLANT, TileEntityChromaFlower.class, 0, "ChromaFlowerRenderer"),
	ENCHANTER("chroma.enchanter", 		ChromaBlocks.TILEENTITY, TileEntityAutoEnchanter.class, 0),
	LIQUIFIER("chroma.liquifier", 		ChromaBlocks.TILEENTITY, TileEntityAuraLiquifier.class, 1),
	REPROGRAMMER("chroma.reprogrammer", ChromaBlocks.TILEMODELLED, TileEntitySpawnerReprogrammer.class, 0, "RenderSpawnerProgrammer"),
	COLLECTOR("chroma.collector", 		ChromaBlocks.TILEENTITY, TileEntityCollector.class, 4),
	TABLE("chroma.table", 				ChromaBlocks.TILEENTITY, TileEntityCastingTable.class, 5),
	RIFT("chroma.rift", 				ChromaBlocks.RIFT, TileEntityRift.class, 0, "RenderRift"),
	BREWER("chroma.brewer", 			ChromaBlocks.TILEENTITY, TileEntityCrystalBrewer.class, 6),
	GUARDIAN("chroma.guardian", 		ChromaBlocks.TILECRYSTAL, TileEntityGuardianStone.class, 0, "GuardianStoneRenderer"),
	ACCELERATOR("chroma.accelerator", 	ChromaBlocks.TILECRYSTALNONCUBE, TileEntityAccelerator.class, 0, "AcceleratorRenderer"),
	PYLON("chroma.pylon", 				ChromaBlocks.PYLON, TileEntityCrystalPylon.class, 0, "RenderCrystalPylon"),
	REPEATER("chroma.repeater", 		ChromaBlocks.PYLON, TileEntityCrystalRepeater.class, 1, "RenderCrystalRepeater"),
	LASER("chroma.laser", 				ChromaBlocks.TILEMODELLED, TileEntityCrystalLaser.class, 1, "RenderCrystalLaser"),
	STAND("chroma.stand", 				ChromaBlocks.TILEMODELLED, TileEntityItemStand.class, 2, "RenderItemStand"),
	CHARGER("chroma.charger", 			ChromaBlocks.TILEMODELLED, TileEntityCrystalCharger.class, 3, "RenderCrystalCharger"),
	FURNACE("chroma.furnace", 			ChromaBlocks.TILEENTITY, TileEntityCrystalFurnace.class, 7),
	RITUAL("chroma.ritual", 			ChromaBlocks.TILEENTITY, TileEntityRitualTable.class, 8),
	TANK("chroma.tank", 				ChromaBlocks.TILEENTITY, TileEntityCrystalTank.class, 9, "TankRender"),
	FENCE("chroma.fence", 				ChromaBlocks.TILEMODELLED, TileEntityCrystalFence.class, 4, "RenderCrystalFence"),
	BEACON("chroma.beacon", 			ChromaBlocks.TILEMODELLED, TileEntityCrystalBeacon.class, 5, "RenderCrystalBeacon"),
	ITEMRIFT("chroma.itemrift", 		ChromaBlocks.TILEMODELLED, TileEntityItemRift.class, 6, "RenderItemRift"),
	CRYSTAL("chroma.chromacrystal", 	ChromaBlocks.RAINBOWCRYSTAL, TileEntityChromaCrystal.class, 0),
	INFUSER("chroma.infuser", 			ChromaBlocks.TILEMODELLED, TileEntityAuraInfuser.class, 7, "RenderInfuser3"),
	FABRICATOR("chroma.fabricator",		ChromaBlocks.TILEMODELLED, TileEntityItemFabricator.class, 8, "RenderItemFabricator"),
	MINER("chroma.miner",				ChromaBlocks.TILEMODELLED, TileEntityMiner.class, 9, "RenderMiner"),
	HEATLILY("chroma.heatlily",			ChromaBlocks.DECOPLANT, TileEntityHeatLily.class, 0),
	ITEMCOLLECTOR("chroma.itemcoll",	ChromaBlocks.TILEENTITY, TileEntityItemCollector.class, 10),
	TICKER("chroma.ticker",				ChromaBlocks.TILEENTITY, TileEntityInventoryTicker.class, 11),
	AISHUTDOWN("chroma.aishutdown",		ChromaBlocks.TILEMODELLED, TileEntityAIShutdown.class, 10),
	TELEPUMP("chroma.telepump",			ChromaBlocks.TILEENTITY, TileEntityTeleportationPump.class, 12),
	//HELP("chroma.helpblock",			ChromaBlocks.TILEMODELLED, TileEntityHelpBlock.class, 11, "HelpBlockRenderer"),
	COMPOUND("chroma.compound",			ChromaBlocks.PYLON,	TileEntityCompoundRepeater.class, 2, "RenderMultiRepeater"),
	FIBERSOURCE("chroma.fibersource", 	ChromaBlocks.TILEMODELLED, TileEntityFiberReceiver.class, 12, "RenderFiberReceiver"),
	FIBER("chroma.fiber",				ChromaBlocks.FIBER,	TileEntityFiberOptic.class, 0, "RenderFiberOptic"),
	FIBERSINK("chroma.fibersink", 		ChromaBlocks.TILEMODELLED, TileEntityFiberTransmitter.class, 13, "RenderFiberEmitter"),
	ASPECT("chroma.aspect", 			ChromaBlocks.TILEMODELLED, TileEntityAspectFormer.class, 14, "RenderAspectFormer", ModList.THAUMCRAFT),
	LAMP("chroma.lamp",					ChromaBlocks.TILEMODELLED, TileEntityChromaLamp.class, 15, "RenderRainbowLamp"),
	POWERTREE("chroma.powertree",		ChromaBlocks.PYLON, 		TileEntityPowerTree.class, 3, "PowerTreeRender"),
	LAMPCONTROL("chroma.lampcontrol",	ChromaBlocks.TILEMODELLED2, TileEntityLampController.class, 0, "RenderLampControl"),
	CREATIVEPYLON("chroma.creativepylon",ChromaBlocks.PYLON, 		TileEntityCreativeSource.class, 4, "RenderCreativePylon"),
	STRUCTCONTROL("chroma.structcontrol",ChromaBlocks.PYLON,		TileEntityStructControl.class,	5, "RenderStructControl");
	//WIRELESS("chroma.wireless",			ChromaBlocks.PYLON,	TileEntityWirelessRepeater.class, 3);
	//CRYSTALFLOWER("chroma.crystalflower", ChromaBlocks.TILEPLANT, TileEntityCrystalFlower.class, 1),
	;//MIXER(),
	//SPLITTER();

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
		case RIFT:
		case PYLON:
		case REPEATER:
		case COMPOUND:
		case LASER:
		case BEACON:
		case FENCE:
		case STAND:
		case FIBERSOURCE:
		case ASPECT:
		case LAMP:
		case POWERTREE:
		case LAMPCONTROL:
		case STRUCTCONTROL:
			//case TANK:
			//case ITEMRIFT:
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
			throw new RegistrationException(ChromatiCraft.instance, "ID "+id+" and Metadata "+meta+" failed illegally accessed its TileEntity of "+TEClass);
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
		for (int i = 0; i < ChromaTiles.TEList.length; i++) {
			ChromaTiles r = ChromaTiles.TEList[i];
			Block id = r.getBlock();
			int meta = r.getBlockMetadata();
			chromaMappings.put(id, meta, r);
		}
	}

	public boolean hasSneakActions() {
		return false;
	}

	public ItemStack getCraftedProduct() {
		if (this == RIFT)
			return ChromaItems.RIFT.getStackOf();
		if (this == CREATIVEPYLON || this == STRUCTCONTROL)
			return null;
		return ChromaItems.PLACER.getStackOfMetadata(this.ordinal());
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
		default:
			return 0;
		}
	}

	public double getMaxX(TileEntityChromaticBase te) {
		switch(this) {
		case CHARGER:
			return 0.875;
		default:
			return 1;
		}
	}

	public double getMaxY(TileEntityChromaticBase te) {
		switch(this) {
		case FIBERSOURCE:
			return 0.625;
		case INFUSER:
			return 0.5;
		default:
			return 1;
		}
	}

	public double getMaxZ(TileEntityChromaticBase te) {
		switch(this) {
		case CHARGER:
			return 0.875;
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
			return 0;
		case CHARGER:
			return 29;
		default:
			return 21;
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean hasBlockRender() {
		return !this.hasRender() || this == ChromaTiles.TANK || this == ChromaTiles.TABLE;
	}

	public boolean isPlant() {
		return this.getBlock() instanceof BlockChromaPlantTile || this.getBlock() instanceof BlockDecoPlant;
	}

	public boolean isDummiedOut() {
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

}

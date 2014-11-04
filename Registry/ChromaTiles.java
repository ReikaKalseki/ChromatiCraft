/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
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
import Reika.ChromatiCraft.TileEntity.TileEntityAIShutdown;
import Reika.ChromatiCraft.TileEntity.TileEntityAccelerator;
import Reika.ChromatiCraft.TileEntity.TileEntityAuraInfuser;
import Reika.ChromatiCraft.TileEntity.TileEntityAuraLiquifier;
import Reika.ChromatiCraft.TileEntity.TileEntityAutoEnchanter;
import Reika.ChromatiCraft.TileEntity.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.TileEntityChromaCrystal;
import Reika.ChromatiCraft.TileEntity.TileEntityCollector;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalBeacon;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalBrewer;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalCharger;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalFence;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalFurnace;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalLaser;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalRepeater;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalTank;
import Reika.ChromatiCraft.TileEntity.TileEntityGuardianStone;
import Reika.ChromatiCraft.TileEntity.TileEntityHelpBlock;
import Reika.ChromatiCraft.TileEntity.TileEntityInventoryTicker;
import Reika.ChromatiCraft.TileEntity.TileEntityItemCollector;
import Reika.ChromatiCraft.TileEntity.TileEntityItemFabricator;
import Reika.ChromatiCraft.TileEntity.TileEntityItemRift;
import Reika.ChromatiCraft.TileEntity.TileEntityItemStand;
import Reika.ChromatiCraft.TileEntity.TileEntityMiner;
import Reika.ChromatiCraft.TileEntity.TileEntityRift;
import Reika.ChromatiCraft.TileEntity.TileEntityRitualTable;
import Reika.ChromatiCraft.TileEntity.TileEntitySpawnerReprogrammer;
import Reika.ChromatiCraft.TileEntity.TileEntityTeleportationPump;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityChromaFlower;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityHeatLily;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.BlockMap;
import Reika.DragonAPI.Interfaces.SidePlacedTile;

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
	INFUSER("chroma.infuser", 			ChromaBlocks.TILEMODELLED, TileEntityAuraInfuser.class, 7, "RenderInfuser"),
	FABRICATOR("chroma.fabricator",		ChromaBlocks.TILEMODELLED, TileEntityItemFabricator.class, 8, "RenderItemFabricator"),
	MINER("chroma.miner",				ChromaBlocks.TILEMODELLED, TileEntityMiner.class, 9, "RenderMiner"),
	HEATLILY("chroma.heatlily",			ChromaBlocks.DECOPLANT, TileEntityHeatLily.class, 0),
	ITEMCOLLECTOR("chroma.itemcoll",	ChromaBlocks.TILEENTITY, TileEntityItemCollector.class, 10),
	TICKER("chroma.ticker",				ChromaBlocks.TILEENTITY, TileEntityInventoryTicker.class, 11),
	AISHUTDOWN("chroma.aishutdown",		ChromaBlocks.TILEMODELLED, TileEntityAIShutdown.class, 10),
	TELEPUMP("chroma.telepump",			ChromaBlocks.TILEENTITY, TileEntityTeleportationPump.class, 12),
	HELP("chroma.helpblock",			ChromaBlocks.TILEMODELLED, TileEntityHelpBlock.class, 11);
	//CRYSTALFLOWER("chroma.crystalflower", ChromaBlocks.TILEPLANT, TileEntityCrystalFlower.class, 1),
	;//MIXER(),
	//SPLITTER();

	private final Class tile;
	private final String name;
	private final String renderer;
	private final int metadata;
	private final ChromaBlocks block;
	private TileEntity renderInstance;

	public static final ChromaTiles[] TEList = values();

	private static final BlockMap<ChromaTiles> chromaMappings = new BlockMap();

	private ChromaTiles(String n, ChromaBlocks b, Class <? extends TileEntityChromaticBase> c, int meta) {
		this(n, b, c, meta, null);
	}

	private ChromaTiles(String n, ChromaBlocks b, Class <? extends TileEntityChromaticBase> c, int meta, String r) {
		tile = c;
		name = n;
		block = b;
		metadata = meta;
		renderer = r;
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
		case LASER:
		case BEACON:
		case FENCE:
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
		return "Reika.ChromatiCraft.Render."+renderer;
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
		return ChromaItems.PLACER.getStackOfMetadata(this.ordinal());
	}

	public boolean isAvailableInCreativeInventory() {
		if (this == RIFT)
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

}

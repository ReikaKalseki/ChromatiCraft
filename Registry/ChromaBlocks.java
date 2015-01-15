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

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.BlockChromaTile;
import Reika.ChromatiCraft.Base.BlockModelledChromaTile;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Block.BlockActiveChroma;
import Reika.ChromatiCraft.Block.BlockCaveCrystal;
import Reika.ChromatiCraft.Block.BlockChromaPlantTile;
import Reika.ChromatiCraft.Block.BlockChromaPortal;
import Reika.ChromatiCraft.Block.BlockCrystalFence;
import Reika.ChromatiCraft.Block.BlockCrystalHive;
import Reika.ChromatiCraft.Block.BlockCrystalLamp;
import Reika.ChromatiCraft.Block.BlockCrystalPlant;
import Reika.ChromatiCraft.Block.BlockCrystalPylon;
import Reika.ChromatiCraft.Block.BlockCrystalRune;
import Reika.ChromatiCraft.Block.BlockCrystalTank;
import Reika.ChromatiCraft.Block.BlockCrystalTile;
import Reika.ChromatiCraft.Block.BlockCrystalTileNonCube;
import Reika.ChromatiCraft.Block.BlockDecoPlant;
import Reika.ChromatiCraft.Block.BlockEnderTNT;
import Reika.ChromatiCraft.Block.BlockFiberOptic;
import Reika.ChromatiCraft.Block.BlockLiquidEnder;
import Reika.ChromatiCraft.Block.BlockLootChest;
import Reika.ChromatiCraft.Block.BlockPath;
import Reika.ChromatiCraft.Block.BlockPath.PathType;
import Reika.ChromatiCraft.Block.BlockPowerTree;
import Reika.ChromatiCraft.Block.BlockPylonStructure;
import Reika.ChromatiCraft.Block.BlockRainbowCrystal;
import Reika.ChromatiCraft.Block.BlockRangeLamp;
import Reika.ChromatiCraft.Block.BlockRift;
import Reika.ChromatiCraft.Block.BlockStructureShield;
import Reika.ChromatiCraft.Block.BlockSuperCrystal;
import Reika.ChromatiCraft.Block.BlockTieredOre;
import Reika.ChromatiCraft.Block.BlockTieredPlant;
import Reika.ChromatiCraft.Block.Dye.BlockDye;
import Reika.ChromatiCraft.Block.Dye.BlockDyeFlower;
import Reika.ChromatiCraft.Block.Dye.BlockDyeGrass;
import Reika.ChromatiCraft.Block.Dye.BlockDyeLeaf;
import Reika.ChromatiCraft.Block.Dye.BlockDyeSapling;
import Reika.ChromatiCraft.Block.Dye.BlockRainbowLeaf;
import Reika.ChromatiCraft.Block.Dye.BlockRainbowSapling;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockChromaFlower;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockChromaTiered;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockCrystal;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockCrystalHive;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockCrystalPlant;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockDyeColors;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockDyeTypes;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockMultiType;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockPath;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockRainbowLeaf;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockRainbowSapling;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockStructShield;
import Reika.DragonAPI.Base.BlockCustomLeaf;
import Reika.DragonAPI.Interfaces.BlockEnum;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;

public enum ChromaBlocks implements BlockEnum {

	TILEPLANT(BlockChromaPlantTile.class, 		ItemBlockChromaFlower.class, 	"Chromatic Plant"),
	TILEENTITY(BlockChromaTile.class, 											"Chromatic Tile"),
	TILEMODELLED(BlockModelledChromaTile.class, 								"Modelled Chromatic Tile"),
	RUNE(BlockCrystalRune.class, 				ItemBlockDyeColors.class, 		"block.crystalrune"),
	CHROMA(BlockActiveChroma.class, 			ChromatiCraft.chroma, 			"fluid.chroma"),
	//ACTIVECHROMA(BlockActiveChroma.class, 		ChromatiCraft.activechroma, 	"Active Liquid Chroma"),
	RIFT(BlockRift.class, 														"Rift"),
	CRYSTAL(BlockCaveCrystal.class, 			ItemBlockCrystal.class, 		"crystal.cave"), //Cave Crystal
	RAINBOWCRYSTAL(BlockRainbowCrystal.class, 									"crystal.rainbow"),
	LAMP(BlockCrystalLamp.class, 				ItemBlockCrystal.class, 		"crystal.lamp"),
	SUPER(BlockSuperCrystal.class, 				ItemBlockCrystal.class, 		"crystal.super"),
	PLANT(BlockCrystalPlant.class, 				ItemBlockCrystalPlant.class, 	"crystal.plant"),
	HIVE(BlockCrystalHive.class, 				ItemBlockCrystalHive.class, 	"block.crystalhive"),
	TILECRYSTAL(BlockCrystalTile.class,											"Crystal Tile"),
	TILECRYSTALNONCUBE(BlockCrystalTileNonCube.class,							"Crystal Tile Non-Cube"),
	DECAY(BlockDyeLeaf.class, 					ItemBlockDyeTypes.class, 		"dye.leaf"),
	DYELEAF(BlockDyeLeaf.class, 				ItemBlockDyeTypes.class, 		"dye.leaf"),
	DYESAPLING(BlockDyeSapling.class, 			ItemBlockDyeTypes.class, 		"dye.sapling"),
	DYE(BlockDye.class, 						ItemBlockDyeTypes.class, 		"dye.block"),
	RAINBOWLEAF(BlockRainbowLeaf.class, 		ItemBlockRainbowLeaf.class, 	"rainbow.leaf"),
	RAINBOWSAPLING(BlockRainbowSapling.class, 	ItemBlockRainbowSapling.class, 	"rainbow.sapling"),
	DYEFLOWER(BlockDyeFlower.class, 			ItemBlockDyeTypes.class, 		"dye.flower"),
	ENDER(BlockLiquidEnder.class, 				ChromatiCraft.ender,			"Liquid Ender"),
	DYEGRASS(BlockDyeGrass.class,				ItemBlockDyeTypes.class,		"dye.grass"),
	PYLONSTRUCT(BlockPylonStructure.class,		ItemBlockMultiType.class,		"block.pylon"),
	PYLON(BlockCrystalPylon.class,				ItemBlockMultiType.class,		"crystal.pylon"),
	TANK(BlockCrystalTank.class,												"crystal.tank"),
	FENCE(BlockCrystalFence.class,												"crystal.fence"),
	TIEREDPLANT(BlockTieredPlant.class,			ItemBlockChromaTiered.class,	"chroma.tieredplant"),
	TIEREDORE(BlockTieredOre.class,				ItemBlockChromaTiered.class,	"chroma.tieredore"),
	DECOPLANT(BlockDecoPlant.class, 			 								"Chromatic Plant 2"),
	//GUI(BlockInWorldGui.class,													"GUI"),
	FIBER(BlockFiberOptic.class,												"Fiber"),
	POWERTREE(BlockPowerTree.class,				ItemBlockDyeTypes.class,		"chroma.powerleaf"),
	TILEMODELLED2(BlockModelledChromaTile.class, 								"Modelled Chromatic Tile 2"),
	LAMPBLOCK(BlockRangeLamp.class,				ItemBlockDyeTypes.class,		"chroma.lampblock"),
	TNT(BlockEnderTNT.class,													"chroma.endertnt"),
	PATH(BlockPath.class,						ItemBlockPath.class,			"chroma.path"),
	STRUCTSHIELD(BlockStructureShield.class,	ItemBlockStructShield.class,	"chroma.shield"),
	LOOTCHEST(BlockLootChest.class,												"chroma.loot"),
	PORTAL(BlockChromaPortal.class,												"chroma.portal");

	private Class blockClass;
	private String blockName;
	private Class itemBlock;
	private Fluid fluid;

	public static final ChromaBlocks[] blockList = values();
	private static final HashMap<Block, ChromaBlocks> blockMap = new HashMap();

	private ChromaBlocks(Class <? extends Block> cl, Class<? extends ItemBlock> ib, Fluid f, String n) {
		blockClass = cl;
		blockName = n;
		itemBlock = ib;
		fluid = f;
	}

	private ChromaBlocks(Class <? extends Block> cl, Fluid f, String n) {
		this(cl, null, f, n);
	}

	private ChromaBlocks(Class <? extends Block> cl, Class<? extends ItemBlock> ib, String n) {
		this(cl, ib, null, n);
	}

	private ChromaBlocks(Class <? extends Block> cl, String n) {
		this(cl, null, null, n);
	}

	public Material getBlockMaterial() {
		if (this.isCrystal())
			return ChromatiCraft.crystalMat;
		switch(this) {
		case TILEPLANT:
		case TIEREDPLANT:
			return Material.plants;
		case CHROMA:
			//case ACTIVECHROMA:
		case ENDER:
			return Material.water;
		case TILECRYSTAL:
		case TILECRYSTALNONCUBE:
		case PYLON:
		case FIBER:
		case POWERTREE:
			return ChromatiCraft.crystalMat;
		case TNT:
			return Material.tnt;
		case PORTAL:
			return Material.portal;
		default:
			return Material.rock;
		}
	}

	public boolean isFluid() {
		return fluid != null;
	}

	public Fluid getFluid() {
		return fluid;
	}

	public boolean isDye() {
		switch(this) {
		case DYE:
		case DYELEAF:
		case DECAY:
		case DYESAPLING:
		case DYEFLOWER:
		case DYEGRASS:
			return true;
		default:
			return false;
		}
	}

	public boolean isLeaf() {
		return BlockCustomLeaf.class.isAssignableFrom(blockClass);
	}

	public boolean isDyePlant() {
		return this == DYESAPLING || this == DYEGRASS || this == DYEFLOWER;
	}

	public boolean isSapling() {
		return BlockSapling.class.isAssignableFrom(blockClass);
	}

	public boolean isTechnical() {
		return BlockChromaTile.class.isAssignableFrom(blockClass);
	}

	@Override
	public Class[] getConstructorParamTypes() {
		if (this.isFluid())
			return new Class[]{Fluid.class, Material.class};
		if (this == DECAY || this == DYELEAF)
			return new Class[]{boolean.class};
		if (this.isLeaf() || this.isDyePlant() || this.isSapling())
			return new Class[0];
		return new Class[]{Material.class};
	}

	@Override
	public Object[] getConstructorParams() {
		if (this.isFluid())
			return new Object[]{this.getFluid(), this.getBlockMaterial()};
		if (this == DECAY || this == DYELEAF)
			return new Object[]{this == DECAY};
		if (this.isLeaf() || this.isDyePlant() || this.isSapling())
			return new Object[0];
		return new Object[]{this.getBlockMaterial()};
	}

	@Override
	public String getUnlocalizedName() {
		return ReikaStringParser.stripSpaces(blockName);
	}

	@Override
	public Class getObjectClass() {
		return blockClass;
	}

	@Override
	public String getBasicName() {
		return StatCollector.translateToLocal(blockName);
	}

	@Override
	public String getMultiValuedName(int meta) {
		if (this.isCrystal() || this.isDye())
			return CrystalElement.elements[meta].displayName+" "+this.getBasicName();
		switch(this) {
		case RUNE:
		case PLANT: //"Crystal Bloom"
		case LAMPBLOCK:
		case POWERTREE:
			return CrystalElement.elements[meta].displayName+" "+this.getBasicName();
		case HIVE:
			return meta == 0 ? "Crystal Hive" : "Pure Hive";
		case PYLON:
			return this.getBasicName();
		case PYLONSTRUCT:
			return StatCollector.translateToLocal("chromablock.pylon."+meta);
		case TIEREDORE:
		case TIEREDPLANT:
			return StatCollector.translateToLocal(this.getBasicName()+"."+meta);
		case PATH:
			return PathType.list[meta].name+" "+this.getBasicName();
		case STRUCTSHIELD:
			return this.getBasicName()+" "+BlockStructureShield.BlockType.list[meta%8].name;
		default:
			return "";
		}
	}

	@Override
	public boolean hasMultiValuedName() {
		if (this == TANK)
			return false;
		if (this == TNT)
			return false;
		if (this == FENCE)
			return false;
		if (this == LOOTCHEST)
			return false;
		return true;
	}

	public boolean isCrystal() {
		return CrystalBlock.class.isAssignableFrom(blockClass);
	}

	@Override
	public int getNumberMetadatas() {
		if (this.isCrystal() || this.isDye())
			return ReikaDyeHelper.dyes.length;
		switch(this) {
		case RUNE:
		case PLANT:
			return 16;
		case HIVE:
			return 2;
		case PYLON:
			return 2;
		case PYLONSTRUCT:
			return 16;
		case PATH:
			return BlockPath.PathType.list.length;
		case STRUCTSHIELD:
			return BlockStructureShield.BlockType.list.length;
		default:
			return 1;
		}
	}

	@Override
	public Class<? extends ItemBlock> getItemBlock() {
		return itemBlock;
	}

	@Override
	public boolean hasItemBlock() {
		return itemBlock != null;
	}

	public boolean isDummiedOut() {
		return blockClass == null;
	}

	public Block getBlockInstance() {
		return ChromatiCraft.blocks[this.ordinal()];
	}

	public static ChromaBlocks getEntryByID(Block id) {
		return blockMap.get(id);
	}

	public Item getItem() {
		return Item.getItemFromBlock(this.getBlockInstance());
	}

	public static void loadMappings() {
		for (int i = 0; i < blockList.length; i++) {
			blockMap.put(blockList[i].getBlockInstance(), blockList[i]);
		}
	}

	public boolean match(ItemStack is) {
		return is != null && is.getItem() == Item.getItemFromBlock(this.getBlockInstance());
	}

	public ItemStack getStackOf() {
		return this.getStackOfMetadata(0);
	}

	public ItemStack getStackOfMetadata(int meta) {
		return new ItemStack(this.getBlockInstance(), 1, meta);
	}

	public boolean hasModel() {
		if (this == TILEMODELLED || this == TILEMODELLED2 || this == PYLON || this == TILECRYSTALNONCUBE)
			return true;
		return false;
	}

}

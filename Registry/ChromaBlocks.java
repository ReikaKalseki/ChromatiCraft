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

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.ItemBlockChromaFlower;
import Reika.ChromatiCraft.ItemBlockDyeColors;
import Reika.ChromatiCraft.Base.BlockChromaTile;
import Reika.ChromatiCraft.Base.BlockModelledChromaTile;
import Reika.ChromatiCraft.Block.BlockChromaPlantTile;
import Reika.ChromatiCraft.Block.BlockCrystalRune;
import Reika.ChromatiCraft.Block.BlockLiquidChroma;
import Reika.ChromatiCraft.Block.BlockRift;
import Reika.DragonAPI.Interfaces.RegistryEnum;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;

public enum ChromaBlocks implements RegistryEnum {

	TILEPLANT(BlockChromaPlantTile.class, ItemBlockChromaFlower.class, "Chromatic Plant", false),
	TILEENTITY(BlockChromaTile.class, "Chromatic Tile", false),
	TILEMODELLED(BlockModelledChromaTile.class, "Modelled Chromatic Tile", true),
	RUNE(BlockCrystalRune.class, ItemBlockDyeColors.class, "block.crystalrune", false),
	CHROMA(BlockLiquidChroma.class, ChromatiCraft.chroma, "Liquid Chroma", false),
	RIFT(BlockRift.class, "Rift", false);
	//LASER(null, null, null, false);

	private Class blockClass;
	private String blockName;
	private Class itemBlock;
	private boolean model;
	private Fluid fluid;

	public static final ChromaBlocks[] blockList = values();
	private static final HashMap<Integer, ChromaBlocks> blockMap = new HashMap();

	private ChromaBlocks(Class <? extends Block> cl, Class<? extends ItemBlock> ib, Fluid f, String n, boolean m) {
		blockClass = cl;
		blockName = n;
		itemBlock = ib;
		model = m;
		fluid = f;
	}

	private ChromaBlocks(Class <? extends Block> cl, Fluid f, String n, boolean m) {
		this(cl, null, f, n, m);
	}

	private ChromaBlocks(Class <? extends Block> cl, Class<? extends ItemBlock> ib, String n, boolean m) {
		this(cl, ib, null, n, m);
	}

	private ChromaBlocks(Class <? extends Block> cl, String n) {
		this(cl, null, null, n, false);
	}

	private ChromaBlocks(Class <? extends Block> cl, String n, boolean m) {
		this(cl, null, null, n, m);
	}

	public int getBlockID() {
		return ChromatiCraft.config.getBlockID(this.ordinal());
	}

	public Material getBlockMaterial() {
		switch(this) {
		case TILEPLANT:
			return Material.plants;
		case CHROMA:
			return Material.water;
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

	@Override
	public Class[] getConstructorParamTypes() {
		if (this.isFluid())
			return new Class[]{int.class, Fluid.class, Material.class};
		return new Class[]{int.class, Material.class};
	}

	@Override
	public Object[] getConstructorParams() {
		if (this.isFluid())
			return new Object[]{this.getBlockID(), this.getFluid(), this.getBlockMaterial()};
		return new Object[]{this.getBlockID(), this.getBlockMaterial()};
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
		switch(this) {
		case RUNE:
			return ReikaDyeHelper.dyes[meta].colorName+" "+this.getBasicName();
		default:
			return "";
		}
	}

	@Override
	public boolean hasMultiValuedName() {
		return true;
	}

	@Override
	public int getNumberMetadatas() {
		switch(this) {
		case RUNE:
			return 16;
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

	@Override
	public String getConfigName() {
		return blockName;
	}

	@Override
	public int getDefaultID() {
		return 630+this.ordinal();
	}

	@Override
	public boolean isBlock() {
		return true;
	}

	@Override
	public boolean isItem() {
		return false;
	}

	@Override
	public String getCategory() {
		return "Chromatic Blocks";
	}

	public boolean isDummiedOut() {
		return blockClass == null;
	}

	public Block getBlockVariable() {
		return ChromatiCraft.blocks[this.ordinal()];
	}

	public boolean isModelled() {
		return model;
	}

	public int getID() {
		return this.getBlockID();
	}

	@Override
	public boolean overwritingItem() {
		return false;
	}

	public static ChromaBlocks getEntryByID(int id) {
		return blockMap.get(id);
	}

	public static void loadMappings() {
		for (int i = 0; i < blockList.length; i++) {
			blockMap.put(blockList[i].getBlockID(), blockList[i]);
		}
	}

}

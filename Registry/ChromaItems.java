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

import Reika.ChromatiCraft.ChromaNames;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Items.ItemChromaCrafting;
import Reika.ChromatiCraft.Items.ItemChromaPlacer;
import Reika.ChromatiCraft.Items.ItemCluster;
import Reika.ChromatiCraft.Items.ItemCrystalSeeds;
import Reika.ChromatiCraft.Items.ItemCrystalShard;
import Reika.ChromatiCraft.Items.ItemRiftPlacer;
import Reika.ChromatiCraft.Items.ItemTreeDye;
import Reika.ChromatiCraft.Items.Tools.ItemChromaBucket;
import Reika.ChromatiCraft.Items.Tools.ItemCrystalPotion;
import Reika.ChromatiCraft.Items.Tools.ItemEnderCrystal;
import Reika.ChromatiCraft.Items.Tools.ItemExcavator;
import Reika.ChromatiCraft.Items.Tools.ItemInventoryLinker;
import Reika.ChromatiCraft.Items.Tools.ItemManipulator;
import Reika.ChromatiCraft.Items.Tools.ItemPendant;
import Reika.ChromatiCraft.Items.Tools.ItemVacuumGun;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.ItemEnum;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.common.registry.GameRegistry;

public enum ChromaItems implements ItemEnum {

	BUCKET(16, true, 		"chroma.bucket", 		ItemChromaBucket.class),
	PLACER(0, true,			"chroma.placer",		ItemChromaPlacer.class),
	LINK(0,	false,			"chroma.invlink",		ItemInventoryLinker.class),
	RIFT(0, false,			"chroma.rift",			ItemRiftPlacer.class),
	TOOL(32, false,			"chroma.tool",			ItemManipulator.class),
	SHARD(64, true, 		"crystal.shard", 		ItemCrystalShard.class),
	POTION(0, true, 		"crystal.potion", 		ItemCrystalPotion.class),
	CLUSTER(80, true, 		"crystal.cluster", 		ItemCluster.class),
	PENDANT(96, true, 		"crystal.pendant", 		ItemPendant.class),
	PENDANT3(112, true, 	"crystal.pendant3", 	ItemPendant.class),
	SEED(128, true, 		"crystal.seeds", 		ItemCrystalSeeds.class),
	ENDERCRYSTAL(0, true, 	"chroma.endercrystal", 	ItemEnderCrystal.class),
	DYE(48, true,			"dye.item", 			ItemTreeDye.class),
	EXCAVATOR(33, false,	"chroma.excavator",		ItemExcavator.class),
	VACUUMGUN(34, false,	"chroma.vac",			ItemVacuumGun.class),
	CRAFTING(256, true,		"chroma.craft",			ItemChromaCrafting.class);

	private final int index;
	private final boolean hasSubtypes;
	private final String name;
	private final Class itemClass;
	private int texturesheet;
	private ModList condition;

	private int maxindex;

	private ChromaItems(int tex, boolean sub, String n, Class <?extends Item> iCl) {
		this(tex, sub, n, iCl, null);
	}

	private ChromaItems(int tex, boolean sub, String n, Class <?extends Item> iCl, ModList api) {
		index = tex%256;
		texturesheet = tex/256;
		hasSubtypes = sub;
		name = n;
		itemClass = iCl;
		condition = api;
	}
	/*
	private ChromaItems(int lotex, int hitex, boolean sub, String n, Class <?extends Item> iCl) {
		if (lotex > hitex)
			throw new RegistrationException(ChromatiCraft.instance, "Invalid item sprite registration for "+n+"! Backwards texture bounds?");
		texturesheet = 1;
		if (lotex < 0) {
			lotex = -lotex;
			hitex = -hitex;
			texturesheet = 0;
		}
		if (lotex > 255) {
			texturesheet = lotex/256;
			lotex -= texturesheet*256;
			hitex -= texturesheet*256;
		}
		index = lotex;
		maxindex = lotex;
		hasSubtypes = sub;
		name = n;
		itemClass = iCl;
	}*/

	public static final ChromaItems[] itemList = values();

	@Override
	public Class[] getConstructorParamTypes() {
		if (this.isArmor()) {
			return new Class[]{int.class, int.class}; // ID, Sprite index, Armor render
		}

		return new Class[]{int.class}; // ID, Sprite index
	}

	@Override
	public Object[] getConstructorParams() {
		if (this.isArmor()) {
			return new Object[]{this.getTextureIndex(), this.getArmorRender()};
		}
		else
			return new Object[]{this.getTextureIndex()};
	}

	private ArmorMaterial getArmorMaterial() {
		return null;
	}

	public int getArmorType() {
		switch(this) {
		default:
			return 0;
		}
	}

	public int getTextureIndex() {
		return index;
	}

	public static boolean isRegistered(ItemStack is) {
		return isRegistered(is.getItem());
	}

	public static boolean isRegistered(Item id) {
		for (int i = 0; i < itemList.length; i++) {
			if (itemList[i].getItemInstance() == id)
				return true;
		}
		return false;
	}

	public static ChromaItems getEntryByID(Item id) {
		for (int i = 0; i < itemList.length; i++) {
			if (itemList[i].getItemInstance() == id)
				return itemList[i];
		}
		//throw new RegistrationException(ChromatiCraft.instance, "Item ID "+id+" was called to the item registry but does not exist there!");
		return null;
	}

	public static ChromaItems getEntry(ItemStack is) {
		if (is == null)
			return null;
		return getEntryByID(is.getItem());
	}

	public String getName(int dmg) {
		if (this.hasMultiValuedName())
			return this.getMultiValuedName(dmg);
		return name;
	}

	public String getBasicName() {
		return StatCollector.translateToLocal(name);
	}

	public String getMultiValuedName(int meta) {
		if (!this.hasMultiValuedName())
			throw new RuntimeException("Item "+name+" was called for a multi-name, yet does not have one!");
		switch(this) {
		case PLACER:
			return ChromaTiles.TEList[meta].getName();
		case BUCKET:
			return StatCollector.translateToLocal(ChromaNames.getFluidName(meta))+" "+this.getBasicName();
		case SHARD:
		case POTION:
		case PENDANT:
		case PENDANT3:
		case DYE:
			return ReikaDyeHelper.dyes[meta].colorName+" "+this.getBasicName();
		case CLUSTER:
			return StatCollector.translateToLocal(ChromaNames.clusterNames[meta]);
		case SEED:
			return ReikaDyeHelper.dyes[meta%16].colorName+" "+this.getBasicName();
		case ENDERCRYSTAL:
			return this.getBasicName();
		case CRAFTING:
			return StatCollector.translateToLocal(ChromaNames.craftingNames[meta]);
		default:
			break;
		}
		throw new RuntimeException("Item "+name+" was called for a multi-name, but it was not registered!");
	}

	public int getArmorRender() {
		if (!this.isArmor())
			throw new RegistrationException(ChromatiCraft.instance, "Item "+name+" is not an armor yet was called for its render!");
		throw new RegistrationException(ChromatiCraft.instance, "Item "+name+" is an armor yet has no specified render!");
	}

	public String getUnlocalizedName() {
		return ReikaStringParser.stripSpaces(name).toLowerCase();
	}

	public Item getItemInstance() {
		return ChromatiCraft.items[this.ordinal()];
	}

	public boolean hasMultiValuedName() {
		return hasSubtypes;
	}

	public boolean isTool() {
		return false;//ItemChromatiTool.class.isAssignableFrom(itemClass);
	}

	public boolean isCreativeOnly() {
		return false;
	}

	public int getTextureSheet() {
		return texturesheet;
	}

	public int getNumberMetadatas() {
		if (!hasSubtypes)
			return 1;
		switch(this) {
		case BUCKET:
			return 2;
		case PLACER:
			return ChromaTiles.TEList.length;
		case SHARD:
		case PENDANT:
		case PENDANT3:
		case POTION:
		case DYE:
			return ReikaDyeHelper.dyes.length;
		case CLUSTER:
			return ChromaNames.clusterNames.length;
		case SEED:
			return 32;
		case ENDERCRYSTAL:
			return 2;
		case CRAFTING:
			return ChromaNames.craftingNames.length;
		default:
			throw new RegistrationException(ChromatiCraft.instance, "Item "+name+" has subtypes but the number was not specified!");
		}
	}

	public boolean isArmor() {
		switch(this) {
		default:
			return false;
		}
	}

	public ItemStack getCraftedProduct(int amt) {
		return new ItemStack(this.getItemInstance(), amt, 0);
	}

	public ItemStack getCraftedMetadataProduct(int amt, int meta) {
		return new ItemStack(this.getItemInstance(), amt, meta);
	}

	public ItemStack getStackOf() {
		return this.getCraftedProduct(1);
	}

	public ItemStack getStackOfMetadata(int meta) {
		return this.getCraftedMetadataProduct(1, meta);
	}

	public boolean overridesRightClick(ItemStack is) {
		switch(this) {
		case TOOL:
			return true;
		default:
			return false;
		}
	}

	@Override
	public Class getObjectClass() {
		return itemClass;
	}

	public boolean isDummiedOut() {
		if (this.hasPrerequisite() && !this.getPrerequisite().isLoaded())
			return true;
		return itemClass == null;
	}

	private boolean hasPrerequisite() {
		return condition != null;
	}

	private ModList getPrerequisite() {
		return condition;
	}

	public void addRecipe(Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(this.getStackOf(), params);
			//WorktableRecipes.getInstance().addRecipe(this.getStackOf(), params);
		}
	}

	public void addSizedRecipe(int num, Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(this.getCraftedProduct(num), params);
			//WorktableRecipes.getInstance().addRecipe(this.getCraftedProduct(num), params);
		}
	}

	public void addMetaRecipe(int meta, Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(this.getStackOfMetadata(meta), params);
			//WorktableRecipes.getInstance().addRecipe(this.getStackOfMetadata(meta), params);
		}
	}

	public void addSizedMetaRecipe(int meta, int num, Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(this.getCraftedMetadataProduct(num, meta), params);
			//WorktableRecipes.getInstance().addRecipe(this.getCraftedMetadataProduct(num, meta), params);
		}
	}

	public void addEnchantedRecipe(Object... params) {
		if (!this.isDummiedOut()) {
			ItemStack is = this.getEnchantedStack();
			GameRegistry.addRecipe(is, params);
			//WorktableRecipes.getInstance().addRecipe(is, params);
		}
	}

	public void addShapelessEnchantedRecipe(Object... params) {
		if (!this.isDummiedOut()) {
			ItemStack is = this.getEnchantedStack();
			GameRegistry.addShapelessRecipe(is, params);
			//WorktableRecipes.getInstance().addShapelessRecipe(is, params);
		}
	}

	public ItemStack getEnchantedStack() {
		ItemStack is = this.getStackOf();
		if (is == null)
			return is;
		switch(this) {
		default:
			break;
		}
		return is;
	}

	public void addShapelessRecipe(Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addShapelessRecipe(this.getStackOf(), params);
			//WorktableRecipes.getInstance().addShapelessRecipe(this.getStackOf(), params);
		}
	}

	public void addRecipe(IRecipe ir) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(ir);
			//WorktableRecipes.addRecipe(ir);
		}
	}

	public void addOreRecipe(Object... in) {
		if (!this.isDummiedOut()) {
			ItemStack out = this.getStackOf();
			boolean added = ReikaRecipeHelper.addOreRecipe(out, in);
			if (added)
				;//WorktableRecipes.addRecipe(new ShapedOreRecipe(out, in));
		}
	}

	public boolean isAvailableInCreativeInventory() {
		if (ChromatiCraft.instance.isLocked())
			return false;
		if (this.isDummiedOut())
			return false;
		return true;
	}

	@Override
	public boolean overwritingItem() {
		return false;
	}

	public boolean isContinuousCreativeMetadatas() {
		if (this.isTool())
			return false;
		if (this.isArmor())
			return false;
		switch(this) {
		default:
			return true;
		}
	}

	public boolean matchWith(ItemStack is) {
		return is.getItem() == this.getItemInstance();
	}

	public boolean isPlacer() {
		switch(this) {
		case PLACER:
		case RIFT:
		case ENDERCRYSTAL:
			return true;
		default:
			return false;
		}
	}
}
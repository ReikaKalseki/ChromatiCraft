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

import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.StatCollector;
import Reika.ChromatiCraft.ChromaNames;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaBucket;
import Reika.ChromatiCraft.Items.ItemChromaPlacer;
import Reika.ChromatiCraft.Items.ItemInventoryLinker;
import Reika.ChromatiCraft.Items.ItemManipulator;
import Reika.ChromatiCraft.Items.ItemRiftPlacer;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.RegistryEnum;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import cpw.mods.fml.common.registry.GameRegistry;

public enum ChromaItems implements RegistryEnum {

	BUCKET(16, true, 		"chroma.bucket", 		ItemChromaBucket.class),
	PLACER(0, true,			"chroma.placer",		ItemChromaPlacer.class),
	LINK(0,	false,			"chroma.invlink",		ItemInventoryLinker.class),
	RIFT(0, false,			"chroma.rift",			ItemRiftPlacer.class),
	TOOL(32, false,			"chroma.tool",			ItemManipulator.class);

	private int index;
	private boolean hasSubtypes;
	private String name;
	private Class itemClass;
	private int texturesheet;
	private ModList condition;

	private int maxindex;

	private ChromaItems(int tex, boolean sub, String n, Class <?extends Item> iCl) {
		this(tex, sub, n, iCl, null);
	}

	private ChromaItems(int tex, boolean sub, String n, Class <?extends Item> iCl, ModList api) {
		texturesheet = 1;
		if (tex < 0) {
			tex = -tex;
			texturesheet = 0;
		}
		if (tex > 255) {
			texturesheet = tex/256;
			tex -= texturesheet*256;
		}
		index = tex;
		hasSubtypes = sub;
		name = n;
		itemClass = iCl;
		condition = api;
	}

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
	}

	public static final ChromaItems[] itemList = values();

	@Override
	public Class[] getConstructorParamTypes() {
		if (this.isArmor()) {
			return new Class[]{int.class, int.class, int.class}; // ID, Sprite index, Armor render
		}

		return new Class[]{int.class, int.class}; // ID, Sprite index
	}

	@Override
	public Object[] getConstructorParams() {
		if (this.isArmor()) {
			return new Object[]{ChromatiCraft.config.getItemID(this.ordinal()), this.getTextureIndex(), this.getArmorRender()};
		}
		else
			return new Object[]{ChromatiCraft.config.getItemID(this.ordinal()), this.getTextureIndex()};
	}

	private EnumArmorMaterial getArmorMaterial() {
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
		return isRegistered(is.itemID);
	}

	public static boolean isRegistered(int id) {
		for (int i = 0; i < itemList.length; i++) {
			if (itemList[i].getShiftedID() == id)
				return true;
		}
		return false;
	}

	public static ChromaItems getEntryByID(int id) {
		for (int i = 0; i < itemList.length; i++) {
			if (itemList[i].getShiftedID() == id)
				return itemList[i];
		}
		//throw new RegistrationException(ChromatiCraft.instance, "Item ID "+id+" was called to the item registry but does not exist there!");
		return null;
	}

	public static ChromaItems getEntry(ItemStack is) {
		if (is == null)
			return null;
		return getEntryByID(is.itemID);
	}

	public String getName(int dmg) {
		if (this.hasMultiValuedName())
			return this.getMultiValuedName(dmg);
		return name;
	}

	public String getBasicName() {
		String sg = name;
		return StatCollector.translateToLocal(sg);
	}

	public String getMultiValuedName(int dmg) {
		if (!this.hasMultiValuedName())
			throw new RuntimeException("Item "+name+" was called for a multi-name, yet does not have one!");
		switch(this) {
		case PLACER:
			return ChromaTiles.TEList[dmg].getName();
		case BUCKET:
			return StatCollector.translateToLocal(ChromaNames.getFluidName(dmg))+" "+this.getBasicName();
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

	public int getID() {
		return ChromatiCraft.config.getItemID(this.ordinal());
	}

	public int getShiftedID() {
		return ChromatiCraft.config.getItemID(this.ordinal())+256;
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
		return index/256;
	}

	public int getNumberMetadatas() {
		if (!hasSubtypes)
			return 1;
		switch(this) {
		case BUCKET:
			return 1;
		case PLACER:
			return ChromaTiles.TEList.length;
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
		return new ItemStack(this.getShiftedID(), amt, 0);
	}

	public ItemStack getCraftedMetadataProduct(int amt, int meta) {
		return new ItemStack(this.getShiftedID(), amt, meta);
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

	@Override
	public Class<? extends ItemBlock> getItemBlock() {
		return null;
	}

	@Override
	public boolean hasItemBlock() {
		return false;
	}

	@Override
	public String getConfigName() {
		return this.getBasicName();
	}

	@Override
	public int getDefaultID() {
		return 21500+this.ordinal();
	}

	@Override
	public boolean isBlock() {
		return false;
	}

	@Override
	public boolean isItem() {
		return true;
	}

	@Override
	public String getCategory() {
		if (this.isTool())
			return "Tool Item IDs";
		return "Item IDs";
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
}

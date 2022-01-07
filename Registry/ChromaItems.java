/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.util.HashMap;
import java.util.Locale;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import Reika.ChromatiCraft.ChromaNames;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemCrystalBasic;
import Reika.ChromatiCraft.Base.ItemPoweredChromaTool;
import Reika.ChromatiCraft.Items.ItemAdjacencyPlacer;
import Reika.ChromatiCraft.Items.ItemChromaBerry;
import Reika.ChromatiCraft.Items.ItemChromaCrafting;
import Reika.ChromatiCraft.Items.ItemChromaMisc;
import Reika.ChromatiCraft.Items.ItemChromaPlacer;
import Reika.ChromatiCraft.Items.ItemCluster;
import Reika.ChromatiCraft.Items.ItemCrystalSeeds;
import Reika.ChromatiCraft.Items.ItemCrystalShard;
import Reika.ChromatiCraft.Items.ItemDimGen;
import Reika.ChromatiCraft.Items.ItemElementalStone;
import Reika.ChromatiCraft.Items.ItemFertilitySeed;
import Reika.ChromatiCraft.Items.ItemInfoFragment;
import Reika.ChromatiCraft.Items.ItemMagicBranch;
import Reika.ChromatiCraft.Items.ItemPatternCrystal;
import Reika.ChromatiCraft.Items.ItemStorageCrystal;
import Reika.ChromatiCraft.Items.ItemT2EnderEye;
import Reika.ChromatiCraft.Items.ItemTieredResource;
import Reika.ChromatiCraft.Items.ItemUnknownArtefact;
import Reika.ChromatiCraft.Items.ItemBlock.ItemLinkedTilePlacer;
import Reika.ChromatiCraft.Items.Tools.ItemAuraPouch;
import Reika.ChromatiCraft.Items.Tools.ItemBoostedPendant;
import Reika.ChromatiCraft.Items.Tools.ItemBottleneckFinder;
import Reika.ChromatiCraft.Items.Tools.ItemBulkMover;
import Reika.ChromatiCraft.Items.Tools.ItemCaveExitFinder;
import Reika.ChromatiCraft.Items.Tools.ItemChainGun;
import Reika.ChromatiCraft.Items.Tools.ItemChromaBook;
import Reika.ChromatiCraft.Items.Tools.ItemChromaBucket;
import Reika.ChromatiCraft.Items.Tools.ItemConnector;
import Reika.ChromatiCraft.Items.Tools.ItemCrystalCard;
import Reika.ChromatiCraft.Items.Tools.ItemCrystalPotion;
import Reika.ChromatiCraft.Items.Tools.ItemDataCrystal;
import Reika.ChromatiCraft.Items.Tools.ItemDoorKey;
import Reika.ChromatiCraft.Items.Tools.ItemEfficiencyCrystal;
import Reika.ChromatiCraft.Items.Tools.ItemEnderBucket;
import Reika.ChromatiCraft.Items.Tools.ItemEnderBucket.BucketMode;
import Reika.ChromatiCraft.Items.Tools.ItemEnderCrystal;
import Reika.ChromatiCraft.Items.Tools.ItemEtherealPendant;
import Reika.ChromatiCraft.Items.Tools.ItemFloatstoneBoots;
import Reika.ChromatiCraft.Items.Tools.ItemInventoryLinker;
import Reika.ChromatiCraft.Items.Tools.ItemKillAuraGun;
import Reika.ChromatiCraft.Items.Tools.ItemLightGun;
import Reika.ChromatiCraft.Items.Tools.ItemManipulator;
import Reika.ChromatiCraft.Items.Tools.ItemMultiTool;
import Reika.ChromatiCraft.Items.Tools.ItemOreSilker;
import Reika.ChromatiCraft.Items.Tools.ItemOwnerKey;
import Reika.ChromatiCraft.Items.Tools.ItemPendant;
import Reika.ChromatiCraft.Items.Tools.ItemPylonFinder;
import Reika.ChromatiCraft.Items.Tools.ItemRecipeCacheCapsule;
import Reika.ChromatiCraft.Items.Tools.ItemSplashGun;
import Reika.ChromatiCraft.Items.Tools.ItemSplineAttack;
import Reika.ChromatiCraft.Items.Tools.ItemStructureMap;
import Reika.ChromatiCraft.Items.Tools.ItemTeleGateLock;
import Reika.ChromatiCraft.Items.Tools.ItemThrowableGem;
import Reika.ChromatiCraft.Items.Tools.ItemVacuumGun;
import Reika.ChromatiCraft.Items.Tools.ItemWarpCapsule;
import Reika.ChromatiCraft.Items.Tools.ItemWideCollector;
import Reika.ChromatiCraft.Items.Tools.Powered.ItemCrystalProbe;
import Reika.ChromatiCraft.Items.Tools.Powered.ItemNetherKey;
import Reika.ChromatiCraft.Items.Tools.Powered.ItemOrePick;
import Reika.ChromatiCraft.Items.Tools.Powered.ItemPurifyCrystal;
import Reika.ChromatiCraft.Items.Tools.Powered.ItemSpawnerBypass;
import Reika.ChromatiCraft.Items.Tools.Powered.ItemStructureFinder;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemBuilderWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemCaptureWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemDuplicationWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemExcavationWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemFlightWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemGrowthWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemLightWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemMobSonar;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemMobilityWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemResetWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemShooWand;
import Reika.ChromatiCraft.Items.Tools.Wands.ItemTransitionWand;
import Reika.ChromatiCraft.ModInterface.ItemColoredModInteract;
import Reika.ChromatiCraft.ModInterface.AE.ItemCrystalCell;
import Reika.ChromatiCraft.ModInterface.AE.ItemShieldedCell;
import Reika.ChromatiCraft.ModInterface.AE.ItemVoidStorage;
import Reika.ChromatiCraft.ModInterface.Bees.ItemChromaBeeFrame;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.ItemAbilityFocus;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.ItemManipulatorFocus;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.ItemWarpProofer;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant.Modifier;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.Registry.ItemEnum;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.common.registry.GameRegistry;

public enum ChromaItems implements ItemEnum {

	BUCKET(16, true, 		"chroma.bucket", 		ItemChromaBucket.class),
	PLACER(0, true,			"chroma.placer",		ItemChromaPlacer.class),
	LINK(0,	false,			"chroma.invlink",		ItemInventoryLinker.class),
	RIFT(0, false,			"chroma.rift",			ItemLinkedTilePlacer.class),
	ADJACENCY(0, true,		"chroma.adjacency",		ItemAdjacencyPlacer.class),
	TOOL(32, false,			"chroma.tool",			ItemManipulator.class),
	SHARD(64, true, 		"crystal.shard", 		ItemCrystalShard.class),
	POTION(0, true, 		"crystal.potion", 		ItemCrystalPotion.class),
	CLUSTER(80, true, 		"crystal.cluster", 		ItemCluster.class),
	PENDANT(96, true, 		"crystal.pendant", 		ItemPendant.class),
	PENDANT3(112, true, 	"crystal.pendant3", 	ItemBoostedPendant.class),
	SEED(128, true, 		"crystal.seeds", 		ItemCrystalSeeds.class),
	ENDERCRYSTAL(0, true, 	"chroma.endercrystal", 	ItemEnderCrystal.class),
	DYE(48, true,			"dye.item", 			ItemCrystalBasic.class),
	EXCAVATOR(80, false,	"chroma.excavator",		ItemExcavationWand.class),
	VACUUMGUN(8, false,		"chroma.vac",			ItemVacuumGun.class),
	CRAFTING(0, true,		"chroma.craft",			ItemChromaCrafting.class),
	LENS(144, true,			"chroma.lens",			ItemCrystalBasic.class),
	STORAGE(2, true,		"chroma.storage",		ItemStorageCrystal.class),
	LINKTOOL(3, false,		"chroma.linker",		ItemConnector.class),
	BERRY(176, true,		"chroma.berry",			ItemChromaBerry.class),
	FINDER(4, false,		"chroma.finder",		ItemPylonFinder.class),
	TIERED(128, true,		"chroma.tiered",		ItemTieredResource.class),
	ELEMENTAL(192, true,	"chroma.elemental",		ItemElementalStone.class),
	TRANSITION(81, false,	"chroma.transition",	ItemTransitionWand.class),
	HELP(5, false,			"chroma.helpitem",		ItemChromaBook.class),
	WARP(7, true,			"chroma.warp",			ItemWarpProofer.class, ModList.THAUMCRAFT),
	MISC(224, true,			"chroma.misc",			ItemChromaMisc.class),
	FRAGMENT(9, true,		"chroma.fragment",		ItemInfoFragment.class),
	DUPLICATOR(82, false,	"chroma.duplicator",	ItemDuplicationWand.class),
	TELEPORT(83, false,		"chroma.teleport",		ItemMobilityWand.class),
	BUILDER(84,	false,		"chroma.builder",		ItemBuilderWand.class),
	CAPTURE(85, false,		"chroma.capture",		ItemCaptureWand.class),
	VOIDCELL(10, false,		"chroma.aecell",		ItemVoidStorage.class, ModList.APPENG),
	MODINTERACT(176, true,	"chroma.modinteract",	ItemChromaMisc.class),
	MULTITOOL(25, false,	"chroma.multitool",		ItemMultiTool.class),
	SHOO(86, false,			"chroma.shoo",			ItemShooWand.class),
	OREPICK(27, false,		"chroma.orepick",		ItemOrePick.class),
	ORESILK(26, false,		"chroma.oresilk",		ItemOreSilker.class),
	AURAPOUCH(29, false,	"chroma.aurapouch",		ItemAuraPouch.class),
	GROWTH(87, false,		"chroma.growth",		ItemGrowthWand.class),
	//REMOTETERM(11, false,	"chroma.terminal",		ItemRemoteTerminal.class, ModList.APPENG),
	BULKMOVER(12, false,	"chroma.bulkmove",		ItemBulkMover.class),
	CHAINGUN(13, false,		"chroma.chaingun",		ItemChainGun.class),
	HOVERWAND(88, false,	"chroma.hover",			ItemFlightWand.class),
	DIMGEN(48, true,		"chroma.dimgen",		ItemDimGen.class),
	SPLASHGUN(14, false,	"chroma.splashgun",		ItemSplashGun.class),
	KEY(15,	false,			"chroma.key",			ItemDoorKey.class),
	CARD(31, false,			"chroma.card",			ItemCrystalCard.class),
	SHARE(30, false,		"chroma.share",			ItemOwnerKey.class),
	RESET(89, false,		"chroma.reset",			ItemResetWand.class),
	COLOREDMOD(0, true, 	"chroma.colormod", 		ItemColoredModInteract.class),
	//FLUIDWAND(90, false,	"chroma.fluid",			ItemFluidWand.class),
	LIGHTWAND(91, false,	"chroma.lightwand",		ItemLightWand.class),
	CRYSTALCELL(22, false,	"chroma.crystalcell",	ItemCrystalCell.class, ModList.APPENG),
	PURIFY(33, true,		"chroma.purify",		ItemPurifyCrystal.class),
	EFFICIENCY(37, false,	"chroma.efficiency",	ItemEfficiencyCrystal.class),
	//FADETORCH(38, true,		"chroma.torch",			ItemFadingTorch.class),
	KILLAURAGUN(38, false,	"chroma.killauragun",	ItemKillAuraGun.class),
	THROWGEM(0,	true,		"chroma.throwgem",		ItemThrowableGem.class),
	FLOATBOOTS(40, false,	"chroma.floatboots",	ItemFloatstoneBoots.class),
	WARPCAPSULE(41, false,	"chroma.warpitem",		ItemWarpCapsule.class),
	BEEFRAME(208, false, 	"chroma.beeframe",		ItemChromaBeeFrame.class, ModList.FORESTRY),
	STRUCTUREFINDER(49, false, "chroma.structfind",	ItemStructureFinder.class),
	MAGICBRANCH(0, true, 	"chroma.branch",		ItemMagicBranch.class),
	ARTEFACT(112, true,		"chroma.artefact",		ItemUnknownArtefact.class),
	DATACRYSTAL(44, false, 	"chroma.datacrystal",	ItemDataCrystal.class),
	MOBSONAR(92, false,		"chroma.mobsonar",		ItemMobSonar.class),
	FERTILITYSEED(209, true,"chroma.fertileseed",	ItemFertilitySeed.class),
	CAVEPATHER(45, false,	"chroma.cavepather",	ItemCaveExitFinder.class),
	SPLINEATTACK(46, false,	"chroma.splineattack",	ItemSplineAttack.class),
	SHIELDEDCELL(47, false,	"chroma.shieldedcell",	ItemShieldedCell.class),
	BOTTLENECK(48, false,	"chroma.bottleneck",	ItemBottleneckFinder.class),
	SPAWNERBYPASS(43, false,"chroma.spawnerbypass",	ItemSpawnerBypass.class),
	ENDEREYE(0,	false,		"chroma.endereye",		ItemT2EnderEye.class),
	LIGHTGUN(51, false,		"chroma.lightgun",		ItemLightGun.class),
	STRUCTMAP(52, false,	"chroma.structmap",		ItemStructureMap.class),
	PROBE(53, true,			"chroma.probe",			ItemCrystalProbe.class),
	TELEGATELOCK(63, false,	"chroma.telegatelock",	ItemTeleGateLock.class),
	ENDERBUCKET(64, true,	"chroma.enderbucket",	ItemEnderBucket.class),
	WIDECOLLECTOR(66, false, "chroma.widecollector",ItemWideCollector.class),
	MANIPFOCUS(0, false,	"chroma.manipfocus",	ItemManipulatorFocus.class, ModList.THAUMCRAFT),
	NETHERKEY(67, false,	"chroma.netherkey",		ItemNetherKey.class),
	ABILITYFOCUS(0, false,	"chroma.abilityfocus",	ItemAbilityFocus.class, ModList.THAUMCRAFT),
	RECIPECACHE(69, false,	"chroma.recipecapsule",	ItemRecipeCacheCapsule.class),
	MULTIPATTERN(71, false,	"chroma.patterncrystal",ItemPatternCrystal.class, ModList.APPENG),
	ETHERPENDANT(72, false,	"chroma.etherpendant",	ItemEtherealPendant.class),
	;

	private final int index;
	private final boolean hasSubtypes;
	private final String name;
	private final Class itemClass;
	private int texturesheet;
	private ModList condition;

	private int maxindex;

	public static final ChromaItems[] itemList = values();
	private static final HashMap<Item, ChromaItems> itemMap = new HashMap();

	private ChromaItems(int tex, boolean sub, String n, Class <?extends Item> iCl) {
		this(tex, sub, n, iCl, null);
	}

	private ChromaItems(int tex, boolean sub, String n, Class <?extends Item> iCl, ModList api) {
		index = tex;
		hasSubtypes = sub;
		name = n;
		itemClass = iCl;
		condition = api;
	}

	@Override
	public Class[] getConstructorParamTypes() {
		if (this.isArmor()) {
			return new Class[]{int.class, int.class}; // ID, Sprite index, Armor render
		}
		else if (this == MANIPFOCUS || this == ABILITYFOCUS)
			return new Class[] {};

		return new Class[]{int.class}; // ID, Sprite index
	}

	@Override
	public Object[] getConstructorParams() {
		if (this.isArmor()) {
			return new Object[]{this.getTextureIndex(), this.getArmorRender()};
		}
		else if (this == MANIPFOCUS || this == ABILITYFOCUS)
			return new Object[0];
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
		return getEntryByID(id) != null;
	}

	public static ChromaItems getEntryByID(Item id) {
		return itemMap.get(id);
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
		if (meta == OreDictionary.WILDCARD_VALUE)
			return this.getBasicName()+" (Any)";
		switch(this) {
			case PLACER:
				return ChromaTiles.TEList[meta].getName();
			case ADJACENCY:
				return AdjacencyUpgrades.upgrades[meta].getName();
			case BUCKET:
				//if (meta >= 2)
				//	meta = 2;
				return StatCollector.translateToLocal(ChromaNames.fluidNames[meta])+" "+this.getBasicName();
			case SHARD:
				String s = meta >= 16 ? /*EnumChatFormatting.GREEN.toString()*/"Boosted " : "";
				return s+CrystalElement.elements[meta%16].displayName+" "+this.getBasicName();
			case POTION:
			case PENDANT:
			case PENDANT3:
			case DYE:
			case LENS:
			case BERRY:
			case ELEMENTAL:
			case THROWGEM:
				return CrystalElement.elements[meta].displayName+" "+this.getBasicName();
			case CLUSTER:
				return StatCollector.translateToLocal(ChromaNames.clusterNames[meta]);
			case TIERED:
				return StatCollector.translateToLocal(ChromaNames.tieredNames[meta]);
			case SEED:
				return CrystalElement.elements[meta%16].displayName+" "+this.getBasicName();
			case MISC:
				return StatCollector.translateToLocal(ChromaNames.miscNames[meta]);
			case MODINTERACT:
				return StatCollector.translateToLocal(ChromaNames.modInteractNames[meta]);
			case COLOREDMOD:
				return CrystalElement.elements[meta%16].displayName+" "+StatCollector.translateToLocal(ChromaNames.coloredModInteractNames[meta/16]);
			case ENDERCRYSTAL:
				return this.getBasicName();
			case CRAFTING:
				return StatCollector.translateToLocal(ChromaNames.craftingNames[meta]);
			case STORAGE:
				return StatCollector.translateToLocal(ChromaNames.storageNames[meta])+" "+this.getBasicName();
			case FRAGMENT:
				return this.getBasicName();
			case OREPICK:
			case ORESILK:
			case MULTITOOL:
				return this.getBasicName();
			case WARP:
				String pre = meta == 1 ? "Charged " : "Inert ";
				return pre+this.getBasicName();
			case DIMGEN:
				return StatCollector.translateToLocal(ChromaNames.dimGenNames[meta]);
				//case FADETORCH:
				//	return this.getBasicName();
			case MAGICBRANCH:
				return ItemMagicBranch.BranchTypes.list[meta].name().toLowerCase(Locale.ENGLISH)+" "+this.getBasicName();
			case ARTEFACT:
				return StatCollector.translateToLocal("chromaunknownartefact."+ItemUnknownArtefact.ArtefactTypes.list[meta].name().toLowerCase(Locale.ENGLISH));
			case FERTILITYSEED:
				return this.getBasicName();
			case ENDERBUCKET:
				return this.getBasicName()+" ("+BucketMode.list[meta].displayName+" Mode)";
			default:
				break;
		}
		throw new RuntimeException("Item "+name+" was called for a multi-name, but it was not registered!");
	}

	public int getArmorRender() {
		if (!this.isArmor())
			throw new RegistrationException(ChromatiCraft.instance, "Item "+name+" is not an armor yet was called for its render!");
		switch(this) {
			case FLOATBOOTS:
				return ChromatiCraft.proxy.armor;
			default:
				break;
		}
		throw new RegistrationException(ChromatiCraft.instance, "Item "+name+" is an armor yet has no specified render!");
	}

	public String getUnlocalizedName() {
		return ReikaStringParser.stripSpaces(name).toLowerCase(Locale.ENGLISH);
	}

	public Item getItemInstance() {
		return ChromatiCraft.items[this.ordinal()];
	}

	public boolean hasMultiValuedName() {
		return hasSubtypes && !ItemPoweredChromaTool.class.isAssignableFrom(itemClass);
	}

	public boolean isTool() {
		return false;//ItemChromatiTool.class.isAssignableFrom(itemClass);
	}

	public boolean isCreativeOnly() {
		return false;
	}

	public int getNumberMetadatas() {
		if (ItemPoweredChromaTool.class.isAssignableFrom(itemClass))
			return 1;//((ItemPoweredChromaTool)this.getItemInstance()).getChargeStates();
		if (!hasSubtypes)
			return 1;
		switch(this) {
			case BUCKET:
				return ChromaNames.fluidNames.length;
			case PLACER:
				return ChromaTiles.TEList.length;
			case ADJACENCY:
				return CrystalElement.elements.length;
			case SHARD:
				return CrystalElement.elements.length*2;
			case PENDANT:
			case PENDANT3:
			case POTION:
			case DYE:
			case LENS:
			case BERRY:
			case ELEMENTAL:
			case THROWGEM:
				return CrystalElement.elements.length;
			case CLUSTER:
				return ChromaNames.clusterNames.length;
			case TIERED:
				return ChromaNames.tieredNames.length;
			case MISC:
				return ChromaNames.miscNames.length;
			case MODINTERACT:
				return ChromaNames.modInteractNames.length;
			case COLOREDMOD:
				return ChromaNames.coloredModInteractNames.length*CrystalElement.elements.length;
			case SEED:
				return ReikaMathLibrary.intpow2(2, Modifier.list.length+4);//16; //was 32
			case ENDERCRYSTAL:
				return 2;
			case CRAFTING:
				return ChromaNames.craftingNames.length;
			case STORAGE:
				return ChromaNames.storageNames.length;
			case FRAGMENT:
				return ReikaJavaLibrary.getEnumLengthWithoutInitializing(ChromaResearch.class);
			case WARP:
				return 2;
			case OREPICK:
				return 720;
			case MULTITOOL:
				return 400;
			case ORESILK:
				return 180;
			case DIMGEN:
				return ChromaNames.dimGenNames.length;
				//case FADETORCH:
				//	return ItemFadingTorch.STATES;
			case MAGICBRANCH:
				return ItemMagicBranch.BranchTypes.list.length;
			case ARTEFACT:
				return ItemUnknownArtefact.ArtefactTypes.list.length;
			case FERTILITYSEED:
				return 7;
			case ENDERBUCKET:
				return 2;
			default:
				throw new RegistrationException(ChromatiCraft.instance, "Item "+name+" has subtypes but the number was not specified!");
		}
	}

	public boolean isArmor() {
		switch(this) {
			case FLOATBOOTS:
				return true;
			default:
				return false;
		}
	}

	public ItemStack getCraftedProduct(int amt) {
		return new ItemStack(this.getItemInstance(), amt, 0);
	}

	public ItemStack getCraftedMetadataProduct(int amt, int meta) {
		if (this.getItemInstance() == null)
			throw new IllegalStateException(this+" item is null!");
		return new ItemStack(this.getItemInstance(), amt, meta);
	}

	public ItemStack getStackOf() {
		return this.getCraftedProduct(1);
	}

	public ItemStack getStackOfMetadata(int meta) {
		return this.getCraftedMetadataProduct(1, meta);
	}

	public ItemStack getStackOf(CrystalElement e) {
		return this.getStackOfMetadata(e.ordinal());
	}

	public boolean overridesRightClick(ItemStack is) {
		switch(this) {
			case TOOL:
			case LINKTOOL:
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
		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment())
			return false;
		if (this.hasPrerequisite() && !this.getPrerequisite().isLoaded())
			return true;
		return itemClass == null;
	}

	public boolean hasPrerequisite() {
		return condition != null;
	}

	public ModList getPrerequisite() {
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
		if (is == null)
			return false;
		return is.getItem() == this.getItemInstance();
	}

	public boolean isPlacer() {
		switch(this) {
			case PLACER:
			case RIFT:
			case ADJACENCY:
			case ENDERCRYSTAL:
				return true;
			default:
				return false;
		}
	}

	public static void loadMappings() {
		for (int i = 0; i < itemList.length; i++) {
			ChromaItems r = itemList[i];
			itemMap.put(r.getItemInstance(), r);
		}
	}

	public ItemStack getAnyMetaStack() {
		return this.getStackOfMetadata(OreDictionary.WILDCARD_VALUE);
	}

	public boolean isConfigDisabled() {
		return false;
	}

	public ChromaResearch getFragment() {
		return ChromaResearch.getPageFor(this.getStackOf());
	}
}

/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.Bees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Block.Dimension.BlockDimensionDeco.DimDecoTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre.TieredOres;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredPlant.TieredPlants;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.ModInterface.ItemColoredModInteract;
import Reika.ChromatiCraft.ModInterface.Bees.ChromaBeeHelpers.CompoundConditionalProductProvider;
import Reika.ChromatiCraft.ModInterface.Bees.ChromaBeeHelpers.ConditionalProductBee;
import Reika.ChromatiCraft.ModInterface.Bees.ChromaBeeHelpers.ConditionalProductProvider;
import Reika.ChromatiCraft.ModInterface.Bees.EffectAlleles.ArtefactEffect;
import Reika.ChromatiCraft.ModInterface.Bees.EffectAlleles.ChromaEffect;
import Reika.ChromatiCraft.ModInterface.Bees.EffectAlleles.CrystalEffect;
import Reika.ChromatiCraft.ModInterface.Bees.EffectAlleles.PolychromaEffect;
import Reika.ChromatiCraft.ModInterface.Bees.EffectAlleles.PrecursorEffect;
import Reika.ChromatiCraft.ModInterface.Bees.EffectAlleles.RechargeEffect;
import Reika.ChromatiCraft.ModInterface.Bees.EffectAlleles.SparklifyEffect;
import Reika.ChromatiCraft.ModInterface.Bees.FlowerAlleles.CrystalAllele;
import Reika.ChromatiCraft.ModInterface.Bees.FlowerAlleles.FlowerProviderCrystal;
import Reika.ChromatiCraft.ModInterface.Bees.FlowerAlleles.FlowerProviderMulti;
import Reika.ChromatiCraft.ModInterface.Bees.FlowerAlleles.MetaAlloyAllele;
import Reika.ChromatiCraft.ModInterface.Bees.FlowerAlleles.MultiAllele;
import Reika.ChromatiCraft.ModInterface.Bees.ProductChecks.AreaBlockCheck;
import Reika.ChromatiCraft.ModInterface.Bees.ProductChecks.AuraLocusCheck;
import Reika.ChromatiCraft.ModInterface.Bees.ProductChecks.ChargedShardCheck;
import Reika.ChromatiCraft.ModInterface.Bees.ProductChecks.CrystalPlantCheck;
import Reika.ChromatiCraft.ModInterface.Bees.ProductChecks.FlowerCheck;
import Reika.ChromatiCraft.ModInterface.Bees.ProductChecks.IridescentShardCheck;
import Reika.ChromatiCraft.ModInterface.Bees.ProductChecks.LeafCheck;
import Reika.ChromatiCraft.ModInterface.Bees.ProductChecks.ProductCondition;
import Reika.ChromatiCraft.ModInterface.Bees.ProductChecks.ProgressionCheck;
import Reika.ChromatiCraft.ModInterface.Bees.ProductChecks.RainbowTreeCheck;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.ModularLogger;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.GUI.StatusLogger;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Effect;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Fertility;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Flower;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Flowering;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Life;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Speeds;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Tolerance;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies.BeeBranch;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies.TraitsBee;
import Reika.DragonAPI.ModInteract.Bees.BeeTraits;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ForestryHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ForestryHandler.Combs;
import Reika.DragonAPI.ModInteract.ItemHandlers.MagicBeesHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.OreBerryBushHandler.BerryTypes;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorState;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IIndividual;

public class CrystalBees {

	static final String LOGGER_TAG = "CrystalBees";

	static final Random rand = new Random();

	static RawBee feedstock;

	static BasicBee protective;
	static BasicBee luminous;
	static BasicBee hostile;
	static BasicBee magical;

	static BasicBee sparkle;

	static BasicBee crystal;
	static BasicBee purity;

	static PrecursorBee tower;

	static PrecursorBee ua;
	static PrecursorBee precursor;

	static AdvancedBee chroma;
	static AdvancedBee lumen;
	static AdvancedBee aura;
	static AdvancedBee multi;

	static Fertility superFertility;
	static Territory superTerritory;
	static Speeds superSpeed;
	static Flowering superFlowering;

	static Fertility noFertility;
	static Territory noTerritory;
	static Speeds noWork;
	static Flowering noFlowering;

	static Life superLife;
	static Life blinkLife;
	//static IAlleleTolerance anyTemperature; green
	//static IAlleleTolerance anyHumidity; gray

	static BeeBranch crystalBranch;
	static BeeBranch precursorBranch;
	static BeeBranch advancedCrystalBranch;

	static MultiAllele multiFlower;
	static PolychromaEffect multiEffect;
	static ChromaEffect chromaEffect;
	static RechargeEffect rechargeEffect;

	static SparklifyEffect sparkleEffect;

	static MetaAlloyAllele metaFlower;
	static ArtefactEffect uaEffect;
	static PrecursorEffect precursorEffect;

	private static ColorBlendList chromaColor;
	private static ColorBlendList auraColor;
	private static ColorBlendList lumenColor;
	private static ColorBlendList sparkleColor;
	private static ColorBlendList[] crystalColors;
	private static ColorBlendList multiColor;

	private static ColorBlendList towerColor;
	private static ColorBlendList uaColor;
	private static ColorBlendList precursorColor;

	static final IErrorState conditionalsUnavailable = new IErrorState() {

		private IIcon icon;

		@Override
		public short getID() {
			return 1600;
		}

		@Override
		public String getUniqueName() {
			return "ChromatiCraft:noconditionals";
		}

		@Override
		public String getDescription() {
			return "Conditionals Unavailable";
		}

		@Override
		public String getHelp() {
			return "Specialized products are unavailable due to their conditions not being met.";
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void registerIcons(IIconRegister register) {
			icon = register.registerIcon("chromaticraft:forestry/no-conditionals");
		}

		@Override
		@SideOnly(Side.CLIENT)
		public IIcon getIcon() {
			return icon;
		}

	};

	static final EnumMap<CrystalElement, CrystalBee> beeMap = new EnumMap(CrystalElement.class);
	static final EnumMap<CrystalElement, CrystalEffect> effectMap = new EnumMap(CrystalElement.class);
	static final EnumMap<CrystalElement, CrystalAllele> flowerMap = new EnumMap(CrystalElement.class);
	static final EnumMap<CrystalElement, ItemHashMap<ProductCondition>> productConditions = new EnumMap(CrystalElement.class);
	static final Collection<ConditionalProductBee> conditionalBees = new ArrayList();
	private static final ArrayList<BeeSpecies> basicBees = new ArrayList();
	private static final ArrayList<BeeSpecies> advancedBees = new ArrayList();

	static {
		ModularLogger.instance.addLogger(ChromatiCraft.instance, LOGGER_TAG);

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			loadColorData();
		}
	}

	@SideOnly(Side.CLIENT)
	private static void loadColorData() {
		chromaColor = new ColorBlendList(5F, ChromaFX.getChromaColorTiles());
		auraColor = new ColorBlendList(18F, 0xffff00, 0xffffff, 0x000000, 0x8000ff, 0xff0000);
		lumenColor = new ColorBlendList(10F, 0x0000ff, 0xffffff, 0x22aaff);
		crystalColors = new ColorBlendList[16];
		multiColor = new ColorBlendList(20F);

		sparkleColor = new ColorBlendList(6F, 0xffffff, 0xCEB2FF, 0xffffff, 0xB2CEFF);

		uaColor = new ColorBlendList(10F, 0x7A8499, 0x7A4355, 0x7A8499, 0x7A8499, 0x7A8499, 0x7A8499, 0x7C6275, 0x7A4355, 0xBC542A, 0xFFFF7F, 0xBD7F3A, 0x7A8499, 0x7A8499, 0x7A4355, 0xBC542A, 0xFFFF7F, 0xFFFF00, 0xB6FF00, 0xF1FF00, 0xB6FF00, 0xECFF00, 0xAAEF00, 0x7FA04A, 0x7A8499);
		precursorColor = new ColorBlendList(25F, 0xffffff, 0x404040, 0x22aaff, 0x404040, 0xDA8CFF, 0x404040, 0xFFF1AD, 0x404040);
		towerColor = new ColorBlendList(15F, 0x6DE1FF, 0x6DE1FF, 0xffffff, 0x6DE188, 0x6DE188, 0xffffff);

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			int c = e.getColor();
			int c1 = ReikaColorAPI.mixColors(c, 0x000000, 0.5F);
			int c2 = ReikaColorAPI.mixColors(c, 0xffffff, 0.5F);
			crystalColors[i] = new ColorBlendList(40F, c, c, c, c1, c, c, c, c2);
			multiColor.addColor(c);
		}
	}

	public static void register() {
		superFertility = Fertility.createNew("multiply", 8, false);
		superSpeed = Speeds.createNew("accelerated", 4F, false);
		superFlowering = Flowering.createNew("naturalistic", 240, false);
		superTerritory = Territory.createNew("exploratory", 32, 16, false);

		noFertility = Fertility.createNew("sterile", 0, false);
		noWork = Speeds.createNew("unproductive", 0, false);
		noFlowering = Flowering.createNew("nonpollinating", 0, false);
		noTerritory = Territory.createNew("lethargic", 1, 1, false);

		superLife = Life.createNew("eon", 600, false);
		blinkLife = Life.createNew("blink", 2, false);
		//anyTemperature = Tolerance.createNew("", new OmniToleranceCheck(), false);
		//anyHumidity = Tolerance.createNew("", new OmniToleranceCheck(), false);

		crystalBranch = new BeeBranch("branch.cccrystal", "Crystal", "Vitreus", "These bees can sense and sometimes field the crystal elements.");
		advancedCrystalBranch = new BeeBranch("branch.ccadvcrystal", "PolyCrystal", "Vitreus Corona", "These bees are masters of elemental manipulation and harnessing.");
		precursorBranch = new BeeBranch("branch.ccprecursor", "Forerunner", "Praeministri", PrecursorBee.description);

		feedstock = new RawBee("Raw", "Imperitus Materia", 0x888888);
		feedstock.register();

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement color = CrystalElement.elements[i];
			BeeTraits traits = CrystalBeeTypes.list[i].getTraits();
			CrystalBee bee = new CrystalBee(color, traits);
			CrystalEffect eff = new CrystalEffect(color);
			CrystalAllele flw = new CrystalAllele(color);
			effectMap.put(color, eff);
			flowerMap.put(color, flw);
			bee.register();
			beeMap.put(color, bee);
		}

		multiFlower = new MultiAllele();
		multiEffect = new PolychromaEffect();
		rechargeEffect = new RechargeEffect();
		chromaEffect = new ChromaEffect();

		sparkleEffect = new SparklifyEffect();

		metaFlower = new MetaAlloyAllele();
		uaEffect = new ArtefactEffect();
		precursorEffect = new PrecursorEffect();

		ForestryAPI.errorStateRegistry.registerErrorState(conditionalsUnavailable);

		crystal = new BasicBee("Crystalline", "Vitreus Crystallum", Speeds.NORMAL, Life.SHORTEST, Flowering.SLOWEST, Fertility.LOW, Territory.DEFAULT, 0x46A7FF, crystalBranch);
		purity = new BasicBee("Pure", "Purus Mundi", Speeds.SLOWER, Life.NORMAL, Flowering.AVERAGE, Fertility.NORMAL, Territory.DEFAULT, 0xffffff, crystalBranch);
		//crystal.setCave();
		//purity.setCave();

		protective = new BasicBee("Protective", "Vitreus Auxilium", Speeds.SLOWER, Life.ELONGATED, Flowering.SLOWER, Fertility.NORMAL, Territory.DEFAULT, 0xFF5993, crystalBranch);
		luminous = new BasicBee("Luminous", "Vitreus Lumens", Speeds.SLOW, Life.SHORTER, Flowering.SLOWER, Fertility.HIGH, Territory.DEFAULT, 0xBAEBFF, crystalBranch);
		hostile = new BasicBee("Hostile", "Vitreus Inimicus", Speeds.SLOWEST, Life.SHORT, Flowering.SLOW, Fertility.LOW, Territory.DEFAULT, 0xFF6A00, crystalBranch);
		magical = new BasicBee("Proximal", "Vitreus Proxima", Speeds.NORMAL, Life.NORMAL, Flowering.SLOW, Fertility.NORMAL, Territory.DEFAULT, 0xD53DFF, crystalBranch);

		sparkle = new SparkleBee("Shimmering", "Vitreus Nitidus", Speeds.SLOW, Life.SHORTENED, Flowering.AVERAGE, Fertility.NORMAL, Territory.DEFAULT, 0xCEB2FF, crystalBranch);

		tower = new PrecursorBee("Communicative", "Monumentum Vitreus Notitia", Speeds.SLOWER, Life.LONG, Flowering.SLOWEST, Fertility.LOW, Territory.LARGER, towerColor, EnumTemperature.NORMAL, ProgressStage.TOWER);

		crystal.register();
		purity.register();

		protective.register();
		luminous.register();
		hostile.register();
		magical.register();

		sparkle.register();

		tower.register();

		chroma = (AdvancedBee)new AdvancedBee("Iridescent", "Auram Stellans", Speeds.SLOWER, Life.NORMAL, Flowering.SLOWEST, Fertility.NORMAL, Territory.DEFAULT, chromaColor, EnumTemperature.COLD, ProgressStage.ALLOY).setEffect(chromaEffect);
		lumen = new AdvancedBee("Luminescent", "Auram Ardens", Speeds.NORMAL, Life.SHORTENED, Flowering.SLOWER, Fertility.NORMAL, Territory.DEFAULT, lumenColor, EnumTemperature.NORMAL, ProgressStage.DIMENSION);
		aura = (AdvancedBee)new AdvancedBee("Radiant", "Auram Pharus", Speeds.SLOW, Life.LONG, Flowering.SLOW, Fertility.NORMAL, Territory.DEFAULT, auraColor, EnumTemperature.ICY, ProgressStage.CTM).setEffect(rechargeEffect);
		multi = (AdvancedBee)new AdvancedBee("Polychromatic", "Pigmentum Pluralis", Speeds.SLOWEST, Life.ELONGATED, Flowering.AVERAGE, Fertility.LOW, Territory.DEFAULT, multiColor, EnumTemperature.WARM, ProgressStage.CTM).setEffect(multiEffect);

		precursor = (PrecursorBee)new PrecursorBee("Forerunner", "Populus Antecessoris", Speeds.NORMAL, Life.NORMAL, Flowering.AVERAGE, Fertility.LOW, Territory.LARGE, precursorColor, EnumTemperature.NORMAL, ProgressStage.TOWER).setEffect(precursorEffect);
		ua = (PrecursorBee)new PrecursorBee("Monumental", "Peritia Incognita", Speeds.SLOWER, Life.SHORTEST, Flowering.SLOWER, Fertility.LOW, Territory.DEFAULT, uaColor, EnumTemperature.COLD, ProgressStage.ARTEFACT).setEffect(uaEffect);

		chroma.register();
		lumen.register();
		aura.register();
		multi.register();

		precursor.register();
		ua.register();

		addBreeding(CrystalElement.RED, CrystalElement.YELLOW, CrystalElement.ORANGE);
		addBreeding(CrystalElement.WHITE, CrystalElement.GREEN, CrystalElement.LIME);
		addBreeding(CrystalElement.RED, CrystalElement.WHITE, CrystalElement.PINK);
		addBreeding(CrystalElement.RED, CrystalElement.BLUE, CrystalElement.PURPLE);
		addBreeding(CrystalElement.WHITE, CrystalElement.BLACK, CrystalElement.GRAY);
		addBreeding(CrystalElement.BLUE, CrystalElement.GREEN, CrystalElement.CYAN);
		addBreeding(CrystalElement.BLUE, CrystalElement.WHITE, CrystalElement.LIGHTBLUE);
		addBreeding(CrystalElement.WHITE, CrystalElement.GRAY, CrystalElement.LIGHTGRAY);
		addBreeding(CrystalElement.PINK, CrystalElement.PURPLE, CrystalElement.MAGENTA);

		addBreeding(purity, crystal, CrystalElement.WHITE);
		addBreeding(magical, crystal, CrystalElement.BLACK);
		addBreeding(protective, crystal, CrystalElement.RED);
		addBreeding(luminous, crystal, CrystalElement.BLUE);

		addBreeding("Rural", crystal, CrystalElement.BROWN);
		addBreeding("Industrious", crystal, CrystalElement.YELLOW);
		addBreeding("Tropical", hostile, CrystalElement.GREEN);

		protective.addBreeding("Heroic", crystal, 10);
		hostile.addBreeding("Demonic", crystal, 10);
		luminous.addBreeding("Ended", purity, 5);

		if (ModList.MAGICBEES.isLoaded()) {
			sparkle.addBreeding("Transmuting", ModList.MAGICBEES, luminous, 8);
		}
		else {
			sparkle.addBreeding("Exotic", ModList.FORESTRY, luminous, 8);
		}

		if (ModList.MAGICBEES.isLoaded()) {
			magical.addBreeding("Imperial", ModList.FORESTRY, "Arcane", ModList.MAGICBEES, 4);
			ua.addBreeding("Savant", ModList.MAGICBEES, tower, 3);
		}
		else {
			magical.addBreeding("Imperial", "Wintry", 4);
			ua.addBreeding(purity, tower, 2);
		}

		precursor.addBreeding(magical, tower, 3);

		chroma.addBreeding(beeMap.get(CrystalElement.PURPLE), beeMap.get(CrystalElement.WHITE), 5);
		lumen.addBreeding(beeMap.get(CrystalElement.BLUE), beeMap.get(CrystalElement.BLACK), 5);
		aura.addBreeding(lumen, beeMap.get(CrystalElement.YELLOW), 3);
		multi.addBreeding(aura, chroma, 2);

		protective.addSpecialty(new ItemStack(Blocks.obsidian), 2);
		hostile.addSpecialty(new ItemStack(Items.gunpowder), 4);
		luminous.addSpecialty(new ItemStack(Items.glowstone_dust), 5);
		magical.addSpecialty(ChromaStacks.chromaDust, 5);
		protective.addProduct(Combs.HONEY.getItem(), 10);
		hostile.addProduct(Combs.HONEY.getItem(), 10);
		luminous.addProduct(Combs.HONEY.getItem(), 10);
		sparkle.addProduct(Combs.HONEY.getItem(), 10);
		magical.addProduct(Combs.HONEY.getItem(), 10);
		crystal.addSpecialty(ChromaStacks.crystalPowder, 5);
		purity.addSpecialty(new ItemStack(Items.ghast_tear), 1);
		sparkle.addSpecialty(new ItemStack(Items.glowstone_dust), 7);
		sparkle.addSpecialty(new ItemStack(Items.gold_nugget), 2);

		chroma.addSpecialty(ChromaStacks.iridCrystal, 2);
		chroma.otherChecks.add(new IridescentShardCheck());
		aura.addSpecialty(ChromaStacks.lumaDust, 2);
		lumen.otherChecks.add(new AreaBlockCheck(new BlockKey(ChromaBlocks.POWERTREE.getBlockInstance()), 2, 1));
		lumen.addProduct(new ItemStack(Items.glowstone_dust), 15);
		lumen.addSpecialty(ChromaStacks.glowbeans, 4);
		aura.otherChecks.add(new AuraLocusCheck());

		if (ModList.MAGICBEES.isLoaded()) {
			tower.addProduct(MagicBeesHandler.Combs.MEMORY.getItem(), 4);
			tower.addProduct(MagicBeesHandler.Combs.FORGOTTEN.getItem(), 4);
			precursor.addProduct(MagicBeesHandler.Combs.MEMORY.getItem(), 10);
			precursor.addProduct(MagicBeesHandler.Combs.FORGOTTEN.getItem(), 10);
		}
		else {
			tower.addProduct(Combs.HONEY.getItem(), 8);
			tower.addProduct(ChromaStacks.crystalPowder, 4);
			precursor.addProduct(Combs.HONEY.getItem(), 8);
			precursor.addProduct(Combs.MELLOW.getItem(), 4);
		}

		ua.addProduct(ChromaStacks.crystalPowder, 10);

		tower.addSpecialty(new ItemStack(ChromaBlocks.METAALLOYLAMP.getBlockInstance()), 1);
		ua.addSpecialty(ChromaStacks.unknownFragments, 0.25F);
		for (int i = 0; i < DimDecoTypes.list.length; i++) {
			precursor.addSpecialty(ChromaItems.DIMGEN.getStackOfMetadata(i), 0.5F);
			if (DimDecoTypes.list[i].canSilkTouch())
				;//precursor.addSpecialty(ChromaBlocks.DIMGEN.getStackOfMetadata(i), 0.001F);
		}

		precursor.otherChecks.add(new ProgressionCheck(ProgressStage.DIMENSION));

		RainbowTreeCheck tree = new RainbowTreeCheck();
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			ItemStack shard = ChromaStacks.getChargedShard(e);
			multi.addSpecialty(shard, 4);
			ItemStack is = ChromaItems.BERRY.getStackOfMetadata(i);
			multi.addSpecialty(is, 20/16F); // /16 since one of each
			FlowerProviderMulti.conditions.put(is, tree);
			FlowerProviderMulti.conditions.put(shard, new ChargedShardCheck(e));
		}

		chroma.initConditions();
		lumen.initConditions();
		aura.initConditions();
		multi.initConditions();

		tower.initConditions();
		precursor.initConditions();
		ua.initConditions();

		GameRegistry.registerWorldGenerator(HiveGenerator.instance, -5);
	}

	public static BeeSpecies getElementalBee(CrystalElement e) {
		return beeMap.get(e);
	}

	public static BeeSpecies getCrystalBee() {
		return crystal;
	}

	public static BeeSpecies getPureBee() {
		return purity;
	}

	public static BeeSpecies getSparkleBee() {
		return sparkle;
	}

	public static BeeSpecies getLuminousBee() {
		return luminous;
	}

	public static BeeSpecies getProximalBee() {
		return magical;
	}

	public static BeeSpecies getTowerBee() {
		return tower;
	}

	public static BeeSpecies getUABee() {
		return ua;
	}

	public static BeeSpecies getPrecursorBee() {
		return precursor;
	}

	public static BeeSpecies getRainbowBee() {
		return multi;
	}

	public static int beeCount() {
		return 16+basicBees.size()+advancedBees.size();
	}

	public static BeeSpecies getBeeByIndex(int idx) {
		if (idx < basicBees.size()) {
			return basicBees.get(idx);
		}
		idx -= basicBees.size();
		if (idx < 16) {
			return beeMap.get(CrystalElement.elements[idx]);
		}
		idx -= 16;
		if (idx < advancedBees.size()) {
			return advancedBees.get(idx);
		}
		return null;
	}

	public static ArrayList<String> getBeeDescription(BeeSpecies b) {
		ArrayList<String> ret = new ArrayList();
		ret.add(b.getName());
		ret.add("");
		if (b instanceof CrystalBee) {
			CrystalBee cb = (CrystalBee)b;
			ret.add("This bee has one extremely powerful gene.");
		}
		if (b.getClass() == BasicBee.class) {
			ret.add("This bee is a breeding intermediate, not");
			ret.add("particularly valuable on its own.");
			ret.add("");
			ret.add("It may, however, be the first step towards");
			ret.add("something that is.");
		}
		if (b instanceof ConditionalProductBee) {
			ConditionalProductProvider p = ((ConditionalProductBee)b).getProductProvider();
			ret.add("Special Products:");
			ret.add("");
			ret.add("General Conditions:");
			ArrayList<String> li = p.getGeneralRequirements();
			for (String s : li) {
				ret.add(s);
			}
			ItemHashMap<ProductCondition> map = p.getConditions();
			if (!map.isEmpty()) {
				ret.add("");
				ret.add("Specific Conditions:");
				for (ItemStack is : map.keySet()) {
					ProductCondition c = map.get(is);
					String d = c.getDescription();
					ArrayList<String> dl = ReikaStringParser.splitStringByNewlines(d);
					if (dl.size() > 1) {
						ret.add(is.getDisplayName()+":");
						for (String in : dl) {
							ret.add(in);
						}
						ret.add("");
					}
					else {
						ret.add(is.getDisplayName()+": "+d);
					}
				}
			}
		}
		return ret;
	}

	public static Collection<BeeSpecies> getBasicBees() {
		return Collections.unmodifiableCollection(basicBees);
	}

	public static Collection<BeeSpecies> getAdvancedBees() {
		return Collections.unmodifiableCollection(advancedBees);
	}

	public static Collection<ConditionalProductBee> getConditionalBees() {
		return Collections.unmodifiableCollection(conditionalBees);
	}

	private static final void addBreeding(String in1, BeeSpecies in2, CrystalElement out) {
		CrystalBee cb = beeMap.get(out);
		cb.addBreeding(in1, in2, 8);
	}

	private static final void addBreeding(BeeSpecies in1, BeeSpecies in2, CrystalElement out) {
		CrystalBee cb = beeMap.get(out);
		cb.addBreeding(in1, in2, 8);
	}

	private static final void addBreeding(CrystalElement in1, CrystalElement in2, CrystalElement out) {
		CrystalBee p1 = beeMap.get(in1);
		CrystalBee p2 = beeMap.get(in2);
		CrystalBee cb = beeMap.get(out);
		cb.addBreeding(p1, p2, 8);
	}

	public static Flowering getNoFlowering() {
		return noFlowering;
	}

	public static Flowering getSuperFlowering() {
		return superFlowering;
	}

	public static Fertility getSuperFertility() {
		return superFertility;
	}

	public static Life getSuperLife() {
		return superLife;
	}

	static class PrecursorBee extends AdvancedBee {

		private static final String description = "Something feels strange about these bees, almost as if they are a link to the past, or some long-lost knowledge.";

		private PrecursorBee(String name, String latin, Speeds s, Life l, Flowering f, Fertility f2, Territory a, ColorBlendList c, EnumTemperature t, ProgressStage p) {
			super(name, latin, s, l, f, f2, a, c, t, p, precursorBranch);
		}

		private PrecursorBee(String name, String latin, Speeds s, Life l, Flowering f, Fertility f2, Territory a, int c, EnumTemperature t, ProgressStage p) {
			this(name, latin, s, l, f, f2, a, new ColorBlendList(1, c), t, p);
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public IAlleleFlowers getFlowerAllele() {
			return metaFlower;
		}

		@Override
		public String getIconMod() {
			return "chromaticraft";
		}

		@Override
		public String getIconCategory() {
			return "precursor";
		}

		@Override
		protected String getIconFolderRoot() {
			return "forestry/bees";
		}

		@Override
		public boolean simplifiedIconSystem() {
			return true;
		}

		@Override
		public int getBeeStripeColor() {
			return ReikaColorAPI.mixColors(this.getOutlineColor(), 0xffffff, 0.5F);
		}
	}

	static class AdvancedBee extends BasicBee implements ConditionalProductBee {

		private final ColorBlendList colorList;
		private final ProgressionCheck progress;
		protected final Collection<ProductCondition> otherChecks = new ArrayList();
		private final CompoundConditionalProductProvider manager = new CompoundConditionalProductProvider();

		protected AdvancedBee(String name, String latin, Speeds s, Life l, Flowering f, Fertility f2, Territory a, ColorBlendList c, EnumTemperature t, ProgressStage p) {
			this(name, latin, s, l, f, f2, a, c, t, p, advancedCrystalBranch);
		}

		protected AdvancedBee(String name, String latin, Speeds s, Life l, Flowering f, Fertility f2, Territory a, ColorBlendList c, EnumTemperature t, ProgressStage p, BeeBranch branch) {
			super(name, latin, s, l, f, f2, a, 0xffffff, t, branch);
			colorList = c;
			progress = new ProgressionCheck(p);
			conditionalBees.add(this);
			advancedBees.add(this);
		}

		protected final void initConditions() {
			if (this == multi)
				manager.add((FlowerProviderMulti)multiFlower.getProvider());
			manager.addGeneral(progress);
			for (ProductCondition p : otherChecks)
				manager.addGeneral(p);
		}

		@Override
		public String getDescription() {
			return "Hybridized from the crystal bees, these bees are mysterious and have unknown but surely significant beneficial effects.";
		}

		@Override
		public boolean isJubilant(IBeeGenome ibg, IBeeHousing ibh) {
			if (!super.isJubilant(ibg, ibh))
				return false;
			World world = ibh.getWorld();
			int x = ibh.getCoordinates().posX;
			int y = ibh.getCoordinates().posY;
			int z = ibh.getCoordinates().posZ;
			for (ProductCondition p : otherChecks) {
				if (!p.check(world, x, y, z, ibg, ibh))
					return false;
			}
			return progress.check(world, x, y, z, ibg, ibh);
		}

		@Override
		public boolean hasEffect() {
			return true;
		}

		@Override
		public IAlleleFlowers getFlowerAllele() {
			return this == multi ? multiFlower : super.getFlowerAllele();
		}

		@Override
		public int getOutlineColor() {
			return colorList != null ? colorList.getColor(DragonAPICore.getSystemTimeAsInt()/30D) : 0xffffff;
		}

		@Override
		public ConditionalProductProvider getProductProvider() {
			return manager;
		}

		@Override
		protected String getIconMod() {
			return "chromaticraft";
		}

		@Override
		protected String getIconCategory() {
			return "crystal";
		}

		@Override
		protected String getIconFolderRoot() {
			return "forestry/bees";
		}

		@Override
		protected boolean simplifiedIconSystem() {
			return true;
		}

		@Override
		public int getBeeStripeColor() {
			return ReikaColorAPI.mixColors(this.getOutlineColor(), 0xffffff, 0.375F);
		}

	}

	static class RawBee extends BasicBee {

		private RawBee(String name, String latin, int color) {
			super(name, latin, noWork, Life.NORMAL, noFlowering, noFertility, noTerritory, color, crystalBranch);
		}

		@Override
		public boolean isJubilant(IBeeGenome ibg, IBeeHousing ibh) {
			return false;
		}

		@Override
		public String getDescription() {
			return "With no traits or utility of their own, completely worthless as anything but raw feedstock.";
		}
	}

	static class BasicBee extends TraitsBee {

		public final int outline;
		private IAlleleBeeEffect effect;

		private BasicBee(String name, String latin, Speeds s, Life l, Flowering f, Fertility f2, Territory a, int color, BeeBranch branch) {
			this(name, latin, s, l, f, f2, a, color, EnumTemperature.NORMAL, branch);
		}

		private BasicBee(String name, String latin, Speeds s, Life l, Flowering f, Fertility f2, Territory a, int color, EnumTemperature t, BeeBranch branch) {
			super(name, "bee."+name.toLowerCase(Locale.ENGLISH), latin, "Reika", branch, new BeeTraits());
			traits.speed = s;
			traits.lifespan = l;
			traits.flowering = f;
			traits.fertility = f2;
			traits.area = a;
			traits.tempDir = Tolerance.NONE;
			traits.humidDir = Tolerance.NONE;
			traits.tempTol = 0;
			traits.humidTol = 0;
			traits.temperature = t;
			traits.humidity = EnumHumidity.NORMAL;
			traits.temperature = t;
			outline = color;

			if (this.getClass() == BasicBee.class)
				basicBees.add(this);
		}

		protected BasicBee setEffect(IAlleleBeeEffect eff) {
			effect = eff;
			return this;
		}
		/*
		private BasicBee setCave() {
			traits.isCaveDwelling = true;
			//traits.isNocturnal = true;
			return this;
		}
		 */
		@Override
		public boolean isJubilant(IBeeGenome ibg, IBeeHousing ibh) {
			return ReikaBeeHelper.isDefaultJubilance(this, ibg, ibh);
		}

		@Override
		public String getDescription() {
			return "These bees do little on their own, but perhaps they could be purified into something stronger.";
		}

		@Override
		public boolean hasEffect() {
			return false;
		}

		@Override
		public boolean isSecret() {
			return false;
		}

		@Override
		public boolean isCounted() {
			return false;
		}

		@Override
		public boolean isDominant() {
			return true;
		}

		@Override
		public IAlleleFlowers getFlowerAllele() {
			return Flower.VANILLA.getAllele();
		}

		@Override
		public IAlleleBeeEffect getEffectAllele() {
			return effect != null ? effect : Effect.NONE.getAllele();
		}

		@Override
		public int getOutlineColor() {
			return outline;
		}

	}

	static class SparkleBee extends BasicBee {

		private SparkleBee(String name, String latin, Speeds s, Life l, Flowering f, Fertility f2, Territory a, int color, BeeBranch branch) {
			super(name, latin, s, l, f, f2, a, color, branch);
			this.setEffect(sparkleEffect);
		}

		@Override
		public boolean isJubilant(IBeeGenome ibg, IBeeHousing ibh) {
			return super.isJubilant(ibg, ibh) && ChromatiCraft.isRainbowForest(ibh.getBiome());
		}

		@Override
		public String getDescription() {
			return "This bee makes its surroundings glitter like itself.";
		}

		@Override
		public boolean isDominant() {
			return false;
		}

		@Override
		public int getOutlineColor() {
			return sparkleColor != null ? sparkleColor.getColor(DragonAPICore.getSystemTimeAsInt()/30D) : 0xffffff;
		}

		@Override
		public IAlleleBeeEffect getEffectAllele() {
			return sparkleEffect;
		}

	}

	static final class CrystalBee extends BeeSpecies implements ConditionalProductBee {

		public final CrystalElement color;
		public final Speeds speed;
		public final Fertility fertility;
		public final Flowering flowering;
		public final Life lifespan;
		public final Territory area;
		public final Tolerance tempDir;
		public final Tolerance humidDir;
		public final int tempTol;
		public final int humidTol;
		public final EnumTemperature temperature;
		public final EnumHumidity humidity;

		public CrystalBee(CrystalElement color, BeeTraits traits) {
			super(color.displayName+" Crystal", "bee.crystal."+color.name().toLowerCase(Locale.ENGLISH), "Vitreus "+color.displayName, "Reika", crystalBranch);
			this.color = color;
			speed = traits.speed;
			fertility = traits.fertility;
			flowering = traits.flowering;
			lifespan = traits.lifespan;
			area = traits.area;
			tempDir = traits.tempDir;
			humidDir = traits.humidDir;
			tempTol = traits.tempTol;
			humidTol = traits.humidTol;
			temperature = traits.temperature;
			humidity = traits.humidity;

			this.addConditionalProduct(ChromaItems.BERRY.getStackOf(color), 25, true, new LeafCheck(color));
			this.addConditionalProduct(ItemColoredModInteract.ColoredModItems.COMB.getItem(color), 8, true, new CrystalPlantCheck(color));
			this.addProduct(ForestryHandler.Combs.HONEY.getItem(), 15);
			this.addConditionalProduct(ChromaOptions.isVanillaDyeMoreCommon(color) ? new ItemStack(Items.dye, 1, color.ordinal()) : ChromaItems.DYE.getStackOf(color), 20, false, new FlowerCheck(color));
			switch(color) {
				case BLACK:
					this.addConditionalProduct(ChromaStacks.auraDust, 5, true, new ProgressionCheck(TieredPlants.FLOWER.level));
					break;
				case RED:
					this.addSpecialty(ChromaStacks.etherBerries, 5);
					break;
				case GREEN:
					this.addSpecialty(ForestryHandler.Combs.SILKY.getItem(), 10);
					this.addSpecialty(ChromaStacks.livingEssence, 5);
					break;
				case PURPLE:
					if (ModList.TINKERER.isLoaded())
						this.addSpecialty(BerryTypes.XP.getStack(), 3);
					this.addSpecialty(ChromaStacks.voidDust, 10);
					break;
				case BROWN: {
					ArrayList<ItemStack> li = OreDictionary.getOres("nuggetIron");
					if (!li.isEmpty())
						this.addSpecialty(li.get(0), 5);
					break;
				}
				case ORANGE:
					this.addSpecialty(new ItemStack(Items.blaze_powder), 10);
					this.addConditionalProduct(ChromaStacks.fireEssence, 5, true, new ProgressionCheck(TieredOres.FIRESTONE.level));
					break;
				case BLUE:
					this.addConditionalProduct(ChromaStacks.beaconDust, 5, true, new ProgressionCheck(TieredPlants.DESERT.level));
					break;
				case YELLOW:
					this.addSpecialty(ChromaStacks.energyPowder, 5);
					break;
				case WHITE:
					this.addConditionalProduct(ChromaStacks.purityDust, 5, true, new ProgressionCheck(TieredPlants.CAVE.level));
					break;
				case CYAN:
					this.addConditionalProduct(ChromaStacks.waterDust, 5, true, new ProgressionCheck(TieredOres.WATERY.level));
					break;
				case LIME:
					this.addConditionalProduct(ChromaStacks.spaceDust, 5, true, new ProgressionCheck(TieredOres.SPACERIFT.level));
					break;
				case GRAY:
					this.addSpecialty(ChromaStacks.teleDust, 5);
					break;
				case LIGHTGRAY:
					this.addSpecialty(ChromaStacks.icyDust, 5);
					break;
				case MAGENTA:
					ItemStack is = ReikaBeeHelper.getBeeItem(feedstock.getUID(), EnumBeeType.PRINCESS);
					this.addSpecialty(is, 1);
				default:
					break;
			}

			conditionalBees.add(this);
		}

		private void addConditionalProduct(ItemStack is, int chance, boolean specialty, ProductCondition c) {
			if (specialty)
				this.addSpecialty(is, chance);
			else
				this.addProduct(is, chance);
			ItemHashMap<ProductCondition> map = productConditions.get(color);
			if (map == null) {
				map = new ItemHashMap();
				productConditions.put(color, map);
			}
			map.put(is, c);
		}

		@Override
		public boolean isNocturnal() {
			return color == CrystalElement.BLUE;
		}

		@Override
		public boolean isJubilant(IBeeGenome ibg, IBeeHousing ibh) {
			World world = ibh.getWorld();
			int x = ibh.getCoordinates().posX;
			int y = ibh.getCoordinates().posY;
			int z = ibh.getCoordinates().posZ;
			if (!(ibg.getFlowerProvider() instanceof FlowerProviderCrystal))
				return false;
			if (((FlowerProviderCrystal)ibg.getFlowerProvider()).color != color)
				return false;
			if (rand.nextFloat() > ibg.getSpeed())
				return false;
			if (ibg.getFlowering() < Flowering.AVERAGE.getAllele().getValue())
				return false;
			if (!ChromatiCraft.isRainbowForest(world.getBiomeGenForCoords(x, z))) {
				if (rand.nextInt(2) > 0) {
					return false;
				}
			}
			if (!ReikaMathLibrary.isValueInsideBoundsIncl(8, 32, ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z)))
				return false;

			return rand.nextInt(3) > 0 || ChromaBeeHelpers.checkProgression(world, ibh, ProgressStage.SHARDCHARGE);
		}

		@Override
		public String getDescription() {
			return "These bees seem to enjoy the magic aura of the cave crystals. So much so, in fact, that they will only thrive around their corresponding color.";
		}

		@Override
		public EnumTemperature getTemperature() {
			return temperature;
		}

		@Override
		public EnumHumidity getHumidity() {
			return humidity;
		}

		@Override
		public boolean hasEffect() {
			return true;
		}

		@Override
		public boolean isSecret() {
			return false;
		}

		@Override
		public boolean isCounted() {
			return false;
		}

		@Override
		public boolean isDominant() {
			return true;
		}

		@Override
		public IAlleleFlowers getFlowerAllele() {
			return flowerMap.get(color);
		}

		@Override
		public Speeds getProductionSpeed() {
			return speed;
		}

		@Override
		public Fertility getFertility() {
			return fertility;
		}

		@Override
		public Flowering getFloweringRate() {
			return flowering;
		}

		@Override
		public Life getLifespan() {
			return lifespan;
		}

		@Override
		public Territory getTerritorySize() {
			return area;
		}

		@Override
		public boolean isCaveDwelling() {
			return true;
		}

		@Override
		public int getTemperatureTolerance() {
			return tempTol;
		}

		@Override
		public int getHumidityTolerance() {
			return humidTol;
		}

		@Override
		public Tolerance getHumidityToleranceDir() {
			return humidDir;
		}

		@Override
		public Tolerance getTemperatureToleranceDir() {
			return tempDir;
		}

		@Override
		public boolean isTolerantFlyer() {
			return color == CrystalElement.CYAN;
		}

		@Override
		public int getOutlineColor() {
			return crystalColors != null ? crystalColors[color.ordinal()].getColor(DragonAPICore.getSystemTimeAsInt()/5D-color.ordinal()*32) : color.getColor(); //because MagicBees is a dumb and calls this from server side
		}

		@Override
		public IAlleleBeeEffect getEffectAllele() {
			return effectMap.get(color);
		}

		@Override
		public ConditionalProductProvider getProductProvider() {
			return (FlowerProviderCrystal)flowerMap.get(color).getProvider();
		}

		@Override
		protected String getIconMod() {
			return "chromaticraft";
		}

		@Override
		protected String getIconCategory() {
			return "crystal";
		}

		@Override
		protected String getIconFolderRoot() {
			return "forestry/bees";
		}

		@Override
		protected boolean simplifiedIconSystem() {
			return true;
		}

		@Override
		public int getBeeStripeColor() {
			return ReikaColorAPI.mixColors(color.getColor(), 0xffffff, 0.5F);
		}

	}

	public static void showConditionalStatuses(World world, int x, int y, int z, EntityPlayer ep, IBeeHousing ibh) {
		ItemStack is = ibh.getBeeInventory().getQueen();
		if (is != null) {
			IIndividual ii = AlleleManager.alleleRegistry.getIndividual(is);
			if (ii instanceof IBee) {
				IBeeGenome ibg = ((IBee)ii).getGenome();
				IAlleleBeeSpecies sp = ibg.getPrimary();
				if (sp instanceof ConditionalProductBee) {
					ReikaChatHelper.sendChatToPlayer(ep, "Product Conditions for "+EnumChatFormatting.GOLD+sp.getName()+EnumChatFormatting.RESET+" Bee:");
					ConditionalProductBee cb = (ConditionalProductBee)sp;
					ConditionalProductProvider cp = cb.getProductProvider();
					StatusLogger log = new StatusLogger();
					cp.sendStatusInfo(world, x, y, z, log, ibg, ibh);
					log.sendToPlayer(ep);
				}
			}
		}
	}

}

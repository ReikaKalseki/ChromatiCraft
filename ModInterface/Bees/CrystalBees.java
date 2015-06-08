/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.Bees;

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.ModInteract.Bees.BasicGene;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies.Fertility;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies.Flowering;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies.Life;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies.Speeds;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies.Territory;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies.TraitsBee;
import Reika.DragonAPI.ModInteract.Bees.BeeTraits;
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;

public class CrystalBees {

	protected static BasicBee protective;
	protected static BasicBee luminous;
	protected static BasicBee hostile;

	protected static BasicBee crystal;
	protected static BasicBee purity;

	protected static final HashMap<CrystalElement, CrystalBee> beeMap = new HashMap();
	protected static final HashMap<CrystalElement, CrystalEffect> effectMap = new HashMap();
	protected static final HashMap<CrystalElement, CrystalAllele> flowerMap = new HashMap();

	public static void register() {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement color = CrystalElement.elements[i];
			BeeTraits traits = CrystalBeeTypes.list[color.ordinal()].getTraits();
			CrystalBee bee = new CrystalBee(color, traits);
			CrystalEffect eff = new CrystalEffect(color);
			CrystalAllele flw = new CrystalAllele(color);
			effectMap.put(color, eff);
			flowerMap.put(color, flw);
			bee.register();
			beeMap.put(color, bee);
		}

		protective = new BasicBee("Protective", "Vitreus Auxilium", Speeds.SLOWER, Life.ELONGATED, Flowering.SLOWER, Fertility.NORMAL, Territory.DEFAULT, 0xFF5993);
		luminous = new BasicBee("Luminous", "Vitreus Lumens", Speeds.SLOW, Life.SHORTER, Flowering.SLOWER, Fertility.HIGH, Territory.DEFAULT, 0xBAEBFF);
		hostile = new BasicBee("Hostile", "Vitreus Inimicus", Speeds.SLOWEST, Life.SHORT, Flowering.SLOW, Fertility.LOW, Territory.DEFAULT, 0xFF6A00);

		crystal = new BasicBee("Crystalline", "Vitreus Crystallum", Speeds.NORMAL, Life.SHORTEST, Flowering.SLOWEST, Fertility.LOW, Territory.DEFAULT, 0x46A7FF);
		purity = new BasicBee("Pure", "Purus Mundi", Speeds.SLOWER, Life.NORMAL, Flowering.AVERAGE, Fertility.NORMAL, Territory.DEFAULT, 0xffffff);
		crystal.setCave();
		purity.setCave();

		protective.register();
		luminous.register();
		hostile.register();
		crystal.register();
		purity.register();

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
		addBreeding(protective, hostile, CrystalElement.BLACK);
		addBreeding(protective, crystal, CrystalElement.RED);
		addBreeding(luminous, crystal, CrystalElement.BLUE);

		addBreeding("Rural", crystal, CrystalElement.BROWN);
		addBreeding("Industrious", crystal, CrystalElement.YELLOW);
		addBreeding("Tropical", hostile, CrystalElement.GREEN);

		protective.addBreeding("Heroic", crystal, 10);
		hostile.addBreeding("Demonic", crystal, 10);
		luminous.addBreeding("Ended", purity, 5);

		GameRegistry.registerWorldGenerator(HiveGenerator.instance, -5);
	}

	protected static final CrystalBee getBeeFor(CrystalElement color) {
		return beeMap.get(color);
	}

	public static BeeSpecies getCrystalBee() {
		return crystal;
	}

	public static BeeSpecies getPureBee() {
		return purity;
	}

	public static BeeSpecies getElementalBee(CrystalElement e) {
		return beeMap.get(e);
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

	private static final class BasicBee extends TraitsBee {

		public final int outline;

		private BasicBee(String name, String latin, Speeds s, Life l, Flowering f, Fertility f2, Territory a, int color) {
			this(name, latin, s, l, f, f2, a, color, EnumTemperature.NORMAL);
		}

		private BasicBee(String name, String latin, Speeds s, Life l, Flowering f, Fertility f2, Territory a, int color, EnumTemperature t) {
			super(name, "bee."+name.toLowerCase(), latin, "Reika", new BeeTraits());
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
		}

		private BasicBee setCave() {
			traits.isCaveDwelling = true;
			traits.isNocturnal = true;
			return this;
		}

		@Override
		public boolean isJubilant(IBeeGenome ibg, IBeeHousing ibh) {
			return false;
		}

		@Override
		public String getDescription() {
			return "These bees do nothing on their own, but perhaps they could be purified into something stronger.";
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
		public IAllele getFlowerAllele() {
			return Flower.VANILLA.getAllele();
		}

		@Override
		public IAllele getEffectAllele() {
			return Effect.NONE.getAllele();
		}

		@Override
		public int getOutlineColor() {
			return outline;
		}

	}

	private static final class CrystalEffect extends BasicGene implements IAlleleBeeEffect {

		public final CrystalElement color;

		public CrystalEffect(CrystalElement color) {
			super("effect.cavecrystal."+color.name().toLowerCase(), color.displayName+" Crystal");
			this.color = color;
		}

		@Override
		public boolean isCombinable() {
			return false;
		}

		@Override
		public IEffectData validateStorage(IEffectData ied) {
			return null;
		}

		@Override
		public IEffectData doEffect(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			World world = ibh.getWorld();
			int x = ibh.getXCoord();
			int y = ibh.getYCoord();
			int z = ibh.getZCoord();
			int[] r = ibg.getTerritory();
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).expand(r[0], r[1], r[2]);
			List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
			for (EntityLivingBase e : li) {
				CrystalBlock.applyEffectFromColor(600, 0, e, color);
			}
			return null;
		}

		@Override
		public IEffectData doFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			return null;
		}

	}

	private static final class CrystalAllele extends BasicGene implements IAlleleFlowers {

		public final CrystalElement color;
		private final FlowerProviderCrystal provider;;

		public CrystalAllele(CrystalElement color) {
			super("flower.cavecrystal."+color.name().toLowerCase(), color.displayName+" Cave Crystal");
			this.color = color;
			provider = new FlowerProviderCrystal(color);
		}

		@Override
		public IFlowerProvider getProvider() {
			return provider;
		}
	}

	public static class FlowerProviderCrystal implements IFlowerProvider {

		public final CrystalElement color;

		public FlowerProviderCrystal(CrystalElement color) {
			this.color = color;
		}

		@Override
		public boolean isAcceptedFlower(World world, IIndividual individual, int x, int y, int z) {
			return world.getBlock(x, y, z) == ChromaBlocks.CRYSTAL.getBlockInstance() && world.getBlockMetadata(x, y, z) == color.ordinal();
		}

		@Override
		public boolean isAcceptedPollinatable(World world, IPollinatable ip) {
			return false;
		}

		@Override
		public boolean growFlower(World world, IIndividual individual, int x, int y, int z) {
			return false;
		}

		@Override
		public String getDescription() {
			return color.displayName+" Crystals";
		}

		@Override
		public ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products) {
			return products;
		}

		@Override
		public ItemStack[] getItemStacks() {
			return new ItemStack[]{new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, color.ordinal())};
		}

	}

	private static final class CrystalBee extends BeeSpecies {

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
			super(color.displayName+" Crystal", "bee.crystal."+color.name().toLowerCase(), "Vitreus "+color.displayName, "Reika");
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
			if (traits.speed == null)
				throw new RuntimeException("AFSDFSDF");
		}

		@Override
		public boolean isNocturnal() {
			return true;
		}

		@Override
		public boolean isJubilant(IBeeGenome ibg, IBeeHousing ibh) {
			return false;
		}

		@Override
		public String getDescription() {
			return "These bees seem to enjoy the magic aura of the cave crystals." +
					"So much so, in fact, that they will only thrive around their corresponding color.";
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
		public IAllele getFlowerAllele() {
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
		public int getOutlineColor() {
			return color.getJavaColor().getRGB();
		}

		@Override
		public IAllele getEffectAllele() {
			return effectMap.get(color);
		}

	}

}

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import Reika.ChromatiCraft.ModInterface.Bees.ProductChecks.ProductCondition;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.BeeGene;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Fertility;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Flowering;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Speeds;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;

import com.mojang.authlib.GameProfile;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IIndividual;


public class ChromaBeeHelpers {

	public static interface ConditionalProductProvider {

		public ItemHashMap<ProductCondition> getConditions();

		public ArrayList<String> getGeneralRequirements();

	}

	public static interface ConditionalProductBee {

		public ConditionalProductProvider getProductProvider();

	}

	static class CompoundConditionalProductProvider implements ConditionalProductProvider {

		private Collection<ConditionalProductProvider> list = new ArrayList();
		private Collection<ProductCondition> extras = new ArrayList();

		CompoundConditionalProductProvider() {

		}

		void add(ConditionalProductProvider p) {
			list.add(p);
		}

		void addGeneral(ProductCondition c) {
			extras.add(c);
		}

		@Override
		public ItemHashMap<ProductCondition> getConditions() {
			ItemHashMap<ProductCondition> map = new ItemHashMap();
			for (ConditionalProductProvider p : list) {
				map.putAll(p.getConditions());
			}
			return map;
		}

		@Override
		public ArrayList<String> getGeneralRequirements() {
			ArrayList<String> li = new ArrayList();
			for (ConditionalProductProvider p : list) {
				li.addAll(p.getGeneralRequirements());
			}
			for (ProductCondition c : extras) {
				li.add(c.getDescription());
			}
			return li;
		}

	}

	public static boolean isBestPossibleBee(IBeeGenome ibg) {
		if (!ibg.getNocturnal() || !ibg.getCaveDwelling() || !ibg.getTolerantFlyer())
			return false;
		if (ibg.getToleranceHumid() != EnumTolerance.BOTH_5 || ibg.getToleranceTemp() != EnumTolerance.BOTH_5)
			return false;
		if (ibg.getFertility() < CrystalBees.superFertility.getAllele().getValue())
			return false;
		if (ibg.getFlowering() < CrystalBees.superFlowering.getAllele().getValue())
			return false;
		if (ibg.getLifespan() < CrystalBees.superLife.getAllele().getValue())
			return false;
		if (ibg.getSpeed() < CrystalBees.superSpeed.getAllele().getValue())
			return false;
		return Arrays.equals(ibg.getTerritory(), CrystalBees.superTerritory.getAllele().getValue());
	}

	public static boolean isLightningBoostActive(IBeeGenome ibg, IBeeHousing ibh) {
		return ibg.getEffect() == CrystalBees.effectMap.get(CrystalElement.YELLOW) && ibh.canBlockSeeTheSky() && ibh.getBiome().canSpawnLightningBolt() && ibh.getWorld().getWorldInfo().isThundering();
	}

	abstract static class SpecialGeneticEffect {

		public final CrystalElement color;

		protected SpecialGeneticEffect(CrystalElement e) {
			color = e;
			CrystalBees.specialGenes.put(e, this);
		}

		public abstract void doEffect(IBeeGenome ibg, IBeeHousing ibh, Random rand);

	}

	static class LightningProductionEffect extends SpecialGeneticEffect {

		LightningProductionEffect() {
			super(CrystalElement.YELLOW);
		}

		@Override
		public void doEffect(IBeeGenome ibg, IBeeHousing ibh, Random rand) {
			if (isLightningBoostActive(ibg, ibh))
				ReikaBeeHelper.runProductionCycle(ibh);
		}

	}

	static class HistoryRewriteEffect extends SpecialGeneticEffect {

		private static final double mateRewriteChance = 0.01;

		HistoryRewriteEffect() {
			super(CrystalElement.LIGHTGRAY);
		}

		@Override
		public void doEffect(IBeeGenome ibg, IBeeHousing ibh, Random rand) {
			if (ReikaRandomHelper.doWithChance(mateRewriteChance)) {
				ItemStack queen = ibh.getBeeInventory().getQueen();
				if (queen != null) {
					IIndividual ii = AlleleManager.alleleRegistry.getIndividual(queen);
					if (ii instanceof IBee) {
						ReikaBeeHelper.setBeeMate((IBee)ii, (IBee)ii);
					}
				}
			}
		}

	}

	static class GeneticStabilityEffect extends SpecialGeneticEffect {

		private static final double pristineConversionChance = 0.0005;
		private static final double geneBalancingChance = 0.005;

		GeneticStabilityEffect() {
			super(CrystalElement.WHITE);
		}

		@Override
		public void doEffect(IBeeGenome ibg, IBeeHousing ibh, Random rand) {
			if (ReikaRandomHelper.doWithChance(pristineConversionChance)) {
				ItemStack queen = ibh.getBeeInventory().getQueen();
				if (queen != null && AlleleManager.alleleRegistry.getIndividual(queen) instanceof IBee) {
					ReikaBeeHelper.setPristine(queen, true);
				}
			}
			if (ReikaRandomHelper.doWithChance(geneBalancingChance)) {
				ItemStack queen = ibh.getBeeInventory().getQueen();
				if (queen != null && AlleleManager.alleleRegistry.getIndividual(queen) instanceof IBee) {
					EnumBeeChromosome gene = EnumBeeChromosome.values()[rand.nextInt(EnumBeeChromosome.values().length)];
					if (this.canBalance(queen, ibg, gene)) {
						this.balanceGene(queen, ibg, gene);
					}
				}
			}
		}

		private void balanceGene(ItemStack queen, IBeeGenome ibg, EnumBeeChromosome gene) {
			ReikaBeeHelper.setGene(queen, ibg, gene, ibg.getActiveAllele(gene), true);
		}

		private boolean canBalance(ItemStack queen, IBeeGenome ibg, EnumBeeChromosome gene) {
			if (gene == EnumBeeChromosome.HUMIDITY)
				return false;
			IAllele primary = ibg.getActiveAllele(gene);
			IAllele secondary = ibg.getInactiveAllele(gene);
			return !primary.getUID().equals(secondary.getUID());
		}

	}

	static class GeneticImprovementEffect extends SpecialGeneticEffect {

		private static final double geneImprovementChance = 0.002;

		GeneticImprovementEffect() {
			super(CrystalElement.BLACK);
		}

		@Override
		public void doEffect(IBeeGenome ibg, IBeeHousing ibh, Random rand) {
			if (ReikaRandomHelper.doWithChance(geneImprovementChance)) {
				ItemStack queen = ibh.getBeeInventory().getQueen();
				if (queen != null && AlleleManager.alleleRegistry.getIndividual(queen) instanceof IBee) {
					EnumBeeChromosome gene = EnumBeeChromosome.values()[rand.nextInt(EnumBeeChromosome.values().length)];
					if (this.canImprove(gene, ibg)) {
						this.improveGene(gene, ibg, queen);
					}
				}
			}
		}

		private void improveGene(EnumBeeChromosome gene, IBeeGenome ibg, ItemStack queen) {
			switch(gene) {
				case FERTILITY:
				case FLOWERING:
				case TERRITORY:
				case SPEED:
					BeeGene g = ReikaBeeHelper.getGeneEnum(gene, ibg);
					if (g == null)
						break;
					g = g.oneBetter();
					if (g == null)
						break;
					ReikaBeeHelper.setGene(queen, ibg, gene, g.getAllele(), true);
					break;
				case TEMPERATURE_TOLERANCE: {
					EnumTolerance next = ReikaBeeHelper.getOneBetterTolerance(ibg.getToleranceTemp());
					if (next == null)
						break;
					ReikaBeeHelper.setGene(queen, ibg, gene, ReikaBeeHelper.getToleranceGene(next), true);
					break;
				}
				case HUMIDITY_TOLERANCE: {
					EnumTolerance next = ReikaBeeHelper.getOneBetterTolerance(ibg.getToleranceHumid());
					if (next == null)
						break;
					ReikaBeeHelper.setGene(queen, ibg, gene, ReikaBeeHelper.getToleranceGene(next), true);
					break;
				}
				default:
					break;
			}
		}

		private boolean canImprove(EnumBeeChromosome gene, IBeeGenome ibg) {
			switch(gene) {
				case FERTILITY:
					return ibg.getFertility() < Fertility.MAXIMUM.getAllele().getValue();
				case FLOWERING:
					return ibg.getFlowering() < Flowering.FASTEST.getAllele().getValue();
				case HUMIDITY_TOLERANCE:
					return ReikaBeeHelper.getToleranceValue(ibg.getToleranceHumid()) > 0 && ReikaBeeHelper.getToleranceValue(ibg.getToleranceHumid()) < 2;
				case SPEED:
					return ibg.getSpeed() < Speeds.FASTEST.getAllele().getValue();
				case TEMPERATURE_TOLERANCE:
					return ReikaBeeHelper.getToleranceValue(ibg.getToleranceTemp()) > 0 && ReikaBeeHelper.getToleranceValue(ibg.getToleranceTemp()) < 2;
				case TERRITORY:
					return ibg.getTerritory()[0] < Territory.LARGEST.getAllele().getValue()[0];
				case CAVE_DWELLING:
				case EFFECT:
				case FLOWER_PROVIDER:
				case HUMIDITY:
				case LIFESPAN:
				case NOCTURNAL:
				case SPECIES:
				case TOLERANT_FLYER:
				default:
					return false;
			}
		}

	}

	static int[] getSearchRange(IBeeGenome ibg, IBeeHousing ibh) {
		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(ibh);
		int tr = (int)(ibg.getTerritory()[0]*3F*beeModifier.getTerritoryModifier(ibg, 1.0F)); //x, should == z; code from HasFlowersCache
		int r = tr >= 64 ? 128 : MathHelper.clamp_int(16*ReikaMathLibrary.intpow2(2, (tr-9)/2), 16, 96);
		int r2 = r >= 64 ? 24 : r >= 32 ? 16 : r >= 16 ? 12 : 8;
		return new int[]{r, r2};
	}

	public static WorldLocation getLocation(IBeeHousing ibh) {
		ChunkCoordinates c = ibh.getCoordinates();
		return new WorldLocation(ibh.getWorld(), c.posX, c.posY, c.posZ);
	}

	public static EntityPlayer getOwner(IBeeHousing ibh) {
		GameProfile p = ibh.getOwner();
		return p != null && p.getId() != null ? ibh.getWorld().func_152378_a(p.getId()) : null;
	}

}

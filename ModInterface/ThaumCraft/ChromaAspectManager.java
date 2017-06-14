/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.ThaumCraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes.PoolRecipe;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockDecoFlower.Flowers;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre.TieredOres;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredPlant.TieredPlants;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.ReversibleMultiMap;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;

public class ChromaAspectManager {

	private final ReversibleMultiMap<CrystalElement, Aspect> aspectsColor = new ReversibleMultiMap(new MultiMap.HashSetFactory());
	private final ReversibleMultiMap<CrystalElement, Aspect> aspectsThematic = new ReversibleMultiMap(new MultiMap.HashSetFactory());

	public final Aspect PUZZLE = new Aspect("perplexus", 0x90ffb0, new Aspect[]{Aspect.MIND, Aspect.TRAP}, new ResourceLocation("chromaticraft", "textures/aspects/puzzle.png"), 1);
	public final Aspect SIGNAL = new Aspect("signum", 0xffff00, new Aspect[]{Aspect.ENERGY, Aspect.AURA}, new ResourceLocation("chromaticraft", "textures/aspects/signal.png"), 1);
	public final Aspect PRECURSOR = new Aspect("principia", 0x75DAFF, new Aspect[]{Aspect.ELDRITCH, Aspect.MIND}, new ResourceLocation("chromaticraft", "textures/aspects/precursor.png"), 1);

	//private final HashMap<Aspect, ElementTagCompound> costs = new HashMap();

	public static final ChromaAspectManager instance = new ChromaAspectManager();

	private ChromaAspectManager() {
		/*
		this.addAspect(CrystalElement.BLACK, Aspect.MAGIC, Aspect.DARKNESS);
		this.addAspect(CrystalElement.RED, Aspect.ARMOR);
		this.addAspect(CrystalElement.GREEN, Aspect.POISON, Aspect.EARTH);
		this.addAspect(CrystalElement.BROWN, Aspect.HUNGER, Aspect.METAL);
		this.addAspect(CrystalElement.BLUE, Aspect.SENSES, Aspect.LIGHT);
		this.addAspect(CrystalElement.PURPLE, Aspect.MAGIC, Aspect.CRAFT, Aspect.TOOL);
		this.addAspect(CrystalElement.CYAN, Aspect.WATER);
		this.addAspect(CrystalElement.LIGHTGRAY, Aspect.TRAP, Aspect.ENTROPY);
		this.addAspect(CrystalElement.GRAY, Aspect.EXCHANGE);
		this.addAspect(CrystalElement.PINK, Aspect.WEAPON, Aspect.BEAST);
		this.addAspect(CrystalElement.LIME, Aspect.FLIGHT, Aspect.TRAVEL);
		this.addAspect(CrystalElement.YELLOW, Aspect.MINE, Aspect.ENERGY);
		this.addAspect(CrystalElement.LIGHTBLUE, Aspect.MOTION);
		this.addAspect(CrystalElement.MAGENTA, Aspect.HEAL, Aspect.LIFE);
		this.addAspect(CrystalElement.ORANGE, Aspect.FIRE);
		this.addAspect(CrystalElement.WHITE, Aspect.VOID, Aspect.AIR, Aspect.ORDER);
		 */


		this.addAspectColor(CrystalElement.BLACK, Aspect.DARKNESS);
		this.addAspectColor(CrystalElement.RED, Aspect.LIFE, Aspect.HUNGER, Aspect.HEAL);
		this.addAspectColor(CrystalElement.GREEN, Aspect.PLANT, Aspect.POISON, Aspect.EARTH);
		this.addAspectColor(CrystalElement.BROWN, Aspect.BEAST, Aspect.TREE);
		this.addAspectColor(CrystalElement.BLUE, Aspect.WATER, Aspect.TOOL);
		this.addAspectColor(CrystalElement.PURPLE, Aspect.MAGIC, Aspect.TAINT, Aspect.ELDRITCH, PUZZLE);
		this.addAspectColor(CrystalElement.CYAN, Aspect.ARMOR, Aspect.SENSES);
		this.addAspectColor(CrystalElement.LIGHTGRAY, Aspect.VOID, Aspect.SOUL, Aspect.METAL);
		this.addAspectColor(CrystalElement.GRAY, Aspect.ENTROPY);
		this.addAspectColor(CrystalElement.PINK, Aspect.MIND);
		this.addAspectColor(CrystalElement.LIME, Aspect.SLIME);
		this.addAspectColor(CrystalElement.YELLOW, Aspect.CROP, Aspect.LIGHT, Aspect.AIR, Aspect.GREED, SIGNAL);
		this.addAspectColor(CrystalElement.LIGHTBLUE, Aspect.CRYSTAL, Aspect.MECHANISM, Aspect.MOTION);
		this.addAspectColor(CrystalElement.MAGENTA, Aspect.FLESH);
		this.addAspectColor(CrystalElement.ORANGE, Aspect.FIRE);
		this.addAspectColor(CrystalElement.WHITE, Aspect.ORDER, Aspect.COLD, Aspect.FLIGHT, Aspect.WEATHER);

		this.addAspectTheme(CrystalElement.BLACK, Aspect.MAGIC);
		this.addAspectTheme(CrystalElement.RED, Aspect.ARMOR);
		this.addAspectTheme(CrystalElement.GREEN, Aspect.EARTH);
		this.addAspectTheme(CrystalElement.BROWN, Aspect.METAL, Aspect.MINE);
		this.addAspectTheme(CrystalElement.BLUE, Aspect.LIGHT);
		this.addAspectTheme(CrystalElement.PURPLE, Aspect.AURA);
		this.addAspectTheme(CrystalElement.CYAN, Aspect.WATER);
		this.addAspectTheme(CrystalElement.LIGHTGRAY, Aspect.ENTROPY, Aspect.TRAP);
		this.addAspectTheme(CrystalElement.GRAY, Aspect.EXCHANGE);
		this.addAspectTheme(CrystalElement.PINK, Aspect.WEAPON);
		this.addAspectTheme(CrystalElement.LIME, Aspect.MOTION, Aspect.TRAVEL, Aspect.FLIGHT);
		this.addAspectTheme(CrystalElement.YELLOW, Aspect.ENERGY, SIGNAL);
		if (Aspect.getAspect("tempus") != null)
			this.addAspectTheme(CrystalElement.LIGHTBLUE, Aspect.getAspect("tempus"));
		this.addAspectTheme(CrystalElement.MAGENTA, Aspect.LIFE, Aspect.HEAL);
		this.addAspectTheme(CrystalElement.ORANGE, Aspect.FIRE);
		this.addAspectTheme(CrystalElement.WHITE, Aspect.ORDER, Aspect.AIR);
	}

	private void addAspectColor(CrystalElement e, Aspect... as) {
		for (int i = 0; i < as.length; i++) {
			Aspect a = as[i];
			aspectsColor.addValue(e, a);
		}
	}

	private void addAspectTheme(CrystalElement e, Aspect... as) {
		for (int i = 0; i < as.length; i++) {
			Aspect a = as[i];
			aspectsThematic.addValue(e, a);
		}
	}
	/*
	private void addAspectCost(Aspect a, CrystalElement e) {
		ElementTagCompound tag = costs.get(a);
		if (tag == null) {
			tag = new ElementTagCompound();
			costs.put(a, tag);
		}
		tag.setTag(e, 1);
	}
	/*
	private void addAspect(CrystalElement color, Aspect... asps) {
		ArrayList li = new ArrayList();
		for (int i = 0; i < asps.length; i++)
			li.add(asps[i]);
		aspects.put(color, li);
	}
	 */

	public Collection<Aspect> getAspects(CrystalElement color) {
		return this.getAspects(color, true);
	}

	public Collection<Aspect> getAspects(CrystalElement color, boolean theme) {
		ReversibleMultiMap<CrystalElement, Aspect> map = theme ? aspectsThematic : aspectsColor;
		Collection<Aspect> li = map.getForward(color);
		return li != null ? Collections.unmodifiableCollection(li) : null;
	}

	public Collection<CrystalElement> getColorsForAspect(Aspect a) {
		return this.getColorsForAspect(a, true);
	}

	public Collection<CrystalElement> getColorsForAspect(Aspect a, boolean theme) {
		ReversibleMultiMap<CrystalElement, Aspect> map = theme ? aspectsThematic : aspectsColor;
		Collection<CrystalElement> li = map.getBackward(a);
		return li != null ? Collections.unmodifiableCollection(li) : null;
	}

	public void register() {
		Object[] asp = new Object[]{
				Aspect.AURA, 10, Aspect.AIR, 3, Aspect.CROP, 4, Aspect.CRYSTAL, 1, Aspect.EARTH, 4, Aspect.TREE, 10,
				Aspect.EXCHANGE, 5, Aspect.HEAL, 10, Aspect.LIFE, 10, Aspect.LIGHT, 2, Aspect.MAGIC, 10, Aspect.ORDER, 10,
				Aspect.PLANT, 10, /*Aspect.SEED, 2,*/ Aspect.VOID, 1
		};
		ReikaThaumHelper.addAspectsToBlock(ChromaBlocks.RAINBOWLEAF.getBlockInstance(), asp);
		ReikaThaumHelper.addAspectsToBlock(ChromaBlocks.RAINBOWSAPLING.getBlockInstance(), asp);

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement dye = CrystalElement.elements[i];
			ItemStack crystal = new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, i);
			ItemStack lamp = new ItemStack(ChromaBlocks.LAMP.getBlockInstance(), 1, i);
			ItemStack potion = new ItemStack(ChromaBlocks.SUPER.getBlockInstance(), 1, i);
			ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
			ItemStack bshard = ChromaItems.SHARD.getStackOfMetadata(i+16);
			ItemStack rune = new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, i);
			ItemStack leaf = new ItemStack(ChromaBlocks.DECAY.getBlockInstance(), 1, i);
			ItemStack leaf2 = new ItemStack(ChromaBlocks.DYELEAF.getBlockInstance(), 1, i);
			ItemStack berry = ChromaItems.BERRY.getStackOfMetadata(i);
			ItemStack lumenleaf = new ItemStack(ChromaBlocks.POWERTREE.getBlockInstance(), 1, i);
			ItemStack pendant = ChromaItems.PENDANT.getStackOf(dye);
			ItemStack pendant3 = ChromaItems.PENDANT3.getStackOf(dye);
			ItemStack acc = ChromaItems.ADJACENCY.getStackOfMetadata(i);
			ItemStack stone = ChromaItems.ELEMENTAL.getStackOfMetadata(i);
			ItemStack seed = ChromaItems.SEED.getStackOfMetadata(i);
			ItemStack plant = ChromaBlocks.PLANT.getStackOfMetadata(i);
			List<Aspect> li = new ArrayList();
			li.addAll(aspectsColor.getForward(dye));
			li.addAll(aspectsThematic.getForward(dye));
			ReikaThaumHelper.addAspects(shard, Aspect.CRYSTAL, 1);
			ReikaThaumHelper.addAspects(bshard, Aspect.CRYSTAL, 2);
			ReikaThaumHelper.addAspects(crystal, Aspect.CRYSTAL, 20);
			ReikaThaumHelper.addAspects(crystal, Aspect.AURA, 4);
			ReikaThaumHelper.addAspects(crystal, Aspect.LIGHT, 3);
			ReikaThaumHelper.addAspects(crystal, Aspect.MAGIC, 6);
			ReikaThaumHelper.addAspects(rune, Aspect.MAGIC, 8);
			ReikaThaumHelper.addAspects(rune, Aspect.CRAFT, 2);
			ReikaThaumHelper.addAspects(lamp, Aspect.LIGHT, 8);
			ReikaThaumHelper.addAspects(lamp, Aspect.CRYSTAL, 20);
			ReikaThaumHelper.addAspects(potion, Aspect.LIGHT, 8);
			ReikaThaumHelper.addAspects(potion, Aspect.MAGIC, 16);
			ReikaThaumHelper.addAspects(potion, Aspect.AURA, 16);
			ReikaThaumHelper.addAspects(potion, Aspect.CRYSTAL, 20);
			ReikaThaumHelper.addAspects(leaf, Aspect.PLANT, 3);
			ReikaThaumHelper.addAspects(leaf2, Aspect.PLANT, 3);
			ReikaThaumHelper.addAspects(berry, Aspect.PLANT, 1);
			ReikaThaumHelper.addAspects(berry, Aspect.HUNGER, 1);
			ReikaThaumHelper.addAspects(berry, Aspect.ENERGY, 1);
			ReikaThaumHelper.addAspects(lumenleaf, Aspect.ENERGY, 4);
			ReikaThaumHelper.addAspects(lumenleaf, Aspect.CRYSTAL, 4);
			ReikaThaumHelper.addAspects(lumenleaf, Aspect.TREE, 4);
			ReikaThaumHelper.addAspects(lumenleaf, Aspect.LIGHT, 4);
			ReikaThaumHelper.addAspects(lumenleaf, Aspect.CRAFT, 4);
			ReikaThaumHelper.addAspects(pendant, Aspect.CRYSTAL, 8);
			ReikaThaumHelper.addAspects(pendant3, Aspect.CRYSTAL, 8);
			ReikaThaumHelper.addAspects(pendant, Aspect.TOOL, 8);
			ReikaThaumHelper.addAspects(pendant3, Aspect.TOOL, 8);
			ReikaThaumHelper.addAspects(acc, Aspect.LIGHT, 4);
			ReikaThaumHelper.addAspects(acc, Aspect.AURA, 6);
			ReikaThaumHelper.addAspects(acc, Aspect.CRYSTAL, 4);
			ReikaThaumHelper.addAspects(acc, Aspect.ENERGY, 4);
			ReikaThaumHelper.addAspects(acc, Aspect.TRAVEL, 4);
			ReikaThaumHelper.addAspects(stone, Aspect.ENERGY, 1);
			ReikaThaumHelper.addAspects(stone, Aspect.EARTH, 1);
			ReikaThaumHelper.addAspects(plant, Aspect.PLANT, 2);
			ReikaThaumHelper.addAspects(plant, Aspect.CROP, 1);
			ReikaThaumHelper.addAspects(seed, Aspect.PLANT, 1);
			for (Aspect a : li) {
				ReikaThaumHelper.addAspects(shard, a, 2);
				ReikaThaumHelper.addAspects(bshard, a, 5);
				ReikaThaumHelper.addAspects(crystal, a, 16);
				ReikaThaumHelper.addAspects(potion, a, 16);
				ReikaThaumHelper.addAspects(rune, a, 4);
				ReikaThaumHelper.addAspects(leaf, a, 2);
				ReikaThaumHelper.addAspects(leaf2, a, 2);
				ReikaThaumHelper.addAspects(berry, a, 2);
				ReikaThaumHelper.addAspects(lumenleaf, a, 8);
				ReikaThaumHelper.addAspects(pendant, a, 12);
				ReikaThaumHelper.addAspects(pendant3, a, 24);
				ReikaThaumHelper.addAspects(acc, a, 8);
				ReikaThaumHelper.addAspects(stone, a, 4);
				ReikaThaumHelper.addAspects(seed, a, 1);
				ReikaThaumHelper.addAspects(plant, a, 3);
			}
		}

		Aspect[] flowers = {
				Aspect.DARKNESS,
				Aspect.LIFE,
				Aspect.POISON,
				Aspect.BEAST,
				Aspect.WATER,
				Aspect.TAINT,
				Aspect.MOTION,
				Aspect.SOUL,
				Aspect.ENTROPY,
				Aspect.FLESH,
				Aspect.SLIME,
				Aspect.GREED,
				Aspect.COLD,
				Aspect.MAGIC,
				Aspect.FIRE,
				Aspect.ORDER
		};
		for (int i = 0; i < 16; i++)
			ReikaThaumHelper.addAspectsToBlockMeta(ChromaBlocks.DYEFLOWER.getBlockInstance(), i, flowers[i], 1, Aspect.PLANT, 2);

		ReikaThaumHelper.addAspects(ChromaStacks.auraDust, Aspect.AURA, 2);
		ReikaThaumHelper.addAspects(ChromaStacks.auraDust, Aspect.MAGIC, 2);

		ReikaThaumHelper.addAspects(ChromaStacks.beaconDust, Aspect.AURA, 2);
		ReikaThaumHelper.addAspects(ChromaStacks.beaconDust, Aspect.ENERGY, 2);
		ReikaThaumHelper.addAspects(ChromaStacks.beaconDust, Aspect.LIGHT, 2);

		ReikaThaumHelper.addAspects(ChromaStacks.purityDust, Aspect.ORDER, 2);

		ReikaThaumHelper.addAspects(ChromaStacks.elementDust, Aspect.MAGIC, 2);

		ReikaThaumHelper.addAspects(ChromaStacks.bindingCrystal, Aspect.TRAP, 2);
		ReikaThaumHelper.addAspects(ChromaStacks.bindingCrystal, Aspect.CRYSTAL, 2);

		ReikaThaumHelper.addAspects(ChromaStacks.focusDust, Aspect.LIGHT, 2);
		ReikaThaumHelper.addAspects(ChromaStacks.focusDust, Aspect.EXCHANGE, 2);

		ReikaThaumHelper.addAspects(ChromaStacks.firaxite, Aspect.FIRE, 2);

		ReikaThaumHelper.addAspects(ChromaStacks.spaceDust, Aspect.ELDRITCH, 2);
		ReikaThaumHelper.addAspects(ChromaStacks.spaceDust, Aspect.VOID, 2);

		ReikaThaumHelper.addAspects(ChromaStacks.waterDust, Aspect.WATER, 2);

		ReikaThaumHelper.addAspects(ChromaStacks.enderDust, Aspect.TRAVEL, 2);
		ReikaThaumHelper.addAspects(ChromaStacks.enderDust, Aspect.MAGIC, 2);

		ReikaThaumHelper.addAspects(ChromaStacks.resocrystal, Aspect.ENERGY, 2);

		ReikaThaumHelper.addAspects(ChromaStacks.resonanceDust, Aspect.AURA, 2);

		ReikaThaumHelper.addAspects(ChromaStacks.lumaDust, Aspect.LIGHT, 4);
		ReikaThaumHelper.addAspects(ChromaStacks.lumaDust, Aspect.ENERGY, 4);

		ReikaThaumHelper.addAspects(ChromaStacks.echoCrystal, Aspect.MAGIC, 4);
		ReikaThaumHelper.addAspects(ChromaStacks.echoCrystal, Aspect.SENSES, 6);
		ReikaThaumHelper.addAspects(ChromaStacks.echoCrystal, SIGNAL, 6);

		ReikaThaumHelper.addAspects(ChromaStacks.fireEssence, Aspect.FIRE, 4);
		ReikaThaumHelper.addAspects(ChromaStacks.fireEssence, Aspect.AURA, 4);

		ReikaThaumHelper.addAspects(ChromaStacks.thermiticCrystal, Aspect.FIRE, 4);
		ReikaThaumHelper.addAspects(ChromaStacks.thermiticCrystal, Aspect.CRYSTAL, 4);

		ReikaThaumHelper.addAspects(ChromaStacks.glowbeans, Aspect.EXCHANGE, 8);
		ReikaThaumHelper.addAspects(ChromaStacks.glowbeans, Aspect.ENERGY, 4);

		ReikaThaumHelper.addAspects(ChromaStacks.boostroot, Aspect.GREED, 8);
		ReikaThaumHelper.addAspects(ChromaStacks.boostroot, Aspect.MAGIC, 4);

		ReikaThaumHelper.addAspects(ChromaStacks.lumenGem, Aspect.ENERGY, 4);
		ReikaThaumHelper.addAspects(ChromaStacks.lumenGem, SIGNAL, 4);

		for (PoolRecipe r : PoolRecipes.instance.getAllPoolRecipes()) {
			Collection<ItemStack> ins = r.getInputs();
			ItemStack main = r.getMainInput();
			ItemStack out = r.getOutput();
			AspectList al = new AspectList();
			for (ItemStack in : ins) {
				AspectList al2 = ThaumcraftApiHelper.generateTags(in.getItem(), in.getItemDamage());
				if (al2 != null)
					al.merge(al2);
			}
			AspectList al2 = ThaumcraftApiHelper.generateTags(main.getItem(), main.getItemDamage());
			if (al2 != null)
				al.merge(al2);
			ReikaThaumHelper.addAspects(out, al);
		}

		Aspect[] tierores = {
				Aspect.MAGIC,
				Aspect.CRAFT,
				Aspect.TRAP,
				Aspect.LIGHT,
				Aspect.TRAVEL,
				Aspect.WATER,
				Aspect.FIRE,
				Aspect.ORDER,
				Aspect.ENTROPY,
				Aspect.AURA,
				Aspect.ELDRITCH,
				Aspect.GREED,
				Aspect.WEAPON,
				Aspect.CRYSTAL,
				Aspect.ENERGY,
		};

		Aspect[] tierplants = {
				Aspect.AURA,
				Aspect.ORDER,
				Aspect.ENERGY,
				Aspect.PLANT,
				Aspect.EXCHANGE,
				Aspect.LIGHT,
				Aspect.GREED
		};

		for (int i = 0; i < TieredOres.list.length; i++) {
			ReikaThaumHelper.addAspects(ChromaBlocks.TIEREDORE.getStackOfMetadata(i), tierores[i], 2);
			ReikaThaumHelper.addAspects(ChromaBlocks.TIEREDORE.getStackOfMetadata(i), Aspect.MAGIC, 3);
		}

		for (int i = 0; i < TieredPlants.list.length; i++) {
			ReikaThaumHelper.addAspects(ChromaBlocks.TIEREDPLANT.getStackOfMetadata(i), tierplants[i], 2);
			ReikaThaumHelper.addAspects(ChromaBlocks.TIEREDPLANT.getStackOfMetadata(i), Aspect.MAGIC, 3);
		}

		ReikaThaumHelper.addAspects(ChromaBlocks.LOOTCHEST.getStackOf(), Aspect.MAGIC, 2, Aspect.GREED, 2, Aspect.TRAP, 1, Aspect.VOID, 20);
		ReikaThaumHelper.addAspects(ChromaBlocks.SELECTIVEGLASS.getStackOf(), Aspect.MAGIC, 5, Aspect.CRYSTAL, 4, Aspect.AURA, 2, Aspect.ARMOR, 8, Aspect.MIND, 1);
		ReikaThaumHelper.addAspects(ChromaBlocks.PORTAL.getStackOf(), Aspect.MAGIC, 40, Aspect.VOID, 20, Aspect.TRAVEL, 80, PUZZLE, 120, SIGNAL, 120, Aspect.CRYSTAL, 10);
		ReikaThaumHelper.addAspects(ChromaBlocks.REPEATERLAMP.getStackOf(), Aspect.LIGHT, 8, Aspect.CRYSTAL, 8);
		ReikaThaumHelper.addAspects(ChromaBlocks.CAVEINDICATOR.getStackOf(), Aspect.LIGHT, 4, Aspect.TRAP, 2);
		ReikaThaumHelper.addAspects(ChromaBlocks.METAALLOYLAMP.getStackOf(), PRECURSOR, 6, Aspect.LIGHT, 4, Aspect.LIFE, 4);

		ReikaThaumHelper.addAspects(ChromaBlocks.ENDER.getStackOf(), Aspect.MAGIC, 10, Aspect.WATER, 5, Aspect.AURA, 3, Aspect.TRAVEL, 5, Aspect.ENTROPY, 10, Aspect.EXCHANGE, 5, Aspect.ELDRITCH, 3);
		ReikaThaumHelper.addAspects(ChromaBlocks.CHROMA.getStackOf(), Aspect.MAGIC, 10, Aspect.WATER, 5, Aspect.AURA, 3, Aspect.CRAFT, 5, Aspect.ORDER, 10, Aspect.EXCHANGE, 5, Aspect.HEAL, 3);
		ReikaThaumHelper.addAspects(ChromaBlocks.LUMA.getStackOf(), Aspect.MAGIC, 4, Aspect.AURA, 5, Aspect.VOID, 10);

		ReikaThaumHelper.addAspects(ChromaBlocks.ARTEFACT.getStackOf(), PRECURSOR, 20);
		ReikaThaumHelper.addAspects(ChromaItems.DATACRYSTAL.getStackOf(), PRECURSOR, 10, Aspect.MIND, 5);
		ReikaThaumHelper.addAspects(ChromaStacks.unknownArtefact, PRECURSOR, 20);
		ReikaThaumHelper.addAspects(ChromaStacks.unknownFragments, PRECURSOR, 4, Aspect.ENTROPY, 2);

		ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.SMOOTH.ordinal()), Aspect.EARTH, 4, Aspect.CRYSTAL, 4, Aspect.MAGIC, 2);
		ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.BEAM.ordinal()), Aspect.EARTH, 4, Aspect.CRYSTAL, 4, Aspect.MAGIC, 2);
		ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.COLUMN.ordinal()), Aspect.EARTH, 4, Aspect.CRYSTAL, 4, Aspect.MAGIC, 2);
		ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.GLOWCOL.ordinal()), Aspect.EARTH, 4, Aspect.CRYSTAL, 4, Aspect.MAGIC, 2, Aspect.ENERGY, 8);
		ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.GLOWBEAM.ordinal()), Aspect.EARTH, 4, Aspect.CRYSTAL, 4, Aspect.MAGIC, 2, Aspect.ENERGY, 8);
		ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.FOCUS.ordinal()), Aspect.EARTH, 4, Aspect.CRYSTAL, 4, Aspect.MAGIC, 2, Aspect.ENERGY, 12, Aspect.AURA, 8);
		ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.CORNER.ordinal()), Aspect.EARTH, 4, Aspect.CRYSTAL, 4, Aspect.MAGIC, 2);
		ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.ENGRAVED.ordinal()), Aspect.EARTH, 4, Aspect.CRYSTAL, 4, Aspect.MAGIC, 2, Aspect.CRAFT, 1);
		ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.EMBOSSED.ordinal()), Aspect.EARTH, 4, Aspect.CRYSTAL, 4, Aspect.MAGIC, 2, Aspect.CRAFT, 1);
		ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.FOCUSFRAME.ordinal()), Aspect.EARTH, 4, Aspect.CRYSTAL, 4, Aspect.MAGIC, 1, Aspect.ENERGY, 1, Aspect.AURA, 1, Aspect.ENTROPY, 1);
		ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.GROOVE1.ordinal()), Aspect.EARTH, 4, Aspect.CRYSTAL, 4, Aspect.MAGIC, 2, Aspect.CRAFT, 1);
		ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.GROOVE2.ordinal()), Aspect.EARTH, 4, Aspect.CRYSTAL, 4, Aspect.MAGIC, 2, Aspect.CRAFT, 1);
		ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.BRICKS.ordinal()), Aspect.EARTH, 4, Aspect.CRYSTAL, 4, Aspect.MAGIC, 2, Aspect.CRAFT, 1);
		ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.MULTICHROMIC.ordinal()), Aspect.EARTH, 4, Aspect.CRYSTAL, 4, Aspect.MAGIC, 8, Aspect.CRAFT, 2, Aspect.ENERGY, 2, Aspect.AURA, 2);
		ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.STABILIZER.ordinal()), Aspect.EARTH, 4, Aspect.CRYSTAL, 4, Aspect.MAGIC, 2, Aspect.ENERGY, 12, Aspect.AURA, 8);
		ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.RESORING.ordinal()), Aspect.EARTH, 4, Aspect.CRYSTAL, 4, Aspect.MAGIC, 2, Aspect.ENERGY, 12, Aspect.AURA, 4, Aspect.EXCHANGE, 6);
		for (int i = 0; i < 16; i++) {
			for (Aspect a : aspectsThematic.getForward(CrystalElement.elements[i])) {
				ReikaThaumHelper.addAspects(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.MULTICHROMIC.ordinal()), a, 2);
			}
		}

		for (int i = 0; i <= 8; i += 8) {
			ReikaThaumHelper.addAspects(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.CLOAK.ordinal()+i), Aspect.DARKNESS, 4, Aspect.EARTH, 4, Aspect.MAGIC, 2, Aspect.ARMOR, 4, PUZZLE, 4, PRECURSOR, 2);
			ReikaThaumHelper.addAspects(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.STONE.ordinal()+i), Aspect.EARTH, 4, Aspect.MAGIC, 2, Aspect.ARMOR, 4, PUZZLE, 4, PRECURSOR, 2);
			ReikaThaumHelper.addAspects(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.COBBLE.ordinal()+i), Aspect.EARTH, 4, Aspect.MAGIC, 2, Aspect.ARMOR, 4, PUZZLE, 4, PRECURSOR, 2);
			ReikaThaumHelper.addAspects(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.CRACK.ordinal()+i), Aspect.EARTH, 4, Aspect.MAGIC, 2, Aspect.ARMOR, 4, PUZZLE, 4, PRECURSOR, 2);
			ReikaThaumHelper.addAspects(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.MOSS.ordinal()+i), Aspect.EARTH, 4, Aspect.MAGIC, 2, Aspect.ARMOR, 4, PUZZLE, 4, Aspect.PLANT, 1, PRECURSOR, 2);
			ReikaThaumHelper.addAspects(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.GLASS.ordinal()+i), Aspect.EARTH, 4, Aspect.MAGIC, 2, Aspect.ARMOR, 4, PUZZLE, 4, PRECURSOR, 2);
			ReikaThaumHelper.addAspects(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.LIGHT.ordinal()+i), Aspect.LIGHT, 4, Aspect.EARTH, 4, Aspect.MAGIC, 2, Aspect.ARMOR, 4, PUZZLE, 4, PRECURSOR, 2);
			ReikaThaumHelper.addAspects(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockType.CRACKS.ordinal()+i), Aspect.EARTH, 4, Aspect.MAGIC, 2, Aspect.ARMOR, 4, PUZZLE, 4, PRECURSOR, 2);
		}

		for (int i = 0; i < Flowers.list.length; i++) {
			ItemStack is = ChromaBlocks.DECOFLOWER.getStackOfMetadata(i);
			ItemStack is2 = Flowers.list[i].getDrop();
			ReikaThaumHelper.addAspects(is, Aspect.PLANT, 2, Aspect.MAGIC, 2);
			ReikaThaumHelper.addAspects(is2, Aspect.PLANT, 1, Aspect.MAGIC, 2);
			switch(Flowers.list[i]) {
				case ENDERFLOWER:
					ReikaThaumHelper.addAspects(is, Aspect.AURA, 4, Aspect.ELDRITCH, 1);
					ReikaThaumHelper.addAspects(is2, Aspect.AURA, 1, Aspect.ELDRITCH, 1);
					break;
				case FLOWIVY:
					ReikaThaumHelper.addAspects(is, Aspect.LIFE, 4, Aspect.EARTH, 1);
					ReikaThaumHelper.addAspects(is2, Aspect.LIFE, 1, Aspect.EARTH, 1);
					break;
				case LUMALILY:
					ReikaThaumHelper.addAspects(is, Aspect.COLD, 4, Aspect.CRYSTAL, 1);
					ReikaThaumHelper.addAspects(is2, Aspect.COLD, 1, Aspect.CRYSTAL, 1);
					break;
				case RESOCLOVER:
					ReikaThaumHelper.addAspects(is, Aspect.ENERGY, 4, Aspect.MOTION, 1);
					ReikaThaumHelper.addAspects(is2, Aspect.ENERGY, 1, Aspect.MOTION, 1);
					break;
				case SANOBLOOM:
					ReikaThaumHelper.addAspects(is, Aspect.HEAL, 4, Aspect.TRAVEL, 1);
					ReikaThaumHelper.addAspects(is2, Aspect.HEAL, 1, Aspect.TRAVEL, 1);
					break;
				case VOIDREED:
					ReikaThaumHelper.addAspects(is, Aspect.VOID, 4, Aspect.DARKNESS, 1);
					ReikaThaumHelper.addAspects(is2, Aspect.VOID, 1, Aspect.DARKNESS, 1);
					break;
				case GLOWDAISY:
				case GLOWROOT:
					ReikaThaumHelper.addAspects(is, Aspect.LIGHT, 4);
					break;
			}
		}
	}

	public ElementTagCompound getElementCost(Aspect a, float depthcost) {
		ElementTagCompound tag = new ElementTagCompound();
		this.recursiveCount(a, tag, 0, depthcost);
		if (tag.isEmpty()) {
			ChromatiCraft.logger.logError("Aspect "+a.getName()+" was calculated to have zero cost!");
		}
		return tag;
	}

	private void recursiveCount(Aspect a, ElementTagCompound tag, int depth, float depthcost) {
		Collection<CrystalElement> li = aspectsThematic.getBackward(a);
		if (li == null || li.isEmpty()) {
			Aspect[] parents = a.getComponents();
			if (parents != null && parents.length > 0) {
				for (int i = 0; i < parents.length; i++) {
					this.recursiveCount(parents[i], tag, depth+1, depthcost);
				}
			}
		}
		else {
			int amt = depthcost > 0 ? (int)((1+depth)*depthcost) : 1;
			for (CrystalElement e : li) {
				tag.addValueToColor(e, amt);
			}
		}
	}

}

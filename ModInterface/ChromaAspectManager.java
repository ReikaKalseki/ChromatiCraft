/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;

public class ChromaAspectManager {

	private final EnumMap<CrystalElement, ArrayList<Aspect>> aspects = new EnumMap(CrystalElement.class);
	private final HashMap<Aspect, ElementTagCompound> costs = new HashMap();

	public static final ChromaAspectManager instance = new ChromaAspectManager();

	private ChromaAspectManager() {
		this.addAspect(CrystalElement.BLACK, Aspect.MAGIC, Aspect.DARKNESS);
		this.addAspect(CrystalElement.BLUE, Aspect.SENSES, Aspect.LIGHT);
		this.addAspect(CrystalElement.BROWN, Aspect.HUNGER, Aspect.METAL);
		this.addAspect(CrystalElement.CYAN, Aspect.WATER);
		this.addAspect(CrystalElement.GRAY, Aspect.EXCHANGE);
		this.addAspect(CrystalElement.GREEN, Aspect.POISON, Aspect.EARTH);
		this.addAspect(CrystalElement.LIGHTBLUE, Aspect.MOTION);
		this.addAspect(CrystalElement.LIGHTGRAY, Aspect.TRAP, Aspect.ENTROPY);
		this.addAspect(CrystalElement.LIME, Aspect.FLIGHT, Aspect.TRAVEL);
		this.addAspect(CrystalElement.MAGENTA, Aspect.HEAL, Aspect.LIFE);
		this.addAspect(CrystalElement.ORANGE, Aspect.FIRE);
		this.addAspect(CrystalElement.PINK, Aspect.WEAPON, Aspect.BEAST);
		this.addAspect(CrystalElement.PURPLE, Aspect.MAGIC, Aspect.CRAFT, Aspect.TOOL);
		this.addAspect(CrystalElement.RED, Aspect.ARMOR);
		this.addAspect(CrystalElement.WHITE, Aspect.VOID, Aspect.AIR, Aspect.ORDER);
		this.addAspect(CrystalElement.YELLOW, Aspect.MINE, Aspect.ENERGY);

		this.addAspectCost(Aspect.ORDER, CrystalElement.WHITE);
		this.addAspectCost(Aspect.FIRE, CrystalElement.ORANGE);
		this.addAspectCost(Aspect.ENERGY, CrystalElement.YELLOW);
		this.addAspectCost(Aspect.MAGIC, CrystalElement.BLACK);
		this.addAspectCost(Aspect.MOTION, CrystalElement.LIME);
		this.addAspectCost(Aspect.LIFE, CrystalElement.MAGENTA);
		this.addAspectCost(Aspect.ARMOR, CrystalElement.RED);
		this.addAspectCost(Aspect.EARTH, CrystalElement.GREEN);
		this.addAspectCost(Aspect.HUNGER, CrystalElement.BROWN);
		this.addAspectCost(Aspect.LIGHT, CrystalElement.BLUE);
		this.addAspectCost(Aspect.CRAFT, CrystalElement.PURPLE);
		this.addAspectCost(Aspect.WEAPON, CrystalElement.PINK);
		this.addAspectCost(Aspect.EXCHANGE, CrystalElement.GRAY);
		this.addAspectCost(Aspect.MIND, CrystalElement.LIGHTGRAY);
		//this.addAspectCost(Aspect.AURA, CrystalElement.LIGHTBLUE);
		this.addAspectCost(Aspect.WATER, CrystalElement.CYAN);
		this.addAspectCost(Aspect.ENTROPY, CrystalElement.BLACK);
		this.addAspectCost(Aspect.AIR, CrystalElement.WHITE);
	}

	private void addAspectCost(Aspect a, CrystalElement e) {
		ElementTagCompound tag = costs.get(a);
		if (tag == null) {
			tag = new ElementTagCompound();
			costs.put(a, tag);
		}
		tag.setTag(e, 1);
	}

	private void addAspect(CrystalElement color, Aspect... asps) {
		ArrayList li = new ArrayList();
		for (int i = 0; i < asps.length; i++)
			li.add(asps[i]);
		aspects.put(color, li);
	}

	public ArrayList<Aspect> getAspects(CrystalElement color) {
		ArrayList li = new ArrayList();
		ArrayList l2 = aspects.get(color);
		if (l2 != null)
			li.addAll(l2);
		return li;
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
			ItemStack rune = new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, i);
			ArrayList<Aspect> li = this.getAspects(dye);
			ReikaThaumHelper.addAspects(shard, Aspect.CRYSTAL, 1);
			ReikaThaumHelper.addAspects(crystal, Aspect.CRYSTAL, 20);
			ReikaThaumHelper.addAspects(crystal, Aspect.AURA, 4);
			ReikaThaumHelper.addAspects(crystal, Aspect.LIGHT, 3);
			ReikaThaumHelper.addAspects(crystal, Aspect.MAGIC, 6);
			ReikaThaumHelper.addAspects(lamp, Aspect.LIGHT, 8);
			ReikaThaumHelper.addAspects(lamp, Aspect.CRYSTAL, 20);
			ReikaThaumHelper.addAspects(potion, Aspect.LIGHT, 8);
			ReikaThaumHelper.addAspects(potion, Aspect.MAGIC, 16);
			ReikaThaumHelper.addAspects(potion, Aspect.AURA, 16);
			ReikaThaumHelper.addAspects(potion, Aspect.CRYSTAL, 20);
			for (int k = 0; k < li.size(); k++) {
				Aspect as = li.get(k);
				ReikaThaumHelper.addAspects(shard, as, 2);
				ReikaThaumHelper.addAspects(crystal, as, 16);
				ReikaThaumHelper.addAspects(potion, as, 16);
				ReikaThaumHelper.addAspects(rune, as, 4);
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
	}

	public ElementTagCompound getElementCost(Aspect a, float depthcost) {
		ElementTagCompound tag = new ElementTagCompound();
		this.recursiveCount(a, tag, 0, depthcost);
		return tag;
	}

	private void recursiveCount(Aspect a, ElementTagCompound tag, int depth, float depthcost) {
		ElementTagCompound cost = costs.get(a);
		if (cost == null) {
			Aspect[] parents = a.getComponents();
			if (parents != null) {
				for (int i = 0; i < parents.length; i++)
					this.recursiveCount(parents[i], tag, depth+1, depthcost);
			}
		}
		else {
			cost = cost.copy();
			if (depthcost > 0)
				cost.scale((1+depth)*depthcost);
			tag.addTag(cost);
		}
	}

}

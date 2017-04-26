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

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;

public class ChromaAspectMapper {

	public static final ChromaAspectMapper instance = new ChromaAspectMapper();

	private final HashMap<MachineKey, AspectList> data = new HashMap();

	private final ItemHashMap<AspectList> calcedValues = new ItemHashMap();

	private ChromaAspectMapper() {

		this.addAspect(ChromaTiles.GUARDIAN, Aspect.ARMOR, 12);
		this.addAspect(ChromaTiles.GUARDIAN, Aspect.AURA, 6);
		this.addAspect(ChromaTiles.GUARDIAN, Aspect.LIGHT, 4);
		this.addAspect(ChromaTiles.GUARDIAN, Aspect.CRYSTAL, 4);

		this.addAspect(ChromaTiles.FARMER, Aspect.HARVEST, 12);
		this.addAspect(ChromaTiles.FARMER, Aspect.AURA, 4);

		this.addAspect(ChromaTiles.CHROMAFLOWER, Aspect.AURA, 1);
		this.addAspect(ChromaTiles.CHROMAFLOWER, Aspect.PLANT, 2);
		this.addAspect(ChromaTiles.CHROMAFLOWER, Aspect.MAGIC, 1);

		this.addAspect(ChromaTiles.HEATLILY, Aspect.AURA, 2);
		this.addAspect(ChromaTiles.HEATLILY, Aspect.FIRE, 2);
		this.addAspect(ChromaTiles.HEATLILY, Aspect.PLANT, 2);

		this.addAspect(ChromaTiles.POWERTREE, Aspect.ENERGY, 12);
		this.addAspect(ChromaTiles.POWERTREE, Aspect.LIGHT, 12);
		this.addAspect(ChromaTiles.POWERTREE, Aspect.EXCHANGE, 12);

		this.addAspect(ChromaTiles.REPEATER, Aspect.ENERGY, 12);
		this.addAspect(ChromaTiles.REPEATER, Aspect.LIGHT, 8);
		this.addAspect(ChromaTiles.REPEATER, Aspect.EXCHANGE, 6);
		this.addAspect(ChromaTiles.REPEATER, ChromaAspectManager.instance.SIGNAL, 18);

		this.addAspect(ChromaTiles.WEAKREPEATER, Aspect.ENERGY, 6);
		this.addAspect(ChromaTiles.WEAKREPEATER, Aspect.LIGHT, 4);
		this.addAspect(ChromaTiles.WEAKREPEATER, Aspect.EXCHANGE, 3);
		this.addAspect(ChromaTiles.WEAKREPEATER, ChromaAspectManager.instance.SIGNAL, 9);

		this.addAspect(ChromaTiles.COMPOUND, Aspect.ENERGY, 16);
		this.addAspect(ChromaTiles.COMPOUND, Aspect.LIGHT, 12);
		this.addAspect(ChromaTiles.COMPOUND, Aspect.EXCHANGE, 8);
		this.addAspect(ChromaTiles.COMPOUND, ChromaAspectManager.instance.SIGNAL, 18);

		this.addAspect(ChromaTiles.BROADCAST, Aspect.ENERGY, 60);
		this.addAspect(ChromaTiles.BROADCAST, Aspect.LIGHT, 60);
		this.addAspect(ChromaTiles.BROADCAST, Aspect.EXCHANGE, 60);
		this.addAspect(ChromaTiles.BROADCAST, ChromaAspectManager.instance.SIGNAL, 100);

		this.addAspect(ChromaTiles.CRYSTAL, Aspect.ENERGY, 12);
		this.addAspect(ChromaTiles.CRYSTAL, Aspect.MAGIC, 12);

		this.addAspect(ChromaTiles.RIFT, Aspect.VOID, 12);
		this.addAspect(ChromaTiles.RIFT, Aspect.TRAVEL, 8);

		this.addAspect(ChromaTiles.TELEPUMP, Aspect.VOID, 4);
		this.addAspect(ChromaTiles.TELEPUMP, Aspect.MAGIC, 4);
		this.addAspect(ChromaTiles.TELEPUMP, Aspect.WATER, 12);
		this.addAspect(ChromaTiles.TELEPUMP, Aspect.ENERGY, 4);

		this.addAspect(ChromaTiles.MINER, Aspect.VOID, 4);
		this.addAspect(ChromaTiles.MINER, Aspect.MAGIC, 4);
		this.addAspect(ChromaTiles.MINER, Aspect.MINE, 12);
		this.addAspect(ChromaTiles.MINER, Aspect.ENERGY, 4);

		this.addAspect(ChromaTiles.ASPECTJAR, Aspect.VOID, 12);
		this.addAspect(ChromaTiles.ASPECTJAR, Aspect.MAGIC, 12);
		this.addAspect(ChromaTiles.ASPECTJAR, Aspect.WATER, 4);

		this.addAspect(ChromaTiles.ASPECT, Aspect.AURA, 6);
		this.addAspect(ChromaTiles.ASPECT, Aspect.MAGIC, 8);
		this.addAspect(ChromaTiles.ASPECT, Aspect.EXCHANGE, 12);

		this.addAspect(ChromaTiles.BEACON, Aspect.ARMOR, 24);
		this.addAspect(ChromaTiles.BEACON, Aspect.AURA, 12);

		this.addAspect(ChromaTiles.FURNACE, Aspect.FIRE, 12);
		this.addAspect(ChromaTiles.FURNACE, Aspect.CRAFT, 8);
		this.addAspect(ChromaTiles.FURNACE, Aspect.GREED, 4);

		this.addAspect(ChromaTiles.ENCHANTER, Aspect.MAGIC, 8);
		this.addAspect(ChromaTiles.ENCHANTER, Aspect.TOOL, 12);
		this.addAspect(ChromaTiles.ENCHANTER, Aspect.MAN, 4);

		this.addAspect(ChromaTiles.FABRICATOR, Aspect.CRAFT, 12);
		this.addAspect(ChromaTiles.FABRICATOR, Aspect.MAGIC, 8);
		this.addAspect(ChromaTiles.FABRICATOR, Aspect.EXCHANGE, 8);

		this.addAspect(ChromaTiles.BIOMEPAINTER, Aspect.EARTH, 12);
		this.addAspect(ChromaTiles.BIOMEPAINTER, Aspect.WEATHER, 8);
		this.addAspect(ChromaTiles.BIOMEPAINTER, Aspect.EXCHANGE, 8);

		this.addAspect(ChromaTiles.BREWER, Aspect.CRAFT, 12);
		this.addAspect(ChromaTiles.BREWER, Aspect.HUNGER, 4);
		this.addAspect(ChromaTiles.BREWER, Aspect.MAGIC, 8);
		this.addAspect(ChromaTiles.BREWER, Aspect.WATER, 8);

		this.addAspect(ChromaTiles.ITEMCOLLECTOR, Aspect.AURA, 6);
		this.addAspect(ChromaTiles.ITEMCOLLECTOR, Aspect.TRAP, 8);
		this.addAspect(ChromaTiles.ITEMCOLLECTOR, Aspect.TRAVEL, 12);
		this.addAspect(ChromaTiles.ITEMCOLLECTOR, Aspect.GREED, 4);

		this.addAspect(ChromaTiles.TANK, Aspect.CRYSTAL, 6);
		this.addAspect(ChromaTiles.TANK, Aspect.WATER, 12);
		this.addAspect(ChromaTiles.TANK, Aspect.VOID, 8);

		this.addAspect(ChromaTiles.MEDISTRIBUTOR, Aspect.MECHANISM, 4);
		this.addAspect(ChromaTiles.MEDISTRIBUTOR, Aspect.MIND, 4);

		this.addAspect(ChromaTiles.TICKER, Aspect.TOOL, 4);
		if (Aspect.getAspect("tempus") != null)
			this.addAspect(ChromaTiles.TICKER, Aspect.getAspect("tempus"), 8);

		this.addAspect(ChromaTiles.STAND, Aspect.TOOL, 4);
		this.addAspect(ChromaTiles.STAND, Aspect.CRAFT, 8);

		this.addAspect(ChromaTiles.TABLE, Aspect.CRAFT, 12);
		this.addAspect(ChromaTiles.TABLE, Aspect.CRYSTAL, 6);

		this.addAspect(ChromaTiles.AUTOMATOR, Aspect.CRAFT, 12);
		this.addAspect(ChromaTiles.AUTOMATOR, Aspect.CRYSTAL, 6);
		this.addAspect(ChromaTiles.AUTOMATOR, Aspect.MECHANISM, 8);
		this.addAspect(ChromaTiles.AUTOMATOR, Aspect.SOUL, 8);

		this.addAspect(ChromaTiles.RITUAL, Aspect.CRAFT, 4);
		this.addAspect(ChromaTiles.RITUAL, Aspect.MAGIC, 8);
		this.addAspect(ChromaTiles.RITUAL, Aspect.MAN, 6);
		this.addAspect(ChromaTiles.RITUAL, Aspect.SOUL, 4);

		this.addAspect(ChromaTiles.REPROGRAMMER, Aspect.BEAST, 6);
		this.addAspect(ChromaTiles.REPROGRAMMER, Aspect.EXCHANGE, 6);

		this.addAspect(ChromaTiles.AURAPOINT, Aspect.AURA, 12);
		this.addAspect(ChromaTiles.AURAPOINT, Aspect.MAGIC, 12);
		this.addAspect(ChromaTiles.AURAPOINT, Aspect.ARMOR, 12);
		this.addAspect(ChromaTiles.AURAPOINT, Aspect.WEAPON, 12);
		this.addAspect(ChromaTiles.AURAPOINT, Aspect.SENSES, 12);
		this.addAspect(ChromaTiles.AURAPOINT, Aspect.ENERGY, 12);
		this.addAspect(ChromaTiles.AURAPOINT, Aspect.CRYSTAL, 12);
		this.addAspect(ChromaTiles.AURAPOINT, Aspect.CROP, 12);

		this.addAspect(ChromaTiles.DIMENSIONCORE, Aspect.CRYSTAL, 8);
		this.addAspect(ChromaTiles.DIMENSIONCORE, Aspect.MAGIC, 8);

		this.addAspect(ChromaTiles.LASER, Aspect.CRYSTAL, 8);
		this.addAspect(ChromaTiles.LASER, Aspect.AURA, 8);
		this.addAspect(ChromaTiles.LASER, Aspect.LIGHT, 8);

		this.addAspect(ChromaTiles.FENCE, Aspect.CRYSTAL, 6);
		this.addAspect(ChromaTiles.FENCE, Aspect.ARMOR, 4);
		this.addAspect(ChromaTiles.FENCE, Aspect.TRAP, 8);

		this.addAspect(ChromaTiles.LAMPCONTROL, Aspect.CRYSTAL, 4);
		this.addAspect(ChromaTiles.LAMPCONTROL, Aspect.LIGHT, 8);
		this.addAspect(ChromaTiles.LAMPCONTROL, Aspect.AURA, 6);

		this.addAspect(ChromaTiles.CHARGER, Aspect.ENERGY, 12);
		this.addAspect(ChromaTiles.CHARGER, Aspect.CRYSTAL, 8);

		this.addAspect(ChromaTiles.STRUCTCONTROL, Aspect.CRYSTAL, 8);
		this.addAspect(ChromaTiles.STRUCTCONTROL, Aspect.MAGIC, 8);
		this.addAspect(ChromaTiles.STRUCTCONTROL, Aspect.TRAP, 8);
		this.addAspect(ChromaTiles.STRUCTCONTROL, Aspect.MIND, 8);

		this.addAspect(ChromaTiles.LIGHTER, Aspect.LIGHT, 8);
		this.addAspect(ChromaTiles.LIGHTER, Aspect.MINE, 4);

		this.addAspect(ChromaTiles.RFDISTRIBUTOR, Aspect.ENERGY, 8);
		this.addAspect(ChromaTiles.RFDISTRIBUTOR, Aspect.AURA, 4);

		this.addAspect(ChromaTiles.FLUIDDISTRIBUTOR, Aspect.WATER, 8);
		this.addAspect(ChromaTiles.FLUIDDISTRIBUTOR, Aspect.AURA, 4);

		this.addAspect(ChromaTiles.WINDOW, Aspect.TRAVEL, 12);

		this.addAspect(ChromaTiles.TURRET, Aspect.WEAPON, 12);

		this.addAspect(ChromaTiles.CLOAKING, Aspect.AURA, 4);
		this.addAspect(ChromaTiles.CLOAKING, Aspect.TRAP, 6);
		this.addAspect(ChromaTiles.CLOAKING, Aspect.MIND, 8);

		this.addAspect(ChromaTiles.GLOWFIRE, Aspect.ENERGY, 4);
		this.addAspect(ChromaTiles.GLOWFIRE, Aspect.GREED, 4);
		this.addAspect(ChromaTiles.GLOWFIRE, Aspect.EXCHANGE, 8);

		this.addAspect(ChromaTiles.ESSENTIARELAY, Aspect.ENERGY, 6);
		this.addAspect(ChromaTiles.ESSENTIARELAY, Aspect.TRAVEL, 6);
		this.addAspect(ChromaTiles.ESSENTIARELAY, Aspect.AURA, 3);

		this.addAspect(ChromaTiles.INSERTER, Aspect.SOUL, 4);
		this.addAspect(ChromaTiles.INSERTER, Aspect.TRAVEL, 2);

		this.addAspect(ChromaTiles.REVERTER, Aspect.TAINT, 1);
		this.addAspect(ChromaTiles.REVERTER, Aspect.EARTH, 4);
		this.addAspect(ChromaTiles.REVERTER, Aspect.HEAL, 6);
		this.addAspect(ChromaTiles.REVERTER, Aspect.PLANT, 3);

		this.addAspect(ChromaTiles.COBBLEGEN, Aspect.EXCHANGE, 6);
		this.addAspect(ChromaTiles.COBBLEGEN, Aspect.WATER, 4);
		this.addAspect(ChromaTiles.COBBLEGEN, Aspect.FIRE, 4);
		this.addAspect(ChromaTiles.COBBLEGEN, Aspect.PLANT, 3);

		this.addAspect(ChromaTiles.PLANTACCEL, Aspect.PLANT, 3);
		if (Aspect.getAspect("tempus") != null)
			this.addAspect(ChromaTiles.PLANTACCEL, Aspect.getAspect("tempus"), 6);

		this.addAspect(ChromaTiles.CROPSPEED, Aspect.PLANT, 3);
		this.addAspect(ChromaTiles.CROPSPEED, Aspect.CROP, 6);
		if (Aspect.getAspect("tempus") != null)
			this.addAspect(ChromaTiles.CROPSPEED, Aspect.getAspect("tempus"), 4);

		this.addAspect(ChromaTiles.HARVESTPLANT, Aspect.HUNGER, 4);
		this.addAspect(ChromaTiles.HARVESTPLANT, Aspect.HARVEST, 6);
		this.addAspect(ChromaTiles.HARVESTPLANT, Aspect.PLANT, 3);

		this.addAspect(ChromaTiles.ENCHANTDECOMP, Aspect.EXCHANGE, 6);
		this.addAspect(ChromaTiles.ENCHANTDECOMP, Aspect.CRAFT, 4);
		this.addAspect(ChromaTiles.ENCHANTDECOMP, Aspect.MAGIC, 6);
		this.addAspect(ChromaTiles.ENCHANTDECOMP, Aspect.MIND, 2);

		this.addAspect(ChromaTiles.LUMENWIRE, Aspect.SENSES, 4);
		this.addAspect(ChromaTiles.LUMENWIRE, ChromaAspectManager.instance.SIGNAL, 4);

		this.addAspect(ChromaTiles.PARTICLES, Aspect.LIGHT, 4);

		this.addAspect(ChromaTiles.WIRELESS, Aspect.ENERGY, 10);
		this.addAspect(ChromaTiles.WIRELESS, Aspect.AURA, 8);

		this.addAspect(ChromaTiles.METEOR, Aspect.WEAPON, 12);
		this.addAspect(ChromaTiles.METEOR, Aspect.TRAVEL, 6);
		this.addAspect(ChromaTiles.METEOR, Aspect.FIRE, 8);
		this.addAspect(ChromaTiles.METEOR, Aspect.SENSES, 4);

		this.addAspect(ChromaTiles.AREABREAKER, Aspect.MINE, 8);
		this.addAspect(ChromaTiles.AREABREAKER, Aspect.MAGIC, 4);

		this.addAspect(ChromaTiles.TELEPORT, Aspect.TRAVEL, 12);
		this.addAspect(ChromaTiles.TELEPORT, Aspect.MAGIC, 8);
		this.addAspect(ChromaTiles.TELEPORT, Aspect.AURA, 4);
		this.addAspect(ChromaTiles.TELEPORT, Aspect.EXCHANGE, 6);

		this.addAspect(ChromaTiles.FLUIDRELAY, Aspect.WATER, 6);
		this.addAspect(ChromaTiles.FLUIDRELAY, Aspect.TRAVEL, 3);

		this.addAspect(ChromaTiles.BOOKDECOMP, Aspect.MIND, 6);
		this.addAspect(ChromaTiles.BOOKDECOMP, Aspect.GREED, 4);
		this.addAspect(ChromaTiles.BOOKDECOMP, Aspect.ENTROPY, 2);

		this.addAspect(ChromaTiles.AVOLASER, Aspect.WEAPON, 12);
		this.addAspect(ChromaTiles.AVOLASER, Aspect.ENERGY, 8);
		this.addAspect(ChromaTiles.AVOLASER, Aspect.MAGIC, 6);
		this.addAspect(ChromaTiles.AVOLASER, Aspect.MECHANISM, 3);

		this.addAspect(ChromaTiles.ALVEARY, Aspect.BEAST, 8);
		this.addAspect(ChromaTiles.ALVEARY, Aspect.MAGIC, 4);
		this.addAspect(ChromaTiles.ALVEARY, Aspect.FIRE, 6);
		this.addAspect(ChromaTiles.ALVEARY, Aspect.WATER, 6);
		this.addAspect(ChromaTiles.ALVEARY, Aspect.AIR, 6);
		this.addAspect(ChromaTiles.ALVEARY, Aspect.HEAL, 6);
		if (Aspect.getAspect("tempus") != null)
			this.addAspect(ChromaTiles.ALVEARY, Aspect.getAspect("tempus"), 6);

		this.addAspect(ChromaTiles.ROUTERHUB, Aspect.TRAVEL, 8);
		this.addAspect(ChromaTiles.ROUTERHUB, Aspect.MECHANISM, 6);
		this.addAspect(ChromaTiles.ROUTERHUB, Aspect.HARVEST, 2);

		if (Aspect.getAspect("tempus") != null)
			this.addAspect(ChromaTiles.FOCUSCRYSTAL, Aspect.getAspect("tempus"), 6);
		this.addAspect(ChromaTiles.FOCUSCRYSTAL, ChromaAspectManager.instance.SIGNAL, 12);
		this.addAspect(ChromaTiles.FOCUSCRYSTAL, Aspect.MAGIC, 4);
	}

	private void addAspect(ChromaTiles m, Aspect a, int amt) {
		MachineKey key = new MachineKey(m);
		AspectList al = data.get(key);
		if (al == null) {
			al = new AspectList();
			data.put(key, al);
		}
		al.merge(a, amt);
		//ReikaJavaLibrary.pConsole(this.data.get(key));
	}

	private void addAspect(ChromaTiles m, int offset, Aspect a, int amt) {
		MachineKey key = new MachineKey(m, offset);
		AspectList al = data.get(key);
		if (al == null) {
			al = new AspectList();
			data.put(key, al);
		}
		al.merge(a, amt);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (MachineKey m : data.keySet()) {
			sb.append(m+": "+ReikaThaumHelper.aspectsToString(data.get(m)));
			sb.append("; ");
		}
		sb.append("]");
		return sb.toString();
	}

	private static class MachineKey {
		public final ChromaTiles machine;
		public final int offset;

		private MachineKey(ChromaTiles m) {
			this(m, 0);
		}

		private MachineKey(ChromaTiles m, int offset) {
			machine = m;
			this.offset = offset;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof MachineKey) {
				MachineKey mk = (MachineKey)o;
				return mk.machine == machine && mk.offset == offset;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return machine.ordinal()+offset*2048;
		}

		@Override
		public String toString() {
			return machine+"%"+offset;
		}
	}

	private AspectList getAspects(ChromaTiles m, int offset) {
		ItemStack is = this.getItem(m, offset);
		AspectList al = ThaumcraftApiHelper.generateTags(is.getItem(), is.getItemDamage());
		if (al == null)
			al = new AspectList();
		al.merge(this.getCraftedCalcedAspects(is));
		if (m.hasNBTVariants()) {
			al.merge(Aspect.EXCHANGE, 1);
		}
		AspectList prekey = data.get(new MachineKey(m, offset));
		if (prekey != null) {
			al.merge(prekey);
		}
		return al;
	}

	private HashSet<KeyedItemStack> calcing = new HashSet();

	private AspectList getCraftedCalcedAspects(ItemStack is) {
		AspectList al = calcedValues.get(is);
		if (al != null)
			return al;
		KeyedItemStack ks = new KeyedItemStack(is).setIgnoreNBT(true);
		if (calcing.contains(ks))
			return new AspectList();
		calcing.add(ks);
		al = ThaumcraftApiHelper.generateTags(is.getItem(), is.getItemDamage());
		if (al == null)
			al = new AspectList();
		for (CastingRecipe c : RecipesCastingTable.instance.getAllRecipesMaking(is)) {
			for (ItemStack in : c.getAllInputs()) {
				al.merge(this.getCraftedCalcedAspects(in));
			}
		}
		calcedValues.put(is, al);
		calcing.remove(ks);
		return al;
	}

	private ItemStack getItem(ChromaTiles m, int offset) {
		return m.getCraftedProduct();
	}

	public void register() {
		for (int i = 0; i < ChromaTiles.TEList.length; i++) {
			ChromaTiles m = ChromaTiles.TEList[i];
			ItemStack is = m.getCraftedProduct();
			if (is != null) {
				AspectList al = this.getAspects(m, 0);
				if (al != null) {
					ReikaThaumHelper.clearNullAspects(al);
					ReikaThaumHelper.addAspects(is, al);
					ReikaThaumHelper.addAspectsToBlockMeta(m.getBlock(), m.getBlockMetadata(), al);
				}
			}
		}
	}

}

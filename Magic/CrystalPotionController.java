/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import thaumcraft.api.aspects.Aspect;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class CrystalPotionController {

	private static HashMap<CrystalElement, Potion> map = new HashMap();
	private static HashMap<CrystalElement, Potion> nethermap = new HashMap();
	private static HashMap<CrystalElement, ArrayList<Aspect>> aspects = new HashMap();

	static {
		addColorPotion(CrystalElement.BLUE, Potion.nightVision);
		addColorPotion(CrystalElement.CYAN, Potion.waterBreathing);
		addColorPotion(CrystalElement.GRAY, Potion.moveSlowdown);
		addColorPotion(CrystalElement.GREEN, Potion.poison);
		addColorPotion(CrystalElement.LIGHTBLUE, Potion.moveSpeed);
		addColorPotion(CrystalElement.LIGHTGRAY, Potion.weakness);
		addColorPotion(CrystalElement.LIME, Potion.jump);
		addColorPotion(CrystalElement.MAGENTA, Potion.regeneration);
		addColorPotion(CrystalElement.RED, Potion.resistance);
		addColorPotion(CrystalElement.ORANGE, Potion.fireResistance);
		addColorPotion(CrystalElement.PINK, Potion.damageBoost);
		addColorPotion(CrystalElement.YELLOW, Potion.digSpeed);
		addColorPotion(CrystalElement.WHITE, Potion.invisibility);
		addColorPotion(CrystalElement.BROWN, Potion.field_76443_y);

		addNetherPotion(CrystalElement.BLACK, Potion.wither);
		addNetherPotion(CrystalElement.CYAN, Potion.hunger);
		addNetherPotion(CrystalElement.GRAY, Potion.blindness);
		addNetherPotion(CrystalElement.GREEN, Potion.poison);
		addNetherPotion(CrystalElement.LIGHTBLUE, Potion.moveSlowdown);
		addNetherPotion(CrystalElement.LIGHTGRAY, Potion.weakness);
		addNetherPotion(CrystalElement.YELLOW, Potion.digSlowdown);
		addNetherPotion(CrystalElement.WHITE, Potion.invisibility);
		addNetherPotion(CrystalElement.BROWN, Potion.confusion);
		addNetherPotion(CrystalElement.BLUE, Potion.nightVision);
		addNetherPotion(CrystalElement.PINK, Potion.damageBoost);
		addNetherPotion(CrystalElement.MAGENTA, Potion.regeneration);

		addAspect(CrystalElement.BLACK, Aspect.BEAST, Aspect.DARKNESS, Aspect.MIND, Aspect.SENSES);
		addAspect(CrystalElement.BLUE, Aspect.SENSES, Aspect.LIGHT);
		addAspect(CrystalElement.BROWN, Aspect.HUNGER);
		addAspect(CrystalElement.CYAN, Aspect.WATER);
		addAspect(CrystalElement.GRAY, Aspect.WEAPON);
		addAspect(CrystalElement.GREEN, Aspect.POISON, Aspect.BEAST);
		addAspect(CrystalElement.LIGHTBLUE, Aspect.MOTION, Aspect.TRAVEL);
		addAspect(CrystalElement.LIGHTGRAY, Aspect.MOTION);
		addAspect(CrystalElement.LIME, Aspect.FLIGHT);
		addAspect(CrystalElement.MAGENTA, Aspect.HEAL, Aspect.LIFE);
		addAspect(CrystalElement.ORANGE, Aspect.FIRE);
		addAspect(CrystalElement.PINK, Aspect.WEAPON);
		addAspect(CrystalElement.PURPLE, Aspect.MAGIC, Aspect.CRAFT, Aspect.TOOL, Aspect.EXCHANGE);
		addAspect(CrystalElement.RED, Aspect.ARMOR, Aspect.WEAPON);
		addAspect(CrystalElement.WHITE, Aspect.VOID, Aspect.AIR);
		addAspect(CrystalElement.YELLOW, Aspect.MINE, Aspect.TOOL);
	}

	private static void addAspect(CrystalElement color, Aspect... asps) {
		ArrayList li = new ArrayList();
		for (int i = 0; i < asps.length; i++)
			li.add(asps[i]);
		aspects.put(color, li);
	}

	public static ArrayList<Aspect> getAspects(CrystalElement color) {
		ArrayList li = new ArrayList();
		ArrayList l2 = aspects.get(color);
		if (l2 != null)
			li.addAll(l2);
		return li;
	}

	private static void addColorPotion(CrystalElement color, Potion pot) {
		map.put(color, pot);
	}

	private static void addNetherPotion(CrystalElement color, Potion pot) {
		nethermap.put(color, pot);
	}

	public static PotionEffect getEffectFromColor(CrystalElement color, int dura, int level) {
		Potion pot = map.get(color);
		if (pot == null)
			return null;
		return new PotionEffect(pot.id, dura, level);
	}

	public static PotionEffect getNetherEffectFromColor(CrystalElement color, int dura, int level) {
		Potion pot = nethermap.get(color);
		if (pot == null)
			return null;
		return new PotionEffect(pot.id, dura, level);
	}

	public static String getPotionName(CrystalElement color) {
		if (color == CrystalElement.BLACK)
			return "corrupting";
		if (color == CrystalElement.BROWN)
			return "lengthening";
		if (color == CrystalElement.PURPLE)
			return "enhancing";
		Potion pot = map.get(color);
		if (pot == null)
			return "";
		return StatCollector.translateToLocal(pot.getName());
	}

	public static boolean requiresCustomPotion(CrystalElement color) {
		if (color == CrystalElement.CYAN)
			return true;
		if (color == CrystalElement.YELLOW)
			return true;
		if (color == CrystalElement.LIME)
			return true;
		if (color == CrystalElement.RED)
			return true;
		return false;
	}

	public static boolean isCorruptedPotion(CrystalElement color) {
		if (color == CrystalElement.GRAY)
			return true;
		if (color == CrystalElement.LIGHTGRAY)
			return true;
		if (color == CrystalElement.WHITE)
			return true;
		return false;
	}

	public static boolean isPotionModifier(CrystalElement color) {
		if (color == CrystalElement.BLACK)
			return true;
		if (color == CrystalElement.BROWN)
			return true;
		if (color == CrystalElement.PURPLE)
			return true;
		return false;
	}

	public static String getEffectName(CrystalElement color) {
		if (color == CrystalElement.BLACK)
			return "Confuses Mobs";
		if (color == CrystalElement.PURPLE)
			return "Gives XP";
		if (color == CrystalElement.WHITE)
			return "Clears Effects";
		return StatCollector.translateToLocal(map.get(color).getName());
	}

	public static String getNetherEffectName(CrystalElement color) {
		if (color == CrystalElement.BROWN)
			return "Nausea";
		if (color == CrystalElement.PURPLE)
			return "Takes XP";
		if (color == CrystalElement.ORANGE)
			return "Fire Damage";
		if (color == CrystalElement.LIME)
			return "Jump Disability";
		if (color == CrystalElement.RED)
			return "Direct Damage";
		return StatCollector.translateToLocal(nethermap.get(color).getName());
	}
}

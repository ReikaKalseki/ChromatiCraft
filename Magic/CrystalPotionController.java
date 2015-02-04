/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import java.util.HashMap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ExtraUtilsHandler;

public class CrystalPotionController {

	private static HashMap<CrystalElement, Potion> map = new HashMap();
	private static HashMap<CrystalElement, Potion> nethermap = new HashMap();

	static {
		addColorPotion(CrystalElement.BLUE, Potion.nightVision);
		addColorPotion(CrystalElement.CYAN, Potion.waterBreathing);
		addColorPotion(CrystalElement.GRAY, Potion.moveSlowdown);
		addColorPotion(CrystalElement.GREEN, Potion.poison); //change
		addColorPotion(CrystalElement.LIGHTBLUE, Potion.moveSpeed);
		addColorPotion(CrystalElement.LIGHTGRAY, Potion.weakness);
		addColorPotion(CrystalElement.LIME, Potion.jump);
		addColorPotion(CrystalElement.MAGENTA, Potion.regeneration);
		addColorPotion(CrystalElement.RED, Potion.resistance);
		addColorPotion(CrystalElement.ORANGE, Potion.fireResistance);
		addColorPotion(CrystalElement.PINK, Potion.damageBoost);
		addColorPotion(CrystalElement.YELLOW, Potion.digSpeed);
		addColorPotion(CrystalElement.WHITE, Potion.invisibility);
		addColorPotion(CrystalElement.BROWN, ChromatiCraft.betterSat);

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
	}

	private static void addColorPotion(CrystalElement color, Potion pot) {
		map.put(color, pot);
	}

	private static void addNetherPotion(CrystalElement color, Potion pot) {
		nethermap.put(color, pot);
	}

	public static boolean shouldBeHostile(World world) {
		if (world.provider.dimensionId == ExtraUtilsHandler.getInstance().darkID)
			return true;
		return world.provider.isHellWorld;
	}

	public static boolean isPotionAllowed(PotionEffect eff, EntityLivingBase e) {
		if (eff == null)
			return false;
		Potion pot = Potion.potionTypes[eff.getPotionID()];
		PotionEffect has = e.getActivePotionEffect(pot);
		if (has != null) {
			if (has.getAmplifier() > eff.getAmplifier())
				return false;
			if (has.getDuration() > eff.getDuration())
				return false;
		}
		if (!(e instanceof EntityPlayer)) {
			return e.worldObj.provider.isHellWorld ? !ReikaPotionHelper.isBadEffect(pot) : true;
		}
		if (e.worldObj.provider.isHellWorld)
			return eff.getPotionID() == Potion.nightVision.id || ReikaPotionHelper.isBadEffect(pot);
		if (e.worldObj.provider.dimensionId == 1)
			return true;
		return !ReikaPotionHelper.isBadEffect(pot);
	}

	public static boolean isBadPotion(CrystalElement e) {
		Potion pot = map.get(e);
		return pot != null && ReikaPotionHelper.isBadEffect(pot);
	}

	public static PotionEffect getEffectFromColor(CrystalElement color, int dura, int level) {
		Potion pot = map.get(color);
		if (pot == null)
			return null;
		return new PotionEffect(pot.id, dura, level, true);
	}

	public static PotionEffect getNetherEffectFromColor(CrystalElement color, int dura, int level) {
		Potion pot = nethermap.get(color);
		if (pot == null)
			return null;
		return new PotionEffect(pot.id, dura, level, true);
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
			return "[None]";
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

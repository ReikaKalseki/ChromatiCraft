/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Items.Tools.ItemPurifyCrystal;
import Reika.ChromatiCraft.ModInterface.MystPages;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumIDHandler;

public class CrystalPotionController {

	private static final EnumMap<CrystalElement, Potion> map = new EnumMap(CrystalElement.class);
	private static final EnumMap<CrystalElement, Potion> nethermap = new EnumMap(CrystalElement.class);

	private static final HashSet<Integer> ignoredPotions = new HashSet();

	private static final Random rand = new Random();

	static {
		addColorPotion(CrystalElement.BLUE, Potion.nightVision);
		addColorPotion(CrystalElement.CYAN, Potion.waterBreathing);
		addColorPotion(CrystalElement.GRAY, Potion.moveSlowdown);
		addColorPotion(CrystalElement.GREEN, Potion.poison); //change
		addColorPotion(CrystalElement.LIGHTBLUE, Potion.moveSpeed);
		addColorPotion(CrystalElement.LIGHTGRAY, Potion.weakness);
		addColorPotion(CrystalElement.LIME, Potion.jump);
		addColorPotion(CrystalElement.MAGENTA, ChromatiCraft.betterRegen);
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
		addNetherPotion(CrystalElement.MAGENTA, ChromatiCraft.betterRegen);
	}

	private static void addColorPotion(CrystalElement color, Potion pot) {
		map.put(color, pot);
	}

	private static void addNetherPotion(CrystalElement color, Potion pot) {
		nethermap.put(color, pot);
	}

	public static boolean shouldBeHostile(EntityLivingBase e, World world) {
		if (e instanceof EntityPlayer && ItemPurifyCrystal.isActive((EntityPlayer)e))
			return false;
		return isWorldHostile(world);
	}

	public static boolean isWorldHostile(World world) {
		if (ModList.EXTRAUTILS.isLoaded() && ExtraUtilsHandler.getInstance().initializedProperly() && world.provider.dimensionId == ExtraUtilsHandler.getInstance().darkID)
			return true;
		if (ModList.THAUMCRAFT.isLoaded() && ThaumIDHandler.getInstance().initializedProperly() && world.provider.dimensionId == ThaumIDHandler.getInstance().dimensionID)
			return true;
		if (ModList.MYSTCRAFT.isLoaded() && MystPages.Pages.HOSTILE.existsInWorld(world))
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
		if (e instanceof EntityPlayer && ItemPurifyCrystal.isActive((EntityPlayer)e))
			return !ReikaPotionHelper.isBadEffect(pot);
		if (!(e instanceof EntityPlayer)) {
			boolean flag = false;
			if (e instanceof EntityCreature) {
				flag = ((EntityCreature)e).getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
			}
			return shouldBeHostile(e, e.worldObj) ? ReikaPotionHelper.isBadEffect(pot) == flag : true;
		}
		if (shouldBeHostile(e, e.worldObj))
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
		if (color == CrystalElement.PURPLE)
			return "enhancing";
		Potion pot = map.get(color);
		if (pot == null && color == CrystalElement.BROWN)
			return "lengthening";
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
		if (color == CrystalElement.BROWN)
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

	public static Set<Integer> ignoredBadPotionsForLevelZero() {
		return Collections.unmodifiableSet(ignoredPotions);
	}

	public static void addIgnoredPotion(Potion p) {
		ignoredPotions.add(p.id);
	}

	public static void applyEffectFromColor(int dura, int level, EntityLivingBase e, CrystalElement color, boolean doFX) {
		if (CrystalPotionController.shouldBeHostile(e, e.worldObj)) {
			switch(color) {
				case ORANGE:
					e.setFire(2);
					break;
				case RED:
					e.attackEntityFrom(DamageSource.magic, 1);
					break;
				case PURPLE:
					if (!e.worldObj.isRemote && rand.nextInt(5) == 0 && e instanceof EntityPlayer) {
						EntityPlayer ep = (EntityPlayer)e;
						if (ep.experienceLevel > 0) {
							ep.addExperienceLevel(-1);
						}
						else {
							ep.experienceTotal = 0;
							ep.experience = 0;
						}
					}
					break;
				case BROWN:
					if (!e.isPotionActive(Potion.confusion.id))
						addPotionEffect(e, new PotionEffect(Potion.confusion.id, Math.max(100, (int)(dura*1.8)), level, true));
					break;
				case LIME:
					addPotionEffect(e, new PotionEffect(Potion.jump.id, dura, -5, true));
					break;
				default:
					PotionEffect eff = CrystalPotionController.getNetherEffectFromColor(color, dura, level);
					if (CrystalPotionController.isPotionAllowed(eff, e))
						addPotionEffect(e, eff);
			}
		}
		else {
			switch(color) {
				case BLACK:
					if (e instanceof EntityMob) {  //clear AI
						EntityMob m = (EntityMob)e;
						m.setAttackTarget(null);
						m.getNavigator().clearPathEntity();
					}
					break;
				case WHITE:
					//ReikaPotionHelper.clearPotionsExceptPerma(e);
					ReikaPotionHelper.clearBadPotions(e, level > 0 ? null : CrystalPotionController.ignoredBadPotionsForLevelZero());
					break;
				case PURPLE:
					if (e instanceof EntityPlayer && !e.worldObj.isRemote && (level > 0 || rand.nextInt(2) == 0)) {
						EntityPlayer ep = (EntityPlayer)e;
						if (doFX)
							ep.worldObj.playSoundAtEntity(ep, "random.orb", 0.2F, rand.nextFloat()*2);
						ep.addExperience(1);
					}
					break;
				default:
					PotionEffect eff = CrystalPotionController.getEffectFromColor(color, dura, level);
					if (eff != null) {
						if (CrystalPotionController.isPotionAllowed(eff, e)) {
							addPotionEffect(e, eff);
						}
					}
			}
		}
	}

	private static void addPotionEffect(EntityLivingBase e, PotionEffect eff) {
		PotionEffect cur = e.getActivePotionEffect(Potion.potionTypes[eff.getPotionID()]);
		if (e instanceof EntityPlayer || cur == null || cur.getAmplifier() < eff.getAmplifier() || cur.getDuration() < 20 || eff.getDuration() < 80)
			e.addPotionEffect(eff);
	}
}

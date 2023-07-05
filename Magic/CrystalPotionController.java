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
import net.minecraft.entity.EntityLiving;
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
import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;
import Reika.ChromatiCraft.API.CrystalPotionAPI;
import Reika.ChromatiCraft.Items.Tools.ItemPendant;
import Reika.ChromatiCraft.Items.Tools.Powered.ItemPurifyCrystal;
import Reika.ChromatiCraft.ModInterface.MystPages;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumIDHandler;

public class CrystalPotionController implements CrystalPotionAPI {

	public static final CrystalPotionController instance = new CrystalPotionController();

	private final EnumMap<CrystalElement, Potion> potions = new EnumMap(CrystalElement.class);
	private final EnumMap<CrystalElement, Potion> potionsNether = new EnumMap(CrystalElement.class);

	private final HashSet<Integer> ignoredPotions = new HashSet();

	private final Random rand = new Random();

	private CrystalPotionController() {
		this.addColorPotion(CrystalElement.BLUE, Potion.nightVision);
		this.addColorPotion(CrystalElement.CYAN, Potion.waterBreathing);
		this.addColorPotion(CrystalElement.GRAY, Potion.moveSlowdown);
		this.addColorPotion(CrystalElement.GREEN, Potion.poison); //change
		this.addColorPotion(CrystalElement.LIGHTBLUE, Potion.moveSpeed);
		this.addColorPotion(CrystalElement.LIGHTGRAY, Potion.weakness);
		this.addColorPotion(CrystalElement.LIME, Potion.jump);
		this.addColorPotion(CrystalElement.MAGENTA, ChromatiCraft.betterRegen);
		this.addColorPotion(CrystalElement.RED, Potion.resistance);
		this.addColorPotion(CrystalElement.ORANGE, Potion.fireResistance);
		this.addColorPotion(CrystalElement.PINK, Potion.damageBoost);
		this.addColorPotion(CrystalElement.YELLOW, Potion.digSpeed);
		//this.addColorPotion(CrystalElement.WHITE, Potion.invisibility);
		this.addColorPotion(CrystalElement.BROWN, ChromatiCraft.betterSat);

		this.addNetherPotion(CrystalElement.BLACK, Potion.wither);
		this.addNetherPotion(CrystalElement.CYAN, Potion.hunger);
		this.addNetherPotion(CrystalElement.GRAY, Potion.blindness);
		this.addNetherPotion(CrystalElement.GREEN, Potion.poison);
		this.addNetherPotion(CrystalElement.LIGHTBLUE, Potion.moveSlowdown);
		this.addNetherPotion(CrystalElement.LIGHTGRAY, Potion.weakness);
		this.addNetherPotion(CrystalElement.YELLOW, Potion.digSlowdown);
		this.addNetherPotion(CrystalElement.WHITE, Potion.invisibility);
		this.addNetherPotion(CrystalElement.BROWN, Potion.confusion);
		this.addNetherPotion(CrystalElement.BLUE, Potion.nightVision);
		this.addNetherPotion(CrystalElement.PINK, Potion.damageBoost);
		this.addNetherPotion(CrystalElement.MAGENTA, ChromatiCraft.betterRegen);
	}

	private void addColorPotion(CrystalElement color, Potion pot) {
		potions.put(color, pot);
	}

	private void addNetherPotion(CrystalElement color, Potion pot) {
		potionsNether.put(color, pot);
	}

	public boolean shouldBeHostile(EntityLivingBase e, World world) {
		if (e instanceof EntityPlayer && ItemPurifyCrystal.isActive((EntityPlayer)e))
			return false;
		return this.isWorldHostile(world);
	}

	public boolean isWorldHostile(World world) {
		if (ModList.EXTRAUTILS.isLoaded() && ExtraUtilsHandler.getInstance().initializedProperly() && world.provider.dimensionId == ExtraUtilsHandler.getInstance().darkID)
			return true;
		if (ModList.THAUMCRAFT.isLoaded() && ThaumIDHandler.getInstance().initializedProperly() && world.provider.dimensionId == ThaumIDHandler.getInstance().dimensionID)
			return true;
		if (ModList.MYSTCRAFT.isLoaded() && MystPages.Pages.HOSTILE.existsInWorld(world))
			return true;
		return world.provider.isHellWorld;
	}

	public boolean isPotionAllowed(PotionEffect eff, EntityLivingBase e) {
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
			return this.shouldBeHostile(e, e.worldObj) ? ReikaPotionHelper.isBadEffect(pot) == flag : true;
		}
		if (this.shouldBeHostile(e, e.worldObj))
			return eff.getPotionID() == Potion.nightVision.id || ReikaPotionHelper.isBadEffect(pot);
		if (e.worldObj.provider.dimensionId == 1)
			return true;
		return !ReikaPotionHelper.isBadEffect(pot);
	}

	public boolean isBadPotion(CrystalElement e) {
		Potion pot = potions.get(e);
		return pot != null && ReikaPotionHelper.isBadEffect(pot);
	}

	public PotionEffect getEffectFromColor(CrystalElement color, int dura, int level, boolean evil) {
		EnumMap<CrystalElement, Potion> map = evil ? potionsNether : potions;
		Potion pot = map.get(color);
		if (pot == null)
			return null;
		return new PotionEffect(pot.id, dura, level, true);
	}

	public String getPotionName(CrystalElement color) {
		if (color == CrystalElement.BLACK)
			return "corrupting";
		if (color == CrystalElement.PURPLE)
			return "enhancing";
		Potion pot = potions.get(color);
		if (pot == null && color == CrystalElement.BROWN)
			return "lengthening";
		if (pot == null)
			return "[None]";
		return StatCollector.translateToLocal(pot.getName());
	}

	public boolean requiresCustomPotion(CrystalElement color) {
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

	public boolean isCorruptedPotion(CrystalElement color) {
		if (color == CrystalElement.GRAY)
			return true;
		if (color == CrystalElement.LIGHTGRAY)
			return true;
		if (color == CrystalElement.WHITE)
			return true;
		return false;
	}

	public boolean isPotionModifier(CrystalElement color) {
		if (color == CrystalElement.BLACK)
			return true;
		if (color == CrystalElement.PURPLE)
			return true;
		return false;
	}

	public String getEffectName(CrystalElement color, boolean boost) {
		if (color == CrystalElement.BLACK)
			return "Confuses Mobs";
		if (color == CrystalElement.PURPLE)
			return "Gives XP";
		if (color == CrystalElement.BROWN && !boost)
			return "Prevents Starvation";
		if (color == CrystalElement.ORANGE && boost)
			return "Protects from Extreme Heat";
		if (color == CrystalElement.WHITE)
			return boost ? "Clears All Negative Effects" : "Clears Most Negative Effects";
		return StatCollector.translateToLocal(potions.get(color).getName());
	}

	public String getNetherEffectName(CrystalElement color) {
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
		return StatCollector.translateToLocal(potionsNether.get(color).getName());
	}

	public Set<Integer> ignoredBadPotionsForLevelZero() {
		return Collections.unmodifiableSet(ignoredPotions);
	}

	public void addBadPotionForIgnore(Potion p) {
		ignoredPotions.add(p.id);
	}

	public void applyEffectFromColor(int dura, int level, EntityLivingBase e, CrystalElement color, boolean doFX) {
		this.applyEffectFromColor(dura, level, e, color, doFX, false, false);
	}

	public void applyEffectFromColor(int dura, int level, EntityLivingBase e, CrystalElement color, boolean doFX, boolean forceGood, boolean forceBad) {
		if (forceBad || (!forceGood && this.shouldBeHostile(e, e.worldObj))) {
			switch(color) {
				case ORANGE:
					e.setFire(2);
					break;
				case RED:
					e.attackEntityFrom(DamageSource.magic, 1);
					break;
				case PURPLE:
					if (!e.worldObj.isRemote) {
						if (e instanceof EntityPlayer) {
							if (rand.nextInt(5) == 0) {
								EntityPlayer ep = (EntityPlayer)e;
								if (ep.experienceLevel > 0) {
									ep.addExperienceLevel(-1);
								}
								else {
									ep.experienceTotal = 0;
									ep.experience = 0;
								}
							}
						}
						else if (e instanceof EntityLiving) {
							((EntityLiving)e).experienceValue++;
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
					PotionEffect eff = this.getEffectFromColor(color, dura, level, true);
					if (forceBad || this.isPotionAllowed(eff, e))
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
					ReikaPotionHelper.clearBadPotions(e, level > 0 ? null : this.ignoredBadPotionsForLevelZero());
					break;
				case PURPLE:
					if (e instanceof EntityPlayer && !e.worldObj.isRemote && (level > 0 || rand.nextInt(2) == 0)) {
						EntityPlayer ep = (EntityPlayer)e;
						if (doFX)
							ep.worldObj.playSoundAtEntity(ep, "random.orb", 0.2F, rand.nextFloat()*2);
						int amt = 1;

						int lvl = ItemPendant.getActivePendantLevel(ep, CrystalElement.PURPLE);
						if (lvl == 1) {
							amt *= 3;
						}
						else if (lvl == 0) {
							amt *= 2;
						}
						ep.addExperience(amt);
					}
					break;
				default:
					PotionEffect eff = this.getEffectFromColor(color, dura, level, false);
					if (eff != null) {
						if (forceGood || this.isPotionAllowed(eff, e)) {
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

	@Override
	public PotionEffect getEffectFromColor(CrystalElementProxy color, int dura, int level, boolean evil) {
		return this.getEffectFromColor(CrystalElement.elements[color.ordinal()], dura, level, evil);
	}
}

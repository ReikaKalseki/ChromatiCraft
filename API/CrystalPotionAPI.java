/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;


public interface CrystalPotionAPI {

	/** Whether a world is "evil" like the Nether, thus corrupting the effect of the crystals. */
	public boolean isWorldHostile(World world);

	/** Whether crystals can even apply this potion effect to this entity. */
	public boolean isPotionAllowed(PotionEffect eff, EntityLivingBase e);

	/** Registers a potion to the ignore list for T1 white pendants. */
	public void addBadPotionForIgnore(Potion p);

	/** Fetches the ignore list for T1 white pendants. */
	public Set<Integer> ignoredBadPotionsForLevelZero();

	/** Constructs a potion effect for the given color, with a specified duration and amplifier, optionally corrupting it. */
	public PotionEffect getEffectFromColor(CrystalElementProxy color, int dura, int level, boolean evil);

}

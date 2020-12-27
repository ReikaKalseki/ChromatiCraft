/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.util.Locale;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentAggroMask;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentAirMiner;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentAutoCollect;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentBetterEfficiency;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentBossKill;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentDataKeeper;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentEnderLock;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentFastSinking;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentHarvestLevel;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentPhasingSequence;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentRareLoot;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentUseRepair;
import Reika.ChromatiCraft.Magic.Enchantment.EnchantmentWeaponAOE;
import Reika.DragonAPI.Interfaces.Registry.EnchantmentEnum;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;


public enum ChromaEnchants implements EnchantmentEnum {

	WEAPONAOE(EnchantmentWeaponAOE.class, ExtraChromaIDs.WEAPONAOEID),
	AGGROMASK(EnchantmentAggroMask.class, ExtraChromaIDs.AGGROMASKID),
	ENDERLOCK(EnchantmentEnderLock.class, ExtraChromaIDs.ENDERLOCKID),
	USEREPAIR(EnchantmentUseRepair.class, ExtraChromaIDs.USEREPAIRID),
	RARELOOT(EnchantmentRareLoot.class, ExtraChromaIDs.RARELOOTID),
	FASTSINK(EnchantmentFastSinking.class, ExtraChromaIDs.FASTSINKID),
	HARVESTLEVEL(EnchantmentHarvestLevel.class, ExtraChromaIDs.HARVESTLEVELID),
	AIRMINER(EnchantmentAirMiner.class, ExtraChromaIDs.AIRMINERID),
	PHASING(EnchantmentPhasingSequence.class, ExtraChromaIDs.PHASINGID),
	BOSSKILL(EnchantmentBossKill.class, ExtraChromaIDs.BOSSKILLID),
	AUTOCOLLECT(EnchantmentAutoCollect.class, ExtraChromaIDs.AUTOCOLLECTID),
	DATAKEEP(EnchantmentDataKeeper.class, ExtraChromaIDs.DATAKEEPERID),
	MINETIME(EnchantmentBetterEfficiency.class, ExtraChromaIDs.MINETIMEID);

	private final Class enchantmentClass;
	private final ExtraChromaIDs enchantmentID;

	public static final String OVERENCHANT_TAG = "cc_overenchant";

	public static final ChromaEnchants[] enchantmentList = values();

	private ChromaEnchants(Class<? extends Enchantment> c, ExtraChromaIDs id) {
		enchantmentClass = c;
		enchantmentID = id;
	}

	@Override
	public String getBasicName() {
		return StatCollector.translateToLocal(this.getUnlocalizedName());
	}

	@Override
	public boolean isDummiedOut() {
		return false;
	}

	@Override
	public Class getObjectClass() {
		return enchantmentClass;
	}

	@Override
	public String getUnlocalizedName() {
		return "chroma."+this.name().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public Enchantment getEnchantment() {
		return ChromatiCraft.enchants[this.ordinal()];
	}

	@Override
	public int getEnchantmentID() {
		return enchantmentID.getValue();
	}

	public int getLevel(ItemStack tool) {
		if (tool.stackTagCompound == null)
			return 0;
		Enchantment e = this.getEnchantment();
		int lim = e.getMaxLevel()-1;
		int has = ReikaEnchantmentHelper.getEnchantmentLevel(this.getEnchantment(), tool);
		if (has > lim) {
			tool.stackTagCompound.setBoolean(OVERENCHANT_TAG, true);
			return lim;
		}
		else {
			tool.stackTagCompound.setBoolean(OVERENCHANT_TAG, false);
		}
		return has;
	}

}

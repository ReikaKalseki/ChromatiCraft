/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipeList;

import Reika.ChromatiCraft.Magic.Artefact.UATrades;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;


public class VillageTradeHandler implements IVillageTradeHandler {

	public static final VillageTradeHandler instance = new VillageTradeHandler();

	private static final String NBT_KEY = "tradeChanceCache";

	private VillageTradeHandler() {

	}

	@Override
	public void manipulateTradesForVillager(EntityVillager ev, MerchantRecipeList li, Random rand) {
		if (ev.buyingList == null)
			ev.buyingList = new MerchantRecipeList();
		if (this.withRandomChance(ev, 0.25, "FocusTrade") && !this.hasFocusTrade(ev.buyingList)) {
			ev.buyingList.add(new FocusCrystalTrade()); //add an unlocked trade
		}
		if (this.withRandomChance(ev, 0.25, "FragmentTrade") && !this.hasFragmentTrade(ev.buyingList)) {
			ev.buyingList.add(new FragmentTrade()); //add an unlocked trade
		}
		UATrades.instance.addTradesToVillager(ev, li, rand);
	}

	public boolean withRandomChance(EntityVillager ev, double c, String key) {
		NBTTagCompound tag = ev.getEntityData().getCompoundTag(NBT_KEY);
		if (tag.getBoolean(key))
			return false;
		tag.setBoolean(key, true);
		ev.getEntityData().setTag(NBT_KEY, tag);
		return ReikaRandomHelper.doWithChance(c);
	}

	private boolean hasFocusTrade(MerchantRecipeList li) {
		for (Object r : li) {
			if (r instanceof FocusCrystalTrade)
				return true;
		}
		return false;
	}

	private boolean hasFragmentTrade(MerchantRecipeList li) {
		for (Object r : li) {
			if (r instanceof FragmentTrade)
				return true;
		}
		return false;
	}

}

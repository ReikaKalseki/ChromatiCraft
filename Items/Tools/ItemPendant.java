/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Base.ItemCrystalBasic;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;

public class ItemPendant extends ItemCrystalBasic {

	public ItemPendant(int tex) {
		super(tex);
		hasSubtypes = true;
		maxStackSize = 1;
		this.setNoRepair();
	}

	@Override
	protected final CreativeTabs getCreativePage() {
		return ChromatiCraft.tabChromaTools;
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int par4, boolean par5) {
		int level = this.isEnhanced() ? 2 : 0;
		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer) e;
			ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(is.getItemDamage());
			if (dye != ReikaDyeHelper.PURPLE) {
				int dura = dye == ReikaDyeHelper.BLUE ? 3 : 100;
				PotionEffect pot = CrystalPotionController.getEffectFromColor(dye, dura, level);
				if (pot == null || dye == ReikaDyeHelper.BLUE || !ep.isPotionActive(pot.getPotionID()))
					CrystalBlock.applyEffectFromColor(dura, level, ep, dye);
			}
			if (ChromaOptions.NOPARTICLES.getState())
				ReikaEntityHelper.setNoPotionParticles(ep);
		}
	}

	public boolean isEnhanced() {
		return this == ChromaItems.PENDANT3.getItemInstance();
	}

}

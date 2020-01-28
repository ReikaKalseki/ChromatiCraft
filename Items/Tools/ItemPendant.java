/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
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
import Reika.ChromatiCraft.Base.ItemCrystalBasic;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;

public class ItemPendant extends ItemCrystalBasic {

	private static final String TAG = "last_kuropend";
	private static final String TAG2 = "last_kuropend2";

	private static final String TAGb = "last_zambpend";
	private static final String TAG2b = "last_zambpend2";

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
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean selected) {
		int level = this.isEnhanced() ? 2 : 0;
		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer) e;
			CrystalElement color = CrystalElement.elements[is.getItemDamage()];
			if (color == CrystalElement.BLACK) {
				e.getEntityData().setLong(this.isEnhanced() ? TAG2 : TAG, world.getTotalWorldTime());
			}
			else if (color == CrystalElement.PURPLE) {
				e.getEntityData().setLong(this.isEnhanced() ? TAG2b : TAGb, world.getTotalWorldTime());
			}
			else {
				int dura = this.isEnhanced() ? 6000 : color == CrystalElement.BLUE ? 3 : 100;
				PotionEffect pot = CrystalPotionController.getEffectFromColor(color, dura, level);
				if (pot == null || color == CrystalElement.BLUE || !ep.isPotionActive(pot.getPotionID())) {
					CrystalPotionController.applyEffectFromColor(dura, level, ep, color, true);
				}
			}
			if (ChromaOptions.NOPARTICLES.getState())
				ReikaEntityHelper.setNoPotionParticles(ep);
		}
	}

	public static boolean isKuroPendantActive(EntityPlayer ep) {
		return ep.worldObj.getTotalWorldTime()-ep.getEntityData().getLong(TAG) < 20;
	}

	public static boolean isEnhancedKuroPendantActive(EntityPlayer ep) {
		return ep.worldObj.getTotalWorldTime()-ep.getEntityData().getLong(TAG2) < 20;
	}

	public static boolean isZambarauPendantActive(EntityPlayer ep) {
		return ep.worldObj.getTotalWorldTime()-ep.getEntityData().getLong(TAGb) < 20;
	}

	public static boolean isEnhancedZambarauPendantActive(EntityPlayer ep) {
		return ep.worldObj.getTotalWorldTime()-ep.getEntityData().getLong(TAG2b) < 20;
	}

	public boolean isEnhanced() {
		return this == ChromaItems.PENDANT3.getItemInstance();
	}

}

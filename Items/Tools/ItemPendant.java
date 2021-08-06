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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemCrystalBasic;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;

public class ItemPendant extends ItemCrystalBasic {

	private static final String ROOT_KEY = "pendant_status";

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
	public final void onUpdate(ItemStack is, World world, Entity e, int slot, boolean selected) {
		if (this.isFunctional(is)) {
			int level = this.isEnhanced() ? 2 : 0;
			if (e instanceof EntityPlayer) {
				EntityPlayer ep = (EntityPlayer) e;
				CrystalElement color = CrystalElement.elements[is.getItemDamage()];
				long bits = world.getTotalWorldTime() & (~1);
				bits |= this.isEnhanced() ? 1 : 0;
				getRootStorage(e).setLong(color.name(), bits);
				if (color == CrystalElement.BLACK || color == CrystalElement.PURPLE) {

				}
				else {
					int dura = this.isEnhanced() ? 6000 : color == CrystalElement.BLUE ? 3 : 100;
					PotionEffect pot = CrystalPotionController.instance.getEffectFromColor(color, dura, level, false);
					if (pot == null || color == CrystalElement.BLUE) {
						CrystalPotionController.instance.applyEffectFromColor(dura, level, ep, color, true);
					}
				}
				if (ChromaOptions.NOPARTICLES.getState())
					ReikaEntityHelper.setNoPotionParticles(ep);
				if (!world.isRemote)
					this.onTick(is, world, ep, slot);
			}
		}
	}

	protected void onTick(ItemStack is, World world, EntityPlayer ep, int slot) {

	}

	protected boolean isFunctional(ItemStack is) {
		return true;
	}

	private static NBTTagCompound getRootStorage(Entity e) {
		if (!e.getEntityData().hasKey(ROOT_KEY)) {
			e.getEntityData().setTag(ROOT_KEY, new NBTTagCompound());
		}
		return e.getEntityData().getCompoundTag(ROOT_KEY);
	}

	public boolean isEnhanced() {
		return false;
	}

	/** 0 for basic, 1 for enhanced, -1 for none */
	public static int getActivePendantLevel(EntityPlayer ep, CrystalElement e) {
		long val = getRootStorage(ep).getLong(e.name());
		long dur = ep.worldObj.getTotalWorldTime()-val;
		return dur <= 20 ? (int)(dur & 1) : -1;
	}

}

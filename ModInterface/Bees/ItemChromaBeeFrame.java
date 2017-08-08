/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.Bees;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IHiveFrame;

@Strippable("forestry.api.apiculture.IHiveFrame")
public class ItemChromaBeeFrame extends ItemChromaTool implements IHiveFrame {

	private static final IBeeModifier modifier = new FrameModifier();

	public ItemChromaBeeFrame(int index) {
		super(index);
		this.setMaxDamage(240);
	}

	@Override
	public ItemStack frameUsed(IBeeHousing housing, ItemStack frame, IBee queen, int wear) {
		int lvl = ReikaEnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking, frame);
		if (lvl < 5) {
			int n = 1+lvl;
			if (itemRand.nextInt(n) == 0) {
				frame.setItemDamage(frame.getItemDamage()+wear);
				if (frame.getItemDamage() > frame.getMaxDamage())
					frame = null;
			}
		}
		return frame;
	}

	@Override
	public IBeeModifier getBeeModifier() {
		return modifier;
	}

	@Override
	public String getTexture(ItemStack is) {
		return "/Reika/ChromatiCraft/Textures/Items/items_resource.png";
	}

	private static class FrameModifier implements IBeeModifier {

		@Override
		public float getTerritoryModifier(IBeeGenome genome, float f) {
			return 1;
		}

		@Override
		public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float f) {
			return 1;
		}

		@Override
		public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate, float f) {
			return 0.1F;
		}

		@Override
		public float getProductionModifier(IBeeGenome genome, float f) {
			return 0;
		}

		@Override
		public float getFloweringModifier(IBeeGenome genome, float f) {
			return 0;
		}

		@Override
		public float getGeneticDecay(IBeeGenome genome, float f) {
			return 200;
		}

		@Override
		public boolean isSealed() {
			return false;
		}

		@Override
		public boolean isSelfLighted() {
			return false;
		}

		@Override
		public boolean isSunlightSimulated() {
			return false;
		}

		@Override
		public boolean isHellish() {
			return false;
		}

	}

}

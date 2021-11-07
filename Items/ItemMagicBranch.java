/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Base.ItemChromaMulti;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Interfaces.Item.AnimatedSpritesheet;


public class ItemMagicBranch extends ItemChromaMulti implements AnimatedSpritesheet {

	public ItemMagicBranch(int tex) {
		super(tex);
	}

	public static enum BranchTypes {

		SPEED(),
		RANGE(),
		POWER(),
		BONUS(),
		DAMAGE(),
		CRYSTAL();

		public static final BranchTypes[] list = values();

		public ItemStack getStack() {
			return ChromaItems.MAGICBRANCH.getStackOfMetadata(this.ordinal());
		}

	}

	@Override
	public boolean useAnimatedRender(ItemStack is) {
		return true;
	}

	@Override
	public int getFrameCount(ItemStack is) {
		return 16;
	}

	@Override
	public int getBaseRow(ItemStack is) {
		return is.getItemDamage();
	}

	@Override
	public int getColumn(ItemStack is) {
		return 0;
	}

	@Override
	public int getFrameOffset(ItemStack is) {
		return is.getItemDamage()*4;
	}

	@Override
	public int getFrameSpeed(ItemStack is) {
		return 4;
	}

	@Override
	public String getTexture(ItemStack is) {
		return this.useAnimatedRender(is) ? "/Reika/ChromatiCraft/Textures/Items/branches.png" : super.getTexture(is);
	}

	@Override
	public boolean verticalFrames() {
		return false;
	}

}

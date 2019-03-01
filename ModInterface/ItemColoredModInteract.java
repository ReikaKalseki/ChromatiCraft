/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Base.ItemChromaMulti;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;


public class ItemColoredModInteract extends ItemChromaMulti {

	public ItemColoredModInteract(int tex) {
		super(tex);
	}

	@Override
	public final String getTexture(ItemStack is) {
		return "/Reika/ChromatiCraft/Textures/Items/items_colormod.png";
	}

	public static enum ColoredModItems {
		COMB(ModList.FORESTRY);

		private final ModList mod;

		private static final ColoredModItems[] list = values();

		private ColoredModItems(ModList m) {
			mod = m;
		}

		public ItemStack getItem(CrystalElement e) {
			return ChromaItems.COLOREDMOD.getStackOfMetadata(this.ordinal()*16+e.ordinal());
		}

		public boolean isAvailable() {
			return mod.isLoaded();
		}
	}

}

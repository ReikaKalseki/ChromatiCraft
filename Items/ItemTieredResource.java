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
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.TieredItem;
import Reika.ChromatiCraft.Base.ItemChromaMulti;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre.TieredOres;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredPlant.TieredPlants;
import Reika.ChromatiCraft.Registry.ChromaItems;

public class ItemTieredResource extends ItemChromaMulti implements TieredItem {

	public ItemTieredResource(int tex) {
		super(tex);
	}

	@Override
	public int getNumberTypes() {
		return ChromaItems.TIERED.getNumberMetadatas();
	}

	@Override
	public ProgressStage getDiscoveryTier(ItemStack is) {
		switch(is.getItemDamage()) {
			case 0:
				return TieredOres.INFUSED.level;
			case 1:
				return TieredPlants.FLOWER.level;
			case 2:
				return TieredPlants.CAVE.level;
			case 3:
				return TieredOres.FOCAL.level;
			case 4:
				return TieredPlants.LILY.level;
			case 5:
				return TieredPlants.DESERT.level;
			case 6:
				return TieredOres.BINDING.level;
			case 7:
				return TieredPlants.BULB.level;
			case 8:
				return TieredOres.TELEPORT.level;
			case 9:
				return TieredOres.WATERY.level;
			case 10:
				return TieredOres.FIRAXITE.level;
			case 11:
				return TieredOres.LUMA.level;
			case 12:
				return TieredOres.ECHO.level;
			case 13:
				return ProgressStage.CAVERN;
			case 14:
				return ProgressStage.BURROW;
			case 15:
				return ProgressStage.OCEAN;
			case 16:
				return TieredOres.FIRESTONE.level;
			case 17:
				return TieredOres.THERMITE.level;
			case 18:
				return TieredOres.RESO.level;
			case 19:
				return TieredOres.SPACERIFT.level;
			case 20:
				return ProgressStage.DESERTSTRUCT;
			case 21:
				return TieredPlants.POD.level;
			case 22:
				return TieredPlants.ROOT.level;
			case 23:
				return TieredOres.RAINBOW.level;
			case 24:
				return TieredOres.AVOLITE.level;
			default:
				return null;
		}
	}

	@Override
	public boolean isTiered(ItemStack is) {
		if (is.getItemDamage() <= 16)
			return true;
		switch(is.getItemDamage()) {

			default:
				return true;
		}
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.NEI;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEIChromaConfig implements IConfigureNEI {

	private static final CrystalBrewerHandler crystal = new CrystalBrewerHandler();
	private static final CastingTableHandler casting = new CastingTableHandler();

	private static final ChromaNEITabOccluder occlusion = new ChromaNEITabOccluder();

	@Override
	public void loadConfig() {
		ChromatiCraft.logger.log("Loading NEI Compatibility!");

		API.registerNEIGuiHandler(occlusion);

		API.registerRecipeHandler(crystal);
		API.registerUsageHandler(crystal);

		API.registerRecipeHandler(casting);
		API.registerUsageHandler(casting);

		ChromatiCraft.logger.log("Hiding technical blocks from NEI!");
		for (int i = 0; i < ChromaBlocks.blockList.length; i++) {
			ChromaBlocks b = ChromaBlocks.blockList[i];
			if (b.isTechnical())
				this.hideBlock(b.getBlockInstance());
		}

		if (ChromatiCraft.instance.isLocked()) {
			for (int i = 0; i < ChromaItems.itemList.length; i++) {
				ChromaItems ir = ChromaItems.itemList[i];
				API.hideItem(new ItemStack(ir.getItemInstance()));
			}
			for (int i = 0; i < ChromaBlocks.blockList.length; i++) {
				ChromaBlocks b = ChromaBlocks.blockList[i];
				this.hideBlock(b.getBlockInstance());
			}
		}
	}

	private void hideBlock(Block b) {
		API.hideItem(new ItemStack(b));
	}

	@Override
	public String getName() {
		return "ChromatiCraft NEI Handlers";
	}

	@Override
	public String getVersion() {
		return "Gamma";
	}

}

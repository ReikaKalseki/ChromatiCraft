/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Artefact;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Items.ItemUnknownArtefact.ArtefactTypes;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Interfaces.PlayerSpecificTrade;


public class UATrade extends MerchantRecipe implements PlayerSpecificTrade {

	public UATrade() {
		super(ChromaItems.ARTEFACT.getStackOfMetadata(ArtefactTypes.ARTIFACT.ordinal()), new ItemStack(Items.emerald, 64, 0));
	}

	@Override
	public void incrementToolUses() {
		//No-op to prevent expiry
	}

	@Override
	public boolean isValid(EntityPlayer ep) {
		return ProgressionManager.ProgressStage.ARTEFACT.isPlayerAtStage(ep);
	}

}

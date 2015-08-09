/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import java.util.HashSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ResearchFetcher {

	public static ResearchRegistry researchManager;
	public static ProgressRegistry progressManager;

	public static interface ResearchRegistry extends ProgressManager {

		public boolean lexiconHasFragment(ItemStack book, String key);

		public String getResearchLevelForPlayer(EntityPlayer ep);

	}

	public static interface ProgressRegistry extends ProgressManager {

		public boolean canPlayerStepTo(EntityPlayer ep, String key);

		public boolean playerDiscoveredElement(EntityPlayer ep, CrystalElementProxy e);

	}

	public static interface ProgressManager {

		public boolean playerHasResearch(EntityPlayer ep, String key);

		public HashSet<String> getPrerequisites(String key);

		public HashSet<String> getAllResearches();

	}

}

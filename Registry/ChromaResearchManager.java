/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StatCollector;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.DragonAPI.Instantiable.Data.SequenceMap;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

public final class ChromaResearchManager {

	/** For hard links */
	private final SequenceMap<ChromaResearch> data = new SequenceMap();

	private static final Random rand = new Random();
	private static final String NBT_TAG = "Chroma_Research";

	public static final ChromaResearchManager instance = new ChromaResearchManager();

	private ChromaResearchManager() {

	}

	private void addLink(ChromaResearch obj, ChromaResearch parent) {

	}

	public ArrayList<ChromaResearch> getNextResearchesFor(EntityPlayer ep) {
		this.checkForUpgrade(ep);
		ArrayList<ChromaResearch> li = new ArrayList();
		for (int i = 0; i < ChromaResearch.researchList.length; i++) {
			ChromaResearch r = ChromaResearch.researchList[i];
			if (!r.isParent() && !this.playerHasFragment(ep, r)) {
				if (r.level == null || this.getPlayerResearchLevel(ep).ordinal() >= r.level.ordinal()) {
					Collection<ChromaResearch> deps = data.getParents(r);
					boolean missingdep = false;
					if (deps != null && !deps.isEmpty()) {
						for (ChromaResearch p : deps) {
							if (!this.playerHasFragment(ep, p)) {
								missingdep = true;
								break;
							}
						}
					}
					if (!missingdep)
						li.add(ChromaResearch.researchList[i]);
				}
			}
		}
		return li;
	}

	public ChromaResearch getRandomNextResearchFor(EntityPlayer ep) {
		ArrayList<ChromaResearch> li = this.getNextResearchesFor(ep);
		return li.isEmpty() ? null : li.get(rand.nextInt(li.size()));
	}

	/** Is this research one of the next ones available to the player, but without the player already having it */
	public boolean canPlayerStepTo(EntityPlayer ep, ChromaResearch r) {
		return this.getNextResearchesFor(ep).contains(r);
	}

	public boolean playerHasFragment(EntityPlayer ep, ChromaResearch r) {
		return this.getFragments(ep).contains(r);
	}

	public boolean givePlayerFragment(EntityPlayer ep, ChromaResearch r) {
		if (!this.playerHasFragment(ep, r)) {
			this.getNBTFragments(ep).appendTag(new NBTTagString(r.name()));
			this.checkForUpgrade(ep);
			if (ep instanceof EntityPlayerMP)
				ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
			return true;
		}
		return false;
	}

	private void checkForUpgrade(EntityPlayer ep) {
		ResearchLevel rl = this.getPlayerResearchLevel(ep);
		Collection<ChromaResearch> li = this.getResearchForLevel(rl);
		if (this.playerHasAllFragments(ep, li) && rl.ordinal() < ResearchLevel.levelList.length-1) {
			ResearchLevel next = ResearchLevel.levelList[rl.ordinal()+1];
			if (rl.canProgressTo(ep)) {
				this.stepPlayerResearchLevel(ep, next);
			}
		}
	}

	private Collection<ChromaResearch> getResearchForLevel(ResearchLevel rl) {
		return ChromaResearch.levelMap.get(rl);
	}

	private boolean playerHasAllFragments(EntityPlayer ep, Collection<ChromaResearch> li) {
		for (ChromaResearch r : li)
			if (!this.playerHasFragment(ep, r))
				return false;
		return true;
	}

	public boolean stepPlayerResearchLevel(EntityPlayer ep, ResearchLevel r) {
		return (this.getPlayerResearchLevel(ep).ordinal() == r.ordinal()-1) && this.setPlayerResearchLevel(ep, r);
	}

	public boolean setPlayerResearchLevel(EntityPlayer ep, ResearchLevel r) {
		if (r.movePlayerTo(ep)) {
			if (ep instanceof EntityPlayerMP)
				ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
			return true;
		}
		return false;
	}

	public ResearchLevel getPlayerResearchLevel(EntityPlayer ep) {
		return ResearchLevel.levelList[this.getNBT(ep).getInteger("research_level")];
	}

	public void maxPlayerResearch(EntityPlayer ep) {
		this.setPlayerResearchLevel(ep, ResearchLevel.levelList[ResearchLevel.levelList.length-1]);
		for (int i = 0; i < ChromaResearch.researchList.length; i++) {
			ChromaResearch r = ChromaResearch.researchList[i];
			this.givePlayerFragment(ep, r);
		}
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
	}

	public void resetPlayerResearch(EntityPlayer ep) {
		ReikaPlayerAPI.getDeathPersistentNBT(ep).removeTag(NBT_TAG);
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
	}

	private Collection<ChromaResearch> getFragments(EntityPlayer ep) {
		Collection<ChromaResearch> c = new ArrayList();
		NBTTagList li = this.getNBTFragments(ep);
		for (Object o : li.tagList) {
			c.add(ChromaResearch.valueOf(((NBTTagString)o).func_150285_a_()));
		}
		return c;
	}

	private NBTTagList getNBTFragments(EntityPlayer ep) {
		String key = "fragments";
		NBTTagCompound tag = this.getNBT(ep);
		if (!tag.hasKey(key))
			tag.setTag(key, new NBTTagList());
		NBTTagList li = tag.getTagList(key, NBTTypes.STRING.ID);
		tag.setTag(key, li);
		return li;
	}

	private NBTTagCompound getNBT(EntityPlayer ep) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		if (!nbt.hasKey(NBT_TAG))
			nbt.setTag(NBT_TAG, new NBTTagCompound());
		NBTTagCompound li = nbt.getCompoundTag(NBT_TAG);
		return li;
	}

	/** The part that must be completed before getting a given research available */
	public static enum ResearchLevel {
		ENTRY(),
		RAWEXPLORE(),
		BASICCRAFT(),
		RUNECRAFT(),
		ENERGYEXPLORE(),
		CHARGESELF(), //?
		MULTICRAFT(),
		NETWORKING(),
		PYLONCRAFT(),
		ENDGAME();

		public static final ResearchLevel[] levelList = values();

		private boolean movePlayerTo(EntityPlayer ep) {
			NBTTagCompound tag = instance.getNBT(ep);
			int has = tag.getInteger("research_level");
			if (has != this.ordinal()) {
				tag.setInteger("research_level", this.ordinal());
				return true;
			}
			return false;
		}

		private boolean canProgressTo(EntityPlayer ep) {
			switch(this) {
			case ENTRY:
				return true;
			case CHARGESELF:
				return ProgressionManager.instance.isPlayerAtStage(ep, ProgressStage.CHARGE);
			case RAWEXPLORE:
				return ProgressionManager.instance.isPlayerAtStage(ep, ProgressStage.CRYSTALS);
			case ENERGYEXPLORE:
				return ProgressionManager.instance.isPlayerAtStage(ep, ProgressStage.PYLON);
			case BASICCRAFT:
				return ProgressionManager.instance.isPlayerAtStage(ep, ProgressStage.CRYSTALS); //for now
			case RUNECRAFT:
				return RecipesCastingTable.playerHasCrafted(ep, RecipeType.CRAFTING);
			case MULTICRAFT:
				return RecipesCastingTable.playerHasCrafted(ep, RecipeType.TEMPLE);
			case PYLONCRAFT:
				return RecipesCastingTable.playerHasCrafted(ep, RecipeType.MULTIBLOCK);
			case NETWORKING:
				return RecipesCastingTable.playerHasCrafted(ep, RecipeType.MULTIBLOCK);
			case ENDGAME:
				return RecipesCastingTable.playerHasCrafted(ep, RecipeType.PYLON);
			default:
				return false;
			}
		}

		public String getDisplayName() {
			return StatCollector.translateToLocal("chromaresearch."+this.name().toLowerCase());
		}

		public ResearchLevel pre() {
			return this.ordinal() > 0 ? levelList[this.ordinal()-1] : this;
		}

		public ResearchLevel post() {
			return this.ordinal() < levelList.length-1 ? levelList[this.ordinal()+1] : this;
		}
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StatCollector;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Instantiable.Data.Maps.SequenceMap;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

public final class ChromaResearchManager {

	/** For hard links */
	private final SequenceMap<ChromaResearch> data = new SequenceMap();

	private static final Random rand = new Random();
	private static final String NBT_TAG = "Chroma_Research";

	public static final ChromaResearchManager instance = new ChromaResearchManager();

	public final Comparator researchComparator = new ChromaResearchComparator();

	private ChromaResearchManager() {

	}

	private void addLink(ChromaResearch obj, ChromaResearch parent) {

	}

	public ArrayList<ChromaResearch> getNextResearchesFor(EntityPlayer ep) {
		return this.getNextResearchesFor(ep, false);
	}

	private ArrayList<ChromaResearch> getNextResearchesFor(EntityPlayer ep, boolean debug) {
		if (!this.playerHasFragment(ep, ChromaResearch.FRAGMENT)) { //get fragment first, always
			ArrayList li = new ArrayList();
			li.add(ChromaResearch.FRAGMENT);
			return li;
		}
		this.checkForUpgrade(ep);
		ArrayList<ChromaResearch> li = new ArrayList();
		for (ChromaResearch r : ChromaResearch.getAllNonParents()) {
			if (!this.playerHasFragment(ep, r)) {
				if (r.level == null || this.getPlayerResearchLevel(ep).ordinal() >= r.level.ordinal()) {
					boolean missingdep = false;
					if (!r.playerHasProgress(ep)) {
						missingdep = true;
						if (debug)
							ChromatiCraft.logger.log("Fragment "+r+" rejected; insufficient progress "+Arrays.toString(r.getRequiredProgress())+".");
					}
					else {
						Collection<ChromaResearch> deps = data.getParents(r);
						if (deps != null && !deps.isEmpty()) {
							for (ChromaResearch p : deps) {
								if (!this.playerHasFragment(ep, p)) {
									missingdep = true;
									if (debug)
										ChromatiCraft.logger.log("Fragment "+r+" rejected; missing dependency "+p+".");
									break;
								}
							}
						}
					}
					if (!missingdep)
						li.add(r);
				}
				else if (debug) {
					ChromatiCraft.logger.log("Fragment "+r+" rejected; insufficient research level.");
				}
			}
		}
		return li;
	}

	public ChromaResearch getRandomNextResearchFor(EntityPlayer ep) {
		ArrayList<ChromaResearch> li = this.getNextResearchesFor(ep);
		return li.isEmpty() ? null : li.get(rand.nextInt(li.size()));
	}

	public Collection<ChromaResearch> getPreReqsFor(ChromaResearch r) {
		Collection<ChromaResearch> c = data.getParents(r);
		return c != null ? Collections.unmodifiableCollection(c) : new ArrayList();
	}

	/** Is this research one of the next ones available to the player, but without the player already having it */
	public boolean canPlayerStepTo(EntityPlayer ep, ChromaResearch r) {
		return this.getNextResearchesFor(ep).contains(r);
	}

	public boolean playerHasFragment(EntityPlayer ep, ChromaResearch r) {
		return r == ChromaResearch.START || this.getFragments(ep).contains(r);
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
		this.checkForUpgrade(ep, false);
	}

	private void checkForUpgrade(EntityPlayer ep, boolean debug) {
		ResearchLevel rl = this.getPlayerResearchLevel(ep);
		Collection<ChromaResearch> li = this.getResearchForLevel(rl);
		if (debug) {
			ChromatiCraft.logger.log("Fragments for level "+rl+": "+li);
			ChromatiCraft.logger.log(ep.getCommandSenderName()+" has: "+this.playerHasAllFragments(ep, li));
		}
		else {
			if (this.playerHasAllFragmentsThatMatter(ep, li) && rl.ordinal() < ResearchLevel.levelList.length-1) {
				ResearchLevel next = ResearchLevel.levelList[rl.ordinal()+1];
				if (next.canProgressTo(ep)) {
					this.stepPlayerResearchLevel(ep, next);
				}
			}
		}
	}

	public Collection<ChromaResearch> getResearchForLevelAndBelow(ResearchLevel rl) {
		Collection<ChromaResearch> c = new ArrayList();
		while (true) {
			c.addAll(ChromaResearch.levelMap.get(rl));
			if (rl == rl.pre())
				break;
			else
				rl = rl.pre();
		}
		return c;
	}

	public Collection<ChromaResearch> getResearchForLevel(ResearchLevel rl) {
		return Collections.unmodifiableCollection(ChromaResearch.levelMap.get(rl));
	}

	private boolean playerHasAllFragmentsThatMatter(EntityPlayer ep, Collection<ChromaResearch> li) {
		for (ChromaResearch r : li)
			if (r.isGating() && !this.playerHasFragment(ep, r))
				return false;
		return true;
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
		for (ChromaResearch r : ChromaResearch.getAllNonParents()) {
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

	public Collection<ChromaResearch> getFragments(EntityPlayer ep) {
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

		public boolean canProgressTo(EntityPlayer ep) {
			switch(this) {
			case ENTRY:
				return true;
			case CHARGESELF:
				return ProgressStage.CHARGE.isPlayerAtStage(ep);
			case RAWEXPLORE:
				return ProgressStage.CRYSTALS.isPlayerAtStage(ep);
			case ENERGYEXPLORE:
				return ProgressStage.PYLON.isPlayerAtStage(ep);
			case BASICCRAFT:
				return ProgressStage.CRYSTALS.isPlayerAtStage(ep); //for now
			case RUNECRAFT:
				return RecipesCastingTable.playerHasCrafted(ep, RecipeType.CRAFTING);
			case MULTICRAFT:
				return RecipesCastingTable.playerHasCrafted(ep, RecipeType.TEMPLE);
			case PYLONCRAFT:
				return ProgressStage.REPEATER.isPlayerAtStage(ep);
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

	public static class ChromaResearchDebugCommand extends DragonCommandBase {

		@Override
		public void processCommand(ICommandSender ics, String[] args) {
			EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
			if (args[0].toLowerCase().equals("fragments"))
				ChromatiCraft.logger.log("Next fragments for "+ep.getCommandSenderName()+": "+instance.getNextResearchesFor(ep, true));
			if (args[0].toLowerCase().equals("level"))
				instance.checkForUpgrade(ep, true);
		}

		@Override
		public String getCommandString() {
			return "chromaresearchdebug";
		}

		@Override
		protected boolean isAdminOnly() {
			return true;
		}

	}

	private static final class ChromaResearchComparator implements Comparator<ChromaResearch> {

		@Override
		public int compare(ChromaResearch o1, ChromaResearch o2) {
			return 1000*(o1.level.ordinal()-o2.level.ordinal())+o1.ordinal()-o2.ordinal();
		}

	}

}

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
import java.util.HashSet;
import java.util.Random;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StatCollector;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.ResearchFetcher;
import Reika.ChromatiCraft.API.ResearchFetcher.ResearchRegistry;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.ChromaOverlays;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Items.Tools.ItemChromaBook;
import Reika.DragonAPI.Command.DragonCommandBase;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.SequenceMap;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

import com.google.common.collect.HashBiMap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ChromaResearchManager implements ResearchRegistry {

	/** For hard links */
	private final SequenceMap<ChromaResearch> data = new SequenceMap();

	private final MultiMap<ResearchLevel, ChromaResearchTarget> priority = new MultiMap();

	private static final Random rand = new Random();
	private static final String NBT_TAG = "Chroma_Research";

	public static final ChromaResearchManager instance = new ChromaResearchManager();

	public final Comparator researchComparator = new ChromaResearchComparator();

	//public final Comparator progressComparator = new ProgressComparator();

	private final HashBiMap<Integer, ProgressElement> progressIDs = HashBiMap.create();

	private ChromaResearchManager() {
		ResearchFetcher.researchManager = this;

		priority.addValue(ResearchLevel.ENTRY, new ChromaResearchTarget(ChromaResearch.FRAGMENT, -1)); //get fragment first, always

		priority.addValue(ResearchLevel.RUNECRAFT, new ChromaResearchTarget(ChromaResearch.CRAFTING, 10));
		priority.addValue(ResearchLevel.RUNECRAFT, new ChromaResearchTarget(ChromaResearch.CASTING1, 5));
		priority.addValue(ResearchLevel.MULTICRAFT, new ChromaResearchTarget(ChromaResearch.CASTING2, 10));
		priority.addValue(ResearchLevel.PYLONCRAFT, new ChromaResearchTarget(ChromaResearch.CASTING3, 10));

		priority.addValue(ResearchLevel.NETWORKING, new ChromaResearchTarget(ChromaResearch.REPEATER, 100));
		priority.addValue(ResearchLevel.NETWORKING, new ChromaResearchTarget(ChromaResearch.COMPOUND, 10));
		priority.addValue(ResearchLevel.NETWORKING, new ChromaResearchTarget(ChromaResearch.REPEATERSTRUCT, 25));
		priority.addValue(ResearchLevel.NETWORKING, new ChromaResearchTarget(ChromaResearch.COMPOUNDSTRUCT, 2));

		priority.shuffleValues();

		/*
		for (int i = 0; i < ResearchLevel.levelList.length; i++) {
			this.register(ResearchLevel.levelList[i]);
		}

		for (int i = 0; i < ProgressStage.list.length; i++) {
			this.register(ProgressStage.list[i]);
		}
		 */
	}

	private void addLink(ChromaResearch obj, ChromaResearch parent) {

	}

	private ChromaResearch getPriorityResearchFor(EntityPlayer ep) {
		Collection<ChromaResearchTarget> c = priority.get(this.getPlayerResearchLevel(ep));
		WeightedRandom<ChromaResearch> wr = new WeightedRandom();
		for (ChromaResearchTarget t : c) {
			if (!this.playerHasFragment(ep, t.fragment)) {
				if (t.weight < 0)
					return t.fragment; //-1 is max priority
				else
					wr.addEntry(t.fragment, t.weight);
			}
		}
		return !wr.isEmpty() ? wr.getRandomEntry() : null;
	}

	public ArrayList<ChromaResearch> getNextResearchesFor(EntityPlayer ep) {
		return this.getNextResearchesFor(ep, false);
	}

	private ArrayList<ChromaResearch> getNextResearchesFor(EntityPlayer ep, boolean debug) {
		ChromaResearch pri = this.getPriorityResearchFor(ep);
		if (pri != null) {
			ArrayList li = new ArrayList();
			li.add(pri);
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
		return !r.isDummiedOut() && this.getNextResearchesFor(ep).contains(r);
	}

	public boolean playerHasFragment(EntityPlayer ep, ChromaResearch r) {
		return r.isAlwaysPresent() || r == ChromaResearch.PACKCHANGES || this.getFragments(ep).contains(r);
	}

	public boolean givePlayerFragment(EntityPlayer ep, ChromaResearch r) {
		if (!this.playerHasFragment(ep, r)) {
			this.getNBTFragments(ep).appendTag(new NBTTagString(r.name()));
			this.checkForUpgrade(ep);
			if (ep instanceof EntityPlayerMP)
				ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
			this.notifyPlayerOfProgression(ep, r);
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
			this.notifyPlayerOfProgression(ep, r);
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
		for (CastingRecipe r : RecipesCastingTable.instance.getAllRecipes()) {
			this.givePlayerRecipe(ep, r);
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
		Collection<ChromaResearch> c = new HashSet();
		NBTTagList li = this.getNBTFragments(ep);
		for (Object o : li.tagList) {
			ChromaResearch r = ChromaResearch.getByName(((NBTTagString)o).func_150285_a_());
			if (r != null) //may be null if a key is removed
				c.add(r);
		}
		return c;
	}
	/*
	public Collection<CastingRecipe> getRecipesPerformed(EntityPlayer ep) {
		Collection<CastingRecipe> c = new HashSet();
		NBTTagList li = this.getNBTRecipes(ep);
		for (Object o : li.tagList) {
			NBTTagCompound tag = (NBTTagCompound)o;
			ItemStack is = ItemStack.loadItemStackFromNBT(tag);
			c.addAll(RecipesCastingTable.instance.getAllRecipesMaking(is));
		}
		return c;
	}

	private NBTTagList getNBTRecipes(EntityPlayer ep) {
		String key = "recipes";
		NBTTagCompound tag = this.getNBT(ep);
		if (!tag.hasKey(key))
			tag.setTag(key, new NBTTagList());
		NBTTagList li = tag.getTagList(key, NBTTypes.COMPOUND.ID);
		tag.setTag(key, li);
		return li;
	}

	public boolean givePlayerRecipe(EntityPlayer ep, CastingRecipe cr) {
		if (!this.playerHasRecipe(ep, cr)) {
			NBTTagCompound tag = new NBTTagCompound();
			cr.getOutput().writeToNBT(tag);
			this.getNBTRecipes(ep).appendTag(tag);
			if (ep instanceof EntityPlayerMP)
				ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
			this.notifyPlayerOfProgression(ep, cr);
			return true;
		}
		return false;
	}

	public boolean playerHasRecipe(EntityPlayer ep, CastingRecipe cr) {
		return this.getRecipesPerformed(ep).contains(cr);
	}
	 */
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

	public void notifyPlayerOfProgression(EntityPlayer ep, ProgressElement p) {
		if (ep.worldObj.isRemote) {
			ChromaOverlays.instance.addProgressionNote(p);
		}
		else if (ep instanceof EntityPlayerMP) {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.PROGRESSNOTE.ordinal(), (EntityPlayerMP)ep, this.getID(p));
			if (ChromaOptions.PROGRESSNOTIFY.getState()) {
				ChromaAux.notifyServerPlayersExcept(ep, p);
			}
		}
	}

	public boolean playerHasUsedRecipe(EntityPlayer ep, CastingRecipe cr) {
		return this.getPlayerRecipes(ep).contains(cr.getIDCode());
	}

	public boolean givePlayerRecipe(EntityPlayer ep, CastingRecipe cr) {
		if (!this.playerHasUsedRecipe(ep, cr)) {
			this.getNBTRecipes(ep).appendTag(new NBTTagInt(cr.getIDCode()));
			if (ep instanceof EntityPlayerMP)
				ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
			return true;
		}
		return false;
	}

	private Collection<Integer> getPlayerRecipes(EntityPlayer ep) {
		HashSet<Integer> c = new HashSet();
		NBTTagList li = this.getNBTRecipes(ep);
		for (Object o : li.tagList) {
			int s = ((NBTTagInt)o).func_150287_d();
			c.add(s);
		}
		return c;
	}

	private NBTTagList getNBTRecipes(EntityPlayer ep) {
		String key = "recipes";
		NBTTagCompound tag = this.getNBT(ep);
		if (!tag.hasKey(key))
			tag.setTag(key, new NBTTagList());
		NBTTagList li = tag.getTagList(key, NBTTypes.INT.ID);
		tag.setTag(key, li);
		return li;
	}

	/** The part that must be completed before getting a given research available */
	public static enum ResearchLevel implements ProgressElement {
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

		private ResearchLevel() {
			instance.register(this);
		}

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

		@Override
		@SideOnly(Side.CLIENT)
		public String getTitle() {
			return this.getDisplayName();
		}

		@Override
		@SideOnly(Side.CLIENT)
		public String getShortDesc() {
			return "More of the world becomes visible to you.";
		}

		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getIcon() {
			return ChromaItems.FRAGMENT.getStackOf();
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

	private static class ChromaResearchTarget {

		private final ChromaResearch fragment;
		private final int weight;

		private ChromaResearchTarget(ChromaResearch r, int w) {
			fragment = r;
			weight = w;
		}

	}

	public static interface ProgressElement {

		@SideOnly(Side.CLIENT)
		public String getTitle();

		@SideOnly(Side.CLIENT)
		public String getShortDesc();

		@SideOnly(Side.CLIENT)
		public ItemStack getIcon();

	}

	public void register(ProgressElement p) {
		progressIDs.put(progressIDs.size(), p);
	}

	public ProgressElement getProgressForID(int id) {
		return progressIDs.get(id);
	}

	public int getID(ProgressElement e) {
		return progressIDs.inverse().get(e);
	}

	@Override
	public boolean playerHasResearch(EntityPlayer ep, String key) {
		ChromaResearch r = ChromaResearch.getByName(key.toUpperCase());
		if (r == null) {
			ChromatiCraft.logger.logError("A mod tried to fetch the state of an invalid research '"+key+"'!");
			Thread.dumpStack();
			return false;
		}
		return this.playerHasFragment(ep, r);
	}

	@Override
	public boolean lexiconHasFragment(ItemStack book, String key) {
		ChromaResearch r = ChromaResearch.getByName(key.toUpperCase());
		if (r == null) {
			ChromatiCraft.logger.logError("A mod tried to fetch the state of an invalid research '"+key+"'!");
			Thread.dumpStack();
			return false;
		}
		return ItemChromaBook.hasPage(book, r);
	}

	@Override
	public HashSet<String> getAllResearches() {
		HashSet<String> c = new HashSet();
		for (ChromaResearch r : ChromaResearch.getAllNonParents()) {
			c.add(r.name());
		}
		return c;
	}

	@Override
	public HashSet<String> getPrerequisites(String key) {
		ChromaResearch r = ChromaResearch.getByName(key.toUpperCase());
		if (r == null) {
			ChromatiCraft.logger.logError("A mod tried to fetch the state of an invalid research '"+key+"'!");
			Thread.dumpStack();
			return null;
		}
		Collection<ChromaResearch> c = this.getPreReqsFor(r);
		HashSet<String> h = new HashSet();
		for (ChromaResearch req : c) {
			h.add(req.name());
		}
		return h;
	}

	@Override
	public String getResearchLevelForPlayer(EntityPlayer ep) {
		return this.getPlayerResearchLevel(ep).name();
	}

}

/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Progression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import com.google.common.collect.HashBiMap;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.MinecraftForge;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.ProgressionAPI;
import Reika.ChromatiCraft.API.ProgressionAPI.ResearchRegistry;
import Reika.ChromatiCraft.API.Event.ProgressionEvent;
import Reika.ChromatiCraft.API.Event.ProgressionEvent.ResearchType;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaOverlays;
import Reika.ChromatiCraft.Items.Tools.ItemChromaBook;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.SequenceMap;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

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
	private final HashMap<String, ProgressElement> byName = new HashMap();

	private ChromaResearchManager() {
		ProgressionAPI.instance.researchManager = this;

		priority.addValue(ResearchLevel.ENTRY, new ChromaResearchTarget(ChromaResearch.FRAGMENT, -1)); //get fragment first, always

		priority.addValue(ResearchLevel.RUNECRAFT, new ChromaResearchTarget(ChromaResearch.CRAFTING, 10));
		priority.addValue(ResearchLevel.RUNECRAFT, new ChromaResearchTarget(ChromaResearch.CASTING1, 5));
		priority.addValue(ResearchLevel.MULTICRAFT, new ChromaResearchTarget(ChromaResearch.CASTING2, 10));
		priority.addValue(ResearchLevel.PYLONCRAFT, new ChromaResearchTarget(ChromaResearch.CASTING3, 10));

		priority.addValue(ResearchLevel.NETWORKING, new ChromaResearchTarget(ChromaResearch.REPEATER, 100));
		priority.addValue(ResearchLevel.NETWORKING, new ChromaResearchTarget(ChromaResearch.COMPOUND, 10));
		priority.addValue(ResearchLevel.NETWORKING, new ChromaResearchTarget(ChromaResearch.REPEATERSTRUCT, 25));
		priority.addValue(ResearchLevel.NETWORKING, new ChromaResearchTarget(ChromaResearch.COMPOUNDSTRUCT, 2));

		priority.addValue(ResearchLevel.ENERGY, new ChromaResearchTarget(ChromaResearch.ENERGY, 100));
		priority.addValue(ResearchLevel.ENERGY, new ChromaResearchTarget(ChromaResearch.SELFCHARGE, 50));
		priority.addValue(ResearchLevel.ENERGY, new ChromaResearchTarget(ChromaResearch.TRANSMISSION, 20));

		priority.addValue(ResearchLevel.RUNECRAFT, new ChromaResearchTarget(ChromaResearch.NETHERKEY, 20));

		priority.shuffleValues();

		/*
		for (int i = 0; i < ResearchLevel.levelList.length; i++) {
			this.register(ResearchLevel.levelList[i]);
		}

		for (int i = 0; i < ProgressStage.list.length; i++) {
			this.register(ProgressStage.list[i]);
		}
		 */

		this.addLink(ChromaResearch.TELEGATELOCK, ChromaResearch.GATE);
		this.addLink(ChromaResearch.TRANSMISSION, ChromaResearch.ENERGY);
		this.addLink(ChromaResearch.RELAY, ChromaResearch.ENERGY);
	}

	private void addLink(ChromaResearch obj, ChromaResearch parent) {
		data.addChild(parent, obj);
	}

	public Collection<ProgressAccess> getResearchLevelAdvancementBlocks(EntityPlayer ep) {
		Collection<ChromaResearch> li = this.getResearchLevelMissingFragments(ep);
		HashSet<ProgressAccess> c = new HashSet(li);
		for (ChromaResearch r : li) {
			for (ProgressStage s : r.getRequiredProgress()) {
				if (!s.isPlayerAtStage(ep))
					c.add(s);
			}
		}
		return c;
	}

	public HashSet<ChromaResearch> getResearchLevelMissingFragments(EntityPlayer ep) {
		Collection<ChromaResearch> cp = ChromaResearchManager.instance.getFragments(ep);
		ResearchLevel lvl = this.getPlayerResearchLevel(ep);
		HashSet<ChromaResearch> missing = new HashSet(ChromaResearchManager.instance.getResearchForLevel(lvl));
		//ReikaJavaLibrary.pConsole(missing+" - "+cp+" = ");
		missing.removeAll(cp);
		this.removeAllDummiedFragments(missing);
		Iterator<ChromaResearch> it = missing.iterator();
		while (it.hasNext()) {
			ChromaResearch r = it.next();
			if (!r.isGating(lvl))
				it.remove();
		}
		return missing;
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
		for (ChromaResearch r : ChromaResearch.getAllObtainableFragments()) {
			if (!this.playerHasFragment(ep, r)) {
				if (r.level == null || this.getPlayerResearchLevel(ep).isAtLeast(r.level)) {
					boolean missingdep = false;
					if (!r.canPlayerProgressTo(ep)) {
						missingdep = true;
						if (debug)
							ChromatiCraft.logger.log("Fragment "+r+" rejected; insufficient progress "+Arrays.toString(r.getRequiredProgress())+".");
					}
					else {
						if (!this.playerHasDependencies(r, ep)) {
							if (debug)
								ChromatiCraft.logger.log("Fragment "+r+" rejected; missing dependency.");// "+p+".");
							missingdep = true;
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

	public boolean playerHasDependencies(ChromaResearch r, EntityPlayer ep) {
		Collection<ChromaResearch> deps = data.getParents(r);
		if (deps != null && !deps.isEmpty()) {
			for (ChromaResearch p : deps) {
				if (!this.playerHasFragment(ep, p)) {
					return false;
				}
			}
		}
		return true;
	}

	public ChromaResearch getRandomNextResearchFor(EntityPlayer ep) {
		ArrayList<ChromaResearch> li = this.getNextResearchesFor(ep);
		return li.isEmpty() ? null : li.get(rand.nextInt(li.size()));
	}

	public Collection<ChromaResearch> getPreReqsFor(ChromaResearch r) {
		Collection<ChromaResearch> c = data.getParents(r);
		return c != null ? Collections.unmodifiableCollection(c) : new ArrayList();
	}

	public void removeAllDummiedFragments(Collection<ChromaResearch> li) {
		Iterator<ChromaResearch> it = li.iterator();
		while (it.hasNext()) {
			ChromaResearch r = it.next();
			if (r.isDummiedOut())
				it.remove();
		}
	}

	/** Is this research one of the next ones available to the player, but without the player already having it */
	public boolean canPlayerStepTo(EntityPlayer ep, ChromaResearch r) {
		return !r.isDummiedOut() && this.getNextResearchesFor(ep).contains(r);
	}

	public boolean playerHasFragment(EntityPlayer ep, ChromaResearch r) {
		return r.isAlwaysPresent() || r == ChromaResearch.PACKCHANGES || this.getFragments(ep).contains(r);
	}

	public boolean removePlayerFragment(EntityPlayer ep, ChromaResearch r, boolean notify) {
		if (this.playerHasFragment(ep, r)) {
			NBTTagList li = this.getNBTFragments(ep);
			Iterator<NBTTagString> it = li.tagList.iterator();
			while (it.hasNext()) {
				NBTTagString s = it.next();
				if (s.func_150285_a_().equals(r.name()))
					it.remove();
			}
			if (ep instanceof EntityPlayerMP)
				ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
			ProgressionLoadHandler.instance.updateProgressCache(ep);
			ProgressionLoadHandler.instance.updateBackup(ep);
			return true;
		}
		return false;
	}

	public boolean givePlayerFragment(EntityPlayer ep, ChromaResearch r, boolean notify) {
		if (!this.playerHasFragment(ep, r)) {
			this.getNBTFragments(ep).appendTag(new NBTTagString(r.name()));
			this.checkForUpgrade(ep);
			if (ep instanceof EntityPlayerMP)
				ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
			if (notify)
				this.notifyPlayerOfProgression(ep, r);
			ProgressionLoadHandler.instance.updateProgressCache(ep);
			ProgressionLoadHandler.instance.updateBackup(ep);
			MinecraftForge.EVENT_BUS.post(new ProgressionEvent(ep, r.name(), ResearchType.FRAGMENT));
			return true;
		}
		return false;
	}

	public void checkForUpgrade(EntityPlayer ep) {
		ResearchLevel rl = this.getPlayerResearchLevel(ep);
		Collection<ChromaResearch> li = this.getResearchForLevel(rl);
		if (this.playerHasAllFragmentsThatMatter(ep, li) && rl.ordinal() < ResearchLevel.levelList.length-1) {
			ResearchLevel next = ResearchLevel.levelList[rl.ordinal()+1];
			if (next.canProgressTo(ep)) {
				this.stepPlayerResearchLevel(ep, next);
			}
		}
	}

	public Collection<ChromaResearch> getResearchForLevelAndBelow(ResearchLevel rl) {
		Collection<ChromaResearch> c = new ArrayList();
		while (true) {
			c.addAll(ChromaResearch.getPagesFor(rl));
			if (rl == rl.pre())
				break;
			else
				rl = rl.pre();
		}
		return c;
	}

	public Collection<ChromaResearch> getResearchForLevel(ResearchLevel rl) {
		return Collections.unmodifiableCollection(ChromaResearch.getPagesFor(rl));
	}

	public Collection<ChromaResearch> getMissingResearch(EntityPlayer ep) {
		return this.getMissingResearch(ep, null);
	}

	public Collection<ChromaResearch> getMissingResearch(EntityPlayer ep, ProgressStage req) {
		Collection<ChromaResearch> ret = new ArrayList();
		ResearchLevel lvl = this.getPlayerResearchLevel(ep);
		for (ChromaResearch r : ChromaResearch.getPagesFor(lvl)) {
			if (!r.isGating(lvl))
				continue;
			if (this.playerHasFragment(ep, r))
				continue;
			if (req != null && !r.requiresProgress(req))
				continue;
			ret.add(r);
		}
		return ret;
	}

	private boolean playerHasAllFragmentsThatMatter(EntityPlayer ep, Collection<ChromaResearch> li) {
		ResearchLevel lvl = this.getPlayerResearchLevel(ep);
		for (ChromaResearch r : li)
			if (r.isGating(lvl) && !this.playerHasFragment(ep, r))
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
		return (this.getPlayerResearchLevel(ep).ordinal() == r.ordinal()-1) && this.setPlayerResearchLevel(ep, r, true);
	}

	public boolean setPlayerResearchLevel(EntityPlayer ep, ResearchLevel r, boolean notify) {
		if (this.movePlayerTo(ep, r)) {
			if (ep instanceof EntityPlayerMP)
				ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
			if (notify)
				this.notifyPlayerOfProgression(ep, r);
			ProgressionLoadHandler.instance.updateProgressCache(ep);
			ProgressionLoadHandler.instance.updateBackup(ep);
			return true;
		}
		return false;
	}

	public ResearchLevel getPlayerResearchLevel(EntityPlayer ep) {
		NBTTagCompound base = this.getRootProgressionNBT(ep);
		NBTBase tag = base.getTag("research_level");
		if (tag instanceof NBTTagString) {
			return ResearchLevel.valueOf(((NBTTagString)tag).func_150285_a_());
		}
		else {
			int idx = base.getInteger("research_level");
			if (idx > ResearchLevel.ENERGY.ordinal())
				idx--;
			ResearchLevel ret = ResearchLevel.levelList[idx];
			base.setString("research_level", ret.name());
			return ret;
		}
	}

	public void maxPlayerResearch(EntityPlayer ep, boolean notify) {
		this.setPlayerResearchLevel(ep, ResearchLevel.levelList[ResearchLevel.levelList.length-1], notify);
		for (ChromaResearch r : ChromaResearch.getAllObtainableFragments()) {
			this.givePlayerFragment(ep, r, notify);
		}
		for (CastingRecipe r : RecipesCastingTable.instance.getAllRecipes()) {
			this.givePlayerRecipe(ep, r);
		}
		if (ep instanceof EntityPlayerMP)
			ReikaPlayerAPI.syncCustomData((EntityPlayerMP)ep);
	}

	public void resetPlayerResearch(EntityPlayer ep, boolean notify) {
		this.getRootNBTTag(ep).removeTag(NBT_TAG);
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
		NBTTagCompound tag = this.getRootProgressionNBT(ep);
		if (!tag.hasKey(key))
			tag.setTag(key, new NBTTagList());
		NBTTagList li = tag.getTagList(key, NBTTypes.STRING.ID);
		tag.setTag(key, li);
		return li;
	}

	public NBTTagCompound getRootProgressionNBT(EntityPlayer ep) {
		NBTTagCompound nbt = this.getRootNBTTag(ep);
		if (!nbt.hasKey(NBT_TAG))
			nbt.setTag(NBT_TAG, new NBTTagCompound());
		NBTTagCompound li = nbt.getCompoundTag(NBT_TAG);
		return li;
	}

	private boolean movePlayerTo(EntityPlayer ep, ResearchLevel rl) {
		ResearchLevel at = this.getPlayerResearchLevel(ep);
		if (at != rl) {
			NBTTagCompound tag = ChromaResearchManager.instance.getRootProgressionNBT(ep);
			tag.setString("research_level", rl.name());
			return true;
		}
		return false;
	}

	public NBTTagCompound getRootNBTTag(EntityPlayer ep) {
		NBTTagCompound tag = ReikaPlayerAPI.isFake(ep) ? null : ReikaPlayerAPI.getDeathPersistentNBT(ep);
		if (tag == null || tag.hasNoTags()/* || !tag.hasKey(NBT_TAG) || !tag.hasKey(ProgressionManager.MAIN_NBT_TAG)*/) {
			NBTTagCompound repl = ProgressionLoadHandler.instance.attemptToLoadBackup(ep);
			if (repl != null) {
				if (tag == null)
					tag = new NBTTagCompound();
				ReikaNBTHelper.copyNBT(repl, tag);
			}
		}
		return tag;
	}

	public void notifyPlayerOfProgression(EntityPlayer ep, ProgressElement p) {
		if (ep.worldObj.isRemote) {
			ChromaOverlays.instance.addProgressionNote(p);
		}
		else if (ep instanceof EntityPlayerMP) {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.PROGRESSNOTE.ordinal(), (EntityPlayerMP)ep, this.getID(p));
			if (ChromaOptions.PROGRESSNOTIFY.getState()) {
				if (ChromaOptions.PROGRESSNOTIFY_SELF.getState())
					ChromaAux.notifyServerPlayers(ep, p);
				else
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
		NBTTagCompound tag = this.getRootProgressionNBT(ep);
		if (!tag.hasKey(key))
			tag.setTag(key, new NBTTagList());
		NBTTagList li = tag.getTagList(key, NBTTypes.INT.ID);
		tag.setTag(key, li);
		return li;
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

	public static interface ProgressIndicator {

		@SideOnly(Side.CLIENT)
		//public ItemStack getIcon();
		public void renderIcon(RenderItem ri, FontRenderer fr, int x, int y);

		public String name();

	}

	public static interface ProgressElement extends ProgressIndicator {

		//@SideOnly(Side.CLIENT)
		public String getTitle();

		//@SideOnly(Side.CLIENT)
		public String getShortDesc();

		//@SideOnly(Side.CLIENT)
		public String getFormatting();

		public boolean giveToPlayer(EntityPlayer ep, boolean notify);

	}

	public void register(ProgressElement p) {
		progressIDs.put(progressIDs.size(), p);
		byName.put(p.name(), p);
	}

	public ProgressElement getProgressForID(int id) {
		return progressIDs.get(id);
	}

	public int getID(ProgressElement e) {
		return progressIDs.inverse().get(e);
	}

	public ProgressElement getProgressForString(String id) {
		return byName.get(id);
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

	public ResearchLevel getEarliestResearchLevelRequiring(ProgressStage p) {
		ResearchLevel min = ProgressionManager.instance.getEarliestAllowedGate(p);
		ResearchLevel rl = null;
		for (ChromaResearch r : ChromaResearch.getAllObtainableFragments()) {
			if (r.isGating(r.level) && r.requiresProgress(p)) {
				if (rl == null || rl.isAtLeast(r.level))
					rl = r.level;
			}
		}
		if (rl != null && min.isAtLeast(rl))
			rl = min;
		return rl;
	}

}

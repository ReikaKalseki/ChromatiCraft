package Reika.ChromatiCraft.Magic.Progression;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.Magic.Progression.FragmentCategorizationSystem.FragmentCategorization;
import Reika.ChromatiCraft.Magic.Progression.FragmentCategorizationSystem.FragmentCategory;
import Reika.ChromatiCraft.Magic.Progression.FragmentCategorizationSystem.FragmentTable;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;

public class ProgressionChoiceSystem {

	private final Random rand;

	private final EntityPlayer player;

	public ProgressionChoiceSystem(EntityPlayer ep) {
		player = ep;

		rand = new Random(ep.getUniqueID().hashCode() + DragonAPICore.getLaunchTime() * System.identityHashCode(this));
		rand.nextBoolean();
		rand.nextBoolean();
	}
	/*
	public ChromaResearch selectInCategory(FragmentCategory fc) {
		FragmentTable table = FragmentCategorizationSystem.instance.getFragments(fc);
		WeightedRandom<ChromaResearch> wr = table.getSelectionForPlayer(player);
		if (wr.isEmpty())
			return ChromaResearchManager.instance.getRandomNextResearchFor(player);
		return wr.getRandomEntry();
	}
	 */
	public ArrayList<Selection> pickThreeCategories() {
		EnumSet<FragmentCategory> set = EnumSet.noneOf(FragmentCategory.class);
		ArrayList<ChromaResearch> next = ChromaResearchManager.instance.getNextResearchesFor(player);
		if (next.isEmpty())
			throw new IllegalStateException("No Valid Next Fragments!?");
		for (ChromaResearch r : next) {
			set.addAll(FragmentCategorizationSystem.instance.getCategories(r).set());
		}
		ArrayList<Selection> picked = new ArrayList();
		ArrayList<FragmentCategory> li = new ArrayList();
		EnumSet<FragmentCategory> unusable = EnumSet.noneOf(FragmentCategory.class);

		while (picked.size() < 3) {
			if (li.isEmpty()) {
				li.addAll(set);
				li.removeAll(unusable);
				if (li.isEmpty()) {
					break;
				}
			}
			FragmentCategory fc = li.remove(rand.nextInt(li.size()));
			FragmentTable table = FragmentCategorizationSystem.instance.getFragments(fc);
			WeightedRandom<ChromaResearch> wr = table.getSelectionForPlayer(player);
			if (wr.isEmpty()) {
				unusable.add(fc);
			}
			else {
				wr.setRNG(rand);
				picked.add(new Selection(fc, wr.getRandomEntry()));
			}
		}
		while (picked.size() < 3 && !next.isEmpty()) {
			ChromaResearch choose = next.remove(rand.nextInt(next.size()));
			FragmentCategorization c = FragmentCategorizationSystem.instance.getCategories(choose);
			WeightedRandom<FragmentCategory> wr = c.getSelection();
			wr.setRNG(rand);
			picked.add(new Selection(wr.getRandomEntry(), choose));
		}
		if (picked.isEmpty())
			throw new IllegalStateException("Valid Next Fragments yet none pickable!?");
		while (picked.size() < 3) {
			picked.add(picked.get(0));
		}
		return picked;
	}

	public static class Selection {

		public final ChromaResearch fragment;
		public final FragmentCategory category;

		public Selection(FragmentCategory fc, ChromaResearch r) {
			fragment = r;
			category = fc;
		}

		public boolean giveToPlayer(EntityPlayer ep) {
			return giveToPlayer(ep, fragment);
		}

		public static boolean giveToPlayer(EntityPlayer ep, ChromaResearch r) {
			if (r.canPlayerProgressTo(ep) && ChromaResearchManager.instance.getNextResearchesFor(ep).contains(r)) {
				ChromaResearchManager.instance.givePlayerFragment(ep, r, true);
				return true;
			}
			return false;
		}

	}

}

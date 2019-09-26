package Reika.ChromatiCraft.Magic.Progression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

public class ProgressionLinking {

	private static final String COOPERATE_NBT_TAG = "Chroma_Cooperation";

	public static final ProgressionLinking instance = new ProgressionLinking();

	private ProgressionLinking() {

	}

	public Collection<UUID> getSlavedIDs(EntityPlayer ep) {
		Collection<UUID> c = new HashSet();
		for (NBTTagString s : ((List<NBTTagString>)this.getCooperatorList(ep).tagList)) {
			try {
				c.add(UUID.fromString(s.func_150285_a_()));
			}
			catch (IllegalArgumentException e) {
				ChromatiCraft.logger.logError("Could not load cooperator UUID "+s.func_150285_a_()+"' as a cooperator with "+ep.getCommandSenderName());
			}
		}
		return c;
	}

	public LinkFailure linkProgression(EntityPlayer ep1, EntityPlayer ep2) {
		return this.doLinkProgression(ep1, ep2, true);
	}

	public void unlinkProgression(EntityPlayer ep1, EntityPlayer ep2) {
		this.doLinkProgression(ep1, ep2, false);
	}

	public LinkFailure doLinkProgression(EntityPlayer ep1, EntityPlayer ep2, boolean link) {
		if (link)
			ChromatiCraft.logger.debug("Attempting to link progression from "+ep1.getCommandSenderName()+" to "+ep2.getCommandSenderName());
		else
			ChromatiCraft.logger.debug("Attempting to unlink progression of "+ep1.getCommandSenderName()+" and "+ep2.getCommandSenderName());

		if (!this.canEverLinkProgression(ep1, ep2)) {
			ChromatiCraft.logger.debug("Failed to link progression; players are invalid/fake/etc!");
			return LinkFailure.INVALID;
		}

		/*
		if (ChromaResearchManager.instance.getPlayerResearchLevel(ep1) != ChromaResearchManager.instance.getPlayerResearchLevel(ep2)) {
			ChromatiCraft.logger.debug("Failed to link progression; players are at different research levels!");
			return LinkFailure.LEVELS;
		}
		 */

		ProgressionRegion p1 = this.getRegion(ep1);
		ProgressionRegion p2 = this.getRegion(ep2);
		if (p1 != p2) {
			ChromatiCraft.logger.debug("Failed to link progression; players are in different progression regions!");
			return LinkFailure.REGIONS;
		}

		LinkFailure lf = p1.checkLinkValidity(ep1, ep2);
		if (lf != null) {
			ChromatiCraft.logger.debug("Failed to link progression: "+lf.toString());
			return lf;
		}

		NBTTagString s1 = new NBTTagString(ep2.getUniqueID().toString());
		NBTTagString s2 = new NBTTagString(ep1.getUniqueID().toString());
		NBTTagList li1 = this.getCooperatorList(ep1);
		NBTTagList li2 = this.getCooperatorList(ep2);
		if (link) {
			li1.appendTag(s1);
			li2.appendTag(s2);
		}
		else {
			li1.tagList.remove(s1);
			li2.tagList.remove(s2);
		}
		ChromaResearchManager.instance.getRootNBTTag(ep1).setTag(COOPERATE_NBT_TAG, li1);
		ChromaResearchManager.instance.getRootNBTTag(ep2).setTag(COOPERATE_NBT_TAG, li2);
		p1.onLink(ep1, ep2);
		return null;
	}

	private ProgressionRegion getRegion(EntityPlayer ep) {
		if (ProgressStage.DIMENSION.isPlayerAtStage(ep))
			return ProgressionRegion.FINAL;
		if (ProgressStage.MULTIBLOCK.isPlayerAtStage(ep) || ChromaResearchManager.instance.getPlayerResearchLevel(ep).isAtLeast(ResearchLevel.MULTICRAFT))
			return ProgressionRegion.LATE;
		if (ProgressStage.RUNEUSE.isPlayerAtStage(ep) || ChromaResearchManager.instance.getPlayerResearchLevel(ep).isAtLeast(ResearchLevel.RUNECRAFT))
			return ProgressionRegion.MID;
		return ProgressionRegion.EARLY;
	}

	private boolean canEverLinkProgression(EntityPlayer ep1, EntityPlayer ep2) {
		if (ReikaPlayerAPI.isFake(ep1) || ReikaPlayerAPI.isFake(ep2))
			return false;
		return true;
	}

	private ProgressStage[] getLinkIgnoreList() {
		return new ProgressStage[] {ProgressStage.CAVERN, ProgressStage.BURROW, ProgressStage.OCEAN, ProgressStage.DESERTSTRUCT, ProgressStage.SNOWSTRUCT, ProgressStage.TOWER, ProgressStage.ARTEFACT, ProgressStage.STRUCTCHEAT, ProgressStage.DIE, ProgressStage.VOIDMONSTER};
	}

	private NBTTagList getCooperatorList(EntityPlayer ep) {
		NBTTagCompound nbt = ChromaResearchManager.instance.getRootNBTTag(ep);
		if (!nbt.hasKey(COOPERATE_NBT_TAG))
			nbt.setTag(COOPERATE_NBT_TAG, new NBTTagList());
		NBTTagList li = nbt.getTagList(COOPERATE_NBT_TAG, NBTTypes.STRING.ID);
		return li;
	}

	private static enum ProgressionRegion {
		EARLY(), // up to and including having the KEP; brings both players "up" to this point
		MID(), //from KEP to multicraft; must completely match to link
		LATE(), //post multicraft; brings both players DOWN to this point
		FINAL(); //post dimension; too late to link

		private LinkFailure checkLinkValidity(EntityPlayer ep1, EntityPlayer ep2) {
			List<ProgressStage> check = this.getCheckList();
			for (ProgressStage p : check) {
				if (!this.match(p, ep1, ep2))
					return new LinkFailure(p);
			}
			ResearchLevel rl1 = ChromaResearchManager.instance.getPlayerResearchLevel(ep1);
			ResearchLevel rl2 = ChromaResearchManager.instance.getPlayerResearchLevel(ep2);
			switch(this) {
				case EARLY:
					if (rl1.getDifference(rl2) >= 2) {
						return new LinkFailure("Mismatched Research Levels");
					}
					return null;
				case MID:
					if (rl1 != rl2) {
						return new LinkFailure("Mismatched Research Levels");
					}
					if (!ProgressionManager.instance.isProgressionEqual(ep1, ep2, instance.getLinkIgnoreList())) {
						return new LinkFailure("Mismatched Progression");
					}
					return null;
				case LATE:
					if (rl1.getDifference(rl2) > 2) {
						return new LinkFailure("Mismatched Research Levels");
					}
					return null;
				case FINAL:
					return LinkFailure.TOO_LATE;
				default:
					return LinkFailure.INVALID;
			}
		}

		private List<ProgressStage> getCheckList() {
			switch(this) {
				case EARLY:
					return Arrays.asList(ProgressStage.PYLON, ProgressStage.CRYSTALS, ProgressStage.ALLCOLORS);
				case MID:
					return new ArrayList();
				case LATE:
					return Arrays.asList(ProgressStage.LINK);
				case FINAL:
					return new ArrayList();
				default:
					return new ArrayList();
			}
		}

		private boolean match(ProgressStage p, EntityPlayer ep1, EntityPlayer ep2) {
			return p.isPlayerAtStage(ep1) == p.isPlayerAtStage(ep2);
		}

		private void onLink(EntityPlayer ep1, EntityPlayer ep2) {
			switch(this) {
				case EARLY:
					break;
				case MID:
					break;
				case LATE:
					break;
				case FINAL:
					break;
			}
		}
	}

	public static class LinkFailure {

		private static final LinkFailure INVALID = new LinkFailure("Invalid Players");
		private static final LinkFailure REGIONS = new LinkFailure("Progression Too Different");
		private static final LinkFailure LEVELS = new LinkFailure("Mismatched Levels");
		private static final LinkFailure TOO_LATE = new LinkFailure("Too Late");

		public final String text;

		private LinkFailure(ProgressStage p) {
			text = p.getTitleString();
		}

		private LinkFailure(String s) {
			text = s;
		}

		@Override
		public String toString() {
			return text;
		}

	}
}

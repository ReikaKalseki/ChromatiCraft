package Reika.ChromatiCraft.Magic.Progression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Auxiliary.IconLookupRegistry;
import Reika.DragonAPI.Instantiable.Rendering.TextureSlot;
import Reika.DragonAPI.Instantiable.Rendering.TextureSubImage;
import Reika.DragonAPI.Interfaces.IconEnum;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ProgressionLinking {

	private static final String COOPERATE_NBT_TAG = "Chroma_Cooperation";

	public static final ProgressionLinking instance = new ProgressionLinking();

	private ProgressionLinking() {

	}

	private Collection<UUID> getSlavedIDs(EntityPlayer ep) {
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

	public LinkFailure linkProgression(EntityPlayer from, EntityPlayer to) {
		return this.doLinkProgression(from, to, true);
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
		ChromaResearchManager.instance.getRootProgressionNBT(ep1).setTag(COOPERATE_NBT_TAG, li1);
		ChromaResearchManager.instance.getRootProgressionNBT(ep2).setTag(COOPERATE_NBT_TAG, li2);
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
		if (ep1 == ep2 || ReikaPlayerAPI.isFake(ep1) || ReikaPlayerAPI.isFake(ep2))
			return false;
		return true;
	}

	private ProgressStage[] getLinkIgnoreList() {
		return new ProgressStage[] {ProgressStage.CAVERN, ProgressStage.BURROW, ProgressStage.OCEAN, ProgressStage.DESERTSTRUCT, ProgressStage.SNOWSTRUCT, ProgressStage.TOWER, ProgressStage.ARTEFACT, ProgressStage.STRUCTCHEAT, ProgressStage.DIE, ProgressStage.VOIDMONSTER};
	}

	private NBTTagList getCooperatorList(EntityPlayer ep) {
		NBTTagCompound nbt = ChromaResearchManager.instance.getRootProgressionNBT(ep);
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
						return LinkFailure.LEVELS;
					}
					return null;
				case MID:
					if (rl1 != rl2) {
						return LinkFailure.LEVELS;
					}
					if (!ProgressionManager.instance.isProgressionEqual(ep1, ep2, instance.getLinkIgnoreList())) {
						return new LinkFailure("Mismatched Progression", ChromaIcons.QUESTION);
					}
					return null;
				case LATE:
					if (rl1.getDifference(rl2) > 2) {
						return LinkFailure.LEVELS;
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

		private void onLink(EntityPlayer from, EntityPlayer to) {
			switch(this) {
				case EARLY:
					ProgressionManager.instance.copyProgressStages(to, from);
					break;
				case MID:
					break;
				case LATE:
					ProgressionManager.instance.copyProgressStages(from, to);
					break;
				case FINAL:
					break;
			}
		}
	}

	public static class LinkFailure {

		private static final LinkFailure INVALID = new LinkFailure("Invalid Players", ChromaIcons.NOENTER);
		private static final LinkFailure REGIONS = new LinkFailure("Progression Too Different", getInfoIconSlot(1, 3));
		private static final LinkFailure LEVELS = new LinkFailure("Mismatched Levels", getInfoIconSlot(0, 3));
		private static final LinkFailure TOO_LATE = new LinkFailure("Too Late", getInfoIconSlot(2, 3));

		public final String text;
		private final String iconName;
		private TextureSubImage icon;
		private final ProgressStage progress;

		private LinkFailure(ProgressStage p) {
			text = p.getTitleString();
			progress = p;
			iconName = null;
		}

		private static TextureSlot getInfoIconSlot(int x, int y) {
			return TextureSlot.fromSpritesheet(ChromatiCraft.class, "Textures/infoicons.png", x, y, 16);
		}

		private LinkFailure(String s, TextureSlot ico) {
			text = s;
			progress = null;
			iconName = null;
			if (ico == null)
				throw new IllegalArgumentException("Null texture!");
			icon = ico;
		}

		private LinkFailure(String s, IconEnum ico) {
			text = s;
			progress = null;
			if (ico == null)
				throw new IllegalArgumentException("Null icon!");
			if (ico.getIcon() == null && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				throw new IllegalArgumentException("No-data icon!");
			iconName = ico.name();
			icon = FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? new TextureSubImage(ico.getIcon()) : null;
		}

		@Override
		public String toString() {
			return text;
		}

		@SideOnly(Side.CLIENT)
		public void render(Tessellator v5, double s, float f) {
			BlendMode.DEFAULT.apply();
			if (progress != null) {
				progress.renderIconInWorld(v5, s, 0, 0, f);
			}
			else {
				GL11.glRotated(180, 0, 0, 1);
				this.bindTexture();
				v5.startDrawingQuads();
				int a = (int)Math.min(255, f*(255-35*f+32*f*Math.sin(System.currentTimeMillis()/250D)));
				v5.setColorRGBA_I(0xffffff, a);
				TextureSubImage ico = icon != null ? icon : new TextureSubImage(ChromaIcons.X.getIcon());
				v5.addVertexWithUV(-s, s, 0, ico.minU, ico.maxV);
				v5.addVertexWithUV(s, s, 0, ico.maxU, ico.maxV);
				v5.addVertexWithUV(s, -s, 0, ico.maxU, ico.minV);
				v5.addVertexWithUV(-s, -s, 0, ico.minU, ico.minV);
				v5.draw();
				GL11.glPopMatrix();
			}
		}

		private void bindTexture() {
			if (icon instanceof TextureSlot) {
				((TextureSlot)icon).bindTexture();
			}
			else {
				ReikaTextureHelper.bindTerrainTexture();
			}
		}

		public NBTTagCompound writeToNBT() {
			NBTTagCompound ret = new NBTTagCompound();
			ret.setString("label", text);
			if (progress != null) {
				ret.setInteger("prog", progress.ordinal());
			}
			if (icon != null)
				ret.setTag("icon", icon.writeToNBT());
			if (iconName != null)
				ret.setString("iconName", iconName);
			return ret;
		}

		public static LinkFailure readFromNBT(NBTTagCompound NBT) {
			if (NBT.hasKey("prog")) {
				return new LinkFailure(ProgressStage.list[NBT.getInteger("prog")]);
			}
			else if (NBT.hasKey("iconName")) {
				return new LinkFailure(NBT.getString("label"), IconLookupRegistry.instance.getIcon(NBT.getString("iconName")));
			}
			else if (NBT.hasKey("icon")) {
				return new LinkFailure(NBT.getString("label"), TextureSlot.readFromNBT(NBT.getCompoundTag("icon")));
			}
			else {
				return new LinkFailure(NBT.getString("label")+" (+invalid data)", ChromaIcons.X); //error
			}
		}

	}

	public boolean hasLinkedPlayers(EntityPlayer ep) {
		return !this.getCooperatorList(ep).tagList.isEmpty();
	}

	public Collection<EntityPlayer> getShareablePlayers(EntityPlayer ep, ProgressStage s) {
		Collection<EntityPlayer> players = new ArrayList();
		Collection<UUID> coop = this.getSlavedIDs(ep);
		for (UUID u : coop) {
			EntityPlayer e = ep.worldObj.func_152378_a(u);
			if (e == null || ReikaPlayerAPI.isFake(e)) {
				continue;
			}
			if (s != null && !s.getShareability().canShareTo(ep, e)) {
				continue;
			}
			players.add(e);
		}
		return players;
	}

	public void attemptSyncTo(EntityPlayer main, EntityPlayer follower) {
		boolean flag = true;
		while (flag) {
			flag = false;
			for (ProgressStage p : ProgressionManager.instance.getStagesFor(main)) {
				if (p.isPlayerAtStage(follower) || !p.playerHasPrerequisites(follower))
					continue;
				switch(p.reloadLevel) {
					case ALWAYS:
						if (p.stepPlayerTo(follower, false))
							flag = true;
						break;
					case TRIGGER:
					case NEVER:
					default:
						break;
				}
			}
		}
	}

	public void attemptSyncAllInGroup(EntityPlayer player) {
		for (EntityPlayer ep : this.getShareablePlayers(player, null)) {
			this.attemptSyncTo(ep, player);
			this.attemptSyncTo(player, ep);
		}
	}

	public void attemptSyncTriggerProgressFor(EntityPlayer ep, ProgressStage p) {
		for (EntityPlayer ep2 : this.getShareablePlayers(ep, p)) {
			if (p.isPlayerAtStage(ep2)) {
				p.stepPlayerTo(ep);
			}
		}
	}
}

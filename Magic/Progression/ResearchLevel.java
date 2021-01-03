package Reika.ChromatiCraft.Magic.Progression;

import java.util.Locale;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager.ProgressElement;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** The part that must be completed before getting a given research available */
public enum ResearchLevel implements ProgressElement {
	ENTRY(),
	RAWEXPLORE(),
	BASICCRAFT(),
	RUNECRAFT(),
	ENERGY(),
	MULTICRAFT(),
	NETWORKING(),
	PYLONCRAFT(),
	ENDGAME(),
	CTM();

	public static final ResearchLevel[] levelList = values();

	private ResearchLevel() {
		ChromaResearchManager.instance.register(this);
	}

	public boolean canProgressTo(EntityPlayer ep) {
		switch(this) {
			case ENTRY:
				return true;
			case RAWEXPLORE:
				return ProgressStage.CRYSTALS.isPlayerAtStage(ep);
			case ENERGY:
				return ProgressStage.CHARGE.isPlayerAtStage(ep) && ProgressStage.PYLON.isPlayerAtStage(ep);
			case BASICCRAFT:
				return ProgressStage.ANYSTRUCT.isPlayerAtStage(ep);
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
			case CTM:
				return ProgressStage.CTM.isPlayerAtStage(ep);
			default:
				return false;
		}
	}

	public String getDisplayName() {
		return StatCollector.translateToLocal("chromaresearch."+this.name().toLowerCase(Locale.ENGLISH));
	}

	public ResearchLevel pre() {
		return this.ordinal() > 0 ? levelList[this.ordinal()-1] : this;
	}

	public ResearchLevel post() {
		return this.ordinal() < levelList.length-1 ? levelList[this.ordinal()+1] : this;
	}

	@Override
	//@SideOnly(Side.CLIENT)
	public String getTitle() {
		return this.getDisplayName();
	}

	@Override
	//@SideOnly(Side.CLIENT)
	public String getShortDesc() {
		return "More of the world becomes visible to you.";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderIcon(RenderItem ri, FontRenderer fr, int x, int y) {
		ReikaGuiAPI.instance.drawItemStack(ri, fr, ChromaItems.FRAGMENT.getStackOf(), x, y);
	}

	@Override
	public String getFormatting() {
		return EnumChatFormatting.BOLD.toString();
	}

	@Override
	public boolean giveToPlayer(EntityPlayer ep, boolean notify) {
		return ChromaResearchManager.instance.setPlayerResearchLevel(ep, this, notify);
	}

	public boolean isAtLeast(ResearchLevel rl) {
		return rl.ordinal() <= this.ordinal();
	}

	public int getDifference(ResearchLevel rl) {
		return Math.abs(rl.ordinal()-this.ordinal());
	}
}

/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.Auxiliary.Interfaces.EnergyLinkingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Magic.Progression.ResearchLevel;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingAuto;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;


public abstract class RepeaterRecipe extends MultiBlockCastingRecipe implements EnergyLinkingRecipe {

	private final ChromaTiles tile;

	public RepeaterRecipe(ChromaTiles c, ItemStack main) {
		super(c.getCraftedProduct(), main);
		tile = c;

		this.addRune(CrystalElement.BLACK, 0, -1, 5);
		this.addRune(CrystalElement.BLACK, 0, -1, -5);
		this.addRune(CrystalElement.BLACK, 5, -1, 0);
		this.addRune(CrystalElement.BLACK, -5, -1, 0);

		this.addRune(CrystalElement.WHITE, 5, -1, 5);
		this.addRune(CrystalElement.WHITE, -5, -1, -5);
		this.addRune(CrystalElement.WHITE, 5, -1, -5);
		this.addRune(CrystalElement.WHITE, -5, -1, 5);

		this.addRune(CrystalElement.RED, -5, -1, -4);
		this.addRune(CrystalElement.RED, 5, -1, 4);
		this.addRune(CrystalElement.BLUE, 5, -1, -4);
		this.addRune(CrystalElement.BLUE, -5, -1, 4);

		this.addRune(CrystalElement.GREEN, -4, -1, -5);
		this.addRune(CrystalElement.GREEN, 4, -1, 5);
		this.addRune(CrystalElement.YELLOW, 4, -1, -5);
		this.addRune(CrystalElement.YELLOW, -4, -1, 5);
	}

	@Override
	public int getDuration() {
		return (int)(2.4*super.getDuration());
	}

	@Override
	public float[] getHarmonics() {
		return new float[]{(float)MusicKey.A5.getRatio(MusicKey.D5), (float)MusicKey.Fs5.getRatio(MusicKey.D5)};
	}

	@Override
	public final boolean canRunRecipe(TileEntity te, EntityPlayer ep) {
		return super.canRunRecipe(te, ep) && ChromaResearchManager.instance.getPlayerResearchLevel(ep).ordinal() >= ResearchLevel.NETWORKING.ordinal() && ProgressStage.BLOWREPEATER.isPlayerAtStage(ep);
	}

	@Override
	public final NBTTagCompound handleNBTResult(TileEntityCastingTable te, EntityPlayer ep, NBTTagCompound originalCenter, NBTTagCompound tag) {
		if (tile.isTurbochargeableRepeater()) {
			if (tag == null)
				tag = new NBTTagCompound();
			tag.setBoolean("boosted", false);
		}
		if (tile.getFragment().level.ordinal() >= ResearchLevel.NETWORKING.ordinal()) {
			if (tag == null)
				tag = new NBTTagCompound();
			tag.setString("caster", ep.getUniqueID().toString());
		}
		return tag;
	}

	@Override
	public void onCrafted(TileEntityCastingTable te, EntityPlayer ep, ItemStack output, int amount) {
		super.onCrafted(te, ep, output, amount);

		ProgressStage.REPEATER.stepPlayerTo(ep);
	}

	@Override
	protected final void filterMatchTags(ItemStack is) {
		super.filterMatchTags(is);
		is.stackTagCompound.removeTag("boosted");
		is.stackTagCompound.removeTag("caster");
	}

	@Override
	public final float getAutomationCostFactor(TileEntityCastingAuto ae, TileEntityCastingTable te, ItemStack is) {
		switch(tile) {
			case REPEATER:
				return 1.5F;
			case COMPOUND:
				return 2;
			case BROADCAST:
				return 3;
			default:
				return 1;
		}
	}

	@Override
	public final boolean requiresTuningKey() {
		return true;
	}
}

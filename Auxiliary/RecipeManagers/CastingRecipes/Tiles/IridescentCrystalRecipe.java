/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingAuto;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class IridescentCrystalRecipe extends PylonCastingRecipe {

	public IridescentCrystalRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.iridChunk, -2, 0);
		this.addAuxItem(ChromaStacks.iridChunk, -4, 0);
		this.addAuxItem(ChromaStacks.iridChunk, 2, 0);
		this.addAuxItem(ChromaStacks.iridChunk, 4, 0);
		this.addAuxItem(ChromaStacks.iridChunk, 0, -2);
		this.addAuxItem(ChromaStacks.iridChunk, 0, -4);

		this.addAuxItem(Blocks.obsidian, -4, 2);
		this.addAuxItem(Blocks.obsidian, -2, 2);
		this.addAuxItem(Blocks.obsidian, 0, 2);
		this.addAuxItem(Blocks.obsidian, 2, 2);
		this.addAuxItem(Blocks.obsidian, 4, 2);

		this.addAuxItem(Blocks.glowstone, 2, -2);
		this.addAuxItem(Blocks.glowstone, -2, -2);

		this.addAuraRequirement(CrystalElement.YELLOW, 15000);
		this.addAuraRequirement(CrystalElement.BLACK, 25000);
		this.addAuraRequirement(CrystalElement.PURPLE, 10000);
	}

	@Override
	public int getDuration() {
		return 4*super.getDuration();
	}

	@Override
	public void onCrafted(TileEntityCastingTable te, EntityPlayer ep, int amount) {
		super.onCrafted(te, ep, amount);
		//ProgressStage.POWERCRYSTAL.stepPlayerTo(ep);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 128;
	}

	@Override
	public boolean canBeStacked() {
		return true;
	}

	@Override
	public float getConsecutiveStackingTimeFactor(TileEntityCastingTable te) {
		return 0.975F;
	}

	@Override
	public float getAutomationCostFactor(TileEntityCastingAuto ae, TileEntityCastingTable te, ItemStack is) {
		return is == null ? 4 : 2;
	}

	@Override
	public void onRecipeTick(TileEntityCastingTable te) {
		int tick = te.getCraftingTick();
		if (te.worldObj.isRemote) {
			this.doParticleFX(te, tick);
		}
		else {

		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticleFX(TileEntityCastingTable te, int tick) {
		double px = te.xCoord+te.getRandom().nextDouble();
		double pz = te.zCoord+te.getRandom().nextDouble();
		double vy = 0.125;
		double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.0625);
		double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.0625);
		float g = (float)ReikaRandomHelper.getRandomBetween(0D, 0.125);
		EntityBlurFX fx = new EntityBlurFX(te.worldObj, px, te.yCoord+1, pz, vx, vy, vz).setIcon(ChromaIcons.CHROMA).setBasicBlend().setGravity(g);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		int t = this.getDuration()-tick;
		if (t%18 == 0) {
			CrystalElement e = CrystalElement.elements[t/18%16];
			for (int i = 0; i < 64; i++) {
				px = te.xCoord+te.getRandom().nextDouble();
				pz = te.zCoord+te.getRandom().nextDouble();
				vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
				vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
				fx = new EntityBlurFX(te.worldObj, px, te.yCoord+1, pz, vx, vy, vz).setIcon(ChromaIcons.FADE_STAR).setColor(e.getColor());
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
			float f = (float)CrystalMusicManager.instance.getDingPitchScale(e);
			ReikaSoundHelper.playClientSound(ChromaSounds.BUFFERWARNING_EMPTY, te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5, 1, f);
		}
	}

}

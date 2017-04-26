/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Block.Decoration.BlockEtherealLight.Flags;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFloatingSeedsFX;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingAuto;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PylonTurboRecipe extends PylonCastingRecipe {

	public PylonTurboRecipe(ItemStack out, ItemStack main, RecipeCrystalRepeater repeater) {
		super(out, main);

		this.addAuxItem(ChromaStacks.iridChunk, 0, -4);
		this.addAuxItem(Blocks.obsidian, 0, -2);

		this.addAuxItem(ChromaStacks.purityDust, 2, 0);
		this.addAuxItem(ChromaStacks.purityDust, -2, 0);

		this.addAuxItem(ChromaStacks.glowbeans, 4, 0);
		this.addAuxItem(ChromaStacks.glowbeans, -4, 0);

		this.addAuxItem(Items.glowstone_dust, 4, 2);
		this.addAuxItem(Items.glowstone_dust, -4, 2);

		this.addAuxItem(ChromaStacks.chargedWhiteShard, 2, -2);
		this.addAuxItem(ChromaStacks.chargedWhiteShard, -2, -2);

		this.addAuxItem(ChromaStacks.focusDust, 2, -4);
		this.addAuxItem(ChromaStacks.focusDust, -2, -4);

		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getStackOf(), -2, 2);
		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getStackOf(), 0, 2);
		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getStackOf(), 2, 2);

		this.addAuxItem(ChromaBlocks.PYLONSTRUCT.getStackOf(), 0, 4);

		this.addAuxItem(Items.iron_ingot, -4, 4);
		this.addAuxItem(Items.iron_ingot, 4, 4);

		this.addAuxItem(Items.gold_ingot, -2, 4);
		this.addAuxItem(Items.gold_ingot, 2, 4);

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			this.addAuraRequirement(e, 50000);
			this.addRuneRingRune(e);
		}
		this.addAuraRequirement(CrystalElement.PURPLE, 250000);
		this.addAuraRequirement(CrystalElement.BLACK, 150000);
		this.addAuraRequirement(CrystalElement.BLUE, 100000);
		this.addAuraRequirement(CrystalElement.YELLOW, 200000);
		this.addAuraRequirement(CrystalElement.WHITE, 75000);

		this.addRunes(repeater.getRunes());
	}

	@Override
	public int getNumberProduced() {
		return 9;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 16;
	}

	@Override
	public int getDuration() {
		return super.getDuration()*24;
	}

	@Override
	public int getPenaltyThreshold() {
		return this.getTypicalCraftedAmount();
	}

	@Override
	public float getPenaltyMultiplier() {
		return 0;
	}

	@Override
	public boolean canBeStacked() {
		return true;
	}

	@Override
	public void onRecipeTick(TileEntityCastingTable te) {
		if (!te.worldObj.isRemote) {
			int tick = te.getCraftingTick();
			for (int i = 0; i < EffectType.list.length; i++) {
				EffectType e = EffectType.list[i];
				if (e.getChance(te.getRandom(), tick)) {
					e.doEffect(te);
					ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.PYLONTURBORECIPE.ordinal(), te, 64, i);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void onClientSideRandomTick(TileEntityCastingTable te, int effect) {
		//ReikaJavaLibrary.pConsole("P"+var);
		EffectType.list[effect].doEffect(te);
	}

	private static enum EffectType {
		PARTICLERING(false),
		GLOWAREA(true),
		SKYPARTICLE(false);

		public final boolean runServerside;

		private static final EffectType[] list = values();

		private EffectType(boolean b) {
			runServerside = b;
		}

		public boolean getChance(Random rand, int tick) {
			switch(this) {
				case PARTICLERING:
					return rand.nextInt(10+tick/8) == 0;
				case GLOWAREA:
					return rand.nextInt(20+tick/2) == 0;
				case SKYPARTICLE:
					return rand.nextInt(10+tick%64) == 0;
				default:
					return false;
			}
		}

		public void doEffect(TileEntityCastingTable te) {
			if (!te.worldObj.isRemote && !runServerside)
				return;
			switch(this) {
				case PARTICLERING:
					genParticleRing(te);
					break;
				case GLOWAREA:
					if (te.worldObj.isRemote)
						glowAreaClient(te);
					else
						glowArea(te);
					break;
				case SKYPARTICLE:
					generateSkyParticle(te);
					break;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private static void genParticleRing(TileEntityCastingTable te) {
		double x = te.xCoord+0.5;
		double y = te.yCoord+0.5;
		double z = te.zCoord+0.5;
		ReikaSoundHelper.playClientSound(ChromaSounds.ORB, x, y, z, 1, 1F);
		ReikaSoundHelper.playClientSound(ChromaSounds.ORB, x, y, z, 1, 0.5F);
		for (double a = 0; a < 360; a += 2) {
			float s = (float)ReikaRandomHelper.getRandomBetween(2D, 5D);
			EntityFloatingSeedsFX fx = (EntityFloatingSeedsFX)new EntityFloatingSeedsFX(te.worldObj, x, y, z, a, 0).setScale(s).setNoSlowdown();
			int c = CrystalElement.getBlendedColor((int)a, 360/16);
			fx.setColor(c);
			fx.particleVelocity = 0.25;
			fx.freedom *= 2;
			fx.angleVelocity *= 8;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private static void glowArea(TileEntityCastingTable te) {
		for (int i = 2; i < 6; i++) {
			for (int k = 1; k < 8; k += 1) {
				Coordinate c = new Coordinate(te).offset(ForgeDirection.VALID_DIRECTIONS[i], k);
				if (c.getBlock(te.worldObj).isAir(te.worldObj, c.xCoord, c.yCoord, c.zCoord))
					c.setBlock(te.worldObj, ChromaBlocks.LIGHT.getBlockInstance(), Flags.DECAY.getFlag());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private static void glowAreaClient(TileEntityCastingTable te) {
		double x = te.xCoord+0.5;
		double y = te.yCoord+0.5;
		double z = te.zCoord+0.5;
		ReikaSoundHelper.playClientSound(ChromaSounds.FIRE, x, y, z, 1, 2F);
		Random rand = te.getRandom();
		int n = 64+rand.nextInt(128);
		for (int i = 0; i < n; i++) {
			float s = (float)ReikaRandomHelper.getRandomBetween(5D, 10D);
			int c = ReikaColorAPI.mixColors(CrystalElement.BLUE.getColor(), 0xffffff, rand.nextFloat());
			double px = ReikaRandomHelper.getRandomPlusMinus(x, 12);
			double py = ReikaRandomHelper.getRandomPlusMinus(y, 0.5);
			double pz = ReikaRandomHelper.getRandomPlusMinus(z, 12);
			int l = 20+rand.nextInt(60);
			float g = 2*(float)ReikaRandomHelper.getRandomBetween(0.03125, 0.25);
			double v = ReikaRandomHelper.getRandomBetween(0.125, 0.5);
			EntityFX fx = new EntityBlurFX(te.worldObj, px, py, pz, 0, v, 0).setRapidExpand().setScale(s).setColor(c).setLife(l).setIcon(ChromaIcons.FADE_RAY).setColliding().setGravity(g);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void generateSkyParticle(TileEntityCastingTable te) {
		double x = te.xCoord+0.5;
		double y = te.yCoord+0.5;
		double z = te.zCoord+0.5;
		double r = 1.5;
		double v = ReikaRandomHelper.getRandomBetween(0.125, 0.5);
		ReikaSoundHelper.playClientSound(ChromaSounds.DISCHARGE, x, y, z, 1, 0.5F);
		int l = 150;
		int c1 = CrystalElement.YELLOW.getColor();
		int c3 = CrystalElement.WHITE.getColor();
		int c2 = ReikaColorAPI.mixColors(c1, c3, 0.5F);
		for (double d = -r; d <= r; d += 0.03125) {
			double dy = y+d;
			double ms = 6;
			double s = ms-Math.abs(d)*ms/r;
			double s2 = s*0.5;
			double s3 = s*0.25;
			EntityFX fx = new EntityBlurFX(te.worldObj, x, dy, z, 0, v, 0).setNoSlowdown().setRapidExpand().setScale((float)s).setColor(c1).setLife(l).setIcon(ChromaIcons.FADE_STAR);
			EntityFX fx2 = new EntityBlurFX(te.worldObj, x, dy, z, 0, v, 0).setNoSlowdown().setRapidExpand().setScale((float)s2).setColor(c2).setLife(l).setIcon(ChromaIcons.FADE_STAR).lockTo(fx);
			EntityFX fx3 = new EntityBlurFX(te.worldObj, x, dy, z, 0, v, 0).setNoSlowdown().setRapidExpand().setScale((float)s3).setColor(c3).setLife(l).setIcon(ChromaIcons.FADE_STAR).lockTo(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx3);
		}

		EntityFX fx = new EntityBlurFX(te.worldObj, x, y, z).setRapidExpand().setScale(24).setColor(c1).setLife(l).setIcon(ChromaIcons.RINGFLARE);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	public float getAutomationCostFactor(TileEntityCastingAuto ae, TileEntityCastingTable te, ItemStack is) {
		return is == null ? 8 : ReikaItemHelper.matchStacks(is, ChromaStacks.lumenCore) ? 4 : 2;
	}

	@Override
	public int getEnhancedTableAccelerationFactor() {
		return 8;
	}

}

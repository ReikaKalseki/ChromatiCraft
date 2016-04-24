/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import java.util.Collection;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CoreRecipe;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.RecipeCrystalRepeater;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntitySparkleFX;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PortalRecipe extends PylonRecipe implements CoreRecipe {

	public PortalRecipe(ItemStack out, ItemStack main, RecipeCrystalRepeater repeater) {
		super(out, main);

		this.addAuxItem(ChromaStacks.enderDust, -2, 0);
		this.addAuxItem(ChromaStacks.enderDust, 2, 0);
		this.addAuxItem(ChromaStacks.enderDust, 0, 2);
		this.addAuxItem(ChromaStacks.enderDust, 0, -2);

		this.addAuxItem(ChromaStacks.spaceDust, -2, -2);
		this.addAuxItem(ChromaStacks.spaceDust, 2, -2);
		this.addAuxItem(ChromaStacks.spaceDust, -2, 2);
		this.addAuxItem(ChromaStacks.spaceDust, 2, 2);

		this.addAuxItem(Items.glowstone_dust, -4, 0);
		this.addAuxItem(Items.glowstone_dust, 4, 0);
		this.addAuxItem(Items.ender_eye, 0, 4);
		this.addAuxItem(Items.ender_eye, 0, -4);

		this.addAuxItem(ChromaStacks.complexIngot, -4, -4);
		this.addAuxItem(ChromaStacks.complexIngot, 4, 4);

		this.addAuxItem(Blocks.beacon, -4, 4);
		this.addAuxItem(Blocks.beacon, 4, -4);

		this.addAuxItem(Blocks.end_stone, -2, -4);
		this.addAuxItem(Blocks.end_stone, 4, -2);
		this.addAuxItem(Blocks.end_stone, 2, 4);
		this.addAuxItem(Blocks.end_stone, -4, 2);

		this.addAuxItem(Items.emerald, 2, -4);
		this.addAuxItem(Items.emerald, -4, -2);
		this.addAuxItem(Items.emerald, -2, 4);
		this.addAuxItem(Items.emerald, 4, 2);

		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			this.addAuraRequirement(e, e.isPrimary() ? 120000 : 60000);
			this.addRuneRingRune(e);
		}
		this.addRunes(repeater.getRunes());
	}

	@Override
	public int getNumberProduced() {
		return 9;
	}

	@Override
	public void onCrafted(TileEntityCastingTable te, EntityPlayer ep) {
		super.onCrafted(te, ep);
	}

	@Override
	public void onRecipeTick(TileEntityCastingTable te) {
		if (!te.worldObj.isRemote) {
			int tick = te.getCraftingTick();
			for (int i = 0; i < EffectType.list.length; i++) {
				EffectType e = EffectType.list[i];
				if (e.getChance(te.getRandom(), tick)) {
					e.doEffect(te);
					ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.PORTALRECIPE.ordinal(), te, 64, i);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void onClientSideRandomTick(TileEntityCastingTable te, int effect) {
		//ReikaJavaLibrary.pConsole("P"+var);
		EffectType.list[effect].doEffect(te);
	}

	@Override
	public ChromaSounds getSoundOverride(TileEntityCastingTable te, int soundTimer) {
		return null;
	}

	@Override
	protected void getRequiredProgress(Collection<ProgressStage> c) {
		c.addAll(ProgressionManager.instance.getPrereqs(ProgressStage.DIMENSION));
	}

	@Override
	public int getExperience() {
		return super.getExperience()*50;
	}

	@Override
	public int getDuration() {
		return 2400;
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 1;
	}

	@Override
	public int getPenaltyThreshold() {
		return 1;
	}

	@Override
	public float getPenaltyMultiplier() {
		return 0;
	}

	private static enum EffectType {
		EXPLODE(true),
		LIGHTNING(true),
		INFUSE(false);

		public final boolean runServerside;

		private static final EffectType[] list = values();

		private EffectType(boolean b) {
			runServerside = b;
		}

		public boolean getChance(Random rand, int tick) {
			switch(this) {
				case EXPLODE:
					return rand.nextInt(5+tick/2) == 0;
				case LIGHTNING:
					return rand.nextInt(10+tick/4) == 0;
				case INFUSE:
					return rand.nextInt(5+tick%32) == 0;
				default:
					return false;
			}
		}

		public void doEffect(TileEntityCastingTable te) {
			if (!te.worldObj.isRemote && !runServerside)
				return;
			switch(this) {
				case EXPLODE:
					genExplosion(te);
					break;
				case LIGHTNING:
					genLightning(te);
					break;
				case INFUSE:
					generateBurstParticles(te);
					break;
			}
		}
	}

	private static void genExplosion(TileEntityCastingTable te) {
		int x = ReikaRandomHelper.getRandomPlusMinus(te.xCoord, 8);
		int y = ReikaRandomHelper.getRandomPlusMinus(te.yCoord, 2);
		int z = ReikaRandomHelper.getRandomPlusMinus(te.zCoord, 8);
		ReikaParticleHelper.EXPLODE.spawnAroundBlock(te.worldObj, x, y, z, 4);
		ReikaSoundHelper.playSoundAtBlock(te.worldObj, x, y, z, "random.explode");
		ReikaSoundHelper.playSoundAtBlock(te.worldObj, te.xCoord, te.yCoord, te.zCoord, "random.explode");
	}

	private static void genLightning(TileEntityCastingTable te) {
		te.worldObj.addWeatherEffect(new EntityLightningBolt(te.worldObj, te.xCoord, te.yCoord, te.zCoord));
		ChromaSounds.DISCHARGE.playSoundAtBlock(te);
	}

	@SideOnly(Side.CLIENT)
	private static void generateBurstParticles(TileEntityCastingTable te) {
		int x = te.xCoord;
		int y = te.yCoord;
		int z = te.zCoord;
		ReikaSoundHelper.playClientSound(ChromaSounds.INFUSE, x+0.5, y+0.5, z+0.5, 1, 1);
		ElementTagCompound tag = ElementTagCompound.getUniformTag(1);
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			if (e.isPrimary()) {
				tag.setTag(e, 4);
			}
		}
		WeightedRandom<CrystalElement> w = tag.asWeightedRandom();
		for (int i = 0; i < 32; i++) {
			CrystalElement e = w.getRandomEntry();
			double ang = te.getRandom().nextDouble()*360;
			double v = 0.125;
			double vx = v*Math.cos(Math.toRadians(ang));
			double vy = ReikaRandomHelper.getRandomPlusMinus(v, v);
			double vz = v*Math.sin(Math.toRadians(ang));
			int c = ReikaColorAPI.mixColors(e.getColor(), 0xffffff, 0.5F);
			EntityFX fx = new EntitySparkleFX(te.worldObj, x+0.5, y+0.5, z+0.5, vx, vy, vz).setColor(c).setScale(1);
			fx.noClip = true;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

}

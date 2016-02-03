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
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CoreRecipe;
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

		this.addAuxItem(Blocks.beacon, -4, -4);
		this.addAuxItem(Blocks.beacon, 4, -4);
		this.addAuxItem(Blocks.beacon, -4, 4);
		this.addAuxItem(Blocks.beacon, 4, 4);

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
			this.addAuraRequirement(e, e.isPrimary() ? 100000 : 50000);
			this.addRuneRingRune(e);
		}
		this.addRunes(repeater);
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
			if (te.getRandom().nextInt(5+tick/2) == 0) {
				int x = ReikaRandomHelper.getRandomPlusMinus(te.xCoord, 8);
				int y = ReikaRandomHelper.getRandomPlusMinus(te.yCoord, 2);
				int z = ReikaRandomHelper.getRandomPlusMinus(te.zCoord, 8);
				genExplosion(te, x, y, z);
				//ReikaJavaLibrary.pConsole("S0");
				ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.PORTALRECIPE.ordinal(), te, 48, x, y, z, 0);
			}
			else if (te.getRandom().nextInt(10+tick/4) == 0) {
				genLightning(te);
				//ReikaJavaLibrary.pConsole("S1");
				ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.PORTALRECIPE.ordinal(), te, 48, 0, 0, 0, 1);
			}
			else if (te.getRandom().nextInt(5+tick%32) == 0) {
				ChromaSounds.INFUSE.playSoundAtBlock(te);
				//ReikaJavaLibrary.pConsole("S2");
				ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.PORTALRECIPE.ordinal(), te, 48, 0, 0, 0, 2);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void onClientSideRandomTick(TileEntityCastingTable te, int x, int y, int z, int var) {
		//ReikaJavaLibrary.pConsole("P"+var);
		switch(var) {
			case 0:
				genExplosion(te, x, y, z);
				break;
			case 1:
				genLightning(te);
				break;
			case 2:
				generateBurstParticles(te);
				break;
		}
	}

	private static void genExplosion(TileEntityCastingTable te, int x, int y, int z) {
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
			EntityFX fx = new EntitySparkleFX(te.worldObj, te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5, vx, vy, vz).setColor(c).setScale(1);
			fx.noClip = true;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public ChromaSounds getSoundOverride(int soundTimer) {
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

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.crafting.IInfusionStabiliser;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CrystalRenderedBlock;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.ISBRH.CrystalRenderer;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFloatingSeedsFX;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Strippable(value={"thaumcraft.api.crafting.IInfusionStabiliser"})
public abstract class CrystalBlock extends CrystalTypeBlock implements CrystalRenderedBlock, IInfusionStabiliser, SemiUnbreakable {

	protected final IIcon[] icons = new IIcon[CrystalElement.elements.length];

	protected static final Random rand = new Random();

	public CrystalBlock(Material mat) {
		super(mat);
		this.setHardness(1F);
		this.setResistance(2F);
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		return this.isUnbreakable(world, x, y, z, world.getBlockMetadata(x, y, z)) ? -1 : super.getPlayerRelativeBlockHardness(ep, world, x, y, z);
	}

	public boolean isUnbreakable(World world, int x, int y, int z, int meta) {
		return false;
	}

	@Override
	public final void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
			CrystalElement e = CrystalElement.elements[world.getBlockMetadata(x, y, z)];
			ding(world, x, y, z, e, (float)getDingPitchFromRedstone(e, world.getBlockPowerInput(x, y, z)));
		}
	}

	private static double getDingPitchFromRedstone(CrystalElement e, int power) {
		if (power >= 12) {
			return CrystalMusicManager.instance.getOctave(e);
		}
		else if (power >= 8) {
			return CrystalMusicManager.instance.getFifth(e);
		}
		else if (power >= 4) {
			return CrystalMusicManager.instance.getThird(e);
		}
		else {
			return CrystalMusicManager.instance.getDingPitchScale(e);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public final IIcon getIcon(int s, int meta) {
		return icons[meta];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public final void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			icons[i] = ico.registerIcon("ChromatiCraft:crystal/crystal_outline");
		}
	}

	@Override
	public final int getRenderType() {
		return ChromatiCraft.proxy.crystalRender;
	}

	@Override
	public final boolean isOpaqueCube() {
		return false;
	}

	@Override
	public final boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public final int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass)
	{
		CrystalRenderer.renderPass = pass;
		return pass <= 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		CrystalElement e = this.getCrystalElement(world, x, y, z);

		this.doParticles(world, x, y, z, e, rand);

		if (this.shouldGiveEffects(e) && this.performEffect(e)) {
			if (rand.nextInt(3) == 0)
				ReikaPacketHelper.sendUpdatePacket(ChromatiCraft.packetChannel, ChromaPackets.CRYSTALEFFECT.ordinal(), world, x, y, z);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer) {
		int x = target.blockX;
		int y = target.blockY;
		int z = target.blockZ;;
		CrystalElement e = this.getCrystalElement(world, x, y, z);

		this.doParticles(world, x, y, z, e, rand);

		if (this.shouldGiveEffects(e) && this.performEffect(e)) {
			if (e != CrystalElement.PURPLE && e != CrystalElement.BROWN) //prevent exploit
				ReikaPacketHelper.sendUpdatePacket(ChromatiCraft.packetChannel, ChromaPackets.CRYSTALEFFECT.ordinal(), world, x, y, z);
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z, CrystalElement e, Random rand) {
		EntityFX fx;
		if (rand.nextInt(20) > 0) {
			double rx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.5);
			double rz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.5);
			double ry = ReikaRandomHelper.getRandomPlusMinus(y+0.5+0.125, 0.5);
			float s = 1+rand.nextFloat()*1.5F;
			int l = 5+rand.nextInt(60);
			int n = 3+rand.nextInt(6);
			float f = s/16F;
			float s2 = s/4F;
			for (int i = 0; i < n; i++) {
				double rrx = ReikaRandomHelper.getRandomPlusMinus(rx, f);
				double rry = ReikaRandomHelper.getRandomPlusMinus(ry, f);
				double rrz = ReikaRandomHelper.getRandomPlusMinus(rz, f);
				fx = new EntityBlurFX(e, world, rrx, rry, rrz, 0, 0, 0).setLife(l).setScale(s2).setIcon(ChromaIcons.SPARKLEPARTICLE);
				if (rand.nextBoolean())
					((EntityBlurFX)fx).setRapidExpand();
				if (rand.nextBoolean())
					((EntityBlurFX)fx).setBasicBlend();
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
		else {
			double rx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.35);
			double rz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.35);
			fx = new EntityFloatingSeedsFX(world, rx, y+0.5, rz, 0, 90).setColor(e.getColor()).setIcon(ChromaIcons.CENTER).setScale(4).setLife(120).setColliding();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public void updateEffects(World world, int x, int y, int z) {
		if (!world.isRemote) {
			CrystalElement color = CrystalElement.elements[world.getBlockMetadata(x, y, z)];
			if (this.shouldMakeNoise()) {
				float f1 = rand.nextFloat();
				float f2 = rand.nextFloat();
				float f3 = 0.5F*((f1-f2)*0.7F+1.8F);
				world.playSoundEffect(x+0.5, y+0.5, z+0.5, "random.orb", 0.05F, f3/*this.getRandomPitch(color)*/);
			}
			int r = this.getRange();
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).expand(r, r, r);
			List<EntityLivingBase> inbox = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
			Collections.shuffle(inbox);
			this.applyEffect(color, inbox, x, y, z, r);
		}
	}

	private void applyEffect(CrystalElement color, List<EntityLivingBase> li, int x, int y, int z, int r) {
		int level = this.getPotionLevel(color);
		int dura = this.getDuration(color);
		boolean boost = level > 0;
		boolean player = false;
		for (EntityLivingBase e : li) {
			if (e instanceof EntityPlayer) {
				if (player)
					continue;
				else
					player = true;
			}
			if (ReikaMathLibrary.py3d(e.posX-x-0.5, e.posY+e.getEyeHeight()/2F-y-0.5, e.posZ-z-0.5) <= r) {
				this.applyEffectFromColor(dura, level, e, color);
			}
		}
	}

	public abstract boolean shouldMakeNoise();

	public abstract boolean shouldGiveEffects(CrystalElement e);
	public abstract boolean performEffect(CrystalElement e);

	public abstract int getRange();

	public abstract int getDuration(CrystalElement e);

	public boolean renderAllArms() {
		return this.renderBase();
	}

	public abstract int getPotionLevel(CrystalElement e);

	public static void applyEffectFromColor(int dura, int level, EntityLivingBase e, CrystalElement color) {
		if (CrystalPotionController.shouldBeHostile(e.worldObj)) {
			switch(color) {
				case ORANGE:
					e.setFire(2);
					break;
				case RED:
					e.attackEntityFrom(DamageSource.magic, 1);
					break;
				case PURPLE:
					if (!e.worldObj.isRemote && rand.nextInt(5) == 0 && e instanceof EntityPlayer) {
						EntityPlayer ep = (EntityPlayer)e;
						if (ep.experienceLevel > 0) {
							ep.addExperienceLevel(-1);
						}
						else {
							ep.experienceTotal = 0;
							ep.experience = 0;
						}
					}
					break;
				case BROWN:
					if (!e.isPotionActive(Potion.confusion.id))
						e.addPotionEffect(new PotionEffect(Potion.confusion.id, (int)(dura*1.8), level, true));
					break;
				case LIME:
					e.addPotionEffect(new PotionEffect(Potion.jump.id, dura, -5, true));
					break;
				default:
					PotionEffect eff = CrystalPotionController.getNetherEffectFromColor(color, dura, level);
					if (CrystalPotionController.isPotionAllowed(eff, e))
						e.addPotionEffect(eff);
			}
		}
		else {
			switch(color) {
				case BLACK:
					if (e instanceof EntityMob) {  //clear AI
						EntityMob m = (EntityMob)e;
						m.setAttackTarget(null);
						m.getNavigator().clearPathEntity();
					}
					break;
				case WHITE:
					//ReikaPotionHelper.clearPotionsExceptPerma(e);
					ReikaPotionHelper.clearBadPotions(e, level > 0 ? null : CrystalPotionController.ignoredBadPotionsForLevelZero());
					break;
				case PURPLE:
					if (e instanceof EntityPlayer && !e.worldObj.isRemote && (level > 0 || rand.nextInt(2) == 0)) {
						EntityPlayer ep = (EntityPlayer)e;
						e.playSound("random.orb", 0.2F, rand.nextFloat()*2);
						ep.addExperience(1);
					}
					break;
				default:
					PotionEffect eff = CrystalPotionController.getEffectFromColor(color, dura, level);
					if (eff != null) {
						if (CrystalPotionController.isPotionAllowed(eff, e)) {
							e.addPotionEffect(eff);
						}
					}
			}
		}
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity e) {
		return false;
	}

	public final int getTintColor(int meta) {
		int c0 = ReikaColorAPI.getModifiedSat(CrystalElement.elements[meta].getColor(), 0.65F);
		int c1 = ReikaDyeHelper.dyes[meta].color;
		//int c2 = ReikaColorAPI.getColorWithBrightnessMultiplier(c0, 0.85F);
		return ReikaColorAPI.mixColors(c0, c1, 0.65F);
	}

	public final boolean canStabaliseInfusion(World world, int x, int y, int z) {
		return true;
	}
}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
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
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CrystalRenderedBlock;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.ISBRH.CrystalRenderer;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class CrystalBlock extends Block implements CrystalRenderedBlock {

	protected final IIcon[] icons = new IIcon[ReikaDyeHelper.dyes.length];

	private static final Random rand = new Random();

	public CrystalBlock(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		this.setHardness(1F);
		this.setResistance(2F);
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
	public final int getLightValue(IBlockAccess iba, int x, int y, int z) {
		int color = CrystalElement.elements[iba.getBlockMetadata(x, y, z)].getColor();
		int l = this.getBrightness(iba, x, y, z);
		return ModList.COLORLIGHT.isLoaded() ? color&0xff << 15 | color&0xff00 << 10 | color&0xff0000 << 5 | l : l;
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

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		int color = world.getBlockMetadata(x, y, z);
		double[] v = ReikaDyeHelper.getColorFromDamage(color).getRedstoneParticleVelocityForColor();
		ReikaParticleHelper.spawnColoredParticles(world, x, y, z, v[0], v[1], v[2], 1);
		if (rand.nextInt(3) == 0)
			ReikaPacketHelper.sendUpdatePacket(ChromatiCraft.packetChannel, ChromaPackets.CRYSTALEFFECT.ordinal(), world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer)
	{
		Random rand = new Random();
		int x = target.blockX;
		int y = target.blockY;
		int z = target.blockZ;
		int color = world.getBlockMetadata(x, y, z);
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(color);
		double[] v = dye.getRedstoneParticleVelocityForColor();
		ReikaParticleHelper.spawnColoredParticles(world, x, y, z, v[0], v[1], v[2], 4);
		if (dye != ReikaDyeHelper.PURPLE) //prevent an XP exploit
			ReikaPacketHelper.sendUpdatePacket(ChromatiCraft.packetChannel, ChromaPackets.CRYSTALEFFECT.ordinal(), world, x, y, z);
		return false;
	}

	public void updateEffects(World world, int x, int y, int z) {
		if (!world.isRemote) {
			if (this.shouldMakeNoise()) {
				float f1 = rand.nextFloat();
				float f2 = rand.nextFloat();
				world.playSoundEffect(x+0.5, y+0.5, z+0.5, "random.orb", 0.05F, 0.5F*((f1-f2)*0.7F+1.8F));
			}
			CrystalElement color = CrystalElement.elements[world.getBlockMetadata(x, y, z)];
			if (this.shouldGiveEffects(color)) {
				int r = this.getRange();
				AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1).expand(r, r, r);
				List<EntityLivingBase> inbox = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
				for (EntityLivingBase e : inbox) {
					if (ReikaMathLibrary.py3d(e.posX-x-0.5, e.posY+e.getEyeHeight()/2F-y-0.5, e.posZ-z-0.5) <= 4) {
						this.applyEffectFromColor(this.getDuration(color), this.getPotionLevel(color), e, color);
					}
				}
			}
		}
	}

	public abstract boolean shouldMakeNoise();

	public abstract boolean shouldGiveEffects(CrystalElement e);

	public abstract int getRange();

	public abstract int getDuration(CrystalElement e);

	public abstract int getBrightness(IBlockAccess iba, int x, int y, int z);

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
					e.addPotionEffect(new PotionEffect(Potion.confusion.id, (int)(dura*1.8), level));
				break;
			case LIME:
				e.addPotionEffect(new PotionEffect(Potion.jump.id, dura, -5));
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
				e.clearActivePotions();
				break;
			case PURPLE:
				if (e instanceof EntityPlayer && !e.worldObj.isRemote && (level > 0 || rand.nextInt(2) == 0)) {
					EntityPlayer ep = (EntityPlayer)e;
					e.playSound("random.orb", 1, 1);
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
}

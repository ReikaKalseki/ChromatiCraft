/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Worldgen;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Items.ItemUnknownArtefact;
import Reika.ChromatiCraft.Items.ItemUnknownArtefact.ArtefactTypes;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaISBRH;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockUnknownArtefact extends Block {

	public BlockUnknownArtefact(Material mat) {
		super(mat);

		this.setResistance(300000);
		this.setHardness(12);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
		this.setBlockBounds(0, 0, 0, 1, 0.75F, 1);
	}

	@Override
	public int getRenderType() {
		return ChromaISBRH.artefact.getRenderID();
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		this.onInteracted(world, x, y, z, ep);
		return true;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer ep) {
		this.onInteracted(world, x, y, z, ep);
	}

	private void onInteracted(World world, int x, int y, int z, EntityPlayer ep) {
		if (world.rand.nextInt(8) == 0 && !world.isRemote) {
			ChromaSounds.MONUMENTRAY.playSoundNoAttenuation(world, x+0.5, y+0.5, z+0.5, 1, 0.625F, 64);
			//ChromaSounds.MONUMENTRAY.playSoundAtBlockNoAttenuation(world, x+0.5, y+0.5, z+0.5, 1, 0.5F, 64);
			double[] angs = ReikaPhysicsHelper.cartesianToPolar(ep.posX-x-0.5, ep.posY-y-0.5, ep.posZ-z-0.5);
			double[] v = ReikaPhysicsHelper.polarToCartesian(6, angs[1]-90, -angs[2]-90);
			ep.motionX += v[0];
			ep.motionZ += v[2];
			ep.motionY = MathHelper.clamp_double(v[1], 1.25, 1.75);
			//ChromaSounds.DISCHARGE.playSoundAtBlockNoAttenuation(world, ep.posX+ep.motionX*4, ep.posY+ep.motionY*4, ep.posZ+ep.motionZ*4, 1, 0.5F, 64);
			ep.velocityChanged = true;
			ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.ARTEFACTCLICK.ordinal(), world, x, y, z, 64);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void doInteractFX(World world, int x, int y, int z) {
		for (int i = 0; i < 64; i++) {
			double px = x+world.rand.nextDouble();
			double py = y+world.rand.nextDouble();
			double pz = z+world.rand.nextDouble();
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.75);
			double vy = ReikaRandomHelper.getRandomPlusMinus(0, 0.75);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.75);
			int l = ReikaRandomHelper.getRandomBetween(8, 12);
			float f = (float)ReikaRandomHelper.getRandomBetween(0.5, 1);
			EntityFX fx = new EntityCCBlurFX(world, px, py, pz, vx, vy, vz).setColor(0xffffff).setScale(f).setLife(l);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:ua");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		double d = ep.getDistanceSq(x+0.5, y+0.5, z+0.5);
		if (d < 576) {
			ItemUnknownArtefact.doUA_FX(world, x+0.5, y+0.5+1.5*rand.nextDouble(), z+0.5, false);
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return new ArrayList();//super.getDrops(world, x, y, z, metadata, fortune);
	}

	@Override
	public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
		return false;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer ep, int x, int y, int z, boolean harvest) {
		if (EnchantmentHelper.getSilkTouchModifier(ep)) {
			ProgressStage.ARTEFACT.stepPlayerTo(ep);
		}
		return world.setBlockToAir(x, y, z);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta) {
		if (ep.capabilities.isCreativeMode || world.isRemote)
			return;
		else if (EnchantmentHelper.getSilkTouchModifier(ep)) {
			ReikaItemHelper.dropItem(world, x+world.rand.nextDouble(), y+world.rand.nextDouble(), z+world.rand.nextDouble(), ChromaItems.ARTEFACT.getStackOfMetadata(ArtefactTypes.ARTIFACT.ordinal()));
		}
		else {
			int n = 1+world.rand.nextInt(4);
			for (int i = 0; i < n; i++) {
				ReikaItemHelper.dropItem(world, x+world.rand.nextDouble(), y+world.rand.nextDouble(), z+world.rand.nextDouble(), ChromaItems.ARTEFACT.getStackOfMetadata(ArtefactTypes.FRAGMENT.ordinal()));
			}
		}
	}

}

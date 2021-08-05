/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaBasic;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.BreakerCallback;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.ProgressiveBreaker;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent.UpdateFlags;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaCropHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.ModRegistry.ModCropList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemFertilitySeed extends ItemChromaBasic implements BreakerCallback {

	public static final int INITIAL_DELAY = 40;

	private static final Random fxRand = new Random();
	private final WeightedRandom<Integer> dropChances = new WeightedRandom();

	public int getRandomAmbientDropMeta(Random rand) {
		dropChances.setRNG(rand);
		return dropChances.getRandomEntry();
	}

	public ItemFertilitySeed(int tex) {
		super(tex);
		this.setMaxStackSize(8);
		hasSubtypes = true;
		this.setMaxDamage(0);

		for (int i = 0; i <= 6; i++)
			dropChances.addEntry(i, 125-i*20);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		if (world.getBlock(x, y, z) == Blocks.dirt && !world.isRemote) {
			ProgressiveBreaker br = ProgressiveRecursiveBreaker.instance.addCoordinateWithReturn(world, x, y, z, 3);
			br.drops = false;
			br.tickRate = 2;
			br.player = ep;
			br.hungerFactor = 0;
			br.causeUpdates = false;
			br.call = this;
			if (!ep.capabilities.isCreativeMode)
				is.stackSize--;
			return true;
		}
		else if (ItemDye.applyBonemeal(is, world, x, y, z, ep))
			return true;
		return false;
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem ei) {
		if (ei.age >= 4800) //compensating for creative setAgeToDespawn
			ei.age -= 4800;
		if (ei.age < INITIAL_DELAY) {
			//vanilla code
		}
		else if (ei.age < INITIAL_DELAY+10 && !ei.worldObj.isRemote) {
			ItemStack is = ei.getEntityItem();
			int ticks = (2+2*is.getItemDamage())*is.stackSize;
			for (int i = 0; i < ticks; i++) {
				int x = MathHelper.floor_double(ReikaRandomHelper.getRandomPlusMinus(ei.posX, 2));
				int y = MathHelper.floor_double(ReikaRandomHelper.getRandomPlusMinus(ei.posY, 1));
				int z = MathHelper.floor_double(ReikaRandomHelper.getRandomPlusMinus(ei.posZ, 2));
				Block b = ei.worldObj.getBlock(x, y, z);
				if (this.canTickBlock(b, ei.worldObj.getBlockMetadata(x, y, z))) {
					b.updateTick(ei.worldObj, x, y, z, ei.worldObj.rand);
					MinecraftForge.EVENT_BUS.post(new BlockTickEvent(ei.worldObj, x, y, z, b, UpdateFlags.FORCED.flag));
					ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.FERTILITYSEED.ordinal(), ei.worldObj, x, y, z, 96);
				}
			}
		}
		else {
			ei.setDead();
			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	public static void doFertilizeFX(World world, int x, int y, int z) {
		double d = Minecraft.getMinecraft().thePlayer.getDistanceSq(x+0.5, y+0.5, z+0.5);
		int n = d >= 4096 ? 4 : d >= 1024 ? 6 : 8;
		int maxl = d >= 4096 ? 20 : d >= 576 ? 30 : 40;
		Block b = world.getBlock(x, y, z);
		b.setBlockBoundsBasedOnState(world, x, y, z);
		for (int i = 0; i < n; i++) {
			double px = x+b.getBlockBoundsMinX()+(b.getBlockBoundsMaxX()-b.getBlockBoundsMinX())*fxRand.nextDouble();
			double py = y+b.getBlockBoundsMinY()+(b.getBlockBoundsMaxY()-b.getBlockBoundsMinY())*fxRand.nextDouble();
			double pz = z+b.getBlockBoundsMinZ()+(b.getBlockBoundsMaxZ()-b.getBlockBoundsMinZ())*fxRand.nextDouble();
			int c = ReikaColorAPI.getModifiedHue(0x00ff00, ReikaRandomHelper.getRandomBetween(80, 150));
			float f = 0.5F+fxRand.nextFloat();
			int l = ReikaRandomHelper.getRandomBetween(10, maxl);
			EntityFX fx = new EntityCCBlurFX(world, px, py, pz).setColor(c).setScale(f).setLife(l);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private boolean canTickBlock(Block b, int meta) {
		return b == Blocks.grass || b == Blocks.mycelium || b instanceof BlockSapling || ReikaCropHelper.isCrop(b) || ModCropList.isModCrop(b, meta);
	}

	@Override
	public boolean canBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		return id == Blocks.dirt && world.getBlock(x, y+1, z).getLightOpacity(world, x, y+1, z) <= 2;
	}

	@Override
	public void onPreBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {

	}

	@Override
	public void onPostBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		world.setBlock(x, y, z, Blocks.grass);
	}

	@Override
	public void onFinish(ProgressiveBreaker b) {

	}

}

/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes.PoolRecipe;
import Reika.ChromatiCraft.Block.BlockActiveChroma;
import Reika.ChromatiCraft.Block.BlockActiveChroma.TileEntityChroma;
import Reika.ChromatiCraft.Items.ItemCrystalShard;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.RotaryCraft.API.ReservoirAPI.TankHandler;

public class ReservoirHandlers {

	private static final Random rand = new Random();

	public static final int ACCEL_FACTOR = 2;

	private static abstract class ChromaReservoirRecipeHandlerBase implements TankHandler {

		@Override
		public final int onTick(TileEntity te, FluidStack stored, EntityPlayer owner) {
			if (stored != null && stored.amount >= 1000 && stored.getFluid() == FluidRegistry.getFluid("chroma")) {
				return this.doTick(te, stored, owner);
			}
			else {
				return 0;
			}
		}

		protected abstract int doTick(TileEntity te, FluidStack fs, EntityPlayer owner);

	}

	public static class ChromaPrepHandler extends ChromaReservoirRecipeHandlerBase {

		@Override
		protected int doTick(TileEntity te, FluidStack fs, EntityPlayer owner) {
			int dye = fs.tag != null ? fs.tag.getInteger("berries") : 0;
			int ether = fs.tag != null ? fs.tag.getInteger("ether") : 0;
			CrystalElement e = dye > 0 ? CrystalElement.elements[fs.tag.getInteger("element")] : null;
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(te.xCoord, te.yCoord, te.zCoord);
			List<EntityItem> li = te.worldObj.getEntitiesWithinAABB(EntityItem.class, box);
			boolean flag = false;
			for (EntityItem ei : li) {
				ItemStack is = ei.getEntityItem();
				if (!te.worldObj.isRemote && rand.nextInt(5) == 0 && dye < TileEntityChroma.BERRY_SATURATION && ChromaItems.BERRY.matchWith(is) && (e == null || is.getItemDamage() == e.ordinal())) {
					e = CrystalElement.elements[is.getItemDamage()];
					if (fs.tag == null)
						fs.tag = new NBTTagCompound();
					fs.tag.setInteger("element", e.ordinal());
					while (dye < TileEntityChroma.BERRY_SATURATION && is.stackSize > 0) {
						dye = fs.tag.getInteger("berries")+1;
						fs.tag.setInteger("berries", dye);
						is.stackSize--;
					}
					if (is.stackSize <= 0)
						ei.setDead();
					flag = true;
				}
				else if (!te.worldObj.isRemote && rand.nextInt(2) == 0 && ether < TileEntityChroma.ETHER_SATURATION && !this.isRecentEtherDissolve(te, fs) && ReikaItemHelper.matchStacks(is, ChromaStacks.etherBerries)) {
					if (fs.tag == null)
						fs.tag = new NBTTagCompound();
					if (ether < TileEntityChroma.ETHER_SATURATION && is.stackSize > 0) {
						ether = fs.tag.getInteger("ether")+1;
						fs.tag.setInteger("ether", ether);
						fs.tag.setLong("etherdissolve", te.worldObj.getTotalWorldTime());
						is.stackSize--;
						ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.PARTICLE.ordinal(), te, 24, ReikaParticleHelper.SPELL.ordinal(), 8);
					}
					if (is.stackSize <= 0)
						ei.setDead();
					flag = true;
				}
			}
			if (flag && e != null)
				fs.tag.setInteger("renderColor", BlockActiveChroma.getColor(e, dye));
			return 0;
		}

		private boolean isRecentEtherDissolve(TileEntity te, FluidStack fs) {
			return fs.tag != null && fs.tag.getLong("etherdissolve")+5 >= te.getWorldObj().getTotalWorldTime();
		}
	}

	public static class ShardBoostingHandler extends ChromaReservoirRecipeHandlerBase {

		@Override
		protected int doTick(TileEntity te, FluidStack fs, EntityPlayer owner) {
			int dye = fs.tag != null ? fs.tag.getInteger("berries") : 0;
			int ether = fs.tag != null ? fs.tag.getInteger("ether") : 0;
			CrystalElement e = dye > 0 ? CrystalElement.elements[fs.tag.getInteger("element")] : null;
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(te.xCoord, te.yCoord, te.zCoord);
			List<EntityItem> li = te.worldObj.getEntitiesWithinAABB(EntityItem.class, box);
			for (EntityItem ei : li) {
				ItemStack is = ei.getEntityItem();
				if (e != null && is.getItemDamage() == e.ordinal() && ChromaItems.SHARD.matchWith(is) && dye == TileEntityChroma.BERRY_SATURATION) {
					if (ItemCrystalShard.canCharge(ei)) {
						boolean done = false;
						for (int i = 0; i < ACCEL_FACTOR && !done; i++) {
							done = ItemCrystalShard.tickShardCharging(ei, e, ether, te.xCoord, te.yCoord, te.zCoord);
						}
						if (!te.worldObj.isRemote && done) {
							fs.tag = null;
							return 200;
						}
					}
				}
			}
			return 0;
		}

	}

	public static class PoolRecipeHandler extends ChromaReservoirRecipeHandlerBase {

		@Override
		protected int doTick(TileEntity te, FluidStack fs, EntityPlayer owner) {
			if (rand.nextInt(3) == 0) {
				int ether = fs.tag != null ? fs.tag.getInteger("ether") : 0;
				AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(te.xCoord, te.yCoord, te.zCoord);
				List<EntityItem> li = te.worldObj.getEntitiesWithinAABB(EntityItem.class, box);
				for (EntityItem ei : li) {
					if (PoolRecipes.instance.canAlloyItem(ei)) {
						PoolRecipe pr = PoolRecipes.instance.getPoolRecipe(ei, li, false, owner);
						if (pr != null) {
							if (ei.worldObj.isRemote) {
								for (int i = 0; i < ACCEL_FACTOR; i++) {
									ChromaFX.poolRecipeParticles(ei);
								}
							}
							else if (ei.ticksExisted > 20 && rand.nextInt(20/ACCEL_FACTOR) == 0 && (ei.ticksExisted >= 600 || rand.nextInt((600-ei.ticksExisted)/ACCEL_FACTOR) == 0)) {
								PoolRecipes.instance.makePoolRecipe(ei, pr, ether, te.xCoord, te.yCoord, te.zCoord);
								fs.tag = null;
								return 1000;
							}
							break;
						}
						ei.lifespan = Integer.MAX_VALUE;
					}
				}
			}
			return 0;
		}

	}

}

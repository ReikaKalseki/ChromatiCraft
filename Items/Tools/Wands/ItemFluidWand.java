/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools.Wands;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.BreakerCallback;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.ProgressiveBreaker;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

public class ItemFluidWand extends ItemWandBase implements BreakerCallback {

	private static final int MAX_DEPTH = 18;
	private static final int MAX_DEPTH_BOOST = 24;

	private static final HashMap<Integer, EntityPlayer> breakers = new HashMap();

	public ItemFluidWand(int index) {
		super(index);
		this.addEnergyCost(CrystalElement.CYAN, 1);
		this.addEnergyCost(CrystalElement.YELLOW, 2);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlock(ep, 5, true);
		if (mov != null && !world.isRemote) {
			int x = mov.blockX;
			int y = mov.blockY;
			int z = mov.blockZ;
			Block id = world.getBlock(x, y, z);
			if (id != Blocks.air) {
				Fluid f = ReikaFluidHelper.lookupFluidForBlock(id);
				if (f != null) {
					ProgressiveBreaker b = ProgressiveRecursiveBreaker.instance.addCoordinateWithReturn(world, x, y, z, 900);
					//b.looseMatches.put(Blocks.redstone_ore, new BlockKey(Blocks.lit_redstone_ore));
					b.call = this;
					b.drops = false;
					b.player = ep;
					b.tickRate = 5;
					b.bounds = new BlockBox(x, y, z, x, y, z).expand(128, 128, 128);
					for (int i = 0; i < 16; i++)
						b.addBlock(new BlockKey(id, i));
					if (f == FluidRegistry.WATER) {
						for (int i = 0; i < 16; i++) {
							b.addBlock(new BlockKey(Blocks.water, i));
							b.addBlock(new BlockKey(Blocks.flowing_water, i));
						}
					}
					else if (f == FluidRegistry.LAVA) {
						for (int i = 0; i < 16; i++) {
							b.addBlock(new BlockKey(Blocks.lava, i));
							b.addBlock(new BlockKey(Blocks.flowing_lava, i));
						}
					}
					breakers.put(b.hashCode(), ep);
				}
			}
		}
		return is;
	}

	public static int getDepth(EntityPlayer ep) {
		return canUseBoostedEffect(ep) ? MAX_DEPTH_BOOST : MAX_DEPTH;
	}

	@Override
	public void onPreBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {

	}

	@Override
	public void onPostBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		EntityPlayer ep = breakers.get(b.hashCode());
		if (ep != null) {
			boolean exists = world.getPlayerEntityByName(ep.getCommandSenderName()) != null;
			if (exists) {
				this.drainPlayer(ep);
			}
			else {
				b.terminate();
			}
		}
	}

	@Override
	public boolean canBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		EntityPlayer ep = breakers.get(b.hashCode());
		if (ep != null) {
			boolean exists = world.getPlayerEntityByName(ep.getCommandSenderName()) != null;
			if (exists) {
				return this.sufficientEnergy(ep) && (world.isRemote || ReikaPlayerAPI.playerCanBreakAt((WorldServer)world, x, y, z, (EntityPlayerMP)ep));
			}
		}
		return false;
	}

	@Override
	public void onFinish(ProgressiveBreaker b) {
		breakers.remove(b.hashCode());
	}


}

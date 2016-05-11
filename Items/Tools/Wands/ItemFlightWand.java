/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools.Wands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Block.BlockHoverBlock.HoverType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;

public class ItemFlightWand extends ItemWandBase {

	public ItemFlightWand(int index) {
		super(index);

		this.addEnergyCost(CrystalElement.BLACK, 5);
		this.addEnergyCost(CrystalElement.LIME, 20);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		boolean flag = false;
		if (this.canUse(ep)) {
			flag = this.trigger(world, is, ep, ReikaPlayerAPI.getLookedAtBlock(ep, 8, true));
		}

		if (!flag) {
			ep.openGui(ChromatiCraft.instance, ChromaGuis.HOVER.ordinal(), world, 0, 0, 0);
		}
		return is;
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		return this.canUse(ep) && this.trigger(world, is, ep, new MovingObjectPosition(x, y, z, s, Vec3.createVectorHelper(a, b, c)));
	}

	private boolean canUse(EntityPlayer ep) {
		return this.sufficientEnergy(ep);
	}

	private boolean trigger(World world, ItemStack is, EntityPlayer ep, MovingObjectPosition mov) {
		if (mov != null) {
			if (mov.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				int x = mov.blockX;
				int y = mov.blockY;
				int z = mov.blockZ;
				if (world.getBlock(x, y+1, z).isAir(world, x, y+1, z)) {

					ChromaSounds.CAST.playSound(ep);

					for (double d = 0; d <= 1; d += 0.125) {
						double dx = ep.posX+d*(x-ep.posX);
						double dy = ep.posY+d*(y-ep.posY);
						double dz = ep.posZ+d*(z-ep.posZ);
						double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
						double vy = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
						double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
						ReikaParticleHelper.ENCHANTMENT.spawnAt(world, dx, dy, dz, vx, vy, vz);
					}

					for (int i = 1; i <= 8; i++) {
						if (world.getBlock(x, y+i, z).isAir(world, x, y+i, z)) {
							HoverType mode = this.getMode(is);
							world.setBlock(x, y+i, z, ChromaBlocks.HOVER.getBlockInstance(), mode.getDecayMeta(), 3);
							this.drainPlayer(ep, this.getEnergyCostFactor(mode));
						}
					}

					return true;
				}
			}
		}
		return false;
	}

	public static HoverType getMode(ItemStack is) {
		return is.stackTagCompound != null ? HoverType.list[is.stackTagCompound.getInteger("mode")] : HoverType.ELEVATE;
	}

	public static void setMode(ItemStack is, HoverType type) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();

		is.stackTagCompound.setInteger("mode", type.ordinal());
	}

	private float getEnergyCostFactor(HoverType type) {
		switch(type) {
		case ELEVATE:
			return 1.5F;
		case FASTELEVATE:
			return 2F;
		default:
			return 1;
		}
	}

}

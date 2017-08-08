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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class ItemMobilityWand extends ItemWandBase {

	public ItemMobilityWand(int index) {
		super(index);
		this.addEnergyCost(CrystalElement.BLACK, 100);
		this.addEnergyCost(CrystalElement.LIME, 250);
		this.addEnergyCost(CrystalElement.PURPLE, 50);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlock(ep, 192, true);
		if (mov != null) {
			ForgeDirection dir = mov.sideHit >= 0 ? ForgeDirection.VALID_DIRECTIONS[mov.sideHit] : ForgeDirection.UNKNOWN;
			int x = mov.blockX+dir.offsetX;
			int y = mov.blockY+dir.offsetY;
			int z = mov.blockZ+dir.offsetZ;
			if (Chromabilities.ORECLIP.enabledOn(ep) || this.isValidTarget(world, x, y, z)) {
				float f = this.getUsageFactor(ep.posX, ep.posY, ep.posZ, x, y, z);
				if (this.sufficientEnergy(ep, f)) {
					if (!world.isRemote) {
						this.drainPlayer(ep, f);
					}
					ep.setPositionAndUpdate(x+0.5, y+0.25, z+0.5);
					ep.playSound("mob.endermen.portal", 1, 1);
					for (int i = 0; i < 128; i++) {
						double rx = ReikaRandomHelper.getRandomPlusMinus(ep.posX, 0.75);
						double ry = ReikaRandomHelper.getRandomPlusMinus(ep.posY-1, 0.5);
						double rz = ReikaRandomHelper.getRandomPlusMinus(ep.posZ, 0.75);
						ReikaParticleHelper.PORTAL.spawnAt(world, rx, ry, rz);
					}
				}
			}
		}
		return is;
	}

	private float getUsageFactor(double posX, double posY, double posZ, int x, int y, int z) {
		float num = 1;
		if (y >= posY) {
			num *= 1.5F;
		}
		if (y >= posY+32) {
			num *= 2F;
		}
		if (y >= posY+64) {
			num *= 2.5F;
		}
		if (y >= posY+128) {
			num *= 4F;
		}
		double dd = ReikaMathLibrary.py3d(x-posX, 0, y-posY);
		if (dd >= 16) {
			num *= 1.25F;
		}
		if (dd >= 32) {
			num *= 1.5F;
		}
		if (dd >= 64) {
			num *= 2.5F;
		}
		if (dd >= 128) {
			num *= 4F;
		}
		//ReikaJavaLibrary.pConsole(num+": "+this.getEnergy(num), Side.SERVER);
		return num;
	}

	private boolean isValidTarget(World world, int x, int y, int z) {
		return ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.softBlocks(world, x, y+1, z);
	}

}

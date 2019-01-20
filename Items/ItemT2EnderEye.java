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

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import Reika.ChromatiCraft.Base.ItemChromaBasic;
import Reika.ChromatiCraft.Entity.EntityEnderEyeT2;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Interfaces.Item.AnimatedSpritesheet;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;


public class ItemT2EnderEye extends ItemChromaBasic implements AnimatedSpritesheet {

	public static final int FUZZ = 440;

	public ItemT2EnderEye(int tex) {
		super(tex);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		return Items.ender_eye.onItemUse(is, ep, world, x, y, z, s, a, b, c);
	}

	@Override
	public boolean useAnimatedRender(ItemStack is) {
		return true;
	}

	@Override
	public int getFrameCount() {
		return 16;
	}

	@Override
	public int getBaseRow(ItemStack is) {
		return 8;
	}

	@Override
	public int getColumn(ItemStack is) {
		return 0;
	}

	@Override
	public int getFrameOffset(ItemStack is) {
		return 0;
	}

	@Override
	public int getFrameSpeed() {
		return 2;
	}

	@Override
	public String getTexture(ItemStack is) {
		return this.useAnimatedRender(is) ? "/Reika/ChromatiCraft/Textures/Items/miscanim.png" : super.getTexture(is);
	}

	@Override
	public boolean verticalFrames() {
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		MovingObjectPosition mov = this.getMovingObjectPositionFromPlayer(world, ep, false);

		if (mov != null && mov.typeOfHit == MovingObjectType.BLOCK && world.getBlock(mov.blockX, mov.blockY, mov.blockZ) == Blocks.end_portal_frame) {
			return is;
		}
		else {
			if (!world.isRemote) {
				ChunkPosition pos = world.findClosestStructure("Stronghold", (int)ep.posX, (int)ep.posY, (int)ep.posZ);

				if (pos != null) {
					if (is.stackTagCompound == null || !is.stackTagCompound.hasKey("owner")) {
						is.stackTagCompound = new NBTTagCompound();
						is.stackTagCompound.setString("owner", ep.getUniqueID().toString());
					}
					EntityEnderEyeT2 eye = new EntityEnderEyeT2(world, ep.posX, ep.posY + 1.62D - ep.yOffset, ep.posZ);
					CrystalElement[] key = this.encodeLocation(ep, pos);
					/*
					int[] data = new int[key.length-1];
					for (int i = 0; i < data.length; i++) {
						data[i] = key[i+1].ordinal()-key[0].ordinal();
						while(data[i] < 0)
							data[i] += 16;
					}
					ReikaJavaLibrary.pConsole(pos.chunkPosX+":"+pos.chunkPosZ+" > "+Arrays.toString(data));*/
					eye.readEntityFromNBT(is.stackTagCompound);
					eye.setColorKey(key);
					eye.moveTowards(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
					world.spawnEntityInWorld(eye);
					world.playSoundAtEntity(ep, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
					world.playAuxSFXAtEntity((EntityPlayer)null, 1002, (int)ep.posX, (int)ep.posY, (int)ep.posZ, 0);

					if (!ep.capabilities.isCreativeMode) {
						--is.stackSize;
					}
				}
				else {
					ChromaSounds.ERROR.playSound(ep);
				}
			}

			return is;
		}
	}

	private static CrystalElement[] encodeLocation(EntityPlayer ep, ChunkPosition pos) {
		int x = pos.chunkPosX;//ReikaRandomHelper.getRandomPlusMinus(pos.chunkPosX, FUZZ);
		int z = pos.chunkPosZ;//ReikaRandomHelper.getRandomPlusMinus(pos.chunkPosZ, FUZZ);
		x = ReikaMathLibrary.roundToNearestX(FUZZ, x);
		z = ReikaMathLibrary.roundToNearestX(FUZZ, z);
		byte[] valsX = ReikaJavaLibrary.splitIntToHexChars(x);
		byte[] valsZ = ReikaJavaLibrary.splitIntToHexChars(z);
		ArrayList<Byte> vx = ReikaJavaLibrary.makeIntListFromArray(valsX);
		ArrayList<Byte> vz = ReikaJavaLibrary.makeIntListFromArray(valsZ);
		Collections.reverse(vx);
		while (vx.get(0) == 0 && vx.size() >= 1) //strip leading values
			vx.remove(0);
		Collections.reverse(vz);
		while (vz.get(0) == 0 && vz.size() >= 1)
			vz.remove(0);
		int offset = ep.getUniqueID().hashCode()%16;
		CrystalElement[] ret = new CrystalElement[1+vx.size()+vz.size()];
		ret[0] = CrystalElement.elements[offset];
		for (int i = 0; i < vx.size(); i++) {
			int idx = (vx.get(i)+offset)%16;
			ret[1+i] = CrystalElement.elements[idx];
		}
		for (int i = 0; i < vz.size(); i++) {
			int idx = (vz.get(i)+offset)%16;
			ret[1+i+vx.size()] = CrystalElement.elements[idx];
		}
		return ret;
	}

	@Override
	public int getEntityLifespan(ItemStack is, World world) {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem ei) {
		ReikaEntityHelper.setInvulnerable(ei, true);
		NBTTagCompound tag = ei.getEntityItem().stackTagCompound;
		if (tag != null && tag.hasKey("owner")) {
			UUID uid = UUID.fromString(tag.getString("owner"));
			EntityPlayer ep = ei.worldObj.func_152378_a(uid);
			if (ep != null) {
				if (ei.delayBeforeCanPickup == 0) {
					double x = ep.posX;
					double y = ep.posY+1.5;
					double z = ep.posZ;
					double dx = (x+0.5 - ei.posX);
					double dy = (y+0.5 - ei.posY);
					double dz = (z+0.5 - ei.posZ);
					double ddt = ReikaMathLibrary.py3d(dx, dy, dz);
					if (ddt < 128) {
						if (ReikaMathLibrary.py3d(dx, 0, dz) < 1) {
							ei.onCollideWithPlayer(ep);
						}
						else {
							ei.motionX += dx/ddt/ddt/1;
							ei.motionY += dy/ddt/ddt/2;
							ei.motionZ += dz/ddt/ddt/1;
							ei.motionX = MathHelper.clamp_double(ei.motionX, -0.75, 0.75);
							ei.motionY = MathHelper.clamp_double(ei.motionY, -0.75, 0.75);
							ei.motionZ = MathHelper.clamp_double(ei.motionZ, -0.75, 0.75);
							if (ei.posY < y)
								ei.motionY += 0.125;
							if (ei.posY < 0)
								ei.motionY = Math.max(1, ei.motionY);
							if (!ei.worldObj.isRemote)
								ei.velocityChanged = true;
						}
					}
				}
			}
		}
		return false;
	}

}

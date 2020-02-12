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

import java.util.UUID;

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

import Reika.ChromatiCraft.Base.ItemChromaBasic;
import Reika.ChromatiCraft.Entity.EntityEnderEyeT2;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.Item.AnimatedSpritesheet;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;


public class ItemT2EnderEye extends ItemChromaBasic implements AnimatedSpritesheet {

	public ItemT2EnderEye(int tex) {
		super(tex);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		boolean ret = Items.ender_eye.onItemUse(is, ep, world, x, y, z, s, a, b, c);
		if (ret) {
			if (ReikaWorldHelper.checkForAdjBlock(world, x, y, z, Blocks.end_portal) != null) {
				is.stackSize = 12;
				is.animationsToGo = 5;
				ReikaSoundHelper.playSoundAtEntity(ep.worldObj, ep, "random.pop", 1, 1.35F);
				if (world.isRemote)
					ReikaSoundHelper.playClientSound("random.pop", ep.posX, ep.posY, ep.posZ, 1, 1.35F, true);
			}
		}
		return ret;
	}

	@Override
	public boolean useAnimatedRender(ItemStack is) {
		return true;
	}

	@Override
	public int getFrameCount(ItemStack is) {
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
					EntityEnderEyeT2 eye = EntityEnderEyeT2.create(world, ep.posX, ep.posY + 1.62D - ep.yOffset, ep.posZ, is.stackTagCompound);
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

	@Override
	public int getEntityLifespan(ItemStack is, World world) {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem ei) {
		ReikaEntityHelper.setInvulnerable(ei, true);
		if (new Coordinate(ei).getBlock(ei.worldObj) == Blocks.water) {
			ei.motionY += 0.07;
			ei.motionX *= 0.85;
			ei.motionZ *= 0.85;
			ei.velocityChanged = true;
		}
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

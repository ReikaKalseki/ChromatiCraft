/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;


public class ItemWarpCapsule extends ItemChromaTool {

	public ItemWarpCapsule(int index) {
		super(index);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		DecimalPosition pos = getPosition(is);
		int dim = getDimension(is);
		if (pos == null) {
			setPosition(is, ep);
		}
		else if (!world.isRemote && ep instanceof EntityPlayerMP) {
			teleport((EntityPlayerMP)ep, pos, dim);
		}
		return is;
	}

	private static void teleport(EntityPlayerMP ep, DecimalPosition pos, int dim) {
		if (canTeleport(ep, pos, dim)) {
			double r = 12;
			double dx;
			double dy = pos.yCoord;
			double dz;
			WorldServer world = (WorldServer)ep.worldObj;
			EntityPlayer test = new EntityPlayerMP(ep.mcServer, world, ep.getGameProfile(), ep.theItemInWorldManager);
			do {
				dx = ReikaRandomHelper.getRandomPlusMinus(pos.xCoord, r);
				dz = ReikaRandomHelper.getRandomPlusMinus(pos.zCoord, r);
				test.setLocationAndAngles(dx, dy, dz, 0, 0);
			} while(!world.getCollidingBoundingBoxes(ep, ep.boundingBox).isEmpty());

			do {
				double maxh = 1+ep.worldObj.rand.nextDouble()*8;
				dy = pos.yCoord+maxh;
				test.setLocationAndAngles(dx, dy, dz, 0, 0);
			} while(!world.getCollidingBoundingBoxes(ep, ep.boundingBox).isEmpty());

			ep.setPositionAndUpdate(dx, dy, dz);

			double vel = ReikaRandomHelper.getRandomBetween(1, 3);
			double v[] = ReikaPhysicsHelper.polarToCartesian(vel, ep.worldObj.rand.nextDouble()*360, ep.worldObj.rand.nextDouble()*360);

			ep.motionX = v[0];
			ep.motionY = v[1];
			ep.motionZ = v[2];
			ep.velocityChanged = true;
			ep.fallDistance = ep.worldObj.rand.nextFloat()*5F+(float)Math.max(-ep.motionY*5, 0);

			ep.setCurrentItemOrArmor(0, null);

			ReikaSoundHelper.playSoundFromServer(world, dx, dy, dz, "mob.endermen.portal", 2, 1, true);
			ReikaSoundHelper.playSoundFromServer(world, dx, dy, dz, "mob.endermen.portal", 2, 1, true);
			ChromaSounds.RIFT.playSound(ep, 1, 0.5F);
			ChromaSounds.RIFT.playSound(ep, 1, 0.75F);

			ep.addPotionEffect(new PotionEffect(Potion.confusion.id, 150, 5));
		}
	}

	private static boolean canTeleport(EntityPlayer ep, DecimalPosition pos, int dim) {
		return dim == ep.worldObj.provider.dimensionId && new WorldLocation(ep).canSeeTheSky() && pos.getDistanceTo(new DecimalPosition(ep)) <= 2000;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		DecimalPosition pos = this.getPosition(is);
		if (pos != null) {
			li.add("Linked to around "+pos.formattedString(1)+" in DIM"+getDimension(is));
		}
	}

	private static int getDimension(ItemStack is) {
		return is.stackTagCompound != null ? is.stackTagCompound.getInteger("dim") : 0;
	}

	private static DecimalPosition getPosition(ItemStack is) {
		return is.stackTagCompound != null ? DecimalPosition.readFromNBT("pos", is.stackTagCompound) : null;
	}

	private static void setPosition(ItemStack is, Entity e) {
		DecimalPosition p = new DecimalPosition(e);
		is.stackTagCompound = new NBTTagCompound();
		p.writeToNBT("pos", is.stackTagCompound);
		is.stackTagCompound.setInteger("dim", e.worldObj.provider.dimensionId);
	}

	@Override
	public int getItemSpriteIndex(ItemStack item) {
		int base = super.getItemSpriteIndex(item);
		return this.getPosition(item) == null ? base : base+1;
	}

}

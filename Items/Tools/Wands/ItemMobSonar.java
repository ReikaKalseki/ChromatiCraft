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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Render.MobSonarRenderer;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.Satisforestry.API.PointSpawnLocation;
import Reika.Satisforestry.API.SFAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMobSonar extends ItemWandBase {

	private static final int RANGE = 64;

	public ItemMobSonar(int index) {
		super(index);

		this.addEnergyCost(CrystalElement.PINK, 4);
		this.addEnergyCost(CrystalElement.BLUE, 4);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (world.isRemote) {
			this.doPing(world, ep);
		}
		else {
			if (is.stackTagCompound == null) {
				is.stackTagCompound = new NBTTagCompound();
				is.stackTagCompound.setInteger("typeFlags", MobSonarRenderer.EntitySonarType.getAllFlags());
			}
			this.drainPlayer(ep);
			if (ModList.SATISFORESTRY.isLoaded()) {
				this.findLizardDoggo(world, ep);
			}
		}
		return is;
	}

	@ModDependent(ModList.SATISFORESTRY)
	private void findLizardDoggo(World world, EntityPlayer ep) {
		PointSpawnLocation s = SFAPI.spawningHandler.getNearestSpawnPointOfType(ep, 512, SFAPI.genericLookups.getDoggoClass());
		if (s != null) {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.DOGGOSONAR.ordinal(), (EntityPlayerMP)ep, s.getX(), s.getY(), s.getZ());
		}
	}

	@SideOnly(Side.CLIENT)
	private void doPing(World world, EntityPlayer ep) {
		MobSonarRenderer.instance.addPing(ep, RANGE);
		ReikaSoundHelper.playClientSound(ChromaSounds.ORB, ep, 1, 0.5F);
	}

}

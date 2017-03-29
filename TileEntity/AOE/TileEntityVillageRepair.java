/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE;

import net.minecraft.village.Village;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;


public class TileEntityVillageRepair extends TileEntityChromaticBase {

	private Village village;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.VILLAGEREPAIR;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (this.getTicksExisted() == 0 || this.getTicksExisted()%128 == 0) {
			this.findVillage(world, x, y, z);
		}

		/*
		if (village != null && world.isRemote) {
			for (int i = 0; i < 16; i++) {
				double a = Math.toRadians(rand.nextDouble()*360);
				double px = village.getCenter().posX+0.5+village.getVillageRadius()*Math.cos(a);
				double pz = village.getCenter().posZ+0.5+village.getVillageRadius()*Math.sin(a);
				EntityFX fx = new EntityBlurFX(world, px, y, pz).setScale(2).setGravity(-0.03125F);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
		 */
	}

	private void findVillage(World world, int x, int y, int z) {
		village = world.villageCollectionObj.findNearestVillage(x, y, z, 128);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Plants;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityChromaFlower extends TileEntityChromaticBase {

	private int hueBaseOffset = rand.nextInt(360);
	private int hueOffset = 10+rand.nextInt(21);

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.CHROMAFLOWER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@SideOnly(Side.CLIENT)
	public int getHue1() {
		return (hueBaseOffset+this.getTicksExisted())%360;
	}

	@SideOnly(Side.CLIENT)
	public int getHue2() {
		return (hueBaseOffset+this.getTicksExisted()+hueOffset)%360;
	}

	@SideOnly(Side.CLIENT)
	public int getHue3() {
		return (hueBaseOffset+this.getTicksExisted()-hueOffset)%360;
	}

}

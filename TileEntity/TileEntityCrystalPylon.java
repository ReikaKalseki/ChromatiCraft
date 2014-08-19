/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import Reika.ChromatiCraft.Base.TileEntity.CrystalTransmitterBase;
import Reika.ChromatiCraft.Magic.CrystalSource;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityFlareFX;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
//Make player able to manufacture in the very late game, otherwise rare worldgen
public class TileEntityCrystalPylon extends CrystalTransmitterBase implements CrystalSource {

	public boolean hasMultiblock = false;
	private CrystalElement color = CrystalElement.WHITE;
	public int randomOffset = rand.nextInt(360);

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.PYLON;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e == color;
	}

	public CrystalElement getColor() {
		return color;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (hasMultiblock && world.isRemote) {
			this.spawnParticle(world, x, y, z);
		}
	}

	private void spawnParticle(World world, int x, int y, int z) {
		double d = 1.25;
		double rx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, d);
		double ry = ReikaRandomHelper.getRandomPlusMinus(y+0.5, d);
		double rz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, d);
		EntityFlareFX fx = new EntityFlareFX(color, world, rx, ry, rz);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		color = CrystalElement.elements[NBT.getInteger("color")];
		hasMultiblock = NBT.getBoolean("multi");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("color", color.ordinal());
		NBT.setBoolean("multi", hasMultiblock);
	}

	@Override
	public int getSendRange() {
		return 32;
	}

	@Override
	public boolean canConduct() {
		return hasMultiblock;
	}

	@Override
	public int maxThroughput() {
		return 10;
	}

	@Override
	public int getTransmissionStrength() {
		return 100;
	}

	public void setColor(CrystalElement e) {
		color = e;
	}

}

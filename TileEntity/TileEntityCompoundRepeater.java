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

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Magic.CrystalNetworker;
import Reika.ChromatiCraft.Magic.CrystalSource;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCompoundRepeater extends TileEntityCrystalRepeater {

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (world.isRemote && this.canConduct())
			this.particles(world, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	private void particles(World world, int x, int y, int z) {
		if (this.getTicksExisted()%32 == 0) {
			double px = x+0.5;//rand.nextDouble();
			double py = y+0.5;//0.25+y+rand.nextDouble();
			double pz = z+0.5;//rand.nextDouble();
			CrystalElement e = CrystalElement.elements[(this.getTicksExisted()/32)%16];
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityRuneFX(world, px, py, pz, 0, 0, 0, e).setScale(5).setFading());
		}
	}

	@Override
	public void receiveElement(CrystalElement e, int amt) {

	}

	@Override
	public void onPathBroken(CrystalElement e) {

	}

	@Override
	public int getReceiveRange() {
		return 24;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return this.canConduct();
	}

	@Override
	public int maxThroughput() {
		return 1000;
	}

	@Override
	public int getSendRange() {
		return 16;
	}

	@Override
	public int getSignalDegradation() {
		return 5;
	}

	@Override
	protected boolean checkForStructure() {
		for (int i = 1; i <= 5; i++) {
			if (i != 0) {
				int dx = xCoord+facing.offsetX*i;
				int dy = yCoord+facing.offsetY*i;
				int dz = zCoord+facing.offsetZ*i;
				Block b = worldObj.getBlock(dx, dy, dz);
				int meta = worldObj.getBlockMetadata(dx, dy, dz);
				int m2 = i == 3 ? 13 : i == 1 || i == 5 ? 12 : 2;
				if (b != ChromaBlocks.PYLONSTRUCT.getBlockInstance() || meta != m2) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.COMPOUND;
	}

	@Override
	public boolean needsLineOfSight() {
		return true;
	}

	@Override
	public boolean checkConnectivity() {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			if (CrystalNetworker.instance.checkConnectivity(e, worldObj, xCoord, yCoord, zCoord, this.getReceiveRange()))
				return true;
		}
		return false;
	}

	@Override
	public CrystalElement getActiveColor() {
		return CrystalElement.randomElement();
	}

	@Override
	public CrystalSource getEnergySource(CrystalElement e) {
		return e != null ? CrystalNetworker.instance.getConnectivity(e, worldObj, xCoord, yCoord, zCoord, this.getReceiveRange()) : null;
	}

	@Override
	public void onRelayPlayerCharge(EntityPlayer player, TileEntityCrystalPylon p) {
		if (!worldObj.isRemote) {
			if (!player.capabilities.isCreativeMode && !Chromabilities.PYLON.enabledOn(player) && rand.nextInt(20) == 0)
				p.attackEntityByProxy(player, this);
			CrystalNetworker.instance.makeRequest(this, p.getColor(), 15000, this.getReceiveRange());
		}
	}

}

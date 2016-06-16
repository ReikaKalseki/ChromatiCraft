/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Networking;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityWirelessPowered;
import Reika.ChromatiCraft.Magic.Interfaces.WirelessSource;
import Reika.ChromatiCraft.Magic.Network.PylonFinder;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityWirelessSource extends CrystalReceiverBase implements WirelessSource {

	public static final int TRANSMIT_RANGE = 18;

	public static final double LOSS_PER_LUMEN = 0.2;
	public static final int OCCLUSION_FACTOR = 20;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (world.isRemote) {
			this.doParticles(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		if (rand.nextInt(1+Minecraft.getMinecraft().gameSettings.particleSetting) == 0) {
			int c = CrystalElement.getBlendedColor(this.getTicksExisted()+15, 25);
			double v = ReikaRandomHelper.getRandomBetween(0.03125, 0.125);
			double[] vel = ReikaPhysicsHelper.polarToCartesian(v, rand.nextDouble()*360, rand.nextDouble()*360);
			float g = rand.nextBoolean() ? 0.0625F : -0.0625F;
			EntityBlurFX fx = new EntityBlurFX(world, x+0.5, y+0.5, z+0.5, vel[0], vel[1], vel[2]).setColor(c).setRapidExpand().setGravity(g).setColliding();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public int getReceiveRange() {
		return 24;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null;
	}

	@Override
	public int maxThroughput() {
		return 1000;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 120000;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.WIRELESS;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean canTransmitTo(TileEntityWirelessPowered te) {
		return te.getDistanceFrom(xCoord+0.5, yCoord+0.5, zCoord+0.5) <= TRANSMIT_RANGE*TRANSMIT_RANGE;
	}

	@Override
	public boolean request(CrystalElement e, int amt, int x, int y, int z) {
		amt *= 1+LOSS_PER_LUMEN;
		if (!PylonFinder.lineOfSight(worldObj, xCoord, yCoord, zCoord, x, y, z))
			amt *= OCCLUSION_FACTOR;
		boolean flag = energy.containsAtLeast(e, amt);
		if (flag) {
			energy.subtract(e, amt);
		}
		return flag;
	}

}

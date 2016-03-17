/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityChromaFluidFX;
import Reika.ChromatiCraft.Render.Particle.EntityFloatingSeedsFX;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityChromaCrystal extends TileEntityChromaticBase implements OwnedTile {

	private int omega;
	private int torque;
	private long power;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.CRYSTAL;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public void destroy() {
		ChromaSounds.POWERDOWN.playSoundAtBlock(this, 2, 1);
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.POWERCRYSDESTROY.ordinal(), this, 32);
		this.delete();
	}

	@SideOnly(Side.CLIENT)
	public static void doDestroyParticles(World world, int x, int y, int z) {
		for (int i = 0; i < 24; i++) {
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.125);
			double vy = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
			double px = x+rand.nextDouble();
			double py = y+rand.nextDouble();
			double pz = z+rand.nextDouble();
			EntityFX fx = new EntityChromaFluidFX(world, px, py, pz, vx, vy, vz).setLife(80).setGravity(0.25F).setScale(1+rand.nextFloat());
			fx.noClip = false;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		for (int i = 0; i < 16; i++) {
			double px = x+rand.nextDouble();
			double py = y+rand.nextDouble();
			double pz = z+rand.nextDouble();
			EntityFloatingSeedsFX fx = (EntityFloatingSeedsFX)new EntityFloatingSeedsFX(world, px, py, pz, 0, 90).setIcon(ChromaIcons.CHROMA).setLife(80);
			fx.particleVelocity = 0.125;
			fx.freedom *= 2;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}
}

/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAuraPoint;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.LocationCached;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


// Shoot down hostile mobs,
public abstract class TileEntityLocusPoint extends TileEntityChromaticBase implements LocationCached {

	private static final Collection<WorldLocation> cache = new ArrayList();

	@Override
	public void breakBlock() {
		cache.remove(new WorldLocation(this));
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote) {
			this.spawnParticles(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {

		int n = this instanceof TileEntityAuraPoint ? 1+rand.nextInt(4) : 1;

		for (int i = 0; i < n; i++) {
			int color = this.getRenderColor();

			double v = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
			if (rand.nextInt(4) == 0)
				v *= ReikaRandomHelper.getRandomPlusMinus(2, 0.5);
			double theta = rand.nextDouble()*360;
			double phi = rand.nextDouble()*360;
			double[] xyz = ReikaPhysicsHelper.polarToCartesian(v, theta, phi);

			int r = ReikaColorAPI.getRed(color);
			int g = ReikaColorAPI.getGreen(color);
			int b = ReikaColorAPI.getBlue(color);

			int l = ReikaRandomHelper.getRandomPlusMinus(20, 10);
			EntityFX fx = new EntityBlurFX(world, x+0.5, y+0.5, z+0.5, xyz[0], xyz[1], xyz[2]).setColor(r, g, b).setScale(2).setLife(l);
			EntityFX fx2 = new EntityBlurFX(world, x+0.5, y+0.5, z+0.5, xyz[0], xyz[1], xyz[2]).setColor(255, 255, 255).setScale(1.1F).setLife(l);
			fx.noClip = false;
			fx2.noClip = false;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
	}

	public abstract int getRenderColor();

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.cacheTile();
	}

	private void cacheTile() {
		cache.add(new WorldLocation(this));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final double getMaxRenderDistanceSquared() {
		return 65536D;
	}

}

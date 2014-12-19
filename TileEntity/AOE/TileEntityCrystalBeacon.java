/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import Reika.DragonAPI.Interfaces.BreakAction;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCrystalBeacon extends CrystalReceiverBase implements BreakAction {

	private static final Collection<WorldLocation> cache = new ArrayList();

	public static final int RATIO = 100;
	public static final int POWER = 2;
	public static final int MAXRANGE = 32;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.BEACON;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (world.isRemote)
			this.spawnParticles(world, x, y, z);

		if (!world.isRemote && this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.checkAndRequest();
		}
	}

	private void checkAndRequest() {
		CrystalElement e = CrystalElement.RED;
		int capacity = this.getMaxStorage(e);
		int space = capacity-this.getEnergy(e);
		if (space > 0) {
			this.requestEnergy(e, space);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		double angle = (System.currentTimeMillis()/15D)%360;
		double d = 0.05;
		double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, d);
		double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, d);
		double py = ReikaRandomHelper.getRandomPlusMinus(y+1.5+0.5*(1+Math.sin(Math.toRadians(angle))), d);
		CrystalElement c = CrystalElement.randomElement();//CrystalElement.elements[(this.getTicksExisted()/16)%16];
		EntityBlurFX fx = new EntityBlurFX(c, world, px, py, pz, 0, 0, 0).setScale(2F).setLife(10).setIcon(ChromaIcons.CENTER);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public static boolean isPlayerInvincible(EntityPlayer ep, float dmg) {
		for (WorldLocation loc : cache) {
			if (Math.abs(ep.posY-loc.yCoord) <= 8 && loc.getDistanceTo(ep) <= MAXRANGE) {
				TileEntityCrystalBeacon te = (TileEntityCrystalBeacon)loc.getTileEntity();
				return te.isPlacer(ep) && te.prevent(dmg);
			}
		}
		return false;
	}

	private boolean prevent(float dmg) {
		int amt = (int)(RATIO*Math.pow(dmg, POWER));
		if (energy.containsAtLeast(CrystalElement.RED, amt)) {
			this.drainEnergy(CrystalElement.RED, amt);
			return true;
		}
		return false;
	}

	@Override
	public void onPathBroken(CrystalElement e) {

	}

	@Override
	public int getReceiveRange() {
		return 16;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e == CrystalElement.RED;
	}

	@Override
	public int maxThroughput() {
		return 500;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 250000;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		WorldLocation loc = new WorldLocation(this);
		if (!cache.contains(loc))
			cache.add(loc);
	}

	@Override
	public void breakBlock() {
		WorldLocation loc = new WorldLocation(this);
		cache.remove(loc);
	}

}

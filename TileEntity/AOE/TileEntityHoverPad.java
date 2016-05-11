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

import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//Make TESR render bounds as lines (think prelude)
public class TileEntityHoverPad extends TileEntityChromaticBase {

	private HoverMode mode = HoverMode.HOVER;

	private double particleX[];
	private double particleY[];
	private double particleZ[];

	private AxisAlignedBB hoverBox;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (hoverBox != null) {
			if (world.isRemote) {
				this.spawnParticles(world, x, y, z);
			}
			//else {
			List<EntityPlayer> li = world.getEntitiesWithinAABB(EntityPlayer.class, hoverBox);
			for (EntityPlayer ep : li) {
				if (!ep.isSneaking() && !ep.onGround && !ep.capabilities.isFlying) {
					ep.motionY = mode.fallVelocity;//+0.03125/2.5;
					ep.velocityChanged = true;
					float v = 0.15F;
					float v2 = 0;
					if (KeyWatcher.instance.isKeyDown(ep, Key.LEFT))
						v2 = v/2;
					else if (KeyWatcher.instance.isKeyDown(ep, Key.RIGHT))
						v2 = v/2;
					ep.moveFlying(v2, v, v);
				}
			}
			//}
		}
	}

	private int getParticleRate() {
		int base = mode == HoverMode.HOVER ? 1 : 4;
		base *= 4*Math.sqrt(ReikaAABBHelper.getVolume(hoverBox)/480D);
		return base;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		hoverBox = this.calculateBox(world, x, y, z);
		ReikaJavaLibrary.pConsole(hoverBox);
		if (hoverBox != null)
			this.initParticleVectors();
	}

	private AxisAlignedBB calculateBox(World world, int x, int y, int z) {
		int dy = y-1;
		while (world.getBlock(x, dy, z).isAir(world, x, dy, z)) {
			dy--;
		}
		Block bk = world.getBlock(x, dy, z);
		if (bk != ChromaBlocks.PAD.getBlockInstance())
			return null;
		BlockArray b = new BlockArray();
		b.recursiveAddWithBounds(world, x, dy, z, ChromaBlocks.PAD.getBlockInstance(), x-32, x+32, dy, dy, z-32, z+32);
		HashSet<Coordinate> set = new HashSet(b.keySet());
		for (int i = 1; i < 16; i++) {
			for (Coordinate c : set) {
				bk = world.getBlock(c.xCoord, c.yCoord+i, c.zCoord);
				if (bk.isAir(world, c.xCoord, c.yCoord+i, c.zCoord))
					b.addBlockCoordinate(c.xCoord, c.yCoord+i, c.zCoord);
			}
		}
		b.shaveToCube();
		return b.asAABB();
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		int t = mode == HoverMode.HOVER ? 2 : 8;
		int n = this.getParticleRate();
		for (int i = 0; i < n; i++) {
			if ((this.getTicksExisted()+i*t/n)%t == 0) {
				particleX[i] = ReikaRandomHelper.getRandomBetween(hoverBox.minX, hoverBox.maxX);
				particleY[i] = ReikaRandomHelper.getRandomBetween(hoverBox.minY, hoverBox.maxY);
				particleZ[i] = ReikaRandomHelper.getRandomBetween(hoverBox.minZ, hoverBox.maxZ);
			}
			float s = 1.5F;//+rand.nextFloat();
			int l = 25;//60+rand.nextInt(40);
			float g = 0;//mode.particleGravity*1.5F;
			int c = mode.particleColor;
			double v = -mode.particleGravity*2;
			EntityFX fx = new EntityBlurFX(world, particleX[i], particleY[i], particleZ[i], 0, v, 0).setGravity(g).setColor(c).setLife(l).setScale(s).setIcon(ChromaIcons.FADE_GENTLE);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.HOVERPAD;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public void toggleMode() {
		mode = mode.next();
		this.initParticleVectors();
		ChromaSounds.USE.playSoundAtBlock(this, 0.75F, 1);
	}

	private void initParticleVectors() {
		particleX = new double[this.getParticleRate()];
		particleY = new double[this.getParticleRate()];
		particleZ = new double[this.getParticleRate()];
		this.randomizeParticleLocations();
	}

	private void randomizeParticleLocations() {
		int n = this.getParticleRate();
		for (int i = 0; i < n; i++) {
			particleX[i] = ReikaRandomHelper.getRandomBetween(hoverBox.minX, hoverBox.maxX);
			particleY[i] = ReikaRandomHelper.getRandomBetween(hoverBox.minY, hoverBox.maxY);
			particleZ[i] = ReikaRandomHelper.getRandomBetween(hoverBox.minZ, hoverBox.maxZ);
		}
	}

	public static enum HoverMode {

		RISE(0x00ff00, -0.0625F, 0.1875),
		HOVER(0xffff00, 0F, 0),
		FALL(0xff0000, 0.0625F, -0.1875);

		public final int particleColor;
		public final float particleGravity;

		public final double fallVelocity;

		private static final HoverMode[] list = values();

		private HoverMode(int c, float g, double v) {
			particleColor = c;
			particleGravity = g;
			fallVelocity = v;
		}

		private HoverMode next() {
			return this.ordinal() == list.length-1 ? list[0] : list[this.ordinal()+1];
		}

	}

}

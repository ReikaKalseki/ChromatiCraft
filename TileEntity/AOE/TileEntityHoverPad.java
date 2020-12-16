/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.BlockHoverPad.HoverPadAuxTile;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import Reika.DragonAPI.Instantiable.CompoundAABB;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//Make TESR render bounds as lines (think prelude)
public class TileEntityHoverPad extends TileEntityChromaticBase {

	private static final double ACCELERATION_HORIZONTAL = 0.035;
	private static final double ACCELERATION_VERTICAL = 0.025;

	private static final double MAX_VEL_HORIZ = 0.45;
	private static final double MAX_VEL_VERT = 0.35;

	private CompoundAABB hoverBox;

	private final PlayerMap<Vec3> playerVelocities = new PlayerMap();

	@SideOnly(Side.CLIENT)
	private ParticleField playerParticles;
	private int particleBoost = 20;
	private int particleExpiry = 100;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (hoverBox == null)
			hoverBox = this.calculateBox(world, x, y, z);
		if (particleBoost > 0)
			particleBoost--;
		HashSet<UUID> watching = new HashSet(playerVelocities.keySet());
		if (this.hasRedstoneSignal()) {
			playerVelocities.clear();
			if (world.isRemote)
				this.updateMode(ParticleMode.DISABLED);
		}
		else {
			List<EntityPlayer> li = hoverBox.getEntitiesWithinAABB(EntityPlayer.class, world);
			for (EntityPlayer ep : li) {
				ParticleMode mode = this.handlePlayer(ep);
				watching.remove(ep.getUniqueID());
				if (world.isRemote)
					this.updateMode(mode);
			}
			for (UUID uid : watching) {
				playerVelocities.directRemove(uid);
			}
		}
		if (world.isRemote) {
			this.spawnParticles(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		if (playerVelocities.isEmpty()) {
			if (particleExpiry > 0)
				particleExpiry--;
			else
				playerParticles = null;
		}
		else {
			particleExpiry = 100;
		}
		if (playerParticles != null)
			playerParticles.doParticles(world, x, y, z, this);
	}

	private ParticleMode handlePlayer(EntityPlayer ep) {
		ParticleMode ret = ParticleMode.HOVER;
		if (ep.onGround) {
			playerVelocities.remove(ep);
			return ret;
		}
		ep.fallDistance = 0;
		Vec3 v = playerVelocities.get(ep);
		if (v == null) {
			v = Vec3.createVectorHelper(ep.motionX, ep.motionY*0, ep.motionZ);
			playerVelocities.put(ep, v);
		}
		if (KeyWatcher.instance.isKeyDown(ep, Key.LCTRL)) {
			v.xCoord *= 0.86;
			v.yCoord *= 0.86;
			v.zCoord *= 0.86;
		}
		else {
			if (KeyWatcher.instance.isKeyDown(ep, Key.JUMP)) {
				v.yCoord += ACCELERATION_VERTICAL;
				ret = ParticleMode.RISE;
			}
			if (KeyWatcher.instance.isKeyDown(ep, Key.SNEAK)) {
				v.yCoord -= ACCELERATION_VERTICAL;
				ret = ParticleMode.FALL;
			}
			if (KeyWatcher.instance.isKeyDown(ep, Key.FORWARD)) {
				Vec3 look = ep.getLookVec();
				v.xCoord += ACCELERATION_HORIZONTAL*look.xCoord;
				v.zCoord += ACCELERATION_HORIZONTAL*look.zCoord;
				ret = ParticleMode.MOVE;
			}
			if (KeyWatcher.instance.isKeyDown(ep, Key.BACK)) {
				Vec3 look = ep.getLookVec();
				v.xCoord -= ACCELERATION_HORIZONTAL*look.xCoord;
				v.zCoord -= ACCELERATION_HORIZONTAL*look.zCoord;
				ret = ParticleMode.MOVE;
			}
			if (KeyWatcher.instance.isKeyDown(ep, Key.LEFT)) {
				Vec3 look = ep.getLookVec();
				look = ReikaVectorHelper.rotateVector(look, 0, -90, 0);
				v.xCoord += ACCELERATION_HORIZONTAL*look.xCoord;
				v.zCoord += ACCELERATION_HORIZONTAL*look.zCoord;
				ret = ParticleMode.MOVE;
			}
			if (KeyWatcher.instance.isKeyDown(ep, Key.RIGHT)) {
				Vec3 look = ep.getLookVec();
				look = ReikaVectorHelper.rotateVector(look, 0, 90, 0);
				v.xCoord += ACCELERATION_HORIZONTAL*look.xCoord;
				v.zCoord += ACCELERATION_HORIZONTAL*look.zCoord;
				ret = ParticleMode.MOVE;
			}
		}
		v.xCoord = MathHelper.clamp_double(v.xCoord, -MAX_VEL_HORIZ, MAX_VEL_HORIZ);
		v.yCoord = MathHelper.clamp_double(v.yCoord, -MAX_VEL_VERT, MAX_VEL_VERT);
		v.zCoord = MathHelper.clamp_double(v.zCoord, -MAX_VEL_HORIZ, MAX_VEL_HORIZ);
		if (ret == ParticleMode.HOVER) {
			if (v.yCoord < -0.025)
				ret = ParticleMode.FALL;
			else if (v.yCoord > 0.025)
				ret = ParticleMode.RISE;
			else if (ReikaMathLibrary.py3d(v.xCoord, 0, v.zCoord) > 0.025)
				ret = ParticleMode.MOVE;
		}
		ep.motionX = v.xCoord;
		ep.motionY = v.yCoord;
		ep.motionZ = v.zCoord;
		ep.velocityChanged = true;
		return ret;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {

	}

	public void clearBox() {
		hoverBox = this.calculateBox(worldObj, xCoord, yCoord, zCoord);
		particleBoost = 20;
		if (worldObj.isRemote && playerParticles != null)
			playerParticles.initParticleVectors(this);
		if (!worldObj.isRemote) {
			ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.CLEARHOVERBOX.ordinal(), this, 192);
		}
	}

	private CompoundAABB calculateBox(World world, int x, int y, int z) {
		BlockArray b = new BlockArray();
		HashSet<BlockKey> ids = new HashSet();
		ids.add(new BlockKey(ChromaBlocks.PAD.getBlockInstance(), 0));
		ids.add(new BlockKey(this.getTile().getBlock(), this.getTile().getBlockMetadata()));
		b.recursiveAddMultipleWithBounds(world, x, y, z, ids, x-32, y, z-32, x+32, y, z+32);
		HashSet<Coordinate> set = new HashSet(b.keySet());
		for (Coordinate c : set) {
			Block b1 = c.getBlock(world);
			if (b1 == ChromaBlocks.PAD.getBlockInstance()) {
				HoverPadAuxTile te = (HoverPadAuxTile)c.getTileEntity(world);
				te.setTile(this);
			}
			for (int i = 1; i < 24; i++) {
				Block bk = world.getBlock(c.xCoord, c.yCoord+i, c.zCoord);
				if (bk.getCollisionBoundingBoxFromPool(world, c.xCoord, c.yCoord, c.zCoord) == null)
					b.addBlockCoordinate(c.xCoord, c.yCoord+i, c.zCoord);
				else
					break;
			}
		}
		return new CompoundAABB(b);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.HOVERPAD;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@SideOnly(Side.CLIENT)
	private void updateMode(ParticleMode to) {
		ParticleMode prev = playerParticles != null ? playerParticles.mode : null;
		if (prev != to) {
			playerParticles = new ParticleField(to);
			playerParticles.initParticleVectors(this);
		}
	}

	private static enum ParticleMode {

		HOVER(0xffff00, 0F, 2, 1),
		RISE(0x00ff00, -0.0625F, 8, 4),
		FALL(0xff0000, 0.0625F, 8, 4),
		MOVE(0x22aaff, 0, 5, 2),
		DISABLED(0xbababa, 0, 2, 1);

		public final int particleColor;
		public final float particleGravity;
		public final int particleModulo;
		public final int particleCount;

		private static final ParticleMode[] list = values();

		private ParticleMode(int c, float g, int m, int n) {
			particleColor = c;
			particleGravity = g;
			particleModulo = m;
			particleCount = n;
		}

		private int getParticleRate(TileEntityHoverPad te) {
			int base = particleCount;
			base *= 4*Math.sqrt(te.hoverBox.getVolume()/480D);
			return Math.max(1, base);
		}

	}

	private static class ParticleField {

		private final ParticleMode mode;

		private double particleX[];
		private double particleY[];
		private double particleZ[];

		private ParticleField(ParticleMode m) {
			mode = m;
		}

		private void initParticleVectors(TileEntityHoverPad te) {
			particleX = new double[mode.getParticleRate(te)];
			particleY = new double[mode.getParticleRate(te)];
			particleZ = new double[mode.getParticleRate(te)];
			this.randomizeParticleLocations(te);
		}

		private void randomizeParticleLocations(TileEntityHoverPad te) {
			for (int i = 0; i < particleX.length; i++) {
				AxisAlignedBB box = te.hoverBox.getRandomComponentBox(true);
				particleX[i] = ReikaRandomHelper.getRandomBetween(box.minX, box.maxX);
				particleY[i] = ReikaRandomHelper.getRandomBetween(box.minY, box.maxY);
				particleZ[i] = ReikaRandomHelper.getRandomBetween(box.minZ, box.maxZ);
			}
		}

		@SideOnly(Side.CLIENT)
		private void doParticles(World world, int x, int y, int z, TileEntityHoverPad te) {
			int n = mode.getParticleRate(te);
			for (int i = 0; i < n; i++) {
				if ((te.getTicksExisted()+i*mode.particleModulo/n)%mode.particleModulo == 0) {
					AxisAlignedBB box = te.hoverBox.getRandomComponentBox(true);
					particleX[i] = ReikaRandomHelper.getRandomBetween(box.minX, box.maxX);
					particleY[i] = ReikaRandomHelper.getRandomBetween(box.minY, box.maxY);
					particleZ[i] = ReikaRandomHelper.getRandomBetween(box.minZ, box.maxZ);
				}
				float s = 1.5F;//+rand.nextFloat();
				int l = 25;//60+rand.nextInt(40);
				float g = 0;//mode.particleGravity*1.5F;
				int c = mode.particleColor;
				double v = -mode.particleGravity*2;
				double vx = 0;
				double vz = 0;
				if (mode == ParticleMode.MOVE) {
					double dv = 0.05;
					EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
					double dd = ReikaMathLibrary.py3d(ep.motionX, 0, ep.motionZ);
					vx = ep.motionX/dd*dv;
					vz = ep.motionZ/dd*dv;
				}
				if (mode == ParticleMode.DISABLED) {
					l *= 0.4;
					s *= 0.8;
				}
				EntityCCBlurFX fx = new EntityCCBlurFX(world, particleX[i], particleY[i], particleZ[i], vx, v, vz);
				fx.setIcon(ChromaIcons.FADE_GENTLE).setGravity(g).setColor(c).setLife(l).setScale(s);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}

	}

}

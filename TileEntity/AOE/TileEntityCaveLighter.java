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

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Interfaces.RangeUpgradeable;
import Reika.ChromatiCraft.Auxiliary.RangeTracker;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.Decoration.BlockEtherealLight;
import Reika.ChromatiCraft.Block.Decoration.BlockEtherealLight.Flags;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockSpiral;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityCaveLighter extends TileEntityChromaticBase implements RangeUpgradeable {

	public static final int BASE_RANGE = Math.min(64, ChromaOptions.CAVELIGHTERRANGE.getValue());
	public static final int MAXY = 80;
	private static final int ZONE_SIZE = MathHelper.clamp_int(4, 16, ChromaOptions.CAVELIGHTERSIZE.getValue());

	private final BlockSpiral[] spiral = new BlockSpiral[MAXY/ZONE_SIZE];

	private int idleTicks;
	//private boolean complete = false;

	private final RangeTracker range = new RangeTracker(BASE_RANGE);

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.LIGHTER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (range.update(this)) {
			this.initSpirals(world, x, y, z);
		}
		if (world.isRemote) {
			this.doParticles(world, x, y, z);
		}
		else/* if (!complete)*/ {
			this.placeLights(world, x, y, z);
		}
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		//NBT.setBoolean("finished", complete);
		NBT.setInteger("idle", idleTicks);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		//complete = NBT.getBoolean("finished");
		idleTicks = NBT.getInteger("idle");
	}

	private void placeLights(World world, int x, int y, int z) {
		boolean flag = false;
		for (int m = 0; m < 1/*6*/; m++) {
			for (int i = 0; i < spiral.length; i++) {
				if (spiral[i].getSize() > 0) {
					Coordinate c = spiral[i].getNextAndMoveOn();
					int cx = c.xCoord+rand.nextInt(ZONE_SIZE);
					int cy = 4+c.yCoord+rand.nextInt(ZONE_SIZE);
					int cz = c.zCoord+rand.nextInt(ZONE_SIZE);
					if (this.placeBlockAt(world, cx, cy, cz)) {
						world.setBlock(cx, cy, cz, ChromaBlocks.LIGHT.getBlockInstance(), Flags.PARTICLES.getFlag(), 3);
						world.markBlockForUpdate(cx, cy, cz);
						ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.LIGHTERACT.ordinal(), this, 32, cx, cy, cz);
						//ReikaJavaLibrary.pConsole("Lighting "+new Coordinate(cx, cy, cz));
					}
					//else {
					//	Coordinate cp = new Coordinate(cx, cy, cz);
					//	ReikaJavaLibrary.pConsole("Not lighting "+cp+", "+cp.getBlock(world).getLocalizedName());
					//}
					flag = true;
					if (spiral[i].getSize() == 0) {
						ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.LIGHTERDELTAY.ordinal(), this, 32, (i+1)*ZONE_SIZE);
						if (i == spiral.length-1) {
							ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.LIGHTEREND.ordinal(), this, 32);
							//complete = true;
						}
					}
					break;
				}
			}
		}
		if (!flag) {
			idleTicks++;
			if (idleTicks > 100) {
				idleTicks = 0;
				this.initSpirals(world, x, y, z);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		if (/*complete*/idleTicks > 0) {
			ReikaParticleHelper.spawnColoredParticlesWithOutset(world, x, y, z, 5, 5, 5, 8, 0.0625);
		}
	}

	@SideOnly(Side.CLIENT)
	public void doDeltaYParticles(int newY) {
		int n = 6+rand.nextInt(18);
		int c = BlockEtherealLight.getParticleColor(worldObj, newY);
		for (int i = 0; i < n; i++) {
			double rx = xCoord+rand.nextDouble();
			double ry = yCoord+rand.nextDouble();
			double rz = zCoord+rand.nextDouble();
			EntityFX fx = new EntityCCBlurFX(worldObj, rx, ry, rz).setColor(c).setGravity(-0.03125F).setLife(30).setScale(1.25F);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@SideOnly(Side.CLIENT)
	public void doCompletionParticles() {
		int n = 16+rand.nextInt(24);
		for (int i = 0; i < n; i++) {
			double dx = rand.nextDouble()-0.5;
			double dy = rand.nextDouble()-0.5;
			double dz = rand.nextDouble()-0.5;
			double v = ReikaRandomHelper.getRandomPlusMinus(0.25, 0.125);
			double rx = xCoord+0.5+dx;
			double ry = yCoord+0.5+dy;
			double rz = zCoord+0.5+dz;
			EntityFX fx = new EntityCCBlurFX(worldObj, rx, ry, rz, dx*v, dy*v, dz*v).setColor(0xffffff).setGravity(0).setLife(60).setScale(1.75F).setRapidExpand();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		ReikaSoundHelper.playClientSound(ChromaSounds.CAST, xCoord+0.5, yCoord+0.5, zCoord+0.5, 1, 1);
	}

	@SideOnly(Side.CLIENT)
	public void doLightedParticles(int x, int y, int z) {
		double dx = x-xCoord;
		double dz = z-zCoord;
		double ang = Math.toRadians(-90-ReikaPhysicsHelper.cartesianToPolar(dx, 0, dz)[2]);
		double mr = 3;
		for (double r = 0; r < mr; r += 0.0625) {
			double px = xCoord+0.5+r*Math.cos(ang);
			double pz = zCoord+0.5+r*Math.sin(ang);
			int c = BlockEtherealLight.getParticleColor(worldObj, y);
			float s = (float)(2-(r/mr)*2);
			EntityFX fx = new EntityCCBlurFX(worldObj, px, yCoord+0.5, pz).setIcon(ChromaIcons.CENTER).setColor(c).setGravity(0).setLife(80).setScale(s).setRapidExpand();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		range.initialize(this);
		//if (!complete)
		this.initSpirals(world, x, y, z);
	}

	private void initSpirals(World world, int x, int y, int z) {
		int r = this.getRange();
		for (int i = 0; i < spiral.length; i++) {
			spiral[i] = new BlockSpiral(x, i*ZONE_SIZE, z, r/ZONE_SIZE).setRightHanded().setGridSize(ZONE_SIZE).calculate();
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	private boolean placeBlockAt(World world, int x, int y, int z) {
		return world.getBlock(x, y, z).isAir(world, x, y, z) && y < this.getMaxY(world, x, z) && this.isDark(world, x, y, z);
	}

	private int getMaxY(World world, int x, int z) {
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		return BiomeGlowingCliffs.isGlowingCliffs(b) ? 60 : MAXY;
	}

	private boolean isDark(World world, int x, int y, int z) {
		return !world.canBlockSeeTheSky(x, y+1, z) && world.getBlockLightValue(x, y, z) <= 7 && world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) < 4;
	}

	@Override
	public void upgradeRange(double r) {

	}

	@Override
	public int getRange() {
		return range.getRange();
	}

}

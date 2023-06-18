/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Decoration;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromaClient;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Base.CrystalTypeBlock;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.DragonAPI.Instantiable.CubeRotation;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Interfaces.TextureFetcher;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockColoredAltar extends CrystalTypeBlock {

	private static final Random rand = new Random();

	public BlockColoredAltar(Material mat) {
		super(mat);

		this.setHardness(2);
		this.setResistance(600000);

		this.setBlockBounds(0, 0, 0, 1, 0.5F, 1);

		this.setCreativeTab(ChromatiCraft.tabChromaDeco);
	}

	public static class TileEntityColoredAltar extends TileEntity implements RenderFetcher {

		private int ticks;
		private int tickOffset;

		public CrystalElement renderColor = CrystalElement.WHITE;

		public static final int NUMBER_CUBES = 12;

		public final CubeRotation[] cubeRotations = new CubeRotation[NUMBER_CUBES];

		private int neighborAreaCheckCount = -1;
		private float neighborAreaCheckValue = -1;
		private final ArrayList<Coordinate> otherAltars = new ArrayList();

		public TileEntityColoredAltar() {
			this.randomize();
		}

		private void randomize() {
			tickOffset = rand.nextInt(360);

			for (int i = 0; i < cubeRotations.length; i++) {
				cubeRotations[i] = new CubeRotation().randomize(rand);
			}
		}

		public CrystalElement getColor() {
			return worldObj != null ? CrystalElement.elements[this.getBlockMetadata()] : renderColor;
		}

		@Override
		public boolean canUpdate() {
			return true;
		}

		@Override
		public void updateEntity() {
			ticks++;

			if (worldObj.isRemote) {
				this.renderParticles(this.getColor());

				if (neighborAreaCheckCount < 0 || this.getTicksExisted()%80 == 0) {
					this.calcNeighborDensity();
				}

				this.doAoEFX();
			}
		}

		@SideOnly(Side.CLIENT)
		private void doAoEFX() {
			float f = neighborAreaCheckValue/neighborAreaCheckCount;
			float f2 = Math.min(f, f*f*27);
			if (!otherAltars.isEmpty() && f > 0.0125F/25 && rand.nextFloat() < f) {
				CrystalElement e = CrystalElement.elements[this.getBlockMetadata()];
				if (rand.nextFloat() < f) {
					EntityPlayer ep = worldObj.getClosestPlayer(xCoord, yCoord, zCoord, 12);
					if (ep != null) {
						//ChromaAux.dischargeIntoPlayer(xCoord+0.5, yCoord+0.5, zCoord+0.5, rand, ep, e, 1, 1);
						ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ALTARSHOCK.ordinal(), PacketTarget.server, xCoord, yCoord, zCoord, e.ordinal());
					}
				}
				else {
					Coordinate c = ReikaJavaLibrary.getRandomListEntry(rand, otherAltars);
					if (c != null)
						ChromaFX.doBoltFX(worldObj, xCoord, yCoord, zCoord, new DecimalPosition(c), e.getColor(), 1, 1, false);
					ReikaSoundHelper.playClientSound(ChromaSounds.MONUMENTRAY, Minecraft.getMinecraft().thePlayer, 1, (float)CrystalMusicManager.instance.getDingPitchScale(e), true);
				}
				if (f > 0.0006F && rand.nextFloat() < f2) {
					ReikaSoundHelper.playClientSound(ChromaSounds.POWERDOWN, Minecraft.getMinecraft().thePlayer, 1, 1, true);
					//ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.SETAIR.ordinal(), PacketTarget.server, xCoord, yCoord, zCoord);
					ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ALTARBREAK.ordinal(), PacketTarget.server, xCoord, yCoord, zCoord);
					ReikaRenderHelper.spawnDropParticles(worldObj, xCoord, yCoord, zCoord, this.getBlockType(), this.getBlockMetadata());
					this.updateNeighbors();
					double da = 20;
					for (double a1 = 0; a1 < 360; a1 += da) {
						for (double a2 = -90+da; a2 < 90-da; a2 += da) {
							double v = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
							double[] xyz = ReikaPhysicsHelper.polarToCartesian(v, a2, a1);
							float s = (float)ReikaRandomHelper.getRandomBetween(2D, 3D);
							int c1 = e.getColor();
							int l = 30+rand.nextInt(20);
							EntityCCBlurFX fx = new EntityCCBlurFX(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, xyz[0], xyz[1], xyz[2]);
							fx.setRapidExpand().setScale(s).setLife(l).setGravity(0).fadeColors(0xffffff, c1);
							fx.setIcon(ChromaIcons.FADE_RAY).setColliding();
							Minecraft.getMinecraft().effectRenderer.addEffect(fx);
						}
					}
				}
			}
		}

		private void updateNeighbors() {
			for (Coordinate c : otherAltars) {
				TileEntity te = c.getTileEntity(worldObj);
				if (te instanceof TileEntityColoredAltar)
					((TileEntityColoredAltar)te).calcNeighborDensity();
			}
		}

		private void calcNeighborDensity() {
			otherAltars.clear();
			int r = 9;
			int ry = 3;
			Block b = this.getBlockType();
			neighborAreaCheckCount = 0;
			neighborAreaCheckValue = 0;
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					for (int j = -ry; j <= ry; j++) {
						if (i != 0 || j != 0 || k != 0) {
							int dx = xCoord+i;
							int dy = yCoord+j;
							int dz = zCoord+k;
							neighborAreaCheckCount++;
							if (worldObj.getBlock(dx, dy, dz) == b) {
								Coordinate c = new Coordinate(dx, dy, dz);
								otherAltars.add(c);
								float f = MathHelper.clamp_float(4-c.getTaxicabDistanceTo(xCoord, yCoord, zCoord)/6, 1, 4);
								neighborAreaCheckValue += f*f;
							}
						}
					}
				}
			}
		}

		//Have particles (blur) spill over the rim, use some more to make a pulsing glow effect in the middle, and then make some laser ones lazily float up
		@SideOnly(Side.CLIENT)
		private void renderParticles(CrystalElement e) {
			int ps = Minecraft.getMinecraft().gameSettings.particleSetting;
			double n = 2;
			switch(ps) {
				case 1:
					n = 1;
					break;
				case 2:
					n = 0.5;
					break;
			}
			double d = Math.max(1, Minecraft.getMinecraft().thePlayer.getDistanceSq(xCoord+0.5, yCoord+0.25, zCoord+0.5)/256D);
			n /= d;

			int num = n <= 1 ? ReikaRandomHelper.doWithChance(n) ? 1 : 0 : (int)n;

			for (int i = 0; i < num; i++) {
				double r = ReikaRandomHelper.getRandomPlusMinus(0.55, 0.05);
				double ang = rand.nextDouble()*360;;//(ticks*8+i*360/n)%360
				double dx = xCoord+0.5+r*Math.sin(Math.toRadians(ang));
				double dy = yCoord+0.5;
				double dz = zCoord+0.5+r*Math.cos(Math.toRadians(ang));
				float g = (float)ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
				int l = 20+rand.nextInt(20);
				float s = (float)ReikaRandomHelper.getRandomPlusMinus(2F, 1F);
				double dang = ReikaRandomHelper.getRandomPlusMinus(ang, 10);
				EntityFX fx = new EntityCCBlurFX(e, worldObj, dx, dy, dz, 0, 0, 0).setGravity(g).setLife(l).setScale(s).setColliding(dang);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}

			if (rand.nextInt(2) == 0) {
				double dr = 0.25;
				double dx = ReikaRandomHelper.getRandomPlusMinus(xCoord+0.5, dr);
				double dz = ReikaRandomHelper.getRandomPlusMinus(zCoord+0.5, dr);
				double dy = yCoord+0.375;
				int l = 10+rand.nextInt(60);
				float s = (float)ReikaRandomHelper.getRandomPlusMinus(1.5F, 1F);

				int r = e.getRed();
				int g = e.getGreen();
				int b = e.getBlue();

				r = (int)(r*ReikaRandomHelper.getRandomPlusMinus(0.5, 0.25));
				g = (int)(g*ReikaRandomHelper.getRandomPlusMinus(0.5, 0.25));
				b = (int)(b*ReikaRandomHelper.getRandomPlusMinus(0.5, 0.25));

				if (e == CrystalElement.WHITE || e == CrystalElement.BLACK || e == CrystalElement.GRAY || e == CrystalElement.LIGHTGRAY) {
					int avg = (r+g+b)/3;
					r = g = b = avg;
				}

				EntityFX fx = new EntityCCBlurFX(worldObj, dx, dy, dz, 0, 0, 0).setGravity(0).setLife(l).setScale(s).setColor(r, g, b);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}

			if (rand.nextInt(8) == 0) {
				double dr = 0.375;
				double dx = ReikaRandomHelper.getRandomPlusMinus(xCoord+0.5, dr);
				double dz = ReikaRandomHelper.getRandomPlusMinus(zCoord+0.5, dr);
				double dy = yCoord+0.55;
				int l = 40+rand.nextInt(40);
				float s = (float)ReikaRandomHelper.getRandomPlusMinus(1.5F, 1F);
				float g = -(float)ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
				EntityFX fx = new EntityLaserFX(e, worldObj, dx, dy, dz, 0, 0, 0).setGravity(g).setScale(s);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}

		@Override
		public boolean shouldRenderInPass(int pass) {
			return pass <= 1;
		}

		@Override
		public AxisAlignedBB getRenderBoundingBox() {
			return AxisAlignedBB.getBoundingBox(xCoord-0.5, yCoord, zCoord-0.5, xCoord+1.5, yCoord+3, zCoord+1.5);
		}

		public int getTicksExisted() {
			return ticks;
		}

		public int getRenderTick() {
			return this.getTicksExisted()+tickOffset;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public TextureFetcher getRenderer() {
			return ChromaClient.altarRenderer;
		}

	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityColoredAltar();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public int getBrightness(IBlockAccess iba, int x, int y, int z) {
		return 15;
	}

	@Override
	protected void onBroken(World world, int x, int y, int z) {
		if (world.isRemote) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileEntityColoredAltar) {
				((TileEntityColoredAltar)te).updateNeighbors();
			}
		}
	}



	@Override
	@SideOnly(Side.CLIENT)
	public final boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer eff) {
		return ReikaRenderHelper.addModelledBlockParticles("/Reika/ChromatiCraft/Textures/TileEntity/", world, x, y, z, this, eff, ReikaJavaLibrary.makeListFrom(new double[]{0,0,1,1}), ChromatiCraft.class);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean addHitEffects(World world, MovingObjectPosition tg, EffectRenderer eff) {
		return ReikaRenderHelper.addModelledBlockParticles("/Reika/ChromatiCraft/Textures/TileEntity/", world, tg, this, eff, ReikaJavaLibrary.makeListFrom(new double[]{0,0,1,1}), ChromatiCraft.class);
	}
}

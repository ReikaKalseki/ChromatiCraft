/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.WorldGenInterceptionRegistry;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.AbstractSearch.PropagationCondition;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.IterativeRecurser;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldChunk;
import Reika.DragonAPI.Instantiable.Data.Maps.NestedMap;
import Reika.DragonAPI.Instantiable.Effects.LightningBolt;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent;
import Reika.DragonAPI.Instantiable.Event.SetBlockEvent;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Instantiable.Math.Noise.NoiseGeneratorBase;
import Reika.DragonAPI.Instantiable.Math.Noise.Simplex3DGenerator;
import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EndOverhaulManager extends IRenderHandler implements RetroactiveGenerator {

	public static final EndOverhaulManager instance = new EndOverhaulManager();

	public static final double DEGREE_SEPARATION = 12;
	public static final double MIN_DIST_SQ_CH = 2000;
	public static final double MAX_DIST_SQ_CH = 3000;

	private static final double TENDRIL_THRESH_0 = 0.03;
	private static final double TENDRIL_THRESH_1 = 0.02;
	private static final double TENDRIL_THRESH_2 = 0.065;

	private static final double MIN_HEIGHT = 40;
	private static final double MAX_HEIGHT = 55;

	private final BlockKey coreBlock = new BlockKey(Blocks.obsidian, 0);
	private final BlockKey coatingBlock = new BlockKey(Blocks.end_stone, 0);

	private long seed;
	private SimplexNoiseGenerator placementNoise;
	private SimplexNoiseGenerator yLevelNoise;
	private final ArrayList<Tendril> tendrils = new ArrayList();

	@SideOnly(Side.CLIENT)
	private float renderFactor;
	@SideOnly(Side.CLIENT)
	private float fogDistance;

	private EndOverhaulManager() {

	}

	public static double getAngleOffset(World world) {
		return world.getSeed()%10000;
	}

	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.dimensionId != 1)
			return;

		WorldGenInterceptionRegistry.skipLighting = true;
		SetBlockEvent.eventEnabledPre = false;
		SetBlockEvent.eventEnabledPost = false;
		BlockTickEvent.disallowAllUpdates = true;

		this.setSeed(world);
		/*
		for (int i = 0; i < 16; i++) {
			for (int k = 0; k < 16; k++) {
				int dx = chunkX*16+i;
				int dz = chunkZ*16+k;
				double val = Math.abs(placementNoise.getValue(dx, dz));
				if (val <= TENDRIL_THRESH_2) {
					double h = 0;
					int c = 0;
					int min = Integer.MAX_VALUE;
					for (int a = -24; a <= 24; a += 8) {
						for (int b = -24; b <= 24; b += 8) {
							double hval = ReikaMathLibrary.normalizeToBounds(yLevelNoise.getValue(dx+a, dz+b), MIN_HEIGHT, MAX_HEIGHT);
							int top = world.getTopSolidOrLiquidBlock(dx, dz);
							if (top > 0)
								hval = Math.min(hval, top-3);
							h += hval;
							min = Math.min((int)hval, min);
							c++;
						}
					}
					h /= c;
					int y = (int)h;
					double f = ((val-TENDRIL_THRESH_1)/(TENDRIL_THRESH_2-TENDRIL_THRESH_1));
					double t = val < TENDRIL_THRESH_1 ? 1 : 1-f*f;
					int th = (int)(t*4);
					int y0 = y-th;
					for (int dy = y0; dy <= y; dy++) {
						if (world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz)) {
							BlockKey bk = val < TENDRIL_THRESH_0 && (dy == y || dy >= y0+2) ? coreBlock : coatingBlock;
							if (bk == coreBlock && dy >= min)
								continue;
							bk.place(world, dx, dy, dz);
						}
					}
				}
			}
		}
		 */
		for (Tendril t : tendrils) {
			WorldChunk wc = new WorldChunk(world, chunkX, chunkZ);
			Map<Coordinate, BlockKey> map = t.blocks.getMap(wc);
			if (map != null) {
				for (Entry<Coordinate, BlockKey> e : map.entrySet()) {
					Coordinate c = e.getKey();
					BlockKey bk = e.getValue();
					if (!c.isEmpty(world) && (bk.equals(coatingBlock) || c.getBlock(world) != Blocks.end_stone))
						continue;
					bk.place(world, c.xCoord, c.yCoord, c.zCoord);
				}
			}
		}
		BlockTickEvent.disallowAllUpdates = false;
		WorldGenInterceptionRegistry.skipLighting = false;
		SetBlockEvent.eventEnabledPre = true;
		SetBlockEvent.eventEnabledPost = true;
	}

	private void setSeed(World world) {
		long s = world.getSeed();
		if (seed != s || placementNoise == null) {
			seed = s;
			Random rand = new Random(seed);
			rand.nextBoolean();
			placementNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed).setFrequency(0.01);
			yLevelNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(-seed).setFrequency(0.13);
			tendrils.clear();
			double off = this.getAngleOffset(world);
			for (double d = 0; d < 360; d += DEGREE_SEPARATION) {
				Tendril t = new Tendril(rand.nextLong(), d+off).calculate(world);
				tendrils.add(t);
			}
		}
	}

	@Override
	public String getIDString() {
		return "Chroma_EndTendrils";
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return world.provider.dimensionId == 1;
	}

	public void clear() {
		seed = -1;
		placementNoise = null;
		yLevelNoise = null;
		tendrils.clear();
	}

	@SideOnly(Side.CLIENT)
	public void updateRenderer(AbstractClientPlayer ep) {
		if (ep == null || ep.worldObj == null || ep.worldObj.provider.dimensionId != 1) {
			renderFactor = 0;
			return;
		}
		ep.worldObj.provider.setSkyRenderer(this);
		double distChSq = (ep.posX*ep.posX+ep.posZ*ep.posZ)/256;
		float target = 0;
		float lim = 0.75F;
		float spf = 1;
		float fogTarget = 999;
		if (distChSq > 60) {
			if (distChSq < MIN_DIST_SQ_CH+240) {
				target = lim;
				fogTarget = 80;
			}
			else if (distChSq < MAX_DIST_SQ_CH) {
				target = 0.1F;
				fogTarget = 240;
			}
		}
		else if (distChSq > 30) {
			target = lim*(float)(distChSq-30)/30F;
			spf *= 5;
		}
		//ReikaJavaLibrary.pConsole(distChSq+" > "+target+" > "+renderFactor);
		if (renderFactor > target) {
			renderFactor = Math.max(target, renderFactor-0.0008F*spf);
		}
		else if (renderFactor < target) {
			renderFactor = Math.min(target, renderFactor+0.02F*spf);
		}
		if (fogDistance > fogTarget) {
			fogDistance = Math.max(fogTarget, fogDistance-3.5F*spf);
		}
		else if (fogDistance < fogTarget) {
			fogDistance = Math.min(fogTarget, fogDistance+18F*spf);
		}
		ChromaShaders.ENDRING.setIntensity(renderFactor*0);
		if (renderFactor > 0.2 && !Minecraft.getMinecraft().isGamePaused() && ReikaRandomHelper.doWithChance(renderFactor*0.04)) {
			double px = ReikaRandomHelper.getRandomPlusMinus(ep.posX, 32);
			double py = ReikaRandomHelper.getRandomPlusMinus(ep.posY, 32);
			double pz = ReikaRandomHelper.getRandomPlusMinus(ep.posZ, 32);
			EntityCCBlurFX fx = new EntityCCBlurFX(ep.worldObj, px, py, pz);
			fx.setIcon(ChromaIcons.FADE_BASICBLEND).setBasicBlend().setAlphaFading().setRapidExpand().setLife(600).setScale(20).setColor(DragonAPICore.rand.nextInt(5) == 0 ? 0xE57042 : 0xffffff);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@SideOnly(Side.CLIENT)
	public float getRenderFactor() {
		return renderFactor;
	}

	@SideOnly(Side.CLIENT)
	public float rampFog(float original) {
		return fogDistance*renderFactor+(1-renderFactor)*original;
	}

	private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		float fd = Math.max(0.001F, renderFactor*renderFactor-0.2F);
		if (fd > 0)
			GL11.glEnable(GL11.GL_FOG);
		else
			GL11.glDisable(GL11.GL_FOG);
		GL11.glFogf(GL11.GL_FOG_DENSITY, MathHelper.clamp_float(renderFactor*0.000002F, 0, 1));
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		RenderHelper.disableStandardItemLighting();
		//GL11.glDepthMask(false);
		mc.renderEngine.bindTexture(locationEndSkyPng);
		Tessellator v5 = Tessellator.instance;

		for (int i = 0; i < 6; ++i) {
			GL11.glPushMatrix();
			switch(i) {
				case 1:
					GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
					break;

				case 2:
					GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
					break;

				case 3:
					GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
					break;

				case 4:
					GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
					break;

				case 5:
					GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
					break;
			}

			float s = 200;//MathHelper.clamp_double(ReikaMathLibrary.linterpolate(renderFactor, 0.75, 0.25, 100, 20), 0, 100);
			GL11.glFogf(GL11.GL_FOG_START, (float)ReikaMathLibrary.linterpolate(renderFactor, 0, 1, 150, 0));
			int a = (int)(255*Math.max(0, 1-4*renderFactor));
			v5.startDrawingQuads();
			//v5.setColorOpaque_I(0x282828);
			v5.setColorRGBA_I(0x282828, a);
			v5.addVertexWithUV(-s, -s, -s, 0.0D, 0.0D);
			v5.addVertexWithUV(-s, -s, s, 0.0D, 16.0D);
			v5.addVertexWithUV(s, -s, s, 16.0D, 16.0D);
			v5.addVertexWithUV(s, -s, -s, 16.0D, 0.0D);
			v5.draw();
			GL11.glPopMatrix();
		}

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
	}

	private class Island {

		private final Coordinate center;
		private final int radius;

		private final Collection<Island> children = new ArrayList();

		private final HashSet<Coordinate> stone = new HashSet();
		private final HashMap<Coordinate, Integer> topLevel = new HashMap();

		private final long seed;
		private final NoiseGeneratorBase shape;

		private Island(Coordinate c, int size, long seed) {
			center = c;
			radius = size;
			this.seed = seed;
			shape = new Simplex3DGenerator(seed).setFrequency(1/16D);
		}

		private Island addChild(int dx, int dy, int dz, float fr) {
			children.add(new Island(center.offset(dx, dy, dz), (int)(fr*radius), (seed << 4) | (seed >> (64 - 4))));
			return this;
		}

		public void generate(World world, Random rand) {
			this.runGen1(world, rand, stone);
			for (Island is : children)
				is.runGen1(world, rand, stone);
			for (Coordinate c : stone) {
				Coordinate k = c.to2D();
				Integer get = topLevel.get(k);
				if (get == null || get.intValue() < c.yCoord)
					topLevel.put(k, c.yCoord);
			}
			for (Coordinate c : stone) {
				Coordinate c2 = c;
				while (c2.yCoord <= topLevel.get(c.to2D())) {
					c2.setBlock(world, Blocks.end_stone, 0, 2);
					c2 = c2.offset(0, 1, 0);
				}
			}
			this.runGen2(world, rand);
		}

		private void runGen1(World world, Random rand, HashSet<Coordinate> set) {
			int x = center.xCoord;
			int y = center.yCoord;
			int z = center.zCoord;
			final double thickness = 0.15+rand.nextDouble()*0.15;
			IterativeRecurser it = new IterativeRecurser(x, y, z);
			it.limit = BlockBox.block(x, y, z).expand(radius+2, (int)(radius*thickness)+2, radius+2);
			it.run(world, new PropagationCondition() {

				@Override
				public boolean isValidLocation(World world, int x, int y, int z, Coordinate from) {
					int dx = x-center.xCoord;
					int dy = y-center.yCoord;
					int dz = z-center.zCoord;
					double dr = ReikaMathLibrary.normalizeToBounds(shape.getValue(x, y, z), 0.75, 1.25);
					return ReikaMathLibrary.isPointInsideEllipse(dx, dy, dz, dr*radius, dr*radius*thickness, dr*radius);
				}

			});

			set.addAll(it.getResult());
		}

		private void runGen2(World world, Random rand) {
			for (Coordinate c : stone) {
				Coordinate c2 = c.offset(0, 1, 0);
				if (stone.contains(c2))
					continue;
			}
		}

	}

	private class Tendril {

		private final double rootAngle;
		private final double length;
		private final DecimalPosition origin;
		private final DecimalPosition endpoint;

		private final Random rand;

		private final NestedMap<WorldChunk, Coordinate, BlockKey> blocks = new NestedMap();

		private Tendril(long seed, double ang) {
			rootAngle = ((ang%360)+360)%360;
			rand = new Random(seed);
			rand.nextBoolean();
			rand.nextBoolean();

			length = Math.sqrt(ReikaRandomHelper.getRandomBetween(MIN_DIST_SQ_CH+(MAX_DIST_SQ_CH-MIN_DIST_SQ_CH)*0.67, MAX_DIST_SQ_CH, rand))*16D;
			origin = new DecimalPosition(ReikaRandomHelper.getRandomPlusMinus(0.5, 32, rand), 45, ReikaRandomHelper.getRandomPlusMinus(0.5, 32, rand));
			endpoint = new DecimalPosition(length*Math.cos(Math.toRadians(ang)), origin.yCoord, length*Math.sin(Math.toRadians(ang)));
		}

		@Override
		public String toString() {
			return "Tendril "+origin+" > "+endpoint+" @ "+rootAngle;
		}

		private Tendril calculate(World world) {
			LightningBolt lb = new LightningBolt(origin, endpoint, (int)(length/32)).setVariance(24, 6, 24).setRandom(rand);
			lb.maximize();
			List<DecimalPosition> li = lb.spline(SplineType.CHORDAL, lb.nsteps*12);
			for (DecimalPosition pos : li) {
				//Coordinate c1 = pos.getCoordinate();
				//blocks.put(new WorldChunk(world, c1.xCoord >> 4, c1.zCoord >> 4), c1, coatingBlock);
				//blocks.put(new WorldChunk(world, c2.xCoord >> 4, c2.zCoord >> 4), c2, coatingBlock);
				for (int a = -3; a <= 3; a++) {
					for (int b = -3; b <= 3; b++) {
						for (int c = -3; c <= 3; c++) {
							double dist = ReikaMathLibrary.py3d(a, b, c);
							if (dist <= 4) {/*
								BlockKey bk = null;
								if (dist <= 3)
									bk = coreBlock;
								else if (ReikaMathLibrary.py3d(a, 0, c) <= 1.2)
									bk = coatingBlock;

								if (bk != null) {
									double dx = pos.xCoord+a;
									double dy = Math.min(60, pos.yCoord)+b;
									double dz = pos.zCoord+c;
									Coordinate loc = new Coordinate(dx, dy, dz);
									WorldChunk wc = new WorldChunk(world, loc.xCoord >> 4, loc.zCoord >> 4);
									Map<Coordinate, BlockKey> map = blocks.getMap(wc);
									if (map != null && coatingBlock.equals(map.get(loc)))
										continue;
									blocks.put(wc, loc, bk);
								}*/
								double dx = pos.xCoord+a;
								double dy = pos.yCoord+b;
								double dz = pos.zCoord+c;
								Coordinate loc = new Coordinate(dx, dy, dz);
								WorldChunk wc = new WorldChunk(world, loc.xCoord >> 4, loc.zCoord >> 4);
								blocks.put(wc, loc, coreBlock);
							}
						}
					}
				}
			}
			int lastThorn = -1;
			for (int i = 1; i < li.size()-1; i++) {
				DecimalPosition prev = li.get(i-1);
				DecimalPosition pos = li.get(i);
				DecimalPosition next = li.get(i+1);
				//double ang1 = Math.toDegrees(Math.atan2(pos.zCoord-prev.zCoord, pos.xCoord-prev.xCoord));
				//double ang2 = Math.toDegrees(Math.atan2(next.zCoord-pos.zCoord, next.xCoord-pos.xCoord));
				//double ang = (ang2+ang1)/2;
				double rotAng = ((i-1)*1.5)%360D;
				this.addSpiralPoint(world, prev, pos, next, rotAng, 0, 4, 0);
				this.addSpiralPoint(world, prev, pos, next, rotAng, 0, -4, 0);
				//this.addSpiralPoint(world, prev, pos, next, rotAng, 4, 0, 0);
				//this.addSpiralPoint(world, prev, pos, next, rotAng, -4, 0, 0);
				if (rand.nextInt(18) == 0 && (lastThorn < 0 || pos.getDistanceTo(li.get(lastThorn)) > 9)) {
					lastThorn = i;
					double len = ReikaRandomHelper.getRandomBetween(6D, 18D, rand);
					double ang = rand.nextDouble()*360;
					Vec3 point = ReikaVectorHelper.rotatePointAroundAxisByAngle(0, len, 0, 0, 0, 0, next.xCoord-prev.xCoord, next.yCoord-prev.yCoord, next.zCoord-prev.zCoord, ang);
					for (double d = 0; d <= 1; d += 0.03125) {
						double dx = pos.xCoord+d*point.xCoord;
						double dy = pos.yCoord+d*point.yCoord;
						double dz = pos.zCoord+d*point.zCoord;
						double r = ReikaMathLibrary.linterpolate(d, 0, 1, 1.9+len*0.02, 0.9);
						int ri = MathHelper.ceiling_double_int(r);
						for (int a = -ri; a <= ri; a++) {
							for (int b = -ri; b <= ri; b++) {
								for (int c = -ri; c <= ri; c++) {
									if (ReikaMathLibrary.py3d(a, b, c) <= r) {
										Coordinate loc = new Coordinate(dx+a, dy+b, dz+c);
										WorldChunk wc = new WorldChunk(world, loc.xCoord >> 4, loc.zCoord >> 4);
										blocks.put(wc, loc, coreBlock);
									}
								}
							}
						}
					}
				}
			}
			Island il = new Island(endpoint.getCoordinate(), ReikaRandomHelper.getRandomBetween(24, 60, rand), rand.nextLong());
			while(rand.nextInt(4) > 0) {
				int dx = ReikaRandomHelper.getRandomPlusMinus(0, 30, rand);
				int dz = ReikaRandomHelper.getRandomPlusMinus(0, 30, rand);
				int dy = ReikaRandomHelper.getRandomPlusMinus(0, 12, rand);
				float f = 0.3F+rand.nextFloat()*0.5F;
				il.addChild(dx, dy, dz, f);
			}
			il.generate(world, rand);
			return this;
		}

		private void addSpiralPoint(World world, DecimalPosition prev, DecimalPosition pos, DecimalPosition next, double rotAng, double dx, double dy, double dz) {
			Vec3 point = ReikaVectorHelper.rotatePointAroundAxisByAngle(dx, dy, dz, 0, 0, 0, next.xCoord-prev.xCoord, next.yCoord-prev.yCoord, next.zCoord-prev.zCoord, rotAng);
			for (int a = -2; a <= 2; a++) {
				for (int b = -2; b <= 2; b++) {
					for (int c = -2; c <= 2; c++) {
						if (ReikaMathLibrary.py3d(a, b, c) <= 1.6) {
							//ReikaJavaLibrary.pConsole(new DecimalPosition(a, b+4, c)+" > "+point);
							Coordinate loc = new Coordinate(point.xCoord+pos.xCoord+a, point.yCoord+pos.yCoord+b, point.zCoord+pos.zCoord+c);
							if (!blocks.containsInnerKey(loc))
								blocks.put(new WorldChunk(world, loc.xCoord >> 4, loc.zCoord >> 4), loc, coatingBlock);
						}
					}
				}
			}
		}

	}

}

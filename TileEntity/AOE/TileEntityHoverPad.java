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
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityHoverPad extends TileEntityChromaticBase {

	private HoverMode mode = HoverMode.HOVER;

	private double particleX[];
	private double particleY[];
	private double particleZ[];

	private AxisAlignedBB hoverBox;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote) {
			this.spawnParticles(world, x, y, z);
		}
		else {
			List<EntityPlayer> li = world.getEntitiesWithinAABB(EntityPlayer.class, hoverBox);
			for (EntityPlayer ep : li) {
				ep.motionY = mode.fallVelocity;
			}
		}
	}

	private int getParticleRate() {
		return mode == HoverMode.HOVER ? 1 : 4;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		hoverBox = this.calculateBox(world, x, y, z);
		this.initParticleVectors();
	}

	private AxisAlignedBB calculateBox(World world, int x, int y, int z) {
		int dy = y-1;
		while (world.getBlock(x, dy, z).isAir(world, x, dy, z)) {
			dy--;
		}
		Block bk = world.getBlock(x, dy, z);
		if (bk != ChromaBlocks.PAD.getBlockInstance())
			return ReikaAABBHelper.getBlockAABB(x, dy, z);
		BlockArray b = new BlockArray();
		b.recursiveAddWithBounds(world, x, dy, z, ChromaBlocks.PAD.getBlockInstance(), x-32, x+32, dy, dy, z-32, z+32);
		HashSet<Coordinate> set = new HashSet(b.keySet());
		for (int i = 1; i < 16; i++) {
			for (Coordinate c : set) {
				b.addBlockCoordinate(x, dy+i, z);
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
			float s = 1+rand.nextFloat();
			int l = 60+rand.nextInt(40);
			EntityFX fx = new EntityBlurFX(world, particleX[i], particleY[i], particleZ[i]).setGravity(mode.particleGravity*1.5F).setColor(mode.particleColor).setRapidExpand().setLife(l).setScale(s);
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

		RISE(0x00ff00, -0.0625F, 0.03125),
		HOVER(0xffff00, 0F, 0),
		FALL(0xff0000, 0.0625F, -0.03125);

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

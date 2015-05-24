package Reika.ChromatiCraft.Block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;

public class BlockHoverBlock extends Block {

	public static enum HoverType {
		STATIONARY(0xffff00, 0),
		DAMPER(0xff00ff, -0.125),
		ELEVATE(0x2288ff, 0.125),
		FASTELEVATE(0x00ff00, 0.75);

		private static final HoverType[] list = values();
		private final int renderColor;
		private final double velocityFactor;

		private HoverType(int c, double v) {
			renderColor = c;
			velocityFactor = v;
		}

		public int getPermanentMeta() {
			return this.ordinal();
		}

		public int getDecayMeta() {
			return this.ordinal()+8;
		}

		public boolean movesUpwards() {
			return velocityFactor > 0;//this == ELEVATE || this == FASTELEVATE;
		}

		public void updateEntity(EntityPlayer e) {
			double dv = e.motionY-velocityFactor;
			if (dv < 0) {
				e.motionY -= 0.125*dv;
			}
		}

		public static HoverType getFromMeta(int meta) {
			return list[meta%4];
		}
	}

	public BlockHoverBlock(Material mat) {
		super(mat);

		this.setBlockUnbreakable();
		this.setCreativeTab(DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment() ? ChromatiCraft.tabChroma : null);
		this.setResistance(600000);
		this.setTickRandomly(true);
	}

	public boolean decay(int meta) {
		return meta >= 8;
	}

	public boolean doDecay(int meta) {
		return meta >= 12;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:basic/hover");
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random r) {
		int meta = world.getBlockMetadata(x, y, z);
		if (this.decay(meta)) {
			if (this.doDecay(meta)) {
				world.setBlockToAir(x, y, z);
			}
			else {
				world.setBlockMetadataWithNotify(x, y, z, meta+4, 3);
				world.scheduleBlockUpdate(x, y, z, this, 60+r.nextInt(180));
			}
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		world.scheduleBlockUpdate(x, y, z, this, 240);
	}

	@Override
	public int getRenderColor(int meta) {
		int color = HoverType.getFromMeta(meta).renderColor;
		if (this.doDecay(meta)) {
			color = ReikaColorAPI.getModifiedSat(color, 0.75F);
		}
		return color;
	}

	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		return this.getRenderColor(iba.getBlockMetadata(x, y, z));
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		ReikaParticleHelper.PORTAL.spawnAroundBlock(world, x, y, z, 32);
		ReikaParticleHelper.PORTAL.spawnAroundBlock(world, x, y+1, z, 32);
	}

	@Override
	public Item getItemDropped(int meta, Random r, int fortune) {
		return null;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (e instanceof EntityPlayer) {
			int meta = world.getBlockMetadata(x, y, z);
			HoverType.getFromMeta(meta).updateEntity((EntityPlayer)e);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean canCollideCheck(int meta, boolean hitLiquid) {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return 0;//return ChromatiCraft.proxy.hoverRender;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}
	/*
	@Override
	public boolean canRenderInPass(int pass) {
		HoverBlockRenderer.renderPass = pass;
		return pass <= 1;
	}
	 */
	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int s) {
		return s > 1 && iba.getBlock(x, y, z) != this;
	}
}

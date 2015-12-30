package Reika.ChromatiCraft.Block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockEtherealLight extends Block {

	public BlockEtherealLight(Material mat) {
		super(mat);
		this.setResistance(3600000F);
		this.setHardness(0);
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	public static enum Flags {
		MINEABLE(),
		PARTICLES();

		public boolean isPresent(World world, int x, int y, int z) {
			return this.isPresent(world.getBlockMetadata(x, y, z));
		}

		public boolean isPresent(int meta) {
			return (meta & this.getFlag()) != 0;
		}

		public int getFlag() {
			return (1 << this.ordinal());
		}
	}

	@Override
	public boolean isAir(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public Item getItemDropped(int id, Random r, int fortune) {
		return null;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random r) {

	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block n) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (Flags.PARTICLES.isPresent(world, x, y, z)) {
			if (rand.nextInt(2) == 0) {
				double rx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.0625);
				double ry = ReikaRandomHelper.getRandomPlusMinus(y+0.5, 0.0625);
				double rz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.0625);
				float gv = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.0625);
				int l = 10+rand.nextInt(50);
				double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
				double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
				int c = ReikaColorAPI.getModifiedHue(0xff0000, (int)(360D*y/64D));
				int r = ReikaColorAPI.getRed(c);//32;
				int g = ReikaColorAPI.getGreen(c);//127;
				int b = ReikaColorAPI.getBlue(c);//255;
				EntityBlurFX fx = new EntityBlurFX(world, rx, ry, rz, vx, 0, vz).setGravity(gv).setLife(l).setScale(1.5F).setColor(r, g, b).setRapidExpand().setColliding();
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	@Override
	public int damageDropped(int par1) {
		return 0;
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 0;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return 15;
	}

	@Override
	public final ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		//if (Flags.MINEABLE.isPresent(world, x, y, z)) {
		//	ret.add(new ItemStack(this, 1, metadata));
		//}
		return ret;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block b, int meta) {
		super.breakBlock(world, x, y, z, b, meta);
		if (Flags.MINEABLE.isPresent(meta)) {
			ReikaItemHelper.dropItem(world, x, y, z, new ItemStack(b, 1, meta));
		}
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean canCollideCheck(int par1, boolean par2) {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return blockIcon;
	}

	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		blockIcon = par1IconRegister.registerIcon("ChromatiCraft:trans");
	}

}
